package com.example.coderescue.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.example.coderescue.Fragments.HomeFragment;
import com.example.coderescue.R;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

public class SplashScreenActivity extends AppCompatActivity {

    TextView textView1, textView2;
    Animation bottom_animation, fade_in, fade_out;
    GifImageView gifImageView;
//    CircleImageView logo;
    private static int SPLASH_SCREEN = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        gifImageView = findViewById(R.id.gifImageView);
//        logo = findViewById(R.id.logo);

        bottom_animation = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);
        fade_in = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        textView1.setAnimation(bottom_animation);
        textView2.setAnimation(bottom_animation);
//        gifImageView.setAnimation(fade_out);
//        logo.setAnimation(fade_in);
//        logo.animate().setStartDelay(2100);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreenActivity.this, HomeFragment.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_SCREEN);
    }
}