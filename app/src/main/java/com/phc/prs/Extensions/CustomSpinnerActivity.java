package com.phc.prs.Extensions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.DropdownModel;
import com.phc.prs.R;
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

import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.GET_DROPDOWN_URL;

public class CustomSpinnerActivity extends Activity {

    private RecyclerView recyclerView;
    private ArrayList<DropdownModel> mData;
    private Button buttonCancel;
    private int KEY_FOR_PROBLEM = 1001;
    private View decorView;
    private TextView textTitle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_spiner);


        bindWidget();
        feedDataProblem();
        bindEvents();
        setupScreen();
    }

    @SuppressLint("SetTextI18n")
    private void setupScreen() {
        String _currentLanguage = Prefs.getString(PREF_KEY_LANGUAGE_USE, "en");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == 0) {
                    decorView.setSystemUiVisibility(hideSystemUI());
                }
            }
        });

        if(_currentLanguage.equals("th")){
            textTitle.setText("กรุณาเลือก");
            buttonCancel.setText("ยกเลิก");
        }else{
            textTitle.setText("Select");
            buttonCancel.setText("cancel");
        }
    }

    private void bindEvents() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindWidget() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        textTitle = (TextView) findViewById(R.id.textTitle);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
    }

    private void feedDataProblem() {
        OkHttpClient _client = new OkHttpClient();
        RequestBody _requestBody = new FormBody.Builder()
                .add("p_data", "problem")
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DROPDOWN_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                CustomSpinnerActivity.this.runOnUiThread(new Runnable() {
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

                    Type type = new TypeToken<List<DropdownModel>>() {
                    }.getType();

                    mData = _gson.fromJson(_result, type);

                    CustomSpinnerActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (mData.size() > 0) {
                                recyclerView.setAdapter(new customAdepter(Prefs.getString(PREF_KEY_LANGUAGE_USE, "en")));
                            } else {

                            }

                        }
                    });
                }
            }
        });

    }

    public void sendDataProblem(String value, String text) {

        Intent output = new Intent();
        output.putExtra("PROBLEM_ID", value);
        output.putExtra("PROBLEM_TEXT", text);
        setResult(KEY_FOR_PROBLEM, output);
        finish();
    }

    private class customAdepter extends RecyclerView.Adapter<ViewHolder> {

        private String currentLanguage = "en";

        public customAdepter(String currentLanguage) {
            this.currentLanguage = currentLanguage;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View _view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_spinner, parent, false);
            return new ViewHolder(_view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
            String _textProblem = mData.get(position).getProblemNameEng();
            if (currentLanguage.equals("th")) {
                _textProblem = mData.get(position).getData();
            }
            holder.textProblem.setText(_textProblem);

            final String final_textProblem = _textProblem;

            holder.textProblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendDataProblem(mData.get(position).getValue(), final_textProblem);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textProblem;

        public ViewHolder(@NonNull View v) {
            super(v);
            textProblem = v.findViewById(R.id.textProblem);

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

                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                ;
    }
}