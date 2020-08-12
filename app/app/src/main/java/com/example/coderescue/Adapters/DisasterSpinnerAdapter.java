package com.example.coderescue.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.coderescue.Classes.DisasterSpinnerCardModel;
import com.example.coderescue.R;

import java.util.ArrayList;

import soup.neumorphism.NeumorphImageButton;

public class DisasterSpinnerAdapter extends ArrayAdapter<DisasterSpinnerCardModel> {

    public DisasterSpinnerAdapter(@NonNull Context context, ArrayList<DisasterSpinnerCardModel> customList) {
        super(context, 0 , customList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        return DisasterSpinnerView(position, view, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        return DisasterSpinnerView(position, view, parent);
    }

    public View DisasterSpinnerView(int position, @Nullable View view, @NonNull ViewGroup parent){
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.disaster_spinner_item, parent, false);
        }

        DisasterSpinnerCardModel items = getItem(position);
        ImageView spinnerImage = view.findViewById(R.id.disaster_image);
        TextView spinnerName = view.findViewById(R.id.text);
        if (items != null) {
            spinnerImage.setBackgroundResource(items.getImage());
            spinnerName.setText(items.getText());
        }
        return view;
    }
}
