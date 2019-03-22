package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.enums.LoginType;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ApiException;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.FireBaseOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;
import com.scottyab.aescrypt.AESCrypt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import retrofit2.Call;
import retrofit2.Callback;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private RelativeLayout no_connection_layout;
    private AppCompatButton email_sign_in_button;
    private AppCompatButton button_signup;
    private AppCompatCheckBox checkbox_show_password;
    private EditText text_email;
    private EditText text_password;
    private static TextView link_password;
    private ProgressDialog progressDialog;
    private final String USER_NORMAL="user";
    private final String operation_login ="login";
    private final String operation_new_user ="new";
    private String flow_previous = "";

    /*redes sociales*/
    private CallbackManager callbackmanager;
    public static final String TYPE_LOGIN = "type_login";
    private final String generic_facebook_password = "facebook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        /*redes sociales*/
        FacebookSdk.sdkInitialize(getApplicationContext());
        /*redes sociales fin*/

        Intent i = getIntent();
        flow_previous = i.getStringExtra(Constants.flow);
        Log.d(TAG,"flow_previous:"+flow_previous);
        init();
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assignActions();
    }
    private void init(){
        no_connection_layout = (RelativeLayout) findViewById(R.id.no_connection_layout);
        email_sign_in_button = (AppCompatButton) findViewById(R.id.email_sign_in_button);
        button_signup = (AppCompatButton) findViewById(R.id.button_signup);
        checkbox_show_password = (AppCompatCheckBox) findViewById(R.id.checkbox_show_password);
        text_email = (EditText) findViewById(R.id.text_email);
        text_password = (EditText) findViewById(R.id.text_password);
        link_password = (TextView) findViewById(R.id.link_password);
        ApplicationPreferences.saveLocalPreference(this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
    }
    public void revokeAccessFacebook(View view) {
        //LoginManager.getInstance().logOut();

        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }

        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, new GraphRequest
                .Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {

                LoginManager.getInstance().logOut();

            }
        }).executeAsync();
    }

    public void onFblogin(View view)
    {
        Log.d(TAG,"onFblogin...");

        ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.FACEBOOK));
        callbackmanager = CallbackManager.Factory.create();

        // Set permissions
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));

        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(
                                            JSONObject json,
                                            GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                            ShowConfirmations.showConfirmationMessage(response.getError().getErrorMessage(), LoginActivity.this);
                                        } else {


                                            String str_firstname, str_email, str_lastname, str_name = "";
                                            String name_user = "";
                                            String jsonresult = String.valueOf(json);
                                            //Log.d(TAG, "jsonresult:" + jsonresult);
                                            //ShowConfirmations.showConfirmationMessage("JSON Result" + jsonresult,LoginActivity.this);
                                            try {
                                                str_email = json.getString("email");
                                            } catch (JSONException e) {//el usuario no tiene email publico o es un usuario nuevo en facebook y no ha confirmado su email
                                                e.printStackTrace();
                                                str_email = "";
                                                ShowConfirmations.showConfirmationMessage(getString(R.string.facebook_email_error), LoginActivity.this);
                                                revokeAccessFacebook(null);
                                                //reset login type method
                                                ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                                            }

                                                /*try {
                                                    str_firstname = json.getString("first_name");
                                                } catch (JSONException e) {
                                                    str_firstname = "";
                                                }
                                                try {
                                                    str_lastname = json.getString("last_name");
                                                } catch (JSONException e) {
                                                    str_lastname = "";
                                                }*/

                                            try {
                                                str_name = json.getString("name");
                                            } catch (JSONException e) {//el usuario no tiene email publico o es un usuario nuevo en facebook y no ha confirmado su email
                                                str_name = "";
                                            }


                                            //curso normal, proceder al login / registro, pendiente actualizar nombre del usuario
                                            //guardar el tipo de login FACEBOOK
                                            UserItem user_facebook = new UserItem();
                                            user_facebook.setEmail(str_email);

                                            if (str_name != null && !str_name.isEmpty()) {
                                                name_user = str_name;
                                            }

                                                /*if (str_firstname != null && !str_firstname.isEmpty()){
                                                    name_user += " "+str_firstname;
                                                }
                                                if (str_lastname!=null && !str_lastname.isEmpty()) {
                                                    name_user += " "+str_lastname;
                                                }*/
                                            user_facebook.setName(name_user);
                                            user_facebook.setPassword(generic_facebook_password);
                                            signup(user_facebook, LoginType.FACEBOOK);


                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,first_name,last_name,email");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                    }

                    @Override
                    public void onError(FacebookException error) {
                        ShowConfirmations.showConfirmationMessage(error.toString(), LoginActivity.this);
                        revokeAccessFacebook(null);
                        ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                    }
                });
    }
    private void assignActions(){
        checkbox_show_password.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_show_password.isChecked()) {
                    text_password.setTransformationMethod(null);
                } else {
                    text_password.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });
        link_password.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                recovery_Popup(v.getContext(), LoginActivity.this);
            }
        });
        email_sign_in_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
        button_signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    String email = text_email.getText().toString().trim();
                    String password = text_password.getText().toString().trim();
                    //guardar el tipo de login
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                    UserItem user_native = new UserItem();
                    user_native.setEmail(email);
                    user_native.setName("");
                    user_native.setPassword(password);
                    signup(user_native, LoginType.NATIVE);

                }
            }
        });
    }

    private void signup(UserItem userItem, Enum typelogin){
        createProgressDialog(getString(R.string.promt_loading));
        String token = ApplicationPreferences.getLocalStringPreference(getApplicationContext(), Constants.firebase_token);
        userItem.setToken(token);
        String encryptedPassword = "";
        try {
            encryptedPassword =  AESCrypt.encrypt(Constants.seedValue, userItem.getPassword());//aqui
            userItem.setPassword(encryptedPassword);
        } catch (Exception e) {
            e.printStackTrace();
            userItem.setPassword("encrypt_error");
        }
        //Log.d(TAG,"TOKEN:"+userItem.getToken());
        UserOperationRequest request = new UserOperationRequest();
        request.setUser(userItem);
        request.setOperation(operation_new_user);
        request.setOperationSecondary(String.valueOf(typelogin));//tipo de loggin(google , facebook, nativo)
        createUser(request);
    }
    private void onSuccess(UserResponse login_response ){
        Log.d(TAG, "Respuesta: " + login_response.getResult().toString());
        Gson gson = new Gson();
        UserItem userloged = login_response.getResult();
        ApplicationPreferences.saveLocalPreference(getApplicationContext(),Constants.user_object,gson.toJson(userloged));
        FireBaseOperations.subscribe(getApplicationContext(), userloged.getProfile());
        hideProgressDialog();
        //load main activity
        startMainActivity();
        //Finish login activity
        finish();
    }
    private void createUser(UserOperationRequest request){
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
                        ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                    } else {

                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
                        onError(getString(R.string.error_invalid_login, response_error));
                    }


                } else {
                    ApplicationPreferences.saveLocalPreference(LoginActivity.this, LoginActivity.TYPE_LOGIN, String.valueOf(LoginType.NATIVE));
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
    private boolean validate(){
        // Store values at the time of the login attempt.
        String email = text_email.getText().toString();
        String password = text_password.getText().toString();
        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            text_password.setError(getString(R.string.error_field_required));
            focusView = text_password;
            cancel = true;
        }
        if (!isPasswordValid(password)) {
            text_password.setError(getString(R.string.error_invalid_password));
            focusView = text_password;
            cancel = true;
        }


        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            text_email.setError(getString(R.string.error_field_required));
            focusView = text_email;
            cancel = true;
        } else if (!isEmailValid(email)) {
            text_email.setError(getString(R.string.error_invalid_email));
            focusView = text_email;
            cancel = true;
        }
        if (cancel) focusView.requestFocus();
        return cancel?false:true;
    }

    private void attemptLogin() {

        if (validate()) {
            login(USER_NORMAL);
        }
    }
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return (password.length() >= 4 && password.length() < 30);
    }
    private void login(String typeLogin){
        String token = ApplicationPreferences.getLocalStringPreference(getApplicationContext(),Constants.firebase_token);
        String email = "";
        String password = "";
        String encryptedPassword = "";

        if (typeLogin.equals(USER_NORMAL)) {
            createProgressDialog(getString(R.string.promt_loading));
            email = text_email.getText().toString().trim();
            password = text_password.getText().toString().trim();
            try {
                encryptedPassword =  AESCrypt.encrypt(Constants.seedValue,password);//aqui
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        login(email,encryptedPassword,token);
    }
    private void onError(String response_error){
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,response_error),LoginActivity.this);
        hideProgressDialog();
    }

    private void login(String email, String password,String token){
        UserOperationRequest request = new UserOperationRequest();
        request.setUser(new UserItem(email,password,token));//inicialmente todos los usuario nacen como cliente..
        request.setOperation(operation_login);

        RestServiceWrapper.userOperation(request, new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    UserResponse login_response = response.body();
                    if (login_response != null && login_response.getStatus().equals(Constants.success)) {
                        // Log.d(TAG, "UserResponse:" + login_response.toString() + "  condicion:" + (login_response != null && login_response.getStatus().equals(Constants.success)));
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
                    onError(getString(R.string.error_invalid_login, getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                ApiException apiException = new ApiException();
                try {
                    apiException.setMessage(t.getMessage());
                    onError(getString(R.string.error_invalid_login, apiException.getMessage()));
                } catch (Exception ex) {
                    // do nothing
                }
            }
        });
    }
    private void startMainActivity(){
        Context context = LoginActivity.this;
        Intent intentMain =  new Intent(context, MainActivity.class);
        context.startActivity(intentMain);
    }
    private void hideProgressDialog(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

    }
    private void createProgressDialog(String message){
        progressDialog = new ProgressDialog(LoginActivity.this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    private void recovery_Popup(Context context, Activity activity) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(activity);
        final View content = layoutInflaterAndroid.inflate(R.layout.password_recovery_layout, null);
        final View mView = layoutInflaterAndroid.inflate(R.layout.dialog_template, null);
        LinearLayout fields = (LinearLayout)mView.findViewById(R.id.content);
        fields.addView(content);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(activity);
        alertDialogBuilderUserInput.setView(mView);

        final EditText text_email_recovery = (EditText) mView.findViewById(R.id.text_email_recovery);
        final ProgressBar pbLoading_update = (ProgressBar) mView.findViewById(R.id.pbLoading_update);
        final TextView layout_text = (TextView) mView.findViewById(R.id.layout_text);
        final LinearLayout layout_email = (LinearLayout) mView.findViewById(R.id.layout_email);
        final TextView layout_text_error = (TextView) mView.findViewById(R.id.layout_text_error);
        //customize title
        ((TextView)mView.findViewById(R.id.text_title)).setText(getResources().getString(R.string.promt_password_recovery_title));
        ((TextView)mView.findViewById(R.id.text_title)).setBackgroundColor(ContextCompat.getColor(activity,R.color.colorPrimary));//primary
        ((TextView)mView.findViewById(R.id.text_title)).setTextColor(ContextCompat.getColor(activity,R.color.white_all));
        AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_password_recovery);
        final AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean valid = true;
                layout_text_error.setVisibility(View.GONE);
                if (text_email_recovery.getText().toString().trim().isEmpty()) {
                    text_email_recovery.setError(getString(R.string.error_field_required));
                    valid = false;
                } else {
                    text_email_recovery.setError(null);
                    valid = true;
                }
                if (valid) {
                    pbLoading_update.setVisibility(View.VISIBLE);
                    layout_text.setVisibility(View.GONE);
                    layout_email.setVisibility(View.GONE);
                    v.setEnabled(false);
                    //generate new password
                    String password = Constants.reset_password + (int)Math.floor((Math.random() * 365) + 1);
                    String encryptedPassword = "";
                    try {
                        encryptedPassword =  AESCrypt.encrypt(Constants.seedValue,password);//aqui
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    sendEmailPassword(text_email_recovery.getText().toString().trim(), password,encryptedPassword,alertDialogAndroid,mView);
                }
            }
        });

    }
    private void sendEmailPassword(String email,String password,String encryptedPassword,final AlertDialog alert,final View mView){
        RestServiceWrapper.recoveryPassword(email,password,encryptedPassword,new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GenericResponse config_response = response.body();

                    if (config_response != null && config_response.getStatus().equals(Constants.success)) {

                        if ( config_response.getResult()!=null && config_response.getStatus().equals(Constants.success)){
                            (mView.findViewById(R.id.pbLoading_update)).setVisibility(View.GONE);
                            (mView.findViewById(R.id.layout_text)).setVisibility(View.VISIBLE);
                            ((TextView)mView.findViewById(R.id.layout_text)).setText(getString(R.string.promt_password_recovery_message));
                            AppCompatButton button = (AppCompatButton) mView.findViewById(R.id.button_password_recovery);
                            button.setEnabled(true);
                            button.setText(getString(R.string.promt_button_ready));
                            button.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    alert.dismiss();
                                }
                            });
                        }else{
                            onError(getString(R.string.error_invalid_login,config_response.getMessage()));
                        }

                    } else if (config_response != null && config_response.getStatus().equals(Constants.no_data)) {
                        String response_error = config_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }else{
                        String response_error = response.message();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }


                }else{
                    onError(getString(R.string.error_invalid_login,getString(R.string.error_generic)));
                }
            }
            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d(TAG,"ERROR: " +t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                onError(getString(R.string.error_invalid_login,t.getMessage()));
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
    private void redirectToMain(){
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }
    @Override
    public void onBackPressed() {
        if (flow_previous.equals(MainActivity.flow_no_registered)){
            finish();
            NavUtils.navigateUpTo(LoginActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }else if (flow_previous.equals(MainActivity.flow_main)){
            super.onBackPressed();
        }else {
            finish();
            NavUtils.navigateUpTo(LoginActivity.this, new Intent(getApplicationContext(), MainActivity.class));
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        String login_type_request = ApplicationPreferences.getLocalStringPreference(this, LoginActivity.TYPE_LOGIN);
        Log.d(TAG, "requestCode:::::::::::" + requestCode+ "     login_type_request:"+login_type_request);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if(login_type_request.equals(String.valueOf(LoginType.FACEBOOK))){Log.d(TAG, "onActivityResult CASO Facebook");

            callbackmanager.onActivityResult(requestCode, resultCode, data);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
