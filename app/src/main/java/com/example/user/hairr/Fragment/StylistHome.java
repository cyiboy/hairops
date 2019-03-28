package com.example.user.hairr.Fragment;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Model.Post;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class StylistHome extends Fragment {
    private static final int PICK_IMAGE_REQUEST = 2;
    FirebaseAuth auth;
    private RecyclerView postList;
    private DatabaseReference mUsersDatabase, postDatabase,likes;
    private LinearLayoutManager mLayoutManager;
    private FloatingActionButton addPost;
    private ProgressDialog mProgressBar;
    private ImageView circleImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private StorageTask mUploadTask;
    private boolean mProcessLike = false;
    private String key,uid;
    public StylistHome() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_stylist_home, container, false);
        return root;


    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mProgressBar = new ProgressDialog(getContext());
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        postDatabase = FirebaseDatabase.getInstance().getReference().child("Posts");
        mStorageRef = FirebaseStorage.getInstance().getReference().child("Post Upload");
        likes = FirebaseDatabase.getInstance().getReference().child("Likes");
        postList = (RecyclerView) view.findViewById(R.id.rvPost);
        addPost = (FloatingActionButton) view.findViewById(R.id.btnAddPost);

        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddPostDialog();
            }
        });

        mLayoutManager = new LinearLayoutManager(getContext());
        postList.setHasFixedSize(true);
        postDatabase.keepSynced(true);
        likes.keepSynced(true);
        postList.setLayoutManager(mLayoutManager);
    }

    private void showAddPostDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.singlepostitemadd);
        circleImageView = dialog.findViewById(R.id.postImageAdd);
        EditText styistName = dialog.findViewById(R.id.edtAddPost);

        FancyButton button = dialog.findViewById(R.id.btnAddPost);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUpload(styistName.getText().toString().trim());
            }
        });

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        dialog.show();
    }

    private void startUpload(String trim) {
        String date = new SimpleDateFormat("yyyy-mm-dd", Locale.getDefault()).format(new Date());

        if (mImageUri != null) {
            mProgressBar.setMessage("Uploading Post.. please wait");
            mProgressBar.show();

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downUri = task.getResult();
                        String imageUrl = downUri.toString();
                        String uid = auth.getCurrentUser().getUid();

                        mUsersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String imageUrls = dataSnapshot.child("imageUrl").getValue().toString();
                                String spec = dataSnapshot.child("specialization").getValue().toString();

                                Post post = new Post();
                                post.setPosterId(uid);
                                post.setPostImageUrl(imageUrl);
                                post.setUserImage(imageUrls);
                                post.setUsername(name);
                                post.setUserSpecialization(spec);
                                post.setDate(date);
                                post.setComments(0);
                                post.setLikes(0);

                                if (trim.isEmpty()) {
                                    post.setPosttText("");
                                } else {
                                    post.setPosttText(trim);
                                }

                                postDatabase.push().setValue(post)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    mProgressBar.dismiss();
                                                    Toast.makeText(getContext(), "upload done", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        mProgressBar.dismiss();
                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                    }
                }
            });
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        initAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
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
                key = getRef(position).getKey();
                String uid = auth.getCurrentUser().getUid();


                viewHolder.setDisplayName(model.getUsername());
                viewHolder.setPostImage(model.getPostImageUrl(), getContext());
                viewHolder.setUserImage(model.getUserImage(), getContext());
                viewHolder.setLikeBtn(key);
                viewHolder.date.setText(model.getDate());
                viewHolder.setNumberOfLikes(key);
                viewHolder.postText.setText(model.getPosttText());
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
                        mProcessLike = true;

                        likes.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (mProcessLike) {

                                    if (dataSnapshot.child(key).hasChild(uid)) {
                                        likes.child(key).child(uid).removeValue();
                                        viewHolder.like.setImageResource(R.drawable.ic_unlike);
                                        mProcessLike = false;
                                        viewHolder.setNumberOfLikes(key);

                                    } else {
                                        likes.child(key).child(uid).setValue("liked");
                                        viewHolder.like.setImageResource(R.drawable.ic_like);
                                        mProcessLike = false;
                                        viewHolder.setNumberOfLikes(key);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
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



            }
        };

        postList.setAdapter(adapter);
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    // image chooser
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(getContext()).load(mImageUri).transform(new CircleTransform()).into(circleImageView);


        }
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView comment;
        ImageView like;
        ImageView userImage;
        ImageView postImage;
        LinearLayout userLayout;
        FirebaseAuth auth;
        TextView numberOfLikes,postText,date;
        DatabaseReference likes;
        String uidd;


        public PostViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            like = (ImageView) mView.findViewById(R.id.postLike);
            comment = (ImageView) mView.findViewById(R.id.postComment);
            userImage = (ImageView) mView.findViewById(R.id.posterImage);
            postImage = (ImageView) mView.findViewById(R.id.postImage);
            userLayout = (LinearLayout) mView.findViewById(R.id.linUser);
            numberOfLikes = (TextView)mView.findViewById(R.id.numberOfLikes);
            postText = (TextView)mView.findViewById(R.id.postalText);
            date = mView.findViewById(R.id.date);

            auth = FirebaseAuth.getInstance();
            uidd = auth.getCurrentUser().getUid();
            likes = FirebaseDatabase.getInstance().getReference().child("Likes");
            likes.keepSynced(true);

        }
        public void setNumberOfLikes(String key){

            likes.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int size = (int) dataSnapshot.getChildrenCount();

                    numberOfLikes.setText(String.valueOf(size));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        public void setLikeBtn(String key){

            likes.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(key).hasChild(uidd)){
                        like.setImageResource(R.drawable.ic_like);

                    }else {
                        like.setImageResource(R.drawable.ic_unlike);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

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

            Picasso.with(context).load(status).networkPolicy(NetworkPolicy.OFFLINE).into(postImage, new Callback() {
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
