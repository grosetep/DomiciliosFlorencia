package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.AddProductRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.LookForProductsResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.StringResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.lookForStoresResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.AddToCartActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ShoppingCartActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.StoreActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.LookForMerchantsAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;

import static com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment.ADD_TO_CART_INTENT;
import static com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment.CART_OBJECT;
import static com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment.PRODUCT_OBJECT;
import static com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment.SHOW_CART;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends Fragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private ProgressBar pbLoading;
    private RecyclerView recyclerview_merchants;
    private RelativeLayout layout_no_publications;
    private RelativeLayout no_connection_layout;
    private AppCompatButton button_retry_search;
    private AppCompatButton button_retry;
    private LinearLayoutManager llm;

    private ArrayList<MerchantItem> merchants = new ArrayList<>();
    private ArrayList<PublicationCardViewModel> products = new ArrayList<>();

    private LinearLayout layout_results;
    private ScrollView layout_general_content;
    private LinearLayout layout_content;

    private LookForMerchantsAdapter adapterMerchants;

    private String type_service;
    private String query = "";
    private String id_country;
    private MerchantItem merchant_selected;
    private String current_id_merchant_on_cart = null;

    public String getCurrent_id_merchant_on_cart() {
        return current_id_merchant_on_cart;
    }

    public void setCurrent_id_merchant_on_cart(String current_id_merchant_on_cart) {
        this.current_id_merchant_on_cart = current_id_merchant_on_cart;
    }

    public MerchantItem getMerchant_selected() {
        return merchant_selected;
    }

    public void setMerchant_selected(MerchantItem merchant_selected) {
        this.merchant_selected = merchant_selected;
    }

    public String getType_service() {
        return type_service;
    }

    public void setType_service(String type_service) {
        this.type_service = type_service;
    }

    public ArrayList<MerchantItem> getMerchants() {
        return merchants;
    }

    public void setMerchants(ArrayList<MerchantItem> merchants) {
        this.merchants = merchants;
    }

    public ArrayList<PublicationCardViewModel> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<PublicationCardViewModel> products) {
        this.products = products;
    }

    public static SearchFragment createInstance(String type_service){
        Bundle args = new Bundle();
        args.putString(Constants.TYPE_SERVICE,type_service);
        SearchFragment frm = new SearchFragment();
        frm.setArguments(args);
        return frm;
    }

    public SearchFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            setType_service(getArguments().getString(Constants.TYPE_SERVICE));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_search, container, false);
        pbLoading = v.findViewById(R.id.pbLoading);
        layout_no_publications = v.findViewById(R.id.layout_no_publications);
        button_retry_search =  v.findViewById(R.id.button_retry_search);
        no_connection_layout = v.findViewById(R.id.no_connection_layout);

        //lista comercios
        recyclerview_merchants = v.findViewById(R.id.recyclerview_merchants);
        recyclerview_merchants.setVisibility(View.INVISIBLE);
        recyclerview_merchants.setHasFixedSize(true);
        recyclerview_merchants.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);
        if (recyclerview_merchants.getLayoutManager()==null){ recyclerview_merchants.setLayoutManager(llm);}

        layout_results = v.findViewById(R.id.layout_results);
        layout_content = v.findViewById(R.id.layout_content);
        layout_general_content = v.findViewById(R.id.layout_general_content);

        button_retry = v.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                merchants.clear();
                products.clear();
                loadingView();
                search(query,Constants.cero, Constants.load_more_tax_large,true);

            }
        });

        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                merchants.clear();
                products.clear();
                loadingView();
                search(query,Constants.cero, Constants.load_more_tax_large,true);
            }
        });

        id_country = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.id_country);
        if (savedInstanceState==null) {
            getExistingMerchantOnCart();//obtener el id merchant del carrito
        }

        return v;
    }

    private void getExistingMerchantOnCart(){
        UserItem user= GeneralFunctions.getCurrentUser(getActivity());
        RestServiceWrapper.getExistingMerchantOnCart(user.getIdUser(), new Callback<StringResponse>() {
            @Override
            public void onResponse(Call<StringResponse> call, retrofit2.Response<StringResponse> response) {
                //Log.d(TAG, "Respuesta StringResponse: " + response);
                StringResponse cl_response = response.body();
                if (response != null && response.isSuccessful()) {

                    if (cl_response != null && cl_response.getStatus().equals(Constants.success)) {//procesa respuesta y almacena en lista para proceder a segundo llamado

                        //Log.d(TAG,"Asignando..merchnt de carrito..........."+ cl_response.getResult());
                        setCurrent_id_merchant_on_cart(cl_response.getResult());//assign current id merchant if exist
                    }
                }
            }

            @Override
            public void onFailure(Call<StringResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });

    }

    private void updateUI(final boolean connection_error){
        if (connection_error) {
            no_connection_layout.setVisibility(View.VISIBLE);
            recyclerview_merchants.setVisibility(View.GONE);
            layout_results.setVisibility(View.GONE);
            layout_general_content.setVisibility(View.GONE);
            layout_no_publications.setVisibility(View.GONE);
            pbLoading.setVisibility(View.GONE);
        }else if (getMerchants()!=null && getMerchants().size()>0) {
            no_connection_layout.setVisibility(View.GONE);
            layout_results.setVisibility(View.VISIBLE);
            recyclerview_merchants.setVisibility(View.VISIBLE);
            layout_general_content.setVisibility(View.VISIBLE);
            layout_no_publications.setVisibility(View.GONE);
            pbLoading.setVisibility(View.GONE);
        }else{
            no_connection_layout.setVisibility(View.GONE);
            recyclerview_merchants.setVisibility(View.GONE);
            layout_results.setVisibility(View.GONE);
            layout_general_content.setVisibility(View.GONE);
            layout_no_publications.setVisibility(View.VISIBLE);
            pbLoading.setVisibility(View.GONE);
        }
    }

    public void search(String query,int start,int end,boolean initial){
        this.query = query;
        Log.d(TAG,"SEARCHING......................:"+query);

        if (initial) {
            merchants.clear();
            products.clear();
            loadingView();
        }

        //ejecuta request
        lookForStores(query,Constants.cero, Constants.load_more_tax_extended, initial);
    }

    public void startShoppingCartActivity(){
        Intent i = new Intent(getActivity(), ShoppingCartActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MERCHANT_OBJECT,getMerchant_selected());
        i.putExtras(args);
        startActivityForResult(i,SHOW_CART);
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recyclerview_merchants.setVisibility(View.GONE);
        layout_results.setVisibility(View.GONE);
        layout_general_content.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading.setVisibility(View.VISIBLE);
    }


    private void lookForStores(final String query, final int start, final int end, final boolean load_initial){
        String id_country  = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.id_country);

        RestServiceWrapper.lookForStores(query, getType_service(),id_country,start, end , new Callback<lookForStoresResponse>() {
            @Override
            public void onResponse(Call<lookForStoresResponse> call, retrofit2.Response<lookForStoresResponse> response) {
                //Log.d(TAG, "Respuesta lookForStores: " + response);
                lookForStoresResponse cl_response = response.body();
                if (response != null && response.isSuccessful()) {

                    if (cl_response != null && cl_response.getStatus().equals(Constants.success)) {//procesa respuesta y almacena en lista para proceder a segundo llamado
                        if (load_initial) {
                            processingResponseInitStores(cl_response.getResult());
                        }else {
                            processingResponseStores(cl_response.getResult());
                        }

                        lookForProducts(query, start, end, load_initial);
                    } else if (cl_response != null && cl_response.getStatus().equals(Constants.no_data)) {

                        if(!load_initial) {
                            notifyListChangedStores();
                            String response_error = cl_response.getMessage();
                            Log.d(TAG, "Mensage:" + response_error);
                            onError(getString(R.string.error_invalid_login, response_error));
                        }else{updateUI(true);}


                    } else {

                        if(!load_initial) {
                            notifyListChangedStores();
                            String response_error = cl_response.getMessage();
                            Log.d(TAG, "Mensage:" + response_error);
                            onError(getString(R.string.error_invalid_login, response_error));
                        }else{updateUI(true);}
                    }


                } else {

                    if(!load_initial) {
                        notifyListChangedStores();

                        onError(getString(R.string.generic_error));
                    }else{updateUI(true);}
                }
            }

            @Override
            public void onFailure(Call<lookForStoresResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });
    }
    private void processingResponseInitStores(List<MerchantItem> list) {
        //Log.d(TAG,"processingResponseInitStores initial search");

        if (list!=null && list.size()>0){
            merchants.addAll(list);
        }

        setupAdapterStores();
        updateUI(false);

    }
    private void processingResponseStores(ArrayList<MerchantItem> list) {
       // Log.d(TAG,"processingResponseInitStores initial search");

        if (list!=null && list.size()>0){
            addNewElements(list);
        }else{
            if ( list == null && merchants.size()>Constants.cero ){merchants.clear();updateUI(false);}
        }
    }
    private void addNewElements(ArrayList<MerchantItem> new_merchants){
        if (new_merchants!=null && new_merchants.size()>Constants.cero) {
            merchants.addAll(GeneralFunctions.FilterMerchants(merchants, new_merchants));
        }
    }
    private void onError(String err){
        ShowConfirmations.showConfirmationMessage(err, getActivity());
    }

    private void setupAdapterStores() {
        getActivity().runOnUiThread(new
                                            Runnable() {
                                                @Override
                                                public void run() {
                                                    if (recyclerview_merchants.getAdapter() == null) {
                                                        adapterMerchants = new LookForMerchantsAdapter(merchants, SearchFragment.this, recyclerview_merchants);
                                                        recyclerview_merchants.setAdapter(adapterMerchants);
                                                        adapterMerchants.setOnLoadMoreListener(new OnLoadMoreListener() {
                                                            @Override
                                                            public void onLoadMore() {
                                                                int index = merchants != null ? (getMerchants().size()) : 0;
                                                                int end = index + Constants.load_more_tax_large;
                                                                lookForStores(query, index, end, false);
                                                            }

                                                        });
                                                    } else {
                                                        notifyListChangedStores();
                                                    }
                                                }
                                            });
    }

    private void notifyListChangedStores() {
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapterMerchants.notifyDataSetChanged();
                adapterMerchants.setLoaded();
            }
        }, Constants.cero);
    }


    private void lookForProducts(final String query,final  int start, final int end,final boolean load_initial){
       // Log.d(TAG, "lookForProducts....load_initial:" + load_initial);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                executeLookForProducts(query, start, end, load_initial);
            }
        }, Constants.cero);

        //   }
    }

    private void executeLookForProducts( final String query, int start, int end, final boolean load_initial){
         Log.d(TAG, "executeLookForProducts load_initial: "+load_initial);
        String id_country = ApplicationPreferences.getLocalStringPreference(getActivity(),Constants.id_country);

        RestServiceWrapper.executeLookForProducts(query,getType_service(), id_country, start, end, new Callback<LookForProductsResponse>() {
            @Override
            public void onResponse(Call<LookForProductsResponse> call, retrofit2.Response<LookForProductsResponse> response) {

                if (response != null && response.isSuccessful()) {
                    LookForProductsResponse cl_response = response.body();

                    if (cl_response != null && cl_response.getStatus().equals(Constants.success)) {//procesa respuesta y almacena en lista para proceder a segundo llamado
                        //onSuccess(login_response);
                        ArrayList<PublicationCardViewModel> new_elements = new ArrayList(cl_response.getResult());
                        products.addAll(new_elements);
                        /*Log.d(TAG," LISTA DE PRODUCTOS");
                        for (PublicationCardViewModel p:new_elements){
                            Log.d(TAG," "+p.toString());
                        }*/


                        generateProductsByMerchantViews(new_elements);
                        hideKeyboard();
                    } else if (cl_response != null && cl_response.getStatus().equals(Constants.no_data)) {
                        Log.d(TAG," no hay PRODUCTOS");
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
            public void onFailure(Call<LookForProductsResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });

    }
    private View createElementMore(final String idMerchant){
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        final View currentCustomView = mInflater.inflate(R.layout.card_view_layout_more, null);

        currentCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStoreActivity(getMerchantById(idMerchant));
            }
        });

        return currentCustomView;
    }
    private View createHeaderElement(final String idMerchant){
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        final View currentCustomView = mInflater.inflate(R.layout.card_view_layout_header, null);
        ImageView image_header = currentCustomView.findViewById(R.id.image_header);
        TextView text_name = currentCustomView.findViewById(R.id.text_name);
        TextView text_show_more = currentCustomView.findViewById(R.id.text_show_more);
        text_show_more.setText(getString(R.string.link_show_store,GeneralFunctions.getTitleByServiceSelectedOnMoreOption(getType_service(),getActivity())));
        final MerchantItem item = getMerchantById(idMerchant);
        if (item!=null) {
            text_name.setText(item.getNameBussiness());
            String ImagePath = item.getImagePath() + item.getImageName();
            Glide.with(getActivity())
                    .load(ImagePath)
                    .into(image_header);
        }

        text_show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStoreActivity(item);
            }
        });
        currentCustomView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStoreActivity(item);
            }
        });

        return currentCustomView;
    }
    private View createElement(final PublicationCardViewModel p){
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        final View currentCustomView = mInflater.inflate(R.layout.card_view_layout_resumed, null);
        TextView text_card_name = currentCustomView.findViewById(R.id.text_card_name);
        ImageView image_card_cover = currentCustomView.findViewById(R.id.image_card_cover);
        TextView text_priceOff = currentCustomView.findViewById(R.id.text_priceOff);
        TextView text_price = currentCustomView.findViewById(R.id.text_price);
        final AppCompatButton image_action_add = currentCustomView.findViewById(R.id.image_action_add);
        RelativeLayout layout_over = currentCustomView.findViewById(R.id.layout_over);

        text_card_name.setText(p.getProduct());
        Glide.with(image_card_cover.getContext())
                .load(p.getPath() + p.getImage())
                .into(image_card_cover);

        if (p.getOfferPrice()!=null && !p.getOfferPrice().isEmpty()){
            text_priceOff.setText(StringOperations.getStringWithA(StringOperations.getAmountFormat(p.getOfferPrice(),id_country)));
            text_price.setText(StringOperations.getStringWithDe(StringOperations.getAmountFormat(p.getRegularPrice(),id_country)));
            text_price.setVisibility(View.VISIBLE);
        }else{
            text_priceOff.setText(StringOperations.getStringWithA(StringOperations.getAmountFormat(p.getRegularPrice(),id_country)));
            text_price.setVisibility(View.INVISIBLE);
        }
        image_action_add.setVisibility(View.VISIBLE);
        pbLoading.setVisibility(View.GONE);
        image_action_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserItem user = GeneralFunctions.getCurrentUser(getActivity());
                if (user!=null) {

                    pbLoading.setVisibility(View.VISIBLE);
                    //Log.d(TAG,"PRODUCTO SELECCIONADO getIdMerchant: "+ p.getIdMerchant());
                    if (getCurrent_id_merchant_on_cart()==null || (getCurrent_id_merchant_on_cart().equals(p.getIdMerchant()))) {//permite agregar si no hay nada en carrito o si el producto es del mismo negocio
                        startAddToCartActivity(p);
                    }else{ShowConfirmations.showConfirmationMessage(getString(R.string.prompt_cant_add_product),getActivity());}

                }else{
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }
            }
        });
        int stock = 0;
        try{
            stock = Integer.parseInt(p.getStock());
        }catch(NumberFormatException e){

        }
        if (stock <= 0) {
            layout_over.setVisibility(View.VISIBLE);
            currentCustomView.setOnClickListener(null);
        } else {
            layout_over.setVisibility(View.GONE);//aqui mostrar opciones para agregar producto al carrito en lugar de mostrar detalle
            currentCustomView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserItem user = GeneralFunctions.getCurrentUser(getActivity());
                    if (user!=null) {

                        pbLoading.setVisibility(View.VISIBLE);
                        //Log.d(TAG,"PRODUCTO SELECCIONADO getIdMerchant: "+ p.getIdMerchant());
                        if (getCurrent_id_merchant_on_cart()==null || (getCurrent_id_merchant_on_cart().equals(p.getIdMerchant()))) {//permite agregar si no hay nada en carrito o si el producto es del mismo negocio
                            startAddToCartActivity(p);
                        }else{ShowConfirmations.showConfirmationMessage(getString(R.string.prompt_cant_add_product),getActivity());}

                    }else{
                        Intent i = new Intent(getContext(),LoginActivity.class);
                        i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                        startActivity(i);
                    }
                }
            });
        }
        return currentCustomView;
    }
    private View createDivider(){
        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        View divider = mInflater.inflate(R.layout.simple_view_divider, null);
        return divider;
    }
    private void generateProductsByMerchantViews(ArrayList<PublicationCardViewModel> new_elements){
        layout_content.removeAllViews();//limpiamos el contenido

        boolean start = true;
        String id_merchant = "";

        LinearLayout general_rows = new LinearLayout(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        general_rows.setOrientation(LinearLayout.VERTICAL);
        general_rows.setLayoutParams(layoutParams);


        LinearLayout list_elements_temp = new LinearLayout(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        list_elements_temp.setOrientation(LinearLayout.HORIZONTAL);
        lp.weight = 1.0f;
        lp.gravity = Gravity.BOTTOM;
        list_elements_temp.setLayoutParams(lp);


        int element_counter = 0;
        for(int i = 0;i<new_elements.size();i++) {


            if (start) {
                start = false;
                id_merchant = new_elements.get(i).getIdMerchant();
                //crear elemento de encabezado

                View header = createHeaderElement(new_elements.get(i).getIdMerchant());
                View element = createElement(new_elements.get(i));

                general_rows.addView(header);
                list_elements_temp.addView(element);element_counter++;

                //si solo hay un producto a mostrar entonces agregarlo a la lista horizontal y terminar
                if (new_elements.size()==1) {
                    //crear elemento more
                    View more = createElementMore(new_elements.get(i).getIdMerchant());
                    list_elements_temp.addView(more);

                    HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
                    horizontalScrollView.setHorizontalScrollBarEnabled(false);
                    horizontalScrollView.addView(list_elements_temp);
                    general_rows.addView(horizontalScrollView);//agrega el container hasta el ultimo creado del anterior merchant
                    //crea divisor
                    general_rows.addView(createDivider());
                }


            } else {

                if (id_merchant.equals(new_elements.get(i).getIdMerchant())) {//crear element y agregar a general_row existente
                    element_counter++;
                    View element = createElement(new_elements.get(i));

                    if (element_counter<=Constants.max_elements_on_search && element_counter<new_elements.size()) {//valida si agregar o no el elemento
                        list_elements_temp.addView(element);





                    }else if(element_counter==new_elements.size()){//agregar more porqye ya no hay mas elementos a iterar
                        //agregar elemento mostrar mas
                        list_elements_temp.addView(element);
                        View more = createElementMore(new_elements.get(i).getIdMerchant());
                        list_elements_temp.addView(more);

                        //crea horizontal scroll y agrega el linearlayout en el, despues agregar el scrollview a la vista
                        HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
                        horizontalScrollView.setHorizontalScrollBarEnabled(false);
                        horizontalScrollView.addView(list_elements_temp);
                        general_rows.addView(horizontalScrollView);//agrega el container hasta el ultimo creado del anterior merchant

                        general_rows.addView(createDivider());

                    }else{continue;}

                } else {//agregar el ultimo general_row creado, crear un nuevo general_row, crear un nuevo element y agregarlo al general_row
                    if (element_counter<=Constants.max_elements_on_search){
                        //agregar elemento mostrar mas
                        //Log.d(TAG,"createElementMore...");
                        View more = createElementMore(new_elements.get(i).getIdMerchant());
                        list_elements_temp.addView(more);


                    }

                    //crea horizontal scroll y agrega el linearlayout en el, despues agregar el scrollview a la vista
                    HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getActivity());
                    horizontalScrollView.setHorizontalScrollBarEnabled(false);
                    horizontalScrollView.addView(list_elements_temp);
                    general_rows.addView(horizontalScrollView);//agrega el container hasta el ultimo creado del anterior merchant

                    general_rows.addView(createDivider());

                    id_merchant = new_elements.get(i).getIdMerchant();
                    element_counter = 0;

                    //crea nuevo linealayout
                    list_elements_temp = new LinearLayout(getActivity());
                    list_elements_temp.setLayoutParams(lp);
                    list_elements_temp.setOrientation(LinearLayout.HORIZONTAL);

                    View element = createElement(new_elements.get(i));
                    View header = createHeaderElement(new_elements.get(i).getIdMerchant());
                    general_rows.addView(header);
                    list_elements_temp.addView(element);//agregar nuevo elemento del nuevo merchant al layout nuevo
                    element_counter++;

                    if (i==new_elements.size()-1){//fin de la lista, agregar ultima lista de elementos
                        HorizontalScrollView horizontalScrollView_final = new HorizontalScrollView(getActivity());
                        horizontalScrollView_final.setHorizontalScrollBarEnabled(false);
                        horizontalScrollView_final.addView(list_elements_temp);

                        //agregar elemento mostrar mas
                        //Log.d(TAG,"createElementMore...");
                        View more = createElementMore(new_elements.get(i).getIdMerchant());
                        list_elements_temp.addView(more);

                        general_rows.addView(horizontalScrollView_final);//agrega el container hasta el ultimo creado del anterior merchant
                    }
                }
            }
        }

        layout_content.addView(general_rows);//agrega todos los layout al general
    }
    private MerchantItem getMerchantById(String merchant_id){
        if (!merchant_id.equals(Constants.cero)) {
            for (MerchantItem m : merchants) {
                if (String.valueOf(m.getIdMerchant()).equals(merchant_id)) {
                    return m;
                }
            }
        }
        return null;
    }
    private PublicationCardViewModel getProductById(String id_product){
        for(PublicationCardViewModel p:products){
            if (String.valueOf(p.getIdProduct()).equals(id_product)){
                return p;
            }
        }
        return null;
    }

    public void startStoreActivity(MerchantItem item){
        Intent i = new Intent(getActivity(),StoreActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(Constants.MERCHANT_OBJECT,item);
        args.putString(Constants.TYPE_SERVICE,type_service);
        i.putExtras(args);
        startActivity(i);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getActivity().getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void startAddToCartActivity(PublicationCardViewModel p){

        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        AddProductRequest request = new AddProductRequest();
        request.setId_product(p.getIdProduct());
        request.setId_user(user != null ? user.getIdUser() : "0");
        request.setUnits("1");
        request.setOperation("add");

        if (p.getOfferPrice() != null && !p.getOfferPrice().isEmpty()) {//hay oferta, tomar precio oferta
            request.setPrice_product(p.getOfferPrice());
        } else {//tomar precio regular
            request.setPrice_product(p.getRegularPrice());
        }

        Intent intent = new Intent(getActivity(), AddToCartActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ProductsFragment.CART_OBJECT,request);
        args.putSerializable(ProductsFragment.PRODUCT_NAME,p.getProduct());
        intent.putExtras(args);
        startActivityForResult(intent, ProductsFragment.ADD_TO_CART_INTENT);

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SHOW_CART:
                if (resultCode == Activity.RESULT_CANCELED) {
                    String current_id_merchant_on_cart_onBackPressed = data.getStringExtra(ShoppingCartActivity.ID_MERCHANT);
                    //Log.d(TAG,"retorno: " + current_id_merchant_on_cart_onBackPressed);

                    setCurrent_id_merchant_on_cart(current_id_merchant_on_cart_onBackPressed);

                }
        }
    }


}
