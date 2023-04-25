package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hkct.project.Model.Product;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private Activity context;
    private List<Product> productList;
    private List<Users> usersList;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private String Uid;

    public ProductAdapter(Activity context, List<Product> productList, List<Users> usersList) {
        this.productList = productList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_products, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.setProductName(product.getName());
//        holder.setProductName(product.name);
        holder.setProductDetail(product.getDetail());
//        holder.setProductDetail(product.detail);
        holder.setProductPrice(product.getPrice());
//        holder.setProductPrice(product.price);
        holder.setProductPic(product.getImage());
//        holder.setProductPic(product.image);

        long milliseconds = product.getTime().getTime();
//        long milliseconds = product.time.getTime();
        String date = DateFormat.getDateInstance().format(new Date(milliseconds));
        holder.setProductDate(date);

        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();
        holder.setProductUsername(username);
        holder.setProfilePic(image);

        String productId = product.ProductId;
        String currentUserId = auth.getCurrentUser().getUid();

        // like btn
        holder.likeProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Products/" + productId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            likesMap.put("user", currentUserId);
                            firestore.collection("Products/" + productId + "/Likes").document(currentUserId).set(likesMap);
                        } else {
                            firestore.collection("Products/" + productId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        // like btn color change
        firestore.collection("Products/" + productId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.exists()) {
                        holder.likeProduct.setImageDrawable(context.getDrawable(R.drawable.after_liked));
                    } else {
                        holder.likeProduct.setImageDrawable(context.getDrawable(R.drawable.before_liked));
                    }
                }
            }
        });

        // likes count
        firestore.collection("Products/" + productId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (!value.isEmpty()) {
                        int count = value.size();
                        holder.setPostLikes(count);
                    } else {
                        holder.setPostLikes(0);
                    }
                }
            }
        });

        if (currentUserId.equals(product.getUser())) {
            holder.deleteBtn.setVisibility(View.VISIBLE);
            holder.deleteBtn.setClickable(true);
            holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Delete")
                            .setMessage("Are you sure?")
                            .setNegativeButton("No", null)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    firestore.collection("Products/" + productId + "/Likes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                firestore.collection("Products/" + productId + "/Likes").document(snapshot.getId()).delete();
                                            }
                                        }
                                    });
                                    firestore.collection("Products").document(productId).delete();
                                    productList.remove(position);
                                    notifyDataSetChanged();
                                }
                            });
                    alert.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDetail, productDate, productUsername, postLikes;
        ImageView productPic, likeProduct, deleteBtn;
        CircleImageView profilePic;
        View mView;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            likeProduct = mView.findViewById(R.id.productlikebtn);
            postLikes = mView.findViewById(R.id.like_count_tv);
            deleteBtn = mView.findViewById(R.id.product_delete_btn);
        }

        public void setPostLikes(int count) {
            postLikes.setText(count + " Likes");
        }

        public void setProductPic(String image) {
            productPic = mView.findViewById(R.id.productimage);
            if (image != null) {
                Glide.with(context).load(image).into(productPic);
            }
        }

        public void setProductName(String name) {
            productName = mView.findViewById(R.id.productname);
            productName.setText(name);
        }

        public void setProductPrice(String price) {
            productPrice = mView.findViewById(R.id.productprice);
            productPrice.setText("$ " + price);
        }

        public void setProductDetail(String detail) {
            productDetail = mView.findViewById(R.id.productdetails);
            if (productDetail != null) {
                productDetail.setText(detail);
            }
        }

        public void setProductDate(String date) {
            productDate = mView.findViewById(R.id.date_tv);
            productDate.setText(date);
        }

        public void setProfilePic(String urlProfile) {
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }

        public void setProductUsername(String username) {
            productUsername = mView.findViewById(R.id.username_tv);
            productUsername.setText(username);
        }
    }
}
