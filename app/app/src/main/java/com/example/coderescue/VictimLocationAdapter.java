package com.example.coderescue;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.PathToVictimActivity;

import java.util.ArrayList;

public class VictimLocationAdapter extends RecyclerView.Adapter<VictimLocationHolder>{

    Context c;
    ArrayList<VictimLocationCardModel> models;

    public VictimLocationAdapter(Context c, ArrayList<VictimLocationCardModel> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public VictimLocationHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.victim_location_card, null);

        return new VictimLocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VictimLocationHolder myHolder, int i) {
        String lat = models.get(i).getTitle();
        String longi = models.get(i).getDescription();
        String username = models.get(i).getRescueUsername();
        myHolder.mTitle.setText(models.get(i).getTitle());
        myHolder.mDes.setText(models.get(i).getDescription());
        System.out.println("jai shree ram2");

        //WHEN ITEM IS CLICKED
        myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

                //INTENT OBJ
                Intent iii=new Intent(c, PathToVictimActivity.class);
                System.out.println("jai shree ram");
                //ADD DATA TO OUR INTENT
                iii.putExtra("latitude",lat);
                iii.putExtra("longitude",longi);
                iii.putExtra("username", username);
                //START DETAIL ACTIVITY
                c.startActivity(iii);

            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
