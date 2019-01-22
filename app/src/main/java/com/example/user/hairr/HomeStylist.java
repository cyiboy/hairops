package com.example.user.hairr;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.user.hairr.Fragment.StylistHome;
import com.example.user.hairr.Fragment.StylistProfile;
import com.example.user.hairr.Fragment.stylistBookings;
import com.example.user.hairr.Fragment.stylistTransaction;

public class HomeStylist extends AppCompatActivity {


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            android.support.v4.app.Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.stylistHome:
                    fragment = new StylistHome();
                    break;
                case R.id.stylistBooking:
                   fragment = new stylistBookings();
                    break;
                case R.id.stylistTransaction:
                   fragment = new stylistTransaction();
                    break;

                case R.id.stylistProfile:
                   fragment = new StylistProfile();
                    break;
            }
          return  loadFragment(fragment);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_stylist);


        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
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
}
