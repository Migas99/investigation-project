package Application.Repository;

import Application.Enumerations.Entities;
import Application.Enumerations.Elements;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.List;

public class Constraints {

    public static void initializeDatabase(Driver driver) {
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
    }

    public static boolean isFileNameUnique(Driver driver, String fileName) {
        try (Session session = driver.session()) {
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

}
