package org.jboss.aerogear.test;

import org.slf4j.Logger;

import java.util.Observable;
import java.util.Observer;

public class ConsoleObserver implements Observer {

    private int totalApps;
    private int totalVariants;
    private int totalTokens;

    private int currentAppProgress = 0;
    private int currentAppFailed = 0;

    private int currentVariant = 0;
    private int failedVariants = 0;
    private int currentToken = 0;
    private int failedToken = 0;

    private long startTime = System.currentTimeMillis();

    private final Logger LOG;

    public ConsoleObserver(Logger logger, final int totalApps, final int totalVariants, final int totalTokens) {
        this.totalApps = totalApps;
        this.totalVariants = totalVariants;
        this.totalTokens = totalTokens;
        this.LOG = logger;
    }

    /**
     * Increments the count of elaborated variants for the current app
     * @param failed if <code>true</code> increments the counter for failed variant creation
     */
    public synchronized void variantElaborated(boolean failed, Throwable thr) {
        if (failed) {
            LOG.error("Failure creating variant: {}", new Object[]{thr.getMessage()}, thr);
            failedVariants ++;
        } else {
            currentVariant++;
        }
        printUpdate();
    }

    /**
     * Increments the count of elaborated tokens for the current app
     * @param failed if <code>true</code> increments the counter for failed tokens creation
     */
    public synchronized void tokenElaborated(boolean failed, Throwable thr) {
        if (failed) {
            LOG.error("Failure creating token: {}", new Object[]{thr.getMessage()}, thr);
            failedToken ++;
        } else {
            currentToken++;
        }
        printUpdate();
    }

    /**
     * Increments the count of elaborated apps
     * @param failed if <code>true</code> increments the counter for failed apps creation
     */
    public synchronized  void appElaborated(boolean failed, Throwable thr) {
        if (failed) {
            LOG.error("Failure creating app: {}", new Object[]{thr.getMessage()}, thr);
            currentAppFailed ++;
        } else {
            currentAppProgress++;
        }

        printUpdate();
        System.out.println();
        reset();
    }

    /**
     * Resets the counters for tokens and variants
     */
    private synchronized void reset() {
        printUpdate();
        currentToken = currentVariant = failedToken = failedVariants = 0;
        startTime = System.currentTimeMillis();
    }

    /**
     * Print a progress update
     */
    private synchronized void printUpdate() {
        System.out.printf("\rApps created/failed/total: %3d/%3d/%3d - Variants created/failed/total: %3d/%3d/%3d - Tokens created/failed/total: %5d/%5d/%5d - Elapsed: %d secs",
            currentAppProgress, currentAppFailed, totalApps,
            currentVariant, failedVariants, totalVariants,
            currentToken, failedToken, totalTokens * totalVariants, (System.currentTimeMillis() - startTime)/1000);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof MockTokenLoader) {
            if (arg instanceof Exception) {
                tokenElaborated(true, (Exception) arg);
                return;
            } else {
                tokenElaborated(false, null);
            }
        }

        if (o instanceof MockVariantLoader) {
            if (arg instanceof Exception) {
                variantElaborated(true, (Exception) arg);
                return;
            } else {
                variantElaborated(false, null);
            }
        }

        if (o instanceof MockAppLoader) {
            if (arg instanceof Exception) {
                appElaborated(true, (Exception) arg);
                return;
            } else {
                appElaborated(false, null);
            }
        }
    }
}
