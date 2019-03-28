package com.example.user.hairr.Fragment;


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

import com.example.user.hairr.Model.Customer;
import com.example.user.hairr.Model.HairStylist;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class StylistList extends Fragment {


    FirebaseAuth auth;
    private RecyclerView stylistRv;
    private DatabaseReference mUsersDatabase;
    private LinearLayoutManager mLayoutManager;
    private Query stylistQuery;
    private Customer customer;


    public StylistList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stylist_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        stylistRv = (RecyclerView) view.findViewById(R.id.rvAllStylist);
        stylistQuery = mUsersDatabase.orderByChild("status").equalTo("stylist");

        mLayoutManager = new LinearLayoutManager(getContext());
        stylistRv.setHasFixedSize(true);
        stylistRv.setLayoutManager(mLayoutManager);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAdapter();
    }

    @Override
    public void onStart() {
        super.onStart();
        initAdapter();
    }

    private void initAdapter() {
        FirebaseRecyclerAdapter<HairStylist,AllStylistViewHolder> adapter = new FirebaseRecyclerAdapter<HairStylist, AllStylistViewHolder>(
                HairStylist.class,
                R.layout.single_user,
                AllStylistViewHolder.class,
                stylistQuery
        ) {
            @Override
            protected void populateViewHolder(AllStylistViewHolder viewHolder, HairStylist model, int position) {

                viewHolder.setDisplayName(model.getName());
                viewHolder.setUserImage(model.getImageUrl(),getContext());

            }
        };
        stylistRv.setAdapter(adapter);
    }

    public static class AllStylistViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView userImage;
        TextView name;


        public AllStylistViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            userImage = (ImageView) mView.findViewById(R.id.allUsersImage);
            name = (TextView)mView.findViewById(R.id.allUserName);

        }

        public void setDisplayName(String sname){

            name.setText(sname);

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
