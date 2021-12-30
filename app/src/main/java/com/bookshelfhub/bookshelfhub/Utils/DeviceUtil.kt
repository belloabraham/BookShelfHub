package com.bookshelfhub.bookshelfhub.Utils

import android.os.Build
import com.bookshelfhub.bookshelfhub.extensions.capitalize
import javax.inject.Inject

object DeviceUtil{

    fun getDeviceBrandAndModel():String{
        val manufacturer= Build.MANUFACTURER
        val model = Build.MODEL
        if (model.startsWith(manufacturer)){
            return manufacturer.capitalize()!!
        }
        return "$manufacturer $model".capitalize()!!

    }

     fun getDeviceOSVersionInfo(osVersion:Int):String{
            when(osVersion) {
                Build.VERSION_CODES.S_V2 ->
                    return "Android 12 V2 - API ${osVersion}"
                Build.VERSION_CODES.S ->
                    return "Android 12 - API ${osVersion}"
                Build.VERSION_CODES.P ->
                    return "Android 9 - API ${osVersion}"
                Build.VERSION_CODES.Q ->
                    return "Android 10 - API ${osVersion}"
                Build.VERSION_CODES.O ->
                    return "Android 8 - API ${osVersion}"
                Build.VERSION_CODES.O_MR1 ->
                    return "Android 8.1 - API ${osVersion}"
                Build.VERSION_CODES.R->
                    return "Android 11 - API ${osVersion}"
                Build.VERSION_CODES.N ->
                    return "Android 7 - API ${osVersion}"
                Build.VERSION_CODES.N_MR1->
                    return "Android 7.1 - API ${osVersion}"
                Build.VERSION_CODES.M ->
                    return "Android 6 - API ${osVersion}"
                Build.VERSION_CODES.LOLLIPOP->
                    return "Android 5 - API ${osVersion}"
                Build.VERSION_CODES.LOLLIPOP_MR1->
                    return "Android 5.1 - API ${osVersion}"
                Build.VERSION_CODES.BASE->
                    return "Android 5.1 - API ${osVersion}"
            }
        return "Android - API Version ${osVersion}"
    }

}