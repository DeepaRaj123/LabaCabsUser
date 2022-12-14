package com.laba.user.ui.fragment.service;

import static android.content.Context.MODE_PRIVATE;
import static com.laba.user.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.laba.user.MvpApplication.mLastKnownLocation;
import static com.laba.user.base.BaseActivity.RIDE_REQUEST;
import static com.laba.user.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.laba.user.R;
import com.laba.user.base.BaseFragment;
import com.laba.user.common.EqualSpacingItemDecoration;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.APIClient;
import com.laba.user.data.network.model.EstimateFare;
import com.laba.user.data.network.model.Provider;
import com.laba.user.data.network.model.Service;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.activity.payment.PaymentActivity;
import com.laba.user.ui.adapter.ServiceAdapter;
import com.laba.user.ui.fragment.RateCardFragment;
import com.laba.user.ui.fragment.book_ride.BookRideFragment;
import com.laba.user.ui.fragment.schedule.ScheduleFragment;
import com.laba.user.data.network.model.Datum;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceFragment extends BaseFragment implements ServiceIView {

    private final ServicePresenter<ServiceFragment> presenter = new ServicePresenter<>();

    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.no_service_text)
    TextView noServiceText;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    @BindView(R.id.surgeLayout)
    LinearLayout surgeLayout;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
//    @BindView(R.id.viewlayout)
//    LinearLayout viewlayout;


    Unbinder unbinder;
    ServiceAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    List<Service> mEstimatedFare = new ArrayList<>();

    List<Integer> mDuration = new ArrayList<Integer>();
    List<String> mEstimatedTime = new ArrayList<String>();
    List<String> list1 = new ArrayList<String>();
    List<String> list2 = new ArrayList<String>();

    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;
    @BindView(R.id.get_pricing)
    Button proceedBt;
    @BindView(R.id.scrolllayout)
    LinearLayout scrolllayout;


    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private double walletAmount;
    private int surge;
    Set<String> set = new HashSet<String>();
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor myEdit;
    String dropOffTime;
    String arrivalTime;
    List<String> AvailableOrNot = new ArrayList<>();
    List<String> AvailableTime = new ArrayList<>();
    private int delay = 100;
    private Runnable runnable;
    private Handler handler;
    public static Datum DATUM = null;
    LocationManager locationManager;
    String latitude, longitude;
    Boolean swipeStatus = false;
    public Criteria criteria;
    public String bestProvider;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    Boolean isOnclick = false;

    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        presenter.services(map);
        serviceRv.setVisibility(View.VISIBLE);
        noServiceText.setVisibility(View.GONE);
        sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        myEdit = sharedPreferences.edit();
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLocationPermission();
        getLocation();
        final float[] firstX_point = new float[1];
        final float[] firstY_point = new float[1];

        Log.e("MySharedPref", String.valueOf(proceedBt.getY()));

//        scrolllayout.setOnTouchListener(new View.OnTouchListener() {
//             @Override
//             public boolean onTouch(View view, MotionEvent motionEvent) {
//                 if(isOnclick){
//
//                 }else{
//                 ViewGroup.LayoutParams params = serviceRv.getLayoutParams();
//                 if(swipeStatus){
//                     params.height = 850;
//                     swipeStatus = false;
//                       // proceedBt.setY(1490.0F);
//
//                 }else{
//                     params.height = 1350;
//                     swipeStatus = true;
//                   //  proceedBt.setY(990.0F);
//
//                 }
//
//
//                     serviceRv.setLayoutParams(params);
//                 }
//                 return false;
//             }
//         });

//        serviceRv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                isOnclick = true;
//                Log.e("hours", String.valueOf(true));
//            }
//        });
//
//        serviceRv.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                isOnclick = true;
//                return false;
//            }
//        });
        return view;
    }

    public static boolean isLocationEnabled(Context context) {
        //...............
        return true;
    }


    private void getLocation() {
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener((Activity) getContext(), task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        LatLng deviceLocation = new LatLng(
                                mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        latitude = String.valueOf(mLastKnownLocation.getLatitude());
                        longitude = String.valueOf(mLastKnownLocation.getLongitude());

                        SharedHelper.putKey(activity(), "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(activity(), "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        latitude = "-33.8523341";
                        longitude = "151.2106085";
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getLocalizedMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this.activity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }


    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) getActivity()).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null && service.isAvailable()) {
                        RIDE_REQUEST.put("service_type", service.getId());
                        if (RIDE_REQUEST.containsKey("service_type") && RIDE_REQUEST.get("service_type") != null) {
                            Log.e("SERVICES", "Send Request");
                            showLoading();
                            estimatedApiCall();
                        }
                    } else {
                        Toasty.error(getContext(), "No Cars Available!").show();
                    }
