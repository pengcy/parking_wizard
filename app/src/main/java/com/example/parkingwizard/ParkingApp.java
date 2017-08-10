package com.example.parkingwizard;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.example.parkingwizard.dagger.components.AppComponent;
import com.example.parkingwizard.dagger.components.DaggerAppComponent;
import com.example.parkingwizard.dagger.modules.AppModule;
import io.fabric.sdk.android.Fabric;

/**
 * Created by peng on 8/8/17.
 */

public class ParkingApp extends Application {

    private AppComponent appComponent;
    private static Location currentLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        initDagger(this);

        initLocation();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    private void initDagger(ParkingApp application) {
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(application))
                .build();
    }

    private void initLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);

        String provider = locationManager.getBestProvider(criteria, false);
        Location location = null;
        try {
            location = locationManager.getLastKnownLocation(provider);

            LocationListener locationlistener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    currentLocation = location;
                }

                @Override
                public void onStatusChanged(String s, int i, Bundle bundle) {

                }

                @Override
                public void onProviderEnabled(String s) {

                }

                @Override
                public void onProviderDisabled(String s) {

                }
            };

            if (location != null) {
                locationlistener.onLocationChanged(location);
            } else {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }

            // location updates per second
            locationManager.requestLocationUpdates(provider, 1000, 1, locationlistener);
        } catch (SecurityException e) {
            Log.e("SecurityException", e.getMessage());
        }
    }

    public static Location getCurrentLocation() {
        return currentLocation;
    }
}
