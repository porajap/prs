package com.phc.prs;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.pixplicity.easyprefs.library.Prefs;

import static com.phc.prs.Constants.Constants.PREF_KEY_FIRST_INSTALL_APP;
import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;

public class SelectLanguageFirstActivity extends LocalizationActivity {

    private View decorView;
    private ScrollView scrollview;

    private LinearLayoutCompat btnEnglish, btnThai;
    private ImageView icCheckEng, icCheckTh;
    private TextView textEng, textThai;
    private Button btnNext;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_language_first);

        String _currentLanguage = getCurrentLanguage().toString();
        setLanguage(_currentLanguage);
        bindWidgets();
        setupScreen();
        setBackground(_currentLanguage);
        bindEvents();
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

    }

    private void bindWidgets() {

        scrollview = (ScrollView) findViewById(R.id.scrollview);

        btnEnglish = (LinearLayoutCompat) findViewById(R.id.btnEnglish);
        btnThai = (LinearLayoutCompat) findViewById(R.id.btnThai);

        icCheckEng = (ImageView) findViewById(R.id.icCheckEng);
        icCheckTh = (ImageView) findViewById(R.id.icCheckTh);

        textEng = (TextView) findViewById(R.id.textEng);
        textThai = (TextView) findViewById(R.id.textThai);

        btnNext = (Button) findViewById(R.id.btnNext);
    }


    private void bindEvents() {

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String _currentLanguage = getCurrentLanguage().toString();
                setLanguage(_currentLanguage);
                Prefs.putBoolean(PREF_KEY_FIRST_INSTALL_APP, true);
                Prefs.putString(PREF_KEY_LANGUAGE_USE, _currentLanguage);

                Intent i = new Intent(getApplication(), LoginActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCallSetLanguage("en");
            }
        });

        btnThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCallSetLanguage("th");
            }
        });
    }

    private void setBackground(String language) {
        if (language.equals("en")) {
            btnEnglish.setBackgroundResource(R.drawable.blue_background);
            btnThai.setBackgroundResource(R.drawable.white_blue_border_background);

            icCheckEng.setVisibility(View.VISIBLE);
            icCheckTh.setVisibility(View.INVISIBLE);

            textEng.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_white));
            textThai.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_black));
        } else {
            btnEnglish.setBackgroundResource(R.drawable.white_blue_border_background);
            btnThai.setBackgroundResource(R.drawable.blue_background);

            icCheckEng.setVisibility(View.INVISIBLE);
            icCheckTh.setVisibility(View.VISIBLE);

            textEng.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_black));
            textThai.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_white));
        }
    }

    private void isCallSetLanguage(String language) {
        if (language.equals("en")) {
            setLanguage("en");
        } else {
            setLanguage("th");
        }

    }

    @Override
    public void onAfterLocaleChanged() {
        super.onAfterLocaleChanged();

        String _currentLanguage = getCurrentLanguage().toString();
        setBackground(_currentLanguage);
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