package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.BudgetResult;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.OrderShipping;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PickupAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ShippingOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.DividerItemDecoration;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.BudgetAdapter;
import com.google.gson.Gson;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;

public class BudgetActivity extends AppCompatActivity {
    private static final String TAG = BudgetActivity.class.getSimpleName();
    private BudgetResult result;
    private OrderShipping order_shipping;
    private RecyclerView recyclerview;
    private TextView text_total_description;
    private AppCompatButton button_confirm;
    private LinearLayoutManager llm;
    private BudgetAdapter mAdapter;
    private ProgressDialog progressDialog;
    public static final String NEW_BUDGET = "new_budget";
    public static final String NEW_ORDER = "new_order";
    //text fields for resume budget
    private TextView text_distance;
    //private TextView text_time_estimated;
    private TextView text_destinies;
    private TextView text_type_shipping;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.title_budget));

        Intent i = getIntent();
        result = (BudgetResult)i.getSerializableExtra(NEW_BUDGET);
        order_shipping = (OrderShipping) i.getSerializableExtra(NEW_ORDER);
        init();
        if (result!=null && order_shipping!=null) showBudget();
    }

    public BudgetResult getBudget() {
        return result;
    }

    public OrderShipping getOrder_shipping() {
        return order_shipping;
    }

    private void init(){
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        text_total_description = (TextView) findViewById(R.id.text_total_description);
        button_confirm = (AppCompatButton) findViewById(R.id.button_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                createShippingOrder();
            }
        });
        //agregar distancia 0 y costo 0 al recorrido desde la ubicacion del repartidor al origen
        order_shipping.getDestinations().add(0, order_shipping.getOrigin());
        result.getDistances().add(0, "0");
        for(int i = 0;i<order_shipping.getDestinations().size();i++){
            PickupAddress address = order_shipping.getDestinations().get(i);
            address.setDistance(result.getDistances().get(i));
        }
        //text fields for resume budget
        text_distance = (TextView) findViewById(R.id.text_distance);
        //text_time_estimated = (TextView) findViewById(R.id.text_time_estimated);
        text_destinies = (TextView) findViewById(R.id.text_destinies);
        text_type_shipping = (TextView) findViewById(R.id.text_type_shipping);
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
    private String getFormattedDistance(String distance_string){
        float distance = Float.parseFloat(distance_string);
        //DecimalFormat df = new DecimalFormat("###.0");//formato a 1 decimal
        DecimalFormat df = new DecimalFormat("###");//sin decimales
        if ((distance/1000) < 1.0) {//distancia en metros
            return df.format(distance) + " mts";//covertir a metros
        } else {//distancia en km
            //convertir a km, redondeo a 2 decimales
            //Mediante Math.Round() donde la cantidad de ceros es la cantidad de decimales a limitar
            double rounded_distance = Math.round((distance / 1000) * 100.0) / 100.0;
            return rounded_distance + " km";
        }
    }

    private void showBudget(){
        //Redondear total a enteros
        String id_country = ApplicationPreferences.getLocalStringPreference(BudgetActivity.this, Constants.id_country);
        Long total = 0L;
        if (result.getTotal()!=null){
            total = Math.round(result.getTotal());
            result.setTotal(Double.parseDouble(String.valueOf(total)));
        }
        text_total_description.setText(getString(R.string.title_total_budget) + " " + StringOperations.getAmountFormat(total.toString(),id_country));
        //resume budget
        text_distance.setText(getFormattedDistance(result.getTotalDistance()));
        //text_time_estimated.setText("20 min");
        Log.d(TAG,"isRoundTrip:"+order_shipping.isRoundTrip() + "  , getOriginalDestinations:"+order_shipping.getOriginalDestinations() + ", getDestinations:"+order_shipping.getDestinations().size());
        if (order_shipping.isRoundTrip()) {//if is round trim, then destinies =
            text_destinies.setText(String.valueOf(order_shipping.getOriginalDestinations()));
            text_type_shipping.setText(getString(R.string.title_circular_tour));//poner texto ida y vuelta
        }else {
            text_destinies.setText(String.valueOf(order_shipping.getDestinations().size()-1));
            text_type_shipping.setText(getString(R.string.title_destinies));//texto destinos
        }



        mAdapter = new BudgetAdapter( order_shipping.getDestinations() , this);
        recyclerview.setAdapter(mAdapter);
        recyclerview.addItemDecoration(new DividerItemDecoration(BudgetActivity.this));
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerview.setHasFixedSize(true);
        if (recyclerview.getLayoutManager()==null){
            recyclerview.setLayoutManager(llm);
        }
    }
    private void createShippingOrder(){
        ConfigItem config = GeneralFunctions.getConfiguration(getApplicationContext());
        final ShippingOrderRequest request = new ShippingOrderRequest();
        UserItem user = null;

        Gson gson = new Gson();
        String json_user = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.user_object);
        if (json_user!=null){
            //get user saved
            user = gson.fromJson(json_user,UserItem.class);
            if (user!=null && user.getIdUser()!=null) {
                request.setUser(user);
            }
        }
        request.setBusinessName(config!=null?config.getBusinessName():"");
        request.setToken(GeneralFunctions.getTokenUser(getApplicationContext()));
        //enviar solo las direcciones de destino, eliminar el destino 0 que representa el origen
        order_shipping.getDestinations().remove(0);
        request.setOrder_shipping(order_shipping);
        request.setId_order("0");

        request.setBudget(result);
        RestServiceWrapper.createShippingOrder(request, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {

                if (response != null && response.isSuccessful()) {
                    Log.d(TAG, "Respuesta: " + response.body().toString());
                    GenericResponse cart_response = response.body();
                    if (cart_response != null && cart_response.getStatus().equals(Constants.success)) {
                        if (cart_response.getResult().getStatus().equals(String.valueOf(Constants.uno))) {
                            //check response
                            initProcess(false);
                            startCongratsActivity(cart_response,request);

                        } else {
                            Log.d(TAG, "\nresponse:" + cart_response.getResult().toString());
                            initProcess(false);

                            ShowConfirmations.showConfirmationMessage(cart_response.getResult().getMessage(), BudgetActivity.this);
                            Log.d(TAG, "Error response...................:" + cart_response.getResult().getMessage());
                        }


                    } else if (cart_response != null && cart_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);

                        ShowConfirmations.showConfirmationMessage(response_error, BudgetActivity.this);
                        initProcess(false);
                    } else {
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);

                        ShowConfirmations.showConfirmationMessage(response_error, BudgetActivity.this);
                        initProcess(false);
                    }
                } else {
                    initProcess(false);
                    //inicializa nuevamente lista de destinos
                    order_shipping.getDestinations().add(0, order_shipping.getOrigin());
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), BudgetActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                initProcess(false);
                //inicializa nuevamente lista de destinos
                order_shipping.getDestinations().add(0, order_shipping.getOrigin());
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());
                    ShowConfirmations.showConfirmationMessage(apiException.getMessage(), BudgetActivity.this);
                    initProcess(false);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    //return response to NewOrderActivity
    public void startCongratsActivity(GenericResponse response,ShippingOrderRequest request){
       /* Intent i = new Intent(this,CongratsActivity.class);
        Bundle args = new Bundle();
        //asigna numero de orden generada
        request.setId_order(response.getMessage());
        args.putSerializable(CongratsActivity.NEW_ORDER,request);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtras(args);
        finish();
        startActivity(i);*/
        Intent data = new Intent();
        Bundle args = new Bundle();
        request.setId_order(response.getMessage());
        args.putSerializable(CongratsActivity.NEW_ORDER, request);
        data.putExtras(args);
        setResult(Activity.RESULT_OK, data);
        finish();
    }
}
