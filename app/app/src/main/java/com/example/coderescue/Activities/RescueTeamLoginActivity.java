package com.example.coderescue.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coderescue.Fragments.HomeFragment;
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


public class RescueTeamLoginActivity extends AppCompatActivity {

    public static RemoteMongoClient mongoClient;
    EditText username,password;
    soup.neumorphism.NeumorphImageButton showPassword;
    soup.neumorphism.NeumorphButton submit;
    Context c;

    private boolean password_notvisible = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rescue_team_login);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        showPassword = findViewById(R.id.show_password);
        submit = findViewById(R.id.submit);
        c=this;

        getWindow().setBackgroundDrawable(getDrawable(R.drawable.image));
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
    }


    public void loginaction(View view) {
        Intent intent = new Intent(this, RescueTeamDashboard.class);
        submit.setShapeType(ShapeType.PRESSED);
        submit.setEnabled(false);

        String user = username.getText().toString();
        String pswd = password.getText().toString();
        // 1. Instantiate the Stitch client


        mongoClient = HomeFragment.client.getServiceClient(RemoteMongoClient.factory, "mongodb-atlas");

        final RemoteMongoCollection<Document> teams =
                mongoClient.getDatabase("authorization").getCollection("rescue_team");
//        int count=0;
//        RemoteFindIterable docs= teams.find(and(eq("username", user), eq("password", pswd)));

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
                        CharSequence text = "Invalid username or password";
                        int duration = Toast.LENGTH_LONG;

                        Toast toast = Toast.makeText(c, text, duration);
                        toast.show();
                    }
                    else{
                        System.out.println(items.get(0));
                        Log.d("Correct Sign In", "Correct username and password");
                        intent.putExtra("username", user);
                        intent.putExtra("disaster_id", items.get(0).getString("disaster_id"));
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Log.e("app", "Failed to count documents with exception: ", task.getException());
                }
            }
        });
    }



}
