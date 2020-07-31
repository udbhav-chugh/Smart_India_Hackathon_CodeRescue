package com.example.coderescue.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.example.coderescue.Activities.RescueTeamDashboard;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteFindIterable;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoClient;
import com.mongodb.stitch.android.services.mongodb.remote.RemoteMongoCollection;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import soup.neumorphism.ShapeType;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

public class RescueTeamLoginFragment extends Fragment {

    public static RemoteMongoClient mongoClient;
    EditText username,password;
    soup.neumorphism.NeumorphImageButton showPassword;
    soup.neumorphism.NeumorphButton submit;

    private boolean password_notvisible = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_rescue_team_login, container, false);

        username = root.findViewById(R.id.username);
        password = root.findViewById(R.id.password);
        showPassword = root.findViewById(R.id.show_password);
        submit = root.findViewById(R.id.submit);

        root.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.image, null));
        showPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password_notvisible) {
                    showPassword.setShapeType(ShapeType.PRESSED);
                    password_notvisible = false;
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    password.setSelection(password.getText().length());
                }
                else{
                    showPassword.setShapeType(ShapeType.FLAT);
                    password_notvisible = true;
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    password.setSelection(password.getText().length());
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginAction();
            }
        });

        return root;
    }

    public void loginAction() {
        Intent intent = new Intent(getActivity(), RescueTeamDashboard.class);
//        submit.setShapeType(ShapeType.PRESSED);
//        submit.setEnabled(false);

        String user = username.getText().toString();
        String pswd = password.getText().toString();
        // 1. Instantiate the Stitch client


        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> teams =
                mongoClient.getDatabase("authorization").getCollection("rescue_team");

        RemoteFindIterable findResults = teams.find(and(eq("username", user), eq("password", pswd)));
        Task <List<Document>> itemsTask = findResults.into(new ArrayList<Document>());
        itemsTask.addOnCompleteListener(new OnCompleteListener <List<Document>> () {
            @Override
            public void onComplete(@NonNull Task<List<Document>> task) {
                if (task.isSuccessful()) {
                    List<Document> items = task.getResult();
                    int numDocs = items.size();
                    if(numDocs==0){
                        Log.d("Incorrect Sign In", "Wrong username or password");
                    }
                    else{
                        System.out.println(items.get(0));
                        Log.d("Correct Sign In", "Correct username and password");
                        intent.putExtra("username", user);
                        intent.putExtra("disaster_id", items.get(0).getString("disaster_id"));
                        startActivity(intent);
//                        finish();
                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }

}
