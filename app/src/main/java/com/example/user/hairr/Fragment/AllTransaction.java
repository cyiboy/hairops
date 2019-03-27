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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Model.AdminBooking;
import com.example.user.hairr.Model.StylistBookingModel;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllTransaction extends Fragment {

    FirebaseAuth auth;
    private RecyclerView requestedMoneyRv;
    private DatabaseReference mUsersDatabase,adminBookingRef,bookingRef;
    private LinearLayoutManager mLayoutManager;
    private String imageUrl,spec,key;


    public AllTransaction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        adminBookingRef = FirebaseDatabase.getInstance().getReference().child("adminBooking");
        bookingRef = FirebaseDatabase.getInstance().getReference().child("stylistBookings");
        requestedMoneyRv = (RecyclerView) view.findViewById(R.id.rvAdminTransactions);

        mLayoutManager = new LinearLayoutManager(getContext());

        requestedMoneyRv.setHasFixedSize(true);
        requestedMoneyRv.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<AdminBooking,AllStylistViewHolder> adapter = new FirebaseRecyclerAdapter<AdminBooking, AllStylistViewHolder>(
                AdminBooking.class,
                R.layout.single_admin_transaction,
                AllStylistViewHolder.class,
                adminBookingRef
        ) {
            @Override
            protected void populateViewHolder(AllStylistViewHolder viewHolder, AdminBooking model, int position) {

                String key = getRef(position).getKey();

                viewHolder.setAhortDes(model.getClientName()+" booked "+model.getServiceBooked()+ " for "+model.getNumberOfPeople()+ " of person(s)");
                viewHolder.setDisplayName(model.getClientName());
                viewHolder.setStylistaName(model.getStylistName());
                viewHolder.setUserImage(model.getClientImageUrl(),getContext());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String uid = auth.getCurrentUser().getUid();

                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.single_booking_details);

                        TextView clientName = dialog.findViewById(R.id.clientNameDetails);
                        TextView clientNumber = dialog.findViewById(R.id.clientNumberDetails);
                        TextView numberOfPerson = dialog.findViewById(R.id.numberOFPeopleBookedFor);
                        TextView stylistName = dialog.findViewById(R.id.stylistNameDetail);
                        TextView stylistNumber = dialog.findViewById(R.id.stylistNumberDetail);
                        TextView totalAmount = dialog.findViewById(R.id.totalAmountDetail);
                        TextView status = dialog.findViewById(R.id.bookingStatus);

                        FancyButton button = dialog.findViewById(R.id.btnConfirmBooking);


                        clientName.setText(model.getClientName());
                        clientNumber.setText(model.getClientNumber());
                        numberOfPerson.setText("You booked for "+model.getNumberOfPeople()+" of person(s)");
                        stylistName.setText(model.getStylistName());
                        stylistNumber.setText(model.getStylistNumber());
                        totalAmount.setText(model.getTotalAmount());
                        status.setText(model.getStatus());


                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                button.setEnabled(false);
                                Toast.makeText(getContext(), "Confirming booking.  please wait", Toast.LENGTH_SHORT).show();
                                adminBookingRef.child(key).child("status").setValue("Confirmed")
                                        .addOnCompleteListener(task -> {
                                            StylistBookingModel stylistBookingModel = new StylistBookingModel();

                                            stylistBookingModel.setBookingKey(model.getBookingKey());
                                            stylistBookingModel.setClientImageUrl(model.getClientName());
                                            stylistBookingModel.setClientName(model.getClientName());
                                            stylistBookingModel.setStyle(model.getStyle());

                                            bookingRef.child(model.getStylistUid()).push().setValue(stylistBookingModel)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            dialog.dismiss();
                                                            // TODO send notification here

                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {

                                                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                            }
                        });

                        dialog.show();


                    }
                });


            }
        };

        requestedMoneyRv.setAdapter(adapter);
    }

    public static class AllStylistViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name,stylistaName,ahortDes;


        public AllStylistViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);
            userImage = (ImageView) mView.findViewById(R.id.CustomerBookingImage);
            name = (TextView)mView.findViewById(R.id.customerBookingName);
            stylistaName = (TextView)mView.findViewById(R.id.stylistNameCustomerBooking);
            ahortDes = (TextView)mView.findViewById(R.id.swshortDescriptionCustomerBooking);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }


        public void setStylistaName(String name){

            stylistaName.setText(name);

        }


        public void setAhortDes(String name){

            ahortDes.setText(name);

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
