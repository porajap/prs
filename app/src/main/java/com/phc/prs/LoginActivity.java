package com.phc.prs;

import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.UserLoginModel;
import com.pixplicity.easyprefs.library.Prefs;

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

import static com.phc.prs.Constants.Constants.CUS_IS_MANAGER;
import static com.phc.prs.Constants.Constants.CUS_ORGANIZATION_NAME;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_NAME;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_NAME;
import static com.phc.prs.Constants.Constants.CUS_PERMISSION;
import static com.phc.prs.Constants.Url.GET_LOIN_URL;
import static com.phc.prs.Constants.Constants.PREF_KEY_CHECKBOX;
import static com.phc.prs.Constants.Constants.PREF_KEY_DAY;
import static com.phc.prs.Constants.Constants.PREF_KEY_IS_LOGIN;
import static com.phc.prs.Constants.Constants.PREF_KEY_USERNAME;

public class LoginActivity extends LocalizationActivity {

    private View decorView;
    private CheckBox checkboxRememberLogin;
    private Button buttonLogin;
    private EditText editTextUsername, editTextPassword;
    private ScrollView scrollview;

    private ArrayList<UserLoginModel> dataUserLogin;
    private String username;
    private String password;
    private boolean remember;
    private LinearLayoutCompat inputLayout1, inputLayout2;
    private ImageView logoTop;
    private LinearLayoutCompat layoutCheckbox;
    private LinearLayoutCompat layoutBottom;
    private int daysTimeout = 0;
    private Button btnRegister;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bindWidgets();
        setupScreen();
        bindEvents();
        dateForTimeout();
        checkLogin();

    }

    @Override
    public void onResume() {
        super.onResume();
        hideKeyboardOnTouchOutEitText();
    }

    private void dateForTimeout() {
        Calendar _today = Calendar.getInstance();

        long _thatDay = Prefs.getLong(PREF_KEY_DAY, _today.getTimeInMillis());
        long _diff = _today.getTimeInMillis() - _thatDay;
        daysTimeout = (int) (_diff / (24 * 60 * 60 * 1000));
    }

    private void checkLogin() {
        //is login -> open homepage
        if (Prefs.getBoolean(PREF_KEY_IS_LOGIN, false) && daysTimeout == 0) {
            openHomePage();
        } else {
            bindEvents();
        }
        String _username = Prefs.getString(PREF_KEY_USERNAME, "");
        boolean _remember = Prefs.getBoolean(PREF_KEY_CHECKBOX, false);
        editTextUsername.setText(_username);
        checkboxRememberLogin.setChecked(_remember);
    }

    private void bindEvents() {
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });
    }

    private void validate() {
        username = editTextUsername.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        remember = checkboxRememberLogin.isChecked();

        if (username.isEmpty() || password.isEmpty()) {
            showSnackBar(R.string.username_password_incorrect);
            return;
        }
        getUserData();
    }

    private void getUserData() {
        buttonLogin.setEnabled(false);
        Login();
    }

    private void openHomePage() {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }

    private void bindWidgets() {
        editTextUsername = (EditText) findViewById(R.id.edittext_username_login);
        editTextPassword = (EditText) findViewById(R.id.edittext_password_login);
        checkboxRememberLogin = (CheckBox) findViewById(R.id.checkbox_remember_login);
        buttonLogin = (Button) findViewById(R.id.button_login);
        scrollview = (ScrollView) findViewById(R.id.scrollview);

        logoTop = (ImageView) findViewById(R.id.logo_top);
        inputLayout1 = (LinearLayoutCompat) findViewById(R.id.input_layout_1);
        inputLayout2 = (LinearLayoutCompat) findViewById(R.id.input_layout_2);
        layoutCheckbox = (LinearLayoutCompat) findViewById(R.id.layout_checkbox);
        layoutBottom = (LinearLayoutCompat) findViewById(R.id.layout_bottom);

        btnRegister = (Button) findViewById(R.id.button_register);

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

        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);

        Animation _animation = AnimationUtils.loadAnimation(this, R.anim.top);
        logoTop.setAnimation(_animation);

        _animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        inputLayout1.setAnimation(_animation);
        inputLayout2.setAnimation(_animation);
        layoutBottom.setAnimation(_animation);

        _animation = AnimationUtils.loadAnimation(this, R.anim.bottom);
        buttonLogin.startAnimation(_animation);
        layoutCheckbox.startAnimation(_animation);

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
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
    }

    private void Login() {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody _requestBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_LOIN_URL).post(_requestBody).build();

        client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LoginActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showSnackBar(R.string.check_your_network);
                        setEnableButtonLogin();
                    }
                });
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<UserLoginModel>>() {
                    }.getType();

                    dataUserLogin = _gson.fromJson(_result, type);

                    LoginActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!dataUserLogin.isEmpty()) {
                                saveDataToStorage();
                            } else {
                                showSnackBar(R.string.username_password_incorrect);
                            }
                            setEnableButtonLogin();
                        }
                    });
                }
            }
        });
    }

    private void setEnableButtonLogin() {
        buttonLogin.setEnabled(true);

    }

    private void showSnackBar(int message) {
        Snackbar snackbar = Snackbar.make(decorView, message, Snackbar.LENGTH_SHORT);
        TextView mainTextView = (TextView) (snackbar.getView().findViewById(R.id.snackbar_text));
        mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        mainTextView.setTextSize(20);
        snackbar.show();
    }

    private void saveDataToStorage() {
        // data user login
        Prefs.putString(CUS_LOGIN, dataUserLogin.get(0).getCus_Login());
        Prefs.putString(CUS_CODE, dataUserLogin.get(0).getCus_Code());
        Prefs.putString(CUS_DEP_CODE, dataUserLogin.get(0).getCus_Dep_Code());
        Prefs.putString(CUS_PERMISSION, dataUserLogin.get(0).getPermission());
        Prefs.putString(CUS_NAME, dataUserLogin.get(0).getCus_Name());
        Prefs.putString(CUS_DEP_NAME, dataUserLogin.get(0).getCus_Dep_Name());
        Prefs.putString(CUS_IS_MANAGER, dataUserLogin.get(0).getIsManager());
        Prefs.putString(CUS_ORGANIZATION_NAME, dataUserLogin.get(0).getOrganizationName());

        //remember username
        if (remember) {
            Prefs.putString(PREF_KEY_USERNAME, username);
            Prefs.putBoolean(PREF_KEY_CHECKBOX, true);
        } else {
            Prefs.putString(PREF_KEY_USERNAME, null);
            Prefs.putBoolean(PREF_KEY_CHECKBOX, false);
        }

        //set status login = true (for come back to home menu)
        Prefs.putBoolean(PREF_KEY_IS_LOGIN, true);

        //current date (for check timeout give login again when date diff > 0)
        Calendar _today = Calendar.getInstance();
        Prefs.putLong(PREF_KEY_DAY, _today.getTimeInMillis());

        openHomePage();
    }

    private void hideKeyboardOnTouchOutEitText() {
        findViewById(R.id.scrollview).setOnTouchListener(new View.OnTouchListener() {
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