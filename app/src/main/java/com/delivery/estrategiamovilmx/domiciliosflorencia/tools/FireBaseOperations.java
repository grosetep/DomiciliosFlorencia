package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Created by administrator on 26/08/2017.
 */
public class FireBaseOperations {
    private static final String TAG = FireBaseOperations.class.getSimpleName();
    public static  void subscribe(Context context,String topic) {
        Log.d(TAG, "perfil...." + topic);
        String firebase_token = ApplicationPreferences.getLocalStringPreference(context,Constants.firebase_token);
        if (firebase_token!=null){
            switch (topic){
                case Constants.profile_client:
                    Log.d(TAG, "subscribe...." + Constants.app_label + topic);
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.app_label + topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + Constants.profile_deliver_man);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label +Constants.profile_admin);
                    break;
                case Constants.profile_admin:
                    Log.d(TAG, "subscribe...." + Constants.app_label + topic);
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.app_label + topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + Constants.profile_client);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + Constants.profile_deliver_man);
                    break;
                case Constants.profile_deliver_man:
                    Log.d(TAG, "subscribe...." + Constants.app_label + topic);
                    FirebaseMessaging.getInstance().subscribeToTopic(Constants.app_label + topic);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + Constants.profile_client);
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + Constants.profile_admin);
                    break;
            }

        }
    }
    public static  void unSubscribe(Context context,String topic) {
        Log.d(TAG, "unSubscribe...." + topic);
        String firebase_token = ApplicationPreferences.getLocalStringPreference(context,Constants.firebase_token);
        if (firebase_token!=null){
            FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.app_label + topic);
        }

    }
}
