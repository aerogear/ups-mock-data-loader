package org.jboss.aerogear.test;

import at.ftec.aerogear.api.impl.DefaultAerogearAdminService;
import org.jboss.aerogear.test.builders.AndroidVariantBuilder;
import org.jboss.aerogear.test.builders.VariantBuilder;
import org.jboss.aerogear.unifiedpush.api.PushApplication;
import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.api.VariantType;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Loader for mock variant objects
 */
public class MockVariantLoader extends Observable implements Observer, Runnable {

    private static final String VARIANT_DESC_PATTERN = "Variant %d for %s";
    private static final String VARIANT_DEVELOPER = "Mock Developer";
    private static final String VARIANT_NAME_PATTERN = "Variant-%s-%d";

    private final String appId;
    private final String appName;
    private final int variantCount;
    private final AerogearAdminServiceProvider provider;

    /**
     * Initializes the object
     * @param provider provider to be used to authenticate to UPS
     * @param appId id of the application owning these variants
     * @param appName name of the application owning these variants
     * @param variantCount number of variants to be created
     */
    MockVariantLoader(AerogearAdminServiceProvider provider, final String appId, final String appName, final int variantCount) {
        this.appId = appId;
        this.appName = appName;
        this.variantCount = variantCount;
        this.provider = provider;
    }

    /**
     * Runs this loader
     */
    public void run() {
        generateVariants(this.appId);
    }

    /**
     * Generates the variants
     * @param appId ID of the app owning the variants.
     */
    private void generateVariants(final String appId) {

        this.provider.setCacheAnswer(false);
        for (int variantNumber = 0; variantNumber < variantCount; variantNumber++) {
            setChanged();
            String variantID = UUID.randomUUID().toString();
            String variantSecret = UUID.randomUUID().toString();

            Variant v = VariantBuilder.<AndroidVariantBuilder>forVariant(VariantType.ANDROID)
                .withVariantId(variantID)
                .withDescription(String.format(VARIANT_DESC_PATTERN, variantNumber, appName))
                .withDeveloper(VARIANT_DEVELOPER)
                .withId(variantID)
                .withName(String.format(VARIANT_NAME_PATTERN, appName, variantNumber))
                .withSecret(variantSecret)
                .withGoogleKey("googlekey")
                .withProjectNumber("123456")
                .build();

            try {
                DefaultAerogearAdminService aerogearAdminService = provider.getAdminService();
                v = aerogearAdminService.createVariant(v, appId);
                notifyObservers(v);
            } catch (Exception e) {
                notifyObservers(e);
            }
        }
    }

    @Override
    public void update(Observable o, Object newApplication) {
        if (newApplication instanceof PushApplication) {
            PushApplication pa = (PushApplication) newApplication;
            generateVariants(pa.getPushApplicationID());
        }
    }
}
