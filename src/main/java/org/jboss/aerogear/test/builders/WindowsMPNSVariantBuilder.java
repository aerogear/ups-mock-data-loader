package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.WindowsMPNSVariant;

/**
 * Builder for WindowsMPNS variant.
 */
public class WindowsMPNSVariantBuilder extends VariantBuilder<WindowsMPNSVariantBuilder, WindowsMPNSVariant> {
    WindowsMPNSVariantBuilder() {

    }

    @Override
    protected WindowsMPNSVariant createInstance() {
        return new WindowsMPNSVariant();
    }

    @Override
    protected void populateFields(WindowsMPNSVariant instance) {
        // Nothing to do
    }

    @Override
    protected WindowsMPNSVariantBuilder getThis() {
        return this;
    }
}
