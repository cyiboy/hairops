package com.example.user.hairr.Catergory;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.example.user.hairr.Fragment.Details;
import com.example.user.hairr.Fragment.DetailsHairdressing;
import com.example.user.hairr.Fragment.hairdressing;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.ViewPagerAdapter;

public class HairDressingCatergory extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageButton button;

    @Override


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hair_dressing_catergory);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        button = findViewById(R.id.onback);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new hairdressing(), "Booking");
        adapter.addFragment(new DetailsHairdressing(), "Details");
        viewPager.setAdapter(adapter);
    }
}