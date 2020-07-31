package com.example.coderescue.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.coderescue.Activities.CameraWithGoogleMapsActivity;
import com.example.coderescue.Activities.GoogleMapActivity;
import com.example.coderescue.Activities.MainDashboardActivity;
import com.example.coderescue.Activities.RescueTeamLoginActivity;
import com.example.coderescue.Activities.SendMessageActivity;
import com.example.coderescue.Activities.UpdateInfoActivity;
import com.example.coderescue.Activities.VictimHomeActivity;
import com.example.coderescue.Activities.VictimNotifications;
import com.example.coderescue.Adapters.SectionsPagerAdapter;
import com.example.coderescue.R;
import com.example.coderescue.navar.MapsActivity;
import com.example.coderescue.navar.NavActivity;
import com.example.coderescue.navar.PoiBrowserActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
//
//public class HomeFragment extends Fragment {
//
//    public static StitchAppClient client;
//    Button button_helper, button_victim, button_victim_notif, button_dashboard, button_update_info, button_ar_map, button_ar_camera, button_send_msg;
//    ImageButton button_voice;
//    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
//
//    public View onCreateView(@NonNull LayoutInflater inflater,
//                             ViewGroup container, Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        button_victim = root.findViewById(R.id.button_victim);
//        button_helper = root.findViewById(R.id.button_helper);
//        button_victim_notif = root.findViewById(R.id.button_victim_notif);
//        button_dashboard = root.findViewById(R.id.button_dashboard);
//        button_update_info = root.findViewById(R.id.button_update_info);
//        button_ar_map = root.findViewById(R.id.button_ar_map);
//        button_ar_camera = root.findViewById(R.id.button_ar_camera);
//        button_voice = root.findViewById(R.id.voiceBtn);
//        button_send_msg = root.findViewById(R.id.button_send_msg);
//
//        //added
//        button_helper.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), RescueTeamLoginActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //added
//        button_victim.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), VictimHomeActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //added
//        button_victim_notif.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), VictimNotifications.class);
//                startActivity(intent);
//            }
//        });
//
//        //futile
//        button_dashboard.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MainDashboardActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        //added
//        button_update_info.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
//                startActivity(intent);
//            }
//        });
//        button_ar_map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), GoogleMapActivity.class);
//                startActivity(intent);
//            }
//        });
//        button_ar_camera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), PoiBrowserActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        button_send_msg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), SendMessageActivity.class);
//                startActivity(intent);
//            }
//        });
//
//        enableAnonymousAuth();
//
//        button_voice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                speak();
//            }
//        });
//
//        return root;
//    }
//
//    private void speak(){
//
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");
//
//        try {
//            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
//        }
//        catch (Exception e){
//            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode){
//            case REQUEST_CODE_SPEECH_INPUT:{
//                if (resultCode == -1 && null!=data){
//                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    String spoken = result.get(0);
//                    if(spoken.contains("help")){
//                        Intent intent = new Intent(getActivity(), VictimHomeActivity.class);
//                        startActivity(intent);
//                    }
//                    if(spoken.contains("message")){
//                        Intent intent = new Intent(getActivity(), SendMessageActivity.class);
//                        startActivity(intent);
//                    }
//                }
//            }
//        }
//    }
//
//    private void enableAnonymousAuth(){
//        client = Stitch.getDefaultAppClient();
//        UserPasswordCredential credential = new UserPasswordCredential("coderescue2020@gmail.com", "sih@2020");
//        client.getAuth().loginWithCredential(credential).addOnCompleteListener(
//                new OnCompleteListener<StitchUser>() {
//                    @Override
//                    public void onComplete(@NonNull final Task<StitchUser> task) {
//                        if (task.isSuccessful()) {
//                            Log.d("myApp", String.format(
//                                    "logged in as user %s with provider %s",
//                                    task.getResult().getId(),
//                                    task.getResult().getLoggedInProviderType()));
//                            System.out.println("lol login");
//
//                        } else {
//                            Log.e("myApp", "failed to log in", task.getException());
//                            System.out.println("nhi hua login");
//                        }
//                    }
//                });
//    }
//}


public class HomeFragment extends AppCompatActivity {

