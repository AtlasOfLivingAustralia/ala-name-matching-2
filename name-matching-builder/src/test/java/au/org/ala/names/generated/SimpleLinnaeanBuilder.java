package au.org.ala.names.generated;

import au.org.ala.bayesian.InferenceException;
import au.org.ala.bayesian.ParameterAnalyser;
import au.org.ala.names.builder.Builder;
import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.Source;
import org.apache.commons.cli.*;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Deque;

import au.org.ala.util.TaxonNameSoundEx;

public class SimpleLinnaeanBuilder extends Builder<SimpleLinnaeanParameters> {
  private TaxonNameSoundEx soundex;

  public SimpleLinnaeanBuilder() {
    this.soundex = new TaxonNameSoundEx();
  }

  @Override
  public void infer(Document document) {
    for (String v: document.getValues("dwc_scientificName")) {
      String d = this.soundex.soundEx(v);
      document.add(new StringField("ala_soundexScientificName", d, Store.YES));
    }
  }

    @Override
    public void expand(Document document, Deque<Document> parents) {
    Document d_6 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "genus".equals(x))) {
        d_6 = d;
        break;
      }
    }
    if (d_6 != null){
      for(String v: d_6.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_genus" ,d,Store.YES));
      }
    }
    Document d_7 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "family".equals(x))) {
        d_7 = d;
        break;
      }
    }
    if (d_7 != null){
      for(String v: d_7.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_family" ,d,Store.YES));
      }
    }
    Document d_8 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "order".equals(x))) {
        d_8 = d;
        break;
      }
    }
    if (d_8 != null){
      for(String v: d_8.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_order" ,d,Store.YES));
      }
    }
    Document d_9 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "class".equals(x))) {
        d_9 = d;
        break;
      }
    }
    if (d_9 != null){
      for(String v: d_9.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_class" ,d,Store.YES));
      }
    }
    Document d_10 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "phylum".equals(x))) {
        d_10 = d;
        break;
      }
    }
    if (d_10 != null){
      for(String v: d_10.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_phylum" ,d,Store.YES));
      }
    }
    Document d_11 = null;
    for (Document d: parents) {
      if (Arrays.stream(d.getValues("dwc_taxonRank")).anyMatch(x -> "kingdom".equals(x))) {
        d_11 = d;
        break;
      }
    }
    if (d_11 != null){
      for(String v: d_11.getValues("dwc_scientificName")){
        String d= v;
        document.add(new StringField("dwc_kingdom" ,d,Store.YES));
      }
    }
  }

  @Override
  public SimpleLinnaeanParameters createParameters() {
      return new SimpleLinnaeanParameters();
  }

  @Override
  public void calculate(SimpleLinnaeanParameters parameters, ParameterAnalyser<Document> analyser, Document document) throws InferenceException {
    parameters.prior_t$taxonID = analyser.computePrior(analyser.getObservation(true, "taxonID", document));
    parameters.inf_t_t$taxonRank = analyser.computeConditional(analyser.getObservation(true, "taxonRank", document) , analyser.getObservation(true, "taxonID", document));
    parameters.inf_t_f$taxonRank = analyser.computeConditional(analyser.getObservation(true, "taxonRank", document) , analyser.getObservation(false, "taxonID", document));
    parameters.inf_t_t$specificEpithet = analyser.computeConditional(analyser.getObservation(true, "specificEpithet", document) , analyser.getObservation(true, "taxonID", document));
    parameters.inf_t_f$specificEpithet = analyser.computeConditional(analyser.getObservation(true, "specificEpithet", document) , analyser.getObservation(false, "taxonID", document));
    parameters.inf_t_t$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, "scientificNameAuthorship", document) , analyser.getObservation(true, "taxonID", document));
    parameters.inf_t_f$scientificNameAuthorship = analyser.computeConditional(analyser.getObservation(true, "scientificNameAuthorship", document) , analyser.getObservation(false, "taxonID", document));
    parameters.inf_t_tt$scientificName = analyser.computeConditional(analyser.getObservation(true, "scientificName", document) , analyser.getObservation(true, "taxonID", document), analyser.getObservation(true, "specificEpithet", document));
    parameters.inf_t_tf$scientificName = analyser.computeConditional(analyser.getObservation(true, "scientificName", document) , analyser.getObservation(true, "taxonID", document), analyser.getObservation(false, "specificEpithet", document));
    parameters.inf_t_ft$scientificName = analyser.computeConditional(analyser.getObservation(true, "scientificName", document) , analyser.getObservation(false, "taxonID", document), analyser.getObservation(true, "specificEpithet", document));
    parameters.inf_t_ff$scientificName = analyser.computeConditional(analyser.getObservation(true, "scientificName", document) , analyser.getObservation(false, "taxonID", document), analyser.getObservation(false, "specificEpithet", document));
    parameters.inf_t_t$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, "soundexScientificName", document) , analyser.getObservation(true, "scientificName", document));
    parameters.inf_t_f$soundexScientificName = analyser.computeConditional(analyser.getObservation(true, "soundexScientificName", document) , analyser.getObservation(false, "scientificName", document));
    parameters.inf_t_tt$genus = analyser.computeConditional(analyser.getObservation(true, "genus", document) , analyser.getObservation(true, "scientificName", document), analyser.getObservation(true, "soundexScientificName", document));
    parameters.inf_t_tf$genus = analyser.computeConditional(analyser.getObservation(true, "genus", document) , analyser.getObservation(true, "scientificName", document), analyser.getObservation(false, "soundexScientificName", document));
    parameters.inf_t_ft$genus = analyser.computeConditional(analyser.getObservation(true, "genus", document) , analyser.getObservation(false, "scientificName", document), analyser.getObservation(true, "soundexScientificName", document));
    parameters.inf_t_ff$genus = analyser.computeConditional(analyser.getObservation(true, "genus", document) , analyser.getObservation(false, "scientificName", document), analyser.getObservation(false, "soundexScientificName", document));
    parameters.inf_t_t$family = analyser.computeConditional(analyser.getObservation(true, "family", document) , analyser.getObservation(true, "genus", document));
    parameters.inf_t_f$family = analyser.computeConditional(analyser.getObservation(true, "family", document) , analyser.getObservation(false, "genus", document));
    parameters.inf_t_t$order = analyser.computeConditional(analyser.getObservation(true, "order", document) , analyser.getObservation(true, "family", document));
    parameters.inf_t_f$order = analyser.computeConditional(analyser.getObservation(true, "order", document) , analyser.getObservation(false, "family", document));
    parameters.inf_t_t$class = analyser.computeConditional(analyser.getObservation(true, "class", document) , analyser.getObservation(true, "order", document));
    parameters.inf_t_f$class = analyser.computeConditional(analyser.getObservation(true, "class", document) , analyser.getObservation(false, "order", document));
    parameters.inf_t_t$phylum = analyser.computeConditional(analyser.getObservation(true, "phylum", document) , analyser.getObservation(true, "class", document));
    parameters.inf_t_f$phylum = analyser.computeConditional(analyser.getObservation(true, "phylum", document) , analyser.getObservation(false, "class", document));
    parameters.inf_t_t$kingdom = analyser.computeConditional(analyser.getObservation(true, "kingdom", document) , analyser.getObservation(true, "phylum", document));
    parameters.inf_t_f$kingdom = analyser.computeConditional(analyser.getObservation(true, "kingdom", document) , analyser.getObservation(false, "phylum", document));
  }

   public static void main(String[] args) throws Exception {
     Options options = new Options();
     Option configOption = Option.builder("c").longOpt("config").desc("Specify a configuration file").hasArg().argName("URL").type(URL.class).build();
     Option workOption = Option.builder("w").longOpt("work").desc("Working directory").hasArg().argName("DIR").type(File.class).build();
     Option outputOption = Option.builder("o").longOpt("output").desc("Output index directory").hasArg().argName("DIR").type(File.class).build();
     Option helpOption = Option.builder("h").longOpt("help").desc("Print help").build();
     options.addOption(configOption);
     options.addOption(workOption);
     options.addOption(outputOption);
     options.addOption(helpOption);
     IndexBuilderConfiguration config;
     File output;

     CommandLineParser parser = new DefaultParser();
     CommandLine cmd = parser.parse(options, args);

     if (cmd.hasOption(helpOption.getOpt())) {
       HelpFormatter help = new HelpFormatter();
       help.printHelp("java -jar name-matching-builder.jar [OPTIONS] [SOURCES]", options);
       System.exit(0);
     }
     if (cmd.hasOption(configOption.getOpt())) {
       config = IndexBuilderConfiguration.read(((URL) cmd.getParsedOptionValue(configOption.getOpt())));
     } else {
       config = new IndexBuilderConfiguration();
       config.setBuilderClass(SimpleLinnaeanBuilder.class);
       config.setNetwork(SimpleLinnaeanBuilder.class.getResource("SimpleLinnaeanBuilder.json"));
     }
     if (cmd.hasOption(workOption.getOpt())) {
       config.setWork((File) cmd.getParsedOptionValue(workOption.getOpt()));
     }
     if (cmd.hasOption(outputOption.getOpt())) {
       output = (File) cmd.getParsedOptionValue(outputOption.getOpt());
     } else {
       output = new File(config.getWork(), "output");
     }
     IndexBuilder builder = new IndexBuilder(config);
     for (String input: cmd.getArgs()) {
       URL in = new URL(input);
       Source source = null;
       source = Source.create(in);
       builder.load(source);
       source.close();
     }
     builder.build();
     builder.buildIndex(output);
     builder.close();
   }
}
