package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
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
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.ConfigItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.FireBaseOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UtilPermissions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.HowToFragment;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.PrincipalMenuFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    public Toolbar toolbar;
    public static final String flow_main = "flow_main";
    public static final String flow_no_registered = "flow_no_registered";

    /*private ViewPager viewPager;
    private TabLayout tabLayout;*/

    public Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);


       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String json_user = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.user_object);
                Log.d(TAG,"json_object:"+json_user);
                if (json_user==null || json_user.isEmpty()){
                    finish();
                    Intent i = new Intent(getBaseContext(),LoginActivity.class);
                    i.putExtra(Constants.flow,MainActivity.flow_no_registered);
                    startActivity(i);
                }else {
                    Intent i = new Intent(getApplicationContext(), NewOrderActivity.class);
                    startActivity(i);
                }
            }
        });*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        initGUI();
        loadProfile();
        subscribeUser();

    }
    private void prepareCall(){
        ConfigItem config = GeneralFunctions.getConfiguration(getApplicationContext());
        if (config!=null)
            makeCall(config.getPhone());
        else
            makeCall(getString(R.string.shipping_phone));
    }

    private void subscribeUser(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        if (user!=null){//usuario logueado, suscribir al topic de su perfil
            FireBaseOperations.subscribe(getApplicationContext(), user.getProfile());
        }else{//no logueado, suscribir a client y quitar de los otros
            FireBaseOperations.subscribe(getApplicationContext(), Constants.profile_client);
        }
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
    private void loadProfile(){
        Gson gson = new Gson();
        String json_user = ApplicationPreferences.getLocalStringPreference(MainActivity.this, Constants.user_object);
        Log.d(TAG, "loadProfile--->");
        if (json_user!=null){
            //get user saved
            UserItem user = gson.fromJson(json_user,UserItem.class);


            if (user!=null && user.getIdUser()!=null) {
                //usuario ya logueado, recuperar info
                Log.d(TAG, "Carga datos login:::::::::::::::::::::::::::::" + user.toString());
                updateNavigationView(user);
            }else{//cierre de session, no existe idUser
                Log.d(TAG,"Usuario no logueado valor ID local: ");
                updateNavigationView(null);
            }
        }
    }
    public void updateNavigationView(UserItem user) {

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);
        if (hView != null) {
            final TextView text_name_user = (TextView) hView.findViewById(R.id.text_name_user);
            TextView text_email_user = (TextView) hView.findViewById(R.id.text_email_user);
            ImageView image_profile = (ImageView) hView.findViewById(R.id.image_profile);
            Log.d(TAG,"Datos del usuario a mostrar-------------------------------------------------------------------------------------------------------");

            final String user_name = ((user!=null && !user.getName().isEmpty())?user.getName():getResources().getString(R.string.text_user_guess));
            text_email_user.setText((user != null) ? user.getEmail() : "");
            if (user!=null) {
                Log.d(TAG,"name:"+user.getName() + " user_name:"+user_name + " image:"+user.getAvatarPath()+user.getAvatarImage()+" profile:"+user.getProfile());
                final boolean show_tip = (user!=null && (user.getName()==null || user.getName().isEmpty()));
                final String profile = user.getProfile().equals(Constants.profile_client) ? "" : " - ".concat(user.getProfile());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_name_user.setText((show_tip) ? getResources().getString(R.string.text_user_guess_tip).concat(profile) : user_name.concat(profile));
                    }
                });
                loadProfileImage(user, image_profile);
            } else {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        text_name_user.setText(getResources().getString(R.string.text_user_guess));
                    }
                });
            }

        }
    }
    private void loadProfileImage(UserItem user,ImageView image_profile){
        Log.d(TAG,"loadProfileImage....................."+user.getAvatarPath()+user.getAvatarImage());
        if (user!=null && user.getAvatarPath()!=null && user.getAvatarImage()!=null){//existe ya un usuario logueado
            Glide.with(image_profile.getContext())
                    .load(user.getAvatarPath()+user.getAvatarImage())
                    .into(image_profile);

        }else{//usuario invitado
            Glide.with(image_profile.getContext())
                    .load(R.drawable.ic_account_circle)
                    .into(image_profile);
        }
    }
    /*private void initGUI2(){
        //init viewpager
        viewPager = (ViewPager) findViewById(R.id.viewpager_principal);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }


        //init bottomNavigation
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        //bottomNavigation.inflateMenu(R.menu.bottom_menu);
        fragmentManager = getSupportFragmentManager();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.action_orders://solo si el perfil es repartidor, sino ..
                        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());

                        if (user != null && user.getIdUser() != null) {
                            viewPager.setCurrentItem(0);
                        } else {
                            redirectToLogin();
                        }

                        break;
                    case R.id.action_profile:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.action_info:
                        viewPager.setCurrentItem(2);
                        break;
                }

                return true;
            }
        });

    }*/
    /*private void setupViewPager(ViewPager viewPager) {
        Adapter  adapter = new Adapter (getSupportFragmentManager());

        // adapter.addFragment(MenuFragment.createInstance("init"),"MENU");
        adapter.addFragment(PendingOrdersFragment.newInstance(OrdersClientFragment.ALL_ORDERS), "ACTIVAS");
        adapter.addFragment(ProfileFragment.newInstance(MainActivity.this),"PERFIL");
        adapter.addFragment(new OfferFragment(),"COMO FUNCIONA");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(3);//fragments in memory
    }*/
    private void initGUI(){
        bottomNavigation = findViewById(R.id.bottom_navigation);
        //bottomNavigation.inflateMenu(R.menu.bottom_menu);
        fragmentManager = getSupportFragmentManager();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    /*case R.id.action_orders://solo si el perfil es repartidor, sino ..
                        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());

                        if (user != null && user.getIdUser() != null) {
                            fragment = PendingOrdersFragment.newInstance(OrdersClientFragment.ALL_ORDERS);//user.getProfile()usar el profile para mostrar unos u otros fragmentos
                        }else{
                            redirectToLogin();
                        }

                        break;*/
                    case R.id.init:
                        fragment = new PrincipalMenuFragment();
                        break;
                    case R.id.action_info:
                        fragment = new HowToFragment();
                        break;
                }
                final FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.main_container, fragment).commit();
                return true;
            }
        });
        bottomNavigation.setSelectedItemId(R.id.init);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
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
        if (id == R.id.action_cart) {
            UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
            if (user!=null) {
                Intent i = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                startActivity(i);
            }else{
                Log.d(TAG,"onOptionsItemSelected CASO 1");
                Intent i = new Intent(getBaseContext(),LoginActivity.class);
                i.putExtra(Constants.flow,flow_no_registered);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            redirectToLogin();

        }else if (id == R.id.nav_profile) {
            Intent i = new Intent(this, ProfileActivity.class);
            i.putExtra(Constants.flow, flow_main);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else if (id == R.id.nav_orders) {
            UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
            if (user != null && user.getIdUser() != null) {
                Intent i;

                //if ((Constants.app_label+user.getProfile()).equals(Constants.profile_deliver_man)) {
                    i = new Intent(this, OrdersDeliverActivity.class);
                //} else {
                  //  i = new Intent(this, OrdersActivity.class);
                //}
                i.putExtra(Constants.flow, flow_main);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
            else {

                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(Constants.flow, MainActivity.flow_no_registered);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
        else if (id == R.id.nav_location) {
            Intent i = new Intent(this,LocationActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }else if(id == R.id.nav_exit){
            Intent i = new Intent(this,SignOutActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void redirectToLogin(){
        Intent intent = new Intent(this,LoginActivity.class);
        intent.putExtra(Constants.flow,flow_main);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    static class Adapter extends FragmentStatePagerAdapter {
        //FragmentPagerAdapter : Mantiene datos en memoria, destruye fragment cuando no son visibles.
        //FragmentStatePagerAdapter: El fragment se destruye y solo se guarda su estado, es como listview pero con fragments
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
