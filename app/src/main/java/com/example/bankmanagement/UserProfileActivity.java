package com.example.bankmanagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class UserProfileActivity extends AppCompatActivity {
    private TextView textviewwelcome,textviewfullname,textviewemail,textviewdob,textviewgender,textviewmobile;
    private ProgressBar progressBar;
    private String fullname,email,dob,gender,mobile;
    private FirebaseAuth authProfile;
    private ImageView imageView;
    private SwipeRefreshLayout swipeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        getSupportActionBar().setTitle("User Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        swipeToRefresh();

        textviewwelcome =findViewById(R.id.textview_show_welcome);
        textviewfullname=findViewById(R.id.textview_show_full_name);
        textviewemail=findViewById(R.id.textview_email);
        textviewdob=findViewById(R.id.textview_birthday);
        textviewgender=findViewById(R.id.textview_gender);
        textviewmobile=findViewById(R.id.textview_mobile);
        progressBar=findViewById(R.id.progressbar);

        imageView =findViewById(R.id.imageview_profile_Dp);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(UserProfileActivity.this,UploadProfilePicActivity.class);
                startActivity(intent);

            }
        });


        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser == null){
            Toast.makeText(UserProfileActivity.this, "something went wrong! user's details are not available at that moment", Toast.LENGTH_SHORT).show();
        }else{

            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);

        }

    }

    @SuppressLint("ResourceAsColor")
    private void swipeToRefresh() {

        swipeContainer=findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);

            }
        });
         swipeContainer.setColorSchemeColors(android.R.color.holo_blue_bright, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light);
    }


    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID = firebaseUser.getUid();
         DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registerd users");
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ReadWriteUserDetails readUserDetails=snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullname =firebaseUser.getDisplayName();
                    email =firebaseUser.getEmail();

                    dob=readUserDetails.doB;
                    gender=readUserDetails.gender;
                    mobile=readUserDetails.mobile;

                    textviewwelcome.setText("welcome,"+fullname+"!");
                    textviewfullname.setText(fullname);
                    textviewemail.setText(email);
                    textviewdob.setText(dob);
                    textviewgender.setText(gender);
                    textviewmobile.setText(mobile);

                    Uri uri =firebaseUser.getPhotoUrl();
                    Picasso.get().load(uri).into(imageView);

                }else{
                    Toast.makeText(UserProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();


        if(id == R.id.menu_update_profile){
            Intent intent =new Intent(UserProfileActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.menu_settings){
            Intent intent =new Intent(UserProfileActivity.this,Settings.class);
            startActivity(intent);

        }else if(id == R.id.menu_log_out){
            authProfile.signOut();
            Toast.makeText(UserProfileActivity.this,"logged out",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(UserProfileActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{

        }
        return super.onOptionsItemSelected(item);
    }
}