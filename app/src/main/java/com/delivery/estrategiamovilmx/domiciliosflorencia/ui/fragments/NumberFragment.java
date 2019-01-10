package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class NumberFragment extends Fragment {
    private int mPageNumber;
    private EditText text_number;
    private TextView text_head_value;
    private LinearLayout layout_area;

    public String getData(){ return text_number.getText().toString();}

    public NumberFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static NumberFragment create(int pageNumber) {
        NumberFragment fragment = new NumberFragment();
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
                .inflate(R.layout.fragment_number, container, false);
        text_number = (EditText) view.findViewById(R.id.text_number);
        text_head_value = (TextView) view.findViewById(R.id.text_head_value);
        layout_area = (LinearLayout) view.findViewById(R.id.layout_area);
        text_head_value.setText(getString(R.string.promt_who));
        layout_area.setVisibility(View.GONE);
        return view;
    }

    public void setError(String error){
        text_number.setError(error);
    }

}
