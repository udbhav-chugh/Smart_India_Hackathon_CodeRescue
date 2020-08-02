package com.example.coderescue;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VictimLocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView mTitle, mLat, mLong, countvic;
    private ItemClickListener itemClickListener;

    public VictimLocationHolder (@NonNull View itemView){
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.titleTv2);
        this.mLat = itemView.findViewById(R.id.latitude);
        this.mLong = itemView.findViewById(R.id.longitude);
        this.countvic = itemView.findViewById(R.id.countvic);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        System.out.println("jai shree ram3");

        this.itemClickListener.onItemClick(v,getLayoutPosition());
    }

    public void setItemClickListener(ItemClickListener ic)
    {
        System.out.println("jai shree ram4");

        this.itemClickListener=ic;

    }
}
