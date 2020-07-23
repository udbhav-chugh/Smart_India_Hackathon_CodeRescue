package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.example.coderescue.VictimLocationAdapter;
import com.example.coderescue.VictimLocationCardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class PathToVictimActivity extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_to_victim);
        Intent i=getIntent();
        String lat=i.getExtras().getString("latitude");
        String longi=i.getExtras().getString("longitude");
        username = i.getExtras().getString("username");
        TextView victimtext = findViewById(R.id.textViewVictim);
        victimtext.setText(lat + "\n" + longi);
        rescueCoordinates();

    }

    public void rescueCoordinates(){
        TextView rescuetext = findViewById(R.id.textViewRescue);
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> rescueTeams = mongoClient.getDatabase("authorization").getCollection("rescue_team");

        RemoteFindIterable findResults = rescueTeams.find(eq("username", username));
        Task<List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener<List<Document>>() {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Incorrect", "Wrong username should not happen");
                    }
                    else{
                        System.out.println(items.get(0));
                        Log.d("Correct", "Correct disaster_id");
//                        TextView textView = findViewById(R.id.textView3);
                        Document first = items.get(0);
                        Document temp = (Document)first.get("coordinates");
                        String lat = temp.getString("latitude");
                        String longi = temp.getString("longitude");
                        rescuetext.setText(lat + "\n" + longi);

                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });

    }
}