    ViewPager viewPager;
    soup.neumorphism.NeumorphCardView tile_victim, tile_rescue, tile_third, tile_notif;
    LinearLayout normal_victim, normal_rescue, normal_third, normal_notif;
    int totalFragments = 0;
    public static StitchAppClient client;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home2);
        viewPager = findViewById(R.id.view_pager);

        normal_victim = findViewById(R.id.normal_victim);
        normal_rescue = findViewById(R.id.normal_rescue);
        normal_third = findViewById(R.id.normal_third);
        normal_notif = findViewById(R.id.normal_notif);
        tile_victim = findViewById(R.id.tile_victim);
        tile_rescue = findViewById(R.id.tile_rescue);
        tile_third = findViewById(R.id.tile_third);
        tile_notif = findViewById(R.id.tile_notif);

        normal_victim.setOnClickListener(this::onClick);
        normal_rescue.setOnClickListener(this::onClick);
        normal_third.setOnClickListener(this::onClick);
        normal_notif.setOnClickListener(this::onClick);
        tile_victim.setOnClickListener(this::onClick);
        tile_rescue.setOnClickListener(this::onClick);
        tile_third.setOnClickListener(this::onClick);
        tile_notif.setOnClickListener(this::onClick);

        enableAnonymousAuth();

        List<Fragment> fragments = getFragments();
        totalFragments = fragments.size();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(1);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Log.d("home page", "onPageSeelected: " + Integer.toString(position));
                lowerOthers();
                raisePosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void raisePosition(int position) {
        position+=1;
        Log.d("home page", "raisePosition: " + Integer.toString(position));
        switch (position){
            case 1:
                tile_victim.setVisibility(View.VISIBLE);
                normal_victim.setVisibility(View.INVISIBLE);
                break;
            case 2:
                tile_rescue.setVisibility(View.VISIBLE);
                normal_rescue.setVisibility(View.INVISIBLE);
                break;
            case 3:
                tile_third.setVisibility(View.VISIBLE);
                normal_third.setVisibility(View.INVISIBLE);
                break;
            case 4:
                tile_notif.setVisibility(View.VISIBLE);
                normal_notif.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragments = new ArrayList<Fragment>();
        fragments.add(new VictimHomeFragment());
        fragments.add(new RescueTeamLoginFragment());
        fragments.add(new UpdateInfoFragment());
        fragments.add(new VictimNotificationFragment());
//        fragments.add(new HomeFragment());
        return fragments;
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.normal_victim:
            case R.id.tile_victim:
                Log.d("home page", "victim click triggered");
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.normal_rescue:
            case R.id.tile_rescue:
                Log.d("home page", "rescue click triggered");
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.normal_third:
            case R.id.tile_third:
                Log.d("home page", "third click triggered");
                viewPager.setCurrentItem(2, true);
                break;
            case R.id.normal_notif:
            case R.id.tile_notif:
                Log.d("home page", "notif click triggered");
                viewPager.setCurrentItem(3, true);
                break;
        }
    }

    private void lowerOthers() {
        Log.d("home page", "lowerOthers");

        tile_victim.setVisibility(View.INVISIBLE);
        normal_victim.setVisibility(View.VISIBLE);

        tile_rescue.setVisibility(View.INVISIBLE);
        normal_rescue.setVisibility(View.VISIBLE);

        tile_third.setVisibility(View.INVISIBLE);
        normal_third.setVisibility(View.VISIBLE);

        tile_notif.setVisibility(View.INVISIBLE);
        normal_notif.setVisibility(View.VISIBLE);
    }


    private void speak(){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi speak something");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        }
        catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if (resultCode == -1 && null!=data){
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String spoken = result.get(0);
                    if(spoken.contains("help")){
                        Intent intent = new Intent(HomeFragment.this, VictimHomeActivity.class);
                        startActivity(intent);
                    }
                    if(spoken.contains("message")){
                        Intent intent = new Intent(HomeFragment.this, SendMessageActivity.class);
                        startActivity(intent);
                    }
                }
            }
        }
    }

    private void enableAnonymousAuth(){
        client = Stitch.getDefaultAppClient();
        UserPasswordCredential credential = new UserPasswordCredential("coderescue2020@gmail.com", "sih@2020");
        client.getAuth().loginWithCredential(credential).addOnCompleteListener(
                new OnCompleteListener<StitchUser>() {
                    @Override
                    public void onComplete(@NonNull final Task<StitchUser> task) {
                        if (task.isSuccessful()) {
                            Log.d("myApp", String.format(
                                    "logged in as user %s with provider %s",
                                    task.getResult().getId(),
                                    task.getResult().getLoggedInProviderType()));
                            System.out.println("lol login");

                        } else {
                            Log.e("myApp", "failed to log in", task.getException());
                            System.out.println("nhi hua login");
                        }
                    }
                });
    }


}