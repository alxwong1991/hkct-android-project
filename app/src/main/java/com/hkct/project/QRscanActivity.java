package com.hkct.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;


public class QRscanActivity extends AppCompatActivity {

    private final String TAG = "QRscanActivity===>";
    private SurfaceView surfaceView;
    private TextView textView;
    private CameraSource cameraSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);


//        // Scan Button
//        textView = findViewById(R.id.textView);
//        textView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(QRscanActivity.this, ScanOtherUserActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("uidQR", "N77hXGofqjdLT3lbPYX2bLQnyr73");
//                intent.putExtras(bundle);
//
//                startActivity(intent);
//
//                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            }
//        });



        getPermissionCamera();

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        textView = (TextView) findViewById(R.id.textView);

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.QR_CODE).build();

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>(){

            @Override
            public void release() {

            }


            @Override
            public void receiveDetections(@NonNull Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> qrCodes=detections.getDetectedItems();
                if(qrCodes.size()!=0){
                    textView.post(() -> textView.setText(qrCodes.valueAt(0).displayValue));

//                    Intent intent = new Intent(QRscanActivity.this, ScanOtherUserActivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putString("uidQR", textView.toString());
//                    intent.putExtras(bundle);
//
//                    startActivity(intent);
//
//                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(QRscanActivity.this, ScanOtherUserActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("uidQR", textView.getText().toString());
//                bundle.putString("uidQR", "N77hXGofqjdLT3lbPYX2bLQnyr73");
                intent.putExtras(bundle);

                startActivity(intent);

                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

                    Log.d(TAG, "displayValue->" + qrCodes.valueAt(0).displayValue);
                }
            }
        });

        cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .build();
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    return;
                try {
                    cameraSource.start(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

                cameraSource.stop();

            }
        });

    }


    private static final int REQUEST_CAMERA_PERMISSION = 1;

    /**
     * Get camera permissions
     */
    public void getPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            return;
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

            // Remind users why permissions are needed
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle("Camera permission required")
                    .setMessage("Camera permission is required to scan QR Code, please grant camera permission")
                    .setPositiveButton("OK", (dialog, which) -> {
                                // Show permission grant window again
                                ActivityCompat.requestPermissions(QRscanActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                            }
                    )
                    .show();
        } else {
            // Ask for permissions for the first time, or the user clicks "Don't ask again"
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    /**
     * Get the result of asking camera permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // User agrees to grant permissions
                    Toast.makeText(this, "Camera permission has been obtained", Toast.LENGTH_SHORT).show();

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(surfaceView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                } else {
                    // User denied permission
                    Toast.makeText(this, "Camera permission not obtained", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public void backClick(View v){
        startActivity(new Intent(this,MembershipActivity.class));

        int version = Integer.valueOf(android.os.Build.VERSION.SDK);
        if(version >=5){

//            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        }
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(QRscanActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }

}