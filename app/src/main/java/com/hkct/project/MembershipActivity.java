package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.hkct.project.Adapter.PostAdapter;
import com.hkct.project.Model.Post;
import com.hkct.project.Model.Users;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.http.POST;

public class MembershipActivity extends AppCompatActivity {

    private final String TAG="MembershipActivity===>";
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;


    private TextView txtOutput, mProfileName;
    private CircleImageView mProfileImage;
    private NavigationView navView;
    private FirebaseFirestore firestore;
    private String Uid;
    private FirebaseAuth auth;
    private Uri mImageUri;
    private Query query;
    private ListenerRegistration listenerRegistration;
    private List<Post> posts;
    private List<Users> user;
    private PostAdapter adapter;

    //Membership
    private TextView plan;

    // test member
    private Button mTestBtn;
    private Button mCancelTestBtn;
    private ImageView mMembershipIcon;

    RecyclerView mRecyclerView;

    //Stripe 1
    Button button;
    String SECRET_KEY = "sk_test_51MBVL8FlCzf4JXifpD5y1j0SW5rk5d5UwFWbcCiUWc3hg5x6mxj8BrNaH05bmOKkO6iCLH7fh6AIJ8QNZyRn7Wjh00fSefZas4";
    String PUBLISH_KEY = "pk_test_51MBVL8FlCzf4JXifoVqhKPCcwCNvM32gwfs3p34f0c4ZzSG4MROMPDBfIQdQReaq6bIbBi0ZlQJt4s0d25jqlSE100CYMwm02F";
    PaymentSheet paymentSheet;

    String customerID;
    String EphericalKey;
    String ClientSecret;
    Date date;
    TextView planDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership);

        //ActivityName
        setTitle("");

        // Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        setNavigationDrawer();

        Log.d(TAG,"===>MembershipActivity!!!");

        // access to firestore
        firestore = FirebaseFirestore.getInstance();

        // access to user's uid
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();

        mProfileName = findViewById(R.id.profile_name_personal);
        mProfileImage = findViewById(R.id.profile_pic_personal);


        // test member
//        mTestBtn = findViewById(R.id.test_member_btn);
//        mCancelTestBtn = findViewById(R.id.test_cancel_member_btn);
        mMembershipIcon = findViewById(R.id.membership_icon);

        mRecyclerView = findViewById(R.id.recyclerViewPosts);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MembershipActivity.this));
//        int numberOfColumns = 3;
//        LinearLayoutManager linearLayoutManager = new GridLayoutManager(ProfileActivity.this, numberOfColumns);
//        mRecyclerView.setLayoutManager(linearLayoutManager);

        posts = new ArrayList<>();
        user = new ArrayList<>();
        adapter = new PostAdapter(MembershipActivity.this, posts, user);
        mRecyclerView.setAdapter(adapter);


        //planDate
        Calendar rightNow = Calendar.getInstance();

        //add planDate
        rightNow.add(Calendar.DAY_OF_YEAR, 30);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);

        Date date = rightNow.getTime();

        String format = sdf.format(date);


//        // get membership
//        mTestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // update firestore user's field
//                firestore.collection("Users").document(Uid).update("membership", "1").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MembershipActivity.this, "Subscribe!", Toast.LENGTH_SHORT).show();
//
//
//
//                            refreshPage();
//                        } else {
//                            Toast.makeText(MembershipActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });

//         cancel membership
//        mCancelTestBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                firestore.collection("Users").document(Uid).update("membership", "0").addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            Toast.makeText(MembershipActivity.this, "Unsubscribe!", Toast.LENGTH_SHORT).show();
//                            refreshPage();
//                        } else {
//                            Toast.makeText(MembershipActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//
        firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        String name = task.getResult().getString("name");
                        String imageUrl = task.getResult().getString("image");

                        // get membership status of user
                        String memberShip = task.getResult().getString("membership");
                        mProfileName.setText(name);
                        mImageUri = Uri.parse(imageUrl);

                        // if membership is 1 then icon appear otherwise the icon is invisible
                        if (memberShip.equals("1")) {
                            mMembershipIcon.setVisibility(View.VISIBLE);
                            plan=findViewById(R.id.plan);
                            plan.setText("Premium");
                            plan.setTextColor(Color.parseColor("#FFE384"));
                            plan.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50);

