package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
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

public class UpdateInfoActivity extends AppCompatActivity {

    EditText victim_count, location, disaster_type;
    TextView latitude, longitude;
    Double lat = 0.0, lon = 0.0;
//    RecyclerView disaster_type;
    soup.neumorphism.NeumorphButton submit;
    public static RemoteMongoClient mongoClient;
    private String TAG = "autocomplete places";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
        victim_count = findViewById(R.id.victim_count);
        location = findViewById(R.id.location);
        disaster_type = findViewById(R.id.disaster_type);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        submit = findViewById(R.id.submit_info);

//        disaster_type.setLayoutManager(new LinearLayoutManager(UpdateInfoActivity.this));

        String apiKey = "AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";

        Places.initialize(UpdateInfoActivity.this, apiKey);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
                Log.d(TAG, "intent starting");
                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
                        .build(UpdateInfoActivity.this);
                startActivityForResult(intent, 100);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVictimInfo();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 100 && resultCode == RESULT_OK && data != null){
            Place place = Autocomplete.getPlaceFromIntent(data);
            location.setText(place.getAddress());
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            latitude.setText(Double.toString(lat));
            longitude.setText(Double.toString(lon));
//            Toast.makeText(this, String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
        }
        else{
            Status status = data == null ? null : Autocomplete.getStatusFromIntent(data);
            Log.e(TAG, (status == null || status.getStatusMessage() == null) ? "null" : status.getStatusMessage());
        }
    }


    private void updateVictimInfo() {
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
                        if(remoteInsertOneResult != null) Log.d("update victim info", "Victim info updated with id: " + remoteInsertOneResult.getInsertedId().toString().trim());
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

//    @Override
//    public void onClick(String disaster) {
//        updateVictimInfo(disaster);
//    }
}