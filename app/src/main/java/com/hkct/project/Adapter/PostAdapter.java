package com.hkct.project.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.hkct.project.CommentsActivity;
import com.hkct.project.Model.Post;
import com.hkct.project.Model.Users;
import com.hkct.project.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> mList;
    private List<Users> usersList;
    private Activity context;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;
    private String Uid;

    public PostAdapter(Activity context, List<Post> mList, List<Users> usersList) {
        this.mList = mList;
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.each_post, parent, false);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        Uid = auth.getCurrentUser().getUid();
        return new PostViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = mList.get(position);
        holder.setPostPic(post.getImage());
        holder.setPostCaption(post.getCaption());

        long milliseconds = post.getTime().getTime();
        String date = DateFormat.getDateInstance().format(new Date(milliseconds));
        holder.setPostDate(date);

        String username = usersList.get(position).getName();
        String image = usersList.get(position).getImage();

        holder.setProfilePic(image);
        holder.setPostUsername(username);


        // like btn
        String postId = post.PostId;
        String currentUserId = auth.getCurrentUser().getUid();
        holder.likePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.getResult().exists()) {
                            Map<String, Object> likesMap = new HashMap<>();
                            likesMap.put("timestamp", FieldValue.serverTimestamp());
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).set(likesMap);
                        } else {
                            firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).delete();
                        }
                    }
                });
            }
        });

        // like btn color change
        firestore.collection("Posts/" + postId + "/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error == null) {
                    if (value.exists()) {
                        holder.likePic.setImageDrawable(context.getDrawable(R.drawable.after_liked));
                    } else {
                        holder.likePic.setImageDrawable(context.getDrawable(R.drawable.before_liked));
                    }
                }
            }
        });

        // likes count
        firestore.collection("Posts/" + postId + "/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

        //comments implementation
        holder.commentsPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("postid", postId);
                context.startActivity(commentIntent);
            }
        });

        holder.postLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent likeIntent = new Intent(context, )
            }
        });

        if (currentUserId.equals(post.getUser())) {

            firestore.collection("Users").document(Uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            String memberShip = task.getResult().getString("membership");
                            if (memberShip.equals("1")) {
                                holder.membershipIcon.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });

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

                                    firestore.collection("Posts/" + postId + "/Comments").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                firestore.collection("Posts/" + postId + "/Comments").document(snapshot.getId()).delete();
                                            }
                                        }
                                    });
                                    firestore.collection("Posts/" + postId + "/Likes").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                firestore.collection("Posts/" + postId + "/Likes").document(snapshot.getId()).delete();
                                            }
                                        }
                                    });
                                    firestore.collection("Posts").document(postId).delete();
                                    mList.remove(position);
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
        return mList.size();
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView postPic, commentsPic, likePic;
        CircleImageView profilePic;
        TextView postUsername, postDate, postCaption, postLikes;
        ImageView deleteBtn;
        ImageView membershipIcon;

        View mView;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            mView = itemView;
            likePic = mView.findViewById(R.id.like_btn);
            commentsPic = mView.findViewById(R.id.comments_post);
            deleteBtn = mView.findViewById(R.id.delete_btn);
            membershipIcon = mView.findViewById(R.id.membership_icon_post);
            CircleImageView circleImageView;
        }

        public void setPostLikes(int count) {
            postLikes = mView.findViewById(R.id.like_count_tv);
            postLikes.setText(count + " Likes");
        }

        public void setPostPic(String urlPost) {
            postPic = mView.findViewById(R.id.user_post);
            Glide.with(context).load(urlPost).into(postPic);
        }

        public void setProfilePic(String urlProfile) {
            profilePic = mView.findViewById(R.id.profile_pic);
            Glide.with(context).load(urlProfile).into(profilePic);
        }

        public void setPostUsername(String username) {
            postUsername = mView.findViewById(R.id.username_tv);
            postUsername.setText(username);
        }

        public void setPostDate(String date) {
            postDate = mView.findViewById(R.id.date_tv);
            postDate.setText(date);
        }

        public void setPostCaption(String caption) {
            postCaption = mView.findViewById(R.id.caption_tv);
            postCaption.setText(caption);
        }
    }
}
