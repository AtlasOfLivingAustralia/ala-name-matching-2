package au.org.ala.bayesian;

import java.util.concurrent.atomic.AtomicInteger;

public class TemporaryVariable extends Variable {
    private static final AtomicInteger ID_STREAM = new AtomicInteger();

    public TemporaryVariable() {
        super("t$" + ID_STREAM.incrementAndGet());
    }
}
