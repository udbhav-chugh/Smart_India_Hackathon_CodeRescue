package com.example.coderescue.Activities.UISamples;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coderescue.R;
import com.github.sshadkany.neoText;

public class NeoTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neo_text);
        neoText neotext = findViewById(R.id.neotext);
        findViewById(R.id.backgroundLayout).setBackgroundColor(neotext.background_color);
    }
}