package com.phc.prs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Adepter.ApproveAdepter;
import com.phc.prs.Extensions.DialogShow;
import com.phc.prs.Extensions.DividerItemDecoration;
import com.phc.prs.Models.NotificationModel;
import com.phc.prs.Models.RegisterModel;
import com.pixplicity.easyprefs.library.Prefs;

import org.jetbrains.annotations.NotNull;

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

import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Constants.CUS_IS_MANAGER;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.CUS_PERMISSION;
import static com.phc.prs.Constants.Url.APPROVE_USER_URL;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.GET_DATA_REGISTER_URL;

public class ApproveActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<NotificationModel> dataNotification;
    private RecyclerView recyclerView;
    private TextView dataNotFound;
    private ArrayList<RegisterModel> dataRegister;

    boolean IS_APPROVE_ACTION = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approve);

        bindWidgets();
        setupScreen();
        bindEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        getDataRegister();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(ApproveActivity.this, LinearLayoutManager.VERTICAL));

        dataNotFound = (TextView) findViewById(R.id.data_not_found);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
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
        titleMenu.setText(R.string.register);
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getDataRegister();
            }
        });

        final LinearLayoutManager _LayoutManager = new LinearLayoutManager(ApproveActivity.this);
        recyclerView.setLayoutManager(_LayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (_LayoutManager.findLastCompletelyVisibleItemPosition() <= 5) {
                    swipeRefresh.setEnabled(true);
                } else {
                    swipeRefresh.setEnabled(false);
                }

            }
        });
    }

    private void getDataRegister() {
        swipeRefresh.setRefreshing(true);
        feedDataRegister();
    }

    private void feedDataRegister() {
        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("manager", Prefs.getString(CUS_IS_MANAGER, "0"))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DATA_REGISTER_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApproveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        dataNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(ApproveActivity.this, R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type _type = new TypeToken<List<RegisterModel>>() {
                    }.getType();

                    dataRegister = _gson.fromJson(_result, _type);

                    ApproveActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);

                            if (!dataRegister.isEmpty()) {
                                dataNotFound.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                String _currentLanguage = getCurrentLanguage().toString();
                                recyclerView.setAdapter(new ApproveAdepter(dataRegister, ApproveActivity.this, _currentLanguage));

                            } else {
                                dataNotFound.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                        }
                    });
                }
            }
        });

    }

    public void clickToApproveUser(String cusLogin, String action) {
        if(!IS_APPROVE_ACTION){
            IS_APPROVE_ACTION = true;
            confirmDialog(cusLogin, action);
        }
    }

    private void confirmDialog(final String cusLogin, final  String action) {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_choice, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);

        if(action.equals("approve")){
            _text.setText(R.string.register_dialog_confirm);
        }else{
            _text.setText(R.string.register_dialog_cancel);
        }

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

                approveUser(cusLogin, action);

                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IS_APPROVE_ACTION = false;
                alertDialog.dismiss();
            }
        });
    }

    private void approveUser(String cusLogin, final String action){
        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("cusLogin", cusLogin)
                .add("action", action)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + APPROVE_USER_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                ApproveActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        IS_APPROVE_ACTION = false;
                        Toast.makeText(ApproveActivity.this, R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()){
                    final String _result = response.body().string();
                    ApproveActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String _message = "";
                            if(_result.equals("success")){
                                if(action.equals("approve")){
                                    _message = getString(R.string.register_approve_success);
                                }else{
                                    _message = getString(R.string.register_cancel_success);
                                }
                            }else{
                                if(action.equals("approve")){
                                    _message = getString(R.string.register_approve_failed);
                                }else{
                                    _message = getString(R.string.register_cancel_failed);
                                }
                            }
                            new DialogShow(ApproveActivity.this, getViewGroup(), _message);
                            IS_APPROVE_ACTION = false;
                            getDataRegister();
                        }
                    });
                }
            }
        });
    }

    private ViewGroup getViewGroup() {
        return findViewById(android.R.id.content);
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