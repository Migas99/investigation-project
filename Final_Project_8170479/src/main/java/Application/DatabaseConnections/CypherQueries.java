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
     * Pesquisa por todos os fornecedores
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de fornecedores
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllSuppliers(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Supplier + ") "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(account) "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.SupplierRelationships.HAS_SUPPLIER_TAX_ID + "]->(supplierTaxID) "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.OtherRelationships.HAS_COMPANY + "]->(companyName) "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.SupplierRelationships.HAS_CONTACTS + "]->(contacts) "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.SupplierRelationships.HAS_BILLING_ADDRESS + "]->(billingAddress) "
                    + "OPTIONAL MATCH "
                    + "(supplier)-[:" + EnumsOfEntities.SupplierRelationships.HAS_SHIP_FROM_ADDRESS + "]->(shipFromAddress) "
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
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(shipBuildingNumber) "
                    + "OPTIONAL MATCH "
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_STREET_NAME + "]->(shipStreetName) "
                    + "OPTIONAL MATCH "
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_CITY + "]->(shipCity) "
                    + "OPTIONAL MATCH "
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE + "]->(shipPostalCode) "
                    + "OPTIONAL MATCH "
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_REGION + "]->(shipRegion) "
                    + "OPTIONAL MATCH "
                    + "(shipFromAddress)-[:" + EnumsOfEntities.AddressRelationships.HAS_COUNTRY + "]->(shipCountry) "
                    + " "
                    + "RETURN "
                    + "supplier.SupplierID AS SupplierID, "
                    + "account.AccountID AS AccountID, "
                    + "supplierTaxID.SupplierTaxID AS SupplierTaxID, "
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
                    + "AddressDetail: shipFromAddress.AddressDetail, "
                    + "City: shipCity.City, "
                    + "PostalCode: shipPostalCode.PostalCode, "
                    + "Region: shipRegion.Region, "
                    + "Country: shipCountry.Country "
                    + "} AS ShipFromAddress, "

                    + "telephone.Telephone AS Telephone, "
                    + "fax.Fax AS Fax, "
                    + "email.Email AS Email, "
                    + "website.Website AS Website, "
                    + "supplier.SelfBillingIndicator AS SelfBillingIndicator"
                    + " "
                    + "ORDER BY supplier.SupplierID"
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
     * Pesquisa por todos os produtos
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de produtos
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllProducts(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(product)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Product + ") "
                    + "OPTIONAL MATCH "
                    + "(product)-[:" + EnumsOfEntities.ProductRelationships.HAS_PRODUCT_TYPE + "]->(productType) "
                    + "OPTIONAL MATCH "
                    + "(product)-[:" + EnumsOfEntities.ProductRelationships.HAS_PRODUCT_GROUP + "]->(productGroup) "
                    + "OPTIONAL MATCH "
                    + "(product)-[:" + EnumsOfEntities.ProductRelationships.HAS_PRODUCT_NUMBER_CODE + "]->(productNumberCode) "
                    + "OPTIONAL MATCH "
                    + "(product)-[:" + EnumsOfEntities.ProductRelationships.HAS_CUSTOMS_DETAILS + "]->(customsDetails)"
                    + " "
                    + "RETURN "
                    + "productType.ProductType AS ProductType, "
                    + "product.ProductCode AS ProductCode, "
                    + "productGroup.ProductGroup AS ProductGroup, "
                    + "product.ProductDescription AS ProductDescription, "
                    + "productNumberCode.ProductNumberCode AS ProductNumberCode, "
                    + "collect({ "
                    + "CNCode: customsDetails.CNCode, "
                    + "UNNumber: customsDetails.UNNumber "
                    + "}) AS CustomsDetails"
                    + " "
                    + "ORDER BY product.ProductCode"
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
     * Pesquisa pelas GeneralLedgerEntries
     *
     * @param driver instância para comunicar com a base de dados
     * @return a GeneralLedgerEntries
     */
    public static Map<String, Object> obtainGeneralLedgerEntries(Driver driver) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(ledgerEntree)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.GeneralLedgerEntries + ") "
                    + "OPTIONAL MATCH "
                    + "(ledgerEntree)-[:" + EnumsOfEntities.GeneralLedgerEntriesRelationships.HAS_TOTAL_DEBIT + "]->(totalDebit) "
                    + "OPTIONAL MATCH "
                    + "(ledgerEntree)-[:" + EnumsOfEntities.GeneralLedgerEntriesRelationships.HAS_TOTAL_CREDIT + "]->(totalCredit) "
                    + " "
                    + "RETURN "
                    + "ledgerEntree.NumberOfEntries AS NumberOfEntries, "
                    + "totalDebit.TotalDebit AS TotalDebit, "
                    + "totalCredit.TotalCredit AS TotalCredit"
                    + " "
                    + "ORDER BY ledgerEntree.NumberOfEntries"
            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * Pesquisa por todos os jornais
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de jornais
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllJournals(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(journal)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Journal + ") "
                    + " "
                    + "RETURN "
                    + "journal.JournalID AS JournalID, "
                    + "journal.Description AS Description"
                    + " "
                    + "ORDER BY journal.JournalID"
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
     * Pesquisa por todas as transações
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de transações
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllTransactions(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Transaction + ") "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_PERIOD + "]->(period) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_DATE + "]->(date) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_SOURCE + "]->(source) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER + "]->(archivalNumber) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_TYPE + "]->(type) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_GL_POSTING_DATE + "]->(postingDate) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_CUSTOMER + "]->(customer) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_SUPPLIER + "]->(supplier) "
                    + " "
                    + "RETURN "
                    + "transaction.TransactionID AS TransactionID, "
                    + "period.Period AS Period, "
                    + "date.TransactionDate AS TransactionDate, "
                    + "source.SourceID AS SourceID, "
                    + "transaction.Description AS Description, "
                    + "archivalNumber.DocArchivalNumber AS DocArchivalNumber, "
                    + "type.TransactionType AS TransactionType, "
                    + "postingDate.GLPostingDate AS GLPostingDate, "
                    + "customer.CustomerID AS CustomerID, "
                    + "supplier.SupplierID AS SupplierID"
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
     * Pesquisa por todas as transações associadas a um jornal específico
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de transações
     */
    public static LinkedList<Map<String, Object>> obtainListOfTransactionsByJournalId(Driver driver, String journalID) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(journal)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Journal + ") "
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity2:" + EnumsOfEntities.Entities.Transaction + ") "
                    + "MATCH "
                    + "(journal)-[:" + EnumsOfEntities.JournalRelationships.HAS_TRANSACTION + "]->(transaction) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_PERIOD + "]->(period) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_DATE + "]->(date) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_SOURCE + "]->(source) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER + "]->(archivalNumber) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_TYPE + "]->(type) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_GL_POSTING_DATE + "]->(postingDate) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_CUSTOMER + "]->(customer) "
                    + "OPTIONAL MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.HAS_SUPPLIER + "]->(supplier) "
                    + "WITH "
                    + "journal, transaction, period, date, source, archivalNumber, type, postingDate, customer, supplier"
                    + " "
                    + "WHERE "
                    + "journal.JournalID = '" + journalID + "'"
                    + " "
                    + "RETURN "
                    + "transaction.TransactionID AS TransactionID, "
                    + "period.Period AS Period, "
                    + "date.TransactionDate AS TransactionDate, "
                    + "source.SourceID AS SourceID, "
                    + "transaction.Description AS Description, "
                    + "archivalNumber.DocArchivalNumber AS DocArchivalNumber, "
                    + "type.TransactionType AS TransactionType, "
                    + "postingDate.GLPostingDate AS GLPostingDate, "
                    + "customer.CustomerID AS CustomerID, "
                    + "supplier.SupplierID AS SupplierID"
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
     * Pesquisa por todas as linhas de débito associadas a uma transação específica
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de linhas de débito
     */
    public static LinkedList<Map<String, Object>> obtainListOfDebitLinesByTransactionId(Driver driver, String transactionId) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Transaction + ") "
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_LINES + "]->(lines) "
                    + "MATCH "
                    + "(lines)-[:" + EnumsOfEntities.LinesRelationships.HAS_DEBIT_LINE + "]->(debitLine) "
                    + "OPTIONAL MATCH "
                    + "(debitLine)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(account) "
                    + "OPTIONAL MATCH "
                    + "(debitLine)-[:" + EnumsOfEntities.DebitLineRelationships.HAS_SOURCE_DOCUMENT + "]->(document) "
                    + "OPTIONAL MATCH "
                    + "(debitLine)-[:" + EnumsOfEntities.DebitLineRelationships.HAS_SYSTEM_ENTRY_DATE + "]->(date) "
                    + "OPTIONAL MATCH "
                    + "(debitLine)-[:" + EnumsOfEntities.DebitLineRelationships.HAS_DEBIT_AMOUNT + "]->(amount)"
                    + " "
                    + "WITH "
                    + "transaction, debitLine, account, document, date, amount"
                    + " "
                    + "WHERE "
                    + "transaction.TransactionID = '" + transactionId + "'"
                    + " "
                    + "RETURN "
                    + "debitLine.RecordID AS RecordID, "
                    + "account.AccountID AS AccountID, "
                    + "document.SourceDocumentID AS SourceDocumentID, "
                    + "date.SystemEntryDate AS SystemEntryDate, "
                    + "debitLine.Description AS Description, "
                    + "amount.DebitAmount AS DebitAmount"
                    + " "
                    + "ORDER BY debitLine.RecordID"
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
     * Pesquisa por todas as linhas de crédito associadas a uma transação específica
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de linhas de crédito
     */
    public static LinkedList<Map<String, Object>> obtainListOfCreditLinesByTransactionId(Driver driver, String transactionId) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Transaction + ") "
                    + "MATCH "
                    + "(transaction)-[:" + EnumsOfEntities.TransactionRelationships.HAS_LINES + "]->(lines) "
                    + "MATCH "
                    + "(lines)-[:" + EnumsOfEntities.LinesRelationships.HAS_CREDIT_LINE + "]->(creditLine) "
                    + "OPTIONAL MATCH "
                    + "(creditLine)-[:" + EnumsOfEntities.OtherRelationships.HAS_ACCOUNT + "]->(account) "
                    + "OPTIONAL MATCH "
                    + "(creditLine)-[:" + EnumsOfEntities.CreditLineRelationships.HAS_SOURCE_DOCUMENT + "]->(document) "
                    + "OPTIONAL MATCH "
                    + "(creditLine)-[:" + EnumsOfEntities.CreditLineRelationships.HAS_SYSTEM_ENTRY_DATE + "]->(date) "
                    + "OPTIONAL MATCH "
                    + "(creditLine)-[:" + EnumsOfEntities.CreditLineRelationships.HAS_CREDIT_AMOUNT + "]->(amount) "
                    + " "
                    + "WITH "
                    + "transaction, creditLine, account, document, date, amount"
                    + " "
                    + "WHERE "
                    + "transaction.TransactionID = '" + transactionId + "'"
                    + " "
                    + "RETURN "
                    + "creditLine.RecordID AS RecordID, "
                    + "account.AccountID AS AccountID, "
                    + "document.SourceDocumentID AS SourceDocumentID, "
                    + "date.SystemEntryDate AS SystemEntryDate, "
                    + "creditLine.Description AS Description, "
                    + "amount.CreditAmount AS CreditAmount"
                    + " "
                    + "ORDER BY creditLine.RecordID"
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
     * Pesquisa pelas SalesInvoices
     *
     * @param driver instância para comunicar com a base de dados
     * @return a SalesInvoices
     */
    public static Map<String, Object> obtainSalesInvoices(Driver driver) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(salesInvoices)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.SalesInvoices + ") "
                    + "OPTIONAL MATCH "
                    + "(salesInvoices)-[:" + EnumsOfEntities.SalesInvoicesRelationships.HAS_TOTAL_DEBIT + "]->(totalDebit) "
                    + "OPTIONAL MATCH "
                    + "(salesInvoices)-[:" + EnumsOfEntities.SalesInvoicesRelationships.HAS_TOTAL_CREDIT + "]->(totalCredit) "
                    + " "
                    + "RETURN "
                    + "salesInvoices.NumberOfEntries AS NumberOfEntries, "
                    + "totalDebit.TotalDebit AS TotalDebit, "
                    + "totalCredit.TotalCredit AS TotalCredit"
            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * Pesquisa por todas as faturas
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de faturas
     */
    public static LinkedList<Map<String, Object>> obtainListOfAllInvoices(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_ATCUD + "]->(atcud) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_HASH + "]->(hash) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_HASH_CONTROL + "]->(hashControl) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_PERIOD + "]->(period) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_INVOICE_DATE + "]->(invoiceDate) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_INVOICE_TYPE + "]->(invoiceType) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.HAS_SOURCE + "]->(source) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_EAC_Code + "]->(eacCode) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_SYSTEM_ENTRY_DATE + "]->(entryDate) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.HAS_TRANSACTION + "]->(transaction) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.HAS_CUSTOMER + "]->(customer) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_MOVEMENT_END_TIME + "]->(mEndTime) "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_MOVEMENT_START_TIME + "]->(mStartTime)"
                    + " "
                    + "RETURN "
                    + "invoice.InvoiceNo AS InvoiceNo, "
                    + "atcud.ATCUD AS ATCUD, "
                    + "hash.Hash AS Hash, "
                    + "hashControl.HashControl AS HashControl, "
                    + "period.Period AS Period, "
                    + "invoiceDate.InvoiceDate AS InvoiceDate, "
                    + "invoiceType.InvoiceType AS InvoiceType, "
                    + "source.SourceID AS SourceID, "
                    + "eacCode.EACCode AS EACCode, "
                    + "entryDate.SystemEntryDate AS SystemEntryDate, "
                    + "transaction.TransactionID AS TransactionID, "
                    + "customer.CustomerID AS CustomerID, "
                    + "mEndTime.MovementEndTime AS MovementEndTime, "
                    + "mStartTime.MovementStartTime AS MovementStartTime"
                    + " "
                    + "ORDER BY invoice.InvoiceNo"
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
     * Pesquisa pelo status de uma dada fatura
     *
     * @param driver instância para comunicar com a base de dados
     * @return status da fatura
     */
    public static Map<String, Object> obtainDocumentStatusByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_DOCUMENT_STATUS + "]->(documentStatus) "
                    + "OPTIONAL MATCH "
                    + "(documentStatus)-[:" + EnumsOfEntities.DocumentStatusRelationships.HAS_REASON + "]->(reason) "
                    + "OPTIONAL MATCH "
                    + "(documentStatus)-[:" + EnumsOfEntities.OtherRelationships.HAS_SOURCE + "]->(source) "
                    + "OPTIONAL MATCH "
                    + "(documentStatus)-[:" + EnumsOfEntities.DocumentStatusRelationships.HAS_SOURCE_BILLING + "]->(billing) "
                    + " "
                    + "WITH "
                    + "invoice, documentStatus, reason, source, billing"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "documentStatus.InvoiceStatus AS InvoiceStatus, "
                    + "documentStatus.InvoiceStatusDate AS InvoiceStatusDate, "
                    + "reason.Reason AS Reason, "
                    + "source.SourceID AS SourceID, "
                    + "billing.SourceBilling AS SourceBilling"
            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * Pesquisa pelos regimes especiais de uma dada fatura
     *
     * @param driver instância para comunicar com a base de dados
     * @return regimes especiais da fatura
     */
    public static Map<String, Object> obtainSpecialRegimesByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "OPTIONAL MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_SPECIAL_REGIMES + "]->(special) "
                    + " "
                    + "WITH "
                    + "invoice, special"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "special.SelfBillingIndicator AS SelfBillingIndicator, "
                    + "special.CashVATSchemeIndicator AS CashVATSchemeIndicator, "
                    + "special.ThirdPartiesBillingIndicator AS ThirdPartiesBillingIndicator"
            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * @param driver instância para comunicar com a base de dados
     * @return shipTo da fatura
     */
    public static Map<String, Object> obtainShipToByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_SHIP_TO + "]->(ship) "
                    + "OPTIONAL MATCH "
                    + "(ship)-[:" + EnumsOfEntities.ShipToRelationships.HAS_ADDRESS + "]->(address) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(buildingNumber) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_STREET_NAME + "]->(streetName) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_CITY + "]->(city) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE + "]->(postalCode) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_REGION + "]->(region) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_COUNTRY + "]->(country) "
                    + " "
                    + "WITH "
                    + "invoice, ship, address, buildingNumber, streetName, city, postalCode, region, country"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "ship.DeliveryID AS DeliveryID, "
                    + "ship.DeliveryDate AS DeliveryDate, "

                    + "{ "
                    + "BuildingNumber: buildingNumber.BuildingNumber, "
                    + "StreetName: streetName.StreetName, "
                    + "AddressDetail: address.AddressDetail, "
                    + "City: city.City, "
                    + "PostalCode: postalCode.PostalCode, "
                    + "Region: region.Region, "
                    + "Country: country.Country "
                    + "} AS Address"

            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * @param driver instância para comunicar com a base de dados
     * @return shipFrom da fatura
     */
    public static Map<String, Object> obtainShipFromByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_SHIP_FROM + "]->(ship) "
                    + "OPTIONAL MATCH "
                    + "(ship)-[:" + EnumsOfEntities.ShipToRelationships.HAS_ADDRESS + "]->(address) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(buildingNumber) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_STREET_NAME + "]->(streetName) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_CITY + "]->(city) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE + "]->(postalCode) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_REGION + "]->(region) "
                    + "OPTIONAL MATCH "
                    + "(address)-[:" + EnumsOfEntities.AddressRelationships.HAS_COUNTRY + "]->(country) "
                    + " "
                    + "WITH "
                    + "invoice, ship, address, buildingNumber, streetName, city, postalCode, region, country"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "ship.DeliveryID AS DeliveryID, "
                    + "ship.DeliveryDate AS DeliveryDate, "

                    + "{ "
                    + "BuildingNumber: buildingNumber.BuildingNumber, "
                    + "StreetName: streetName.StreetName, "
                    + "AddressDetail: address.AddressDetail, "
                    + "City: city.City, "
                    + "PostalCode: postalCode.PostalCode, "
                    + "Region: region.Region, "
                    + "Country: country.Country "
                    + "} AS Address"

            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * Pesquisa pelas linhas de uma fatura
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de linhas de uma fatura
     */
    public static LinkedList<Map<String, Object>> obtainListOfLineByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_LINE + "]->(line) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_ORDER_REFERENCES + "]->(orderRef) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.OtherRelationships.HAS_PRODUCT + "]->(product) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_QUANTITY + "]->(quantity) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_UNIT_OF_MEASURE + "]->(measure) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_UNIT_PRICE + "]->(unitPrice) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_TAX_BASE + "]->(taxBase) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_TAX_POINT_DATE + "]->(taxPointDate) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_REFERENCES + "]->(ref) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_PRODUCT_SERIAL_NUMBER + "]->(serialNumber) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_DEBIT_AMOUNT + "]->(debitAmount) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_CREDIT_AMOUNT + "]->(creditAmount) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_TAX_TABLE + "]->(tax), "
                    + "(tax)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_TYPE + "]->(taxType) "
                    + "OPTIONAL MATCH "
                    + "(tax)-[:" + EnumsOfEntities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION + "]->(taxCountryRegion) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_TAX_EXEMPTION + "]->(exemption) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_SETTLEMENT_AMOUNT + "]->(sAmount) "
                    + "OPTIONAL MATCH "
                    + "(line)-[:" + EnumsOfEntities.LineRelationships.HAS_CUSTOMS_INFORMATION + "]->(customs)"
                    + " "
                    + "WITH "
                    + "invoice, line, orderRef, product, quantity, measure, unitPrice, taxBase, taxPointDate, ref, serialNumber, "
                    + "debitAmount, creditAmount, tax, taxType, taxCountryRegion, exemption, sAmount, customs, "
                    + "collect({ OriginatingON: orderRef.OriginatingON, OrderDate: orderRef.OrderDate }) AS OrderReferences, "
                    + "collect({ Reference: ref.Reference, Reason: ref.Reason }) AS References, "
                    + "collect({ SerialNumber: serialNumber.SerialNumber }) AS ProductSerialNumber, "
                    + "collect({ TaxType: taxType.TaxType, TaxCountryRegion: taxCountryRegion.TaxCountryRegion, "
                    + "TaxCode: tax.TaxCode, TaxPercentage: tax.TaxPercentage, TaxAmount: tax.TaxAmount }) AS Tax"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "line.LineNumber AS LineNumber, "

                    /*+ "{ "
                    + "OriginatingON: orderRef.OriginatingON, "
                    + "OrderDate: orderRef.OrderDate "
                    + "} AS OrderReferences, "*/
                    + "OrderReferences, "

                    + "product.ProductCode AS ProductCode, "
                    + "product.ProductDescription AS ProductDescription, "
                    + "quantity.Quantity AS Quantity, "
                    + "measure.UnitOfMeasure AS UnitOfMeasure, "
                    + "unitPrice.UnitPrice AS UnitPrice, "
                    + "taxBase.TaxBase AS TaxBase, "
                    + "taxPointDate.TaxPointDate AS TaxPointDate, "

                    /*+ "{ "
                    + "Reference: ref.Reference, "
                    + "Reason: ref.Reason "
                    + "} AS References, "*/
                    + "References, "

                    + "line.Description AS Description, "

                    /*+ "{ "
                    + "SerialNumber: serialNumber.SerialNumber "
                    + "} AS ProductSerialNumber, "*/
                    + "ProductSerialNumber, "

                    + "debitAmount.DebitAmount AS DebitAmount, "
                    + "creditAmount.CreditAmount AS CreditAmount, "

                    /*+ "{ "
                    + "TaxType: taxType.TaxType, "
                    + "TaxCountryRegion: taxCountryRegion.TaxCountryRegion, "
                    + "TaxCode: tax.TaxCode, "
                    + "TaxPercentage: tax.TaxPercentage, "
                    + "TaxAmount: tax.TaxAmount "
                    + "} AS Tax, "*/
                    + "Tax, "

                    + "exemption.TaxExemptionReason AS TaxExemptionReason, "
                    + "exemption.TaxExemptionCode AS TaxExemptionCode, "
                    + "sAmount.SettlementAmount AS SettlementAmount, "

                    + "{ "
                    + "ARCNo: customs.ARCNo, "
                    + "IECAmount: customs.IECAmount "
                    + "} AS CustomsInformation"

                    + " "
                    + "ORDER BY line.LineNumber"
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
     * Pesquisa pela informação relativa aos totais de uma fatura
     *
     * @param driver instância para comunicar com a base de dados
     * @return retorna os totais de uma fatura
     */
    public static Map<String, Object> obtainDocumentTotalsByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_DOCUMENT_TOTALS + "]->(documentTotals) "
                    + "OPTIONAL MATCH "
                    + "(documentTotals)-[:" + EnumsOfEntities.DocumentTotalsRelationships.HAS_TAX_PAYABLE + "]->(taxPayable) "
                    + "OPTIONAL MATCH "
                    + "(documentTotals)-[:" + EnumsOfEntities.DocumentTotalsRelationships.HAS_NET_TOTAL + "]->(netTotal) "
                    + "OPTIONAL MATCH "
                    + "(documentTotals)-[:" + EnumsOfEntities.DocumentTotalsRelationships.HAS_CURRENCY + "]->(currency) "
                    + "OPTIONAL MATCH "
                    + "(documentTotals)-[:" + EnumsOfEntities.DocumentTotalsRelationships.HAS_SETTLEMENT + "]->(settlement) "
                    + "OPTIONAL MATCH "
                    + "(documentTotals)-[:" + EnumsOfEntities.DocumentTotalsRelationships.HAS_PAYMENT + "]->(payment) "
                    + " "
                    + "WITH "
                    + "invoice, documentTotals, taxPayable, netTotal, currency, "
                    + "collect({ SettlementDiscount: settlement.SettlementDiscount, SettlementAmount: settlement.SettlementAmount, "
                    + "SettlementDate: settlement.SettlementDate , PaymentTerms: settlement.PaymentTerms }) AS Settlement, "
                    + "collect({ PaymentMechanism: payment.PaymentMechanism, PaymentAmount: payment.PaymentAmount, "
                    + "PaymentDate: payment.PaymentDate }) AS Payment"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "taxPayable.TaxPayable AS TaxPayable, "
                    + "netTotal.NetTotal AS NetTotal, "
                    + "documentTotals.GrossTotal AS GrossTotal, "

                    + "{ "
                    + "CurrencyCode: currency.CurrencyCode, "
                    + "CurrencyAmount: currency.CurrencyAmount, "
                    + "ExchangeRate: currency.ExchangeRate "
                    + "} AS Currency, "

                    + "Settlement, "
                    + "Payment"
            ).single());

            return queryResult.asMap();
        }
    }

    /**
     * Pesquisa pela lista de WithholdingTax por fatura
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista de WithholdingTax
     */
    public static List<Map<String, Object>> obtainListOfWithholdingTaxByInvoiceId(Driver driver, String invoiceNo) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.OtherRelationships.TYPE_OF + "]->(entity:" + EnumsOfEntities.Entities.Invoice + ") "
                    + "MATCH "
                    + "(invoice)-[:" + EnumsOfEntities.InvoiceRelationships.HAS_WITHHOLDING_TAX + "]->(holdingTax) "
                    + "OPTIONAL MATCH "
                    + "(holdingTax)-[:" + EnumsOfEntities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_TYPE + "]->(type) "
                    + "OPTIONAL MATCH "
                    + "(holdingTax)-[:" + EnumsOfEntities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_AMOUNT + "]->(amount) "
                    + " "
                    + "WITH "
                    + "invoice, holdingTax, type, amount"
                    + " "
                    + "WHERE "
                    + "invoice.InvoiceNo = '" + invoiceNo + "'"
                    + " "
                    + "RETURN "
                    + "type.WithholdingTaxType AS WithholdingTaxType, "
                    + "holdingTax.WithholdingTaxDescription AS WithholdingTaxDescription, "
                    + "amount.WithholdingTaxAmount AS WithholdingTaxAmount"
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
