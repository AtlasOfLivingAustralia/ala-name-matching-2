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


public class GrassBuilder extends Builder<GrassParameters> {

  public GrassBuilder() {
  }

  @Override
  public void infer(Document document) {
  }

    @Override
    public void expand(Document document, Deque<Document> parents) {
  }

  @Override
  public GrassParameters createParameters() {
      return new GrassParameters();
  }

  @Override
  public void calculate(GrassParameters parameters, ParameterAnalyser<Document> analyser, Document document) throws InferenceException {
    parameters.prior_t$rain = analyser.computePrior(analyser.getObservation(true, "rain", document));
    parameters.inf_t_t$sprinkler = analyser.computeConditional(analyser.getObservation(true, "sprinkler", document) , analyser.getObservation(true, "rain", document));
    parameters.inf_t_f$sprinkler = analyser.computeConditional(analyser.getObservation(true, "sprinkler", document) , analyser.getObservation(false, "rain", document));
    parameters.inf_t_tt$wet = analyser.computeConditional(analyser.getObservation(true, "wet", document) , analyser.getObservation(true, "rain", document), analyser.getObservation(true, "sprinkler", document));
    parameters.inf_t_tf$wet = analyser.computeConditional(analyser.getObservation(true, "wet", document) , analyser.getObservation(true, "rain", document), analyser.getObservation(false, "sprinkler", document));
    parameters.inf_t_ft$wet = analyser.computeConditional(analyser.getObservation(true, "wet", document) , analyser.getObservation(false, "rain", document), analyser.getObservation(true, "sprinkler", document));
    parameters.inf_t_ff$wet = analyser.computeConditional(analyser.getObservation(true, "wet", document) , analyser.getObservation(false, "rain", document), analyser.getObservation(false, "sprinkler", document));
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
       config.setBuilderClass(GrassBuilder.class);
       config.setNetwork(GrassBuilder.class.getResource("GrassBuilder.json"));
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