//                    else {
//                        requireActivity().onBackPressed();
//                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) getActivity()).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            default:
                break;
        }
    }


    public void estimatedApiCall() {

        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);

        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {

                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();

                    RateCardFragment.SERVICE = estimateFare.getService();
                    mEstimateFare = estimateFare;


                    surge = estimateFare.getSurge();
                    walletAmount = estimateFare.getWalletBalance();
                    SharedHelper.putKey(getContext(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(
                                SharedHelper.getKey(getContext(), "currency") + " "
                                        + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }

                    if (isFromAdapter) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());

                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else {
                        if (adapter != null) {
                            isFromAdapter = false;
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
                                bundle.putDouble("use_wallet", walletAmount);
                                BookRideFragment bookRideFragment = new BookRideFragment();
                                bookRideFragment.setArguments(bundle);
                                ((MainActivity) getActivity()).changeFragment(bookRideFragment);
                            }
                        }
                    }
                } else if (response.raw().code() == 500) {
                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }

            }


            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
                onErrorBase(t);
                System.out.println(" call = [" + call + "], t = [" + t + "]");

            }

        });

    }

    public void estimatedApiCall1(int i) {

        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);

        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {

                try {
                    hideLoading();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();

                    RateCardFragment.SERVICE = estimateFare.getService();
                    mEstimateFare = estimateFare;
                    mEstimatedTime.add(String.valueOf(response.body().getEstimatedFare()));
                    mDuration.add(i);
                    surge = estimateFare.getSurge();
                    walletAmount = estimateFare.getWalletBalance();
                    SharedHelper.putKey(getContext(), "wallet", String.valueOf(estimateFare.getWalletBalance()));
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(
                                SharedHelper.getKey(getContext(), "currency") + " "
                                        + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }

                    if (isFromAdapter && adapter!=null) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());

                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else {
                        if (adapter != null) {
                            isFromAdapter = false;
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
                                bundle.putDouble("use_wallet", walletAmount);
                                BookRideFragment bookRideFragment = new BookRideFragment();
                                bookRideFragment.setArguments(bundle);
                                ((MainActivity) getActivity()).changeFragment(bookRideFragment);
                            }
                        }
                    }
                } else if (response.raw().code() == 500) {
                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {

                onErrorBase(t);
                System.out.println(" call = [" + call + "], t = [" + t + "]");

            }

        });

    }


    @Override
    public void onSuccess(List<Service> services) {

        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put("service_type", 1);
            mServices.clear();
            int firstAvailable = 0;

            mServices.addAll(services);

            serviceRv.setVisibility(View.VISIBLE);
            noServiceText.setVisibility(View.GONE);
            Boolean availableflag;
            int i;
            String message = null;
//            for(int j=0;j<=mServices.size();j++){
//                if (mServices.get(j).isAvailable()) {
//                    String result  = String.valueOf(1);
//                    AvailableTime.add(String.valueOf(result + "min away"));
//
//                } else {
//
//                }
//                RIDE_REQUEST.put("service_type", mServices.get(j).getId());
//                Log.e("testData1", String.valueOf(mServices.get(j).getId()));
//                estimatedApiCall1(j);
//
//            }
            for (i = 0; i < mServices.size(); i++) {
                RIDE_REQUEST.put("service_type", mServices.get(i).getId());
                estimatedApiCall1(i);
                if (mServices.get(i).isAvailable()) {

                    Location startPoint = new Location("locationA");
//                    startPoint.setLatitude((Double) RIDE_REQUEST.get("s_latitude"));
//                    startPoint.setLongitude((Double) RIDE_REQUEST.get("s_longitude"));
                    Location endPoint = new Location("locationB");
                    endPoint.setLatitude((Double) RIDE_REQUEST.get("s_latitude"));
                    endPoint.setLongitude((Double) RIDE_REQUEST.get("s_longitude"));
                    if(SharedHelper.getProviders(requireActivity()).isEmpty()){

                    }else{
                        startPoint.setLatitude(Double.parseDouble(String.valueOf(SharedHelper.getProviders(requireActivity()).get(i).getLatitude())));
                        startPoint.setLongitude(Double.parseDouble(String.valueOf(SharedHelper.getProviders(requireActivity()).get(i).getLongitude())));
                        float distance = startPoint.distanceTo(endPoint) / 1000;

                        double estimatedDriveTimeInMinutes = distance * 3;
                        String result;
                        estimatedDriveTimeInMinutes = (int) (Math.round(estimatedDriveTimeInMinutes));
                        if(estimatedDriveTimeInMinutes<60)
                        result = String.valueOf(estimatedDriveTimeInMinutes);
                        else
                            result = String.valueOf(10);

                        result = (result.indexOf(".") >= 0 ? result.replaceAll("\\.?0+$", "") : result);
                        AvailableTime.add(String.valueOf(result + "min away"));
                    }

                    Log.e("testData", String.valueOf(SharedHelper.getProviders(requireActivity())));





                } else {

                }


            }


            adapter = new ServiceAdapter(getActivity(), mServices, mDuration, mEstimatedTime, mListener, capacity, mEstimateFare, dropOffTime, AvailableTime, new ServiceAdapter.ServiceAdapterOnClickListener() {
                @Override
                public void adapterClick(Service service) {
                    //    Log.e("CHOOSE1", String.valueOf(1));

                    proceedBt.setTextColor(getResources().getColor(R.color.defaultTextColor));
                    proceedBt.setText(service.isAvailable() ?
                            "CHOOSE " + service.getName()
                            : "NO CARS AVAILABLE"
                    );
                }
            }, 0);

            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.VERTICAL));
            serviceRv.setAdapter(adapter);


            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) {
                    RIDE_REQUEST.put("service_type", mService.getId());
                    proceedBt.setTextColor(getResources().getColor(R.color.defaultTextColor)
                    );
                    proceedBt.setText(mService.isAvailable() ?
                            "CHOOSE " + adapter.getServiceTypeName(firstAvailable)
                            : "NO CARS AVAILABLE"
                    );
                }

            }

            mListener.whenClicked(0);


        } else {
            proceedBt.setText("CLOSE");
            serviceRv.setVisibility(View.GONE);
            noServiceText.setVisibility(View.VISIBLE);
        }

    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(EstimateFare estimateFare) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (estimateFare != null) {
            mEstimateFare = estimateFare;
            double walletAmount = estimateFare.getWalletBalance();
            SharedHelper.putKey(getContext(), "wallet",
                    String.valueOf(estimateFare.getWalletBalance()));
            if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
            else {
                walletBalance.setVisibility(View.VISIBLE);
                walletBalance.setText(
                        SharedHelper.getKey(getContext(), "currency") + " "
                                + getNewNumberFormat(Double.parseDouble(String.valueOf(walletAmount))));
            }
            if (estimateFare.getSurge() == 0) {
                surgeValue.setVisibility(View.GONE);
                tvDemand.setVisibility(View.GONE);
            } else {
                surgeValue.setVisibility(View.VISIBLE);
                surgeValue.setText(estimateFare.getSurgeValue());
                tvDemand.setVisibility(View.VISIBLE);
            }
            if (isFromAdapter) {
                mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());

                RIDE_REQUEST.put("distance", estimateFare.getDistance());
                adapter.notifyDataSetChanged();
                if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                else errorLayout.setVisibility(View.GONE);
            } else {
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("service_name", service.getName());
                        bundle.putSerializable("mService", service);
                        bundle.putSerializable("estimate_fare", estimateFare);
                        bundle.putDouble("use_wallet", walletAmount);
                        BookRideFragment bookRideFragment = new BookRideFragment();
                        bookRideFragment.setArguments(bundle);
                        ((MainActivity) getActivity()).changeFragment(bookRideFragment);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private final ServiceListener mListener = pos -> {
        isFromAdapter = true;
        servicePos = pos;
        RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
        showLoading();
        estimatedApiCall();
        List<Provider> providers = new ArrayList<>();
        for (Provider provider : SharedHelper.getProviders(requireActivity())) {
            if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId())) {

                providers.add(provider);
            }
        }
        ((MainActivity) getActivity()).setSpecificProviders(providers);
    };

    public interface ServiceListener {
        void whenClicked(int pos);
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        try {
            hideLoading();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        activity().sendBroadcast(new Intent("INTENT_FILTER"));
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }

}
