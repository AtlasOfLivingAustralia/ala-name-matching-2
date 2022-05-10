package au.org.ala.bayesian;

public interface ClassificationMatcherMXBean {
    /**
     * Get the number of requests
     *
     * @return The number of requests
     */
    long getRequests();

    /**
     * Get a summary of the time elapsed statistics
     *
     * @return The time statitics
     */
    String getTimeStatistics();

    /**
     * Get a summary of the search statistics.
     *
     * @return The search statistics
     */
    String getSearchStatistics();

    /**
     * Get a summary of the search modification statistics.
     *
     * @return The search modification statistics
     */
    String getSearchModificationStatistics();

    /**
     * Get a summary of the retrieval statistics.
     *
     * @return The retrieval statistics
     */
    String getCandidateStatistics();

    /**
     * Get a summary of the maximum acceptable candidiate statistics.
     *
     * @return The maximum acceptable candidiate statistics
     */
    String getMaxCandidateStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    String getHintModificationStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    String getMatchStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    String getMatchModificationStatistics();

    /**
     * Get a summary of the hint modification statistics.
     *
     * @return The hint modification statistics
     */
    String getMatchableStatistics();
}
