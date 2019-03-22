package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.SearchFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.List;

public class LookForProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private List<PublicationCardViewModel> products;
    private SearchFragment fragment;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    private RecyclerView.OnScrollListener listener;
    private boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView recyclerview;
    private String id_country;
    private static final String TAG = LookForProductsAdapter.class.getSimpleName();

    public LookForProductsAdapter(List<PublicationCardViewModel> myDataset, Fragment frm, RecyclerView list) {
        products = myDataset;
        this.fragment = (SearchFragment) frm;
        recyclerview = list;
        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerview.getLayoutManager();
        id_country = ApplicationPreferences.getLocalStringPreference(fragment.getActivity(),Constants.id_country);

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
        products.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<PublicationCardViewModel> list) {
        if (list != null && list.size() > 0) {
            products.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    @Override
    public int getItemViewType(int position) {
        return products.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.layout_circle_image_text_item, parent, false);
            LookForProductsAdapter.ViewHolder vh = new LookForProductsAdapter.ViewHolder(v);
            return vh;
        } else {
            View view = LayoutInflater.from(fragment.getActivity()).inflate(R.layout.layout_loading_item, parent, false);
            return new LookForProductsAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PublicationAdapter.ViewHolder) {
            final PublicationAdapter.ViewHolder p_holder = (PublicationAdapter.ViewHolder) holder;
            final String ImagePath = products.get(position).getPath();
            final String ImageName = products.get(position).getImage();
            p_holder.mBoundString = products.get(position).getIdProduct();
            p_holder.text_card_name.setText(products.get(position).getProduct());

            Glide.with(p_holder.image_card_cover.getContext())
                    .load(ImagePath + ImageName)
                    .into(p_holder.image_card_cover);

            if (products.get(position).getOfferPrice()!=null && !products.get(position).getOfferPrice().isEmpty()) {//hay oferta, mostrar ambos precios
                p_holder.text_priceOff.setText( StringOperations.getStringWithA(StringOperations.getAmountFormat(products.get(position).getOfferPrice(),id_country)));
                p_holder.text_price.setText(StringOperations.getStringWithDe(StringOperations.getAmountFormat(products.get(position).getRegularPrice(),id_country)));
                p_holder.text_price.setVisibility(View.VISIBLE);
            }else{//no hay oferta: mostrar solo precio regular EN EL PRECIO DE LA OFERTA PARA QUE SALGA RESALTADO
                p_holder.text_priceOff.setText(StringOperations.getStringWithA(StringOperations.getAmountFormat(products.get(position).getRegularPrice(),id_country)));
                p_holder.text_price.setVisibility(View.INVISIBLE);
            }

            //check if product is added

            p_holder.image_action_add.setVisibility(View.VISIBLE);
            p_holder.pbLoading.setVisibility(View.GONE);

            p_holder.image_action_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserItem user = GeneralFunctions.getCurrentUser(fragment.getActivity());
                    if (user!=null) {
                        if (fragment != null) {
                            //p_holder.image_action_add.setVisibility(View.GONE);
                            //p_holder.pbLoading.setVisibility(View.VISIBLE);
                            //fragment.startAddToCartActivity(position, v);
                        }
                    }else{
                        Intent i = new Intent(fragment.getContext(),LoginActivity.class);
                        i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                        fragment.getActivity().startActivity(i);
                    }
                }
            });
            //p_holder.text_availability.setText(products.get(position).getStock());

            int stock = 0;
            try{
                stock = Integer.parseInt(products.get(position).getStock());
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


        } else if (holder instanceof PublicationAdapter.LoadingViewHolder) {
            PublicationAdapter.LoadingViewHolder loadingViewHolder = (PublicationAdapter.LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }

    }

    @Override
    public int getItemCount() {
        return products == null ? 0 : products.size();
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
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }
}