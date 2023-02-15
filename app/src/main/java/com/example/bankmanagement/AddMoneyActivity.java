package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddMoneyActivity extends AppCompatActivity {

    private TextView textViewAdMny;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoney);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textViewAdMny = findViewById(R.id.textview_ad_mny);

        textViewAdMny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddMoneyActivity.this,CreditCardActivity.class);
                startActivity(intent);
            }
        });
    }
}
