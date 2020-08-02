package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.coderescue.Classes.NetworkConnectivity;
import com.example.coderescue.Classes.ReceiveMessageUtility;
import com.example.coderescue.Classes.SendMessageUtility;
import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.SafeHouseAdapter;
import com.example.coderescue.SafeHouseCardModel;
import com.example.coderescue.R;
import com.example.coderescue.navar.PoiBrowserActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class ViewSafeHousesActivity extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    RecyclerView recyclerView;
    SafeHouseAdapter myAdapter;
    Context context;
    double latvic, longivic;
    String latvics, longivics;
    ArrayList<SafeHouseCardModel> models = new ArrayList<>();
    SafeHouseCardModel safeHouseCardModel;
    private ProgressBar prog;
    ImageButton speak_msg;
    int flag;

    public String lat, longi;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    public static String state;
    soup.neumorphism.NeumorphButton button_ar_map, button_ar_camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_safe_houses);
        speak_msg = findViewById(R.id.voiceBtn3);
        button_ar_map = findViewById(R.id.button_ar_map);
        button_ar_camera = findViewById(R.id.button_ar_camera);
        prog = findViewById(R.id.progressBar2);
        recyclerView = findViewById(R.id.recylcerView2);

        flag = 0;
        context = this;

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String toastText = "No internet";
        if (NetworkConnectivity.isInternetAvailable(ViewSafeHousesActivity.this))
            toastText = "Internet Available";
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

        if (!NetworkConnectivity.isInternetAvailable(ViewSafeHousesActivity.this)) {
//            SendMessageUtility.sendMessage(ViewSafeHousesActivity.this, RescueTeamDashboard.this, "testing send message");
        } else {
            if (ContextCompat.checkSelfPermission(
                    ViewSafeHousesActivity.this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ViewSafeHousesActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                getCurrentLocation();
            }
        }
        speak_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        button_ar_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSafeHousesActivity.this, GoogleMapActivity.class);
                startActivity(intent);
            }
        });
        button_ar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewSafeHousesActivity.this, PoiBrowserActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SendMessageUtility.REQUEST_CODE_SEND_MESSAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!NetworkConnectivity.isInternetAvailable(ViewSafeHousesActivity.this)) {
                        SendMessageUtility.sendMessage(ViewSafeHousesActivity.this, ViewSafeHousesActivity.this, "testing send message");
                    }
                } else {
                    Toast.makeText(ViewSafeHousesActivity.this, "You don't have the required permissions for sending text messages", Toast.LENGTH_SHORT).show();
                }
            case ReceiveMessageUtility.REQUEST_CODE_RECEIVE_MESSAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: add code here
                } else {
                    Toast.makeText(ViewSafeHousesActivity.this, "You don't have the required permissions for receiving text messages", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getCurrentLocation() {
        prog.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.getFusedLocationProviderClient(ViewSafeHousesActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback() {
                    @Override

                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(ViewSafeHousesActivity.this)
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            lat = Double.toString(latitude);
                            longi = Double.toString(longitude);

                            Geocoder gcd = new Geocoder(getBaseContext(),
                                    Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(latitude,
                                        longitude, 1);
                                if (addresses.size() > 0) {
                                    state = addresses.get(0).getAdminArea();
                                    getSafeHouses();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }


    public void getSafeHouses(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> allSafeHouses = mongoClient.getDatabase("main").getCollection("safeHouses");

        RemoteFindIterable findResults = allSafeHouses.find(eq("state", state));

        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    Log.d("safe houses", items.toString());

                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Incorrect", "Wrong disaster_id should not happen");
                    }
                    else{
                        System.out.println(items.get(0));
                        Document first = items.get(0);
                        List<Document> temp = (List<Document>)first.get("safehouse");
                        models.clear();
                        if(temp != null) {
                            for (Document i : temp) {
                                float[] results = new float[1];
                                latvic = Double.parseDouble(i.getString("latitude"));
                                longivic = Double.parseDouble(i.getString("longitude"));

                                Location.distanceBetween(Double.parseDouble(lat), Double.parseDouble(longi),
                                        latvic, longivic,
                                        results);
                                safeHouseCardModel = new SafeHouseCardModel();
                                safeHouseCardModel.setLatitude(i.getString("latitude"));
                                safeHouseCardModel.setLongitude(i.getString("longitude"));
                                safeHouseCardModel.setDistance(results[0]);
                                safeHouseCardModel.setState(state);
                                safeHouseCardModel.setName(i.getString("name"));
                                models.add(safeHouseCardModel);
                                System.out.println(i);
                            }
                        }
                        Collections.sort(models, SafeHouseCardModel.DistSort);
                        latvics = models.get(0).getLatitude();
                        longivics = models.get(0).getLongitude();
                        latvic = Double.parseDouble(latvics);
                        longivic = Double.parseDouble(longivics);

                        myAdapter=new SafeHouseAdapter(context,models);
                        recyclerView.setAdapter(myAdapter);
                        prog.setVisibility(View.GONE);
                    }

                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }

    private void speak(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e){
            Toast.makeText(ViewSafeHousesActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == -1 && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spoken = result.get(0);
                    if(spoken.contains("near")){
                        flag=1;
//                        snd2.performClick();
                    }
                }
            }
        }
    }


}
