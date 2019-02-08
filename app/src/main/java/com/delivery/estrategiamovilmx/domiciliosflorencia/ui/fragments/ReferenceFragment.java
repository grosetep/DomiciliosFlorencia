package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ManageLocationsActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferenceFragment extends Fragment {
    private int mPageNumber;
    private EditText text_reference;
    private TextView text_area;
    private ShippingAddress address_editable;
    private TextView text_head_value;

    public String getData(){ return text_reference.getText().toString();}

    public ReferenceFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
        address_editable = (ShippingAddress) getArguments().getSerializable(ManageLocationsActivity.ADDRESS_EDITABLE);
    }

    public static ReferenceFragment create(int pageNumber,ShippingAddress address_param) {
        ReferenceFragment fragment = new ReferenceFragment();
        Bundle args = new Bundle();
        args.putInt(AddShippingAddressActivity.ARG_PAGE, pageNumber);
        args.putSerializable(ManageLocationsActivity.ADDRESS_EDITABLE, address_param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup view = (ViewGroup) inflater
                .inflate(R.layout.fragment_reference, container, false);
        text_reference = (EditText) view.findViewById(R.id.text_reference);
        text_area = (TextView) view.findViewById(R.id.text_area);

        if (address_editable!=null){//hay una direccion editable
            text_reference.setText(address_editable.getReference());
        }

        text_head_value = view.findViewById(R.id.text_head_value);
        text_head_value.setText(R.string.promt_reference_title);
        return view;
    }

    public void setError(String error){
        text_reference.setError(error);
    }

    @Override
    public void onResume() {
        super.onResume();
        text_area.setText(AddShippingAddressActivity.getShipping_point().getGooglePlace());
    }
}