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
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by Joy on 2016/8/20.
 */
public class MainActivity extends Activity implements LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        OnMapReadyCallback {
    private static final String joytag = "joydebug.mainactivity.";

    private static final int PERMISSION_LOCATION_REQUEST_CODE = 0;

    private static final long UPDATE_LOCATION_INTERVAL_MS = 10000;
    private static final long UPDATE_LOCATION_FASTEST_INTERVAL_MS = 5000;

    private MapFrag mMap;
    private TextView mPosition;
    private ImageView mSetting, mAddLocation;

    private LocationManager mManager;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private GoogleMap mGoogleMap;
    private ArrayList<LatLng> mLatLngs;

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
        ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
        mAddLocation = (ImageView) findViewById(R.id.add_location);
        mAddLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMarkerOnMap();
                Toast.makeText(getApplicationContext(), "Add location ("+mLastLocation.getLatitude()
                        +", "+mLastLocation.getLongitude()+")", Toast.LENGTH_SHORT)
                        .show();
            }
        });
        mPosition = (TextView) findViewById(R.id.position);
        mSetting = (ImageView) findViewById(R.id.setting);
        mSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SettingActivity.class));
            }
        });

        mLatLngs = new ArrayList<>();

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            Log.i(joytag+"onConnected()", "updatePositionText(mLastLocation);");
            updatePosition(mLastLocation);
        }

        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
//        result.setResultCallback(this);

        if (checkPermission(this)) {
            startLocationUpdates();
        }
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
        updatePosition(location);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.i(joytag+"onMapReady(+)", "mLastLocation="+mLastLocation);
        mGoogleMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        // Add a marker in Taipei and move the camera
        LatLng taipei = new LatLng(25.033408, 121.564099);
        mGoogleMap.addMarker(new MarkerOptions().position(taipei).title("Marker in Taipei 101"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(taipei));

        // Create a CameraPosition object
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(taipei)
                .zoom(17)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void updatePosition(Location location) {
        if (location != null) {
            mLastLocation = location;
        }
        updatePositionText(location);
        moveMap(location);
    }

    private void updatePositionText(Location location) {
        Log.i(joytag+"updatePositionText(+)", ""+location);
        StringBuilder builder = new StringBuilder();
        builder.append(location.getLatitude()).append(" ")
                .append(location.getLongitude());
        mPosition.setText(builder.toString());
    }

    private void moveMap(Location location) {
        Log.i(joytag+"moveMap(+)", "mGoogleMap="+mGoogleMap+", location="+location);
        if (mGoogleMap != null && location != null) {
            LatLng newLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
            // Create a CameraPosition object
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(newLocation)
                    .zoom(17)
                    .build();
            mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setMarkerOnMap() {
        Log.i(joytag+"setMarkerOnMap(+)", "+++");
        if (mGoogleMap != null && mLastLocation != null) {
            LatLng last = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            mLatLngs.add(last);
            mGoogleMap.addMarker(new MarkerOptions().position(last).title("Location "+mLatLngs.size()));
        }
    }
}
