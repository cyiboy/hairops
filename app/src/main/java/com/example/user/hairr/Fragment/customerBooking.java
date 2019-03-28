package com.example.user.hairr.Fragment;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Catergory.BarbingCategory;
import com.example.user.hairr.Catergory.HairDressingCatergory;
import com.example.user.hairr.Catergory.MakeupCategory;
import com.example.user.hairr.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class customerBooking extends Fragment {

    FirebaseAuth auth;
    CardView barbing, makeup, hairdressing;
    private DatabaseReference mUsersDatabase, postDatabase;
    TextView tname;

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
        barbing = view.findViewById(R.id.bookHairCut);
        makeup = view.findViewById(R.id.bookMakeup);
        hairdressing = view.findViewById(R.id.bookHairDressing);
        tname = view.findViewById(R.id.titleTxt);
        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        String uid = auth.getCurrentUser().getUid();

        mUsersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    tname.setText("Hello "+ dataSnapshot.child("name").getValue().toString());
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "error occurs: " + databaseError.toString(), Toast.LENGTH_SHORT).show();
            }
        });


        barbing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), BarbingCategory.class));
            }
        });
        makeup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), MakeupCategory.class));
            }
        });
        hairdressing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), HairDressingCatergory.class));
            }
        });

    }
}
