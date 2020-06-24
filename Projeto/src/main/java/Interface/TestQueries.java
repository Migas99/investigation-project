package Interface;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.NULL;

public class TestQueries {

    private static Driver driver;
    private static final String URL = "neo4j://localhost:7687";
    private static final String user = "neo4j";
    private static final String password = "12345";

    public static void main(String[] args) {

        driver = GraphDatabase.driver(URL, AuthTokens.basic(user, password));
        Iterator<String> iterator = obtainListOfDaysWithoutSales();
        while (iterator.hasNext()) {
            System.out.println(iterator.next().toString());
        }

        driver.close();
    }

    private static Iterator<Map<String, Object>> obtainListOfCustomersNotIdentified() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(customer)-[:TYPE_OF]->(entity:Customer), "
                    + "(customer)-[:HAS_COMPANY]->(company)"
                    + " "
                    + "WHERE "
                    + "customer.CustomerID = ''"
                    + " OR "
                    + "customer.CustomerID = NULL"
                    + " OR "
                    + "company.CompanyName = ''"
                    + " OR "
                    + "company.CompanyName = NULL"
                    + " "
                    + "RETURN "
                    + "customer.CustomerID AS Customer, "
                    + "company.CompanyName AS Company"
                    + " "
                    + "ORDER BY customer.CustomerID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results.iterator();
        }
    }

    private static Iterator<Map<String, Object>> obtainListOfSuppliersNotIdentified() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(supplier)-[:TYPE_OF]->(entity:Supplier), "
                    + "(supplier)-[:HAS_COMPANY]->(company)"
                    + " "
                    + "WHERE "
                    + "supplier.SupplierID = ''"
                    + " OR "
                    + "supplier.SupplierID = NULL"
                    + " OR "
                    + "company.CompanyName = ''"
                    + " OR "
                    + "company.CompanyName = NULL"
                    + " "
                    + "RETURN "
                    + "supplier.SupplierID AS Supplier, "
                    + "company.CompanyName AS Company"
                    + " "
                    + "ORDER BY supplier.SupplierID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results.iterator();
        }
    }

    private static Iterator<String> obtainListOfInvoicesNotAssociatedWithCustomers() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:TYPE_OF]->(entity:Invoice), "
                    + "(invoice)-[:HAS_PERIOD]->(period)"
                    + " "
                    + "WHERE "
                    + "NOT EXISTS( (invoice)-[:HAS_CUSTOMER]->() )"
                    + " "
                    + "RETURN "
                    + "invoice.InvoiceNo AS Invoice"
                    + " "
                    + "ORDER BY period.Period"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<String> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap().get("Invoice").toString());
            }

            return results.iterator();
        }
    }

    private static Iterator<Map<String, Object>> obtainListOfNegativeAmountsInGeneralLedger() {
        /*try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:TYPE_OF]->(b:Transaction), "
                    + "(transaction)-[:HAS_LINES]->(lines), "
                    + "(lines)-[:HAS_DEBIT_LINE]->(debitLine), "
                    + "(lines)-[:HAS_CREDIT_LINE]->(creditLine), "
                    + "(debitLine)-[:HAS_DEBIT_AMOUNT]->(debitAmount), "
                    + "(creditLine)-[:HAS_CREDIT_AMOUNT]->(creditAmount)"
                    + " "
                    + "WHERE "
                    + "debitAmount.DebitAmount < 0"
                    + " OR "
                    + "creditAmount.CreditAmount < 0"
                    + " "
                    + "RETURN "
                    + "transaction.TransactionID AS Transaction, "
                    + "debitLine.RecordID AS DebitLineRecordID, "
                    + "debitAmount.DebitAmount AS DebitAmount, "
                    + "creditLine.RecordID AS CreditLineRecordID ,"
                    + "creditAmount.CreditAmount AS CreditAmount"
                    + " "
                    + "ORDER BY transaction.TransactionID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }*/

        LinkedList<Map<String, Object>> results = new LinkedList<>();

        results.addAll(obtainListOfCreditLinesWithNegativeAmounts());
        results.addAll(obtainListOfDebitLinesWithNegativeAmounts());

        return results.iterator();
        //}

    }

    private static LinkedList<Map<String, Object>> obtainListOfCreditLinesWithNegativeAmounts() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:TYPE_OF]->(b:Transaction), "
                    + "(transaction)-[:HAS_LINES]->(lines), "
                    + "(lines)-[:HAS_CREDIT_LINE]->(creditLine), "
                    + "(creditLine)-[:HAS_CREDIT_AMOUNT]->(creditAmount)"
                    + " "
                    + "WHERE "
                    + "creditAmount.CreditAmount < 0"
                    + " "
                    + "RETURN "
                    + "transaction.TransactionID AS Transaction, "
                    + "creditLine.RecordID AS RecordID ,"
                    + "creditAmount.CreditAmount AS CreditAmount"
                    + " "
                    + "ORDER BY transaction.TransactionID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }


    private static LinkedList<Map<String, Object>> obtainListOfDebitLinesWithNegativeAmounts() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:TYPE_OF]->(b:Transaction), "
                    + "(transaction)-[:HAS_LINES]->(lines), "
                    + "(lines)-[:HAS_DEBIT_LINE]->(debitLine), "
                    + "(debitLine)-[:HAS_DEBIT_AMOUNT]->(debitAmount)"
                    + " "
                    + "WHERE "
                    + "debitAmount.DebitAmount < 0"
                    + " "
                    + "RETURN "
                    + "transaction.TransactionID AS Transaction, "
                    + "debitLine.RecordID AS RecordID, "
                    + "debitAmount.DebitAmount AS DebitAmount"
                    + " "
                    + "ORDER BY transaction.TransactionID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    private static Iterator<String> obtainListOfDaysWithoutSales() {
        try (Session session = driver.session()) {
            Record fiscalYearDates = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(fiscalYear)-[:TYPE_OF]->(entity:FiscalYear), "
                    + "(fiscalYear)-[:HAS_START_DATE]->(startDate), "
                    + "(fiscalYear)-[:HAS_END_DATE]->(endDate)"
                    + " "
                    + "RETURN startDate.StartDate AS StartDate, endDate.EndDate AS EndDate"
            ).single());

            String startDate = fiscalYearDates.asMap().get("StartDate").toString();
            String endDate = fiscalYearDates.asMap().get("EndDate").toString();

            List<Record> daysWithSalesQuery = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:TYPE_OF]->(entity:Invoice), "
                    + "(invoice)-[:HAS_INVOICE_DATE]->(invoiceDate)"
                    + " "
                    + "RETURN "
                    + "invoice.InvoiceNo AS InvoiceNo, "
                    + "invoiceDate.InvoiceDate AS InvoiceDate"
                    + " "
                    + "ORDER BY invoiceDate.InvoiceDate"
            ).list());

            //Ainda não faço ideia de como o fazer apenas usando a query
            List<Record> daysWithoutSalesQuery = session.writeTransaction(tx -> tx.run(""
                    + "CALL {"
                    + " "
                    + "MATCH "
                    + "(fiscalYear)-[:TYPE_OF]->(a:FiscalYear), "
                    + "(fiscalYear)-[:HAS_START_DATE]->(startDate), "
                    + "(fiscalYear)-[:HAS_END_DATE]->(endDate)"
                    + " "
                    + "RETURN "
                    + "date(startDate.StartDate) AS start, "
                    + "date(endDate.EndDate) AS end"
                    + " "
                    + "}"
                    + " "
                    + "MATCH (invoice)-[:TYPE_OF]->(entity:Invoice), (invoice)-[:HAS_INVOICE_DATE]->(invoiceDate)"
                    + " "
                    + "RETURN invoice.InvoiceNo AS InvoiceNo, invoiceDate.InvoiceDate AS InvoiceDate"
                    + " "
                    + "ORDER BY invoiceDate.InvoiceDate"
            ).list());

            LinkedList<String> daysWithSales = new LinkedList<>();
            Iterator<Record> iterator = daysWithSalesQuery.iterator();

            while (iterator.hasNext()) {
                daysWithSales.add(iterator.next().asMap().get("InvoiceDate").toString());
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

    private static Iterator<Map<String, Object>> obtainListOfNetTotalAndTaxPayableByTaxCode() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(taxTable)-[:TYPE_OF]->(b:TaxTable), "
                    + "(invoice)-[:TYPE_OF]->(d:Invoice), "
                    + "(invoice)-[:HAS_LINE]->(line), "
                    + "(line)-[:HAS_TAX_TABLE]->(taxTable), "
                    + "(invoice)-[:HAS_DOCUMENT_TOTALS]->(documentTotals), "
                    + "(documentTotals)-[:HAS_TAX_PAYABLE]-(taxPayable), "
                    + "(documentTotals)-[:HAS_NET_TOTAL]-(taxAmount)"
                    + " "
                    + "RETURN "
                    + "taxTable.TaxCode AS TaxCode, "
                    + "SUM(taxPayable.TaxPayable) AS TotalTaxPayable, "
                    + "SUM(taxAmount.TaxAmount) AS TotalTaxAmount"
                    + " "
                    + "ORDER BY taxTable.TaxCode"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results.iterator();
        }
    }

    private static Iterator<Map<String, Object>> obtainListOfSalesByPeriod() {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:TYPE_OF]->(b:Invoice), "
                    + "(invoice)-[:HAS_PERIOD]->(period), "
                    + "(invoice)-[:HAS_DOCUMENT_TOTALS]->(documentTotals), "
                    + "(documentTotals)-[:HAS_NET_TOTAL]->(netTotal)"
                    + " "
                    + "RETURN "
                    + "DISTINCT(period.Period) AS Period, "
                    + "SUM(documentTotals.GrossTotal) AS TotalSalesWithoutTax, "
                    + "SUM(netTotal.NetTotal) AS TotalSalesWithTax"
                    + " "
                    + "ORDER BY period.Period"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results.iterator();
        }
    }
}
