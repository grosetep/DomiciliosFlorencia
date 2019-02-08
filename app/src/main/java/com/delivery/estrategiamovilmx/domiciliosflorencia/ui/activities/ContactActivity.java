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
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Contact;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShoppingCart;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetContactsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.ContactsAdapter;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class ContactActivity extends AppCompatActivity {
    private static final String TAG = ContactActivity.class.getSimpleName();
    private ArrayList<Contact> contacts=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview_contact;
    private ShoppingCart shopping_cart;
    private ShippingAddress shipping;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_actions;
    private AppCompatButton button_retry;
    private ContactsAdapter mAdapter;
    private int contact_position_selected = -1;
    private AppCompatButton button_next_contact;
    private AppCompatButton button_previous_contact;
    private final int NEW_CONTACT = 1;
    private MerchantItem merchant;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        //
        Intent i = getIntent();
        shopping_cart = (ShoppingCart)i.getSerializableExtra(Constants.SELECTED_SHOPPING_CART);
        shipping = (ShippingAddress) i.getSerializableExtra(Constants.SELECTED_SHIPPING);
        merchant = (MerchantItem) i.getSerializableExtra(Constants.MERCHANT_OBJECT);
        init();
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar_contact);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            contacts.clear();
            contacts.add(null);
            initProcess(true);
            getContacts();
        }else{
            showNoConnectionLayout();
        }
    }
    private void getContacts(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        RestServiceWrapper.getContactsByUser(user != null ? user.getIdUser() : "0", new Callback<GetContactsResponse>() {
            @Override
            public void onResponse(Call<GetContactsResponse> call, retrofit2.Response<GetContactsResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GetContactsResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {
                        for (Contact p : products_response.getResult()) {
                            Log.d(TAG, p.toString());
                        }
                        if (products_response.getResult().size() > 0) {
                            contacts.addAll(products_response.getResult());
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
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), ContactActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GetContactsResponse> call, Throwable t) {
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
    private void init(){
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_actions = (LinearLayout) findViewById(R.id.layout_actions);
        recyclerview_contact = (RecyclerView) findViewById(R.id.recyclerview_contact);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerview_contact.setHasFixedSize(true);
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                getContacts();
            }
        });
        button_next_contact = (AppCompatButton) findViewById(R.id.button_next_contact);
        button_next_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getContact_position_selected()!=-1) {
                    Intent i = new Intent(getApplicationContext(),PaymentMethodActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.SELECTED_CONTACT,contacts.get(getContact_position_selected()));
                    args.putSerializable(Constants.SELECTED_SHOPPING_CART,shopping_cart);
                    args.putSerializable(Constants.SELECTED_SHIPPING,shipping);
                    args.putSerializable(Constants.MERCHANT_OBJECT,merchant);
                    i.putExtras(args);
                    startActivity(i);
                }

            }
        });
        button_previous_contact = (AppCompatButton) findViewById(R.id.button_previous_contact);
        button_previous_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag? View.VISIBLE: View.GONE);
        layout_actions.setVisibility(flag? View.GONE: View.VISIBLE);
        recyclerview_contact.setVisibility(flag? View.GONE: View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_actions.setVisibility(View.GONE);
        recyclerview_contact.setVisibility(View.GONE);
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
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }
    private void setupAdapter(){
        // Set the adapter
        mAdapter = new ContactsAdapter(ContactActivity.this,contacts);
        recyclerview_contact.setAdapter(mAdapter);
        recyclerview_contact.invalidate();
        if (recyclerview_contact.getLayoutManager()==null){
            recyclerview_contact.setLayoutManager(llm);
        }
    }
    public void startAddContact(){
        Intent i = new Intent(this,AddContactActivity.class);
        startActivityForResult(i,NEW_CONTACT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case NEW_CONTACT:
                if (resultCode == Activity.RESULT_OK) {//NEW_CONTACT
                    Contact contact = (Contact) data.getSerializableExtra(AddContactActivity.NEW_CONTACT);
                    //reset all elements selected
                    if (getContact_position_selected()!=-1) {//ya hay uno seleccionado
                        resetElement(getContact_position_selected());
                    }
                    //Reset de todos los elementos existentes a..no seleccionados
                    resetElementsNotSelected();
                    //create element on list
                    mAdapter.addItem(0,contact);
                    setContactSelected(0);
                    recyclerview_contact.scrollToPosition(0);
                }
                break;
        }
    }
    public void setContactSelected(int position){
        contact_position_selected = position;
        button_next_contact.setEnabled(true);
        contacts.get(position).setSelected(true);
        mAdapter.notifyItemChanged(position);
    }
    public void resetElement(int position){
        if (position!=-1) {
            Contact old_contact = contacts.get(position);

            if (old_contact != null) {
                old_contact.setSelected(false);
                mAdapter.notifyItemChanged(position);
            }
        }
    }
    public void resetElementsNotSelected(){
        for(Contact c:contacts) {if (c !=null ) {c.setSelected(false);}}
        mAdapter.notifyDataSetChanged();
    }
    public int getContact_position_selected() {
        return contact_position_selected;
    }
}
