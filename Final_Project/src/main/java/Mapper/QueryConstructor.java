package Mapper;

public class QueryConstructor {

    private final StringBuilder mergeComponent;
    private final StringBuilder createComponent;
    private int nodeCount;

    public QueryConstructor() {
        this.mergeComponent = new StringBuilder();
        this.createComponent = new StringBuilder();
        this.nodeCount = 0;
    }

    public String getUploadQuery() {
        String query = this.mergeComponent.append(this.createComponent).toString();
        System.out.println(query);
        return this.createComponent.toString();
    }

    private void addToMergeComponent(String query) {
        this.mergeComponent.append("\n").append(query);
    }

    private void addToCreateComponent(String query) {
        this.createComponent.append("\n").append(query);
    }

    private String newNode() {
        String node = "n" + this.nodeCount;
        this.nodeCount++;
        return node;
    }

    public String CREATE() {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + ")");
        return node;
    }

    public String CREATE(String label) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + ":" + label + ")");
        return node;
    }

    public String CREATE(String property, int propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String CREATE(String property, double propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String CREATE(String property, String propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + " { " + property + ": '" + propertyValue + "' }" + ")");
        return node;
    }

    public String CREATE(String label, String property, int propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + ":" + label + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String CREATE(String label, String property, double propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + ":" + label + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String CREATE(String label, String property, String propertyValue) {
        String node = this.newNode();
        this.addToCreateComponent("CREATE (" + node + ":" + label + " { " + property + ": '" + propertyValue + "' }" + ")");
        return node;
    }

    public String MERGE(String property, int propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String MERGE(String property, double propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + " { " + property + ": " + propertyValue + " }" + ")");
        return node;
    }

    public String MERGE(String property, String propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + " { " + property + ": '" + propertyValue + "' }" + ")");
        return node;
    }

    public String MERGE(String label, String property, int propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + ":" + label + " { " + property + ": " + propertyValue + " })");
        return node;
    }

    public String MERGE(String label, String property, double propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + ":" + label + " { " + property + ": " + propertyValue + " })");
        return node;
    }

    public String MERGE(String label, String property, String propertyValue) {
        String node = this.newNode();
        this.addToMergeComponent("MERGE (" + node + ":" + label + " { " + property + ": '" + propertyValue + "' })");
        return node;
    }

    public void SET_LABEL(String identifier, String label) {
        this.addToCreateComponent("SET " + identifier + ":" + label);
    }

    public void SET_PROPERTY(String identifier, String property, int propertyValue) {
        this.addToCreateComponent("SET " + identifier + "." + property + " = " + propertyValue);
    }

    public void SET_PROPERTY(String identifier, String property, double propertyValue) {
        this.addToCreateComponent("SET " + identifier + "." + property + " = " + propertyValue);
    }

    public void SET_PROPERTY(String identifier, String property, String propertyValue) {
        this.addToCreateComponent("SET " + identifier + "." + property + " = '" + propertyValue + "'");
    }

    public void CREATE_RELATIONSHIP(String from, String target, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->(" + target + ")");
    }

    public void MERGE_RELATIONSHIP(String from, String target, String relationship) {
        this.addToMergeComponent("MERGE (" + from + ")-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, int value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, double value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, String value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": '" + value + "'})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, int value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, double value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, String value, String relationship) {
        this.addToCreateComponent("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": '" + value + "'})");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, int value, String target, String relationship) {
        this.addToCreateComponent("CREATE ({" + property + ": " + value + "})-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, double value, String target, String relationship) {
        this.addToCreateComponent("CREATE ({" + property + ": " + value + "})-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, String value, String target, String relationship) {
        this.addToCreateComponent("CREATE ({" + property + ": '" + value + "'})-[:" + relationship + "]->(" + target + ")");
    }

    public void MERGE_AND_RELATE_TO_RIGHT(String from, String property, String value, String relationship) {
        this.addToMergeComponent("MERGE (" + from + ")-[:" + relationship + "]->({" + property + ": '" + value + "'})");
    }

    public void MERGE_AND_RELATE_TO_RIGHT(String from, String label, String property, String value, String relationship) {
        this.addToMergeComponent("MERGE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": '" + value + "'})");
    }
}
