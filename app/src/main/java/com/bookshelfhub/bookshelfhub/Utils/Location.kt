package com.bookshelfhub.bookshelfhub.Utils

import android.content.Context
import android.telephony.TelephonyManager


object Location {

    /**
     * Returns a 2 character alpha-2 country code e.g NG for Nigeria
     */
    fun getCountryCode(context: Context): String? {
        return  try {
            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val simCountry = tm.simCountryIso
            simCountry?.uppercase()
                ?: if (tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                    val networkCountry = tm.networkCountryIso
                    networkCountry?.uppercase()
                }else{
                    null
                }
        } catch(e:Exception){
            null
        }
    }
}