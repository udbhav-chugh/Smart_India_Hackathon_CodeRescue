package com.example.coderescue.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.coderescue.R;

public class PathToVictimActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_to_victim);
        Intent i=getIntent();
        String lat=i.getExtras().getString("latitude");
        String longi=i.getExtras().getString("longitude");
        TextView victimtext = findViewById(R.id.textViewVictim);
        TextView rescuetext = findViewById(R.id.textViewRescue);
        victimtext.setText(lat + "\n" + longi);
        rescuetext.setText(lat + "\n" + longi);

    }
}
