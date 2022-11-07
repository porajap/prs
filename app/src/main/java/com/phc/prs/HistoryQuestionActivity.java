package com.phc.prs;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Adepter.HistoryQuestionAdepter;
import com.phc.prs.Models.QuestionHistoryModel;
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
import static com.phc.prs.Constants.Url.GET_HISTORY_QUESTION_URL;

public class HistoryQuestionActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private ScrollView scrollview;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private ArrayList<QuestionHistoryModel> mData;
    private TextView dataNotFound;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_question);
        bindWidgets();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupScreen();
        bindEvents();
        feedHistory();
    }

    private void feedHistory() {
        swipeRefresh.setRefreshing(true);
        feedData();
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
                feedHistory();
            }
        });

        //when item in recycler view position <= 3 set swipe_refresh(true)
        final LinearLayoutManager _LayoutManager = new LinearLayoutManager(HistoryQuestionActivity.this);
        recyclerView.setLayoutManager(_LayoutManager);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(_LayoutManager.findLastCompletelyVisibleItemPosition() <= 5){
                    swipeRefresh.setEnabled(true);
                }else{
                    swipeRefresh.setEnabled(false);
                }

            }
        });

    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        scrollview = (ScrollView) findViewById(R.id.scrollview);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
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

        titleMenu.setText(R.string.question_history);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnRightTopBar.setImageResource(R.drawable.ic_history);

        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        dataNotFound = (TextView) findViewById(R.id.data_not_found);

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

    private void feedData(){
        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("Cus_Login", Prefs.getString(CUS_LOGIN, ""))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_HISTORY_QUESTION_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                HistoryQuestionActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefresh.setRefreshing(false);
                        dataNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        Toast.makeText(HistoryQuestionActivity.this, R.string.check_your_network, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){
                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<QuestionHistoryModel>>() {}.getType();

                    mData = _gson.fromJson(_result, type);

                    HistoryQuestionActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setVisibility(View.VISIBLE);
                            swipeRefresh.setRefreshing(false);
                            if(mData != null){
                                dataNotFound.setVisibility(View.GONE);
                                String _currentLanguage = getCurrentLanguage().toString();
                                recyclerView.setAdapter(new HistoryQuestionAdepter(mData, _currentLanguage));
                            }else{
                                dataNotFound.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(null);
                            }
                        }
                    });
                }
            }
        });
    }
}