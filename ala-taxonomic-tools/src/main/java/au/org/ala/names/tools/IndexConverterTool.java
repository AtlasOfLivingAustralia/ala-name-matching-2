package au.org.ala.names.tools;

import au.org.ala.bayesian.ExternalContext;
import au.org.ala.bayesian.NetworkFactory;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.AlaLinnaeanFactory;
import au.org.ala.names.AlaVernacularFactory;
import au.org.ala.names.TaxonNameSoundEx;
import au.org.ala.names.lucene.LuceneClassifier;
import au.org.ala.util.Counter;
import au.org.ala.util.Metadata;
import au.org.ala.vocab.TaxonomicStatus;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.FileConverter;
import lombok.Getter;
import old.au.org.ala.names.lucene.analyzer.LowerCaseKeywordAnalyzer;
import old.au.org.ala.names.model.ALAParsedName;
import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.gbif.dwc.terms.Term;
import org.gbif.dwc.terms.TermFactory;
import org.gbif.nameparser.api.ParsedName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Convert an index in the current form into the old ala-name-matching style index.
 * <p>
 * This tool can be used to create an index for older applications that has the same
 * left- and right-values that the source index does.
 * </p>
 */
public class IndexConverterTool  {
    private static final Logger logger = LoggerFactory.getLogger(IndexConverterTool.class);

    private static final int BATCH_SIZE = 1000;

    private static final Pattern NON_ALPHA =Pattern.compile("[^A-Z]");

    private static final ThreadLocal<TaxonNameSoundEx> SOUNDEX = ThreadLocal.withInitial(TaxonNameSoundEx::new);

