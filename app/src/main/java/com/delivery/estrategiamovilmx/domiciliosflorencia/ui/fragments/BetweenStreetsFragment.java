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
public class BetweenStreetsFragment extends Fragment {
    private int mPageNumber;
    private EditText text_between;
    private TextView text_area;

    public String getData(){ return text_between.getText().toString();}

    public BetweenStreetsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static BetweenStreetsFragment create(int pageNumber) {
        BetweenStreetsFragment fragment = new BetweenStreetsFragment();
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
                .inflate(R.layout.fragment_between_streets, container, false);
        text_between = (EditText) view.findViewById(R.id.text_between);
        text_area = (TextView) view.findViewById(R.id.text_area);
        return view;
    }

    public void setError(String error){
        text_between.setError(error);
    }

}
