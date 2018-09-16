package com.minus.occupancychecker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation {
    LocationManager lm;
    LocationResult locationResult;
    boolean gps_enabled=false;

    @SuppressLint("MissingPermission")
    public void getLocation(Context context, LocationResult result)
    {
        //I use LocationResult callback class to pass location value from MyLocation to user code.
        locationResult=result;
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}

        Location gps_loc = null;
        if(gps_enabled) {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        //if there are both values use the latest one
        if(gps_loc!=null){
            locationResult.gotLocation(gps_loc);
        }
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            locationResult.gotLocation(location);
            lm.removeUpdates(this);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };


    public static abstract class LocationResult{
        public abstract void gotLocation(Location location);
    }
}
