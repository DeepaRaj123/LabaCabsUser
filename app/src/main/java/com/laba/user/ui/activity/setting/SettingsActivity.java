package com.laba.user.ui.activity.setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.common.Constants;
import com.laba.user.common.LocaleHelper;
import com.laba.user.data.network.model.Address;
import com.laba.user.data.network.model.AddressResponse;
import com.laba.user.ui.activity.location_pick.LocationPickActivity;
import com.laba.user.ui.activity.main.MainActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.laba.user.MvpApplication.PICK_LOCATION_REQUEST_CODE;

import androidx.annotation.NonNull;

public class SettingsActivity extends BaseActivity implements SettingsIView {

    @BindView(R.id.choose_language)
    RadioGroup chooseLanguage;
    @BindView(R.id.english)
    RadioButton english;
    @BindView(R.id.arabic)
    RadioButton arabicRb;
    @BindView(R.id.french)
    RadioButton frenchRb;
    @BindView(R.id.home_status)
    TextView homeStatus;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_status)
    TextView workStatus;
    @BindView(R.id.work_address)
    TextView workAddress;


    private String type = "home";
    private String language;
    private SettingsPresenter<SettingsActivity> presenter = new SettingsPresenter<>();
    private Address work = null, home = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);

        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.settings));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        showLoading();
        presenter.address();

        languageReset();

        chooseLanguage.setOnCheckedChangeListener((group, checkedId) -> {
            showLoading();
            switch (checkedId) {
                case R.id.english:
                    language = Constants.Language.ENGLISH;
                    break;
                case R.id.arabic:
                    language = Constants.Language.ARABIC;
                    break;
                case R.id.french:
                    language = Constants.Language.FRENCH;
                    break;
                default:
                    language = Constants.Language.ARABIC;
            }
            presenter.changeLanguage(language);

        });
    }

    private void languageReset() {
        String dd = LocaleHelper.getLanguage(getApplicationContext());
        switch (dd) {
            case Constants.Language.ENGLISH:
                english.setChecked(true);
                break;
            case Constants.Language.FRENCH:
                frenchRb.setChecked(true);
                break;
            case Constants.Language.ARABIC:
                arabicRb.setChecked(true);
                break;
            default:
                arabicRb.setChecked(true);
                break;
        }
    }

    @Override
    public void onSuccessAddress(Object object) {
        presenter.address();
    }

    @Override
    public void onLanguageChanged(Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        LocaleHelper.setLocale(getApplicationContext(), language);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK));
        this.overridePendingTransition(R.anim.rotate_in,R.anim.rotate_out);
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) {
            homeAddress.setText("");
            homeStatus.setText(getString(R.string.add));
            homeStatus.setTag("add");
            home = null;
        } else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeStatus.setText(getString(R.string.delete));
        }

        if (address.getWork().isEmpty()) {
            workAddress.setText("");
            workStatus.setText(getString(R.string.add));
            workStatus.setTag("add");
            work = null;
        } else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workStatus.setText(getString(R.string.delete));
        }
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @OnClick({R.id.home_status, R.id.work_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.home_status:
                if (home == null) {
                    type = "home";
                    Intent intent = new Intent(this, LocationPickActivity.class);
                    intent.putExtra("isSetting","homeSetting");
                    intent.putExtra("isHideDestination", true);
                    startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.are_sure_you_want_to_delete))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> {
                                showLoading();
                                presenter.deleteAddress(home.getId(), new HashMap<>());
                            }).setNegativeButton(getString(R.string.no), null).show();
                }
                break;
            case R.id.work_status:
                if (work == null) {
                    type = "work";
                    Intent workIntent = new Intent(this, LocationPickActivity.class);
                    workIntent.putExtra("isSetting","workSetting");
                    workIntent.putExtra("isHideDestination", true);
                    startActivityForResult(workIntent, PICK_LOCATION_REQUEST_CODE);
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage(getString(R.string.are_sure_you_want_to_delete))
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(getString(R.string.delete), (dialog, whichButton) -> {
                                showLoading();
                                presenter.deleteAddress(work.getId(), new HashMap<>());
                            }).setNegativeButton(getString(R.string.no), null).show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_LOCATION_REQUEST_CODE) if (resultCode == Activity.RESULT_OK) {
            if(data != null) {
                showLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("type", type);
                map.put("address", data.getStringExtra("s_address"));
                map.put("latitude", data.getDoubleExtra("s_latitude",0.0));
                map.put("longitude", data.getDoubleExtra("s_longitude",0.0));
                presenter.addAddress(map);
            }

        }
           /* if (RIDE_REQUEST.containsKey("s_address")) {
                showLoading();
                HashMap<String, Object> map = new HashMap<>();
                map.put("type", type);
                map.put("address", RIDE_REQUEST.get("s_address"));
                map.put("latitude", RIDE_REQUEST.get("s_latitude"));
                map.put("longitude", RIDE_REQUEST.get("s_longitude"));
                presenter.addAddress(map);
            }*/
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        finishAffinity();
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        return true;
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}