package com.example.user.hairr.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.user.hairr.Catergory.BarbingCategory;
import com.example.user.hairr.Catergory.HairDressingCatergory;
import com.example.user.hairr.Catergory.MakeupCategory;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.squareup.picasso.Picasso;


/**
 * A simple {@link Fragment} subclass.
 */
public class customerBooking extends Fragment {

    ImageView barbing,makeup,hairdressing;


    public customerBooking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        makeup = (ImageView)view.findViewById(R.id.imgMakeup);
        barbing = (ImageView)view.findViewById(R.id.imgBarbing);
        hairdressing =(ImageView)view.findViewById(R.id.imgHairDressing);


        Picasso.with(getContext()).load(R.drawable.barbing).transform(new CircleTransform())
                .into(barbing);

        Picasso.with(getContext()).load(R.drawable.hairdressing).transform(new CircleTransform())
                .into(hairdressing);

        Picasso.with(getContext()).load(R.drawable.makeup).transform(new CircleTransform())
                .into(makeup);

        makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), MakeupCategory.class));
            }
        });


        barbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), BarbingCategory.class));
            }
        });


        hairdressing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), HairDressingCatergory.class));
            }
        });



    }
}
