package com.example.moviecatalogue;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;

public class AboutActivity extends AppCompatActivity {
    TextView titleTV,contentTV;
    ImageView gdkImg, dicodingImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ImageView logoApp = findViewById(R.id.appCompatImageView);
        titleTV = findViewById(R.id.textView6);
        contentTV  = findViewById(R.id.textView8);
        gdkImg = findViewById(R.id.gdk_logo);
        dicodingImg = findViewById(R.id.dicoding_logo);
        titleTV.setVisibility(View.INVISIBLE);
        gdkImg.setVisibility(View.INVISIBLE);
        dicodingImg.setVisibility(View.INVISIBLE);
        contentTV.setVisibility(View.INVISIBLE);
        ViewAnimator
                .animate(logoApp)
                .duration(500)
                .scale(0,1)
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        animateTitle();
                    }
                })
                .start();
    }

    private void animateTitle(){
        ViewAnimator
                .animate(titleTV)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        titleTV.setVisibility(View.VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        animateContent();
                    }
                })
                .duration(500)
                .scale(0,1)
                .start();
    }

    public void animateContent(){
        ViewAnimator
                .animate(contentTV)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        contentTV.setVisibility(View.VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        animateGdk();
                    }
                })
                .duration(500)
                .scale(0,1)
                .start();
    }

    public void animateGdk(){
        ViewAnimator
                .animate(gdkImg)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        gdkImg.setVisibility(View.VISIBLE);
                    }
                })
                .onStop(new AnimationListener.Stop() {
                    @Override
                    public void onStop() {
                        animateDicoding();
                    }
                })
                .duration(500)
                .scale(0,1)
                .start();
    }

    public void animateDicoding(){
        ViewAnimator
                .animate(dicodingImg)
                .onStart(new AnimationListener.Start() {
                    @Override
                    public void onStart() {
                        dicodingImg.setVisibility(View.VISIBLE);
                    }
                })
                .duration(500)
                .scale(0,1)
                .start();
    }
}
