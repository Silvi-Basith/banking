package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class sourceofwithdraw extends AppCompatActivity {


    TextView bkash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sourceofwithdraw);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bkash = findViewById(R.id.textview_bkash);

        bkash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(sourceofwithdraw.this, Bkash.class);
                startActivity(intent);
            }
        });
    }
}