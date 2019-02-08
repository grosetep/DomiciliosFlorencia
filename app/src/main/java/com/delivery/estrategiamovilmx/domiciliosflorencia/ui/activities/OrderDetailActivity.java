package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderDetail;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.DestinyView;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.OrderDetailResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

public class OrderDetailActivity extends AppCompatActivity {
    private static final String TAG = OrderDetailActivity.class.getSimpleName();
    public static String ID_ORDER = "order";
    private TextView text_order;
    private TextView text_hour;
    private TextView text_date;
    private ImageView image_status;
    private TextView text_status;
    private TextView text_name_view;
    private TextView text_email_view;
    //private TextView text_address;
    //private LinearLayout layout_address;
    private LinearLayout container_destinations;
    private TextView text_products;
    private TextView title_total;
    private TextView text_total;
    private FrameLayout container_loading;
    private LinearLayout layout_principal;
    private RelativeLayout no_connection_layout;
    private OrderItem order;
    private OrderDetail detail;
    private UserItem user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //show detail and drive for delivers only
        Intent i = getIntent();
        order = (OrderItem) i.getSerializableExtra(OrderDetailActivity.ID_ORDER);
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


        } else {
            showNoConnectionLayout();
        }
    }

    private void showInitValues() {
        //assign values
        text_order.setText(order.getIdOrder());
        Drawable clone_review = ContextCompat.getDrawable(this, R.drawable.ic_description).getConstantState().newDrawable().mutate();
        Drawable clone_accepted = ContextCompat.getDrawable(this, R.drawable.ic_assignment_turned_in).getConstantState().newDrawable().mutate();
        Drawable clone_on_way = ContextCompat.getDrawable(this, R.drawable.ic_motorcycle).getConstantState().newDrawable().mutate();
        Drawable clone_deliver = ContextCompat.getDrawable(this, R.drawable.ic_check_circle).getConstantState().newDrawable().mutate();

        switch (order.getStatus()) {
            case Constants.status_review:
                //image_status.setImageResource(R.drawable.ic_description);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_review, android.R.color.holo_green_light,getApplicationContext()));
                text_status.setText(Constants.status_review.substring(0, 1).toUpperCase() + Constants.status_review.substring(1));
                break;
            case Constants.status_rejected://rechazado, cambia textoy color icono
                /*image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_red_light,getApplicationContext()));
                text_status.setText(Constants.status_rejected.substring(0, 1).toUpperCase() + Constants.status_rejected.substring(1));
                break;
            case Constants.status_accepted:
                //image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_green_light,getApplicationContext()));
                text_status.setText(Constants.status_accepted.substring(0, 1).toUpperCase() + Constants.status_accepted.substring(1));
                break;
            case Constants.status_cancel:
                /*image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_accepted, android.R.color.holo_red_light,getApplicationContext()));
                text_status.setText(Constants.status_cancel.substring(0, 1).toUpperCase() + Constants.status_cancel.substring(1));
                break;
            case Constants.status_on_way:
                //image_status.setImageResource(R.drawable.ic_motorcycle);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_on_way, android.R.color.holo_green_light,getApplicationContext()));
                text_status.setText(Constants.status_on_way.substring(0, 1).toUpperCase() + Constants.status_on_way.substring(1));
                break;

            case Constants.status_deliver:
                //image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_deliver, android.R.color.holo_green_light,getApplicationContext()));
                text_status.setText(Constants.status_deliver.substring(0, 1).toUpperCase() + Constants.status_deliver.substring(1));
                break;
            case Constants.status_no_deliver:
                /*image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));*/
                image_status.setImageDrawable(GeneralFunctions.getTintedDrawable(clone_deliver, android.R.color.holo_red_light,getApplicationContext()));
                text_status.setText(Constants.status_no_deliver.substring(0, 1).toUpperCase() + Constants.status_no_deliver.substring(1));
                break;
        }
    }
    private void showInitValuesPrevious() {
        //assign values
        text_order.setText(order.getIdOrder());
        switch (order.getStatus()) {
            case Constants.status_review:
                image_status.setImageResource(R.drawable.ic_description);
                text_status.setText(Constants.status_review.substring(0, 1).toUpperCase() + Constants.status_review.substring(1));
                break;
            case Constants.status_rejected://rechazado, cambia textoy color icono
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_rejected.substring(0, 1).toUpperCase() + Constants.status_rejected.substring(1));
                break;
            case Constants.status_accepted:
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                text_status.setText(Constants.status_accepted.substring(0, 1).toUpperCase() + Constants.status_accepted.substring(1));
                break;
            case Constants.status_cancel:
                image_status.setImageResource(R.drawable.ic_assignment_turned_in);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_cancel.substring(0, 1).toUpperCase() + Constants.status_cancel.substring(1));
                break;
            case Constants.status_on_way:
                image_status.setImageResource(R.drawable.ic_motorcycle);
                text_status.setText(Constants.status_on_way.substring(0, 1).toUpperCase() + Constants.status_on_way.substring(1));
                break;

            case Constants.status_deliver:
                image_status.setImageResource(R.drawable.ic_check_circle);
                text_status.setText(Constants.status_deliver.substring(0, 1).toUpperCase() + Constants.status_deliver.substring(1));
                break;
            case Constants.status_no_deliver:
                image_status.setImageResource(R.drawable.ic_check_circle);
                image_status.setColorFilter(ContextCompat.getColor(this, android.R.color.holo_red_light));
                text_status.setText(Constants.status_no_deliver.substring(0, 1).toUpperCase() + Constants.status_no_deliver.substring(1));
                break;
        }
    }

    private void assignActions() {

        /*if (user != null && user.getProfile().equals(Constants.profile_deliver_man)) {
            layout_address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDetail() != null) {
                        String geolocation = "geo:".concat(getDetail().getLatitude()).concat(",").concat(getDetail().getLongitude());
                        Uri gmmIntentUri = Uri.parse(geolocation + "?q=" + Uri.encode(getDetail().getGooglePlace()));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        //mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapIntent);
                        } else {
                            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), OrderDetailActivity.this);
                        }
                    }
                }
            });
        }*/

        if (order != null) {
            text_order.setText(order.getIdOrder());

        }
    }

    private void getDetailOrder() {
        RestServiceWrapper.getDetailOrder(order.getIdOrder(), new Callback<OrderDetailResponse>() {
            @Override
            public void onResponse(Call<OrderDetailResponse> call, retrofit2.Response<OrderDetailResponse> response) {
                //Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    if (response.body().getStatus().equals(String.valueOf(Constants.uno))) {
                        detail = response.body().getResult();
                        setDetailedValues(order, user, detail);
                        initProcess(false);
                    } else {
                        onError(response.body().getMessage());
                    }
                } else {
                    onError(response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<OrderDetailResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(t.getMessage());
            }
        });
    }
    private HashMap<String,DestinyView> getOrderedDestinations(String destinations){
        HashMap<String,DestinyView> list_ordered = new HashMap<>();
        String[] tokens = destinations.split("\\|");
        for (String des:tokens) {//Log.d(TAG,"procesando .......................destinos:"+des);
            String[] elements = des.split("@@@");Log.d(TAG,"elements ......................."+elements.length);
            //Log.d(TAG,"agregando en index ......................."+elements[0]+" "+elements[1]+" "+elements[2]);
            list_ordered.put(elements[0],new DestinyView(elements[0],elements[1],elements[2],elements[3],elements[4],elements[5],elements[6],elements[7],elements[8],elements[9],elements[10]));
        }
        return list_ordered;
    }
    private void setDetailedValues(OrderItem order, UserItem user, OrderDetail detail) {
        String id_country = ApplicationPreferences.getLocalStringPreference(OrderDetailActivity.this, Constants.id_country);
        StringBuffer buffer = new StringBuffer();
        if (order != null) {
            //Log.d(TAG, "Generando lista de direcciones con detalles:" + detail.getDestinations());
            //gerenar hastmap con los destinos
            HashMap<String,DestinyView> destinatios_list = getOrderedDestinations(detail.getDestinations());
            //getDestinationsView(detail.getDestinations(), container_destinations);
            getDestinationsView(destinatios_list, container_destinations);
            text_name_view.setText(detail.getNameClient());
            text_email_view.setText(detail.getEmail());
            if (user.getProfile().equals(Constants.profile_deliver_man)) {
                title_total.setText(getString(R.string.title_total_commission));
                text_total.setText(StringOperations.getAmountFormat(order.getDeliverman_commision(),id_country));
            } else {
                title_total.setText(getString(R.string.title_conf_total));
                text_total.setText(StringOperations.getAmountFormat(order.getTotal(),id_country));
            }
            //llenar campos del detalle
            text_hour.setText(detail.getHourCreation());
            text_date.setText(detail.getDateCreation());
        } else {
            onError(getString(R.string.generic_error));
        }

    }

    private void onError(String msgErr) {
        ShowConfirmations.showConfirmationMessage(msgErr, this);
        initProcess(false);
        onBackPressed();
    }

    private void initProcess(boolean flag) {
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        layout_principal.setVisibility(flag ? View.GONE : View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }

    private void showNoConnectionLayout() {
        container_loading.setVisibility(View.GONE);
        layout_principal.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    private void init() {
        layout_principal = (LinearLayout) findViewById(R.id.layout_principal);
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        text_order = (TextView) findViewById(R.id.text_order);
        text_hour = (TextView) findViewById(R.id.text_hour);
        text_date = (TextView) findViewById(R.id.text_date);
        image_status = (ImageView) findViewById(R.id.image_status);
        text_status = (TextView) findViewById(R.id.text_status);
        text_name_view = (TextView) findViewById(R.id.text_name_view);
        text_email_view = (TextView) findViewById(R.id.text_email_view);

        text_products = (TextView) findViewById(R.id.text_products);
        title_total = (TextView) findViewById(R.id.title_total);
        text_total = (TextView) findViewById(R.id.text_total);
        container_destinations = (LinearLayout) findViewById(R.id.container_destinations);

    }

    /*public void getDestinationsViewWithStrings(String destinations, LinearLayout container_destinations) {
        LinearLayout customView = new LinearLayout(OrderDetailActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        customView.setLayoutParams(lp);
        customView.setOrientation(LinearLayout.VERTICAL);
        TextView text_title;//numero de destino
        TextView text_description;//direccion o punto
        TextView text_extra_info;//distancia
        TextView text_times;
        TextView text_instructions;
        TextView text_contact_data;
        ImageButton button_drive;

        final LinearLayout fl = new LinearLayout(OrderDetailActivity.this);
        fl.setLayoutParams(lp);
        fl.setOrientation(LinearLayout.VERTICAL);
        //iterar por los destinos

        if (destinations != null) {
            String[] destinstions_array = destinations.split("\\|");
            for (int k = 0; k < destinstions_array.length; k++) {
                // Create Layout Parameters for FrameLayout
                LayoutInflater mInflater = LayoutInflater.from(this);
                final View currentCustomView = mInflater.inflate(R.layout.layout_destiny_item, null);

                text_title = (TextView) currentCustomView.findViewById(R.id.text_title);
                text_description = (TextView) currentCustomView.findViewById(R.id.text_description);
                text_extra_info = (TextView) currentCustomView.findViewById(R.id.text_extra_info);
                text_times = (TextView) currentCustomView.findViewById(R.id.text_times);
                text_contact_data = (TextView) currentCustomView.findViewById(R.id.text_contact_data);
                text_instructions = (TextView) currentCustomView.findViewById(R.id.text_instructions);
                button_drive = (ImageButton) currentCustomView.findViewById(R.id.button_drive);
                //formato de respuesta en destinos:
                //pickup_point_number,'@@@',hora_desde,'@@@',hora_hasta,'@@@',google_place,'@@@',distancia,'@@@',contacto,'@@@',telefono_contacto,'@@@',comentario,'@@@',tipo_direccion,'@@@',latitud,'@@@',longitud
                final String[] elements_destiny = destinstions_array[k].split("@@@");
                text_title.setText((elements_destiny[0] != null && (k == 0)) ? getString(R.string.promt_origin) : getString(R.string.title_destination, k));
                text_times.setText(elements_destiny[1] != null ? getString(R.string.promt_times, elements_destiny[1], elements_destiny[2]) : "");
                text_description.setText(elements_destiny[3]);
                String comma = k == 0 ? "" : ",";
                text_extra_info.setText(GeneralFunctions.getDistanceFormat(elements_destiny[4], String.valueOf(k)).concat(comma));
                text_instructions.setText(elements_destiny[7] != null ? elements_destiny[7] : "Sin instrucciones");
                text_contact_data.setText(elements_destiny[5] != null ? getString(R.string.promt_contact_data, elements_destiny[5]) : "");//agrega evento llamar
                if (user.getProfile().equals(Constants.profile_deliver_man)) {
                    button_drive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drive(elements_destiny[9],elements_destiny[10],elements_destiny[3]);
                        }
                    });
                    text_contact_data.setVisibility(View.VISIBLE);
                    text_contact_data.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeCall(elements_destiny[6]);
                        }
                    });
                } else {
                    text_contact_data.setOnClickListener(null);
                    text_contact_data.setVisibility(View.GONE);
                    button_drive.setOnClickListener(null);
                }

                //promt_times
                fl.addView(currentCustomView);
            }
            container_destinations.addView(fl);
        }
    }*/
    public void getDestinationsView(HashMap<String,DestinyView> destinatios_list, LinearLayout container_destinations) {
        LinearLayout customView = new LinearLayout(OrderDetailActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        customView.setLayoutParams(lp);
        customView.setOrientation(LinearLayout.VERTICAL);
        TextView text_title;//numero de destino
        TextView text_description;//direccion o punto
        TextView text_extra_info;//distancia
        TextView text_times;
        TextView text_instructions;
        TextView text_contact_data;
        ImageButton button_drive;

        final LinearLayout fl = new LinearLayout(OrderDetailActivity.this);
        fl.setLayoutParams(lp);
        fl.setOrientation(LinearLayout.VERTICAL);
        //iterar por los destinos

        if (destinatios_list != null && destinatios_list.size()>0) {

            Iterator it = destinatios_list.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry destiny_entry = (Map.Entry) it.next();
                final DestinyView destiny = (DestinyView) destiny_entry.getValue();
                // Create Layout Parameters for FrameLayout
                LayoutInflater mInflater = LayoutInflater.from(this);
                final View currentCustomView = mInflater.inflate(R.layout.layout_destiny_item, null);

                text_title = (TextView) currentCustomView.findViewById(R.id.text_title);
                text_description = (TextView) currentCustomView.findViewById(R.id.text_description);
                text_extra_info = (TextView) currentCustomView.findViewById(R.id.text_extra_info);
                text_times = (TextView) currentCustomView.findViewById(R.id.text_times);
                text_contact_data = (TextView) currentCustomView.findViewById(R.id.text_contact_data);
                text_instructions = (TextView) currentCustomView.findViewById(R.id.text_instructions);
                button_drive = (ImageButton) currentCustomView.findViewById(R.id.button_drive);
                //formato de respuesta en destinos:
                //pickup_point_number,'@@@',hora_desde,'@@@',hora_hasta,'@@@',google_place,'@@@',distancia,'@@@',contacto,'@@@',telefono_contacto,'@@@',comentario,'@@@',tipo_direccion,'@@@',latitud,'@@@',longitud
                text_title.setText(destiny.getPickupPointNumber().equals(String.valueOf(Constants.cero)) ? getString(R.string.promt_origin) : getString(R.string.title_destination, destiny.getPickupPointNumber()));
                text_times.setText(destiny.getTime_from() != null ? getString(R.string.promt_times, destiny.getTime_from(), destiny.getTime_to()) : "");
                text_description.setText(destiny.getGooglePlace());
                String comma = destiny.getPickupPointNumber().equals(String.valueOf(Constants.cero)) ? "" : ",";
                text_extra_info.setText(GeneralFunctions.getDistanceFormat(destiny.getDistance(), String.valueOf(destiny.getPickupPointNumber())).concat(comma));
                text_instructions.setText(destiny.getComment() != null ? destiny.getComment() : "Sin instrucciones");
                text_contact_data.setText(destiny.getConctac() != null ? getString(R.string.promt_contact_data, destiny.getConctac()) : "");//agrega evento llamar
                if (user.getProfile().equals(Constants.profile_deliver_man)) {
                    button_drive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            drive(destiny.getLatitude(),destiny.getLongitude(),destiny.getGooglePlace());
                        }
                    });
                    text_contact_data.setVisibility(View.VISIBLE);
                    text_contact_data.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            makeCall(destiny.getContactPhone());
                        }
                    });
                } else {
                    text_contact_data.setOnClickListener(null);
                    text_contact_data.setVisibility(View.GONE);
                    button_drive.setOnClickListener(null);
                }

                //promt_times
                fl.addView(currentCustomView);
            }
            container_destinations.addView(fl);
        }
    }

    private void drive(String latitude, String longitude, String location) {
        String geolocation = "geo:".concat(latitude.concat(",").concat(longitude));
        Uri gmmIntentUri = Uri.parse(geolocation + "?q=" + Uri.encode(location));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        //mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            ShowConfirmations.showConfirmationMessage(getString(R.string.generic_error), OrderDetailActivity.this);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        Intent i = new Intent( getApplicationContext() , OrdersDeliverActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra(Constants.flow,CongratsActivity.flow_congrats);
        startActivity(i);
        finish();
        //NavUtils.navigateUpTo(OrderDetailActivity.this, new Intent(getApplicationContext(), OrdersActivity.class));
    }
    public OrderDetail getDetail() {
        return detail;
    }
}
