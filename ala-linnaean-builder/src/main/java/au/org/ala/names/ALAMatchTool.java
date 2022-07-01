package au.org.ala.names;

import au.org.ala.bayesian.ClassificationMatcherConfiguration;
import au.org.ala.bayesian.ClassifierSearcher;
import au.org.ala.bayesian.MatchMeasurement;
import au.org.ala.bayesian.MatchTool;
import au.org.ala.names.lucene.LuceneClassifierSearcher;
import au.org.ala.names.lucene.LuceneClassifierSearcherConfiguration;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;
import java.util.List;

public class ALAMatchTool extends MatchTool<AlaLinnaeanClassification, AlaLinnaeanInferencer, AlaLinnaeanFactory, MatchMeasurement> {
    private final CSVReader source;
    private final CSVWriter report;

    public ALAMatchTool(ClassifierSearcher<?> searcher, ClassificationMatcherConfiguration config, boolean instrument, Reader input, Writer output) {
        super(AlaLinnaeanFactory.instance(), searcher, config, instrument);
        this.source = new CSVReader(input);
        this.report = new CSVWriter(output);
    }

    /**
     * Close this tool
     *
     * @throws Exception if this resource cannot be closed
     */
    @Override
    public void close() throws Exception {
        this.report.close();
        this.source.close();
        super.close();
    }

    /**
     * Write the headers.
     * <p>
     * By default, this writes to the logger.
     * Do something sensible with it for a proper tool.
     * </p>
     */
    @Override
    public void writeHeader() {
        this.writeValues(this.headers);
    }

    /**
     * Write the list of values for the result as a CSV
     *
     * @param values The log values
     */
    @Override
    protected void writeValues(List<Object> values) {
        String[] row = new String[values.size()];
        for (int i = 0; i < row.length; i++) {
            Object v = values.get(i);
            row[i] = v == null ? null : v.toString();
        }
        this.report.writeNext(row);
    }

    /**
     * Read the next row of input values.
     *
     * @return The next row or null for none
     */
    @Override
    protected String[] nextRow() throws CsvValidationException, IOException {
        return this.source.readNext();
    }

    public static void main(String[] arguments) throws Exception {
        Args args = new Args();
        JCommander.newBuilder().addObject(args).args(arguments).build();
        LuceneClassifierSearcherConfiguration sConfig = LuceneClassifierSearcherConfiguration.builder()
                .build();
        File index = new File(args.index);
        LuceneClassifierSearcher searcher = new LuceneClassifierSearcher(index, sConfig, AlaLinnaeanFactory.taxonId);
        Reader input;
        if (args.files.get(0).equals("-"))
            input = new InputStreamReader(System.in);
        else
            input = new FileReader(args.files.get(0));
        Writer output;
        if (args.files.get(1).equals("-"))
            output = new OutputStreamWriter(System.out);
        else
            output = new FileWriter(args.files.get(1));
        ClassificationMatcherConfiguration cConfig = ClassificationMatcherConfiguration.builder()
                .statistics(args.statistics)
                .build();
        ALAMatchTool tool = new ALAMatchTool(searcher, cConfig, args.measure, input, output);
        tool.run();
    }

    public static class Args {
        @Parameter(names = "-i", description = "The index source", required = true)
        private String index;
        @Parameter(names = "-m", description = "Measure matching performance")
        private boolean measure = false;
        @Parameter(names = "-s", description = "Collect match statistics")
        private boolean statistics = false;
        @Parameter(description = "The input and output CSVs, with - meaning standard input/output", arity = 2, required = true)
        private List<String> files;
    }

}
