package au.org.ala.names;

import au.org.ala.bayesian.*;
import au.org.ala.vocab.ALATerm;
import org.gbif.api.vocabulary.NomenclaturalCode;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.nameparser.api.ParsedName;
import org.gbif.nameparser.api.Rank;
import org.junit.Before;
import org.junit.Test;
import static au.org.ala.names.ScientificNameAnalyser.Analysis;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

public class ScientificNameAnalyserTest {
    private static final Issues DEFAULT_ISSUES = Issues.of(ALATerm.TaxonomicIssue);

    private ScientificNameAnalyser<Classification> analyser;

    @Before
    public void setUp() throws Exception {
        this.analyser = new ScientificNameAnalyser<Classification>() {
            @Override
            public void analyseForIndex(Classification classification) throws InferenceException {
                throw new UnsupportedOperationException();
            }

            @Override
            public void analyseForSearch(Classification classification) throws InferenceException {
                throw new UnsupportedOperationException();
            }

            @Override
            public Set<String> analyseNames(Classifier classifier, Observable name, Optional<Observable> complete, Optional<Observable> additional, boolean canonical) throws InferenceException {
                throw new UnsupportedOperationException();
            }
        };
    }

    Analysis makeAnalysis(String scientificName, String scientificNameAuthorship, Rank rank, NomenclaturalCode code) {
        return new Analysis(scientificName, scientificNameAuthorship, rank, code);
    }

