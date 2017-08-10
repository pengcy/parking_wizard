package com.example.parkingwizard.data;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;

/**
 * Created by peng on 8/9/17.
 */

public class ParkingData {
    private static ParkingData instance;
    private ParkingData() {}
    private HashMap<Marker, ParkingSpot> markerParkingSpot = new HashMap<>();
    private SearchData searchData;
    private ParkingSpot reservedSpot;



    public static ParkingData getInstance() {
        if (instance == null) {
            synchronized (ParkingData.class) {
                if (instance == null) {
                    instance = new ParkingData();
                }
            }
        }
        return instance;
    }

    public void putMarkerParkingSpot(Marker marker, ParkingSpot parkingSpot) {
        markerParkingSpot.put(marker, parkingSpot);
    }

    public ParkingSpot getParkingSpotByMarker(Marker marker) {
        return markerParkingSpot.get(marker);
    }

    public void removeMarkerParkingSpotByMarker(Marker marker) {
        markerParkingSpot.remove(marker);
    }

    public void clearMarkerParkingSpot() {
        markerParkingSpot.clear();
    }

    public SearchData getSearchData() {
        return searchData;
    }

    public void setSearchData(SearchData searchData) {
        this.searchData = searchData;
    }

    public ParkingSpot getReservedSpot() {
        return reservedSpot;
    }

    public void setReservedSpot(ParkingSpot reservedSpot) {
        this.reservedSpot = reservedSpot;
    }
}
