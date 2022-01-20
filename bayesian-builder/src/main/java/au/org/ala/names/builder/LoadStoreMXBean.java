package au.org.ala.names.builder;

/**
 * Mbean interface for load stores.
 */
public interface LoadStoreMXBean {
    /**
     * Get the number of classifiers that have been created.
     *
     * @return The created count
     */
    public int getCreated();

    /**
     * Get the number of classifiers that have been written to the store.
     *
     * @return The written count
     */
    public int getWritten();

    /**
     * Get the number of classifiers that have been added as new elements to the store.
     *
     * @return The added count
     */
    public int getAdded();

    /**
     * Get the number of classifiers that have been updated in the store.
     *
     * @return The updated count
     */
    public int getUpdated();

    /**
     * Get the number of classifiers that have been retrieved from the store.
     *
     * @return The get count
     */
    public int getGets();

    /**
     * Get the number of queries made to the store
     *
     * @return The get count
     */
    public int getQueries();

    /**
     * Is this a temporary store?
     *
     * @return True if this store is cleaned up on closing.
     */
    public boolean isTemporary();

    /**
     * Get the store location
     *
     * @return The path to the store
     */
    public String getLocation();
}
