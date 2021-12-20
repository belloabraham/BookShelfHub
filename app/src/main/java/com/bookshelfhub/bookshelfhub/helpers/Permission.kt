package com.bookshelfhub.bookshelfhub.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import pub.devrel.easypermissions.EasyPermissions

object Permission {

    const val WRITE_STORAGE_RC =  1000

    fun hasPermission(context: Context, manifestPerm:String): Boolean {
       return EasyPermissions.hasPermissions(context, manifestPerm)
    }

    fun requestPermission(activity: Activity, rational:String, requestCode: Int, permission:String){
        EasyPermissions.requestPermissions(activity, rational,
            requestCode, permission)
    }

    fun setPermissionResult(requestCode:Int, permission:Array<out String>, grantResult:IntArray){
        EasyPermissions.onRequestPermissionsResult(requestCode, permission, grantResult)
    }

    fun isPermissionPermanentlyDenied(activity: Activity, permission:List<String>): Boolean {
       return EasyPermissions.somePermissionPermanentlyDenied(activity, permission)
    }

}