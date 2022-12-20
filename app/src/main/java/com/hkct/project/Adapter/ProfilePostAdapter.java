package com.hkct.project.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hkct.project.Model.Post;
import com.hkct.project.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ProfilePostAdapter extends RecyclerView.Adapter<ProfilePostAdapter.ViewHolder> {

    private Context context;
    private List<Post> mPosts;

    public ProfilePostAdapter(Context context, List<Post> mPosts) {
        this.context = context;
        this.mPosts = mPosts;
    }


    @androidx.annotation.NonNull
    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.each_profile_post, parent, false);
        return new ProfilePostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        Glide.with(context).load(post.getImage()).into(holder.profile_post_image);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView profile_post_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profile_post_image = itemView.findViewById(R.id.profile_posts);
        }
    }
}
