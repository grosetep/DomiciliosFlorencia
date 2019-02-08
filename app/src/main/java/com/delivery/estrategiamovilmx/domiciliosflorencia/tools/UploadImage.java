package com.delivery.estrategiamovilmx.domiciliosflorencia.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.delivery.estrategiamovilmx.domiciliosflorencia.items.UserItem;
import com.delivery.estrategiamovilmx.domiciliosflorencia.model.ImagePublication;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by administrator on 22/07/2016.
 */
public class UploadImage {

    private static String KEY_IMAGE = "image";
    private static String KEY_USER_ID = "user_id";
    private static String KEY_USER_EMAIL = "email";
    private static String KEY_NAME = "name";
    private static String KEY_ORDER = "order_id";
    private static Context context;
    public static int ready = 0;
    public static int error = 0;
    private static ArrayList<String> nameImageUploaded=new ArrayList<>();
    private static int totalIterations;
    private static HashMap<String, ImagePublication> mapError = new HashMap<>();
    private static final String TAG = UploadImage.class.getSimpleName();
    public UploadImage() {

    }

    public static HashMap<String, ImagePublication> getMapError() {
        return mapError;
    }

    public static void resetMapError(){
        mapError = new HashMap<>();
    }
    public static void initUploadImageVariables(){
        UploadImage.ready = 0;
        UploadImage.error = 0;
        UploadImage.resetMapError();
    }
    private static String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public static void uploadImage(Context ctx, final String nameImage, final UserItem user, final Bitmap imageBitmap) {
        //Showing the progress dialog
        context = ctx;
        final ImagePublication imageIteration = new ImagePublication(imageBitmap,nameImage,nameImage);
        Log.d(TAG,"Imagen a subir....."+nameImage);
        //final ProgressDialog loading = ProgressDialog.show(context,"Guardando Perfíl...","Espere...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_IMAGE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        // loading.dismiss();
                        //Showing toast message of the response

                        String status = "",message = "";
                        JSONObject json = null;
                        Log.d(TAG, "response:" + s.toString());
                        try {
                            json = new JSONObject(s);
                            status = json.getString("status");
                            message = json.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        switch (status) {
                            case "1"://assign values
                                //nothign to show to user
                                Log.d(TAG, "message:" + message);
                                UploadImage.ready = 1;
                                error = 0;
                                Log.d(TAG, "Asignacion caso 1::  ready=" + UploadImage.ready + " error="+UploadImage.error + " map:"+UploadImage.getMapError().size());
                                resetMapError();
                                Log.d(TAG,"reset MapError !!");
                                break;
                            case "2"://login failed
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;break;
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        UploadImage.ready = 1;
                        error = 1;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(imageBitmap);
                //Getting Image Name
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, nameImage);
                params.put(KEY_USER_ID, user.getIdUser());
                params.put(KEY_USER_EMAIL, user.getEmail());

                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public static void uploadImageSignature(Context ctx, final String nameImage, final UserItem user, final Bitmap imageBitmap, final String idOrder) {
        //Showing the progress dialog
        context = ctx;
        final ImagePublication imageIteration = new ImagePublication(imageBitmap,nameImage,nameImage);
        Log.d(TAG,"Imagen de firma a subir....."+nameImage);
        //final ProgressDialog loading = ProgressDialog.show(context,"Guardando Perfíl...","Espere...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.UPLOAD_IMAGE_SIGNATURE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        // loading.dismiss();
                        //Showing toast message of the response

                        String status = "",message = "";
                        JSONObject json = null;
                        Log.d(TAG, "response:" + s.toString());
                        try {
                            json = new JSONObject(s);
                            status = json.getString("status");
                            message = json.getString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        switch (status) {
                            case "1"://assign values
                                //nothign to show to user
                                Log.d(TAG, "message:" + message);
                                UploadImage.ready = 1;
                                error = 0;
                                Log.d(TAG, "Asignacion caso 1::  ready=" + UploadImage.ready + " error="+UploadImage.error + " map:"+UploadImage.getMapError().size());
                                resetMapError();
                                Log.d(TAG,"reset MapError !!");
                                break;
                            case "2"://login failed
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;
                                //Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                mapError.put((imageIteration.getPath()),imageIteration);
                                UploadImage.ready = 1;
                                error = 1;break;
                        }
                    }


                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        UploadImage.ready = 1;
                        error = 1;
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image_bitmat_as_string = getStringImage(imageBitmap);
                //Getting Image Name
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put(KEY_IMAGE, image_bitmat_as_string);
                params.put(KEY_NAME, nameImage);
                params.put(KEY_ORDER, idOrder);


                //returning parameters
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
