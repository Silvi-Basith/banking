package com.example.bankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuItem;
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

public class UpdateEmailActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail , userPwd;
    private Button buttonUpdateEmail ;
    private EditText editTextNewEmail,editTextPwd ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);

        getSupportActionBar().setTitle("Update Email");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressbar);
        editTextPwd=findViewById(R.id.edttxt_pswd);
        editTextNewEmail=findViewById(R.id.edttxt_Update_email);
        textViewAuthenticated=findViewById(R.id.txtvw_Update_email_hd);

        buttonUpdateEmail=findViewById(R.id.btn_upt_eml);
        buttonUpdateEmail.setEnabled(false);
        editTextNewEmail.setEnabled(false);



        ImageView imageViewshowhideemailpwd =findViewById(R.id.showhideemailpassword);
        imageViewshowhideemailpwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhideemailpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                   editTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhideemailpwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhideemailpwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        userOldEmail =firebaseUser.getEmail();
        TextView textViewOldEmail = findViewById(R.id.edttxt_current_email);
        textViewOldEmail.setText(userOldEmail);

        if (firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Something went Wrong! User's Details not available", Toast.LENGTH_SHORT).show();

        }else{
            reAuthenticated (firebaseUser);

        }

    }

    private void reAuthenticated(FirebaseUser firebaseUser) {
        Button buttonVerifyUser =findViewById(R.id.btn_aut_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd = editTextPwd.getText().toString();

                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Password is needed to continue", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("please enter your password for authentication");
                    editTextPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(userOldEmail,userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         progressBar.setVisibility(View.GONE);

                         Toast.makeText(UpdateEmailActivity.this, "Password has been verified", Toast.LENGTH_SHORT).show();

                         textViewAuthenticated.setText("You are authenticated .You can update email now ");

                         editTextNewEmail.setEnabled(true);
                         editTextPwd.setEnabled(false);
                         buttonVerifyUser.setEnabled(false);
                         buttonUpdateEmail.setEnabled(true);

                         buttonUpdateEmail.setBackgroundTintList(ContextCompat.getColorStateList(UpdateEmailActivity.this, com.google.android.material.R.color.material_blue_grey_800));

                         buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                             @Override
                             public void onClick(View v) {
                                 userNewEmail =editTextNewEmail.getText().toString();
                                 if(TextUtils.isEmpty(userNewEmail)){
                                     Toast.makeText(UpdateEmailActivity.this, "New Email is required", Toast.LENGTH_SHORT).show();
                                     editTextNewEmail.setError("please enter new email");
                                     editTextNewEmail.requestFocus();

                                 }else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                     Toast.makeText(UpdateEmailActivity.this,"please re-enter your email",Toast.LENGTH_LONG).show();
                                     editTextNewEmail.setError("valid email is Required");
                                     editTextNewEmail.requestFocus();
                                 }else if (userOldEmail.matches(userNewEmail)){
                                     Toast.makeText(UpdateEmailActivity.this,"New email can't be same as previous email",Toast.LENGTH_LONG).show();
                                     editTextNewEmail.setError("Please enter new email");
                                     editTextNewEmail.requestFocus();
                                 }else{
                                     progressBar.setVisibility(View.VISIBLE);

                                     updateEmail(firebaseUser);

                                 }
                             }
                         });

                     }else{
                         try{
                             throw task.getException();
                         }catch (Exception e){
                             Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                         }
                     }
                        }
                    });
                }

            }
        });
    }

    private void updateEmail(FirebaseUser firebaseUser) {

        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this, "Email has been updated. please verify your new email", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(UpdateEmailActivity.this,Settings.class);
                    startActivity(intent);
                    finish();

                }else{
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }


}