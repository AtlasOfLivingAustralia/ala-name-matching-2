package au.org.ala.names;

import au.org.ala.bayesian.Match;
import org.apache.commons.lang3.StringUtils;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

/**
 * Ensure that well-known and common species are correctly found
 */
public class PerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    public static final String INDEX = "/data/lucene/index-20210811";
    public static final String VERNACULAR_INDEX = "/data/lucene/vernacular-20210811";


    private ALANameSearcher searcher;
    private RankAnalysis rankAnalysis = new RankAnalysis();

    @Before
    public void setUp() throws Exception {
        File index = new File(INDEX);
        File vernacular = new File(VERNACULAR_INDEX);
        if (!index.exists())
            throw new IllegalStateException("Index " + index + " not present");
        if (!vernacular.exists())
            throw new IllegalStateException("Vernacular Index " + vernacular + " not present");
        this.searcher = new ALANameSearcher(index, vernacular);
    }

    @After
    public void tearDown() throws Exception {
        this.searcher.close();
    }

    private void testFile(String name) throws Exception {
        InputStream s = null;
        int errors = 0;
        int matched = 0;
        int succcess = 0;
        double rate, successRate;
        long startTime, endTime;
        try {
            s = this.getClass().getResourceAsStream(name);
            CSVReader reader = CSVReaderFactory.build(s, StandardCharsets.UTF_8.name(), ",", '"', 1);
            int line = 1;
            startTime = System.currentTimeMillis();
            while (reader.hasNext()) {
                String row[] = reader.next();
                line++;
                AlaLinnaeanClassification classification = new AlaLinnaeanClassification();
                boolean expectValid = false;
                try {
                    if (row[0].startsWith("#")) // Skip comment
                        continue;
                    String valid = StringUtils.trimToNull(row[0]);
                    expectValid = valid == null ? false : Boolean.parseBoolean(valid);
                    classification.scientificName = StringUtils.trimToNull(row[1]);
                    classification.scientificNameAuthorship = StringUtils.trim(row[2]);
                    classification.kingdom = StringUtils.trimToNull(row[3]);
                    classification.class_ = StringUtils.trimToNull(row[4]);
                    classification.order = StringUtils.trimToNull(row[5]);
                    classification.family = StringUtils.trimToNull(row[6]);
                    classification.genus = StringUtils.trimToNull(row[7]);
                    classification.taxonRank = this.rankAnalysis.fromString(StringUtils.trimToNull(row[8]));
                } catch (Exception ex) {
                    throw new IllegalStateException("Error on line " + line, ex);
                }
                matched++;
                try {
                    Match<AlaLinnaeanClassification> match = this.searcher.search(classification);
                    if (match.isValid()) {
                        succcess++;
                    }
                    if (expectValid != match.isValid()){
                        logger.info("Unexpected validity line {}, values {}" , line, row);
                    }
                } catch (Exception ex) {
                    logger.warn("Error on line " + line, ex);
                    errors++;
                }
            }
            endTime = System.currentTimeMillis();
            rate = (matched * 1000.0) / (endTime - startTime);
            successRate = (succcess * 100.0) / matched;
            logger.info("Processed " + matched + " entries, " + succcess + " successful, " + errors + " errors");
            logger.info("Processing rate " + rate + " macthes per second");
            logger.info("Successful match rate " + successRate);
        } finally {
            if (s != null)
                s.close();
        }
        assertEquals(0, errors);
    }

    @Test
    public void testPerfomance1() throws Exception {
        this.testFile("sampled_names_2021.csv");
    }
}
