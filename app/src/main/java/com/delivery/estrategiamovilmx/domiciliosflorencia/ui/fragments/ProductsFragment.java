package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.CategoryViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.CartRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.PublicationAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;
import com.delivery.estrategiamovilmx.domiciliosflorencia.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProductsFragment extends Fragment {
    private ArrayList<PublicationCardViewModel> products = new ArrayList<>();
    private static RecyclerView recList;
    private SwipeRefreshLayout swipeRefresh_products;
    //private TextView text_change_category;
    //private LinearLayout change_products_container;
    private AppCompatButton button_retry_search;
    private AppCompatButton button_retry;
    private RelativeLayout layout_no_publications;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_products;
    private StaggeredGridLayoutManager llm;
    private static final String TAG = ProductsFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private static final int SELECT_CATEGORY = 1;
    public final boolean load_initial = true;
    private static HashMap<String, String> params = new HashMap<>();
    private CategoryViewModel category;
    private PublicationAdapter adapter;

    public ProductsFragment() {
        // Required empty public constructor
    }
    public static ProductsFragment createInstance(HashMap<String, String> arguments) {
        ProductsFragment fragment = new ProductsFragment();
        fragment.setParams(arguments);
        return fragment;
    }
    public static void setParams(HashMap<String, String> params) {
        ProductsFragment.params = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_products, container, false);
        pbLoading_products = (ProgressBar) rootView.findViewById(R.id.pbLoading_products);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        layout_no_publications = (RelativeLayout) rootView.findViewById(R.id.layout_no_publications);
        recList = (RecyclerView) rootView.findViewById(R.id.cardList);
        recList.setItemAnimator(null);
        recList.setHasFixedSize(true);

        llm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        llm.setOrientation(LinearLayoutManager.VERTICAL);

        if (recList.getLayoutManager() == null) {
            recList.setLayoutManager(llm);
        }
        swipeRefresh_products = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_products);
        swipeRefresh_products.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_products.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        setupListItems(null, Constants.cero, Constants.load_more_tax_extended, load_initial,true);
                    }
                }
        );
       /* text_change_category = (TextView) rootView.findViewById(R.id.text_change_category);
        change_products_container = (LinearLayout) rootView.findViewById(R.id.change_products_container);
        change_products_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCategoryActivity.class);
                startActivityForResult(intent, SELECT_CATEGORY);
            }
        });*/
        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isNetworkAvailable(getContext())) {
                    products.clear();
                    String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
                    loadingView();
                    setupListItems(null, Constants.cero, Constants.load_more_tax_extended, load_initial,false);
                }
            }
        });
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.clear();
                String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
                loadingView();
                setupListItems(null, Constants.cero, Constants.load_more_tax_extended, load_initial,false);
            }
        });

        if (Connectivity.isNetworkAvailable(getContext())) {
            products.clear();
            String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
            loadingView();
            setupListItems(null, Constants.cero, Constants.load_more_tax_extended, load_initial,false);
        } else {
            updateUI(true);
        }
        return rootView;
    }
    private void updateUI(final boolean connection_error){
        getActivity().runOnUiThread(new
            Runnable() {
                @Override
                public void run() {
                    if (connection_error) {
                        no_connection_layout.setVisibility(View.VISIBLE);
                        swipeRefresh_products.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.GONE);
                        pbLoading_products.setVisibility(View.GONE);
                    }else if (getProducts()!=null && getProducts().size()>0) {
                        no_connection_layout.setVisibility(View.GONE);
                        swipeRefresh_products.setVisibility(View.VISIBLE);
                        layout_no_publications.setVisibility(View.GONE);
                        pbLoading_products.setVisibility(View.GONE);
                    }else{
                        no_connection_layout.setVisibility(View.GONE);
                        swipeRefresh_products.setVisibility(View.GONE);
                        layout_no_publications.setVisibility(View.VISIBLE);
                        pbLoading_products.setVisibility(View.GONE);
                    }
                }
            });
    }

    public ArrayList<PublicationCardViewModel> getProducts() {
        return products;
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        swipeRefresh_products.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_products.setVisibility(View.VISIBLE);
    }
    public void setupListItems(CategoryViewModel category, int start, int end, boolean load_initial, boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PRODUCTS;
        Log.d(TAG, "setupListItems PRODUCTOS---------------------------------------------load_initial:" + load_initial);
        //validation
        VolleyGetRequest(newUrl + "?method=getAllProducts" + "&start=" + start + "&end=" + end, load_initial,isRefresh);
    }

    public void VolleyGetRequest(final String url, boolean load_initial,boolean isRefresh) {
        Log.d(TAG, "VolleyGetRequest Products:" + url);
        if (isRefresh) {
            makeRequest(url,load_initial,isRefresh);
        }else if (!load_initial) {
            addLoadingAndMakeRequest(url);


        } else {
            makeRequest(url, load_initial,isRefresh);
        }
    }
    private void makeRequest(String url, final boolean load_initial, final boolean isRefresh) {
        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                url,
                                (String) null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(final JSONObject response) {
                                        if (isRefresh) {//only update list
                                            processingResponse(response,isRefresh);
                                            swipeRefresh_products.setRefreshing(false);
                                        }else if (load_initial) {
                                            processingResponseInit(response);
                                        }else {
                                            products.remove(products.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(products.size());
                                            processingResponse(response,isRefresh);
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error){
                                        error.printStackTrace();
                                        if(isRefresh){
                                            swipeRefresh_products.setRefreshing(false);
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else if(!load_initial) {
                                            products.remove(products.size() - 1);//delete loading..
                                            adapter.notifyItemRemoved(products.size());
                                            notifyListChanged();
                                            ShowConfirmations.showConfirmationMessage(getResources().getString(R.string.no_internet),getActivity());
                                        }else{updateUI(true);}
                                    }
                                }

                        ).setRetryPolicy(new DefaultRetryPolicy(
                                Constants.MY_SOCKET_TIMEOUT_MS,
                                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                );
    }
    private void addLoadingAndMakeRequest(final String url) {
        Log.d(TAG, "addLoading...");
        final boolean isRefresh = false;
        final boolean first_load= false;

        products.add(null);
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                adapter.notifyItemInserted(products.size() - 1);
                makeRequest(url, first_load,isRefresh);
            }
        };
        handler.post(r);

    }

    private void processingResponse(JSONObject response, boolean isRefresh) {
        Log.d(TAG, "RESPONSE:" + response.toString());
        ArrayList<PublicationCardViewModel> new_products = null;
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS
                    JSONArray mensaje = response.getJSONArray("products");
                    new_products = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    addNewElements(new_products,isRefresh);
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "ProductsFragment.noData..." + mensaje2);
                    if (isRefresh && new_products == null && products.size()>Constants.cero ){products.clear();updateUI(false);}
                    break;
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());

        }finally {
            notifyListChanged();
        }
    }
    private void addNewElements(ArrayList<PublicationCardViewModel> new_publications,boolean isRefresh){
        if (new_publications!=null && new_publications.size()>Constants.cero) {
            Log.d(TAG, "new_publications:" + new_publications.size());
            if (isRefresh){Log.d(TAG, "isRefresh..");
                products.clear();
                products.addAll(new_publications);
            }else{
                products.addAll(GeneralFunctions.FilterPublications(products, new_publications));
            }
        }
    }
    private void notifyListChanged() {
        Log.d(TAG, "notifyListChanged()...");
        //Load more data for reyclerview
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
                adapter.setLoaded();
            }
        }, Constants.cero);
    }

    private void processingResponseInit(JSONObject response) {
        Log.d(TAG, "processingResponse products initial....." + response.toString());
        try {
            // Obtener atributo "estado"
            String status = response.getString("status");
            switch (status) {
                case "1": // SUCCESS

                    JSONArray mensaje = response.getJSONArray("products");
                    ArrayList<PublicationCardViewModel> new_products = new ArrayList<>(Arrays.asList(gson.fromJson(mensaje.toString(), PublicationCardViewModel[].class)));
                    if (new_products != null && new_products.size() > 0) {
                        products.addAll(new_products);
                    }
                    break;
                case "2": // NO DATA FOUND
                    String mensaje2 = response.getString("message");
                    Log.d(TAG, "ProductsFragment.noData..." + mensaje2);
                    break;
            }

        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }finally {
            setupAdapter();
            updateUI(false);
        }
    }


    private void setupAdapter(){
        if (recList.getAdapter()==null) {
            adapter = new PublicationAdapter(getActivity(),products, recList,this);
            recList.setAdapter(adapter);
            //load more functionallity
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_products.isRefreshing()) {
                        int index = products != null ? (products.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax_extended;
                        setupListItems(category, index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }
    public  void addToCart( final int position, final View v) {
        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        CartRequest request = new CartRequest();
        request.setId_product(products.get(position).getIdProduct());
        request.setId_user(user!=null?user.getIdUser():"0");
        request.setUnits("1");
        request.setOperation("add");

        RestServiceWrapper.shoppingCart(request,new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GenericResponse cart_response = response.body();
                    if (cart_response != null && cart_response.getStatus().equals(Constants.success)) {

                        PublicationCardViewModel product = products.get(position);
                        product.setAdded(true);
                        adapter.notifyItemChanged(position);
                        ShowConfirmations.showConfirmationMessage(cart_response.getResult().getMessage(), getActivity());

                    } else if (cart_response != null && cart_response.getStatus().equals(Constants.no_data)){
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        ShowConfirmations.showConfirmationMessage(response_error, getActivity());
                    }else{
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                        ShowConfirmations.showConfirmationMessage(response_error, getActivity());
                    }

                }else{
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,getString(R.string.error_generic)),getActivity());
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d(TAG,"ERROR: " +t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });




    }
}
