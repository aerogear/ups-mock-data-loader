package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.SimplePushVariant;

/**
 * Builder for simple push variant
 */
public class SimplePushVariantBuilder extends VariantBuilder<SimplePushVariantBuilder, SimplePushVariant>{
    SimplePushVariantBuilder() {

    }

    @Override
    protected SimplePushVariant createInstance() {
        return new SimplePushVariant();
    }

    @Override
    protected void populateFields(SimplePushVariant instance) {
        // Nothing to do
    }

    @Override
    protected SimplePushVariantBuilder getThis() {
        return this;
    }
}
