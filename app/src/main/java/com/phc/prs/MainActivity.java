package com.phc.prs;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.pixplicity.easyprefs.library.Prefs;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

import static com.phc.prs.Constants.Constants.CUS_IS_MANAGER;
import static com.phc.prs.Constants.Constants.CUS_ORGANIZATION_NAME;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.COUNT_NOTIFICATION_URL;
import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_CODE;
import static com.phc.prs.Constants.Constants.CUS_DEP_NAME;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_NAME;
import static com.phc.prs.Constants.Constants.CUS_PERMISSION;
import static com.phc.prs.Constants.Constants.PREF_KEY_DAY;
import static com.phc.prs.Constants.Constants.PREF_KEY_IS_LOGIN;
import static com.phc.prs.Constants.Url.COUNT_USER_REGISTER_URL;
import static com.phc.prs.Constants.Url.GOOGLE_PLAY_VERSION_URL;

public class MainActivity extends LocalizationActivity {

    private View decorView;

    private RelativeLayout btnMenuMaintenance, btnMenuCheckStatus, btnMenuQA;
    private ScrollView scrollView;

    private TextView cusName;
    private TextView cusPermission;
    private TextView textNotification;

    int NOTIFICATION = 0;
    int USER_REGISTER = 0;
    private RelativeLayout buttonNotification;
    private SwipeRefreshLayout swipeRefresh;
    private RelativeLayout backgroundMain;

    private ImageView btnMenu;

    public String currentVersion, lastVersion = "0";
    private FloatingActionButton fabUserApprove;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindWidgets();
        setupAnimation();
        setupScreen();

        //show text welcome in first time
        Snackbar snackbar = Snackbar.make(decorView, R.string.welcome_back, Snackbar.LENGTH_SHORT);
        TextView mainTextView = (TextView) (snackbar.getView().findViewById(R.id.snackbar_text));
        mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mainTextView.setGravity(Gravity.CENTER_HORIZONTAL);
        snackbar.show();

