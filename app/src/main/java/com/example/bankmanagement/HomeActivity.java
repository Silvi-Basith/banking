package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private TextView mBalanceText;
    private DatabaseReference mDatabase;

     FirebaseAuth authProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mBalanceText = findViewById(R.id.balance);


        authProfile = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("BALANCE").child(authProfile.getCurrentUser().getUid());




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



        CardView cardViewuser =findViewById(R.id.cardview_user);
        cardViewuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,UserProfileActivity.class));
            }
        });

        CardView cardViewDeposit =findViewById(R.id.cardview_deposit);
        cardViewDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,AddMoneyActivity.class));
            }
        });

        CardView cardViewwithdraw =findViewById(R.id.cardview_withdraw);
        cardViewwithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,sourceofwithdraw.class));
            }
        });

        CardView cardViewTransection =findViewById(R.id.cardview_transection);
        cardViewTransection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(HomeActivity.this,TransectionActivity.class));
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();


       if(id == R.id.menu_settings){
            Intent intent =new Intent(HomeActivity.this,Settings.class);
            startActivity(intent);

        }else if(id == R.id.menu_log_out){
            authProfile.signOut();
            Toast.makeText(HomeActivity.this,"logged out",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(HomeActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}