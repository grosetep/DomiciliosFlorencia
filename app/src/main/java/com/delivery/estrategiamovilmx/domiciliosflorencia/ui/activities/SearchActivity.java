package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.fragments.SearchFragment;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = SearchActivity.class.getSimpleName();
    private View mCustomView;
    private String type_service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Intent i = getIntent();
        type_service = i.getStringExtra(Constants.TYPE_SERVICE);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setContentInsetsAbsolute(0,0);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayOptions(
                getSupportActionBar().DISPLAY_SHOW_CUSTOM,
                getSupportActionBar().DISPLAY_SHOW_CUSTOM |  getSupportActionBar().DISPLAY_SHOW_HOME
                        |  getSupportActionBar().DISPLAY_SHOW_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(this);
        mCustomView = mInflater.inflate(R.layout.actionbar_custom_view_search_action, null);
        mCustomView.findViewById(R.id.image_search).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        mCustomView.findViewById(R.id.text_search).setEnabled(true);
                    }
                });
        final EditText text_search = (EditText)mCustomView.findViewById(R.id.text_search);
        text_search.addTextChangedListener(
                new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        if (s.toString().length()>0) {
                            mCustomView.findViewById(R.id.text_cancel).setVisibility(View.VISIBLE);
                        }else{
                            mCustomView.findViewById(R.id.text_cancel).setVisibility(View.INVISIBLE);
                        }
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) {}
                });
        text_search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String query = text_search.getText().toString().trim().toLowerCase();
                    SearchFragment fragment = (SearchFragment)
                            getSupportFragmentManager().findFragmentByTag("SearchFragment");

                    if (fragment != null) {
                        fragment.search(query,Constants.cero, Constants.load_more_tax,true);
                    }
                    return true;
                }
                return false;
            }
        });
        mCustomView.findViewById(R.id.text_cancel).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // "Done"
                        ((EditText)mCustomView.findViewById(R.id.text_search)).setText("");
                    }
                });


        mCustomView.findViewById(R.id.image_cart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchFragment fragment = (SearchFragment)
                        getSupportFragmentManager().findFragmentByTag("SearchFragment");

                if (fragment != null) {
                    fragment.startShoppingCartActivity();
                }

            }
        });

        getSupportActionBar().setCustomView(mCustomView);
        getSupportActionBar().setDisplayShowCustomEnabled(true);


        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_search,
                            SearchFragment.createInstance(getType_service()),"SearchFragment").commit();

        }

    }

    public String getType_service() {
        return type_service;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //reset search parameter
                SearchFragment fragment = (SearchFragment)
                        getSupportFragmentManager().findFragmentByTag("SearchFragment");

                if (fragment != null) {
                    finish();
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

}
