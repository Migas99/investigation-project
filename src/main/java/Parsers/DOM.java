package Parsers;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class DOM {

    private Document document = null;
    private String root = null;

    public DOM(String XMLFileName) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            this.document = builder.parse(XMLFileName);
            this.root = this.document.getDocumentElement().getNodeName();

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void read() {
        NodeList listOne = this.document.getElementsByTagName(this.root);

        for (int i = 0; i < listOne.getLength(); i++) {
            Node node = listOne.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                NodeList listTwo = element.getChildNodes();

                for (int j = 0; j < listTwo.getLength(); j++) {
                    Node nodeTwo = listTwo.item(j);

                    if (nodeTwo.getNodeType() == Node.ELEMENT_NODE) {
                        Element content = (Element) nodeTwo;
                        System.out.println("[Element: " + content.getNodeName() + "] [Content: " + content.getTextContent() + "]");
                    }

                }

            }

            if(i < listOne.getLength() - 1){
                System.out.println();
            }
        }
    }
}
