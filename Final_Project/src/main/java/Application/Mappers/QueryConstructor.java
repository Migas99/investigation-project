package Application.Mappers;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que contêm métodos que possibilitam a construção de um query de upload
 */
public class QueryConstructor {

    private final StringBuilder uploadQuery;
    private final Map<String, Object> parameters;

    private int nodeCount;
    private int parameterCount;

    public QueryConstructor() {
        this.uploadQuery = new StringBuilder();
        this.parameters = new HashMap<>();
        this.nodeCount = 0;
        this.parameterCount = 0;
    }

    public String getUploadQuery() {
        return this.uploadQuery.toString();
    }

    public Map<String, Object> getParameters(){
        return this.parameters;
    }

    private void addToUploadQuery(String query) {
        this.uploadQuery.append("\n").append(query);
    }

    private String newNode() {
        String node = "n" + this.nodeCount;
        this.nodeCount++;
        return node;
    }

    private String newParameter(int value) {
        String parameter = "p" + this.parameterCount;
        this.parameterCount++;

        this.parameters.put(parameter, value);

        return "$" + parameter;
    }

    private String newParameter(double value) {
        String parameter = "p" + this.parameterCount;
        this.parameterCount++;

        this.parameters.put(parameter, value);

        return "$" + parameter;
    }

    private String newParameter(String value) {
        String parameter = "p" + this.parameterCount;
        this.parameterCount++;

        this.parameters.put(parameter, value);

        return "$" + parameter;
    }

    public String CREATE() {
        String node = this.newNode();
        this.addToUploadQuery("CREATE (" + node + ")");
        return node;
    }

    public String CREATE(String label) {
        String node = this.newNode();
        this.addToUploadQuery("CREATE (" + node + ":" + label + ")");
        return node;
    }

    public String CREATE(String property, int propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String CREATE(String property, double propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String CREATE(String property, String propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String CREATE(String label, String property, int propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + ":" + label + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String CREATE(String label, String property, double propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + ":" + label + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String CREATE(String label, String property, String propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("CREATE (" + node + ":" + label + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String MERGE(String property, int propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String MERGE(String property, double propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String MERGE(String property, String propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + " { " + property + ": " + parameter + " }" + ")");
        return node;
    }

    public String MERGE(String label, String property, int propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + ":" + label + " { " + property + ": " + parameter + " })");
        return node;
    }

    public String MERGE(String label, String property, double propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + ":" + label + " { " + property + ": " + parameter + " })");
        return node;
    }

    public String MERGE(String label, String property, String propertyValue) {
        String node = this.newNode();
        String parameter = this.newParameter(propertyValue);

        this.addToUploadQuery("MERGE (" + node + ":" + label + " { " + property + ": " + parameter + " })");
        return node;
    }

    public void SET_LABEL(String identifier, String label) {
        this.addToUploadQuery("SET " + identifier + ":" + label);
    }

    public void SET_PROPERTY(String identifier, String property, int propertyValue) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("SET " + identifier + "." + property + " = " + parameter);
    }

    public void SET_PROPERTY(String identifier, String property, double propertyValue) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("SET " + identifier + "." + property + " = " + parameter);
    }

    public void SET_PROPERTY(String identifier, String property, String propertyValue) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("SET " + identifier + "." + property + " = " + parameter);
    }

    public void CREATE_RELATIONSHIP(String from, String target, String relationship) {
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->(" + target + ")");
    }

    public void MERGE_RELATIONSHIP(String from, String target, String relationship) {
        this.addToUploadQuery("MERGE (" + from + ")-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, int propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, double propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String property, String propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->({" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, int propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, double propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String from, String label, String property, String propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + parameter + "})");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, int propertyValue, String target, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE ({" + property + ": " + parameter + "})-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, double propertyValue, String target, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE ({" + property + ": " + parameter + "})-[:" + relationship + "]->(" + target + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, String propertyValue, String target, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("CREATE ({" + property + ": " + parameter + "})-[:" + relationship + "]->(" + target + ")");
    }

    public void MERGE_AND_RELATE_TO_RIGHT(String from, String property, String propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("MERGE (" + from + ")-[:" + relationship + "]->({" + property + ": " + parameter + "})");
    }

    public void MERGE_AND_RELATE_TO_RIGHT(String from, String label, String property, String propertyValue, String relationship) {
        String parameter = this.newParameter(propertyValue);
        this.addToUploadQuery("MERGE (" + from + ")-[:" + relationship + "]->(:" + label + " {" + property + ": " + parameter + "})");
    }
}
