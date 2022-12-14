package com.laba.user.ui.activity.main;

import static com.google.android.gms.common.util.CollectionUtils.listOf;
import static com.laba.user.MvpApplication.DEFAULT_ZOOM;
import static com.laba.user.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.laba.user.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.laba.user.MvpApplication.mLastKnownLocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransitMode;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.model.Waypoint;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.maps.model.TransitRoutingPreference;
import com.laba.user.BuildConfig;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.base.BaseBottomSheetDialogFragment;
import com.laba.user.common.Constants;
import com.laba.user.common.InfoWindowData;
import com.laba.user.common.LocaleHelper;
import com.laba.user.common.MarkerCarUtils;
import com.laba.user.common.Utilities;
import com.laba.user.common.fcm.ForceUpdateChecker;
import com.laba.user.common.fcm.MyFireBaseMessagingService;
import com.laba.user.data.SharedHelper;
import com.laba.user.data.network.model.AddressResponse;
import com.laba.user.data.network.model.DataResponse;
import com.laba.user.data.network.model.Datum;
import com.laba.user.data.network.model.InitSettingsResponse;
import com.laba.user.data.network.model.Provider;
import com.laba.user.data.network.model.User;
import com.laba.user.ui.activity.coupon.CouponActivity;
import com.laba.user.ui.activity.help.HelpActivity;
import com.laba.user.ui.activity.location_pick.LocationPickActivity;
import com.laba.user.ui.activity.passbook.WalletHistoryActivity;
import com.laba.user.ui.activity.payment.PaymentActivity;
import com.laba.user.ui.activity.profile.ProfileActivity;
import com.laba.user.ui.activity.setting.SettingsActivity;
import com.laba.user.ui.activity.wallet.WalletActivity;
import com.laba.user.ui.activity.your_trips.YourTripActivity;
import com.laba.user.ui.dialog.DriverRideCancelledDialog;
import com.laba.user.ui.dialog.DriverRideCancelledAfterRideDialog;
import com.laba.user.ui.dialog.ScheduleSuccessfulDialog;
import com.laba.user.ui.fragment.RateCardFragment;
import com.laba.user.ui.fragment.book_ride.BookRideFragment;
import com.laba.user.ui.fragment.invoice.InvoiceFragment;
import com.laba.user.ui.fragment.rate.RatingDialogFragment;
import com.laba.user.ui.fragment.schedule.ScheduleFragment;
import com.laba.user.ui.fragment.searching.SearchingFragment;
import com.laba.user.ui.fragment.service.ServiceFragment;
import com.laba.user.ui.fragment.service_flow.ServiceFlowFragment;
import com.laba.user.ui.fragment.service_timeout.ServiceTimeoutFragment;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.imperiumlabs.geofirestore.GeoFirestore;
import org.imperiumlabs.geofirestore.GeoQuery;
import org.imperiumlabs.geofirestore.listeners.GeoQueryEventListener;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.HttpException;
import retrofit2.Response;


