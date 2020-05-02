package Interface;

import Database.Neo4j;
import Parsers.StAX;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class Run {

    private static final String XMLFile = "database/SAFTP.XML";
    private static final String XSDFile = "database/SAFTP.XSD";

    private static final String URL = "neo4j://localhost:7687";
    private static final String user = "neo4j";
    private static final String password = "12345";

    public static void main(String[] args) {
        if (validateXML(XMLFile, XSDFile)) {
            System.out.println("Ficheiro válido!");
        } else {
            System.out.println("Ficheiro inválido!");
        }

        Neo4j driver = new Neo4j(URL, user, password);
        StAX parser = new StAX(driver);
        parser.processXMLToNeo4j(XMLFile);
    }

    public static boolean validateXML(String XMLFile, String XSDFile) {
        try {
            XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(new FileInputStream(XMLFile));
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(XSDFile));

            Validator validator = schema.newValidator();
            validator.validate(new StAXSource(reader));

        } catch (IOException | SAXException | XMLStreamException e) {
            System.out.println(e.getMessage());
            return false;
        }

        return true;
    }
}