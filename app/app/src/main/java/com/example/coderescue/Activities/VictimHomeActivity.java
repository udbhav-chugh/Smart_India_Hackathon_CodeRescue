package com.example.coderescue.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;

public class VictimHomeActivity extends AppCompatActivity {
    private Button snd;
    //    private TextView msg;
    private int PERMISSIONS_REQUEST = 1;
    private LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationClient;
    public static RemoteMongoClient mongoClient;
    private Location local;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_home);

//        final boolean[] check_once = {true};
//
//        msg.setOnClickListener(new View.OnClickListener() {
//            @Override                                                     //IGNORE THIS
//            public void onClick(View v) {
//                if (check_once[0]) {
//                    msg.setText("");
//                    check_once[0] = false;
//                }
//            }
//        });

        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST);
        } else {
            getlocation();
        }
    }

    private void getlocation() {
        snd = (Button) findViewById(R.id.snd_msg);
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Context context = getApplicationContext();
            CharSequence text = "Location Permissions not granted! Restart app and try again";
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            local = location;
                            Context context = getApplicationContext();
                            CharSequence text = "Location Permissions granted!";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                        }
                    }
                });

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (local != null) {
                    // SEND LOCATION AS MESSAGE

                    snd.setClickable(false);
                } else {
                    // pop-up a TOAST
                    Context context = getApplicationContext();
                    CharSequence text = "Wri";
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}
