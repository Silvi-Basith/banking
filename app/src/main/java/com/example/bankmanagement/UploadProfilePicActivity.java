package com.example.bankmanagement;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfilePicActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile_pic);

        getSupportActionBar().setTitle("Upload Profile Picture");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Button buttonuploadpicchoose=findViewById(R.id.upload_pic_choose_button);
        Button buttonuploadpic =findViewById(R.id.upload_pic_button);
        progressBar=findViewById(R.id.progressbar);
        imageViewUploadPic=findViewById(R.id.imageview_profile_Dp);

        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference("Displaypics");
         Uri uri =firebaseUser.getPhotoUrl();

         Picasso.get().load(uri).into(imageViewUploadPic);


         buttonuploadpicchoose.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 openFileChooser();
             }
         });

         buttonuploadpic.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 progressBar.setVisibility(View.VISIBLE);
                 UploadPic();
             }
         });


    }

    private void openFileChooser() {

        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriImage =data.getData();
            imageViewUploadPic.setImageURI(uriImage);
        }
    }

    private void UploadPic(){
        if (uriImage!=null){

            StorageReference fileReference = storageReference.child(authProfile.getCurrentUser().getUid()+"."+getFileExtensiom(uriImage));

            fileReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri =uri;
                            firebaseUser =authProfile.getCurrentUser();
                            UserProfileChangeRequest profileUpdates =new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfilePicActivity.this, "Upload Successful", Toast.LENGTH_SHORT).show();

                    Intent intent =new Intent(UploadProfilePicActivity.this,UserProfileActivity.class);
                    startActivity(intent);
                    finish();
                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UploadProfilePicActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else{
            progressBar.setVisibility(View.GONE);
            Toast.makeText(UploadProfilePicActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtensiom (Uri uri){
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mime =MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.common_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id= item.getItemId();


        if(id == R.id.menu_update_profile){
            Intent intent =new Intent(UploadProfilePicActivity.this,UpdateProfileActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.menu_settings){
            Intent intent =new Intent(UploadProfilePicActivity.this,Settings.class);
            startActivity(intent);

        }else if(id == R.id.menu_log_out){
            authProfile.signOut();
            Toast.makeText(UploadProfilePicActivity.this,"logged out",Toast.LENGTH_LONG).show();
            Intent intent =new Intent(UploadProfilePicActivity.this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(this, "Something went Wrong", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}