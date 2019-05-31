package org.jboss.aerogear.test;

import com.google.gson.*;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import org.jboss.aerogear.test.retrofit.UnifiedPushService;
import org.jboss.aerogear.unifiedpush.api.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used to return an already authenticated {@link UnifiedPushService} object.
 */
public class AerogearAdminServiceProvider {

    private static final Gson gson;

    private static //Kill ssl trust
    final TrustManager[] TRUST_ALL_CERTS = new TrustManager[]{
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            }
    };

    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(VariantType.class, (JsonDeserializer<VariantType>) (jsonElement, type, jsonDeserializationContext) -> VariantType.valueOf(jsonElement.getAsString().toUpperCase()))
                .registerTypeAdapter(PushApplication.class, (JsonDeserializer<PushApplication>) (jsonElement, type, jsonDeserializationContext) -> {

                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Logger.getAnonymousLogger().log(Level.SEVERE, jsonObject.toString());

                    PushApplication pushApplication = new PushApplication();

                    pushApplication.setName(getString("name", jsonElement));
                    pushApplication.setDescription(getString("description", jsonElement));
                    pushApplication.setDeveloper(getString("developer", jsonElement));
                    pushApplication.setMasterSecret(getString("masterSecret", jsonElement));
                    pushApplication.setPushApplicationID(getString("pushApplicationID", jsonElement));


                    List<Variant> variantList = new ArrayList<>();
                    if (jsonObject.has("variants")) {
                        JsonArray variants = jsonObject.get("variants").getAsJsonArray();
                        for (JsonElement variantJson : variants) {
                            String typeString = getString("type", variantJson);

                            Variant variant = null;
                            if (typeString.equals("ios")) {
                                variant = new iOSVariant();
                            } else if (typeString.equals("android")) {
                                variant = new AndroidVariant();
                            }
                            variantList.add(variant);
                        }
                    }

                    pushApplication.setVariants(variantList);
                    return pushApplication;
                })
                .create();
    }

    private final String url;
    private final String token;

    private boolean cacheAnswer = false;

    private UnifiedPushService cachedInstance = null;

    /**
     * Builds a new provider
     *
     * @param url   URL to the aerogear UPS server
     * @param token the Authorization Header token to attach to requests
     */
    public AerogearAdminServiceProvider(final String url, final String token) {

        if (url == null) {
            throw new IllegalArgumentException("Url must not be null");
        }

        this.url = url;

        if (token == null) {
            throw new IllegalArgumentException("Token must not be null");
        }
        this.token = token;
    }

    /**
     * Sets if the answer should be cached or not
     *
     * @param cacheAnswer true or false
     */
    public void setCacheAnswer(boolean cacheAnswer) {
        this.cacheAnswer = cacheAnswer;
    }

    /**
     * If this method has been successfully called at least one time and {@link #cacheAnswer} is true,
     * the same answer is returned at every call (without invoking the UPS server).
     * if {@link #cacheAnswer} is false, a new authentication is performed at each call
     *
     * @return an {@link org.jboss.aerogear.test.retrofit.UnifiedPushService} instance
     */
    public UnifiedPushService getAdminService() {
        if (cacheAnswer && cachedInstance != null) {
            return cachedInstance;
        }


        try {
            OkHttpClient.Builder okHttpClientBuilder = defaultOkHttpClientBuilder(()->"Bearer " + token);

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            cachedInstance = retrofit.create(UnifiedPushService.class);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
        return cachedInstance;
    }

    private static String getString(String key, JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().get(key).getAsString();
    }

    public UnifiedPushService getAdminService(String variantID, String variantSecret) {
        try {
            OkHttpClient.Builder okHttpClientBuilder = defaultOkHttpClientBuilder((()->Credentials.basic(variantID, variantSecret)));


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(url)
                    .client(okHttpClientBuilder.build())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            return retrofit.create(UnifiedPushService.class);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    private Interceptor auth(Supplier<String> headerSupplier) {

        return (chain) -> {
            System.out.println(headerSupplier.get());
            Request request = chain.request();
            Request.Builder newRequest = request.newBuilder().header("Authorization", headerSupplier.get());
            return chain.proceed(newRequest.build());
        };
    }

    private OkHttpClient.Builder defaultOkHttpClientBuilder(Supplier<String> authorizationHeaderSupplier ) throws NoSuchAlgorithmException, KeyManagementException {


        // Create an ssl socket factory with our all-trusting manager
        SSLSocketFactory sslSocketFactory = insecureSocketFactory();

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(logging);
        okHttpClientBuilder.addInterceptor(auth(authorizationHeaderSupplier));
        okHttpClientBuilder.sslSocketFactory(sslSocketFactory, (X509TrustManager) TRUST_ALL_CERTS[0]).hostnameVerifier((hostname, session) -> true);

        return okHttpClientBuilder;
    }

    private SSLSocketFactory insecureSocketFactory() throws NoSuchAlgorithmException, KeyManagementException {

        // Install the all-trusting trust manager
        final SSLContext sslContext;

        sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null, TRUST_ALL_CERTS, new java.security.SecureRandom());

        return sslContext.getSocketFactory();
    }
}
