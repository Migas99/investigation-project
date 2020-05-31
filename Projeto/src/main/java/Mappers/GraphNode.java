package Mappers;

public class GraphNode {
    private long id;
    private String XMLElement;
    private String value;

    public GraphNode(long id, String XMLElement, String value) {
        this.id = id;
        this.XMLElement = XMLElement;
        this.value = value;
    }

    public GraphNode(long id, String XMLElement) {
        this.id = id;
        this.XMLElement = XMLElement;
        this.value = null;
    }

    public GraphNode(String XMLElement) {
        this.XMLElement = XMLElement;
    }

    public long getId() {
        return id;
    }

    public String getXMLElement() {
        return XMLElement;
    }

    public String getValue() {
        return value;
    }
}
