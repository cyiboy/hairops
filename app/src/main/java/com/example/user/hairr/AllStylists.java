package com.example.user.hairr;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Model.AdminBooking;
import com.example.user.hairr.Model.Booking;
import com.example.user.hairr.Model.BookingTransactionModel;
import com.example.user.hairr.Model.Customer;
import com.example.user.hairr.Model.HairStylist;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import mehdi.sakout.fancybuttons.FancyButton;

public class AllStylists extends AppCompatActivity {
    FirebaseAuth auth;
    private RecyclerView stylistRv;
    private DatabaseReference mUsersDatabase,bookingRef,adminBookingRef,transRef;
    private LinearLayoutManager mLayoutManager;
    private Query stylistQuery;
    private String lng,lat,type,numberOfPeople,date,style,spec,specType;
    private Customer customer;
    private int finalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_stylists);

        lng = getIntent().getStringExtra("longitude");
        lat = getIntent().getStringExtra("latitude");
        type = getIntent().getStringExtra("type");
        numberOfPeople = getIntent().getStringExtra("numberOfPerson");
        date = getIntent().getStringExtra("date");
        style = getIntent().getStringExtra("style");
        spec = getIntent().getStringExtra("spec");
        specType = getIntent().getStringExtra("specType");

        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        bookingRef = FirebaseDatabase.getInstance().getReference().child("Bookings");
        adminBookingRef = FirebaseDatabase.getInstance().getReference().child("adminBooking");
        transRef = FirebaseDatabase.getInstance().getReference().child("bookingTransaction");
        stylistRv = (RecyclerView) findViewById(R.id.rvAllStylist);

        mLayoutManager = new LinearLayoutManager(this);

        if (spec.equalsIgnoreCase("Barber")){
            stylistQuery = mUsersDatabase.orderByChild("specialization").equalTo("Barber");
        }else if (spec.equalsIgnoreCase("Hair Stylist")){
            stylistQuery = mUsersDatabase.orderByChild("specialization").equalTo("Hair Stylist");
        }else {
            stylistQuery = mUsersDatabase.orderByChild("specialization").equalTo("Makeup Artist");
        }

        stylistRv.setHasFixedSize(true);
        stylistRv.setLayoutManager(mLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<HairStylist,AllStylistViewHolder> adapter = new FirebaseRecyclerAdapter<HairStylist, AllStylistViewHolder>(

                HairStylist.class,
                R.layout.singleuseritem,
                AllStylistViewHolder.class,
                stylistQuery
        ) {
            @Override
            protected void populateViewHolder(AllStylistViewHolder viewHolder, HairStylist model, int position) {

                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserImage(model.getImageUrl(),AllStylists.this);

                Location loc1 = new Location("");
                loc1.setLatitude(Double.parseDouble(lat));
                loc1.setLongitude(Double.parseDouble(lng));

                Location loc2 = new Location("");
                loc2.setLatitude(Double.parseDouble(model.getLatitude()));
                loc2.setLongitude(Double.parseDouble(model.getLongitude()));

                float distanceInMeters = loc1.distanceTo(loc2);

                viewHolder.setDistance(String.valueOf(distanceInMeters)+" meters from you");

                viewHolder.viewDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(AllStylists.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.singlebookitem);
                        final ImageView stylistImage = dialog.findViewById(R.id.imgBookStylist);
                        TextView styistName = dialog.findViewById(R.id.txtBookStylistName);
                        TextView stylistSpacialization = dialog.findViewById(R.id.txtBookStyleOrdered);
                        TextView dateOrdered = dialog.findViewById(R.id.txtBookDate);
                        TextView Total = dialog.findViewById(R.id.txtBookTotalPrice);
                        TextView typeOrdered = dialog.findViewById(R.id.txtBookType);
                        TextView stylistAddress = dialog.findViewById(R.id.txtBookStylistAddress);
                        TextView  numberOfP = dialog.findViewById(R.id.txtBookNumberOfPeople);
                        FancyButton button = dialog.findViewById(R.id.btnBook);

                        Picasso.with(AllStylists.this).load(model.getImageUrl()).transform(new CircleTransform()).into(stylistImage);

                        styistName.setText("Stylist name : "+model.getName());
                        stylistSpacialization.setText("Specialization : "+model.getSpecialization());
                        stylistAddress.setText("Address : "+model.getAddress());

                        dateOrdered.setText("Date picked : "+date);
                        typeOrdered.setText("Type ordered : "+type);
                        numberOfP.setText("Number of people : "+numberOfPeople);

                        if (spec.equalsIgnoreCase("Barber")){
                            if (type.equalsIgnoreCase("Male")){
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 1000;
                                finalPrice = price + 500;


                            }else if (type.equalsIgnoreCase("Female")){
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 1000;
                                finalPrice = price + 500;

                            }else {
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 700;
                                finalPrice = price + 500;

                            }
                        }else if (spec.equalsIgnoreCase("Makeup Artist")){
                            if (specType.equalsIgnoreCase("normal")){
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 1000;
                                finalPrice = price + 500;

                            }else {
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 25000;
                                finalPrice = price + 500;

                            }
                        }else {

                            if (specType.equalsIgnoreCase("braiding")){
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 3000;
                                finalPrice = price + 500;

                            }else {
                                int number = Integer.parseInt(numberOfPeople);
                                int price = number * 3000;
                                finalPrice = price + 500;

                            }

                        }

                        Total.setText("Total amount: N"+String.valueOf(finalPrice));

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                String uid = auth.getCurrentUser().getUid();

                                mUsersDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            customer = dataSnapshot.getValue(Customer.class);


                                           if (customer.getBalance() >= finalPrice){
                                               DatabaseReference book = bookingRef.push();
                                               String key = book.getKey();
                                               Booking booking = new Booking();
                                               booking.setAddressOfStylist(model.getAddress());
                                               booking.setCustomerName(customer.getName());
                                               booking.setCustomerNumber(customer.getNumber());
                                               booking.setDate(date);
                                               booking.setLatitude(lat);
                                               booking.setLongitude(lng);
                                               booking.setNameOfStylist(model.getName());
                                               booking.setNumberOfPeople(numberOfPeople);
                                               booking.setBookingKey(key);
                                               booking.setStatus("Unconfirmed");
                                               booking.setStatusClient("Not Started");
                                               booking.setStatusStylist("Not Started");
                                               booking.setStyle(style);
                                               booking.setClientImageUrl(customer.getImageUrl());
                                               booking.setStylistImageUrl(model.getImageUrl());
                                               booking.setStylistUid(model.getUid());
                                               booking.setCustomerUid(customer.getUid());
                                               booking.setType(type);
                                               booking.setPrice(String.valueOf(finalPrice));
                                               booking.setNumberOfStylist(model.getNumber());

                                               book.setValue(booking)
                                                       .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<Void> task) {
                                                               if (task.isSuccessful()){

                                                                   AdminBooking adminBooking = new AdminBooking();
                                                                   adminBooking.setBookingKey(key);
                                                                   adminBooking.setClientName(customer.getName());
                                                                   adminBooking.setClientNumber(customer.getNumber());
                                                                   adminBooking.setNumberOfPeople(numberOfPeople);
                                                                   adminBooking.setServiceBooked(spec);
                                                                   adminBooking.setStylistName(model.getName());
                                                                   adminBooking.setStylistNumber(model.getNumber());
                                                                   adminBooking.setTotalAmount(String.valueOf(finalPrice));
                                                                   adminBooking.setClientImageUrl(customer.getImageUrl());
                                                                   adminBooking.setStylistImageUrl(model.getImageUrl());
                                                                   adminBooking.setStyle(style);
                                                                   adminBooking.setStylistUid(model.getUid());
                                                                   adminBooking.setStatus("Unconfirmed");


                                                                   adminBookingRef.push().setValue(adminBooking)
                                                                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                               @Override
                                                                               public void onComplete(@NonNull Task<Void> task) {

                                                                                   if (task.isSuccessful()){

                                                                                       BookingTransactionModel bookingTransactionModel  = new BookingTransactionModel();
                                                                                       bookingTransactionModel.setBookingKey(key);

                                                                                       transRef.child(uid).push().setValue(bookingTransactionModel)
                                                                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                   @Override
                                                                                                   public void onComplete(@NonNull Task<Void> task) {
                                                                                                       if (task.isSuccessful()){
                                                                                                           Toast.makeText(AllStylists.this, "Your barbing has been placed, go to the transaction tab to review it", Toast.LENGTH_SHORT).show();
                                                                                                           dialog.dismiss();
                                                                                                           startActivity(new Intent(AllStylists.this,HomeCustomer.class));
                                                                                                       }
                                                                                                   }
                                                                                               }).addOnFailureListener(new OnFailureListener() {
                                                                                           @Override
                                                                                           public void onFailure(@NonNull Exception e) {
                                                                                               dialog.dismiss();
                                                                                               Toast.makeText(AllStylists.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                                           }
                                                                                       });

                                                                                   }

                                                                               }
                                                                           }).addOnFailureListener(new OnFailureListener() {
                                                                       @Override
                                                                       public void onFailure(@NonNull Exception e) {
                                                                           Toast.makeText(AllStylists.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                       }
                                                                   });

                                                               }
                                                           }
                                                       }).addOnFailureListener(new OnFailureListener() {
                                                   @Override
                                                   public void onFailure(@NonNull Exception e) {
                                                       Toast.makeText(AllStylists.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                   }
                                               });


                                           }else {
                                               Toast.makeText(AllStylists.this, "Insufficient Funds, please fund your wallet with the required amount and try again", Toast.LENGTH_SHORT).show();
                                               return;
                                           }



                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }
                        });



                        dialog.show();
                    }
                });

                viewHolder.viewInMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent goToMap = new Intent(AllStylists.this,MapsActivity.class);
                        goToMap.putExtra("myLat",lat);
                        goToMap.putExtra("myLng",lng);
                        goToMap.putExtra("stylistLat",model.getLatitude());
                        goToMap.putExtra("stylistLng",model.getLongitude());
                        goToMap.putExtra("name",model.getName());
                        startActivity(goToMap);
                    }
                });


            }
        };
        stylistRv.setAdapter(adapter);
    }

    public static class AllStylistViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        Button viewDetails,viewInMap;
        TextView name,occupation,distance;


        public AllStylistViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);
            userImage = (ImageView) mView.findViewById(R.id.allStylistProfileImage);
            name = (TextView)mView.findViewById(R.id.allStylistName);
            occupation = (TextView)mView.findViewById(R.id.allStylistOccupation);
            distance = (TextView)mView.findViewById(R.id.allStylistDistance);
            viewDetails = (Button)mView.findViewById(R.id.btnViewDetails);
            viewInMap = (Button)mView.findViewById(R.id.btnViewInMap);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }


        public void setDistance(String name){

            distance.setText(name);

        }


        public void setOccupation(String name){

            occupation.setText(name);

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
