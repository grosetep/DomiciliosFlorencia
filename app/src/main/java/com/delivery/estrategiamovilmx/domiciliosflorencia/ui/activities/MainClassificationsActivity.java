package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ClassificationItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ShippingAddress;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.ClassificationsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.MerchantsByServiceResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.ClassificationsAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.MerchantsAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.BottomSheetFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PrincipalMenuFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;

public class MainClassificationsActivity extends AppCompatActivity {
    private String type_service;
    private RecyclerView merchants_recyclerview;
    private SwipeRefreshLayout swipeRefresh_merchants;
    private ArrayList<ClassificationItem> clasifications = new ArrayList<>();
    private ArrayList<MerchantItem> merchants = new ArrayList<>();
    private LinearLayoutManager llm;
    private AppCompatButton button_retry_search;
    private AppCompatButton button_retry;
    private ImageButton button_select_filters;

    public final boolean load_initial = true;
    private MerchantsAdapter adapter;
    private ClassificationsAdapter adapterClassifications;
    private TextView text_number_elements;
    /*campos que desaparecen al cargar*/
    private TextView text_service;
    private RecyclerView classifications_recycler;
    private LinearLayout layout_searching_content;
    private NestedScrollView layout_content;
    /*campos para manejo de errores*/
    private RelativeLayout layout_no_publications;
    private RelativeLayout no_connection_layout;
    private RelativeLayout pbLoading_products;
    private View mCustomView;
    private int classification_position_selected = -1;
    private static final String TAG = MainClassificationsActivity.class.getSimpleName();

