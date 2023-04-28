package com.hkct.project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Adapter.LikeProductAdapter;
import com.hkct.project.Model.LikeProduct;
import com.hkct.project.Model.Users;

import java.util.ArrayList;
import java.util.List;

public class ProductLikesActivity extends AppCompatActivity {

    private FirebaseFirestore firestore;
    private RecyclerView mProductLikesRecyclerView;
    private String product_id;
    private List<LikeProduct> likeProductList;
    private List<Users> usersList;
    private LikeProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_likes);

        firestore = FirebaseFirestore.getInstance();
        mProductLikesRecyclerView = findViewById(R.id.product_like_recyclerView);

        likeProductList = new ArrayList<>();
        usersList = new ArrayList<>();
        adapter = new LikeProductAdapter(ProductLikesActivity.this, usersList, likeProductList);

        product_id = getIntent().getStringExtra("productId");
        mProductLikesRecyclerView.setHasFixedSize(true);
        mProductLikesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mProductLikesRecyclerView.setAdapter(adapter);

        firestore.collection("Products/" + product_id + "/Likes").addSnapshotListener(ProductLikesActivity.this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    System.err.println("Listen failed: " + error);
                } else {
                    for (DocumentChange documentChange : value.getDocumentChanges()) {
                        if (documentChange.getType() == DocumentChange.Type.ADDED) {
                            LikeProduct likeProduct = documentChange.getDocument().toObject(LikeProduct.class);
                            String userId = documentChange.getDocument().getString("user");

                            firestore.collection("Users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Users users = task.getResult().toObject(Users.class);
                                        usersList.add(users);
                                        likeProductList.add(likeProduct);
                                        adapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(ProductLikesActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }
}