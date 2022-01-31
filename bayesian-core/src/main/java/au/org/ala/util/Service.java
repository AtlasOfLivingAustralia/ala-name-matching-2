package au.org.ala.util;

/**
 * Indicates a service object.
 * <p>
 * A service object has referential transparancy and can be called, thread-safely, by multiple clients.
 * The service itself may hold mutable state in the form of caches or other thread-safe objects.
 * However, from the point of view of a client object, all the service does is take some values and
 * return a result.
 * </p>
 * <p>
 * This annotation does not enfornce anything. It simply records the nature of the class.
 * </p>
 * <p>
 * Subclasses are expected to also be servces.
 * </p>
 */
public @interface Service {
}
