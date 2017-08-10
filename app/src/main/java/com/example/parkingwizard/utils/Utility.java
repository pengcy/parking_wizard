package com.example.parkingwizard.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;

import com.example.parkingwizard.ParkingApp;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Utility {

    public static Map<String, String> buildSearchParams(LatLng latLng) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("lat", Double.toString(latLng.latitude));
        params.put("lng", Double.toString(latLng.longitude));
        return params;
    }

    public static Address getAddressByLatLng(Context context, LatLng latLng) {

        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(context, Locale.getDefault());
        Address address  = null;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            address = addresses.get(0);
        } catch (IOException e) {
            Log.e("Geocoder Error", "Failed to get address from latlng, exception: " + e.toString());
        }

        return address;
    }


    public static String calculateDistanceMile(Location loc1, Location loc2) {
        float distanceInMeter = loc1.distanceTo(loc2);
        double distanceInMile = distanceInMeter / 1609.344;

        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.FLOOR);
        return df.format(distanceInMile);
    }



}
