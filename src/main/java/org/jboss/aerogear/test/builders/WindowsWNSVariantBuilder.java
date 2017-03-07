package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.WindowsWNSVariant;

/**
 * Builder for WidnowsWNS variant.
 */
public class WindowsWNSVariantBuilder extends VariantBuilder<WindowsWNSVariantBuilder, WindowsWNSVariant> {

    private String sid;
    private String clientSecret;

    WindowsWNSVariantBuilder() {
    }

    public WindowsWNSVariantBuilder withSid(String sid) {
        this.sid = sid;
        return this;
    }

    public WindowsWNSVariantBuilder withClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    @Override
    protected WindowsWNSVariant createInstance() {
        return new WindowsWNSVariant();
    }

    @Override
    protected void populateFields(WindowsWNSVariant instance) {
        instance.setSid(this.sid);
        instance.setClientSecret(this.clientSecret);
    }

    @Override
    protected WindowsWNSVariantBuilder getThis() {
        return this;
    }
}
