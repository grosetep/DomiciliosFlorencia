package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.OrdersActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 15/08/2017.
 */
public class OrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private OrdersActivity activity;
    private ArrayList<OrderItem> list;
    private UserItem current_user;
    //
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    private static final String TAG = OrdersAdapter.class.getSimpleName();

    public OrdersAdapter(OrdersActivity act, ArrayList<OrderItem> myDataset,UserItem user,RecyclerView recycler) {
        list = myDataset;
        activity=act;
        current_user = user;
        recyclerview = recycler;
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
        private TextView text_num_order;
        private TextView text_day;
        private TextView text_num_day;
        //new delivery fields
        private TextView text_total_destinations;
        private TextView text_origin_hours_html;
        private LinearLayout container_destinations;
        private TextView text_content;

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
            //reset colors
            image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
            image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.darker_gray));
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
        }
        public LinearLayout getDestinationsView(String destinations){
            Log.d(TAG,"getDestinationsView:" + destinations);
            LinearLayout customView = new LinearLayout(activity);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            customView.setLayoutParams(lp);
            TextView text_title;//numero de destino
            TextView text_description;//direccion o punto
            TextView text_extra_info;//distancia

            //iterar por los destinos
            if (destinations!=null) {
                String[] destinstions_array = destinations.split("\\|");
                Log.d(TAG,"destinations.split:" + destinstions_array.length);
                for(String destiny:destinstions_array) {
                    Log.d(TAG,"DESTINY>>"+destiny);
                    String[] elements_destiny = destiny.split("@@@");
                    Log.d(TAG,"elements_destiny:"+elements_destiny.length);
                    LinearLayout fl = new LinearLayout(activity);
                    // Create Layout Parameters for FrameLayout
                    LayoutInflater mInflater = LayoutInflater.from(activity);
                    final View currentCustomView = mInflater.inflate(R.layout.destiny_item_layout, null);
                    text_title = (TextView) currentCustomView.findViewById(R.id.text_title);
                    text_description = (TextView) currentCustomView.findViewById(R.id.text_description);
                    text_extra_info = (TextView) currentCustomView.findViewById(R.id.text_extra_info);
                    for(int i =0;i<elements_destiny.length;i++) {
                       switch (i){
                           case 0:text_title.setText(elements_destiny[0]);break;
                           case 1:text_description.setText(elements_destiny[1].substring(1,10).concat("..."));break;
                           case 2:text_extra_info.setText(elements_destiny[2]);break;
                       }

                    }
                    fl.addView(currentCustomView);
                    customView.addView(fl);
                }
            }
            //fin ciclo
            return customView;
        }

        public void bind(OrderItem model) {
            reset();
            text_day.setText(model.getReview_day());
            text_num_day.setText(model.getReview_num_day());
            text_num_order.setText(activity.getString(R.string.promt_num_order, model.getIdOrder()));
            text_total.setText(StringOperations.getAmountFormat(model.getTotal()));
            //delivery fields
            text_total_destinations.setText(model.getTotalDestinations());
            text_origin_hours_html.setText( GeneralFunctions.fromHtml(activity.getString(R.string.promt_origin_hours_html, model.getTimeFromOrigin(), model.getTimeToOrigin())));
            container_destinations.addView(getDestinationsView(model.getDestinations()));
            text_content.setText(model.getContent());

            //establece progreso de entrega en base al estatus del pedido, si el pedido esta rechazado, cancelado o no entregado se muestra el texto de comentario
            switch (model.getStatus()){
                case Constants.status_review:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    break;
                case Constants.status_accepted:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    break;
                case Constants.status_rejected:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                    text_accepted.setText(Constants.status_rejected.substring(0,1).toUpperCase() + Constants.status_rejected.substring(1));
                    text_problem.setVisibility(View.VISIBLE);
                    text_problem.setText(model.getComment());
                    break;
                case Constants.status_on_way:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    break;
                case Constants.status_deliver:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    break;
                case Constants.status_no_deliver:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_on_way.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_deliver.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                    text_deliver.setText(Constants.status_no_deliver.substring(0,1).toUpperCase() + Constants.status_no_deliver.substring(1).replace("_"," "));
                    text_problem.setVisibility(View.VISIBLE);
                    text_problem.setText(model.getComment());
                    break;
                case Constants.status_cancel:
                    image_review.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_light));
                    image_accepted.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_light));
                    text_accepted.setText(Constants.status_cancel.substring(0,1).toUpperCase() + Constants.status_cancel.substring(1));
                    text_problem.setVisibility(View.VISIBLE);
                    text_problem.setText(model.getComment());
                    break;
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
                                    text_problem.setText(activity.getString(R.string.promt_hint_accepted_comment));
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
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof OrdersAdapter.ViewHolder) {
            final OrdersAdapter.ViewHolder p_holder = (OrdersAdapter.ViewHolder) holder;
            final OrderItem order = list.get(position);
            p_holder.bind(order);


            //asigna eventos por tipo de perfil
            switch(current_user.getProfile()) {
                case Constants.profile_client:
                    p_holder.button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            activity.changeStatusOrder(position, Constants.status_cancel, p_holder.text_problem.getText().toString().trim());
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
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            activity.changeStatusOrder(position, Constants.status_accepted, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            activity.changeStatusOrder(position, Constants.status_rejected, p_holder.text_problem.getText().toString().trim());
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
                            activity.changeStatusOrder(position, Constants.status_on_way, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_delivered.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            activity.changeStatusOrder(position, Constants.status_deliver, p_holder.text_problem.getText().toString().trim());
                        }
                    });
                    p_holder.button_no_delivered.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            v.setEnabled(false);
                            v.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.darker_gray));
                            activity.changeStatusOrder(position, Constants.status_no_deliver, p_holder.text_problem.getText().toString().trim());
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
                    activity.getOrderDetail(position);
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
