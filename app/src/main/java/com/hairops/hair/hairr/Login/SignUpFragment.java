package com.hairops.hair.hairr.Login;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hairops.hair.hairr.R;
import com.hairops.hair.hairr.StackActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements OnSignUpListener {

    private FirebaseAuth auth;
    private EditText edtEmail,edtPassword,edtConfirmPassword;
    private Button signup;
    private ProgressDialog dialog;
SharedPreferences preferences;

    public SignUpFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        auth = FirebaseAuth.getInstance();
        preferences = getContext().getSharedPreferences("user", Context.MODE_PRIVATE);

        edtEmail = (EditText)root.findViewById(R.id.edtSignUpEmail);
        edtPassword = (EditText)root.findViewById(R.id.edtSignUpPassword);
        edtConfirmPassword = (EditText)root.findViewById(R.id.edtSignUpConfirmPassword);
        dialog = new ProgressDialog(getContext());

        signup = (Button)root.findViewById(R.id.btnSignUp);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });




        return root;
    }

    private void startSignUp() {
        dialog.setMessage("Signing up... please wait");
        dialog.show();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (email.isEmpty()||password.isEmpty()||confirmPassword.isEmpty()){
            Toast.makeText(getContext(), "Please enter these fields to continue", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        if (!confirmPassword.equalsIgnoreCase(password)){
            Toast.makeText(getContext(), "Passwords does not match", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            return;
        }

        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(getContext(), "you have successfully signup, try login in again to enter", Toast.LENGTH_SHORT).show();
                         SharedPreferences.Editor editor = preferences.edit();
                         editor.putString("email",email);
                         editor.apply();
                            startActivity(new Intent(getContext(), StackActivity.class));
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), String.valueOf(e), Toast.LENGTH_SHORT).show();

            }
        });

    }

    @Override
    public void signUp() {

    }
}
