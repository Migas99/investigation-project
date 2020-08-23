package Application.Repository;

import org.neo4j.driver.*;
import java.util.*;

/**
 *
 */
public class Upload {

    public static void uploadToDatabase(Driver driver, String query, Map<String, Object> parameters) {
        try (Session session = driver.session()) {
            System.out.println("[SERVER] Processing the query ...");
            session.writeTransaction(tx -> tx.run(query, parameters));
            System.out.println("[SERVER] Merging entities ...");
            session.writeTransaction(tx -> tx.run(mergeCompanies()));
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
