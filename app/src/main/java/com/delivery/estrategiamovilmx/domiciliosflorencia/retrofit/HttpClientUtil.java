package com.delivery.estrategiamovilmx.domiciliosflorencia.retrofit;

import android.content.Context;
import android.util.Log;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;

import java.security.cert.X509Certificate;

import java.security.cert.CertificateException;


/**
 * Created by administrator on 19/07/2017.
 */
public class HttpClientUtil {
    private static final String TAG = HttpClientUtil.class.getSimpleName();
    private static OkHttpClient client;
    private static X509TrustManager trustManager;
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    public synchronized static OkHttpClient getClient(Context context) {

        if (client == null) {

            // Set cache size
            int cacheSize = 10 * 1024 * 1024; // 10 MiB
            //Cache cache = new Cache(new File(context.getCacheDir().getPath() + "okhttp"), cacheSize);

            // Set client
            client = new OkHttpClient.Builder()
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .sslSocketFactory(createSslSocketFactory(),trustManager)//si LA URL ES con https entonces se vlaida vertificado, si es http nos e valida nada
                    .build();
        }

        return client;
    }
    private static SSLSocketFactory createSslSocketFactory() {

        trustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                try {
                    Log.d(TAG,"Inmediattoo Checking Certificate validity ..............");
                    chain[0].checkValidity();
                    Log.d(TAG, "ok ..............");
                } catch (Exception e) {
                    throw new CertificateException("Inmediattoo Certificate not valid or trusted.");
                }
            }
        };

        TrustManager[] byPassTrustManagers = new TrustManager[]{trustManager};

        SSLContext sslContext = null;
        SSLSocketFactory sslSocketFactory = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, byPassTrustManagers, new SecureRandom());
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

        return sslSocketFactory;
    }
}
