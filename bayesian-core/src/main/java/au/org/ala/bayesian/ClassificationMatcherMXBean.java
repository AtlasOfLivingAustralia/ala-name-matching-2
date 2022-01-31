package au.org.ala.bayesian;

public interface ClassificationMatcherMXBean {
    /**
     * Get the number of requests
     *
     * @return The number of requests
     */
    public long getRequests();

    /**
     * Get a summary of the time elapsed statistics
     *
     * @return The time statitics
     */
    public String getTimeStatistics();

    /**
     * Get a summary of the search statistics.
     *
     * @return The search statistics
     */
    public String getSearchStatistics();

    /**
     * Get a summary of the search modification statistics.
     *
     * @return The search modification statistics
     */
    public String getSearchModificationStatistics();

    /**
     * Get a summary of the retrieval statistics.
     *
     * @return The retrieval statistics
     */
    public String getCandidateStatistics();

    /**
     * Get a summary of the maximum acceptable candidiate statistics.
     *
     * @return The maximum acceptable candidiate statistics
     */
    public String getMaxCandidateStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    public String getHintModificationStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    public String getMatchStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    public String getMatchModificationStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    public String getMatchableStatistics();
}
