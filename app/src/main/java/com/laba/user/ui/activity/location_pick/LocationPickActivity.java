package com.laba.user.ui.activity.location_pick;

import static com.laba.user.MvpApplication.DEFAULT_ZOOM;
import static com.laba.user.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.laba.user.MvpApplication.getInstance;
import static com.laba.user.MvpApplication.mLastKnownLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.GeoPoint;
import com.laba.user.R;
import com.laba.user.base.BaseActivity;
import com.laba.user.data.network.model.Address;
import com.laba.user.data.network.model.AddressResponse;
import com.laba.user.ui.activity.main.MainActivity;
import com.laba.user.ui.adapter.PlacesAutoCompleteAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Objects;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationPickIView, PlacesAutoCompleteAdapter.ClickListener {

    private static final String TAG = "LocationPickActivity";

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.button3)
    Button pickUpLocationBt;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;

    private boolean isLocationRvClick = false;
    private boolean isSettingLocationClick = false;
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    //    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private Address home;
    private final Address work = null;

    private final LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();

    private EditText selectedEditText;
    boolean isEnableIdle = false;
    private boolean isMapKeyShow = false;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private String isSetting;
    ProgressDialog pd;

    PlacesClient placesClient;

    @BindView(R.id.ll_srcDest)
    LinearLayout llsrcDest;

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }

    private int convertDPtoPx(int dp) {
        // Converts 14 dip into its equivalent px
        Resources r = getResources();
        float px = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        return (int) px;
    }

    @Override
    public void initView() {
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String fieldClicked = getIntent().getStringExtra("fieldClicked");
        s_latitude = getIntent().getDoubleExtra("s_lat", 0);
        s_longitude = getIntent().getDoubleExtra("s_long", 0);

        if (RIDE_REQUEST.containsKey("d_address")) {
            Log.e("placeLocation2", String.valueOf(RIDE_REQUEST.get("d_address")));
            destination.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
        }else {
            destination.requestFocus();
            selectedEditText = destination;
            destination.setText("");
        }
        if (RIDE_REQUEST.containsKey("s_address")) {
            source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
        } else {
            destination.requestFocus();
            selectedEditText = destination;
            source.setText("");
        }

        if (!TextUtils.isEmpty(fieldClicked))
            if (fieldClicked.equalsIgnoreCase("pickupAddress")) {
                Log.e(TAG, "fieldClicked: pickupAddress");
                source.requestFocus();
                selectedEditText = source;
            } else if (fieldClicked.equalsIgnoreCase("dropAddress")) {
                Log.e(TAG, "fieldClicked: dropAddress");
                destination.requestFocus();
                selectedEditText = destination;
            } else hideKeyboard();


        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, placesClient);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        mAutoCompleteAdapter.setClickListener(this);
        locationsRv.setAdapter(mAutoCompleteAdapter);

        source.addTextChangedListener(filterTextWatcher);
        destination.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = source;
        });

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = destination;
        });

        destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });

        pickUpLocationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doneLogic();
            }
        });

        isSetting = getIntent().getStringExtra("isSetting");

        if (isSetting.equalsIgnoreCase("homeSetting")) {
            llsrcDest.setVisibility(View.GONE);
            source.requestFocus();

            source.setHint("Enter location");

            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, convertDPtoPx(125), 0, 0);
            dd.setLayoutParams(params);

        } else if (isSetting.equalsIgnoreCase("workSetting")) {
            llsrcDest.setVisibility(View.GONE);
            source.requestFocus();

            source.setHint("Enter location");

            CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(
                    CoordinatorLayout.LayoutParams.MATCH_PARENT,
                    CoordinatorLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, convertDPtoPx(125), 0, 0);
            dd.setLayoutParams(params);
        } else {
            llsrcDest.setVisibility(View.VISIBLE);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationLayout.setVisibility(extras.getBoolean("isHideDestination", false) ? View.GONE : View.VISIBLE);
        } else source.setHint(getString(R.string.pickup_location));
        presenter.address();
    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick,
                                 boolean isSettingLocationClick) {

        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            Log.e("setLocationText", "selectedEditText: " + selectedEditText.getTag());

            if (selectedEditText.getTag().equals("source")) {
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;
                RIDE_REQUEST.put("s_address", address);
                RIDE_REQUEST.put("s_latitude", latLng.latitude);
                RIDE_REQUEST.put("s_longitude", latLng.longitude);
            }
            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.put("d_address", address);
                RIDE_REQUEST.put("d_latitude", latLng.latitude);
                RIDE_REQUEST.put("d_longitude", latLng.longitude);
                if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }
        } else {
            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                RIDE_REQUEST.remove("s_address");
                RIDE_REQUEST.remove("s_latitude");
                RIDE_REQUEST.remove("s_longitude");
            }
            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
                Log.d("Destination", "d_latitude");
            }
        }

        if (isSettingLocationClick) {
            hideKeyboard();
            locationsRv.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @OnClick({R.id.source, R.id.destination, R.id.reset_source, R.id.reset_destination, R.id.home_address_layout, R.id.work_address_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                break;
            case R.id.destination:
                break;
            case R.id.reset_source:
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.reset_destination:
                destination.requestFocus();
                selectedEditText = destination;
                setLocationText(null, null, isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(), new LatLng(home.getLatitude(), home.getLongitude()), isLocationRvClick, isSettingLocationClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(), new LatLng(work.getLatitude(), work.getLongitude()), isLocationRvClick, isSettingLocationClick);
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        pickUpLocationBt.setVisibility(View.VISIBLE);
        locationsRv.setVisibility(View.GONE);
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                isMapKeyShow = true;
                if (isMapKeyShow) hideKeyboard();
                else showKeyboard();
                setLocationText(address, cameraPosition.target, isLocationRvClick, isSettingLocationClick);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraMove() {
        pickUpLocationBt.setVisibility(View.GONE);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

     /*   try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }*/
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));

                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
                updateLocationUI();
                getDeviceLocation();
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    private final TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable)
                if (!s.toString().equals("")) {
                    locationsRv.setVisibility(View.VISIBLE);
                    mAutoCompleteAdapter.getFilter().filter(s.toString());
//                    if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
//                        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            if (s.toString().equals("")) {
                locationsRv.setVisibility(View.GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    @Override
    public void onBackPressed() {
//        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
//            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        else super.onBackPressed();

        finish();
//        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                doneLogic();
                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doneLogic() {
        try {
            if (isSetting != null) {
                if (isSetting.equalsIgnoreCase("homeSetting") ||
                        isSetting.equalsIgnoreCase("workSetting")) {
                    if (source.getText().toString().isEmpty()) {
                        source.requestFocus();
                        return;
                    }
                    Intent intent = new Intent();
                    intent.putExtra("s_address", s_address);
                    intent.putExtra("s_latitude", s_latitude);
                    intent.putExtra("s_longitude", s_longitude);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                    return;
                }

                if (source.getText().toString().isEmpty()) {
                    source.requestFocus();
                } else if (destination.getText().toString().isEmpty()) {
                    destination.requestFocus();
                } else {
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onSuccess(AddressResponse address) {
        homeAddressLayout.setVisibility(View.GONE);
        workAddressLayout.setVisibility(View.GONE);

//        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
//        else {
//            home = address.getHome().get(address.getHome().size() - 1);
//            homeAddress.setText(home.getAddress());
//            homeAddressLayout.setVisibility(View.VISIBLE);
//        }
//
//        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
//        else {
//            work = address.getWork().get(address.getWork().size() - 1);
//            workAddress.setText(work.getAddress());
//            workAddressLayout.setVisibility(View.VISIBLE);
//        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void place(Place place) {
        isLocationRvClick = true;
        isMapKeyShow = false;
        isSettingLocationClick = true;
        Log.e("placeLocation", String.valueOf(place.getLatLng()));
        setLocationText(place.getAddress(), place.getLatLng(), isLocationRvClick, isSettingLocationClick);

        if (getIntent().getExtras().containsKey("fieldClicked")) {
            doneLogic();
        } else {
            destination.requestFocus();
        }
    }



}

