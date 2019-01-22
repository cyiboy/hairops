package com.example.user.hairr.Catergory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.user.hairr.R;
import com.example.user.hairr.Utils.CircleTransform;
import com.squareup.picasso.Picasso;

public class BarbingCategory extends AppCompatActivity {
    ImageView barbingMen,barbingWomen,barbingChildren;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barbing_category);

        barbingMen = (ImageView)findViewById(R.id.imgBarbingMen);
        barbingWomen = (ImageView)findViewById(R.id.imgBarbingWomen);
        barbingChildren =(ImageView)findViewById(R.id.imgBarbingChildren);


        Picasso.with(this).load(R.drawable.men).transform(new CircleTransform())
                .into(barbingMen);

        Picasso.with(this).load(R.drawable.women).transform(new CircleTransform())
                .into(barbingWomen);

        Picasso.with(this).load(R.drawable.children).transform(new CircleTransform())
                .into(barbingChildren);

        barbingMen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarbingCategory.this, MakeupCategory.class));
            }
        });


        barbingWomen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarbingCategory.this, BarbingCategory.class));
            }
        });


        barbingChildren.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BarbingCategory.this, HairDressingCatergory.class));
            }
        });


    }
}
