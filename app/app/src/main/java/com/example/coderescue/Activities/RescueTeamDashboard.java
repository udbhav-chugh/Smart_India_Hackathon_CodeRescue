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
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Classes.NetworkConnectivity;
import com.example.coderescue.Classes.ReceiveMessageUtility;
import com.example.coderescue.Classes.SendMessageUtility;
import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.VictimLocationAdapter;
import com.example.coderescue.VictimLocationCardModel;
import com.example.coderescue.R;
import com.example.coderescue.navar.PoiBrowserActivity;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.mod;

public class RescueTeamDashboard extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    Button snd2;
    String disaster_id;
    String username;
    RecyclerView mRecylcerView;
    VictimLocationAdapter myAdapter;
    Context c;
    double latvic,longivic;
    String latvics,longivics;
    ArrayList<VictimLocationCardModel> models = new ArrayList<>();
    VictimLocationCardModel m;
    private ProgressBar prog;
    ImageButton speak_msg;
    int flag;

    public String lat,longi;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    public static String state;
    soup.neumorphism.NeumorphButton button_ar_map, button_ar_camera;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_team_dashboard);
        speak_msg = findViewById(R.id.voiceBtn3);
        button_ar_map = findViewById(R.id.button_ar_map);
        button_ar_camera = findViewById(R.id.button_ar_camera);
        flag=0;

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        disaster_id = intent.getExtras().getString("disaster_id");
        username = intent.getExtras().getString("username");
        prog=findViewById(R.id.progressBar2);


        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.textView3);
