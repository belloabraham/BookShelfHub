package com.bookshelfhub.downloadmanager.utils


import com.bookshelfhub.downloadmanager.Constants
import com.bookshelfhub.downloadmanager.core.Core
import com.bookshelfhub.downloadmanager.httpclient.HttpClient
import com.bookshelfhub.downloadmanager.internal.ComponentHolder
import com.bookshelfhub.downloadmanager.request.DownloadRequest
import java.io.File
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import kotlin.experimental.and


object Utils {

    private const val MAX_REDIRECTION = 10

    fun getPath(dirPath: String, fileName: String): String {
        return dirPath + File.separator + fileName
    }

    fun getTempPath(dirPath: String, fileName: String): String {
        return getPath(dirPath, fileName) + ".temp"
    }

    @Throws(IOException::class)
    fun renameFileName(oldPath: String, newPath: String) {
        val oldFile = File(oldPath)
        try {
            val newFile = File(newPath)
            if (newFile.exists()) {
                if (!newFile.delete()) {
                    throw IOException("Deletion Failed")
                }
            }
            if (!oldFile.renameTo(newFile)) {
                throw IOException("Rename Failed")
            }
        } finally {
            if (oldFile.exists()) {
                oldFile.delete()
            }
        }
    }

    fun deleteTempFileAndDatabaseEntryInBackground(path: String, downloadId: Int) {
        Core.getInstance().getExecutorSupplier().forBackgroundTasks()
            .execute {
                ComponentHolder.getInstance().getDbHelper().remove(downloadId)
                val file = File(path)
                if (file.exists()) {
                    file.delete()
                }
            }
    }

    fun deleteUnwantedModelsAndTempFiles(days: Int) {
        Core.getInstance().getExecutorSupplier().forBackgroundTasks()
            .execute {
                val models = ComponentHolder.getInstance()
                    .getDbHelper()
                    .getUnwantedModels(days)
                models?.let {
                    for (model in it) {
                        val tempPath = getTempPath(model.dirPath, model.fileName)
                        ComponentHolder.getInstance().getDbHelper().remove(model.id)
                        val file = File(tempPath)
                        if (file.exists()) {
                            file.delete()
                        }
                    }
                }
            }
    }

    fun getUniqueId(url: String, dirPath: String, fileName: String): Int {
        val filePath = url + File.separator + dirPath + File.separator + fileName
        val hash: ByteArray = try {
            MessageDigest.getInstance("MD5").digest(filePath.toByteArray(charset("UTF-8")))
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("NoSuchAlgorithmException", e)
        } catch (e: UnsupportedEncodingException) {
            throw RuntimeException("UnsupportedEncodingException", e)
        }

        val hex = StringBuilder(hash.size * 2)
        for (b in hash) {
            if (b and 0xFF.toByte() < 0x10) hex.append("0")
            hex.append(Integer.toHexString((b and 0xFF.toByte()).toInt()))
        }
        return hex.toString().hashCode()
    }

    @Throws(IOException::class, IllegalAccessException::class)
    fun getRedirectedConnectionIfAny(
        httpClient2: HttpClient,
        request: DownloadRequest
    ): HttpClient{
        var httpClient: HttpClient = httpClient2
        var redirectTimes = 0
        var code: Int = httpClient.getResponseCode()
        var location: String? = httpClient.getResponseHeader("Location")
        while (isRedirection(code)) {
            if (location == null) {
                throw IllegalAccessException("Location is null")
            }
            httpClient.close()
            request.setUrl(location)
            httpClient = ComponentHolder.getInstance().getHttpClient()
            httpClient.connect(request)
            code = httpClient.getResponseCode()
            location = httpClient.getResponseHeader("Location")
            redirectTimes++
            if (redirectTimes >= MAX_REDIRECTION) {
                throw IllegalAccessException("Max redirection done")
            }
        }
        return httpClient
    }

    private fun isRedirection(code: Int): Boolean {
        return code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER || code == HttpURLConnection.HTTP_MULT_CHOICE || code == Constants.HTTP_TEMPORARY_REDIRECT || code == Constants.HTTP_PERMANENT_REDIRECT
    }

}