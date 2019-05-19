package com.hairops.hair.hairr.Login;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hairops.hair.hairr.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetPasswword extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText edtEmail;
    private Button btnResetEMail;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_passwword);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        btnResetEMail = (Button) findViewById(R.id.btnResetPassword);
        edtEmail = (EditText)findViewById(R.id.forgetEmail);

        btnResetEMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetPassword();
            }
        });

    }

    private void resetPassword() {

        String email = edtEmail.getText().toString().trim();

        if (email.isEmpty()){
            Toast.makeText(this, "Enter an email to actually reset the password", Toast.LENGTH_SHORT).show();
            return;
        }

        dialog.setMessage("Resetting you password..please wait");
        dialog.setCancelable(false);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(ForgetPasswword.this, "Follow the instruction sent to you in your email to reset your password", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(ForgetPasswword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


}
