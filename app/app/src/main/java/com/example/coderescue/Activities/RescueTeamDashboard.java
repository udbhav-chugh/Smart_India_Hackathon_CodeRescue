package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.VictimLocationAdapter;
import com.example.coderescue.VictimLocationCardModel;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class RescueTeamDashboard extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    String message;
    RecyclerView mRecylcerView;
    VictimLocationAdapter myAdapter;
    Context c;
    ArrayList<VictimLocationCardModel> models = new ArrayList<>();
    VictimLocationCardModel m;
    private ProgressBar prog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rescue_team_dashboard);

        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        message = intent.getStringExtra(RescueTeamLoginActivity.EXTRA_MESSAGE);
        prog=findViewById(R.id.progressBar2);

        // Capture the layout's TextView and set the string as its text
//        TextView textView = findViewById(R.id.textView3);
//        textView.setText(message);
        mRecylcerView=findViewById(R.id.recylcerView2);
        c = this;
        mRecylcerView.setLayoutManager(new LinearLayoutManager(this));
        prog.setVisibility(View.VISIBLE);
        getVictims();
    }
    public void getVictims(){
        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");
        final RemoteMongoCollection<Document> disasters = mongoClient.getDatabase("main").getCollection("victimNeedHelp");

        RemoteFindIterable findResults = disasters.find(eq("disaster_id", message));
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
                        for(Document i: temp){
                            if(i.getInteger("isactive")==1){
//                                textView.append(i.getString("latitude"));
//                                textView.append(i.getString("longitude"));
                                m = new VictimLocationCardModel();
                                m.setTitle(i.getString("latitude"));
                                m.setDescription(i.getString("longitude"));
                                models.add(m);
                                System.out.println(i);
                            }
                        }
                        myAdapter=new VictimLocationAdapter(c,models);
                        mRecylcerView.setAdapter(myAdapter);
                        prog.setVisibility(View.GONE);

                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }

}
