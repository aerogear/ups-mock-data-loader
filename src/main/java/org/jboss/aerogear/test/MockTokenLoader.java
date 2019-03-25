package org.jboss.aerogear.test;

import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.Variant;

import java.util.Observable;
import java.util.Observer;
import java.util.UUID;

/**
 * Loader for Mock tokens
 */
public class MockTokenLoader extends Observable implements Observer, Runnable {
    private final String variantID;
    private final String variantSecret;
    private final int tokenCount;
    private final String tokenAlias;
    private final CSV csvFile;
    private final AerogearAdminServiceProvider provider;

    /**
     * Initializes the loader
     * @param provider provider to be used to authenticate to UPS
     * @param variantID variant id owning this variants
     * @param variantSecret the variant secret
     * @param tokenCount number of tokens to be created
     * @param tokenAlias alias to be used for the created tokens
     * @param csvFilePath path to a csv file to be created containing the token data (variantid, token alias and token id)
     */
    MockTokenLoader(final AerogearAdminServiceProvider provider, final String variantID, final String variantSecret, final int tokenCount, final String tokenAlias, final String csvFilePath, boolean append)  {
        try {
            this.variantID = variantID;
            this.variantSecret = variantSecret;
            this.tokenCount = tokenCount;
            this.tokenAlias = tokenAlias;
            this.csvFile = csvFilePath == null ? CSV.NOOP.INSTANCE : new CSV(csvFilePath, append, "VARIANT ID", "TOKEN ALIAS", "TOKEN ID");
            this.provider = provider;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generate a unique token id for android devices
     * @return the token id
     */
    private static String generateAndroidToken() {

        String raw = UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString() + UUID.randomUUID().toString();
        raw = raw.replaceAll("-","");

        return raw;
    }

    /**
     * Generate a unique token id for ios devices
     * @return teh token id
     */
    private static String generateiOSToken() {

        String raw = UUID.randomUUID().toString() + UUID.randomUUID().toString();
        raw = raw.replaceAll("-","");

        return raw;
    }

    /**
     * Runs the loader
     */
    public void run() {
        generateTokens(this.variantID, this.variantSecret);
    }

    /**
     * Generate the tokens.
     *
     * @param variantID Variant owning the tokens
     * @param variantSecret Variant secret
     */
    private void generateTokens(final String variantID, final String variantSecret) {
        provider.setCacheAnswer(true);

        for (int i = 0; i < tokenCount; i++) {
            setChanged();
            String DEVICE_ALIAS = this.tokenAlias == null ? UUID.randomUUID().toString() : this.tokenAlias;
            try {
                Installation installation = new Installation();
                installation.setDeviceToken(generateAndroidToken());
                installation.setAlias(DEVICE_ALIAS);

                installation = provider.getAdminService(variantID, variantSecret).registerDevice(installation);

                notifyObservers(installation);
                csvFile.addLine(variantID, installation.getAlias(), installation.getId());
            } catch (Exception e) {
                notifyObservers(e);
            }
        }
    }

    @Override
    public void update(final Observable o, final Object newVariant) {
        // Can observe only variant loader...
        if (newVariant instanceof Variant) {
            Variant v = (Variant) newVariant;
            generateTokens(v.getVariantID(), v.getSecret());
        }
    }
}
