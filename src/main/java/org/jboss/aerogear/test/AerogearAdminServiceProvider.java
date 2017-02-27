package org.jboss.aerogear.test;

import at.ftec.aerogear.api.impl.DefaultAerogearAdminService;
import at.ftec.aerogear.model.PushServer;

/**
 * Used to return an already authenticated {@link at.ftec.aerogear.api.AerogearAdminService} object.
 */
public class AerogearAdminServiceProvider {
    private final String url;
    private final String clientid;
    private final String username;
    private final String password;

    private boolean cacheAnswer = false;

    private DefaultAerogearAdminService cachedInstance = null;

    public AerogearAdminServiceProvider(final String url) {
        this.url = url;
        this.clientid = null;
        this.username = null;
        this.password = null;
    }

    /**
     * Builds a new provider
     * @param url URL to the aerogear UPS server
     * @param clientid clientid to be used to authenticate
     * @param username ups username
     * @param password ups password
     */
    public AerogearAdminServiceProvider(final String url, final String clientid, final String username, final String password) {
        this.url = url;
        this.clientid = clientid;
        this.username = username;
        this.password = password;
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
     * @return an {@link at.ftec.aerogear.api.AerogearAdminService} instance
     */
    public DefaultAerogearAdminService getAdminService() {
        if (cacheAnswer && cachedInstance != null) {
            return cachedInstance;
        }
        PushServer pushServer = new PushServer(url);

        if (username != null) {
            pushServer.setKeycloakCredentials(username, password, clientid);
        }

        cachedInstance = new DefaultAerogearAdminService(pushServer);

        return cachedInstance;
    }
}
