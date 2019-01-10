package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PickupPointFragment;

import java.util.ArrayList;
import java.util.List;

public class DestinationAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
private Context activity;
private PickupPointFragment fragment;
private ArrayList<ShippingAddress> list;
private static final String TAG = DestinationAddressAdapter.class.getSimpleName();
private final int VIEW_TYPE_ITEM = 0;
private final int VIEW_TYPE_NEW = 1;

    public DestinationAddressAdapter(Context act, PickupPointFragment frm, ArrayList<ShippingAddress> myDataset) {
        list = myDataset;
        activity=act;
        fragment = frm;
        }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_shipping_address, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_select_an_address, parent, false);
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
    private TextView text_detail;

    public ViewHolder(View v) {
        super(v);
        text_shipping_address = (TextView) v.findViewById(R.id.text_shipping_address);
        text_detail = v.findViewById(R.id.text_detail);
    }
    public void bind(ShippingAddress model) {
        String show_address  = model.getGooglePlace().length()> Constants.address_max_length?model.getGooglePlace().substring(0,Constants.address_max_length)+"...,":model.getGooglePlace();
        text_shipping_address.setText(show_address);
        text_detail.setText((model.getNum_int() != null && !model.getNum_int().isEmpty() ? activity.getString(R.string.prompt_interior)+model.getNum_int()+", " : "").concat(model.getReference() != null && !model.getReference().isEmpty() ? model.getReference():""));
    }
}
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof DestinationAddressAdapter.ViewHolder) {
            final DestinationAddressAdapter.ViewHolder p_holder = (DestinationAddressAdapter.ViewHolder) holder;
            final ShippingAddress ship = list.get(position);
            p_holder.bind(ship);


            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //reset old element selected
                    //fragment.resetElement(fragment.getAddress_position_selected());
                    //fragment.setShippingAddressSelected(position);
                    fragment.startManageShippingAddresses();
                }
            });


        }else if(holder instanceof AddViewHolder){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //fragment.startAddShippingAddress();
                    fragment.startManageShippingAddresses();
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
