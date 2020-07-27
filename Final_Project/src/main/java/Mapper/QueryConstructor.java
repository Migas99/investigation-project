package Mapper;

import java.util.HashMap;
import java.util.Map;

public class QueryConstructor {

    private final Map<String, Object> parameters;
    private final StringBuilder mergeComponent;
    private final StringBuilder createComponent;

    private int nodeCount;
    private int parameterCount;

    public QueryConstructor() {
        this.parameters = new HashMap<>();
        this.mergeComponent = new StringBuilder();
        this.createComponent = new StringBuilder();
        this.nodeCount = 0;
        this.parameterCount = 0;
    }

    public String getUploadQuery() {
        String query = this.mergeComponent.append(this.createComponent).toString();

        System.out.println(query);

        return query;
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    private void addToMergeComponent(String query) {
        this.mergeComponent.append("\n").append(query);
    }

    private void addToCreateComponent(String query) {
        this.createComponent.append("\n").append(query);
    }

    private String newIdentifier() {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        return identifier;
    }

    public String CREATE() {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + ")");

        return identifier;
    }

    public String CREATE(String label) {
        String identifier = this.newIdentifier();

        this.addToCreateComponent("CREATE (" + identifier + ":" + label + ")");

        return identifier;
    }

    public String CREATE(String property, int propertyValue) {
        String node = this.newIdentifier();

        this.addToCreateComponent("CREATE (" + node + " { " + property + ": " + propertyValue + " }" + ")");

        return node;
    }

    public String CREATE(String property, double propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public String CREATE(String property, String propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")");

        return identifier;
    }

    public String CREATE(String label, String property, int propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + ":" + label + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public String CREATE(String label, String property, double propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + ":" + label + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public String CREATE(String label, String property, String propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToCreateComponent("CREATE (" + identifier + ":" + label + " { " + property + ": '" + propertyValue + "' }" + ")");

        return identifier;
    }

    public String MERGE(String property, int propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public String MERGE(String property, double propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public String MERGE(String property, String propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")");

        return identifier;
    }

    public String MERGE(String label, String property, int propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + ":" + label + " { " + property + ": " + propertyValue + " })");

        return identifier;
    }

    public String MERGE(String label, String property, double propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + ":" + label + " { " + property + ": " + propertyValue + " })");

        return identifier;
    }

    public String MERGE(String label, String property, String propertyValue) {
        String identifier = "n" + this.nodeCount;
        this.nodeCount++;

        this.addToMergeComponent("MERGE (" + identifier + ":" + label + " { " + property + ": '" + propertyValue + "' })");

        return identifier;
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

    public void RELATIONSHIP(String identifier1, String identifier2, String relationship) {
        this.addToCreateComponent("CREATE (" + identifier1 + ")-[:" + relationship + "]->(" + identifier2 + ")");
    }

    public void MERGE_RELATIONSHIP(String identifier1, String identifier2, String relationship) {
        this.addToMergeComponent("MERGE (" + identifier1 + ")-[:" + relationship + "]->(" + identifier2 + ")");
    }
}
