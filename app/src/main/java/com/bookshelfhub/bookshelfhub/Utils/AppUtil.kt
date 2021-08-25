package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import javax.inject.Inject


class AppUtil @Inject constructor (private val context: Context) {

    fun getAppVersionCode():Long{
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            }else{
                packageInfo.versionCode.toLong()
            }
        } catch (e: PackageManager.NameNotFoundException) {
            0
        }
    }

    fun getAppVersionName():String{
        return try {
            val packageInfo = context.getPackageManager().getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }

}