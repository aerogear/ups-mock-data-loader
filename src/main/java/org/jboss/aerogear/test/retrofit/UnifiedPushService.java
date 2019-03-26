package org.jboss.aerogear.test.retrofit;

import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.jboss.aerogear.unifiedpush.api.Variant;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * A retrofit API to talk to the unified push service
 */
public interface UnifiedPushService {


    public static final String PUSH_REST_CONTEXT = "rest";

    public static final String APPLICATION_ENDPOINT = PUSH_REST_CONTEXT  + "/applications";
    public static final String PLATFORM_APPLICATION_ENDPOINT = APPLICATION_ENDPOINT +"/{appId}/{platformName}";
    public static final String PLATFORM_VARIANT_ENDPOINT = PLATFORM_APPLICATION_ENDPOINT +"/{variantId}";

    public static final String APPLICATION_RESOURCE_ENDPOINT = APPLICATION_ENDPOINT + "/{applicationId}";

    public static final String HEALTH_ENDPOINT = PUSH_REST_CONTEXT  + "/sys/info/health";

    public static final String DEVICE_ENDPOINT = PUSH_REST_CONTEXT  + "/registry/device";
    public static final String DEVICE_RESOURCE_ENDPOINT = DEVICE_ENDPOINT + "/{deviceToken}";

    public static final String VARIANT_INSTALLATIONS_ENDPOINT = PUSH_REST_CONTEXT  + "/export/{variantId}/installations";


    @POST(APPLICATION_ENDPOINT)
    Call<PushApplication> createPushApplication(@Body PushApplication app);

    @POST(DEVICE_ENDPOINT)
    Call<Installation> registerDevice(@Body Installation installation);

    @POST(PLATFORM_APPLICATION_ENDPOINT)
    Call<Variant> createVariant(@Body Variant v, @Path("appId") String appId, @Path("platformName") String platformName);



}
