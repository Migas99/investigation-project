package Mapper;

import Enumerations.Entities;

import java.util.Iterator;

public class QueryConstructor {

    private String uploadQuery;
    private int count;

    public QueryConstructor() {
        this.uploadQuery = "";
        this.count = 0;
    }

    public String getUploadQuery() {
        return this.uploadQuery;
    }

    private void addToQuery(String extra) {
        this.uploadQuery = this.uploadQuery + " " + extra;
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

    public void MERGE(String identity, String property, String propertyValue) {

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

    public void RELATIONSHIP_TYPE_OF(String identifier, String label) {
        this.addToQuery("CREATE (" + identifier + ")-[:" + Entities.OtherRelationships.TYPE_OF + "]->(identity" + this.count + ":" + label + ")");
        this.count++;
    }

}
