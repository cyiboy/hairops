package com.example.user.hairr.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.AllStylists;
import com.example.user.hairr.R;
import com.github.florent37.singledateandtimepicker.dialog.SingleDateAndTimePickerDialog;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class makup extends Fragment {
    Button btnNext, pickdate, picklocation;
    TextView selectdate, selectlocation;
    private MaterialSpinner  spinnerstyle;
    int PLACE_PICKER_REQUEST = 1;
    private DatabaseReference mDatabaseRef;
    private LatLng latLng;
    public String lat, lng, Style,Dated,Time;
    FirebaseAuth mAuth;
    EditText numberOfPeople;
    private Calendar calendar;
    private SimpleDateFormat simpledateformat;
    private static final String[] style = {
            "Bridal Makeup",
            "Normal Makeup"

    };
    public makup() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_makup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View views, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(views, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

       // spinnertype = (MaterialSpinner)views.findViewById(R.id.selectType);
        spinnerstyle = (MaterialSpinner)views.findViewById(R.id.selectStyle);
        btnNext = (Button)views.findViewById(R.id.btnNext);
        pickdate = (Button)views.findViewById(R.id.selectTimeAndDate);
        picklocation = (Button)views.findViewById(R.id.selectLocation);
        selectlocation = (TextView)views.findViewById(R.id.locationText);
        selectdate = (TextView)views.findViewById(R.id.dateText);
        numberOfPeople = (EditText) views.findViewById(R.id.edtNumberOfPeople);


        picklocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLocationPicker();
            }
        });

        pickdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SingleDateAndTimePickerDialog.Builder(getContext())
                        .mustBeOnFuture()
                        .title("Enter Booking Date and Time")
                        .displayYears(true)
                        .listener(new SingleDateAndTimePickerDialog.Listener() {
                            @Override
                            public void onDateSelected(Date date) {

                                simpledateformat = new SimpleDateFormat("yyyy.MM.dd HH:mm z");
                                Dated = simpledateformat.format(date);
                                selectdate.setText(Dated);
                                selectdate.setVisibility(View.VISIBLE);
                            }
                        })
                        .curved().mainColor(getResources().getColor(R.color.colorPrimary))
                        .display();
            }
        });


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent stylist = new Intent(getContext(), AllStylists.class);

                stylist.putExtra("style",Style);
                stylist.putExtra("date",Dated);
                stylist.putExtra("longitude",lng);
                stylist.putExtra("spec","Makeup Artist");
                stylist.putExtra("specType","normal");
                stylist.putExtra("latitude",lat);
                stylist.putExtra("numberOfPerson",numberOfPeople.getText().toString().trim());
                getContext().startActivity(stylist);
            }
        });






        spinnerstyle.setItems(style);
        spinnerstyle.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> Style = item);
        spinnerstyle.setOnNothingSelectedListener(spinner -> Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show());


    }

    private void showLocationPicker() {

        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
            startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }


    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, getContext());
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(getContext(), toastMsg, Toast.LENGTH_LONG).show();
                latLng = place.getLatLng();
                lat = String.valueOf(latLng.latitude);
                lng = String.valueOf(latLng.longitude);
            }
        }
    }


}
