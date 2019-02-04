package com.example.user.hairr.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.hairr.Model.Post;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class customerHome extends Fragment {

    FirebaseAuth auth;
    private RecyclerView postList;
    private DatabaseReference mUsersDatabase, postDatabase;
    private LinearLayoutManager mLayoutManager;
    private LinearLayout comment;
    private String key;
    int num_of_likes;
    int num_of_comment;


    public customerHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customer_home, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        postDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        postList = (RecyclerView) view.findViewById(R.id.lastestNews);

        mLayoutManager = new LinearLayoutManager(getContext());
        comment = view.findViewById(R.id.linComment);



        postList.setHasFixedSize(true);
        postList.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<Post, PostViewHolder> adapter = new FirebaseRecyclerAdapter<Post, PostViewHolder>(
                Post.class,
                R.layout.singlepostitem,
                PostViewHolder.class,
                postDatabase
        ) {
            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, Post model, int position) {

                viewHolder.setDisplayName(model.getUsername());
                viewHolder.setPostImage(model.getPostImageUrl(), getContext());
                viewHolder.setUserImage(model.getUserImage(), getContext());
                viewHolder.userLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //ToDO show dialog to see user profile
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialoguserprofile);
                        final ImageView stylistImage = dialog.findViewById(R.id.userImageThumbNail);
                        TextView styistName = dialog.findViewById(R.id.stylistName);
                        TextView stylistSpacialization = dialog.findViewById(R.id.stylistName);
                        FancyButton button = dialog.findViewById(R.id.close);

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Picasso.with(getContext()).load(model.getUserImage()).transform(new CircleTransform()).into(stylistImage);

                        styistName.setText(model.getUsername());
                        stylistSpacialization.setText(model.getUserSpecialization());


                        dialog.show();
                    }
                });
                viewHolder.like.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if(model.getLikes()==0){
                            model.getLikes();
                        }else {
                            model.getLikes();
                        }
                    }
                });
                viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), com.example.user.hairr.comment.class);

                        intent.putExtra("postid", model.getPosterId());
                        startActivity(intent);




                    }
                });

                key = getRef(position).getKey();

            }
        };

        postList.setAdapter(adapter);
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView comment;
        ImageView like;
        ImageView userImage;
        ImageView postImage;
        LinearLayout userLayout;


        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            like = (ImageView) mView.findViewById(R.id.postLike);
            comment = (ImageView) mView.findViewById(R.id.postComment);
            userImage = (ImageView) mView.findViewById(R.id.posterImage);
            postImage = (ImageView) mView.findViewById(R.id.postImage);
            userLayout = (LinearLayout) mView.findViewById(R.id.linUser);

        }

        public void setDisplayName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.posterName);
            userNameView.setText(name);

        }

        public void setUserImage(String status, Context context) {

            Picasso.with(context).load(status).transform(new CircleTransform()).networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(status).into(userImage);

                }
            });


        }


        public void setPostImage(String status, Context context) {

            Picasso.with(context).load(status).transform(new CircleTransform()).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(status).into(postImage);

                }
            });


        }


    }

}


