package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class RescueTeamLoginActivity extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rescue_team_login);

    }


    public void loginaction(View view) {
        Intent intent = new Intent(this, RescueTeamDashboard.class);
        EditText username,password;
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        String user = username.getText().toString();
        String pswd = password.getText().toString();
        // 1. Instantiate the Stitch client


        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> teams =
                mongoClient.getDatabase("authorization").getCollection("rescue_team");
//        int count=0;
//        RemoteFindIterable docs= teams.find(and(eq("username", user), eq("password", pswd)));

        teams.count(and(eq("username", user), eq("password", pswd))).addOnCompleteListener(new OnCompleteListener <Long> () {
            @Override
            public void onComplete(@NonNull Task <Long> task) {
                if (task.isSuccessful()) {
                    Long numDocs = task.getResult();
                    if(numDocs==0){
                        Log.d("Incorrect Sign In", "Wrong username or password");
                    }
                    else{
                        Log.d("Correct Sign In", "Correct username and password");
                        intent.putExtra(EXTRA_MESSAGE, user);
                        startActivity(intent);
                    }
                    Log.d("app", String.format("%s items have a review.", numDocs.toString()));
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }



}
