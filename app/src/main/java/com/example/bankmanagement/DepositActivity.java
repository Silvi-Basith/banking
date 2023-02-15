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

public class DepositActivity extends AppCompatActivity {

    private TextView mBalanceText;
    private EditText mDepositAmount;
    private Button mDepositButton;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);

        mBalanceText = findViewById(R.id.balance_deposit_text);
        mDepositAmount = findViewById(R.id.Deposit_amount);
        mDepositButton = findViewById(R.id.Deposit_button);

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

        mDepositButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String depositAmountString = mDepositAmount.getText().toString().trim();

                if (TextUtils.isEmpty(depositAmountString)) {
                    Toast.makeText(DepositActivity.this, "Please enter a deposit amount", Toast.LENGTH_SHORT).show();
                    return;
                }

                Double deposit = Double.parseDouble(depositAmountString);

                if (deposit < 500 || deposit > 20000) {
                    Toast.makeText(DepositActivity.this, "The deposit amount must be between $500 and $20000", Toast.LENGTH_SHORT).show();
                    return;
                }

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Double balance = 0.0;
                        if (dataSnapshot.hasChild("balance")) {
                            balance = dataSnapshot.child("balance").getValue(Double.class);
                        }
                        balance += deposit;
                        mDatabase.child("balance").setValue(balance);
                        Toast.makeText(DepositActivity.this, "Successfully deposited", Toast.LENGTH_SHORT).show();

                        DatabaseReference DepositHistoryRef = mDatabase.child("DepositHistory");

                        String withdraw = DepositHistoryRef.push().getKey();

                        // Save the withdraw transaction under the user's withdraw history
                        DepositHistoryRef.child(withdraw).child("amount").setValue(depositAmountString);
                        Intent intent =new Intent(DepositActivity.this,HomeActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DepositActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();


                    }
                    });
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
            Intent intent = new Intent(DepositActivity.this, Settings.class);
            startActivity(intent);

        } else if (id == R.id.menu_log_out) {
            mAuth.signOut();
            Toast.makeText(DepositActivity.this, "logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(DepositActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}


