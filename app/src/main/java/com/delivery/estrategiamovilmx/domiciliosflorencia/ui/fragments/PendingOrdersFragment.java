package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderOnList;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.ChangeStatusOrderRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetOrdersResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UploadImage;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.OrderDetailActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.OrdersDeliverActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.SignatureActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.PendingOrdersAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class PendingOrdersFragment extends Fragment {
    private static final String TAG = PendingOrdersFragment.class.getSimpleName();
    private ArrayList<OrderItem> orders=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private AppCompatButton button_retry;
    private PendingOrdersAdapter mAdapter;
    private UserItem user;
    private static final String TYPE_QUERY = "query";
    private static final int GET_SIGNATURE = 0;
    public static final String ORDER_ELEMENT_ON_LIST = "order_item";
    private String type_query = "";
    //
    public final boolean load_initial = true;
    private SwipeRefreshLayout swipeRefresh;

    private Timer timer = null;

    public PendingOrdersFragment() {
        // Required empty public constructor
    }
    public static PendingOrdersFragment newInstance(String typeQuery) {
        PendingOrdersFragment fragment = new PendingOrdersFragment();
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

        View v =  inflater.inflate(R.layout.fragment_pending_orders, container, false);
        init(v);

        if (Connectivity.isNetworkAvailable(getContext())) {
            user = GeneralFunctions.getCurrentUser(getContext());

            if (user!=null) {
                orders.clear();
                initProcess(true);
                boolean isRefresh = false;
                boolean is_load_more = false;
                getOrders(Constants.cero, Constants.load_more_tax, load_initial,isRefresh,is_load_more);
            }else{
                getActivity().finish();
                Intent intent = new Intent(getContext(),LoginActivity.class);
                intent.putExtra(Constants.flow, MainActivity.flow_no_registered);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
        boolean is_load_more = false;
        getOrders(Constants.cero, Constants.load_more_tax, load_initial, isRefresh, is_load_more);
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
                        boolean is_load_more = false;
                        getOrders(Constants.cero, Constants.load_more_tax, false, true, is_load_more);

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
                boolean is_load_more = false;
                getOrders(Constants.cero, Constants.load_more_tax, true, isRefresh, is_load_more);
            }
        });
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        recyclerview.setVisibility(flag?View.GONE:View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    private void makeRequest(int start, int end,final  boolean load_initial,final boolean isRefresh, final boolean is_load_more){
        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        //get days configuration
        String days = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.days_to_show_orders);

        if (user!=null) {
            //validating query to execute based on profile user
            //if (user.getProfile().equals(Constants.profile_deliver_man)) {
                Log.d(TAG,"getOrders...................type_query:"+type_query + " perfil:"+user.getProfile());
                RestServiceWrapper.getDeliverManOrders(user.getIdUser(), user.getProfile(), type_query, start, end, days.isEmpty() ? Constants.days_to_show_orders_default : days, new Callback<GetOrdersResponse>() {
                    @Override
                    public void onResponse(Call<GetOrdersResponse> call, retrofit2.Response<GetOrdersResponse> response) {

                        if (response != null && response.isSuccessful()) {
                            if (isRefresh) {//only update list
                                processResponse(response, isRefresh,is_load_more);
                                swipeRefresh.setRefreshing(false);
                            } else if (load_initial) {
                                processResponseInit(response);
                            } else {
                                orders.remove(orders.size() - 1);//delete loading..
                                mAdapter.notifyItemRemoved(orders.size());
                                processResponse(response, isRefresh,is_load_more);
                            }
                        } else {
                            onError(isRefresh);
                            //ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), getActivity());
                        }
                    }

                    @Override
                    public void onFailure(Call<GetOrdersResponse> call, Throwable t) {
                        Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                        onError(isRefresh);
                    }
                });
            /*}else{
                Log.d(TAG,"getOrders...................");
                RestServiceWrapper.getOrders(user.getIdUser(),user.getProfile(), start, end , days.isEmpty()?Constants.days_to_show_orders_default:days,new Callback<GetOrdersResponse>() {
                    @Override
                    public void onResponse(Call<GetOrdersResponse> call, retrofit2.Response<GetOrdersResponse> response) {

                        if (response != null && response.isSuccessful()) {
                            if (isRefresh) {//only update list
                                processResponse(response,isRefresh,is_load_more);
                                swipeRefresh.setRefreshing(false);
                            }else if (load_initial) {
                                processResponseInit(response);
                            }else {
                                orders.remove(orders.size() - 1);//delete loading..
                                mAdapter.notifyItemRemoved(orders.size());
                                processResponse(response,isRefresh,is_load_more);
                            }
                        } else {
                            Log.d(TAG, "ERROR: " );
                            onError(isRefresh);
                            //ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), OrdersActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<GetOrdersResponse> call, Throwable t) {
                        Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                        onError(isRefresh);
                    }
                });
            }*/
            }else{ShowConfirmations.showConfirmationMessage(getString(R.string.promt_error),getActivity());}
    }
    private void processResponse(retrofit2.Response<GetOrdersResponse> response,boolean isRefresh, boolean is_load_more){
        GetOrdersResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {Log.d(TAG,"processResponse....CASO 1");
            addNewElements(orders_response.getResult(), isRefresh);

        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {Log.d(TAG,"processResponse....CASO 2 NO DATA");
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Mensage:" + response_error);
            //no agrega elementos pero si refresca las existentes por si alguno ha cambiado de estatus cuando es refresh
            if (!is_load_more) orders.clear();

        } else {Log.d(TAG,"processResponse....CASO 3 ERROR");
            String response_error = response.message();
            ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Error:" + response_error);
        }

        Log.d(TAG,"notifyListChanged INVOKE");
        notifyListChanged();

    }
    private void addNewElements(List<OrderItem> new_elements, boolean isRefresh){
        if (new_elements!=null && new_elements.size()>Constants.cero) {

            if (isRefresh){Log.d(TAG,"isRefresh");
                orders.clear();
                orders.addAll(new_elements);
            }else{Log.d(TAG,"aplica Filter");
                orders.addAll(GeneralFunctions.Filter(orders, new_elements));
            }
        }
    }
    private void processResponseInit(retrofit2.Response<GetOrdersResponse> response){
        Log.d(TAG,"processResponseInit...");
        GetOrdersResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
            Log.d(TAG,"caso 1...");
            if (orders_response.getResult().size() > 0) {
                orders.addAll(orders_response.getResult());
            }
            for (OrderItem o:orders_response.getResult()){Log.d(TAG,""+o.toString());}
        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
            Log.d(TAG,"caso 2...");
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Mensage:" + response_error);
        } else {Log.d(TAG,"caso error...");
            String response_error = response.message();
            ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), getActivity());
            Log.d(TAG, "Error:" + response_error);
        }

        Log.d(TAG,"invoke onSuccess...");
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
    private void getOrders(int start, int end, boolean load_initial,boolean isRefresh, boolean is_load_more){
        if (isRefresh) {
            makeRequest(start,end,load_initial,isRefresh,is_load_more);
        }else if (!load_initial) {//load more
            addLoadingAndMakeRequest(start,end,is_load_more);
        } else {//carga inicial
            makeRequest(start,end, load_initial,isRefresh,is_load_more);
        }
    }
    private void addLoadingAndMakeRequest(final int start, final int end,final boolean is_load_more) {

        final boolean isRefresh = false;
        final boolean first_load= false;

        orders.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                mAdapter.notifyItemInserted(orders.size() - 1);
                makeRequest(start, end, first_load,isRefresh,is_load_more);
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
        Log.d(TAG, "setupAdapter...");
        if (recyclerview.getAdapter()==null) {
        mAdapter = new PendingOrdersAdapter(getActivity(),orders,user,this,recyclerview);
        recyclerview.setAdapter(mAdapter);
        recyclerview.invalidate();
            mAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh.isRefreshing()) {
                        int index = orders != null ? (orders.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax_extended;
                        boolean is_load_more = true;
                        getOrders(index, end, false, false,is_load_more);
                    }
                }
            });
        }else{notifyListChanged();}
    }
    public void getOrderDetail(int position){
        Intent i = new Intent(getContext(),OrderDetailActivity.class);
        i.putExtra(OrderDetailActivity.ID_ORDER,orders.get(position));
        startActivity(i);
    }
    public void getSignature(int position){
        Intent get_signature = new Intent(getActivity(), SignatureActivity.class);
        get_signature.putExtra(ORDER_ELEMENT_ON_LIST, position);
        startActivityForResult(get_signature, GET_SIGNATURE);
    }

    /*funciones para subir la firma a la base*/
    public void uploadSignature(int position, ImageView image_signature,String status_to_update, String comment){
        UserItem user = GeneralFunctions.getCurrentUser(getActivity());
        String nameImageToServer =  orders.get(position).getIdOrder().concat("_").concat(user.getIdUser()).concat(new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()))+ ".png";
        if (orders.get(position).getSignatureImageLocalPath()!=null) { //repartidor obtuvo la firma de cliente
            Bitmap bitmap = BitmapFactory.decodeFile(orders.get(position).getSignatureImageLocalPath());
            UploadImage.uploadImageSignature(getActivity(), nameImageToServer, user, bitmap, orders.get(position).getIdOrder());
            start(new OrderOnList(position, status_to_update,comment));
        }
    }
    public void start(OrderOnList element) {
        if (timer != null) {
            timer.cancel();
        }

        startChekingUploading(element);
    }
    public void stop() {

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startChekingUploading(final OrderOnList element_on_list) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if (UploadImage.ready == 1 && UploadImage.error == 0) {
                            stop();
                            //update database
                            //updateImageProfileRoute();

                            ShowConfirmations.showConfirmationMessage(getString(R.string.promt_signature_ok), getActivity());
                            OrdersDeliverActivity.SIGNATURE_TAKED = 1;
                            changeStatusOrder(element_on_list.getPosition(), element_on_list.getStatusToUpdate(), element_on_list.getComment());
                        } else if (UploadImage.ready == 1 && UploadImage.error == 1) {// hubo errores, reintentar...

                            stop();
                            ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_error), getActivity());
                            mAdapter.notifyItemChanged(element_on_list.getPosition());
                        }

                    }
                });


            }

        }, 0, Constants.DURATION);
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
                        OrderItem order = orders.get(position);
                        order.setStatus(status_to_update);
                        order.setComment(comment);
                        mAdapter.notifyItemChanged(position);
                    } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        mAdapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), getActivity());
                    } else {
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
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, t.getMessage()), getActivity());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });

    }
    public void setSignatureToOrder(int orderOnList,String imagePath){
        //bitmap listo para subir y solo se muestra la imagen guardada en el paso anterior
        OrderItem order = orders.get(orderOnList);
        order.setSignatureImageLocalPath(imagePath);
        mAdapter.notifyItemChanged(orderOnList);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case GET_SIGNATURE:
                int element = 0;
                if (resultCode == Activity.RESULT_OK) {
                    element = (int)  data.getSerializableExtra(ORDER_ELEMENT_ON_LIST);
                    String image_path = (String) data.getStringExtra(SignatureActivity.IMAGE_PATH);
                    //setear imagen en el elemento y despues actualizar estatus
                    setSignatureToOrder(element,image_path);
                }else{
                    mAdapter.notifyItemChanged(element);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.promt_no_signature),getActivity());
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }


}
