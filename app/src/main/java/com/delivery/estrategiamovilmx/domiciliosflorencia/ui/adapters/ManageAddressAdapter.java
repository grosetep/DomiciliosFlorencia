package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.BottomSheetFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ManageLocationsFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ManageAddressAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ManageLocationsFragment fragment;
    private Activity ctx;
    private BottomSheetFragment fragment_sheet;
    private ArrayList<ShippingAddress> list;
    private static final String TAG = ManageAddressAdapter.class.getSimpleName();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_NEW = 1;

    public ManageAddressAdapter(Fragment act, ArrayList<ShippingAddress> myDataset) {
        list = myDataset;

        if (act instanceof ManageLocationsFragment) {
            fragment = (ManageLocationsFragment) act;
            ctx = fragment.getActivity();
        }else if(act instanceof BottomSheetFragment){
            fragment_sheet = (BottomSheetFragment) act;
            ctx = fragment_sheet.getActivity();
        }
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_address_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = null;
            if (fragment!=null) {
                view = LayoutInflater.from(fragment.getContext()).inflate(R.layout.layout_add_fav_shipping_address, parent, false);
            }else{
                view = LayoutInflater.from(fragment_sheet.getContext()).inflate(R.layout.layout_add_fav_shipping_address, parent, false);
            }
            return new AddViewHolder(view);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_shipping_address;
        private TextView text_detail;
        private ImageView overflow;
        private ImageView image_star_yellow;
        private ImageView image_star_gray;
        //layout actions
        private LinearLayout layout_actions;
        private ImageView image_close;
        private ImageView image_edit;
        private ImageView image_delete;

        public ViewHolder(View v) {
            super(v);
            text_shipping_address = v.findViewById(R.id.text_shipping_address);
            text_detail = v.findViewById(R.id.text_detail);
            layout_actions = v.findViewById(R.id.layout_actions);
            image_close = v.findViewById(R.id.image_close);
            image_edit = v.findViewById(R.id.image_edit);
            image_delete = v.findViewById(R.id.image_delete);
            overflow = v.findViewById(R.id.overflow);
            image_star_yellow = v.findViewById(R.id.image_star_yellow);
            image_star_gray = v.findViewById(R.id.image_star_gray);
        }
        public void bind(ShippingAddress model) {
            String show_detail = "";
            String show_address  = model.getGooglePlace().length()> Constants.address_max_length?model.getGooglePlace().substring(0,Constants.address_max_length)+"..,":model.getGooglePlace();
            if (fragment!=null) {
                show_detail = (model.getNum_int() != null && !model.getNum_int().isEmpty() ? fragment.getString(R.string.prompt_interior) + model.getNum_int() + ", " : "").concat(model.getReference() != null && !model.getReference().isEmpty() ? model.getReference() : "");
            }else{
                show_detail = (model.getNum_int() != null && !model.getNum_int().isEmpty() ? fragment_sheet.getString(R.string.prompt_interior) + model.getNum_int() + ", " : "").concat(model.getReference() != null && !model.getReference().isEmpty() ? model.getReference() : "");
            }
            text_shipping_address.setText(show_address);
            text_detail.setText(show_detail.length() > Constants.detail_max_length? show_detail.substring(0,Constants.detail_max_length)+"..,":show_detail);
            if (model.isSelected()){
                image_star_gray.setVisibility(View.GONE);
                image_star_yellow.setVisibility(View.VISIBLE);
            }else{
                image_star_gray.setVisibility(View.VISIBLE);
                image_star_yellow.setVisibility(View.GONE);
            }
            layout_actions.setVisibility(View.GONE);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ManageAddressAdapter.ViewHolder) {
            final ManageAddressAdapter.ViewHolder p_holder = (ManageAddressAdapter.ViewHolder) holder;
            final ShippingAddress ship = list.get(position);
            p_holder.bind(ship);


            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //return selected location
                    saveLocationFavorite(ship);
                    if (fragment!=null)
                        fragment.returnAddressSelected(ship);
                    else
                        fragment_sheet.assignAddressSelected(position,true);


                }
            });

            p_holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p_holder.layout_actions.setVisibility(View.VISIBLE);
                    p_holder.itemView.setOnClickListener(null);//deshabilitar el evento
                }
            });

            p_holder.image_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    p_holder.layout_actions.setVisibility(View.GONE);
                    p_holder.itemView.setOnClickListener(new View.OnClickListener() {//volver a poner el evento
                        @Override
                        public void onClick(View v) {
                            saveLocationFavorite(ship);
                            if (fragment!=null)
                                fragment.returnAddressSelected(ship);
                            else
                                fragment_sheet.assignAddressSelected(position,true);
                        }
                    });
                }
            });
            p_holder.image_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (fragment!=null)
                        fragment.startAddShippingAddress(ship,position);
                    else
                        fragment_sheet.startAddShippingAddress(ship,position);
                }
            });

            p_holder.image_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment!=null)
                        fragment.deleteShippingAddress(position);
                    else
                        fragment_sheet.deleteShippingAddress(position);
                }
            });

            if (ship.isSelected()){//asignar valor de preferencias
                saveLocationFavorite(ship);
            }
        }else if(holder instanceof AddViewHolder){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (fragment!=null)
                        fragment.startAddShippingAddress(null,position);
                    else
                        fragment_sheet.startAddShippingAddress(null,position);
                }
            });
        }
        //assign current address favorite selectes if exist

    }
    private void saveLocationFavorite(ShippingAddress ship){
        Gson gson = new Gson();
        String json_address = gson.toJson(ship);
        if (fragment!=null) {
            ApplicationPreferences.saveLocalPreference(fragment.getContext(), Constants.FAVORITE_ADDRESS_SELECTED, json_address);
        }else{
            ApplicationPreferences.saveLocalPreference(fragment_sheet.getContext(), Constants.FAVORITE_ADDRESS_SELECTED, json_address);
        }

    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        CardView card_view_add;

        public AddViewHolder(View itemView) {
            super(itemView);
            card_view_add = itemView.findViewById(R.id.card_view_add);
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