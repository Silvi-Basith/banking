package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class Bkash extends AppCompatActivity {

    private EditText bkashNumberEditText;
    private Button withdrawButton;
    int code;
    private  static final Pattern mobile_pattern =Pattern.compile("^01[3-9][0-9]{8}$");




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bkash);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        bkashNumberEditText = findViewById(R.id.bkash_number);
        withdrawButton = findViewById(R.id.withdraw_button);





        withdrawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bkashNumber = bkashNumberEditText.getText().toString().trim();
                if (bkashNumber.isEmpty()) {
                    bkashNumberEditText.setError("Bkash number cannot be empty");
                    return;
                }else if (!mobile_pattern.matcher(bkashNumber).matches()){
                    Toast.makeText(Bkash.this,"please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    bkashNumberEditText.setError("valid phone number required");
                    bkashNumberEditText.requestFocus();
                }else {



                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user != null) {
                        String email = user.getEmail();
                        Random random =new Random();
                        code =random.nextInt(8999)+1000;
                        String url ="https://aimanazadkhan.000webhostapp.com/SendEmail.php";
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                                intent.putExtra("code", code);
                                startActivity(intent);

                                finish();
                                Toast.makeText(Bkash.this, ""+response, Toast.LENGTH_SHORT).show();




                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(Bkash.this, ""+error, Toast.LENGTH_SHORT).show();

                            }
                        }){
                            @Nullable
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {

                                Map<String,String> params =new HashMap<>();
                                params.put("email",email);
                                params.put("code",String.valueOf(code));
                                return params;
                            }
                        };
                        requestQueue.add(stringRequest);


                    } else {
                        // No user is signed in
                    }

                }

            }
        });
    }


}