public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, DirectionCallback,
        MainIView,
        LocationListener, ForceUpdateChecker.OnUpdateNeededListener, PaymentResultListener {

    private static final String TAG = "MainActivity";
    public static String currentStatus = "EMPTY";

    @BindView(R.id.container)
    FrameLayout container;

    @BindView(R.id.textView)
    TextView bookARideNow;


    @BindView(R.id.textView2)
    public TextView sourceAddressTx;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.source)
    TextView sourceTxt;
    @BindView(R.id.destination)
    TextView destinationTxt;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;

    @BindView(R.id.ll_srcdesimages)
    LinearLayout ll_srcdesimages;

    @BindView(R.id.src_dot)
    ImageView src_dot;

    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;
    @BindView(R.id.llPickHomeAdd)
    LinearLayout llPickHomeAdd;
    @BindView(R.id.llPickWorkAdd)
    LinearLayout llPickWorkAdd;
    boolean check_current_location = false;
    Location mLocation;
    LatLng oldPosition = null;
    private InfoWindowData destinationLeg;
    private PlacesClient placesClient;
    private boolean doubleBackToExitPressedOnce = false;
    private LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;
    private BottomSheetBehavior bottomSheetBehavior;
    private final MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();
    private CircleImageView picture;
    private TextView name;
    private String STATUS = "";
    private TextView sub_name;
    private boolean initialProcess = true;
    private LatLng newPosition = null;
    private Marker marker;
    private final HashMap<Integer, Marker> providersMarker = new HashMap<>();
    private DataResponse checkStatusResponse = new DataResponse();
    private Runnable r;
    private Handler h;
    private final int delay = 5000;
    @Nullable
    private List<LatLng> waypointList;
    private com.laba.user.data.network.model.Address home = null, work = null;
    private DatabaseReference mProviderLocation;
    private final BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainPresenter.checkStatus(1);
        }
    };
    private final boolean adjustBounds = true;
    private List<Provider> specificProviders;
    private GeoFirestore mGeoFire;
    private GeoQuery mGeoQuery;
    MarkerCarUtils markerCarUtils;

    // layout
    @BindView(R.id.book_a_ride_layout)
    LinearLayout bookARideLayout;

    // home address layout
    @BindView(R.id.imageView4)
    ImageView homeIconImg;
    @BindView(R.id.textView4)
    TextView homeNameTx;
    @BindView(R.id.textView5)
    TextView homeAddressTx;
    @BindView(R.id.imageView5)
    ImageView homeArrowImg;

    // work address layout
    @BindView(R.id.imageView6)
    ImageView workIconImg;
    @BindView(R.id.textView6)
    TextView workNameTx;
    @BindView(R.id.textView7)
    TextView workAddressTx;
    @BindView(R.id.imageView7)
    ImageView workArrowImg;


    GeoQueryEventListener geoQueryListener = new GeoQueryEventListener() {
        public void onGeoQueryError(Exception error) {
        }

        public void onGeoQueryReady() {
        }

        public void onKeyEntered(String param1String, GeoPoint param1GeoPoint) {
            addDriverMarkers(param1String, param1GeoPoint);
        }

        public void onKeyExited(String key) {
            try {
                int i = Integer.parseInt(key);
                if (providersMarker.containsKey(i)) {
                    // providersMarker.get(i).remove();
                    Marker cabMarker = providersMarker.get(i);
                    cabMarker.remove();
                    providersMarker.remove(i);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        public void onKeyMoved(String param1String, GeoPoint param1GeoPoint) {
            addDriverMarkers(param1String, param1GeoPoint);
        }
    };


    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        this.mGeoFire = new GeoFirestore(FirebaseFirestore.getInstance().collection("DriverLocation"));
//        homeIconImg = findViewById(R.id.imageView4);
//        homeNameTx = findViewById(R.id.textView4);
//        homeAddressTx = findViewById(R.id.textView5);
//        homeArrowImg = findViewById(R.id.imageView5);

//        workIconImg = findViewById(R.id.imageView6);
//        workNameTx = findViewById(R.id.textView6);
//        workAddressTx = findViewById(R.id.textView7);
//        workArrowImg = findViewById(R.id.imageView7);

//        profilePresenter.attachView(this);
//        profilePresenter.profile();


        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                    WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        }

        ButterKnife.bind(this);

        ForceUpdateChecker.with(this).onUpdateNeeded(this).check();
        registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_FILTER));

        mainPresenter.attachView(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        picture = headerView.findViewById(R.id.picture);
        name = headerView.findViewById(R.id.name);
        sub_name = headerView.findViewById(R.id.sub_name);
        headerView.setOnClickListener(v -> {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, picture, ViewCompat.getTransitionName(picture));
            startActivity(new Intent(MainActivity.this, ProfileActivity.class), options.toBundle());
        });
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        waypointList = new ArrayList<>();
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(container);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.e("hours1","hours1");
                        BottomSheetBehavior.from(container).setHideable(false);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.e("hours","hours");
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        h = new Handler();
        r = () -> {
            mainPresenter.checkStatus(2);
            h.postDelayed(r, delay);
        };
        h.postDelayed(r, delay);

        mainPresenter.address();

        String here = getIntent().getStringExtra("move_to");
        if (here != null) {
            Intent in = new Intent(getApplicationContext(), YourTripActivity.class);
            in.putExtra("move_to", "upcomming_trip");
            startActivity(in);
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    private final BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.e("GPSLocationUpdates", "onReceive");
            // Get extra data included in the Intent
            boolean showDialog = intent.getBooleanExtra("show_dialog", false);
            Log.e("show_dialog", "" + showDialog);
            if (showDialog) {
                new ScheduleSuccessfulDialog(getSupportFragmentManager());
            }

            boolean rideCancelled = intent.getBooleanExtra("ride_cancelled", false);
            Log.e("ride_cancelled", "" + rideCancelled);
            if (rideCancelled) {
                new DriverRideCancelledDialog(getSupportFragmentManager());
            }
            boolean rideCancelledAfterRide = intent.getBooleanExtra("ride_cancelled_after_ride", false);
            if (rideCancelledAfterRide) {
                new DriverRideCancelledAfterRideDialog(getSupportFragmentManager());
            }

//            String message = intent.getStringExtra("Status");
//            Bundle b = intent.getBundleExtra("Location");
//            lastKnownLoc = (Location) b.getParcelable("Location");
//            if (lastKnownLoc != null) {
//                tvLatitude.setText(String.valueOf(lastKnownLoc.getLatitude()));
//                tvLongitude
//                        .setText(String.valueOf(lastKnownLoc.getLongitude()));
//                tvAccuracy.setText(String.valueOf(lastKnownLoc.getAccuracy()));
//                tvTimestamp.setText((new Date(lastKnownLoc.getTime())
//                        .toString()));
//                tvProvider.setText(lastKnownLoc.getProvider());
//            }
//            tvStatus.setText(message);
            // Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.profile();
        mainPresenter.address();
        mainPresenter.checkStatus(3);
        mainPresenter.settings();
        showCurrentPlace();

       /* getDeviceLocation();
        showCurrentPlace();*/
    }

    @Override
    protected void onDestroy() {
        mainPresenter.onDetach();
        unregisterReceiver(myReceiver);
        h.removeCallbacks(r);
        RIDE_REQUEST.clear();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);


        sourceTxt.setVisibility(View.VISIBLE);
        ll_srcdesimages.setVisibility(View.VISIBLE);
        src_dot.setVisibility(View.GONE);


        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else {
            if (getSupportFragmentManager().findFragmentById(R.id.container)
                    instanceof ServiceFlowFragment) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                else {
                    if (doubleBackToExitPressedOnce) {
                        currentStatus = "EMPTY";
                        finishAffinity();
                        return;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, getString(R.string.please_click_back_again_to_exit),
                            Toast.LENGTH_SHORT).show();
                }
            } else if (getSupportFragmentManager().getBackStackEntryCount() > 0) {

                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mainPresenter.checkStatus(4);
                    changeFlow("EMPTY");
                    bookARideLayout.setVisibility(View.VISIBLE);
                    pickLocationLayout.setVisibility(View.GONE);
                    getDeviceLocation();
                    onCameraIdle();
//                    if (mLastKnownLocation != null) {
//                        LatLng currentLatLng  = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
//                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
//                    }
                }
            }
