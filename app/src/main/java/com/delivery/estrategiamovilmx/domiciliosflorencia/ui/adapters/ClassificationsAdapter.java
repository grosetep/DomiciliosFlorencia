package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ClassificationItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainClassificationsActivity;

import java.util.ArrayList;
import java.util.List;

public class ClassificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<ClassificationItem> list;
    private MainClassificationsActivity activity;

    private static final String TAG = BudgetAdapter.class.getSimpleName();

    public ClassificationsAdapter( ArrayList<ClassificationItem> myDataset,MainClassificationsActivity act) {
        list = myDataset;
        activity = act;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_classification_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private String classificationKey;
        private ImageView image;
        private TextView text_title;
        private ImageView image_pressed;
        private TextView text_title_pressed;
        private LinearLayout layout_normal;
        private LinearLayout layout_pressed;


        public ViewHolder(View v) {
            super(v);
            classificationKey = "";
            image = v.findViewById(R.id.image);
            text_title = v.findViewById(R.id.text_title);
            image_pressed = v.findViewById(R.id.image_pressed);
            text_title_pressed = v.findViewById(R.id.text_title_pressed);
            layout_pressed = v.findViewById(R.id.layout_pressed);
            layout_normal =  v.findViewById(R.id.layout_normal);
        }
        public void bind(ClassificationItem model) {
            classificationKey = model.getClassificationKey();
            String ImagePath = model.getPathIcon() + model.getIcon();
            Glide.with(activity.getApplicationContext())
                    .load(ImagePath)
                    .into(image);
            text_title.setText(model.getClassification());
            Glide.with(activity.getApplicationContext())
                    .load(ImagePath)
                    .into(image_pressed);
            text_title_pressed.setText(model.getClassification());

            if (model.getSelected().equals(String.valueOf(true))){
                layout_pressed.setVisibility(View.VISIBLE);
                layout_normal.setVisibility(View.GONE);
            }else{
                layout_pressed.setVisibility(View.GONE);
                layout_normal.setVisibility(View.VISIBLE);
            }

        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ClassificationsAdapter.ViewHolder p_holder = (ClassificationsAdapter.ViewHolder) holder;
        final ClassificationItem item = list.get(position);
        p_holder.bind(item);

        p_holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getSelected().equals(String.valueOf(true))){//des seleccionar elemento y consulta inicial
                    activity.resetElement(position);
                    activity.setClassificationItemSelected(-1);
                    activity.getMerchantsByClassifitacion(-1);
                }else {
                    //reset old element selected
                    activity.resetElement(activity.getClassificationSelected());
                    activity.setClassificationItemSelected(position);
                    activity.getMerchantsByClassifitacion(position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /************************Animations*********************/
    public void animateTo(List<ClassificationItem> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<ClassificationItem> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final ClassificationItem model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<ClassificationItem> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ClassificationItem model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ClassificationItem> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ClassificationItem model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public ClassificationItem removeItem(int position) {
        final ClassificationItem model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ClassificationItem model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final ClassificationItem model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}