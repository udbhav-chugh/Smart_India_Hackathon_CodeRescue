package com.example.coderescue.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Looper;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.SendMessageActivity;
import com.example.coderescue.Activities.VictimHomeActivity;
import com.example.coderescue.Classes.DisasterSpinnerCardModel;
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

    private soup.neumorphism.NeumorphButton snd, button_send_msg;

    ArrayList<VictimHomeCardModel> models = new ArrayList<>();
    VictimHomeCardModel m;
    public static RemoteMongoClient mongoClient;
    public String lat, longi;
    double latitude, longitude;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    public String deviceid;
    private ProgressBar prog;
    public static String state;
    RecyclerView mRecyclerView;
    VictimHomeAdapter myAdapter;
    Context c;
    EditText msg_input;
    soup.neumorphism.NeumorphImageButton voiceBtn2;
    CardView send;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_victim_home, container, false);
        deviceid = "temp";
        c = getActivity();

        prog = root.findViewById(R.id.progressBar2);
        snd = root.findViewById(R.id.snd_msg);
        button_send_msg = root.findViewById(R.id.snd_msg2);
        mRecyclerView = root.findViewById(R.id.recylcerView5);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        snd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button_click();
                snd.setEnabled(false);
            }
        });
        button_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SendMessageActivity.class);
//                startActivity(intent);
                send_message_dialog();
            }
        });
        return root;
    }

    public void send_message_dialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getLayoutInflater().inflate(R.layout.message_dialog, null);

        msg_input = mView.findViewById(R.id.msg_input);
        voiceBtn2 = mView.findViewById(R.id.voiceBtn2);
        send = mView.findViewById(R.id.send_msg);

        if (ContextCompat.checkSelfPermission(
                getActivity(), Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        } else {
            getCurrentLocation_Send_Message();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inital = "";
                if (latitude != -1) {
                    inital += "Latitude: " + latitude + "\n" + "Longitude: " + longitude + "\n";
                    String mapsurl = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude;
                    inital += "Maps Link: " + mapsurl + "\n";
                }
                inital += "Message: \n";
                String message = inital + msg_input.getText().toString();
                SendMessageUtility.sendMessage(getActivity(), getActivity(), message);
                String toastText = "Message sent successfully";
                Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
                msg_input.setText("");
            }
        });

        voiceBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void getCurrentLocation_Send_Message() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            longitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            System.out.println("aagyi location");
                            Geocoder gcd = new Geocoder(getActivity(),
                                    Locale.getDefault());
                            List<Address> addresses;
                            try {
                                addresses = gcd.getFromLocation(latitude,
                                        longitude, 1);
                                if (addresses.size() > 0) {
                                    state = addresses.get(0).getAdminArea();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, Looper.getMainLooper());
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
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    msg_input.setText(spoken);
                    send.performClick();
                }
            }
        }
    }

    public void button_click() {
        String toastText = "No internet. Send a message to the helpline instead instead.";
        if (NetworkConnectivity.isInternetAvailable(getActivity())) {
            toastText = "Internet Available";
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getActivity(), toastText, Toast.LENGTH_LONG).show();

        if (!NetworkConnectivity.isInternetAvailable(getActivity())) {
            send_message_dialog();
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
            case REQUEST_CODE_LOCATION_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation_Send_Message();
                }
                else  Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();

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
                        m.setCategory(i.getString("category"));
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
