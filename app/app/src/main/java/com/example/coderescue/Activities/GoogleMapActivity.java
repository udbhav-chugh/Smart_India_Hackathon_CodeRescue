package com.example.coderescue.Activities;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.beyondar.android.plugin.googlemap.GoogleMapWorldPlugin;
import com.beyondar.android.world.GeoObject;
import com.beyondar.android.world.World;
import com.example.coderescue.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private GoogleMapWorldPlugin mGoogleMapPlugin;
    private World mWorld;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_google);

        SupportMapFragment supportMapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
//        if (mMap == null){
//            return;
//        }
        supportMapFragment.getMapAsync(this);

        // We create the world and fill it
//        CustomWorldHelper.getDisasters();
    }
//
//    @Override
//    public boolean onMarkerClick(Marker marker) {
//        // To get the GeoObject that owns the marker we use the following
//        // method:
//        GeoObject geoObject = mGoogleMapPlugin.getGeoObjectOwner(marker);
//        if (geoObject != null) {
//            Toast.makeText(this,
//                    "Click on a marker owned by a GeoOject with the name: " + geoObject.getName(),
//                    Toast.LENGTH_SHORT).show();
//        }
//        return false;
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        setUpMap();
    }

    public void setUpMap() {
        Task task =  CustomWorldHelper.getDisasters();

        task.addOnCompleteListener( (new OnCompleteListener<Object>() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    Log.d("Correct", "yayy yayy yayya yayy");

                    // We create the world and fill the world
                    try {
                        mWorld = CustomWorldHelper.generateObjects(GoogleMapActivity.this);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // As we want to use GoogleMaps, we are going to create the plugin and
                    // attach it to the World
                    mGoogleMapPlugin = new GoogleMapWorldPlugin(GoogleMapActivity.this);
                    // Then we need to set the map in to the GoogleMapPlugin
                    mGoogleMapPlugin.setGoogleMap(mMap);
                    // Now that we have the plugin created let's add it to our world.
                    // NOTE: It is better to load the plugins before start adding object in to the world.
                    mWorld.addPlugin(mGoogleMapPlugin);

//                  mMap.setOnMarkerClickListener(this);

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mGoogleMapPlugin.getLatLng(), 15));
                    mMap.animateCamera(CameraUpdateFactory.zoomTo(4), 2000, null);

                    // Lets add the user position
//                    GeoObject user = new GeoObject(1000l);
//                    user.setGeoPosition(23.8240, 79.4389);
////                    user.setImageResource(R.drawable.flag);
//                    user.setName("Centre of India");
//                    mWorld.addBeyondarObject(user);
                }
            }
        }));
    }
}