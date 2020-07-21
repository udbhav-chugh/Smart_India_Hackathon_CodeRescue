package com.example.coderescue;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NotificationHolder extends RecyclerView.ViewHolder {

    TextView mTitle, mDes;

    public NotificationHolder (@NonNull View itemView){
        super(itemView);

        this.mTitle = itemView.findViewById(R.id.titleTv);
        this.mDes = itemView.findViewById(R.id.descriptionTv);
    }
}
