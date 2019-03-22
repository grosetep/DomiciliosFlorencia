package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Additional;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.DetailPublication;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.PublicationCardViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Variant;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.AddProductRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GetVariantAdditionalResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.DetailProductsFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class AddToCartActivity extends AppCompatActivity {
    private static final String TAG = AddToCartActivity.class.getSimpleName();
    private NestedScrollView container;
    private RelativeLayout container_loading;
    private RelativeLayout no_connection_layout;
    private LinearLayout layout_actions;
    private AppCompatButton button_retry;
    private LinearLayout secondary_container_variants;
    private TextView text_product;
    private EditText text_comment;
    private ImageButton button_less;
    private ImageButton button_add;
    private TextView text_quantity;
    private LinearLayout secondary_container_additionals;
    private AddProductRequest cart;
    private LinearLayout layout_title_variants;
    private LinearLayout layout_title_additionals;
    private ArrayList<Integer> additionals_added = new ArrayList<>();//additional id
    private boolean variants_added = false;
    private List<Additional> additionals_by_product = new ArrayList<>();
    private TextView text_number_prod_added;
    private TextView text_total;
    private LinearLayout layout_button_1;
    private LinearLayout layout_button_2;
    private String total;
    private String variant_added;
    private String id_country;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_cart);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //inicializa
        //obtener id producto y consultar: variantes, adicionales
        //validar si existen variantes: mostrar variantes en lista con check
        //validar si existen adicionales: mostrar adicionales en lista con check y recalcular el costo
        //agrega campo para comentario opcional...
        //agregar control para definir el numero de unidades
        id_country = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.id_country);
        Intent i = getIntent();
        cart = (AddProductRequest) i.getExtras().get(ProductsFragment.CART_OBJECT);
        String product =  i.getExtras().getString(ProductsFragment.PRODUCT_NAME);
        init();
        initProcess(true);

        if (cart!=null){
            text_product.setText(product);
            getVariantsXProduct(cart.getId_product());
        }
    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag ? View.VISIBLE : View.GONE);
        layout_actions.setVisibility(flag? View.GONE: View.VISIBLE);
        container.setVisibility(flag? View.GONE: View.VISIBLE);
        no_connection_layout.setVisibility(View.GONE);
    }
    private void onSuccess(){
        initProcess(false);
        //setupAdapter();
        //calculateTotal();
    }
    private void showNoConnectionLayout(){
        container_loading.setVisibility(View.GONE);
        layout_actions.setVisibility(View.GONE);
        container.setVisibility(View.GONE);
        no_connection_layout.setVisibility(View.VISIBLE);
    }

    private void init(){
        container = (NestedScrollView) findViewById(R.id.container);
        secondary_container_variants = (LinearLayout) findViewById(R.id.secondary_container_variants);
        secondary_container_additionals = (LinearLayout) findViewById(R.id.secondary_container_additionals);
        layout_title_variants = (LinearLayout) findViewById(R.id.layout_title_variants);
        layout_title_additionals = (LinearLayout) findViewById(R.id.layout_title_additionals);
        text_number_prod_added = (TextView) findViewById(R.id.text_number_prod_added);
        text_total = (TextView) findViewById(R.id.text_total);
        text_product = (TextView) findViewById(R.id.text_product);
        text_comment = (EditText) findViewById(R.id.text_comment);
        button_less = (ImageButton) findViewById(R.id.button_less);
        button_add = (ImageButton) findViewById(R.id.button_add);
        text_quantity = (TextView) findViewById(R.id.text_quantity);
        container_loading = (RelativeLayout) findViewById(R.id.container_loading);
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        layout_actions = (LinearLayout) findViewById(R.id.layout_actions);
        layout_actions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //obtener todos los datos del producto y agregarlo al carrito
                initProcess(true);
                if (getTotal().isEmpty()){setTotal(cart.getPrice_product());}
                cart.setTotal(getTotal());
                cart.setComment(text_comment.getText().toString().trim());
                cart.setId_variant(getVariant_added() != null ? getVariant_added() : "");
                cart.setList_additionals(getIdsCommaSeparated(additionals_added));

                Log.d(TAG,"CART:"+cart.toString());
                RestServiceWrapper.shoppingCart(cart, new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                        Log.d(TAG, "Respuesta: " + response);
                        if (response != null && response.isSuccessful()) {
                            GenericResponse cart_response = response.body();
                            if (cart_response != null && cart_response.getStatus().equals(Constants.success)) {

                                initProcess(false);

                                ShowConfirmations.showConfirmationMessage(cart_response.getResult().getMessage(), AddToCartActivity.this);
                                finish();
                            } else if (cart_response != null && cart_response.getStatus().equals(Constants.no_data)) {
                                String response_error = response.body().getMessage();
                                Log.d(TAG, "Mensage:" + response_error);
                                ShowConfirmations.showConfirmationMessage(response_error, AddToCartActivity.this);
                            } else {
                                String response_error = response.message();
                                Log.d(TAG, "Error:" + response_error);
                                initProcess(false);
                                ShowConfirmations.showConfirmationMessage(response_error, AddToCartActivity.this);
                            }

                        } else {
                            initProcess(false);
                            ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), AddToCartActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                        ApiException apiException = new ApiException();
                        try {
                            apiException.setMessage(t.getMessage());
                            initProcess(false);
                        } catch (Exception ex) {
                            // do nothing
                        }
                    }
                });
            }
        });
        layout_button_1 = (LinearLayout) findViewById(R.id.layout_button_1);
        layout_button_2 = (LinearLayout) findViewById(R.id.layout_button_2);
        button_retry = (AppCompatButton) findViewById(R.id.button_retry);
        button_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initProcess(true);
                getVariantsXProduct(cart.getId_product());
            }
        });
        button_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int units_user = Integer.parseInt(text_quantity.getText().toString());
                if (units_user==1){
                    return;
                }else {
                    units_user = units_user - 1;
                    cart.setUnits(String.valueOf(units_user));
                    text_quantity.setText("" + units_user);
                    calculateTotal();
                }
            }
        });
        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int units = Integer.parseInt(text_quantity.getText().toString());
                units = units +1;
                cart.setUnits(String.valueOf(units));
                text_quantity.setText(cart.getUnits());
                calculateTotal();
            }
        });
        additionals_added = new ArrayList<>();
        variants_added = false;
        text_total.setText(StringOperations.getAmountFormat(cart.getPrice_product(),id_country));
        text_number_prod_added.setText(getString(R.string.promt_add_to_cart,cart.getUnits()));
    }

    public String getTotal() {
        return total==null?"":total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    private void calculateTotal(){
        Float total = 0f;
        Float total_additionals = 0f;
        Float price = 0f;
        price = Float.parseFloat(cart.getPrice_product()) * Integer.parseInt(cart.getUnits().trim());
        total = total + price;
        //sumar costos de adicionales
        //recorre lista de adicionales agregados y sumar precios al total
        for(int id_additional:additionals_added){
            int index_of_additional_object = getAdditionalIndex(id_additional);//12
            total_additionals = total_additionals  + Float.parseFloat(additionals_by_product.get(index_of_additional_object).getPrice());
        }

        total_additionals = total_additionals * Integer.parseInt(cart.getUnits());
        total = total + total_additionals;

        setTotal(String.valueOf(total));
        text_number_prod_added.setText(getString(R.string.promt_add_to_cart,cart.getUnits()));
        text_total.setText(StringOperations.getAmountFormat(String.valueOf(total),id_country));
    }
    private int getAdditionalIndex(int id_additional){

        for(int i=0;i<additionals_by_product.size();i++){
            if(additionals_by_product.get(i).getIdAdditional().equals("" + id_additional)){
                return i;
            }
        }
        return -1;
    }
    private void getVariantsXProduct(String id_product) {
        //Log.d(TAG,"getVariantsXProduct....."+id_product);
        //consultar variantes y adicionales
        additionals_added = new ArrayList<>();
        variants_added = false;
        RestServiceWrapper.getVariantsXProduct(id_product, new Callback<GetVariantAdditionalResponse>() {
            @Override
            public void onResponse(Call<GetVariantAdditionalResponse> call, retrofit2.Response<GetVariantAdditionalResponse> response) {

                if (response != null && response.isSuccessful()) {
                    GetVariantAdditionalResponse cart_response = response.body();
                    if (cart_response != null && cart_response.getStatus().equals(Constants.success)) {

                        if (cart_response.getResult().getVariants() != null && cart_response.getResult().getVariants().size() > 0) {
                            //create variants list
                            generateVariants(cart_response.getResult().getVariants());
                        }else{hideVariants();}
                        if (cart_response.getResult().getAdditionals() != null && cart_response.getResult().getAdditionals().size() > 0) {
                            //create additionals list
                            additionals_by_product = cart_response.getResult().getAdditionals();
                            generateAdditionals((ArrayList<Additional>) additionals_by_product);
                        }else{hideAdditionals();}
                        onSuccess();
                    } else if (cart_response != null && cart_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        initProcess(false);
                        ShowConfirmations.showConfirmationMessage(response_error, AddToCartActivity.this);
                    } else {
                        String response_error = response.message();
                        ShowConfirmations.showConfirmationMessage(response_error, AddToCartActivity.this);
                        initProcess(false);
                    }
                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), AddToCartActivity.this);
                    initProcess(false);
                }
            }

            @Override
            public void onFailure(Call<GetVariantAdditionalResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());
                    ShowConfirmations.showConfirmationMessage(apiException.getMessage(), AddToCartActivity.this);
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    private void hideVariants(){
        layout_title_variants.setVisibility(View.GONE);
        secondary_container_variants.setVisibility(View.GONE);
        enableButtonAddToCart();
    }
    private void enableButtonAddToCart(){
        //sino hay variantes se pone por defaul y se habilita el boton de agregar
        variants_added = true;
        layout_actions.setClickable(true);
        layout_button_1.setBackgroundColor(Color.parseColor(Constants.color_secondary));
        layout_button_2.setBackgroundColor(Color.parseColor(Constants.color_secondary));
    }
    private void hideAdditionals(){
        layout_title_additionals.setVisibility(View.GONE);
        secondary_container_additionals.setVisibility(View.GONE);
    }
    private void generateVariants(List<Variant> variants){
        RadioGroup dynamicRadiogroup = new RadioGroup(AddToCartActivity.this);

        for (Variant v:variants) {
            final RadioButton radio = new RadioButton(this);
            radio.setText(v.getVariante());
            radio.setTextSize(16);
            radio.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            radio.setId(Integer.parseInt(v.getIdVariante().trim()));
            radio.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutparams_temp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            radio.setTextColor(Color.parseColor(Constants.negro));
            radio.setPadding(0, 30, 0, 30);
            radio.setLayoutParams(layoutparams_temp);
            dynamicRadiogroup.addView(radio);
        }
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        dynamicRadiogroup.setLayoutParams(layoutparams);

        dynamicRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //Log.d(TAG, "onCheckedChanged....checkedId:" + checkedId);
                enableButtonAddToCart();
                setVariant_added(String.valueOf(checkedId));
            }
        });

        secondary_container_variants.addView(dynamicRadiogroup);
        if (secondary_container_variants.getVisibility()==View.INVISIBLE)
            secondary_container_variants.setVisibility(View.VISIBLE);

    }

    public String getVariant_added() {
        return variant_added;
    }

    public void setVariant_added(String variant_added) {
        this.variant_added = variant_added;
    }

    private int getIndexAdditionalAdded(int id_additional){
        for(int i =0;i<additionals_added.size();i++){
            if (String.valueOf(additionals_added.get(i)).equals(String.valueOf(id_additional))) {
                return i;
            }
        }
        return -1;
    }
    private void generateAdditionals(final ArrayList<Additional> additionals){

        for (Additional a:additionals){
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            LinearLayout additional_row = new LinearLayout(this);
            additional_row.setLayoutParams(layoutparams);
            additional_row.setOrientation(LinearLayout.HORIZONTAL);
            additional_row.setWeightSum(100);
            //complemento

            final CheckBox check = new CheckBox(this);
            check.setId(Integer.parseInt(a.getIdAdditional()));
            check.setText(a.getAdditional());
            check.setTextSize(16);
            check.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
            check.setPadding(0, 30, 0, 30);
            LinearLayout.LayoutParams layoutparams_temp = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,30);
            check.setLayoutParams(layoutparams_temp);
            check.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (check.isChecked()) {//agrega adicional a lista de seleccionados
                        additionals_added.add(check.getId());
                        calculateTotal();
                    }else{
                        int index = getIndexAdditionalAdded(check.getId());
                        additionals_added.remove(index);
                        calculateTotal();
                    }
                }
            });
