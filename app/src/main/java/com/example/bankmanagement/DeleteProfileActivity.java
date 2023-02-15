package com.example.bankmanagement;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DeleteProfileActivity extends AppCompatActivity {

    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAutheticated;
    private ProgressBar progressBar;
    private String userpwd;
    private Button buttonReAuthenticated,buttonDeleteuser;
    private static final String TAG ="DeleteProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);



        ImageView imageViewshowhidepwd =findViewById(R.id.showhidepassword);
        imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhidepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editTextUserPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    editTextUserPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextUserPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewshowhidepwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        getSupportActionBar().setTitle("Delete Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar=findViewById(R.id.progressbar);
        editTextUserPwd=findViewById(R.id.edttxt_dlt_usr_pwd);
        textViewAutheticated=findViewById(R.id.txtvw_dlt_hd);
        buttonDeleteuser=findViewById(R.id.btn_dlt_user);
        buttonReAuthenticated=findViewById(R.id.btn_aut_user);

        buttonDeleteuser.setEnabled(false);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        if(firebaseUser.equals("")){
            Toast.makeText(DeleteProfileActivity.this, "something went wrong"+"user's details are not available at the moment", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(DeleteProfileActivity.this,Settings.class);
            startActivity(intent);
            finish();

        }else{
            reAuthenticateUser(firebaseUser);
        }

    }
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticated.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userpwd = editTextUserPwd.getText().toString();

                if (TextUtils.isEmpty(userpwd)) {

                    Toast.makeText(DeleteProfileActivity.this, "password is needed", Toast.LENGTH_SHORT).show();
                    editTextUserPwd.setError("please enter your current password");
                    editTextUserPwd.requestFocus();

                } else {
                    progressBar.setVisibility(View.VISIBLE);

                    AuthCredential credential = EmailAuthProvider.getCredential(firebaseUser.getEmail(),userpwd);

                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {


                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);

                                editTextUserPwd.setEnabled(false);
                               buttonReAuthenticated.setEnabled(false);

                                buttonDeleteuser.setEnabled(true);


                                textViewAutheticated.setText("you are authenticated, you can delete your profile  now ");
                                Toast.makeText(DeleteProfileActivity.this, "password has been verified", Toast.LENGTH_SHORT).show();

                                buttonDeleteuser.setBackgroundTintList(ContextCompat.getColorStateList(DeleteProfileActivity.this, com.google.android.material.R.color.material_blue_grey_800));

                                buttonDeleteuser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        showAlertDialog();
                                    }
                                });
                            }


                            else {
                                try {
                                    throw task.getException();
                                } catch (Exception e) {
                                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }
    private void showAlertDialog() {
        AlertDialog.Builder builder= new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Delete User ");
        builder.setMessage("Do you really want to delete your profile.This action is irreversible");

        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               deleteUserData(firebaseUser);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(DeleteProfileActivity.this,Settings.class);
                startActivity(intent);
                finish();




            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.light_gray));
            }
        });
    }

    private void deleteUser() {

        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    authProfile.signOut();
                    Toast.makeText(DeleteProfileActivity.this, "user has been deleted", Toast.LENGTH_SHORT).show();

                    Intent intent =new Intent(DeleteProfileActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    try{
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(DeleteProfileActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void deleteUserData(FirebaseUser firebaseUser) {

        if(firebaseUser.getPhotoUrl()!=null){
            FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
            StorageReference storageReference =firebaseStorage.getReferenceFromUrl(this.firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e(TAG,"OnSuccess: Photo Deleted");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());

                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        if (this.firebaseUser != null) {
            databaseReference.child("BALANCE").child(this.firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.e(TAG, "OnSuccess: user balance Deleted");
                    databaseReference.child("Registerd users").child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e(TAG, "OnSuccess: user Registered Users data Deleted");
                            deleteUser();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(DeleteProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e(TAG, "Firebase user is null");
        }





    }

}