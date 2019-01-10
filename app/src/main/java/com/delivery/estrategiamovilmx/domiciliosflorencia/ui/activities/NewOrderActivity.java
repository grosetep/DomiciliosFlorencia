package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ElementChanged;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.OrderShipping;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PackageDetail;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PickupAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingBudgetRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.BudgetResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.DatePickerFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.DetailOrderFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PickupPointFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class NewOrderActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener{
    private static final String TAG = NewOrderActivity.class.getSimpleName();
    public static final String ARG_PAGE = "page";
    private static final int PAGE_CP = 0;
    private static final int BUDGET_REQUEST = 1;
    //crear lista de objectos direccion y agregar origen y destinos...

    private ViewPager mPager;
    private float MIN_SCALE = 1f - 1f / 7f;
    private float MAX_SCALE = 1f;
    private AppCompatButton button_next;
    private AppCompatButton button_previous;
    private RelativeLayout layout_loading;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private ProgressDialog progressDialog;
    private int destinations = 1;//por default se considera 1 origen y 1 destino inicial
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private OrderShipping order_shipping = new OrderShipping();
    private ArrayList<PickupAddress> circular_tour_destinations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order);

        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_origin));


        mPager = findViewById(R.id.pager);

        fragments.add(PickupPointFragment.create(Constants.ORIGIN_ADDRESS, Constants.cero, this));
        fragments.add(PickupPointFragment.create(Constants.DESTINATION_ADDRESS, getDestinations(), this));
        fragments.add(DetailOrderFragment.newInstance());
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),fragments);

        //mPagerAdapter.addFragment(PickupPointFragment.create(Constants.ORIGIN_ADDRESS,Constants.cero,this));
        //mPagerAdapter.addFragment(PickupPointFragment.create(Constants.DESTINATION_ADDRESS,getDestinations(),this));
        //mPagerAdapter.addFragment(DetailOrderFragment.newInstance());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);//initial fragments in memory
        mPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                button_previous.setEnabled(mPager.getCurrentItem() > 0?true:false);
                button_next.setText((mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)?R.string.text_button_budget:R.string.text_button_next);
                assignTitle(mPager.getCurrentItem());


            }
        });

        button_previous = (AppCompatButton) findViewById(R.id.button_previous);
        button_next = (AppCompatButton) findViewById(R.id.button_next);
        layout_loading = (RelativeLayout) findViewById(R.id.layout_loading);
        assignActions();
    }

    public int getDestinations() {
        return destinations;
    }
    public PickupAddress getCurrentDestination(){
        ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
        PickupPointFragment current_fragment = (PickupPointFragment)adapter.getFragment(mPager.getCurrentItem());

        PickupPointFragment fragment = (PickupPointFragment) current_fragment;
        PickupAddress address = new PickupAddress();
        address.setGooglePlace(fragment.getAddressSelected() != null ? fragment.getAddressSelected() : null);
        //address.setLatitude(fragment.getLatitude() != null ? fragment.getLatitude() : "");
        //address.setLongitude(fragment.getLongitude() != null ? fragment.getLongitude() : "");
        //address.setPostalCode(fragment.getPostalCode() != null ? fragment.getPostalCode() : "");
        address.setNameContact(fragment.getNameContact() != null ? fragment.getNameContact() : "");
        address.setPhoneContact(fragment.getPhoneContact() != null ? fragment.getPhoneContact() : "");
        address.setDateShipping(fragment.getDate() != null ? fragment.getDate() : "");
        address.setStartHour(fragment.getTimeFrom() != null ? fragment.getTimeFrom() : "");
        address.setEndHour(fragment.getTimeTo() != null ? fragment.getTimeTo() : "");
        address.setComment(fragment.getComment() != null ? fragment.getComment() : "");
        address.setTypeAddress(fragment.getType_address());
        address.setPickupPointNumber(fragment.getPickup_point_number());
        return address;
    }
    public void addDestination(){
        //validar si ya existe el actual destino agregado, sino entonces ejecutar  validate(); para agregarlo
        PickupAddress address = getCurrentDestination();
        ElementChanged element = GeneralFunctions.somethingHasChanged(order_shipping.getDestinations(), address);
        if (element.isNew()) {
            //Log.d(TAG, "DIRECCION nueva desde boton agregar....");
            order_shipping.getDestinations().add(address);
        }
        destinations+=1;
        Fragment last_fragment = fragments.get(fragments.size()-1);

        fragments.remove(fragments.size() - 1);

        fragments.add(PickupPointFragment.create(Constants.DESTINATION_ADDRESS, getDestinations(), this));

        fragments.add(last_fragment);

        mPager.setAdapter(null);
        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments));
        //agregado, de esta manera tambien funciona agregar el fragment a pager, pero valores en memoria se pierden a menos que esten en campos del layout
        /*ScreenSlidePagerAdapter current_adapter = (ScreenSlidePagerAdapter)mPager.getAdapter();
        mPager.setAdapter(current_adapter);*/

        mPager.setCurrentItem(mPagerAdapter.getCount() - 2, true);
        assignTitle(destinations);

    }
    public int getTotalDestinations(){return destinations;}
    //elimina el ultimo y no el seleccionado
    public void delDestination(int pickup_point_number){
        //Log.d(TAG,"DESTINO A ELIMINAR: "+pickup_point_number + " dentro de un total de " +order_shipping.getDestinations().size()+" destinos");
        //Log.d(TAG,"Eliminar de la vista......");

        int index_frm = -1;
        for(int i=0;i<fragments.size();i++){//itera todas las direcciones desde el origen y todos los destinos
            if (fragments.get(i) instanceof PickupPointFragment){
                PickupPointFragment frm_temp = (PickupPointFragment) fragments.get(i);
                    if (frm_temp.getPickup_point_number() == pickup_point_number) {
                        index_frm = i;
                        //Log.d(TAG,"INDICE EN VIEWPAGER:"+index_frm);
                        break;
                    }

            }
        }

        fragments.remove(index_frm);
        //Log.d(TAG, "Total de elementos visuales actuales: " + fragments.size());
        destinations-=1;
        //reestructurar elementos dentro de viewpager
        mPager.setAdapter(null);
        for(int i=1;i<fragments.size()-1;i++){
            if(fragments.get(i) instanceof PickupPointFragment) {
                PickupPointFragment frm = (PickupPointFragment) fragments.get(i);
                //Log.d(TAG, "Revisando elemento:" + i + "  point_number:" + frm.getPickup_point_number());
                frm.setPickup_point_number(i);
                //Log.d(TAG, "Actualizado a:" + frm.getPickup_point_number());
            }
        }
        //Log.d(TAG, "FRAGMENTS ACTUALIZADOS:");
        /*for(Fragment frm : fragments){
            if (frm  instanceof PickupPointFragment){
                PickupPointFragment t = (PickupPointFragment) frm;
                Log.d(TAG,"punto:"+t.getPickup_point_number());}
        }*/


        mPager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager(), fragments));
        //Log.d(TAG, "Esteblecer destino: " + (pickup_point_number - 1) + " como vista actual");
        mPager.setCurrentItem(pickup_point_number - 1, true);
        assignTitle(pickup_point_number - 1);
        mPagerAdapter.notifyDataSetChanged();


        //Log.d(TAG, "elementos despues del proceso:");
        // asi quedo la lista de fragments
        /*for(int i=0;i<fragments.size();i++) {
            if (fragments.get(i) instanceof PickupPointFragment) {
                PickupPointFragment t = (PickupPointFragment) fragments.get(i);
                Log.d(TAG, "punto_f:" + t.getPickup_point_number());

            }else{
                Log.d(TAG,"final...");}
        }*/
        //eliminar elemento de la lista de destinos
        //Log.d(TAG, "Eliminar de lista de destinos..........");
        for (int i=0;i<order_shipping.getDestinations().size();i++){
            if (order_shipping.getDestinations().get(i).getPickupPointNumber() == pickup_point_number){
                order_shipping.getDestinations().remove(i);
            }
        }
        Collections.sort(order_shipping.getDestinations());
    }

    private void assignTitle(int page){
        if (page == Constants.cero){ // primer paso
            getSupportActionBar().setTitle(getString(R.string.title_origin));
        }else if(page == destinations + 1){ // ultimo paso
            getSupportActionBar().setTitle(getString(R.string.promt_title_activity_detail_order));
        }else{//pasos intermedios
            getSupportActionBar().setTitle(getString(R.string.title_destination,page));
        }

    }
    private void nextPage(){
        if (mPager.getCurrentItem()<mPagerAdapter.getCount()-1) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    }

    private boolean validatePoints(PickupAddress address){
        boolean valid = true;
        if (address.getGooglePlace()==null ||
                address.getNameContact().isEmpty() ||
                address.getPhoneContact().isEmpty() ||
                address.getDateShipping().isEmpty() ||
                address.getStartHour().isEmpty() ||
                address.getEndHour().isEmpty() ||
                address.getComment().isEmpty()) {
                Fragment fragment = mPagerAdapter.getFragment(address.getPickupPointNumber());

                if (fragment instanceof PickupPointFragment){

                    PickupPointFragment frm = (PickupPointFragment) fragment;
                    //set error y mover hacia ese fragmento
                    mPager.setCurrentItem(address.getPickupPointNumber());
                    //frm.getText_location().setError(getString(R.string.error_field_required));
                    ShowConfirmations.showConfirmationMessage(getString(R.string.prompt_error_field_required),this);

                }
            return false;
        }
        return valid;
    }
    private boolean validateOrder(){

        //validar en forma secuencial y donde encuentre error detenerse y enviar a esa pantalla al usuario.
        boolean valid = true;
        //valida origen
        if (validatePoints(order_shipping.getOrigin())){
            //valida destinos
            for(PickupAddress address:order_shipping.getDestinations()){
                if(!validatePoints(address)){
                    return false;
                }
            }
            //valida detalle
            if (order_shipping.getPackageDetail().getVehicle().isEmpty() ||
                    order_shipping.getPackageDetail().getWeigth().isEmpty() ||
                        order_shipping.getPackageDetail().getContent().isEmpty()){
                mPager.setCurrentItem(mPagerAdapter.getCount()-1);
                ShowConfirmations.showConfirmationMessage(getString(R.string.prompt_error_field_required), this);
                return  false;
            }



        }else{return false;}
        return valid;
    }

    private ArrayList<PickupAddress> getNewCircularDestinies(){
        ArrayList<PickupAddress> destinations_inverse = new ArrayList<>();
        int total_detinations = order_shipping.getDestinations().size();
        if(total_detinations == 1){
            //Log.d(TAG, "total_detinations:" + total_detinations + "  no hay iteraciones");
            PickupAddress destiny_1 = PickupAddress.getCopy(order_shipping.getDestinations().get(0));
            destiny_1.setAddedByCircularTour(true);
            destiny_1.setPickupPointNumber(2);

            PickupAddress origen = PickupAddress.getCopy(order_shipping.getOrigin());
            origen.setAddedByCircularTour(true);
            origen.setPickupPointNumber(3);
            origen.setTypeAddress(Constants.DESTINATION_ADDRESS);

            destinations_inverse.add(destiny_1);
            destinations_inverse.add(origen);

        }else {

            //Log.d(TAG, "total_detinations:" + total_detinations + "  inicia iteracion reverse...");

            for (int i = order_shipping.getDestinations().size() - 1; i >= 0; i--) {//agrega cada destino en orden inverso
                PickupAddress temp = (PickupAddress) PickupAddress.getCopy(order_shipping.getDestinations().get(i));
                //Log.d(TAG, "elemento:" + temp.getPickupPointNumber());
                temp.setAddedByCircularTour(true);
                total_detinations += 1;
                temp.setPickupPointNumber(total_detinations);//aumenta el numero de destino en la lista
                //Log.d(TAG, "set pickupnumber:" + temp.getPickupPointNumber() + "");
                destinations_inverse.add(temp);
            }
            //Log.d(TAG, "destinations_inverse size:" + destinations_inverse.size());
            //agrega el origen al final para quedar en punto final
            PickupAddress origen = PickupAddress.getCopy(order_shipping.getOrigin());
            origen.setAddedByCircularTour(true);
            total_detinations += 1;
            origen.setPickupPointNumber(total_detinations);
            origen.setTypeAddress(Constants.DESTINATION_ADDRESS);
            destinations_inverse.add(origen);
        }
        return destinations_inverse;
    }
    private boolean validate(){
        boolean valid = true;
        /*******************************************************************Ultimo paso***************************************************************/
        if (mPager.getCurrentItem()==mPagerAdapter.getCount()-1) {
            //set value referece
            ScreenSlidePagerAdapter dapter_temp= (ScreenSlidePagerAdapter)mPager.getAdapter();
            Fragment fragment = dapter_temp.getFragment(mPager.getCurrentItem());
            if (fragment instanceof DetailOrderFragment){
                DetailOrderFragment frm = (DetailOrderFragment) fragment;
                final int originalDestinations = order_shipping.getDestinations().size();

                PackageDetail detail = new PackageDetail();
                detail.setVehicle(frm.getText_vehicle().getText().toString());
                detail.setWeigth(frm.getText_weighing().getText().toString());
                detail.setContent(frm.getText_content().getText().toString());

                //validation about circular_tour_enabled, if yes then copy destinies inverse for getting budget
                Log.d(TAG,"is_circular_tour:"+frm.getCheckbox_circular_tour().isChecked());
                order_shipping.setOriginalDestinations(originalDestinations);
                if (frm.getCheckbox_circular_tour().isChecked()){
                    circular_tour_destinations.addAll(getNewCircularDestinies());
                    order_shipping.getDestinations().addAll(circular_tour_destinations);
                    order_shipping.setRoundTrip(true);
                }else{
                    order_shipping.setRoundTrip(false);
                }

                order_shipping.setPackageDetail(detail);
                if(validateOrder()){//si campos son validos entonces calcular costo y presentar pagina de confirmación
                    //Log.d(TAG,"Calcular costo del envio !!!!!!!!!!!");
                    initProcess(true);
                    Log.d(TAG, order_shipping.toString());
                    ShippingBudgetRequest request = new ShippingBudgetRequest();
                    request.setId_order("0");
                    request.setOrder_shipping(order_shipping);
                    RestServiceWrapper.budget(request, new Callback<BudgetResponse>() {
                        @Override
                        public void onResponse(Call<BudgetResponse> call, retrofit2.Response<BudgetResponse> response) {
                            initProcess(false);
                            //Log.d(TAG, "Respuesta Presupuesto: " + response.toString());
                            if (response != null && response.isSuccessful()) {
                                BudgetResponse budget_response = response.body();
                                if (budget_response != null && budget_response.getStatus().equals(Constants.success)) {
                                    onSuccess(budget_response,originalDestinations);
                                } else if (budget_response != null && budget_response.getStatus().equals(Constants.no_data)) {
                                    String response_error = budget_response.getMessage();
                                    Log.d(TAG, "Mensage:" + response_error);
                                    onError(getString(R.string.error_invalid_login, response_error));
                                } else {
                                    String response_error = budget_response.getMessage();
                                    Log.d(TAG, "Error:" + response_error);
                                    onError(getString(R.string.error_invalid_login, response_error));
                                }


                            } else {
                                Log.d(TAG, "Peticion errónea");
                                onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                            }
                        }

                        @Override
                        public void onFailure(Call<BudgetResponse> call, Throwable t) {
                            Log.d(TAG, "ERROR en parseo de respuesta: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                            initProcess(false);
                            onError(getString(R.string.error_invalid_login, t.getMessage()));
                        }
                    });
                }else{
                    order_shipping.getDestinations().clear();
                    order_shipping.getDestinations().addAll(GeneralFunctions.cleanDirections(order_shipping.getDestinations()));
                }

            }
            /*if (fragment_reference.getData().isEmpty()){
                fragment_reference.setError(getString(R.string.error_field_required));
                valid = false;
            }else{
                shipping_point.setReference(fragment_reference.getData());
            }*/


            //validations


            /*if (shipping_point.getGooglePlace()!=null && !shipping_point.getGooglePlace().isEmpty()){
                if(shipping_point.getReference()!=null && !shipping_point.getReference().isEmpty()){
                    Log.d(TAG,"Exito en validaciones, regresar a principal y crear nueva direcion en lista....");
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    ShippingAddress new_address = getShipping_point();
                    args.putSerializable(NEW_SHIPPING_ADDRESS, new_address);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK,intent);
                    shipping_point = new ShippingAddress();
                    finish();
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
            }*/
        }else{/************************************************Primer paso***************************************************************/
            if (mPager.getCurrentItem()==0){//guardar direccion origen

                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                final PickupPointFragment fragment = (PickupPointFragment)adapter.getFragment(mPager.getCurrentItem());
                nextPage();
                //guardar datos existentes en un objeto pickupAddress
                //y agregarlo a la orden de esta actividad
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        PickupAddress address = new PickupAddress();
                        address.setGooglePlace(fragment.getAddressSelected());
                        //address.setLatitude(fragment.getLatitude());
                        //address.setLongitude(fragment.getLongitude());
                        //address.setPostalCode(fragment.getPostalCode());
                        address.setNameContact(fragment.getNameContact());
                        address.setPhoneContact(fragment.getPhoneContact());
                        address.setDateShipping(fragment.getDate());
                        address.setStartHour(fragment.getTimeFrom());
                        address.setEndHour(fragment.getTimeTo());
                        address.setComment(fragment.getComment());
                        address.setTypeAddress(fragment.getType_address());
                        address.setPickupPointNumber(fragment.getPickup_point_number());
                        //address.setPlaceId(fragment.getText_place_id());
                        order_shipping.setOrigin(address);
                        Log.d(TAG,"Guardar direccion origen:"+order_shipping.getOrigin().toString());
                    }
                });


            }else{
                /************************************************Pasos intermedios***************************************************************/
               Log.d(TAG,"PASO INTERMEDIO....");
                //guardar direcciones destino
                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                final Fragment current_fragment = adapter.getFragment(mPager.getCurrentItem());

                if(current_fragment instanceof PickupPointFragment){

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            PickupPointFragment fragment = (PickupPointFragment) current_fragment;
                            PickupAddress address = new PickupAddress();
                            address.setGooglePlace(fragment.getAddressSelected());
                            //address.setLatitude(fragment.getLatitude());
                            //address.setLongitude(fragment.getLongitude());
                            //address.setPostalCode(fragment.getPostalCode());
                            address.setNameContact(fragment.getNameContact());
                            address.setPhoneContact(fragment.getPhoneContact());
                            address.setDateShipping(fragment.getDate());
                            address.setStartHour(fragment.getTimeFrom());
                            address.setEndHour(fragment.getTimeTo());
                            address.setComment(fragment.getComment());
                            address.setTypeAddress(fragment.getType_address());
                            address.setPickupPointNumber(fragment.getPickup_point_number());
                            //address.setPlaceId(fragment.getText_place_id());
                            //limpiar direccion anterior y actualizarla para evitar duplicados

                            ElementChanged element = GeneralFunctions.somethingHasChanged(order_shipping.getDestinations(),address);
                            if (element.isNew()){
                                //Log.d(TAG, "DIRECCION nueva....");
                                order_shipping.getDestinations().add(address);
                            }else if(element.isChanged()){
                                //Log.d(TAG, "PASO actualiza....elimina elemento en indeice:"+element.getIndexElement() +" y agrega elemento actualizado.");
                                order_shipping.getDestinations().remove(element.getIndexElement());
                                order_shipping.getDestinations().add(element.getIndexElement(), address);
                            }else{
                                //Log.d(TAG, "DIRECCION ...."+ element.getIndexElement() + " sin cambios.");
                            }
                        }
                    });

                    //Log.d(TAG,"ORIGEN GUARDADO::"+order_shipping.getOrigin().toString());
                    nextPage();
                }

            }

        }
        return valid;
    }
    private void onSuccess(BudgetResponse response, int originalDestinations){
        initProcess(false);
        Intent i = new Intent(this,BudgetActivity.class);
        Bundle args = new Bundle();
        response.getResult().setOriginalDestinations(originalDestinations);//update real destinations if round trip is enabled
        //Log.d(TAG, "Presupuesto::::::::::::"+response.getResult().toString());
        args.putSerializable(BudgetActivity.NEW_BUDGET, response.getResult());
        args.putSerializable(BudgetActivity.NEW_ORDER,order_shipping);
        i.putExtras(args);
        startActivityForResult(i,BUDGET_REQUEST);


    }
    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promt_loading));
        else
            closeProgressDialog();
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
    }
    private void onError(String response_error){
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,response_error),this);
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
                if ( mPager.getCurrentItem()>0) {
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                }
            }
        });
    }

    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.prompt_discart_promt_title));
        alertDialogBuilder.setTitle(getString(R.string.prompt_discart_promt));
        alertDialogBuilder.setPositiveButton(getString(R.string.prompt_action_discart), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                open();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        open();
    }

    static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments = null;

        public ScreenSlidePagerAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        public void removeFragment(int index) {
            mFragments.remove(index);
            notifyDataSetChanged();
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
    /*Funcionalidad para fechas*/
    @Override
    public void onDateSelected(int ano, int mes, int dia) {
        //Log.d(TAG,"fecha: " + ano + " / " + mes + " / " + dia);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
        SimpleDateFormat sdf_to = new SimpleDateFormat("dd-M-yy");
        String dateInString = dia+"-"+(mes+1)+"-"+ano;
        Date date = null;
        try {
            date = sdf.parse(dateInString);
        } catch (ParseException e) {

        }

        actualizarFecha(sdf_to.format(date));
    }
    private Calendar createDateCalendar(int year,int month, int day){
        final Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        return c;
    }
    public void actualizarFecha(String date) {
        // Setear en el textview la fecha
        ScreenSlidePagerAdapter dapter_temp= (ScreenSlidePagerAdapter)mPager.getAdapter();
        Fragment fragment = dapter_temp.getFragment(mPager.getCurrentItem());

        DatePickerFragment f1=(DatePickerFragment)getSupportFragmentManager().findFragmentByTag("date_"+mPager.getCurrentItem());

        //String date = ano + "-" + (mes + 1) + "-" + dia;
        Log.d(TAG, "Se asigna fecha: " + date + " al campo en fragmento: " + mPager.getCurrentItem());
        if (f1 != null) {

            if (f1 != null) {
                if (fragment instanceof PickupPointFragment){
                    PickupPointFragment frm = (PickupPointFragment) fragment;
                    frm.setDateSelected(date);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BUDGET_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d(TAG, "Respuesta del presupuesto::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
                    Bundle args = data.getExtras();
                    ShippingOrderRequest request = (ShippingOrderRequest)args.getSerializable(CongratsActivity.NEW_ORDER);
                    Log.d(TAG,"Presupuesto que se manda a Congrats Activity");
                    Log.d(TAG,"getBudget:"+request.getBudget().toString());
                    //aqui terminar el activity y invocar CongratsActivity
                    startCongratsActivity(request);
                }else{order_shipping.getDestinations().removeAll(circular_tour_destinations); Log.d(TAG, "Se regreso::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");}
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;

        }
    }
    public void startCongratsActivity(ShippingOrderRequest request){
        Intent i = new Intent(this,CongratsActivity.class);
        Bundle args = new Bundle();
        //asigna numero de orden generada
        args.putSerializable(CongratsActivity.NEW_ORDER,request);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(args);
        finish();
        startActivity(i);
    }
}
