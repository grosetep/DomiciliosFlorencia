package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;

import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class ScreenSlidePageFragment extends Fragment {

    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    private static final String TAG = ScreenSlidePageFragment.class.getSimpleName();
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;
    private String current_step;
    private LinearLayout layout_area;
    private TextView text_area;
    private FrameLayout layout_container;
    /*each fild on the viewpager*/
    private EditText text_cp;
    private RelativeLayout layout_loading;
    private EditText text_street;
    private EditText text_num_ext;
    private AppCompatCheckBox checkbox_no_num;
    private EditText text_num_int;
    private EditText text_between;
    private EditText text_reference;
    private EditText text_colony;
    private TextView text_coordinate;



    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {
        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater
                .inflate(R.layout.fragment_screen_slide_page, container, false);


        layout_area = (LinearLayout) rootView.findViewById(R.id.layout_area);
        text_area = (TextView) rootView.findViewById(R.id.text_area);
        layout_container =(FrameLayout) rootView.findViewById(R.id.layout_container);
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getActivity());
        //final View content = layoutInflaterAndroid.inflate(getLayout(), null);
        //layout_container.addView(content);
        initDinamicLayout(rootView);
        return rootView;
    }
    private void initDinamicLayout( ViewGroup rootView){
        switch(getPageNumber()){
            case 0:text_cp = (EditText) rootView.findViewById(R.id.text_cp);
                layout_loading = (RelativeLayout) rootView.findViewById(R.id.layout_loading);
                break;
            case 1:text_street = (EditText) rootView.findViewById(R.id.text_street);break;
            case 2:text_num_ext = (EditText) rootView.findViewById(R.id.text_num_ext);
                checkbox_no_num = (AppCompatCheckBox) rootView.findViewById(R.id.checkbox_no_num);
                break;
            case 3:text_num_int = (EditText) rootView.findViewById(R.id.text_num_int);break;
            case 4:text_between = (EditText) rootView.findViewById(R.id.text_between);break;
            case 5:text_reference = (EditText) rootView.findViewById(R.id.text_reference);break;
            case 6:text_colony = (EditText) rootView.findViewById(R.id.text_colony);break;
            case 7:text_coordinate = (TextView) rootView.findViewById(R.id.text_coordinate);break;
        }
    }
   /* private int getLayout(){
        switch(getPageNumber()){
            case 0:return R.layout.layout_cp;
            case 1:return R.layout.layout_street;
            case 2:return R.layout.layout_num_ext;
            case 3:return R.layout.layout_num_int;
            case 4:return R.layout.layout_beetwen_streets;
            case 5:return R.layout.layout_reference;
            case 6:return R.layout.layout_colony;
            case 7:return R.layout.layout_gmaps_point;
        }
        return 0;
    }*/
    public ShippingAddress setData(ShippingAddress address){
        switch (getPageNumber()){
            case 0:address.setPostal_code(text_cp.getText().toString());
                layout_area.setVisibility(View.GONE);
                break;
            case 1:address.setStreet(text_street.getText().toString());
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);
                break;
            case 2:address.setNum_ext(text_num_ext.getText().toString());
                address.setNo_number(String.valueOf(checkbox_no_num.isChecked()));
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);
                break;
            case 3:address.setNum_int(text_num_int.getText().toString());
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);
                break;
            case 4:address.setBetween_streets(text_between.getText().toString());
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);break;
            case 5:address.setReference(text_reference.getText().toString());
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);break;
            case 6:address.setMunicipality(text_colony.getText().toString());
                text_area.setText(address.getTown());
                layout_area.setVisibility(View.VISIBLE);break;
        }
        return address;
    }
    public String getArgPage(){return "Pagina actual "+mPageNumber;}
    /**
     * Returns the page number represented by this fragment object.
     */
    public int getPageNumber() {
        return mPageNumber;
    }

    public void setStateAndCountry(final Address data){
        text_cp = (EditText) layout_container.findViewById(R.id.text_cp);
        if (text_cp!=null) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    text_cp.setText(data.getAdminArea() + " , " + data.getCountryName());
                }
            });

        }
    }
    public void setCoordinate(final Address data){
        text_coordinate = (TextView) layout_container.findViewById(R.id.text_coordinate);
        if (text_coordinate!=null){
            if (data!=null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_coordinate.setText(data.getPostalCode()+" , " + data.getLatitude() +" , "+ data.getAdminArea() + " ," + data.getCountryName());
                    }
                });
            }

        }
    }
    public void loadingView(boolean flag){
        layout_loading.setVisibility(flag?View.VISIBLE:View.GONE);

    }

}
