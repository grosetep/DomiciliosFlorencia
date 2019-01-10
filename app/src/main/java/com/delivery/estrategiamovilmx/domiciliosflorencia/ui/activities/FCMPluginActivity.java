package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;

import java.util.HashMap;
import java.util.Map;

public class FCMPluginActivity extends AppCompatActivity {
    private static String TAG = "FCMPlugin";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fcmplugin);
        Log.d(TAG, "==> FCMPluginActivity onCreate");

        Map<String, Object> data = new HashMap<String, Object>();
        if (getIntent().getExtras() != null) {
            Log.d(TAG, "==> USER TAPPED NOTFICATION");
            data.put("wasTapped", true);
            for (String key : getIntent().getExtras().keySet()) {
                String value = getIntent().getExtras().getString(key);
                Log.d(TAG, "\tKey: " + key + " Value: " + value);
                data.put(key, value);
            }
        }

        // FCMPlugin.sendPushPayload(data); //enviar los datos de la notificacion

        finish();

        forceOrdersActivityReload();
    }
    private void forceOrdersActivityReload() {
        PackageManager pm = getPackageManager();
        Intent launchIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
        startActivity(launchIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "==> FCMPluginActivity onResume");
        final NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "==> FCMPluginActivity onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "==> FCMPluginActivity onStop");
    }
}
