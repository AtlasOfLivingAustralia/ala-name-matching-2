package au.org.ala.names.generated;

import au.org.ala.names.builder.IndexBuilder;
import au.org.ala.names.builder.IndexBuilderConfiguration;
import au.org.ala.names.builder.Source;
import org.apache.commons.cli.*;

import java.io.File;
import java.net.URL;



public class GrassCli {
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
       config.setFactoryClass(GrassFactory.class);
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
       source = Source.create(in, builder.getNetwork().getObservables());
       builder.load(source);
       source.close();
     }
     builder.build();
     builder.buildIndex(output);
     builder.close();
   }
}
