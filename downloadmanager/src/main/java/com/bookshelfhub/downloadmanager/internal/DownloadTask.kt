package com.bookshelfhub.downloadmanager.internal


import com.bookshelfhub.downloadmanager.Constants
import com.bookshelfhub.downloadmanager.Progress
import com.bookshelfhub.downloadmanager.Response
import com.bookshelfhub.downloadmanager.Status
import com.bookshelfhub.downloadmanager.database.DownloadModel
import com.bookshelfhub.downloadmanager.handler.ProgressHandler
import com.bookshelfhub.downloadmanager.httpclient.HttpClient
import com.bookshelfhub.downloadmanager.internal.stream.FileDownloadOutputStream
import com.bookshelfhub.downloadmanager.internal.stream.FileDownloadRandomAccessFile
import com.bookshelfhub.downloadmanager.request.DownloadRequest
import com.bookshelfhub.downloadmanager.utils.Utils

import java.io.*
import java.net.HttpURLConnection


class DownloadTask(private val request: DownloadRequest) {

    private var progressHandler: ProgressHandler? = null
    private var lastSyncTime: Long = 0
    private var lastSyncBytes: Long = 0
    private var inputStream: InputStream? = null
    private var outputStream: FileDownloadOutputStream? = null
    private var httpClient: HttpClient? = null
    private var totalBytes: Long = 0
    private var responseCode = 0
    private var eTag: String? = null
    private var isResumeSupported = false
    private var tempPath: String? = null


    companion object{
        private const val BUFFER_SIZE = 1024 * 4
        private const val TIME_GAP_FOR_SYNC: Long = 2000
        private const val MIN_BYTES_FOR_SYNC: Long = 65536
        fun create(request: DownloadRequest): DownloadTask{
            return DownloadTask(request)
        }
    }


    fun run(): Response {
        val response = Response()
        if (request.getStatus() === Status.CANCELLED) {
            response.isCancelled=true
            return response
        } else if (request.getStatus() === Status.PAUSED) {
            response.isPaused=true
            return response
        }
        try {
            if (request.getOnProgressListener() != null) {
                progressHandler = ProgressHandler(request.getOnProgressListener())
            }
            tempPath = Utils.getTempPath(request.getDirPath(), request.getFileName())
            val file = File(tempPath!!)
            var model = getDownloadModelIfAlreadyPresentInDatabase()
            if (model != null) {
                if (file.exists()) {
                    request.setTotalBytes(model.totalBytes)
                    request.setDownloadedBytes(model.downloadedBytes)
                } else {
                    removeNoMoreNeededModelFromDatabase()
                    request.setDownloadedBytes(0)
                    request.setTotalBytes(0)
                    model = null
                }
            }
            httpClient = ComponentHolder.getInstance().getHttpClient()
            httpClient!!.connect(request)
            if (request.getStatus() === Status.CANCELLED) {
                response.isCancelled=true
                return response
            } else if (request.getStatus() === Status.PAUSED) {
                response.isPaused =true
                return response
            }
            httpClient = Utils.getRedirectedConnectionIfAny(httpClient!!, request)
            responseCode = httpClient!!.getResponseCode()
            eTag = httpClient!!.getResponseHeader(Constants.ETAG)
            if (checkIfFreshStartRequiredAndStart(model)) {
                model = null
            }
            if (!isSuccessful()) {
                val error = com.bookshelfhub.downloadmanager.Error()
                error.setServerError(true)
                error.setServerErrorMessage(convertStreamToString(httpClient!!.getErrorStream()))
                error.setHeaderFields(httpClient!!.getHeaderFields())
                error.setResponseCode(responseCode)
                response.error = error
                return response
            }
            setResumeSupportedOrNot()
            totalBytes = request.getTotalBytes()
            if (!isResumeSupported) {
                deleteTempFile()
            }
            if (totalBytes == 0L) {
                totalBytes = httpClient!!.getContentLength()
                request.setTotalBytes(totalBytes)
            }
            if (isResumeSupported && model == null) {
                createAndInsertNewModel()
            }
            if (request.getStatus() === Status.CANCELLED) {
                response.isCancelled = true
                return response
            } else if (request.getStatus() === Status.PAUSED) {
                response.isPaused = true
                return response
            }
            request.deliverStartEvent()
            inputStream = httpClient!!.getInputStream()
            val buff = ByteArray(BUFFER_SIZE)
            if (!file.exists()) {

                if(file.parentFile?.exists() == true){
                    if (file.parentFile?.mkdir() == true) {
                        file.createNewFile()
                    }
                }else{
                    file.createNewFile()
                }
            }
            outputStream = FileDownloadRandomAccessFile.create(file)
            if (isResumeSupported && request.getDownloadedBytes() != 0L) {
                outputStream!!.seek(request.getDownloadedBytes())
            }
            if (request.getStatus() === Status.CANCELLED) {
                response.isCancelled = true
                return response
            } else if (request.getStatus() === Status.PAUSED) {
                response.isPaused = true
                return response
            }
            do {
                val byteCount = inputStream!!.read(buff, 0, BUFFER_SIZE)
                if (byteCount == -1) {
                    break
                }
                outputStream!!.write(buff, 0, byteCount)
                request.setDownloadedBytes(request.getDownloadedBytes() + byteCount)
                sendProgress()
                syncIfRequired(outputStream)
                if (request.getStatus() === Status.CANCELLED) {
                    response.isCancelled = true
                    return response
                } else if (request.getStatus() === Status.PAUSED) {
                    sync(outputStream)
                    response.isPaused = true
                    return response
                }
            } while (true)
            val path: String = Utils.getPath(request.getDirPath(), request.getFileName())
            Utils.renameFileName(tempPath!!, path)
            response.isSuccessful =true
            if (isResumeSupported) {
                removeNoMoreNeededModelFromDatabase()
            }
        } catch (e: IOException) {
            if (!isResumeSupported) {
                deleteTempFile()
            }
            val error = com.bookshelfhub.downloadmanager.Error()
            error.setConnectionError(true)
            error.setConnectionException(e)
            response.error = error
        } catch (e: IllegalAccessException) {
            if (!isResumeSupported) {
                deleteTempFile()
            }
            val error = com.bookshelfhub.downloadmanager.Error()
            error.setConnectionError(true)
            error.setConnectionException(e)
            response.error = error
        } finally {
            closeAllSafely(outputStream)
        }
        return response
    }

