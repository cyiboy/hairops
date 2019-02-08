package com.example.user.hairr.Fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.user.hairr.CustomersCompleteReg;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class booking extends Fragment {
    Button btnNext, pickdate, picklocation;
    TextView selectdate, selectlocation;
    private MaterialSpinner spinnertype, spinnerstyle;
    int PLACE_PICKER_REQUEST = 1;
    private DatabaseReference mDatabaseRef;
    private LatLng latLng;
    public String lat, lng,Type,Style,Dated,Time;
    FirebaseAuth mAuth;
    EditText numberOfPeople;
    private Calendar calendar;
    private SimpleDateFormat simpledateformat;

    private static final String[] type = {
            "Male",
            "Female",
            "Children"


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
    public void onViewCreated(@NonNull View views, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(views, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Users");

        spinnertype = (MaterialSpinner)views.findViewById(R.id.selectType);
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

                            }
                        })
                        .curved()
                        .display();
            }
        });





        spinnertype.setItems(type);
        spinnertype.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> Type = item);
        spinnertype.setOnNothingSelectedListener(spinner -> Snackbar.make(spinner, "Nothing selected", Snackbar.LENGTH_LONG).show());


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

    public static class DatePickerFragment extends android.support.v4.app.DialogFragment implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        public void onDateSet(DatePicker view, int year, int month, int day) {

           startTimePicker(year,month,day);
        }

        private void startTimePicker(int year, int month, int day) {
            TimePicker mTimePicker = new TimePicker();
            mTimePicker.show(getFragmentManager(), "Select time");
        }


    }

    public static class TimePicker extends android.support.v4.app.DialogFragment implements TimePickerDialog.OnTimeSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }
        @Override
        public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {

        }
    }
}
