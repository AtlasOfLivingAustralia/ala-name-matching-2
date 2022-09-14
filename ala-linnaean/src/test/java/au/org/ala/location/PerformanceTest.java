package au.org.ala.location;

import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.bayesian.Match;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.bayesian.MatchOptions;
import au.org.ala.names.ALANameSearcher;
import au.org.ala.names.AlaLinnaeanClassification;
import au.org.ala.names.AlaLinnaeanFactory;
import au.org.ala.names.RankAnalysis;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import au.org.ala.vocab.GeographyType;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Test against a sample of locations drawn from ALA data
 */
public class PerformanceTest {
    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    public static final int SEARCH_QUERY_LIMIT = 15;
    public static final int SEARCH_CACHE_SIZE = 10000;
    public static final String LOCATION_INDEX = "/data/lucene/location-2022";


    private LuceneClassifierSearcher searcher;
    private ALALocationClassificationMatcher matcher;

    @Before
    public void setUp() throws Exception {
        File location = new File(LOCATION_INDEX);
         if (!location.exists())
            throw new IllegalStateException("Index " + location + " not present");
         LuceneClassifierSearcherConfiguration sConfig = LuceneClassifierSearcherConfiguration.builder()
                .queryLimit(SEARCH_QUERY_LIMIT)
                .cacheSize(SEARCH_CACHE_SIZE)
                .build();
        ClassificationMatcherConfiguration cConfig = ClassificationMatcherConfiguration.builder()
                .enableJmx(true)
                .statistics(true)
                .build();
        this.searcher = new LuceneClassifierSearcher(location, sConfig, AlaLocationFactory.locationId);
        this.matcher = new ALALocationClassificationMatcher(AlaLocationFactory.instance(), this.searcher, cConfig);
    }

    @After
    public void tearDown() throws Exception {
        this.matcher.close();
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
            CSVReader reader = CSVReaderFactory.build(s, StandardCharsets.UTF_8.name(), ",", '"', 0);
            String[] header = reader.next();
            int validIndex = this.indexOf(header, "valid");
            int stateProvinceIndex = this.indexOf(header, "stateProvince");
            int countryIndex = this.indexOf(header, "country");
            int countryCodeIndex = this.indexOf(header, "countryCode");
            int continentIndex = this.indexOf(header, "continent");
            int islandIndex = this.indexOf(header, "island");
            int islandGroupIndex = this.indexOf(header, "islandGroup");
            int waterBodyIndex = this.indexOf(header, "waterBody");
            int matchIndex = this.indexOf(header, "match");
            int line = 1;
            startTime = System.currentTimeMillis();
            while (reader.hasNext()) {
                String row[] = reader.next();
                line++;
                AlaLocationClassification classification = new AlaLocationClassification();
                boolean expectValid = false;
                boolean flag = false;
                String expectedLocality = null;
                try {
                    if (row[0].startsWith("#")) // Skip comment
                        continue;
                    String valid = validIndex < 0 ? "true" : StringUtils.trimToNull(row[validIndex]);
                    expectValid = !(valid == null || valid.equalsIgnoreCase("false"));
                    flag = valid != null && valid.equalsIgnoreCase("flag");
                    classification.stateProvince = stateProvinceIndex < 0 ? null : StringUtils.trimToNull(row[stateProvinceIndex]);
                    classification.country = countryIndex < 0 ? null : StringUtils.trim(row[countryIndex]);
                    classification.countryCode = countryCodeIndex < 0 ? null : StringUtils.trimToNull(row[countryCodeIndex]);
                    classification.continent = continentIndex < 0 ? null : StringUtils.trimToNull(row[continentIndex]);
                    classification.island = islandIndex < 0 ? null : StringUtils.trimToNull(row[islandIndex]);
                    classification.islandGroup = islandGroupIndex < 0 ? null : StringUtils.trimToNull(row[islandGroupIndex]);
                    classification.waterBody = waterBodyIndex < 0 ? null : StringUtils.trimToNull(row[waterBodyIndex]);
                    expectedLocality = matchIndex < 0 ? null : StringUtils.trimToNull(row[matchIndex]);
                } catch (Exception ex) {
                    throw new IllegalStateException("Error on line " + line, ex);
                }
                matched++;
                try {
                    Match<AlaLocationClassification, MatchMeasurement> match = this.matcher.findMatch(classification.clone(), MatchOptions.ALL);
                    for (Term issue : match.getIssues()) {
                        int count = issueCount.getOrDefault(issue, 0);
                        issueCount.put(issue, count + 1);
                    }
                    if (match.isValid()) {
                        succcess++;
                        classification.inferForSearch(this.matcher.getAnalyser(), MatchOptions.ALL);
                        String cleanLocality = classification.locality;
                        if (cleanLocality.equalsIgnoreCase(match.getMatch().locality))
                            clean++;
                        final String searchName;
                        final Collection<String> names = match.getAcceptedCandidate().getNames();
                        if (expectedLocality != null) {
                            searchName = expectedLocality;
                        } else {
                            if (match.getIssues().contains(AlaLocationFactory.HIGHER_LOCALITY)) {
                                GeographyType geographyType = match.getAccepted().geographyType;
                                if (geographyType == GeographyType.island) {
                                    searchName = classification.island;
                                } else if (geographyType == GeographyType.islandGroup) {
                                    searchName = classification.islandGroup;
                                } else if (geographyType == GeographyType.stateProvince) {
                                    searchName = classification.stateProvince;
                                } else if (geographyType == GeographyType.country) {
                                    searchName = classification.country;
                                } else if (geographyType == GeographyType.continent) {
                                    searchName = classification.continent;
                                } else {
                                    searchName = classification.locality;
                                }
                            } else {
                                searchName = classification.locality;
                            }
                        }
                        if (searchName != null && names.stream().anyMatch(n -> searchName.equalsIgnoreCase(n)))
                            accurate++;
                        else
                            logger.warn("Unexpected match on line {}, got {} {} not {} on {}", line, match.getAccepted().locality, match.getAccepted().locationId, searchName, row);
                    }
                    if (expectValid == match.isValid()) {
                        expected++;
                    } else {
                        logger.info("Unexpected validity line {}, values {}", line, row);
                        if (match.isValid())
                            logger.warn("Matched line {} to {}: {}", line, match.getAccepted().locationId, match.getAccepted().locality);
                    }
                    if (flag) {
                        logger.info("Flag line {} as {}: {}", line, match.isValid() ? match.getMatch().locality : "invalid", row);
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
            this.matcher.reportStatistics(sw);
            logger.info(sw.toString());
        } finally {
            if (s != null)
                s.close();
        }
        assertEquals(0, errors);
        assertEquals(matched, expected);
        assertEquals(succcess, accurate);
    }

    protected int indexOf(String[] header, String column) {
        for (int i = 0; i < header.length; i++)
            if (column.equalsIgnoreCase(header[i]))
                return i;
        return -1;
    }

    // Sample locations from 1.6 million ALA records
    @Test
    public void testPerfomance1() throws Exception {
        this.testFile("sampled-locations-1.csv");
    }

    // Sample locations from 42 million ALA records
    @Test
    public void testPerfomance2() throws Exception {
        this.testFile("sampled-locations-2.csv");
    }

}
