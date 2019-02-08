package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.LoginActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.MainClassificationsActivity;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities.NewOrderActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrincipalMenuFragment extends Fragment {

    private CardView card_view_super;
    private CardView card_view_restaurants;
    private CardView card_view_custom;
    private CardView card_view_send_something;
    private CardView card_view_drugstore;

    public final static String BUSINESS_AREA = "business_area";
    public PrincipalMenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_principal_menu, container, false);
        //ViewPager pager = v.findViewById(R.id.photos_viewpager);
        //ArrayList<Fragment> fragments = new ArrayList<>();
        //fragments.add(new ShippingServicesFragment());
        //fragments.add(new ShippingServicesFragment());
        //pager.setAdapter(new PrincipalSlidePagerAdapter(getFragmentManager(), fragments));

        //TabLayout tabLayout = v.findViewById(R.id.tab_layout);
        //tabLayout.setupWithViewPager(pager, true);

        init(v);
        return v;
    }
    /*static class PrincipalSlidePagerAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments = null;

        public PrincipalSlidePagerAdapter(FragmentManager fm,List<Fragment> fragments) {
            super(fm);
            mFragments = fragments;
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
            notifyDataSetChanged();
        }

        public void removeFragment(int index) {
            mFragments.remove(index);
            notifyDataSetChanged();
        }
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        public Fragment getFragment(int key){
            return mFragments.get(key);
        }
        @Override
        public int getCount() {
            return mFragments.size();
        }

    }*/
    private void init(View v){
        /*button_new_order = v.findViewById(R.id.button_new_order);
        button_new_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                //Log.d("PrincipalMenuFragment", "json_object:" + json_user);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), NewOrderActivity.class);
                    startActivity(i);
                }
            }
        });*/

        card_view_super = v.findViewById(R.id.card_view_super);
        card_view_super.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), MainClassificationsActivity.class);
                    i.putExtra(BUSINESS_AREA,Constants.service_supers);
                    startActivity(i);
                }
            }
        });
        card_view_restaurants = v.findViewById(R.id.card_view_restaurants);
        card_view_restaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), MainClassificationsActivity.class);
                    i.putExtra(BUSINESS_AREA,Constants.service_restaurants);
                    startActivity(i);
                }
            }
        });

        card_view_custom = v.findViewById(R.id.card_view_custom);
        card_view_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), MainClassificationsActivity.class);
                    i.putExtra(BUSINESS_AREA,Constants.service_custom);
                    startActivity(i);
                }
            }
        });
        card_view_send_something = v.findViewById(R.id.card_view_send_something);
        card_view_send_something.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), NewOrderActivity.class);
                    i.putExtra(BUSINESS_AREA,Constants.service_mototaxi);
                    startActivity(i);
                }
            }
        });
        card_view_drugstore = v.findViewById(R.id.card_view_drugstore);
        card_view_drugstore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String json_user = ApplicationPreferences.getLocalStringPreference(getContext(), Constants.user_object);
                if (json_user==null || json_user.isEmpty()){
                    Intent i = new Intent(getContext(),LoginActivity.class);
                    i.putExtra(Constants.flow, MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getContext(), MainClassificationsActivity.class);
                    i.putExtra(BUSINESS_AREA,Constants.service_drugstore);
                    startActivity(i);
                }
            }
        });
    }
}
