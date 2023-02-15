package com.example.bankmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class UpdateProfileActivity extends AppCompatActivity {

    private EditText editTextUpdateName, editTextUpdateDoB,editTextMobile;
    private RadioGroup radioGroupGender;
    private RadioButton radioButtongenderSelected;
    private String textFullName,textDoB,textGender,textMobile;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;
    private DatePickerDialog picker;
    private  static final Pattern mobile_pattern =Pattern.compile("^01[3-9][0-9]{8}$");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getSupportActionBar().setTitle("Update Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressbar);
        editTextUpdateName=findViewById(R.id.edittext_update_profile_name);
        editTextUpdateDoB =findViewById(R.id.edittext_update_profile_DoB);
        editTextMobile =findViewById(R.id.edittext_update_profile_mobile);

        radioGroupGender =findViewById(R.id.radiogroup_update_gender);


        editTextUpdateName.setEnabled(false);
        editTextUpdateDoB.setEnabled(false);





        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        showProfile(firebaseUser);


        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String textSADoB[] =textDoB.split("/");

            int day  = Integer.parseInt(textSADoB[0]);
            int month  = Integer.parseInt(textSADoB[1])-1;
            int year = Integer.parseInt(textSADoB[2]);

            DatePickerDialog picker;


            picker =new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                }
            },year,month,day);
            picker.show();

        }
       });

        Button buttonUpdateProfile=findViewById(R.id.button_update);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });

    }

    private void updateProfile(FirebaseUser firebaseUser) {

        int SelectedGenderId =radioGroupGender.getCheckedRadioButtonId();
        radioButtongenderSelected =findViewById(SelectedGenderId);

        if(TextUtils.isEmpty(textFullName)){
            Toast.makeText(UpdateProfileActivity.this,"please enter your full name",Toast.LENGTH_LONG).show();
            editTextUpdateName.setError("Full name is Required");
            editTextUpdateName.requestFocus();
        }
        else if (TextUtils.isEmpty(textDoB)){
            Toast.makeText(UpdateProfileActivity.this,"please enter your date of birth",Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Date of birth Required");
            editTextUpdateDoB.requestFocus();
        }else if (TextUtils.isEmpty(radioButtongenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this,"please select yout gender ",Toast.LENGTH_LONG).show();
            radioButtongenderSelected.setError("Gender vis Required");
            radioButtongenderSelected.requestFocus();
        }else if (TextUtils.isEmpty(textMobile)){
            Toast.makeText(UpdateProfileActivity.this,"please enter your mobile number",Toast.LENGTH_LONG).show();
            editTextMobile.setError("mobile number  is Required");
            editTextMobile.requestFocus();
        }else if (!mobile_pattern.matcher(textMobile).matches()){
            Toast.makeText(UpdateProfileActivity.this,"please re-enter your mobile number",Toast.LENGTH_LONG).show();
            editTextMobile.setError("valid phone number required");
            editTextMobile.requestFocus();
        }else{
            textGender=radioButtongenderSelected.getText().toString();
            textFullName=editTextUpdateName.getText().toString();
            textDoB=editTextUpdateDoB.getText().toString();
            textMobile=editTextMobile.getText().toString();



            ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(textDoB,textGender,textMobile);
            DatabaseReference referenceProfile =FirebaseDatabase.getInstance().getReference("Registerd users");

            String userID = firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);

            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();

                        firebaseUser.updateProfile(profileUpdates);

                        Toast.makeText(UpdateProfileActivity.this, "Update successful", Toast.LENGTH_SHORT).show();

                        Intent intent =new Intent(UpdateProfileActivity.this,UserProfileActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }else{
                        try{
                            throw  task.getException();

                        }catch (Exception e){
                            Toast.makeText(UpdateProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);

                }
            });


        }

    }



      private void showProfile(FirebaseUser firebaseUser) {
        String userIdofRegisterd =firebaseUser.getUid();

        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registerd users");

        progressBar.setVisibility(View.VISIBLE);

        referenceProfile.child(userIdofRegisterd).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails =snapshot.getValue(ReadWriteUserDetails.class);

                if (readUserDetails != null){

                     textFullName= firebaseUser.getDisplayName();
                     textDoB=readUserDetails.doB;
                     textGender=readUserDetails.gender;
                     textMobile=readUserDetails.mobile;
                     editTextUpdateName.setText(textFullName);
                     editTextMobile.setText(textMobile);
                     editTextUpdateDoB.setText(textDoB);

                     if(textGender.equals("Male")){
                         radioButtongenderSelected=findViewById(R.id.radio_male);

                     }else{
                         radioButtongenderSelected=findViewById(R.id.radio_female);
                     }
                     radioButtongenderSelected.setChecked(true);
                }else{
                    Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UpdateProfileActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }
}
