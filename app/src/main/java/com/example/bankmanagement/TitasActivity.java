package com.example.bankmanagement;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class TitasActivity extends AppCompatActivity {


    private  static final Pattern mobile_pattern =Pattern.compile("^01[3-9][0-9]{8}$");
    int code;
    private EditText Accountnumber;

    private  EditText Date;
    private  EditText Contactnumber;
    private Button btntitas;

    private DatePickerDialog picker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_titas);




        Accountnumber =findViewById(R.id.editText_titas);
        Contactnumber = findViewById(R.id.editText_titas1);
        btntitas = findViewById(R.id.button_titas);
        Date =findViewById(R.id.editText_titas2);

        Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calender =Calendar.getInstance();
                int day =calender.get(Calendar.DAY_OF_MONTH);
                int month =calender.get(Calendar.MONTH);
                int year= calender.get(Calendar.YEAR);

                picker =new DatePickerDialog(TitasActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        Date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                },year,month,day);
                picker.show();

            }
        })  ;




        btntitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ContactNumber = Contactnumber.getText().toString();
                String AccountNumber =Accountnumber.getText().toString();
                String date =Date.getText().toString();

                if (AccountNumber.isEmpty()) {
                    Accountnumber.setError("Account number cannot be empty");
                    return;
                }else if (ContactNumber.isEmpty()) {
                    Contactnumber.setError("contact number cannot be empty");
                    return;
                }else if (!mobile_pattern.matcher(ContactNumber).matches()){
                    Toast.makeText(TitasActivity.this,"please re-enter your mobile number",Toast.LENGTH_LONG).show();
                    Contactnumber.setError("valid phone number required");
                    Contactnumber.requestFocus();
                }else if (date.isEmpty()) {
                    Toast.makeText(TitasActivity.this, "please enter your email", Toast.LENGTH_LONG).show();
                    Date.setError("Email is Required");
                    Date.requestFocus();


                } else {



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
                                Toast.makeText(TitasActivity.this, ""+response, Toast.LENGTH_SHORT).show();




                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(TitasActivity.this, ""+error, Toast.LENGTH_SHORT).show();

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