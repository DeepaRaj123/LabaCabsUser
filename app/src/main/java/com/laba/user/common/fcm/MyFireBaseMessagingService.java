package com.laba.user.common.fcm;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.WindowManager;

import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.laba.user.R;
import com.laba.user.common.Utilities;
import com.laba.user.data.SharedHelper;
import com.laba.user.ui.activity.OnBoardActivity;
import com.laba.user.ui.activity.main.MainActivity;

import java.util.List;
import java.util.Map;

public class MyFireBaseMessagingService extends FirebaseMessagingService {

    int notificationId = 0;
    private static final String TAG = " FireBaseMessaging";
    public static final String INTENT_FILTER = "INTENT_FILTER";
    public static final String INTENT_PROVIDER = "INTENT_PROVIDER";
    private String message;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);

        @SuppressLint("HardwareIds")
        String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.d("DEVICE_ID: ", deviceId);
        Log.d("FCM_TOKEN", s);

        SharedHelper.putKey(this, "device_token", s);
        SharedHelper.putKey(this, "device_id", deviceId);

    }

    private static void sendMessageToActivity(Context context) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("show_dialog", true);
//        intent.putExtra("Status", msg);
//        Bundle b = new Bundle();
//        b.putParcelable("Location", l);
//        intent.putExtra("Location", b);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message: " + remoteMessage.toString());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            //message
            //Sorry for inconvience time, Our partner or busy. Please try after some time
//            if (remoteMessage.getData()!=null){
//                for (Map.Entry<String, String> entry : remoteMessage.getData().entrySet()) {
//                    String key = entry.getKey();
//                    String value = entry.getValue();
//                    Log.d(TAG, "key, " + key + " value " + value);
//                }
//            }

            if(remoteMessage.getData().get("message").equalsIgnoreCase(
                    "Driver Cancelled the Ride")){
                Intent intent = new Intent("GPSLocationUpdates");
                intent.putExtra("ride_cancelled_after_ride", true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
            if(remoteMessage.getData().get("message").equalsIgnoreCase(
                    "Sorry for the inconvenience, Our Partners are busy. Please try after some time..")){
                Intent intent = new Intent("GPSLocationUpdates");
                intent.putExtra("ride_cancelled", true);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }
                Log.e("message1", String.valueOf(remoteMessage.getData()));
            String typeOfData = remoteMessage.getData().get("key");

            if(typeOfData != null && typeOfData.equalsIgnoreCase("222")){
                sendMessageToActivity(getApplicationContext());
            }

            if (remoteMessage.getFrom().startsWith("/topics/")) {
                Intent intent = new Intent(INTENT_PROVIDER);
                intent.putExtra("latitude", Double.valueOf(remoteMessage.getData().get("latitude")));
                intent.putExtra("longitude", Double.valueOf(remoteMessage.getData().get("longitude")));
                sendBroadcast(intent);
                System.out.println(" INTENT_PROVIDER intent = " + intent);
            } else {
                sendBroadcast(new Intent(INTENT_FILTER));
                String message = remoteMessage.getData().get("message");
                sendBroadcast(new Intent(INTENT_FILTER).putExtra("msg",message));
                Map<String, String> data = remoteMessage.getData();
                boolean pushToUser = data.containsValue("PushToUser");
              //  if(remoteMessage.getData().get("message").equalsIgnoreCase(
              //          "Driver Cancelled the Ride")){
             //   }
                if(pushToUser){
                    sendNotification(remoteMessage.getData().get("message"),true);
                }
                else{
                    sendNotification(remoteMessage.getData().get("message"),false);
                }

            }
            sendBroadcast(new Intent(INTENT_FILTER));
            String message = remoteMessage.getData().get("message");

            if (!isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Utilities.printV(TAG, "foreground");
            } else {
                Utilities.printV(TAG, "background");
                    if (!message.matches("")) {
                        Intent mainIntent = new Intent(this, MainActivity.class);
                        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                    }

            }

        } else sendBroadcast(new Intent(INTENT_FILTER));

    }
    // [END receive_message]

    /**
     * Schedule a job using FirebaseJobDispatcher.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job myJob = dispatcher.newJobBuilder()
                .setService(MyJobService.class)
                .setTag("my-job-tag")
                .build();
        dispatcher.schedule(myJob);
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d("TAG", "Short lived task is done.");
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody,boolean moveToScheduledWindow) {
        if(messageBody.equalsIgnoreCase(
                "Driver Cancelled the Ride")){
            messageBody = "Driver Cancelled the Ride";
        }
        PendingIntent pendingIntent;
        if(moveToScheduledWindow){
//            Intent intent = new Intent(this, OnBoardActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                    PendingIntent.FLAG_ONE_SHOT);

            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("move_to","now");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);        }
        }
        else{
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_IMMUTABLE);
            } else {
                pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);        }
        }

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(R.raw.alert_tone);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.noti_icon_new)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(notificationId++ /* ID of notification */, notificationBuilder.build());

    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
