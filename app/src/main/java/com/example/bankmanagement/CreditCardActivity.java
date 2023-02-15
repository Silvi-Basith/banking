package com.example.bankmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreditCardActivity extends AppCompatActivity {
    private EditText etCardNumber, etCVC, etMMDY, etCardHolderName;
    private Button btnDeposit;

    int code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card);

        etCardNumber = findViewById(R.id.edttxt_card_number);
        etCVC = findViewById(R.id.edttxttvw_cvc);
        etMMDY = findViewById(R.id.edttxttvw_mmdy);
        etCardHolderName = findViewById(R.id.edtxtvw_cardholdername);
        btnDeposit = findViewById(R.id.button_addmoney);

        btnDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cardNumber = etCardNumber.getText().toString().trim();
                String cvc = etCVC.getText().toString().trim();
                String mmdy = etMMDY.getText().toString().trim();
                String cardHolderName = etCardHolderName.getText().toString().trim();



                if (!isValidCardNumber(cardNumber)) {
                    etCardNumber.setError("Card number must be 16 digits.");
                    return;
                }

                if (!isValidCVC(cvc)) {
                    etCVC.setError("CVC must be 3 or 4 digits.");
                    return;
                }

                if (!isValidMMDY(mmdy)) {
                    etMMDY.setError("MM/DY format is invalid.");
                    return;
                }

                if (!isValidCardHolderName(cardHolderName)) {
                    etCardHolderName.setError("Card holder name must contain only alphabetical characters.");
                    return;
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

                                Intent intent = new Intent(getApplicationContext(), AddMoneyOTP.class);
                                intent.putExtra("code", code);
                                startActivity(intent);

                                finish();
                                Toast.makeText(CreditCardActivity.this, ""+response, Toast.LENGTH_SHORT).show();




                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                                Toast.makeText(CreditCardActivity.this, ""+error, Toast.LENGTH_SHORT).show();

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

    private boolean isValidCardNumber(String cardNumber) {
        if (TextUtils.isEmpty(cardNumber)) {
            etCardNumber.setError("Card number cannot be empty.");
            return false;
        }
        return cardNumber.length() == 16;
    }

    private boolean isValidCVC(String cvc) {
        if (TextUtils.isEmpty(cvc)) {
            etCVC.setError("CVC cannot be empty.");
            return false;
        }
        return cvc.length() == 3 || cvc.length() == 4;
    }

    private boolean isValidMMDY(String mmdy) {
        if (TextUtils.isEmpty(mmdy)) {
            etMMDY.setError("MM/DY cannot be empty.");
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
            Date date = sdf.parse(mmdy);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int month = cal.get(Calendar.MONTH) + 1;
            int year = cal.get(Calendar.YEAR) % 100;
            return (month >= 1 && month <= 12) && (year >= 0 && year <= 99);
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isValidCardHolderName(String cardHolderName) {
        if (TextUtils.isEmpty(cardHolderName)) {
            etCardHolderName.setError("Card holder name cannot be empty.");
            return false;
        }
        Pattern pattern = Pattern.compile("[a-zA-Z]+");
        Matcher matcher = pattern.matcher(cardHolderName);
        return matcher.matches();
    }

}
