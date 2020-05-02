package Parsers;

import Database.Neo4j;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class StAX {

    private final Neo4j driver;

    public StAX(Neo4j driver) {
        this.driver = driver;
    }

    public void processXMLToNeo4j(String XMLFileName) {
        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFileName));

            /**
             * Variàveis de auxílio à criação do grafo
             */
            LinkedList<Long> ids = new LinkedList<>();
            LinkedList<String> nodes = new LinkedList<>();
            String current = null;
            boolean isTrash = true;

            XMLEvent nextEvent;
            while (reader.hasNext()) {
                nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    System.out.println("StartElement: " + nextEvent.asStartElement().getName().getLocalPart());
                    current = nextEvent.asStartElement().getName().getLocalPart();
                    isTrash = false;
                }

                if (nextEvent.isEndElement()) {
                    String endElement = nextEvent.asEndElement().getName().getLocalPart();
                    isTrash = true;
                    System.out.println("EndElement: " + nextEvent.asEndElement().getName().getLocalPart());
                    if (nodes.contains(endElement)) {
                        nodes.remove(endElement);
                        long id = ids.removeLast();

                        if(!ids.isEmpty()) {
                            this.driver.addRelationship(ids.getLast(), id, endElement + "Of");
                        }
                    }
                }

                if (nextEvent.isCharacters()) {
                    Characters characters = nextEvent.asCharacters();

                    /**
                     * Caso não contenha um valor, significa que isto será um
                     * node, caso contrário, será um atributo de um node
                     */
                    if (characters.isWhiteSpace() && !isTrash) {
                        long id = this.driver.addNode(current);
                        nodes.add(current);
                        ids.add(id);
                    } else {
                        if (!characters.isWhiteSpace() && !isTrash) {
                            System.out.println("Characters: " + characters.getData());
                            this.driver.addAttributesToNode(ids.getLast(), current, characters.getData());
                        }
                    }
                }
            }

            this.driver.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.println(e.getMessage());
        }
    }
}

