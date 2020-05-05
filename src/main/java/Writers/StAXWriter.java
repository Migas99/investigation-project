package Writers;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.util.LinkedList;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

/**
 * Nada funciona a 100% yet
 */
public class StAXWriter {

    private static final String XMLFileName = "database/SAFTP.XML";

    public static void main(String[] args) {

        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFileName));

            LinkedList<String> startElements = new LinkedList<>();
            LinkedList<String> endElements = new LinkedList<>();
            LinkedList<String> heads = new LinkedList<>();
            boolean isTrash = false;
            String current = null;

            XMLEvent nextEvent;
            writer.writeStartDocument();
            while (reader.hasNext()) {
                nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    current = nextEvent.asStartElement().getName().getLocalPart();

                    if (startElements.isEmpty()) {
                        System.out.println("StartElement: " + current);
                        startElements.add(current);
                        writer.writeStartElement(current);
                    } else {
                        if (startElements.size() == 1) {
                            System.out.println("StartElement: " + current);
                            heads.add(current);
                            startElements.add(current);
                            writer.writeStartElement(current);
                        } else {
                            if (!startElements.contains(current)) {
                                System.out.println("StartElement: " + current);
                                startElements.add(current);
                                writer.writeStartElement(current);
                            }
                        }
                    }

                    isTrash = false;
                }

                if (nextEvent.isEndElement()) {
                    current = nextEvent.asEndElement().getName().getLocalPart();

                    if (!endElements.contains(current)) {
                        endElements.add(current);
                        writer.writeEndElement();
                        System.out.println("EndElement: " + current);
                    }

                    if (current.equalsIgnoreCase(heads.getLast())) {
                        String lastHead = heads.removeLast();
                        int position = startElements.indexOf(lastHead);

                        while(position < startElements.size()){
                            startElements.removeLast();
                            position++;
                        }

                        position = endElements.indexOf(lastHead);
                        while(position < endElements.size()){
                            endElements.removeLast();
                            position++;
                        }
                    }

                    isTrash = true;
                }

                if(nextEvent.isCharacters()){
                    if(!isTrash){
                        Characters characters = nextEvent.asCharacters();
                        if(characters.isWhiteSpace()){
                            heads.add(current);
                        }
                    }
                }
            }

            writer.writeEndDocument();
            writer.flush();
            writer.close();

            String xmlString = stringWriter.getBuffer().toString();

            stringWriter.close();

            System.out.println(xmlString);

        } catch (XMLStreamException | IOException e) {
            System.err.println(e.getMessage());
        }

    }

    public void keepitsafe(){
        try {
            StringWriter stringWriter = new StringWriter();
            XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = xMLOutputFactory.createXMLStreamWriter(stringWriter);

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFileName));

            LinkedList<String> startElements = new LinkedList<>();
            LinkedList<String> endElements = new LinkedList<>();
            String head = null;
            String current = null;

            XMLEvent nextEvent;
            writer.writeStartDocument();
            while (reader.hasNext()) {
                nextEvent = reader.nextEvent();
                if (nextEvent.isStartElement()) {
                    current = nextEvent.asStartElement().getName().getLocalPart();

                    if (startElements.isEmpty()) {
                        System.out.println("StartElement: " + current);
                        startElements.add(current);
                        writer.writeStartElement(current);
                    } else {
                        if (startElements.size() == 1) {
                            System.out.println("StartElement: " + current);
                            head = current;
                            startElements.add(current);
                            writer.writeStartElement(current);
                        } else {
                            if (!startElements.contains(current)) {
                                System.out.println("StartElement: " + current);
                                startElements.add(current);
                                writer.writeStartElement(current);
                            }
                        }
                    }
                }

                if (nextEvent.isEndElement()) {
                    current = nextEvent.asEndElement().getName().getLocalPart();

                    if (!endElements.contains(current)) {
                        endElements.add(current);
                        writer.writeEndElement();
                        System.out.println("EndElement: " + current);
                    }

                    if (current.equalsIgnoreCase(head)) {
                        String root = startElements.getFirst();
                        startElements = new LinkedList<>();
                        endElements = new LinkedList<>();
                        startElements.add(root);
                    }
                }
            }

            writer.writeEndDocument();
            writer.flush();
            writer.close();

            String xmlString = stringWriter.getBuffer().toString();

            stringWriter.close();

            System.out.println(xmlString);

        } catch (XMLStreamException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

}

