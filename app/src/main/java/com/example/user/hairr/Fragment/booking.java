package com.example.user.hairr.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.user.hairr.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.w3c.dom.Text;

/**
 * A simple {@link Fragment} subclass.
 */
public class booking extends Fragment {
Button button, pickdate,picklocation;
Text selectdate, selectlocation;
    private MaterialSpinner spinnertype,spinnerstyle;
    private DatabaseReference mDatabaseRef;
    private LatLng latLng;
    private String lat,lng;
    FirebaseAuth mAuth;

    private static final String[] type = {
            "Male",
            "Female",
            "Childern"


    };

    private static final String[] style = {
            "Low cut",
            "skin cut",
            "afro cut",
            "ponk",
            "black niterbacar",
            "Round cut",
            "gallas"

    };
    public booking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");
    }
}
