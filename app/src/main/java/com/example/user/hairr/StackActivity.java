package com.example.user.hairr;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;



public class StackActivity extends AppCompatActivity {

    ImageView stylist , customer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);

        stylist = (ImageView) findViewById(R.id.circleimageHairStylist);
        customer = (ImageView) findViewById(R.id.circleimageNormalUser);

        stylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StackActivity.this, "you clicked stylist", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StackActivity.this,HairStylistCompleteReg.class));
            }
        });


        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(StackActivity.this, "you clicked customer", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(StackActivity.this,CustomersCompleteReg.class));
            }
        });
    }
}
