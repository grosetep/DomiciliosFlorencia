package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderPurchaseItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ChangeStatusOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetOrdersPurchaseResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.OrderDetailActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.OrderDetailPurchaseActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.PendingOrdersPurchaseAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrdersPurchaseFragment extends Fragment {
    private static final String TAG = PendingOrdersFragment.class.getSimpleName();
    private ArrayList<OrderPurchaseItem> orders=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private AppCompatButton button_retry;
    private PendingOrdersPurchaseAdapter mAdapter;
    private UserItem user;
    private static final String TYPE_QUERY = "query";
    private String type_query = "";
    //
    public final boolean load_initial = true;
    private SwipeRefreshLayout swipeRefresh;

    public PendingOrdersPurchaseFragment() {
        // Required empty public constructor
    }
    public static PendingOrdersPurchaseFragment newInstance(String typeQuery) {
        PendingOrdersPurchaseFragment fragment = new PendingOrdersPurchaseFragment();
        Bundle args = new Bundle();
        args.putString(TYPE_QUERY,typeQuery);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            type_query = (String) getArguments().get(TYPE_QUERY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_pending_orders_purchase, container, false);
        init(v);

        if (Connectivity.isNetworkAvailable(getContext())) {
            user = GeneralFunctions.getCurrentUser(getContext());

            if (user!=null) {
                orders.clear();
                initProcess(true);
                boolean isRefresh = false;
                getOrders(Constants.cero, Constants.load_more_tax, load_initial,isRefresh);
            }else{
                getActivity().finish();
                Intent i = new Intent(getContext(),LoginActivity.class);
                startActivity(i);
            }
        }else{
            showNoConnectionLayout();
        }
        return v;
    }
    public void reloadFragment(){
        orders.clear();
        initProcess(true);
        boolean isRefresh = false;
        getOrders(Constants.cero, Constants.load_more_tax, load_initial, isRefresh);
    }
    private void init(View v){
        container_loading = (FrameLayout) v.findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) v.findViewById(R.id.no_connection_layout);
        recyclerview = (RecyclerView) v.findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recyclerview.getLayoutManager() == null) {
            recyclerview.setLayoutManager(llm);
        }
        recyclerview.setHasFixedSize(true);
        swipeRefresh = (SwipeRefreshLayout) v.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {

                        getOrders(Constants.cero, Constants.load_more_tax, false, true);

                    }
                }
        );
        button_retry = (AppCompatButton) v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.clear();
                initProcess(true);
                swipeRefresh.setRefreshing(false);
                boolean isRefresh = false;
                getOrders(Constants.cero, Constants.load_more_tax, true, isRefresh);
            }
        });
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag? View.VISIBLE: View.GONE);
        recyclerview.setVisibility(flag? View.GONE: View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    private void makeRequest(int start, int end,final  boolean load_initial,final boolean isRefresh){
        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        //get days configuration
        String days = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.days_to_show_orders);

        if (user!=null) {
            RestServiceWrapper.getDeliverManOrdersPurchase(user.getIdUser(), user.getProfile(), type_query, start, end, days.isEmpty() ? Constants.days_to_show_orders_default : days, new Callback<GetOrdersPurchaseResponse>() {
                @Override
                public void onResponse(Call<GetOrdersPurchaseResponse> call, retrofit2.Response<GetOrdersPurchaseResponse> response) {

                    if (response != null && response.isSuccessful()) {
                        if (isRefresh) {//only update list
                            processResponse(response, isRefresh);
                            swipeRefresh.setRefreshing(false);
                        } else if (load_initial) {
                            processResponseInit(response);
                        } else {
                            orders.remove(orders.size() - 1);//delete loading..
                            mAdapter.notifyItemRemoved(orders.size());
                            processResponse(response, isRefresh);
                        }
                    } else {
                        onError(isRefresh);
                        //ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                    }
                }

                @Override
                public void onFailure(Call<GetOrdersPurchaseResponse> call, Throwable t) {
                    Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                    onError(isRefresh);
                }
            });
        }else{ShowConfirmations.showConfirmationMessage(getString(R.string.promt_error),getActivity());}
    }
    private void processResponse(retrofit2.Response<GetOrdersPurchaseResponse> response,boolean isRefresh){
        Log.d(TAG,"processResponse.....");
        GetOrdersPurchaseResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
            addNewElements(orders_response.getResult(), isRefresh);

        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Mensage:" + response_error);
        } else {
            String response_error = response.message();
            ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Error:" + response_error);
        }


        notifyListChanged();

    }
    private void addNewElements(List<OrderPurchaseItem> new_elements, boolean isRefresh){
        if (new_elements!=null && new_elements.size()>Constants.cero) {

            if (isRefresh){
                orders.clear();
                orders.addAll(new_elements);
            }else{
                orders.addAll(GeneralFunctions.FilterPurchases(orders, new_elements));
            }
        }
    }
    private void processResponseInit(retrofit2.Response<GetOrdersPurchaseResponse> response){
        GetOrdersPurchaseResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
            Log.d(TAG,"Pedidos:");
            for(OrderPurchaseItem o:orders_response.getResult()){
                Log.d(TAG,o.toString());
            }
            if (orders_response.getResult().size() > 0) {
                orders.addAll(orders_response.getResult());
            }else{
                //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_no_data), getActivity());
            }

        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Mensage:" + response_error);
        } else {
            String response_error = response.message();
            ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Error:" + response_error);
        }


        onSuccess();

    }
    private void onError(boolean isRefresh){
        if(isRefresh){
            swipeRefresh.setRefreshing(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
        }else if(!load_initial) {
            orders.remove(orders.size() - 1);//delete loading..
            mAdapter.notifyItemRemoved(orders.size());
            notifyListChanged();
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
        }else{showNoConnectionLayout();}
    }
    private void notifyListChanged() {
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
                mAdapter.setLoaded();
            }
        }, Constants.cero);
    }
    private void getOrders(int start, int end, boolean load_initial,boolean isRefresh){
        Log.d(TAG, "3......." );
        Log.d(TAG,"getOrders.......load_initial:"+load_initial+"   isRefresh:"+isRefresh);
        if (isRefresh) {
            makeRequest(start,end,load_initial,isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(start,end);
        } else {
            makeRequest(start,end, load_initial,isRefresh);
        }
    }
    private void addLoadingAndMakeRequest(final int start, final int end) {

        final boolean isRefresh = false;
        final boolean first_load= false;

        orders.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(orders.size() - 1);
                makeRequest(start, end, first_load,isRefresh);
            }
        };
        handler.post(r);

    }
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }
    private void setupAdapter(){
        // Set the adapter
        if (recyclerview.getAdapter()==null) {
            mAdapter = new PendingOrdersPurchaseAdapter(getActivity(),orders,user,this,recyclerview);
            recyclerview.setAdapter(mAdapter);
            recyclerview.invalidate();
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh.isRefreshing()) {
                        int index = orders != null ? (orders.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax_extended;
                        getOrders(index, end, false, false);
                    }
                }
            });
        }else{notifyListChanged();}
    }
    public void getOrderDetail(int position){
        Intent i = new Intent(getContext(),OrderDetailPurchaseActivity.class);
        i.putExtra(OrderDetailActivity.ID_ORDER,orders.get(position));
        startActivity(i);
    }
    public void changeStatusOrder(final int position, final String status_to_update, final String comment){
        ChangeStatusOrderRequest request = new ChangeStatusOrderRequest();
        request.setIdUser(user.getIdUser());
        request.setIdOrder(orders.get(position).getIdOrder());
        request.setStatusToUpdate(status_to_update);
        request.setComment(comment);

        //id user, quien hace el cambio, agregar column id_admin en pedidos
        //status_to_update , a que estatus se debe actualizar
        //id order, cual pedido se debe actualizar
        //comment, si existe algun comentario al realizar la actuaizacion
        RestServiceWrapper.ChangeStatusOrder(request, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {

                if (response != null && response.isSuccessful()) {
                    GenericResponse orders_response = response.body();
                    if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
                        OrderPurchaseItem order = orders.get(position);
                        order.setStatus(status_to_update);
                        order.setComment(comment);
                        mAdapter.notifyItemChanged(position);
                    } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {//error controlado
                        String response_error = response.body().getResult().getMessage();
                        mAdapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
                    } else {// otro error
                        String response_error = response.message();
                        mAdapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
                    }
                } else {
                    mAdapter.notifyItemChanged(position);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                //Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                try {
                    mAdapter.notifyItemChanged(position);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,t.getMessage()), getActivity());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });

    }
}
