package com.example.bankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {


    private FirebaseAuth authProfile;
    private EditText editTextcrntpwd, editTextnewpwd, editTextcnfrmnewpwd;
    private TextView textViewAuthenticated;
    private Button btncngpwd, btnauthenticated;
    private ProgressBar progressBar;
    private String userPwdCurr;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[!@#$%&*()]).{6,20}");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        getSupportActionBar().setTitle("Change password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextcrntpwd = findViewById(R.id.edttxt_current_pwd);
        editTextnewpwd = findViewById(R.id.edttxt_Update_pwd);
        editTextcnfrmnewpwd = findViewById(R.id.edttxt_cnfrm_update_pwd);
        textViewAuthenticated = findViewById(R.id.txtvw_Update_pwd_hd);
        progressBar = findViewById(R.id.progressbar);

        btnauthenticated = findViewById(R.id.btn_aut_user);
        btncngpwd = findViewById(R.id.btn_upt_pwd);

        editTextnewpwd.setEnabled(false);
        editTextcnfrmnewpwd.setEnabled(false);
        btncngpwd.setEnabled(false);


        ImageView imageViewshowhidecrntpwd =findViewById(R.id.showhidecrntpassword);
        imageViewshowhidecrntpwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhidecrntpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextcrntpwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextcrntpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhidecrntpwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextcrntpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhidecrntpwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        ImageView imageViewshowhideupdtmpwd =findViewById(R.id.showhideupdtpassword);
        imageViewshowhideupdtmpwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhideupdtmpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextnewpwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextnewpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhideupdtmpwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextnewpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhideupdtmpwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        ImageView imageViewshowhideconfirmmpwd =findViewById(R.id.showhidecnfrmpassword);
        imageViewshowhideconfirmmpwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhideconfirmmpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextcnfrmnewpwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextcnfrmnewpwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhideconfirmmpwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextcnfrmnewpwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                   imageViewshowhideconfirmmpwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();

        if (firebaseUser.equals("")) {
            Toast.makeText(ChangePasswordActivity.this, "Something went wrong, User's details not available", Toast.LENGTH_SHORT).show();

        } else {
            reAuthenticateUser(firebaseUser);


        }


    }

    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        btnauthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwdCurr = editTextcrntpwd.getText().toString();

                if (TextUtils.isEmpty(userPwdCurr)) {

                    Toast.makeText(ChangePasswordActivity.this, "password is needed", Toast.LENGTH_SHORT).show();
                    editTextcrntpwd.setError("please enter your current password");
                    editTextcrntpwd.requestFocus();

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                editTextcrntpwd.setEnabled(false);
                                btnauthenticated.setEnabled(false);
                                editTextnewpwd.setEnabled(true);
                                editTextcnfrmnewpwd.setEnabled(true);
                                btncngpwd.setEnabled(true);


                                textViewAuthenticated.setText("you are authenticated, you can change password now ");
                                Toast.makeText(ChangePasswordActivity.this, "password has been verified", Toast.LENGTH_SHORT).show();

                                btncngpwd.setBackgroundTintList(ContextCompat.getColorStateList(ChangePasswordActivity.this, com.google.android.material.R.color.material_blue_grey_800));

                                btncngpwd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        changepwd(firebaseUser);
                                    }
                                });
                            } else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, "e.getMessage", Toast.LENGTH_SHORT).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void changepwd(FirebaseUser firebaseUser) {

        String userPwdnew = editTextnewpwd.getText().toString();
        String userPwdCnfrmNew = editTextcnfrmnewpwd.getText().toString();

        if (TextUtils.isEmpty(userPwdnew)) {
            Toast.makeText(ChangePasswordActivity.this, "New password is needeed", Toast.LENGTH_SHORT).show();
            editTextnewpwd.setError("Please enter your new password");
            editTextnewpwd.requestFocus();

        } else if (TextUtils.isEmpty(userPwdCnfrmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "New password is needeed", Toast.LENGTH_SHORT).show();
            editTextnewpwd.setError("Please re-enter your new password");
            editTextnewpwd.requestFocus();
        }else if (!PASSWORD_PATTERN.matcher(userPwdnew).matches()){
            Toast.makeText(ChangePasswordActivity.this,"please re-enter your password",Toast.LENGTH_LONG).show();
            editTextnewpwd.setError("1 digit, 1 lower, 1 upper, 1 special char and length 6-20");
            editTextnewpwd.requestFocus();
        } else if (!userPwdnew.equals(userPwdCnfrmNew)) {
            Toast.makeText(ChangePasswordActivity.this, "please enter same password", Toast.LENGTH_LONG).show();
            editTextcnfrmnewpwd.setError("password confirmation is required");
            editTextcnfrmnewpwd.requestFocus();

            editTextcnfrmnewpwd.clearComposingText();
            editTextcnfrmnewpwd.clearComposingText();


        }else{
            progressBar.setVisibility(View.VISIBLE);

            firebaseUser.updatePassword(userPwdnew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(ChangePasswordActivity.this, "password has been changed", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(ChangePasswordActivity.this,Settings.class);
                        startActivity(intent);
                        finish();
                    }else{
                        try{
                            throw task.getException();
                        }catch (Exception e){
                            Toast.makeText(ChangePasswordActivity.this, "e.getMessage", Toast.LENGTH_SHORT).show();
                        }
                    }
                    progressBar.setVisibility(View.GONE);

                }
            });
        }
    }
}