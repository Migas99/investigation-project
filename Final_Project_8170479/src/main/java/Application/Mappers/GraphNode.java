package Application.Mappers;

public class GraphNode {
    private long id;
    private String XMLElement;

    public GraphNode(long id, String XMLElement) {
        this.id = id;
        this.XMLElement = XMLElement;
    }

    public long getId() {
        return id;
    }

    public String getXMLElement() {
        return XMLElement;
    }
}
