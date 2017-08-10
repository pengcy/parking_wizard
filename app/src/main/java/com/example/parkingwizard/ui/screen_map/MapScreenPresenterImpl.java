/*
 * Copyright (c) 2016 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.example.parkingwizard.ui.screen_map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.widget.Toast;

import com.example.parkingwizard.ParkingApp;
import com.example.parkingwizard.data.Constants;
import com.example.parkingwizard.data.ParkingData;
import com.example.parkingwizard.data.ParkingSpot;
import com.example.parkingwizard.network.ParkingSpotApiClient;
import com.example.parkingwizard.utils.ReservationConfirmationModal;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

//import javax.inject.Inject;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.QueryMap;

public class MapScreenPresenterImpl implements MapScreenPresenter {

    private static long lastGetParkingSpotCall = 0 ;

    private MapScreenView view;
    @Inject
    ParkingSpotApiClient parkingSpotApiClient;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public MapScreenPresenterImpl(Context context) {
        ((ParkingApp) context).getAppComponent().inject(this);
    }


    @Override
    public void setView(MapScreenView view) {
        this.view = view;
    }

    @Override
    public void getParkingSpots() {
        long callInterval = System.currentTimeMillis() - lastGetParkingSpotCall;
        if (callInterval < Constants.GET_PARKING_SPOTS_CALL_INTERVAL) return;

        lastGetParkingSpotCall = System.currentTimeMillis();
        Disposable disposable = parkingSpotApiClient.getAllParkingSpotsRx()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<List<ParkingSpot>>>() {
                    @Override
                    public void onNext(@NonNull Response<List<ParkingSpot>> listResponse) {
                        if (listResponse.code() != 200) {
                            view.showErrorMessage();
                        } else if (listResponse.body() != null) {
                            view.showParkingSpots(listResponse.body());
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        view.showErrorMessage();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void reserveParkingSpot(String parkingSpotId, int time) {
        Disposable disposable = parkingSpotApiClient.reserveParkingSpotRx(parkingSpotId, time)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<ParkingSpot>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Response<ParkingSpot> listResponse) {
                        if (listResponse != null && listResponse.body() != null) {
                            Log.d("Search result", String.format("reserved spot: ", listResponse.body().toString()));
                            ParkingSpot reservedSpot = listResponse.body();
                            ParkingData.getInstance().setReservedSpot(reservedSpot);
                            view.showReserveConfirmation();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }


    @Override
    public void searchParkingSpots(@QueryMap Map<String, String> params) {
        Disposable disposable = parkingSpotApiClient.searchParkingSpotRx(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Response<List<ParkingSpot>>>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Response<List<ParkingSpot>> listResponse) {
                        if (listResponse != null && listResponse.body() != null) {
                            view.showSearchResult(listResponse.body());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void clearTasks() {
        compositeDisposable.clear();
    }
}
