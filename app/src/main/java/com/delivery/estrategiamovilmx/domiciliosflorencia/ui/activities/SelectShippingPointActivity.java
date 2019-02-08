package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

public class SelectShippingPointActivity extends AppCompatActivity {
    private static final String TAG = SelectShippingPointActivity.class.getSimpleName();
    private ImageButton button_select_map;
    private EditText text_location;
    private TextView text_location_ltd;
    private TextView text_location_lng;

    private final int PLACE_PICKER_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shipping_point);
        text_location = (EditText) findViewById(R.id.text_location);
        text_location_ltd = (TextView) findViewById(R.id.text_location_ltd);
        text_location_lng = (TextView) findViewById(R.id.text_location_lng);

        button_select_map = (ImageButton) findViewById(R.id.button_select_map);
        button_select_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar snackbar = Snackbar.make(v, getResources().getString(R.string.prompt_loading_map), Snackbar.LENGTH_LONG);
                snackbar.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(SelectShippingPointActivity.this), PLACE_PICKER_REQUEST);


                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    ShowConfirmations.showConfirmationMessage(getString(R.string.no_connection), SelectShippingPointActivity.this);

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(SelectShippingPointActivity.this, data);

                    if (place != null && !place.getAddress().equals("")) {
                        Log.d(TAG, place.getAddress() + " -> " + place.getLatLng());

                        text_location.setError(null);
                        text_location.setText(place.getAddress());
                        LatLng latlng = place.getLatLng();
                        text_location_ltd.setText(latlng.latitude+"");
                        text_location_lng.setText(latlng.longitude+"");

                    }
                }
                break;
        }
    }


}
