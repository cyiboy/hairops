package com.example.user.hairr;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.hairr.Model.Customer;
import com.example.user.hairr.Model.HairStylist;
import com.example.user.hairr.Utils.CircleTransform;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UpdateStylistProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 2;
    int PLACE_PICKER_REQUEST = 1;
    SharedPreferences preferences;
    FirebaseAuth mAuth;
    ImageView profileImage, onback, save;
    EditText name, phoneNumber, address, email,bankName,bankAccountName,bankAccountNumber;
    Button change;
    private ProgressDialog mProgressBar;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_stylist_profile);

        profileImage = findViewById(R.id.profileImageStyle);
        onback = findViewById(R.id.onbackStyle);
        save = findViewById(R.id.saveStyle);
        name = findViewById(R.id.nameStyle);
        address = findViewById(R.id.addressStyle);
        phoneNumber = findViewById(R.id.phoneNumberStyle);
        email = findViewById(R.id.emailStyle);
        bankName = findViewById(R.id.bankName);
        bankAccountName = findViewById(R.id.bankAccountName);
        bankAccountNumber = findViewById(R.id.bankAccountNumber);
        change = findViewById(R.id.changePicsStyle);

        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        mProgressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mStorageRef = FirebaseStorage.getInstance().getReference("profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");
        name.setText(preferences.getString("firstName", null));
        phoneNumber.setText(preferences.getString("phoneNumber", null));
        email.setText(preferences.getString("email", null));
        bankName.setText(preferences.getString("bankName", null));
        bankAccountNumber.setText(preferences.getString("bankAccountNumber", null));
        bankAccountName.setText(preferences.getString("bankAccountName", null));
        address.setText(preferences.getString("address", null));
        String im = preferences.getString("imageUrl", null);
        Picasso.with(getApplicationContext()).load(im).transform(new CircleTransform()).into(profileImage);

        onback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save();
            }
        });
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


    }

    private void save() {
        String Email = email.getText().toString().trim();
        String phone = phoneNumber.getText().toString().trim();
        String Name = name.getText().toString().trim();
        String Address = address.getText().toString().trim();
        String BankName = bankName.getText().toString().trim();
        String BankAccntName = bankAccountName.getText().toString().trim();
        String BankAccntNumber = bankAccountNumber.getText().toString().trim();


        if (mImageUri != null && Name != null && Email != null && phone != null && Address != null && BankName != null && BankAccntNumber != null && BankAccntName != null) {

            mProgressBar.setMessage("updating.. please wait");
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


                        HairStylist customer = new HairStylist();
                        customer.setEmail(Email);
                        customer.setName(Name);
                        customer.setNumber(phone);
                        customer.setAddress(Address);
                        customer.setBankAccountName(BankAccntName);
                        customer.setBankName(BankName);
                        customer.setBankAccountNumber(BankAccntNumber);


                        String uploadId = mAuth.getCurrentUser().getUid();

                        mDatabaseRef.child(uploadId).setValue(customer)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            SharedPreferences.Editor edit = preferences.edit();
                                            edit.putString("firstName", Name);
                                            edit.putString("phoneNumber", phone);
                                            edit.putString("email", Email);
                                            edit.putString("imageUrl", imageUrl);
                                            edit.putString("bankAccountName",BankAccntName);
                                            edit.putString("bankName",BankName);
                                            edit.putString("address",Address);
                                            edit.putString("bankAccountNumber",BankAccntNumber);
                                            edit.apply();
                                            //startActivity(new Intent(getApplicationContext(), HomeCustomer.class));
                                            onBackPressed();
                                            mProgressBar.show();
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                mProgressBar.show();
                            }
                        });


                    }
                }
            });
            mProgressBar.show();

        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(getApplicationContext()).load(mImageUri).transform(new CircleTransform()).into(profileImage);


        }
    }
}