//            else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else {
                if (doubleBackToExitPressedOnce) {
                    currentStatus = "EMPTY";
                    onCameraIdle();
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
            }
        }

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_payment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_coupon:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_passbook:
                startActivity(new Intent(this, WalletHistoryActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_become_driver:
                alertBecomeDriver();
                break;
            case R.id.nav_logout:
                // alertLogout();
                ShowLogoutPopUp();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void ShowLogoutPopUp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    mainPresenter.logout(SharedHelper.getKey(this, "user_id"));
                }).setNegativeButton(getString(R.string.no), (dialog, id) -> {
                    dialog.cancel();
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertBecomeDriver() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.PROVIDER_PACKAGE_NAME));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    boolean clearMap = true;

    @Override
    public void onCameraIdle() {
//        if(bookARideLayout.getWindowVisibility() == View.GONE)
//            bookARideLayout.setVisibility(View.VISIBLE);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if (STATUS.equals("SERVICE") || STATUS.equals("EMPTY")) {
            try {
                //CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
                CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
                HashMap<String, Object> map = new HashMap<>();
                map.put("latitude", cameraPosition.target.latitude);
                map.put("longitude", cameraPosition.target.longitude);
                mainPresenter.providers(map);

                if (STATUS.equals("EMPTY")) {
                    if (clearMap) {
                        mGoogleMap.clear();
                        providersMarker.clear();
                    }

                    try {
                        GeoPoint geoPoint = new GeoPoint(cameraPosition.target.latitude, cameraPosition.target.longitude);
                        if (mGeoQuery == null || clearMap) {
                            mGeoQuery = mGeoFire.queryAtLocation(geoPoint, 50000.0D);
                            mGeoQuery.addGeoQueryEventListener(this.geoQueryListener);
                        } else {
                            mGeoQuery.setCenter(geoPoint);
                        }
                        //mGeoQuery.removeGeoQueryEventListener(this.geoQueryListener);
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    clearMap = false;
                } else {
                    if (mGeoQuery != null) {
                        // mGeoQuery.removeAllListeners();
                    }
                    clearMap = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            clearMap = true;
        }
    }

    @OnClick({R.id.menu, R.id.gps, R.id.source, R.id.destination, R.id.ivBack,
            R.id.llPickHomeAdd, R.id.llPickWorkAdd,
            R.id.imageView5, R.id.textView4, R.id.textView5,
            R.id.textView6, R.id.textView7, R.id.imageView7,
            R.id.textView3})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //book a ride
            case R.id.textView3:
                RIDE_REQUEST.remove("d_address");
                 Address address = getAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                if (address != null) {
                    String streetAddress = getStreetAddress(address);
                    sourceTxt.setText(streetAddress);
                    RIDE_REQUEST.put("s_address", streetAddress);
                    RIDE_REQUEST.put("s_latitude", mLastKnownLocation.getLatitude());
                    RIDE_REQUEST.put("s_longitude", mLastKnownLocation.getLongitude());
                } else {
                    Toasty.info(this, "Unable to find address.", Toast.LENGTH_SHORT).show();
                    RIDE_REQUEST.remove("s_address");
                    RIDE_REQUEST.remove("s_latitude");
                    RIDE_REQUEST.remove("s_longitude");
                }
                gotToLocationPickActivity(false);
                break;
            //home
            case R.id.imageView5:
            case R.id.textView4:
            case R.id.textView5:
                if (home == null)
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                else {
                    bookARideLayout.setVisibility(View.GONE);
                    pickLocationLayout.setVisibility(View.VISIBLE);
                    updateSavedAddress(home);
                }
                break;
            //work
            case R.id.textView6:
            case R.id.textView7:
            case R.id.imageView7:
                if (work == null)
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                else {
                    bookARideLayout.setVisibility(View.GONE);
                    pickLocationLayout.setVisibility(View.VISIBLE);
                    updateSavedAddress(work);
                }

                break;
            case R.id.menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
                    if (user != null) {
                        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                        sub_name.setText(user.getEmail());
                        SharedHelper.putKey(activity(), "picture", user.getPicture());
                        Glide.with(activity())
                                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(picture);
                    }
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                }
                break;
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                sourceIntent.putExtra("srcClick", "isSource");
                sourceIntent.putExtra("isSetting", "source");
                sourceIntent.putExtra("destination", sourceTxt.getText().toString());
                sourceIntent.putExtra("s_add", sourceAddressTx.getText().toString());
                sourceIntent.putExtra("s_lat", mLastKnownLocation.getLatitude());
                sourceIntent.putExtra("s_long", mLastKnownLocation.getLongitude());
                sourceIntent.putExtra("fieldClicked", "pickupAddress");
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.destination:
                gotToLocationPickActivity(true);
                break;
            case R.id.llPickHomeAdd:
                updateSavedAddress(home);
                break;
            case R.id.llPickWorkAdd:
                updateSavedAddress(work);
                break;
        }
    }

    private void gotToLocationPickActivity(boolean destinationClicked) {
        Log.e("destination", destinationTxt.getText().toString());
        if (mLastKnownLocation == null) {
            Toasty.error(this, "Unknown Location").show();
            return;
        }
        Intent intent = new Intent(this, LocationPickActivity.class);
        intent.putExtra("destClick", "isDest");
        intent.putExtra("isSetting", "destination");
        intent.putExtra("destination", destinationTxt.getText().toString());
        intent.putExtra("s_add", sourceAddressTx.getText().toString());
        intent.putExtra("s_lat", mLastKnownLocation.getLatitude());
        intent.putExtra("s_long", mLastKnownLocation.getLongitude());
        if (destinationClicked)
            intent.putExtra("fieldClicked", "dropAddress");
        startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
    }

    private void updateSavedAddress(com.laba.user.data.network.model.Address address) {
        RIDE_REQUEST.put("d_address", address.getAddress());
        RIDE_REQUEST.put("d_latitude", address.getLatitude());
        RIDE_REQUEST.put("d_longitude", address.getLongitude());
        destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));

        if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
            LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
            LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
            drawRoute(origin, destination);
            currentStatus = "SERVICE";
            changeFlow(currentStatus);
        }
    }

    @Override
    public void onCameraMove() {
//        if(bookARideLayout.getWindowVisibility() == View.VISIBLE)
//            bookARideLayout.setVisibility(View.GONE);
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady");

        this.mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
        showCurrentPlace();

      /*  try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        } */

    }

    public void initPayment(int amount) {
        String mail = SharedHelper.getKey(getApplicationContext(), "mail");
        String mobile = SharedHelper.getKey(getApplicationContext(), "mobile");
        Checkout checkout = new Checkout();
        checkout.setImage(R.drawable.app_icon);
        final Activity activity = this;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", getString(R.string.app_name));
            jsonObject.put("description", getString(R.string.app_name));
            jsonObject.put("currency", "INR");
            jsonObject.put("amount", amount);
            JSONObject preFill = new JSONObject();
            if (mail.equalsIgnoreCase("")) {
                preFill.put("email", "");
            } else {
                preFill.put("email", mail);
            }
            if (mobile.equalsIgnoreCase("")) {
                preFill.put("contact", "");
            } else {
                preFill.put("contact", mobile);
            }
            jsonObject.put("prefill", preFill);
            checkout.open(activity, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    private void updateDriverNavigation(double lat, double lng) {
//        try {
//            if (!canReRoot) {
//                if (lng > 0 && lat > 0) {
//                    addCar(new LatLng(lat, lng));
//
//                }
//            } else {
//                if (lng > 0 && lat > 0) {
//                    adjustBounds = false;
////                            addCar(new LatLng(lat, lng));
//                    if ("STARTED".equalsIgnoreCase(DATUM.getStatus())) {
//                        LatLng source = new LatLng(lat, lng);
//                        LatLng destination = new LatLng(DATUM.getSLatitude(), DATUM.getSLongitude());
//                        drawRoute(source, destination);
//                    } else {
//                        LatLng origin = new LatLng(lat, lng);
//                        LatLng destination = new LatLng(DATUM.getDLatitude(), DATUM.getDLongitude());
//                        drawRoute(origin, destination);
//                    }
//                }
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    public void onSuccess(DataResponse dataResponse) {

        Log.e(TAG, "CHECK STATUS: " + currentStatus + 1);
        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, "sosNumber", dataResponse.getSos());
        try {
            if (!dataResponse.getData().isEmpty()) {
                System.out.println(" MainActivity currentStatus = " + dataResponse.getData().get(0).getStatus());
                System.out.println(" MainActivity isPaid = " + dataResponse.getData().get(0).getPaid());
                Log.e(TAG, "CHECK STATUS: " + currentStatus + 2);
            }

            if (dataResponse.getData() != null && !dataResponse.getData().isEmpty() &&
                    dataResponse.getData().get(0).getProvider() != null) {
                DATUM = dataResponse.getData().get(0);
                provider = DATUM.getProvider();
                provider.setLatitude(provider.getLatitude());
                provider.setLongitude(provider.getLongitude());
                Log.e(TAG, "CHECK STATUS: " + currentStatus + 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "CHECK STATUS: " + currentStatus + 4);
        }

        if (!dataResponse.getData().isEmpty()) {
            Log.e(TAG, "CHECK STATUS: " + currentStatus + 5);
            if (!currentStatus.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);
                currentStatus = DATUM.getStatus();
                changeFlow(currentStatus);
                pickLocationLayout.setVisibility(View.GONE);
                Log.e(TAG, "CHECK STATUS: " + currentStatus + 6);
            }
        } else if (currentStatus.equals("SERVICE")) {
            //      Do nothing
            Log.e(TAG, "CHECK STATUS: " + currentStatus + 7);
        } else {
            currentStatus = "EMPTY";
            changeFlow(currentStatus);
            onCameraIdle();
            Log.e(TAG, "CHECK STATUS: " + currentStatus + 8);
        }
        if (currentStatus.equals("STARTED")
                || currentStatus.equals("ARRIVED")
                || currentStatus.equals("PICKEDUP")) {
            Log.e(TAG, "CHECK STATUS: " + currentStatus + 9);
            addCar(new LatLng(dataResponse.getData().get(0).getProvider().getLatitude(),
                    dataResponse.getData().get(0).getProvider().getLongitude()));
        }
    }

    public void changeFlow(String status) {

        STATUS = status;

        llPickHomeAdd.setVisibility(View.INVISIBLE);
        llPickWorkAdd.setVisibility(View.INVISIBLE);
        dismissDialog("SEARCHING");
        dismissDialog("INVOICE");
        dismissDialog("RATING");
        System.out.println("From status: " + STATUS);
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        switch (STATUS) {
            case "EMPTY":
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                //mGoogleMap.clear();
                //   providersMarker.clear();

                bookARideLayout.setVisibility(View.VISIBLE);
                pickLocationLayout.setVisibility(View.GONE);

                showCurrentPlace();
                //addDriverMarkers(SharedHelper.getProviders(this));
                destinationTxt.setText(getString(R.string.where_to));
                changeFragment(null);
                if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
                else llPickHomeAdd.setVisibility(View.INVISIBLE);
                if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
                else llPickWorkAdd.setVisibility(View.INVISIBLE);
                break;
            case "SERVICE":
                bookARideLayout.setVisibility(View.GONE);
                // canCallCurrentLocation = false;
                ivBack.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
                updatePaymentEntities();
                changeFragment(new ServiceFragment());
                break;
            case "SEARCHING":
                bookARideLayout.setVisibility(View.GONE);
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), "SEARCHING");
                break;
            case "STARTED":
                bookARideLayout.setVisibility(View.GONE);
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                if (DATUM != null) {
                    initialProcess = true;
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                }
                changeFragment(new ServiceFlowFragment());
                break;
            case "ARRIVED":
                bookARideLayout.setVisibility(View.GONE);
                changeFragment(new ServiceFlowFragment());
                break;
            case "PICKEDUP":
                bookARideLayout.setVisibility(View.GONE);
                changeFragment(new ServiceFlowFragment());
                break;
            case "DROPPED":
            case "COMPLETED":
                try {
                    /*if (DATUM.getPaid() == 1) {
                        currentStatus = "RATING";
                        changeFlow(currentStatus);
                    } else*/
                    bookARideLayout.setVisibility(View.GONE);
                    changeFragment(InvoiceFragment.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "RATING":
                bookARideLayout.setVisibility(View.GONE);
                RIDE_REQUEST.remove("s_address");
                RIDE_REQUEST.remove("s_latitude");
                RIDE_REQUEST.remove("s_longitude");
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
                check_current_location = false;
                getDeviceLocation();
                changeFragment(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                ratingDialogFragment.show(getSupportFragmentManager(), "RATING");
                RIDE_REQUEST.clear();
                mGoogleMap.clear();
                // TODO: 02/11/21 was visible
//                pickLocationLayout.setVisibility(View.VISIBLE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText("");
                break;
            case "TIMEOUT":
                bookARideLayout.setVisibility(View.GONE);
                getSupportFragmentManager().popBackStack();
                new ServiceTimeoutFragment().show(getSupportFragmentManager(), "SERVICE_TIMEOUT");
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;

        if (fragment != null) {
            if (fragment instanceof BookRideFragment || fragment instanceof ServiceFragment ||
                    fragment instanceof ServiceFlowFragment || fragment instanceof RateCardFragment)
                container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            else container.setBackgroundColor(getResources().getColor(R.color.white));

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceFragment)
                fragmentTransaction.addToBackStack("ServiceFragment");

            try {
                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            container.removeAllViews();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    public void dismissServiceTimeout() {

        Fragment fragment = getSupportFragmentManager().findFragmentByTag("SERVICE_TIMEOUT");
        if (fragment instanceof ServiceTimeoutFragment) {
            ServiceTimeoutFragment df = (ServiceTimeoutFragment) fragment;
            df.dismissAllowingStateLoss();
        }
        STATUS = "EMPTY";
        changeFlow(STATUS);
    }

    public void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof BaseBottomSheetDialogFragment)
            ((BaseBottomSheetDialogFragment) fragment).dismissAllowingStateLoss();
//        if (fragment instanceof SearchingFragment) {
//            SearchingFragment df = (SearchingFragment) fragment;
//            df.dismissAllowingStateLoss();
//        }
//        if (fragment instanceof RatingDialogFragment) {
//            RatingDialogFragment df = (RatingDialogFragment) fragment;
//            df.dismissAllowingStateLoss();
//        }
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    showLoading();
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        LatLng deviceLocation = new LatLng(
                                mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude());
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(deviceLocation, DEFAULT_ZOOM));
                        try {
                            hideLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String s_address = getAddress(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()));
                        if (s_address != null) {
                            sourceTxt.setText(s_address);
                            RIDE_REQUEST.put("s_address", s_address);
                            RIDE_REQUEST.put("s_latitude", mLastKnownLocation.getLatitude());
                            RIDE_REQUEST.put("s_longitude", mLastKnownLocation.getLongitude());

                        }

//                        ((TextView)findViewById(R.id.textView2)).setText(s_address);
                        sourceAddressTx.setText(s_address);
                        CURRENT_LOCATION = s_address;

                        SharedHelper.putKey(activity(), "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(activity(), "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                    } else {
                        try {
                            hideLoading();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Log.d("Map", "Current location is null. Using defaults.");
                        mDefaultLocation = new LatLng(
                                Double.valueOf(SharedHelper.getKey(activity(), "latitude", "-33.8523341")),
                                Double.valueOf(SharedHelper.getKey(activity(), "longitude", "151.2106085"))
                        );
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getLocalizedMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
                showCurrentPlace();
            }
        }
    }

    public void drawRoute(LatLng source, LatLng destination) {

        sourceTxt.setVisibility(View.VISIBLE);
        ll_srcdesimages.setVisibility(View.VISIBLE);
        src_dot.setVisibility(View.GONE);

        Log.d("drawRoute", "drawRoute: " + SharedHelper.getKey(activity(), "map_key"));

        waypointList.add(new LatLng(10.155517,76.354428));
        waypointList.add(new LatLng(10.1517834,76.3907693));

        GoogleDirection
                .withServerKey(SharedHelper.getKey(activity(), "map_key"))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    private Bitmap getMarkerBitmapFromView() {

        //HERE YOU CAN ADD YOUR CUSTOM VIEW
        View mView = ((LayoutInflater) this.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.map_custom_infowindow, null);

        //IN THIS EXAMPLE WE ARE TAKING TEXTVIEW BUT YOU CAN ALSO TAKE ANY KIND OF VIEW LIKE IMAGEVIEW, BUTTON ETC.
        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();

        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
       // tvEtaVal.setText(arrivalTime);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mView.getMeasuredWidth(),
                mView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        mView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {

        if (direction.isOK()) {
            initialProcess = true;
            mGoogleMap.clear();

            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Leg leg = route.getLegList().get(0);
                InfoWindowData originLeg = new InfoWindowData();
                originLeg.setAddress(leg.getStartAddress());
                originLeg.setArrival_time(null);
                originLeg.setDistance(leg.getDistance().getText());

                destinationLeg = new InfoWindowData();
                destinationLeg.setAddress(leg.getEndAddress());
                destinationLeg.setArrival_time(leg.getDuration().getText());
                destinationLeg.setDistance(leg.getDistance().getText());

                LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());
                if (currentStatus.equals("SERVICE")) {
                    if (specificProviders != null)
                        for (Provider provider : specificProviders) {

                            MarkerOptions markerOptions = new MarkerOptions()
                                    .anchor(0.5f, 0.5f)
                                    .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                                    .rotation(0.0f)
                                    .snippet("" + provider.getId())
                                    .icon(MarkerCarUtils.INSTANCE.getCarBitmapDescriptor(MainActivity.this));
                            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                        }
                } else {
                    mGoogleMap.addMarker(new MarkerOptions()
                            .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sourceicon))
                            .position(origin));
                }

                mGoogleMap.addMarker(new MarkerOptions()
                        .icon(bitmapDescriptorFromVector(getApplicationContext(), R.drawable.ic_sourceicon))
                        .position(origin));

                mGoogleMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                                .position(destination))
                        .setTag(destinationLeg);
            }

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            mGoogleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 5, getResources().getColor(R.color.colorPrimary)));
            if (adjustBounds) setCameraWithCoordinationBounds(route);

        } else {
            System.out.println(" onDirectionFailure = [");
            changeFlow("EMPTY");
            Toast.makeText(this, direction.getErrorMessage() != null
                    ? direction.getErrorMessage() : "No road found!", Toast.LENGTH_SHORT).show();
            onCameraIdle();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        System.out.println(" onDirectionFailure = [" + t.getMessage() + "]");
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
        } catch (Exception e) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void addCar(LatLng latLng) {
        final String[] eta = {""};

        if (isFinishing()) return;
        if (latLng != null && latLng.latitude > 0 && latLng.longitude > 0) {
            if (newPosition != null) {
                oldPosition = newPosition;
                newPosition = latLng;
            } else {
                newPosition = latLng;
            }
            if (marker == null) {
                marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.75f)
                        .icon(MarkerCarUtils.INSTANCE.getCarBitmapDescriptor(MainActivity.this)));
            } else {
                marker.remove();
                marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .icon(MarkerCarUtils.INSTANCE.getCarBitmapDescriptor(MainActivity.this)));
