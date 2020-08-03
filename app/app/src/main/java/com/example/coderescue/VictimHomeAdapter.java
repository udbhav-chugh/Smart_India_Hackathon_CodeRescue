package com.example.coderescue;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.PathToVictimActivity;
import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.Fragments.UpdateInfoFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteInsertOneResult;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class VictimHomeAdapter extends RecyclerView.Adapter<VictimHomeHolder>{

    Context c;
    public static RemoteMongoClient mongoClient;
    ArrayList<VictimHomeCardModel> models;

    public VictimHomeAdapter(Context c, ArrayList<VictimHomeCardModel> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public VictimHomeHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.victim_home_card, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new VictimHomeHolder(view);
    }

    int getDisasterSpinnerDrawable(String category){
        switch (category){
            case "avalanche": return R.drawable.disaster_avalanche;
            case "blizzard": return  R.drawable.disaster_blizzard;
            case "desertification": return  R.drawable.disaster_desertification;
            case "earthquake": return  R.drawable.disaster_earthquake;
            case "flood": return  R.drawable.disaster_flood;
            case "hurricane": return  R.drawable.disaster_hurricane;
            case "landslide": return  R.drawable.disaster_landslide;
            case "meteorites": return  R.drawable.disaster_meteorite;
            case "rain": return  R.drawable.disaster_rain;
            case "smog": return  R.drawable.disaster_smog;
            case "snow": return  R.drawable.disaster_snow;
            case "thunderstorm": return  R.drawable.disaster_thunderstorm;
            case "tornado": return  R.drawable.disaster_tornado;
            case "toxic": return  R.drawable.disaster_toxic;
            case "tsunami": return  R.drawable.disaster_tsunami;
            case "virus": return  R.drawable.disaster_virus;
            case "volcano": return  R.drawable.disaster_volcano;
            case "wind": return  R.drawable.disaster_wind;
            default: return R.drawable.disaster_alarm;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull VictimHomeHolder myHolder, int i) {
        String dis_id = models.get(i).getDescription();
        String lat = models.get(i).getLatitude();
        String longi = models.get(i).getLongitude();
        // String longi = models.get(i).getDescription();
        // String username = models.get(i).getRescueUsername();
        myHolder.mTitle.setText(models.get(i).getTitle());
        myHolder.image.setBackgroundResource(getDisasterSpinnerDrawable(models.get(i).getCategory().toLowerCase()));
        // myHolder.mDes.setText(models.get(i).getDescription());
        // System.out.println("jai shree ram2");

        //WHEN ITEM IS CLICKED
        myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

                final RemoteMongoCollection<Document> teams = mongoClient.getDatabase("main").getCollection("victimsneedhelp");

                RemoteFindIterable findResults = teams.find(eq("disaster_id", dis_id));
                Task <List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
                itemsTask.addOnCompleteListener(new OnCompleteListener <List<Document>> () {
                    @Override
                    public void onComplete(@NonNull Task<List<Document>> task) {
                        if (task.isSuccessful()) {
                            List<Document> items = task.getResult();
                            int numDocs = items.size();
                            if(numDocs==0){
                                Log.d("Doesn't exist", "Insert");
                                final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                                Document newItem = new Document()
                                        .append("disaster_id", dis_id)
                                        .append("victims", Arrays.asList(
                                                new Document()
                                                        .append("latitude", lat)
                                                        .append("longitude", longi)
                                                        .append("count",1)
                                                        .append("isactive", 1)
                                        ));


                                final Task<RemoteInsertOneResult> insertTask = victimneedhelp.insertOne(newItem);
                                insertTask.addOnCompleteListener(new OnCompleteListener<RemoteInsertOneResult>() {
                                    @Override
                                    public void onComplete(@com.mongodb.lang.NonNull Task <RemoteInsertOneResult> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("app", String.format("successfully inserted item with id %s",
                                                    task.getResult().getInsertedId()));
                                        } else {
                                            Log.e("app", "failed to insert document with: ", task.getException());
                                        }
                                    }
                                });
                            }
                            else{
                                System.out.println(items.get(0));
                                Document first = items.get(0);
                                final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                                List<Document> temp = (List<Document>)first.get("victims");
                                Document newvic = new Document().append("latitude",lat).append("longitude",longi).append("count",1).append("isactive",1);
                                temp.add(newvic);
                                Log.d("Exists", "update");
                                Document filterDoc = new Document().append("disaster_id", dis_id);
                                Document updateDoc = new Document().append("$set",
                                        new Document()
                                                .append("disaster_id", dis_id)
                                                .append("victims", temp)
                                );

                                final Task<RemoteUpdateResult> updateTask =
                                        victimneedhelp.updateOne(filterDoc, updateDoc);
                                updateTask.addOnCompleteListener(new OnCompleteListener <RemoteUpdateResult> () {
                                    @Override
                                    public void onComplete(@NonNull Task <RemoteUpdateResult> task) {
                                        if (task.isSuccessful()) {
                                            long numMatched = task.getResult().getMatchedCount();
                                            long numModified = task.getResult().getModifiedCount();
                                            Log.d("app", String.format("successfully matched %d and modified %d documents",
                                                    numMatched, numModified));
                                        } else {
                                            Log.e("app", "failed to update document with: ", task.getException());
                                        }
                                    }
                                });
                            }
                            Context context = c
                                    .getApplicationContext();
                            CharSequence text = "Request Sent. Rescue Team Will Arrive as soon as possible!";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Log.e("app", "Failed to count documents with exception: ", task.getException());
                        }
                    }
                });


            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