    @Test
    public void processIndeterminate1() {
        Analysis analysis = new Analysis("Chaetodon speculum", null, null, null);
        this.analyser.processIndeterminate(analysis, null, null);
        assertEquals("Chaetodon speculum", analysis.getScientificName());
        assertFalse(analysis.isIndeterminate());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processIndeterminate2() {
        Analysis analysis = new Analysis("Chaetodon speculum", null, null, null);
        this.analyser.processIndeterminate(analysis, " ", DEFAULT_ISSUES);
        assertEquals("Chaetodon speculum", analysis.getScientificName());
        assertFalse(analysis.isIndeterminate());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processIndeterminate3() {
        Analysis analysis = new Analysis("Chaetodon? speculum", null, null, null);
        this.analyser.processIndeterminate(analysis, null, DEFAULT_ISSUES);
        assertEquals("Chaetodon? speculum", analysis.getScientificName());
        assertTrue(analysis.isIndeterminate());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processIndeterminate4() {
        Analysis analysis = new Analysis("Chaetodon? speculum", null, null, null);
        this.analyser.processIndeterminate(analysis, " ", DEFAULT_ISSUES);
        assertEquals("Chaetodon speculum", analysis.getScientificName());
        assertTrue(analysis.isIndeterminate());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processAffinitySpecies1() {
        Analysis analysis = new Analysis("Eudyptula minor", null, null, null);
        this.analyser.processAffinitySpecies(analysis, null, null);
        assertEquals("Eudyptula minor", analysis.getScientificName());
        assertFalse(analysis.isAffinitySpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processAffinitySpecies2() {
        Analysis analysis = new Analysis("Eudyptula minor", null, null, null);
        this.analyser.processAffinitySpecies(analysis, " aff. ", DEFAULT_ISSUES);
        assertEquals("Eudyptula minor", analysis.getScientificName());
        assertFalse(analysis.isAffinitySpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processAffinitySpecies3() {
        Analysis analysis = new Analysis("Eudyptula aff. minor", null, null, null);
        this.analyser.processAffinitySpecies(analysis, null, null);
        assertEquals("Eudyptula aff. minor", analysis.getScientificName());
        assertTrue(analysis.isAffinitySpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processAffinitySpecies4() {
        Analysis analysis = new Analysis("Eudyptula aff. minor", null, null, null);
        this.analyser.processAffinitySpecies(analysis, " aff. ", DEFAULT_ISSUES);
        assertEquals("Eudyptula aff. minor", analysis.getScientificName());
        assertTrue(analysis.isAffinitySpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }


    @Test
    public void processAffinitySpecies5() {
        Analysis analysis = new Analysis("Eudyptula aff minor", null, null, null);
        this.analyser.processAffinitySpecies(analysis, " aff. ", DEFAULT_ISSUES);
        assertEquals("Eudyptula aff. minor", analysis.getScientificName());
        assertTrue(analysis.isAffinitySpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processConferSpecies1() {
        Analysis analysis = new Analysis("Neoceratodus forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, null, null);
        assertEquals("Neoceratodus forsteri", analysis.getScientificName());
        assertFalse(analysis.isConferSpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processConferSpecies2() {
        Analysis analysis = new Analysis("Neoceratodus forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, " cf. ", DEFAULT_ISSUES);
        assertEquals("Neoceratodus forsteri", analysis.getScientificName());
        assertFalse(analysis.isConferSpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processConferSpecies3() {
        Analysis analysis = new Analysis("Neoceratodus cf forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, null, null);
        assertEquals("Neoceratodus cf forsteri", analysis.getScientificName());
        assertTrue(analysis.isConferSpecies());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processConferSpecies4() {
        Analysis analysis = new Analysis("Neoceratodus cf. forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, " cf. ", DEFAULT_ISSUES);
        assertEquals("Neoceratodus cf. forsteri", analysis.getScientificName());
        assertTrue(analysis.isConferSpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processConferSpecies5() {
        Analysis analysis = new Analysis("Neoceratodus cf forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, " cf. ", DEFAULT_ISSUES);
        assertEquals("Neoceratodus cf. forsteri", analysis.getScientificName());
        assertTrue(analysis.isConferSpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processConferSpecies6() {
        Analysis analysis = new Analysis("Neoceratodus cfr. forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, " cf. ", DEFAULT_ISSUES);
        assertEquals("Neoceratodus cf. forsteri", analysis.getScientificName());
        assertTrue(analysis.isConferSpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processConferSpecies7() {
        Analysis analysis = new Analysis("Neoceratodus conf forsteri", null, null, null);
        this.analyser.processConferSpecies(analysis, " cf. ", DEFAULT_ISSUES);
        assertEquals("Neoceratodus cf. forsteri", analysis.getScientificName());
        assertTrue(analysis.isConferSpecies());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }
    
    @Test
    public void processSpeciesNovum1() {
        Analysis analysis = new Analysis("Lates calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, false, null);
        assertEquals("Lates calcarifer", analysis.getScientificName());
        assertFalse(analysis.isSpeciesNovum());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum2() {
        Analysis analysis = new Analysis("Lates calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, true, DEFAULT_ISSUES);
        assertEquals("Lates calcarifer", analysis.getScientificName());
        assertFalse(analysis.isSpeciesNovum());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum3() {
        Analysis analysis = new Analysis("Lates sp nov calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, false, null);
        assertEquals("Lates sp nov calcarifer", analysis.getScientificName());
        assertTrue(analysis.isSpeciesNovum());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum4() {
        Analysis analysis = new Analysis("Lates sp. nov. calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, true, DEFAULT_ISSUES);
        assertEquals("Lates sp. nov. calcarifer", analysis.getScientificName());
        assertTrue(analysis.isSpeciesNovum());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum5() {
        Analysis analysis = new Analysis("Lates sp novum calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, true, DEFAULT_ISSUES);
        assertEquals("Lates sp. nov. calcarifer", analysis.getScientificName());
        assertTrue(analysis.isSpeciesNovum());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum6() {
        Analysis analysis = new Analysis("Lates gen. nov. calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, true, DEFAULT_ISSUES);
        assertEquals("Lates gen. nov. calcarifer", analysis.getScientificName());
        assertTrue(analysis.isSpeciesNovum());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processSpeciesNovum7() {
        Analysis analysis = new Analysis("Lates ord novum calcarifer", null, null, null);
        this.analyser.processSpeciesNovum(analysis, true, DEFAULT_ISSUES);
        assertEquals("Lates ord. nov. calcarifer", analysis.getScientificName());
        assertTrue(analysis.isSpeciesNovum());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processPhraseName1() {
        Analysis analysis = new Analysis("Hapalochlaena maculosa", null, null, null);
        this.analyser.processPhraseName(analysis, null);
        assertEquals("Hapalochlaena maculosa", analysis.getScientificName());
        assertFalse(analysis.isPhraseName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processPhraseName2() {
        Analysis analysis = new Analysis("Acacia sp. Hervey Range (G.Cocks AQ398930) Qld Herbarium", null, null, null);
        this.analyser.processPhraseName(analysis, null);
        assertEquals("Acacia sp. Hervey Range (G.Cocks AQ398930) Qld Herbarium", analysis.getScientificName());
        assertTrue(analysis.isPhraseName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processPhraseName3() {
        Analysis analysis = new Analysis("Acacia sp. Mulga Holey Trunk (P.K.Latz 12458)", null, null, null);
        this.analyser.processPhraseName(analysis, null);
        assertEquals("Acacia sp. Mulga Holey Trunk (P.K.Latz 12458)", analysis.getScientificName());
        assertTrue(analysis.isPhraseName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processPhraseName4() {
        Analysis analysis = new Analysis("Acacia sp. P129 (B.R.Maslin 5171)", null, null, null);
        this.analyser.processPhraseName(analysis, DEFAULT_ISSUES);
        assertEquals("Acacia sp. P129 (B.R.Maslin 5171)", analysis.getScientificName());
        assertTrue(analysis.isPhraseName());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processRankEnding1() {
        Analysis analysis = new Analysis("Dendrochirus zebra", null, null, null);
        this.analyser.processRankEnding(analysis, false, null);
        assertEquals("Dendrochirus zebra", analysis.getScientificName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processRankEnding2() {
        Analysis analysis = new Analysis("Dendrochirus sp.", null, null, null);
        this.analyser.processRankEnding(analysis, false, DEFAULT_ISSUES);
        assertEquals("Dendrochirus sp.", analysis.getScientificName());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processRankEnding3() {
        Analysis analysis = new Analysis("Dendrochirus spp.", null, null, null);
        this.analyser.processRankEnding(analysis, true, DEFAULT_ISSUES);
        assertEquals("Dendrochirus", analysis.getScientificName());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processRankMarker1() {
        Analysis analysis = new Analysis("Gymnothorax javanicus", null, null, null);
        this.analyser.processRankMarker(analysis, false, null);
        assertEquals("Gymnothorax javanicus", analysis.getScientificName());
        assertEquals(Rank.UNRANKED, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processRankMarker2() {
        Analysis analysis = new Analysis("Gymnothorax sp. javanicus", null, null, null);
        this.analyser.processRankMarker(analysis, false, DEFAULT_ISSUES);
        assertEquals("Gymnothorax sp. javanicus", analysis.getScientificName());
        assertEquals(Rank.SPECIES, analysis.getRank());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processRankMarker3() {
        Analysis analysis = new Analysis("Gymnothorax sp. javanicus", null, null, null);
        this.analyser.processRankMarker(analysis, true, null);
        assertEquals("Gymnothorax javanicus", analysis.getScientificName());
        assertEquals(Rank.SPECIES, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processRankMarker4() {
        Analysis analysis = new Analysis("Poa sect. Atropis", null, null, null);
        this.analyser.processRankMarker(analysis, true, DEFAULT_ISSUES);
        assertEquals("Poa sect. Atropis", analysis.getScientificName());
        assertEquals(Rank.SECTION, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName1() throws Exception {
        Analysis analysis = new Analysis("Toxotes chatareus", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Toxotes chatareus", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Toxotes chatareus", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.SPECIES, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName2() throws Exception {
        Analysis analysis = new Analysis(" ", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertNull(analysis.getParsedName());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void parseName3() throws Exception {
        Analysis analysis = new Analysis("Blepharidophyllum subgen. Clandarium", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Blepharidophyllum subgen. Clandarium", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Blepharidophyllum subgen. Clandarium", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.SUBGENUS, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName4() throws Exception {
        Analysis analysis = new Analysis("Calyptorhynchus (Zanda) latirostris", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Calyptorhynchus (Zanda) latirostris", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Calyptorhynchus (Zanda) latirostris", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.SPECIES, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName5() throws Exception {
        Analysis analysis = new Analysis("Hypnodendron spininervium subsp. archeri", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Hypnodendron spininervium subsp. archeri", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Hypnodendron spininervium subsp. archeri", analysis.getParsedName().canonicalNameComplete());
         assertEquals(Rank.SUBSPECIES, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName6() throws Exception {
        Analysis analysis = new Analysis("Hypnodendron spininervium archeri", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Hypnodendron spininervium archeri", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Hypnodendron spininervium archeri", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.INFRASPECIFIC_NAME, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void parseName7() throws Exception {
        Analysis analysis = new Analysis("Cymbiola pulchra cracenta (McMichael, 1963)", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Cymbiola pulchra cracenta (McMichael, 1963)", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Cymbiola pulchra cracenta (McMichael, 1963)", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.INFRASPECIFIC_NAME, analysis.getRank());
        assertEquals(Issues.of(), analysis.getIssues());
    }


    @Test
    public void parseName8() throws Exception {
        Analysis analysis = new Analysis("Viola sp. Lamington NP (R.Schodde 1153)", null, null, null);
        this.analyser.parseName(analysis, DEFAULT_ISSUES);
        assertEquals("Viola sp. Lamington NP (R.Schodde 1153)", analysis.getScientificName());
        assertNotNull(analysis.getParsedName());
        assertEquals(ParsedName.State.COMPLETE, analysis.getParsedName().getState());
        assertEquals("Viola sp. Lamington NP (R.Schodde 1153)", analysis.getParsedName().canonicalNameComplete());
        assertEquals(Rank.SPECIES, analysis.getRank());
        assertTrue(analysis.isPhraseName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void reducedName1() throws Exception {
        Analysis analysis = new Analysis("Toxotes chatareus", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        assertEquals("Toxotes chatareus", this.analyser.reducedName(analysis.getParsedName()));
    }

    @Test
    public void reducedName2() throws Exception {
        Analysis analysis = new Analysis("Blepharidophyllum subgen. Clandarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        assertEquals("Blepharidophyllum Clandarium", this.analyser.reducedName(analysis.getParsedName()));
    }

    @Test
    public void reducedName3() throws Exception {
        Analysis analysis = new Analysis("Hypnodendron spininervium subsp. archeri", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        assertEquals("Hypnodendron spininervium archeri", this.analyser.reducedName(analysis.getParsedName()));
    }

    @Test
    public void reducedName4() throws Exception {
        Analysis analysis = new Analysis("Cymbiola pulchra cracenta (McMichael, 1963)", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        assertEquals("Cymbiola pulchra cracenta", this.analyser.reducedName(analysis.getParsedName()));
    }

    @Test
    public void reducedName5() throws Exception {
        Analysis analysis = new Analysis("Viola sp. Lamington NP (R.Schodde 1153) Qld Herbarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        assertEquals("Viola sp. Lamington NP (R.Schodde 1153)", this.analyser.reducedName(analysis.getParsedName()));
    }
    
    @Test
    public void processParsedScientificName1() throws Exception {
        Analysis analysis = new Analysis("Toxotes chatareus", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, DEFAULT_ISSUES);
        assertEquals("Toxotes chatareus", analysis.getScientificName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processParsedScientificName2() throws Exception {
        Analysis analysis = new Analysis("Blepharidophyllum subgen. Clandarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, DEFAULT_ISSUES);
        assertEquals("Blepharidophyllum subgen. Clandarium", analysis.getScientificName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processParsedScientificName3() throws Exception {
        Analysis analysis = new Analysis("Hypnodendron spininervium subsp. archeri", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, DEFAULT_ISSUES);
        assertEquals("Hypnodendron spininervium subsp. archeri", analysis.getScientificName());
        assertEquals(Issues.of(), analysis.getIssues());
    }

    @Test
    public void processParsedScientificName4() throws Exception {
        Analysis analysis = new Analysis("Cymbiola pulchra cracenta (McMichael, 1963)", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, DEFAULT_ISSUES);
        assertEquals("Cymbiola pulchra cracenta", analysis.getScientificName());
        assertEquals("(McMichael, 1963)", analysis.getScientificNameAuthorship());
        assertEquals(DEFAULT_ISSUES, analysis.getIssues());
    }

    @Test
    public void processParsedScientificName5() throws Exception {
        Analysis analysis = new Analysis("Viola sp. Lamington NP (R.Schodde 1153) Qld Herbarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, DEFAULT_ISSUES);
        assertEquals("Viola sp. Lamington NP (R.Schodde 1153)", analysis.getScientificName());
        assertEquals(Issues.of(), analysis.getIssues());
    }


    @Test
    public void processAdditionalScientificNames1() throws Exception {
        Analysis analysis = new Analysis("Toxotes chatareus", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, null);
        this.analyser.processAdditionalScientificNames(analysis);
        assertNotNull(analysis.getNames());
        assertEquals(1, analysis.getNames().size());
        assertTrue(analysis.getNames().contains("Toxotes chatareus"));
    }

    @Test
    public void processAdditionalScientificNames2() throws Exception {
        Analysis analysis = new Analysis("Blepharidophyllum subgen. Clandarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, null);
        this.analyser.processAdditionalScientificNames(analysis);
        assertNotNull(analysis.getNames());
        assertEquals(3, analysis.getNames().size());
        assertTrue(analysis.getNames().contains("Blepharidophyllum subgen. Clandarium"));
        assertTrue(analysis.getNames().contains("Blepharidophyllum Clandarium"));
        assertTrue(analysis.getNames().contains("Clandarium"));
    }

    @Test
    public void processAdditionalScientificNames3() throws Exception {
        Analysis analysis = new Analysis("Hypnodendron spininervium subsp. archeri", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, null);
        this.analyser.processAdditionalScientificNames(analysis);
        assertNotNull(analysis.getNames());
        System.out.println(analysis.getNames());
        assertEquals(2, analysis.getNames().size());
        assertTrue(analysis.getNames().contains("Hypnodendron spininervium subsp. archeri"));
        assertTrue(analysis.getNames().contains("Hypnodendron spininervium archeri"));
    }

    @Test
    public void processAdditionalScientificNames4() throws Exception {
        Analysis analysis = new Analysis("Cymbiola pulchra cracenta (McMichael, 1963)", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, null);
        this.analyser.processAdditionalScientificNames(analysis);
        assertNotNull(analysis.getNames());
        assertEquals(2, analysis.getNames().size());
        assertTrue(analysis.getNames().contains("Cymbiola pulchra cracenta (McMichael, 1963)"));
        assertTrue(analysis.getNames().contains("Cymbiola pulchra cracenta"));
    }

    @Test
    public void processAdditionalScientificNames5() throws Exception {
        Analysis analysis = new Analysis("Viola sp. Lamington NP (R.Schodde 1153) Qld Herbarium", null, null, null);
        this.analyser.parseName(analysis, null);
        assertNotNull(analysis.getParsedName());
        this.analyser.processParsedScientificName(analysis, null);
        this.analyser.processAdditionalScientificNames(analysis);
        assertNotNull(analysis.getNames());
        assertEquals(3, analysis.getNames().size());
        assertTrue(analysis.getNames().contains("Viola sp. Lamington NP (R.Schodde 1153) Qld Herbarium"));
        assertTrue(analysis.getNames().contains("Viola sp. Lamington NP (R.Schodde 1153)"));
        assertTrue(analysis.getNames().contains("Viola sp. Lamington NP"));
    }
}