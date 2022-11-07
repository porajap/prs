package com.phc.prs;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.localizationactivity.ui.LocalizationActivity;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phc.prs.Models.MaintenanceBarcodeModel;
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

import static com.phc.prs.Constants.Constants.CUS_CODE;
import static com.phc.prs.Constants.Url.BASE_URL;
import static com.phc.prs.Constants.Url.GET_DATA_BARCODE_URL;


public class CameraBarcodeActivity extends LocalizationActivity {


    SurfaceView surfaceView;
    private View decorView;
    private TextView txtBarcodeValue;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private int REQUEST_CAMERA_PERMISSION = 201;
    private String intentData;
    private String QR_CODE_FOR_TEN_CHARACTER;
    private String QR_CODE_FOR_EIGHT_CHARACTER;
    private boolean IsOpen = true;
    private ArrayList<MaintenanceBarcodeModel> barcodeData;
    private ImageView btnRightTopBar;
    private ImageView btnBack;
    private TextView titleMenu;
    private ImageView areaColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_bacode);
        bindWidgets();
        setupScreen();
        bindEvents();
    }

    private void bindEvents() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

        titleMenu.setText(R.string.barcode_scan_barcode);
        btnRightTopBar.setVisibility(View.INVISIBLE);
        btnBack.setVisibility(View.VISIBLE);
        btnRightTopBar.setImageResource(R.drawable.ic_history);
    }

    private void openCamera() {
        barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setAutoFocusEnabled(true) //you should add this feature
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(CameraBarcodeActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        ActivityCompat.requestPermissions(CameraBarcodeActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
                Toast.makeText(getApplicationContext(), "To prevent memory leaks barcode scanner has been stopped", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes.size() != 0) {

                    txtBarcodeValue.post(new Runnable() {

                        @Override
                        public void run() {
                            intentData = barcodes.valueAt(0).displayValue;

                            System.out.println("intentData = " + intentData);

                            String _contents = intentData;

                            if (barcodes.valueAt(0).displayValue != null) {
                                try {
                                    QR_CODE_FOR_TEN_CHARACTER = _contents.substring(_contents.length() - 10, _contents.length());
                                    QR_CODE_FOR_EIGHT_CHARACTER = _contents.substring(_contents.length() - 8, _contents.length());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return;
                                }

                                if (IsOpen) {
                                    try {
                                        IsOpen = false;
                                        getDataBarCode();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                }
                            }

                        }
                    });

                }
            }
        });
    }

    private void bindWidgets() {
        surfaceView = findViewById(R.id.surfaceView);
        txtBarcodeValue = findViewById(R.id.txtBarcodeValue);

        btnRightTopBar = (ImageView) findViewById(R.id.btn_right_top);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        titleMenu = (TextView) findViewById(R.id.textview_title_menu);

        areaColor = (ImageView) findViewById(R.id.area_color);
    }

    public void onResume() {
        super.onResume();
        openCamera();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void getDataBarCode() {
        getDataBarcode();
    }


    private void getDataBarcode() {
        OkHttpClient _client = new OkHttpClient();
        RequestBody _requestBody = new FormBody.Builder()
                .add("qrCodeTenChar", QR_CODE_FOR_TEN_CHARACTER)
                .add("qrCodeEightChar", QR_CODE_FOR_EIGHT_CHARACTER)
                .add("cusCode", Prefs.getString(CUS_CODE, ""))
                .build();

        Request _request = new Request.Builder().url(BASE_URL + GET_DATA_BARCODE_URL).post(_requestBody).build();

        _client.newCall(_request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                CameraBarcodeActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        IsOpen = true;
                        Toast.makeText(getApplicationContext(), R.string.check_your_network, Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String _result = response.body().string();

                    Gson _gson = new Gson();

                    Type type = new TypeToken<List<MaintenanceBarcodeModel>>() {
                    }.getType();

                    barcodeData = _gson.fromJson(_result, type);

                    CameraBarcodeActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!barcodeData.isEmpty()) {
                                areaColor.setImageResource(R.drawable.border_camera_barode_green);
                                openMaintenance();
                            } else {
                                areaColor.setImageResource(R.drawable.border_camera_barode_red);
                                showDialog();
                            }
                        }
                    });
                }
            }
        });

    }

    private void showDialog() {
        ViewGroup viewGroup = findViewById(android.R.id.content);

        View dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_show, viewGroup, false);
        TextView _text = (TextView) dialogView.findViewById(R.id.textview_message);
        _text.setText("ไม่พบข้อมูลในระบบ กรุณาลองใหม่");

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
                IsOpen = true;
                areaColor.setImageResource(R.drawable.border_camera_barcode_white);
                alertDialog.dismiss();
            }
        });
    }

    private void openMaintenance() {
        int _qrCodeLength = barcodeData.get(0).getQrCode().length();
        String _qrCode = QR_CODE_FOR_TEN_CHARACTER;
        if(_qrCodeLength == 8){
            _qrCode = QR_CODE_FOR_EIGHT_CHARACTER;
        }

        Intent i = new Intent(CameraBarcodeActivity.this, MaintenanceActivity.class);
        i.putExtra("qrCode", _qrCode);
        i.putExtra("DispenserID", barcodeData.get(0).getDispenserID());
        i.putExtra("id_machine", barcodeData.get(0).getID());

        i.putExtra("BuildingName", barcodeData.get(0).getBuildingName());
        i.putExtra("BuildingFloor", barcodeData.get(0).getBuildingFloor());
        i.putExtra("RoomName", barcodeData.get(0).getRoomName());
        startActivity(i);
        finish();
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