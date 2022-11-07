package com.phc.prs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.pixplicity.easyprefs.library.Prefs;

import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Constants.PREF_KEY_FIRST_INSTALL_APP;

public class SplashActivity extends AppCompatActivity {

    private View decorView;
    private Animation anim;
    private ImageView imageView;
    private boolean OPEN_FIRST_APP = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        setupPref();
        setupScreen();

        OPEN_FIRST_APP = Prefs.getBoolean(PREF_KEY_FIRST_INSTALL_APP, false);

        imageView = (ImageView) findViewById(R.id.imageView);
        setAnimation();


    }

    private void setupPref() {
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(SplashActivity.this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    private void setAnimation() {
        anim = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.fade_in); // Create the animation.
        imageView.startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent i;
                if (!OPEN_FIRST_APP) {
                    i = new Intent(SplashActivity.this, SelectLanguageFirstActivity.class);
                    //set first install = true (for next use app)
                } else {
                    i = new Intent(SplashActivity.this, LoginActivity.class);
                }
                overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
                startActivity(i);
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
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