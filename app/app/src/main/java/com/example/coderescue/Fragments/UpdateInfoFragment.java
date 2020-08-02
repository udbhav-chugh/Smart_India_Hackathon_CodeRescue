package com.example.coderescue.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.HomeActivity;
import com.example.coderescue.Activities.UpdateInfoActivity;
import com.example.coderescue.R;
import com.example.coderescue.VictimHomeAdapter;
import com.example.coderescue.VictimHomeCardModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class UpdateInfoFragment extends Fragment {

    EditText victim_count, location;
    Spinner spinner;
    soup.neumorphism.NeumorphButton submit;
    public static RemoteMongoClient mongoClient;
    private String TAG = "autocomplete places";
    TextView latitude, longitude;
    Double lat = 0.0, lon = 0.0;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    ArrayList<String> arrayList;
    ArrayList<String> arrayList2;
    ArrayAdapter<String> arrayAdapter;
    String dis_id;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_update_info, container, false);
        victim_count = root.findViewById(R.id.victim_count);
        location = root.findViewById(R.id.location);
        latitude = root.findViewById(R.id.latitude);
        longitude = root.findViewById(R.id.longitude);
        submit = root.findViewById(R.id.submit_info);
        dis_id = "unique_id_4";
        String apiKey = "AIzaSyDYoQybddM6c-Daz0bHVe7h2tuyzxHmW1k";

        spinner = root.findViewById(R.id.spinner);
        arrayList = new ArrayList<>();
        arrayList2 = new ArrayList<>();

                arrayList.add("JAVA");
                arrayList2.add("lol");
//        arrayList.add("ANDROID");
//        arrayList.add("C Language");
//        arrayList.add("CPP Language");
//        arrayList.add("Go Language");
//        arrayList.add("AVN SYSTEMS");
//
        arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        getDisasters();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                String tutorialsName = parent.getItemAtPosition(position).toString();
//                Toast.makeText(parent.getContext(), "Selected: " + arrayList2.get(position),          Toast.LENGTH_LONG).show();
                dis_id = arrayList2.get(position);
                System.out.println(dis_id + "cur disaster");

            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        if (ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
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
            DecimalFormat df = new DecimalFormat("#.########");
            lat = place.getLatLng().latitude;
            lon = place.getLatLng().longitude;
            lat=(double)Math.round(lat * 100000000d) / 100000000d;
            lon=(double)Math.round(lon * 100000000d) / 100000000d;
            latitude.setText(Double.toString(lat));
            longitude.setText(Double.toString(lon));
//            Toast.makeText(getActivity(), String.valueOf(place.getLatLng()), Toast.LENGTH_SHORT).show();
        } else {
            Status status = data == null ? null : Autocomplete.getStatusFromIntent(data);
            Log.e(TAG, (status == null || status.getStatusMessage() == null) ? "null" : status.getStatusMessage());
        }
    }

    public void updateVictimInfo() {
        int count = Integer.parseInt(victim_count.getText().toString().trim());

        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> teams = mongoClient.getDatabase("main").getCollection("victimsneedhelp");

        RemoteFindIterable findResults = teams.find(eq("disaster_id", dis_id));
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Doesn't exist", "Insert");
                        final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                        Document newItem = new Document()
                                .append("disaster_id", dis_id)
                                .append("victims", Arrays.asList(
                                        new Document()
                                                .append("latitude", Double.toString(lat))
                                                .append("longitude", Double.toString(lon))
                                                .append("count",count)
                                                .append("isactive", 1)
                                ));


                        final Task<RemoteInsertOneResult> insertTask = victimneedhelp.insertOne(newItem);
                        insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
                            @Override
                            public void onComplete(@com.mongodb.lang.NonNull Task <RemoteInsertOneResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("app", String.format("successfully inserted item with id %s",
                                            task.getResult().getInsertedId()));
                                } else {
                                    Log.e("app", "failed to insert document with: ", task.getException());
                                }
                            }
                        });
                    }
                    else{
                        System.out.println(items.get(0));
                        Document first = items.get(0);
                        final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                        List<Document> temp = (List<Document>)first.get("victims");
                        Document newvic = new Document().append("latitude",Double.toString(lat)).append("longitude",Double.toString(lon)).append("count",count).append("isactive",1);
                        temp.add(newvic);
                        Log.d("Exists", "update");
                        Document filterDoc = new Document().append("disaster_id", dis_id);
                        Document updateDoc = new Document().append("$set",
                                new Document()
                                        .append("disaster_id", dis_id)
                                        .append("victims", temp)
                        );

                        final Task<RemoteUpdateResult> updateTask =
                                victimneedhelp.updateOne(filterDoc, updateDoc);
                        updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
                            @Override
                            public void onComplete(@NonNull Task <RemoteUpdateResult> task) {
                                if (task.isSuccessful()) {
                                    long numMatched = task.getResult().getMatchedCount();
                                    long numModified = task.getResult().getModifiedCount();
                                    Log.d("app", String.format("successfully matched %d and modified %d documents",
                                            numMatched, numModified));

                                Toast.makeText(getActivity(), "Submitted Successfully",          Toast.LENGTH_LONG).show();
                                } else {
                                    Log.e("app", "failed to update document with: ", task.getException());
                                }
                            }
                        });
                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
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
                            double latt = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longg = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            lat = latt;
                            lon = longg;
                            latitude.setText(Double.toString(lat));
                            longitude.setText(Double.toString(lon));
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void getDisasters(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> disasters = mongoClient.getDatabase("main").getCollection("disaster");
//        final RemoteMongoCollection<Document> notifications = mongoClient.getDatabase("main").getCollection("notification");

        RemoteFindIterable findResults = disasters.find(eq("isactive", 1));
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    arrayList.clear();
                    arrayList2.clear();
                    for(Document i: items){
                        String dis_name = i.getString("name");
                        String dis_id = i.getString("id");
                        arrayList.add(dis_name);
                        arrayList2.add(dis_id);
                        System.out.println(i);
                    }
                    arrayAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, arrayList);
                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(arrayAdapter);
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });



        System.out.println("wow2");
    }

}
