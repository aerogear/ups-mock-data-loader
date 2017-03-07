package org.jboss.aerogear.test;


import org.apache.commons.cli.CommandLine;
import org.slf4j.LoggerFactory;

/**
 * Builder for mock data loaders
 */
public class MockDataLoaderBuilder {

    /**
     * Creates a new builder for {@link MockTokenLoader} objects.
     * @param url URL to the UPS server
     * @return the builder instance
     */
    public static MockTokenLoaderBuilder forMockTokenLoader(final String url) {
        return new MockTokenLoaderBuilder(new AerogearAdminServiceProvider(url));
    }

    /**
     * Creates a new builder for {@link MockVariantLoader} objects.
     * @param url URL to the UPS server
     * @param username username for the UPS server
     * @param password password for the UPS server
     * @param clientId clientId for the UPS server
     * @return the builder instance
     */
    public static MockVariantLoaderBuilder forMockVariantLoader(final String url, final String username, final String password, final String clientId) {
        return new MockVariantLoaderBuilder(new AerogearAdminServiceProvider(url, clientId, username, password));
    }

    /**
     * Creates a new builder for {@link MockAppLoader} objects.
     * @param url URL to the UPS server
     * @param username username for the UPS server
     * @param password password for the UPS server
     * @param clientId clientId for the UPS server
     * @return the builder instance
     */
    public static MockAppLoaderBuilder forMockAppLoader(final String url, final String username, final String password, final String clientId, int appCount) {
        return new MockAppLoaderBuilder(new AerogearAdminServiceProvider(url, clientId, username, password), appCount);
    }

    public static MockLoaderBuilder forCli(final CommandLine cli) {
        return new MockLoaderBuilder(cli);
    }


    public static class MockTokenLoaderBuilder {
        private String variantID;
        private String variantSecret;
        private int tokenCount;
        private String tokenAlias;
        private String csvFile;

        private final AerogearAdminServiceProvider provider;

        private MockTokenLoaderBuilder(AerogearAdminServiceProvider provider) {
            this.provider = provider;
        }

        public MockTokenLoaderBuilder with(String variantId, String variantSecret, int tokenCount) {
            this.variantID = variantId;
            this.variantSecret = variantSecret;
            this.tokenCount = tokenCount;
            return this;
        }

        public MockTokenLoaderBuilder withTokenAlias(String alias) {
            this.tokenAlias = alias;
            return this;
        }

        public MockTokenLoaderBuilder withCsvFile(String path) {
            this.csvFile = path;
            return this;
        }

        public Runnable build() {
            //LoggerThread lt = new LoggerThread(LoggerFactory.getLogger(MockTokenLoader.class), 0, 0, tokenCount);
            MockTokenLoader res = new MockTokenLoader(provider, variantID, variantSecret, tokenCount, tokenAlias, csvFile);

            res.addObserver(new ConsoleObserver(LoggerFactory.getLogger(MockTokenLoader.class), 0, 0, tokenCount));
            return res;
        }
    }

    public static class MockVariantLoaderBuilder {
        private String appId;
        private String appName;
        private int variantCount;
        private int tokenCount;
        private String tokenAlias;
        private String csvPath;

        private final AerogearAdminServiceProvider provider;

        private MockVariantLoaderBuilder(AerogearAdminServiceProvider provider) {
            this.provider = provider;
        }

        private MockVariantLoaderBuilder with(final String appId, final String appName, final int variantCount) {
            this.appId = appId;
            this.appName = appName;
            this.variantCount = variantCount;
            return this;
        }

        public MockVariantLoaderBuilder withTokens(final int tokenCount, final String tokenAlias) {
            this.tokenCount = tokenCount;
            this.tokenAlias = tokenAlias;
            return this;
        }

        public MockVariantLoaderBuilder withCSVLog(final String csvPath) {
            this.csvPath = csvPath;
            return this;
        }

