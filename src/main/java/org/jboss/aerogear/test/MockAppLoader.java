package org.jboss.aerogear.test;

import org.jboss.aerogear.test.builders.PushApplicationBuilder;
import org.jboss.aerogear.unifiedpush.api.PushApplication;

import java.util.Observable;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Loader for MOCK apps.
 * Notifies its list of {@link java.util.Observer}s everytime a new app is created or if the creation has failed.
 */
public class MockAppLoader extends Observable implements Runnable {
    private String APPNAME_PATTERN = "Test App %d";
    private String DEVELOPER_PATTERN = "Developer %d";
    private String DESCRIPTION_PATTERN = "Test App %d";
    private String MASTER_SECRET = "Shhh!!! Don't tell anyone!!";

    private final int appCount;

    private final AerogearAdminServiceProvider provider;

    /**
     * Initializes the loader.
     * @param provider provider to be used to authenticate to UPS
     * @param appCount number of apps to be created
     */
    MockAppLoader(final AerogearAdminServiceProvider provider, final int appCount) {
        this.provider = provider;
        this.appCount = appCount;
    }

    /**
     * Runs the loader
     */
    public void run() {

        provider.setCacheAnswer(false);

        for (int i = 0; i < appCount; i++) {
            // Whatever the creation succeeds or fails, the change state must be true to notify the observers
            setChanged();
            String appId = UUID.randomUUID().toString();
            String appName = String.format(APPNAME_PATTERN, i);

            try {

                PushApplicationBuilder builder =
                    PushApplicationBuilder.forApplication(appId, appName)
                        .withDescription(String.format(DESCRIPTION_PATTERN, i))
                        .withDeveloper(String.format(DEVELOPER_PATTERN, i))
                        .withMasterSecret(MASTER_SECRET)
                        .withPushApplicationID(appId);

                PushApplication app = provider.getAdminService().createPushApplication(builder.build()).execute().body();

                // App created: notify the observers and pass the newly created app§
                notifyObservers(app);
            } catch (Exception re) {
                Logger.getAnonymousLogger().log(Level.SEVERE, re.getMessage(), re);
                // Creation failed: notify the observers and pass the exception
                notifyObservers(re);
            }
        }
    }
}
