package au.org.ala.util;

import lombok.Getter;
import org.slf4j.Logger;

import java.text.MessageFormat;
import java.util.Date;

/**
 * A counter that reports via a log at regular intervals.
 * <p>
 * When the count interval is reached, the message is printed.
 * This has the following elements {0} = total count, {1} = total time, {2} = rate per second, {3} = percentage done
 * {4} = last object processed.
 * </p>
 */
public class Counter implements CounterMXBean {
    private String message;
    private Logger logger;
    private int counter;
    private int current;
    private int size;
    private long start;
    private long stop;
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
     * Get the start time for the counter.
     *
     * @retyrn The time the counter started
     */
    @Override
    public Date getStartTime() {
        return this.start == 0 ? null : new Date(this.start);
    }

    /**
     * Get the stop time for the counter.
     *
     * @retyrn The time the counter stapped
     */
    @Override
    public Date getStopTime() {
        return this.stop == 0 ? null : new Date(this.stop);
    }

    /**
     * Get the current rate for the counter.
     * <p>
     * If the counter is stopped, return the total rate.
     * </p>
     *
     * @return The number of counts per second in the current segment
     */
    @Override
    public double getCurrentRate() {
        if (this.stop != 0)
            return this.computeRate(this.counter, this.start, this.stop);
        return this.computeRate(this.current, this.segment, System.currentTimeMillis());
    }

    /**
     * Get the total count.
     *
     * @return The count
     */
    @Override
    public int getCount() {
        return this.counter;
    }

    /**
     * Start the counter
     */
    synchronized public void start() {
        this.counter = 0;
        this.current = 0;
        this.start = System.currentTimeMillis();
        this.segment = this.start;
        this.stop = 0;
    }

    /**
     * Stop the counter
     */
    synchronized public void stop() {
        this.stop = System.currentTimeMillis();
        this.report(null);
    }

    /**
     * Increment the counter
     *
     * @param last The last object processed (can be null)
     */
    synchronized public void increment(Object last) {
        this.counter++;
        this.current++;
        if (this.counter % this.interval == 0) {
            this.segment = this.report(last);
            this.current = 0;
        }
    }

    /**
     * Build the logging message
     *
     * @param now The current time
     * @param last The last object processed
     *
     * @return The formatted message
     */
    protected String buildMessgae(long now, Object last) {
        double elapsed = (now - this.start) * 0.001;
        double percentage = this.size <= 0 ? 0.0 : (100.0 * this.counter) / this.size;
        double speed = this.stop == 0 ? this.computeRate(this.current, this.segment, now) : this.computeRate(this.counter, this.start, this.stop);
        return MessageFormat.format(this.message, this.counter, elapsed, speed, percentage, last == null ? "-" : last);

    }

    /**
     * Report progress.
     *
     * @param last The last object processed
     */
    private long report(Object last) {
        long now = this.stop != 0 ? this.stop : System.currentTimeMillis();
        this.logger.info(this.buildMessgae(now, last));
        return now;
    }

    private double computeRate(int counter, long start, long stop) {
        long time = stop - start;
        return time <= 0 ? 0.0 : (counter * 1000.0) / time;
    }

}
