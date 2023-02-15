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

public class Link3Activity extends AppCompatActivity {



    private  static final Pattern mobile_pattern =Pattern.compile("^01[3-9][0-9]{8}$");
    int code;
    private EditText Accountnumber;
    private  EditText Contactnumber;
    private Button btnlink3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link3);

        Accountnumber =findViewById(R.id.editText_link3);
        Contactnumber = findViewById(R.id.editText_link31);
        btnlink3 = findViewById(R.id.button_link3);



        btnlink3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ContactNumber = Contactnumber.getText().toString();
                String AccountNumber =Accountnumber.getText().toString();

                if (AccountNumber.isEmpty()) {
                    Accountnumber.setError("Account number cannot be empty");
                    return;
                }else if (ContactNumber.isEmpty()) {
                    Contactnumber.setError("Contact number cannot be empty");
                    return;
                }else if (!mobile_pattern.matcher(ContactNumber).matches()){
                    Toast.makeText(Link3Activity.this,"please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    Contactnumber.setError("valid phone number required");
                    Contactnumber.requestFocus();
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
                                Toast.makeText(Link3Activity.this, ""+response, Toast.LENGTH_SHORT).show();




                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(Link3Activity.this, ""+error, Toast.LENGTH_SHORT).show();

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