package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;
import com.google.gson.Gson;

public class CongratsActivity extends AppCompatActivity {
    private static final String TAG = CongratsActivity.class.getSimpleName();
    private ImageButton button_call;
    private TextView text_shipping_message;
    private TextView text_shipping_message_extra_info;
    private TextView text_order;
    private TextView text_phone;
    private ShippingOrderRequest request_order;

    private AppCompatButton button_done;
    private ConfigItem config = null;
    public static final String flow_congrats = "flow_congrats";
    public static final String NEW_ORDER = "new_order";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congrats);
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        request_order = (ShippingOrderRequest) i.getSerializableExtra(NEW_ORDER);
        init();
        assigActions();
    }

    private void init() {
        text_shipping_message = (TextView) findViewById(R.id.text_shipping_message);
        text_shipping_message_extra_info = (TextView) findViewById(R.id.text_shipping_message_extra_info);
        text_order = (TextView) findViewById(R.id.text_order);
        text_phone = (TextView) findViewById(R.id.text_phone);
        button_call = (ImageButton) findViewById(R.id.button_call);
        button_done = (AppCompatButton) findViewById(R.id.button_done);
    }

    private void assigActions() {
        Gson gson = new Gson();
        String json_config = ApplicationPreferences.getLocalStringPreference(CongratsActivity.this, Constants.configuration_object);

        if (json_config!=null) {
            config = gson.fromJson(json_config, ConfigItem.class);
        }

        text_shipping_message.setText(getString(R.string.shipping_message, ((request_order.getBudget() != null) ? request_order.getBudget().getTotalDestinations() : getString(R.string.shipping_address))));
        text_shipping_message_extra_info.setText("");
        String order_no = (request_order.getId_order()!=null)?request_order.getId_order():"0000";
        text_order.setText(getString(R.string.congrats_order,order_no));
        text_phone.setText(config!=null && config.getPhone()!=null?config.getPhone():getString(R.string.shipping_phone));

        button_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (config!=null && config.getPhone()!=null){
                    makeCall(config.getPhone());
                }else{makeCall(getString(R.string.shipping_phone));}
            }
        });
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent( context , OrdersDeliverActivity.class);
                i.putExtra(Constants.flow,flow_congrats);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
                finish();

            }
        });
    }

    private void makeCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

            if (!UtilPermissions.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
            }
        }
        startActivity(intent);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Context context = getApplicationContext();
        Intent i = new Intent( context , OrdersActivity.class);
        i.putExtra(Constants.flow,flow_congrats);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}
