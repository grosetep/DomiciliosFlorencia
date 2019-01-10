package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.OrderItem;
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
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.OrdersAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class OrdersActivity extends AppCompatActivity {
    private static final String TAG = OrdersActivity.class.getSimpleName();
    private ArrayList<OrderItem> orders=new ArrayList();
    private FrameLayout container_loading;
    private RecyclerView recyclerview;
    private LinearLayoutManager llm;
    private RelativeLayout no_connection_layout;
    private AppCompatButton button_retry;
    private OrdersAdapter mAdapter;
    private UserItem user;
    private String previous_flow = "";
    public final boolean load_initial = true;
    private SwipeRefreshLayout swipeRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        init();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        previous_flow = intent.getStringExtra(Constants.flow);



        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            user = GeneralFunctions.getCurrentUser(getApplicationContext());
            Log.d(TAG,"Usuario activo:" + user.toString());
            if (user!=null) {
                switch (user.getProfile()){
                    case Constants.profile_admin:getSupportActionBar().setTitle(getString(R.string.title_activity_orders_admin));
                        break;
                    case Constants.profile_client:getSupportActionBar().setTitle(getString(R.string.title_activity_orders_client));
                        break;
                    case Constants.profile_deliver_man:getSupportActionBar().setTitle(getString(R.string.title_activity_orders_deliver_man));
                        break;

                }
                orders.clear();
                initProcess(true);
                boolean isRefresh = false;
                getOrders(Constants.cero, Constants.load_more_tax, load_initial,isRefresh);
            }else{
                finish();
                Intent i = new Intent(getBaseContext(),LoginActivity.class);
                startActivity(i);
            }
        }else{
            showNoConnectionLayout();
        }
    }
    private void init(){
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        llm = new LinearLayoutManager(OrdersActivity.this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recyclerview.getLayoutManager() == null) {
            recyclerview.setLayoutManager(llm);
        }
        recyclerview.setHasFixedSize(true);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
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
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orders.clear();
                initProcess(true);
                swipeRefresh.setRefreshing(false);
                boolean isRefresh = false;
                getOrders(Constants.cero, Constants.load_more_tax, true,isRefresh);
            }
        });
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
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GenericResponse orders_response = response.body();
                    if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
                        OrderItem order = orders.get(position);
                        order.setStatus(status_to_update);
                        order.setComment(comment);
                        mAdapter.notifyItemChanged(position);
                        //ShowConfirmations.showConfirmationMessage(orders_response.getResult().getMessage(), OrdersActivity.this );
                    } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        mAdapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
                    } else {
                        String response_error = response.message();
                        mAdapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
                    }
                } else {
                    mAdapter.notifyItemChanged(position);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), OrdersActivity.this);
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                //Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                try {
                    mAdapter.notifyItemChanged(position);
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, t.getMessage()), OrdersActivity.this);

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });

    }

    public void getOrderDetail(int position){
        Intent i = new Intent(getApplicationContext(),OrderDetailActivity.class);
        i.putExtra(OrderDetailActivity.ID_ORDER, orders.get(position));
        startActivity(i);
    }
    private void getOrders(int start, int end, boolean load_initial,boolean isRefresh){
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

    private void makeRequest(int start, int end,final  boolean load_initial,final boolean isRefresh){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        //get days configuration
        String days = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.days_to_show_orders);

        if (user!=null) {
            RestServiceWrapper.getOrders(user.getIdUser(),user.getProfile(), start, end , days.isEmpty()?Constants.days_to_show_orders_default:days,new Callback<GetOrdersResponse>() {
                @Override
                public void onResponse(Call<GetOrdersResponse> call, retrofit2.Response<GetOrdersResponse> response) {

                    if (response != null && response.isSuccessful()) {
                        if (isRefresh) {//only update list
                            processResponse(response,isRefresh);
                            swipeRefresh.setRefreshing(false);
                        }else if (load_initial) {
                            processResponseInit(response);
                        }else {
                            orders.remove(orders.size() - 1);//delete loading..
                            mAdapter.notifyItemRemoved(orders.size());
                            processResponse(response,isRefresh);
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
        }else{ShowConfirmations.showConfirmationMessage(getString(R.string.promt_error),OrdersActivity.this);}
    }
    private void processResponse(retrofit2.Response<GetOrdersResponse> response,boolean isRefresh){
        GetOrdersResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
            addNewElements(orders_response.getResult(), isRefresh);

        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
            Log.d(TAG, "Mensage:" + response_error);
        } else {
            String response_error = response.message();
            //ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
            Log.d(TAG, "Error:" + response_error);
        }


        notifyListChanged();

    }
    private void processResponseInit(retrofit2.Response<GetOrdersResponse> response){
        GetOrdersResponse orders_response = response.body();
        if (orders_response != null && orders_response.getStatus().equals(Constants.success)) {
            Log.d(TAG, "Respuesta:" + orders_response.getResult().size());
            if (orders_response.getResult().size() > 0) {
                orders.addAll(orders_response.getResult());
            }else{ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_no_data), OrdersActivity.this);}

        } else if (orders_response != null && orders_response.getStatus().equals(Constants.no_data)) {
            String response_error = response.body().getMessage();
            //ShowConfirmations.showConfirmationMessageShort(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
            Log.d(TAG, "Mensage:" + response_error);
        } else {
            String response_error = response.message();
            //ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, response_error), OrdersActivity.this);
            Log.d(TAG, "Error:" + response_error);
        }


        onSuccess();

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
    private void addNewElements(List<OrderItem> new_elements, boolean isRefresh){
        if (new_elements!=null && new_elements.size()>Constants.cero) {

            if (isRefresh){Log.d(TAG, "isRefresh..");
                orders.clear();
                orders.addAll(new_elements);
            }else{
                orders.addAll(GeneralFunctions.Filter(orders, new_elements));
            }
        }
    }
    private void onError(boolean isRefresh){
        if(isRefresh){
            swipeRefresh.setRefreshing(false);
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),OrdersActivity.this);
        }else if(!load_initial) {
            orders.remove(orders.size() - 1);//delete loading..
            mAdapter.notifyItemRemoved(orders.size());
            notifyListChanged();
            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),OrdersActivity.this);
        }else{showNoConnectionLayout();}
    }
    private void onSuccess(){
        initProcess(false);
        setupAdapter();
    }
    private void setupAdapter(){
        // Set the adapter
        if (recyclerview.getAdapter()==null) {
            mAdapter = new OrdersAdapter(OrdersActivity.this, orders, user, recyclerview);
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
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag?View.VISIBLE:View.GONE);
        recyclerview.setVisibility(flag?View.GONE:View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        recyclerview.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:
                orders.clear();
                initProcess(true);
                getOrders(Constants.cero, Constants.load_more_tax, load_initial,false);
                return true;
            case R.id.action_orders_config:
                filters_Popup(getApplicationContext(),OrdersActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (previous_flow.equals(CongratsActivity.flow_congrats) || previous_flow.equals(ReplyActivity.flow_notification)) {
            finish();
            NavUtils.navigateUpTo(OrdersActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }else{super.onBackPressed();}
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_orders, menu);
        return true;
    }
    private void filters_Popup(Context context, Activity activity) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View content = layoutInflaterAndroid.inflate(R.layout.filter_orders_layout, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
        fields.addView(content);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
        alertDialogBuilderUserInput.setView(mView);

        final EditText text_days = (EditText) mView.findViewById(R.id.text_days);
        final ProgressBar pbLoading_update = (ProgressBar) mView.findViewById(R.id.pbLoading_update);
        final TextView layout_text = (TextView) mView.findViewById(R.id.layout_text);
        final LinearLayout layout_filters = (LinearLayout) mView.findViewById(R.id.layout_filters);
        final TextView layout_text_error = (TextView) mView.findViewById(R.id.layout_text_error);
        //set custom user values saved
        String current_days = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.days_to_show_orders);
        text_days.setText(current_days.isEmpty()?Constants.days_to_show_orders_default:current_days);
        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(getResources().getString(R.string.promt_filter_orders_title));
        ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(ContextCompat.getColor(activity, R.color.colorPrimary));//primary
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(ContextCompat.getColor(activity, R.color.white_all));
        AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_filters);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                layout_text_error.setVisibility(View.GONE);
                if (text_days.getText().toString().trim().isEmpty()) {
                    text_days.setError(getString(R.string.error_field_required));
                    valid = false;
                } else {
                    text_days.setError(null);
                    valid = true;
                }
                if (valid) {
                    pbLoading_update.setVisibility(View.VISIBLE);
                    layout_text.setVisibility(View.GONE);
                    layout_filters.setVisibility(View.GONE);
                    v.setEnabled(false);
                    ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.days_to_show_orders, text_days.getText().toString().trim());
                    getOrdersFiltered(alertDialogAndroid,mView);
                }
            }
        });

    }
    private void getOrdersFiltered(final AlertDialog alert,final View mView){
        (mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
        alert.dismiss();
        orders.clear();
        initProcess(true);
        getOrders(Constants.cero, Constants.load_more_tax, load_initial, false);
    }
}
