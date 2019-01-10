package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.CategoryViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Connectivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.adapters.OfferAdapter;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.OnLoadMoreListener;
import com.delivery.estrategiamovilmx.domiciliosflorencia.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class OfferFragment extends Fragment {
    private ArrayList<PublicationCardViewModel> products = new ArrayList<>();
    private static RecyclerView recList_offers;
    private SwipeRefreshLayout swipeRefresh_offers;
    private RelativeLayout layout_no_publications;
    private RelativeLayout no_connection_layout;
    private ProgressBar pbLoading_offers;
    private LinearLayoutManager llm;
    private static final String TAG = OfferFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private static final int SELECT_CATEGORY = 1;
    public final boolean load_initial = true;
    private static HashMap<String, String> params = new HashMap<>();
    private CategoryViewModel category;
    private OfferAdapter adapter;
    private AppCompatButton button_retry_search;
    private AppCompatButton button_retry;
    public OfferFragment() {
        // Required empty public constructor
    }
    public static OfferFragment createInstance(HashMap<String, String> arguments) {
        OfferFragment fragment = new OfferFragment();
        fragment.setParams(arguments);
        return fragment;
    }
    public static void setParams(HashMap<String, String> params) {
        OfferFragment.params = params;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_offer, container, false);
        pbLoading_offers = (ProgressBar) rootView.findViewById(R.id.pbLoading_offers);
        no_connection_layout = (RelativeLayout) rootView.findViewById(R.id.no_connection_layout);
        layout_no_publications = (RelativeLayout) rootView.findViewById(R.id.layout_no_publications);
        recList_offers = (RecyclerView) rootView.findViewById(R.id.cardList_offers);
        recList_offers.setItemAnimator(new DefaultItemAnimator());
        recList_offers.setHasFixedSize(true);

        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        if (recList_offers.getLayoutManager() == null) {
            recList_offers.setLayoutManager(llm);
        }
        swipeRefresh_offers = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh_offers);
        swipeRefresh_offers.setColorSchemeResources(
                R.color.s1,
                R.color.s2,
                R.color.s3,
                R.color.s4
        );
        swipeRefresh_offers.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.d(TAG,"REFRESH DATA.....");
                        setupListItems(null, Constants.cero, Constants.load_more_tax, load_initial,true);
                    }
                }
        );

        pbLoading_offers.setVisibility(View.VISIBLE);
        recList_offers.setVisibility(View.GONE);
        button_retry_search = (AppCompatButton) rootView.findViewById(R.id.button_retry_search);
        button_retry_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                products.clear();
                String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
                loadingView();
                setupListItems(null, Constants.cero, Constants.load_more_tax, load_initial,false);
            }
        });
        button_retry = (AppCompatButton) rootView.findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connectivity.isNetworkAvailable(getContext())) {
                    products.clear();
                    String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
                    loadingView();
                    setupListItems(null, Constants.cero, Constants.load_more_tax, load_initial,false);
                }
            }
        });

        if (Connectivity.isNetworkAvailable(getContext())) {
            products.clear();
            String category_string = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.products_category_selected);
            loadingView();
            setupListItems(null, Constants.cero, Constants.load_more_tax, load_initial,false);
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
                            recList_offers.setVisibility(View.GONE);
                            layout_no_publications.setVisibility(View.GONE);
                            pbLoading_offers.setVisibility(View.GONE);
                        }else if (getProducts()!=null && getProducts().size()>0) {Log.d(TAG,"Mostrar resultados !!!!!");
                            no_connection_layout.setVisibility(View.GONE);
                            recList_offers.setVisibility(View.VISIBLE);
                            layout_no_publications.setVisibility(View.GONE);
                            pbLoading_offers.setVisibility(View.GONE);
                        }else{
                            no_connection_layout.setVisibility(View.GONE);
                            recList_offers.setVisibility(View.GONE);
                            layout_no_publications.setVisibility(View.VISIBLE);
                            pbLoading_offers.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public ArrayList<PublicationCardViewModel> getProducts() {
        return products;
    }

    private void loadingView(){
        no_connection_layout.setVisibility(View.GONE);
        recList_offers.setVisibility(View.GONE);
        layout_no_publications.setVisibility(View.GONE);
        pbLoading_offers.setVisibility(View.VISIBLE);
    }
    public void setupListItems(CategoryViewModel category, int start, int end, boolean load_initial, boolean isRefresh) {//obtener datos de la publicacion mas datos de las url de imagenes
        String newUrl = Constants.GET_PRODUCTS;
        Log.d(TAG, "setupListItems PRODUCTOS---------------------------------------------load_initial:" + load_initial);
        //validation
        VolleyGetRequest(newUrl + "?method=getAllOffers" + "&start=" + start + "&end=" + end, load_initial,isRefresh);
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
                                            swipeRefresh_offers.setRefreshing(false);
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
                                            swipeRefresh_offers.setRefreshing(false);
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
                    Log.d(TAG, "OfferFragment.noData..." + mensaje2);
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
                adapter.notifyListChanged();
                adapter.setLoaded();
            }
        }, Constants.cero);
    }

    private void processingResponseInit(JSONObject response) {
        Log.d(TAG, "processingResponse offers initial....." + response.toString());
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
                    Log.d(TAG, "OfferFragment.noData..." + mensaje2);
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
        if (recList_offers.getAdapter()==null) {
            adapter = new OfferAdapter(getActivity(),getProducts(), recList_offers);
            recList_offers.setAdapter(adapter);
            //load more functionallity
            adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
                @Override
                public void onLoadMore() {
                    if (!swipeRefresh_offers.isRefreshing()) {
                        int index = products != null ? (products.size()) : Constants.cero;
                        int end = index + Constants.load_more_tax;
                        setupListItems(category, index, end, false, false);
                    }
                }
            });
        }else{
            notifyListChanged();
        }
    }


}
