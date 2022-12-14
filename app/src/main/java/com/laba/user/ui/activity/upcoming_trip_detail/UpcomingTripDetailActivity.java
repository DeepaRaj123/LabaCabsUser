package com.laba.user.ui.activity.upcoming_trip_detail;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.common.CancelRequestInterface;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.Provider;
import com.laba.user.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.laba.user.MvpApplication.PERMISSIONS_REQUEST_PHONE;

public class UpcomingTripDetailActivity extends BaseActivity implements
        CancelRequestInterface, UpcomingTripDetailsIView {

    @BindView(R.id.static_map)
    ImageView staticMap;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.schedule_at)
    TextView scheduleAt;
    @BindView(R.id.s_address)
    TextView sAddress;
    @BindView(R.id.d_address)
    TextView dAddress;
    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.payment_image)
    ImageView paymentImage;

    private CancelRequestInterface callback;
    private UpcomingTripDetailsPresenter<UpcomingTripDetailActivity> presenter = new UpcomingTripDetailsPresenter();
    String providerPhoneNumber = null;

    @Override
    public int getLayoutId() {
        return R.layout.activity_upcoming_trip_detail;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        // Activity title will be updated after the locale has changed in Runtime
        setTitle(getString(R.string.upcoming_trip_details));
        callback = this;
        if (DATUM != null) {
            Datum datum = DATUM;

            showLoading();
            Log.e("testingTrip", String.valueOf(datum.getId()));

            presenter.getUpcomingTripDetails(datum.getId());

        }
    }

    @OnClick({R.id.cancel, R.id.call})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
                cancelRideDialogFragment.show(getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                break;
            case R.id.call:
                callProvider();
                break;
        }
    }

    private void callProvider() {
        if (providerPhoneNumber != null && !providerPhoneNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + providerPhoneNumber));
            startActivity(intent);
        }
    }

    @Override
    public void cancelRequestMethod() {
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(activity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onSuccess(List<Datum> upcomingTripDetails) {

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        Datum datum = upcomingTripDetails.get(0);
        if (datum != null) {
            bookingId.setText(datum.getBookingId());
            scheduleAt.setText(datum.getScheduleAt());
            Glide.with(activity()).load(datum.getStaticMap()).apply(RequestOptions.placeholderOf(R.drawable.ic_launcher_background).dontAnimate().error(R.drawable.ic_launcher_background)).into(staticMap);
            initPayment(datum.getPaymentMode(), paymentMode, paymentImage);
            sAddress.setText(datum.getSAddress());
            dAddress.setText(datum.getDAddress());

            Provider provider = datum.getProvider();
            if (provider != null) {
                providerPhoneNumber = provider.getMobile();
                Glide.with(activity()).load(provider.getAvatar()).apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder).dontAnimate().error(R.drawable.ic_user_placeholder)).into(avatar);

                name.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
                rating.setRating(Float.parseFloat(provider.getRating()));
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }
}
