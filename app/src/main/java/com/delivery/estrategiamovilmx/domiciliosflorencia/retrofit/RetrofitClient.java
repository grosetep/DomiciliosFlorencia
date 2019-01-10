package com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit;

import android.content.Context;

import com.delivery.estrategiamovilmx.domiciliosflorencia.ui.interfaces.WebServicesInterface;

import retrofit2.Retrofit;

/**
 * Created by administrator on 19/07/2017.
 */
public class RetrofitClient {
    private static String mKey = null;
    private static String mKeyType = null;
    private static Context mContext = null;
    private static Retrofit retrofit = null;

    private WebServicesInterface shipping;
    private static final String TAG = RetrofitClient.class.getSimpleName();
    public static final String KEY_TYPE_PUBLIC = "public_key";
    public static final String KEY_TYPE_PRIVATE = "private_key";

    public static synchronized Retrofit getClient(String baseUrl) {
        if (retrofit==null) {
            System.setProperty("http.keepAlive", "false");
            Retrofit.Builder mRestAdapterApi =
                    new Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(JsonUtil.getInstance().getGsonConverterFactory());

            retrofit =  mRestAdapterApi.client( HttpClientUtil.getClient(mContext) ).build();
        }
        return retrofit;
    }

    /*private RetrofitClient(Builder builder_init) {
        System.setProperty("http.keepAlive", "false");
        this.mKey = builder_init.mKey;
        this.mContext = builder_init.mContext;
        this.mKeyType = builder_init.mKeyType;

        Retrofit.Builder mRestAdapterApi =
                new Retrofit.Builder()
                        .baseUrl(Constants.HTTPS + Constants.IP + Constants.PUERTO_HOST + Constants.SUBDOMAIN)
                        .addConverterFactory(JsonUtil.getInstance().getGsonConverterFactory());

        retrofit =  mRestAdapterApi.client( HttpClientUtil.getClient(this.mContext) ).build();
        shipping = retrofit.create(WebServicesInterface.class);

    }*/




   /* public static class Builder {

        private Context mContext;
        private String mKey;
        private String mKeyType;

        public Builder() {

            mContext = null;
            mKey = null;
        }

        public Builder setContext(Context context) {

            if (context == null) throw new IllegalArgumentException("context is null");
            this.mContext = context;
            return this;
        }

        public Builder setKey(String key, String keyType) {

            this.mKey = key;
            this.mKeyType = keyType;
            return this;
        }

        public Builder setPrivateKey(String key) {

            this.mKey = key;
            this.mKeyType = RetrofitClient.KEY_TYPE_PRIVATE;
            return this;
        }

        public Builder setPublicKey(String key) {

            this.mKey = key;
            this.mKeyType = RetrofitClient.KEY_TYPE_PUBLIC;
            return this;
        }

        public RetrofitClient build() {

            if (this.mContext == null) throw new IllegalStateException("context is null");
            if (this.mKey == null) throw new IllegalStateException("key is null");
            if (this.mKeyType == null) throw new IllegalStateException("key type is null");
            if ((!this.mKeyType.equals(RetrofitClient.KEY_TYPE_PRIVATE)) &&
                    (!this.mKeyType.equals(RetrofitClient.KEY_TYPE_PUBLIC))) throw new IllegalArgumentException("invalid key type");
            return new RetrofitClient(this);
        }
    }*/


}
