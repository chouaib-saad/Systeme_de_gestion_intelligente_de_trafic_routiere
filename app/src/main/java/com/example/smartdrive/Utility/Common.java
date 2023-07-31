package com.example.smartdrive.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Common {
    public static boolean isConnectedToInternet(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);


        if (connectivityManager != null) {

            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();

            if (info != null) {
                for (NetworkInfo networkInfo : info) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
