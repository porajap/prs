package com.phc.prs.Extensions;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.DropdownModel;
import com.phc.prs.Models.RegisterDropdownModel;
import com.phc.prs.R;
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

import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.GET_DROPDOWN_REGISTER_URL;


public class CustomSpinnerSearchActivity extends Activity {

    private RecyclerView recyclerView;
    private ArrayList<RegisterDropdownModel> mData;
    private Button buttonCancel;
    private View decorView;
    private TextView textTitle;
    private String TEXT_REQUEST = "department";
    private TextView searchText;

    private ProgressBar progressBar;
    private TextView dataNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_spinner_search);

        bindIntent();
        bindWidget();
        bindEvents();
        setupScreen();
        feedData(TEXT_REQUEST, "");
    }

    private void bindIntent() {
        Intent intent = getIntent();
        TEXT_REQUEST = intent.getStringExtra("request");
    }

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

        if (_currentLanguage.equals("th")) {
            textTitle.setText("กรุณาเลือก");
            buttonCancel.setText("ยกเลิก");
        } else {
            textTitle.setText("Please select");
            buttonCancel.setText("cancel");
        }

        if(TEXT_REQUEST.equals("permission")){
            searchText.setVisibility(View.GONE);
        }else{
            searchText.setVisibility(View.VISIBLE);
        }
    }

    private void bindEvents() {
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                feedData(TEXT_REQUEST, s.toString().trim());
            }
        });
    }


    private void bindWidget() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        textTitle = (TextView) findViewById(R.id.textTitle);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);

        searchText = (TextView) findViewById(R.id.searchText);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        dataNotFound = (TextView) findViewById(R.id.data_not_found);
    }

    private void feedData(String textRequest, String textSearch) {
        OkHttpClient _client = new OkHttpClient();
        RequestBody _requestBody = new FormBody.Builder()
                .add("textRequest", textRequest)
                .add("textSearch", textSearch)
                .add("languageUse", Prefs.getString(PREF_KEY_LANGUAGE_USE, "en"))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DROPDOWN_REGISTER_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                CustomSpinnerSearchActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        dataNotFound.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<RegisterDropdownModel>>() {
                    }.getType();

                    mData = _gson.fromJson(_result, type);

                    CustomSpinnerSearchActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setVisibility(View.GONE);

                            if (mData.size() > 0) {
                                dataNotFound.setVisibility(View.INVISIBLE);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(new customAdepter(Prefs.getString(PREF_KEY_LANGUAGE_USE, "en")));
                            }else{
                                recyclerView.setVisibility(View.INVISIBLE);
                                dataNotFound.setVisibility(View.VISIBLE);
                            }
                        }
                    });
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
            String _textSelect = mData.get(position).getOptionTextEng();
            if (currentLanguage.equals("th")) {
                _textSelect = mData.get(position).getOptionTextTh();
            }
            holder.textProblem.setText(_textSelect);

            final String final_textSelect = _textSelect;

            holder.textProblem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendData(mData.get(position).getOptionId(), final_textSelect);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mData.size();
        }
    }

    public void sendData(String value, String text) {

        Intent output = new Intent();
        output.putExtra("SELECT_ID", value);
        output.putExtra("SELECT_TEXT", text);
        int KEY_FOR_VALUE = 1001;

        if(TEXT_REQUEST.equals("customer")){
            KEY_FOR_VALUE = 1002;
        }

        setResult(KEY_FOR_VALUE, output);
        finish();
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textProblem;

        public ViewHolder(@NonNull View v) {
            super(v);
            textProblem = v.findViewById(R.id.textProblem);

        }
    }
}