//------------------------------------------------------------------------------------------------
            //precio
            TextView price = new TextView(this);
            LinearLayout.LayoutParams layoutparams_temp_price = new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,70);
            price.setTextColor(Color.parseColor(Constants.gris_claro));
            String price_label = "+"+StringOperations.getAmountFormat(a.getPrice(),id_country);
            price.setLayoutParams(layoutparams_temp_price);
            price.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
            price.setPadding(0, 30, 10, 30);//left,top,rigth,bottom
            price.setGravity(Gravity.CENTER);
            price.setTypeface(null, Typeface.BOLD);
            price.setText(price_label);
            price.setTextSize(18);//MediumAppearence
            //add views
            additional_row.addView(check);
            additional_row.addView(price);

            secondary_container_additionals.addView(additional_row);
        }

        if (secondary_container_additionals.getVisibility()==View.INVISIBLE)
            secondary_container_additionals.setVisibility(View.VISIBLE);
    }
    private String getIdsCommaSeparated(ArrayList<Integer> list) {
        StringBuffer buffer = new StringBuffer();
        if (list.size()>0) {
            if (list.size()==1) {
                buffer.append(list.get(0));
            }
            else {
                for (int i = 0; i < list.size(); i++) {
                    String id = list.get(i).toString();
                    if (i == list.size()) {
                        buffer.append(id);
                    } else {
                        buffer.append(id.concat(","));
                    }
                }
                //delete las comma
                buffer.deleteCharAt(buffer.length()-1);
            }
        }
        return buffer.toString();
    }

    @Override
    public void onBackPressed() {
        open();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                open();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.prompt_discart_promt_title));
        alertDialogBuilder.setTitle(getString(R.string.prompt_discart_promt));
        alertDialogBuilder.setPositiveButton(getString(R.string.prompt_action_discart), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });

        alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

