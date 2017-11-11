package com.getinstaapp.instaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPassword extends AppCompatActivity {
private EditText edresetpswd,edconfirmpswd;
    private Button butreset_otp;
    private String sreset_mobile,sreset_password;
    String fori;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edresetpswd=(EditText) findViewById(R.id.edresetpswd);
        edconfirmpswd=(EditText) findViewById(R.id.edconfirnpswd);
        butreset_otp=(Button ) findViewById(R.id.butreset_otp);
        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            sreset_mobile = extras.getString("mobile_number");
        }
        else
        {
            Toast.makeText(getApplicationContext(),"no dta",Toast.LENGTH_SHORT).show();
        }

        //Intent intent=getIntent();
        edresetpswd.setOnFocusChangeListener( new View.OnFocusChangeListener(){

            public void onFocusChange( View view, boolean hasfocus){
                if(hasfocus){
                    edresetpswd.setTextColor(Color.RED);

                    edresetpswd.setBackgroundResource( R.drawable.edittext_afterseslect);
                    edconfirmpswd.setTextColor(Color.BLACK);

                }

            }
        });
        edconfirmpswd.setOnFocusChangeListener( new View.OnFocusChangeListener(){

            public void onFocusChange( View view, boolean hasfocus){
                if(hasfocus){
                    edconfirmpswd.setTextColor(Color.RED);

                    edconfirmpswd.setBackgroundResource( R.drawable.edittext_afterseslect);
                    edresetpswd.setTextColor(Color.BLACK);
                }
            }
        });
        edconfirmpswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String passwrd = edresetpswd.getText().toString();
                String cpasswrd = edconfirmpswd.getText().toString();
                if (editable.length() > 0 && passwrd.length() > 0) {
                    if(!cpasswrd .equals(passwrd )){
                        Toast.makeText(ResetPassword.this,"Please match your passwords",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        butreset_otp.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                sreset_password=edconfirmpswd.getText().toString();
                                resetpassword(sreset_mobile,sreset_password);
                                Intent intent=new Intent(ResetPassword.this,Categories.class);
                                intent.putExtra("mobile_number",sreset_mobile);
                                Toast.makeText(getApplicationContext(),sreset_mobile,Toast.LENGTH_SHORT).show();

                                startActivity(intent);
                            }
                        });
                    }

                }
            }
        });

    }


    public void resetpassword(final String s1, final String s2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.user_resetpassword, new Response.Listener<String>() {
            public void onResponse(String response) {
//                try {
//                    JSONObject jObj = new JSONObject(response);
//                    boolean abc = jObj.getBoolean("exits");
//                    System.out.print(abc);
//                    if (abc)
//                    {
//                      //  JSONObject users = jObj.getJSONObject("user_det");
//                       // String uname1 = users.getString("mobile_number");
//
//                        Intent intent=new Intent(ResetPassword.this,OTPVerify.class);
//                        intent.putExtra("mobile_number",sreset_mobile);
//
//
//                        startActivity(intent);
//
//                    }
//                    else
//                    {
//                        Toast.makeText(getApplicationContext(),"Please enter correct number",Toast.LENGTH_SHORT).show();
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile_number", s1);
                params.put("password", s2);



                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
