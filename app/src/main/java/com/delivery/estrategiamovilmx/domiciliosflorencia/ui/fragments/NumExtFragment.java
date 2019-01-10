package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
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
public class NumExtFragment extends Fragment {
    private int mPageNumber;
    private EditText text_num_ext;
    private AppCompatCheckBox checkbox_no_num;
    private TextView text_area;

    public String getData(){ return text_num_ext.getText().toString();}
    public String getData2(){ return String.valueOf(checkbox_no_num.isChecked());}
    public NumExtFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }

    public static NumExtFragment create(int pageNumber) {
        NumExtFragment fragment = new NumExtFragment();
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
                .inflate(R.layout.fragment_num_ext, container, false);
        text_num_ext = (EditText) view.findViewById(R.id.text_num_ext);
        checkbox_no_num = (AppCompatCheckBox) view.findViewById(R.id.checkbox_no_num);
        text_area = (TextView) view.findViewById(R.id.text_area);
        return view;
    }
    public void setError(String error){
        text_num_ext.setError(error);
    }
}
