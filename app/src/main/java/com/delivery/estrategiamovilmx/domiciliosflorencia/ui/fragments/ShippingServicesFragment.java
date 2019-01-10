package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingServiceModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.NewOrderActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.PublicationAdapter;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippingServicesFragment extends Fragment {
    private ArrayList<ShippingServiceModel> services_shipping = new ArrayList<>();
    private static RecyclerView recList;
    private StaggeredGridLayoutManager llm;
    private static final String TAG = ProductsFragment.class.getSimpleName();
    private PublicationAdapter adapter;
    private static final String LIST_SERVICES ="list_services";

    public ShippingServicesFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment createInstance(ArrayList<ShippingServiceModel> services) {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        args.putParcelableArrayList(LIST_SERVICES, services);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            ShippingServiceModel[] services = (ShippingServiceModel[]) getArguments().getParcelableArray(LIST_SERVICES);
            if (services!=null){
                services_shipping = new ArrayList<>(Arrays.asList(services));
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_shipping_services, container, false);
        CardView card_view_send_something = v.findViewById(R.id.card_view_send_something);
        card_view_send_something.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                Log.d("PrincipalMenuFragment", "json_object:" + json_user);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), NewOrderActivity.class);
                    startActivity(i);
                }
            }
        });

        return v;
    }


}
