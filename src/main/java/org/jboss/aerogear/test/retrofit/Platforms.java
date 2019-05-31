package org.jboss.aerogear.test.retrofit;

public enum Platforms {
    IOS("ios"),
    ANDROID("android");

    private final String endpoint;

    Platforms(String platformEndpoint) {
        this.endpoint = platformEndpoint;
    }
}
