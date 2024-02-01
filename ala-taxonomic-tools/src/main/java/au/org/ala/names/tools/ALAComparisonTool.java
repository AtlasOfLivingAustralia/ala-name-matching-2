package au.org.ala.names.tools;

import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.*;
import au.org.ala.location.AlaLocationClassification;
import au.org.ala.location.AlaLocationFactory;
import au.org.ala.names.*;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import au.org.ala.util.Counter;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import lombok.Getter;
import old.au.org.ala.names.model.ErrorType;
import old.au.org.ala.names.model.LinnaeanRankClassification;
import old.au.org.ala.names.model.MetricsResultDTO;
import org.gbif.dwc.terms.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.trimToNull;

/**
 * Compare old vs new name matching.
 * <p>
 * This tool takes a csv file containing supplied data and prcesseds to do both a match
 * to the old namematching index and a match to the new matching index, printing out the results side-by-side
 * </p>
 */
public class ALAComparisonTool {
    private static final Logger logger = LoggerFactory.getLogger(ALAComparisonTool.class);

    @Parameter(names = "--help", help = true)
    @Getter
    private boolean help = false;
    @Parameter(names = "-i", description = "The linnaean index path", required = true)
    private File linnaeanIndex;
    @Parameter(names = "-v", description = "The vernacular index path", required = true)
    private File vernacularIndex;
    @Parameter(names = "-l", description = "The location index path", required = true)
    private File locationIndex;
    @Parameter(names = "-n", description = "The old-version index path", required = true)
    private File oldIndex;
    @Parameter(names = "-o", description = "The output file, if - or absent then standard output")
    private String output = "-";
    @Parameter(names = "-m", description = "Measure matching performance")
    private boolean measure = false;
    @Parameter(names = "-s", description = "Collect match statistics")
    private boolean statistics = false;
    @Parameter(description = "The input CSVs, with - meaning standard input")
    private String input = "-";

    private CSVReader source;
    private CSVWriter report;
    protected List<BiConsumer<String[], AlaLocationClassification>> locationInputs;
    protected List<BiConsumer<String[], AlaLinnaeanClassification>> classificationInputs;
    protected List<BiConsumer<String[], AlaVernacularClassification>> vernacularInputs;
    protected List<BiConsumer<String[], LinnaeanRankClassification>> oldInputs;
    protected List<Function<Result, Object>> outputs;
    protected List<String> outputHeaders;
    protected ALANameSearcher newSearcher;
    protected old.au.org.ala.names.search.ALANameSearcher oldSearcher;

