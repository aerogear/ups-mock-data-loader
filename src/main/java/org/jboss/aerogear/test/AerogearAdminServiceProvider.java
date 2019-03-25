package org.jboss.aerogear.test;

import com.google.gson.*;
import okhttp3.Credentials;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jboss.aerogear.test.retrofit.UnifiedPushService;
import org.jboss.aerogear.unifiedpush.api.*;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to return an already authenticated {@link UnifiedPushService} object.
 */
public class AerogearAdminServiceProvider {

    private static final Gson gson;
    static {
        gson = new GsonBuilder()
                .registerTypeAdapter(VariantType.class, (JsonDeserializer<VariantType>) (jsonElement, type, jsonDeserializationContext) -> VariantType.valueOf(jsonElement.getAsString().toUpperCase()))
                .registerTypeAdapter(PushApplication.class, (JsonDeserializer<PushApplication>) (jsonElement, type, jsonDeserializationContext) -> {

                    JsonObject jsonObject = jsonElement.getAsJsonObject();

                    PushApplication pushApplication = new PushApplication();

                    pushApplication.setName( getString("name", jsonElement) );
                    pushApplication.setDescription( getString("description", jsonElement) );
                    pushApplication.setDeveloper( getString("developer", jsonElement) );
                    pushApplication.setMasterSecret( getString("masterSecret", jsonElement));
                    pushApplication.setPushApplicationID( getString("pushApplicationID", jsonElement) );


                    List<Variant> variantList = new ArrayList<>();
                    if (jsonObject.has("variants")) {
                        JsonArray variants = jsonObject.get("variants").getAsJsonArray();
                        for (JsonElement variantJson : variants) {
                            String typeString = getString("type", variantJson);

                            Variant variant = null;
                            if (typeString.equals("ios")) {
                                variant = new iOSVariant();
                            } else if(typeString.equals("android")) {
                                variant = new AndroidVariant();
                            }
                            variantList.add( variant );
                        }
                    }

                    pushApplication.setVariants( variantList );
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
     * @param url URL to the aerogear UPS server
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

        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder newRequest = request.newBuilder().header("Authorization", token);
                    return chain.proceed(newRequest.build());
                });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        cachedInstance = retrofit.create(UnifiedPushService.class);

        return cachedInstance;
    }

    private static String getString(String key, JsonElement jsonElement) {
        return jsonElement.getAsJsonObject().get(key).getAsString();
    }

    public UnifiedPushService getAdminService(String variantID, String variantSecret) {
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request.Builder newRequest = request.newBuilder().header("Authorization", Credentials.basic(variantID, variantSecret));
                    return chain.proceed(newRequest.build());
                });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(okHttpClientBuilder.build())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit.create(UnifiedPushService.class);
    }
}
