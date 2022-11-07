package com.phc.prs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.phc.prs.Adepter.NotificationAdepter;
import com.phc.prs.Extensions.DividerItemDecoration;
import com.phc.prs.Models.NotificationModel;
import com.pixplicity.easyprefs.library.Prefs;

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
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Url.DELETE_NOTIFICATION_URL;
import static com.phc.prs.Constants.Url.GET_DATA_NOTIFICATION_URL;
import static com.phc.prs.Constants.Url.UPDATE_NOTIFICATION_URL;

public class NotificationsActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<NotificationModel> dataNotification;
    private RecyclerView recyclerView;
    private TextView dataNotFound;
    private boolean IS_OPEN_ACTIVITY = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        bindWidgets();
        setupScreen();
        bindEvents();
    }

    @Override
    public void onResume() {
        super.onResume();
        IS_OPEN_ACTIVITY = false;
        getDataNotification();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewNotification);
        recyclerView.addItemDecoration(new DividerItemDecoration(NotificationsActivity.this, LinearLayoutManager.VERTICAL));

        dataNotFound = (TextView) findViewById(R.id.data_not_found);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimaryDark));
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
                getDataNotification();
            }
        });

        final LinearLayoutManager _LayoutManager = new LinearLayoutManager(NotificationsActivity.this);
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

    private void getDataNotification() {
        swipeRefresh.setRefreshing(true);
        feedDataNotification();
    }

    private void feedDataNotification() {
        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("CUS_LOGIN", Prefs.getString(CUS_LOGIN, ""))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DATA_NOTIFICATION_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NotificationsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        dataNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(NotificationsActivity.this, R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<NotificationModel>>() {
                    }.getType();

                    dataNotification = _gson.fromJson(_result, type);

                    NotificationsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            swipeRefresh.setRefreshing(false);
                            if (!dataNotification.isEmpty()) {
                                dataNotFound.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);

                                String _currentLanguage = getCurrentLanguage().toString();
                                recyclerView.setAdapter(new NotificationAdepter(dataNotification, NotificationsActivity.this, _currentLanguage));
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

    public void scrollNotification(String rowId, String status) {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("rowId", rowId)
                .add("status", status)
                .add("action", "SCROLL")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + UPDATE_NOTIFICATION_URL).post(_requestBody).build();
        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NotificationsActivity.this.runOnUiThread(new Runnable() {
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

    public void deleteNotification(String rowId, String status) {

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("rowId", rowId)
                .add("status", status)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + DELETE_NOTIFICATION_URL).post(_requestBody).build();
        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                NotificationsActivity.this.runOnUiThread(new Runnable() {
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
                    if (_result.equals("success")) {
                        NotificationsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getDataNotification();
                            }
                        });
                    }
                }
            }
        });
    }

    public void actionToThisStatusOnSelection(String rowId, String status) {
        if (!IS_OPEN_ACTIVITY) {
            IS_OPEN_ACTIVITY = true;
            Intent i = new Intent(NotificationsActivity.this, NotificationDetailActivity.class);
            i.putExtra("rowId", rowId);
            i.putExtra("status", status);
            startActivity(i);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
        }
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
        titleMenu.setText(R.string.notification_text_notification);
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