package com.phc.prs;

import androidx.appcompat.widget.LinearLayoutCompat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.pixplicity.easyprefs.library.Prefs;

import static com.phc.prs.Constants.Constants.PREF_KEY_LANGUAGE_USE;

public class ChangeLanguageActivity extends LocalizationActivity {

    private View decorView;
    private ScrollView scrollview;
    private TextView titleMenu;
    private ImageView btnBack;
    private ImageView btnRightTopBar;
    private LinearLayoutCompat btnEnglish, btnThai;
    private ImageView icCheckEng, icCheckTh;
    private TextView textEng, textThai;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_language);

        bindWidgets();
        setupScreen();
        bindEvents();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        scrollview = (ScrollView) findViewById(R.id.scrollview);

        btnEnglish = (LinearLayoutCompat) findViewById(R.id.btnEnglish);
        btnThai = (LinearLayoutCompat) findViewById(R.id.btnThai);

        icCheckEng = (ImageView) findViewById(R.id.icCheckEng);
        icCheckTh = (ImageView) findViewById(R.id.icCheckTh);

        textEng = (TextView) findViewById(R.id.textEng);
        textThai = (TextView) findViewById(R.id.textThai);
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnEnglish.setEnabled(false);
                checkCurrentLanguage("en");
            }
        });

        btnThai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnThai.setEnabled(false);
                checkCurrentLanguage("th");
            }
        });
    }

    private void checkCurrentLanguage(String language) {
        String currentLanguage = getCurrentLanguage().toString();
        if (!currentLanguage.equals(language)) {
            askConfirmToChangeLanguage(language);
        }
    }

    private void askConfirmToChangeLanguage(final String language) {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_choice, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(R.string.you_sure_change_language);

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
                isCallSetLanguage(language);
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnEnglish.setEnabled(true);
                btnThai.setEnabled(true);

                alertDialog.dismiss();
            }
        });

    }

    private void isCallSetLanguage(String language) {
        if (language.equals("en")) {
            setLanguage("en");
        } else {
            setLanguage("th");
        }
        Prefs.putString(PREF_KEY_LANGUAGE_USE, language);
        btnEnglish.setEnabled(true);
        btnThai.setEnabled(true);
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

        titleMenu.setText(R.string.change_language);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);

        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);

        String currentLanguage = getCurrentLanguage().toString();
        if(currentLanguage.equals("en")){
            btnEnglish.setBackgroundResource(R.drawable.blue_background);
            btnThai.setBackgroundResource(R.drawable.white_blue_border_background);

            icCheckEng.setVisibility(View.VISIBLE);
            icCheckTh.setVisibility(View.INVISIBLE);

            textEng.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_white));
            textThai.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_black));
        }else{
            btnEnglish.setBackgroundResource(R.drawable.white_blue_border_background);
            btnThai.setBackgroundResource(R.drawable.blue_background);

            icCheckEng.setVisibility(View.INVISIBLE);
            icCheckTh.setVisibility(View.VISIBLE);

            textEng.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_black));
            textThai.setTextColor(getResources().getColorStateList(R.color.text_language_state_color_white));
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