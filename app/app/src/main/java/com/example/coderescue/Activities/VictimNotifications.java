package com.example.coderescue.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.NotificationAdapter;
import com.example.coderescue.NotificationCardModel;
import com.example.coderescue.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

public class VictimNotifications extends AppCompatActivity {

    ArrayList<NotificationCardModel> models = new ArrayList<>();
    NotificationCardModel m;
    public static RemoteMongoClient mongoClient;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private TextView textLatLong;
    private TextView textState;
    private ProgressBar prog;
    public static String state;
    RecyclerView mRecylcerView;
    NotificationAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_notifications);
        textLatLong = findViewById(R.id.textViewLatLong);
        textState = findViewById(R.id.textViewAddress);
        prog=findViewById(R.id.progressBar);
        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(VictimNotifications.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation();
        }
        m = new NotificationCardModel();
        m.setTitle("News Feed");
        m.setDescription("This is hello feed description ..");
        models.add(m);

        mRecylcerView=findViewById(R.id.recylcerView);
        mRecylcerView.setLayoutManager(new LinearLayoutManager(this));

        myAdapter=new NotificationAdapter(this,models);
        mRecylcerView.setAdapter(myAdapter);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation(){
        prog.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(VictimNotifications.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override

                    public void onLocationResult(LocationResult locationResult){
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(VictimNotifications.this)
                                .removeLocationUpdates(this);
                        if(locationResult != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            textLatLong.setText(
                                    String.format(
                                            "Latitude %s\n Longitude %s",
                                            latitude, longitude
                                    )
                            );

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
                                    textState.setText(
                                            String.format(
                                                    "State: %s",
                                                    state
                                            )
                                    );
                                    getNotifications();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        prog.setVisibility(View.GONE);
                    }
                }, Looper.getMainLooper());
    }

    public void getNotifications(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> disasters = mongoClient.getDatabase("main").getCollection("disaster");
        final RemoteMongoCollection<Document> notifications = mongoClient.getDatabase("main").getCollection("notification");

        RemoteFindIterable<Document> importantDisasters = disasters.find(eq("location",state));

        RemoteFindIterable findResults = notifications.find();
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                        for(Document item: items){
                            if(item.getString("directed_from").equals("headquarters") && item.getInteger("is_disaster")==0 && item.getString("location").equals(state)){
                                Log.d("app", String.format("successfully found:  %s", item.toString()));
                                m = new NotificationCardModel();
                                m.setTitle("News Feed");
                                m.setDescription("This is news feed description ..");
                                models.add(m);
                                System.out.println(models.size());
//                                myAdapter=new NotificationAdapter(this,models);
//                                myAdapter.notifyDataSetChanged();
//                                mRecylcerView.setAdapter(myAdapter);
                            }

                            else if (item.getString("directed_from").equals("headquarters") && item.getInteger("is_disaster")==1)
                            {
                                importantDisasters.forEach(itemDisaster -> {
                                    if( itemDisaster.getString("name").equals(item.getString("name")) )
                                    {
                                        ArrayList<String> locArray = (ArrayList<String>) itemDisaster.get("location");
                                        if(locArray.contains(state))
                                            Log.d("app", String.format("successfully found:  %s", item.toString()));
                                        m = new NotificationCardModel();
                                        m.setTitle("News Feed");
                                        m.setDescription("This is news feed description lol..");
                                        models.add(m);
                                    }
                                });
                            }
                        }

                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
        System.out.println("wow");




    }
}
