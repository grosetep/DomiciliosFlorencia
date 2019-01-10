package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.DestinyBriefView;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PendingOrdersFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by administrator on 24/08/2017.
 */
public class PendingOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private PendingOrdersFragment fragment;
    private ArrayList<OrderItem> list;
    private UserItem current_user;
    private HashMap<String,DestinyBriefView> destinationsList;
    //
    //
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private static final String TAG = PendingOrdersAdapter.class.getSimpleName();

    public PendingOrdersAdapter(Activity act, ArrayList<OrderItem> myDataset, UserItem user,PendingOrdersFragment fragment,RecyclerView recycler) {
        list = myDataset;
        activity=act;
        current_user = user;
        this.fragment = fragment;
        recyclerview = recycler;
        destinationsList = new HashMap<>();
    //
    final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();
    listener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            totalItemCount = linearLayoutManager.getItemCount();
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


            if (dy>0) {
                if (!isLoading && lastVisibleItem == totalItemCount - 1) {
                    if (mOnLoadMoreListener != null) {
                        isLoading = true;
                        mOnLoadMoreListener.onLoadMore();
                    }

                }
            }

        }
    };
    recyclerview.addOnScrollListener(listener);
    }
@Override
public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
        int viewType) {
    if (viewType == VIEW_TYPE_ITEM) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_order_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }else{
        View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
        return new LoadingViewHolder(view);
    }
}
public class ViewHolder extends RecyclerView.ViewHolder {
    private EditText text_problem;
    private TextView text_deliver;
    private ImageView image_deliver;//entregado
    private ImageView image_on_way;//en camino
    private TextView text_on_way;
    private TextView text_accepted;
    private ImageView image_accepted;//aceptado
    private ImageView image_review;//en revision
    private TextView text_total;
    private ImageView image_signature;
    private TextView text_num_order;
    private TextView text_day;
    private TextView text_num_day;
    //new delivery fields
    private TextView text_total_destinations;
    private TextView text_origin_hours_html;
    private LinearLayout container_destinations;//deshabilitado para generacion dinamica de contenido
    private TextView text_content;
    private ImageView image_vehicle;

    private LinearLayout layout_1;
    private LinearLayout layout_2;
    private LinearLayout layout_dots;
    private LinearLayout layout_n;
    private TextView text_title_1;
    private TextView text_title_2;
    private TextView text_title_n;
    private TextView text_extra_info_1;
    private TextView text_extra_info_2;
    private TextView text_extra_info_n;
    private TextView text_description_1;
    private TextView text_description_2;
    private TextView text_description_n;


    //layout actions
    private LinearLayout layout_admin_buttons;
    private LinearLayout layout_deliver_buttons;
    private LinearLayout layout_client_buttons;
    //buttons
    private LinearLayout button_take;
    private LinearLayout button_delivered;
    private LinearLayout button_no_delivered;

    private LinearLayout button_cancel;

    private LinearLayout button_reject;
    private LinearLayout button_accept;
    //loadings
    private ProgressBar pbLoading;
    private ImageView overflow;


