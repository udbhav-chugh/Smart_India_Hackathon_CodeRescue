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

import com.example.coderescue.Fragments.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.BasicDBObject;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;
import com.mongodb.stitch.core.services.mongodb.remote.RemoteUpdateResult;

import org.bson.Document;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class SafeHouseAdapter extends RecyclerView.Adapter<SafeHouseHolder>{

    Context c;
    ArrayList<SafeHouseCardModel> models;
    public static RemoteMongoClient mongoClient;


    public SafeHouseAdapter(Context c, ArrayList<SafeHouseCardModel> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public SafeHouseHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.safe_house_card ,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new SafeHouseHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SafeHouseHolder myHolder, int i) {
        String lat = models.get(i).getLatitude();
        String longi = models.get(i).getLongitude();
        String state = models.get(i).getState();
        myHolder.name.setText(models.get(i).getName());
        myHolder.mLat.setText(lat);
        myHolder.mLong.setText(longi);
        Double distance = models.get(i).getDistance();
        if(distance > 1000) myHolder.mTitle.setText(new DecimalFormat("#.##").format(distance / 1000.0) + " Km");
        else myHolder.mTitle.setText(new DecimalFormat("#.##").format(distance) + " m");
        System.out.println("jai shree ram2");

        //WHEN ITEM IS CLICKED
        myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String geoUri = "http://maps.google.com/maps?q=loc:" + lat + "," + longi + " (" + "label items" + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                c.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
