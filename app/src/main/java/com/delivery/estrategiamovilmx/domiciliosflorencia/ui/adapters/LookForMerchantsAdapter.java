package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.support.v4.app.Fragment;
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
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.SearchFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.List;

public class LookForMerchantsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<MerchantItem> merchants;
    private SearchFragment fragment;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = LookForMerchantsAdapter.class.getSimpleName();

    public LookForMerchantsAdapter(List<MerchantItem> myDataset, Fragment frm, RecyclerView list) {
        merchants = myDataset;
        this.fragment = (SearchFragment) frm;
        recyclerview = list;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();
        listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();


                if (dy > 0) {
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

    public void clear() {
        merchants.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<MerchantItem> list) {
        if (list != null && list.size() > 0) {
            merchants.addAll(list);
            notifyDataSetChanged();
        }
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
                    .inflate(R.layout.layout_circle_image_text_item, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof LookForMerchantsAdapter.ViewHolder) {
            final LookForMerchantsAdapter.ViewHolder p_holder = (LookForMerchantsAdapter.ViewHolder) holder;
            final MerchantItem item = merchants.get(position);
            p_holder.bind(item);

            p_holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment.startStoreActivity(item);
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
        public ImageView avatar;
        public TextView text;


        public ViewHolder(View v) {
            super(v);
            idMerchant = "";
            avatar = v.findViewById(R.id.circle_image);
            text = v.findViewById(R.id.text);
        }

        public void bind(MerchantItem model) {
            final String ImagePath = model.getImagePath() + model.getImageName();
            idMerchant = String.valueOf(model.getIdMerchant());
            Glide.with(fragment.getContext())
                    .load(ImagePath)
                    .into(avatar);
            String name = model.getNameBussiness();
            if (name.length()>11) {name = model.getNameBussiness().substring(0,8).concat("...");}
            text.setText(name);

        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }
}