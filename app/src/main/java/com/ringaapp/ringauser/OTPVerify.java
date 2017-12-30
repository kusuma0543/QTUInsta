package com.ringaapp.ringauser;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;
import com.ontbee.legacyforks.cn.pedant.SweetAlert.SweetAlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import swarajsaaj.smscodereader.interfaces.OTPListener;

public class OTPVerify extends AppCompatActivity implements View.OnFocusChangeListener,View.OnClickListener,View.OnKeyListener,TextWatcher, OTPListener {
    private EditText mPinFirstDigitEditText;
    private EditText mPinSecondDigitEditText;
    private EditText mPinThirdDigitEditText;
    private EditText mPinForthDigitEditText;
    private EditText mPinHiddenEditText;
    private Button butotp_verify;
    private TextView tvotp_mobile,tvotp_resend;
    String value;
    CountDownTimer bb;
    String fromforgot,last_number;
    private static final String FORMAT = "%02d:%02d";
    int seconds , minutes;
    private TrackGps gps;
    SharedPreferences pref;
    private TextView k,secondk;
    String user_uname,user_sharedimage;
    String uname4,uname5;
    Double ulat,ulng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverify);
        Intent intent=getIntent();
       // pref=getApplication().getSharedPreferences("Options", MODE_PRIVATE);

        last_number=intent.getStringExtra("mobile_number");
        fromforgot=intent.getStringExtra("fromforgot");

       // int_mobile=pref.getString("mobile_number", "");


        mPinFirstDigitEditText = (EditText) findViewById(R.id.pinone);
        mPinSecondDigitEditText = (EditText) findViewById(R.id.pintwo);
        mPinThirdDigitEditText = (EditText) findViewById(R.id.pinthree);
        mPinForthDigitEditText = (EditText) findViewById(R.id.pinfour);
        mPinHiddenEditText = (EditText) findViewById(R.id.pin_hidden_edittext);
        tvotp_mobile=(TextView) findViewById(R.id.tvotp_mobile);
       // int_mobile=intent.getStringExtra("mobile_number");
            k=(TextView) findViewById(R.id.k);
            secondk=(TextView) findViewById(R.id.secondk);
                    tvotp_resend=(TextView) findViewById(R.id.tvotp_resend);
                    tvotp_resend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
            didTapButton(v);
                //insert(last_number);
            }
        });
        tvotp_mobile.setText(last_number);


        final TextView countdown = (TextView) findViewById(R.id.countdown);
        butotp_verify=(Button) findViewById(R.id.butotp_verify);



      bb= new CountDownTimer(50000,1000) { // adjust the milli seconds here
            public void onTick(long millisUntilFinished) {
                countdown.setText(""+String.format(FORMAT,
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                                TimeUnit.MILLISECONDS.toHours(millisUntilFinished)),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            }


            public void onFinish() {
                bb.cancel();
                Toast.makeText(OTPVerify.this, "Please click Resend to get OTP", Toast.LENGTH_SHORT).show();
            bb.cancel();
            countdown.setVisibility(View.GONE);
            k.setVisibility(View.GONE);
            secondk.setVisibility(View.GONE);


            }

        }.start();




        setPINListeners();

        butotp_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    String s1=mPinFirstDigitEditText.getText().toString().trim();
                String s2=mPinSecondDigitEditText.getText().toString().trim();
                String s3=mPinThirdDigitEditText.getText().toString().trim();
                String s4=mPinForthDigitEditText.getText().toString().trim();
                String s=s1+s2+s3+s4;
                otp_check(last_number,s);


            }
        });
    }

    public final void didTapButton(View view) {

        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce);

        // Use bounce interpolator with amplitude 0.2 and frequency 20
        MyBounceInterpolator interpolator = new MyBounceInterpolator(0.2, 20);
        myAnim.setInterpolator(interpolator);

        tvotp_resend.startAnimation(myAnim);
        insert(last_number);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        final int id = v.getId();
        switch (id) {
            case R.id.pinone:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                 showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pintwo:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                   showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pinthree:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.pinfour:
                if (hasFocus) {
                    setFocus(mPinHiddenEditText);
                    showSoftKeyboard(mPinHiddenEditText);
                }
                break;


            default:
                break;
        }
    }
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int id = v.getId();
            switch (id) {
                case R.id.pin_hidden_edittext:
                    if (keyCode == KeyEvent.KEYCODE_DEL) {

                        if (mPinHiddenEditText.getText().length() == 4)
                            mPinForthDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 3)
                            mPinThirdDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 2)
                            mPinSecondDigitEditText.setText("");
                        else if (mPinHiddenEditText.getText().length() == 1)
                            mPinFirstDigitEditText.setText("");

                        if (mPinHiddenEditText.length() > 0) {
                            mPinHiddenEditText.setText(mPinHiddenEditText.getText().subSequence(0, mPinHiddenEditText.length() - 1));
                            mPinHiddenEditText.post(new Runnable() {
                                @Override
                                public void run() {
                                    mPinHiddenEditText.setSelection(mPinHiddenEditText.getText().toString().length());
                                }
                            });
                        }

                        break;
                    }

                default:
                    return false;
            }
        }

        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        setDefaultPinBackground(mPinFirstDigitEditText);
        setDefaultPinBackground(mPinSecondDigitEditText);
        setDefaultPinBackground(mPinThirdDigitEditText);
        setDefaultPinBackground(mPinForthDigitEditText);

        if (s.length() == 0) {
            setFocusedPinBackground(mPinFirstDigitEditText);
            mPinFirstDigitEditText.setText("");
        } else if (s.length() == 1) {
            setFocusedPinBackground(mPinSecondDigitEditText);
            mPinFirstDigitEditText.setText(s.charAt(0) + "");
            mPinSecondDigitEditText.setText("");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");

        } else if (s.length() == 2) {
            setFocusedPinBackground(mPinThirdDigitEditText);
            mPinSecondDigitEditText.setText(s.charAt(1) + "");
            mPinThirdDigitEditText.setText("");
            mPinForthDigitEditText.setText("");

        } else if (s.length() == 3) {
            setFocusedPinBackground(mPinForthDigitEditText);
            mPinThirdDigitEditText.setText(s.charAt(2) + "");
            mPinForthDigitEditText.setText("");
        } else if (s.length() == 4) {
            mPinForthDigitEditText.setText(s.charAt(3) + "");
        }
    }
    private void setDefaultPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.ggg));
    }

    public static void setFocus(EditText editText) {
        if (editText == null)
            return;

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
    }
    private void setFocusedPinBackground(EditText editText) {
        setViewBackground(editText, getResources().getDrawable(R.drawable.ggg));
    }

    private void setPINListeners() {
        mPinHiddenEditText.addTextChangedListener(this);

        mPinFirstDigitEditText.setOnFocusChangeListener(this);
        mPinSecondDigitEditText.setOnFocusChangeListener(this);
        mPinThirdDigitEditText.setOnFocusChangeListener(this);
        mPinForthDigitEditText.setOnFocusChangeListener(this);

        mPinFirstDigitEditText.setOnKeyListener(this);
        mPinSecondDigitEditText.setOnKeyListener(this);
        mPinThirdDigitEditText.setOnKeyListener(this);
        mPinForthDigitEditText.setOnKeyListener(this);
        mPinHiddenEditText.setOnKeyListener(this);
    }
    @SuppressWarnings("deprecation")
    public void setViewBackground(View view, Drawable background) {
        if (view == null || background == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }
    public void showSoftKeyboard(EditText editText) {
        if (editText == null)
            return;

        InputMethodManager imm = (InputMethodManager) getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    @Override
    public void otpReceived(String messageText) {

    }


    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.butotp_verify:
                break;
        }

    }
    public void otp_check(final String sphone1,final String sphone2) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.user_otpreverify, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean abc = jObj.getBoolean("exits");

                    if (abc)
                    {
                        JSONObject users = jObj.getJSONObject("user_det");

                       String uname1 = users.getString("user_mobile_number");
                        String uname2 = users.getString("user_otp_identification");
                        String uname3=users.getString("user_uid");
                        uname4 = users.getString("user_name");
                        uname5 = users.getString("user_profile_image");
                        verifyclick(uname1,uname2,uname3);

                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Please enter correct otp",Toast.LENGTH_SHORT).show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> insert = new HashMap<String, String>();
                insert.put("user_mobile_number",sphone1);
                insert.put("user_otp_identification", sphone2);

                return insert;

            }



        };
        AppController.getInstance().addToRequestQueue(stringRequest);

    }
    public void insert(final String s1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.user_resend_otp, new Response.Listener<String>() {
            public void onResponse(String response) {

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("user_mobile_number",s1);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
public void verifyclick(final String s1,final String s2,final String s3)
{
    new SweetAlertDialog(OTPVerify.this, SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Location Selection")
            .setContentText("Are you sure to use your location?")
            .setCancelText("No,Select New ")
            .setConfirmText("Yes,use it!")
            .showCancelButton(true)
            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sDialog) {
                    gps = new TrackGps(OTPVerify.this);

                     ulat = gps.getLatitude();
                     ulng = gps.getLongitude();
                    LatLng sydney = new LatLng(ulat, ulng);
                    Geocoder geocoder = new Geocoder(OTPVerify.this, Locale.getDefault());
                    // List<Address> addresses = null;
                    try {
                        List<Address> addresses = geocoder.getFromLocation(ulat,ulng, 1);
                        final String addressn = addresses.get(0).getSubLocality();
                        final String cityName = addresses.get(0).getLocality();
                        String stateName = addresses.get(0).getAdminArea();
                        final String adminArea = addresses.get(0).getAddressLine(0);
                        final String pincode=addresses.get(0).getPostalCode();

//                        Toast.makeText(getApplicationContext(), "Your selected area : " + address, Toast.LENGTH_SHORT).show();
                        final String alladd=adminArea+" "+addressn+" "+cityName+" "+pincode;

//                        Toast.makeText(getApplicationContext(), "Your selected city: " + cityName, Toast.LENGTH_SHORT).show();
                        new SweetAlertDialog(OTPVerify.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Your Location").setContentText(addressn).setConfirmText("OK")
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                                        Intent intentmm=new Intent(OTPVerify.this,Categories.class);
                                        rideme(s3,alladd);

                                        intentmm.putExtra("mobile_number",s1);
                                         intentmm.putExtra("otp_identification",s2);
                                        intentmm.putExtra("oneuid",s3);
                                        intentmm.putExtra("user_unamehome",user_uname);
                                        intentmm.putExtra("user_city", cityName);
                                        intentmm.putExtra("user_area", addressn);
                                        intentmm.putExtra("user_unamehome",uname4);
                                        intentmm.putExtra("updtaedimage",uname5);
                                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(OTPVerify.this);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("useruidentire",s3);
                                        editor.putString("usersseladdressfull",alladd);
                                        editor.putString("usersellatitude",Double.toString(ulat));
                                        editor.putString("usersellongitude",Double.toString(ulng));

                                        editor.putString("user_city", cityName);
                                     //   editor.putString("user_area", addressn);
                                     //   editor.putString("sharedaddress",addressn);
                                       // editor.putString("sharedprofileimages",user_sharedimage);
                                        editor.apply();
                                        startActivity(intentmm);
                                        finish();
                                    }
                                })
                                .show();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //  Toast.makeText(getApplicationContext(), "Your Location was ", Toast.LENGTH_SHORT).show();
                    sDialog.cancel();

                }
            }).setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
        @Override
        public void onClick(SweetAlertDialog sweetAlertDialog) {

            Intent intentm=new Intent(OTPVerify.this,UserSelMap.class);

            intentm.putExtra("mobile_number",s1);
            intentm.putExtra("otp_identification",s2);
            intentm.putExtra("oneuid",s3);

            startActivity(intentm);
            finish();


        }
    })
            .show();

}
    public void rideme(final String uidthree, final String s1) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalUrl.users_autolocation, new Response.Listener<String>() {
            public void onResponse(String response) {


            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error)
            { }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("user_uid",uidthree);
                params.put("user_address_home",s1);


                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }
}
