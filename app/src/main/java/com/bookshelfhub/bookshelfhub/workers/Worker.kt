package com.bookshelfhub.bookshelfhub.workers

import android.content.Context
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
}