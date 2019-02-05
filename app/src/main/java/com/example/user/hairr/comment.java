package com.example.user.hairr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.hairr.Model.commentM;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class comment extends AppCompatActivity {
    ImageView close, send;
    EditText comment;
    FirebaseAuth auth;
    RecyclerView list;
    private DatabaseReference mUsersDatabase, postDatabase;
    private String key;

    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();
        String postid = intent.getStringExtra("postid");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
  postDatabase = FirebaseDatabase.getInstance().getReference().child("Posts/"+postid+"/comment");

        close= findViewById(R.id.close);
        comment=findViewById(R.id.edtComments);
        send=findViewById(R.id.imgSendComment);
        list=findViewById(R.id.rvComments);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            sendcomment();
            }
        });
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        list.setHasFixedSize(true);
        list.setLayoutManager(mLayoutManager);


    }

   private void sendcomment() {
        String mainComment = comment.getText().toString().trim();
        String uid = auth.getCurrentUser().getUid();
        mUsersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String imageUrls = dataSnapshot.child("imageUrl").getValue().toString();
                commentM commentM = new commentM();
                commentM.setCommentText(mainComment);
                commentM.setUserImage(imageUrls);
                commentM.setUsername(name);
                postDatabase.push().setValue(commentM).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<commentM, PostViewHolder> adapter = new FirebaseRecyclerAdapter<commentM, PostViewHolder>(
                commentM.class,
                R.layout.singlecomment,
                PostViewHolder.class,
                postDatabase
        ) {


            @Override
            protected void populateViewHolder(PostViewHolder viewHolder, commentM model, int position) {


                viewHolder.setUserImage(model.getUserImage(), getApplicationContext());
                viewHolder.commentName.setText(model.getUsername());
                viewHolder.comment.setText(model.getCommentText());

                key = getRef(position).getKey();

            }
        };

        list.setAdapter(adapter);
    }


    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView commentName, comment;
        ImageView userImage;




        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            commentName = mView.findViewById(R.id.commentname);
            comment = mView.findViewById(R.id.commentText);
            userImage = (ImageView) mView.findViewById(R.id.commentImg);
            // postImage = (ImageView) mView.findViewById(R.id.postImage);


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


    }


}
