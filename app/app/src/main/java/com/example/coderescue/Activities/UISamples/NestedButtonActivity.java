package com.example.coderescue.Activities.UISamples;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coderescue.R;
import com.github.sshadkany.PolygonButton;

public class NestedButtonActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nested_button);

        PolygonButton polygonButton = findViewById(R.id.polygon_button);

        findViewById(R.id.backgroundLayout).setBackgroundColor(polygonButton.background_color);  // set layout background color from neo object.

        polygonButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("neo", "onClick: I am poly button !!");
            }
        });
    }
}