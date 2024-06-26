package au.org.ala.bayesian;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Trace the processing and inference steps.
 * <p>
 * Implementers of the trace interface can be used to provide detailed information on exactly how
 * something is matched.
 * </p>
 * <p>
 * Traces form a stack-like structure.
 * However, you only need to access the top-level trace object; everything else is done to the current
 * trace object.
 * </p>
 */

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"label", "formula", "derivation", "value", "children"})
public class Trace {
    /** The factory for accessing things like identifiers and names */
    @JsonIgnore
    private NetworkFactory<?, ?, ?> factory;
    /** How to access the elements of a class */
    @JsonIgnore
    private Map<Class<?>, TraceAccessor> accessors;
    /** Identity information */
    @JsonIgnore
    private Map<Object, String> identifiers;
    /** The identifier counter */
    private AtomicInteger identifierCounter;
    /** The trace level accepted */
    @JsonIgnore
    private TraceLevel level;
    /** The operation described by the trace */
    @JsonProperty
    private String label;
    /** The formula by which the value is derived */
    @JsonProperty
    private String formula;
    /** The actual derivation of the value */
    @JsonProperty
    private String derivation;
    /** The final value generated by the operation being traced */
    @JsonProperty
    private Object value;
    /** The parent trace */
    @JsonIgnore
    private Trace parent;
    /** The current trace */
    @JsonIgnore
    private Trace current;
    /** The child traces */
    @JsonProperty
    private List<Trace> children;

    /**
     * Construct a top-level trace.
     *
     * @param factory The factory to use
     * @param level The trace level to accept
     * @param label The label to use
     */
    public Trace(NetworkFactory<?, ?, ?> factory, @NonNull TraceLevel level, String label) {
        this.factory = factory;
        this.accessors = Collections.synchronizedMap(new HashMap<>());
        this.identifiers = Collections.synchronizedMap(new HashMap<>());
        this.identifierCounter = new AtomicInteger();
        this.parent = this;
        this.current = this;
        this.level = level;
        this.label = label;
    }

    /**
     * Construct a sub-trace
     *
     * @param parent The parent trace
     * @param label The trace label
     * @param value Any existing value
     */
    protected Trace(@NonNull Trace parent, @NonNull String label, Object value) {
        this.parent = parent;
        this.level = parent.level;
        this.label = label;
        this.value = value;
    }


    /**
     * Construct a sub-trace with a derivation
     *
     * @param parent The parent trace
     * @param label The trace label
     * @param formula The formula for the derivation
     * @param derivation The actual derviation
     * @param value Any existing value
     */
    protected Trace(@NonNull Trace parent, @NonNull String label, @NonNull String formula, @NonNull String derivation, Object value) {
        this.parent = parent;
        this.level = parent.level;
        this.label = label;
        this.formula = formula;
        this.derivation = derivation;
        this.value = value;
    }

    /**
     * Add a child trace.
     *
     * @param trace The child trace
     */
    protected void add(Trace trace) {
        if (this.children == null)
            this.children = new ArrayList<>();
        this.children.add(trace);
    }

    /**
     * Get the top trace element.
     *
     * @return The top element in the heirarchy of elements
     */
    protected Trace getTop() {
        Trace top = this;
        while (top != null) {
            if (top.parent == null || top.parent == top)
                return top;
            top = top.parent;
        }
        throw new IllegalStateException("No valid top element");
    }

    /**
     * Get the accessor for a class
     *
     * @param clazz The class
     *
     * @return The trace accessor
     */
    protected TraceAccessor getAccessor(Class clazz) {
        final Trace top = this.getTop();
        return top.accessors.computeIfAbsent(clazz, c -> new TraceAccessor(c, top.factory));
    }

    protected Object buildValue(Object o, boolean summary) {
        if (o == null)
            return null;
        Trace top = this.getTop();
        String identifier = null;
        TraceAccessor accessor = top.getAccessor(o.getClass());
        if (accessor.identify) {
            identifier = top.identifiers.get(o);
            if (identifier != null)
                return identifier;
            identifier = accessor.createIdentifier(o, top);
            top.identifiers.put(o, identifier);
            summary = false; // Never summarise the first instance
        }
        Object value = summary ? accessor.getSummary(o) : accessor.getDescription(o);
        return this.buildValue(identifier, value, summary);
     }

