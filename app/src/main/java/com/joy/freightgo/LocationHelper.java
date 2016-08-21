package com.joy.freightgo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by Joy on 2016/8/21.
 */
public class LocationHelper implements LocationListener {
    private static final  String joytag = "joydebug.LocationHelper.";

    private LocationChangedCallback mCallback;

    interface LocationChangedCallback {
        void onChanged(Location location);
    }

    public static boolean checkPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private LocationManager mManager;

    public LocationHelper(Context context) {
        mManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void setListening(boolean listening) {
        if (listening) {
            mManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            mManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        } else {
            mManager.removeUpdates(this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO
        Log.i(joytag+"onLocationChanged()",
                ""+location.getLatitude()
                +", "+location.getAltitude()
                +", "+location.getTime()
        );
        mCallback.onChanged(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.i(joytag, "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.i(joytag, "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.i(joytag, "onProviderDisabled");
    }

    public void setCallback(LocationChangedCallback callback) {
        mCallback = callback;
        //mCallback.onChanged(mManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
    }
}
