package com.delivery.estrategiamovilmx.domiciliosflorencia.notifications;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.support.v4.app.RemoteInput;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.FCMPluginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ReplyActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by administrator on 25/08/2017.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static String KEY_REPLY = "key_reply_message";
    public static String REPLY_ACTION = "MyFirebaseMessagingService";
    private static final String TAG = "MyFirebaseMsgService";
    public static final String CHANNEL_ID = "inmediatoo_chanel";
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = new HashMap<>();
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "onMessageReceived From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage. getData());
            data = remoteMessage.getData();
            for (String key : data.keySet()) {
                String value = data.get(key);
                //Log.d(TAG, "\tKey: " + key + " Value: " + value);
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        sendNotification(data);
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
    // [END receive_message]


    @RequiresApi(Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = CHANNEL_ID;
        // The user-visible name of the channel.
        CharSequence name = "Inmediatoo";
        // The user-visible description of the channel.
        String description = "Inmediatoo controls";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel mChannel = new NotificationChannel(id, name, importance);
        // Configure the notification channel.
        mChannel.setDescription(description);
        mChannel.setShowBadge(false);
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        notificationManager.createNotificationChannel(mChannel);
    }

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param data FCM message body received.
     */
    private void sendNotification(Map<String, String> data ) {
        Log.d(TAG,"sendNotification.init");
        //processing data json response
        NotificationCompat.Builder notificationBuilder = null;
        NotificationChannel notificationChannel = null;
        JSONObject object = null;
        int request_code = 0;

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

                // Configure the notification channel.
                notificationChannel.setDescription("Channel description");
                notificationChannel.enableLights(true);
                notificationChannel.setLightColor(Color.RED);
                notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                notificationChannel.enableVibration(true);
                notificationManager.createNotificationChannel(notificationChannel);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        //built the notification
            UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());

            if (user!=null) {
                Log.d(TAG,"Notificacion para Usuario activo:" + user.toString());
                notificationBuilder = makeSimpleNotification(user,data);
            }else {
                Log.d(TAG, "Notificacion para Usuario no registrado:" + user.toString());
                notificationBuilder = makeSimpleNotification(user,data);
            }

            notificationManager.notify(object != null ? request_code : 0, notificationBuilder.build());


    }



    private NotificationCompat.Builder makeSimpleNotification(UserItem object,Map<String, String> data){
        Log.d(TAG,"makeSimpleNotification.......");
        Intent intent = new Intent(this, FCMPluginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 , intent,
                PendingIntent.FLAG_ONE_SHOT);*/

        PendingIntent replyIntent = getOrderPendingIntent(object);

        //Assign inbox style notification accordance type notification on tickerText field
        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText(data.get("message"));
        bigText.setBigContentTitle(data.get("title"));
        bigText.setSummaryText("By: "+getResources().getString(R.string.app_name));

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,this.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_action_box)
                .setColor(0x0288D1)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(replyIntent)
                .setStyle(bigText);
        return notificationBuilder;
    }

    private NotificationCompat.Builder makeOrdersNotification(UserItem object, Map<String, String> data){
        Log.d(TAG,"makeOrdersNotification:"+object.toString());
        NotificationCompat.Builder mBuilder = null;
        if (object!=null) {
            // Key for the string that's delivered in the action's intent.
            String KEY_TEXT_REPLY = "key_text_reply";
            String replyLabel = getResources().getString(R.string.action_notify_reply);
            RemoteInput remoteInput = null;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                        .setLabel(replyLabel)
                        .build();


                PendingIntent replyIntent = getOrderPendingIntent(object);
                NotificationCompat.Action replyAction =
                        new NotificationCompat.Action.Builder(R.drawable.ic_motorcycle,
                                replyLabel, replyIntent)
                                .addRemoteInput(remoteInput)
                                .setAllowGeneratedReplies(true)
                                .build();
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder = new NotificationCompat.Builder(this,this.CHANNEL_ID)
                        .setContentTitle(data.get("title"))
                        .setContentText(data.get("message"))
                        .setShowWhen(true)
                        .setSmallIcon(R.drawable.ic_action_box)
                        .setColor(0x0288D1)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .addAction(replyAction); // reply action from step b above
                return mBuilder;
            }else{
                makeSimpleNotification(object,data);
            }
        }
        return makeSimpleNotification(object,data);
    }
    private PendingIntent getOrderPendingIntent(UserItem user){
        Log.d(TAG,"getOrderPendingIntent...");
        Intent intent;
        int mNotificationId = 0;



        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        // start a
        // (i)  broadcast receiver which runs on the UI thread or
        // (ii) service for a background task to b executed , but for the purpose of
        // this codelab, will be doing a broadcast receiver
           /* intent = NotificationBroadcastReceiver.getReplyMessageIntent(this, mNotificationId, 0,id_merchant,id_user);
            return PendingIntent.getBroadcast(getApplicationContext(), 100, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        } else {*/
        // start your activity for Android M and below

        intent = ReplyActivity.getReplyMessageIntent(this, mNotificationId, 0, user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // }
    }
}

