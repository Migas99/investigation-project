package Application.Parsers;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import Application.DatabaseConnections.Neo4jMapperHelper;
import Application.Mappers.MapperManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAX {

    private final DefaultHandler handler;
    private final SAXParserFactory factory;

    public SAX(Neo4jMapperHelper driver) {
        this.handler = new SAXHandler(driver);
        this.factory = SAXParserFactory.newInstance();
    }

    public void processXMLToNeo4j(String XMLFileName) {
        try {
            SAXParser parser = this.factory.newSAXParser();
            parser.parse(XMLFileName, this.handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    static class SAXHandler extends DefaultHandler {

        private final Neo4jMapperHelper driver;
        private final MapperManager mapper;
        private String current = null;
        private boolean isTrash = false;

        SAXHandler(Neo4jMapperHelper driver) {
            this.driver = driver;
            this.mapper = new MapperManager(this.driver);
        }

        @Override
        public void startDocument() {
            System.out.println("Start parsing the document!\n");
        }

        @Override
        public void endDocument() {
            this.driver.close();
            System.out.println("\nEnd of parsing the document!");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.current = qName;
            this.isTrash = false;
            //System.out.println("Start element: " + qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            this.mapper.processEndElement(qName);
            this.isTrash = true;
            //System.out.println("End element: " + qName);
        }

        @Override
        public void characters(char ch[], int start, int length) {
            if (!this.isTrash) {
                String data = "";

                for (int i = start; i < start + length; i++) {
                    data = data + ch[i];
                }

                this.mapper.processStartElement(current, data);
            }
        }
    }
}
