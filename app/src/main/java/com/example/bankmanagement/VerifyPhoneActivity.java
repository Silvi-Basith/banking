package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;



public class VerifyPhoneActivity extends AppCompatActivity {

    private EditText editTextOTP;
    private Button buttonVerifyOTP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addmoneyotp);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextOTP = findViewById(R.id.editText_otp);
        buttonVerifyOTP = findViewById(R.id.button_verify_otp);

        buttonVerifyOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCode = editTextOTP.getText().toString().trim();

                if (inputCode.isEmpty()) {
                    Toast.makeText(VerifyPhoneActivity.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = getIntent();
                    int code = intent.getIntExtra("code", 0);

                    if (inputCode.equals(String.valueOf(code))) {
                        Toast.makeText(VerifyPhoneActivity.this, "Success", Toast.LENGTH_SHORT).show();

                        Intent intent1 =new Intent(VerifyPhoneActivity.this,WithdrawActivity.class);
                        startActivity(intent1);
                        finish();

                    } else {
                        Toast.makeText(VerifyPhoneActivity.this, "Incorrect OTP", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
