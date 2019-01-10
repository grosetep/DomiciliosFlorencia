package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;

/**
 * Created by administrator on 27/06/2017.
 */
public class Connectivity {
    private static final String TAG = Connectivity.class.getSimpleName();

    public static boolean isNetworkAvailable(Context context){

        try{
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                return isNetworkAvailableOld(cm,context);
            }else{
                return isNetworkAvailable(cm,context);
            }
        }catch(Exception e){
            return false;
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static boolean isNetworkAvailable(ConnectivityManager cm,Context context) {

        try {
            Network[] networks = cm.getAllNetworks();
            for (Network networkType : networks) {
                NetworkInfo netInfo = cm.getNetworkInfo(networkType);
                if (netInfo.getType() == ConnectivityManager.TYPE_WIFI || netInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
    private static boolean isNetworkAvailableOld(ConnectivityManager cm,Context context) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI};
        try {
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}
