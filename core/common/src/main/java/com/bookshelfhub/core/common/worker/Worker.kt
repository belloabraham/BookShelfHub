package com.bookshelfhub.core.common.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*

class Worker(private val  context: Context){

    fun enqueue(workReq: OneTimeWorkRequest){
        WorkManager.getInstance(context).enqueue(workReq)
    }

    fun enqueueUniquePeriodicWork(tag:String, workReq: PeriodicWorkRequest, workPolicy: ExistingPeriodicWorkPolicy){
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(tag, workPolicy, workReq)
    }

    fun enqueueUniqueWork(tag:String, workPolicy: ExistingWorkPolicy, workReq: OneTimeWorkRequest){
        WorkManager.getInstance(context).enqueueUniqueWork(tag, workPolicy, workReq)
    }


    fun getWorkInfoByIdLiveDataByTag(tag:String): LiveData<MutableList<WorkInfo>> {
        return WorkManager.getInstance(context).getWorkInfosByTagLiveData(tag)
    }
}