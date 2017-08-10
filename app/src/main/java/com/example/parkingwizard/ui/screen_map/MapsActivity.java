package com.example.parkingwizard.ui.screen_map;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationItemView;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.parkingwizard.ParkingApp;
import com.example.parkingwizard.R;
import com.example.parkingwizard.ui.screen_reservation.ReservationDetailFragment;
import com.example.parkingwizard.data.Constants;
import com.example.parkingwizard.data.ParkingData;
import com.example.parkingwizard.data.ParkingSpot;
import com.example.parkingwizard.data.SearchData;
import com.example.parkingwizard.utils.CustomAutoCompleteAdapter;
import com.example.parkingwizard.utils.ParkingSpotInfoWindowAdapter;
import com.example.parkingwizard.utils.ReservationConfirmationModal;
import com.example.parkingwizard.utils.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends AppCompatActivity implements MapScreenView, OnMapReadyCallback {

    private GoogleMap mMap;
    private Calendar myCalendar = Calendar.getInstance();

    @Inject
    MapScreenPresenter presenter;

    @BindView(R.id.navigation)
    BottomNavigationView navigation;
    @BindView(R.id.rl_search_control_main_container)
    RelativeLayout rlMainSearchContainer;
    @BindView(R.id.actv_location)
    AutoCompleteTextView actvLocation;
    @BindView(R.id.btn_search)
    Button btnSearch;
    @BindView(R.id.et_date)
    TextView etDate;
    @BindView(R.id.et_time)
    TextView etTime;
    @BindView(R.id.tv_reserve_time)
    TextView tvReserveTime;
    @BindView(R.id.sb_reserve_time)
    AppCompatSeekBar sbReservationTime;

    TextView tvActionBarTitle;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_search:
                    tvActionBarTitle.setText(getString(R.string.app_main_header));
                    showSearchControl();
                    Fragment mapFragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_MAP);
                    if (mapFragment != null && mapFragment instanceof ReservationDetailFragment) {
                        showFragmentByTag(Constants.FRAGMENT_MAP);
                    } else {
                        launchMapFragment();
                    }
                    return true;
                case R.id.navigation_find:
                    hideSearchControl();
                    return true;
                case R.id.navigation_reservation:
                    tvActionBarTitle.setText(getString(R.string.app_reservation_screen_header));
                    hideSearchControl();
                    Fragment fragment = getSupportFragmentManager().findFragmentByTag(Constants.FRAGMENT_RESERVATION_DETAIL);
                    if (fragment != null && fragment instanceof ReservationDetailFragment) {
                        showFragmentByTag(Constants.FRAGMENT_RESERVATION_DETAIL);
                    } else {
                        launchParkingSpotDetail();
                    }
                    return true;
                case R.id.navigation_my_car:
                    hideSearchControl();
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        ((ParkingApp)getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);

        presenter.setView(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.actionbar);
        tvActionBarTitle = (TextView) actionBar.getCustomView().findViewById(R.id.tv_title);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        launchMapFragment();

