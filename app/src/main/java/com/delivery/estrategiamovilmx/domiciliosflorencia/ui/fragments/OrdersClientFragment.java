package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class OrdersClientFragment extends Fragment {
    private static final String TAG = OrdersClientFragment.class.getSimpleName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter adapter;
    public static final String ALL_ORDERS = "all";
    public static final String ALL_ORDERS_CLIENT = "all_client";
    public static final String ALL_ORDERS_ADMIN = "all_admin";
    public static final String BY_ID_USER_ORDERS = "byId";

    public OrdersClientFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mPageNumber = getArguments().getInt(AddShippingAddressActivity.ARG_PAGE);
    }
    public static OrdersClientFragment newInstance() {
        Log.d(TAG,"OrdersClientFragment newInstance");
        OrdersClientFragment fragment = new OrdersClientFragment();
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,"OrdersClientFragment onCreateView");
        ViewGroup v = (ViewGroup) inflater
                .inflate(R.layout.fragment_orders_client, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }
    private void setupViewPager(ViewPager viewPager) {
        Log.d(TAG,"OrdersClientFragment setupViewPager");
        adapter = new Adapter(getFragmentManager());
        //valida pestañas a mostrar por tipo de perfil
        //pestañas para repartidores
        UserItem user = GeneralFunctions.getCurrentUser(getContext());

        if (user!=null) {
            switch (user.getProfile()){
                case Constants.profile_admin:Log.d("ordenesdebug","entra en admin");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS_ADMIN), "ADMINISTRAR ORDENES");
                    break;
                case Constants.profile_client:Log.d("ordenesdebug","entra en client");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS_CLIENT), "MIS ENTREGAS");
                    break;
                case Constants.profile_deliver_man:Log.d("ordenesdebug","entra en deliverman");
                    adapter.addFragment(PendingOrdersFragment.newInstance(ALL_ORDERS), "ACTIVAS");
                    adapter.addFragment(PendingOrdersFragment.newInstance(BY_ID_USER_ORDERS), "COMPLETADAS");
                    break;
                default:break;
            }

        }
        if (viewPager!=null) viewPager.setAdapter(null);
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
}
