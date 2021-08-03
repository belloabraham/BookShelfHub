package com.bookshelfhub.bookshelfhub.wrappers

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GooglePlayServices(val activity:Activity) {

    private val PLAY_SERVICES_RESOLUTION_REQUEST=9000


     fun checkForGooglePlayServices() {
        val googleAPI = GoogleApiAvailability.getInstance();
        val result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    activity, result,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                ).show();
            }
        }
     }

}