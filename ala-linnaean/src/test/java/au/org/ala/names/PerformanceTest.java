package au.org.ala.names;

import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.bayesian.Match;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.gbif.dwc.terms.Term;
import org.gbif.nameparser.api.Rank;
import org.gbif.utils.file.csv.CSVReader;
import org.gbif.utils.file.csv.CSVReaderFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Ensure that well-known and common species are correctly found
 */
public class PerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    public static final int SEARCH_QUERY_LIMIT = 15;
    public static final int SEARCH_CACHE_SIZE = 10000;
    public static final String INDEX = "/data/lucene/index-20210811-2" ;
    public static final String VERNACULAR_INDEX = "/data/lucene/vernacular-20210811-2" ;


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
        LuceneClassifierSearcherConfiguration sConfig = LuceneClassifierSearcherConfiguration.builder()
                .queryLimit(SEARCH_QUERY_LIMIT)
                .cacheSize(SEARCH_CACHE_SIZE)
                .build();
        ClassificationMatcherConfiguration cConfig = ClassificationMatcherConfiguration.builder()
                .enableJmx(true)
                .statistics(true)
                .build();
        this.searcher = new ALANameSearcher(index, vernacular, sConfig, cConfig);
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
        int expected = 0;
        int accurate = 0;
        int clean = 0;
        Map<Term, Integer> issueCount = new HashMap<>();
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
                boolean flag = false;
                String expectedScientificName = null;
                try {
                    if (row[0].startsWith("#")) // Skip comment
                        continue;
                    String valid = StringUtils.trimToNull(row[0]);
                    expectValid = !(valid == null || valid.equalsIgnoreCase("false"));
                    flag = valid != null && valid.equalsIgnoreCase("flag");
                    classification.scientificName = StringUtils.trimToNull(row[1]);
                    classification.scientificNameAuthorship = StringUtils.trim(row[2]);
                    classification.kingdom = StringUtils.trimToNull(row[3]);
                    classification.class_ = StringUtils.trimToNull(row[4]);
                    classification.order = StringUtils.trimToNull(row[5]);
                    classification.family = StringUtils.trimToNull(row[6]);
                    classification.genus = StringUtils.trimToNull(row[7]);
                    classification.taxonRank = this.rankAnalysis.fromString(StringUtils.trimToNull(row[8]));
                    expectedScientificName = StringUtils.trimToNull(row[9]);
                } catch (Exception ex) {
                    throw new IllegalStateException("Error on line " + line, ex);
                }
                matched++;
                try {
                    classification.inferForSearch(this.searcher.getMatcher().getAnalyser());
                    Rank expectedRank = classification.taxonRank;
                    Match<AlaLinnaeanClassification, MatchMeasurement> match = this.searcher.search(classification.clone());
                    for (Term issue : match.getIssues()) {
                        int count = issueCount.getOrDefault(issue, 0);
                        issueCount.put(issue, count + 1);
                    }
                    if (match.isValid()) {
                        succcess++;
                        String cleanName = classification.scientificName;
                        if (cleanName.equalsIgnoreCase(match.getMatch().scientificName))
                            clean++;
                        final String searchName;
                        final Collection<String> names = match.getCandidate().getNames();
                        if (expectedScientificName != null) {
                            searchName = expectedScientificName;
                        } else {
                            if (match.getIssues().contains(AlaLinnaeanFactory.HIGHER_ORDER_MATCH)) {
                                Rank matchRank = match.getAccepted().taxonRank;
                                if (matchRank == Rank.SPECIES) {
                                    searchName = classification.genus + " " + classification.specificEpithet;
                                } else if (matchRank == Rank.GENUS) {
                                    searchName = classification.genus;
                                } else if (matchRank == Rank.FAMILY) {
                                    searchName = classification.family;
                                } else if (matchRank == Rank.ORDER) {
                                    searchName = classification.order;
                                } else {
                                    searchName = classification.scientificName;
                                }
                            } else {
                                searchName = classification.scientificName;
                            }
                        }
                        if (searchName != null && names.stream().anyMatch(n -> searchName.equalsIgnoreCase(n)))
                            accurate++;
                        else
                            logger.warn("Unexpected match on line {}, got {} not {} on {}", line, match.getMatch().scientificName, searchName, row);
                    }
                    if (expectValid == match.isValid()) {
                        expected++;
                    } else {
                        logger.info("Unexpected validity line {}, values {}", line, row);
                        if (match.isValid())
                            logger.warn("Matched line {} to {}: {}", line, match.getAccepted().taxonId, match.getAccepted().scientificName);
                    }
                    if (flag) {
                        logger.info("Flag line {} as {}: {}", line, match.isValid() ? match.getMatch().scientificName : "invalid", row);
                    }
                } catch (Exception ex) {
                    logger.warn("Error on line " + line, ex);
                    errors++;
                }
            }
            endTime = System.currentTimeMillis();
            double rate = (matched * 1000.0) / (endTime - startTime);
            double successRate = (succcess * 100.0) / matched;
            double expectedRate = (expected * 100.0) / matched;
            double accurateRate = (accurate * 100.0) / matched;
            double cleanRate = (clean * 100.0) / matched;
            logger.info("Processed " + matched + " entries, " + succcess + " successful, " + expected + " expected, " + accurate + " accurate, " + clean + " clean, " + errors + " errors");
            logger.info("Processing rate " + rate + " macthes per second");
            logger.info("Successful match rate " + successRate);
            logger.info("Expected match rate " + expectedRate);
            logger.info("Accurate match rate " + accurateRate);
            logger.info("Clean match rate " + cleanRate);
            for (Map.Entry<Term, Integer> entry : issueCount.entrySet()) {
                logger.info(entry.getKey().toString() + ": " + entry.getValue());
            }
            StringWriter sw = new StringWriter();
            this.searcher.getMatcher().reportStatistics(sw);
            logger.info(sw.toString());
        } finally {
            if (s != null)
                s.close();
        }
        assertEquals(0, errors);
        assertEquals(matched, expected);
        assertEquals(succcess, accurate);
    }

    @Test
    public void testPerfomance1() throws Exception {
        this.testFile("sampled_names_2021-1.csv");
    }

    @Test
    public void testPerfomance2() throws Exception {
        this.testFile("sampled_names_2021-2.csv");
    }

}
