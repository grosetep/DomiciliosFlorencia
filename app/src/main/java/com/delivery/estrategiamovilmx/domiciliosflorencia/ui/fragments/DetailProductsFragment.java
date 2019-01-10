package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.DetailPublication;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ImageSliderPublication;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ProductModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.CartRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.VolleyErrorHelper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.GalleryActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.ShoppingCartActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.web.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailProductsFragment extends Fragment implements  ViewPager.OnPageChangeListener {
    private static final String TAG = DetailProductsFragment.class.getSimpleName();
    private Gson gson = new Gson();
    private LinearLayout principal_container_detail;
    private ProgressBar pb;
    private TextView text_cover;
    private TextView text_detail_availability;
    private TextView text_price;
    private TextView text_category;
    private TextView detailed_description;
    private DetailPublication detail;
    private ProductModel product_detail;
    private AppCompatButton button_order;
    private static int METHOD_GET_PRODUCT_IMAGES = 1;
    private static int METHOD_SHIPPING = 2;
    private static String PRODUCT_DETAIL = "product_detail";
    //Carroussell
    private ViewPager viewPager;
    private static final Integer DURATION = 3500;
    private Timer timer = null;
    private int position;
    private ImageSliderPublication[] images_publication;
    public DetailProductsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        product_detail = (ProductModel) getArguments().getSerializable(PRODUCT_DETAIL);

        String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

        if(!UtilPermissions.hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, UtilPermissions.PERMISSION_ALL);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_detail_products, container, false);
        detailed_description = (TextView) v.findViewById(R.id.detailed_description);
        text_detail_availability = (TextView) v.findViewById(R.id.text_detail_availability);
        text_cover = (TextView) v.findViewById(R.id.text_cover);
        text_category = (TextView) v.findViewById(R.id.text_category);
        text_price = (TextView) v.findViewById(R.id.text_price);
        principal_container_detail = (LinearLayout) v.findViewById(R.id.principal_container_detail);
        button_order = (AppCompatButton) v.findViewById(R.id.button_order);
        pb = (ProgressBar) v.findViewById(R.id.pbLoading_detail);
        pb.setVisibility(ProgressBar.VISIBLE);
        viewPager = (ViewPager) getActivity().findViewById(R.id.viewpager);
        if (product_detail!=null) {
            getProductImages(product_detail.getIdProduct());
            loadInformation();//llamar load information despues al finalizar consulta de imagenes
            assignActions();
        }
        return v;
    }
    private void assignActions(){
        button_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserItem user = GeneralFunctions.getCurrentUser(getContext());
                if (user!=null) {
                    Intent i = new Intent(getActivity(), ShoppingCartActivity.class);
                    startActivity(i);
                }else{
                    getActivity().finish();
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }
            }
        });
    }
    public DetailPublication getDetail() {
        return detail;
    }
    private void setupViewPager(ViewPager viewPager,ImageSliderPublication[] images) {
        viewPager.setAdapter(new CardsPagerAdapter(images));
        viewPager.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                return false;
            }
        });
    }

    public void loadInformation() {
        // Añadir parámetro a la URL del web service
        String newURL = Constants.GET_PRODUCTS + "?method=getDetailProduct" + "&idProduct=" + product_detail.getIdProduct();
        Log.d(TAG, "newURL:" + newURL);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                newURL,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // Procesar la respuesta Json
                        processingResponse(response);
                        //  pDialog.hide();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = VolleyErrorHelper.getErrorType(error, getActivity());
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                getActivity(),
                                message,
                                Toast.LENGTH_SHORT).show();
                    }
                }

        );
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.
                getInstance(getActivity()).
                addToRequestQueue(request
                );
    }

    public void VolleyPostRequest(String url, HashMap<String, String> params, final int callback){
        JSONObject jobject = new JSONObject(params);
        Log.d(TAG, "VolleyPostRequest:" + jobject.toString());

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        url,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                if (callback == METHOD_GET_PRODUCT_IMAGES)
                                    setArrayImages(response);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "Error Volley: " + error.getMessage() + "  " + error.getCause());
                                //dismissProgressDialog();
                                String mensaje2 = "Verifique su conexión a Internet.";
                                Toast.makeText(
                                        getActivity(),
                                        mensaje2,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }


                    @Override
                    public String getBodyContentType() {return "application/json; charset=utf-8" + getParamsEncoding();     }
                }
        );
    }
    private void setArrayImages(JSONObject response){
        Log.d(TAG, response.toString());
        try {
            // Obtener atributo "mensaje"
            String status = response.getString("status");
            switch (status) {
                case "1":
                    JSONArray result = response.getJSONArray("result");
                    images_publication = gson.fromJson(result.toString(), ImageSliderPublication[].class);
                    if (viewPager != null) {// se ejcuta despues de gaber consultado
                        setupViewPager(viewPager,images_publication);
                        if (getActivity()!=null) {
                            ((TextView) getActivity().findViewById(R.id.text_number_photos)).setText("" + images_publication.length);
                            start();
                        }
                    }
                    break;
                case "2":
                    onFailedImages();
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            onFailedImages();
        }

    }
    private void  onFailedImages(){
         ImageSliderPublication img = new ImageSliderPublication();
        img.setPath(product_detail.getPath());
        img.setImageName(product_detail.getImage());
        img.setEnableDeletion("false");
        img.setResource("remote");

         images_publication[0] =img;
        ((TextView)getActivity().findViewById(R.id.text_number_photos)).setText("0");
        if (viewPager != null) {// se ejcuta despues de gaber consultado
            setupViewPager(viewPager,images_publication);
            if (getActivity()!=null) {
                ((TextView) getActivity().findViewById(R.id.text_number_photos)).setText("" + images_publication.length);
                start();
            }
        }
    }
    public void processingResponse(JSONObject response) {
        Log.d(TAG, "response:" + response.toString());
        try {
            // Obtener atributo "mensaje"
            String message = response.getString("status");
            switch (message) {
                case "1"://assign values
                    JSONObject object = response.getJSONObject("details");
                    detail = gson.fromJson(object.toString(), DetailPublication.class);
                    detailed_description.setText(fromHtml(detail.getGeneral()));
                    text_category.setText(detail.getCategory());
                    text_cover.setText(detail.getDescription());
                    text_detail_availability.setText(detail.getStock());
                    String label_text ="";
                    if (detail.getOfferPrice()!=null && !detail.getOfferPrice().isEmpty()) {
                        text_price.setText(StringOperations.getStringWithA(StringOperations.getAmountFormatWithNoDecimals(detail.getOfferPrice())));
                    }else{
                        text_price.setText(StringOperations.getStringWithA(StringOperations.getAmountFormatWithNoDecimals(detail.getRegularPrice())));
                    }
                    principal_container_detail.setVisibility(View.VISIBLE);
                    pb.setVisibility(View.GONE);
                    break;
                case "2"://no detail
                    pb.setVisibility(View.GONE);
                    String errorMessage = response.getString("message");
                    Toast.makeText( getActivity(),  errorMessage, Toast.LENGTH_SHORT).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            pb.setVisibility(View.GONE);
            String errorMessage = getString(R.string.generic_error);
            Toast.makeText( getActivity(),  errorMessage, Toast.LENGTH_SHORT).show();
        }

    }
    public static DetailProductsFragment createInstance(ProductModel product) {
        DetailProductsFragment fragment = new DetailProductsFragment();
        Bundle args = new Bundle();
        args.putSerializable(PRODUCT_DETAIL,product);
        fragment.setArguments(args);
        return fragment;
    }

    public  void addToCart() {
        UserItem user = GeneralFunctions.getCurrentUser(getContext());
        CartRequest request = new CartRequest();
        request.setId_product(getDetail().getIdProduct());
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

                    // onSuccess();
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

    private void getProductImages(String idProduct){
        HashMap<String,String> params = new HashMap<>();
        //text
        params.put("method","getImagesByIdProduct");
        params.put("idProduct", idProduct);
        VolleyPostRequest(Constants.GET_PRODUCTS, params, METHOD_GET_PRODUCT_IMAGES);
    }

    /**********************************Auto Scrolling**************************************************/
    public void start() {
        if (timer != null) {
            timer.cancel();
        }
        position = 0;
        startSlider();
    }
    public void stop() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startSlider() {
        timer = new Timer();
        if (getActivity()!=null) {
            timer.scheduleAtFixedRate(new TimerTask() {

                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (images_publication != null) {
                                if (position > images_publication.length) { // longitud del arreglo de urls
                                    position = 0;
                                    viewPager.setCurrentItem(position++);
                                } else {
                                    viewPager.setCurrentItem(position++);
                                }
                            }

                        }
                    });


                }

            }, 0, DURATION);
        }
    }
// Stops the slider when the Activity is going into the background

    @Override
    public void onPause() {
        super.onPause();
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    /************************************************adapter**********************************************************************/
    private class CardsPagerAdapter extends PagerAdapter {

        private boolean mIsDefaultItemSelected = false;
        private ImageSliderPublication[] gallery;
        public CardsPagerAdapter(ImageSliderPublication[] images){
            gallery = images;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView cardImageView = (ImageView) View.inflate(container.getContext(), R.layout.imageview_card, null);
            ImageSliderPublication temp =  gallery[position];
            Glide.with(cardImageView.getContext())
                    .load(temp.getPath()+temp.getImageName())
                    .centerCrop()
                    .into(cardImageView);

            cardImageView.setTag(position);
            cardImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startGalleryActivity();
                }
            });
            container.addView(cardImageView);
            return cardImageView;
        }
        public void startGalleryActivity(){
            Intent intent = new Intent(getActivity(), GalleryActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.GALLERY,gallery);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return gallery.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

}
