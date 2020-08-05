package Database;

import Enumerations.Entities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.*;

public class CypherAlgorithms {

    private static final String graphName = "MyGraph";

    public static LinkedList<Map<String, Object>> louvainAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.louvain.stream({" + getGraphProjectionWithInvoiceSales() + "})\n"
                    + "YIELD nodeId, communityId\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH DISTINCT(communityId) AS CommunityNumber, collect(c.CompanyName) AS Companies\n"
                    + "RETURN CommunityNumber, Companies\n"
                    + "ORDER BY CommunityNumber\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    public static LinkedList<Map<String, Object>> localClusteringAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.localClusteringCoefficient.stream({" + getGraphProjectionWithInvoiceSales() + "})\n"
                    + "YIELD nodeId, localClusteringCoefficient\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH c.CompanyName AS Company, localClusteringCoefficient AS Coefficient\n"
                    + "RETURN Company, Coefficient\n"
                    + "ORDER BY Coefficient DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    private static String getGraphProjectionWithInvoiceSales() {
        return "\nnodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (i:" + Entities.Labels.Invoice + ")\n"
                + "RETURN ID(i) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(i) AS source, ID(c) AS target\n"
                + "UNION\n"
                + "MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(i) AS source, ID(c) AS target'\n";
    }

    private static void createGraphProjection(Driver driver) {
        try (Session session = Neo4jConnector.getDriver().session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.graph.create.cypher(\n"
                    + "'" + graphName + "',\n"
                    + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "RETURN ID(c) AS id\n"
                    + "UNION\n"
                    + "MATCH (i:" + Entities.Labels.Invoice + ")\n"
                    + "RETURN ID(i) AS id',\n"
                    + "'MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                    + "RETURN ID(i) AS source, ID(c) AS target\n"
                    + "UNION\n"
                    + "MATCH (i:" + Entities.Labels.Invoice + ")-[:" + Entities.InvoiceRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                    + "RETURN ID(i) AS source, ID(c) AS target'\n"
                    + ")"
            ));
        }
    }

    private static void deleteGraphProjection(Driver driver) {
        try (Session session = Neo4jConnector.getDriver().session()) {
            session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.graph.drop('" + graphName + "') YIELD graphName"
            ));
        }
    }
}
