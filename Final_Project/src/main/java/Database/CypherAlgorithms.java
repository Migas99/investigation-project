package Database;

import Enumerations.Entities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.*;

public class CypherAlgorithms {

    public static LinkedList<Map<String, Object>> louvainAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.louvain.stream(" + getAnonymousGraphProjectionWithCompaniesAndTransactions() + ")\n"
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
                    + "CALL gds.localClusteringCoefficient.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthers() + ")\n"
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

    public static LinkedList<Map<String, Object>> pageRankAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.pageRank.stream(" + getAnonymousGraphProjectionWithCompaniesRelatedToOthers() + ")\n"
                    + "YIELD nodeId, score\n"
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c) = nodeId\n"
                    + "WITH c.CompanyName AS Company, score AS Score\n"
                    + "RETURN Company, Score\n"
                    + "ORDER BY Score DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    public static LinkedList<Map<String, Object>> nodeSimilarityAlgorithm(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "CALL gds.nodeSimilarity.stream(" + getAnonymousGraphProjectionWithCompaniesInformation() + ")\n"
                    + "YIELD node1, node2, similarity\n"
                    + "MATCH (c1:" + Entities.Labels.Company + "),(c2:" + Entities.Labels.Company + ")\n"
                    + "WHERE ID(c1) = node1 AND ID(c2) = node2\n"
                    + "WITH c1.CompanyName AS CompanyOne, c2.CompanyName AS CompanyTwo, similarity AS Similarity\n"
                    + "RETURN CompanyOne, CompanyTwo, Similarity\n"
                    + "ORDER BY Similarity DESC\n"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    private static String getAnonymousGraphProjectionWithCompaniesInformation() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (ci:" + Entities.Labels.CompanyInfo + ")\n"
                + "RETURN ID(ci) AS id\n"
                + "UNION\n"
                + "MATCH (ca:" + Entities.Labels.CompanyAddress + ")\n"
                + "RETURN ID(ca) AS id\n"
                + "UNION\n"
                + "MATCH (cc:" + Entities.Labels.CompanyContact + ")\n"
                + "RETURN ID(cc) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_COMPANY_ID + "|"
                + Entities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER + "|"
                + Entities.CompanyRelationships.HAS_BUSINESS_NAME
                + "]->(ci:" + Entities.Labels.CompanyInfo + ")\n"
                + "RETURN ID(c) AS source, ID(ci) AS target, type(r) AS type\n"
                + "UNION\n"
                + "MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_BUILDING_NUMBER + "|"
                + Entities.CompanyRelationships.HAS_STREET_NAME + "|"
                + Entities.CompanyRelationships.HAS_ADDRESS_DETAIL + "|"
                + Entities.CompanyRelationships.HAS_CITY + "|"
                + Entities.CompanyRelationships.HAS_POSTAL_CODE + "|"
                + Entities.CompanyRelationships.HAS_REGION + "|"
                + Entities.CompanyRelationships.HAS_COUNTRY
                + "]->(ca:" + Entities.Labels.CompanyAddress + ")\n"
                + "RETURN ID(c) AS source, ID(ca) AS target, type(r) AS type\n"
                + "UNION\n"
                + "MATCH (c:" + Entities.Labels.Company + ")-[r:"
                + Entities.CompanyRelationships.HAS_TELEPHONE + "|"
                + Entities.CompanyRelationships.HAS_FAX + "|"
                + Entities.CompanyRelationships.HAS_EMAIL + "|"
                + Entities.CompanyRelationships.HAS_WEBSITE
                + "]->(cc:" + Entities.Labels.CompanyContact + ")\n"
                + "RETURN ID(c) AS source, ID(cc) AS target, type(r) AS type'\n"
                + "}\n";
    }

    private static String getAnonymousGraphProjectionWithCompaniesRelatedToOthers() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_CUSTOMER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target\n"
                + "UNION\n"
                + "MATCH (c1:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.IS_SUPPLIER_OF + "]->(c2:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c1) AS source, ID(c2) AS target'\n"
                + "}\n";
    }

    private static String getAnonymousGraphProjectionWithCompaniesAndTransactions() {
        return "\n{\n"
                + "nodeQuery:\n"
                + "'MATCH (c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(c) AS id\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")\n"
                + "RETURN ID(t) AS id',\n"
                + "relationshipQuery:\n"
                + "'MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_BUYER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target\n"
                + "UNION\n"
                + "MATCH (t:" + Entities.Labels.Transaction + ")-[:" + Entities.TransactionRelationships.HAS_SELLER + "]->(c:" + Entities.Labels.Company + ")\n"
                + "RETURN ID(t) AS source, ID(c) AS target'\n"
                + "}\n";
    }

    private static String getAnonymousGraphProjectionWithCompaniesAndInvoiceSales() {
        return "\n{\n"
                + "nodeQuery:\n"
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
                + "RETURN ID(i) AS source, ID(c) AS target'\n"
                + "}\n";
    }
}
