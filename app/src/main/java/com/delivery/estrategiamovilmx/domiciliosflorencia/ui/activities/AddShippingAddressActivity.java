package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UpdateLocationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.AddressOperationResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.NumIntFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ReferenceFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ScreenSlidePageFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.SelectShippingPointFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AddShippingAddressActivity extends AppCompatActivity {
    private static final String TAG = AddShippingAddressActivity.class.getSimpleName();
    public static final String NEW_SHIPPING_ADDRESS = "new_address";
    public static final String EDITED_ADDRESS = "edited_addredd";
    public static final String ARG_PAGE = "page";
    public static final String TYPE_REQUEST_CP = "cp";
    public static final String TYPE_REQUEST_OTHER = "other";
    public static final String TYPE_REQUEST = "type_request";
    private static final int PAGE_CP = 0;


    private static final int PAGE_NUM_INT = 1;
    private static final int PAGE_REFERENCE = 2;


    private ViewPager mPager;
    private float MIN_SCALE = 1f - 1f / 7f;
    private float MAX_SCALE = 1f;
    private AppCompatButton button_next;
    private AppCompatButton button_previous;
    private FrameLayout container_loading;
    private RelativeLayout layout_reference;
    public static ShippingAddress shipping_point = new ShippingAddress();
    private boolean update_address = false;
    private int index_address = 0;
    private String flow="";


    public boolean isUpdate_address() {
        return update_address;
    }

    public void setUpdate_address(boolean update_address) {
        this.update_address = update_address;
    }

    private ScreenSlidePagerAdapter mPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_shipping_address);

        final Toolbar toolbar_shipping = findViewById(R.id.toolbar_add_shipping);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        ShippingAddress address_param = (ShippingAddress)i.getSerializableExtra(ManageLocationsActivity.ADDRESS_EDITABLE);
        index_address = i.getIntExtra(ManageLocationsActivity.INDEX_ADDRESS,0);
        flow = i.getStringExtra(Constants.flow);

        if (address_param!=null){//viene una ubicacion para editar
            shipping_point = address_param;
            setUpdate_address(true);
        }else{//la ubicacion es nueva
            // Instantiate a ViewPager and a PagerAdapter.
            shipping_point.setIsNew(String.valueOf(true));
            shipping_point.setSelected(true);
            shipping_point.setId_location("0");
            setUpdate_address(false);
        }



        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(SelectShippingPointFragment.create(PAGE_CP,address_param));
        mPagerAdapter.addFragment(NumIntFragment.create(PAGE_NUM_INT,address_param));
        mPagerAdapter.addFragment(ReferenceFragment.create(PAGE_REFERENCE,address_param));
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                button_previous.setEnabled(mPager.getCurrentItem() > 0 ? true : false);
                button_next.setText((mPager.getCurrentItem() == mPagerAdapter.getCount() - 1) ? R.string.text_button_done : R.string.text_button_next);
            }
        });
        button_previous = (AppCompatButton) findViewById(R.id.button_previous);
        button_next = (AppCompatButton) findViewById(R.id.button_next);
        container_loading =  findViewById(R.id.container_loading);
        layout_reference = findViewById(R.id.layout_reference);
        assignActions();
    }
    private void nextPage(){
        if (mPager.getCurrentItem()<mPagerAdapter.getCount()-1) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    }
    private boolean validate(){
        boolean valid = true;
        /*******************************************************************Ultimo paso***************************************************************/
        if (mPager.getCurrentItem()==mPagerAdapter.getCount()-1) {
            //set value referece
            ScreenSlidePagerAdapter dapter_temp= (ScreenSlidePagerAdapter)mPager.getAdapter();
            ReferenceFragment fragment_reference = (ReferenceFragment)dapter_temp.getFragment(mPager.getCurrentItem());
            if (fragment_reference.getData().isEmpty()){
                fragment_reference.setError(getString(R.string.error_field_required));
                valid = false;
            }else{
                shipping_point.setReference(fragment_reference.getData());
            }


            //validations


            if (shipping_point.getGooglePlace()!=null && !shipping_point.getGooglePlace().isEmpty()){
                if(shipping_point.getReference()!=null && !shipping_point.getReference().isEmpty()){
                    //ejecutar update de location si aplica y despues devolver resultado. Para las direcciones nuevas queda como está
                    if (isUpdate_address()){//actualizar direccion y regresar a listado de direcciones
                        initProcess(true);
                        updateLocation(getShipping_point());
                    }else{//alta de direccion
                        Log.d(TAG,"Exito en validaciones, regresar a principal y crear nueva direcion en lista....");
                        createNewShippingAddress(getShipping_point());
                        //finishActivity();
                    }

                }else{
                    ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                    Fragment fragment = (Fragment)adapter.getFragment(2);
                    ((ReferenceFragment) fragment).setError(getString(R.string.error_field_required));
                    mPager.setCurrentItem(2);
                }
            }else{
                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                Fragment fragment = (Fragment)adapter.getFragment(PAGE_CP);
                ((SelectShippingPointFragment) fragment).setError(getString(R.string.error_field_required));
            }
        }else{/************************************************Primer paso***************************************************************/
            if (mPager.getCurrentItem()==0){//esta en el primer paso, postal code, calcula estado y pais...

                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                SelectShippingPointFragment fragment = (SelectShippingPointFragment)adapter.getFragment(mPager.getCurrentItem());
                if (fragment.getTextLocation().isEmpty()){
                    fragment.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    shipping_point.setLatitude(fragment.getLatitude());
                    shipping_point.setLongitude(fragment.getLongitude());
                    shipping_point.setPlaceId(fragment.getPlaceId());
                    shipping_point.setGooglePlace(fragment.getTextLocation());
                    nextPage();
                }

            }else{
                /************************************************Pasos intermedios***************************************************************/

                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                Fragment fragment = (Fragment)adapter.getFragment(mPager.getCurrentItem());
                /**************Numero interior ***********/
                if(fragment instanceof NumIntFragment){
                    if (((NumIntFragment) fragment).getData().isEmpty()){
                        shipping_point.setNum_int("");
                        nextPage();
                    }else {
                        shipping_point.setNum_int(((NumIntFragment) fragment).getData());
                        nextPage();
                    }
                    /**************Referencia ***********/
                }else if(fragment instanceof ReferenceFragment){
                    if (((ReferenceFragment) fragment).getData().isEmpty()){
                        ((ReferenceFragment) fragment).setError(getString(R.string.error_field_required));
                        valid = false;
                    }else {
                        shipping_point.setReference(((ReferenceFragment) fragment).getData());
                        nextPage();
                    }
                }

            }

        }
        return valid;
    }

    private void createNewShippingAddress(ShippingAddress ship){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setUser(user);
        request.setAddress(ship);
        request.setOperation(ManageLocationsActivity.CREATE_OPERATION);

        RestServiceWrapper.shippingAddressOperation(request, new Callback<AddressOperationResponse>() {
            @Override
            public void onResponse(Call<AddressOperationResponse> call, retrofit2.Response<AddressOperationResponse> response) {
                //Log.d(TAG, "Respuesta createNewShippingAddress: " + response.body());
                if (response != null && response.isSuccessful()) {
                    AddressOperationResponse operation_response = response.body();

                    if (operation_response != null && operation_response.getStatus().equals(Constants.success)) {
                        //Log.d(TAG, "Asignando id creado: " + operation_response.getResult().getId());
                        getShipping_point().setId_location(operation_response.getResult().getId());
                        finishActivity();

                    } else if (operation_response != null && operation_response.getStatus().equals(Constants.no_data)) {
                        String response_error = operation_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    } else {
                        String response_error = operation_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<AddressOperationResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });
    }

    private void updateLocation(ShippingAddress ship){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        UpdateLocationRequest request = new UpdateLocationRequest();
        request.setUser(user);
        request.setAddress(ship);
        request.setOperation(ManageLocationsActivity.UPDATE_OPERATION);
        RestServiceWrapper.shippingAddressOperation(request, new Callback<AddressOperationResponse>() {
            @Override
            public void onResponse(Call<AddressOperationResponse> call, retrofit2.Response<AddressOperationResponse> response) {
                //Log.d(TAG, "updateLocation Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    AddressOperationResponse operation_response = response.body();

                    if (operation_response != null && operation_response.getStatus().equals(Constants.success)) {
                        finishActivity();
                    } else if (operation_response != null && operation_response.getStatus().equals(Constants.no_data)) {
                        String response_error = operation_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    } else {
                        String response_error = operation_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<AddressOperationResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });
    }

    private void finishActivity(){
        Intent intent = new Intent();
        Bundle args = new Bundle();
        ShippingAddress new_address = getShipping_point();
        args.putSerializable(NEW_SHIPPING_ADDRESS, new_address);
        args.putString(EDITED_ADDRESS, String.valueOf(isUpdate_address()));
        args.putInt(ManageLocationsActivity.INDEX_ADDRESS,index_address);
        intent.putExtras(args);
        setResult(Activity.RESULT_OK, intent);
        shipping_point = new ShippingAddress();
        initProcess(false);
        finish();
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag?View.VISIBLE:View.GONE);
        layout_reference.setVisibility(flag ? View.GONE : View.VISIBLE);
    }
    private void onError(String response_error){
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), AddShippingAddressActivity.this);
        initProcess(false);
    }
    private void assignActions(){

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPager.getCurrentItem()>0)
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
    }
    public static ShippingAddress getShipping_point(){return shipping_point;}


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, ShippingActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public Fragment getFragment(int key){
            return mFragments.get(key);
        }
        @Override
        public int getCount() {
            return mFragments.size();
        }

    }


}