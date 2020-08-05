package Database;

import Enumerations.Elements;
import Enumerations.Entities;
import org.neo4j.driver.*;

import java.util.*;

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

            List<Record> queryResults = session.writeTransaction(tx -> tx.run(
                    "CALL db.indexes()"
            ).list());

            if (queryResults.isEmpty()) {
                session.writeTransaction(tx -> tx.run(
                        "CREATE INDEX CompanyNameIndex FOR (n:" + Entities.Labels.Company + ") ON (n." + Elements.Header.CompanyName + ")"
                ));
                session.writeTransaction(tx -> tx.run(
                        "CREATE INDEX TaxRegistrationNumberIndex FOR (n:" + Entities.Labels.CompanyInfo + ") ON (n." + Elements.Header.TaxRegistrationNumber + ")"
                ));
            }
        }

        driver.close();
    }

    public static void uploadToDatabase(String query) {
        Driver driver = getDriver();

        try (Session session = driver.session()) {
            System.out.println("[SERVER] Processing the query ...");
            session.writeTransaction(tx -> tx.run(query));
            System.out.println("[SERVER] Merging entities ...");
            session.writeTransaction(tx -> tx.run(mergeCompanies()));
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

    public static boolean isFileNameUnique(String fileName) {
        try (Session session = getDriver().session()) {
            final String name = fileName.substring(0, fileName.length() - 4);

            boolean isUnique = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (file:" + Entities.Labels.File + ")\n"
                    + "WHERE file.FileName = '" + name + "'\n"
                    + "RETURN file"
            ).list().isEmpty());

            if (isUnique) {
                return true;
            }

            return false;
        }
    }

    private static String mergeCompanies() {
        return ""
                + "MATCH (n1:Company),(n2:Company)\n"
                + "WHERE n1.CompanyName = n2.CompanyName AND ID(n1) < ID(n2)\n"
                + "WITH [n1,n2] AS results\n"
                + "CALL apoc.refactor.mergeNodes(results, {properties:'discard', mergeRels:true})\n"
                + "YIELD node RETURN 1\n"

                + "UNION\n"

                + "MATCH (n1:CompanyInfo),(n2:CompanyInfo)\n"
                + "WHERE\n"
                + "(n1.CompanyID = n2.CompanyID OR n1.TaxRegistrationNumber = n2.TaxRegistrationNumber OR n1.BussinessName = n2.BussinessName )\n"
                + "AND ID(n1) < ID(n2)\n"
                + "WITH [n1,n2] AS results\n"
                + "CALL apoc.refactor.mergeNodes(results, {properties:'discard', mergeRels:true})\n"
                + "YIELD node RETURN 1\n"

                + "UNION\n"

                + "MATCH (n1:CompanyContact),(n2:CompanyContact)\n"
                + "WHERE\n"
                + "(n1.Telephone = n2.Telephone OR n1.Fax = n2.Fax OR n1.Email = n2.Email OR n1.Website = n2.Website )\n"
                + "AND ID(n1) < ID(n2)\n"
                + "WITH [n1,n2] AS results\n"
                + "CALL apoc.refactor.mergeNodes(results, {properties:'discard', mergeRels:true})\n"
                + "YIELD node RETURN 1\n"

                + "UNION\n"

                + "MATCH (n1:CompanyAddress), (n2:CompanyAddress)\n"
                + "WHERE\n"
                + "(n1.BuildingNumber = n2.BuildingNumber OR n1.StreetName = n2.StreetName OR n1.AddressDetail = n2.AddressDetail\n"
                + "OR n1.City = n2.City OR n1.PostalCode = n2.PostalCode OR n1.Region = n2.Region OR n1.Country = n2.Country )\n"
                + "AND ID(n1) < id(n2)\n"
                + "WITH [n1,n2] as results\n"
                + "CALL apoc.refactor.mergeNodes(results, {properties:'discard', mergeRels:true})\n"
                + "YIELD node RETURN 1\n";
    }
}
