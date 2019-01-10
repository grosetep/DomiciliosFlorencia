package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

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
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Contact;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PaymentMethod;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShoppingCart;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetPaymentMethodResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.PaymentMethodsAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class PaymentMethodActivity extends AppCompatActivity {
    private static final String TAG = ContactActivity.class.getSimpleName();
    private ArrayList<PaymentMethod> methods=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview_method;
    private ShoppingCart shopping_cart;
    private ShippingAddress shipping;
    private Contact contact;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_actions;
    private AppCompatButton button_retry;
    private PaymentMethodsAdapter mAdapter;
    private int method_position_selected = -1;
    private AppCompatButton button_next_method;
    private AppCompatButton button_previous_method;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        //
        Intent i = getIntent();
        shopping_cart = (ShoppingCart) i.getSerializableExtra(Constants.SELECTED_SHOPPING_CART);
        shipping = (ShippingAddress) i.getSerializableExtra(Constants.SELECTED_SHIPPING);
        contact = (Contact) i.getSerializableExtra(Constants.SELECTED_CONTACT);

        init();
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            methods.clear();
            initProcess(true);
            getPaymentMethods();
        }else{
            showNoConnectionLayout();
        }
    }
    private void getPaymentMethods(){
        RestServiceWrapper.getPaymentMethods(new Callback<GetPaymentMethodResponse>() {
            @Override
            public void onResponse(Call<GetPaymentMethodResponse> call, retrofit2.Response<GetPaymentMethodResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GetPaymentMethodResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {
                        for (PaymentMethod p : products_response.getResult()) {
                            Log.d(TAG, p.toString());
                        }
                        if (products_response.getResult().size()>0){
                            methods.addAll(products_response.getResult());
                        }

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)){
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                    }else{
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                    }

                    onSuccess();
                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,getString(R.string.error_generic)),PaymentMethodActivity.this);
                }
            }
            @Override
            public void onFailure(Call<GetPaymentMethodResponse> call, Throwable t) {
                Log.d(TAG,"ERROR: " +t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    private void init(){
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_actions = (LinearLayout) findViewById(R.id.layout_actions);
        recyclerview_method = (RecyclerView) findViewById(R.id.recyclerview_method);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview_method.setHasFixedSize(true);
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                getPaymentMethods();
            }
        });
        button_next_method = (AppCompatButton) findViewById(R.id.button_next_method);
        button_next_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (getMethod_position_selected()!=-1) {
                        Intent i = new Intent(getApplicationContext(),ReviewActivity.class);
                        Bundle args = new Bundle();
                        args.putSerializable(Constants.SELECTED_CONTACT,contact);
                        args.putSerializable(Constants.SELECTED_SHOPPING_CART,shopping_cart);
                        args.putSerializable(Constants.SELECTED_SHIPPING,shipping);
                        args.putSerializable(Constants.SELECTED_PAYMENT_METHOD,methods.get(getMethod_position_selected()));
                        i.putExtras(args);
                        startActivity(i);
                    }
            }
        });
        button_previous_method = (AppCompatButton) findViewById(R.id.button_previous_method);
        button_previous_method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new PaymentMethodsAdapter(PaymentMethodActivity.this,methods);
        recyclerview_method.setAdapter(mAdapter);
        recyclerview_method.invalidate();
        if (recyclerview_method.getLayoutManager()==null){
            recyclerview_method.setLayoutManager(llm);
        }
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag?View.VISIBLE:View.GONE);
        layout_actions.setVisibility(flag?View.GONE:View.VISIBLE);
        recyclerview_method.setVisibility(flag?View.GONE:View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_actions.setVisibility(View.GONE);
        recyclerview_method.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
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

    public void setMethodSelected(int position){
        method_position_selected = position;
        button_next_method.setEnabled(true);
        methods.get(position).setSelected(true);
        mAdapter.notifyItemChanged(position);
    }
    public void resetElement(int position){
        if (position!=-1) {
            PaymentMethod old_method = methods.get(position);

            if (old_method != null) {
                old_method.setSelected(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }
    public void resetElementsNotSelected(){
        for(PaymentMethod p:methods) {if (p !=null ) {p.setSelected(false);}}
        mAdapter.notifyDataSetChanged();
    }
    public int getMethod_position_selected() {
        return method_position_selected;
    }
}
