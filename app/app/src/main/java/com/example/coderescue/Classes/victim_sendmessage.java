package com.example.coderescue.Classes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.coderescue.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class victim_sendmessage extends AppCompatActivity {
    private Button snd;
    private TextView msg;
    private int PERMISSIONS_REQUEST=1;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_sendmessage);

        final boolean[] check_once = {true};

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check_once[0]){
                    msg.setText("");
                    check_once[0] = false;
                }
            }
        });

        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);



        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSIONS_REQUEST);
        }
        else {
            getlocation();
        }
    }

    private void getlocation(){
        snd = (Button) findViewById(R.id.snd_msg);
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {

                            snd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(msg.getText()==""){
                                        // Toast pop-up
                                    }
                                    else{
                                        // send message
                                    }
                                }
                            });

                        }
                    }
                });
    }
}
