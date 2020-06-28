package Application.DatabaseConnections;

import Application.Enums.EnumsOfEntities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CypherQueries {

    /**
     * Pesquisa por todas as contas
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de contas
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllAccounts(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(account)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Account + ")"
                    + " "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_OPENING_DEBIT_BALANCE + "]->(openingDebitBalance) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_OPENING_CREDIT_BALANCE + "]->(openingCreditBalance) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_CLOSING_DEBIT_BALANCE + "]->(closingDebitBalance) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_CLOSING_CREDIT_BALANCE + "]->(closingCreditBalance) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_GROUPING_CATEGORY + "]->(groupingCategory) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_GROUPING_CODE + "]->(groupingCode) "
                    + "OPTIONAL MATCH "
                    + "(account)-[:" + EnumsOfEntities.AccountRelationships.HAS_TAXONOMY_CODE + "]->(taxonomyCode) "
                    + " "
                    + "RETURN "
                    + "account.AccountID AS AccountID, "
                    + "account.AccountDescription AS AccountDescription, "
                    + "openingDebitBalance.OpeningDebitBalance AS OpeningDebitBalance, "
                    + "openingCreditBalance.OpeningCreditBalance AS OpeningCreditBalance, "
                    + "closingDebitBalance.ClosingDebitBalance AS ClosingDebitBalance, "
                    + "closingCreditBalance.ClosingCreditBalance AS ClosingCreditBalance, "
                    + "groupingCategory.GroupingCategory AS GroupingCategory, "
                    + "groupingCode.GroupingCode AS GroupingCode, "
                    + "taxonomyCode.TaxonomyCode AS TaxonomyCode"
                    + " "
                    + "ORDER BY account.AccountID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * Pesquisa por todos os clientes
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de clientes
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllCustomers(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(customer)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Customer + ") "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(account) "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.CustomerRelationships.HAS_CUSTOMER_TAX_ID + "]->(customerTaxID) "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.OtherRelationships.HAS_COMPANY + "]->(companyName) "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.CustomerRelationships.HAS_CONTACTS + "]->(contacts) "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.CustomerRelationships.HAS_BILLING_ADDRESS + "]->(billingAddress) "
                    + "OPTIONAL MATCH "
                    + "(customer)-[:" + EnumsOfEntities.CustomerRelationships.HAS_SHIP_TO_ADDRESS + "]->(shipToAddress) "
                    + "OPTIONAL MATCH "
                    + "(contacts)-[:" + EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE + "]->(telephone) "
                    + "OPTIONAL MATCH "
                    + "(contacts)-[:" + EnumsOfEntities.ContactsRelationships.HAS_FAX + "]->(fax) "
                    + "OPTIONAL MATCH "
                    + "(contacts)-[:" + EnumsOfEntities.ContactsRelationships.HAS_EMAIL + "]->(email) "
                    + "OPTIONAL MATCH "
                    + "(contacts)-[:" + EnumsOfEntities.ContactsRelationships.HAS_WEBSITE + "]->(website) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(buildingNumber) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_STREET_NAME + "]->(streetName) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_CITY + "]->(city) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE + "]->(postalCode) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_REGION + "]->(region) "
                    + "OPTIONAL MATCH "
                    + "(billingAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_COUNTRY + "]->(country) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(shipBuildingNumber) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_STREET_NAME + "]->(shipStreetName) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_CITY + "]->(shipCity) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE + "]->(shipPostalCode) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_REGION + "]->(shipRegion) "
                    + "OPTIONAL MATCH "
                    + "(shipToAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_COUNTRY + "]->(shipCountry) "
                    + " "
                    + "RETURN "
                    + "customer.CustomerID AS CustomerID, "
                    + "account.AccountID AS AccountID, "
                    + "customerTaxID.CustomerTaxID AS CustomerTaxID, "
                    + "companyName.CompanyName AS CompanyName, "
                    + "contacts.Contact AS Contact, "

                    + "{ "
                    + "BuildingNumber: buildingNumber.BuildingNumber, "
                    + "StreetName: streetName.StreetName, "
                    + "AddressDetail: billingAddress.AddressDetail, "
                    + "City: city.City, "
                    + "PostalCode: postalCode.PostalCode, "
                    + "Region: region.Region, "
                    + "Country: country.Country "
                    + "} AS BillingAddress, "

                    + "{ "
                    + "BuildingNumber: shipBuildingNumber.BuildingNumber, "
                    + "StreetName: shipStreetName.StreetName, "
                    + "AddressDetail: shipToAddress.AddressDetail, "
                    + "City: shipCity.City, "
                    + "PostalCode: shipPostalCode.PostalCode, "
                    + "Region: shipRegion.Region, "
                    + "Country: shipCountry.Country "
                    + "} AS ShipToAddress, "

                    + "telephone.Telephone AS Telephone, "
                    + "fax.Fax AS Fax, "
                    + "email.Email AS Email, "
                    + "website.Website AS Website, "
                    + "customer.SelfBillingIndicator AS SelfBillingIndicator"
                    + " "
                    + "ORDER BY customer.CustomerID"
            ).list());

            Iterator<Record> queryIterator = queryResults.iterator();
            LinkedList<Map<String, Object>> results = new LinkedList<>();

            while (queryIterator.hasNext()) {
                results.add(queryIterator.next().asMap());
            }

            return results;
        }
    }

    /**
     * Pesquisa por clientes onde o seu ID ou CompanyName contenham valores vazios
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id do cliente e o nome da empresa
     */
    public static LinkedList<Map<String, Object>> obtainListOfCustomersNotIdentified(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(customer)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:Customer), "
                    + "(company)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity2:Company), "
                    + "(customer)-[:" + EnumsOfEntities.OtherRelationships.HAS_COMPANY + "]->(company)"
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

            return results;
        }
    }

    /**
     * Pesquisa por fornecedores onde o seu ID ou CompanyName contenham valores vazios
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id do fornecedor e o nome da empresa
     */
    public static Iterator<Map<String, Object>> obtainListOfSuppliersNotIdentified(Driver driver) {
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

    /**
     * Pesquisa por faturas que não estejam associadas a um cliente
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o número da fatura que não está associada com nenhum cliente
     */
    public static LinkedList<String> obtainListOfInvoicesNotAssociatedWithCustomers(Driver driver) {
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

            return results;
        }
    }

    /**
     * Pesquisa por transações que contenham linhas onde o seu débito ou crédito é negativo
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id da transação, o id da linha e o valor da linha
     */
    public static LinkedList<Map<String, Object>> obtainListOfNegativeAmountsInGeneralLedger(Driver driver) {
        LinkedList<Map<String, Object>> results = new LinkedList<>();

        results.addAll(obtainListOfCreditLinesWithNegativeAmounts(driver));
        results.addAll(obtainListOfDebitLinesWithNegativeAmounts(driver));

        return results;
    }

    /**
     * Auxilia o método obtainListOfNegativeAmountsInGeneralLedger.
     * Este método pesquisa apenas por linhas do tipo CreditLine.
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id da transação, o id da linha e o valor da linha
     */
    private static LinkedList<Map<String, Object>> obtainListOfCreditLinesWithNegativeAmounts(Driver driver) {
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

    /**
     * Auxilia o método obtainListOfNegativeAmountsInGeneralLedger.
     * Este método pesquisa apenas por linhas do tipo DebitLine.
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id da transação, o id da linha e o valor da linha
     */
    private static LinkedList<Map<String, Object>> obtainListOfDebitLinesWithNegativeAmounts(Driver driver) {
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

    /**
     * Este método pesquisa pelos dias em que foram realizadas vendas e
     * retorna uma lista que contêm os dias onde não houveram vendas algumas
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm datas referentes aos dias que não existiram vendas
     */
    public static LinkedList<String> obtainListOfDaysWithoutSales(Driver driver) {
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

            return daysWithoutSales;
        }
    }

    /**
     * Método que obtêm o total cobrado por taxas, por taxa
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o código da taxa, o total pago e a quantidade
     */
    public static LinkedList<Map<String, Object>> obtainListOfNetTotalAndTaxPayableByTaxCode(Driver driver) {
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

            return results;
        }
    }

    /**
     * Método que pesquisa o total de vendas por período
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o período, o total das vendas sem imposto, e o total das vendas com imposto
     */
    public static LinkedList<Map<String, Object>> obtainListOfSalesByPeriod(Driver driver) {
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

            return results;
        }
    }
}
