package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class GmapFragment extends Fragment {
    
    private int mPageNumber;
    private TextView text_coordinate;


    public String getData(){ return text_coordinate.getText().toString();}
    public GmapFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static GmapFragment create(int pageNumber) {
        GmapFragment fragment = new GmapFragment();
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
                .inflate(R.layout.fragment_gmap, container, false);
        text_coordinate = (TextView) view.findViewById(R.id.text_coordinate);
        return view;
    }

   public void setAddress(Address address){
       text_coordinate.setText(address.toString());
   }
}
