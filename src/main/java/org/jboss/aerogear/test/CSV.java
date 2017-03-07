package org.jboss.aerogear.test;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This object takes care of creating a CSV file with a number of columns.
 */
public class CSV {
    final PrintWriter pw;

    private CSV() {
        this.pw = null;

        Runtime.getRuntime().addShutdownHook(new Thread( () -> {
            CSV.this.close();
        }));
    }

    /**
     * Builds the object
     * @param outputPath path to the CSV file to be created. If it already exists, it is overwritten.
     * @param columns List of columns header. A comment at the beginning of the CSV will be inserted with the list of columns
     * @throws IOException on any IO error
     */
    public CSV(final String outputPath, final String... columns) throws IOException {
        pw = new PrintWriter(new FileOutputStream(outputPath), true);
        String header = String.join(",", columns);
        pw.println("#" + header);
    }

    /**
     * Adds a new line to the CSV file
     * @param columns Columns to be added. No check is performed on the correspondence of these columns with the headers.
     * @throws IOException on any IO error
     */
    public void addLine(String... columns) throws IOException {
        pw.println(String.join(",", columns));
    }

    /**
     * Close the stream to the file.
     */
    public void close() {
        pw.flush();
        pw.close();
    }

    /**
     * NOOP instance.
     * Simply discards all the data it receives.
     */
    static class NOOP extends CSV {

        /**
         * Singleton instance.
         */
        public static final NOOP INSTANCE = new NOOP();

        private NOOP() {
        }

        @Override
        public void addLine(String... columns) {
        }

        @Override
        public void close() {
        }
    }
}