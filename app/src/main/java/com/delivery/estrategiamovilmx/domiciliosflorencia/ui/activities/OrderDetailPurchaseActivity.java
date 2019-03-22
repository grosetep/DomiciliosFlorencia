package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderDetailPurchase;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderPurchaseItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Additional;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Variant;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.OrderDetailPurchaseResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;

public class OrderDetailPurchaseActivity extends AppCompatActivity {
    private static final String TAG = OrderDetailPurchaseActivity.class.getSimpleName();
    public static String ID_ORDER = "order";
    private TextView text_order;
    private TextView text_hour;
    private TextView text_date;
    private ImageView image_status;
    private TextView text_status;
    private TextView text_name_view;
    private TextView text_email_view;
    private TextView text_name_contact;
    private TextView text_phone_contact;
    private TextView text_payment;
    private LinearLayout layout_phone;
    private TextView text_address;
    private LinearLayout layout_address;
    private TextView text_products;
    private TextView text_total;
    private FrameLayout container_loading;
    private LinearLayout layout_principal;
    private RelativeLayout no_connection_layout;
    private OrderPurchaseItem order;
    private OrderDetailPurchase detail;
    private UserItem user;
    private int id_country = 0;
    //maps para adicionales y variantes
    private HashMap<String,ArrayList<Additional>> additionals_map = new HashMap<>();
    private HashMap<String,Variant> variants_map = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail_purchase);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //show detail and drive for delivers only
        Intent i = getIntent();
        order = (OrderPurchaseItem) i.getSerializableExtra(OrderDetailActivity.ID_ORDER);
        String id_country_string = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.id_country);
        if (id_country_string!=null && !id_country_string.isEmpty()){id_country = Integer.parseInt(id_country_string);}
        user = GeneralFunctions.getCurrentUser(getApplicationContext());
        init();
        assignActions();
        if (Connectivity.isNetworkAvailable(getApplicationContext())) {

            initProcess(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                showInitValues();
            }else {
                showInitValuesPrevious();
            }
            getDetailOrder();

        }else{
            showNoConnectionLayout();
        }
    }
    private void showInitValuesPrevious(){
        //assign values
        text_order.setText(order.getIdOrder());
        switch (order.getStatus()){
            case Constants.status_review:
                image_status.setImageResource(R.drawable.ic_description);
                text_status.setText(Constants.status_review.substring(0,1).toUpperCase() + Constants.status_review.substring(1));
                break;
            case Constants.status_rejected://rechazado, cambia textoy color icono
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_rejected.substring(0,1).toUpperCase() + Constants.status_rejected.substring(1));
                break;
            case Constants.status_accepted:
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                text_status.setText(Constants.status_accepted.substring(0,1).toUpperCase() + Constants.status_accepted.substring(1));
                break;
            case Constants.status_cancel:
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_cancel.substring(0,1).toUpperCase() + Constants.status_cancel.substring(1));
                break;
            case Constants.status_on_way:
                image_status.setImageResource(R.drawable.ic_motorcycle);
                text_status.setText(Constants.status_on_way.substring(0,1).toUpperCase() + Constants.status_on_way.substring(1));
                break;

            case Constants.status_deliver:
                image_status.setImageResource(R.drawable.ic_check_circle);
                text_status.setText(Constants.status_deliver.substring(0,1).toUpperCase() + Constants.status_deliver.substring(1));
                break;
            case Constants.status_no_deliver:
                image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_no_deliver.substring(0,1).toUpperCase() + Constants.status_no_deliver.substring(1));
                break;
        }
    }
    private void showInitValues() {
        //assign values
        text_order.setText(order.getIdOrder());
        Drawable clone_review = ContextCompat.getDrawable(this,R.drawable.ic_description).getConstantState().newDrawable().mutate();
        Drawable clone_accepted = ContextCompat.getDrawable(this,R.drawable.ic_assignment_turned_in).getConstantState().newDrawable().mutate();
        Drawable clone_on_way = ContextCompat.getDrawable(this,R.drawable.ic_motorcycle).getConstantState().newDrawable().mutate();
        Drawable clone_deliver = ContextCompat.getDrawable(this,R.drawable.ic_check_circle).getConstantState().newDrawable().mutate();

        switch (order.getStatus()) {
            case Constants.status_review:
                //image_status.setImageResource(R.drawable.ic_description);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_review, android.R.color.holo_green_light, getApplicationContext()));
                text_status.setText(Constants.status_review.substring(0, 1).toUpperCase() + Constants.status_review.substring(1));
                break;
            case Constants.status_rejected://rechazado, cambia textoy color icono
                /*image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_red_light, getApplicationContext()));
                text_status.setText(Constants.status_rejected.substring(0, 1).toUpperCase() + Constants.status_rejected.substring(1));
                break;
            case Constants.status_accepted:
                //image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_green_light, getApplicationContext()));
                text_status.setText(Constants.status_accepted.substring(0, 1).toUpperCase() + Constants.status_accepted.substring(1));
                break;
            case Constants.status_cancel:
                /*image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_red_light, getApplicationContext()));
                text_status.setText(Constants.status_cancel.substring(0, 1).toUpperCase() + Constants.status_cancel.substring(1));
                break;
            case Constants.status_on_way:
                //image_status.setImageResource(R.drawable.ic_motorcycle);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_on_way, android.R.color.holo_green_light, getApplicationContext()));
                text_status.setText(Constants.status_on_way.substring(0, 1).toUpperCase() + Constants.status_on_way.substring(1));
                break;

            case Constants.status_deliver:
                //image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_deliver, android.R.color.holo_green_light, getApplicationContext()));
                text_status.setText(Constants.status_deliver.substring(0, 1).toUpperCase() + Constants.status_deliver.substring(1));
                break;
            case Constants.status_no_deliver:
                /*image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_deliver, android.R.color.holo_red_light, getApplicationContext()));
                text_status.setText(Constants.status_no_deliver.substring(0, 1).toUpperCase() + Constants.status_no_deliver.substring(1));
                break;
        }
    }
    private void assignActions(){

        if (user!=null && (user.getProfile().equals(Constants.profile_deliver_man) ||user.getProfile().equals(Constants.profile_admin) ) ){
            layout_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDetail()!=null){
                        String geolocation = "geo:".concat(getDetail().getLatitude()).concat(",").concat(getDetail().getLongitude());
                        Uri gmmIntentUri = Uri.parse(geolocation + "?q=" + Uri.encode(getDetail().getGooglePlace()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        //mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        }else{
                            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), OrderDetailPurchaseActivity.this);}
                    }
                }
            });
        }
        if (user != null && !user.getProfile().equals(Constants.profile_client)){
            layout_phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeCall(text_phone_contact.getText().toString());
                }
            });

        }
        if (order!=null){
            text_order.setText(order.getIdOrder());

        }
    }
    private void makeCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

            if (!UtilPermissions.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
            }
        }
        startActivity(intent);
    }
    private void getDetailOrder(){
        RestServiceWrapper.getDetailOrderPurchase(order.getIdOrder(), new Callback<OrderDetailPurchaseResponse>() {
            @Override
            public void onResponse(Call<OrderDetailPurchaseResponse> call, retrofit2.Response<OrderDetailPurchaseResponse> response) {
                //Log.d(TAG, "Respuesta getDetailOrderPurchase: " + response.body().getResult().toString());
                if (response != null && response.isSuccessful()) {
                    if (response.body().getStatus().equals(String.valueOf(Constants.uno))) {
                        detail = response.body().getResult();
                        Handler handler = new Handler();
                        final Runnable r = new Runnable() {
                            public void run() {
                                setDetailedValues(detail);
                                initProcess(false);
                            }
                        };
                        handler.post(r);
                    } else {
                        onError(response.body().getMessage());
                    }
                } else {
                    onError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<OrderDetailPurchaseResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(t.getMessage());
            }
        });
    }

    public HashMap<String, Variant> getVariants_map() {
        return variants_map;
    }

    public HashMap<String, ArrayList<Additional>> getAdditionals_map() {
        return additionals_map;
    }

    private void setDetailedValues(OrderDetailPurchase detail){
        StringBuffer buffer = new StringBuffer();

        if (detail!=null){
            //init maps
            if (detail.getAdditionals()!=null){
                String[] list_additionals = detail.getAdditionals().split(Pattern.quote("||"));
                Log.d(TAG,"adicionales antes de procesamiento");
                for (String s:list_additionals){
                    Log.d(TAG,"row adicional:"+s);
                }


                for (int i = 0;i<list_additionals.length-1;i++) {

                    //busca si existe key con id_producto, si existe:obtener la lista que contiene y agregar el elemento, sino entonces crear un nuevo elemento con clave id_producto
                    //y valor lista con el elemento
                    String[] data = list_additionals[i].split("@@@");

                    if (additionals_map.containsKey(data[0])){//obtener lista y agregar elemento encontrado
                        Additional additional_item = new Additional();
                        additional_item.setAdditional(data[2]);//adicional
                        additional_item.setPrice(data[3]);//precio
                        additionals_map.get(data[0]).add(additional_item);
                    }else{//crear nuevo elemento
                        ArrayList<Additional> list_item = new ArrayList<>();
                        Additional additional_item = new Additional();
                        additional_item.setAdditional(data[2]);//adicional
                        additional_item.setPrice(data[3]);//price
                        list_item.add(additional_item);
                        additionals_map.put(data[0],list_item);
                    }
                }
            }

            if (detail.getVariants()!=null){
                String[] list_variants = detail.getVariants().split(Pattern.quote("||"));
                for (int j = 0;j<list_variants.length;j++){
                    String[] data2 = list_variants[j].split("@@@");
                    Variant var = new Variant();
                    var.setVariante(data2[1]);//variente
                    variants_map.put(data2[0], var);
                }

            }

            text_name_view.setText(detail.getNameClient());
            text_email_view.setText(detail.getEmailClient());
            text_name_contact.setText(detail.getContactName());
            text_phone_contact.setText(detail.getContactPhone());
            text_payment.setText(detail.getPayment());
            text_address.setText(StringOperations.fromHtml(detail.getGooglePlace().concat(".&nbsp;").
                    concat(detail.getNumInt() != null && !detail.getNumInt().isEmpty() ? "&nbsp;<strong>Detalle:</strong>" + detail.getNumInt() : "").
                    concat(detail.getReference() != null && !detail.getReference().isEmpty() ? ",&nbsp;<strong>Referencia:</strong>" + detail.getReference():"")));

            text_total.setText(StringOperations.getAmountFormat(detail.getTotal(),String.valueOf(id_country)));
            text_hour.setText(detail.getHourCreation());
            text_date.setText(detail.getDateCreation());

            if (detail.getProducts().length()>0){
                String[] list_products = detail.getProducts().split(Pattern.quote("||"));
                for (int i = 0;i<list_products.length;i++) {
                    String[] detail_product = list_products[i].split("@@@");
                    //prepara texto productos

                    String id_product = detail_product[0];
                    String units = detail_product[1];
                    String product = detail_product[2];
                    String total = detail_product[3];
                    buffer.append("<p>");
                    buffer.append("<strong>"+units+"</strong>").append(" - ").append(product);
                    //Agregar variantes y adicionales
                    if (getVariants_map().size()>0 && getVariants_map().get(id_product)!=null){
                        buffer.append(" - ").append("<strong>".concat(getVariants_map().get(id_product).getVariante()).concat("</strong>"));
                    }
                    int add_counter = 0;
                    if (getAdditionals_map().size()>0 && getAdditionals_map().containsKey(id_product)){

                        for(Additional ad:getAdditionals_map().get(id_product)){

                            if (add_counter==0){ buffer.append("<p><strong>").append("::Extras::").append("</strong></p>");}
                            buffer.append("<p>"+ad.getAdditional()).append(" +").append(getString(R.string.currency_sign)).
                                    append(ad.getPrice()).append("</p>");
                            add_counter++;
                        }

                    }
                    buffer.append("<p><strong>Subtotal:</strong></p>").append(" ".concat(getString(R.string.currency_sign))).append(total);
                    buffer.append("</p>");

                }

                text_products.setText(StringOperations.fromHtml(buffer.toString()));
            }
            //agrega datos de contacto a la entrega


        }else{
            onError(getString(R.string.generic_error));
        }

    }
    private void onError(String msgErr){
        ShowConfirmations.showConfirmationMessage(msgErr,this);
        initProcess(false);
        onBackPressed();
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        layout_principal.setVisibility(flag ? View.GONE : View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_principal.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    private void init(){
        layout_principal = (LinearLayout) findViewById(R.id.layout_principal);
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_phone = (LinearLayout) findViewById(R.id.layout_phone);
        text_payment = (TextView) findViewById(R.id.text_payment);
        text_order = (TextView) findViewById(R.id.text_order);
        text_hour = (TextView) findViewById(R.id.text_hour);
        text_date = (TextView) findViewById(R.id.text_date);
        image_status = (ImageView) findViewById(R.id.image_status);
        text_status = (TextView) findViewById(R.id.text_status);
        text_name_view = (TextView) findViewById(R.id.text_name_view);
        text_email_view = (TextView) findViewById(R.id.text_email_view);
        text_name_contact = (TextView) findViewById(R.id.text_name_contact);
        text_phone_contact = (TextView) findViewById(R.id.text_phone_contact);

        text_address = (TextView) findViewById(R.id.text_address);
        layout_address = (LinearLayout) findViewById(R.id.layout_address);
        text_products = (TextView) findViewById(R.id.text_products);
        text_total = (TextView) findViewById(R.id.text_total);
    }
    @Override
    public void onBackPressed() {
        Intent i = null;
        /*UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        if (user!=null && user.getProfile().equals(Constants.profile_deliver_man) ){*/
        i = new Intent( getApplicationContext() , OrdersDeliverPurchaseActivity.class);
        /*}else {
            i = new Intent(getApplicationContext(), OrdersActivity.class);
        }*/
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Constants.flow,CongratsActivity.flow_congrats);
        startActivity(i);
        finish();
        //NavUtils.navigateUpTo(OrderDetailPurchaseActivity.this, new Intent(getApplicationContext(), OrdersActivity.class));
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

    public OrderDetailPurchase getDetail() {
        return detail;
    }
}
