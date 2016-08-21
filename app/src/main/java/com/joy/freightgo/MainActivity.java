package com.joy.freightgo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Joy on 2016/8/20.
 */
public class MainActivity extends Activity implements LocationHelper.LocationChangedCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String joytag = "joydebug.mainactivity.";

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 0;

    private TextView mPosition;
    private ImageView mSetting, mAddLocation;

    private LocationHelper mLocation;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

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
        mLocation = new LocationHelper(getApplicationContext());

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    // Register the listener with the Location Manager to receive location updates
                    mLocation.setListening(true);
                    mLocation.setCallback(this);
                } else {
                    // Permission denied
                    finish();
                }
                return;
        }
    }

    private void showPermissionDialog() {
        if (!LocationHelper.checkPermission(this)) {
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
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mLocation.setListening(false);
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onChanged(Location location) {
        Log.i(joytag+"onChanged()", ""+location);
        updatePositionText(location);
    }

    private void updatePositionText(Location location) {
        Log.i(joytag+"updatePositionText()", ""+location);
        StringBuilder builder = new StringBuilder();
        builder.append(location.getLatitude()).append(" ")
                .append(location.getLongitude()).append("\n");
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
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
