package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


class AppUtil(val context: Context) {

    fun getAppVersionCode():Long{
         try {
             val packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0)
             return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            }else{
                packageInfo.versionCode as Long
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return 0
        }
    }

}