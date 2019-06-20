package com.carpool.tagalong.notifications;

import android.util.Log;

import com.carpool.tagalong.preferences.TagALongPreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class DeviceTokenService extends FirebaseInstanceIdService {
    private static final String TAG = DeviceTokenService.class.getName();

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        TagALongPreferenceManager.setDeviceToken(refreshedToken,getApplicationContext());
    }
    // [END refresh_token]
}
