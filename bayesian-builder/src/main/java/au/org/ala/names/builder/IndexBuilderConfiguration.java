package au.org.ala.names.builder;

import au.org.ala.bayesian.*;
import au.org.ala.names.lucene.LuceneLoadStore;
import au.org.ala.util.TermDeserializer;
import au.org.ala.util.TermSerializer;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import lombok.Setter;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class IndexBuilderConfiguration {
    /** The working area directory to use, null for the general temparay directory */
    @JsonProperty
    @Getter
    @Setter
    private File work;
    /** The network description file */
    @JsonProperty("network")
    @Getter
    @Setter
    private URL network;
    /** The class of the network factory */
    @JsonProperty
    @Getter
    @Setter
    private Class<? extends NetworkFactory<?, ?, ?, ?>> factoryClass;
    /** The class of the builder */
    @JsonProperty
    @Getter
    @Setter
    private Class<? extends Builder<?>> builderClass;
    /** The class of the load store */
    @JsonProperty
    @Getter
    @Setter
    private Class<? extends LoadStore<?>> loadStoreClass;
    /** The default weight to use for an unweighted taxon */
    @JsonProperty
    @Getter
    @Setter
    private double defaultWeight;
    /** The log interval */
    @JsonProperty
    @Getter
    @Setter
    private int logInterval;
    /** The types of records to load and index */
    @JsonProperty
    @Getter
    @Setter
    private List<Term> types;

    public IndexBuilderConfiguration() {
        this.builderClass = EmptyBuilder.class;
        this.loadStoreClass = LuceneLoadStore.class;
        this.defaultWeight = 1.0;
        this.logInterval = 10000;
        this.types = Arrays.asList(DwcTerm.Taxon);
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
    public <Cl extends Classifier> LoadStore<Cl> createLoadStore(Annotator annotator) throws StoreException {
        Constructor<? extends LoadStore<Cl>> c;

        if (this.loadStoreClass == null)
            throw new StoreException("Load store class not defined");
        try {
            c = (Constructor<? extends LoadStore<Cl>>) this.loadStoreClass.getConstructor(Annotator.class, IndexBuilderConfiguration.class, boolean.class);
            return c.newInstance(annotator, this, true);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        try {
            c = (Constructor<? extends LoadStore<Cl>>) this.loadStoreClass.getConstructor(Annotator.class, File.class, boolean.class);
            return c.newInstance(annotator, this.getWork(), true);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        try {
            c = (Constructor<? extends LoadStore<Cl>>) this.loadStoreClass.getConstructor(Annotator.class);
            return c.newInstance(annotator);
        } catch (InvocationTargetException ex) {
            throw new StoreException("Unable to construct load store for " + this.loadStoreClass, ex.getCause());
        } catch (Exception ex) {
        }
        throw new StoreException("Unable to construct load store for " + this.loadStoreClass);
    }

    /**
     * Construct a factory.
     * <p>
     * The factory must have either:
     * A static instance() method for the factory,
     * A constructor for an index builder configuration,
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
    public <C extends Classification, P extends Parameters, I extends Inferencer<C, P>, F extends NetworkFactory<C, P, I, F>> F createFactory(Annotator annotator) throws StoreException {
        Constructor<F> c;

        if (this.factoryClass == null)
            throw new StoreException("Factory class not defined");
        try {
            Method m = this.factoryClass.getMethod("instance");
            if (m != null && Modifier.isStatic(m.getModifiers()))
                return (F) m.invoke(null);
        } catch (Exception ex) {
        }
        try {
            c = (Constructor<F>) this.factoryClass.getConstructor(IndexBuilderConfiguration.class);
            return c.newInstance(this);
        } catch (Exception ex) {
        }
        try {
            c = (Constructor<F>) this.factoryClass.getConstructor();
            return c.newInstance();
        } catch (Exception ex) {
        }
        throw new StoreException("Unable to construct builder for " + this.builderClass);
    }

    /**
     * Construct a builder.
     * <p>
     * The builder must have either: A constructor for an index builder configuration and a factory,
     * a constructor for a index builder configuration,
     * a constructor for a factory,
     * or an empty constuctor.
     * These are tried in turn.
     * </p>
     *
     * @param annotator The annotator to use
     * @param factory The factory to use
     *
     * @return A newly created builder.
     *
     * @throws StoreException if unable to build the store
     */
    public <P extends Parameters, F extends NetworkFactory<?, P, ?, F>> Builder<P> createBuilder(Annotator annotator, F factory) throws StoreException {
        Constructor<? extends Builder> c;

        if (this.builderClass == null)
            throw new StoreException("Builder class not defined");
        try {
            c = this.builderClass.getConstructor(IndexBuilderConfiguration.class, NetworkFactory.class);
            return c.newInstance(this, factory);
        } catch (Exception ex) {
        }
        try {
            c = this.builderClass.getConstructor(IndexBuilderConfiguration.class);
            return c.newInstance(this);
        } catch (Exception ex) {
        }
        try {
            c = this.builderClass.getConstructor(NetworkFactory.class);
            return c.newInstance(factory);
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

