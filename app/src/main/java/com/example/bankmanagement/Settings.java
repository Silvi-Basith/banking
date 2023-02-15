package com.example.bankmanagement;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle("Settings" );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        TextView textViewupdateemail = findViewById(R.id.textview_email);

        textViewupdateemail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Settings.this, UpdateEmailActivity.class) ;
                startActivity(intent);

            }
        });

        TextView textViewcngpswd = findViewById(R.id.txtvw_pswd);

        textViewcngpswd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Settings.this, ChangePasswordActivity.class) ;
                startActivity(intent);

            }
        });

        TextView textViewDltProfile = findViewById(R.id.txtvw_dlt_profile);

        textViewDltProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Settings.this, DeleteProfileActivity.class) ;
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}