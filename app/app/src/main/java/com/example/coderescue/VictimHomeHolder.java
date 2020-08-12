package com.example.coderescue;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VictimHomeHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    TextView mTitle, mDes;
    ImageView image;
    private ItemClickListener itemClickListener;

    public VictimHomeHolder (@NonNull View itemView){
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.titleTv3);
        this.image = itemView.findViewById(R.id.disaster_image);
        // this.mDes = itemView.findViewById(R.id.descriptionTv2);
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
