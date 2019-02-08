package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.app.Activity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainClassificationsActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.List;

public class MerchantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MerchantItem> merchants;
    private MainClassificationsActivity activity;
    private final int METHOD_LIKE = 2;
    private String id_country = "";
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = MerchantsAdapter.class.getSimpleName();

    public MerchantsAdapter(List<MerchantItem> myDataset,Activity activity,RecyclerView list) {
        id_country = ApplicationPreferences.getLocalStringPreference(activity, Constants.id_country);
        merchants = myDataset;
        this.activity = (MainClassificationsActivity)activity;
        recyclerview = list;
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
    public void clear(){ merchants.clear();notifyDataSetChanged();}
    public void addAll(List<MerchantItem> list){
        if (list!=null && list.size()>0){merchants.addAll(list);notifyDataSetChanged(); }
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return merchants.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_merchant_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof MerchantsAdapter.ViewHolder) {
            final MerchantsAdapter.ViewHolder p_holder = (MerchantsAdapter.ViewHolder) holder;
            final MerchantItem item = merchants.get(position);
            p_holder.bind(item);

            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.startStoreActivity(item);
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }


    @Override
    public int getItemCount() {
        return merchants == null ? 0 : merchants.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public String idMerchant;
        public ImageView image;
        public TextView text_bussiness_name;
        public TextView text_outstanding;
        public TextView text_shipping_cost;
        public TextView text_delivery_prom_time;

        public ViewHolder(View v) {
            super(v);
            idMerchant = "";
            image = v.findViewById(R.id.image);
            text_bussiness_name = v.findViewById(R.id.text_bussiness_name);
            text_outstanding = v.findViewById(R.id.text_outstanding);
            text_shipping_cost = v.findViewById(R.id.text_shipping_cost);
            text_delivery_prom_time = v.findViewById(R.id.text_delivery_prom_time);
        }
        public void bind(MerchantItem model) {
            final String ImagePath = model.getImagePath() + model.getImageName();
            idMerchant = String.valueOf(model.getIdMerchant());
            Glide.with(activity.getApplicationContext())
                    .load(ImagePath)
                    .into(image);
            text_bussiness_name.setText(model.getNameBussiness());
            text_outstanding.setText(model.getImportant());
            text_delivery_prom_time.setText(activity.getString(R.string.prompt_shipping_time, model.getTpoDelivery()));
            text_shipping_cost.setText(activity.getString(R.string.prompt_shipping_cost, StringOperations.getAmountFormat(model.getDeliveryCost(),id_country)));
        }
    }
    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }
}