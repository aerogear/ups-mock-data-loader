package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.AndroidVariant;

/**
 * Builder for android variant.
 */
public class AndroidVariantBuilder extends VariantBuilder<AndroidVariantBuilder, AndroidVariant>{

    private String projectNumber;
    private String googleKey;

    AndroidVariantBuilder() {
        super();
    }

    public AndroidVariantBuilder withProjectNumber(String projectNumber) {
        this.projectNumber = projectNumber;
        return this;
    }

    public AndroidVariantBuilder withGoogleKey(final String googleKey) {
        this.googleKey = googleKey;
        return this;
    }

    @Override
    protected AndroidVariant createInstance() {
        return new AndroidVariant();
    }

    @Override
    protected void populateFields(AndroidVariant instance) {
        instance.setProjectNumber(this.projectNumber);
        instance.setGoogleKey(this.googleKey);
    }

    @Override
    protected AndroidVariantBuilder getThis() {
        return this;
    }
}
