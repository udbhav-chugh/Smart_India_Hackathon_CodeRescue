package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;

import org.bson.Document;

import java.util.Collection;

public class UpdateInfoActivity extends AppCompatActivity {

    EditText victim_count, location, disaster_type;
    Button submit;
    public static RemoteMongoClient mongoClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_info);
//
//        if(getIntent().hasExtra(GoogleSignInActivity.KEY_EXTRA)){
//            Log.d("google sign in", getIntent().getStringExtra(GoogleSignInActivity.KEY_EXTRA));
//        }else{
//            Log.e("google sign in", "No account bundle in intent");
//        }

        victim_count = findViewById(R.id.victim_count);
        location = findViewById(R.id.location);
        disaster_type = findViewById(R.id.disaster_type);
        submit = findViewById(R.id.submit_info);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateVictimInfo();
            }
        });
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