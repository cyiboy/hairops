package com.example.user.hairr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.user.hairr.Fragment.AdminHome;
import com.example.user.hairr.Fragment.AllBooking;
import com.example.user.hairr.Fragment.AllTransaction;
import com.example.user.hairr.Fragment.AllUsers;
import com.example.user.hairr.Fragment.Transaction;
import com.example.user.hairr.Utils.BottomNavigationViewHelper;

public class HomeAdmin extends AppCompatActivity {
    android.support.v4.app.Fragment fragment;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.adminHome:
                   fragment = new AdminHome();
                    loadFragment(fragment);
                    return true;
                case R.id.requestedMoney:
                   fragment = new AllBooking();
                    loadFragment(fragment);
                    return true;
                case R.id.allTransaction:
                    fragment = new Transaction();
                    loadFragment(fragment);
                    return true;
                case R.id.allUser:
                   fragment = new AllUsers();
                    loadFragment(fragment);
                    return true;

                case R.id.allBookings:
                    fragment = new AllTransaction();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_admin);

        fragment =new AdminHome();
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
}
