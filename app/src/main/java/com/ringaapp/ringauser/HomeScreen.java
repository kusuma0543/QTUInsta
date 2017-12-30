package com.ringaapp.ringauser;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class HomeScreen extends AbsRuntimePermission {

    Button home_butsignin,home_butsignup;
private static final int REQUEST_PERMISSION = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        requestAppPermissions(new String[]{
                        android.Manifest.permission.READ_SMS,

                        android.Manifest.permission.ACCESS_COARSE_LOCATION,

                        android.Manifest.permission.READ_EXTERNAL_STORAGE,

                Manifest.permission.READ_CALENDAR,
                Manifest.permission.CALL_PHONE,
            },

                        R.string.msg,REQUEST_PERMISSION);

        home_butsignin=(Button) findViewById(R.id.butsingin);
        home_butsignup=(Button) findViewById(R.id.butsignup);
        home_butsignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cont==1)
                {
                    Intent intent=new Intent(HomeScreen.this,LoginActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You need to enable permissions to move further", Toast.LENGTH_LONG).show();

                }

            }
        });
        home_butsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(cont==1)
                {
                    Intent intent=new Intent(HomeScreen.this,SignupActivity.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "You need to enable permissions to move further", Toast.LENGTH_LONG).show();

                }
            }
        });

    }
    @Override
    public void onPermissionsGranted(int requestCode) {
        //Do anything when permisson granted
        //Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_LONG).show();
    }
}