package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.notifications.MyFirebaseMessagingService;

public class ReplyActivity extends AppCompatActivity {
    public static final String flow_notification = "notification";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Intent getReplyMessageIntent(Context context, int notifyId, int messageId, UserItem user) {
        Log.d(flow_notification,"getReplyMessageIntent..."+user.toString());
        Intent intent = new Intent(context, OrdersDeliverPurchaseActivity.class);

        intent.setAction(MyFirebaseMessagingService.REPLY_ACTION);
        Log.d(flow_notification,"getReplyMessageIntent...ok");
        return intent;
    }
}
