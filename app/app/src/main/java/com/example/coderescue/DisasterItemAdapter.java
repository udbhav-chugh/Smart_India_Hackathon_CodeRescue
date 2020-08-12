package com.example.coderescue;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coderescue.Activities.UpdateInfoActivity;
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

import static com.mongodb.client.model.Filters.eq;

public class DisasterItemAdapter extends RecyclerView.Adapter<DisasterItemHolder>{

    Context c;
    List<String> disasters;
    List<Integer> button_images;
//    private UpdateInfoActivity.onItemClick mInterface;

    public DisasterItemAdapter(Context c, ArrayList<String> disasters, ArrayList<Integer> button_images) {
        this.c = c;
        this.disasters = disasters;
        this.button_images = button_images;
//        this.mInterface = mInterface;
    }

    @NonNull
    @Override
    public DisasterItemHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.disaster_list_item, null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        return new DisasterItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DisasterItemHolder myHolder, int position) {
        if(position >=0 && position < disasters.size() && position < button_images.size()){
            myHolder.disaster_title.setText(disasters.get(position));
            myHolder.disaster_image.setBackgroundResource(button_images.get(position));
        }

        //WHEN ITEM IS CLICKED
        myHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return disasters.size();
    }

}
