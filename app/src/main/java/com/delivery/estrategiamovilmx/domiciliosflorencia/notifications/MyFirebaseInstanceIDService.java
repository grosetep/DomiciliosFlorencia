package com.delivery.estrategiamovilmx.domiciliosflorencia.notifications;

import android.util.Log;


import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.requests.RegisterDeviceRequest;
import com.delivery.estrategiamovilmx.domiciliosflorencia.responses.GenericResponse;
import com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit.RestServiceWrapper;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.ApplicationPreferences;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.Constants;
import com.delivery.estrategiamovilmx.domiciliosflorencia.tools.GeneralFunctions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * Created by administrator on 25/08/2017.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.firebase_token, refreshedToken);
        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {
        Log.d(TAG, "sendRegistrationToServer.....token:" + token);
        UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
        boolean already_registered = false;
        if (user!=null){//usuario ya logueado
            user.setToken(token);
            already_registered = true;
        }else{//usuario invitado
            user = new UserItem();
            user.setToken(token);
        }
        RegisterDeviceRequest request = new RegisterDeviceRequest();
        request.setUser(user);
        request.setAlreadyRegistered(String.valueOf(already_registered));

        RestServiceWrapper.registerDevice(request, new Callback<GenericResponse>() {
            @Override
            public void onResponse(Call<GenericResponse> call, retrofit2.Response<GenericResponse> response) {
                Log.d(TAG, "Respuesta: " + response);
                if (response != null && response.isSuccessful()) {
                    GenericResponse login_response = response.body();
                    UserItem user = GeneralFunctions.getCurrentUser(getApplicationContext());
                    Gson gson = new Gson();
                    if (user != null) {
                        user.setToken(token);
                        ApplicationPreferences.saveLocalPreference(getApplicationContext(), Constants.user_object, gson.toJson(user));
                    }

                } else {
                    Log.d(TAG, "Error al registrar dispositivo: " + response.errorBody().toString());
                    //onError(getString(R.string.error_invalid_login,getString(R.string.error_generic)));
                }
            }

            @Override
            public void onFailure(Call<GenericResponse> call, Throwable t) {
                Log.d(TAG, "ERROR: " + t.getStackTrace().toString() + " --->" + t.getCause() + "  -->" + t.getMessage() + " --->");
                Log.d(TAG, "onFailure al registrar dispositivo: " + t.getMessage());
            }
        });
    }
}