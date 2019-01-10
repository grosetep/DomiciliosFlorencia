package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PostalCodeFragment extends Fragment {
    private int mPageNumber;
    private EditText text_cp;
    private LinearLayout layout_area;

    public PostalCodeFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static PostalCodeFragment create(int pageNumber) {
        PostalCodeFragment fragment = new PostalCodeFragment();
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
                .inflate(R.layout.fragment_postal_code, container, false);
        text_cp = (EditText) view.findViewById(R.id.text_cp);
        layout_area = (LinearLayout) view.findViewById(R.id.layout_area);
        layout_area.setVisibility(View.GONE);
        return view;
    }
    public String getData(){ return text_cp.getText().toString();}

}
