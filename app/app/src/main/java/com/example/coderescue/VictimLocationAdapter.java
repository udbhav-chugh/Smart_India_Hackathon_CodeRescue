package com.example.coderescue;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.MapsActivity;
import com.example.coderescue.Activities.PathToVictimActivity;
import com.example.coderescue.Fragments.HomeFragment;
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
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

public class VictimLocationAdapter extends RecyclerView.Adapter<VictimLocationHolder>{

    Context c;
    ArrayList<VictimLocationCardModel> models;
    public static RemoteMongoClient mongoClient;


    public VictimLocationAdapter(Context c, ArrayList<VictimLocationCardModel> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public VictimLocationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.victim_location_card ,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new VictimLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VictimLocationHolder myHolder, int i) {
        String lat = models.get(i).getLatitude();
        String longi = models.get(i).getLongitude();
        String username = models.get(i).getRescueUsername();
        String dis_id = models.get(i).getDisaster_id();
        myHolder.mTitle.setText(models.get(i).getTitle());
        myHolder.mLat.setText(lat);
        myHolder.mLong.setText(longi);
        System.out.println("jai shree ram2");

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
                                Log.d("Doesn't exist", "Should not happen");
                            }
                            else{
                                System.out.println(items.get(0));
                                Document first = items.get(0);
                                final RemoteMongoCollection<Document> victimneedhelp = mongoClient.getDatabase("main").getCollection("victimsneedhelp");
                                List<Document> temp = (List<Document>)first.get("victims");
                                List<Document> temp2 = new ArrayList<Document>();
                                int count=0;
                                System.out.println(lat);
                                System.out.println(longi);
                                for(Document doc: temp){
                                    System.out.println(count + " " + doc.getString("latitude") + " " + doc.getString("longitude"));
                                    if(count==1 || !doc.getString("latitude").equals(lat) || !doc.getString("longitude").equals(longi))
                                    {
                                        temp2.add(doc);
                                    }
                                    else
                                    {
                                        count=1;
                                        Document notactive = new Document()
                                                .append("latitude", lat)
                                                .append("longitude", longi)
                                                .append("isactive", 0);
                                        temp2.add(notactive);
                                    }
                                }
                                Log.d("Exists", "update");
                                Document filterDoc = new Document().append("disaster_id", dis_id);
                                Document updateDoc = new Document().append("$set",
                                        new Document()
                                                .append("disaster_id", dis_id)
                                                .append("victims", temp2)
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
                            CharSequence text = "Opening Maps to guid to the victim!";
                            int duration = Toast.LENGTH_LONG;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        } else {
                            Log.e("app", "Failed to count documents with exception: ", task.getException());
                        }
                    }
                });
                System.out.println("jai shree ram");
//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", lat, longi);
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + longi + " (" + "label temp" + ")";

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                c.startActivity(intent);
                //INTENT OBJ

//                Intent iii=new Intent(c, MapsActivity.class);

                //ADD DATA TO OUR INTENT
//                iii.putExtra("latitude",lat);
//                iii.putExtra("longitude",longi);
//                iii.putExtra("username", username);
                //START DETAIL ACTIVITY
//                c.startActivity(iii);

            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
