package Mappers;

public class ManageSequence {

    private String XMLElement;
    private int childrenCount;

    public ManageSequence(String XMLElement) {
        this.XMLElement = XMLElement;
        this.childrenCount = 0;
    }

    public void incrementChildren(){
        this.childrenCount++;
    }

    public String getXMLElement() {
        return this.XMLElement;
    }

    public int getChildrenCount() {
        return this.childrenCount;
    }
}