    private fun deleteTempFile() {
        val file = File(tempPath!!)
        if (file.exists()) {
            file.delete()
        }
    }

    private fun isSuccessful(): Boolean {
        return (responseCode >= HttpURLConnection.HTTP_OK
                && responseCode < HttpURLConnection.HTTP_MULT_CHOICE)
    }

    private fun setResumeSupportedOrNot() {
        isResumeSupported = responseCode == HttpURLConnection.HTTP_PARTIAL
    }

    @Throws(IOException::class, IllegalAccessException::class)
    private fun checkIfFreshStartRequiredAndStart(model: DownloadModel?): Boolean {
        if (responseCode == Constants.HTTP_RANGE_NOT_SATISFIABLE || isETagChanged(model)) {
            model?.let {
                removeNoMoreNeededModelFromDatabase()
            }

            deleteTempFile()
            request.setDownloadedBytes(0)
            request.setTotalBytes(0)
            httpClient = ComponentHolder.getInstance().getHttpClient()
            httpClient!!.connect(request)
            httpClient = Utils.getRedirectedConnectionIfAny(httpClient!!, request)
            responseCode = httpClient!!.getResponseCode()
            return true
        }
        return false
    }

    private fun isETagChanged(model: DownloadModel?): Boolean {
        return (!(eTag == null || model?.eTag == null)
                && model.eTag != eTag)
    }

    private fun getDownloadModelIfAlreadyPresentInDatabase(): DownloadModel? {
       return ComponentHolder.getInstance().getDbHelper().find(request.getDownloadId())
    }

    private fun createAndInsertNewModel() {
        val model = DownloadModel(request.getDownloadId())
        model.url = request.getUrl()
        model.eTag = eTag!!
        model.dirPath = request.getDirPath()
        model.fileName = request.getFileName()
        model.downloadedBytes = request.getDownloadedBytes()
        model.totalBytes = totalBytes
        model.lastModifiedAt = System.currentTimeMillis()
        ComponentHolder.getInstance().getDbHelper().insert(model)
    }

    private fun removeNoMoreNeededModelFromDatabase() {
        ComponentHolder.getInstance().getDbHelper().remove(request.getDownloadId())
    }

    private fun sendProgress() {
        if (request.getStatus() !== Status.CANCELLED) {
            if (progressHandler != null) {
                progressHandler!!
                    .obtainMessage(
                        Constants.UPDATE,
                        Progress(
                            request.getDownloadedBytes(),
                            totalBytes
                        )
                    ).sendToTarget()
            }
        }
    }

    private fun syncIfRequired(outputStream: FileDownloadOutputStream?) {
        val currentBytes: Long = request.getDownloadedBytes()
        val currentTime = System.currentTimeMillis()
        val bytesDelta = currentBytes - lastSyncBytes
        val timeDelta = currentTime - lastSyncTime
        if (bytesDelta > MIN_BYTES_FOR_SYNC && timeDelta > TIME_GAP_FOR_SYNC) {
            sync(outputStream)
            lastSyncBytes = currentBytes
            lastSyncTime = currentTime
        }
    }

    private fun sync(outputStream: FileDownloadOutputStream?) {
        var success: Boolean
        try {
            outputStream!!.flushAndSync()
            success = true
        } catch (e: IOException) {
            success = false
            e.printStackTrace()
        }
        if (success && isResumeSupported) {
            ComponentHolder.getInstance().getDbHelper()
                .updateProgress(
                    request.getDownloadId(),
                    request.getDownloadedBytes(),
                    System.currentTimeMillis()
                )
        }
    }

    private fun closeAllSafely(outputStream: FileDownloadOutputStream?) {
        httpClient?.let {
            try {
                it.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        inputStream?.let {
            try {
                it.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        try {
            outputStream?.let {
                try {
                    sync(it)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } finally {
            outputStream?.let {
                try {
                    it.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun convertStreamToString(stream: InputStream?): String {
        val stringBuilder = StringBuilder()
        stream?.let {
            var line: String?
            var bufferedReader: BufferedReader? = null
            try {
                bufferedReader = BufferedReader(InputStreamReader(stream))
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuilder.append(line)
                }
            } catch (ignored: IOException) {
            } finally {
                try {
                    bufferedReader?.close()
                } catch (ignored: NullPointerException) {
                } catch (ignored: IOException) {
                }
            }
        }
        return stringBuilder.toString()
    }


}