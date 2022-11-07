package com.phc.prs;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Extensions.DialogShow;
import com.phc.prs.Models.DropdownModel;
import com.pixplicity.easyprefs.library.Prefs;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_NAME;
import static com.phc.prs.Constants.Url.GET_DROPDOWN_URL;
import static com.phc.prs.Constants.Url.SEND_INSTALL_URL;

public class InstallActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private ScrollView scrollview;
    private SearchableSpinner searchablespinner;
    private TextView dateInstall, timeInstall;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private ArrayList<DropdownModel> mData;

    ArrayList<String> building_ar_text = new ArrayList<>();
    ArrayList<String> building_ar_value = new ArrayList<String>();
    private Button btn_send;
    private RadioButton radioSetting, radioMove, radioUninstall;
    private EditText num, floor;
    private EditText contact;
    private TextView customerName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_install);

        bindWidgets();
        setupScreen();
        bindEvents();
        feedDepartment();
        hideKeyboardOnTouchOutEitText();

        customerName.setText(Prefs.getString(CUS_NAME, ""));
        defaultDateTime();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void feedDepartment() {
        getDepartment();
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });


        //dialog date and time
        dateInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(InstallActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dateInstall.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }

                }, mYear, mMonth, mDay);
                c.add(Calendar.YEAR, 0);
                datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
                c.add(Calendar.YEAR, 1);
                datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
                datePickerDialog.show();
            }
        });

        timeInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(InstallActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                timeInstall.setText(hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });
        //end dialog date and time


        //spinner
        searchablespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String item = parent.getItemAtPosition(position).toString();
                    Toast.makeText(parent.getContext(), "แผนก : " + item, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //end spinner

        //send data
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_send.setEnabled(false);
                validate();
            }
        });
        //end send data
    }

    private void defaultDateTime() {
        // Get Current Date
        final Calendar _calendar = Calendar.getInstance();
        int _year = _calendar.get(Calendar.YEAR);
        int _month = _calendar.get(Calendar.MONTH);
        int _day = _calendar.get(Calendar.DAY_OF_MONTH);

        //set default date
        dateInstall.setText(_day + "-" + (_month + 1) + "-" + _year);

        int _hour = _calendar.get(Calendar.HOUR_OF_DAY);
        int _minute = _calendar.get(Calendar.MINUTE);

        //set default time
        timeInstall.setText(_hour + ":" + _minute);

    }

    private void validate() {
        if (num.getText().toString().isEmpty() || floor.getText().toString().isEmpty() || dateInstall.getText().toString().isEmpty() || timeInstall.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), getString(R.string.install_please_key_data), Toast.LENGTH_LONG).show();
            btn_send.setEnabled(true);

            return;
        } else if (building_ar_value.get(searchablespinner.getSelectedItemPosition()).equals("0")) {
            Toast.makeText(getApplicationContext(), getString(R.string.install_please_key_data), Toast.LENGTH_LONG).show();
            btn_send.setEnabled(true);

            return;
        }

        sendData();
    }

    private void sendData() {
        sendDataInstall();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        scrollview = (ScrollView) findViewById(R.id.scrollview);

        searchablespinner = (SearchableSpinner) findViewById(R.id.search_spinner_department);
        searchablespinner.setTitle(getString(R.string.install_select_dep));
        searchablespinner.setPositiveButton(getString(R.string.cancel));

        dateInstall = (TextView) findViewById(R.id.date_install);
        timeInstall = (TextView) findViewById(R.id.time_install);


        btn_send = (Button) findViewById(R.id.btn_send);

        radioSetting = (RadioButton) findViewById(R.id.radio_setting);
        radioMove = (RadioButton) findViewById(R.id.radio_move);
        radioUninstall = (RadioButton) findViewById(R.id.radio_uninstall);

        num = (EditText) findViewById(R.id.num);
        floor = (EditText) findViewById(R.id.floor);
        contact = (EditText) findViewById(R.id.contact);

        customerName = (TextView) findViewById(R.id.customer_name);

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

        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        titleMenu.setText(R.string.install_install);


        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);

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

    private void getDepartment() {
        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("p_data", "department")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DROPDOWN_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                InstallActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        building_ar_text.clear();
                        building_ar_value.clear();
                        building_ar_text.add("Data not found");
                        building_ar_value.add("0");

                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstallActivity.this, android.R.layout.simple_spinner_dropdown_item, building_ar_text);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        searchablespinner.setAdapter(adapter);
                        searchablespinner.setSelection(0);

                        Toast.makeText(InstallActivity.this, R.string.check_your_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    final String _result = response.body().string();

                    InstallActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Gson _gson = new Gson();

                            Type type = new TypeToken<List<DropdownModel>>() {
                            }.getType();

                            mData = _gson.fromJson(_result, type);

                            building_ar_text.clear();
                            building_ar_value.clear();

                            if (mData.size() == 0) {
                                building_ar_text.add("Data not found");
                                building_ar_value.add("0");
                            } else {
                                int count = 0;
                                building_ar_text.add("-");
                                building_ar_value.add("0");

                                for (int i = 0; i < mData.size(); i++) {
                                    building_ar_text.add(mData.get(i).getData());
                                    building_ar_value.add(mData.get(i).getValue());
                                    count++;
                                }
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(InstallActivity.this, android.R.layout.simple_spinner_dropdown_item, building_ar_text);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            searchablespinner.setAdapter(adapter);
                            searchablespinner.setSelection(0);
                        }
                    });
                }
            }
        });
    }

    private void sendDataInstall() {
        String _mode;
        if (radioSetting.isChecked()) {
            _mode = "1";
        } else if (radioMove.isChecked()) {
            _mode = "2";
        } else {
            _mode = "3";
        }

        String _cus_login = Prefs.getString(CUS_LOGIN, "");
        String _repair_mode = _mode;
        String _service_mode = _mode;

        String _service_piece = num.getText().toString().trim();
        String _service_department_code = building_ar_value.get(searchablespinner.getSelectedItemPosition());
        String _service_floor = floor.getText().toString().trim();

        String _service_contact = contact.getText().toString().trim();
        String _service_date = dateInstall.getText().toString().trim();
        String _service_time = timeInstall.getText().toString().trim();

        OkHttpClient _client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody _requestBody = new FormBody.Builder()
                .add("p_Cus_Login", _cus_login)
                .add("p_Repair_Mode", _repair_mode)
                .add("p_Service_Mode", _service_mode)
                .add("p_Service_Piece", _service_piece)
                .add("p_Service_Department_Code", _service_department_code)
                .add("p_Service_BuildingFloor", _service_floor)
                .add("p_Service_Contact", _service_contact)
                .add("p_Date", _service_date)
                .add("p_Time", _service_time)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + SEND_INSTALL_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                InstallActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(InstallActivity.this, R.string.check_your_network, Toast.LENGTH_SHORT).show();
                        btn_send.setEnabled(true);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String _result = response.body().string();
                    InstallActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (_result.equals("Success")) {
                                clearText();
                                new DialogShow(InstallActivity.this, getViewGroup(), getString(R.string.install_send_success));
                            } else {
                                new DialogShow(InstallActivity.this, getViewGroup(), getString(R.string.install_send_failed));
                            }
                            btn_send.setEnabled(true);
                        }
                    });

                }
            }
        });
    }

    private ViewGroup getViewGroup() {
        return findViewById(android.R.id.content);
    }

    private void clearText() {
        radioSetting.setChecked(true);

        num.setText("");
        searchablespinner.setSelection(0);
        floor.setText("");

        contact.setText("");
        dateInstall.setText("");
        timeInstall.setText("");
    }

    private void hideKeyboardOnTouchOutEitText() {
        findViewById(R.id.formLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return true;
            }
        });

    }
}