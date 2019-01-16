package com.aimitechsolutions.medipal.ui;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.aimitechsolutions.medipal.R;
import com.aimitechsolutions.medipal.utils.CustomToast;

public class SplashScreen extends Fragment {

    View fragV;
    private final int SPLASH_DISPLAY_DURATION = 5000; //milliseconds
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragV = inflater.inflate(R.layout.splash_screen, container, false);
        return fragV;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final ProgressBar pg = fragV.findViewById(R.id.progress_bar);
        pg.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Fragment Transaction to replace with Login and destroy this fragment
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                Login login = new Login();
                transaction.replace(R.id.fragments_container, login);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                transaction.commit();
                pg.setVisibility(View.VISIBLE);
            }
        }, SPLASH_DISPLAY_DURATION);
    }
}
