package com.delivery.estrategiamovilmx.domiciliosflorencia.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;

/**
 * Created by administrator on 25/08/2017.
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "NotificationBroadcastReceiver";
    private static String KEY_NOTIFICATION_ID = "key_noticiation_id";
    private static String KEY_MESSAGE_ID = "key_message_id";
    private static final String KEY_MERCHANT_ID = "key_id_merchant";
    private static final String KEY_USER_ID = "key_id_user";

    public static Intent getReplyMessageIntent(Context context, int notificationId, int messageId, String id_merchant, String idUser) {
        Intent intent = new Intent(context, NotificationBroadcastReceiver.class);
        intent.setAction(MyFirebaseMessagingService.KEY_REPLY);
        intent.putExtra(KEY_NOTIFICATION_ID, notificationId);
        intent.putExtra(KEY_MESSAGE_ID, messageId);
        intent.putExtra(KEY_MERCHANT_ID, id_merchant);
        intent.putExtra(KEY_USER_ID, idUser);
        return intent;
    }
    public NotificationBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // do whatever you want with the message. Send to the server or add to the db.
        // for this tutorial, we'll just show it in a toast;

        int messageId = intent.getIntExtra(KEY_MESSAGE_ID, 0);
        String idMerchant = intent.getStringExtra(KEY_MERCHANT_ID);
        String idUser = intent.getStringExtra(KEY_USER_ID);


        // update notification
        int notifyId = intent.getIntExtra(KEY_NOTIFICATION_ID, 1);
        updateNotification(context, notifyId);
    }

    private void updateNotification(Context context, int notifyId) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,MyFirebaseMessagingService.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_motorcycle)
                .setContentText("content text..");

        notificationManager.notify(notifyId, builder.build());
    }

}
