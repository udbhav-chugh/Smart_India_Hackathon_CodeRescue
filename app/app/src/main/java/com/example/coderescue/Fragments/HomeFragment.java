package com.example.coderescue.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.MainDashboardActivity;
import com.example.coderescue.Activities.RescueTeamLoginActivity;
import com.example.coderescue.Activities.VictimHomeActivity;
import com.example.coderescue.Activities.VictimNotifications;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

public class HomeFragment extends Fragment {

    public static StitchAppClient client;
    Button button_helper, button_victim, button_victim_notif, button_dashboard;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        button_victim = root.findViewById(R.id.button_victim);
        button_helper = root.findViewById(R.id.button_helper);
        button_victim_notif = root.findViewById(R.id.button_victim_notif);
        button_dashboard = root.findViewById(R.id.button_dashboard);

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
        enableAnonymousAuth();
        return root;
    }

    private void enableAnonymousAuth(){
        client = Stitch.getDefaultAppClient();
        client.getAuth().loginWithCredential(new AnonymousCredential()).addOnCompleteListener(
            new OnCompleteListener<StitchUser>() {
                @Override
                public void onComplete(@NonNull final Task<StitchUser> task) {
                    if (task.isSuccessful()) {
                        Log.d("myApp", String.format(
                                "logged in as user %s with provider %s",
                                task.getResult().getId(),
                                task.getResult().getLoggedInProviderType()));
                    } else {
                        Log.e("myApp", "failed to log in", task.getException());
                    }
                }
            });
    }
}
