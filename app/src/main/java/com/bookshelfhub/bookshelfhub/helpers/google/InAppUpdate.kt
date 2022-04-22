package com.bookshelfhub.bookshelfhub.helpers.google

import android.app.Activity
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.clientVersionStalenessDays


class InAppUpdate(private val activity: Activity) {

    private val appUpdateManager = AppUpdateManagerFactory.create(activity)
    private val daysForImmediateUpdate = 90
    private val daysForFlexibleUpdate = 30
    private val highUpdatePriority = 4
    private lateinit var updateStateListener:InstallStateUpdatedListener
    private val appUpdateInfoTask = appUpdateManager.appUpdateInfo


    fun checkForNewAppUpdate(onSuccess:(isImmediateUpdate:Boolean, appUpdateInfo:AppUpdateInfo )->Unit){
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->

            val isImmediateUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            val isFlexibleUpdateAllowed = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)


            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE){

                if ((appUpdateInfo.updatePriority() >= highUpdatePriority || isLastNotifPast90Days(appUpdateInfo)) && isImmediateUpdateAllowed){
                    onSuccess(true, appUpdateInfo)

                }else if ((isFirstUpdateNotif(appUpdateInfo) || isLastNotificationPast30Days(appUpdateInfo)) && isFlexibleUpdateAllowed) {
                    onSuccess(false, appUpdateInfo)
                }

            }

        }
    }

    /**
     * Should be used in onResume in the case where user does not install pre-downloaded update or exist the app while
     * immediate update is going on
     */
    fun checkForDownloadedOrDownloadingUpdate(activityRequestCode:Int, onDownloaded:()->Unit){
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    onDownloaded()
                }else if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ){
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        activity,
                        activityRequestCode
                    )
                }
            }
    }

    fun installNewUpdate(){
        appUpdateManager.completeUpdate()
    }


    /**
     *Check if the last time the user got noticed about notification is past 30 days
     */
    private fun isLastNotificationPast30Days(appUpdateInfo:AppUpdateInfo): Boolean {
        val latsUpdateNotificationDays = appUpdateInfo.clientVersionStalenessDays ?: -1
        return latsUpdateNotificationDays>=daysForFlexibleUpdate
    }

    /**
     *Start an important required immediate app update
     */
     fun startImmediateUpdate(appUpdateInfo:AppUpdateInfo, updateRequestCode:Int){
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, activity, updateRequestCode)
    }


    /**
     *Start an minor ignorable app update
     */
     fun startFlexibleUpdate(appUpdateInfo:AppUpdateInfo, updateRequestCode:Int, onComplete:(InstallState)->Unit){
         updateStateListener = InstallStateUpdatedListener { state ->
            onComplete(state)
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
               appUpdateManager.unregisterListener(updateStateListener)
            }
        }

        appUpdateManager.registerListener(updateStateListener)
        appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, activity, updateRequestCode)
    }

    private fun isLastNotifPast90Days(appUpdateInfo:AppUpdateInfo): Boolean {
        val latsUpdateNotificationDays = appUpdateInfo.clientVersionStalenessDays ?: -1
        return latsUpdateNotificationDays>=daysForImmediateUpdate
    }

    private fun isFirstUpdateNotif(appUpdateInfo:AppUpdateInfo): Boolean {
        return appUpdateInfo.clientVersionStalenessDays == null
    }

    companion object{
        const val ACTIVITY_REQUEST_CODE = 700
    }



}