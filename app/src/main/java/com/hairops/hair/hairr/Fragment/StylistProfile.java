package com.hairops.hair.hairr.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hairops.hair.hairr.MainActivity;
import com.hairops.hair.hairr.Model.HairStylist;
import com.hairops.hair.hairr.Model.Withdraw;
import com.hairops.hair.hairr.R;
import com.hairops.hair.hairr.UpdateStylistProfile;
import com.hairops.hair.hairr.Utils.CircleTransform;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 */
public class StylistProfile extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference userRef,requestMoney;
    private ImageView userImage;
    private TextView stylistName,stylistBalance,txtUpdateStylistProfile;
    private ProgressDialog dialog;
    private LinearLayout withdraw,logout,help,updateProfile;
    private HairStylist model;
    private String uid;
    private Calendar calendar;
    private SimpleDateFormat simpledateformat;
    private String Date;


    public StylistProfile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stylist_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getContext());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        requestMoney = FirebaseDatabase.getInstance().getReference().child("MoneyRequest");
        userImage = (ImageView)view.findViewById(R.id.profileImageStylist);
        stylistBalance = (TextView)view.findViewById(R.id.walletBalanceSytlist);
        txtUpdateStylistProfile = (TextView)view.findViewById(R.id.txtUpdateStylistProfile);
        stylistName = (TextView)view.findViewById(R.id.nameOfUserStylist);
        logout = (LinearLayout) view.findViewById(R.id.logoutStylist);
        help = (LinearLayout) view.findViewById(R.id.txtHelpStylist);
        updateProfile = (LinearLayout) view.findViewById(R.id.updateProfileStylist);
        withdraw = (LinearLayout) view.findViewById(R.id.btnWithdrawStylist);

        withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestForWithdrawer();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showDialog();

            }
        });


        txtUpdateStylistProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), UpdateStylistProfile.class));
            }
        });
        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODo create update page for update profile

                startActivity(new Intent(getContext(), UpdateStylistProfile.class));
            }
        });
        
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Logging you out...please wait", Toast.LENGTH_SHORT).show();
                auth.signOut();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        });

        fetchData();

    }

    private void showDialog() {
        String[] help = {"Call us 08176346026", "Mail us Hairoperators@gmail.com"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Help");
        builder.setItems(help, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                switch(which) {
                    case 0:
                        String uri = "tel:" +"08176346026";
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                        break;
                    case 1:

                        sendEmail();
                        break;
                    default:

                }
            }
        });
        builder.show();
    }

    private void sendEmail() {
        Log.i("Send email", "");

        String[] TO = {"Authorichie@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Your subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            getActivity().finish();

        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(),
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void requestForWithdrawer() {
        dialog.setMessage("Requesting Money Withdrawal..please wait");
        dialog.show();
        calendar = Calendar.getInstance();
        simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
        Date = simpledateformat.format(calendar.getTime());

        double a = model.getBalance();
        double b = a - 200;
        Withdraw withdraw  = new Withdraw();
        withdraw.setBalance(String.valueOf(model.getBalance()));
        withdraw.setAmount(String.valueOf(model.getBalance()));
        withdraw.setBankAccountName(model.getBankAccountName());
        withdraw.setBankAmountNumber(model.getBankAccountNumber());
        withdraw.setDate(Date);
        withdraw.setBankName(model.getBankName());
        withdraw.setStatus("unpaid");
        withdraw.setName(model.getName());
        withdraw.setUid(uid);

        requestMoney.push().setValue(withdraw).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(getContext(), "Request sent and 80% of your desired withdraw amount will be available to you between an interval of two to three hours ", Toast.LENGTH_LONG).show();

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fetchData() {
        dialog.setMessage("Fetching your data..please wait");
        dialog.show();

        uid = auth.getCurrentUser().getUid();

        userRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    dialog.dismiss();
                    model = dataSnapshot.getValue(HairStylist.class);

                    stylistBalance.setText(String.valueOf(model.getBalance()));
                    stylistName.setText(model.getName());

                    Picasso.with(getContext()).load(model.getImageUrl()).transform(new CircleTransform()) .networkPolicy(NetworkPolicy.OFFLINE).into(userImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            Picasso.with(getContext())
                                    .load(model.getImageUrl()).into(userImage);

                        }
                    });

                }else {
                    Toast.makeText(getContext(), "could not fetch your data please try again", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
