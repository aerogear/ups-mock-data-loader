package org.jboss.aerogear.test;

import org.apache.commons.cli.*;

public class Main implements ICliUtils {

    private static void validateCommandLine(final CommandLine cl) throws Exception {

        if (cl.hasOption(OPTION_APPS) && !(cl.hasOption(OPTION_USERNAME) && cl.hasOption(OPTION_PASSWORD))) {
            throw new Exception (String.format("Option <%s> requires both <%s> and <%s>", OPTION_APPS, OPTION_USERNAME, OPTION_PASSWORD));
        }

        switch(cl.getOptionValues(OPTION_TOKENS).length) {
            case 1:
                if (cl.hasOption(OPTION_VARIANTS) && cl.hasOption(OPTION_APPS)) {
                    break;
                }
                throw new Exception (String.format("If no variantid:secret is specified, both <%s> and <%s> params are required", OPTION_VARIANTS, OPTION_APPS));
            case 2:
                if (cl.hasOption(OPTION_VARIANTS) || cl.hasOption(OPTION_APPS)) {
                    throw new Exception (String.format("When variantid:secret is specified, <%s> and <%s> cannot be used", OPTION_VARIANTS, OPTION_APPS));
                }

                final String variantidAndSecret = cl.getOptionValues(OPTION_TOKENS)[1];

                int colonPosition = variantidAndSecret.indexOf(':');

                if (colonPosition <= 0 || colonPosition == variantidAndSecret.length() - 1) {
                    throw new Exception("Both variant id and secret must be specified. Format: VARIANTID:SECRET");
                }

                break;
            default:
                throw new Exception(String.format("<%s> supports only up to 2 arguments", OPTION_TOKENS));
        }


    }

    public static void main(String[] args) {

        Options options = new Options();
        options.addOption(Option.builder("a").longOpt(OPTION_APPS).hasArg(true).argName("total").type(Number.class).desc("Number of apps to be generated").required(false).build());
        options.addOption(Option.builder("v").longOpt(OPTION_VARIANTS).hasArg(true).argName("total").type(Number.class).desc("Number of variants to be generated").required(false).build());
        options.addOption(Option.builder("t").longOpt(OPTION_TOKENS).hasArg(true).numberOfArgs(2).argName("total [variantid:secret]").optionalArg(true).type(Number.class).desc("Number of tokens to be generated").required(true).build());
        options.addOption(Option.builder("u").longOpt(OPTION_USERNAME).hasArg(true).argName("username").desc("Username to be used to authenticate to the UPS").required(false).build());
        options.addOption(Option.builder("p").longOpt(OPTION_PASSWORD).hasArg(true).argName("password").desc("Password to be used to authenticate to the UPS").required(false).build());
        options.addOption(Option.builder("c").longOpt(OPTION_CLIENTID).hasArg(true).argName("id").desc("Client id used to create the apps. Defaults to <" + DEFAULT_CLIENT_ID + ">").required(false).build());
        options.addOption(Option.builder("U").longOpt(OPTION_URL).hasArg(true).argName("UPS URL").desc("URL to the UPS server. Defaults to <" + DEFAULT_URL + ">").required(false).build());
        options.addOption(Option.builder("C").longOpt(OPTION_CSV).hasArg(true).argName("CSV FILE").desc("Generates a CSV file containing: variantid, token alias and tokenid").required(false).build());
        options.addOption(Option.builder("A").longOpt(OPTION_ALIAS).hasArg(true).argName("alias").desc("Use this option if you want a single alias for all the tokens").required(false).build());
        options.addOption(Option.builder("X").longOpt(OPTION_APPEND).desc("Use this option if you want to append the list of created tokens to the given CSV file").required(false).build());

        CommandLineParser parser = new DefaultParser();

        try {
            CommandLine cmd = parser.parse(options, args);

            validateCommandLine(cmd);

            try {
                MockDataLoaderBuilder
                    .forCli(cmd)
                    .build()
                    .run();
            } finally {
            }

        } catch (Exception e) {
            System.out.println("ERROR : " + e.getMessage());
            System.out.println();

            final String syntax = "mock-data-loader.sh " +
                "-u|--username <username>" +
                "-p|--password <password>" +
                "-a|--apps <TOTAL> " +
                "-t|--tokens <TOTAL> [variantid:secret] " +
                "-A|--tokenAlias <alias>" +
                "-v|--variants <TOTAL> " +
                "[-c|--clientid <CLIENTID> " +
                " -U|--url <UPS URL>] " +
                "-C|--csv <path> " +
                "-X|--append";


            new HelpFormatter().printHelp(syntax, options);
        }

    }
}
