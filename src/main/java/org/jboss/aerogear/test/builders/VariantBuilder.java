package org.jboss.aerogear.test.builders;

import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.api.VariantType;

/**
 * /**
 * Base class for Variant builders.
 * @param <T> This describes the real builder implementation class. It is used to return <code>this</code> form the setter methods.
 * @param <U> This describes the real variant class to be returned by the {@link VariantBuilder#build()} method
 */
public abstract class VariantBuilder<T extends VariantBuilder, U extends Variant> {

    private String name;
    private String developer;
    private String description;
    private String id;
    private String secret;
    private String variantId;

    VariantBuilder() {
    }

    public static <T extends VariantBuilder> T forVariant(VariantType variantType) {

        switch (variantType) {
            case ADM: return (T) new AdmVariantBuilder();
            case WINDOWS_WNS: return (T) new WindowsWNSVariantBuilder();
            case WINDOWS_MPNS: return (T) new WindowsMPNSVariantBuilder();
            case SIMPLE_PUSH: return (T) new SimplePushVariantBuilder();
            case IOS: return (T) new IOSVariantBuilder();
            case ANDROID: return (T) new AndroidVariantBuilder();
        }

        throw new IllegalStateException("Unable to build variant of type: " + variantType);
    }

    // Fluent API

    public T withName(final String name) {
        this.name = name;
        return getThis();
    }

    public T withDeveloper(final String developer) {
        this.developer = developer;
        return getThis();
    }

    public T withDescription(final String description) {
        this.description = description;
        return getThis();
    }

    public T withId(final String id) {
        this.id = id;
        return getThis();
    }

    public T withSecret(final String secret) {
        this.secret = secret;
        return getThis();
    }

    public T withVariantId(final String variantId) {
        this.variantId = variantId;
        return getThis();
    }

    /**
     * Sets the values of the attributes common to every variant
     * @param instance instance to be initialized
     */
    private void initInstance(U instance) {
        instance.setName(this.name);
        instance.setDeveloper(this.developer);
        instance.setDescription(this.description);
        instance.setId(this.id);
        instance.setSecret(this.secret);
        instance.setVariantID(this.variantId);
    }

    /**
     * Builds the variant
     * @return the variant configured with all the values passed using the fluent api
     */
    public final U build() {
        U instance = createInstance();
        initInstance(instance);
        populateFields(instance);
        return instance;
    }

    // Abstract methods

    /**
     * This method must be overridden by subclass to return the value of <code>this</code>.
     * @return this
     */
    protected abstract T getThis();

    /**
     * This method must return an empty, newly build Variant instance.
     *
     * @return the variant instance
     */
    protected abstract U createInstance();

    /**
     * This method must set the value for the specific fields of this variant
     * @param instance instance to be configured
     */
    protected abstract void populateFields(U instance);

}
