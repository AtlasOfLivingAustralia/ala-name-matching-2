package au.org.ala.util;

import org.slf4j.Logger;

import java.text.MessageFormat;

/**
 * A counter that reports via a log at regular intervals.
 * <p>
 * When the count interval is reached, the message is printed.
 * This has the following elements {0} = total count, {1} = total time, {2} = rate per second, {3} = percentage done
 * {4} = last object processed.
 * </p>
 */
public class Counter {
    private String message;
    private Logger logger;
    private int counter;
    private int size;
    private long start;
    private long segment;
    private int interval;

    /**
     * Construct with a defined identifier and a logger to report to.
     *
     * @param message The message to use while logging (in {@link MessageFormat} format)
     * @param logger The logger
     * @param interval The logging interval
     * @param size The maximum count (-1 for no limit)
     */
    public Counter(String message, Logger logger, int interval, int size) {
        this.message = message;
        this.logger = logger;
        this.interval = interval;
        this.size = size;
    }

    /**
     * Start the counter
     */
    synchronized public void start() {
        this.counter = 0;
        this.start = System.currentTimeMillis();
        this.segment = this.start;
    }


    /**
     * Stop the counter
     */
    synchronized public void stop() {
        this.report(null);
    }

    /**
     * Incerement the counter
     *
     * @param last The last object processed (can be null)
     */
    synchronized public void increment(Object last) {
        this.counter++;
        if (this.counter % this.interval == 0) {
            this.segment = this.report(last);
        }
    }

    /**
     * Report progress.
     *
     * @param last The last object processed
     */
    private long report(Object last) {
        long now = System.currentTimeMillis();
        double percentage = this.size <= 0 ? 0.0 : (100.0 * this.counter) / this.size;
        double speed = (1000.0 * this.interval) / (now - this.segment);
        double elapsed = (now - this.start) * 0.001;

        this.logger.info(MessageFormat.format(this.message, this.counter, elapsed, speed, percentage, last));
        return now;
    }

}
