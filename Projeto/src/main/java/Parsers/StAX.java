package Parsers;

import Database.Neo4j;
import Mappers.MapperManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import javax.swing.*;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class StAX {

    private final Neo4j driver;
    private final MapperManager mapper;

    public StAX(Neo4j driver) {
        this.driver = driver;
        this.mapper = new MapperManager(this.driver);
    }

    public void processXMLToNeo4j(String XMLFileName) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFileName));

            XMLEvent nextEvent;
            String current = null;
            boolean isTrash = false;

            while (reader.hasNext()) {
                nextEvent = reader.nextEvent();

                if (nextEvent.isStartElement()) {
                    StartElement startElement = nextEvent.asStartElement();
                    current = startElement.getName().getLocalPart();
                    isTrash = false;
                    //System.out.println("StartElement: " + current);
                }

                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    this.mapper.processEndElement(endElement.getName().getLocalPart());
                    isTrash = true;
                    //System.out.println("EndElement: " + endElement.getName().getLocalPart());
                }

                if (nextEvent.isCharacters()) {
                    if (!isTrash) {
                        Characters characters = nextEvent.asCharacters();
                        String data = "";

                        if (!characters.isWhiteSpace()) {
                            data = characters.getData();
                        }

                        //System.out.println("Characters: " + data);
                        this.mapper.processStartElement(current, data);
                    }
                }
            }

            this.driver.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.println(e.getMessage());
        }
    }

}

