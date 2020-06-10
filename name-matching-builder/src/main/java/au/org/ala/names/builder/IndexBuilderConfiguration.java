package au.org.ala.names.builder;

import au.org.ala.names.lucene.LuceneLoadStore;
import au.org.ala.util.TermDeserializer;
import au.org.ala.util.TermSerializer;
import au.org.ala.vocab.ALATerm;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class IndexBuilderConfiguration {
    /** The working area directory to use, null for the general temparay directory */
    @JsonProperty
    private File work;
    /** The network description file */
    @JsonProperty("network")
    private URL network;
    /** The class of the builder */
    @JsonProperty
    private Class<? extends Builder> builderClass;
    /** The class of the load store */
    @JsonProperty
    private Class<? extends LoadStore> loadStoreClass;
    /** The default weight to use for an unweighted taxon */
    @JsonProperty
    private double defaultWeight;
    /** The log interval */
    @JsonProperty
    private int logInterval;

    public IndexBuilderConfiguration() {
        this.builderClass = EmptyBuilder.class;
        this.loadStoreClass = LuceneLoadStore.class;
        this.defaultWeight = 1.0;
        this.logInterval = 100;
    }

    /**
     * Get the working directory.
     *
     * @return The directory used to create temporary work files and directories.
     */
    public File getWork() {
        return this.work;
    }

    /**
     * Set the working directory.
     *
     * @param work The new working directory for temporary storage
     */
    public void setWork(File work) {
        this.work = work;
    }

    /**
     * Get the network URL
     *
     * @return The URL of the source network
     */
    public URL getNetwork() {
        return this.network;
    }

    /**
     * Set the network URL
     * <p>
     * The network will be re-read after this is set.
     * </p>
     *
     * @param network The URL of the source network
     */
    public void setNetwork(URL network) {
        this.network = network;
    }

    /**
     * Get the class of the load-store implementation.
     *
     * @return The load-store class.
     *
     * @see LoadStore
     */
    public Class<? extends LoadStore> getLoadStoreClass() {
        return loadStoreClass;
    }

    /**
     * Set the class of the load-store.
     *
     * @param loadStoreClass The new load-store class.
     */
    public void setLoadStoreClass(Class<? extends LoadStore> loadStoreClass) {
        this.loadStoreClass = loadStoreClass;
    }

    /**
     * Get the builder class.
     *
     * @return The class which calculates builder information for this network.
     */
    public Class<? extends Builder> getBuilderClass() {
        return builderClass;
    }

    /**
     * Set the builder class.
     *
     * @param builderClass The new builder class
     */
    public void setBuilderClass(Class<? extends Builder> builderClass) {
        this.builderClass = builderClass;
    }

    /**
     * Get the default weight to assign to an unweighted entry.
     *
     * @return The default weight
     */
    public double getDefaultWeight() {
        return defaultWeight;
    }

    /**
     * Set the default weight.
     *
     * @param defaultWeight The new default weight.
     */
    public void setDefaultWeight(double defaultWeight) {
        this.defaultWeight = defaultWeight;
    }

    /**
     * Constuct a load-store.
     * <p>
     * The load store must have either: A constuctor for an annotator and index builder condfiguration,
     * a constructor for an annotator and file (working directory) or an annotator only constuctor.
     * These are tried in turn.
     * </p>
     *
     * @param annotator The annotator to use
     *
     * @return A newly created load-store.
     *
     * @throws StoreException if unable to build the store
     */
    public LoadStore createLoadStore(Annotator annotator) throws StoreException {
        Constructor<? extends LoadStore> c;

        if (this.loadStoreClass == null)
            throw new StoreException("Load store class not defined");
        try {
            c = this.loadStoreClass.getConstructor(Annotator.class, IndexBuilderConfiguration.class, boolean.class);
            return c.newInstance(annotator, this, true);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        try {
            c = this.loadStoreClass.getConstructor(Annotator.class, File.class, boolean.class);
            return c.newInstance(annotator, this.getWork(), true);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        try {
            c = this.loadStoreClass.getConstructor(Annotator.class);
            return c.newInstance(annotator);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        throw new StoreException("Unable to construct load store for " + this.loadStoreClass);
    }

    /**
     * Construct a builder.
     * <p>
     * The load store must have either: A constuctor for a index builder condfiguration,
     * or an empty constuctor.
     * These are tried in turn.
     * </p>
     *
     * @param annotator The annotator to use
     *
     * @return A newly created builder.
     *
     * @throws StoreException if unable to build the store
     */
    public Builder createBuilder(Annotator annotator) throws StoreException {
        Constructor<? extends Builder> c;

        if (this.builderClass == null)
            throw new StoreException("Builder class not defined");
        try {
            c = this.builderClass.getConstructor(IndexBuilderConfiguration.class);
            return c.newInstance(this);
        } catch (Exception ex) {
        }
        try {
            c = this.builderClass.getConstructor();
            return c.newInstance();
        } catch (Exception ex) {
        }
        throw new StoreException("Unable to construct builder for " + this.builderClass);
    }

    /**
     * Get the number of elements to skip before issuing a log update.
     * <p>
     * By default 100. Increasing or decreasing this value reduces or
     * increases the log verbosity.
     * </p>
     *
     * @return The log interval
     */
    public int getLogInterval() {
        return this.logInterval;
    }

    /**
     * Set the log interval.
     *
     * @param logInterval The new log interval
     */
    public void setLogInterval(int logInterval) {
        this.logInterval = logInterval;
    }

    /**
     * Write the configuration as JSON.
     *
     * @param writer The writer to write to
     *
     * @throws IOException if unable to write
     */
    public void write(Writer writer) throws IOException {
        createMapper().writeValue(writer, this);
    }
    /**
     * Read a index builder config from a URL.
     *
     * @param source The source URL
     *
     * @return The resulting configuration
     *
     * @throws IOException If unable to read the configuration
     */
    public static IndexBuilderConfiguration read(URL source) throws IOException {
        return createMapper().readValue(source, IndexBuilderConfiguration.class);
    }

    /**
     * Create a standard object mapper for reading configurations.
     *
     * @return The standard object mapper
     */
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Term.class, new TermDeserializer());
        module.addSerializer(Term.class, new TermSerializer());
        mapper.registerModule(module);
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return mapper;
    }

}

