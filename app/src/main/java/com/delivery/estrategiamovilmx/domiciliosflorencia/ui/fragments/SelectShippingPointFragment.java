package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ManageLocationsActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectShippingPointFragment extends Fragment {
    private static final String TAG = SelectShippingPointFragment.class.getSimpleName();
    private ImageButton button_select_map;
    private EditText text_location;
    private TextView text_location_ltd;
    private TextView text_location_lng;
    private TextView text_place_id;
    private ShippingAddress address_editable;
    private final int PLACE_PICKER_REQUEST = 1;

    private TextView text_head_value;
    private LinearLayout layout_area;

    public SelectShippingPointFragment() {
        // Required empty public constructor
    }
    public static SelectShippingPointFragment create(int pageNumber,ShippingAddress address_param) {
        SelectShippingPointFragment fragment = new SelectShippingPointFragment();
        Bundle args = new Bundle();
        args.putInt(AddShippingAddressActivity.ARG_PAGE, pageNumber);
        args.putSerializable(ManageLocationsActivity.ADDRESS_EDITABLE,address_param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        address_editable = (ShippingAddress) getArguments().getSerializable(ManageLocationsActivity.ADDRESS_EDITABLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_select_shipping_point, container, false);
        text_location = (EditText) v.findViewById(R.id.text_location);
        text_location_ltd = (TextView) v.findViewById(R.id.text_location_ltd);
        text_location_lng = (TextView) v.findViewById(R.id.text_location_lng);
        text_place_id = (TextView) v.findViewById(R.id.text_place_id);
        button_select_map = (ImageButton) v.findViewById(R.id.button_select_map);
        button_select_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_location.setError(null);
                Snackbar snackbar = Snackbar.make(v, getResources().getString(R.string.prompt_loading_map), Snackbar.LENGTH_LONG);
                snackbar.show();
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(getActivity()), PLACE_PICKER_REQUEST);

                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                    ShowConfirmations.showConfirmationMessage(getString(R.string.no_connection), getActivity());
                }
            }
        });

        if (address_editable!=null){//hay una direccion editable
            text_location.setText(address_editable.getGooglePlace());
            text_location_ltd.setText(address_editable.getLatitude());
            text_location_lng.setText(address_editable.getLongitude());
            text_place_id.setText(address_editable.getPlaceId());
        }
        layout_area = v.findViewById(R.id.layout_area);
        layout_area.setVisibility(View.GONE);
        text_head_value = v.findViewById(R.id.text_head_value);
        text_head_value.setText(getString(R.string.promt_new_location_title));
        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Place place = PlacePicker.getPlace(getActivity(), data);

                    if (place != null && !place.getAddress().equals("")) {
                        Log.d(TAG, place.getAddress() + " -> " + place.getLatLng() );

                        text_location.setError(null);
                        text_location.setText(place.getAddress());
                        text_place_id.setText(place.getId());
                        AddShippingAddressActivity.getShipping_point().setGooglePlace(place.getAddress().toString());
                        LatLng latlng = place.getLatLng();
                        text_location_ltd.setText(latlng.latitude+"");
                        text_location_lng.setText(latlng.longitude+"");

                    }
                }
                break;
        }
    }
    public String getLatitude(){
        return text_location_ltd.getText().toString();
    }
    public String getLongitude(){
        return text_location_lng.getText().toString();
    }
    public String getTextLocation(){return text_location.getText().toString();}
    public String getPlaceId(){return text_place_id.getText().toString();}
    public void setError(String error){
        text_location.setError(error);
    }
}