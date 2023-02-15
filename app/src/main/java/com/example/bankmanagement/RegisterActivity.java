package com.example.bankmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
     private EditText editTextRegisterFullName ,editTextRegisterEmail, editTextRegisterDoB, editTextRegisterMobile,editTextRegisterPwd,editTextRegisterConfirmPwd;
     private ProgressBar progressBar;
     private RadioGroup radioGroupRegisterGender;
     private RadioButton radioButtonRegisterGenderSelected;

     private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%&*()]).{6,20}");
     private  static final Pattern mobile_pattern =Pattern.compile("^01[3-9][0-9]{8}$");

     private static final String TAG="RegisterActivity";
     private DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Toast.makeText(RegisterActivity.this,"You can register now", Toast.LENGTH_LONG).show();

        progressBar=findViewById(R.id.progressbar);
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDoB=findViewById(R.id.editText_register_dob);
        editTextRegisterMobile=findViewById(R.id.editText_register_mobile);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);
        radioGroupRegisterGender=findViewById(R.id.radiogroup_register_gender);
        radioGroupRegisterGender.clearCheck();

        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calender =Calendar.getInstance();
                int day =calender.get(Calendar.DAY_OF_MONTH);
                int month =calender.get(Calendar.MONTH);
                int year= calender.get(Calendar.YEAR);

                picker =new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextRegisterDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();

            }
        })  ;
        ImageView imageViewshowhidepwd =findViewById(R.id.showhidepassword);
        imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhidepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextRegisterPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextRegisterPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextRegisterPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhidepwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        ImageView imageViewshowhideconfirmpwd =findViewById(R.id.showhideconfirmpassword);
        imageViewshowhideconfirmpwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhideconfirmpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextRegisterConfirmPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextRegisterConfirmPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhideconfirmpwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextRegisterConfirmPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhideconfirmpwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        TextView textViewLogin = findViewById(R.id.textview_login_link);

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(RegisterActivity.this, LoginActivity.class) ;
                startActivity(intent);
            }
        });

        Button buttonRegister = findViewById(R.id.button_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId =radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

                String textFullName =editTextRegisterFullName.getText().toString();
                String textEmail =editTextRegisterEmail.getText().toString();
                String textDoB =editTextRegisterDoB.getText().toString();
                String textMobile =editTextRegisterMobile.getText().toString();
                String textPwd =editTextRegisterPwd.getText().toString();
                String textConfirmPwd=editTextRegisterConfirmPwd.getText().toString();
                String textGender;


                if(TextUtils.isEmpty(textFullName)){
                    Toast.makeText(RegisterActivity.this,"please enter your full name",Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full name is Required");
                    editTextRegisterFullName.requestFocus();
                }
                else if (TextUtils.isEmpty(textEmail)){
                    Toast.makeText(RegisterActivity.this,"please enter your email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Email is Required");
                    editTextRegisterEmail.requestFocus();

                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(RegisterActivity.this,"please re-enter email",Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("valid email is Required");
                    editTextRegisterEmail.requestFocus();
                }else if (TextUtils.isEmpty(textDoB)){
                    Toast.makeText(RegisterActivity.this,"please enter your date of birth",Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Date of birth Required");
                    editTextRegisterDoB.requestFocus();
                } else if (!DateValidator.isValid(textDoB)) {
                    editTextRegisterDoB.setError("Invalid Date of Birth");
                    editTextRegisterDoB.requestFocus();
                } else if (!DateValidator.isAbove18(textDoB)) {
                    editTextRegisterDoB.setError("You must be 18 years or older");
                    editTextRegisterDoB.requestFocus();
                } else if (radioGroupRegisterGender.getCheckedRadioButtonId()== -1){
                    Toast.makeText(RegisterActivity.this,"please select your gender ",Toast.LENGTH_LONG).show();
                   radioButtonRegisterGenderSelected.setError("Gender vis Required");
                   radioButtonRegisterGenderSelected.requestFocus();
                }else if (TextUtils.isEmpty(textMobile)){
                    Toast.makeText(RegisterActivity.this,"please enter your mobile number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("mobile number  is Required");
                    editTextRegisterMobile.requestFocus();
                }else if (!mobile_pattern.matcher(textMobile).matches()){
                    Toast.makeText(RegisterActivity.this,"please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    editTextRegisterMobile.setError("valid phone number required");
                    editTextRegisterMobile.requestFocus();
                }else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(RegisterActivity.this,"please enter your password",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError("password  is Required");
                   editTextRegisterPwd.requestFocus();

                }else if (!PASSWORD_PATTERN.matcher(textPwd).matches()){
                    Toast.makeText(RegisterActivity.this,"please re-enter your password",Toast.LENGTH_LONG).show();
                    editTextRegisterPwd.setError(" At least 1 digit, 1 lower, 1 upper, 1 special char and length 6-20");
                    editTextRegisterPwd.requestFocus();
                } else if (!textPwd.equals(textConfirmPwd)) {
                    Toast.makeText(RegisterActivity.this,"please enter same password",Toast.LENGTH_LONG).show();
                    editTextRegisterConfirmPwd.setError("password confirmation is required");
                   editTextRegisterConfirmPwd.requestFocus();
                   
                   editTextRegisterPwd.clearComposingText();
                   editTextRegisterConfirmPwd.clearComposingText();

                }else{
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName,textEmail,textDoB,textGender,textMobile,textPwd);
                    
                }
            }
        });

    }

    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textMobile, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(textEmail,textPwd).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,"USer Registerd",Toast.LENGTH_LONG).show();
                            FirebaseUser firebaseUser =auth.getCurrentUser();

                            UserProfileChangeRequest ProfileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(ProfileChangeRequest);
                            ReadWriteUserDetails writeUserDetails =new ReadWriteUserDetails(textDoB,textGender,textMobile);



                            DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registerd users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        firebaseUser.sendEmailVerification();
                                        auth.signOut();

                                        Toast.makeText(RegisterActivity.this, "registration successful please verify your email", Toast.LENGTH_LONG).show();


                                       Intent intent = new Intent (RegisterActivity.this, LoginActivity.class);

                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();


                                    } else {
                                        Toast.makeText(RegisterActivity.this, "user Registration failed. please try again", Toast.LENGTH_LONG).show();
                                    }
                                    progressBar.setVisibility(View.GONE);
                                }

                            });


                        }else{
                            try{
                                throw task.getException();
                            }catch(FirebaseAuthUserCollisionException e){
                                editTextRegisterEmail.setError("user is already registerd with this email. use another email");
                                editTextRegisterEmail.requestFocus();
                            } catch (Exception e) {
                                Log.e(TAG,e.getMessage());
                                Toast.makeText(RegisterActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}