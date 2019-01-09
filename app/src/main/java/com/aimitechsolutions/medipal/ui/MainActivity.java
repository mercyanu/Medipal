package com.aimitechsolutions.medipal.ui;

import android.support.v4.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.support.v4.app.FragmentActivity;
import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.aimitechsolutions.medipal.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  --this is to set orientation for each activity
        ActionBar actionBar = getActionBar();
            if(actionBar != null){
                actionBar.hide();
            }

        if(findViewById(R.id.fragments_container) != null){
            if(savedInstanceState != null) return;

            SplashScreen splashScreen = new SplashScreen();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragments_container, splashScreen);
            transaction.commit();
        }
    }
}
