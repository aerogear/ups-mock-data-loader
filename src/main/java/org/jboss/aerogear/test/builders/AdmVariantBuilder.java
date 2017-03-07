package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.AdmVariant;

/**
 * Builder for AdmVariant.
 */
public class AdmVariantBuilder extends VariantBuilder<AdmVariantBuilder, AdmVariant>{

    private String clientId;
    private String clientSecret;

    AdmVariantBuilder() {
        super();
    }

    public AdmVariantBuilder withClientId(final String clientId) {
        this.clientId = clientId;
        return this;
    }

    public AdmVariantBuilder withClientSecret(final String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    public AdmVariant createInstance() {
        return new AdmVariant();
    }

    @Override
    protected void populateFields(AdmVariant instance) {
        instance.setClientId(this.clientId);
        instance.setClientSecret(this.clientSecret);
    }

    @Override
    protected AdmVariantBuilder getThis() {
        return this;
    }
}
