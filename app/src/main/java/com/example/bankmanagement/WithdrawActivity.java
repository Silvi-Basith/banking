package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class WithdrawActivity extends AppCompatActivity {

    private TextView mBalanceText;
    private EditText mWithdrawAmount;
    private Button mWithdrawButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        mBalanceText = findViewById(R.id.balance_text);
        mWithdrawAmount = findViewById(R.id.withdraw_amount);
        mWithdrawButton = findViewById(R.id.withdraw_button);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("BALANCE").child(mAuth.getCurrentUser().getUid());
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("balance")) {
                    double balance = dataSnapshot.child("balance").getValue(Double.class);
                    mBalanceText.setText(String.format("Current Balance: $%.2f", balance));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mWithdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String withdrawAmountString = mWithdrawAmount.getText().toString().trim();

                if (!TextUtils.isEmpty(withdrawAmountString)) {
                    Double withdraw = Double.parseDouble(withdrawAmountString);
                    if (withdraw < 500 || withdraw > 2000) {
                        Toast.makeText(WithdrawActivity.this, "Withdraw amount must be between $500 and $2000", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Double balance = 0.0;
                            if (dataSnapshot.hasChild("balance")) {
                                balance = dataSnapshot.child("balance").getValue(Double.class);
                            }
                            if (balance < withdraw) {
                                Toast.makeText(WithdrawActivity.this, "Insufficient balance", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            balance -= withdraw;
                            mDatabase.child("balance").setValue(balance);
                            Toast.makeText(WithdrawActivity.this, "Task is completed", Toast.LENGTH_SHORT).show();


                            DatabaseReference withdrawHistoryRef = mDatabase.child("withdrawHistory");

                            String withdraw = withdrawHistoryRef.push().getKey();

                            // Save the withdraw transaction under the user's withdraw history
                            withdrawHistoryRef.child(withdraw).child("amount").setValue(withdrawAmountString);

                            Intent intent =new Intent(WithdrawActivity.this,HomeActivity.class);
                            startActivity(intent);
                            finish();

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(WithdrawActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.menu_settings) {
            Intent intent = new Intent(WithdrawActivity.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.menu_log_out) {
            mAuth.signOut();
            Toast.makeText(WithdrawActivity.this, "logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(WithdrawActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}


