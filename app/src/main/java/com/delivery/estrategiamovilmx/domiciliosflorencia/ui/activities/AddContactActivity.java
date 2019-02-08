package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.Contact;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.NameFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.NumberFragment;

import java.util.ArrayList;
import java.util.List;

public class AddContactActivity extends AppCompatActivity {
    public static final String NEW_CONTACT = "new_contact";
    private static final String TAG = AddContactActivity.class.getSimpleName();
    public static final String ARG_PAGE = "page";

    private static final int PAGE_NAME = 0;
    private static final int PAGE_NUMBER = 1;
    private ViewPager mPager;
    private AppCompatButton button_next;
    private AppCompatButton button_previous;
    private RelativeLayout layout_loading;
    public static Contact contact = new Contact();
    private ScreenSlidePagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        final Toolbar toolbar_shipping = (Toolbar) findViewById(R.id.toolbar_add_contact);
        setSupportActionBar(toolbar_shipping);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Instantiate a ViewPager and a PagerAdapter.
        contact.setNew(true);
        contact.setSelected(true);
        contact.setIdContact("0");
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPagerAdapter.addFragment(NameFragment.create(PAGE_NAME));
        mPagerAdapter.addFragment(NumberFragment.create(PAGE_NUMBER));
        mPager.setAdapter(mPagerAdapter);
        mPager.addOnPageChangeListener (new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                button_previous.setEnabled(mPager.getCurrentItem() > 0?true:false);
                button_next.setText((mPager.getCurrentItem() == mPagerAdapter.getCount() - 1)?R.string.text_button_done:R.string.text_button_next);
            }
        });
        button_previous = (AppCompatButton) findViewById(R.id.button_previous);
        button_next = (AppCompatButton) findViewById(R.id.button_next);
        layout_loading = (RelativeLayout) findViewById(R.id.layout_loading);
        assignActions();
    }
    private void nextPage(){
        if (mPager.getCurrentItem()<mPagerAdapter.getCount()-1) {
            mPager.setCurrentItem(mPager.getCurrentItem() + 1);
        }
    }
    private void assignActions(){

        button_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( mPager.getCurrentItem()>0)
                    mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });
    }
    private boolean validate(){
        boolean valid = true;
        /*******************************************************************Ultimo paso***************************************************************/
        if (mPager.getCurrentItem()==mPagerAdapter.getCount()-1) {
            //set value referece
            ScreenSlidePagerAdapter dapter_temp= (ScreenSlidePagerAdapter)mPager.getAdapter();
            NumberFragment fragment_number = (NumberFragment)dapter_temp.getFragment(mPager.getCurrentItem());
            if (fragment_number.getData().isEmpty()){
                fragment_number.setError(getString(R.string.error_field_required));
                valid = false;
            }else{
                contact.setPhone(fragment_number.getData());
            }


            //validations


            if (contact.getName()!=null && !contact.getName().isEmpty()){
                if(contact.getPhone()!=null && !contact.getPhone().isEmpty()){
                    Log.d(TAG, "Exito en validaciones, regresar a principal y crear nuevo contacto en lista....");
                    Intent intent = new Intent();
                    Bundle args = new Bundle();
                    Contact new_contact = getContact();
                    args.putSerializable(NEW_CONTACT, new_contact);
                    intent.putExtras(args);
                    setResult(Activity.RESULT_OK,intent);
                    contact = new Contact();
                    finish();
                }else{
                    ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                    Fragment fragment = (Fragment)adapter.getFragment(PAGE_NUMBER);
                    ((NumberFragment) fragment).setError(getString(R.string.error_field_required));
                    mPager.setCurrentItem(PAGE_NUMBER);
                }
            }else{
                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                Fragment fragment = (Fragment)adapter.getFragment(PAGE_NAME);
                ((NameFragment) fragment).setError(getString(R.string.error_field_required));
                mPager.setCurrentItem(PAGE_NAME);
            }
        }else{/************************************************Primer paso***************************************************************/
            if (mPager.getCurrentItem()==0){//esta en el primer paso, NAME

                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                NameFragment fragment = (NameFragment)adapter.getFragment(mPager.getCurrentItem());
                if (fragment.getData().isEmpty()){
                    fragment.setError(getString(R.string.error_field_required));
                    valid = false;
                }else{
                    contact.setName(fragment.getData());
                    nextPage();
                }

            }else{
                /************************************************Pasos intermedios***************************************************************/

                ScreenSlidePagerAdapter adapter= (ScreenSlidePagerAdapter)mPager.getAdapter();
                Fragment fragment = (Fragment)adapter.getFragment(mPager.getCurrentItem());
                /**************Numero interior ***********/


            }

        }
        return valid;
    }
    public static Contact getContact(){return contact;}
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    /**
     * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment} objects, in
     * sequence.
     */
    static class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
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

    }
}
