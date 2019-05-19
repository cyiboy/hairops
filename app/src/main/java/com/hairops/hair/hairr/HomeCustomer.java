package com.hairops.hair.hairr;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.hairops.hair.hairr.Fragment.customerBooking;
import com.hairops.hair.hairr.Fragment.customerHome;
import com.hairops.hair.hairr.Fragment.customerTransaction;
import com.hairops.hair.hairr.Fragment.customerrofile;
import com.hairops.hair.hairr.Utils.BottomNavigationViewHelper;

public class HomeCustomer extends AppCompatActivity {
    Fragment fragment;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.customerHome:
                     fragment = new customerHome();
                     loadFragment(fragment);
                    return true;
                case R.id.stylistBooking:
                    fragment = new customerBooking();
                    loadFragment(fragment);
                    return true;
                case R.id.customerTransaction:
                    fragment = new customerTransaction();
                    loadFragment(fragment);
                    return true;

                case R.id.customerProfile:
                    fragment = new customerrofile();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_customer);
        fragment =new customerHome();
        loadFragment(fragment);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private boolean loadFragment(Fragment fragment) {
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
