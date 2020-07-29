package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static android.provider.AlarmClock.EXTRA_MESSAGE;
import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;


public class RescueTeamLoginActivity extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    Switch show_password_switch;
    EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rescue_team_login);
        show_password_switch = findViewById(R.id.show_password_switch);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);

        show_password_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                password.setInputType();
            }
        });
    }


    public void loginaction(View view) {
        Intent intent = new Intent(this, RescueTeamDashboard.class);

        String user = username.getText().toString();
        String pswd = password.getText().toString();
        // 1. Instantiate the Stitch client


        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> teams =
                mongoClient.getDatabase("authorization").getCollection("rescue_team");
//        int count=0;
//        RemoteFindIterable docs= teams.find(and(eq("username", user), eq("password", pswd)));

        RemoteFindIterable findResults = teams.find(and(eq("username", user), eq("password", pswd)));
        Task <List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener <List<Document>> () {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Incorrect Sign In", "Wrong username or password");
                    }
                    else{
                        System.out.println(items.get(0));
                        Log.d("Correct Sign In", "Correct username and password");
                        intent.putExtra("username", user);
                        intent.putExtra("disaster_id", items.get(0).getString("disaster_id"));
                        startActivity(intent);
                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }



}
