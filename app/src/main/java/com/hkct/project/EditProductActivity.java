package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

public class EditProductActivity extends AppCompatActivity {

    private String TAG = "EditProductActivity===>";
    private Button mEditProductBtn;
    private EditText mEditProductName, mEditProductPrice, mEditProductDetail;
    private ImageView mEditProductImage;
    private ProgressBar mProgressBar;
    private FirebaseFirestore firestore;
    private String mEditProductImageUrl;
    private Uri mProductImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        String productId = getIntent().getStringExtra("productId");
        mEditProductImageUrl = getIntent().getStringExtra("image");
        mEditProductImage = findViewById(R.id.edit_product_image);

        mEditProductBtn = findViewById(R.id.btn_edit_product_post);

        mEditProductName = findViewById(R.id.edit_product_name);
        mEditProductPrice = findViewById(R.id.edit_product_price);
        mEditProductDetail = findViewById(R.id.edit_product_details);

        firestore = FirebaseFirestore.getInstance();

        firestore.collection("Products").document(productId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mEditProductImageUrl = documentSnapshot.getString("image");
                    mEditProductName.setText(documentSnapshot.getString("name"));
                    mEditProductPrice.setText(documentSnapshot.getString("price"));
                    mEditProductDetail.setText(documentSnapshot.getString("detail"));

                    Glide.with(EditProductActivity.this).load(mEditProductImageUrl).into(mEditProductImage);
                }
            }
        });

        mProgressBar = findViewById(R.id.product_pBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        mEditProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(view.VISIBLE);
                String name = mEditProductName.getText().toString();
                String price = mEditProductPrice.getText().toString();
                String detail = mEditProductDetail.getText().toString();

                DocumentReference updateProduct = firestore.collection("Products").document(productId);
                updateProduct.update(
                        "name", name,
                        "price", price,
                        "detail", detail,
                        "time", FieldValue.serverTimestamp()
                ).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditProductActivity.this, "Product Updated Successfully!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(EditProductActivity.this, StoreActivity.class));
                        } else {
                            mProgressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(EditProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        Log.d(TAG,"===>editProductActivity!!!");
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
        Toast.makeText(EditProductActivity.this, "Logout successful", Toast.LENGTH_SHORT).show();
    }
}