    // In approximate order they appear in the original index
    private static final IndexConverter[] TAXON_CONVERTERS = {
            new ObservableIndexConverter<String, String>(NameIndexField.ID, AlaLinnaeanFactory.taxonId),
            new ObservableIndexConverter<String, String>(NameIndexField.LSID, AlaLinnaeanFactory.taxonId),
            new ObservableIndexConverter<String, String>(NameIndexField.ALA, AlaLinnaeanFactory.taxonId, v -> v != null && v.startsWith("ALA_") ? "T" : null),
            new NameIndexConverter<String, String>(NameIndexField.NAME, LuceneClassifier.NAMES_FIELD, String.class),
            new ObservableIndexConverter<String, String>(NameIndexField.NAME, AlaLinnaeanFactory.altScientificName),
            new ObservableIndexConverter<String, String>(NameIndexField.NAME_CANONICAL, AlaLinnaeanFactory.scientificName),
            new ObservableIndexConverter<String, String>(NameIndexField.NAME_COMPLETE, AlaLinnaeanFactory.altScientificName),
            new ObservableIndexConverter<Integer, Integer>(NameIndexField.RANK_ID, AlaLinnaeanFactory.rankId),
            new ObservableIndexConverter<String, String>(NameIndexField.RANK, AlaLinnaeanFactory.taxonRank),
            new ObservableIndexConverter<String, String>(NameIndexField.iS_SYNONYM, AlaLinnaeanFactory.taxonomicStatus, v -> checkSynonym(v) ? "T" : "F"),
            new ObservableIndexConverter<String, String>(NameIndexField.COMMON_NAME, AlaLinnaeanFactory.vernacularName),
            new IndexIndexConverter(NameIndexField.LEFT, LuceneClassifier.INDEX_FIELD, 0),
            new IndexIndexConverter(NameIndexField.RIGHT, LuceneClassifier.INDEX_FIELD, 1),
            new ObservableIndexConverter<Integer, Integer>(NameIndexField.PRIORITY, AlaLinnaeanFactory.priority),
            new ObservableIndexConverter<String, String>(NameIndexField.GENUS_EX, AlaLinnaeanFactory.soundexGenus),
            new ObservableIndexConverter<String, String>(NameIndexField.SPECIES_EX, AlaLinnaeanFactory.soundexSpecificEpithet),
            new ScientificNameIndexConverter(NameIndexField.INFRA_EX, AlaLinnaeanFactory.scientificName, AlaLinnaeanFactory.taxonRank, AlaLinnaeanFactory.nomenclaturalCode, IndexConverterTool::getInfraspecificSoundex),
            new ScientificNameIndexConverter(NameIndexField.SPECIES, AlaLinnaeanFactory.scientificName, AlaLinnaeanFactory.taxonRank, AlaLinnaeanFactory.nomenclaturalCode, IndexConverterTool::getSpeciesName),
            new ObservableIndexConverter<String, String>(NameIndexField.SPECIES_ID, AlaLinnaeanFactory.speciesId),
            new ObservableIndexConverter<String, String>(NameIndexField.GENUS, AlaLinnaeanFactory.genus),
            new ObservableIndexConverter<String, String>(NameIndexField.GENUS_ID, AlaLinnaeanFactory.genusId),
            new ScientificNameIndexConverter(NameIndexField.PHRASE, AlaLinnaeanFactory.scientificName, AlaLinnaeanFactory.taxonRank, AlaLinnaeanFactory.nomenclaturalCode, IndexConverterTool::getPhrase),
            new ObservableIndexConverter<String, String>(NameIndexField.AUTHOR, AlaLinnaeanFactory.scientificNameAuthorship),
            new ObservableIndexConverter<String, String>(NameIndexField.KINGDOM, AlaLinnaeanFactory.kingdom),
            new ObservableIndexConverter<String, String>(NameIndexField.KINGDOM_ID, AlaLinnaeanFactory.kingdomId),
            new ObservableIndexConverter<String, String>(NameIndexField.FAMILY, AlaLinnaeanFactory.family),
            new ObservableIndexConverter<String, String>(NameIndexField.FAMILY_ID, AlaLinnaeanFactory.familyId),
            new ObservableIndexConverter<String, String>(NameIndexField.PHYLUM, AlaLinnaeanFactory.phylum),
            new ObservableIndexConverter<String, String>(NameIndexField.PHYLUM_ID, AlaLinnaeanFactory.phylumId),
            new ObservableIndexConverter<String, String>(NameIndexField.CLASS, AlaLinnaeanFactory.class_),
            new ObservableIndexConverter<String, String>(NameIndexField.CLASS_ID, AlaLinnaeanFactory.classId),
            new ObservableIndexConverter<String, String>(NameIndexField.ORDER, AlaLinnaeanFactory.order),
            new ObservableIndexConverter<String, String>(NameIndexField.ORDER_ID, AlaLinnaeanFactory.orderId),
            new ScientificNameIndexConverter(NameIndexField.VOUCHER, AlaLinnaeanFactory.scientificName, AlaLinnaeanFactory.taxonRank, AlaLinnaeanFactory.nomenclaturalCode, IndexConverterTool::getVoucher),
            new ObservableIndexConverter<String, String>(NameIndexField.SYNONYM_TYPE, AlaLinnaeanFactory.taxonomicStatus, v -> checkSynonym(v) ? v : null),
            //new ObservableIndexConverter<String, String>(NameIndexField.PARENT_ID, AlaLinnaeanFactory.parentNameUsageId),
            new ObservableIndexConverter<String, String>(NameIndexField.ACCEPTED, AlaLinnaeanFactory.acceptedNameUsageId)
            // new ScientificNameIndexConverter(NameIndexField.INFRA_SPECIFIC, AlaLinnaeanFactory.scientificName, AlaLinnaeanFactory.taxonRank, AlaLinnaeanFactory.nomenclaturalCode, IndexConverterTool::getInfraspecificEipthet),
    };
    // In approximate order they appear in the original index
    private static final IndexConverter[] VERNACULAR_CONVERTERS = {
            new ObservableIndexConverter<String, String>(NameIndexField.LSID, AlaVernacularFactory.taxonId),
            new ObservableIndexConverter<String, String>(NameIndexField.COMMON_NAME, AlaVernacularFactory.vernacularName),
            new ObservableIndexConverter<String, String>(NameIndexField.SEARCHABLE_COMMON_NAME, AlaVernacularFactory.vernacularName, IndexConverterTool::createSearchableCommonName),
            new ObservableIndexConverter<String, String>(NameIndexField.NAME, AlaVernacularFactory.scientificName),
            new ObservableIndexConverter<String, String>(NameIndexField.LANGUAGE, AlaVernacularFactory.language),
            new ObservableIndexConverter<Integer, Integer>(NameIndexField.PRIORITY, AlaVernacularFactory.priority)
    };

    @Parameter(names = "--help", help = true)
    @Getter
    private boolean help = false;
    /**
     * The output directory
     */
    @Parameter(names = {"-o", "--output"}, description = "The directory that holds the output indexes", converter = FileConverter.class, required = true)
    @Getter
    private File output = null;
    /**
     * The input index
     */
    @Parameter(description = "The source indexes", listConverter = FileConverter.class, required = true)
    @Getter
    private List<File> sources;

