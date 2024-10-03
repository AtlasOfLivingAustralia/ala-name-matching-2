package au.org.ala.util;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.NumberFormat;

/**
 * Summary statistics for some sort of element
 */
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);

    private static final NumberFormat PERCENTAGE = new DecimalFormat("0.00%");
    private static final NumberFormat STATISTIC = new DecimalFormat("0.00");

    private static final String[] KEYS = new String[] { "name", "n", "total", "mean", "stddev", "min", "max" };

    @Getter
    private final String name;
    @Getter
    private long count = 0;
    @Getter
    private long sum = 0;
    @Getter
    private long sumSq = 0;
    @Getter
    private long min = Long.MAX_VALUE;
    @Getter
    private long max = Long.MIN_VALUE;

    /**
     * Construct for a name
     *
     * @param name The statistic name
     */
    public Statistics(String name) {
        this.name = name;
    }

    /**
     * Add a value to the statistic.
     *
     * @param value The value
     */
    synchronized public void add(long value) {
        this.count++;
        this.sum += value;
        this.sumSq += value * value;
        this.min = Math.min(this.min, value);
        this.max = Math.max(this.max, value);
    }

    /**
     * Get the mean value of the statistic
     *
     * @return The mean value
     */
    public double getMean() {
        return this.count <= 0 ? Double.NaN : ((double) this.sum) / this.count;
    }

    /**
     * Get the standard deviation of the statistic
     *
     * @return The mean value
     */
    public double getStdDev() {
        if (this.count <= 0)
            return Double.NaN;
        double mean = this.getMean();
        return Math.sqrt(((double) this.sumSq / this.count - mean * mean));
    }

    /**
     * Generate a report line
     *
     * @param full Include all values
     * @param totalCount The total for reporting as a percentage. Less than zero to ignore
     * @param totalSum The total for reporting as a percentage. Less than zero to ignore
     * @return A report line
     */
    public String reportLine(boolean full, long totalCount, long totalSum) {
        StringBuilder builder = new StringBuilder();
        builder.append(this.getName());
        builder.append('\t');
        builder.append(this.getCount());
        if (totalCount >= 0) {
            builder.append(" (");
            if (totalCount == 0)
                builder.append("-");
            else
                builder.append(PERCENTAGE.format(((double) this.getCount()) / ((double) totalCount)));
            builder.append(")");
        }
        builder.append('\t');
        builder.append(this.getSum());
        if (totalSum >= 0) {
            builder.append(" (");
            if (totalSum == 0)
                builder.append("-");
            else
                builder.append(PERCENTAGE.format(((double) this.getSum()) / ((double) totalSum)));
            builder.append(")");
        }
        if (full) {
            builder.append("\t");
            builder.append(STATISTIC.format(this.getMean()));
            builder.append("\t");
            builder.append(STATISTIC.format(this.getStdDev()));
            builder.append("\t");
            builder.append(this.getMin());
            builder.append("\t");
            builder.append(this.getMax());
       }
        return builder.toString();
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.name);
        builder.append("[ n = ");
        builder.append(this.count);
        if (this.count > 0) {
            builder.append(", total = ");
            builder.append(this.sum);
            builder.append(", mean = ");
            builder.append(this.getMean());
            builder.append(", stddev = ");
            builder.append(this.getStdDev());
            builder.append(", min = ");
            builder.append(this.min);
            builder.append(", max = ");
            builder.append(this.max);
        }
        builder.append(" ]");
        return builder.toString();
    }
}
