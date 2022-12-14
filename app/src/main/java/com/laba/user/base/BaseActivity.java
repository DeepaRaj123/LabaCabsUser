package com.laba.user.base;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.laba.user.BuildConfig;
import com.laba.user.MvpApplication;
import com.laba.user.R;
import com.laba.user.common.ConnectivityReceiver;
import com.laba.user.common.LocaleHelper;
import com.laba.user.common.Utilities;
import com.laba.user.data.DebugHashMap;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Provider;
import com.laba.user.data.network.model.Token;
import com.laba.user.ui.activity.OnBoardActivity;
import com.laba.user.ui.activity.login.EmailActivity;
import com.laba.user.ui.activity.login.PasswordActivity;
import com.laba.user.ui.activity.register.RegisterActivity;
import com.laba.user.ui.activity.social.SocialLoginActivity;
import com.laba.user.ui.activity.splash.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity
        implements MvpView, ConnectivityReceiver.ConnectivityReceiverListener {

    public static final int REQUEST_CODE_PICTURE = 23;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 100;
    public static boolean isCash = true;
    public static boolean isCard = true;
    public static boolean isRazorpay = true;
    public static boolean canReRoot = true;
    public static int APP_REQUEST_CODE = 99;
    public static String CURRENT_LOCATION = "";
    public static DebugHashMap<String, Object> RIDE_REQUEST = new DebugHashMap<>();
    public static HashMap<String, Object> RIDE_REQUEST_setting = new HashMap<>();
    public static Datum DATUM = null;
    public static Provider provider = null;
    public static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public static SimpleDateFormat STYLE_DATE_FORMAT = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;
    Activity activity;
    private boolean isNetwork = false;
    private Dialog offlineDialog;
    private String error;
    private BasePresenter<BaseActivity> presenter = new BasePresenter<BaseActivity>();

    public static String getDisplayableTime(long value) {

        long difference;
        Long mDate = java.lang.System.currentTimeMillis();

        if (mDate > value) {
            difference = mDate - value;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 86400) {
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return formatter.format(new Date(value));
                //return "not yet";
            } else if (seconds < 172800) // 48 * 60 * 60
                return "yesterday";
            else if (seconds < 2592000) // 30 * 24 * 60 * 60
                return days + " days ago";
            else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
                return months <= 1 ? "one month ago" : days + " months ago";
            else return years <= 1 ? "one year ago" : years + " years ago";
        }
        return null;
    }

    public static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (json != JSONObject.NULL) retMap = toMap(json);
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) value = toList((JSONArray) value);
            else if (value instanceof JSONObject) value = toMap((JSONObject) value);
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) value = toList((JSONArray) value);
            else if (value instanceof JSONObject) value = toMap((JSONObject) value);
            list.add(value);
        }
        return list;
    }

    public static String getNewNumberFormat(double d) {
        //  String text = Double.toString(Math.abs(d));
        String text = Double.toString(d);
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        if (decimalPlaces == 2) return text;
        else if (decimalPlaces == 1) return text + "0";
        else if (decimalPlaces == 0) return text + ".00";
        else if (decimalPlaces > 2) {
            String converted = String.valueOf((double) Math.round(d * 100) / 100);
            int convertedIntegers = converted.indexOf('.');
            int convertedDecimals = converted.length() - convertedIntegers - 1;
            if (convertedDecimals == 2) return converted;
            else if (convertedDecimals == 1) return converted + "0";
            else if (convertedDecimals == 0) return converted + ".00";
            else return converted;
        } else return text;
    }

    @Override
    public Activity activity() {
        return this;
    }

    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        //MainActivity.currentStatus = "EMPTY";
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        presenter.attachView(this);
        initView();
        activity = this;
        checkConnection();

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.tranxit.app",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
            Log.e("KeyHash", ignored.getMessage());
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (activity instanceof SplashActivity || activity instanceof EmailActivity ||
                activity instanceof PasswordActivity || activity instanceof OnBoardActivity ||
                activity instanceof RegisterActivity || activity instanceof SocialLoginActivity) {
            if (!isNetwork) showOfflineDialog(isConnected, 1);
            else {
                isNetwork = false;
                if (!isConnected) {
                    try {
                        hideLoading();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    Toast.makeText(activity, getString(R.string.current_alternative), Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        hideLoading();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    if (offlineDialog != null && offlineDialog.isShowing()) offlineDialog.dismiss();

                }
            }
        } else showOfflineDialog(isConnected, 0);

    }

    public void showOfflineDialog(boolean isConnected, int position) {
        if (!isConnected) if (activity != null) if (position == 0) try {
            final Dialog offlineDialog = new Dialog(this);
            offlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            offlineDialog.setCancelable(false);
            offlineDialog.setCanceledOnTouchOutside(false);
            offlineDialog.setContentView(R.layout.layout_offline);
            Window window = offlineDialog.getWindow();
            offlineDialog.show();
            ImageView iv_retry = offlineDialog.findViewById(R.id.iv_retry);
            Button btn_send_location = offlineDialog.findViewById(R.id.btn_send_location);
            TextView no_thanks = offlineDialog.findViewById(R.id.no_thanks);
            no_thanks.setOnClickListener(view -> {
                offlineDialog.dismiss();
                finishAffinity();
            });
            btn_send_location.setOnClickListener(view -> {
                if (btn_send_location.getVisibility() == View.VISIBLE) {
                    offlineDialog.dismiss();
                    try {
                        Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                        smsIntent.setData(Uri.parse("smsto:"));
                        smsIntent.setType("vnd.android-dir/mms-sms");
                        smsIntent.putExtra("address", SharedHelper.getKey(getApplicationContext(), "appContact"));
                        smsIntent.putExtra("sms_body", "I need a cab @" +
                                SharedHelper.getKey(activity(), "latitude") + "," +
                                SharedHelper.getKey(activity(), "longitude") + "( Please don't edit this SMS. Standard SMS charges of Rs.3 per SMS may apply )");
                        startActivity(smsIntent);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
            assert window != null;
            WindowManager.LayoutParams param = window.getAttributes();
            param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
            param.windowAnimations = R.style.DialogAnimation;
            window.setAttributes(param);
            Objects.requireNonNull(offlineDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        else try {
                offlineDialog = new Dialog(this);
                offlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                offlineDialog.setCancelable(false);
                offlineDialog.setCanceledOnTouchOutside(false);
                offlineDialog.setContentView(R.layout.layout_offline_alternative);
                Window window = offlineDialog.getWindow();
                offlineDialog.show();
                ImageView iv_retry = offlineDialog.findViewById(R.id.iv_retry);
                TextView no_thanks = offlineDialog.findViewById(R.id.no_thanks);
                no_thanks.setOnClickListener(view -> {
                    offlineDialog.dismiss();
                    finishAffinity();
                });
                iv_retry.setOnClickListener(v -> {
                    isNetwork = true;
                    showLoading();
                    checkConnection();
                });
                assert window != null;
                WindowManager.LayoutParams param = window.getAttributes();
                param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
                param.windowAnimations = R.style.DialogAnimation;
                window.setAttributes(param);
                Objects.requireNonNull(offlineDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        if (!progressDialog.isShowing()) {
            System.out.println("BaseActivity.showLoading...." + this.activity);
            progressDialog.show();
        }
    }

    @Override
    public void hideLoading() throws Exception {
        if (progressDialog != null) if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void switchFragment(Fragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(containerId, fragment).addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }

    public String getAddress(LatLng currentLocation) {
        String address = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
//            List<Address> addresses = geocoder.getFromLocation(36.851547, 10.297449, 1);
            if ((addresses != null) && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0)
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++)
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                else strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                address = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            Log.e("MAP", "getAddress: " + e);
        }
        return address;
    }

    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void pickImage() {
        EasyImage.openChooserWithGallery(this, "", 0);
    }

    public void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }

    public void fbOtpVerify() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
      /*  if (BuildConfig.DEBUG) {
            PhoneNumber phoneNumber = new PhoneNumber("IN", "9003440134", "+91");
            configurationBuilder.setInitialPhoneNumber(phoneNumber);
        }*/
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    public void alertLogout() {
        new AlertDialog.Builder(activity())
                .setMessage(R.string.are_sure_you_want_to_logout)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    SharedHelper.clearSharedPreferences(activity());
                    RIDE_REQUEST.clear();
                    finishAffinity();
                    startActivity(new Intent(activity(), SplashActivity.class));
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @SuppressLint("StringFormatInvalid")
    public void shareApp() {
        try {
            String appName = getString(R.string.app_name);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, appName);
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content, appName, BuildConfig.APPLICATION_ID));
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public float bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return (float) brng;
    }

    public void animateMarker(final LatLng startPosition,
                              final LatLng toPosition, Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 700;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * toPosition.longitude + (1 - t) * startPosition.longitude;
                    double lat = t * toPosition.latitude + (1 - t) * startPosition.latitude;

                    marker.setPosition(new LatLng(lat, lng));

                    // Post again 16ms later.
                    if (t < 1.0) handler.postDelayed(this, 16);
                    else marker.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleError(Throwable e) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (e != null) {

            try {
                if (e instanceof ConnectException || e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException || e instanceof NoRouteToHostException) {
//                     Toasty.error(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException) e).response().errorBody();
                    int responseCode = ((HttpException) e).response().code();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        if (responseCode == 400 || responseCode == 405 || responseCode == 500) {
                            Toasty.error(this, getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        } else if (responseCode == 404) {
                            if (PasswordActivity.TAG.equals("PasswordActivity")) {
                                Collection<Object> values = jsonToMap(jsonObject).values();
                                printIfContainsValue(jsonToMap(jsonObject), values.toString()
                                        .replaceAll("[\\[\\],]", ""), "Password");
                            } else {
                                Toasty.error(this, getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                            }
                        } else if (responseCode == 401) {
                            Utilities.LogoutApp(activity(), getString(R.string.expire_logout));
                        } else if (responseCode == 422) {
                            Collection<Object> values = jsonToMap(jsonObject).values();
                            printIfContainsValue(jsonToMap(jsonObject), values.toString().replaceAll("[\\[\\],]", ""), values.toString()
                                    .replaceAll("[\\[\\],]", ""));
                        } else if (responseCode == 503) {
                            Toasty.error(this, getString(R.string.server_down), Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(this, getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception exception) {
                        //  Toast.makeText(getApplicationContext(), getString(R.string.some_thing_wrong), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Toast.makeText(this, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                // Toast.makeText(this, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void refreshToken() {
        if (!SharedHelper.getKey(this, "refresh_token").equalsIgnoreCase("")) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("grant_type", "refresh_token");
            map.put("refresh_token", SharedHelper.getKey(this, "refresh_token"));
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("scope", "");
            showLoading();
            presenter.refreshToken(map);
        } else Toasty.error(this, getString(R.string.refresh_invaild), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccessRefreshToken(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        SharedHelper.putKey(this, token.getAccessToken(), "access_token");
        Toasty.error(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorRefreshToken(Throwable throwable) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (throwable != null) {
            showLoading();
            presenter.logout(SharedHelper.getKey(this, "user_id"));
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Utilities.LogoutApp(activity(), getString(R.string.logout_successfully));
    }

    @Override
    public void onError(Throwable throwable) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        throwable.printStackTrace();
    }

    public void printIfContainsValue(Map mp, String stringValue, String value) {
        if (value.equals("Password")) {
            String[] parts = stringValue.split("\\.");
            String part1 = parts[0]; // 004
            Toasty.error(this, part1, Toast.LENGTH_LONG).show();
        } else {
            Toasty.error(this, stringValue, Toast.LENGTH_LONG).show();
        }
    }

    public String getErrorMessage(JSONObject jsonObject) {
        try {
            if (jsonObject.has("message")) {
                error = jsonObject.getString("message");
            } else if (jsonObject.has("error")) {
                error = jsonObject.getString("error");
            } else if (jsonObject.has("email")) {
                error = jsonObject.optString("email");
            } else if (jsonObject.has("mobile")) {
                error = jsonObject.optString("mobile");
            } else {
                error = getString(R.string.some_thing_wrong);
            }
            return error;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String printJSON(Object o) {
        return new Gson().toJson(o);
    }

    @Override
    public void onResume() {
        super.onResume();
        MvpApplication.getInstance().setConnectivityListener(this);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showOfflineDialog(isConnected, 1);
        checkConnection();
    }

    public void initPayment(String mode, TextView paymentMode, ImageView paymentImage) {

        switch (mode.toUpperCase()) {
            case Utilities.PaymentMode.cash:
                paymentMode.setText(getString(R.string.cash));
                paymentImage.setImageResource(R.drawable.ic_money);
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case Utilities.PaymentMode.card:
                paymentMode.setText(getString(R.string.card));
                paymentImage.setImageResource(R.drawable.ic_card);
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                break;
            case Utilities.PaymentMode.payPal:
                paymentMode.setText(getString(R.string.paypal));
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case Utilities.PaymentMode.wallet:
                paymentMode.setText(getString(R.string.wallet));
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            case Utilities.PaymentMode.razorpay:
                paymentMode.setText(getString(R.string.razorpay));
                paymentImage.setImageResource(R.drawable.ic_razorpay);
                break;
            default:
                break;
        }
    }

    public void onErrorBase(Throwable e) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("email"))
                    Toast.makeText(activity(), jObjError.optString("email"), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.onDetach();
    }


    protected Address getAddress(double latitude, double longitude) {
        Address address = null;
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    1);
            if (addresses != null && !addresses.isEmpty())
                address = addresses.get(0);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("MAP", e.getMessage());
            Toast.makeText(getApplicationContext(), "Unable to find address!.", Toast.LENGTH_LONG).show();
        }
        return address;
    }

    public Location getLastKnownLocation() {
        LocationManager mLocationManager =
                (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) continue;
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
                    bestLocation = l;
            }
        if (bestLocation == null) return null;
        return bestLocation;
    }

    protected String getStreetAddress(Address address) {
        ArrayList<String> addressFragments = new ArrayList<>();
        // Fetch the address lines using getAddressLine,
        // join them, and send them to the thread.
        for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
            addressFragments.add(address.getAddressLine(i));
        }
        String streetAddress = TextUtils.join(", ", addressFragments);
        Log.d("Base", streetAddress);
        return streetAddress;
    }

}