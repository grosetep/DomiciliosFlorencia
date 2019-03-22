package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.MerchantItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.StringOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.OfferFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.ProductsFragment;

import java.util.ArrayList;
import java.util.List;

public class StoreActivity extends AppCompatActivity {
    private static final String TAG = StoreActivity.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private View mCustomView;
    public Toolbar toolbar;
    private ImageView image_header;
    private String type_service;
    private MerchantItem merchant;
    /*merchant profile*/
    private TextView text_name_bussiness;
    private TextView text_important;
    private TextView text_cost_delivery;
    private TextView text_tpo_delivery;

    public static final String flow_main = "flow_main";
    public static final String flow_no_registered = "flow_no_registered";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent i = getIntent();
        merchant = (MerchantItem) i.getExtras().getSerializable(Constants.MERCHANT_OBJECT);
        type_service = i.getExtras().getString(Constants.TYPE_SERVICE);
        //Log.d(TAG,"establecimiento:"+merchant!=null?merchant.toString():"null");

        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowTitleEnabled(true);
        ab.setTitle("");

        initGUI();
        loadMerchantProfile(merchant);
    }
    private void makeCall(String phone) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            String[] PERMISSIONS = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE};

            if (!UtilPermissions.hasPermissions(this, PERMISSIONS)) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, UtilPermissions.PERMISSION_ALL);
            }
        }
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_cart:
                UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
                if (user!=null) {
                    Intent i = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                    Bundle args = new Bundle();
                    args.putSerializable(Constants.MERCHANT_OBJECT,merchant);
                    i.putExtras(args);
                    startActivity(i);
                }else{
                    Intent i = new Intent(getBaseContext(),LoginActivity.class);
                    i.putExtra(Constants.flow,flow_no_registered);
                    startActivity(i);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    private void initGUI(){
        image_header = findViewById(R.id.image_header);
        text_name_bussiness = findViewById(R.id.text_name_bussiness);
        text_important = findViewById(R.id.text_important);
        text_cost_delivery = findViewById(R.id.text_cost_delivery);
        text_tpo_delivery = findViewById(R.id.text_tpo_delivery);

        if (merchant!=null) {

            String ImagePath = merchant.getImagePath() + merchant.getImageName();
            Glide.with(getApplicationContext())
                    .load(ImagePath)
                    .into(image_header);
        }

        viewPager = findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }
        tabLayout = findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);
    }
    private void loadMerchantProfile(MerchantItem item){
        String id_country = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.id_country);
        if (item!=null) {
            text_name_bussiness.setText(item.getNameBussiness());
            text_important.setText(item.getImportant());
            text_cost_delivery.setText(getString(R.string.prompt_shipping_cost, StringOperations.getAmountFormat(item.getDeliveryCost(), id_country)));
            text_tpo_delivery.setText(getString(R.string.prompt_shipping_time, item.getTpoDelivery()));
        }else{ShowConfirmations.showConfirmationMessage(getString(R.string.promt_object_not_found),StoreActivity.this);
        }
    }
    private void setupViewPager(ViewPager viewPager) {
        Adapter  adapter = new Adapter (getSupportFragmentManager());
        String tabTitle = "";

        if (type_service!=null && type_service.equals(Constants.service_restaurants)){
            tabTitle = getString(R.string.title_menu);
        }else{
            tabTitle = getString(R.string.title_aisle);
        }

        // adapter.addFragment(MenuFragment.createInstance("init"),"MENU");
        adapter.addFragment(ProductsFragment.createInstance(merchant),tabTitle);
        adapter.addFragment(OfferFragment.createInstance(merchant),"PROMOCIONES");
        //adapter.addFragment(DrinksFragment.createInstance(null),"BEBIDAS");

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//fragments in memory
    }
    static class Adapter extends FragmentStatePagerAdapter {
        //FragmentPagerAdapter : Mantiene datos en memoria, destruye fragment cuando no son visibles.
        //FragmentStatePagerAdapter: El fragment se destruye uy solo se guarda su estado, es como listview pero con fragments
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position); //Solo texto en tabs
        }
    }

}
