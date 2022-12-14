package com.laba.user.common.fcm;

import android.content.Context;
import android.content.pm.PackageManager;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

public class ForceUpdateChecker {

    private static final String TAG = "ForceUpdateChecker";
    public static final String KEY_UPDATE_REQUIRED = "isUpdateUserApp";
    public static final String KEY_CURRENT_VERSION = "updateVersionUser";
    public static final String KEY_UPDATE_URL = "playStoreUrlUser";
    public static final String KEY_PRIORITY_UPDATE = "isPriorityUser";

    private OnUpdateNeededListener onUpdateNeededListener;
    private Context context;

    public interface OnUpdateNeededListener {
        void onUpdateNeeded(String updateUrl, boolean isPriorityUpdate);
    }

    public static Builder with(@NonNull Context context) {
        return new Builder(context);
    }

    private ForceUpdateChecker(@NonNull Context context,
                               OnUpdateNeededListener onUpdateNeededListener) {
        this.context = context;
        this.onUpdateNeededListener = onUpdateNeededListener;
    }

    private void check() {

        final FirebaseRemoteConfig remoteConfig = FirebaseRemoteConfig.getInstance();
        Log.d(TAG, "CHECK" + remoteConfig.getBoolean(KEY_UPDATE_REQUIRED));
        if (remoteConfig.getBoolean(KEY_UPDATE_REQUIRED)) {
            String currentVersion = remoteConfig.getString(KEY_CURRENT_VERSION);
            String appVersion = getAppVersion(context);
            String updateUrl = remoteConfig.getString(KEY_UPDATE_URL);

            Log.d(TAG, String.format("CHECK %s, %s", appVersion, currentVersion));
            if (!TextUtils.equals(currentVersion, appVersion)
                    && onUpdateNeededListener != null) {
                onUpdateNeededListener.onUpdateNeeded(updateUrl,remoteConfig.getBoolean(KEY_PRIORITY_UPDATE));
            }
        }
    }

    private String getAppVersion(Context context) {
        String result = "";

        try {
            result = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
            result = result.replaceAll("[a-zA-Z]|-", "");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    public static class Builder {

        private Context context;
        private OnUpdateNeededListener onUpdateNeededListener;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder onUpdateNeeded(OnUpdateNeededListener onUpdateNeededListener) {
            this.onUpdateNeededListener = onUpdateNeededListener;
            return this;
        }

        public ForceUpdateChecker build() {
            return new ForceUpdateChecker(context, onUpdateNeededListener);
        }

        public ForceUpdateChecker check() {
            ForceUpdateChecker forceUpdateChecker = build();
            forceUpdateChecker.check();
            return forceUpdateChecker;

        }
    }
}

