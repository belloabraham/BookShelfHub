package com.bookshelfhub.bookshelfhub.Utils

import android.os.Build
import javax.inject.Inject

class DeviceUtil @Inject constructor () {

    fun getDeviceBrandAndModel():String{
        val manufacturer= Build.MANUFACTURER
        val model = Build.MODEL
        if (model.startsWith(manufacturer)){
            return manufacturer
        }
        return "$manufacturer $model"

    }

     fun getDeviceOSVersionInfo(osVersion:Int):String{
        Build.VERSION_CODES.O
            when(osVersion) {
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
                31->
                    return "Android 12 - API ${osVersion}"
            }
        return "Android version greater than 12 - API ${osVersion}"
    }

}