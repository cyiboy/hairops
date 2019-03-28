package com.example.user.hairr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.user.hairr.Fragment.StylistHome;
import com.example.user.hairr.Fragment.StylistProfile;
import com.example.user.hairr.Fragment.stylistBookings;
import com.example.user.hairr.Fragment.stylistTransaction;
import com.example.user.hairr.Utils.BottomNavigationViewHelper;

public class HomeStylist extends AppCompatActivity {
    android.support.v4.app.Fragment fragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.stylistHome:
                    fragment = new StylistHome();
                    loadFragment(fragment);
                    return true;
                case R.id.stylistBooking:
                   fragment = new stylistBookings();
                    loadFragment(fragment);
                    return true;
                case R.id.stylistTransaction:
                   fragment = new stylistTransaction();
                    loadFragment(fragment);
                    return true;

                case R.id.stylistProfile:
                    fragment = new StylistProfile();
                    loadFragment(fragment);
                    return true;
            }
          return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_stylist);
        fragment =new StylistHome();
        loadFragment(fragment);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    private boolean loadFragment(android.support.v4.app.Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}
