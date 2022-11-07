package com.phc.prs;

import androidx.annotation.Nullable;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.phc.prs.Extensions.CustomSpinnerActivity;

import com.pixplicity.easyprefs.library.Prefs;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Constants.CUS_LOGIN;
import static com.phc.prs.Constants.Url.SEND_REQUEST_PROBLEM_URL;

public class MaintenanceActivity extends LocalizationActivity {

    private View decorView;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private ScrollView scrollview;
    private EditText editTextIdMachine;

    private TextView note;
    private Button btnSend;

    private String QRCODE;

    private TextView buildingName, floor, room;
    private TextView problemSelect;
    private String problemResultId = "";
    private String problemResultText = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        bindWidgets();
    }

    @Override
    public void onResume() {
        super.onResume();
        setupScreen();
        bindEvents();
        bindIntent();
    }

    private void bindIntent() {
        Intent intent = getIntent();
        editTextIdMachine.setText(intent.getStringExtra("DispenserID"));
        QRCODE = intent.getStringExtra("qrCode");

        buildingName.setText(intent.getStringExtra("BuildingName"));
        floor.setText(intent.getStringExtra("BuildingFloor"));
        room.setText(intent.getStringExtra("RoomName"));
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSend.setEnabled(false);
                sendData();
            }
        });


        problemSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCustomProblemActivity();
            }
        });


        note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogInputNote();
            }
        });
    }

    private void sendData() {
        String _note = note.getText().toString().trim();
        if (problemResultText.equals("อื่นๆ") || problemResultId.equals("5")) {
            if (_note.equals("")) {
                dialogPleaseEnterNote(getString(R.string.please_enter_note));
                btnSend.setEnabled(true);
                return;
            }
        } else if (problemResultText.equals("")) {
            dialogPleaseSelectProblem(getString(R.string.maintenance_please_select_problem));
            btnSend.setEnabled(true);
            return;
        }
        sendDataRequest();
    }

    private void bindWidgets() {
        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        scrollview = (ScrollView) findViewById(R.id.scrollview);

        editTextIdMachine = (EditText) findViewById(R.id.id_machine);

        problemSelect = (TextView) findViewById(R.id.problemSelect);

        note = (TextView) findViewById(R.id.note);
        btnSend = (Button) findViewById(R.id.btn_send);

        buildingName = (TextView) findViewById(R.id.buildingName);
        floor = (TextView) findViewById(R.id.floor);
        room = (TextView) findViewById(R.id.room);
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

        titleMenu.setText(R.string.menu_maintenance);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnRightTopBar.setImageResource(R.drawable.ic_history);

        scrollview.setSmoothScrollingEnabled(true);
        scrollview.setVerticalScrollBarEnabled(false);
        scrollview.setHorizontalScrollBarEnabled(false);
    }

    @Override //result from custom spinner problem
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1001) {
            problemResultId = data.getStringExtra("PROBLEM_ID");
            problemResultText = data.getStringExtra("PROBLEM_TEXT");
            problemSelect.setText(problemResultText);

            if (problemResultText.equals("อื่นๆ") || problemResultId.equals("5")) {
                if(note.getText().toString().trim().equals("")){
                    showDialogInputNote();
                }
                btnSend.setEnabled(true);
                return;
            }
        }

    }

    private void sendDataRequest() {
        String _cus_login = Prefs.getString(CUS_LOGIN, "");
        String _problem_code = problemResultId;
        String _note = note.getText().toString().trim();

        OkHttpClient _client = new OkHttpClient();

        RequestBody _requestBody = new FormBody.Builder()
                .add("p_Repair_Problem_Code", _problem_code)
                .add("p_Repair_Note", _note)
                .add("p_Cus_Login", _cus_login)
                .add("qrCode", QRCODE)
                .build();

        Request _request = new Request.Builder().url(BASE_URL + SEND_REQUEST_PROBLEM_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                MaintenanceActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnSend.setEnabled(true);
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String _result = response.body().string();
                if (response.isSuccessful()) {
                    MaintenanceActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            btnSend.setEnabled(true);
                            if (_result.equals("success")) {
                                note.setText("");
                                dialogShow(getString(R.string.maintenance_send_success), "success");
                            } else {
                                dialogShow(getString(R.string.maintenance_send_failed), "failed");
                            }
                        }
                    });
                }
            }
        });
    }

    private void dialogShow(String detail, final String status) {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(detail);
        //Now we need an AlertDialog.Builder object
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //close dialog when click OK
        Button btn_click = (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status.equals("success")) {
                    Intent i = new Intent(getApplication(), CameraBarcodeActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_to_left);
                }
                alertDialog.dismiss();
            }
        });
    }

    private void dialogPleaseSelectProblem(String detail) {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        //then we will inflate the custom alert dialog xml that we created
        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(detail);
        //Now we need an AlertDialog.Builder object
        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        //setting the view of the builder to our custom view that we already inflated
        builder.setView(dialogView);
        //finally creating the alert dialog and displaying it
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //close dialog when click OK
        Button btn_click = (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCustomProblemActivity();
                alertDialog.dismiss();
            }
        });
    }

    private void openCustomProblemActivity() {
        int KEY_FOR_PROBLEM = 1001;
        Intent i = new Intent(MaintenanceActivity.this, CustomSpinnerActivity.class);
        startActivityForResult(i, KEY_FOR_PROBLEM);
    }

    private void showDialogInputNote() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        final View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_note, viewGroup, false);

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this, R.style.AlertDialogStyle);
        builder.setView(dialogView);

        final android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final EditText _message = dialogView.findViewById(R.id.messageNote);
        //if have note in edit text give show in dialog for edit again
        _message.setText(note.getText().toString().trim());

        Button btnConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                note.setText(_message.getText().toString().trim());
                alertDialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    private void dialogPleaseEnterNote(String detail) {

        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText(detail);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);

        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        //close dialog when click OK
        Button btn_click = (Button) dialogView.findViewById(R.id.btn_click);
        btn_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogInputNote();
                alertDialog.dismiss();
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