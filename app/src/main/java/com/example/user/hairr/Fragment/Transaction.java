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

import com.example.user.hairr.Model.MoneyTransaction;
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
public class Transaction extends Fragment {

    FirebaseAuth auth;
    private RecyclerView fundTrans;
    private DatabaseReference FundsRef;
    private LinearLayoutManager mLayoutManager;
    private String imageUrl,name,key;


    public Transaction() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        FundsRef = FirebaseDatabase.getInstance().getReference().child("Funding Transaction");
        fundTrans = (RecyclerView) view.findViewById(R.id.rvFundTrans);

        mLayoutManager = new LinearLayoutManager(getContext());

        fundTrans.setHasFixedSize(true);
        fundTrans.setLayoutManager(mLayoutManager);



    }

    @Override
    public void onStart() {
        super.onStart();
        initFirebaseAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();
        initFirebaseAdapter();
    }

    private void initFirebaseAdapter() {
        FirebaseRecyclerAdapter<MoneyTransaction,WithdrawerViewHolder> adapter = new FirebaseRecyclerAdapter<MoneyTransaction, WithdrawerViewHolder>(
                MoneyTransaction.class,
                R.layout.singlerequestmoneyitem,
                WithdrawerViewHolder.class,
                FundsRef
        ) {
            @Override
            protected void populateViewHolder(WithdrawerViewHolder viewHolder, MoneyTransaction model, int position) {

                key = getRef(position).getKey();


                viewHolder.setBalance("Amount Funded is N "+model.getAmount());
                viewHolder.setDate(model.getDate());
                viewHolder.setDisplayName(model.getName());
                viewHolder.setRequestedAmount("Payment Reference is "+model.getTransactionRef());
                viewHolder.setUserImage(model.getImageUrl(),getContext());

            }
        };

        fundTrans.setAdapter(adapter);
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
