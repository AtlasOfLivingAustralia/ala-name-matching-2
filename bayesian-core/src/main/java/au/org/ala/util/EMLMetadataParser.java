package au.org.ala.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.xml.sax.SAXException;

import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.text.ParseException;

public class EMLMetadataParser {
    private Templates templates;

    public EMLMetadataParser() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource ss = new StreamSource(this.getClass().getResourceAsStream("eml-metadata.xslt"));
        this.templates = factory.newTemplates(ss);
    }

    /**
     * Parse an EML XML document
     *
     * @param source The document source
     * @param metadata A metadata object containing defaults. If null a default is used
     *
     * @return The resulting metadata
     */
    public Metadata parse(URL source, Metadata metadata) throws XPathExpressionException, IOException, SAXException, ParseException, TransformerException {
        StreamSource eml = new StreamSource(source.openStream());
        StringWriter json = new StringWriter();
        StreamResult result = new StreamResult(json);
        this.templates.newTransformer().transform(eml, result);
        ObjectMapper mapper = JsonUtils.createMapper();
        Metadata md = mapper.readValue(json.toString(), Metadata.class);
        return metadata == null ? md : metadata.with(md);
    }
}
