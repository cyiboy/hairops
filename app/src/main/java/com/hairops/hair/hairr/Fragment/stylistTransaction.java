package com.hairops.hair.hairr.Fragment;


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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hairops.hair.hairr.Model.Booking;
import com.hairops.hair.hairr.Model.BookingTransactionModel;
import com.hairops.hair.hairr.Model.Customer;
import com.hairops.hair.hairr.R;
import com.hairops.hair.hairr.Utils.CircleTransform;
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

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class stylistTransaction extends Fragment {
    FirebaseAuth auth;
    private RecyclerView rvStylistTransaction;
    private DatabaseReference customerBookingRef,stylistBookingRef,userRef;
    private LinearLayoutManager mLayoutManager;


    public stylistTransaction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stylist_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        String uid = auth.getCurrentUser().getUid();
        stylistBookingRef = FirebaseDatabase.getInstance().getReference().child("stylistBookings").child(uid);
        customerBookingRef = FirebaseDatabase.getInstance().getReference().child("Bookings");
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        rvStylistTransaction = (RecyclerView) view.findViewById(R.id.rvCustomerStylistTransaction);

        mLayoutManager = new LinearLayoutManager(getContext());

        rvStylistTransaction.setHasFixedSize(true);
        rvStylistTransaction.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<BookingTransactionModel,StylistBookingViewHolder> adapter = new
                FirebaseRecyclerAdapter<BookingTransactionModel, StylistBookingViewHolder>(
                        BookingTransactionModel.class,
                        R.layout.singletransaction,
                        StylistBookingViewHolder.class,
                        stylistBookingRef

                ) {
            @Override
            protected void populateViewHolder(StylistBookingViewHolder viewHolder, BookingTransactionModel model, int position) {

                        customerBookingRef.child(model.getBookingKey())
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            Booking booking = dataSnapshot.getValue(Booking.class);

                                            viewHolder.setStyleBooked(booking.getStyle());
                                            viewHolder.setType(booking.getType());
                                            viewHolder.setUserImage(booking.getClientImageUrl(), getContext());
                                            viewHolder.setDate(booking.getDate());
                                            viewHolder.setStatus(booking.getStatusStylist());
                                            if (booking.getStatusStylist().equalsIgnoreCase("Not Started")){
                                                viewHolder.btnStart.setText("Start");
                                            }else if (booking.getStatusStylist().equalsIgnoreCase("started")){
                                                viewHolder.btnStart.setText("working");
                                            }else {
                                                viewHolder.btnStart.setText("Completed");
                                            }

                                            viewHolder.btnStart.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {


                                                    if (booking.getStatusStylist().equalsIgnoreCase("Not Started")){
                                                        customerBookingRef.child(model.getBookingKey()).child("statusStylist").setValue("Started")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            viewHolder.btnStart.setText("Complete");
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }else if(booking.getStatusStylist().equalsIgnoreCase("Started")){
                                                        customerBookingRef.child(model.getBookingKey()).child("statusStylist").setValue("Finished")
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()){
                                                                            viewHolder.btnStart.setText("Finished");
                                                                            if (booking.getStatusStylist().equalsIgnoreCase(booking.getStatusClient())){

                                                                                userRef.child(booking.getStylistUid()).child("balance").setValue(booking.getPrice())
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()){


                                                                                                    userRef.child(booking.getCustomerUid()).
                                                                                                            addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                    Customer customer = dataSnapshot.getValue(Customer.class);

                                                                                                                    double balance = customer.getBalance();

                                                                                                                    double newAmount = balance - Double.parseDouble(booking.getPrice());

                                                                                                                    String b = String.valueOf(newAmount);

                                                                                                                    userRef.child(booking.getCustomerUid()).child("balance").setValue(b)
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()){
                                                                                                                                        Toast.makeText(getContext(), "Wallet credited with :"+booking.getPrice(), Toast.LENGTH_SHORT).show();

                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                                                                        @Override
                                                                                                                        public void onFailure(@NonNull Exception e) {
                                                                                                                            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                                                        }
                                                                                                                    });





                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                }
                                                                                                            });
                                                                                                }
                                                                                            }
                                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {

                                                                                    }
                                                                                });

                                                                            }else {
                                                                                Toast.makeText(getContext(), "Seems like the customer has not yet finished the transaction, please kindly let the customer know you are done.", Toast.LENGTH_SHORT).show();

                                                                            }
                                                                        }
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });


                                                    }else {

                                                        Toast.makeText(getContext(), "Task already finished", Toast.LENGTH_SHORT).show();
                                                        return;

                                                    }


                                                }
                                            });

                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

//                                        Toast.makeText(getContext(),databaseError.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

            }
        };
        rvStylistTransaction.setAdapter(adapter);
    }

    public static class StylistBookingViewHolder extends RecyclerView.ViewHolder{
        View mView;
        ImageView userImage;
        TextView type,styleBooked,date,status;
        FancyButton btnStart;

        public StylistBookingViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userImage = (ImageView) mView.findViewById(R.id.imgClientBooked);
            type = (TextView)mView.findViewById(R.id.txtTypeBooked);
            styleBooked = (TextView)mView.findViewById(R.id.txtStyleBooked);
            date = (TextView)mView.findViewById(R.id.dateOfService);
            status = (TextView)mView.findViewById(R.id.txtStatus);

            btnStart = (FancyButton)mView.findViewById(R.id.btnStartService);

        }

        public void setType(String sname){

            type.setText(sname);

        }


        public void setDate(String name){

            styleBooked.setText(name);

        }


        public void setStatus(String name){

            status.setText(name);

        }


        public void setStyleBooked(String name){

            date.setText(name);

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
