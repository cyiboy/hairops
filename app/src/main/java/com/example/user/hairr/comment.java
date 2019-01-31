package com.example.user.hairr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class comment extends AppCompatActivity {
ImageView close,send;
EditText comment;

RecyclerView list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        close.findViewById(R.id.close);
        comment.findViewById(R.id.edtComments);
        send.findViewById(R.id.imgSendComment);
        list.findViewById(R.id.rvComments);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendcomment();

            }
        });
    }

    private void sendcomment() {
        String mainComment= comment.getText().toString().trim();

    }
}