    private static final List<Column<Object>> SUMMARY_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("comparison", null, r -> r.comparison())
    );

    private static final List<Column<AlaLocationClassification>> LOCATION_INPUT_COLUMNS = Arrays.asList(
            new Column<>("continent", (n, c) -> c.continent = trimToNull(n), null),
            new Column<>("waterBody", (n, c) -> c.waterBody = trimToNull(n), null),
            new Column<>("islandGroup", (n, c) -> c.islandGroup = trimToNull(n), null),
            new Column<>("island", (n, c) -> c.island = trimToNull(n), null),
            new Column<>("country", (n, c) -> c.country = trimToNull(n), null),
            new Column<>("countryCode", (n, c) -> c.countryCode = trimToNull(n), null),
            new Column<>("stateProvince", (n, c) -> c.stateProvince = trimToNull(n), null)
    );
    private static final List<Column<AlaLocationClassification>> LOCATION_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("location_matched_locationId", null, r -> r.location.isValid() ? r.location.getMatch().locationId : null),
            new Column<>("location_accepted_locationId", null, r -> r.location.isValid() ? r.location.getAccepted().locationId : null),
            new Column<>("location_matched_locality", null, r -> r.location.isValid() ? r.location.getMatch().locality : null),
            new Column<>("location_accepted_locality", null, r -> r.location.isValid() ? r.location.getAccepted().locality : null),
            new Column<>("location_probability", null, r -> r.location.isValid() ? r.location.getProbability().getPosterior() : null),
            new Column<>("location_fidelity", null, r -> r.location.isValid() ? r.location.getFidelity().getFidelity() : null),
            new Column<>("location_time", null, r -> r.locationTime)
    );

    private static final List<Column<AlaLinnaeanClassification>> LINNAEAN_INPUT_COLUMNS = Arrays.asList(
            new Column<>("scientificName", (n, c) -> c.scientificName = trimToNull(n), null),
            new Column<>("scientificNameAuthorship", (n, c) -> c.scientificNameAuthorship = trimToNull(n), null),
            new Column<>("taxonRank", (n, c) -> c.taxonRank = fromString(AlaLinnaeanFactory.taxonRank, n), null),
            new Column<>("kingdom", (n, c) -> c.kingdom = fromString(AlaLinnaeanFactory.kingdom, n), null),
            new Column<>("phylum", (n, c) -> c.phylum = fromString(AlaLinnaeanFactory.phylum, n), null),
            new Column<>("class", (n, c) -> c.class_ = fromString(AlaLinnaeanFactory.class_, n), null),
            new Column<>("order", (n, c) -> c.order = fromString(AlaLinnaeanFactory.order, n), null),
            new Column<>("family", (n, c) -> c.family = fromString(AlaLinnaeanFactory.family, n), null),
            new Column<>("genus", (n, c) -> c.genus = fromString(AlaLinnaeanFactory.genus, n), null)
   );

    private static final List<Column<AlaLinnaeanClassification>> LINNAEAN_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("linnaean_matched_taxonId", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().taxonId : null),
            new Column<>("linnaean_accepted_taxonId", null, r -> r.linnaean.isValid() ? r.linnaean.getAccepted().taxonId : null),
            new Column<>("linnaean_matched_scientificName", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().scientificName : null),
            new Column<>("linnaean_accepted_scientificName", null, r -> r.linnaean.isValid() ? r.linnaean.getAccepted().scientificName : null),
            new Column<>("linnaean_matched_scientificNameAuthorship", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().scientificNameAuthorship : null),
            new Column<>("linnaean_accepted_scientificNameAuthorship", null, r -> r.linnaean.isValid() ? r.linnaean.getAccepted().scientificNameAuthorship : null),
            new Column<>("linnaean_matched_taxonRank", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().taxonRank : null),
            new Column<>("linnaean_accepted_taxonRank", null, r -> r.linnaean.isValid() ? r.linnaean.getAccepted().taxonRank : null),
            new Column<>("linnaean_matched_kingdom", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().kingdom : null),
            new Column<>("linnaean_matched-phylum", null, r -> r.linnaean.isValid() ?  r.linnaean.getMatch().phylum : null),
            new Column<>("linnaean_matched_class", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().class_ : null),
            new Column<>("linnaean_matched_order", null, r -> r.linnaean.isValid() ?  r.linnaean.getMatch().order : null),
            new Column<>("linnaean_matched_family", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().family : null),
            new Column<>("linnaean_matched_genus", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().genus : null),
            new Column<>("linnaean_matched_nomenclaturalCode", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().nomenclaturalCode : null),
            new Column<>("linnaean_matched_taxonomicStatus", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().taxonomicStatus : null),
            new Column<>("linnaean_matched_nomenclaturalStatus", null, r -> r.linnaean.isValid() ? r.linnaean.getMatch().nomenclaturalStatus : null),
            new Column<>("linnaean_probability", null, r -> r.linnaean.isValid() ? r.linnaean.getProbability().getPosterior() : null),
            new Column<>("linnaean_fidelity", null, r -> r.linnaean.isValid() ? r.linnaean.getFidelity().getFidelity() : null),
            new Column<>("linnaean_time", null, r -> r.linnaeanTime)
     );

    private static final List<Column<AlaLinnaeanClassification>> LINNAEAN_NO_LOCATION_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("linnaeanNoLocation_matched_taxonId", null, r -> r.linnaeanNoLocation.isValid() ? r.linnaeanNoLocation.getMatch().taxonId : null),
            new Column<>("linnaeanNoLocation_accepted_taxonId", null, r -> r.linnaeanNoLocation.isValid() ? r.linnaeanNoLocation.getAccepted().taxonId : null),
            new Column<>("linnaeanMoLocation_matched_scientificName", null, r -> r.linnaeanNoLocation.isValid() ? r.linnaeanNoLocation.getMatch().scientificName : null),
            new Column<>("linnaeanNoLocation_accepted_scientificName", null, r -> r.linnaeanNoLocation.isValid() ? r.linnaeanNoLocation.getAccepted().scientificName : null),
            new Column<>("linnaeanNoLocation_matched_taxonomicStatus", null, r -> r.linnaeanNoLocation.isValid() ? r.linnaeanNoLocation.getMatch().taxonomicStatus : null),
            new Column<>("linnaeanNoLocation_matched_locations", null, r -> r.linnaeanNoLocation.isValid() && r.linnaeanNoLocation.getMatch().locationId != null ? r.linnaeanNoLocation.getAccepted().locationId.stream().collect(Collectors.joining(", ")) : null),
            new Column<>("linnaeanNoLocation_matched_issues", null, r ->  r.linnaeanNoLocation.getIssues().stream().map(i -> i.simpleName()).collect(Collectors.joining(", "))),
            new Column<>("linnaeanNoLocation_time", null, r -> r.linnaeanTime)
    );

    private static final List<Column<AlaVernacularClassification>> VERNACULAR_INPUT_COLUMNS = Arrays.asList(
            new Column<>("vernacularName", (n, c) -> c.vernacularName= trimToNull(n), null),
            new Column<>("language", (n, c) -> c.language = trimToNull(n), null)
    );

    private static final List<Column<AlaVernacularClassification>> VERNACULAR_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("vernacular_matched_taxonId", null, r -> r.vernacular.isValid() ? r.vernacular.getMatch().taxonId : null),
            new Column<>("vernacular_accepted_taxonId", null, r -> r.vernacular.isValid() ? r.vernacular.getAccepted().taxonId : null),
            new Column<>("vernacular_matched_vernacularName", null, r -> r.vernacular.isValid() ? r.vernacular.getMatch().vernacularName : null),
            new Column<>("vernacular_accepted_vernacularName", null, r -> r.vernacular.isValid() ? r.vernacular.getAccepted().vernacularName : null),
            new Column<>("vernacular_matched_scientificName", null, r -> r.vernacular.isValid() ? r.vernacular.getMatch().scientificName : null),
            new Column<>("vernacular_accepted_scientificName", null, r -> r.vernacular.isValid() ? r.vernacular.getAccepted().scientificName : null),
            new Column<>("vernacular_accepted_vernacularStatus", null, r -> r.vernacular.isValid() ? r.vernacular.getAccepted().vernacularStatus : null),
            new Column<>("vernacular_probability", null, r -> r.vernacular.isValid() ? r.vernacular.getProbability().getPosterior() : null),
            new Column<>("vernacular_fidelity", null, r -> r.vernacular.isValid() ? r.vernacular.getFidelity().getFidelity() : null),
            new Column<>("vernacular_time", null, r -> r.vernacularTime)
    );

    private static final List<Column<LinnaeanRankClassification>> OLD_INPUT_COLUMNS = Arrays.asList(
            new Column<>("scientificName", (n, c) -> c.setScientificName(trimToNull(n)), null),
            new Column<>("scientificNameAuthorship", (n, c) -> c.setAuthorship(trimToNull(n)), null),
            new Column<>("taxonRank", (n, c) -> c.setRank(trimToNull(n)), null),
            new Column<>("kingdom", (n, c) -> c.setKingdom(trimToNull(n)), null),
            new Column<>("phylum", (n, c) -> c.setPhylum(trimToNull(n)), null),
            new Column<>("class", (n, c) -> c.setKlass(trimToNull(n)), null),
            new Column<>("order", (n, c) -> c.setOrder(trimToNull(n)), null),
            new Column<>("family", (n, c) -> c.setFamily(trimToNull(n)), null),
            new Column<>("genus", (n, c) -> c.setGenus(trimToNull(n)), null)
    );

    private static final List<Column<LinnaeanRankClassification>> OLD_OUTPUT_COLUMNS = Arrays.asList(
            new Column<>("old_matched_taxonId", null, r -> r.original.getResult() != null ? r.original.getResult().getId() : null),
            new Column<>("old_accepted_taxonId", null, r -> r.original.getResult() != null ? r.original.getResult().getAcceptedLsid(): null),
            new Column<>("old_matched_scientificName", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getScientificName(): null),
            new Column<>("old_matched_scientificNameAuthorship", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getAuthorship(): null),
            new Column<>("old_matched_taxonRank", null,  r -> r.original.getResult() != null ? r.original.getResult().getRank(): null),
            new Column<>("old_matched_kingdom", null,  r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getKingdom(): null),
            new Column<>("old_matched_phylum", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getPhylum(): null),
            new Column<>("old_matched_class", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getKlass(): null),
            new Column<>("old_matched_order", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getOrder(): null),
            new Column<>("old_matched_family", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getFamily(): null),
            new Column<>("old_matched_genus", null, r -> r.original.getResult() != null ? r.original.getResult().getRankClassification().getGenus(): null),
            new Column<>("old_matched_synonymType", null, r -> r.original.getResult() != null ? r.original.getResult().getSynonymType(): null),
            new Column<>("old_matched_matchType", null, r -> r.original.getResult() != null ? r.original.getResult().getMatchType() : null),
            new Column<>("old_matched_nameType", null, r -> r.original.getNameType()),
            new Column<>("old_time", null, r -> r.originalTime)
    );


    public ALAComparisonTool() {
    }

    public void run() throws Exception {
        Reader r = this.input.equals("-") ? new InputStreamReader(System.in) : new FileReader(this.input);
        this.source = new CSVReader(r);
        Writer w = this.output.equals("-") ? new OutputStreamWriter(System.out) : new FileWriter(this.output);
        this.report = new CSVWriter(w);
        Counter lines = new Counter("Processed {0} rows, {2,number,0.0}/s", logger, 10000, -1);
        this.createSearchers();
        this.parseSource();
        lines.start();
        this.writeHeader();
        for (String[] row: this.source) {
            this.processRow(row, lines.getCount());
            lines.increment(null);
        }
        lines.stop();
        this.closeSearchers();
    }

    protected void processRow(String[] row, int index) {
        Result result = new Result();
        result.input = row;
        if (!this.locationInputs.isEmpty()) {
            AlaLocationClassification classification = AlaLocationFactory.instance().createClassification();
            for (BiConsumer<String[], AlaLocationClassification> c: this.locationInputs) {
                c.accept(row, classification);
            }
            result.location = Match.emptyMatch();
            if (classification.locality != null || classification.stateProvince != null || classification.countryCode != null || classification.country != null || classification.continent != null || classification.island != null || classification.islandGroup != null || classification.waterBody != null) {
                try {
                    long begin = System.nanoTime();
                    result.location = this.newSearcher.search(classification);
                    result.locationTime = (int) (System.nanoTime() - begin);
                } catch (BayesianException ex) {
                    logger.error("Unable to process row " + index, ex);
                }
            }
        }
        if (!this.classificationInputs.isEmpty()) {
            // First without location information
            AlaLinnaeanClassification classification = AlaLinnaeanFactory.instance().createClassification();
            for (BiConsumer<String[], AlaLinnaeanClassification> c: this.classificationInputs) {
                c.accept(row, classification);
            }
            result.linnaeanNoLocation = Match.emptyMatch();
            if (classification.scientificName != null || classification.genus != null || classification.family != null || classification.order != null || classification.class_ != null || classification.phylum != null || classification.kingdom != null) {
                try {
                    long begin = System.nanoTime();
                    result.linnaeanNoLocation = this.newSearcher.search(classification);
                    result.linnaeanNoLocationTime = (int) (System.nanoTime() - begin);
                } catch (BayesianException ex) {
                    logger.error("Unable to process row " + index, ex);
                }
            }
            // Then with location information
            classification = AlaLinnaeanFactory.instance().createClassification();
            for (BiConsumer<String[], AlaLinnaeanClassification> c: this.classificationInputs) {
                c.accept(row, classification);
            }
            result.linnaean = Match.emptyMatch();
            if (classification.scientificName != null || classification.genus != null || classification.family != null || classification.order != null || classification.class_ != null || classification.phylum != null || classification.kingdom != null) {
                if (result.location.isValid())
                    classification.locationId = result.location.getAllIdentifiers();
                try {
                    long begin = System.nanoTime();
                    result.linnaean = this.newSearcher.search(classification);
                    result.linnaeanTime = (int) (System.nanoTime() - begin);
                } catch (BayesianException ex) {
                    logger.error("Unable to process row " + index, ex);
                }
            }
        }
        if (!this.vernacularInputs.isEmpty()) {
            AlaVernacularClassification classification = AlaVernacularFactory.instance().createClassification();
            for (BiConsumer<String[], AlaVernacularClassification> c: this.vernacularInputs) {
                c.accept(row, classification);
            }
            result.vernacular = Match.emptyMatch();
            if (classification.vernacularName != null) {
                if (result.location.isValid())
                    classification.locationId = result.location.getAllIdentifiers();
                try {
                    long begin = System.nanoTime();
                    result.vernacular = this.newSearcher.search(classification);
                    result.vernacularTime = (int) (System.nanoTime() - begin);
                } catch (BayesianException ex) {
                    logger.error("Unable to process row " + index, ex);
                }
            }
        }
        if (!this.oldInputs.isEmpty()) {
            LinnaeanRankClassification classification = new LinnaeanRankClassification();
             for (BiConsumer<String[], LinnaeanRankClassification> c: this.oldInputs) {
                 c.accept(row, classification);
             }
            result.original = new MetricsResultDTO();
            result.original.setErrors(new HashSet<>());
             if (classification.getScientificName() != null || classification.getGenus() != null || classification.getFamily() != null || classification.getOrder() != null || classification.getKlass() != null || classification.getPhylum() != null || classification.getKingdom() != null) {
                 try {
                     long begin = System.nanoTime();
                     result.original = this.oldSearcher.searchForRecordMetrics(classification, true);
                     result.originalTime = (int) (System.nanoTime() - begin);
                 } catch (Exception ex) {
                    logger.error("Unable to process row " + index, ex);
                }
            }
        }
        List<String> res = this.outputs.stream().map(o -> o.apply(result)).map(v -> v == null ? null : v.toString()).collect(Collectors.toList());
        String[] out = res.toArray(new String[res.size()]);
        this.report.writeNext(out);
    }

    protected void createSearchers() throws Exception {
        ALANameSearcherConfiguration config = ALANameSearcherConfiguration.builder()
                .linnaean(this.linnaeanIndex)
                .vernacular(this.vernacularIndex)
                .location(this.locationIndex)
                .matcherConfiguration(ClassificationMatcherConfiguration.builder().statistics(this.statistics).build())
                .searcherConfiguration(LuceneClassifierSearcherConfiguration.builder().build())
                .build();
        this.newSearcher = new ALANameSearcher(config);
        this.oldSearcher = new old.au.org.ala.names.search.ALANameSearcher(this.oldIndex.getAbsolutePath());
    }

    protected void closeSearchers() throws Exception {
        this.newSearcher.close();
    }

    protected void writeHeader() throws Exception {
        String[] header = new String[this.outputHeaders.size()];
        header = this.outputHeaders.toArray(header);
        this.report.writeNext(header);
    }

    protected void parseSource() throws Exception {
        String[] header = source.readNext();
        this.locationInputs = new ArrayList<>();
        this.classificationInputs = new ArrayList<>();
        this.vernacularInputs = new ArrayList<>();
        this.oldInputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        this.outputHeaders = new ArrayList<>();
        this.addOutputColumns(SUMMARY_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        for (int i = 0; i < header.length; i++) {
            String name = header[i];
            final int index = i;
            this.outputs.add(r -> index < r.input.length ? r.input[index] : null);
            this.outputHeaders.add(name);
        }
        this.addInputColumns(header, LOCATION_INPUT_COLUMNS, this.locationInputs);
        this.addOutputColumns(LOCATION_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        this.addIssueColumns(AlaLocationFactory.instance(), "location_", r -> r.location, this.outputs, this.outputHeaders);
        this.addInputColumns(header, LINNAEAN_INPUT_COLUMNS, this.classificationInputs);
        this.addOutputColumns(LINNAEAN_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        this.addIssueColumns(AlaLinnaeanFactory.instance(), "linnaean_", r -> r.linnaean, this.outputs, this.outputHeaders);
        this.addOutputColumns(LINNAEAN_NO_LOCATION_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        this.addInputColumns(header, VERNACULAR_INPUT_COLUMNS, this.vernacularInputs);
        this.addOutputColumns(VERNACULAR_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        this.addIssueColumns(AlaVernacularFactory.instance(), "vernacular_", r -> r.vernacular, this.outputs, this.outputHeaders);
        this.addInputColumns(header, OLD_INPUT_COLUMNS, this.oldInputs);
        this.addOutputColumns(OLD_OUTPUT_COLUMNS, this.outputs, this.outputHeaders);
        for (ErrorType error: ErrorType.values()) {
            if (error == ErrorType.NONE)
                continue;
            this.outputs.add(r -> r.original.getErrors().contains(error) ? true : null);
            this.outputHeaders.add("old_" + error.toString());
        }
    }

    protected <C> void addInputColumns(String[] header, List<Column<C>> columns, List<BiConsumer<String[], C>> inputs) {
        for (int i = 0; i < header.length; i++) {
            String name = header[i];
            final int index = i;
            final Optional<Column<C>> column = columns.stream().filter(c -> c.name.equals(name)).findFirst();
            column.filter(c -> c.reader != null).ifPresent(c -> inputs.add((r, cl) -> c.reader.accept(r[index], cl)));
        }
    }

    protected <C> void addOutputColumns(List<Column<C>> columns, List<Function<Result, Object>> outputs, List<String> outputHeaders) {
        for (Column<C> column: columns) {
            if (column.writer != null) {
                outputs.add(column.writer);
                outputHeaders.add(column.name);
            }
        }
    }
    
    protected <C extends Classification<C>> void addIssueColumns(NetworkFactory<C, ?, ?> factory, String prefix, Function<Result, Match<C, MatchMeasurement>> element, List<Function<Result, Object>> outputs, List<String> outputHeaders) {
        for (Term term: factory.getAllIssues()) {
            outputs.add(r -> element.apply(r).getIssues().contains(term) ? true : null);
            outputHeaders.add(prefix + term.simpleName());
        }
    }

    protected static <T> T fromString(Observable<T> observable, String value) {
        try {
            return observable.getAnalysis().fromString(value, null);
        } catch (StoreException e) {
            logger.error("Unable to parse " + value + " for " + observable);
            return null;
        }
    }
    

    public static void main(String[] args) throws Exception {
        ALAComparisonTool cli = new ALAComparisonTool();
        JCommander commander = JCommander.newBuilder().addObject(cli).args(args).build();
        if (cli.isHelp()) {
            commander.usage();
            return;
        }
        cli.run();
    }


    public static class Column<C> {
        public String name;
        public BiConsumer<String, C> reader;
        public Function<Result, Object> writer;

        public Column(String name, BiConsumer<String, C> reader, Function<Result, Object> writer) {
            this.name = name;
            this.reader = reader;
            this.writer = writer;
        }
    }

    public class Result {
        public String[] input;
        public Match<AlaLocationClassification, MatchMeasurement> location;
        public Integer locationTime;
        public Match<AlaLinnaeanClassification, MatchMeasurement> linnaeanNoLocation;
        public Integer linnaeanNoLocationTime;
        public Match<AlaLinnaeanClassification, MatchMeasurement> linnaean;
        public Integer linnaeanTime;
        public Match<AlaVernacularClassification, MatchMeasurement> vernacular;
        public Integer vernacularTime;
        public MetricsResultDTO original;
        public Integer originalTime;

        public String comparison() {
            String lid = this.linnaean.isValid() ? this.linnaean.getAccepted().taxonId : null;
            String oid = this.original.getResult() != null ? (this.original.getResult().getAcceptedLsid() != null ? this.original.getResult().getAcceptedLsid() : this.original.getResult().getId()) : null;
            if (Objects.equals(lid, oid))
                return "same";
            if (lid != null && oid == null)
                return "found";
            if (lid == null && oid != null) {
                if (this.linnaeanNoLocation.isValid())
                    return "location";
                return "notFound";
            }
            if (lid != null && oid != null) {
                if (this.linnaean.getAcceptedCandidate().getTrail().contains(oid))
                    return "lowerRank";
                if (!this.linnaean.getIssues().contains(AlaLinnaeanFactory.HIGHER_ORDER_MATCH) && this.linnaean.getMatch().acceptedNameUsageId != null && !this.original.getResult().isSynonym())
                    return "foundSynonym";
                try {
                    Match<AlaLinnaeanClassification, MatchMeasurement> old = ALAComparisonTool.this.newSearcher.search(oid);
                    if (this.original.getResult().getSynonymType() != null && old.getAccepted().parentNameUsageId.equals(lid))
                        return "parentChild";
                    if (this.linnaeanNoLocation.isValid() && Objects.equals(this.linnaeanNoLocation.getAccepted().taxonId, oid))
                        return "location";
                    if (old.getAcceptedCandidate().getTrail().contains(lid)) {
                        return "higherRank";
                    }
                } catch (Exception ex) {
                }
            }
            String lk = this.linnaean.isValid() ? this.linnaean.getAccepted().kingdom : null;
            String ok = this.original.getResult() != null ? this.original.getResult().getRankClassification().getKingdom() : null;
            if (!Objects.equals(lk, ok))
                return "kingdom";
            if (this.linnaean.isValid() && this.original.getResult() != null && Objects.equals(this.linnaean.getMatch().scientificName, this.original.getResult().getRankClassification().getScientificName()))
                return "sameName";
            return "other";
         }
    }

}
