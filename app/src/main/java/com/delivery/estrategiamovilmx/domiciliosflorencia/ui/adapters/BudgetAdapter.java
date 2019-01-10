package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PickupAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.BudgetActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 08/08/2017.
 */
public class BudgetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {

    private ArrayList<PickupAddress> list;
    private BudgetActivity activity;

    private static final String TAG = BudgetAdapter.class.getSimpleName();

    public BudgetAdapter( ArrayList<PickupAddress> myDataset,BudgetActivity act) {
        list = myDataset;
        activity = act;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_destiny_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView text_title;
        private TextView text_description;
        private TextView text_extra_info;

        public ViewHolder(View v) {
            super(v);
            text_title = (TextView) v.findViewById(R.id.text_title);
            text_description = (TextView) v.findViewById(R.id.text_description);
            text_extra_info = (TextView) v.findViewById(R.id.text_extra_info);
        }
        public void bind(PickupAddress model) {
            text_title.setText(model.getPickupPointNumber() == 0 ? activity.getString(R.string.title_origin) : activity.getString(R.string.title_destination, model.getPickupPointNumber()));
            text_description.setText(model.getGooglePlace().getAddressForUser());
            if (model.getPickupPointNumber() != 0 ) {//en el origen no se muestra la distancia
                try {
                    float distance = Float.parseFloat(model.getDistance().trim());
                    //DecimalFormat df = new DecimalFormat("###.0");//formato a 1 decimal
                    DecimalFormat df = new DecimalFormat("###");//sin decimales
                    if ((distance/1000) < 1.0) {//distancia en metros
                        text_extra_info.setText(model.getPickupPointNumber() == 0 ? "" : df.format(distance) + " mts");//covertir a metros
                    } else {//distancia en km
                        //convertir a km, redondeo a 2 decimales
                        //Mediante Math.Round() donde la cantidad de ceros es la cantidad de decimales a limitar
                        double rounded_distance = Math.round((distance / 1000) * 100.0) / 100.0;
                        text_extra_info.setText(model.getPickupPointNumber() == 0 ? "" : rounded_distance + " km");
                    }
                } catch (NumberFormatException e) {
                    text_extra_info.setText(model.getPickupPointNumber() == 0 ? "" : model.getDistance() + " km");
                }
            }
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final BudgetAdapter.ViewHolder p_holder = (BudgetAdapter.ViewHolder) holder;
        final PickupAddress address = list.get(position);
        p_holder.bind(address);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /************************Animations*********************/
    public void animateTo(List<PickupAddress> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<PickupAddress> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final PickupAddress model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PickupAddress> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PickupAddress model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PickupAddress> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PickupAddress model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public PickupAddress removeItem(int position) {
        final PickupAddress model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, PickupAddress model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PickupAddress model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
