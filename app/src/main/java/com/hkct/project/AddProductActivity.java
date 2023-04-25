package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

public class AddProductActivity extends AppCompatActivity {

    private Button mAddProductBtn;
    private EditText mProductName, mProductPrice, mProductDetail;
    private ImageView mProductImage;
    private ProgressBar mProgressBar;
    private Uri productImageUri = null;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        mProductImage = findViewById(R.id.product_image);
        mProductName = findViewById(R.id.product_name);
        mProductPrice = findViewById(R.id.product_price);
        mProductDetail = findViewById(R.id.product_details);
        mAddProductBtn = findViewById(R.id.btn_product_post);

        mProgressBar = findViewById(R.id.product_pBar);
        mProgressBar.setVisibility(View.INVISIBLE);

        storageReference = FirebaseStorage.getInstance().getReference();
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUserId = auth.getCurrentUser().getUid();

        mProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(3,2)
                        .setMinCropResultSize(512, 512)
                        .start(AddProductActivity.this);
            }
        });

        mAddProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgressBar.setVisibility(View.VISIBLE);
                String name = mProductName.getText().toString();
                String price = mProductPrice.getText().toString();
                String detail = mProductDetail.getText().toString();

                if (!detail.isEmpty() && productImageUri != null) {
                    StorageReference productRef = storageReference
                            .child("product_images")
                            .child(FieldValue.serverTimestamp().toString() + ".jpg");

                    productRef.putFile(productImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                productRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        HashMap<String, Object> postProductMap = new HashMap<>();
                                        postProductMap.put("image", uri.toString());
                                        postProductMap.put("user", currentUserId);
                                        postProductMap.put("name", name);
                                        postProductMap.put("price", price);
                                        postProductMap.put("detail", detail);
                                        postProductMap.put("time", FieldValue.serverTimestamp());

                                        firestore.collection("Products").add(postProductMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddProductActivity.this, "Product Added Successfully!!", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(AddProductActivity.this, StoreActivity.class));
                                                } else {
                                                    mProgressBar.setVisibility(View.INVISIBLE);
                                                    Toast.makeText(AddProductActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            } else {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(AddProductActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(AddProductActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                productImageUri = result.getUri();
                mProductImage.setImageURI(productImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, result.getError().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}