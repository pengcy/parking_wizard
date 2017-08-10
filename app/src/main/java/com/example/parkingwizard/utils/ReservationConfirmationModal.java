package com.example.parkingwizard.utils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.parkingwizard.ui.screen_map.MapsActivity;
import com.example.parkingwizard.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by peng on 8/9/17.
 */
public class ReservationConfirmationModal extends Dialog {
    public Activity activity;

    @BindView(R.id.btn_check)
    Button btnCheck;
    @BindView(R.id.tv_close)
    TextView tvClose;

    public ReservationConfirmationModal(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.reservation_confirmation_modal);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.btn_check)
    public void onCheckButtonClick(View view) {
        ((MapsActivity) activity).checkParkingSpotDetail();
        dismiss();
    }

    @OnClick(R.id.tv_close)
    public void onCloseButtonClick(View view) {
        dismiss();
    }

}