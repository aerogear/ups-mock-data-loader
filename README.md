# Mocked data loader

This tools can be used to populate an UPS server with mock applications, variants and tokens

## Build the software

To build from source, you need to have [maven](https://maven.apache.org/) installed on your machine.

```bash
mvn clean install
```

## Load the mock data

uncompress the produced archive in a directory of your choice:

```bash
tar xvfz target/aerogear-mock-data-loader-1.0.0-SNAPSHOT-bin.zip
```
The executable script will be in `aerogear-mock-data-loader-1.0-SNAPSHOT/bin/mock-data-loader.sh`.

#### Creating Mock Data in Standalone UPS

Add your credentials and simply run the script:

```bash
mock-data-loader.sh -u <username> -p <password> --apps <apps> --variants <variants> --tokens <tokens>
```

#### Creating Mock Data in RHMAP

Only to create tokens is possible. You have to specify the variant credentials in otder to do this:
```bash
mock-data-loader.sh -u <username> -p <password> --tokens <tokens> <variantId>:<secret>
```

## More Options

Running the script without arguments will show an help screen:

```bash
usage: mock-data-loader.sh -u|--username <username>-u|--password
                           <password>-a|--apps <TOTAL> -t|--tokens <TOTAL>
                           [variantid:secret] -A|--tokenAlias
                           <alias>-v|--variants <TOTAL> [-c|--clientid
                           <CLIENTID>  -U|--url <UPS URL>]-C|--csv <path>
 -a,--apps <total>                        Number of apps to be generated
 -A,--alias <alias>                       Use this option if you want a
                                          single alias for all the tokens
 -c,--clientid <id>                       Client id used to create the
                                          apps. Defaults to
                                          <unified-push-server-js>
 -C,--csv <CSV FILE>                      Generates a CSV file containing:
                                          variantid, token alias and
                                          tokenid
 -p,--password <password>                 Password to be used to
                                          authenticate to the UPS
 -t,--tokens <total [variantid:secret]>   Number of tokens to be generated
 -u,--username <username>                 Username to be used to
                                          authenticate to the UPS
 -U,--url <UPS URL>                       URL to the UPS server. Defaults
                                          to <http://localhost:8080>
 -v,--variants <total>                    Number of variants to be
                                          generated

```
