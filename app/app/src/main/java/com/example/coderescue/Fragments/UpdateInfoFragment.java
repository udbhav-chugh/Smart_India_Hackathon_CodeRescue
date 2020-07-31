package com.example.coderescue.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderescue.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;

public class UpdateInfoFragment extends Fragment {

    EditText victim_count, location, disaster_type;
    Button submit;
    public static RemoteMongoClient mongoClient;
    private StringBuilder mResult;
    private String TAG = "autocomplete places";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_update_info, container, false);
        victim_count = root.findViewById(R.id.victim_count);
        location = root.findViewById(R.id.location);
        disaster_type = root.findViewById(R.id.disaster_type);
        submit = root.findViewById(R.id.submit_info);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVictimInfo();
            }
        });
        return root;
    }

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