    protected Object buildValue(String identifier, Object value, boolean summary) {
        if (value instanceof Map) {
            LinkedHashMap<String, Object> mapped = new LinkedHashMap<>();
            if (identifier != null) {
                mapped.put("@id", identifier);
            }
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                mapped.put(entry.getKey(), this.buildValue(entry.getValue(), summary));
            }
            return mapped;
        } else if (value instanceof List) {
            return ((List<Object>) value).stream().map(v -> this.buildValue(v, summary)).collect(Collectors.toList());
        } else {
            if (identifier != null) {
                LinkedHashMap<String, Object> mapped = new LinkedHashMap<>();
                mapped.put("@id", identifier);
                mapped.put("value", value);
                return mapped;
            }
        }
        return value;
    }

    /**
     * Add a leaf element to the trace
     *
     * @param level The level this trace is at
     * @param label The leaf label
     * @param value The leaf value
     */
    public void add(TraceLevel level, String label, Object value) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        value = this.buildValue(value, false);
        Trace trace = new Trace(this.current, label, value);
        this.current.add(trace);
    }

    /**
     * Add a summary leaf element to the trace
     *
     * @param level The level this trace is at
     * @param label The leaf label
     * @param value The leaf value
     */
    public void addSummary(TraceLevel level, String label, Object value) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        value = this.buildValue(value, true);
        Trace trace = new Trace(this.current, label, value);
        this.current.add(trace);
    }


    /**
     * Add a leaf element to the trace
     *
     * @param level The level this trace is at
     * @param label The leaf label
     * @param value The leaf value
     */
    public void add(TraceLevel level, String label, String formula, String derivation, Object value) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        value = this.buildValue(value, false);
        Trace trace = new Trace(this.current, label, formula, derivation, value);
        this.current.add(trace);
    }

    /**
     * Push a sub-trace and make it the current trace
     *
     * @param level The level this trace is at
     * @param label The label for the trace operation
     */
    public void push(TraceLevel level, String label) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        Trace trace = new Trace(this.current, label, null);
        this.current.add(trace);
        this.current = trace;
    }

    /**
     * Pop out of a sub-trace and back to the parent trace.
     *
     * @param level The level this trace is at
     */
    public void pop(TraceLevel level) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        this.current = this.current.parent;
    }

    /**
     * Set the resulting value of the trace
     *
     * @param level The level this trace is at
     * @param value The value to set
     */
    public void value(TraceLevel level, Object value) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        this.current.value = this.buildValue(value, false);
    }


    /**
     * Set the resulting value of the trace
     *
     * @param level The level this trace is at
     * @param formula The formula that built the trace
     * @param derivation The value derivation
     * @param value The value to set
     */
    public void value(TraceLevel level, String formula, String derivation, Object value) {
        if (this.current.level.ordinal() < level.ordinal())
            return;
        this.current.formula = formula;
        this.current.derivation = derivation;
        this.current.value = this.buildValue(value, false);
    }

    protected static class TraceAccessor {
        private Class clazz;
        private NetworkFactory<?, ?, ?> factory;
        private boolean identify;
        private Function<Object, String> identifier;
        private Function<Object, Object> description;
        private Function<Object, Object> summary;

        public TraceAccessor(Class clazz, NetworkFactory<?, ?, ?> factory) {
            this.clazz = clazz;
            this.factory = factory;
            this.identifier = null;
            if (clazz.isAnnotationPresent(TraceDescriptor.class)) {
                TraceDescriptor descriptor = (TraceDescriptor) clazz.getAnnotation(TraceDescriptor.class);
                this.identify = descriptor.identify();
                if (this.identify && !descriptor.identifier().isEmpty()) {
                    this.identifier = this.findMethod(descriptor.identifier(), String.class);
                }
                if (!descriptor.description().isEmpty()) {
                    this.description = this.findMethod(descriptor.description(), Object.class);
                } else {
                    this.description = o -> o;
                }
                if (!descriptor.summary().isEmpty())  {
                    this.summary = this.findMethod(descriptor.summary(), Object.class);
                } else {
                    this.summary = this.description;
                }
            } else {
                this.identify = false;
                this.description = o -> o;
                this.summary = o -> o;
            }
        }

        protected <T> Function<Object, T> findMethod(String name, Class<T> returnClass) {
            try {
                final Method method = this.clazz.getMethod(name, NetworkFactory.class);
                return o -> (T) this.call(method, o, this.factory);
            } catch (NoSuchMethodException ex) {
                try {
                    final Method method = this.clazz.getMethod(name);
                    return o -> (T) this.call(method, o);
                } catch (NoSuchMethodException ex1) {
                    throw new IllegalStateException("Unable to get " + name + " method for " + this.clazz, ex1);
                }
            }
        }

        @SneakyThrows
        protected Object call(Method method, Object target, Object... args) {
            return method.invoke(target, args);
        }

        public String createIdentifier(@NonNull Object o, Trace trace) {
            if (this.identifier != null)
                return this.identifier.apply(o);
            return o.getClass().getSimpleName() + ":" + trace.identifierCounter.getAndIncrement();
        }

        public Object getDescription(Object o) {
            return this.description.apply(o);
        }

        public Object getSummary(Object o) {
            return this.summary.apply(o);
        }
    }

    public static enum TraceLevel {
        /** No tracing */
        NONE,
        /** Summary level tracing */
        SUMMARY,
        /** Information level tracing */
        INFO,
        /** Debug level tracing */
        DEBUG,
        /** Detailed tracing, including inference values */
        TRACE;
    }
}
