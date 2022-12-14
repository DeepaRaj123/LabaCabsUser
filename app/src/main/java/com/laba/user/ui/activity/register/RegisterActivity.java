package com.laba.user.ui.activity.register;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.common.countrypicker.Country;
import com.laba.user.common.countrypicker.CountryPicker;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.MyOTP;
import com.laba.user.data.network.model.RegisterResponse;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.activity.otp.OTPActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class RegisterActivity extends BaseActivity implements RegisterIView {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_confirmation)
    EditText passwordConfirmation;
    @BindView(R.id.chkTerms)
    CheckBox chkTerms;
    @BindView(R.id.countryNumber)
    TextView countryNumber;
    @BindView(R.id.countryImage)
    ImageView countryImage;
    @BindView(R.id.txtPhoneNumber)
    EditText mobile;

    private String countryDialCode = "+91";
    private String countryCode = "IN";
    private CountryPicker mCountryPicker;

    private static final int PICK_OTP_VERIFY = 222;

    private RegisterPresenter registerPresenter = new RegisterPresenter();
    private boolean isEmailAvailable = true;
    private MyOTP otp;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        registerPresenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.register));
        setCountryList();
        clickFunctions();
    }

    private void clickFunctions() {
        email.setOnFocusChangeListener((v, hasFocus) -> {
            isEmailAvailable = true;
            if (!hasFocus && !TextUtils.isEmpty(email.getText().toString()))
                if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                    registerPresenter.verifyEmail(email.getText().toString().trim());
        });
    }

    private void setCountryList() {
        mCountryPicker = CountryPicker.newInstance("Select Country");
        List<Country> countryList = Country.getAllCountries();
        Collections.sort(countryList, (s1, s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
        mCountryPicker.setCountriesList(countryList);

        setListener();
    }

    private void setListener() {
        mCountryPicker.setListener((name, code, dialCode, flagDrawableResID) -> {
            countryNumber.setText(dialCode);
            countryDialCode = dialCode;
            countryImage.setImageResource(flagDrawableResID);
            mCountryPicker.dismiss();
        });

        countryImage.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(),
                "COUNTRY_PICKER"));

        countryNumber.setOnClickListener(v -> mCountryPicker.show(getSupportFragmentManager(),
                "COUNTRY_PICKER"));

        getUserCountryInfo();
    }

    private void getUserCountryInfo() {
        Country country = getDeviceCountry(RegisterActivity.this);
        countryImage.setImageResource(country.getFlag());
        countryNumber.setText(country.getDialCode());
        countryDialCode = country.getDialCode();
        countryCode = country.getCode();
    }

    public Country getDeviceCountry(Context context) {
        return Country.getCountryFromSIM(context) != null
                ? Country.getCountryFromSIM(context)
                : new Country("IN", "India", "+91", R.drawable.flag_in);
    }

    @OnClick({R.id.next, R.id.lblTerms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (validate()) {
                    showLoading();
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("mobile", mobile.getText().toString());
                    params.put("phoneonly", mobile.getText().toString());
                    registerPresenter.sendOTP(params);
                  //  register();
                }
                break;
            case R.id.lblTerms:
                showTermsConditionsDialog();
                break;
        }
    }

    private void showTermsConditionsDialog() {
        showLoading();
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getText(R.string.terms_and_conditions));

        WebView wv = new WebView(this);
        wv.loadUrl(BuildConfig.TERMS_CONDITIONS);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                try {
                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                    try {
                        hideLoading();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    alert.show();
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    hideLoading();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }
        });
    }

    private void register() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first_name", firstName.getText().toString());
        map.put("last_name", lastName.getText().toString());
        map.put("email", email.getText().toString());
        map.put("password", password.getText().toString());
        map.put("password_confirmation", passwordConfirmation.getText().toString());
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("mobile", mobile.getText().toString());
      //  map.put("mobile", countryDialCode + mobile.getText().toString());
       // map.put("dial_code", countryDialCode);
        map.put("country_code", countryDialCode);
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("login_by", "manual");
        showLoading();
        registerPresenter.register(map);
    }

    private boolean validate() {
        password.requestFocus();
        if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, "Please enter valid mail address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return false;
        } else if (mobile.getText().toString().length() != 10) {
            Toast.makeText(this, getString(R.string.mobile_10_digit_error), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password_length), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirmation.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();
            return false;
        } else if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            // TODO: 25/08/21 commented when dependency updated
            /*FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                    return;
                }
                Log.d("FCM_TOKEN", task.getResult().getToken());

                SharedHelper.putKey(RegisterActivity.this, "device_token", task.getResult().getToken());
            });*/
            return false;
        } else if (SharedHelper.getKey(this, "device_id").isEmpty()) {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(this, "device_id", deviceId);
            Toast.makeText(this, getString(R.string.invalid_device_id), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!chkTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.please_accept_terms_condition), Toast.LENGTH_SHORT).show();
            return false;
        } else if (isEmailAvailable) {
            Toast.makeText(this, getString(R.string.email_already_exist), Toast.LENGTH_SHORT).show();
            email.requestFocus();
            email.setText(null);
            if (BuildConfig.DEBUG) {
                password.setText(null);
                passwordConfirmation.setText(null);
            }
            return false;
        } else return true;
    }

    @Override
    public void onSuccess(RegisterResponse response) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, getString(R.string.you_have_been_successfully_registered), Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(this, "access_token", "Bearer " + response.getAccessToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(Object object) {
        isEmailAvailable = false;
    }

    @Override
    public void onError(Throwable e) {
        try {
            hideLoading();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (e instanceof HttpException) {
            try {
                ResponseBody responseBody = ((HttpException) e).response().errorBody();
                int responseCode = ((HttpException) e).response().code();
                JSONObject jsonObject = new JSONObject(responseBody.string());
                if (responseCode == 422) {
                    if (jsonObject.has("mobile")) {
                        Toasty.error(this, getErrorMessage(jsonObject), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    handleError(e);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            handleError(e);
        }
    }

    @Override
    public void onVerifyEmailError(Throwable e) {
        isEmailAvailable = true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE && data != null) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    Log.d("AccountKit", "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                    if (AccountKit.getCurrentAccessToken().getToken() != null) {
                        //SharedHelper.putKey(RegisterActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        SharedHelper.putKey(RegisterActivity.this, "dial_code", "+" +
                                phoneNumber.getCountryCode());
                        SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumber.getPhoneNumber());
                        register();
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.e("AccountKit", "onError: Account Kit" + accountKitError);
                }
            });
        }
        else if (requestCode == PICK_OTP_VERIFY) {
            if(data != null){
                String receivedOtp = data.getStringExtra("otp");
                if(receivedOtp.equalsIgnoreCase(otp.getOtp())){
                    register();
                }
                else{
                    Toast.makeText(getApplicationContext(),"OTP verification failed",Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"OTP verification failed",Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    public void onSuccess(MyOTP otp) {
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.otp = otp;
        Intent intent = new Intent(activity(), OTPActivity.class);
        intent.putExtra("isFromRegister", true);
        intent.putExtra("mobile", mobile.getText().toString());
        intent.putExtra("country_code", countryDialCode);
        intent.putExtra("otp", otp.getOtp());
        startActivityForResult(intent, PICK_OTP_VERIFY);
    }

    @Override
    protected void onDestroy() {
        registerPresenter.onDetach();
        super.onDestroy();
    }
}
