package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ShippingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 27/07/2017.
 */
public class ShippingAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private ShippingActivity activity;
    private ArrayList<ShippingAddress> list;
    private static final String TAG = ShippingAddressAdapter.class.getSimpleName();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_NEW = 1;

    public ShippingAddressAdapter(ShippingActivity act, ArrayList<ShippingAddress> myDataset) {
        list = myDataset;
        activity=act;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_shipping_address, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_add_shipping_address, parent, false);
            return new AddViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) == null ? VIEW_TYPE_NEW : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_shipping_address;
        private ImageView image;

        public ViewHolder(View v) {
            super(v);
            text_shipping_address = (TextView) v.findViewById(R.id.text_shipping_address);
            image = (ImageView) v.findViewById(R.id.image);
        }
        public void bind(ShippingAddress model) {
            String show_address  = model.getGooglePlace().length()> Constants.address_max_length?model.getGooglePlace().substring(0,Constants.address_max_length)+"...,":model.getGooglePlace();
            text_shipping_address.setText(show_address.concat(model.getNum_int()!=null && !model.getNum_int().isEmpty()?model.getNum_int():"").concat(", ").concat(model.getReference()));
            if (model.isSelected()){
                image.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent));
            } else if (model.getIsNew().equals(String.valueOf(true))){
                image.setColorFilter(Color.parseColor(Constants.colorSuccess));
            }
            else {image.setColorFilter(ContextCompat.getColor(activity, R.color.gray));}
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ShippingAddressAdapter.ViewHolder) {
            final ShippingAddressAdapter.ViewHolder p_holder = (ShippingAddressAdapter.ViewHolder) holder;
            final ShippingAddress ship = list.get(position);
            p_holder.bind(ship);


            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //reset old element selected
                    Log.d(TAG, "Reset old element: " + activity.getAddress_position_selected());
                    activity.resetElement(activity.getAddress_position_selected());

                    Log.d(TAG, "before---------    ship.isNew():" + ship.getIsNew() + " isSelected:" + ship.isSelected());
                    activity.setShippingAddressSelected(position);
                }
            });


        }else if(holder instanceof AddViewHolder){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startAddShippingAddress();
                }
            });
        }
    }
    static class AddViewHolder extends RecyclerView.ViewHolder {
        CardView card_view_add;

        public AddViewHolder(View itemView) {
            super(itemView);
            card_view_add = (CardView) itemView.findViewById(R.id.card_view_add);
        }


    }
    /************************Animations*********************/
    public void animateTo(List<ShippingAddress> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ShippingAddress> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final ShippingAddress model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ShippingAddress> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ShippingAddress model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ShippingAddress> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ShippingAddress model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ShippingAddress removeItem(int position) {
        final ShippingAddress model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ShippingAddress model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ShippingAddress model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}