        //check last version
        //checkLastVersion();
    }

    private void checkLastVersion() {

        new GetLatestVersion().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        bindEvents();
        setEnableButton();

        countNotification();
        countUserApprove();
    }

    private void setEnableButton() {
        btnMenuMaintenance.setEnabled(true);
        btnMenuCheckStatus.setEnabled(true);
        btnMenuQA.setEnabled(true);
        buttonNotification.setEnabled(true);
        fabUserApprove.setEnabled(true);
    }

    private void bindEvents() {

        btnMenuMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenuMaintenance.setEnabled(false);

                //check permission camera
                int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA);

                //allow
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getApplication(), CameraBarcodeActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);

                } else {
                    askPermission();
                }

            }
        });

        btnMenuCheckStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenuCheckStatus.setEnabled(false);
                Intent i = new Intent(getApplication(), CheckStatusActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });

        btnMenuQA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenuQA.setEnabled(false);
                Intent i = new Intent(getApplication(), QuestionActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });

        buttonNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonNotification.setEnabled(false);
                Intent i = new Intent(getApplication(), NotificationsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh.setRefreshing(true);
                countNotification();
                countUserApprove();
            }
        });


        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnMenu.setEnabled(false);
                showPopupMenu(v);
            }
        });

        fabUserApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabUserApprove.setEnabled(false);
                Intent i = new Intent(getApplication(), ApproveActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });
    }

    private void showPopupMenu(View anchor) {
        //init the wrapper with style
        Context wrapper = new ContextThemeWrapper(this, null);

        //init the popup
        PopupMenu popup = new PopupMenu(wrapper, anchor);

        /*  The below code in try catch is responsible to display icons*/
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        //inflate menu
        popup.getMenuInflater().inflate(R.menu.menu_main, popup.getMenu());
        //implement click events
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                btnMenu.setEnabled(true);
                switch (menuItem.getItemId()) {
                    case R.id.btnOther:
                        openChangeLanguageActivity();
                        break;
                    case R.id.btnCheckVersion:
                        openCheckVersionActivity();
                        break;
                    case R.id.btnLogout:
                        askForConfirmToLogout();
                        break;
                }
                return true;
            }
        });
        popup.setGravity(Gravity.END);

        popup.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
                btnMenu.setEnabled(true);
            }
        });

        popup.show();
    }

    private void openChangeLanguageActivity() {
        Intent i = new Intent(getApplication(), ChangeLanguageActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
    }

    private void openCheckVersionActivity() {
        Intent i = new Intent(getApplication(), CheckVersionActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
    }

    private void bindWidgets() {

        scrollView = (ScrollView) findViewById(R.id.scrollview);

        btnMenuMaintenance = (RelativeLayout) findViewById(R.id.button_menu_maintenance);
        btnMenuCheckStatus = (RelativeLayout) findViewById(R.id.button_menu_check_status);
        btnMenuQA = (RelativeLayout) findViewById(R.id.button_menu_q_a);

        cusName = (TextView) findViewById(R.id.cus_name);
        cusPermission = (TextView) findViewById(R.id.cus_permission);

        textNotification = (TextView) findViewById(R.id.textNotification);

        buttonNotification = (RelativeLayout) findViewById(R.id.buttonNotification);

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));

        backgroundMain = (RelativeLayout) findViewById(R.id.backgroundMain);

        btnMenu = (ImageView) findViewById(R.id.btnMenu);

        fabUserApprove = (FloatingActionButton) findViewById(R.id.fabUserApprove);
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

        scrollView.setSmoothScrollingEnabled(true);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.setHorizontalScrollBarEnabled(false);

        String _permissionName = "";
        switch (Prefs.getString(CUS_PERMISSION, "")) {
            case "2":
                _permissionName = "Admin";
                break;
            case "1":
                _permissionName = "หัวหน้าแผนก";
                break;
            default:
                _permissionName = "ผู้ใช้ทั่วไป";
                break;
        }


        String _isManager = Prefs.getString(CUS_IS_MANAGER, "0");
        if(_isManager.equals("1")){
            fabUserApprove.setVisibility(View.VISIBLE);
        }else{
            fabUserApprove.setVisibility(View.INVISIBLE);
        }

        String _organization = Prefs.getString(CUS_ORGANIZATION_NAME, "");
        String _cusName = Prefs.getString(CUS_NAME, "");

        String _titleName = _organization + " (" + _cusName + ")";

        if(Prefs.getString(CUS_IS_MANAGER, "0").equals("1")){
            _titleName = _cusName ;
        }

        cusName.setText(_titleName);
        cusPermission.setText(_permissionName);
    }

    private void setupAnimation() {
        Animation _animation = AnimationUtils.loadAnimation(this, R.anim.bottom);
        scrollView.setAnimation(_animation);

        _animation = AnimationUtils.loadAnimation(this, R.anim.main_background);
        backgroundMain.setAnimation(_animation);
    }

    private void countNotification() {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("CUS_LOGIN", Prefs.getString(CUS_LOGIN, ""))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + COUNT_NOTIFICATION_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(MainActivity.this, R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String _result = response.body().string();
                    NOTIFICATION = Integer.parseInt(_result);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                            if (NOTIFICATION > 99) {
                                textNotification.setText(NOTIFICATION + "+");
                            } else {
                                textNotification.setText(String.valueOf(NOTIFICATION));
                            }

                            if (NOTIFICATION > 0) {
                                final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                buttonNotification.startAnimation(animShake);

                                new android.os.Handler().postDelayed(
                                        new Runnable() {
                                            public void run() {
                                                buttonNotification.clearAnimation();
                                            }
                                        }, 2000);
                                textNotification.setVisibility(View.VISIBLE);
                            } else {
                                buttonNotification.clearAnimation();
                                textNotification.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });
    }

    private void countUserApprove() {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("cusCode", Prefs.getString(CUS_CODE, ""))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + COUNT_USER_REGISTER_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        Toast.makeText(MainActivity.this, R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String _result = response.body().string();
                    USER_REGISTER = Integer.parseInt(_result);

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            String _isManager = Prefs.getString(CUS_IS_MANAGER, "");
                            if(_isManager.equals("1")){
                                if(USER_REGISTER > 0){
                                    final Animation animShake = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.shake);
                                    fabUserApprove.startAnimation(animShake);

                                    new android.os.Handler().postDelayed(
                                            new Runnable() {
                                                public void run() {
                                                    fabUserApprove.clearAnimation();
                                                }
                                            }, 2000);

                                    fabUserApprove.setVisibility(View.VISIBLE);
                                }else{
                                    fabUserApprove.setVisibility(View.INVISIBLE);
                                }
                            }else{
                                fabUserApprove.setVisibility(View.INVISIBLE);
                            }
                            swipeRefresh.setRefreshing(false);
                        }
                    });
                }
            }
        });
    }

    private void askPermission() {

        Nammu.init(this);
        Nammu.askForPermission(this, Manifest.permission.CAMERA, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Intent i = new Intent(getApplication(), CameraBarcodeActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }

            @Override
            public void permissionRefused() {
                Log.d("permissionCheck", "no");
            }
        });
        btnMenuMaintenance.setEnabled(true);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent i = new Intent(getApplication(), CameraBarcodeActivity.class);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
        }
    }

    private void askForConfirmToLogout() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_choice, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(R.string.logout);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setView(dialogView);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        Button btnConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Prefs.putBoolean(PREF_KEY_IS_LOGIN, false);
                Prefs.putLong(PREF_KEY_DAY, 0);

                Prefs.putString(CUS_LOGIN, "");
                Prefs.putString(CUS_CODE, "");
                Prefs.putString(CUS_DEP_CODE, "");
                Prefs.putString(CUS_PERMISSION, "");
                Prefs.putString(CUS_NAME, "");
                Prefs.putString(CUS_DEP_NAME, "");

                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                alertDialog.dismiss();
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

    }

    private class GetLatestVersion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                lastVersion = Jsoup.connect(GOOGLE_PLAY_VERSION_URL + getPackageName())
                        .timeout(3000)
                        .get()
                        .select("div.hAyfc:nth-child(4)>" +
                                "span:nth-child(2) > div:nth-child(1)" +
                                "> span:nth-child(1)")
                        .first()
                        .ownText();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return lastVersion;
        }

        @Override
        protected void onPostExecute(String s) {
            currentVersion = BuildConfig.VERSION_NAME;
            if (lastVersion != null) {
                //version convert to float
                float _versionCurrent = Float.parseFloat(currentVersion);
                float _versionLast = Float.parseFloat(lastVersion);

                //check condition(last version is greater than current version)
                if (_versionLast > _versionCurrent) {
                    updateAlertDialog();
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private void updateAlertDialog() {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_update_version, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);

        String _textDetail = lastVersion + getString(R.string.dialog_text_start) + currentVersion + getString(R.string.dialog_text_end);
        _text.setText(getResources().getText(R.string.app_name) + " " + _textDetail);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setView(dialogView);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        Button btnConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);

        btnConfirm.setText(R.string.text_update);
        btnCancel.setText(R.string.later);
        btnCancel.setTextColor(getResources().getColor(R.color.colorGray));

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity((new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()))));

                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

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
//                | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                ;
    }

}