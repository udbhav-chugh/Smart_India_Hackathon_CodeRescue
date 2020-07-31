package com.example.coderescue.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.example.coderescue.Adapters.SectionsPagerAdapter;
import com.example.coderescue.Fragments.RescueTeamLoginFragment;
import com.example.coderescue.Fragments.UpdateInfoFragment;
import com.example.coderescue.Fragments.VictimHomeFragment;
import com.example.coderescue.Fragments.VictimNotificationFragment;
import com.example.coderescue.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.core.auth.StitchUser;
import com.mongodb.stitch.core.auth.providers.userpassword.UserPasswordCredential;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FragmentHome2 extends AppCompatActivity {

    ViewPager viewPager;
    soup.neumorphism.NeumorphCardView tile_victim, tile_rescue, tile_third, tile_notif;
    LinearLayout normal_victim, normal_rescue, normal_third, normal_notif;
    int totalFragments = 0;
    public static StitchAppClient client;

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

        List<Fragment> fragments = getFragments();
        totalFragments = fragments.size();
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), fragments);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(3);
        enableAnonymousAuth();
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