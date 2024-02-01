package au.org.ala.names.tools;

import au.org.ala.bayesian.ExternalContext;
import au.org.ala.bayesian.Observable;
import au.org.ala.bayesian.StoreException;
import au.org.ala.names.RankAnalysis;
import au.org.ala.names.lucene.LuceneClassifier;
import old.au.org.ala.names.search.NameIndexField;
import org.apache.lucene.document.Document;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.nameparser.NameParserGBIF;
import org.gbif.nameparser.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Get a part of a scientific name.
 */
public class ScientificNameIndexConverter extends ObservableIndexConverter<String, String> {
    private static final Logger logger = LoggerFactory.getLogger(ScientificNameIndexConverter.class);

    protected static final ThreadLocal<NameParser> PARSER = ThreadLocal.withInitial(NameParserGBIF::new);

    protected static final Cache<String, ParsedName> CACHE = Cache2kBuilder.of(String.class, ParsedName.class)
            .name("parsed")
            .entryCapacity(10000)
            .permitNullValues(true)
            .build();

    private static final boolean REPORT_ALL_PARSE_ERRORS = false;

    private Observable<Rank> rankObservable;
    private Observable<NomenclaturalCode> nomenclaturalCodeObservable;
    private Function<ParsedName, String> element;


    /**
     * Construct for an observable and index field
     *
     * @param field                       The old name index field
     * @param observable                  The observable to get the scienticic name from
     * @param rankObservable              The observable to get the rankl from
     * @param nomenclaturalCodeObservable The observable to get the nomenclatural code from
     * @param element                     The function that returns the element of the parsed name we want
     */
    public ScientificNameIndexConverter(NameIndexField field, Observable<String> observable, Observable<Rank> rankObservable, Observable<NomenclaturalCode> nomenclaturalCodeObservable, Function<ParsedName, String> element) {
        this(field, observable, rankObservable, nomenclaturalCodeObservable, element, null);
    }

    /**
     * Construct for an observable, index field and transform
     *
     * @param field                       The old name index field
     * @param observable                  The observable to get the scienticic name from
     * @param rankObservable              The observable to get the rankl from
     * @param nomenclaturalCodeObservable The observable to get the nomenclatural code from
     * @param element                     The function that returns the element of the parsed name we want
     * @param transform                   The transform to use, null if none
     */
    public ScientificNameIndexConverter(NameIndexField field, Observable<String> observable, Observable<Rank> rankObservable, Observable<NomenclaturalCode> nomenclaturalCodeObservable, Function<ParsedName, String> element, Function<String, String> transform) {
        super(field, observable, transform);
        this.rankObservable = rankObservable;
        this.nomenclaturalCodeObservable = nomenclaturalCodeObservable;
        this.element = element;
    }

    public void convert(String id, Document from, Document to) {
        ParsedName parsed = this.parse(id, from);
        String value = parsed == null ? null : this.element.apply(parsed);
        if (this.transform != null && value != null)
            value = this.transform.apply(value);
        this.field.store(value, to);
    }

    protected ParsedName parse(String id, Document document) {
        String docId = document.get(LuceneClassifier.ID_FIELD);
        return CACHE.computeIfAbsent(docId, k -> this.doParse(k, id, document));
    }

    protected ParsedName doParse(String docId, String id, Document document) {
        ParsedName parsed = null;
        String nomCodeString = this.nomenclaturalCodeObservable == null ? null : document.get(this.nomenclaturalCodeObservable.getExternal(ExternalContext.LUCENE));
        NomenclaturalCode nomenclaturalCode = null;
        try {
            nomenclaturalCode = nomCodeString == null ? null : this.nomenclaturalCodeObservable.getAnalysis().fromString(nomCodeString, null);
        } catch (StoreException ex) {
            logger.error("Unable to parse nomenclatural code for " + nomCodeString);
        }
        NomCode nomCode = nomenclaturalCode == null ? null : NomCode.valueOf(nomenclaturalCode.name());
        String rankString = this.rankObservable == null ? null : document.get(this.rankObservable.getExternal(ExternalContext.LUCENE));
        Rank rank = rankString == null ? null : ((RankAnalysis) this.rankObservable.getAnalysis()).fromString(rankString, nomenclaturalCode);
        String scientificName = document.get(this.observable.getExternal(ExternalContext.LUCENE));
        try {
            parsed = PARSER.get().parse(scientificName, rank, nomCode);
        } catch (UnparsableNameException ex) {
            if (REPORT_ALL_PARSE_ERRORS || (ex.getType() != NameType.HYBRID_FORMULA && ex.getType() != NameType.VIRUS))
                logger.info("Unparseable," + id + "," + ex.getType() + "," + ex.getName());
        } catch (Exception ex) {
            logger.error("Unable to parse " + scientificName, ex);
        }
        return parsed;
    }
}

