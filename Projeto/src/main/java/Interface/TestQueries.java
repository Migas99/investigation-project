package Interface;

import Database.Neo4j;
import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.LinkedList;
import java.util.List;

public class TestQueries {

    private static Driver driver;
    private static final String URL = "neo4j://localhost:7687";
    private static final String user = "neo4j";
    private static final String password = "12345";

    public static void main(String[] args) {

        driver = GraphDatabase.driver(URL, AuthTokens.basic(user, password));
        areThereNegativeAmountsInGeneralLedger();
        driver.close();
    }

    private boolean areAllEntitiesIdentified() {
        if (areAllCustomersCompanyNameIdentified() && areAllSuppliersCompanyNameIdentified() && areAllInvoicesAssociatedWithCustomers()) {
            return true;
        }

        return false;
    }

    private static boolean areAllCustomersCompanyNameIdentified() {
        try (Session session = driver.session()) {
            List<Record> companies = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Customer), (a)-[r:HAS_COMPANY]->(c)"
                    + " "
                    //+ "WHERE c.CompanyName = ''"
                    + " "
                    + "RETURN (a),(c)"
            ).list());

            for (int i = 0; i < companies.size(); i++) {
                Node firstNode = companies.get(i).get("a").asNode();
                Node secondNode = companies.get(i).get("c").asNode();

                System.out.println(firstNode.get("CustomerID"));
                System.out.println(secondNode.get("CompanyName"));
            }

            /*if(companies.size() > 0){
                return false;
            }*/

            return true;
        }
    }

    private static boolean areAllSuppliersCompanyNameIdentified() {
        try (Session session = driver.session()) {
            List<Record> companies = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Supplier), (a)-[r:HAS_COMPANY]->(c)"
                    + " "
                    + "WHERE c.CompanyName = ''"
                    + " "
                    + "RETURN (a),(c)"
            ).list());

            for (int i = 0; i < companies.size(); i++) {
                Node firstNode = companies.get(i).get("a").asNode();
                Node secondNode = companies.get(i).get("c").asNode();

                System.out.println(firstNode.get("SupplierID"));
                System.out.println(secondNode.get("CompanyName"));
            }

            if (companies.size() > 0) {
                return false;
            }

            return true;
        }
    }

    private static boolean areAllInvoicesAssociatedWithCustomers() {
        try (Session session = driver.session()) {
            List<Record> companies = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Invoice), (a)-[:HAS_CUSTOMER]->(c), (c)-[:TYPE_OF]->(d:Customer), (c)-[r:HAS_COMPANY]->(e)"
                    + " "
                    //+ "WHERE e.CompanyName = ''"
                    + " "
                    + "RETURN (a),(c),(e)"
            ).list());

            for (int i = 0; i < companies.size(); i++) {
                Node firstNode = companies.get(i).get("a").asNode();
                Node secondNode = companies.get(i).get("c").asNode();
                Node thirdNode = companies.get(i).get("e").asNode();

                String value = thirdNode.get(" ").asString();

                System.out.println(firstNode.get("InvoiceNo"));
                System.out.println(secondNode.get("CustomerID"));
                System.out.println(thirdNode.get("CompanyName"));
            }

            /*if(companies.size() > 0){
                return false;
            }*/

            return true;
        }
    }

    private static void areThereNegativeAmountsInGeneralLedger() {
        try (Session session = driver.session()) {
            List<Record> results = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Transaction), (a)-[:HAS_LINES]->(c), (c)-[:HAS_DEBIT_LINE]->(d), "
                    + "(c)-[:HAS_CREDIT_LINE]->(e), (d)-[:HAS_DEBIT_AMOUNT]->(f), (e)-[:HAS_CREDIT_AMOUNT]->(g)"
                    + " "
                    + "WHERE f.DebitAmount < 0 OR g.CreditAmount < 0"
                    + " "
                    + "RETURN (a),(d),(f),(g)"
            ).list());


            for (int i = 0; i < results.size(); i++) {
                Node firstNode = results.get(i).get("a").asNode();
                Node fourthNode = results.get(i).get("d").asNode();
                Node secondNode = results.get(i).get("f").asNode();
                Node thirdNode = results.get(i).get("g").asNode();

                System.out.println(firstNode.get("TransactionID").asString());
                System.out.println(fourthNode.get("RecordID").asString());
                System.out.println(secondNode.get("DebitAmount"));
                System.out.println(thirdNode.get("CreditAmount"));
            }

            /*if(companies.size() > 0){
                return false;
            }*/

            //return true;
        }
    }

    private static void obtainListOfDaysWithoutSales() {
        try (Session session = driver.session()) {
            List<Record> results = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Transaction), (a)-[:HAS_TRANSACTION_DATE]->(c)"
                    + " "
                    + "RETURN (a),(c)"
            ).list());

            for (int i = 0; i < results.size(); i++) {
                Node firstNode = results.get(i).get("a").asNode();
                Node secondNode = results.get(i).get("c").asNode();

                System.out.println(firstNode.get("TransactionID").asString());
                System.out.println(secondNode.get("DebitAmount"));
            }
        }
    }

    /*protected class processQuery {

        private String transactionID;
        private

        protected processQuery(){

        }



    }*/
}
