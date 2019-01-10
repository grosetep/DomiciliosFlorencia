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
public class ColonyFragment extends Fragment {
    private int mPageNumber;
    private EditText text_colony;
    private TextView text_area;

    public String getData(){ return text_colony.getText().toString();}

    public ColonyFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static ColonyFragment create(int pageNumber) {
        ColonyFragment fragment = new ColonyFragment();
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
                .inflate(R.layout.fragment_colony, container, false);
        text_colony = (EditText) view.findViewById(R.id.text_colony);
        text_area = (TextView) view.findViewById(R.id.text_area);
        return view;
    }

    public void setError(String error){
        text_colony.setError(error);
    }

}
