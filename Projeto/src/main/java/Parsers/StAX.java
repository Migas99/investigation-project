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
    private final JTextArea displayOutput;
    private double percentage;

    public StAX(Neo4j driver) {
        this.driver = driver;
        this.mapper = new MapperManager(this.driver);
        this.displayOutput = null;
        this.percentage = 0;
    }

    public StAX(Neo4j driver, JTextArea displayOutput) {
        this.driver = driver;
        this.mapper = new MapperManager(this.driver);
        this.displayOutput = displayOutput;
        this.percentage = 0;
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
                    this.setPercentageDone(current, XMLFileName);
                    //System.out.println("StartElement: " + current);
                }

                if (nextEvent.isEndElement()) {
                    EndElement endElement = nextEvent.asEndElement();
                    this.mapper.processEndElement(endElement.getName().getLocalPart());
                    isTrash = true;
                    this.setPercentageDone(current, XMLFileName);
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
                        this.setPercentageDone(current, XMLFileName);
                    }
                }
            }

            this.driver.close();
        } catch (FileNotFoundException | XMLStreamException e) {
            System.err.println(e.getMessage());
        }
    }

    private double getSizeOfFile(String XMLFile) {
        return new File(XMLFile).length();
    }

    private double getSizeOfString(String info) {
        return info.getBytes(StandardCharsets.UTF_8).length;
    }

    private void setPercentageDone(String info, String file) {
        double percentageDone = ((this.getSizeOfString(info) / this.getSizeOfFile(file)) * 100);
        this.percentage = this.percentage + percentageDone;
        this.displayOutput.setText("Importing SAF-T to database ... " + (int)this.percentage + "%");
    }
}

