package com.bookshelfhub.bookshelfhub.helpers.google

import android.app.Activity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability

class GooglePlayServices(val activity:Activity) {

    private val PLAY_SERVICES_RESOLUTION_REQUEST = 9000

    /**
     * Used to check if user device have Google Play Service installed as it is required by this app
     * if not they are requested to download it
     */
     fun checkForGooglePlayServices() {
        val googleAPI = GoogleApiAvailability.getInstance();
        val result = googleAPI.isGooglePlayServicesAvailable(activity);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(
                    activity, result,
                    PLAY_SERVICES_RESOLUTION_REQUEST
                )?.show()
            }
        }
     }

}