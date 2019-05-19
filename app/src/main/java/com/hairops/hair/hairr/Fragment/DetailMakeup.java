package com.hairops.hair.hairr.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hairops.hair.hairr.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailMakeup extends Fragment {


    public DetailMakeup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_makeup, container, false);
    }

}
