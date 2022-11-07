package com.phc.prs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;

import org.jsoup.Jsoup;

import java.io.IOException;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;
import static com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE;
import static com.phc.prs.Constants.Url.GOOGLE_PLAY_VERSION_URL;

public class CheckVersionActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private TextView textCurrentVersion;
    private TextView textLastVersion;

    public String currentVersion, lastVersion;
    private Button btnCheckVersion;
    private ProgressDialog loadingDialog;

    private AppUpdateManager appUpdateManager;
    private static final int MY_REQUEST_CODE = 17326;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_version);

        bindWidgets();
        setupScreen();
        bindEvents();

        //get current version
        currentVersion = BuildConfig.VERSION_NAME;
        textCurrentVersion.setText(currentVersion);
    }

    private void checkLastVersion() {
        new GetLatestVersion().execute();
    }

    private void showLoading(){
        loadingDialog = new ProgressDialog(CheckVersionActivity.this, R.style.DialogCheckVersionStyle);
        loadingDialog.setMessage(getString(R.string.loading));
        loadingDialog.show();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        textCurrentVersion = (TextView) findViewById(R.id.textCurrentVersion);
        btnCheckVersion = (Button) findViewById(R.id.btnCheckVersion);
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

        titleMenu.setText(R.string.check_version);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);

    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        btnCheckVersion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get last version
                btnCheckVersion.setEnabled(false);
                showLoading();
                //  checkLastVersion();
                checkLastVersion2();
            }
        });
    }

    private void checkLastVersion2() {

        appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.registerListener(listener);

        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @SuppressLint("WrongConstant")
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                Log.d("appUpdateInfo :",
                        "packageName :" + appUpdateInfo.packageName() + ", " +
                            "availableVersionCode :" + appUpdateInfo.availableVersionCode() + ", " +
                            "updateAvailability :" + appUpdateInfo.updateAvailability() + ", " +
                            "installStatus :" + appUpdateInfo.installStatus());

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(FLEXIBLE)) {
                    requestUpdate(appUpdateInfo);
                } else if (appUpdateInfo.updateAvailability() == 3) {
                    notifyUser();
                } else {
                    dialogCurrentVersion();
                }
            }
        });

        appUpdateInfoTask.addOnCompleteListener(new OnCompleteListener<AppUpdateInfo>() {
            @Override
            public void onComplete(@NonNull Task<AppUpdateInfo> task) {
                loadingDialog.dismiss();
            }
        });
    }

    private void requestUpdate(AppUpdateInfo appUpdateInfo) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, FLEXIBLE, CheckVersionActivity.this, MY_REQUEST_CODE);
            resume();
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
        btnCheckVersion.setEnabled(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == MY_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    if (resultCode != RESULT_OK) {
                        Toast.makeText(this, "RESULT_OK" + resultCode, Toast.LENGTH_LONG).show();
                        Log.d("RESULT_OK  :", "" + resultCode);
                    }
                    break;
                case Activity.RESULT_CANCELED:
                    if (resultCode != RESULT_CANCELED) {
                        Toast.makeText(this, "RESULT_CANCELED" + resultCode, Toast.LENGTH_LONG).show();
                        Log.d("RESULT_CANCELED  :", "" + resultCode);
                    }
                    break;
                case RESULT_IN_APP_UPDATE_FAILED:
                    if (resultCode != RESULT_IN_APP_UPDATE_FAILED) {
                        Toast.makeText(this, "RESULT_IN_APP_UPDATE_FAILED" + resultCode, Toast.LENGTH_LONG).show();
                        Log.d("RESULT_IN_APP_FAILED:", "" + resultCode);
                    }
            }
        }
    }

    InstallStateUpdatedListener listener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                notifyUser();
            }
        }
    };

    private void notifyUser() {
        Snackbar snackbar = Snackbar.make(decorView, "An update has just been downloaded.", Snackbar.LENGTH_INDEFINITE);

        snackbar.setAction("RESTART", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appUpdateManager.completeUpdate();
            }
        });

        TextView mainTextView = (TextView) (snackbar.getView().findViewById(R.id.snackbar_text));
        mainTextView.setTextSize(18);

        snackbar.setActionTextColor(getResources().getColor(R.color.colorWhile));
        snackbar.show();

        btnCheckVersion.setEnabled(true);

    }

    private void resume() {
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    notifyUser();
                }
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
            loadingDialog.dismiss();
            Log.d("checkLastVersion", lastVersion + "");

            if (lastVersion != null) {
                //version convert to float
                float _versionCurrent = Float.parseFloat(currentVersion);
                float _versionLast = Float.parseFloat(lastVersion);

                //check condition(last version is greater than current version)
                if (_versionLast > _versionCurrent) {
                    updateAlertDialog();
                } else {
                    dialogCurrentVersion();
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

    private void dialogCurrentVersion() {

        View view = findViewById(android.R.id.content);
        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView)dialogView.findViewById(R.id.textview_message);
        _text.setText(R.string.now_last_version);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this , R.style.AlertDialogStyle);

        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btn_click= (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnCheckVersion.setEnabled(true);
                alertDialog.dismiss();
            }
        });
    }
}