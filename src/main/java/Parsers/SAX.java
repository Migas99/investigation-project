package Parsers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SAX {

    private DefaultHandler handler = null;
    private SAXParserFactory factory = null;
    private String XMLFileName = null;

    public SAX(String XMLFileName) {
        this.handler = new SAXHandler();
        this.factory = SAXParserFactory.newInstance();
        this.XMLFileName = XMLFileName;
    }

    public void read() {
        try {
            SAXParser parser = this.factory.newSAXParser();
            parser.parse(this.XMLFileName, this.handler);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private class SAXHandler extends DefaultHandler {

        private final Map<String, Boolean> elements = new HashMap<>();

        @Override
        public void startDocument() {
            System.out.println("Start parsing the document!\n");
        }

        @Override
        public void endDocument() {
            System.out.println("\nEnd of parsing the document!");
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) {
            this.elements.put(qName, true);
            System.out.println("Start element: " + qName);
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            this.elements.put(qName, false);
            System.out.println("End element: " + qName);
        }

        @Override
        public void characters(char ch[], int start, int length) {
            String characters = "";

            for (int i = start; i < start + length; i++) {
                characters = characters + ch[i];
            }

            if (!characters.trim().isEmpty()) {
                System.out.println("Value: " + characters);
            }
        }
    }
}
