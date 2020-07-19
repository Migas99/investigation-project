package Mapper;

import Enumerations.Entities;

import java.util.HashMap;
import java.util.Map;

public class QueryConstructor {

    private final Map<String, String> types;
    private String uploadQuery;
    private int count;

    public QueryConstructor() {
        this.uploadQuery = "";
        this.count = 0;
        this.types = new HashMap<>();

        for (String type : Entities.EntitiesList.getList()) {
            String identifier = "n" + this.count;

            this.types.put(type, identifier);

            this.addToQuery("MATCH (n" + this.count + ":" + type + ")");
            this.count++;
        }

    }

    public String getUploadQuery() {
        System.out.println(this.uploadQuery);
        return this.uploadQuery;
    }

    private void addToQuery(String extra) {
        this.uploadQuery = this.uploadQuery + "\n" + extra;
    }

    public String CREATE() {
        String identifier = "n" + this.count;
        this.addToQuery("CREATE (" + identifier + ")");
        this.count++;

        return identifier;
    }

    public String CREATE(String property, int propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("CREATE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");
        this.count++;

        return identifier;
    }

    public String CREATE(String property, double propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("CREATE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");
        this.count++;

        return identifier;
    }


    public String CREATE(String property, String propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("CREATE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")");
        this.count++;

        return identifier;
    }

    public String MERGE(String property, int propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");
        this.count++;

        return identifier;
    }

    public String MERGE(String property, double propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("MERGE (" + identifier + " { " + property + ": " + propertyValue + " }" + ")");
        this.count++;

        return identifier;
    }

    public String MERGE(String property, String propertyValue) {
        String identifier = "n" + this.count;
        this.addToQuery("MERGE (" + identifier + " { " + property + ": '" + propertyValue + "' }" + ")");
        this.count++;

        return identifier;
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
        this.count++;
    }

}
