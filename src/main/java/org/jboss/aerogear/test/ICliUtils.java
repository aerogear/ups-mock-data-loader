package org.jboss.aerogear.test;

import org.apache.commons.cli.CommandLine;

public interface ICliUtils {
    String OPTION_APPS = "apps";
    String OPTION_TOKENS = "tokens";
    String OPTION_VARIANTS = "variants";
    String OPTION_USERNAME = "username";
    String OPTION_PASSWORD = "password";
    String OPTION_CLIENTID = "clientid";
    String OPTION_URL = "url";
    String OPTION_KEYCLOAK = "keycloak";
    String OPTION_CSV = "csv";
    String OPTION_APPEND = "append";
    String OPTION_ALIAS = "alias";

    String DEFAULT_CLIENT_ID = "unified-push-server-js";
    String DEFAULT_URL = "http://localhost:8080";
    String DEFAULT_KEYCLOAK = "http://localhost:8080";

    public static int getIntOptionValue(final CommandLine cmd, final String optionName) {
        return getIntOptionValue(cmd, optionName, null);
    }

    public static int getIntOptionValue(final CommandLine cmd, final String optionName, final Integer defaultValue) {
        if (!cmd.hasOption(optionName) && defaultValue != null) {
            return defaultValue;
        }
        try {
            return ((Number) cmd.getParsedOptionValue(optionName)).intValue();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }
}
