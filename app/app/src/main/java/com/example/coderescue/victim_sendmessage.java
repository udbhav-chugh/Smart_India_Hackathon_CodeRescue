package com.example.coderescue;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Button;

public class victim_sendmessage extends AppCompatActivity {
    private Button snd;
    private int PERMISSIONS_REQUEST=1;
    private LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_sendmessage);

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
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }
}
