package Mapper;

import Enumerations.Entities;

import java.util.HashMap;
import java.util.Map;

public class QueryConstructor {

    private final Map<String, String> types;
    private String matchTypesQuery;
    private String mergeQuery;
    private String uploadQuery;
    private int count;

    public QueryConstructor() {
        this.matchTypesQuery = "";
        this.mergeQuery = "";
        this.uploadQuery = "";
        this.count = 0;
        this.types = new HashMap<>();

        for (String type : Entities.EntitiesList.getList()) {
            String identifier = "n" + this.count;
            this.types.put(type, identifier);
            this.matchTypesQuery = this.matchTypesQuery + "MATCH (n" + this.count + ":" + type + ")\n";
            this.count++;
        }

    }

    public String getUploadQuery() {

        this.uploadQuery = this.matchTypesQuery + this.mergeQuery + this.uploadQuery;

        System.out.println(this.uploadQuery);
        return this.uploadQuery;
    }

    private void addToQuery(String extra) {
        this.uploadQuery = this.uploadQuery + "\n" + extra;
    }

    public String CREATE() {
        String identifier = "n" + this.count;
        this.count++;

        this.addToQuery("CREATE (" + identifier + ")");

        return identifier;
    }

    public String CREATE(String property, int propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.addToQuery("CREATE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public void CREATE_RANDOM(String property, int propertyValue) {
        this.addToQuery("CREATE ({ " + property + ": " + propertyValue + " }" + ")");
    }

    public String CREATE(String property, double propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.addToQuery("CREATE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");

        return identifier;
    }

    public void CREATE_RANDOM(String property, double propertyValue) {
        this.addToQuery("CREATE ({ " + property + ": " + propertyValue + " }" + ")");
    }

    public String CREATE(String property, String propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.addToQuery("CREATE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")");

        return identifier;
    }

    public void CREATE_RANDOM(String property, String propertyValue) {
        this.addToQuery("CREATE ({ " + property + ": '" + propertyValue + "' }" + ")");
    }

    public String MERGE(String property, int propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.mergeQuery = this.mergeQuery + "MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")\n";

        return identifier;
    }

    public String MERGE(String property, double propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.mergeQuery = this.mergeQuery + "MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")\n";

        return identifier;
    }

    public String MERGE(String property, String propertyValue) {
        String identifier = "n" + this.count;
        this.count++;

        this.mergeQuery = this.mergeQuery + "MERGE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")\n";

        return identifier;
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String property, int value, String identifier, String relationship) {
        this.addToQuery("CREATE (" + identifier + ")-[:" + relationship + "]->({" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String property, double value, String identifier, String relationship) {
        this.addToQuery("CREATE (" + identifier + ")-[:" + relationship + "]->({" + property + ": " + value + "})");
    }

    public void CREATE_AND_RELATE_TO_RIGHT(String property, String value, String identifier, String relationship) {
        this.addToQuery("CREATE (" + identifier + ")-[:" + relationship + "]->({" + property + ": '" + value + "'})");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, int value, String identifier, String relationship) {
        this.addToQuery("CREATE ({" + property + ": " + value + "})-[:" + relationship + "]->(" + identifier + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, double value, String identifier, String relationship) {
        this.addToQuery("CREATE ({" + property + ": " + value + "})-[:" + relationship + "]->(" + identifier + ")");
    }

    public void CREATE_AND_RELATE_TO_LEFT(String property, String value, String identifier, String relationship) {
        this.addToQuery("CREATE ({" + property + ": '" + value + "'})-[:" + relationship + "]->(" + identifier + ")");
    }

    public void PROPERTY(String identifier, String property, int propertyValue) {
        this.addToQuery("SET " + identifier + "." + property + " = " + propertyValue);
    }

    public void PROPERTY(String identifier, String property, double propertyValue) {
        this.addToQuery("SET " + identifier + "." + property + " = " + propertyValue);
    }

    public void PROPERTY(String identifier, String property, String propertyValue) {
        this.addToQuery("SET " + identifier + "." + property + " = '" + propertyValue + "'");
    }

    public void RELATIONSHIP(String identifier1, String identifier2, String relationship) {
        this.addToQuery("CREATE (" + identifier1 + ")-[:" + relationship + "]->(" + identifier2 + ")");
    }

    public void RELATIONSHIP_WITH_MERGE(String identifier1, String identifier2, String relationship) {
        this.addToQuery("MERGE (" + identifier1 + ")-[:" + relationship + "]->(" + identifier2 + ")");
    }

    public void RELATIONSHIP_TYPE_OF(String identifier, String label) {
        this.addToQuery("CREATE (" + identifier + ")-[:" + Entities.OtherRelationships.TYPE_OF + "]->(" + this.types.get(label) + ")");
    }

}
