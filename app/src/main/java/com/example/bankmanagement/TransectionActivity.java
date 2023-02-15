package com.example.bankmanagement;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class TransectionActivity extends Activity {

    private TextView desco;
    private TextView link3;
    private TextView akash;
    private TextView titas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transection);

        desco = findViewById(R.id.textview_desco);
        link3 = findViewById(R.id.txtvw_link3);
        akash = findViewById(R.id.txtvw_akash);
        titas = findViewById(R.id.txtvw_titas);

        desco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransectionActivity.this, DescoActivity.class);
                startActivity(intent);
            }
        });

        link3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransectionActivity.this, Link3Activity.class);
                startActivity(intent);
            }
        });

        akash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransectionActivity.this, AkashActivity.class);
                startActivity(intent);
            }
        });

        titas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TransectionActivity.this, TitasActivity.class);
                startActivity(intent);
            }
        });
    }
}
