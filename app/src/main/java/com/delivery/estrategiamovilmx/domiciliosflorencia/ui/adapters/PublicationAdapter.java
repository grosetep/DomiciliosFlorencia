package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.DetailPublicationActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.List;

/**
 * Created by administrator on 10/07/2017.
 */
public class PublicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<PublicationCardViewModel> publications;
    private Activity activity;
    private final int METHOD_LIKE = 2;

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int pastVisibleItems, visibleItemCount, totalItemCount;
    private RecyclerView recyclerview;
    private static final String TAG = PublicationAdapter.class.getSimpleName();
    private String id_country;
    private ProductsFragment fragment;

    public PublicationAdapter(Activity context, List<PublicationCardViewModel> itemList,RecyclerView list,ProductsFragment fr) {
        publications = itemList;
        this.activity = context;
        recyclerview = list;
        fragment = fr;
        id_country = ApplicationPreferences.getLocalStringPreference(activity,Constants.id_country);
        final StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) recyclerview.getLayoutManager();
        listener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);


                visibleItemCount = staggeredGridLayoutManager.getChildCount();//total de items
                totalItemCount = staggeredGridLayoutManager.getItemCount();

                int[] firstVisibleItems = null;
                firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                if(firstVisibleItems != null && firstVisibleItems.length > 0) {
                    pastVisibleItems = firstVisibleItems[0];//ultimos elementos visibles
                }
                if (dy>0) {
                    if (!isLoading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (mOnLoadMoreListener != null) {
                                mOnLoadMoreListener.onLoadMore();
                                isLoading = true;
                            }

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
                    .inflate(R.layout.card_view_layout, parent, false);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }else{
            View view = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PublicationAdapter.ViewHolder) {
            final PublicationAdapter.ViewHolder p_holder = (PublicationAdapter.ViewHolder) holder;
            final String ImagePath = publications.get(position).getPath();
            final String ImageName = publications.get(position).getImage();
            p_holder.mBoundString = publications.get(position).getIdProduct();
            p_holder.text_card_name.setText(publications.get(position).getProduct());

            Glide.with(p_holder.image_card_cover.getContext())
                    .load(ImagePath + ImageName)
                    .into(p_holder.image_card_cover);

            if (publications.get(position).getOfferPrice()!=null && !publications.get(position).getOfferPrice().isEmpty()) {//hay oferta, mostrar ambos precios
                p_holder.text_priceOff.setText( StringOperations.getStringWithA(StringOperations.getAmountFormat(publications.get(position).getOfferPrice(),id_country)));
                p_holder.text_price.setText(StringOperations.getStringWithDe(StringOperations.getAmountFormat(publications.get(position).getRegularPrice(),id_country)));
                p_holder.text_price.setVisibility(View.VISIBLE);
            }else{//no hay oferta: mostrar solo precio regular EN EL PRECIO DE LA OFERTA PARA QUE SALGA RESALTADO
                p_holder.text_priceOff.setText(StringOperations.getStringWithA(StringOperations.getAmountFormat(publications.get(position).getRegularPrice(),id_country)));
                p_holder.text_price.setVisibility(View.INVISIBLE);
            }

            //check if product is added

            p_holder.image_action_add.setVisibility(View.VISIBLE);
            p_holder.pbLoading.setVisibility(View.GONE);

            p_holder.image_action_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserItem user = GeneralFunctions.getCurrentUser(activity);
                    if (user!=null) {
                        if (fragment != null) {
                            //p_holder.image_action_add.setVisibility(View.GONE);
                            //p_holder.pbLoading.setVisibility(View.VISIBLE);
                            fragment.startAddToCartActivity(position, v);
                        }
                    }else{
                        Intent i = new Intent(activity,LoginActivity.class);
                        i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                        activity.startActivity(i);
                    }
                }
            });
            //p_holder.text_availability.setText(publications.get(position).getStock());

            int stock = 0;
            try{
                stock = Integer.parseInt(publications.get(position).getStock());
            }catch(NumberFormatException e){

            }
            if (stock <= 0) {
                p_holder.layout_over.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(null);
            } else {
                p_holder.layout_over.setVisibility(View.GONE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, DetailPublicationActivity.class);
                        intent.putExtra(DetailPublicationActivity.EXTRA_PRODUCT, p_holder.mBoundString);
                        intent.putExtra(DetailPublicationActivity.EXTRA_IMAGEPATH, ImagePath);
                        intent.putExtra(DetailPublicationActivity.EXTRA_IMAGENAME, ImageName);
                        intent.putExtra(DetailPublicationActivity.EXTRA_FLOW, ProductsFragment.FLOW_PRODUCTS);
                        context.startActivity(intent);
                    }
                });
            }


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
        public String mBoundString;
        public TextView text_card_name;
        public ImageView image_card_cover;
        //public TextView text_availability;
        public TextView text_priceOff;
        public TextView text_price;
        public ImageView image_action_add;
        public ProgressBar pbLoading;
        public RelativeLayout layout_over;


        public ViewHolder(View v) {
            super(v);
            layout_over = (RelativeLayout) v.findViewById(R.id.layout_over);
            text_card_name = (TextView) v.findViewById(R.id.text_card_name);
            image_card_cover = (ImageView) v.findViewById(R.id.image_card_cover);
            //text_availability = (TextView) v.findViewById(R.id.text_availability);
            text_priceOff = (TextView) v.findViewById(R.id.text_priceOff);
            text_price = (TextView) v.findViewById(R.id.text_price);
            image_action_add = (ImageView) v.findViewById(R.id.image_action_add);
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