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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.Model.Withdraw;
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

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllBooking extends Fragment {

    FirebaseAuth auth;
    private RecyclerView requestedMoneyRv;
    private DatabaseReference mUsersDatabase,moneyRequestRef;
    private LinearLayoutManager mLayoutManager;
    private String imageUrl,spec,key;



    public AllBooking() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_all_booking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        moneyRequestRef = FirebaseDatabase.getInstance().getReference().child("MoneyRequest");
        requestedMoneyRv = (RecyclerView) view.findViewById(R.id.rvMoneyRequest);

        mLayoutManager = new LinearLayoutManager(getContext());


        requestedMoneyRv.setHasFixedSize(true);
        requestedMoneyRv.setLayoutManager(mLayoutManager);

    }


    @Override
    public void onStart() {
        super.onStart();
        initFirebaseAdapter();
    }

    private void initFirebaseAdapter() {
        FirebaseRecyclerAdapter<Withdraw,WithdrawerViewHolder> adapter = new FirebaseRecyclerAdapter<Withdraw, WithdrawerViewHolder>(
                Withdraw.class,
                R.layout.singlerequestmoneyitem,
                WithdrawerViewHolder.class,
                moneyRequestRef
        ) {
            @Override
            protected void populateViewHolder(WithdrawerViewHolder viewHolder, Withdraw model, int position) {

                key = getRef(position).getKey();
                mUsersDatabase.child(model.getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){
                                    imageUrl = dataSnapshot.child("imageUrl").getValue().toString();
                                    viewHolder.setBalance("Current balance is N "+model.getBalance());
                                    viewHolder.setDate(model.getDate());
                                    viewHolder.setDisplayName(model.getName());
                                    viewHolder.setRequestedAmount("Amount to be withdrawn is N "+model.getAmount());
                                    viewHolder.setUserImage(imageUrl,getContext());

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.singledialogview);
                        final ImageView stylistImage = dialog.findViewById(R.id.requesterImage);
                        TextView styistName = dialog.findViewById(R.id.NameStylist);
                        TextView balance = dialog.findViewById(R.id.userBalance);
                        TextView amount = dialog.findViewById(R.id.neededAmount);
                        TextView stylistSpacialization = dialog.findViewById(R.id.proffession);
                        FancyButton button = dialog.findViewById(R.id.btnPay);


                        if (model.getStatus().equalsIgnoreCase("unpaid")){
                            button.setText("Pay");
                            button.setEnabled(true);
                        }else {
                            button.setText("Paid");
                            button.setEnabled(false);
                        }

                        button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                moneyRequestRef.child(key).child("status").setValue("paid")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    Toast.makeText(getContext(), "Payment Reference saved", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
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

                        Picasso.with(getContext()).load(imageUrl).transform(new CircleTransform()).into(stylistImage);

                        styistName.setText(model.getName());
                        balance.setText("Current balance is N "+model.getBalance());
                        amount.setText("Amount to be withdrawn is N "+model.getAmount());
                        stylistSpacialization.setText(spec);

                        dialog.show();

                    }
                });


            }
        };

        requestedMoneyRv.setAdapter(adapter);
    }

    public static class WithdrawerViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name,balance,requestedAmount,date;


        public WithdrawerViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            // like = (ImageView) mView.findViewById(R.id.imgLike);
            //   comment = (ImageView) mView.findViewById(R.id.imgComment);
            userImage = (ImageView) mView.findViewById(R.id.profileImage);
           name = (TextView)mView.findViewById(R.id.stylistNameRequest);
           balance = (TextView)mView.findViewById(R.id.currentBalance);
           requestedAmount = (TextView)mView.findViewById(R.id.requestedAmount);
           date = (TextView)mView.findViewById(R.id.dateOfRequest);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

        }


        public void setDate(String name){

            date.setText(name);

        }


        public void setRequestedAmount(String name){

            requestedAmount.setText(name);

        }

        public void setBalance(String name){


            balance.setText(name);

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
