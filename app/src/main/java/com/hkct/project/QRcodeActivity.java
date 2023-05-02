package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class QRcodeActivity extends AppCompatActivity implements View.OnClickListener{

        private final String TAG = "QRcodeActivity===>";
        private CardView btnScan;
        private String uidQR;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qrcode);

            // Navigation drawer icon always appear on the action bar
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//            setNavigationDrawer();

            Log.d(TAG, "===>QRcodeActivity!!!");

            //-g QrCode btn
            CardView btn = (CardView) findViewById(R.id.btn);
            btn.setOnClickListener(this);

            // Scan Button
            btnScan = findViewById(R.id.QRscan);
            btnScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(QRcodeActivity.this, QRscanActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            });

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                uidQR = bundle.getString("uidQR");
                Log.d("===", "bundle.getString(\\\"uidQR\\\");\" "+uidQR);
            }


        }//onCreate

    // -g QRcode by EditText -- findViewById(R.id.qrtext)
    public void getCode(){
        ImageView ivCode=(ImageView)findViewById(R.id.qrview);
        EditText etContent=(EditText)findViewById(R.id.qrtext);
        etContent.setText(uidQR);

        BarcodeEncoder encoder = new BarcodeEncoder();

        try{
            Bitmap bit = encoder.encodeBitmap(etContent.getText().toString()
                    , BarcodeFormat.QR_CODE,250,250);
            ivCode.setImageBitmap(bit);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }


//        private void setNavigationDrawer() {
//            // drawer layout instance
//            drawerLayout = findViewById(R.id.drawerLayout);
//            // Toggle the menu icon
//            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
//            actionBarDrawerToggle.syncState();
//
//            // pass the toggle for the drawer layout listener
//            drawerLayout.addDrawerListener(actionBarDrawerToggle);
//
//        } //setNavigationDrawer()

        // override the onOptionsItemSelected() function to implement
        // the item click listener callback to open and close the navigation
        // drawer when the icon is clicked
//        @Override
//        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//            Log.d(TAG, "onOptionsItemSelected->" + item.getItemId());
//
////        if (item.getItemId()==R.id.nav_account){
////            Log.d(TAG,"onOptionsItemSelected->" + "id=" + R.id.nav_account + "title=" + item.getTitle());
////            txtOutput.setText("Account clicked");
////        }
//
//            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
//                return true;
//            }
//            return super.onOptionsItemSelected(item);
//        } //onOptionsItemSelected()

    @Override
    public void onClick(View v) {
            getCode();
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
        Toast.makeText(QRcodeActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}//MembershipActivity