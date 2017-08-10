package com.example.parkingwizard.utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.parkingwizard.ParkingApp;
import com.example.parkingwizard.R;
import com.example.parkingwizard.data.ParkingData;
import com.example.parkingwizard.data.ParkingSpot;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by peng on 8/8/17.
 */

public class ParkingSpotInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    private Context context;
    public ParkingSpotInfoWindowAdapter(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v =  inflater.inflate(R.layout.parking_spot_info_window_content, null);
        v.setLayoutParams(new RelativeLayout.LayoutParams(500, RelativeLayout.LayoutParams.WRAP_CONTENT));


        TextView tvName = (TextView) v.findViewById(R.id.tv_parking_spot_name);
        TextView tvAddress = (TextView) v.findViewById(R.id.tv_parking_spot_address);
        TextView tvOpenSpot = (TextView) v.findViewById(R.id.tv_parking_spot_open_spot);
        TextView tvCost = (TextView) v.findViewById(R.id.tv_parking_spot_cost);
        TextView tvDistance = (TextView) v.findViewById(R.id.tv_parking_spot_distance);

        ParkingSpot parkingSpot = ParkingData.getInstance().getParkingSpotByMarker(arg0);
        LatLng latLng = arg0.getPosition();
        if (parkingSpot == null) {
            tvAddress.setText("Latitude:" + latLng.latitude + ",Longitude:"+ latLng.longitude);
            return v;
        }

        Address address = Utility.getAddressByLatLng(context, latLng);
        if (address != null) {
            String addressStr = address.getAddressLine(0);
            String knownName = address.getFeatureName();
            tvName.setText(knownName);
            tvAddress.setText(addressStr);
        } else {
            tvName.setText(parkingSpot.getName());
            tvAddress.setText(latLng.latitude + "," + latLng.longitude);
        }


        tvOpenSpot.setText(10 + "");
        tvCost.setText(parkingSpot.getCostPerMinute());

        Location loc1 = ParkingApp.getCurrentLocation();
        Location loc2 = new Location("");
        loc2.setLatitude(latLng.latitude);
        loc2.setLongitude(latLng.longitude);
        tvDistance.setText(Utility.calculateDistanceMile(loc1, loc2));

        return v;
    }


}
