package com.example.user.hairr;

import android.app.Dialog;
import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class imageviewer {
    String url,title;
    Dialog dialog;
    TextView textView;
    ImageView imageView;
    Context context;

    public imageviewer(Context context) {

        this.context = context;
    }
    public imageviewer(){

    }

    public void startdi(String url, String title){
    dialog = new Dialog(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.setContentView(R.layout.imageviewer);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);

        textView= dialog.findViewById(R.id.title);
        imageView = dialog.findViewById(R.id.imageViewer);

        textView.setText(title);
        Picasso.with(context).load(url).networkPolicy(NetworkPolicy.OFFLINE).into(imageView, new Callback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError() {
                Picasso.with(context)
                        .load(url).into(imageView);

            }
        });

        dialog.show();


    }


}
