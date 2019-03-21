package com.example.user.hairr.Fragment;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Model.Booking;
import com.example.user.hairr.Model.BookingTransactionModel;
import com.example.user.hairr.Model.StylistBookingModel;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class stylistBookings extends Fragment {

    FirebaseAuth auth;
    private RecyclerView rvStylistBooking;
    private DatabaseReference stylistBookingRef,customerBookingRef,transRef;
    private LinearLayoutManager mLayoutManager;
    private String imageUrl,spec,key;


    public stylistBookings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stylist_bookings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        stylistBookingRef = FirebaseDatabase.getInstance().getReference().child("stylistBookings").child(uid);
        customerBookingRef = FirebaseDatabase.getInstance().getReference().child("Bookings");
        transRef = FirebaseDatabase.getInstance().getReference().child("bookingTransaction");
        rvStylistBooking = (RecyclerView) view.findViewById(R.id.rvStylistBooking);

        mLayoutManager = new LinearLayoutManager(getContext());
        mLayoutManager.setReverseLayout(true);

        rvStylistBooking.setHasFixedSize(true);
        rvStylistBooking.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {

        FirebaseRecyclerAdapter<StylistBookingModel,StylistBookingViewHolder> adapter  = new
                FirebaseRecyclerAdapter<StylistBookingModel, StylistBookingViewHolder>(
                        StylistBookingModel.class,
                        R.layout.single_stylist_booking,
                        StylistBookingViewHolder.class,
                        stylistBookingRef
                ) {
                    @Override
                    protected void populateViewHolder(StylistBookingViewHolder viewHolder, StylistBookingModel model, int position) {

                        viewHolder.setDisplayName(model.getClientName());
                        viewHolder.setUserImage(model.getClientImageUrl(),getContext());
                        viewHolder.setStyleBooked("Booked for you to " + model.getStyle());

                        String uid = auth.getCurrentUser().getUid();
                        String key = getRef(position).getKey();

                        viewHolder.mView.setOnClickListener(view ->
                                customerBookingRef.child(model.getBookingKey())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()){
                                            Booking booking = dataSnapshot.getValue(Booking.class);

                                            final Dialog dialog = new Dialog(getContext());
                                            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                            dialog.setContentView(R.layout.single_stylist_accept);

                                            TextView clientName = (TextView)dialog.findViewById(R.id.clientNameDetails);
                                            TextView clientNumber = (TextView)dialog.findViewById(R.id.clientNumberDetails);
                                            TextView numberOfPerson = (TextView)dialog.findViewById(R.id.numberOFPeopleBookedFor);
                                            TextView typeBooked = (TextView)dialog.findViewById(R.id.typeBooked);
                                            TextView dateBooked = (TextView)dialog.findViewById(R.id.dateBOoked);
                                            TextView amountTotal = (TextView)dialog.findViewById(R.id.totalAmountDetail);
                                            TextView bookingStyle = (TextView)dialog.findViewById(R.id.bookingStyle);

                                            FancyButton showLocation = (FancyButton)dialog.findViewById(R.id.btnViewAddressInMap);
                                            FancyButton acceptBooking = (FancyButton)dialog.findViewById(R.id.btnAcceptBooking);
                                            FancyButton rejectBooking = (FancyButton)dialog.findViewById(R.id.btnRejectJob);


                                            clientName.setText(booking.getCustomerName());
                                            clientNumber.setText(booking.getCustomerNumber());
                                            numberOfPerson.setText(booking.getNumberOfPeople());
                                            typeBooked.setText(booking.getType());
                                            dateBooked.setText(booking.getDate());
                                            amountTotal.setText(booking.getPrice());
                                            bookingStyle.setText(booking.getStyle());


                                            showLocation.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                }
                                            });


                                            acceptBooking.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    acceptBooking.setEnabled(false);

                                                    customerBookingRef.child(model.getBookingKey()).child("status").setValue("Confirmed")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()){
                                                                        acceptBooking.setEnabled(true);
                                                                        BookingTransactionModel bookingTransactionModel = new BookingTransactionModel();
                                                                        bookingTransactionModel.setBookingKey(model.getBookingKey());

                                                                        transRef.child(uid).push().setValue(bookingTransactionModel)
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()){
                                                                                            Toast.makeText(getContext(), "Successfully accepted the booking, check your transaction tab to start it", Toast.LENGTH_SHORT).show();
                                                                                            dialog.dismiss();
                                                                                        }
                                                                                    }
                                                                                }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                acceptBooking.setEnabled(true);
                                                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                                }
                                            });

                                            rejectBooking.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    rejectBooking.setEnabled(false);
                                                        stylistBookingRef.child(uid).child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()){
                                                                    rejectBooking.setEnabled(true);
                                                                    Toast.makeText(getContext(), "Rejected the offer", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                rejectBooking.setEnabled(true);
                                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                }
                                            });

                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        Toast.makeText(getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                }));


                    }
                };
        rvStylistBooking.setAdapter(adapter);

    }

    public static class StylistBookingViewHolder extends RecyclerView.ViewHolder{
        View mView;
        CircleImageView userImage;
        TextView name,styleBooked;

        public StylistBookingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userImage = (CircleImageView)mView.findViewById(R.id.customerStylistBookingImage);
            name = (TextView)mView.findViewById(R.id.customerStylistBookingName);
            styleBooked = (TextView)mView.findViewById(R.id.customerStyleBooking);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }


        public void setStyleBooked(String name){

            styleBooked.setText(name);

        }






        public void setUserImage(String status, Context context){

            Picasso.with(context).load(status).transform(new CircleTransform()) .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(status).into(userImage);

                }
            });



        }

    }
}
