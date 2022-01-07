package au.org.ala.bayesian;

import lombok.NonNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Hints contain information that may be applicable to a classification, in order to
 * improve its resolution but which may be erroneous.
 * <p>
 * Hints are usually applied by an {@link Analyser} based on information that can be
 * deduced from the data supplied but which is subject to evidence being badly structured
 * or just plain wrong.
 * Strictly added them to a template will, usually end with unexpected inferences.
 * However, they can help to disambiguate odd choices.
 * </p>
 */
public class Hints<C extends Classification<C>> implements Cloneable {
    private Map<Observable, Set> hints;

    /**
     * Construct with a new hint map.
     *
     * @param hints The hint map
     */
    public Hints(Map<Observable, Set> hints) {
        this.hints = hints;
    }

    /**
     * Construct an empty hints list
     */
    public Hints() {
        this(new HashMap<>());
    }

    /**
     * Creates a clone with a modifiable copy hint map.
     */
    @Override
    public Hints<C> clone() throws CloneNotSupportedException {
        Hints<C> clone = (Hints<C>) super.clone();
        clone.hints = this.hints.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> new HashSet(e.getValue())));
        return clone;
    }

    /**
     * Add a hint to the hints list.
     *
     * @param observable The observable
     * @param value The value to add
     *
     * @param <T> The type of observable
     */
    public <T> void addHint(Observable observable, T value) {
        this.hints.computeIfAbsent(observable, k -> new HashSet<>()).add(value);
    }

    /**
     * Get the hint values for a specific observable.
     *
     * @param observable The observable
     *
     * @param <T> The expected hint type
     *
     * @return A set of hint values, empty for no hints.
     */
    @NonNull
    public <T> Set<T> getHints(Observable observable) {
        return this.hints.computeIfAbsent(observable, k -> new HashSet<>());
    }

    /**
     * Build a list of modifications based on hints
     *
     * @param observable The observable
     * @param clazz The class of the hint
     * @param setter How to set a value dfor the hint
     * @param modifications The list of modifications to build
     *
     * @param <T> The type of value the observable has
     */
    public <T> void buildModifications(Observable observable, Class<T> clazz, BiConsumer<C, T> setter, List<List<Function<C, C>>> modifications) {
        Set<T> values = this.hints.get(observable);
        if (values == null)
            return;
        List<Function<C, C>> mods = new ArrayList<>();
        mods.add(null);
        for (T v: values) {
            mods.add(
                    cl -> {
                        C nc = cl.clone();
                        setter.accept(nc, v);
                        return nc;
                    }
            );
        }
        modifications.add(mods);
    }
}