//        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        //forcing the icon and text to show at the same time
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navigation.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShiftingMode(false);
            itemView.setChecked(false);
        }

        initLocationInputView();
        initReservationSeekBar();
    }

    @Override
    protected void onPause() {
        super.onPause();
        presenter.clearTasks();
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
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sanFrancisco = new LatLng(37.7808483, -122.4199707);
        mMap.addMarker(new MarkerOptions().position(sanFrancisco).title("Marker in San Franscisco"));

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        if  (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }


        Location currentLocation = ParkingApp.getCurrentLocation();
        Log.d("MapsActivity", String.format("current lat=%1$f lon=%2$f", currentLocation.getLatitude(), currentLocation.getLongitude()));
        LatLng currentPosition = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, Constants.ZOOM_LEVEL));

        ParkingSpotInfoWindowAdapter markerInfoWindowAdapter = new ParkingSpotInfoWindowAdapter(getApplicationContext());
        mMap.setInfoWindowAdapter(markerInfoWindowAdapter);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(final Marker marker) {
                ParkingSpot parkingSpot = ParkingData.getInstance().getParkingSpotByMarker(marker);
                if (parkingSpot == null) {
                    marker.hideInfoWindow();
                    return;
                }

                int reserverTime = Constants.MIN_RESERVE_TIME;
                SearchData searchData = ParkingData.getInstance().getSearchData();
                if (searchData != null &&  searchData.getReserveTime() > Constants.MIN_RESERVE_TIME) {
                    reserverTime = searchData.getReserveTime();
                }
                marker.hideInfoWindow();
                presenter.reserveParkingSpot("" + parkingSpot.getId(), reserverTime);
            }
        });
        presenter.getParkingSpots();
    }

    @Override
    public void showReserveConfirmation() {
        ReservationConfirmationModal reservationConfirmationModal = new ReservationConfirmationModal(MapsActivity.this);
        reservationConfirmationModal.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        reservationConfirmationModal.show();
    }

    private void addParkingSpotMarkers(List<ParkingSpot> parkingSpots) {
        for (ParkingSpot spot : parkingSpots) {
            if (!spot.getIsReserved()) {
                LatLng parkingSpotCoordinate = new LatLng(Double.parseDouble(spot.getLat()), Double.parseDouble(spot.getLng()));
                Marker parkingSpotMarker = mMap.addMarker(new MarkerOptions().position(parkingSpotCoordinate).title(spot.getName()));
                ParkingData.getInstance().putMarkerParkingSpot(parkingSpotMarker, spot);
            }
        }
    }

    @Override
    public void showParkingSpots(List<ParkingSpot> parkingSpots) {
        addParkingSpotMarkers(parkingSpots);
    }

    @Override
    public void showSearchResult(List<ParkingSpot> parkingSpots) {
        mMap.clear();
        ParkingData.getInstance().clearMarkerParkingSpot();

        LatLng newFocus = new LatLng(Double.parseDouble(parkingSpots.get(0).getLat()), Double.parseDouble(parkingSpots.get(0).getLng()));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newFocus, Constants.ZOOM_LEVEL));

        Log.d("Search result", String.format("result size=%1$d", parkingSpots.size()));
        addParkingSpotMarkers(parkingSpots);

        updateSearchControlVisibility();
        hideKeyboard();
    }

    @Override
    public void showErrorMessage() {

    }

    public void checkParkingSpotDetail() {
        navigation.setSelectedItemId(R.id.navigation_reservation);
    }

    private void launchMapFragment() {
        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        mapFragment.getMapAsync(this);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_map, mapFragment, Constants.FRAGMENT_MAP)
                .commit();
    }

    private void launchParkingSpotDetail() {
        ReservationDetailFragment reservationDetailFragment = ReservationDetailFragment.getInstance();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_map, reservationDetailFragment, Constants.FRAGMENT_RESERVATION_DETAIL)
                .commit();
    }

    private void showFragmentByTag(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        getSupportFragmentManager()
                .beginTransaction().show(fragment).commit();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SEARCH CONTROL UI INITIALIZATION METHODS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void initLocationInputView() {
        String[] sfzips = getResources().getStringArray(R.array.sf_zips);
        final String headerText = getString(R.string.app_search_control_current_location);

        CustomAutoCompleteAdapter adapter = new CustomAutoCompleteAdapter(this, android.R.layout.simple_list_item_1, sfzips, headerText);

        adapter.setOnHeaderClickListener(new CustomAutoCompleteAdapter.OnHeaderClickListener() {
            @Override
            public void onHeaderClicked() {
                actvLocation.dismissDropDown();
                actvLocation.setText(headerText, false);
                actvLocation.setSelection(headerText.length());
            }
        });

        actvLocation.setThreshold(0);
        actvLocation.setAdapter(adapter);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String myFormat = "MM/dd/yy";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            etDate.setText(sdf.format(myCalendar.getTime()));
        }

    };

    private void initReservationSeekBar() {
        tvReserveTime.setText(getString(R.string.app_search_control_reserve_time, getResources().getInteger(R.integer.default_reservation_time)));

        sbReservationTime.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

    private void updateSearchControlVisibility() {
        float yPosition = rlMainSearchContainer.getY();
        float containerHeight = getResources().getDimensionPixelSize(R.dimen.app_search_control_main_container_height);
        float searchIconSize = getResources().getDimensionPixelSize(R.dimen.app_search_control_search_button_size);
        float hideHeight = containerHeight - searchIconSize;
        if (yPosition == 0) {
            rlMainSearchContainer.animate().translationY(-hideHeight);
        } else {
            rlMainSearchContainer.animate().translationY(0);
        }
    }

    private void hideSearchControl() {
        float containerHeight = getResources().getDimensionPixelSize(R.dimen.app_search_control_main_container_height);
        rlMainSearchContainer.animate().translationY(-containerHeight);
    }

    private void showSearchControl() {
        rlMainSearchContainer.animate().translationY(0);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END OF SEARCH CONTROL UI INITIALIZATION METHODS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // CLICK LISTENERS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @OnClick(R.id.rl_search_control_button)
    public void onSearchToggle(View view) {
        updateSearchControlVisibility();
    }

    @OnClick(R.id.btn_search)
    public void onSearchButtonClick(View view) {
        String location, date, time;
        int reservationTime;
        location = actvLocation.getText().toString();
        date = etDate.getText().toString();
        time = etTime.getText().toString();
        reservationTime = sbReservationTime.getProgress();
        SearchData searchData = new SearchData(location, date, time, reservationTime);
        ParkingData.getInstance().setSearchData(searchData);
        Log.d("Search", searchData.toString());


        LatLng locationToSearch = null;

        if (location != null && location.length() > 5) {
            location = location.substring(0, 5);
        }
        String regex = "^[0-9]{5}$"; // 5 digits regex for zip

        if (location.matches(regex)) {
            final Geocoder geocoder = new Geocoder(this);
            final String zip = location;
            try {
                List<Address> addresses = geocoder.getFromLocationName(zip, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    locationToSearch = new LatLng(address.getLatitude(), address.getLongitude());
                    String message = String.format("Latitude: %f, Longitude: %f", address.getLatitude(), address.getLongitude());
                    Log.d("Search", message);
                } else {
                    locationToSearch = null;
                    Log.e("Search", "Unable to geocode zipcode");
                }
            } catch (IOException e) {
                locationToSearch = null;
                Log.e("Geocoder Exception", e.toString());
            }
        }

        Map<String, String> params;
        if (locationToSearch == null)  {
            Location currentLocation = ParkingApp.getCurrentLocation();
            locationToSearch = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        }
        params = Utility.buildSearchParams(locationToSearch);
        presenter.searchParkingSpots(params);
    }

    @OnClick(R.id.et_date)
    public void onDateInputClick(View view) {
        new DatePickerDialog(MapsActivity.this, datePickerListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.et_time)
    public void onTimeInputClick(View view) {
        int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
        int minute = myCalendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog = new TimePickerDialog(MapsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                etTime.setText( selectedHour + ":" + selectedMinute);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // END OF CLICK LISTENERS
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

}
