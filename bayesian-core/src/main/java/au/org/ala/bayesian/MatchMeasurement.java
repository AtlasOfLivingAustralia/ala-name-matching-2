package au.org.ala.bayesian;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A measurement of {@link ClassificationMatcher} performance.
 * <p>
 * This measurement can be used to collect statistics on the effort needed to match a
 * supplied classification.
 * </p>
 * <p>
 * Subclasses may contain additional statistics.
 * </p>
 */
public class MatchMeasurement {
    private static final AtomicLong SEQUENCE = new AtomicLong();
    /**
     * The sequence number for ordering measurements
     */
    @Getter
    private final long sequence = SEQUENCE.getAndIncrement();
    /**
     * The start time for this match
     */
    @Getter
    private long start = 0;
    /**
     * The finish time for this match
     */
    @Getter
    private long stop = 0;
    /**
     * The number of seatches for candidates
     */
    @Getter
    private int searches = 0;
    /**
     * The number of source modifications
     */
    @Getter
    private int searchModifications = 0;
    /**
     * The number of candidates considered
     */
    @Getter
    private int candidates = 0;
    /**
     * The index of the largest candidiate considered worthy
     */
    @Getter
    private int maxCandidate = 0;
    /**
     * The number of hint modfications
     */
    @Getter
    private int hintModifications = 0;
    /**
     * The number of attempts at a match
     */
    @Getter
    private int matches = 0;
    /**
     * The number of match modfications
     */
    @Getter
    private int matchModifications = 0;
    /**
     * The number of matchable candidates
     */
    @Getter
    private int matchable = 0;

    /**
     * Get the elapsed time.
     *
     * @return The difference between the start and stop times or 0 for not yet stopped.
     */
    public long getElapsed() {
        return Math.max(0, this.stop - this.start);
    }

    /**
     * Record the start time
     */
    public void start() {
        this.start = System.currentTimeMillis();
    }

    /**
     * Record the end time
     */
    public void stop() {
        this.stop = System.currentTimeMillis();
    }

    /**
     * Record a search
     */
    public void search() {
        this.searches++;
    }

    /**
     * Record a search modification
     */
    public void searchModification() {
        this.searchModifications++;
    }

    /**
     * Add the number of candidates to the statistics
     *
     * @param n The number of candidates
     */
    public void addCandidates(int n) {
        this.candidates += n;
    }

    /**
     * Record the maximum candidate that is a possible match
     *
     * @param n The maximum candidiate number
     */
    public void maxCandidate(int n) {
        this.maxCandidate = Math.max(this.maxCandidate, n);
    }

    /**
     * Record a hint moddification
     */
    public void hintModification() {
        this.hintModifications++;
    }

    /**
     * Record an attempted match
     */
    public void match() {
        this.matches++;
    }

    /**
     * Record a modification to the attempted match
     */
    public void matchModification() {
        this.matchModifications++;
    }

    /**
     * Record the number of matchable candidates
     *
     * @param n The number of matchable classifiers
     */
    public void addMatchable(int n) {
        this.matchable += n;
    }
}
