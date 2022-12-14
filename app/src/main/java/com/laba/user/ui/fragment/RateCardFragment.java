package com.laba.user.ui.fragment;

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.laba.user.R;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.Constants;
import com.laba.user.common.Utilities;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.Service;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RateCardFragment extends BaseFragment {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.base_fare)
    TextView baseFare;
    @BindView(R.id.fare_type)
    TextView fareType;
    @BindView(R.id.fare_km)
    TextView fareKm;
    @BindView(R.id.tvFareDistance)
    TextView tvFareDistance;

    @BindView(R.id.description)
    TextView tvDescription;

    Unbinder unbinder;

    public static Service SERVICE = new Service();

    public RateCardFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rate_card;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        initView(SERVICE);
        return view;
    }

    @SuppressLint("SetTextI18n")
    void initView(@NonNull Service service) {
        capacity.setText(String.valueOf(service.getCapacity()));
        baseFare.setText(SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency") + " " +
                getNewNumberFormat(service.getFixed()));
        fareKm.setText(SharedHelper.getKey(getContext(), "currency") + " " +
                getNewNumberFormat(service.getPrice()));

        tvDescription.setText(service.getDescription());

        //      MIN,    HOUR,   DISTANCE,   DISTANCEMIN,    DISTANCEHOUR
        switch (service.getCalculator()) {
            case Utilities.InvoiceFare.min:
                fareType.setText(getString(R.string.min));
                break;
            case Utilities.InvoiceFare.hour:
                fareType.setText(getString(R.string.hour));
                break;
            case Utilities.InvoiceFare.distance:
                fareType.setText(getString(R.string.distance));
                break;
            case Utilities.InvoiceFare.distanceMin:
                fareType.setText(getString(R.string.distancemin));
                break;
            case Utilities.InvoiceFare.distanceHour:
                fareType.setText(getString(R.string.distancehour));
                break;
            default:
                fareType.setText(getString(R.string.min));
                break;
        }

        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM))
            tvFareDistance.setText(getString(R.string.fare_km));
        else tvFareDistance.setText(getString(R.string.fare_miles));

        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(image);
        Glide.with(activity()).load(service.getImage()).into(image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