    public ViewHolder(View v) {
        super(v);
        text_problem = (EditText) v.findViewById(R.id.text_problem);
        text_deliver = (TextView) v.findViewById(R.id.text_deliver);
        image_deliver = (ImageView) v.findViewById(R.id.image_deliver);
        image_on_way = (ImageView) v.findViewById(R.id.image_on_way);
        text_on_way = (TextView) v.findViewById(R.id.text_on_way);
        text_accepted = (TextView) v.findViewById(R.id.text_accepted);
        image_accepted = (ImageView) v.findViewById(R.id.image_accepted);
        image_review = (ImageView) v.findViewById(R.id.image_review);
        text_total = (TextView) v.findViewById(R.id.text_total);
        image_signature = (ImageView) v.findViewById(R.id.image_signature);
        text_num_order = (TextView) v.findViewById(R.id.text_num_order);
        text_day = (TextView) v.findViewById(R.id.text_day);
        text_num_day = (TextView) v.findViewById(R.id.text_num_day);
        layout_admin_buttons = (LinearLayout) v.findViewById(R.id.layout_admin_buttons);
        layout_deliver_buttons = (LinearLayout) v.findViewById(R.id.layout_deliver_buttons);
        layout_client_buttons = (LinearLayout) v.findViewById(R.id.layout_client_buttons);
        //delivery fields
        text_total_destinations = (TextView) v.findViewById(R.id.text_total_destinations);
        text_origin_hours_html = (TextView) v.findViewById(R.id.text_origin_hours_html);
        container_destinations = (LinearLayout) v.findViewById(R.id.container_destinations);
        text_content = (TextView) v.findViewById(R.id.text_content);
        image_vehicle = (ImageView) v.findViewById(R.id.image_vehicle);

        layout_1 = (LinearLayout) v.findViewById(R.id.layout_1);
        layout_2 = (LinearLayout) v.findViewById(R.id.layout_2);
        layout_dots = (LinearLayout) v.findViewById(R.id.layout_dots);
        layout_n = (LinearLayout) v.findViewById(R.id.layout_n);
        text_title_1 = (TextView) v.findViewById(R.id.text_title_1);
        text_title_2 = (TextView) v.findViewById(R.id.text_title_2);
        text_title_n = (TextView) v.findViewById(R.id.text_title_n);
        text_extra_info_1 = (TextView) v.findViewById(R.id.text_extra_info_1);
        text_extra_info_2 = (TextView) v.findViewById(R.id.text_extra_info_2);
        text_extra_info_n = (TextView) v.findViewById(R.id.text_extra_info_n);
        text_description_1 = (TextView) v.findViewById(R.id.text_description_1);
        text_description_2 = (TextView) v.findViewById(R.id.text_description_2);
        text_description_n = (TextView) v.findViewById(R.id.text_description_n);

        //buttons deliver_man
        button_take  =(LinearLayout) v.findViewById(R.id.button_take);
        button_delivered =(LinearLayout) v.findViewById(R.id.button_delivered);
        button_no_delivered  =(LinearLayout) v.findViewById(R.id.button_no_delivered);
        //buttons client
        button_cancel = (LinearLayout) v.findViewById(R.id.button_cancel);
        //buttons admin
        button_reject = (LinearLayout) v.findViewById(R.id.button_reject);
        button_accept = (LinearLayout) v.findViewById(R.id.button_accept);
        pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
        overflow = (ImageView) v.findViewById(R.id.overflow);
    }
    public void reset(){
        //reset layout buttons
        layout_admin_buttons.setVisibility(View.GONE);
        layout_client_buttons.setVisibility(View.GONE);
        layout_deliver_buttons.setVisibility(View.GONE);
        //reset text_problem
        text_problem.setText("");
        text_problem.setVisibility(View.GONE);
        //text delivery
        text_total_destinations.setText("");
        text_origin_hours_html.setText("");
        text_content.setText("");
        layout_1.setVisibility(View.GONE);
        layout_2.setVisibility(View.GONE);
        layout_dots.setVisibility(View.GONE);
        layout_n.setVisibility(View.GONE);
        //reset colors
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

        }else{
            image_review.setImageResource(R.drawable.ic_description);
            image_accepted.setImageResource(R.drawable.ic_assignment_turned_in);
            image_on_way.setImageResource(R.drawable.ic_motorcycle);
            image_deliver.setImageResource(R.drawable.ic_check_circle);

            image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
        }

