package Database;

import Enumerations.Elements;
import Enumerations.Entities;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;

public class Neo4jConnector {

    private static final String bolt = "bolt://localhost:7687";
    private static final String username = "neo4j";
    private static final String password = "12345";

    public static Driver getDriver() {
        return GraphDatabase.driver(bolt, AuthTokens.basic(username, password));
    }

    public static void initializeDatabase() {
        Driver driver = getDriver();

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(
                    "CREATE INDEX CompanyNameIndex FOR (n:" + Entities.Labels.Company + ") ON (n." + Elements.Header.CompanyName + ")"
            ));
            session.writeTransaction(tx -> tx.run(
                    "CREATE INDEX TaxRegistrationNumberIndex FOR (n:" + Entities.Labels.CompanyInfo + ") ON (n." + Elements.Header.TaxRegistrationNumber + ")"
            ));
        }

        driver.close();
    }

    public static void uploadToDatabase(String query){
        Driver driver = getDriver();

        try (Session session = driver.session()) {
            System.out.println("[SERVER] Processing the query ...");
            session.writeTransaction(tx -> tx.run(query));
            System.out.println("[SERVER] Merging entities ...");
            session.writeTransaction(tx -> tx.run(CypherQueries.mergeCompanies()));
        }

        driver.close();
    }

    public static void runTransaction(String query) {
        Driver driver = getDriver();

        try (Session session = driver.session()) {
            session.writeTransaction(tx -> tx.run(query));
        }

        driver.close();
    }

    public static void runTransaction(Driver driver, String query) {
        try (Session session = driver.session()) {
            session.run(query);
        }
    }

}
