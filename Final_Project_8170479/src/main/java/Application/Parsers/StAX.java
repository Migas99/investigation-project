package Application.Parsers;

import Application.DatabaseConnections.Neo4jMapperHelper;
import Application.Mappers.MapperManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class StAX {

    private final MapperManager mapper;

    public StAX(Neo4jMapperHelper driver) {
        this.mapper = new MapperManager(driver);
    }

    public void processXMLToNeo4j(File XMLFile) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFile.getAbsolutePath()));

            XMLEvent nextEvent;
            String current = null;
            boolean isTrash = false;

            this.mapper.setFileName(XMLFile.getName());

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

        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.println(e.getMessage());
        }
    }

}