        //reset text
        text_accepted.setText(activity.getString(R.string.promt_accepted));
        text_deliver.setText(activity.getString(R.string.promt_delivered));
        //reset loading
        pbLoading.setVisibility(View.INVISIBLE);
        //reset backgrounds
        switch (current_user.getProfile()){
            case Constants.profile_client:
                button_cancel.setEnabled(true);
                button_cancel.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_red));
                break;
            case Constants.profile_admin:
                button_accept.setEnabled(true);
                button_accept.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_green));
                button_reject.setEnabled(true);
                button_reject.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_red));
                break;
            case Constants.profile_deliver_man:
                button_take.setEnabled(true);
                button_take.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_green));
                button_delivered.setEnabled(true);
                button_delivered.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_green));
                button_no_delivered.setEnabled(true);
                button_no_delivered.setBackground(ContextCompat.getDrawable(activity, R.drawable.button_background_orange));
                break;
        }
        //reset image vehicle
        image_vehicle.setImageDrawable(null);
        //reset list destiny
        destinationsList = new HashMap<>();
        //reset image signature
        image_signature.setImageBitmap(null);
        image_signature.setVisibility(View.GONE);
    }


    private DestinyBriefView getDestiny(int pickup_number,String destinations){
        //Log.d(TAG,"DestinyBriefView::::pickup_number:"+pickup_number+" destinations:"+destinations);
        String[] destinstions_array = destinations.split("\\|");

        if (destinstions_array.length>0) {
            String[] elements_destiny = destinstions_array[pickup_number-1].split("@@@");
            return new DestinyBriefView(elements_destiny[0],elements_destiny[1],elements_destiny[2]);

        }else{return new DestinyBriefView();}

    }

    private HashMap<String,DestinyBriefView> getOrderedDestiny(String list_destiny){
        HashMap<String,DestinyBriefView> list_ordered = new HashMap<>();

        //Log.d(TAG,"getOrderedDestiny......................."+list_destiny);
        String[] tokens = list_destiny.split("\\|");
        //Log.d(TAG,"tokens.......................destinos: "+tokens.length);
        for (String des:tokens) {//Log.d(TAG,"procesando .......................destinos:"+des);
            String[] elements = des.split("@@@");//Log.d(TAG,"elements ......................."+elements.length);
            //Log.d(TAG,"agregando en index ......................."+elements[0]+" "+elements[1]+" "+elements[2]);
            list_ordered.put(elements[0],new DestinyBriefView(elements[0],elements[1],elements[2]));
        }
        return list_ordered;
    }


    /*private String getDistanceFormat(String distance_string, String pickup_number){
        try {
            float distance = Float.parseFloat(distance_string);
            DecimalFormat df = new DecimalFormat("###");
            if ((distance/1000) < 1.0) {//distancia en metros
                return pickup_number.equals("0") ? "" : df.format(distance) + " mts";//distancia ya viene en metros
            } else {//distancia en km
                double rounded_distance = Math.round((distance / 1000) * 100.0) / 100.0;
                return pickup_number.equals("0") ? "" : rounded_distance + " km";//convertir a km
            }
        } catch (NumberFormatException e) {
            return pickup_number.equals("0") ? "" : distance_string + " km";
        }
    }*/
    public void bind(OrderItem model) {
        DestinyBriefView destiny_1 = null;
        DestinyBriefView destiny_2 = null;
        DestinyBriefView destiny_n = null;

        reset();
        destinationsList = getOrderedDestiny(model.getDestinations());
        text_day.setText(model.getReview_day());
        text_num_day.setText(model.getReview_num_day());
        text_num_order.setText(activity.getString(R.string.promt_num_order, model.getIdOrder()));
        //total amount or commission based on user profile
        if (current_user.getProfile().equals(Constants.profile_deliver_man)) {
            text_total.setText(StringOperations.getAmountFormat(model.getDeliverman_commision()));
        }else{
            text_total.setText(StringOperations.getAmountFormat(model.getTotal()));
        }
        //delivery fields
        //si hay valor en originalDestinations entonces ponerlo y sino poner totalDestinarions
        String text_destinies_extra = model.getIsCircularTour()==1?(" (").concat(activity.getString(R.string.prompt_circular_tour)).concat(")"):"";
        text_total_destinations.setText(activity.getString(R.string.promt_total_destinations, model.getOriginalDestinations()).concat(text_destinies_extra));
        text_origin_hours_html.setText(GeneralFunctions.fromHtml(activity.getString(R.string.promt_origin_hours_html, model.getTimeFromOrigin(), model.getTimeToOrigin())));
        text_content.setText(activity.getString(R.string.promt_content, model.getContent()));
        //image vehicle
        //image_vehicle.setImageResource(R.drawable.ic_action_camera);
        if (model.getVehicle().toLowerCase().contains("Bibi".toLowerCase())) {
            Glide.with(activity)
                    .load(R.mipmap.if_bicycle_103274).asBitmap()
                    .error(R.drawable.ic_motorcycle)
                    .into(image_vehicle);
        }else if(model.getVehicle().toLowerCase().contains("Moto".toLowerCase())){
            Glide.with(activity)
                    .load(R.mipmap.moto).asBitmap()
                    .error(R.drawable.ic_motorcycle)
                    .into(image_vehicle);
        }else if(model.getVehicle().toLowerCase().contains("Auto".toLowerCase())){
            Glide.with(activity)
                    .load(R.mipmap.beetle).asBitmap()
                    .error(R.drawable.ic_motorcycle)
                    .into(image_vehicle);
        }else{
            Glide.with(activity)
                    .load(R.mipmap.van).asBitmap()
                    .error(R.drawable.ic_motorcycle)
                    .into(image_vehicle);
        }

        //image signature, solo se muestra cuando acaba de firmar el cliente, sino se deberia mostrar pero en el detalle
       if(model.getSignatureImageLocalPath()!=null){
            image_signature.setVisibility(View.VISIBLE);
            Bitmap bitmap = BitmapFactory.decodeFile(model.getSignatureImageLocalPath());
            image_signature.setImageBitmap(bitmap);
        }



        /*  validar como se va a mostrar la lista de destinos */
        //container_destinations.addView(getDestinationsView(model.getDestinations()));
        //si hay 2 destinos, mostrarlos pero no mostrar dots
        //si hay solo 1 destino, mostrarlo y no mostrar dots ni destino n...
        //si hay mas de de 2 destinos, mostrar todos el layout y en el destino 3 poner el ultimo destino de la lista
        //Log.d(TAG, "Total Destinos:" + model.getTotalDestinations());
        switch (Integer.parseInt(model.getTotalDestinations())+1){
            case 2://un origen + 1 destinos
                //Log.d(TAG, "un origen + 1 destinos....");
                layout_1.setVisibility(View.VISIBLE);
                layout_2.setVisibility(View.VISIBLE);
                layout_dots.setVisibility(View.GONE);
                layout_n.setVisibility(View.GONE);
                //LLENAR CAMPOS
                destiny_1 = destinationsList.get("0");
                destiny_2 = destinationsList.get("1");
                //destiny_1 = getDestiny(1,model.getDestinations());
                //destiny_2 = getDestiny(2,model.getDestinations());
                text_title_1.setText(activity.getString(R.string.promt_origin));
                text_extra_info_1.setText(GeneralFunctions.getDistanceFormat(destiny_1.getDistance(), "0"));
                text_description_1.setText(destiny_1.getLocation());
                text_title_2.setText(activity.getString(R.string.title_destination,destiny_2.getPickUpPointNumber()));
                text_extra_info_2.setText(GeneralFunctions.getDistanceFormat(destiny_2.getDistance(), "1"));
                text_description_2.setText(destiny_2.getLocation());
                break;
            case 3://un origen + 2 destinos
                //Log.d(TAG, "un origen + 2 destinos....");
                layout_1.setVisibility(View.VISIBLE);
                layout_2.setVisibility(View.VISIBLE);
                layout_dots.setVisibility(View.GONE);
                layout_n.setVisibility(View.VISIBLE);
                //LLENAR CAMPOS
                destiny_1 = destinationsList.get("0");
                destiny_2 = destinationsList.get("1");
                destiny_n = destinationsList.get(String.valueOf(Integer.parseInt(model.getTotalDestinations().trim())));
                //destiny_1 = getDestiny(1,model.getDestinations());
                //destiny_2 = getDestiny(2,model.getDestinations());
                //destiny_n = getDestiny(Integer.parseInt(model.getTotalDestinations().trim()),model.getDestinations());
                text_title_1.setText(activity.getString(R.string.promt_origin));
                text_extra_info_1.setText(GeneralFunctions.getDistanceFormat(destiny_1.getDistance(), "0"));
                text_description_1.setText(destiny_1.getLocation());
                text_title_2.setText(activity.getString(R.string.title_destination,destiny_2.getPickUpPointNumber()));
                text_extra_info_2.setText(GeneralFunctions.getDistanceFormat(destiny_2.getDistance(), "1"));
                text_description_2.setText(destiny_2.getLocation());
                text_title_n.setText(activity.getString(R.string.title_destination,destiny_n.getPickUpPointNumber()));
                text_extra_info_n.setText(GeneralFunctions.getDistanceFormat(destiny_n.getDistance(), "2"));
                text_description_n.setText(destiny_n.getLocation());
                break;
            default://un origen + n destinos
                //Log.d(TAG,"un origen + n destinos...");
                if (Integer.parseInt(model.getTotalDestinations().trim())>=3){
                    layout_1.setVisibility(View.VISIBLE);
                    layout_2.setVisibility(View.VISIBLE);
                    layout_dots.setVisibility(View.VISIBLE);
                    layout_n.setVisibility(View.VISIBLE);
                    //LLENAR CAMPOS
                    destiny_1 = destinationsList.get("0");
                    destiny_2 = destinationsList.get("1");
                    destiny_n = destinationsList.get(String.valueOf(Integer.parseInt(model.getTotalDestinations().trim())));
                    //destiny_1 = getDestiny(1,model.getDestinations());
                    //destiny_2 = getDestiny(2,model.getDestinations());
                    //destiny_n = getDestiny(Integer.parseInt(model.getTotalDestinations().trim()),model.getDestinations());
                    text_title_1.setText(activity.getString(R.string.promt_origin));
                    text_extra_info_1.setText(GeneralFunctions.getDistanceFormat(destiny_1.getDistance(), "0"));
                    text_description_1.setText(destiny_1.getLocation());
                    text_title_2.setText(activity.getString(R.string.title_destination,destiny_2.getPickUpPointNumber()));
                    text_extra_info_2.setText(GeneralFunctions.getDistanceFormat(destiny_2.getDistance(), "1"));
                    text_description_2.setText(destiny_2.getLocation());
                    text_title_n.setText(activity.getString(R.string.title_destination,destiny_n.getPickUpPointNumber()));
                    text_extra_info_n.setText(GeneralFunctions.getDistanceFormat(destiny_n.getDistance(), model.getTotalDestinations()));
                    text_description_n.setText(destiny_n.getLocation());
                }else{
                    layout_1.setVisibility(View.GONE);
                    layout_2.setVisibility(View.GONE);
                    layout_dots.setVisibility(View.GONE);
                    layout_n.setVisibility(View.GONE);
                }
                break;

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            assignColorsWithClone(model, image_accepted, image_on_way, image_deliver, text_accepted, text_problem, text_deliver);
        }else{
            assignColors(model, image_accepted, image_on_way, image_deliver, text_accepted, text_problem, text_deliver);
        }

        //establecer que botones estan habilitados en base al estatus del pedido y al perfil del usuario actual
        int current_status_value = Constants.estatus_shipping.get(model.getStatus());
        if (current_status_value!=0) {
            if (current_user != null) {

                switch (current_user.getProfile()) {
                    case Constants.profile_client://---------------------------------------------------reglas para clientes ----------------------------------
                        if (current_status_value<=Constants.estatus_shipping.get(Constants.status_rejected)) { //si el pedido todavia no es enviado, el cliente puede cancelar
                            layout_client_buttons.setVisibility(View.VISIBLE);
                            text_problem.setVisibility(View.VISIBLE);
                            text_problem.setEnabled(true);
                        }else{text_problem.setEnabled(false);}
                        break;
                    case Constants.profile_admin://---------------------------------------------------reglas para administradores ----------------------------------
                        if (current_status_value<=Constants.estatus_shipping.get(Constants.status_rejected)) {//si el pedido esta en revision o rechazado, el administrador lo puede aceptar/rechazar agregando un comentario opcional
                            layout_admin_buttons.setVisibility(View.VISIBLE);
                            if (current_status_value==Constants.estatus_shipping.get(Constants.status_accepted)) {//pedido aceptado, aun se puede rechazar agregando un comentario
                                button_accept.setVisibility(View.GONE);
                                text_problem.setVisibility(View.VISIBLE);
                                text_problem.setEnabled(true);
                            }else if(current_status_value==Constants.estatus_shipping.get(Constants.status_rejected)){//pedido rechazado, se puede aceptar
                                text_problem.setVisibility(View.VISIBLE);
                                button_reject.setVisibility(View.GONE);
                            }
                        }else{// si no se puede autorizar o rechazar solo se muestra el comentario
                            text_problem.setVisibility(View.VISIBLE);
                            text_problem.setEnabled(false);

                        }
                        break;
                    case Constants.profile_deliver_man://---------------------------------------------------reglas para repartidores ----------------------------------
                        if (current_status_value==Constants.estatus_shipping.get(Constants.status_rejected) ||
                                current_status_value==Constants.estatus_shipping.get(Constants.status_cancel) ||
                                current_status_value==Constants.estatus_shipping.get(Constants.status_deliver) ||
                                current_status_value==Constants.estatus_shipping.get(Constants.status_review)){//si el pedido esta rechazado, cancelado,  entregado o en revision no se puede entregar
                            layout_deliver_buttons.setVisibility(View.GONE);
                            text_problem.setEnabled(false);
                        }else if (current_status_value>=Constants.estatus_shipping.get(Constants.status_accepted)) {//Se muestran las opciones para repartidores
                            layout_deliver_buttons.setVisibility(View.VISIBLE);
                            if (current_status_value==Constants.estatus_shipping.get(Constants.status_accepted) ||
                                    current_status_value==Constants.estatus_shipping.get(Constants.status_no_deliver)){//si el pedido esta aceptado o no se pudo entregar anteriormente, entonces el repartidor puede tomarlo para entrega, pero aun no puede poner que ya se entrego
                                button_take.setVisibility(View.VISIBLE);
                                button_delivered.setVisibility(View.GONE);
                                button_no_delivered.setVisibility(View.GONE);
                                if (model.getComment()!=null && !model.getComment().isEmpty()){// si hay comentario se muestra pero deshabilitado
                                    text_problem.setVisibility(View.VISIBLE);
                                    text_problem.setEnabled(false);
                                }else{
                                    text_problem.setVisibility(View.GONE);
                                }
                            }else{//si el pedido ya esta en ruta de entrega, entonces el repartidor puede ahora pasar a entrega exitosa o no entregado agregando un comentario opcional
                                button_take.setVisibility(View.GONE);
                                button_delivered.setVisibility(View.VISIBLE);
                                button_no_delivered.setVisibility(View.VISIBLE);
                                text_problem.setVisibility(View.VISIBLE);
                            }
                        }
                        break;
                }
            }
        }
    }
}

    //si funciona
                /*Drawable mIcon= ContextCompat.getDrawable(activity, R.drawable.ic_assignment_turned_in);
                mIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorHightligthGreen), PorterDuff.Mode.SRC_ATOP);
                image_accepted.setBackground(mIcon);*/
    /*public Drawable getTintedDrawable(@DrawableRes int drawableResId, @ColorRes int colorResId) {
        Drawable drawable = ContextCompat.getDrawable(activity,drawableResId);
        int color = ContextCompat.getColor(activity,colorResId);
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return drawable;
    }*/
    public Drawable getTintedDrawable(Drawable clone,@ColorRes int colorResId) {

        int color = ContextCompat.getColor(activity, colorResId);
        clone.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        return clone;
    }
     public void assignColorsWithClone(OrderItem model,ImageView image_accepted,ImageView image_on_way,ImageView image_deliver, TextView text_accepted,EditText text_problem,TextView text_deliver){
         Drawable clone = ContextCompat.getDrawable(activity, R.drawable.ic_assignment_turned_in).getConstantState().newDrawable().mutate();
         Drawable clone_on_way = ContextCompat.getDrawable(activity, R.drawable.ic_motorcycle).getConstantState().newDrawable().mutate();
         Drawable clone_deliver = ContextCompat.getDrawable(activity, R.drawable.ic_check_circle).getConstantState().newDrawable().mutate();
         //Log.d(TAG,"VERSION ANDROID:"+Build.VERSION.SDK_INT);
         //Log.d(TAG, "ORDER: " + model.getIdOrder() + " estatus: " + model.getStatus());
         //establece progreso de entrega en base al estatus del pedido, si el pedido esta rechazado, cancelado o no entregado se muestra el texto de comentario
         switch (model.getStatus()){
             case Constants.status_review:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.darker_gray));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.darker_gray));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.darker_gray));
                 break;
             case Constants.status_accepted:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_green_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.darker_gray));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.darker_gray));
                 break;
             case Constants.status_rejected:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_red_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.darker_gray));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.darker_gray));

                 text_accepted.setText(Constants.status_rejected.substring(0, 1).toUpperCase() + Constants.status_rejected.substring(1));
                 text_problem.setVisibility(View.VISIBLE);
                 text_problem.setText(model.getComment());
                 break;
             case Constants.status_on_way:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_green_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.holo_green_light));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.darker_gray));
                 break;
             case Constants.status_deliver:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_green_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.holo_green_light));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.holo_green_light));
                 break;
             case Constants.status_no_deliver:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_green_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.holo_green_light));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.holo_red_light));
                 text_deliver.setText(Constants.status_no_deliver.substring(0,1).toUpperCase() + Constants.status_no_deliver.substring(1).replace("_"," "));
                 text_problem.setVisibility(View.VISIBLE);
                 text_problem.setText(model.getComment());
                 break;
             case Constants.status_cancel:
                 image_accepted.setImageDrawable(getTintedDrawable(clone, android.R.color.holo_red_light));
                 image_on_way.setImageDrawable(getTintedDrawable(clone_on_way, android.R.color.darker_gray));
                 image_deliver.setImageDrawable(getTintedDrawable(clone_deliver, android.R.color.darker_gray));

                 text_accepted.setText(Constants.status_cancel.substring(0,1).toUpperCase() + Constants.status_cancel.substring(1));
                 text_problem.setVisibility(View.VISIBLE);
                 text_problem.setText(model.getComment());
                 break;
         }
     }
    public void assignColors(OrderItem model,ImageView image_accepted,ImageView image_on_way,ImageView image_deliver, TextView text_accepted,EditText text_problem,TextView text_deliver){
        //establece progreso de entrega en base al estatus del pedido, si el pedido esta rechazado, cancelado o no entregado se muestra el texto de comentario
        switch (model.getStatus()){
            case Constants.status_review:

                break;
            case Constants.status_accepted:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                break;
            case Constants.status_rejected:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                text_accepted.setText(Constants.status_rejected.substring(0, 1).toUpperCase() + Constants.status_rejected.substring(1));
                text_problem.setVisibility(View.VISIBLE);
                text_problem.setText(model.getComment());
                break;
            case Constants.status_on_way:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                break;
            case Constants.status_deliver:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                break;
            case Constants.status_no_deliver:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                text_deliver.setText(Constants.status_no_deliver.substring(0, 1).toUpperCase() + Constants.status_no_deliver.substring(1).replace("_", " "));
                text_problem.setVisibility(View.VISIBLE);
                text_problem.setText(model.getComment());
                break;
            case Constants.status_cancel:
                image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                text_accepted.setText(Constants.status_cancel.substring(0,1).toUpperCase() + Constants.status_cancel.substring(1));
                text_problem.setVisibility(View.VISIBLE);
                text_problem.setText(model.getComment());
                break;
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof PendingOrdersAdapter.ViewHolder) {

            final PendingOrdersAdapter.ViewHolder p_holder = (PendingOrdersAdapter.ViewHolder) holder;
            final OrderItem order = list.get(position);
            p_holder.bind(order);


            //asigna eventos por tipo de perfil
            switch(current_user.getProfile()) {
                case Constants.profile_client:
                    p_holder.button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            fragment.changeStatusOrder(position, Constants.status_cancel, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    //disable events
                    p_holder.button_accept.setOnClickListener(null);
                    p_holder.button_reject.setOnClickListener(null);
                    p_holder.button_take.setOnClickListener(null);
                    p_holder.button_delivered.setOnClickListener(null);
                    p_holder.button_no_delivered.setOnClickListener(null);
                    break;
                case Constants.profile_admin:
                    p_holder.button_accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            fragment.changeStatusOrder(position, Constants.status_accepted, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            fragment.changeStatusOrder(position, Constants.status_rejected, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_cancel.setOnClickListener(null);
                    p_holder.button_take.setOnClickListener(null);
                    p_holder.button_delivered.setOnClickListener(null);
                    p_holder.button_no_delivered.setOnClickListener(null);
                    break;
                case Constants.profile_deliver_man:
                    p_holder.button_take.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            fragment.changeStatusOrder(position, Constants.status_on_way, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_delivered.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));

                            //obtener la firma del cliente antes de confirmar entrega
                            if (list.get(position).getSignatureImageLocalPath()!=null){// ya se tiene la firma ahora actualizar
                                //fragment.changeStatusOrder(position, Constants.status_deliver, p_holder.text_problem.getText().toString().trim());
                                fragment.uploadSignature(position, p_holder.image_signature,Constants.status_deliver, p_holder.text_problem.getText().toString().trim());
                            }else {
                                fragment.getSignature(position);
                            }
                        }
                    });
                    p_holder.button_no_delivered.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            fragment.changeStatusOrder(position, Constants.status_no_deliver, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_accept.setOnClickListener(null);
                    p_holder.button_reject.setOnClickListener(null);
                    p_holder.button_cancel.setOnClickListener(null);
                    break;
            }

            p_holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragment.getOrderDetail(position);
                }
            });



        }else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }
    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void setLoaded() {
        isLoading = false;
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }
    /************************Animations*********************/
    public void animateTo(List<OrderItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<OrderItem> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final OrderItem model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<OrderItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final OrderItem model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<OrderItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final OrderItem model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public OrderItem removeItem(int position) {
        final OrderItem model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, OrderItem model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final OrderItem model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

}
