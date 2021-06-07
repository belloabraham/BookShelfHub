package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import javax.inject.Inject


class AppUtil @Inject constructor (private val context: Context) {

    fun getAppVersionCode():Long{
         try {
             val packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
             return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            }else{
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return 1
        }
    }

    fun getAppVersionName():String{
        try {
            val packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
             return  packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            return "1.0"
        }
    }

}