package com.example.coderescue.Fragments;

import android.Manifest;
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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.SendMessageActivity;
import com.example.coderescue.Activities.VictimHomeActivity;
import com.example.coderescue.Classes.NetworkConnectivity;
import com.example.coderescue.Classes.ReceiveMessageUtility;
import com.example.coderescue.Classes.SendMessageUtility;
import com.example.coderescue.R;
import com.example.coderescue.VictimHomeAdapter;
import com.example.coderescue.VictimHomeCardModel;
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
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class VictimHomeFragment extends Fragment {

    private Button snd, button_send_msg;

    ArrayList<VictimHomeCardModel> models = new ArrayList<>();
    VictimHomeCardModel m;
    public static RemoteMongoClient mongoClient;
    public String lat, longi;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    public String deviceid;
    private ProgressBar prog;
    public static String state;
    RecyclerView mRecyclerView;
    VictimHomeAdapter myAdapter;
    Context c;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_victim_home, container, false);
        deviceid = "temp";
        c = getActivity();

        prog = root.findViewById(R.id.progressBar2);
        snd = root.findViewById(R.id.snd_msg);
        button_send_msg = root.findViewById(R.id.button_send_msg);
        mRecyclerView = root.findViewById(R.id.recylcerView5);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click();
            }
        });
        button_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    public void button_click() {
        String toastText = "No internet. Send a message to the helpline instead instead.";
        if (NetworkConnectivity.isInternetAvailable(getActivity()))
            toastText = "Internet Available";
        Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();

        if (!NetworkConnectivity.isInternetAvailable(getActivity())) {
            Intent intent = new Intent(getActivity(), SendMessageActivity.class);
            startActivity(intent);
        } else {
            if (ContextCompat.checkSelfPermission(
                    getActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
            } else {
                getCurrentLocation();
            }
        }
        ReceiveMessageUtility.checkPermissions(getActivity(), getActivity());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case SendMessageUtility.REQUEST_CODE_SEND_MESSAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (!NetworkConnectivity.isInternetAvailable(getActivity())) {
                        SendMessageUtility.sendMessage(getActivity(), getActivity(), "testing send message");
                    }
                } else {
                    Toast.makeText(getActivity(), "You don't have the required permissions for sending text messages", Toast.LENGTH_SHORT).show();
                }
            case ReceiveMessageUtility.REQUEST_CODE_RECEIVE_MESSAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: add code here
                } else {
                    Toast.makeText(getActivity(), "You don't have the required permissions for receiving text messages", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getCurrentLocation() {
        prog.setVisibility(View.VISIBLE);
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (getActivity() == null || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            lat = Double.toString(latitude);
                            longi = Double.toString(longitude);

                            Geocoder gcd = new Geocoder(getActivity(),
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
                    models.clear();
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
                    mRecyclerView.setAdapter(myAdapter);
                    prog.setVisibility(View.GONE);
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });



        System.out.println("wow2");
    }
}
