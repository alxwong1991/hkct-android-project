package com.hkct.project.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.LikeProduct;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LikeProductAdapter extends RecyclerView.Adapter<LikeProductAdapter.LikeProductViewHolder> {

    private Activity context;
    private List<Users> usersList;
    private List<LikeProduct> likeProductList;

    public LikeProductAdapter(Activity context, List<Users> usersList, List<LikeProduct> likeProductList) {
        this.context = context;
        this.usersList = usersList;
        this.likeProductList = likeProductList;
    }

    @NonNull
    @Override
    public LikeProductAdapter.LikeProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_product_likes, parent, false);
        return new LikeProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeProductViewHolder holder, int position) {
        Users users = usersList.get(position);
        holder.setmUserName(users.getName());
        holder.setCircleImageView(users.getImage());
    }

    @Override
    public int getItemCount() {
        return likeProductList.size();
    }

    public class LikeProductViewHolder extends RecyclerView.ViewHolder {
        TextView mUserName;
        CircleImageView circleImageView;
        View mView;

        public LikeProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setmUserName(String userName) {
            mUserName = mView.findViewById(R.id.like_product_user);
            mUserName.setText(userName);
        }

        public void setCircleImageView(String profilePic) {
            circleImageView = mView.findViewById(R.id.like_product_profile_pic);
            Glide.with(context).load(profilePic).into(circleImageView);
        }
    }
}
