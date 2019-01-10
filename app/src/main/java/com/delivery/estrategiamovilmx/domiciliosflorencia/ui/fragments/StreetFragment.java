package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class StreetFragment extends Fragment {
    private int mPageNumber;
    private EditText text_street;
    private TextView text_area;

    public StreetFragment() {
        // Required empty public constructor
    }
    public String getData(){ return text_street.getText().toString();}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static StreetFragment create(int pageNumber) {
        StreetFragment fragment = new StreetFragment();
        Bundle args = new Bundle();
        args.putInt(AddShippingAddressActivity.ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        ViewGroup view = (ViewGroup) inflater
                .inflate(R.layout.fragment_street, container, false);
        text_street = (EditText) view.findViewById(R.id.text_street);
        text_area = (TextView) view.findViewById(R.id.text_area);
        return view;
    }
    public void setError(String error){
        text_street.setError(error);
    }
}
