package com.example.user.hairr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.example.user.hairr.Login.LoginButton;
import com.example.user.hairr.Login.LoginFragment;
import com.example.user.hairr.Login.SignUpFragment;
import com.example.user.hairr.intro.intro;
import com.google.firebase.auth.FirebaseAuth;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static com.example.user.hairr.FlexibleFrameLayout.ORDER_LOGIN_STATE;
import static com.example.user.hairr.FlexibleFrameLayout.ORDER_SIGN_UP_STATE;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private static final String TAG = "MainActivity";
    private String uid;
    private boolean isLogin = true;
   // ActivityMainBinding binding;
    CoordinatorLayout layout;
    LoginButton button;
    FrameLayout login_fragment;
    FrameLayout signup_fragment;
    FlexibleFrameLayout wrapper;
    private String status;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        status = sharedpreferences.getString("status", "");
        button = (LoginButton) findViewById(R.id.button);
        login_fragment = (FrameLayout) findViewById(R.id.login_fragment);
        signup_fragment = (FrameLayout) findViewById(R.id.sign_up_fragment);
        layout = (CoordinatorLayout)findViewById(R.id.corinator);
        wrapper  = (FlexibleFrameLayout)findViewById(R.id.wrapper);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null){

            if (!status.isEmpty() || status != null){
                if (status.equalsIgnoreCase("admin")){
                    startActivity(new Intent(MainActivity.this,HomeAdmin.class));
                }else if (status.equalsIgnoreCase("customer")){
                    startActivity(new Intent(MainActivity.this,HomeCustomer.class));
                }else {
                    startActivity(new Intent(MainActivity.this,HomeStylist.class));
                }
            }

        }else {

        }

        intro introScreen = new intro();
        LoginFragment topLoginFragment = new LoginFragment();
        SignUpFragment topSignUpFragment = new SignUpFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.login_fragment, topLoginFragment)
                .replace(R.id.sign_up_fragment, topSignUpFragment)
                .commit();

      login_fragment.setRotation(-90);

        button.setOnSignUpListener( topSignUpFragment);
       button.setOnLoginListener(topLoginFragment);

      button.setOnButtonSwitched(isLogin -> {

                 layout.setBackgroundColor(ContextCompat.getColor(
                            this,
                            isLogin ? R.color.colorPrimary : R.color.secondPage));
        });

        login_fragment.setVisibility(INVISIBLE);


    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        login_fragment.setPivotX(login_fragment.getWidth() / 2);
        login_fragment.setPivotY(login_fragment.getHeight());
        signup_fragment.setPivotX(signup_fragment.getWidth() / 2);
        signup_fragment.setPivotY(signup_fragment.getHeight());
    }

    public void switchFragment(View v) {
        if (isLogin) {
           login_fragment.setVisibility(VISIBLE);
            login_fragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                   signup_fragment.setVisibility(INVISIBLE);
                    signup_fragment.setRotation(90);
                   wrapper.setDrawOrder(ORDER_LOGIN_STATE);
                }
            });
        } else {
           signup_fragment.setVisibility(VISIBLE);
            signup_fragment.animate().rotation(0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    login_fragment.setVisibility(INVISIBLE);
                    login_fragment.setRotation(-90);
                   wrapper.setDrawOrder(ORDER_SIGN_UP_STATE);
                }
            });
        }

        isLogin = !isLogin;
       button.startAnimation();
    }
}
