package com.hairops.hair.hairr.Catergory;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.hairops.hair.hairr.Fragment.DetailMakeup;
import com.hairops.hair.hairr.Fragment.makup;
import com.hairops.hair.hairr.R;
import com.hairops.hair.hairr.Utils.ViewPagerAdapter;

public class MakeupCategory extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageButton button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_makeup_category);

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
        adapter.addFragment(new makup(), "Booking");
        adapter.addFragment(new DetailMakeup(), "Details");
        viewPager.setAdapter(adapter);
    }
}