        public Runnable build() {
            MockVariantLoader mockVariantLoader = new MockVariantLoader(provider, appId, appName, variantCount);
            if (tokenCount != 0) {
                MockTokenLoader mtl = new MockTokenLoader(provider, null, null, tokenCount, this.tokenAlias, this.csvPath);
                mockVariantLoader.addObserver(mtl);
            }

            return mockVariantLoader;
        }
    }

    public static class MockAppLoaderBuilder {
        private final AerogearAdminServiceProvider provider;

        private int appCount;
        private int tokenCount;
        private String tokenAlias;
        private String csvPath;
        private int variantCount;

        private MockAppLoaderBuilder(AerogearAdminServiceProvider provider, final int appCount) {
            this.provider = provider;
            this.appCount = appCount;
        }

        public MockAppLoaderBuilder withTokens(final int tokenCount, final String tokenAlias) {
            this.tokenCount = tokenCount;
            this.tokenAlias = tokenAlias;
            return this;
        }

        public MockAppLoaderBuilder withCSVLog(final String csvPath) {
            this.csvPath = csvPath;
            return this;
        }

        public MockAppLoaderBuilder withVariants(final int variantCount) {
            this.variantCount = variantCount;
            return this;
        }

        public Runnable build() {
            ConsoleObserver observer = new ConsoleObserver(LoggerFactory.getLogger(MockAppLoader.class), appCount, variantCount, tokenCount);

            MockAppLoader appLoader = new MockAppLoader(provider, appCount);
            appLoader.addObserver(observer);
            if (variantCount > 0) {
                MockVariantLoader mockVariantLoader = new MockVariantLoader(provider, null, null, variantCount);
                appLoader.addObserver(mockVariantLoader);
                mockVariantLoader.addObserver(observer);

                if (tokenCount > 0) {
                    MockTokenLoader mtl = new MockTokenLoader(provider, null, null, tokenCount, this.tokenAlias, this.csvPath);
                    mockVariantLoader.addObserver(mtl);
                    mtl.addObserver(observer);
                }
            }

            return appLoader;
        }
    }

    public static class MockLoaderBuilder {
        final CommandLine cli;

        private MockLoaderBuilder(final CommandLine cli) {
            this.cli = cli;
        }

        public Runnable build() {
            if (cli.hasOption(ICliUtils.OPTION_APPS)) {

                return MockDataLoaderBuilder.
                    forMockAppLoader(
                        cli.getOptionValue(ICliUtils.OPTION_URL, ICliUtils.DEFAULT_URL),
                        cli.getOptionValue(ICliUtils.OPTION_USERNAME),
                        cli.getOptionValue(ICliUtils.OPTION_PASSWORD),
                        cli.getOptionValue(ICliUtils.OPTION_CLIENTID, ICliUtils.DEFAULT_CLIENT_ID),
                        ICliUtils.getIntOptionValue(cli, ICliUtils.OPTION_APPS))
                    .withVariants(ICliUtils.getIntOptionValue(cli, ICliUtils.OPTION_VARIANTS, 0))
                    .withTokens(ICliUtils.getIntOptionValue(cli, ICliUtils.OPTION_TOKENS, 0), cli.getOptionValue(ICliUtils.OPTION_ALIAS))
                    .withCSVLog(cli.getOptionValue(ICliUtils.OPTION_CSV))
                    .build();
            } else {
                // Only tokens must be generated
                final String[] tokenOptionValues = cli.getOptionValues(ICliUtils.OPTION_TOKENS);
                int tokenCount = Integer.parseInt(tokenOptionValues[0]);
                final String[] idAndSecret = tokenOptionValues[1].split(":");

                return MockDataLoaderBuilder
                    .forMockTokenLoader(
                        cli.getOptionValue(ICliUtils.OPTION_URL, ICliUtils.DEFAULT_URL))
                    .with(idAndSecret[0], idAndSecret[1], tokenCount)
                    .withCsvFile(cli.getOptionValue(ICliUtils.OPTION_CSV))
                    .withTokenAlias(cli.getOptionValue(ICliUtils.OPTION_ALIAS))
                    .build();
            }

        }
    }
}
