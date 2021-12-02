package au.org.ala.names;

import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.VernacularStatus;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.DwcTerm;

import java.util.function.Predicate;

public class VernacularNameFilter implements Predicate<Record> {
    /** Decode the status */
    private static final VernacularStatusAnalysis STATUS_ANALYSIS = new VernacularStatusAnalysis();

    /**
     * Accept anything that is not specifically excluded.
     *
     * @param record the input argument
     * @return {@code true} if the input argument matches the predicate,
     * otherwise {@code false}
     */
    @Override
    public boolean test(Record record) {
        if (record == null)
            return false;
        VernacularStatus status = STATUS_ANALYSIS.fromString(record.value(ALATerm.status));
        return status == null || !status.isExclude();
    }
}