    protected void convert(File source, String sub, NetworkFactory<?, ?, ?> factory, IndexConverter[] converters, List<Observable> key) throws Exception {
        logger.info("Processing ALA Linnaean index");
        Analyzer analyzer = LowerCaseKeywordAnalyzer.newInstance();
        FSDirectory inputDir = FSDirectory.open(source.toPath());
        IndexReader reader = DirectoryReader.open(inputDir);
        IndexSearcher searcher = new IndexSearcher(reader);
        File outputCb = new File(this.output, sub);
        if (!outputCb.exists())
            outputCb.mkdirs();
        FSDirectory outputDir = FSDirectory.open(outputCb.toPath());
        IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(outputDir, writerConfig);
        Query query = new BooleanQuery.Builder().add(LuceneClassifier.getTypeClause(factory.getConcept())).build();
        int total =  searcher.count(query);
        Set<String> keys = new HashSet<>(10000);
        String idField = factory.getIdentifier().map(o -> o.getExternal(ExternalContext.LUCENE)).orElse(LuceneClassifier.ID_FIELD);
        Counter records = new Counter("Processed {0} concepts, {2,number,0.0}/s, {3,number,0.0}%", logger, 10000, total);
        Counter rejected = new Counter("Rejected {0} concepts, with key collisions {3,number,0.0}%", logger, 10000, total);
        records.start();
        rejected.start();
        ScoreDoc last = null;
        do {
            TopDocs docs = searcher.searchAfter(last, query, BATCH_SIZE, Sort.INDEXORDER);
            last = null;
            for (int i = 0; i < docs.scoreDocs.length; i++) {
                last = docs.scoreDocs[i];
                final Document document = searcher.doc(last.doc);
                String id = document.get(idField);
                String kv = key == null ? null : key.stream().map(o -> document.get(o.getExternal(ExternalContext.LUCENE))).map(v -> v == null ? "" : v.toString()).collect(Collectors.joining("|"));
                if (kv != null) {
                    if (keys.contains(kv)) {
                        rejected.increment(id);
                        continue;
                    }
                    keys.add(kv);
                }
               Document old = new Document();
                for (IndexConverter converter: converters) {
                    converter.convert(id, document, old);
                }
                writer.addDocument(old);
                records.increment(document);
            }
            writer.commit();
        } while (last != null);
        records.stop();
        rejected.stop();
        writer.close();
        reader.close();
    }

    public void convertTaxon(File source) throws Exception {
        logger.info("Processing ALA Linnaean index");
        this.convert(source, "cb", AlaLinnaeanFactory.instance(), TAXON_CONVERTERS, null);
    }


    public void convertVernacular(File source) throws Exception {
        logger.info("Processing ALA Vernacular index");
        this.convert(source, "vernacular", AlaVernacularFactory.instance(), VERNACULAR_CONVERTERS, Arrays.asList(AlaLinnaeanFactory.taxonId, AlaVernacularFactory.vernacularName, AlaVernacularFactory.language));
     }

    /**
     * Run the conversion.
     *
     * @throws Exception if anything untoward happens
     */
    public void run() throws Exception {
        for (File source: this.sources) {
            logger.info("Processing " + source);
            if (!source.exists() || !source.isDirectory())
                throw new IllegalArgumentException("Source " + source + " invalid");
            File metadataFile = new File(source, "metadata.json");
            Metadata metadata = Metadata.read(metadataFile);
            Term concept = TermFactory.instance().findTerm(metadata.getProperties().get("concept"));
            logger.info("Index concept is " + concept);
            if (concept.equals(AlaLinnaeanFactory.CONCEPT))
                this.convertTaxon(source);
            else if (concept.equals(AlaVernacularFactory.CONCEPT))
                this.convertVernacular(source);
        }
    }

    public static void main(String[] args) throws Exception {
        IndexConverterTool cli = new IndexConverterTool();
        JCommander commander = JCommander.newBuilder().addObject(cli).args(args).build();
        if (cli.isHelp()) {
            commander.usage();
            return;
        }
        cli.run();
    }

    protected static boolean checkSynonym(String v) {
        try {
            TaxonomicStatus status = AlaLinnaeanFactory.taxonomicStatus.getAnalysis().fromString(v, null);
            if (status == null)
                return false;
            return status.isSynonymLike() || status == TaxonomicStatus.miscellaneousLiterature;
        } catch (StoreException ex) {
            logger.error("Unable to parse taxonomic status " + v, ex);
            return false;
        }
    }

    protected static String getSpeciesName(ParsedName parsed) {
        if (parsed == null)
            return null;
        if (!parsed.getRank().isSpeciesOrBelow() || parsed.getSpecificEpithet() == null)
            return null;
        return parsed.getGenus() + " " + parsed.getSpecificEpithet();
    }
    protected static String getInfraspecificEipthet(ParsedName parsed) {
        if (parsed == null)
            return null;
        return parsed.getInfraspecificEpithet();
    }

    protected static String getInfraspecificSoundex(ParsedName parsed) {
        if (parsed == null)
            return "<null>";
        String infra = parsed.getInfraspecificEpithet();
        infra = infra == null ? null : SOUNDEX.get().treatWord(infra, parsed.getRank(), parsed.getType(), true);
        return infra == null ? "<null>" : infra;
    }

    protected static String getPhrase(ParsedName parsed) {
        if (parsed == null)
            return null;
        String phrase = parsed.getPhrase();
        if (phrase == null)
            return null;
        ALAParsedName pn = new ALAParsedName();
        pn.setLocationPhraseDescription(phrase);
        return pn.cleanPhrase;
    }

    protected static String getVoucher(ParsedName parsed) {
        if (parsed == null)
            return null;
        String voucher = parsed.getVoucher();
        if (voucher == null)
            return null;
        ALAParsedName pn = new ALAParsedName();
        pn.setPhraseVoucher(voucher);
        return pn.cleanVoucher;
    }

    protected static String createSearchableCommonName(String name) {
        if (name == null)
            return null;
        return NON_ALPHA.matcher(name.toUpperCase()).replaceAll("");
    }

}
