package com.example.app.ourapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import com.example.app.ourapplication.pref.PreferenceEditor;
import com.example.app.ourapplication.rest.model.request.LocationModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;

public class LocationFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener, GoogleMap.OnMapClickListener, OnMapReadyCallback {

    private final String TAG = LocationFragment.class.getSimpleName();
    private static final int PERM_LOCATION = 6;
    private static final int REQ_LOCATION = 7;
    private static final int DEF_RADIUS = 100;

    private double mRadius = DEF_RADIUS;

    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private GoogleApiClient mGoogleApiClient;
    private Circle mCurrLocationMarker;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment LocationFragment.
     */
    public static LocationFragment newInstance() {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLocationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mRadius = DEF_RADIUS + seekBar.getProgress();
                LocationModel model = PreferenceEditor.getInstance(getContext()).getLocation();

                drawCircle(new LatLng(model.getLatitude(),model.getLongitude()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        if(isLocationEnabled()){
            if(isLocationPermAvailable()){
                setupMaps();
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERM_LOCATION);
            }
        }else{
            if(isLocationPermAvailable()){
                showGPSAlert();
            }else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERM_LOCATION);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_LOCATION && resultCode == Activity.RESULT_OK) {
            if (isLocationEnabled()) {
                setupMaps();
                checkInLocation();
            } else {
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(isLocationPermAvailable()){
            if(isLocationEnabled()){
                setupMaps();
            }else{
                showGPSAlert();
            }
        }else{
            // What to do?
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mGoogleApiClient == null && isLocationPermAvailable() && isLocationEnabled()){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setupMaps();
                }
            },1000);
        }else{
            if(mGoogleApiClient != null && !mGoogleApiClient.isConnected() && isLocationPermAvailable() && isLocationEnabled()){
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"OnConnected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        initCamera(location);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"OnConnectionSuspended : "+i);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"OnConnectionFailed : "+connectionResult.getErrorCode());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        initListeners();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {}

    @Override
    public void onMarkerDrag(Marker marker) {}

    @Override
    public void onMarkerDragEnd(Marker marker) {}

    @Override
    public void onMapClick(LatLng latLng) {
        MarkerOptions options = new MarkerOptions().position(latLng);
        options.title(getAddress(latLng));
        drawCircle(latLng);
    }

    private boolean isLocationEnabled() {
        return mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private boolean isLocationPermAvailable(){
        return (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) || (ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    private Location getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location currLoc = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Log.d(TAG, "Current GPS location : " + currLoc);
        if (currLoc == null) {
            currLoc = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Log.d(TAG,"Current Network location : "+currLoc.toString());
        }
        return currLoc;
    }

    private void checkInLocation(){
        Location location = getCurrentLocation();
        if(location == null){
            return;
        }
        LocationModel locationModel = new LocationModel(location);
        PreferenceEditor.getInstance(getContext()).setLocation(locationModel);
    }

    private void initListeners(){
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
        mGoogleMap.setOnMapClickListener(this);
        mGoogleApiClient.connect();
    }

    private void initCamera(Location location){
        if(location == null){
            return;
        }

        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraPosition position = CameraPosition.builder()
                .target(latLng)
                .zoom(16f)
                .bearing(0.0f)
                .tilt(0.0f)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        drawCircle(latLng);
    }

    private String getAddress(LatLng latLng){
        Geocoder geocoder = new Geocoder(getActivity());
        String address = null;
        try {
            address = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1).get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    private void drawCircle(LatLng latLng){
        if(mCurrLocationMarker != null){
            mCurrLocationMarker.remove();
        }
        LocationModel locationModel = new LocationModel();
        locationModel.setLatitude(latLng.latitude);
        locationModel.setLongitude(latLng.longitude);
        locationModel.setRadius(mRadius);
        PreferenceEditor.getInstance(getContext()).setLocation(locationModel);
        CircleOptions options = new CircleOptions();
        options.center(latLng);
        Log.d(TAG,"Zoom level : "+mGoogleMap.getCameraPosition().zoom);
        options.radius(mRadius);
        options.fillColor(ContextCompat.getColor(getActivity(),R.color.map_highlight));
        options.strokeColor(ContextCompat.getColor(getActivity(),R.color.map_highlight_border));
        options.strokeWidth(10);
        mCurrLocationMarker = mGoogleMap.addCircle(options);
    }

    private void showGPSAlert(){
        AlertDialog.Builder alertDlgBuilder = new AlertDialog.Builder(getActivity());
        alertDlgBuilder
                .setTitle(getString(R.string.gps_disabled))
                .setMessage(getString(R.string.enable_gps))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.enable), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQ_LOCATION);
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // What to be done?
                    }
                });
        AlertDialog mAlertDialog = alertDlgBuilder.create();
        mAlertDialog.show();
    }

    private void setupMaps(){
        SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map));
        mapFragment.getMapAsync(this);
    }
}