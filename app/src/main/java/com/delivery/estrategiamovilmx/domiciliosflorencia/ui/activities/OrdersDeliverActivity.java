package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PendingOrdersFragment;

import java.util.ArrayList;
import java.util.List;

public class OrdersDeliverActivity extends AppCompatActivity {
    private static final String TAG = OrdersDeliverActivity.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String previous_flow = "";
    private Adapter adapter;
    public static final String ALL_ORDERS = "all";
    public static final String ALL_ORDERS_CLIENT = "all_client";
    public static final String ALL_ORDERS_ADMIN = "all_admin";
    public static final String BY_ID_USER_ORDERS = "byId";
    public static int SIGNATURE_TAKED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_deliver);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        previous_flow = intent.getStringExtra(Constants.flow);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    private void setupViewPager(ViewPager viewPager) {
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        adapter = new Adapter(getSupportFragmentManager());
        if (user!=null) {
            switch (user.getProfile()){
                case Constants.profile_admin:
                    Log.d("ordenesdebug", "entra en admin");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS_ADMIN), "ADMINISTRAR ORDENES");
                    break;
                case Constants.profile_client:Log.d("ordenesdebug","entra en client");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS_CLIENT), "MIS ENVIOS");
                    break;
                case Constants.profile_deliver_man:Log.d("ordenesdebug","entra en deliverman");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS), "ACTIVAS");
                    adapter.addFragment(PendingOrdersFragment.newInstance(BY_ID_USER_ORDERS), "MIS PENDIENTES");
                    break;
                default:break;
            }

        }
        viewPager.setAdapter(adapter);

    }
    static class Adapter extends FragmentPagerAdapter {
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refresh:

                int pos = viewPager.getCurrentItem();
                Fragment fr = adapter.getItem(pos);
                PendingOrdersFragment pfr = (PendingOrdersFragment) fr;
                pfr.reloadFragment();
                return true;
            case R.id.action_orders_config:
                filters_Popup(getApplicationContext(), OrdersDeliverActivity.this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (OrdersDeliverActivity.SIGNATURE_TAKED== 1) {
            OrdersDeliverActivity.SIGNATURE_TAKED= 0;
        }
        Log.d(TAG,"onBackPressed...VALOR DE previous_flow:"+previous_flow);
        if (previous_flow.equals(CongratsActivity.flow_congrats) || previous_flow.equals(ReplyActivity.flow_notification)) {Log.d(TAG,"CASO 1...");
            finish();
            NavUtils.navigateUpTo(OrdersDeliverActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }else{Log.d(TAG,"CASO 2...");super.onBackPressed();}
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
        String current_days = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.days_to_show_orders);
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
        int pos = viewPager.getCurrentItem();
        Fragment fr = adapter.getItem(pos);
        PendingOrdersFragment pfr = (PendingOrdersFragment) fr;
        pfr.reloadFragment();
    }
}
