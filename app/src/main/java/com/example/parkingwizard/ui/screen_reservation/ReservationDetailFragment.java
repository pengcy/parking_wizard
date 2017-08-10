package com.example.parkingwizard.ui.screen_reservation;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.parkingwizard.ParkingApp;
import com.example.parkingwizard.R;
import com.example.parkingwizard.data.Constants;
import com.example.parkingwizard.data.ParkingData;
import com.example.parkingwizard.data.ParkingSpot;
import com.example.parkingwizard.data.SearchData;
import com.example.parkingwizard.utils.ReservationConfirmationModal;
import com.example.parkingwizard.utils.Utility;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ReservationDetailFragment extends Fragment implements StreetViewPanorama.OnStreetViewPanoramaChangeListener {
    public static ReservationDetailFragment getInstance() {
        return new ReservationDetailFragment();
    }

    private static final String MARKER_POSITION_KEY = "MarkerPosition";
    private StreetViewPanorama mStreetViewPanorama;


    @BindView(R.id.tv_parking_spot_name)
    TextView tvParkingSpotName;
    @BindView(R.id.tv_parking_spot_address)
    TextView tvParkingSpotAddress;
    @BindView(R.id.tv_parking_spot_cost)
    TextView tvCost;
    @BindView(R.id.tv_parking_spot_distance)
    TextView tvDistance;
    @BindView(R.id.et_date)
    EditText etDate;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.tv_reserve_time)
    TextView tvReserveTime;
    @BindView(R.id.sb_reserve_time)
    AppCompatSeekBar acsbReserveTime;
    @BindView(R.id.btn_pay_to_reserve)
    Button btnPayToReserve;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_reservation_detail, container, false);
        ButterKnife.bind(this, view);

        ParkingSpot parkingSpot = ParkingData.getInstance().getReservedSpot();
        if (parkingSpot != null) {
            tvParkingSpotName.setText(parkingSpot.getName());
            tvCost.setText(parkingSpot.getCostPerMinute());

            initStreetViewPanorama(savedInstanceState, parkingSpot);
            initParkingSpotInfo(parkingSpot);
        }

        SearchData searchData = ParkingData.getInstance().getSearchData();
        if (searchData != null) {
            etDate.setText(searchData.getDate());
            etTime.setText(searchData.getTime());
            acsbReserveTime.setProgress(searchData.getReserveTime());
        } else {
            acsbReserveTime.setProgress(Constants.MIN_RESERVE_TIME);
        }

        initReservationSeekBar();

        return view;
    }

    private void initStreetViewPanorama(final Bundle savedInstanceState, ParkingSpot parkingSpot) {
        final LatLng markerPosition;
        if (savedInstanceState == null) {
            markerPosition = new LatLng(Double.parseDouble(parkingSpot.getLat()), Double.parseDouble(parkingSpot.getLng()));
        } else {
            markerPosition = savedInstanceState.getParcelable(MARKER_POSITION_KEY);
        }

        SupportStreetViewPanoramaFragment streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment) getChildFragmentManager().findFragmentById(R.id.streetviewpanorama);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        mStreetViewPanorama.setOnStreetViewPanoramaChangeListener(
                                ReservationDetailFragment.this);
                        // Only need to set the position once as the streetview fragment will maintain
                        // its state.
                        if (savedInstanceState == null) {
                            mStreetViewPanorama.setPosition(markerPosition);
                        }
                    }
                });
    }

    private void initParkingSpotInfo(ParkingSpot parkingSpot) {
        LatLng latLng = new LatLng(Double.parseDouble(parkingSpot.getLat()), Double.parseDouble(parkingSpot.getLng()));
        Address address = Utility.getAddressByLatLng(getContext(), latLng);
        if (address != null) {
            String addressStr = address.getAddressLine(0);
            String knownName = address.getFeatureName();
            tvParkingSpotName.setText(knownName);
            tvParkingSpotAddress.setText(addressStr);
        } else {
            tvParkingSpotName.setText(parkingSpot.getName());
            tvParkingSpotAddress.setText(latLng.latitude + "," + latLng.longitude);
        }

        tvCost.setText(parkingSpot.getCostPerMinute());

        Location loc1 = ParkingApp.getCurrentLocation();
        Location loc2 = new Location("");
        loc2.setLatitude(latLng.latitude);
        loc2.setLongitude(latLng.longitude);
        tvDistance.setText(Utility.calculateDistanceMile(loc1, loc2));
    }

    private void initReservationSeekBar() {
        tvReserveTime.setText(getString(R.string.app_search_control_reserve_time, getResources().getInteger(R.integer.default_reservation_time)));

        acsbReserveTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvReserveTime.setText(getString(R.string.app_search_control_reserve_time, progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    @OnClick(R.id.btn_pay_to_reserve)
    public void payToReserve(View v) {
        ReservationConfirmationModal reservationConfirmationModal = new ReservationConfirmationModal(getActivity());
        reservationConfirmationModal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reservationConfirmationModal.show();
    }


    @Override
    public void onStreetViewPanoramaChange(StreetViewPanoramaLocation location) {

    }
}
