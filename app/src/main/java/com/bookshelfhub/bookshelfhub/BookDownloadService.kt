package com.bookshelfhub.bookshelfhub

import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.bookshelfhub.bookshelfhub.data.Download
import com.bookshelfhub.bookshelfhub.helpers.notification.NotificationBuilder
import com.bookshelfhub.downloadmanager.*
import com.bookshelfhub.downloadmanager.request.DownloadRequest

class BookDownloadService : LifecycleService() {

    companion object{
        private val liveDownloadRequest = MutableLiveData<DownloadRequest>()
        private val liveDownloadResult = MutableLiveData<DownloadResult>()

        fun getLiveDownloadRequest(): MutableLiveData<DownloadRequest> {
            return liveDownloadRequest
        }
        fun getLiveDownloadResult(): MutableLiveData<DownloadResult> {
            return liveDownloadResult
        }
    }

    data class DownloadResult(val id:Int, val error: Error? = null, val isComplete:Boolean=false)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {


        val url = intent?.getStringExtra(Download.URL)
        val dirPath = intent?.getStringExtra(Download.DIR_PATH)
        val fileName= intent?.getStringExtra(Download.FILE_NAME)
        val downloadId = intent?.getIntExtra(Download.DOWNLOAD_ID, 0)

        /*Download Request for downloads already running that was previously started
        //Saved in a global static list variable
        val downloadReq = downloadReqList.find {
            it.getUrl() == url
        }*/

        intent?.let {
            when(it.action){
                Download.ACTION_START->{
                    //Called for every new download started
                    val downloadRequest = getDownloadReq(url!!, dirPath!!, fileName!!)
                    startDownload(downloadRequest)
                    liveDownloadRequest.value = downloadRequest
                }
                Download.ACTION_RESUME->{
                    //Called for already running downloads
                    resumeDownload(downloadId!!)
                }
                Download.ACTION_PAUSE->{
                    //Called for already running downloads
                    pauseDownload(downloadId!!)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }


    private fun getDownloadReq(url:String, dirPath:String, fileName:String): DownloadRequest {

        return DownloadManager.download(url, dirPath, fileName)
            .build()
            .setOnPauseListener(object:OnPauseListener{
                override fun onPause() {
                    //Show Notification Msg for pause
                }
            })
            .setOnProgressListener(object : OnProgressListener{

                override fun onProgress(progress: Progress?) {
                    //Show Notification Msg for download progressing
                }

            })
            .setOnStartOrResumeListener(object:OnStartOrResumeListener{
                override fun onStartOrResume() {
                    //Show Notification Start or Resume
                }
            })
    }

    private fun startDownload(downloadReq:DownloadRequest){
        downloadReq.start(object:OnDownloadListener{
            override fun onDownloadComplete() {
                //Show Notification Msg for download complete
                liveDownloadResult.value = DownloadResult(downloadReq.getDownloadId(), isComplete = true)
            }
            override fun onError(error: Error?) {
                //Show Notification Msg for download error
                liveDownloadResult.value = DownloadResult(downloadReq.getDownloadId(), error = error)

            }
        })
    }

     private fun pauseDownload(downloadId:Int){
        DownloadManager.pause(downloadId)
     }

     private fun resumeDownload(downloadId:Int){
        DownloadManager.resume(downloadId)
     }


    private fun startInForeground(){
        val notification = NotificationBuilder(this)
        notification
            .setActionText("")
            .setAutoCancel(false)
            .setTitle("")
            .setOngoing(true)
            .setMessage("")
            .setTitle("")
            .setPriority(NotificationCompat.PRIORITY_LOW)
        startForeground(0, notification.Builder(this).getNotificationBuiler().build())
    }

}