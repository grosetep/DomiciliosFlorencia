package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.OfferFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.List;

/**
 * Created by administrator on 13/07/2017.
 */
public class OfferAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PublicationCardViewModel> publications;
    private Activity activity;
    private final int METHOD_LIKE = 2;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = OfferAdapter.class.getSimpleName();

    private OfferFragment fragment;



    public OfferAdapter(Activity context, List<PublicationCardViewModel> itemList,RecyclerView list,OfferFragment fr) {
        publications = itemList;
        this.activity = context;
        recyclerview = list;
        fragment = fr;

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
    public void clear(){ publications.clear();notifyDataSetChanged();}
    public void addAll(List<PublicationCardViewModel> list){
        if (list!=null && list.size()>0){publications.addAll(list);notifyDataSetChanged(); }
    }
    public void notifyListChanged(){
        notifyDataSetChanged();
    }
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return publications.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.offer_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OfferAdapter.ViewHolder) {
            final OfferAdapter.ViewHolder p_holder = (OfferAdapter.ViewHolder) holder;
            final String ImagePath = publications.get(position).getPath() + publications.get(position).getImage();
            Glide.with(p_holder.image_card_cover.getContext())
                    .load(ImagePath)
                    .into(p_holder.image_card_cover);
            p_holder.pbLoading.setVisibility(View.GONE);

            p_holder.layout_add_offer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserItem user = GeneralFunctions.getCurrentUser(activity);
                    if (user!=null) {
                        if (fragment != null) {
                            p_holder.pbLoading.setVisibility(View.VISIBLE);
                            fragment.addToCart(position, v);
                        }
                    }else{
                        Intent i = new Intent(activity,LoginActivity.class);
                        i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                        activity.startActivity(i);
                    }
                }
            });

        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return publications == null ? 0 : publications.size();
    }
    public void setLoaded() {
        isLoading = false;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_card_cover;
        public LinearLayout layout_add_offer;
        public ProgressBar pbLoading;

        public ViewHolder(View v) {
            super(v);
            image_card_cover = (ImageView) v.findViewById(R.id.image_card_cover);
            layout_add_offer = (LinearLayout) v.findViewById(R.id.layout_add_offer);
            pbLoading = (ProgressBar) v.findViewById(R.id.pbLoading);
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