package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
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
        private DrawerLayout drawerLayout;
        private ActionBarDrawerToggle actionBarDrawerToggle;

        private Button btnScan;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_qrcode);

            //ActivityName
            setTitle("");

            // Navigation drawer icon always appear on the action bar
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            setNavigationDrawer();

            Log.d(TAG, "===>QRcodeActivity!!!");

            Button btn = (Button) findViewById(R.id.btn);
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


        }//onCreate

    public void getCode(){
        ImageView ivCode=(ImageView)findViewById(R.id.qrview);
        EditText etContent=(EditText)findViewById(R.id.qrtext);

        BarcodeEncoder encoder = new BarcodeEncoder();

        try{
            Bitmap bit = encoder.encodeBitmap(etContent.getText().toString()
                    , BarcodeFormat.QR_CODE,250,250);
            ivCode.setImageBitmap(bit);
        }catch (WriterException e){
            e.printStackTrace();
        }
    }


        private void setNavigationDrawer() {
            // drawer layout instance
            drawerLayout = findViewById(R.id.drawerLayout);
            // Toggle the menu icon
            actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
            actionBarDrawerToggle.syncState();

            // pass the toggle for the drawer layout listener
            drawerLayout.addDrawerListener(actionBarDrawerToggle);

        } //setNavigationDrawer()

        // override the onOptionsItemSelected() function to implement
        // the item click listener callback to open and close the navigation
        // drawer when the icon is clicked
        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            Log.d(TAG, "onOptionsItemSelected->" + item.getItemId());

//        if (item.getItemId()==R.id.nav_account){
//            Log.d(TAG,"onOptionsItemSelected->" + "id=" + R.id.nav_account + "title=" + item.getTitle());
//            txtOutput.setText("Account clicked");
//        }

            if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
                return true;
            }
            return super.onOptionsItemSelected(item);
        } //onOptionsItemSelected()

        public void menu1_click(MenuItem m) {
            Log.d(TAG, "menu1_click()->" + m.getItemId() + "," + m.getTitle());
            startActivity(new Intent(this, DiscoverActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

        public void menu2_click(MenuItem m) {
            Log.d(TAG, "menu2_click()->" + m.getItemId() + "," + m.getTitle());
//        txtOutput.setText(R.string.msg2);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

        public void menu3_click(MenuItem m) {
            Log.d(TAG, "menu3_click()->" + m.getItemId() + "," + m.getTitle());
//        txtOutput.setText(R.string.msg3);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

        public void menu5_click(MenuItem menuItem) {
            Log.d(TAG, "menu5_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
            startActivity(new Intent(this, EventsActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

        //    MembershipActivity
        public void menu6_click(MenuItem menuItem) {
            Log.d(TAG, "menu6_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
            startActivity(new Intent(this, MembershipActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

        //   QR code
        public void menu7_click(MenuItem menuItem) {
            Log.d(TAG, "menu7_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
            startActivity(new Intent(this, QRcodeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            drawerLayout.closeDrawers();
        }

    @Override
    public void onClick(View v) {
        getCode();
    }
}//MembershipActivity