//        textView.setText(message);
        mRecylcerView=findViewById(R.id.recylcerView2);
        c = this;
        mRecylcerView.setLayoutManager(new LinearLayoutManager(this));
        snd2=findViewById(R.id.snd_msg2);
        speak_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
        button_ar_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RescueTeamDashboard.this, GoogleMapActivity.class);
                startActivity(intent);
            }
        });
        button_ar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RescueTeamDashboard.this, PoiBrowserActivity.class);
                startActivity(intent);
            }
        });
    }


    public void button_click2(View view){
        String toastText = "No internet";
        if(NetworkConnectivity.isInternetAvailable(getApplicationContext())) toastText = "Internet Available";
        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

        if(!NetworkConnectivity.isInternetAvailable(getApplicationContext())){
//            SendMessageUtility.sendMessage(getApplicationContext(), RescueTeamDashboard.this, "testing send message");
        }
        else{
            if (ContextCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(RescueTeamDashboard.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                getCurrentLocation();
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case SendMessageUtility.REQUEST_CODE_SEND_MESSAGE_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if(!NetworkConnectivity.isInternetAvailable(getApplicationContext())){
                        SendMessageUtility.sendMessage(getApplicationContext(), RescueTeamDashboard.this, "testing send message");
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "You don't have the required permissions for sending text messages", Toast.LENGTH_SHORT).show();
                }
            case ReceiveMessageUtility.REQUEST_CODE_RECEIVE_MESSAGE_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //TODO: add code here
                }
                else{
                    Toast.makeText(getApplicationContext(), "You don't have the required permissions for receiving text messages", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getCurrentLocation(){
        prog.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(RescueTeamDashboard.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override

                    public void onLocationResult(LocationResult locationResult){
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(RescueTeamDashboard.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
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
//                                    //If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//                                    String address = addresses.get(0).getAddressLine(0);
//                                    String locality = addresses.get(0).getLocality();
//                                    String country = addresses.get(0).getCountryName();
//                                    String postalCode = addresses.get(0).getPostalCode();
//                                    String knownName = addresses.get(0).getFeatureName();
                                    state = addresses.get(0).getAdminArea();
                                    getVictims();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, Looper.getMainLooper());
    }

    public void getVictims(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> disasters = mongoClient.getDatabase("main").getCollection("victimsneedhelp");

        RemoteFindIterable findResults = disasters.find(eq("disaster_id", disaster_id));
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Incorrect", "Wrong disaster_id should not happen");
                    }
                    else{
                        System.out.println(items.get(0));
                        Log.d("Correct", "Correct disaster_id");
//                        TextView textView = findViewById(R.id.textView3);
                        Document first = items.get(0);
                        List<Document> temp = (List<Document>)first.get("victims");
                        models.clear();
                        for(Document i: temp){
                            if(i.getInteger("isactive")==1){
//                                textView.append(i.getString("latitude"));
//                                textView.append(i.getString("longitude"));
                                float[] results = new float[1];
                                latvic = Double.parseDouble(i.getString("latitude"));
                                longivic = Double.parseDouble(i.getString("longitude"));

                                Location.distanceBetween(Double.parseDouble(lat), Double.parseDouble(longi),
                                        latvic, longivic,
                                        results);
                                System.out.println(results[0]);
                                m = new VictimLocationCardModel();
                                m.setLatitude(i.getString("latitude"));
                                m.setLongitude(i.getString("longitude"));
                                m.setDistance(results[0]);
                                m.setTitle(results[0] + " m");
                                m.setRescueUsername(username);
                                m.setDescription("Latitude: " + latvic + "\n" + "Longitude: "+ longivic);
                                m.setDisaster_id(disaster_id);
                                models.add(m);
                                System.out.println(i);
                            }
                        }
                        Collections.sort(models, VictimLocationCardModel.DistSort);
                        latvics = models.get(0).getLatitude();
                        longivics = models.get(0).getLongitude();
                        latvic = Double.parseDouble(latvics);
                        longivic = Double.parseDouble(longivics);
                        if(flag==1){
                            flag=0;

                            final RemoteMongoCollection<Document> teams = mongoClient.getDatabase("main").getCollection("victimsneedhelp");

                            RemoteFindIterable findResults = teams.find(eq("disaster_id", disaster_id));
                            Task <List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
                            itemsTask.addOnCompleteListener(new OnCompleteListener <List<Document>> () {
                                @Override
                                public void onComplete(@NonNull Task<List<Document>> task) {
                                    if (task.isSuccessful()) {
                                        List<Document> items = task.getResult();
                                        int numDocs = items.size();
                                        if(numDocs==0){
                                            Log.d("Doesn't exist", "Should not happen");
                                        }
                                        else{
                                            System.out.println(items.get(0));
                                            Document first = items.get(0);
                                            final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                                            List<Document> temp = (List<Document>)first.get("victims");
                                            List<Document> temp2 = new ArrayList<Document>();
                                            int count=0;
                                            for(Document doc: temp){
                                                if(count==1 || !doc.getString("latitude").equals(latvics) || !doc.getString("longitude").equals(longivics))
                                                {
                                                    temp2.add(doc);
                                                }
                                                else
                                                {
                                                    count=1;
                                                    Document notactive = new Document()
                                                            .append("latitude", latvics)
                                                            .append("longitude", longivics)
                                                            .append("isactive", 0);
                                                    temp2.add(notactive);
                                                }
                                            }
                                            Log.d("Exists", "update");
                                            Document filterDoc = new Document().append("disaster_id", disaster_id);
                                            Document updateDoc = new Document().append("$set",
                                                    new Document()
                                                            .append("disaster_id", disaster_id)
                                                            .append("victims", temp2)
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
                                                    } else {
                                                        Log.e("app", "failed to update document with: ", task.getException());
                                                    }
                                                }
                                            });
                                        }
                                        Context context = c
                                                .getApplicationContext();
                                        CharSequence text = "Request Sent. Rescue Team Will Arrive as soon as possible!";
                                        int duration = Toast.LENGTH_LONG;

                                        Toast toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    } else {
                                        Log.e("app", "Failed to count documents with exception: ", task.getException());
                                    }
                                }
                            });
                            System.out.println("jai shree ram");
//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, longi);
                            String geoUri = "http://maps.google.com/maps?q=loc:" + latvic + "," + longivic + " (" + "label temp" + ")";

                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                            c.startActivity(intent);
                            prog.setVisibility(View.GONE);
                        }
                        else{
                            myAdapter=new VictimLocationAdapter(c,models);
                            mRecylcerView.setAdapter(myAdapter);
                            prog.setVisibility(View.GONE);
                        }

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
            Toast.makeText(RescueTeamDashboard.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        snd2.performClick();
                    }
                }
            }
        }
    }

}