    //texto direccion favorita
    private TextView text_shipping_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_clasifications);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0, 8);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*custom toolbar, show selected user shipping address*/
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM | getSupportActionBar().DISPLAY_SHOW_HOME
                        | getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_change_address_action, null);

        text_shipping_address = mCustomView.findViewById(R.id.text_shipping_address);

        String json_address_favorite = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.FAVORITE_ADDRESS_SELECTED);
        if (json_address_favorite!=null && !json_address_favorite.isEmpty()){//establece direccion favorita si ya existe una
            Gson gson = new Gson();
            ShippingAddress ship = gson.fromJson(json_address_favorite,ShippingAddress.class);
            if (ship!=null){text_shipping_address.setText(ship.getAddressResumed()); Log.d(TAG,"ASIGNA DIRECION:"+ship.getAddressResumed());}
        }

        mCustomView.findViewById(R.id.layout_change_address).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showBottomSheetDialogFragment();

                    }
                });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);


        type_service = getIntent().getStringExtra(PrincipalMenuFragment.BUSINESS_AREA);
        init();
        text_service.setText(GeneralFunctions.getTitleByServiceSelected(type_service,MainClassificationsActivity.this));

        if (Connectivity.isNetworkAvailable(getApplicationContext())) {
            clasifications.clear();
            merchants.clear();
            loadingView();
            getClassificationsByService( Constants.cero, Constants.load_more_tax_extended, load_initial,false);
        } else {
            updateUI(true);
        }
    }


    private void loadingView(){
        text_service.setVisibility(View.GONE);
        layout_searching_content.setVisibility(View.GONE);
        classifications_recycler.setVisibility(View.GONE);
        layout_content.setVisibility(View.INVISIBLE);
        no_connection_layout.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_products.setVisibility(View.VISIBLE);
    }
    private void loadingViewMerchants(){
        layout_content.setVisibility(View.INVISIBLE);
        no_connection_layout.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_products.setVisibility(View.VISIBLE);
    }
    public void init(){
        /*lista de clasificaciones*/
        classifications_recycler = findViewById(R.id.classifications_recycler);
        classifications_recycler.setItemAnimator(new DefaultItemAnimator());
        classifications_recycler.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (classifications_recycler.getLayoutManager() == null) {
            classifications_recycler.setLayoutManager(llm);
        }

        /*lista de establecimientos*/

        merchants_recyclerview = findViewById(R.id.merchants_recyclerview);
        merchants_recyclerview.setItemAnimator(new DefaultItemAnimator());
        merchants_recyclerview.setHasFixedSize(true);

        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (merchants_recyclerview.getLayoutManager() == null) {
            merchants_recyclerview.setLayoutManager(llm);
        }

        swipeRefresh_merchants = findViewById(R.id.swipeRefresh_merchants);
        swipeRefresh_merchants.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_merchants.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        if (getClassificationSelected() != -1) {
                            processRequestGetMerchantsByService(type_service, getClassificationSelected() != -1 ? clasifications.get(getClassificationSelected()) : null, Constants.cero, Constants.load_more_tax, false, true);
                        } else {
                            processRequestGetMerchantsByService(type_service, getClassificationSelected() != -1 ? clasifications.get(getClassificationSelected()) : null, Constants.cero, Constants.load_more_tax, load_initial, true);
                        }
                    }
                }
        );
        text_service = findViewById(R.id.text_service);
        layout_searching_content = findViewById(R.id.layout_searching_content);
        layout_searching_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i  = new Intent(MainClassificationsActivity.this,SearchActivity.class);
                i.putExtra(Constants.TYPE_SERVICE,type_service);
                startActivity(i);

            }
        });
        layout_content = findViewById(R.id.layout_content);
        button_select_filters = findViewById(R.id.button_select_filters);

        //Boton Reintento: Sin resultados
        button_retry_search = findViewById(R.id.button_retry_search);
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isNetworkAvailable(getApplicationContext())) {
                    getMerchantsByClassifitacion(getClassificationSelected());
                }else{updateUI(true);}
            }
        });
        //Boton Reintento: Error de conexion
        button_retry = findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isNetworkAvailable(getApplicationContext())) {
                    clasifications.clear();
                    merchants.clear();
                    loadingView();
                    getClassificationsByService(Constants.cero, Constants.load_more_tax_extended, load_initial, false);
                }else{updateUI(true);}
            }
        });
        no_connection_layout = findViewById(R.id.no_connection_layout);
        layout_no_publications = findViewById(R.id.layout_no_publications);
        pbLoading_products = findViewById(R.id.pbLoading_products);
        text_number_elements = findViewById(R.id.text_number_elements);
    }

    private void getClassificationsByService(  final int start, final int end, final boolean load_initial, final boolean isRefresh){

        RestServiceWrapper.getClassificationsByService(type_service, new Callback<ClassificationsResponse>() {
            @Override
            public void onResponse(Call<ClassificationsResponse> call, retrofit2.Response<ClassificationsResponse> response) {
                // Log.d(TAG, "Respuesta getClassificationsByService: " + response);
                if (response != null && response.isSuccessful()) {
                    ClassificationsResponse cl_response = response.body();

                    if (cl_response != null && cl_response.getStatus().equals(Constants.success)) {//procesa respuesta y almacena en lista para proceder a segundo llamado
                        //onSuccess(login_response);
                        clasifications = new ArrayList(cl_response.getResult());
                        /*for (int i = 0; i < clasifications.size(); i++) {
                            Log.d(TAG, clasifications.get(i).toString());
                        }*/
                        setupAdapterClassifications();
                        processRequestGetMerchantsByService(type_service, getClassificationSelected() != -1 ? clasifications.get(getClassificationSelected()) : null, start, end, load_initial, isRefresh);
                    } else if (cl_response != null && cl_response.getStatus().equals(Constants.no_data)) {
                        String response_error = cl_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    } else {
                        String response_error = cl_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<ClassificationsResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });

    }
    private void addLoadingAndMakeRequest(final String type_service,final ClassificationItem classifitetionItem,final int start,final int end) {

        final boolean isRefresh = false;
        final boolean first_load= false;

        merchants.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(merchants.size() - 1);
                getMerchantsByService(type_service, classifitetionItem,start,end,first_load, isRefresh);
            }
        };
        handler.post(r);

    }
    private void processRequestGetMerchantsByService(String type_service,ClassificationItem classifitetionItem,int start, int end, boolean load_initial, boolean isRefresh){
        // Log.d(TAG, "processRequestGetMerchantsByService....load_initial:" + load_initial);
        if (isRefresh) {
            getMerchantsByService(type_service, classifitetionItem,start,end, load_initial, isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(type_service, classifitetionItem,start,end);
        } else {
            getMerchantsByService(type_service, classifitetionItem,start,end, load_initial, isRefresh);
        }
    }

    private void processingResponseInit(ArrayList<MerchantItem> new_merchants) {//mostrar clasificaciones y cargar merchants por primera vez, en la siguiente solo actualizar merchants.
        // Log.d(TAG, "processingResponse merchants initial....." + new_merchants.size());

        if (new_merchants != null && new_merchants.size() > 0) {
            merchants.addAll(new_merchants);
        }

        setupAdapter();
        updateUI(false);

    }
    private void setupAdapter(){
        if (merchants_recyclerview.getAdapter()==null) {
            adapter = new MerchantsAdapter(merchants, this, merchants_recyclerview);
            merchants_recyclerview.setAdapter(adapter);
            //load more functionallity
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_merchants.isRefreshing()) {
                        int index = merchants != null ? (merchants.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        processRequestGetMerchantsByService(type_service,getClassificationSelected() != -1 ? clasifications.get(getClassificationSelected()) : null, index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    private void processingResponse(ArrayList<MerchantItem> new_merchants,boolean isRefresh) {

        addNewElements(new_merchants, isRefresh);

        notifyListChanged();

    }
    private void addNewElements(ArrayList<MerchantItem> new_merchants,boolean isRefresh){
        if (new_merchants!=null && new_merchants.size()>Constants.cero) {

            if (isRefresh){
                merchants.clear();
                merchants.addAll(new_merchants);
            }else{
                merchants.addAll(GeneralFunctions.FilterMerchants(merchants, new_merchants));
            }
        }
    }
    private void notifyListChanged() {
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }, Constants.cero);
    }
    private void notifyListChangedClassifications() {
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }, Constants.cero);
    }

    /*private String getTitleByServiceSelected(String type_service){
        String service_name = "";
        switch (type_service) {
            case Constants.service_supers:
                service_name = getString(R.string.title_super);
                break;
            case Constants.service_restaurants:
                service_name = getString(R.string.title_restaurants);
                break;
            case Constants.service_custom:
                service_name = getString(R.string.title_anything);
                break;
            case Constants.service_mototaxi:
                service_name = getString(R.string.title_mototaxi);
                break;
            case Constants.service_drugstore:
                service_name = getString(R.string.title_farmacy);
                break;
        }
        return service_name;
    }*/
    private void getMerchantsByService( final String type_service,ClassificationItem classifitetionItem,int start, int end, final boolean load_initial, final boolean isRefresh){
        //Log.d(TAG, "getMerchantsByService load_initial: "+load_initial);
        String id_country = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.id_country);

        RestServiceWrapper.getMerchantsByService(type_service, classifitetionItem != null ? classifitetionItem.getClassificationKey() : Constants.get_all, id_country, start, end, new Callback<MerchantsByServiceResponse>() {
            @Override
            public void onResponse(Call<MerchantsByServiceResponse> call, retrofit2.Response<MerchantsByServiceResponse> response) {

                if (response != null && response.isSuccessful()) {
                    MerchantsByServiceResponse cl_response = response.body();

                    if (cl_response != null && cl_response.getStatus().equals(Constants.success)) {//procesa respuesta y almacena en lista para proceder a segundo llamado
                        //onSuccess(login_response);
                        ArrayList<MerchantItem> new_merchants = new ArrayList(cl_response.getResult());
                        //setting total merchants
                        text_number_elements.setText(getString(R.string.title_total_elements, cl_response.getTotal(), GeneralFunctions.getTitleByServiceSelected(type_service,MainClassificationsActivity.this)));

                        Log.d(TAG,"--------------------------- COMERCIOS-----------------------------");
                        for(MerchantItem m:new_merchants){
                            Log.d(TAG,""+m.toString());
                        }

                        if (isRefresh) {//only update list
                            processingResponse(new_merchants, isRefresh);
                            swipeRefresh_merchants.setRefreshing(false);
                        } else if (load_initial) {
                            processingResponseInit(new_merchants);
                        } else {
                            merchants.remove(merchants.size() - 1);//delete loading..
                            adapter.notifyItemRemoved(merchants.size());
                            processingResponse(new_merchants, isRefresh);
                        }
                    } else if (cl_response != null && cl_response.getStatus().equals(Constants.no_data)) {
                        if (isRefresh) {
                            swipeRefresh_merchants.setRefreshing(false);
                            updateUI(false);
                        } else if (!load_initial) {//laod more
                            merchants.remove(merchants.size() - 1);//delete loading..
                            adapter.notifyItemRemoved(merchants.size());
                            notifyListChanged();
                        } else {//carga inicial
                            updateUI(false);
                        }
                    } else {
                        String response_error = cl_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<MerchantsByServiceResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });

    }

    private void onError(String err){
        ShowConfirmations.showConfirmationMessage(err, this);
    }

    public ArrayList<MerchantItem> getMerchants() {
        return merchants;
    }

    private void updateUI(final boolean connection_error){
        runOnUiThread(new
                              Runnable() {
                                  @Override
                                  public void run() {
                                      if (connection_error) {
                                          no_connection_layout.setVisibility(View.VISIBLE);
                                          text_service.setVisibility(View.GONE);
                                          layout_searching_content.setVisibility(View.GONE);
                                          classifications_recycler.setVisibility(View.GONE);
                                          layout_content.setVisibility(View.INVISIBLE);
                                          swipeRefresh_merchants.setVisibility(View.INVISIBLE);
                                          layout_no_publications.setVisibility(View.GONE);
                                          pbLoading_products.setVisibility(View.GONE);
                                      } else if (getMerchants() != null && getMerchants().size() > 0) {//hay publicaciones de establecimientos
                                          no_connection_layout.setVisibility(View.GONE);
                                          text_service.setVisibility(View.VISIBLE);
                                          layout_searching_content.setVisibility(View.VISIBLE);
                                          classifications_recycler.setVisibility(View.VISIBLE);
                                          layout_content.setVisibility(View.VISIBLE);
                                          swipeRefresh_merchants.setVisibility(View.VISIBLE);
                                          layout_no_publications.setVisibility(View.GONE);
                                          pbLoading_products.setVisibility(View.GONE);

                                      } else {//no hubo resultados
                                          no_connection_layout.setVisibility(View.GONE);
                                          text_service.setVisibility(View.VISIBLE);
                                          layout_searching_content.setVisibility(View.VISIBLE);
                                          classifications_recycler.setVisibility(View.VISIBLE);
                                          layout_content.setVisibility(View.INVISIBLE);
                                          swipeRefresh_merchants.setVisibility(View.INVISIBLE);
                                          layout_no_publications.setVisibility(View.VISIBLE);
                                          pbLoading_products.setVisibility(View.GONE);
                                      }
                                  }
                              });
    }

    private void setupAdapterClassifications(){
        runOnUiThread(new
                              Runnable() {
                                  @Override
                                  public void run() {
                                      if (classifications_recycler.getAdapter()==null) {
                                          adapterClassifications = new ClassificationsAdapter(clasifications, MainClassificationsActivity.this);
                                          classifications_recycler.setAdapter(adapterClassifications);
                                      }else{
                                          notifyListChangedClassifications();
                                      }
                                  }
                              });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //reset search parameter
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        finish();
    }

    public int getClassificationSelected() {
        return classification_position_selected;
    }

    public void resetElement(int position){
        if (position!=-1) {
            ClassificationItem old_item = clasifications.get(position);

            if (old_item != null) {
                old_item.setSelected(String.valueOf(false));
                adapterClassifications.notifyItemChanged(position);
            }
        }
    }
    public void setClassificationItemSelected(int position){
        if (position!=-1) {
            classification_position_selected = position;
            clasifications.get(position).setSelected(String.valueOf(true));
            adapterClassifications.notifyItemChanged(position);
        }else{
            classification_position_selected = position;
        }
    }

    public void getMerchantsByClassifitacion(int position){
        merchants.clear();
        loadingViewMerchants();
        if (position!=-1 && clasifications.get(position) != null) {
            processRequestGetMerchantsByService(type_service, clasifications.get(position), Constants.cero, Constants.load_more_tax_extended, load_initial, false);
        }else{//consultar todos
            processRequestGetMerchantsByService(type_service, null, Constants.cero, Constants.load_more_tax_extended, load_initial, false);
        }
    }

    public void startStoreActivity(MerchantItem item){
        Intent i = new Intent(this,StoreActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MERCHANT_OBJECT,item);
        args.putString(Constants.TYPE_SERVICE,type_service);
        i.putExtras(args);
        startActivity(i);
    }
    public void showBottomSheetDialogFragment() {// al cerrar deberia establecer en la cabecera la direccion seleccionada
        BottomSheetFragment bottomSheetFragment = BottomSheetFragment.createInstance();
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }
    public void assignAddressSelected(ShippingAddress ship){

        text_shipping_address.setText(ship.getAddressResumed());
    }
}
