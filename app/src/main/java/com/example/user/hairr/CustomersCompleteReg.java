package com.example.user.hairr;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.user.hairr.Model.Customer;
import com.example.user.hairr.Utils.CircleTransform;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
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
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

public class CustomersCompleteReg extends AppCompatActivity {
    int PLACE_PICKER_REQUEST = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private ProgressDialog mProgressBar;
    private ImageView circleImageView;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    public static final String MyPREFERENCES = "MyPrefs";
    public static final String STATUS = "status";
    SharedPreferences sharedpreferences;
    FirebaseAuth mAuth;
    EditText inputEmail, inputName,inputPhoneNumber,homeAddress;
    private LatLng latLng;
    private String lat,lng,email,name,phoneNumber,orgainzation,address;
    private MaterialSpinner spinnerOrganization;
SharedPreferences preferences;
    private FancyButton setLocation;
    Button completeSignupCustomer;

    private static final String[] organi = {
            "Organization",
            "Individual",
            "School",
            "Office"

    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customers_complete_reg);
        preferences = getApplicationContext().getSharedPreferences("user", Context.MODE_PRIVATE);
        mProgressBar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        circleImageView = (ImageView)findViewById(R.id.addImageCustomer);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
        inputEmail = (EditText)findViewById(R.id.edtCustomerEmail);
        inputName = (EditText)findViewById(R.id.edtCustomerName);
        inputPhoneNumber = (EditText)findViewById(R.id.edtCustomerPhoneNumber);
        homeAddress = (EditText)findViewById(R.id.edtCustomerAddress);
        spinnerOrganization = (MaterialSpinner)findViewById(R.id.organization);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        spinnerOrganization.setItems(organi);
        spinnerOrganization.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> orgainzation = item);
        spinnerOrganization.setOnNothingSelectedListener(spinner -> Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show());


        circleImageView.setOnClickListener(view -> openFileChooser());

        mStorageRef = FirebaseStorage.getInstance().getReference("profile");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Users");


        setLocation = (FancyButton)findViewById(R.id.btnPickCustomerLocation);
        completeSignupCustomer =  findViewById(R.id.btnCompleteCustomerRegistration);
        completeSignupCustomer.setOnClickListener(view -> startSignUp());
        setLocation.setOnClickListener(view -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

            try {
                startActivityForResult(builder.build(CustomersCompleteReg.this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        });
    }

    private void startSignUp() {
        name = inputName.getText().toString().trim();
        email = inputEmail.getText().toString().trim();
        phoneNumber = inputPhoneNumber.getText().toString().trim();
        address = homeAddress.getText().toString().trim();

if (phoneNumber.length() != 11){
    Toast.makeText(this, "Check the phone number", Toast.LENGTH_SHORT).show();
    return;
}
        if (mImageUri != null && !name.isEmpty() && name != null && !email.isEmpty() && email != null &&!phoneNumber.isEmpty() && phoneNumber != null && !address.isEmpty() && address != null) {

            mProgressBar.setMessage("Completing registration.. please wait");
            mProgressBar.show();

            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downUri = task.getResult();
                        String imageUrl = downUri.toString();

                        String uid = mAuth.getCurrentUser().getUid();



                        Customer customer = new Customer();
                        customer.setEmail(email);
                        customer.setName(name);
                        customer.setNumber(phoneNumber);
                        customer.setOrganization(orgainzation);
                        customer.setImageUrl(imageUrl);
                        customer.setLatitude(lat);
                        customer.setLongitude(lng);
                        customer.setStatus("customer");
                        customer.setAddress(address);
                        customer.setUid(uid);
                        customer.setBalance(0.0);

                        String uploadId = mAuth.getCurrentUser().getUid();

                        mDatabaseRef.child(uploadId).setValue(customer)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            SharedPreferences.Editor editor = sharedpreferences.edit();
                                            editor.putString(STATUS, "customer");
                                            editor.apply();
                                            SharedPreferences.Editor edit = preferences.edit();
                                            edit.putString("firstName",name);
                                            edit.putString("email",email);
                                            edit.putString("phoneNumber",phoneNumber);
                                            edit.putString("oga",orgainzation);
                                            edit.putString("imageUrl",imageUrl);
                                            edit.putString("address",address);
                                            edit.apply();
                                            startActivity(new Intent(CustomersCompleteReg.this,HomeCustomer.class));
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CustomersCompleteReg.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                }
            });


        }else {
            Toast.makeText(this, "Fill details completely to continue", Toast.LENGTH_SHORT).show();

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
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
                latLng = place.getLatLng();
                lat = String.valueOf(latLng.latitude);
                lng = String.valueOf(latLng.longitude);
            }
        }else  if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(CustomersCompleteReg.this).load(mImageUri).transform(new CircleTransform()).into(circleImageView);


        }
    }
}
