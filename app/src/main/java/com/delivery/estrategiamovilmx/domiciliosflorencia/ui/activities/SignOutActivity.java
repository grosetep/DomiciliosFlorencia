package com.delivery.estrategiamovilmx.domiciliosflorencia.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.delivery.estrategiamovilmx.domiciliosflorencia.R;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.UserOperationRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.UserResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.FireBaseOperations;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ShowConfirmations;

import retrofit2.Call;
import retrofit2.Callback;

public class SignOutActivity extends AppCompatActivity {
    private static final String TAG = SignOutActivity.class.getSimpleName();
    private static final String operation_signout = "operation_signout";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signout);
        logout();
    }

    private void logout(){
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        if (user!=null){//elimina token a este usuario
            signout(user);
        }else{
            ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.user_object, null);
            startMainActivity();
        }
    }
    private void signout(UserItem user){
        UserOperationRequest request = new UserOperationRequest();
        request.setUser(user);
        request.setOperation(operation_signout);
        RestServiceWrapper.userOperation(request,new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, retrofit2.Response<UserResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    UserResponse login_response = response.body();

                    if (login_response != null && login_response.getStatus().equals(Constants.success)) {
                        ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.user_object,null);
                        ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.days_to_show_orders,Constants.days_to_show_orders_default);
                        FireBaseOperations.unSubscribe(getApplicationContext(), Constants.profile_client);
                        FireBaseOperations.unSubscribe(getApplicationContext(),Constants.profile_admin);
                        FireBaseOperations.unSubscribe(getApplicationContext(), Constants.profile_deliver_man);
                        startMainActivity();
                    } else if (login_response != null && login_response.getStatus().equals(Constants.no_data)){
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Mensage:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }else{Log.d(TAG, "11");
                        String response_error = login_response.getMessage();
                        Log.d(TAG, "Error:" + response_error);
                        onError(getString(R.string.error_invalid_login,response_error));
                    }


                }else{Log.d(TAG, "33");
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
    private void onError(String response_error){
        onBackPressed();
        ShowConfirmations.showConfirmationMessage(getString(R.string.error_invalid_login,response_error),SignOutActivity.this);
    }

    private void startMainActivity(){
        Context context = SignOutActivity.this;
        Intent intentMain =  new Intent(context, MainActivity.class);
        context.startActivity(intentMain);
        finish();
    }
}
