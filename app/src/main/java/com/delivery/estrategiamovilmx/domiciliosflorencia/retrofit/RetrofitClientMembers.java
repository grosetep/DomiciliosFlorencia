package com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit;

import android.content.Context;

import retrofit2.Retrofit;

public class RetrofitClientMembers   {
   private static Context mContext = null;
   private static Retrofit retrofit_member = null;


   private static final String TAG = RetrofitClient.class.getSimpleName();
   public static final String KEY_TYPE_PUBLIC = "public_key";
   public static final String KEY_TYPE_PRIVATE = "private_key";

   public static synchronized Retrofit getClient(String baseUrl) {
      if (retrofit_member==null) {
         System.setProperty("http.keepAlive", "false");
         Retrofit.Builder mRestAdapterApi =
                 new Retrofit.Builder()
                         .baseUrl(baseUrl)
                         .addConverterFactory(JsonUtil.getInstance().getGsonConverterFactory());

         retrofit_member =  mRestAdapterApi.client( HttpClientUtil.getClient(mContext) ).build();
      }
      return retrofit_member;
   }
}