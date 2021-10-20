package au.org.ala.util;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Stack;
import java.util.function.Function;

/**
 * Generate permutations of an object using a series of modifications.
 *
 * @param <C> The type of object to permute
 */
public class BacktrackingIterator<C> implements Iterator<C>, Iterable<C> {
    /** The initial copy of the object */
    private C init;
    /** The modifications that are applied to each copy */
    private List<List<Function<C, C>>> modifications;
    /** The stack of current positions */
    private Stack<Integer> positions;
    /** The stack of intermediate elements */
    private Stack<C> elements;

    public BacktrackingIterator(C init, List<List<Function<C, C>>> modifications) {
        this.init = init;
        this.elements = new Stack<>();
        this.elements.push(this.init);
        this.modifications = modifications;
        this.positions = new Stack<>();
        this.positions.push(0);
        this.fill();
    }

    /**
     * Returns an iterator over the elements
     *
     * @return this iterator
     */
    @Override
    public Iterator<C> iterator() {
        return this;
    }

    /**
     * Returns {@code true} if the iteration has more elements.
     * (In other words, returns {@code true} if {@link #next} would
     * return an element rather than throwing an exception.)
     *
     * @return {@code true} if the iteration has more elements
     */
    @Override
    public boolean hasNext() {
        return !this.elements.isEmpty();
    }

    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration
     * @throws NoSuchElementException if the iteration has no more elements
     */
    @Override
    public C next() {
        if (this.elements.isEmpty())
            throw new NoSuchElementException();
        C next = this.elements.pop();
        // Find the next modification to make
        int p = this.elements.size() - 1;
        C seed = null;
        while (p >= 0 && seed == null) {
            int pos = this.positions.pop() + 1;
            seed = this.elements.peek();
            List<Function<C, C>> mods = this.modifications.get(p);
            if (mods != null && pos < mods.size()) {
                Function<C, C> mod = mods.get(pos);
                seed = mod == null ? seed : mod.apply(seed);
                this.elements.push(seed);
                this.positions.push(pos);
            } else {
                this.elements.pop();
                seed = null;
            }
            p--;
        }
        if (seed != null)
            this.fill();
        return next;
    }

    /**
     * Fill the stack down to the last modification
     */
    protected void fill() {
        if (this.elements.isEmpty())
            return;
        C seed = this.elements.peek();
        for (int p = this.elements.size() - 1; p < this.modifications.size(); p++) {
            Function<C, C> mod = this.modifications.get(p).get(0);
            seed = mod == null ? seed : mod.apply(seed);
            this.elements.push(seed);
            this.positions.push(0);
        }
    }
}
