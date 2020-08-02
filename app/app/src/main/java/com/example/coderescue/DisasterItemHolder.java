package com.example.coderescue;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DisasterItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView disaster_image, disaster_title;
    private ItemClickListener itemClickListener;

    public DisasterItemHolder(@NonNull View itemView){
        super(itemView);

        this.disaster_image = itemView.findViewById(R.id.disaster_image);
        this.disaster_title = itemView.findViewById(R.id.disaster_title);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic){
        this.itemClickListener=ic;
    }
}
