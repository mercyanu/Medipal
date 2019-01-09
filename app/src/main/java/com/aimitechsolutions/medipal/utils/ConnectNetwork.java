package com.aimitechsolutions.medipal.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.auth.FirebaseAuth;

public class ConnectNetwork {
    public static boolean checkInternet(Context context){
        int[] connTypes = {ConnectivityManager.TYPE_MOBILE, ConnectivityManager.TYPE_WIFI};

        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            for(int connType: connTypes){
                if(networkInfo != null && networkInfo.getType() == connType){
                    return true;
                }
            }
        }catch(Exception e){
            return false;
        }
        return false;
    }
}
