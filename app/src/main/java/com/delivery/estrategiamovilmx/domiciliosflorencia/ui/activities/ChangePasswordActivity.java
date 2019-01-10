package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.google.gson.Gson;
import com.scottyab.aescrypt.AESCrypt;

import retrofit2.Call;
import retrofit2.Callback;

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String TAG = ChangePasswordActivity.class.getSimpleName();
    private EditText text_new_password;
    private EditText text_new_password_confirm;
    private AppCompatButton button_change;
    private AppCompatCheckBox checkbox_show_password;
    private ProgressDialog progressDialog;
    private UserItem user = null;
    private final String METHOD_CHANGE_PASSWORD = "change_password";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initGUI();
        assignActions();
        Gson gson = new Gson();
        String json_user = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.user_object);
        Log.d(TAG,"json_object:"+json_user);
        if (json_user!=null || !json_user.isEmpty()){
            //get user saved
            user = gson.fromJson(json_user,UserItem.class);
        }
    }
    private void initGUI(){
        text_new_password = (EditText) findViewById(R.id.text_new_password);
        text_new_password_confirm = (EditText) findViewById(R.id.text_new_password_confirm);
        checkbox_show_password = (AppCompatCheckBox) findViewById(R.id.checkbox_show_password);
        button_change = (AppCompatButton) findViewById(R.id.button_change);
    }
    private void assignActions(){
        checkbox_show_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_show_password.isChecked()){
                    text_new_password.setTransformationMethod(null);
                    text_new_password_confirm.setTransformationMethod(null);
                }else{
                    text_new_password.setTransformationMethod(new PasswordTransformationMethod());
                    text_new_password_confirm.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        button_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                String p1 = text_new_password.getText().toString();
                String p1_confirm = text_new_password_confirm.getText().toString();

                if (p1.isEmpty() || p1.length() <= 3 || p1.length()>20) {
                    text_new_password.setError(getString(R.string.error_invalid_password));
                    valid = false;
                } else {
                    text_new_password.setError(null);
                }
                if (p1_confirm.isEmpty() || p1_confirm.length() <= 3 || p1_confirm.length()>20) {
                    text_new_password_confirm.setError(getString(R.string.error_invalid_password));
                    valid = false;
                } else {
                    text_new_password_confirm.setError(null);
                }
                if (!p1.equals(p1_confirm)){
                    text_new_password_confirm.setError(getString(R.string.error_invalid_password_not_equal));
                    valid = false;
                }else{
                    text_new_password_confirm.setError(null);
                }
                if (valid){
                    initProcess(true);
                    updatePassword();
                }
            }
        });
    }

    public UserItem getUser() {
        return user;
    }

    private void updatePassword(){
        String newPassword = text_new_password.getText().toString().trim();
        String encryptedPassword = "";
        try {
            encryptedPassword =  AESCrypt.encrypt(Constants.seedValue,newPassword);//aqui
        } catch (Exception e) {
            e.printStackTrace();
        }
        UserOperationRequest request = new UserOperationRequest();
        user.setPassword(encryptedPassword);//new password to update
        request.setUser(getUser());
        request.setOperation(ProfileActivity.METHOD_UPDATE_PROFILE);
        request.setOperationSecondary(METHOD_CHANGE_PASSWORD);

        RestServiceWrapper.userOperation(request, new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    UserResponse login_response = response.body();

                    if (login_response != null && login_response.getStatus().equals(Constants.success)) {
                        onSuccess(login_response);
                    } else if (login_response != null && login_response.getStatus().equals(Constants.no_data)) {
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    } else {
                        Log.d(TAG, "11");
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    Log.d(TAG, "33");
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login, t.getMessage()));
            }
        });

    }
    private void onSuccess(UserResponse response){
        Log.d(TAG, "Respuesta: " + response.getMessage());
        Gson gson = new Gson();
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.user_object,gson.toJson(getUser(),UserItem.class));
        ShowConfirmations.showConfirmationMessage(response.getMessage(),ChangePasswordActivity.this);
        initProcess(false);
        finish();

    }
    private void onError(String response_error){
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,response_error),ChangePasswordActivity.this);
        initProcess(false);
    }
    private void initProcess(boolean flag){
        if (flag)
            createProgressDialog(getString(R.string.promt_loading));
        else
            closeProgressDialog();
    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(ChangePasswordActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void closeProgressDialog(){
        if (progressDialog!=null && progressDialog.isShowing()) progressDialog.dismiss();
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
    /*@Override
    public void onBackPressed() {
        finish();
    }*/
    private void hideKeyBoard(View v){
        //hide keyboord
        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(
                v.getWindowToken(), 0);
    }
}
