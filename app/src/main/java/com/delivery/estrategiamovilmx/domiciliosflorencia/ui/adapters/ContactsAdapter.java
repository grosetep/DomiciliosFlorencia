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
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Contact;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ContactActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by administrator on 03/08/2017.
 */
public class ContactsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ContactActivity activity;
    private ArrayList<Contact> list;
    private static final String TAG = ContactsAdapter.class.getSimpleName();
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_NEW = 1;

    public ContactsAdapter(ContactActivity act, ArrayList<Contact> myDataset) {
        list = myDataset;
        activity=act;
    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_contact_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_add_contact, parent, false);
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
        private ImageView image;
        private TextView text_name;
        private TextView text_number;

        public ViewHolder(View v) {
            super(v);
            text_name = (TextView) v.findViewById(R.id.text_name);
            text_number = (TextView) v.findViewById(R.id.text_number);
            image = (ImageView) v.findViewById(R.id.image);
        }
        public void bind(Contact model) {
            Log.d(TAG,"isSelected:"+model.isSelected() + " isNew:"+ model.isNew());
            text_number.setText(model.getPhone());
            text_name.setText(model.getName());
            if (model.isSelected()){Log.d(TAG,"-------selected");
                image.setColorFilter(ContextCompat.getColor(activity, R.color.colorAccent));
            } else if (model.isNew()){Log.d(TAG,"-------new");
                image.setColorFilter(Color.parseColor(Constants.colorSuccess));
            }
            else {Log.d(TAG,"-------nothing");image.setColorFilter(ContextCompat.getColor(activity, R.color.gray));}
        }
    }
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof ContactsAdapter.ViewHolder) {
            final ContactsAdapter.ViewHolder p_holder = (ContactsAdapter.ViewHolder) holder;
            final Contact contact = list.get(position);
            p_holder.bind(contact);


            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //reset old element selected
                    activity.resetElement(activity.getContact_position_selected());
                    activity.setContactSelected(position);
                }
            });


        }else if(holder instanceof AddViewHolder){
            final ContactsAdapter.AddViewHolder p_holder = (ContactsAdapter.AddViewHolder) holder;
            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startAddContact();
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
    public void animateTo(List<Contact> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<Contact> newModels) {
        for (int i = list.size() - 1; i >= 0; i--) {
            final Contact model = list.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Contact> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final Contact model = newModels.get(i);
            if (!list.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Contact> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final Contact model = newModels.get(toPosition);
            final int fromPosition = list.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public Contact removeItem(int position) {
        final Contact model = list.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, Contact model) {
        list.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        final Contact model = list.remove(fromPosition);
        list.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }
}
