package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CartProductItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ShoppingCartActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 08/08/2017.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private ShoppingCartActivity activity;
    private ArrayList<CartProductItem> list;
    private static final String TAG = ShoppingCartAdapter.class.getSimpleName();
    private String id_country;

    public ShoppingCartAdapter(ShoppingCartActivity act, ArrayList<CartProductItem> myDataset) {
        list = myDataset;
        activity=act;
        id_country = ApplicationPreferences.getLocalStringPreference(activity,Constants.id_country);
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cart_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout layout_over;
        private ImageView image_product;
        private TextView text_method_title;
        private TextView text_description;
        private ImageButton button_less;
        private TextView text_quantity;
        private ImageButton button_add;
        private TextView text_price;
        private ImageView overflow;
        private TextView text_total;
        private TextView text_variant;
        private TextView text_additionals;

        public ViewHolder(View v) {
            super(v);
            layout_over = (RelativeLayout) v.findViewById(R.id.layout_over);
            image_product = (ImageView) v.findViewById(R.id.image_product);
            text_method_title = (TextView) v.findViewById(R.id.text_method_title);
            text_description = (TextView) v.findViewById(R.id.text_description);
            button_less = (ImageButton) v.findViewById(R.id.button_less);
            text_quantity = (TextView) v.findViewById(R.id.text_quantity);
            text_price = (TextView) v.findViewById(R.id.text_price);
            button_add = (ImageButton) v.findViewById(R.id.button_add);
            overflow = (ImageView) v.findViewById(R.id.overflow);
            text_total = (TextView) v.findViewById(R.id.text_total);
            text_variant = (TextView) v.findViewById(R.id.text_variant);
            text_additionals = (TextView) v.findViewById(R.id.text_additionals);
        }
        public void bind(CartProductItem model) {
            if (model.getStock()!=null && model.getStock().equals(String.valueOf(Constants.cero))){
                layout_over.setVisibility(View.VISIBLE);
            }
            Glide.with(activity.getApplicationContext())
                    .load(model.getRoute() + model.getImage())
                    .into(image_product);

            text_method_title.setText(model.getProduct());
            text_description.setText(model.getDescription());
            text_quantity.setText(model.getUnits());
            text_price.setText(model.getOfferPrice() != null && !model.getOfferPrice().isEmpty() ? StringOperations.getAmountFormat(model.getOfferPrice(),id_country) + " X " + model.getUnits() : StringOperations.getAmountFormat(model.getRegularPrice(),id_country) + " X " + model.getUnits());
            //variante
            if (model.getVariant()!=null) {
                text_variant.setText(model.getVariant());
                text_variant.setVisibility(View.VISIBLE);
            }else{text_variant.setVisibility(View.GONE);text_variant.setText("");}

            text_total.setText("Total " + StringOperations.getAmountFormat(model.getTotal(),id_country));

            if (model.getAdditionals()!=null){
                text_additionals.setText(model.getAdditionals());
                text_additionals.setVisibility(View.VISIBLE);
            }else{
                text_additionals.setVisibility(View.GONE);
                text_additionals.setText("");
            }

        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ShoppingCartAdapter.ViewHolder) {
            final ShoppingCartAdapter.ViewHolder p_holder = (ShoppingCartAdapter.ViewHolder) holder;
            final CartProductItem cart = list.get(position);
            p_holder.bind(cart);

            p_holder.overflow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.removeFromCart(position);
                }
            });
            p_holder.button_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int units_user = Integer.parseInt(p_holder.text_quantity.getText().toString());
                    String initial_units = cart.getUnits();
                    int stock = Integer.parseInt(cart.getStock());
                    if (units_user+1<=stock){
                        units_user = units_user +1;
                        cart.setUnits(String.valueOf(units_user));

                        activity.updateItem(cart.getTotal(), initial_units, cart.getUnits(), position);
                        notifyItemChanged(position);
                        activity.calculateTotal();
                    }
                }
            });
            p_holder.button_less.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int units_user = Integer.parseInt(p_holder.text_quantity.getText().toString());
                    if (units_user==1){
                        return;
                    }else {
                        units_user = units_user - 1;
                        String initial_units = cart.getUnits();
                        cart.setUnits(String.valueOf(units_user));
                        p_holder.text_quantity.setText("" + units_user);

                        activity.updateItem(cart.getTotal(), initial_units,cart.getUnits(),position);
                        notifyItemChanged(position);
                        activity.calculateTotal();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /************************Animations*********************/
    public void animateTo(List<CartProductItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<CartProductItem> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final CartProductItem model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<CartProductItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final CartProductItem model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<CartProductItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final CartProductItem model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public CartProductItem removeItem(int position) {
        final CartProductItem model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, CartProductItem model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final CartProductItem model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
