package com.naveejr.chatclient.util;

import android.util.Log;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NetworkUtil {

    private static final String TAG = NetworkUtil.class.getSimpleName();

    private static X509TrustManager getCustomTrustManager() {
        return new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

        };
    }

    private static OkHttpClient getOkHttpClient(String username, String password) {

        try {

            X509TrustManager trustManager = getCustomTrustManager();
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[]{trustManager}, new java.security.SecureRandom());
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.hostnameVerifier((hostname, session) -> true);
            builder.sslSocketFactory(sc.getSocketFactory(), trustManager);
            builder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request request = chain.request();
                    Request authenticatedRequest = request.newBuilder()
                            .header("Authorization", Credentials.basic(username, password)).build();
                    return chain.proceed(authenticatedRequest);
                }
            });
//
//            if (BuildConfig.DEBUG) {
//                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//                logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//                builder.addInterceptor(logging);
//            }

            return builder.build();

        } catch (NoSuchAlgorithmException | KeyManagementException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static Socket getSocket(String url, String username, String password) {
        try {
            IO.Options options = new IO.Options();
            OkHttpClient okHttpClient = getOkHttpClient(username, password);
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);
            options.callFactory = okHttpClient;
            options.webSocketFactory = okHttpClient;
            options.query="Authorization=Basic cmFqZWV2YW46ODY1ODY1";

            return IO.socket(url, options);
        } catch (URISyntaxException e) {
            Log.e(TAG, "Failed to create Socket!", e);
            return null;
        }
    }

}
