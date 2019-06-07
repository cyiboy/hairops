package com.hairops.hair.hairr.intro;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.hairops.hair.hairr.CustomersCompleteReg;
import com.hairops.hair.hairr.HairStylistCompleteReg;
import com.hairops.hair.hairr.R;
import com.hairops.hair.hairr.StackActivity;

public class Signup extends AppCompatActivity {
    private FirebaseAuth auth;
    private EditText edtEmail,edtPassword,edtConfirmPassword;
    private Button signup;
    private ProgressDialog dialog;
    SharedPreferences preferences;
    private String tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        tag = getIntent().getStringExtra("tag");

        preferences = getSharedPreferences("user", Context.MODE_PRIVATE);

        edtEmail = (EditText)findViewById(R.id.edtSignUpEmail);
        edtPassword = (EditText)findViewById(R.id.edtSignUpPassword);
        edtConfirmPassword = (EditText)findViewById(R.id.edtSignUpConfirmPassword);
        dialog = new ProgressDialog(this);

        signup = (Button)findViewById(R.id.btnSignUp);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });

    }

    private void startSignUp() {
        dialog.setMessage("Signing up... please wait");
        dialog.show();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (email.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
            Toast.makeText(Signup.this, "Please enter these fields to continue", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        if (!confirmPassword.equalsIgnoreCase(password)){
            Toast.makeText(Signup.this, "Passwords does not match", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(Signup.this, "you have successfully signup, try login in again to enter", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("email",email);
                            editor.apply();

                            if (tag.equalsIgnoreCase("stylist")){
                                startActivity(new Intent(Signup.this, HairStylistCompleteReg.class));
                            }else {
                                startActivity(new Intent(Signup.this, CustomersCompleteReg.class));
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(Signup.this, String.valueOf(e), Toast.LENGTH_SHORT).show();

            }
        });

    }
}