//                            TextView title_plan=findViewById(R.id.title_plan);
//                            title_plan.setText("transaction number ： " + customerID);


                            TextView monthplan=findViewById(R.id.monthplan);
                            monthplan.setText("Expiry Date");
                            monthplan.setTextColor(Color.parseColor("#E3170D"));

                            planDate=findViewById(R.id.planDate);
                            planDate.setText(format);

                            Button btnPay =findViewById(R.id.btn);
                            btnPay.setVisibility(View.GONE);


                        }

                        Glide.with(MembershipActivity.this).load(imageUrl).into(mProfileImage);
                    }
                }
            }
        });
        Log.d(TAG,"===>profileActivity!!!");
        setNavigationDrawer();


        //Stripe 2
        button = findViewById(R.id.btn);

        PaymentConfiguration.init(this, PUBLISH_KEY);

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentFlow();
            }
        });

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            customerID = object.getString("id");
//                            Toast.makeText(MembershipActivity.this, customerID, Toast.LENGTH_SHORT).show();

                            // transaction number
                            Log.d("debug || customerID  : ", "result :" + customerID );

                            firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().exists()) {
                                            // get membership status of user
                                            String memberShip = task.getResult().getString("membership");

                                            // if membership is 1 then icon appear otherwise the icon is invisible
                                            if (memberShip.equals("1")) {
                                                TextView title_plan=findViewById(R.id.title_plan);
                                                title_plan.setText("Transaction Number " + customerID);
                                                title_plan.setTextColor(Color.parseColor("#FFFAFA"));

                                            }

                                        }
                                    }
                                }
                            });




                            getEphericalKey(customerID);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                //             "Bearer "  -- Must be spacebar in Bearer at the  end
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MembershipActivity.this);
        requestQueue.add(stringRequest);

    }//onCreate

    // test membership
    private void refreshPage() {
        Intent intent = new Intent(MembershipActivity.this, MembershipActivity.class);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(intent);
    }


    //payment success
    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {
        if(paymentSheetResult instanceof PaymentSheetResult.Completed){
            Toast.makeText(this, "payment success", Toast.LENGTH_SHORT).show();

            firestore.collection("Users").document(Uid).update("membership", "1").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(MembershipActivity.this, "Subscribe!", Toast.LENGTH_SHORT).show();


                        refreshPage();
                    } else {
                        Toast.makeText(MembershipActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void getEphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            EphericalKey = object.getString("id");
//                            Toast.makeText(MembershipActivity.this, EphericalKey, Toast.LENGTH_SHORT).show();
                            Log.d("debug || EphericalKey : ", "result :" + EphericalKey);

                            getClientSecret(customerID, EphericalKey);

                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
//             "Bearer "  -- Must be spacebar in Bearer at the  end
                header.put("Authorization", "Bearer " + SECRET_KEY);
                header.put("Stripe-Version", "2022-11-15");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MembershipActivity.this);
        requestQueue.add(stringRequest);
    }

    private void getClientSecret(String customerID, String ephericalKey) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);
                            ClientSecret = object.getString("client_secret");
//                            Toast.makeText(MembershipActivity.this, ClientSecret, Toast.LENGTH_SHORT).show();

                            Log.d("debug || ClientSecret  : ", "result :" + ClientSecret );


                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                //             "Bearer "  -- Must be spacebar in Bearer at the  end
                header.put("Authorization", "Bearer " + SECRET_KEY);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", "299"+"00");
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(MembershipActivity.this);
        requestQueue.add(stringRequest);

    }

    private void PaymentFlow() {

        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration("ABC Company"
                , new PaymentSheet.CustomerConfiguration(
                        customerID,
                        EphericalKey
                ))
        );


    }

    private void setNavigationDrawer(){
        // drawer layout instance
        drawerLayout = findViewById(R.id.drawerLayout);
        // Toggle the menu icon
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.nav_open,R.string.nav_close);
        actionBarDrawerToggle.syncState();

        // pass the toggle for the drawer layout listener
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

    } //setNavigationDrawer()

    // override the onOptionsItemSelected() function to implement
    // the item click listener callback to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Log.d(TAG,"onOptionsItemSelected->" + item.getItemId());

//        if (item.getItemId()==R.id.nav_account){
//            Log.d(TAG,"onOptionsItemSelected->" + "id=" + R.id.nav_account + "title=" + item.getTitle());
//            txtOutput.setText("Account clicked");
//        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    } //onOptionsItemSelected()

    // Menu
    //   Discover
    public void menu1_click(MenuItem menuItem) {
        Log.d(TAG,"menu1_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        startActivity(new Intent(this, DiscoverActivity.class));
        drawerLayout.closeDrawers();
    }
    //   Profile
    public void menu2_click(MenuItem menuItem) {
        Log.d(TAG,"menu2_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, ProfileActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Events
    public void menu3_click(MenuItem menuItem) {
        Log.d(TAG,"menu3_click()->" + menuItem.getItemId() + ","+ menuItem.getTitle());
        startActivity(new Intent(this, EventActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //    MembershipActivity
    public void menu4_click(MenuItem menuItem) {
        Log.d(TAG, "menu4_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, MembershipActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    //   Store
    public void menu5_click(MenuItem menuItem) {
        Log.d(TAG, "menu5_click()->" + menuItem.getItemId() + "," + menuItem.getTitle());
        startActivity(new Intent(this, StoreActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        getMenuInflater().inflate(R.menu.notifications, menu);
        getMenuInflater().inflate(R.menu.logout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void menu_profile_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),SetUpActivity.class));
    }

    public void menu_notification_click(MenuItem m) {
        startActivity(new Intent(MembershipActivity.this, NotificationActivity.class));
    }

    public void menu_logout_click(MenuItem m) {
        startActivity(new Intent(getApplicationContext(),LoginActivity.class));
        FirebaseAuth.getInstance().signOut();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        Toast.makeText(MembershipActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }

    // myQRcode  btn
    public void myQRcode(View v){

        Intent intent = new Intent(MembershipActivity.this, QRcodeActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("uidQR", Uid);
        intent.putExtras(bundle);

        startActivity(intent);


//        startActivity(new Intent(MembershipActivity.this, QRcodeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

}//MembershipActivity