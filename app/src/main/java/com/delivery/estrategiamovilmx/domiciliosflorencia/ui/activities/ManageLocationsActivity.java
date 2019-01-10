package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetShippingAddressResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.ManageAddressAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PickupPointFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ManageLocationsActivity extends AppCompatActivity {
    private static final String TAG = ManageLocationsActivity.class.getSimpleName();
    private ArrayList<ShippingAddress> locations=new ArrayList();
    private RecyclerView recyclerview_manage_locations;
    private FrameLayout container_loading;
    private ManageAddressAdapter mAdapter;

    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;

    public final String TYPE_QUERY = "all";
    public static final String ADDRESS_EDITABLE = "address_aditable";
    public static final String INDEX_ADDRESS = "index_address";

    //operations
    public static final String UPDATE_OPERATION = "update";

    //public static int has_address_updated  = 0;//indica si se actualizaron direcciones para que se actualice lista en pantalla previa
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_locations);

        final Toolbar toolbar_shipping = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_fav));

        //init elements
        recyclerview_manage_locations = findViewById(R.id.recyclerview_manage_locations);
        container_loading = findViewById(R.id.container_loading);
        no_connection_layout = findViewById(R.id.no_connection_layout);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_manage_locations.setHasFixedSize(true);

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            locations.clear();
            locations.add(null);
            initProcess(true);
            getShippingAddresses(TYPE_QUERY);
        }else{
            showNoConnectionLayout();
        }

    }
    private void getShippingAddresses(String type_query) {
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        RestServiceWrapper.getLocationsByUser(user != null ? user.getIdUser() : "0", type_query, new Callback<GetShippingAddressResponse>() {
            @Override
            public void onResponse(Call<GetShippingAddressResponse> call, retrofit2.Response<GetShippingAddressResponse> response) {
                Log.d(TAG, "Respuesta ShippingAddress: " + response);
                if (response != null && response.isSuccessful()) {
                    GetShippingAddressResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {
                        /*for (ShippingAddress p : products_response.getResult()) {
                            Log.d(TAG, p.toString());
                        }*/
                        if (products_response.getResult().size() > 0) {
                            locations.addAll(products_response.getResult());
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
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), ManageLocationsActivity.this);
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
        mAdapter = new ManageAddressAdapter(ManageLocationsActivity.this,locations);
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
        Intent i = new Intent(getApplicationContext(),AddShippingAddressActivity.class);
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
                        setShippingAddressSelected(0);
                        //recyclerview_manage_locations.scrollToPosition(0);
                        //return new location to activity
                        returnAddressSelected(ship);
                    }



                }
                break;
        }
    }
    public void returnAddressSelected(ShippingAddress ship){
        Intent data = new Intent();
        data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        data.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        data.putExtra(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS, ship);
        setResult(Activity.RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
