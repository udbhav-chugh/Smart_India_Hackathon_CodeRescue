package com.example.coderescue.Activities.UISamples;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coderescue.R;
import com.github.sshadkany.shapes.RoundRectView;

public class SecendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secend);
////*************************below code changes sides of polygon when its clicked ***************************************
//        final PolygonView my_polygon = findViewById(R.id.my_polygon);
//        my_polygon.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (my_polygon.isShapeContainsPoint(event.getX(), event.getY())) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_DOWN:
//                            // PRESSED
//                            int side = my_polygon.getSide();
//                            if (side > 12){
//                                my_polygon.setSide(3);
//                            }else {
//                                my_polygon.setSide(side+1);
//                            }
//                            return true;
//                        case MotionEvent.ACTION_UP:
//                        case MotionEvent.ACTION_CANCEL:
//                            // RELEASED
//                            return true;
//                    }
//                } else {
//                    // RELEASED
//                    return true;
//                }
//                return false;
//            }
//        });
//************************************************************************************


        RoundRectView rectView = findViewById(R.id.roundRectView);
        findViewById(R.id.backgroundLayout).setBackgroundColor(rectView.background_color);  // set layout background color from neo object.


    }
}