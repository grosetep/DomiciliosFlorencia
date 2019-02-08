package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.CategoryItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.CategoryViewModel;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.CategoryResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.DividerItemDecoration;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SelectCategoryActivity extends AppCompatActivity {
    public static final String CATEGORY_SELECTED = "category_selected";
    private static final String TAG = SelectCategoryActivity.class.getSimpleName();
    private RecyclerView recyclerview_category;
    private List<CategoryItem> categories;
    private SelectCategoryAdapter mAdapter;
    private ProgressBar pbLoading_sel_category;
    private CategoryViewModel category_selected;
    private LinearLayout layout_all_subcategories;
    private String type_flow;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_category);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_category);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerview_category = (RecyclerView) findViewById(R.id.recyclerview_category);
        recyclerview_category.addItemDecoration(new DividerItemDecoration(this));
        pbLoading_sel_category = (ProgressBar) findViewById(R.id.pbLoading_sel_category);
        initGUI();
        /*getting type flow to show or hide options*/
        Intent i = getIntent();
        type_flow = i.getStringExtra(ProductsFragment.TYPE_FLOW_CATEGORY);
        if (type_flow.equals(ProductsFragment.FLOW_PRODUCTS)) {//show all_categories layout
            layout_all_subcategories.setVisibility(View.VISIBLE);
        }
        initProcess(true);
        getCategories();
    }
    private void initGUI(){
        layout_all_subcategories = (LinearLayout) findViewById(R.id.layout_all_subcategories);
        layout_all_subcategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle args = new Bundle();
                //category
                //CategoryViewModel category_selected_all = new CategoryViewModel("0",getString(R.string.promt_all_categories),"","");
                //subcategory
                //SubCategory sub_category = new SubCategory("-1","-1","","");
                //sub_sub_category
                //SubSubCategory sub_sub_category = new SubSubCategory("-1","-1","");
                //setting objects,
                CategoryItem category_selected_all = new CategoryItem();
                category_selected_all.setCategory(getString(R.string.promt_all_categories));
                category_selected_all.setIdCategory("0");
                Log.d(TAG, "retorno de categorias todas: cat:" + category_selected_all.getIdCategory());
                args.putSerializable(SelectCategoryActivity.CATEGORY_SELECTED, category_selected_all);
                /*args.putSerializable(Constants.SUBCATEGORY_SELECTED,sub_category);
                args.putSerializable(Constants.SUBSUBCATEGORY_SELECTED,sub_sub_category);*/
                intent.putExtras(args);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getCategories() {//obtener datos de la publicacion mas datos de las url de imagenes
        RestServiceWrapper.getCategories(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, retrofit2.Response<CategoryResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    CategoryResponse products_response = response.body();
                    if (products_response != null && products_response.getStatus().equals(Constants.success)) {

                        if (products_response.getResult().size() > 0) {
                            processingResponse(products_response.getResult());
                        }

                    } else if (products_response != null && products_response.getStatus().equals(Constants.no_data)) {
                        String response_error = response.body().getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                    } else {
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                    }

                } else {
                    ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login, getString(R.string.error_generic)), SelectCategoryActivity.this);
                }
            }

            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());

                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
        /*String url = Constants.GET_CATEGORIES+"?method=getCategories";
        JsonObjectRequest request =  new JsonObjectRequest(Request.Method.GET,
                url,
                (String) null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        processingResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        String message = VolleyErrorHelper.getErrorType(error, SelectCategoryActivity.this);
                        Log.d(TAG, "Error Volley: " + error.toString() + " ----- " + error.getCause() + " mensaje usr: " + message);
                        Toast.makeText(
                                SelectCategoryActivity.this,
                                message,
                                Toast.LENGTH_SHORT).show();

                    }
                });
        request.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        VolleySingleton.
                getInstance(SelectCategoryActivity.this).
                addToRequestQueue(
                        request
                );*/
    }
    private void processingResponse( List<CategoryItem>  list) {


        categories = list;
        // Inicializar adaptador
        recyclerview_category.setLayoutManager(new LinearLayoutManager(recyclerview_category.getContext()));
        mAdapter = new SelectCategoryAdapter(SelectCategoryActivity.this, categories);
        //mAdapter.setIdsDrawableCategories(setIconsCategories(categories));
        recyclerview_category.setAdapter(mAdapter);
        initProcess(false);
    }
    /*private HashMap<String , String> setIconsCategories( CategoryViewModel[] categories ){
        HashMap<String , String> map=new HashMap<>();
        for(CategoryViewModel c:categories){
            //Log.d(TAG,"id:"+c.getIdCategory()+" image:" + c.getImageCategory() + " path: " + c.getPathImageCategory() + " category:"+c.getCategory());
            if (c.getPathImageCategory().equals(Constants.imageCategoryInLocalPath)) {//imagen desde recursos locales
                int resID = getResources().getIdentifier(c.getImageCategory().toString(), "drawable", getPackageName());
                map.put(c.getIdCategory(), "" + resID);
            }
            else{//imagen de servidor remoto
                map.put(c.getIdCategory(),c.getPathImageCategory() + c.getImageCategory());
            }

        }
        return map;
    }*/

    private void initProcess(boolean flag){
        if (pbLoading_sel_category!=null && recyclerview_category!=null){
            recyclerview_category.setVisibility(flag?View.GONE:View.VISIBLE);
            pbLoading_sel_category.setVisibility(flag?View.VISIBLE:View.GONE);

        }
    }





    /****************************************Adapter************************************************************************/
    class SelectCategoryAdapter extends RecyclerView.Adapter<SelectCategoryAdapter.CategoryViewHolder> {

        private final LayoutInflater mInflater;
        private final List<CategoryItem> mModels;
        private int mBackground;
        private final TypedValue mTypedValue = new TypedValue();
        //private HashMap<String, String> idsDragableCategories = new HashMap<>();
        private Activity activity;

        public class CategoryViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvText;
            private String idCategory;
            private ImageView imageViewCircle;

            public CategoryViewHolder(View itemView) {
                super(itemView);
                tvText = (TextView) itemView.findViewById(R.id.text_category);
                idCategory = "";
                imageViewCircle = (ImageView) itemView.findViewById(R.id.image_category);
            }

            public void bind(CategoryItem model) {
                tvText.setText(model.getCategory());
                idCategory = model.getIdCategory();
            }
        }

        public SelectCategoryAdapter(Activity act, List<CategoryItem> models) {//eliminar:CategoryViewModel sino se usa con imagenes
            activity = act;
            act.getApplicationContext().getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mInflater = LayoutInflater.from(activity);
            mModels = new ArrayList<>(models);
        }

        @Override
        public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View itemView = mInflater.inflate(R.layout.select_category_layout, parent, false);
            itemView.setBackgroundResource(mBackground);
            return new CategoryViewHolder(itemView);
        }

        private int getRandomShape() { return R.drawable.shape_icon_red;
        }

        @Override
        public void onBindViewHolder(final CategoryViewHolder holder, int position) {
            Context context = holder.imageViewCircle.getContext();
            final CategoryItem model = mModels.get(position);

            holder.bind(model);
            holder.imageViewCircle.setBackgroundResource(getRandomShape());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    args.putSerializable(SelectCategoryActivity.CATEGORY_SELECTED, model);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                   /* category_selected = model;
                    Intent intent = new Intent(v.getContext(), SelectSubCategoryActivity.class);
                    intent.putExtra(Constants.CATEGORY_SELECTED, model.getIdCategory());
                    intent.putExtra(FindBusinessFragment.TYPE_FLOW_CATEGORY, type_flow);
                    activity.startActivityForResult(intent, PublishFragment.SELECT_SUBCATEGORY);*/
                }
            });

            /*Glide.with(holder.imageViewCircle.getContext())
                    .load(mModels.get(position).getPathImageCategory().equals(Constants.imageCategoryInLocalPath) ? new Integer(idsDragableCategories.get(mModels.get(position).getIdCategory())) : idsDragableCategories.get(mModels.get(position).getIdCategory())) //mModels.get(position).getPathImageCategory() + mModels.get(position).getImageCategory()
                    .fitCenter()
                    .into(holder.imageViewCircle);*/
        }

        /*public void setIdsDrawableCategories(HashMap<String, String> mapIconsDrawables) {
            this.idsDragableCategories = mapIconsDrawables;
        }*/

        @Override
        public int getItemCount() {
            return mModels.size();
        }

    }
}

