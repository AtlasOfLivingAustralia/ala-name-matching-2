package au.org.ala.names;

import au.org.ala.bayesian.analysis.BooleanAnalysis;
import au.org.ala.vocab.ALATerm;
import au.org.ala.vocab.VernacularStatus;
import org.gbif.dwc.record.Record;
import org.gbif.dwc.terms.GbifTerm;

import java.util.Comparator;

public class VernacularNameOrder implements Comparator<Record> {
    /** Decode the status */
    private static final VernacularStatusAnalysis STATUS_ANALYSIS = new VernacularStatusAnalysis();
    /** Decode the preferred flag */
    private static final BooleanAnalysis PREFERRED_ANALYSIS = new BooleanAnalysis();

    /**
     * Compare two vernacular name records for priority order
     *
     * @param o1 The first record
     * @param o2 The second record
     * @return
     */
    @Override
    public int compare(Record o1, Record o2) {
        if (o1 == null && o2 == null)
            return 0;
        if (o1 == null)
            return Integer.MIN_VALUE;
        if (o2 == null)
            return Integer.MAX_VALUE;
        if (!o1.rowType().equals(GbifTerm.VernacularName) || !o2.rowType().equals(GbifTerm.VernacularName))
            throw new IllegalArgumentException("Both records must be of type " + GbifTerm.VernacularName);
        VernacularStatus s1 = STATUS_ANALYSIS.fromString(o1.value(ALATerm.status), null);
        VernacularStatus s2 = STATUS_ANALYSIS.fromString(o2.value(ALATerm.status), null);
        Boolean p1 = PREFERRED_ANALYSIS.fromString(o1.value(GbifTerm.isPreferredName), null);
        Boolean p2 = PREFERRED_ANALYSIS.fromString(o2.value(GbifTerm.isPreferredName), null);
        int priority1 = s1 == null ? VernacularStatus.common.getPriority() : s1.getPriority();
        int priority2 = s2 == null ? VernacularStatus.common.getPriority() : s2.getPriority();
        if (p1 != null && p1.booleanValue())
            priority1 += VernacularStatus.PREFERRED_BOOST;
        if (p2 != null && p2.booleanValue())
            priority2 += VernacularStatus.PREFERRED_BOOST;
        return priority1 - priority2;
    }
}
