package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
//import com.google.android.libraries.places.api.Places;
//import com.google.android.libraries.places.api.model.AutocompletePrediction;
//import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
//import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.model.RectangularBounds;
//import com.google.android.libraries.places.api.model.TypeFilter;
//import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
//import com.google.android.libraries.places.api.net.PlacesClient;
//import com.google.android.libraries.places.widget.Autocomplete;
//import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
//import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
//import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class UpdateInfoActivity extends AppCompatActivity {

    EditText victim_count, location, disaster_type;
    Button submit;
    public static RemoteMongoClient mongoClient;
    private StringBuilder mResult;
    private String TAG = "autocomplete places";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);

//        if(getIntent().hasExtra(GoogleSignInActivity.KEY_EXTRA)){
//            Log.d("google sign in", getIntent().getStringExtra(GoogleSignInActivity.KEY_EXTRA));
//        }else{
//            Log.e("google sign in", "No account bundle in intent");
//        }

        victim_count = findViewById(R.id.victim_count);
        location = findViewById(R.id.location);
        disaster_type = findViewById(R.id.disaster_type);
        submit = findViewById(R.id.submit_info);
        String apiKey = getString(R.string.google_maps_key);
//        String apiKey = "AIzaSyCYd9DNtP8fAnic_H5XwgCef7dmqj_7vB0";
//        Places.initialize(getApplicationContext(), apiKey);
//
//        location.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                List<Place.Field> fieldList = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.NAME);
//                Log.d(TAG, "intent starting");
//                Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList)
//                        .build(UpdateInfoActivity.this);
//                startActivityForResult(intent, 100);
//            }
//        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVictimInfo();
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if( requestCode == 100 && resultCode == RESULT_OK && data != null){
//            Place place = Autocomplete.getPlaceFromIntent(data);
//            location.setText(place.getAddress());
//            Toast.makeText(this, String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
//        }
//        else{
//            Status status = data == null ? null : Autocomplete.getStatusFromIntent(data);
//            Log.e(TAG, (status == null || status.getStatusMessage() == null) ? "null" : status.getStatusMessage());
//        }
//    }

    private void updateVictimInfo() {
        String loc = location.getText().toString().trim();
        int count = Integer.parseInt(victim_count.getText().toString().trim());
        String disaster = disaster_type.getText().toString().trim();

        Document document = new Document();
        document.put("location", loc);
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
}