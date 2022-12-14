package com.laba.user.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.laba.user.base.BaseActivity;
import com.laba.user.data.SharedHelper;
import com.laba.user.ui.activity.OnBoardActivity;

import org.apache.commons.lang3.StringEscapeUtils;

import es.dmoral.toasty.Toasty;

public class Utilities {

    public interface InvoiceFare {
        String min = "MIN";
        String hour = "HOUR";
        String distance = "DISTANCE";
        String distanceMin = "DISTANCEMIN";
        String distanceHour = "DISTANCEHOUR";
    }

    public interface PaymentMode {
        String cash = "CASH";
        String card = "CARD";
        String payPal = "PAYPAL";
        String wallet = "WALLET";
        String razorpay="RAZORPAY";
    }

    private static double milesToKm(double miles) {
        return miles * 1.60934;
    }

    private static double kmToMiles(double km) {
        return km * 0.621371;
    }

    public static void LogoutApp(Activity thisActivity, String msg) {
        Toasty.success(thisActivity, msg, Toast.LENGTH_SHORT).show();
        SharedHelper.clearSharedPreferences(thisActivity);
        BaseActivity.RIDE_REQUEST.clear();
        NotificationManager notificationManager = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        thisActivity.startActivity(new Intent(thisActivity, OnBoardActivity.class));
        thisActivity.finishAffinity();
    }

    public static void printV(String TAG, String message) {
        System.out.println(TAG + "==>" + message);
    }

    public static String getEncodeMessage(String message) {
        return StringEscapeUtils.escapeJava(message);
    }
    public static String getDecodeMessage(String message) {
        return StringEscapeUtils.unescapeJava(message);
    }
}
