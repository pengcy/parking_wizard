package com.example.parkingwizard.network;

import com.example.parkingwizard.data.ParkingSpot;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/*
[
  {
    "id": 2038,
    "lat": "37.787994",
    "lng": "-122.407437",
    "name": "",
    "cost_per_minute": "0.50",
    "max_reserve_time_mins": 120,
    "min_reserve_time_mins": 10,
    "is_reserved": true,
    "reserved_until": "2017-04-24T12:57:42.735380Z"
  },
  {
    "id": 2027,
    "lat": "37.774900",
    "lng": "122.419400",
    "name": "",
    "cost_per_minute": "1.00",
    "max_reserve_time_mins": 120,
    "min_reserve_time_mins": 10,
    "is_reserved": true,
    "reserved_until": null
  }
]
*/
public interface ParkingSpotApiClient {

    @GET("api/v1/parkinglocations")
    Observable<Response<List<ParkingSpot>>> getAllParkingSpotsRx();

    @GET("api/v1/parkinglocations/search")
    Observable<Response<List<ParkingSpot>>> searchParkingSpotRx(@QueryMap Map<String, String> params);

    @POST("api/v1/parkinglocations/{id}/reserve")
    @FormUrlEncoded
    Observable<Response<ParkingSpot>> reserveParkingSpotRx(@Path("id") String id, @Field("minute") int minutes);
}
