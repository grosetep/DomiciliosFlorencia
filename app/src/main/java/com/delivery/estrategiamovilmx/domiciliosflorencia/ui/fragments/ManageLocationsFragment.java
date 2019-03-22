package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateLocationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.AddressOperationResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetShippingAddressResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddShippingAddressActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ManageLocationsActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.ManageAddressAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class ManageLocationsFragment extends Fragment {

    private static final String TAG = ManageLocationsFragment.class.getSimpleName();
    private ArrayList<ShippingAddress> locations=new ArrayList();
    private RecyclerView recyclerview_manage_locations;
    private FrameLayout container_loading;
    private ManageAddressAdapter mAdapter;
    private TextView text_header_locations;

    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;

    public final String TYPE_QUERY = "all";
    public static final String ADDRESS_EDITABLE = "address_aditable";
    public static final String INDEX_ADDRESS = "index_address";



    public ManageLocationsFragment() {
        // Required empty public constructor
    }

    public static ManageLocationsFragment createInstance() {
        ManageLocationsFragment fragment = new ManageLocationsFragment();
        Bundle args  = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_manage_locations, container, false);
        //init elements
        recyclerview_manage_locations = v.findViewById(R.id.recyclerview_manage_locations);
        container_loading = v.findViewById(R.id.container_loading);
        no_connection_layout = v.findViewById(R.id.no_connection_layout);

        llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_manage_locations.setHasFixedSize(true);

        text_header_locations = v.findViewById(R.id.text_header_locations);
        UserItem user = GeneralFunctions.getCurrentUser(getActivity());
        if (user!=null && (user.getName()!=null && !user.getName().isEmpty())){
            String first_name = user.getName().substring(Constants.cero,user.getName().indexOf(" "));
            text_header_locations.setText(getString(R.string.text_header_admin_locations,first_name.concat(",")));
        }else{
            text_header_locations.setText(getString(R.string.text_header_admin_locations,","));
        }

        if (Connectivity.isNetworkAvailable(getContext())) {
            locations.clear();
            locations.add(null);
            initProcess(true);
            getShippingAddresses(TYPE_QUERY);
        }else{
            showNoConnectionLayout();
        }
        return v;
    }
    private void getShippingAddresses(String type_query) {
        UserItem user = GeneralFunctions.getCurrentUser(getActivity());
        RestServiceWrapper.getLocationsByUser(user != null ? user.getIdUser() : "0", type_query, new Callback<GetShippingAddressResponse>() {
            @Override
            public void onResponse(Call<GetShippingAddressResponse> call, retrofit2.Response<GetShippingAddressResponse> response) {
                //Log.d(TAG, "Respuesta ShippingAddress: " + response);
                if (response != null && response.isSuccessful()) {
                    GetShippingAddressResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {

                        if (products_response.getResult().size() > 0) {
                            locations.addAll(products_response.getResult());
                        }else{
                            ApplicationPreferences.saveLocalPreference(getContext(),Constants.FAVORITE_ADDRESS_SELECTED,"");
                        }

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                    } else {
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                    }

                    onSuccess();
                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                }
            }

            @Override
            public void onFailure(Call<GetShippingAddressResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new ManageAddressAdapter(this,locations);
        recyclerview_manage_locations.setAdapter(mAdapter);
        recyclerview_manage_locations.invalidate();
        if (recyclerview_manage_locations.getLayoutManager()==null){
            recyclerview_manage_locations.setLayoutManager(llm);
        }
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        recyclerview_manage_locations.setVisibility(flag ? View.GONE : View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }

    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        recyclerview_manage_locations.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    public void startAddShippingAddress(ShippingAddress ship, int position){
        Intent i = new Intent(getContext(),AddShippingAddressActivity.class);
        i.putExtra(ADDRESS_EDITABLE, ship);
        i.putExtra(INDEX_ADDRESS, position);
        startActivityForResult(i, PickupPointFragment.NEW_ADDRESS);
    }

    public void setShippingAddressSelected(int position){
        locations.get(position).setSelected(true);
        mAdapter.notifyItemChanged(position);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PickupPointFragment.NEW_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {//NEW_SHIPPING_ADDRESS
                    ShippingAddress ship = (ShippingAddress) data.getExtras().get(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS);
                    String update_address = (String)data.getExtras().get(AddShippingAddressActivity.EDITED_ADDRESS);
                    int position = data.getExtras().getInt(ManageLocationsActivity.INDEX_ADDRESS);
                    //check if update or new address
                    if (update_address.equals(String.valueOf(true))){//edited address:: update on db
                        locations.set(position,ship);
                        mAdapter.notifyItemChanged(position);
                        //setting flag to indicate one or more addresses updated
                        //ManageLocationsActivity.has_address_updated = 1;
                    }else{//new address::create element on list
                        ship.setIsNew(String.valueOf(true));
                        mAdapter.addItem(0, ship);
                        resetSelectedElement();
                        setShippingAddressSelected(0);
                        //recyclerview_manage_locations.scrollToPosition(0);
                        //return new location to activity
                        returnAddressSelected(ship);
                    }



                }
                break;
        }
    }
    private void resetSelectedElement(){
        for (ShippingAddress s:locations){
            if (s!=null) {
                s.setSelected(false);
            }
        }
        mAdapter.notifyDataSetChanged();
    }
    public void returnAddressSelected(ShippingAddress ship){
        Intent data = new Intent();
        data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        data.putExtra(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS, ship);
        getActivity().setResult(Activity.RESULT_OK, data);
        getActivity().finish();
    }

    public void deleteShippingAddress(final int position){

        //show loading
        initProcess(true);

        ShippingAddress item_to_delete = locations.get(position);
        UserItem user = GeneralFunctions.getCurrentUser(getActivity());
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setAddress(item_to_delete);
        request.setOperation(ManageLocationsActivity.DELETE_OPERATION);
        request.setUser(user);

        RestServiceWrapper.shippingAddressOperation(request, new Callback<AddressOperationResponse>() {
            @Override
            public void onResponse(Call<AddressOperationResponse> call, retrofit2.Response<AddressOperationResponse> response) {

                if (response != null && response.isSuccessful()) {
                    AddressOperationResponse products_response = response.body();

                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {
                        initProcess(false);
                        //delete element and update adapter
                        //Log.d(TAG,"inicio elimina de la lista elemento.....:"+position + " total:"+locations.size());
                        locations.remove(position);
                        //Log.d(TAG,"fin elimina de la lista elemento..... total:"+locations.size());
                        mAdapter.notifyItemRemoved(position);

                        mAdapter.notifyDataSetChanged();

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        //Log.d(TAG, "Mensage:" + response_error);
                    } else {
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                    }


                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                }
            }

            @Override
            public void onFailure(Call<AddressOperationResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
}
