package com.laba.user.ui.activity.login;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.ForgotResponse;
import com.laba.user.data.network.model.Token;
import com.laba.user.ui.activity.forgot_password.ForgotPasswordActivity;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.activity.register.RegisterActivity;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.HttpException;
import retrofit2.Response;

public class PasswordActivity extends BaseActivity implements LoginIView {

    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    public static String TAG = "";

    private String email;
    private loginPresenter presenter = new loginPresenter();

    @Override
    public int getLayoutId() {
        return R.layout.activity_password;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mToolbar.setNavigationOnClickListener(v -> finish());

        presenter.attachView(this);
        Bundle extras = getIntent().getExtras();
        if (extras != null) email = extras.getString("email");
    }

    private void login() {
        try {
            if (password.getText().toString().isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
                return;
            }
            if (email.isEmpty()) {
                Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                return;
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("grant_type", "password");
            map.put("username", email);
            map.put("password", password.getText().toString());
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("device_token", SharedHelper.getKey(this, "device_token", "No device"));
            map.put("device_id", SharedHelper.getKey(this, "device_id", "123"));
            map.put("device_type", BuildConfig.DEVICE_TYPE);

            showLoading();
            presenter.login(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.sign_up, R.id.forgot_password, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sign_up:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
            case R.id.forgot_password:
                showLoading();
                presenter.forgotPassword(email);
                break;
            case R.id.next:
                login();
                break;
        }
    }

    @Override
    public void onSuccess(Token token) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String accessToken = token.getTokenType() + " " + token.getAccessToken();
        SharedHelper.putKey(this, "access_token", accessToken);
        SharedHelper.putKey(this, "refresh_token", token.getRefreshToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        Log.e("accessToken",accessToken);
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(ForgotResponse forgotResponse) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Toast.makeText(this, forgotResponse.getMessage(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("email", forgotResponse.getUser().getEmail());
        intent.putExtra("otp", forgotResponse.getUser().getOtp().toString());
        intent.putExtra("id", forgotResponse.getUser().getId());
        startActivity(intent);
    }

    @Override
    public void onError(Throwable e) {
        TAG = "PasswordActivity";
//        handleError(e);
        try {
            hideLoading();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("email"))
                    Toast.makeText(activity(), jObjError.optString("email"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
