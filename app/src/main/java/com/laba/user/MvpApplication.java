package com.laba.user;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import androidx.multidex.MultiDex;
import android.util.Log;

//import com.facebook.stetho.Stetho;
//import com.facebook.stetho.Stetho;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.laba.user.common.ConnectivityReceiver;
import com.laba.user.common.LocaleHelper;
import com.laba.user.common.fcm.ForceUpdateChecker;

import java.util.HashMap;
import java.util.Map;


public class MvpApplication extends Application {

    private static MvpApplication mInstance;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    public static final int PERMISSIONS_REQUEST_PHONE = 4;
    public static final int REQUEST_CHECK_SETTINGS = 5;
//    public static float DEFAULT_ZOOM = 18;
    public static float DEFAULT_ZOOM = 15;
    public static Location mLastKnownLocation;

    private static final String TAG = "MvpApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: 25/08/21 commented when dependency updated
        /*if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());*/



//        Stetho.initializeWithDefaults(this);
        mInstance = this;
        MultiDex.install(this);
        remoteConfigUpdate();
    }

    private void remoteConfigUpdate() {
        final FirebaseRemoteConfig mRemoteConfig = FirebaseRemoteConfig.getInstance();
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_PRIORITY_UPDATE, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());

        // TODO: 25/08/21 commented when dependency updated
        /*mRemoteConfig.setDefaults(remoteConfigDefaults);
        mRemoteConfig.fetch(30) // fetch every minutes
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "remote config is fetched.");
                        mRemoteConfig.activateFetched();
                    }
                });*/
    }

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "en"));
        //super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }


    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
