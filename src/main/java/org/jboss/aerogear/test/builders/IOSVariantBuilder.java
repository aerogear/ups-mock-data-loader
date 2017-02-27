package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.iOSVariant;

/**
 * Builder for iOS variant.
 */
public class IOSVariantBuilder extends VariantBuilder<IOSVariantBuilder, iOSVariant> {

    private String passphrase;
    private boolean production = false;
    private byte[] certificate;

    IOSVariantBuilder() {
    }

    public IOSVariantBuilder withPassphrase(final String passphrase) {
        this.passphrase = passphrase;
        return this;
    }

    public IOSVariantBuilder withCertificate(final byte[] certificate) {
        this.certificate = certificate;
        return this;
    }

    public IOSVariantBuilder withProduction(final boolean production) {
        this.production = production;
        return this;
    }

    @Override
    protected iOSVariant createInstance() {
        return new iOSVariant();
    }

    @Override
    protected void populateFields(iOSVariant instance) {
        instance.setPassphrase(this.passphrase);
        instance.setProduction(this.production);
        instance.setCertificate(this.certificate);
    }

    @Override
    protected IOSVariantBuilder getThis() {
        return this;
    }
}
