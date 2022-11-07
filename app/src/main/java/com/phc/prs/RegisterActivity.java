package com.phc.prs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Extensions.CustomSpinnerActivity;
import com.phc.prs.Extensions.CustomSpinnerSearchActivity;
import com.phc.prs.Extensions.DialogShow;
import com.phc.prs.Models.DropdownModel;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_NAME;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_NAME;
import static com.phc.prs.Constants.Constants.CUS_PERMISSION;
import static com.phc.prs.Constants.Constants.PREF_KEY_DAY;
import static com.phc.prs.Constants.Constants.PREF_KEY_IS_LOGIN;
import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.GET_DROPDOWN_REGISTER_URL;
import static com.phc.prs.Constants.Url.SUBMIT_DATA_REGISTER_URL;

public class RegisterActivity extends LocalizationActivity {

    private View decorView;
    private ScrollView scrollview;
    private TextView titleMenu;
    private ImageView btnBack;
    private ImageView btnRightTopBar;
    private TextView departmentSelect, customerSelect;

    private String departmentId = "";
    private String departmentText = "";

    private String customerNameId = "";
    private String customerNameText = "";

    private Button btnSubmit;
    private TextView username, password, customerName;
    private int keyForResultSelection = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        bindWidgets();
        setupScreen();
        bindEvents();
//        hideKeyboardOnTouchOutEitText();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        scrollview = (ScrollView) findViewById(R.id.scrollview);

        departmentSelect = (TextView) findViewById(R.id.departmentSelect);
        customerSelect = (TextView) findViewById(R.id.customerSelect);
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

        titleMenu.setText(R.string.menu_register);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnRightTopBar.setImageResource(R.drawable.ic_history);

        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);

        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        username = (TextView) findViewById(R.id.username);
        password = (TextView) findViewById(R.id.password);
        customerName = (TextView) findViewById(R.id.customerName);
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        departmentSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomSpinner("department");
            }
        });

        customerSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomSpinner("customer");
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

    }

    private void validate() {
        String _username = username.getText().toString().trim();
        String _password = password.getText().toString().trim();
        String _customerName = customerName.getText().toString().trim();

        if (
                _username.equals("") ||
                        _password.equals("") ||
                        _customerName.equals("") ||
                        departmentId.equals("") ||
                        customerNameId.equals("")
        ) {
            new DialogShow(RegisterActivity.this, getViewGroup(), getString(R.string.register_please_key_data));
        } else if (_password.length() < 4) {
            new DialogShow(RegisterActivity.this, getViewGroup(), getString(R.string.register_password_failed));
        } else {
            submitData();
        }
    }

    private void submitData() {

        String _username = username.getText().toString().trim();
        String _password = password.getText().toString().trim();
        String _customerName = customerName.getText().toString().trim();

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("username", _username)
                .add("password", _password)
                .add("customerName", _customerName)
                .add("departmentId", departmentId)
                .add("customerId", customerNameId)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + SUBMIT_DATA_REGISTER_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {

                final String _result = response.body().string();

                RegisterActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (_result.equals("success")) {
                            successDialog();
                        } else if (_result.equals("already")) {
                            new DialogShow(RegisterActivity.this, getViewGroup(), getString(R.string.register_submit_already));
                            username.setFocusable(true);
                        } else {
                            new DialogShow(RegisterActivity.this, getViewGroup(), getString(R.string.register_submit_failed));
                        }
                    }
                });
            }
        });

    }

    private void successDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(R.string.register_submit_success);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //close dialog when click OK
        Button btn_click = (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                finish();
            }
        });
    }

    private void openCustomSpinner(String request) {

        switch (request) {
            case "department":
                keyForResultSelection = 1001;
                break;
           case "customer":
                keyForResultSelection = 1002;
                break;
        }

        Intent i = new Intent(RegisterActivity.this, CustomSpinnerSearchActivity.class);
        i.putExtra("request", request);
        startActivityForResult(i, keyForResultSelection);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1001 && data != null) {//department
            departmentId = data.getStringExtra("SELECT_ID");
            departmentText = data.getStringExtra("SELECT_TEXT");
            departmentSelect.setText(departmentText);
        } else if (resultCode == 1002 && data != null) {//customer
            customerNameId = data.getStringExtra("SELECT_ID");
            customerNameText = data.getStringExtra("SELECT_TEXT");
            customerSelect.setText(customerNameText);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            decorView.setSystemUiVisibility(hideSystemUI());
        }
    }

    private int hideSystemUI() {
        return
                View.SYSTEM_UI_FLAG_IMMERSIVE
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

    private ViewGroup getViewGroup() {
        return findViewById(android.R.id.content);
    }

    private void hideKeyboardOnTouchOutEitText() {

        findViewById(R.id.cardView).setOnTouchListener(new View.OnTouchListener() {
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