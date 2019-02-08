package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShoppingCart;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetShippingAddressResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.ShippingAddressAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ShippingActivity extends AppCompatActivity {
    private static final String TAG = ShippingActivity.class.getSimpleName();
    private ArrayList<ShippingAddress> locations=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview_locations;
    private ShoppingCart shopping_cart;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_actions;
    private AppCompatButton button_retry;
    private ShippingAddressAdapter mAdapter;
    private int address_position_selected = -1;
    private AppCompatButton button_next_ship;
    private AppCompatButton button_previous_ship;
    private final int NEW_ADDRESS = 1;
    public final String TYPE_QUERY = "all";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping);
        //
        Intent i = getIntent();
        shopping_cart = (ShoppingCart) i.getSerializableExtra(Constants.SELECTED_SHOPPING_CART);
        if (shopping_cart!=null){  Log.d(TAG,"shopping_cart:"+shopping_cart.getTotal());}

        init();
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar_shipping);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            locations.clear();
            locations.add(null);
            initProcess(true);
            getShippingAddresses();
        }else{
            showNoConnectionLayout();
        }


    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag?View.VISIBLE:View.GONE);
        layout_actions.setVisibility(flag?View.GONE:View.VISIBLE);
        recyclerview_locations.setVisibility(flag?View.GONE:View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }

    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_actions.setVisibility(View.GONE);
        recyclerview_locations.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    private void init(){
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_actions = (LinearLayout) findViewById(R.id.layout_actions);
        recyclerview_locations = (RecyclerView) findViewById(R.id.recyclerview_locations);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview_locations.setHasFixedSize(true);
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                getShippingAddresses();
            }
        });
        button_next_ship = (AppCompatButton) findViewById(R.id.button_next_ship);
        button_next_ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getAddress_position_selected()!=-1){
                    startContact();
                }
            }
        });
        button_previous_ship = (AppCompatButton) findViewById(R.id.button_previous_ship);
        button_previous_ship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
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
    private void getShippingAddresses(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        RestServiceWrapper.getLocationsByUser(user != null ? user.getIdUser() : "0",TYPE_QUERY, new Callback<GetShippingAddressResponse>() {
            @Override
            public void onResponse(Call<GetShippingAddressResponse> call, retrofit2.Response<GetShippingAddressResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GetShippingAddressResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {
                        for (ShippingAddress p : products_response.getResult()) {
                            Log.d(TAG, p.toString());
                        }
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
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), ShippingActivity.this);
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
        mAdapter = new ShippingAddressAdapter(ShippingActivity.this,locations);
        recyclerview_locations.setAdapter(mAdapter);
        recyclerview_locations.invalidate();
        if (recyclerview_locations.getLayoutManager()==null){
            recyclerview_locations.setLayoutManager(llm);
        }
    }
    public void startAddShippingAddress(){
        Intent i = new Intent(this,AddShippingAddressActivity.class);
        startActivityForResult(i,NEW_ADDRESS);
    }
    public void startContact(){
        Intent i = new Intent(this,ContactActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.SELECTED_SHIPPING,locations.get(getAddress_position_selected()));
        args.putSerializable(Constants.SELECTED_SHOPPING_CART,shopping_cart);
        i.putExtras(args);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_ADDRESS:
                if (resultCode == Activity.RESULT_OK) {//NEW_SHIPPING_ADDRESS
                    ShippingAddress ship = (ShippingAddress) data.getSerializableExtra(AddShippingAddressActivity.NEW_SHIPPING_ADDRESS);
                    //reset all elements selected
                    if (getAddress_position_selected()!=-1) {//ya hay uno seleccionado
                        resetElement(getAddress_position_selected());
                    }
                    //Reset de todos los elementos existentes a..no seleccionados
                    resetElementsNotSelected();
                    //create element on list
                    mAdapter.addItem(0,ship);
                    setShippingAddressSelected(0);
                    recyclerview_locations.scrollToPosition(0);
                }
                break;
        }
    }
    public void setShippingAddressSelected(int position){
        address_position_selected = position;
        button_next_ship.setEnabled(true);
        locations.get(position).setSelected(true);
        mAdapter.notifyItemChanged(position);
    }
    public void resetElement(int position){
        if (position!=-1) {
            ShippingAddress old_location = locations.get(position);

            if (old_location != null) {
                old_location.setSelected(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }
    public void resetElementsNotSelected(){
        for(ShippingAddress loc:locations) {if (loc !=null ) {loc.setSelected(false);}}
        mAdapter.notifyDataSetChanged();
    }
    public int getAddress_position_selected() {
        return address_position_selected;
    }
}