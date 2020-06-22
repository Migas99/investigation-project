package Interface;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static org.neo4j.driver.Values.NULL;

public class TestQueries {

    private static Driver driver;
    private static final String URL = "neo4j://localhost:7687";
    private static final String user = "neo4j";
    private static final String password = "12345";

    public static void main(String[] args) throws ParseException {

        driver = GraphDatabase.driver(URL, AuthTokens.basic(user, password));
        areAllCustomersCompanyNameIdentified();
        areAllInvoicesAssociatedWithCustomers();

        Iterator<String> it = obtainListOfDaysWithoutSales();
        System.out.println("Não foram realizadas vendas nos seguintes dias:");
        while (it.hasNext()) {
            System.out.println("Dia: " + it.next());
        }

        Iterator<LinkedList<String>> iterator = obtainListOfNetTotalAndTaxPayableByTaxCode();
        while (iterator.hasNext()) {
            Iterator<String> secondIterator = iterator.next().iterator();
            while (secondIterator.hasNext()) {
                System.out.print(secondIterator.next());
            }

            System.out.println("");
        }

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
            List<Record> customersID = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Customer), (a)-[:HAS_COMPANY]->(c)"
                    + " "
                    + "WHERE a.CustomerID = '' OR a.CustomerID = NULL OR c.CompanyName = '' OR c.CompanyName = NULL"
                    + " "
                    + "RETURN a.CustomerID"
            ).list());

            if (customersID.isEmpty()) {
                System.out.println("They're all identified");
                return true;
            }

            Iterator<Record> iterator = customersID.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().values().get(0));
            }

            return false;
        }
    }

    private static boolean areAllSuppliersCompanyNameIdentified() {
        try (Session session = driver.session()) {
            List<Record> suppliersID = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Supplier), (a)-[r:HAS_COMPANY]->(c)"
                    + " "
                    + "WHERE a.SupplierID = '' OR a.SupplierID = NULL OR c.CompanyName = '' OR c.CompanyName = NULL"
                    + " "
                    + "RETURN a.SupplierID"
            ).list());

            if (suppliersID.isEmpty()) {
                System.out.println("They're all identified");
                return true;
            }

            Iterator<Record> iterator = suppliersID.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().values().get(0));
            }

            return false;
        }
    }

    private static boolean areAllInvoicesAssociatedWithCustomers() {
        try (Session session = driver.session()) {
            List<Record> invoicesNo = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Invoice)"
                    + " "
                    + "WHERE NOT EXISTS( (a)-[:HAS_CUSTOMER]->() )"
                    + " "
                    + "RETURN a.InvoiceNo"
            ).list());

            if (invoicesNo.isEmpty()) {
                System.out.println("They're all identified");
                return true;
            }

            Iterator<Record> iterator = invoicesNo.iterator();
            while (iterator.hasNext()) {
                System.out.println(iterator.next().values().get(0));
            }

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

    private static Iterator<String> obtainListOfDaysWithoutSales() {
        try (Session session = driver.session()) {
            Record fiscalYearDates = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:FiscalYear), (a)-[:HAS_START_DATE]->(c), (a)-[:HAS_END_DATE]->(d)"
                    + " "
                    + "RETURN c.StartDate, d.EndDate"
            ).single());

            String startDate = fiscalYearDates.values().get(0).asString();
            String endDate = fiscalYearDates.values().get(1).asString();

            List<Record> daysWithSalesQuery = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:Invoice), (a)-[:HAS_INVOICE_DATE]->(c)"
                    + " "
                    + "RETURN c.InvoiceDate"
            ).list());

            LinkedList<String> daysWithSales = new LinkedList<>();
            Iterator<Record> iterator = daysWithSalesQuery.iterator();
            while (iterator.hasNext()) {
                daysWithSales.add(iterator.next().values().get(0).asString());
            }

            LinkedList<String> daysWithoutSales = new LinkedList<>();
            String currentDate = startDate;

            while (!currentDate.equalsIgnoreCase(endDate)) {

                if (!daysWithSales.contains(currentDate)) {
                    daysWithoutSales.add(currentDate);
                }

                currentDate = LocalDate.parse(currentDate).plusDays(1).toString();
            }

            return daysWithoutSales.iterator();
        }
    }

    private static Iterator<LinkedList<String>> obtainListOfNetTotalAndTaxPayableByTaxCode() {
        try (Session session = driver.session()) {
            List<Record> results = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (a)-[:TYPE_OF]->(b:TaxTable), (c)-[:TYPE_OF]->(d:Invoice), "
                    + "(c)-[:HAS_LINE]->(e), (e)-[:HAS_TAX_TABLE]->(a), (c)-[:HAS_DOCUMENT_TOTALS]->(f), "
                    + "(f)-[:HAS_TAX_PAYABLE]-(g), (f)-[:HAS_NET_TOTAL]-(h)"
                    + " "
                    + "RETURN a.TaxCode, SUM(g.TaxPayable), SUM(h.TaxAmount)"
            ).list());

            Iterator<Record> queryIterator = results.iterator();
            LinkedList<LinkedList<String>> answer = new LinkedList<>();

            while (queryIterator.hasNext()) {

                Iterator<Value> queryIteratorOfValues = queryIterator.next().values().iterator();
                LinkedList<String> row = new LinkedList<>();
                while (queryIteratorOfValues.hasNext()) {
                    row.add(queryIteratorOfValues.next().asString());
                }

                answer.add(row);

            }

            return answer.iterator();
        }
    }

}