//
//                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
//                marker.setPosition(newPosition);
                animateMarker(oldPosition, newPosition, marker);
                marker.setRotation(bearingBetweenLocations(oldPosition, newPosition));
            }

            if (marker != null && !TextUtils.isEmpty(eta[0])) {
                marker.setTitle("ETA");
                marker.setSnippet(eta[0]);
                marker.showInfoWindow();
            } else marker.hideInfoWindow();
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        bookARideNow.setText("Hi, " + user.getFirstName());

        String dd = LocaleHelper.getLanguage(this);
        String userLanguage = (user.getLanguage() == null) ? Constants.Language.ENGLISH : user.getLanguage();
        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        SharedHelper.putKey(this, "mail", user.getEmail());
        SharedHelper.putKey(this, "mobile", user.getMobile());
        SharedHelper.putKey(this, "lang", user.getLanguage());
        SharedHelper.putKey(this, "stripe_publishable_key", user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(activity(), "picture", user.getPicture());
        Glide.with(activity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    @Override
    public void onSuccess(Object object) {
        changeFlow("RATING");
        try {
            hideLoading();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* private void removeAllMarkerAddDriverMarker(LatLng latLng, Provider provider) {
        if (providersMarker.size() == 1) {
            Marker marker = providersMarker.get(provider.getId());
//            providersMarker.clear();
            LatLng startPosition = marker.getPosition();
            marker.setPosition(latLng);
//            animateMarker(startPosition, latLng, marker);
//            marker.setRotation(bearingBetweenLocations(startPosition, latLng));

//            MarkerOptions markerOptions = new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
//                    .rotation(0.0f)
//                    .snippet("" + provider.getId())
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
//            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
        } else {
            providersMarker.clear();
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(latLng)
                    .rotation(0.0f)
                    .snippet("" + provider.getId())
                    .icon(MarkerCarUtils.INSTANCE.getCarBitmapDescriptor(MainActivity.this));
            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
        }
    }*/

    @Override
    public void onSuccessLogout(Object object) {
        Utilities.LogoutApp(this, getString(R.string.logout_successfully));
    }

    @Override
    public void onSuccess(AddressResponse response) {
        home = (response.getHome().isEmpty()) ? null : response.getHome().get(response.getHome().size() - 1);
        work = (response.getWork().isEmpty()) ? null : response.getWork().get(response.getWork().size() - 1);
//        if (currentStatus.equalsIgnoreCase("EMPTY")) {
        if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
        else llPickHomeAdd.setVisibility(View.INVISIBLE);
        if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
        else llPickWorkAdd.setVisibility(View.INVISIBLE);
//        }

        if (home != null) {
//            homeIconImg.setImageDrawable(getDrawable(R.drawable.ic_home));
            homeNameTx.setText("Home");
            homeAddressTx.setVisibility(View.VISIBLE);
            homeAddressTx.setText(home.getAddress());
            homeArrowImg.setVisibility(View.VISIBLE);
        } else {
//            homeIconImg.setImageDrawable(getDrawable(R.drawable.star_filled));
            homeNameTx.setText("Set Home");
            homeAddressTx.setText("Get to home location faster");
//            homeAddressTx.setVisibility(View.INVISIBLE);
//            homeArrowImg.setVisibility(View.INVISIBLE);
        }


        if (work != null) {
//            workIconImg.setImageDrawable(getDrawable(R.drawable.ic_work));
            workNameTx.setText("Work");
            workAddressTx.setText(work.getAddress());
            workAddressTx.setVisibility(View.VISIBLE);
            workArrowImg.setVisibility(View.VISIBLE);
        } else {
//            workIconImg.setImageDrawable(getDrawable(R.drawable.star_filled));
            workNameTx.setText("Set Work");
            workAddressTx.setText("Get to work location faster");
//            workAddressTx.setVisibility(View.INVISIBLE);
//            workArrowImg.setVisibility(View.INVISIBLE);
        }


    }

    @Override
    public void onSuccess(List<Provider> providerList) {
//        System.out.println(" providerList = " + printJSON(providerList));
        SharedHelper.putProviders(this, printJSON(providerList));
       /* if (providerList != null)
            addDriverMarkers(providerList);*/
    }

    @Override
    public void onSuccess(InitSettingsResponse initSettingsResponse) {
        SharedHelper.putKey(activity(), "map_key", initSettingsResponse.getMapKey());
        Log.d("drawRoute", "drawRoute: " + SharedHelper.getKey(activity(), "map_key"));
    }

    private void addDriverMarkers(String paramString, GeoPoint param) {
        if (STATUS.equals("SERVICE") || STATUS.equals("EMPTY")) {
            try {
                int i = Integer.parseInt(paramString);
                if (this.markerCarUtils == null)
                    this.markerCarUtils = MarkerCarUtils.INSTANCE;
                this.markerCarUtils.updateCarLocation(this, mGoogleMap, i,
                        new LatLng(param.getLatitude(), param.getLongitude()), providersMarker);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

   /* private void addDriverMarkers(List<Provider> providers) {
        if (providers != null) {
            for (Provider provider : providers)
                if (providersMarker.containsKey(provider.getId())) {
                    Marker marker = providersMarker.get(provider.getId());
                    LatLng startPosition = marker.getPosition();
                    LatLng newPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                    marker.setPosition(newPos);
                    animateMarker(startPosition, newPos, marker);
//                    marker.setRotation(bearingBetweenLocations(startPosition, newPos));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                            .rotation(0.0f)
                            .snippet("" + provider.getId())
                            .icon(MarkerCarUtils.INSTANCE.getCarBitmapDescriptor(MainActivity.this));
                    providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                }
        }
    }*/

    public void setSpecificProviders(List<Provider> specificProviders) {
        this.specificProviders = specificProviders;
        LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
        LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
        drawRoute(origin, destination);
    }

    @Override
    public void onError(Throwable e) {
//        handleError(e);
    }

    @Override
    public void onCheckStatusError(Throwable e) {
        Log.d("Error", "My Error" + e.getLocalizedMessage());

        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");
        }
    }

    //  private boolean canCallCurrentLocation = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (RIDE_REQUEST.containsKey("s_address")) {
                    Log.e("onActivityResult", "s_address: " + RIDE_REQUEST.get("s_address"));
                    sourceTxt.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                    sourceTxt.setVisibility(View.VISIBLE);
                    ll_srcdesimages.setVisibility(View.VISIBLE);
                    src_dot.setVisibility(View.GONE);

                    bookARideLayout.setVisibility(View.GONE);
                    pickLocationLayout.setVisibility(View.VISIBLE);
                } else {
                    sourceTxt.setText("");
                    sourceTxt.setVisibility(View.GONE);
                    ll_srcdesimages.setVisibility(View.GONE);
                    src_dot.setVisibility(View.VISIBLE);
                }

                if (RIDE_REQUEST.containsKey("d_address"))
                    destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
                else destinationTxt.setText("");
                if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
                    LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                    LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
                    drawRoute(origin, destination);
                    currentStatus = "SERVICE";
                    changeFlow(currentStatus);
                }
                /*else changeFlow("EMPTY");*/
            }
        }
    }

    //      TODO: Payment Gateway

//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        String nonce = paymentMethodNonce.getNonce();
//        Log.d("PayPal", "onPaymentMethodNonceCreated " + nonce);
//        if (paymentMethodNonce instanceof PayPalAccountNonce) {
//            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
//            String email = payPalAccountNonce.getEmail();
//        }
//    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = location.getLatitude() + "\n" + location.getLongitude()
                    + "\n\nMy Current City is: "
                    + cityName;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mGoogleMap == null) return;
        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            if (mLastKnownLocation != null) {
                if (TextUtils.isEmpty(sourceTxt.getText()) || sourceTxt.getText().toString().equals(getResources().getString(R.string.pickup_location)))
                    mLocation = getLastKnownLocation();
                if (mLocation != null) {
                    Address address = getAddress(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    if (address != null) {
                        if (check_current_location == false) {
                            String streetAddress = getStreetAddress(address);
                            sourceTxt.setText(streetAddress);
                            RIDE_REQUEST.put("s_address", streetAddress);
                            RIDE_REQUEST.put("s_latitude", mLastKnownLocation.getLatitude());
                            RIDE_REQUEST.put("s_longitude", mLastKnownLocation.getLongitude());
                            check_current_location = true;
                        }
                    } else {
                        Toasty.info(this, "Unable to find address.", Toast.LENGTH_SHORT).show();
                    }
                }

            }
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

        } else getLocationPermission();
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;
            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            /*if (isCash) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.cash);
            else if (isCard) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.card);*/
        }
    }

    @Override
    public void onUpdateNeeded(String updateUrl, boolean isPriorityUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.newVersionAvailble))
                .setMessage(getString(R.string.forceUpdateMsg))
                .setPositiveButton(getString(R.string.update),
                        (dialog12, which) -> {
                            final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
        if (isPriorityUpdate) {
            builder.setNegativeButton(getString(R.string.noThanks),
                    (dialog1, which) -> dialog1.dismiss());
        }
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onPaymentSuccess(String s) {
        if (DATUM != null) {
            Datum datum = DATUM;
            showLoading();
            mainPresenter.updateRazorPayment(datum.getId(), s);
        }
    }

    @Override
    public void onPaymentError(int i, String s) {

    }

    public void closeServiceFragment() {
        changeFlow("EMPTY");
        onCameraIdle();
    }
}
