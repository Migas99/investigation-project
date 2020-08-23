package Application.Parsers;

import Application.Enumerations.Elements;
import Application.Mappers.Mapper;
import Application.Mappers.QueryConstructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedList;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

/**
 * Classe que contêm um método responsável pela leitura e processamento do ficheiro.
 */
public class StAX {

    /**
     * Método responsável por realizar a leitura do ficheiro XML e interpretar como o processar.
     *
     * @param XMLFile ficheiro XML
     */
    public static QueryConstructor processXMLToNeo4j(File XMLFile) {

        Mapper mapper = new Mapper(XMLFile.getName());

        try {

            XMLInputFactory factory = XMLInputFactory.newInstance();
            XMLEventReader reader = factory.createXMLEventReader(new FileInputStream(XMLFile.getAbsolutePath()));

            XMLEvent nextEvent;
            String current = null;
            boolean isTrash = false;

            LinkedList<String> sequences = Elements.SequenceElements.getList();

            while (reader.hasNext()) {
                nextEvent = reader.nextEvent();

                if (nextEvent.isStartElement()) {

                    current = nextEvent.asStartElement().getName().getLocalPart();
                    isTrash = false;

                }

                if (nextEvent.isEndElement()) {

                    current = nextEvent.asEndElement().getName().getLocalPart();
                    isTrash = true;

                    if (isSequenceElement(sequences, current)) {
                        mapper.processEndSequence(current);
                    }

                }

                if (nextEvent.isCharacters()) {
                    if (!isTrash) {
                        Characters characters = nextEvent.asCharacters();

                        if (!characters.isWhiteSpace()) {

                            mapper.processElement(current, characters.getData());

                        } else {

                            if (isSequenceElement(sequences, current)) {
                                mapper.processStartSequence(current);
                            }

                        }
                    }
                }

            }

        } catch (FileNotFoundException | XMLStreamException e) {
            e.printStackTrace();
        }

        return mapper.getMap();
    }

    /**
     * Método que verifica se uma dada lista contêm um dado elemento.
     * Este método teve de ser criado devido a alguns problemas com o método
     * contais() do Java Util.
     *
     * @param list    lista de elementos
     * @param element elemento
     * @return se a lista contêm ou não o elemento
     */
    private static boolean isSequenceElement(LinkedList<String> list, String element) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().equalsIgnoreCase(element)) {
                return true;
            }
        }

        return false;
    }

}
