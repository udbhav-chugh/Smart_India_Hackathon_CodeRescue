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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.CameraWithGoogleMapsActivity;
import com.example.coderescue.Activities.GoogleMapActivity;
import com.example.coderescue.Activities.MainDashboardActivity;
import com.example.coderescue.Activities.RescueTeamLoginActivity;
import com.example.coderescue.Activities.SendMessageActivity;
import com.example.coderescue.Activities.UpdateInfoActivity;
import com.example.coderescue.Activities.VictimHomeActivity;
import com.example.coderescue.Activities.VictimNotifications;
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
import java.util.Locale;

public class HomeFragment extends Fragment {

    public static StitchAppClient client;
    Button button_helper, button_victim, button_victim_notif, button_dashboard, button_update_info, button_ar_map, button_ar_camera, button_send_msg;
    ImageButton button_voice;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        button_victim = root.findViewById(R.id.button_victim);
        button_helper = root.findViewById(R.id.button_helper);
        button_victim_notif = root.findViewById(R.id.button_victim_notif);
        button_dashboard = root.findViewById(R.id.button_dashboard);
        button_update_info = root.findViewById(R.id.button_update_info);
        button_ar_map = root.findViewById(R.id.button_ar_map);
        button_ar_camera = root.findViewById(R.id.button_ar_camera);
        button_voice = root.findViewById(R.id.voiceBtn);
        button_send_msg = root.findViewById(R.id.button_send_msg);

        button_helper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RescueTeamLoginActivity.class);
                startActivity(intent);
            }
        });
        button_victim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VictimHomeActivity.class);
                startActivity(intent);
            }
        });
        button_victim_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VictimNotifications.class);
                startActivity(intent);
            }
        });
        button_dashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainDashboardActivity.class);
                startActivity(intent);
            }
        });
        button_update_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), UpdateInfoActivity.class);
                startActivity(intent);
            }
        });
        button_ar_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GoogleMapActivity.class);
                startActivity(intent);
            }
        });
        button_ar_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PoiBrowserActivity.class);
                startActivity(intent);
            }
        });

        button_send_msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMessageActivity.class);
                startActivity(intent);
            }
        });

        enableAnonymousAuth();

        button_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });

        return root;
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
            Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
                        Intent intent = new Intent(getActivity(), VictimHomeActivity.class);
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
