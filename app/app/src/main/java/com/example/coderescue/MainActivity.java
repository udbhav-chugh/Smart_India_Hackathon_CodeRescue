package com.example.coderescue;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.anonymous.AnonymousCredential;

public class MainActivity extends AppCompatActivity {

    public static StitchAppClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        enableAnonymousAuth();
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

    public void gotoLogin(View view) {
        Intent intent = new Intent(this, RescueTeamLoginActivity.class);
        startActivity(intent);
    }
    public void gotoVictimNotifications(View view){
        Intent intent = new Intent(this, VictimNotifications.class);
        startActivity(intent);
    }

}
