package com.example.bankmanagement;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private EditText editTextLoginEmail,editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static final String TAG="LoginActivity";
    int incorrectAttempts = 0;
    private Button buttonLogin;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("Login");
        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_password);
        progressBar =findViewById(R.id.progressbar);

        authProfile=FirebaseAuth.getInstance();

       TextView textViewRegister = findViewById(R.id.textview_register_link);

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(LoginActivity.this, RegisterActivity.class) ;
                startActivity(intent);
            }
        });
        TextView textViewForgotPassword =findViewById(R.id.textview_forgot_password_link);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "you can reset your password now", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class));
            }
        });

        ImageView imageViewshowhidepwd =findViewById(R.id.showhidepassword);
        imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewshowhidepwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  if (editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                      editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                      imageViewshowhidepwd.setImageResource(R.drawable.ic_hide_pwd);
                  }else{
                      editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                      imageViewshowhidepwd.setImageResource(R.drawable.ic_show_pwd);
                  }
            }
        });
                buttonLogin = findViewById(R.id.button_login);
                buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail = editTextLoginEmail.getText().toString();
                String textPwd = editTextLoginPwd.getText().toString();

                if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(LoginActivity.this, "please enter your email", Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();


                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(LoginActivity.this, "please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("valid email is Required");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)) {
                    Toast.makeText(LoginActivity.this, "please enter your password", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("password  is Required");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }

            }
        });
    }

    private void loginUser (String email,String pwd){
        authProfile.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    FirebaseUser firebaseUser = authProfile.getCurrentUser();

                     if(firebaseUser.isEmailVerified()){


                        Toast.makeText(LoginActivity.this, "You are logged in now ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                        finish();
                    }else{
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut();
                        showAlertDialogue();
                    }
                }else{
                    try{
                       throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("user does not exists or is no longer valid . please register again");
                        editTextLoginEmail.requestFocus();
                    }catch (Exception e){

                        incorrectAttempts++;

                        // Check the counter value
                        if (incorrectAttempts == 3) {

                            // Disable the login button
                            buttonLogin.setEnabled(false);



                                // Send email only if user is valid
                                String url = "https://aimanazadkhan.000webhostapp.com/SecurityAlert.php";
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Email sent successfully
                                        Toast.makeText(getApplicationContext(), "Email sent successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        // Error sending email
                                        Toast.makeText(getApplicationContext(), "Error sending email", Toast.LENGTH_SHORT).show();
                                    }
                                }) {
                                    @Nullable
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        // Pass email as parameter to PHP script
                                        Map<String, String> params = new HashMap<>();
                                        params.put("email", email);
                                        return params;
                                    }
                                };
                                requestQueue.add(stringRequest);



                            // Start a 60-second timer to re-enable the login button
                            new CountDownTimer(30000, 1000) {
                                public void onTick(long millisUntilFinished) {
                                    // Do nothing
                                }

                                public void onFinish() {
                                    // Re-enable the login button
                                    buttonLogin.setEnabled(true);

                                    // Reset the counter
                                    incorrectAttempts = 0;
                                }
                            }.start();

                        }
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(LoginActivity.this,e.getMessage(),  Toast.LENGTH_SHORT).show();
                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialogue() {
        AlertDialog.Builder builder= new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("Email not verified");
        builder.setMessage("please verify your email now .you can not login without email verification");

        builder.setPositiveButton("continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent =new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        AlertDialog alertDialog =builder.create();
        alertDialog.show();
    }
    @Override
    protected void onStart(){
        super.onStart();;

       if(authProfile.getCurrentUser() !=null){
            Toast.makeText(LoginActivity.this, "Already Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();
        }else{
           // Toast.makeText(LoginActivity.this, "You can Login Now ", Toast.LENGTH_SHORT).show();
        }
   }
}