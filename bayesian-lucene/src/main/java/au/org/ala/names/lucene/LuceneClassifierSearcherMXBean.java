package au.org.ala.names.lucene;

public interface LuceneClassifierSearcherMXBean {
    /**
     * Get the number of individual document retrievals
     *
     * @return The get count
     */
    long getGets();
    /**
     * Get the number of queries
     *
     * @return The query count
     */
    long getQueries();
}
