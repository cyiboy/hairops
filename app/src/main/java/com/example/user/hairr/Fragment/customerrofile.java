package com.example.user.hairr.Fragment;


import android.app.AlertDialog;
import android.app.Dialog;
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
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.hairr.MainActivity;
import com.example.user.hairr.Model.Customer;
import com.example.user.hairr.Model.MoneyTransaction;
import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
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

import co.paystack.android.Paystack;
import co.paystack.android.PaystackSdk;
import co.paystack.android.model.Card;
import co.paystack.android.model.Charge;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class customerrofile extends Fragment {

    private FirebaseAuth auth;
    private DatabaseReference userRef,fundingTrans;
    private ImageView userImage;
    private LinearLayout logout,help,updateProfile,fundWallet;
    TextView stylistName,stylistBalance;
    private ProgressDialog dialog;
    private  Dialog dialogs;
    private Calendar calendar;
    private SimpleDateFormat simpledateformat;
    private String Date;

    private Customer model;
    private String uid;


    public customerrofile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_customerrofile, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(getContext());
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        fundingTrans = FirebaseDatabase.getInstance().getReference().child("Funding Transaction");
        userImage = (ImageView)view.findViewById(R.id.profileImage);
        stylistBalance =  view.findViewById(R.id.walletBalanceUser);
        stylistName =  view.findViewById(R.id.nameOfUser);
       // stylistAddress = (TextView)view.findViewById(R.id.addressOfUser);
       // stylistSpec = (TextView)view.findViewById(R.id.specializationOfUser);
        logout =  view.findViewById(R.id.logoutUser);
        help =  view.findViewById(R.id.txtHelpUser);
        fundWallet =  view.findViewById(R.id.btnFundWallet);
        updateProfile = view.findViewById(R.id.updateProfileUser);
      //  withdraw = (FancyButton)view.findViewById(R.id.btnFundWallet);

        fundWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fundWallet();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDialog();

            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODo create update page for update profile
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

    private void fundWallet() {

        dialogs = new Dialog(getContext());
        dialogs.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogs.setContentView(R.layout.fundwallet);
        EditText cardNumber = dialogs.findViewById(R.id.edtCardNumber);
        EditText cardExpireMonth = dialogs.findViewById(R.id.edtExpireMonth);
        EditText cardExpireYear = dialogs.findViewById(R.id.edtExpireYear);
        EditText cardCVC = dialogs.findViewById(R.id.edtCVC);
        EditText amount = dialogs.findViewById(R.id.edtAmount);
        FancyButton button = dialogs.findViewById(R.id.close);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String number = cardNumber.getText().toString().trim();
               String month = cardExpireMonth.getText().toString().trim();
               String year = cardExpireYear.getText().toString().trim();
               String cvc = cardCVC.getText().toString().trim();
               String amoun = amount.getText().toString().trim();

               startTrans(number,month,year,cvc,amoun);
            }
        });


    }

    private void startTrans(String number, String month, String year, String cvc, String amoun) {

        dialog.show();
        dialog.setMessage("Funding Wallet, please wait....");
        dialog.setCancelable(false);
        dialog.show();
        String email = auth.getCurrentUser().getEmail();
        int expiryMonth = Integer.parseInt(month); //any month in the future
        int expiryYear = Integer.parseInt(year);
        Charge charge = new Charge();


        Card card = new Card(number, expiryMonth, expiryYear, cvc);

        if (card.isValid()){
            //create a Charge object
            //set the card to charge
            charge.setCard(card);

            charge.setEmail(model.getEmail()); //dummy email address

            charge.setAmount(Integer.parseInt(amoun)); //test amount
        }

        PaystackSdk.chargeCard(getActivity(), charge, new Paystack.TransactionCallback() {
            @Override
            public void onSuccess(co.paystack.android.Transaction transaction) {
                calendar = Calendar.getInstance();
                simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
                Date = simpledateformat.format(calendar.getTime());

                MoneyTransaction trans = new MoneyTransaction();

                trans.setAmount(amoun);
                trans.setEmail(model.getEmail());
                trans.setName(model.getName());
                trans.setTransactionRef(transaction.getReference());
                trans.setImageUrl(model.getImageUrl());
                trans.setDate(Date);

                fundingTrans.child(uid).push().setValue(trans)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "Transaction Successful", Toast.LENGTH_LONG).show();
                                    dialogs.dismiss();
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
            public void beforeValidate(co.paystack.android.Transaction transaction) {

            }

            @Override
            public void onError(Throwable error, co.paystack.android.Transaction transaction) {
                dialog.dismiss();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                dialogs.dismiss();

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
                    model = dataSnapshot.getValue(Customer.class);

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

                    stylistName.setText(model.getName());
                    stylistBalance.setText("Wallet Balance : N "+ String.valueOf(model.getBalance()));
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
}
