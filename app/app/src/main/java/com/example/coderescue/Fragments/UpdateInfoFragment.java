package com.example.coderescue.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.UpdateInfoActivity;
import com.example.coderescue.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class UpdateInfoFragment extends Fragment {

    EditText victim_count, location, disaster_type;
    soup.neumorphism.NeumorphButton submit;
    public static RemoteMongoClient mongoClient;
    private String TAG = "autocomplete places";
    TextView latitude, longitude;
    Double lat = 0.0, lon = 0.0;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_update_info, container, false);
        victim_count = root.findViewById(R.id.victim_count);
        location = root.findViewById(R.id.location);
        disaster_type = root.findViewById(R.id.disaster_type);
        latitude = root.findViewById(R.id.latitude);
        longitude = root.findViewById(R.id.longitude);
        submit = root.findViewById(R.id.submit_info);
        String apiKey = "AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";

        if (ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
        getCurrentLocation();
        latitude.setText(Double.toString(lat));
        longitude.setText(Double.toString(lon));

        Activity activity = getActivity();
        if (activity != null) {
            Places.initialize(getActivity(), apiKey);
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                    Log.d(TAG, "intent starting");
                    Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                            .build(activity);
                    startActivityForResult(intent, 100);
                }
            });
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVictimInfo();
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Place place = Autocomplete.getPlaceFromIntent(data);
            location.setText(place.getAddress());
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            latitude.setText(Double.toString(lat));
            longitude.setText(Double.toString(lon));
//            Toast.makeText(getActivity(), String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
        } else {
            Status status = data == null ? null : Autocomplete.getStatusFromIntent(data);
            Log.e(TAG, (status == null || status.getStatusMessage() == null) ? "null" : status.getStatusMessage());
        }
    }

    private void updateVictimInfo() {
        String loc = location.getText().toString().trim();
        int count = Integer.parseInt(victim_count.getText().toString().trim());
        String disaster = disaster_type.getText().toString().trim();

        Document document = new Document();
        document.put("latitude", lat);
        document.put("longitude", lon);
        document.put("victim_count", count);
        document.put("disaster", disaster);
        //TODO: if this disaster is not present in db, add it
        //TODO: change disaster to a spinner? which is populated from database

        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        mongoClient
                .getDatabase("main")
                .getCollection("victim")
                .insertOne(document)
                .addOnSuccessListener(new OnSuccessListener<RemoteInsertOneResult>() {
                    @Override
                    public void onSuccess(RemoteInsertOneResult remoteInsertOneResult) {
                        if (remoteInsertOneResult != null)
                            Log.d("update victim info", "Victim info updated with id: " + remoteInsertOneResult.getInsertedId().toString().trim());
                        else Log.e("update victim info", "Error in updating victim info");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("update victim info", "Error in updating victim info : " + e.getMessage());
                    }
                });
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(getActivity())
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override

                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            lat = latitude;
                            lon = longitude;
                        }
                    }
                }, Looper.getMainLooper());
    }

}
