package com.example.coderescue.Activities;

import android.content.Intent;
import android.os.Bundle;
//import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.beyondar.android.fragment.BeyondarFragmentSupport;
import com.beyondar.android.world.World;

import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class CameraWithGoogleMapsActivity extends FragmentActivity implements OnClickListener {

    private BeyondarFragmentSupport mBeyondarFragment;
    private World mWorld;

    private Button mShowMap;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        loadViewFromXML();

        // We create the world and fill it
        BeyondARWorld.getVictims();

        Task task =  BeyondARWorld.getVictims();

        task.addOnCompleteListener( (new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d("Correct", "yayy yayy yayya yayy");
                    try {
                        mWorld = BeyondARWorld.generateObjects( CameraWithGoogleMapsActivity.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mBeyondarFragment.setWorld(mWorld);

                }
            }
        }));
//
//        try {
//            mWorld = BeyondARWorld.generateObjects(this);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        mBeyondarFragment.setWorld(mWorld);

    }

    private void loadViewFromXML() {
        setContentView(R.layout.camera_with_google_maps);

        mBeyondarFragment = (BeyondarFragmentSupport) getSupportFragmentManager().findFragmentById(R.id.beyondarFragment);

        mShowMap = (Button) findViewById(R.id.showMapButton);
        mShowMap.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mShowMap) {
            Intent intent = new Intent(this, GoogleMapActivity.class);
            startActivity(intent);
        }
    }

}