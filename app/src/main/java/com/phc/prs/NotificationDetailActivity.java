package com.phc.prs;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.CheckStatusAllModel;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.THIS_NOTIFICATION_URL;
import static com.phc.prs.Constants.Url.UPDATE_NOTIFICATION_URL;

public class NotificationDetailActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;

    private String notificationRowId = "";
    private String notificationStatus = "";

    TextView textMachine, textLocation;
    ImageView imgReceive, imgRepair;

    TextView labelReceive, labelRepair;

    TextView dateRequest, dateReceive, dateRepair;
    TextView timeRequest, timeReceive, timeRepair;

    ImageView icSuccessReceive, icSuccessRepair;

    private ArrayList<CheckStatusAllModel> dataNotification;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);

        bindWidgets();
        bindIntent();
        thisDataOnSelection(notificationRowId); //get data from database

        setupScreen();
        bindEvents();
    }

    private void bindIntent() {
        Intent i = getIntent();
        notificationRowId = i.getStringExtra("rowId");
        notificationStatus = i.getStringExtra("status");
    }


    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);


        textMachine = (TextView) findViewById(R.id.textMachine);
        textLocation = (TextView) findViewById(R.id.textLocation);

        imgReceive = (ImageView) findViewById(R.id.imgReceive);
        imgRepair = (ImageView) findViewById(R.id.imgRepair);

        labelReceive = (TextView) findViewById(R.id.labelReceive);
        labelRepair = (TextView) findViewById(R.id.labelRepair);

        dateRequest = (TextView) findViewById(R.id.dateRequest);
        dateReceive = (TextView) findViewById(R.id.dateReceive);
        dateRepair = (TextView) findViewById(R.id.dateRepair);

        timeRequest = (TextView) findViewById(R.id.timeRequest);
        timeReceive = (TextView) findViewById(R.id.timeReceive);
        timeRepair = (TextView) findViewById(R.id.timeRepair);


        icSuccessReceive = (ImageView) findViewById(R.id.icSuccessReceive);
        icSuccessRepair = (ImageView) findViewById(R.id.icSuccessRepair);
    }

    private void setupScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    decorView.setSystemUiVisibility(hideSystemUI());
                }
            }
        });

        titleMenu.setText(R.string.notification_text_detail);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnRightTopBar.setImageResource(R.drawable.ic_history);
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
    }


    public void thisDataOnSelection(String rowId) {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("rowId", rowId)
                .add("SELECTION", "READ_WHEN_SELECTION")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + THIS_NOTIFICATION_URL).post(_requestBody).build();
        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NotificationDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<CheckStatusAllModel>>() {
                    }.getType();

                    dataNotification = _gson.fromJson(_result, type);

                    NotificationDetailActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!dataNotification.isEmpty()) {
                                showDataNotification();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void showDataNotification() {
        updateThisStatusOnSelection(dataNotification.get(0).getRowId(), notificationStatus);

        String _repairStatus = dataNotification.get(0).getRepair_Status();
        String _machine = dataNotification.get(0).getDispenserID();
        String _location = getString(R.string.text_building) + " " + dataNotification.get(0).getBuildingName() + " " + getString(R.string.text_floor) + " " + dataNotification.get(0).getBuildingFloor() + " " + getString(R.string.text_room) + " " + dataNotification.get(0).getRoomName();

        String _dateRequest = dataNotification.get(0).getDate_Request();
        String _dateReceive = dataNotification.get(0).getDate_Receive();
        String _dateRepair = dataNotification.get(0).getDate_Repair();

        String _timeRequest = dataNotification.get(0).getTime_RequestEng();
        String _timeReceive = dataNotification.get(0).getTime_ReceiveEng();
        String _timeRepair = dataNotification.get(0).getTime_RepairEng();

        String _currentLanguage = getCurrentLanguage().toString();
        if(_currentLanguage.equals("th")){
            _timeRequest = dataNotification.get(0).getTime_RequestTH() + " น.";
            _timeReceive = dataNotification.get(0).getTime_ReceiveTH()  + " น.";
            _timeRepair = dataNotification.get(0).getTime_RepairTH()  + " น.";
        }
        //title
        textMachine.setText(_machine);
        textLocation.setText(_location);

        //date
        dateRequest.setText(_dateRequest);
        dateReceive.setText(_dateReceive);
        dateRepair.setText(_dateRepair);

        //time
        timeRequest.setText(getString(R.string.text_time) + " " + _timeRequest);
        timeReceive.setText(getString(R.string.text_time) + " " + _timeReceive);
        timeRepair.setText(getString(R.string.text_time) + " " + _timeRepair);

        //change image
        imgReceive.setImageResource(setImageReceive(_repairStatus));
        imgRepair.setImageResource(setImageRepair(_repairStatus));

        //set background label and hide detail
        if (_repairStatus.equals("1")) {
            //receive detail
            labelReceive.setBackgroundResource(R.drawable.label_check_status_blue_background);
            labelReceive.setTextColor(getResources().getColor(R.color.colorBlack));
            dateReceive.setVisibility(View.VISIBLE);
            timeReceive.setVisibility(View.VISIBLE);

            //repair detail
            labelRepair.setBackgroundResource(R.drawable.label_check_status_white_tranparent);
            labelRepair.setTextColor(getResources().getColor(R.color.colorGray));
            dateRepair.setVisibility(View.INVISIBLE);
            timeRepair.setVisibility(View.INVISIBLE);

            //icon success
            icSuccessReceive.setVisibility(View.VISIBLE);
            icSuccessRepair.setVisibility(View.INVISIBLE);
        } else {
            //receive detail
            labelReceive.setBackgroundResource(R.drawable.label_check_status_blue_background);
            labelReceive.setTextColor(getResources().getColor(R.color.colorBlack));
            dateReceive.setVisibility(View.VISIBLE);
            timeReceive.setVisibility(View.VISIBLE);

            //repair detail
            labelRepair.setBackgroundResource(R.drawable.label_check_status_blue_background);
            labelRepair.setTextColor(getResources().getColor(R.color.colorBlack));
            dateRepair.setVisibility(View.VISIBLE);
            timeRepair.setVisibility(View.VISIBLE);

            //icon success
            icSuccessReceive.setVisibility(View.VISIBLE);
            icSuccessRepair.setVisibility(View.VISIBLE);
        }

    }

    private int setImageReceive(String status) {
        if (status.equals("0")) {
            return R.drawable.ic_receive_w;
        }
        return R.drawable.ic_receive;
    }

    private int setImageRepair(String status) {
        if (status.equals("2")) {
            return R.drawable.ic_repair;
        }
        return R.drawable.ic_repair_w;
    }

    public void updateThisStatusOnSelection(String rowId, String status) {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("rowId", rowId)
                .add("status", status)
                .add("action", "READ_WHEN_SELECTION")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + UPDATE_NOTIFICATION_URL).post(_requestBody).build();
        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NotificationDetailActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemUI());
        }
    }

    private int hideSystemUI() {
        return View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                ;
    }
}