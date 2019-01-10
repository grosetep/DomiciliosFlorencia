package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.google.gson.Gson;

public class LocationActivity extends AppCompatActivity {
    private TextView text_location;
    private ImageButton button_drive;
    private ConfigItem config;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        assignActions();
        Gson gson = new Gson();
        String json_configuration = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.configuration_object);
        if (json_configuration!=null && !json_configuration.isEmpty()){
            config = gson.fromJson(json_configuration,ConfigItem.class);
            if (config!=null){
                text_location.setText(config.getAddress());
            }
        }
    }
    private void initGUI(){
        text_location = (TextView) findViewById(R.id.text_location);
        button_drive = (ImageButton) findViewById(R.id.button_drive);
    }
    private void assignActions(){
        button_drive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String geolocation = "";
                if (config!=null && !config.getLatitude().equals("0") && !config.getLongitude().equals("0")){
                    geolocation = "geo:".concat(config.getLatitude()).concat(",").concat(config.getLongitude());
                }else{
                    geolocation = "geo:".concat("0").concat(",").concat("0");
                }
                //String geolocation = "geo:".concat(config.getLatitude()).concat(",").concat(config.getLongitude());
                //String geolocation = "geo:".concat("0").concat(",").concat("0");
                Uri gmmIntentUri = Uri.parse(geolocation+"?q=" + Uri.encode(config.getAddress()));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                //mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error),LocationActivity.this);}
            }
        });
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
}
