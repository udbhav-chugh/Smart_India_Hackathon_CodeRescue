package com.example.coderescue.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Classes.NetworkConnectivity;
import com.example.coderescue.Classes.victimNeedHelp;
import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.NotificationCardModel;
import com.example.coderescue.R;
import com.example.coderescue.VictimHomeAdapter;
import com.example.coderescue.VictimHomeCardModel;
import com.example.coderescue.VictimLocationAdapter;
import com.example.coderescue.VictimLocationCardModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.lang.NonNull;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class VictimHomeActivity extends AppCompatActivity {
    private Button snd;

    ArrayList<VictimHomeCardModel> models = new ArrayList<>();
    VictimHomeCardModel m;
    public static RemoteMongoClient mongoClient;
    public String lat,longi;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private ProgressBar prog;
    public static String state;
    RecyclerView mRecylcerView;
    VictimHomeAdapter myAdapter;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_home);
        prog=findViewById(R.id.progressBar2);
        mRecylcerView=findViewById(R.id.recylcerView5);
        c = this;
        mRecylcerView.setLayoutManager(new LinearLayoutManager(this));

        String toastText = "No internet";
        if(NetworkConnectivity.isInternetAvailable(getApplicationContext())) toastText = "Internet Available";

        Toast.makeText(this, toastText, Toast.LENGTH_SHORT).show();

//        if (ContextCompat.checkSelfPermission(
//                getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(VictimHomeActivity.this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
//        } else {
//            getCurrentLocation();
//        }

    }



    private void getCurrentLocation(){
        prog.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(VictimHomeActivity.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){
                    @Override

                    public void onLocationResult(LocationResult locationResult){
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(VictimHomeActivity.this)
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
                                    getDisasters();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        prog.setVisibility(View.GONE);
                    }
                }, Looper.getMainLooper());
    }

    public void getDisasters(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> disasters = mongoClient.getDatabase("main").getCollection("disaster");
//        final RemoteMongoCollection<Document> notifications = mongoClient.getDatabase("main").getCollection("notification");

        RemoteFindIterable findResults = disasters.find(and(eq("location", state), eq("isactive", 1)));
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@androidx.annotation.NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    for(Document i: items){
                        String dis_name = i.getString("name");
                        String dis_id = i.getString("id");
                            m = new VictimHomeCardModel();
                            m.setTitle(dis_name);
                            m.setDescription(dis_id);
                            m.setLatitude(lat);
                            m.setLongitude(longi);
                            models.add(m);
                            System.out.println(i);
                    }
                    myAdapter=new VictimHomeAdapter(c,models);
                    mRecylcerView.setAdapter(myAdapter);
                    prog.setVisibility(View.GONE);
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });



        System.out.println("wow2");
    }
}
