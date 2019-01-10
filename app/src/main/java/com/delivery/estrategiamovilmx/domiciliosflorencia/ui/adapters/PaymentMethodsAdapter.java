package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PaymentMethod;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.PaymentMethodActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 04/08/2017.
 */
public class PaymentMethodsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private PaymentMethodActivity activity;
    private ArrayList<PaymentMethod> list;
    private static final String TAG = PaymentMethodsAdapter.class.getSimpleName();

    public PaymentMethodsAdapter(PaymentMethodActivity act, ArrayList<PaymentMethod> myDataset) {
        list = myDataset;
        activity=act;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_payment_method_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView image_on_delivery;
        private TextView text_method_title;
        private TextView text_description;
        private RelativeLayout layout_over;

        public ViewHolder(View v) {
            super(v);
            text_description = (TextView) v.findViewById(R.id.text_description);
            text_method_title = (TextView) v.findViewById(R.id.text_method_title);
            image_on_delivery = (ImageView) v.findViewById(R.id.image_on_delivery);
            layout_over = (RelativeLayout) v.findViewById(R.id.layout_over);
        }
        public void bind(PaymentMethod model) {
            Log.d(TAG,"isSelected:"+model.isSelected() );
            text_method_title.setText(model.getMethod());
            text_description.setText(model.getDescription());
            Glide.with(activity.getApplicationContext())
                    .load(model.getImage())
                    .into(image_on_delivery);

            layout_over.setVisibility(model.isSelected()?View.VISIBLE:View.GONE);
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof PaymentMethodsAdapter.ViewHolder) {
            final PaymentMethodsAdapter.ViewHolder p_holder = (PaymentMethodsAdapter.ViewHolder) holder;
            final PaymentMethod method = list.get(position);
            p_holder.bind(method);


            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //reset old element selected
                    activity.resetElement(activity.getMethod_position_selected());
                    activity.setMethodSelected(position);
                }
            });



        }
    }
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /************************Animations*********************/
    public void animateTo(List<PaymentMethod> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<PaymentMethod> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final PaymentMethod model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<PaymentMethod> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final PaymentMethod model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<PaymentMethod> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final PaymentMethod model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public PaymentMethod removeItem(int position) {
        final PaymentMethod model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, PaymentMethod model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final PaymentMethod model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
