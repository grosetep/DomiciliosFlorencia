package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ImagePicker;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.UploadImage;
import com.google.gson.Gson;

import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;

public class ProfileActivity extends AppCompatActivity {
    private final String TAG ="ProfileActivity";
    private FrameLayout container_loading;
    private RelativeLayout header_parent;
    private CircleImageView header_imageview;
    private ImageButton button_add_photo;
    private TextView text_name_view;
    private TextView text_email_view;
    private EditText text_name_profile;
    private ImageView button_edit_name;
    private ImageView button_done_name;
    private LinearLayout layout_change_password;
    private Timer timer = null;
    private UserItem user = null;
    private Bitmap bitmap;
    private String nameImage;
    private static final int PICK_IMAGE_ID = 1;
    private static final int METHOD_CHANGE_PASSWORD = 2;
    public static final String METHOD_UPDATE_PROFILE = "update_profile";
    private final String MODIFY_NAME = "modify_name";
    private String flow_previous = "";
    public static int PROFILE_UPDATED = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Intent intent = getIntent();
        flow_previous = intent.getStringExtra(Constants.flow);
        init();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Gson gson = new Gson();
        String json_user = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.user_object);
        Log.d(TAG,"json_object:"+json_user);
        if (json_user==null || json_user.isEmpty()){
            finish();
            Intent i = new Intent(getBaseContext(),LoginActivity.class);
            i.putExtra(Constants.flow,MainActivity.flow_no_registered);
            startActivity(i);
        }else {
            //get user saved
            user = gson.fromJson(json_user,UserItem.class);
            if (user!=null && user.getIdUser()!=null) {
                assignActions();
                container_loading.setVisibility(View.VISIBLE);
                initProcess(true);
                loadInfoUser(user);
            }
        }

    }
    private void loadInfoUser(UserItem user){
        text_email_view.setText(user.getEmail()!=null?user.getEmail():"");
        text_name_view.setText(user.getName()!=null?user.getName():"");
        text_name_profile.setText(user.getName()!=null?user.getName():"");
        if (user.getAvatarImage()!=null && user.getAvatarPath()!=null){
            Glide.with(getApplicationContext())
                    .load(user.getAvatarPath()+user.getAvatarImage())
                    .error(R.drawable.ic_account_circle)
                    .into(header_imageview);
        }
        initProcess(false);

    }
    private void initProcess(boolean flag){
        container_loading.setVisibility(flag?View.VISIBLE:View.GONE);
        header_parent.setVisibility(flag?View.GONE:View.VISIBLE);
    }
    private void init(){
        header_parent = (RelativeLayout) findViewById(R.id.header_parent);
        text_email_view = (TextView) findViewById(R.id.text_email_view);
        text_name_view = (TextView) findViewById(R.id.text_name_view);
        text_name_profile = (EditText) findViewById(R.id.text_name_profile);
        container_loading = (FrameLayout) findViewById(R.id.container_loading);
        header_imageview = (CircleImageView) findViewById(R.id.header_imageview);
        button_add_photo = (ImageButton) findViewById(R.id.button_add_photo);

        button_edit_name = (ImageView) findViewById(R.id.button_edit_name);
        button_done_name = (ImageView) findViewById(R.id.button_done_name);
        layout_change_password = (LinearLayout) findViewById(R.id.layout_change_password);
        nameImage = null;

    }
    private void assignActions(){
        header_imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ProfileActivity.this,getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        button_edit_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text_name_profile.setEnabled(true);
                text_name_profile.requestFocus();
            }
        });
        button_done_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!getUser().getName().equals(text_name_profile.getText().toString()) && text_name_profile.getText().toString().trim().length()>0){
                    hideKeyBoard(text_name_profile);
                    attemptUpdate(MODIFY_NAME);
                }else if(getUser().getName().equals(text_name_profile.getText().toString())){
                    text_name_profile.setEnabled(false);
                }
            }
        });
        button_add_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(ProfileActivity.this,getString(R.string.pick_image_intent_text));
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });
        layout_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                startActivityForResult(intent, METHOD_CHANGE_PASSWORD);
            }
        });
    }
    private void hideKeyBoard(View v){
        //hide keyboord
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }
    private void updateProfile(String type_update){
        UserOperationRequest request = new UserOperationRequest();
        user.setName(text_name_profile.getText().toString().trim());
        request.setUser(getUser());
        request.setOperation(METHOD_UPDATE_PROFILE);
        request.setOperationSecondary(type_update);
        RestServiceWrapper.userOperation(request,new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    UserResponse login_response = response.body();

                    if (login_response != null && login_response.getStatus().equals(Constants.success)) {
                        onSuccess(login_response);
                    } else if (login_response != null && login_response.getStatus().equals(Constants.no_data)){
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }else{
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }


                }else{
                    onError(getString(R.string.error_invalid_login,getString(R.string.error_generic)));
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG,"ERROR: " +t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login,t.getMessage()));
            }
        });

    }
    private void onSuccess(UserResponse response){
        Log.d(TAG, "Respuesta: " + response.getMessage());
        Gson gson = new Gson();
        text_name_view.setText(getUser().getName());
        text_name_profile.setEnabled(false);
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.user_object,gson.toJson(getUser(),UserItem.class));
        ShowConfirmations.showConfirmationMessage(response.getMessage(),ProfileActivity.this);
        ProfileActivity.PROFILE_UPDATED = 1;
        initProcess(false);


    }
    private void onError(String response_error){
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,response_error),ProfileActivity.this);
        initProcess(false);
    }


    private void attemptUpdate(String type_update){
        switch (type_update){
            case MODIFY_NAME:
                initProcess(true);
                updateProfile(type_update);
                break;

        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case PICK_IMAGE_ID:
                if (resultCode == Activity.RESULT_OK) {
                    bitmap = ImagePicker.getImageFromResult(ProfileActivity.this, resultCode, data);
                    header_imageview.setImageBitmap(bitmap);
                    nameImage = ImagePicker.getRealPathFromURI(header_imageview.getContext(), ImagePicker.getUriBitmapSelected());
                    //upload image
                    updateImageProfile();
                }
                break;
            case METHOD_CHANGE_PASSWORD:
                if (resultCode == Activity.RESULT_OK){
                    ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_profile_updated),this);
                    ProfileActivity.PROFILE_UPDATED = 1;
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void updateImageProfile(){
        String idUser = getUser().getIdUser();
        String nameImageToServer = idUser+ "_" + ImagePicker.getImageName(nameImage);
        if (bitmap!=null) { //usuario selecciono avatar
            UploadImage.uploadImage(ProfileActivity.this, nameImageToServer , getUser(),bitmap);
            start(nameImageToServer);
        }
    }
    public void start(String nameImageToServer) {
        if (timer != null) {
            timer.cancel();
        }
        Log.d(TAG,"start");
        startChekingUploading(nameImageToServer);
    }
    public void stop() {
        Log.d(TAG,"stop..");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    public void startChekingUploading(final String nameImageToServer) {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {

                runOnUiThread(new Runnable() {
                    public void run() {
                        if (UploadImage.ready==1 && UploadImage.error == 0) {
                            stop();
                            //update database
                            //updateImageProfileRoute();
                            initProcess(false);
                            user.setAvatarImage(nameImageToServer);
                            Log.d(TAG,"Imagen a mostrar:"+user.getAvatarPath()+user.getAvatarImage());


                            Gson gson = new Gson();
                            ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.user_object,gson.toJson(user,UserItem.class));
                            ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_updated),ProfileActivity.this);
                            ProfileActivity.PROFILE_UPDATED = 1;
                            /*runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.d(TAG,"Actualizando nueva imagen perfil...");
                                Glide.with(ProfileActivity.this)
                                    .load(user.getAvatarPath()+user.getAvatarImage())
                                    .error(R.drawable.ic_account_circle)
                                    .into(header_imageview);
                                                }
                            });*/


                        }else if(UploadImage.ready==1 && UploadImage.error == 1){// hubo errores, reintentar...
                            initProcess(false);
                            stop();
                            ShowConfirmations.showConfirmationMessage(getString(R.string.text_prompt_image_error),ProfileActivity.this);
                            //carga imagen anterior
                            Glide.with(ProfileActivity.this)
                                    .load(R.drawable.ic_account_circle)
                                    .into(header_imageview);
                        }

                    }
                });


            }

        }, 0, Constants.DURATION);
    }

    private UserItem getUser(){
        return user;
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
    @Override
    public void onBackPressed() {
        if (ProfileActivity.PROFILE_UPDATED == 1) {
            ProfileActivity.PROFILE_UPDATED = 0;
            finish();
            NavUtils.navigateUpTo(ProfileActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }if (flow_previous!=null && flow_previous.equals(MainActivity.flow_main)){
            super.onBackPressed();
        }else {
            finish();
            NavUtils.navigateUpTo(ProfileActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
