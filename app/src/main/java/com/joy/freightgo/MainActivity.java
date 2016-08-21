package com.joy.freightgo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;

/**
 * Created by Joy on 2016/8/20.
 */
public class MainActivity extends Activity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String joytag = "joydebug.mainactivity.";

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 0;

    private static final long UPDATE_LOCATION_INTERVAL_MS = 10000;
    private static final long UPDATE_LOCATION_FASTEST_INTERVAL_MS = 5000;

    private TextView mPosition;
    private ImageView mSetting, mAddLocation;

    private LocationManager mManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Log.i(joytag+"", "onCreateOptionsMenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddLocation = (ImageView) findViewById(R.id.add_location);
        mPosition = (TextView) findViewById(R.id.position);
        mSetting = (ImageView) findViewById(R.id.setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });

        showPermissionDialog();
        mManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Register the listener with the Location Manager to receive location updates
//                    mLocation.setListening(true);
//                    mLocation.setCallback(this);
                } else {
                    // Permission denied
                    finish();
                }
                return;
        }
    }

    private void showPermissionDialog() {
        if (!checkPermission(this)) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_LOCATION_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
//        mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        mManager.removeUpdates(this);
        //LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        //mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stopLocationUpdates();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) startLocationUpdates();
    }

    private void updatePositionText(Location location) {
        Log.i(joytag+"updatePositionText()", ""+location);
        StringBuilder builder = new StringBuilder();
        builder.append(location.getLatitude()).append(" ")
                .append(location.getLongitude());
        mPosition.setText(builder.toString());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(joytag+"onConnected()", "updatePositionText(mLastLocation);");
            updatePositionText(mLastLocation);
        }

        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
//        result.setResultCallback(this);

        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_LOCATION_INTERVAL_MS);
        mLocationRequest.setFastestInterval(UPDATE_LOCATION_FASTEST_INTERVAL_MS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
    }

    private boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }


    @Override
    public void onLocationChanged(Location location) {
        // TODO
        Log.i(joytag+"onLocationChanged()",
                ""+location.getLatitude()
                        +", "+location.getAltitude()
                        +", "+location.getTime()
        );
        updatePositionText(location);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }
}
