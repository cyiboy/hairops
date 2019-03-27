package com.example.user.hairr.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.user.hairr.R;
import com.example.user.hairr.Utils.ViewPagerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllUsers extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;



    public AllUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_users, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewPager = view.findViewById(R.id.viewpagerUsers);
       // background = (ImageView)findViewById(R.id.backgroundimage);
      //  viewPager.setTag(key);
        setupViewPager(viewPager);

        tabLayout = view.findViewById(R.id.tabsUsers);
        tabLayout.setupWithViewPager(viewPager);
      //  button = findViewById(R.id.onback);

    }
    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());
        adapter.addFragment(new StylistList(),"Stylist");
        adapter.addFragment(new CustomerList(), "Customer");
        viewPager.setAdapter(adapter);
    }
}
