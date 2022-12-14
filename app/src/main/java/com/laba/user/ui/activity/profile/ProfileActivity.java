package com.laba.user.ui.activity.profile;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.User;
import com.laba.user.ui.activity.change_password.ChangePasswordActivity;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;

public class ProfileActivity extends BaseActivity implements ProfileIView {

    @BindView(R.id.picture)
    CircleImageView picture;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.mobile)
    EditText mobile;
    @BindView(R.id.email)
    EditText email;
    File imgFile = null;
    @BindView(R.id.save)
    Button save;
    @BindView(R.id.change_password)
    TextView changePassword;
    private ProfilePresenter<ProfileActivity> profilePresenter = new ProfilePresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_profile;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        profilePresenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.profile));

        showLoading();
        profilePresenter.profile();
        Glide
                .with(activity())
                .load(SharedHelper.getKey(activity(), "picture"))
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        EasyImage.handleActivityResult(requestCode, resultCode, data, ProfileActivity.this, new DefaultCallback() {
            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
                imgFile = imageFiles.get(0);
                Glide.with(activity())
                        .load(Uri.fromFile(imgFile))
                        .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).dontAnimate().error(R.drawable.ic_user_placeholder))
                        .into(picture);
            }
        });

    }

    @OnClick({R.id.picture, R.id.save, R.id.change_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.picture:
                if (hasPermission(Manifest.permission.CAMERA) && hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    pickImage();
                else
                    requestPermissionsSafely(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, ASK_MULTIPLE_PERMISSION_REQUEST_CODE);
                break;
            case R.id.save:
                updateProfile();
                break;
            case R.id.change_password:
                startActivity(new Intent(this, ChangePasswordActivity.class));
                break;
        }
    }

    private void updateProfile() {
        if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return;
        } else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return;
        } else if (mobile.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_mobile), Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("first_name", RequestBody.create(MediaType.parse("text/plain"), firstName.getText().toString()));
        map.put("last_name", RequestBody.create(MediaType.parse("text/plain"), lastName.getText().toString()));
        map.put("email", RequestBody.create(MediaType.parse("text/plain"), email.getText().toString()));
        map.put("mobile", RequestBody.create(MediaType.parse("text/plain"), mobile.getText().toString()));
        map.put("device_token", RequestBody.create(MediaType.parse("text/plain"), "123456"));


        MultipartBody.Part filePart = null;
        if (imgFile != null)
            try {
                // TODO: 25/08/21 commented when dependency updated
                File compressedImageFile = new Compressor(this).compressToFile(imgFile);
                filePart = MultipartBody.Part.createFormData("picture", compressedImageFile.getName(),
                        RequestBody.create(MediaType.parse("image"), compressedImageFile));

//                filePart = MultipartBody.Part.createFormData("picture", imgFile.getName(),
//                        RequestBody.create(MediaType.parse("image"), imgFile));

            } catch (Exception e) {
                e.printStackTrace();
            }
        showLoading();
        profilePresenter.update(map, filePart);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean permission1 = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean permission2 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (permission1 && permission2) {
                        pickImage();
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.please_give_permissions, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        try {
            hideLoading();
            //Toasty.success(this, "Profile updated successfully", Toast.LENGTH_SHORT, true).show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String loginBy = user.getLoginBy();
        SharedHelper.putKey(this,"lang",user.getLanguage());
        if(loginBy != null){
            if(loginBy.equalsIgnoreCase("facebook") ||
                    loginBy.equalsIgnoreCase("google")) {
                changePassword.setVisibility(View.INVISIBLE);
            } else {
                changePassword.setVisibility(View.VISIBLE);
            }
        }
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());
        SharedHelper.putKey(this,"stripe_publishable_key",user.getStripePublishableKey());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        Glide.with(activity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    @Override
    public void onSuccessUpdate(@NonNull User user) {
        try {
            hideLoading();
            Toasty.success(this, getString(R.string.profile_updated_success), Toast.LENGTH_SHORT, true).show();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        String loginBy = user.getLoginBy();
        SharedHelper.putKey(this,"lang",user.getLanguage());
        if(loginBy.equalsIgnoreCase("facebook") ||
                loginBy.equalsIgnoreCase("google")) {
            changePassword.setVisibility(View.INVISIBLE);
        } else {
            changePassword.setVisibility(View.VISIBLE);
        }
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        mobile.setText(user.getMobile());
        email.setText(user.getEmail());
        SharedHelper.putKey(this,"stripe_publishable_key",user.getStripePublishableKey());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        Glide.with(activity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions
                        .placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate().error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        profilePresenter.onDetach();
        super.onDestroy();
    }

    @OnClick(R.id.cardTrue)
    public void onCardTrueClicked() {
        RIDE_REQUEST.put("payment_mode", "CARD");
        BaseActivity.isCard = true;
    }

    @OnClick(R.id.cardfalse)
    public void onCardFalseClicked() {
        RIDE_REQUEST.remove("payment_mode");
        BaseActivity.isCard = false;
    }

    @OnClick(R.id.cashTrue)
    public void onCashTrueClicked() {
        RIDE_REQUEST.put("payment_mode", "CASH");
        BaseActivity.isCash = true;
    }

    @OnClick(R.id.cashFalse)
    public void onCashFalseClicked() {
        RIDE_REQUEST.remove("payment_mode");
        BaseActivity.isCash = false;
    }
}
