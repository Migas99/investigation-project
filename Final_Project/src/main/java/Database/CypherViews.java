package Database;

import Enumerations.Entities;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Classe que contêm métodos que retornam vistas da base de dados
 */
public class CypherViews {

    /**
     * Este método retorna, por empresa, os ficheiros SAFT associados a esta, guardados
     * nesta base de dados.
     *
     * @param driver instância do driver para comunicar com a base de dados
     * @return lista que contêm a empresa e os nomes dos ficheiros associados a cada uma destas
     */
    public static LinkedList<Map<String, Object>> getListOfFilesByCompany(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (c:" + Entities.Labels.Company + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION + "]->(fi:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                    + "WITH c, collect({ FileName: f.FileName, FiscalYear: fy.FiscalYear }) AS Files\n"
                    + "RETURN DISTINCT(c.CompanyName) AS Company, Files\n"
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
     * Método responsável por retornar o ficheiro, dado o seu nome.
     * Este método retorna as informações que constam no ficheiro, seguindo uma estrutura
     * semelhante à versão XML deste.
     * <p>
     * EM CONSTRUÇÃO E DEBUG
     *
     * @param driver   instância do driver para comunicar com a base de dados
     * @param fileName nome do ficheiro do qual queremos retribuir a informação
     * @return conteúdo do ficheiro
     */
    public static Map<String, Object> getFileByName(Driver driver, String fileName) {
        try (Session session = driver.session()) {
            Record queryResult = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (f:" + Entities.Labels.File + ")\n"
                    + "WHERE f.FileName = '" + fileName + "'\n"
                    + "WITH f\n"

                    //Reconstrução do Header
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION + "]->(fi:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.RELATED_TO_COMPANY + "]->(c:" + Entities.Labels.Company + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_COMPANY_ID + "]->(cid:" + Entities.Labels.CompanyInfo + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER + "]->(ctr:" + Entities.Labels.CompanyInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_TAX_ACCOUNTING_BASIS + "]->(ftab:" + Entities.Labels.FileInfo + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_BUSINESS_NAME + "]->(cbn:" + Entities.Labels.CompanyInfo + ")\n"

                    //CompanyAddress
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_BUILDING_NUMBER + "]->(cbn:" + Entities.Labels.CompanyAddress + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_STREET_NAME + "]->(csn:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_ADDRESS_DETAIL + "]->(cad:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_CITY + "]->(ccity:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_POSTAL_CODE + "]->(cpc:" + Entities.Labels.CompanyAddress + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_REGION + "]->(cr:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_COUNTRY + "]->(ccountry:" + Entities.Labels.CompanyAddress + ")\n"

                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_START_DATE + "]->(fsd:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_END_DATE + "]->(fed:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_CURRENCY_CODE + "]->(fcc:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_DATE_CREATED + "]->(fdc:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_TAX_ENTITY + "]->(fte:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.PRODUCED_BY + "]->(fpctid:" + Entities.Labels.FileInfo + ")\n"
                    + "OPTIONAL MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_ADDITIONAL_COMMENT + "]->(fhc:" + Entities.Labels.FileInfo + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_TELEPHONE + "]->(ct:" + Entities.Labels.CompanyContact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_FAX + "]->(cf:" + Entities.Labels.CompanyContact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_EMAIL + "]->(ce:" + Entities.Labels.CompanyContact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CompanyRelationships.HAS_WEBSITE + "]->(cw:" + Entities.Labels.CompanyContact + ")\n"

                    + "WITH f, "
                    + "{"
                    + "AuditFileVersion: fi.AuditFileVersion,"
                    + "CompanyID: cid.CompanyID,"
                    + "TaxRegistrationNumber: ctr.TaxRegistrationNumber,"
                    + "TaxAccountingBasis: ftab.TaxAccountingBasis,"
                    + "CompanyName: c.CompanyName,"
                    + "BusinessName: cbn.BusinessName,"
                    + "CompanyAddress: {"
                    + "BuildingNumber: cbn.BuildingNumber,"
                    + "StreetName: csn.StreetName,"
                    + "AddressDetail: cad.AddressDetail,"
                    + "City: ccity.City,"
                    + "PostalCode: cpc.PostalCode,"
                    + "Region: cr.Region,"
                    + "Country: ccountry.Country"
                    + "},"
                    + "FiscalYear: fy.FiscalYear,"
                    + "StartDate: fsd.StartDate,"
                    + "EndDate: fed.EndDate,"
                    + "CurrencyCode: fcc.CurrencyCode,"
                    + "DateCreated: fdc.DateCreated,"
                    + "TaxEntity: fte.TaxEntity,"
                    + "ProductCompanyTaxID: fpctid.ProductCompanyTaxID,"
                    + "SoftwareCertificateNumber: fpctid.SoftwareCertificateNumber,"
                    + "ProductID: fpctid.ProductID,"
                    + "ProductVersion: fpctid.ProductVersion,"
                    + "HeaderComment: fhc.HeaderComment,"
                    + "Telephone: ct.Telephone,"
                    + "Fax: cf.Fax,"
                    + "Email: ce.Email,"
                    + "Website: cw.Website"
                    + "} AS Header\n"

                    //Reconstrução dos MasterFiles

                    //GeneralLedgerAccounts
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_GENERAL_LEDGER_ACCOUNTS + "]->(gla:" + Entities.Labels.GeneralLedgerAccounts + ")\n"

                    //Accounts
                    + "MATCH (gla)-[:" + Entities.GeneralLedgerAccountsRelationships.HAS_ACCOUNT + "]->(ac:" + Entities.Labels.Account + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_ACCOUNT_DESCRIPTION + "]->(acd:" + Entities.Labels.AccountInfo + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_OPENING_DEBIT_BALANCE + "]->(odb:" + Entities.Labels.AccountInfo + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_OPENING_CREDIT_BALANCE + "]->(ocb:" + Entities.Labels.AccountInfo + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_CLOSING_DEBIT_BALANCE + "]->(cdb:" + Entities.Labels.AccountInfo + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_CLOSING_CREDIT_BALANCE + "]->(ccb:" + Entities.Labels.AccountInfo + ")\n"
                    + "MATCH (ac)-[:" + Entities.AccountRelationships.HAS_GROUPING_CATEGORY + "]->(gc:" + Entities.Labels.AccountInfo + ")\n"
                    + "OPTIONAL MATCH (ac)-[:" + Entities.AccountRelationships.HAS_GROUPING_CODE + "]->(gcode:" + Entities.Labels.AccountInfo + ")\n"
                    + "OPTIONAL MATCH (ac)-[:" + Entities.AccountRelationships.HAS_TAXONOMY_CODE + "]->(tc:" + Entities.Labels.AccountInfo + ")\n"

                    + "WITH f, Header, gla, collect("
                    + "{"
                    + "AccountID: ac.AccountID,"
                    + "AccountDescription: acd.AccountDescription,"
                    + "OpeningDebitBalance: odb.OpeningDebitBalance,"
                    + "OpeningCreditBalance: ocb.OpeningCreditBalance,"
                    + "ClosingDebitBalance: cdb.ClosingDebitBalance,"
                    + "ClosingCreditBalance: ccb.ClosingCreditBalance,"
                    + "GroupingCategory: gc.GroupingCategory,"
                    + "GroupingCode: gcode.GroupingCode,"
                    + "TaxonomyCode: tc.TaxonomyCode"
                    + "}"
                    + ") AS Accounts\n"

                    + "WITH f, Header, "
                    + "{"
                    + "TaxonomyReference: gla.TaxonomyReference,"
                    + "Accounts: Accounts"
                    + "} AS GeneralLedgerAccounts\n"

                    //Customers
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_CUSTOMER + "]->(c:" + Entities.Labels.Customer + ")\n"
                    + "MATCH (c)-[:" + Entities.OtherRelationships.HAS_ACCOUNT + "]->(ac:" + Entities.Labels.Account + ")\n"
                    + "MATCH (c)-[:" + Entities.CustomerRelationships.HAS_CUSTOMER_TAX_ID + "]->(ctid:" + Entities.Labels.CustomerInfo + ")\n"
                    + "MATCH (c)-[:" + Entities.CustomerRelationships.REPRESENTS_AS_CUSTOMER + "]->(company:" + Entities.Labels.Company + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_CONTACT + "]->(contact:" + Entities.Labels.CustomerInfo + ")\n"

                    //BillingAddress
                    + "MATCH (c)-[:" + Entities.CustomerRelationships.HAS_BILLING_ADDRESS + "]->(ba:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(babn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_STREET_NAME + "]->(basn:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_CITY + "]->(bac:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_POSTAL_CODE + "]->(bapc:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_REGION + "]->(bar:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_COUNTRY + "]->(bacountry:" + Entities.Labels.Address + ")\n"

                    //ShipToAddress
                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_SHIP_TO_ADDRESS + "]->(sa:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(sabn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_STREET_NAME + "]->(sasn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_CITY + "]->(sac:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_POSTAL_CODE + "]->(sapc:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_REGION + "]->(sar:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_COUNTRY + "]->(sacountry:" + Entities.Labels.Address + ")\n"

                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_TELEPHONE + "]->(t:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_FAX + "]->(fax:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_EMAIL + "]->(email:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (c)-[:" + Entities.CustomerRelationships.HAS_WEBSITE + "]->(website:" + Entities.Labels.Contact + ")\n"
                    + "MATCH (c)-[:" + Entities.CustomerRelationships.HAS_SELF_BILLING_INDICATOR + "]->(sbi:" + Entities.Labels.CustomerInfo + ")\n"

                    + "WITH f, Header, GeneralLedgerAccounts, c, ac, ctid, company, contact, ba, babn, basn, bac, bapc, bar, bacountry, "
                    + "collect("
                    + "{"
                    + "BuildingNumber: sabn.BuildingNumber, "
                    + "StreetName: sasn.StreetName, "
                    + "AddressDetail: sa.AddressDetail, "
                    + "City: sac.City, "
                    + "PostalCode: sapc.PostalCode, "
                    + "Region: sar.Region, "
                    + "Country: sacountry.Country "
                    + "}"
                    + ") AS ShipToAddress, t, fax, email, website, sbi\n"

                    + "WITH f, Header, GeneralLedgerAccounts, collect("
                    + "{"
                    + "CustomerID: c.CustomerID,"
                    + "AccountID: ac.AccountID,"
                    + "CustomerTaxID: ctid.CustomerTaxID,"
                    + "CompanyName: company.CompanyName,"
                    + "Contact: contact.Contact,"
                    + "BillingAddress: {"
                    + "BuildingNumber: babn.BuildingNumber, "
                    + "StreetName: basn.StreetName, "
                    + "AddressDetail: ba.AddressDetail, "
                    + "City: bac.City, "
                    + "PostalCode: bapc.PostalCode, "
                    + "Region: bar.Region, "
                    + "Country: bacountry.Country "
                    + "},"
                    + "ShipToAddress: ShipToAddress,"
                    + "Telephone: t.Telephone,"
                    + "Fax: fax.Fax,"
                    + "Email: email.Email,"
                    + "Website: website.Website,"
                    + "SelfBillingIndicator: sbi.SelfBillingIndicator"
                    + "}) AS Customers\n"

                    //Suppliers
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_SUPPLIER + "]->(s:" + Entities.Labels.Supplier + ")\n"
                    + "MATCH (s)-[:" + Entities.OtherRelationships.HAS_ACCOUNT + "]->(ac:" + Entities.Labels.Account + ")\n"
                    + "MATCH (s)-[:" + Entities.SupplierRelationships.HAS_SUPPLIER_TAX_ID + "]->(stid:" + Entities.Labels.SupplierInfo + ")\n"
                    + "MATCH (s)-[:" + Entities.SupplierRelationships.REPRESENTS_AS_SUPPLIER + "]->(company:" + Entities.Labels.Company + ")\n"
                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_CONTACT + "]->(contact:" + Entities.Labels.SupplierInfo + ")\n"

                    //BillingAddress
                    + "MATCH (s)-[:" + Entities.SupplierRelationships.HAS_BILLING_ADDRESS + "]->(ba:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(babn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_STREET_NAME + "]->(basn:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_CITY + "]->(bac:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_POSTAL_CODE + "]->(bapc:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (ba)-[:" + Entities.AddressRelationships.HAS_REGION + "]->(bar:" + Entities.Labels.Address + ")\n"
                    + "MATCH (ba)-[:" + Entities.AddressRelationships.HAS_COUNTRY + "]->(bacountry:" + Entities.Labels.Address + ")\n"

                    //ShipToAddress
                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_SHIP_FROM_ADDRESS + "]->(sa:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_BUILDING_NUMBER + "]->(sabn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_STREET_NAME + "]->(sasn:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_CITY + "]->(sac:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_POSTAL_CODE + "]->(sapc:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_REGION + "]->(sar:" + Entities.Labels.Address + ")\n"
                    + "OPTIONAL MATCH (sa)-[:" + Entities.AddressRelationships.HAS_COUNTRY + "]->(sacountry:" + Entities.Labels.Address + ")\n"

                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_TELEPHONE + "]->(t:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_FAX + "]->(fax:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_EMAIL + "]->(email:" + Entities.Labels.Contact + ")\n"
                    + "OPTIONAL MATCH (s)-[:" + Entities.SupplierRelationships.HAS_WEBSITE + "]->(website:" + Entities.Labels.Contact + ")\n"
                    + "MATCH (s)-[:" + Entities.SupplierRelationships.HAS_SELF_BILLING_INDICATOR + "]->(sbi:" + Entities.Labels.SupplierInfo + ")\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, s, ac, stid, company, contact, ba, babn, basn, bac, bapc, bar, bacountry, "
                    + "collect("
                    + "{"
                    + "BuildingNumber: sabn.BuildingNumber, "
                    + "StreetName: sasn.StreetName, "
                    + "AddressDetail: sa.AddressDetail, "
                    + "City: sac.City, "
                    + "PostalCode: sapc.PostalCode, "
                    + "Region: sar.Region, "
                    + "Country: sacountry.Country "
                    + "}"
                    + ") AS ShipFromAddress, t, fax, email, website, sbi\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, collect("
                    + "{"
                    + "SupplierID: s.SupplierID,"
                    + "AccountID: ac.AccountID,"
                    + "SupplierTaxID: stid.SupplierTaxID,"
                    + "CompanyName: company.CompanyName,"
                    + "Contact: contact.Contact,"
                    + "BillingAddress: {"
                    + "BuildingNumber: babn.BuildingNumber, "
                    + "StreetName: basn.StreetName, "
                    + "AddressDetail: ba.AddressDetail, "
                    + "City: bac.City, "
                    + "PostalCode: bapc.PostalCode, "
                    + "Region: bar.Region, "
                    + "Country: bacountry.Country "
                    + "},"
                    + "ShipFromAddress: ShipFromAddress,"
                    + "Telephone: t.Telephone,"
                    + "Fax: fax.Fax,"
                    + "Email: email.Email,"
                    + "Website: website.Website,"
                    + "SelfBillingIndicator: sbi.SelfBillingIndicator"
                    + "}) AS Suppliers\n"

                    //Products
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_PRODUCT + "]->(p:" + Entities.Labels.Product + ")\n"
                    + "MATCH (p)-[:" + Entities.ProductRelationships.HAS_PRODUCT_TYPE + "]->(pt:" + Entities.Labels.ProductInfo + ")\n"
                    + "OPTIONAL MATCH (p)-[:" + Entities.ProductRelationships.HAS_PRODUCT_GROUP + "]->(pg:" + Entities.Labels.ProductInfo + ")\n"
                    + "MATCH (p)-[:" + Entities.ProductRelationships.HAS_PRODUCT_DESCRIPTION + "]->(pd:" + Entities.Labels.ProductInfo + ")\n"
                    + "MATCH (p)-[:" + Entities.ProductRelationships.HAS_PRODUCT_NUMBER_CODE + "]->(pnc:" + Entities.Labels.ProductInfo + ")\n"

                    //CustomsDetails
                    + "OPTIONAL MATCH (p)-[:" + Entities.ProductRelationships.HAS_CUSTOMS_DETAILS + "]->(cd:" + Entities.Labels.ProductInfo + ")\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, Suppliers, p, pt, pg, pd, pnc, "
                    + "collect("
                    + "{"
                    + "CNCode: cd.CNCode,"
                    + "UNNumber: cd.UNNumber"
                    + "}"
                    + ") AS CustomsDetails\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, Suppliers, "
                    + "collect("
                    + "{"
                    + "ProductType: pt.ProductType,"
                    + "ProductCode: p.ProductCode,"
                    + "ProductGroup: pg.ProductGroup,"
                    + "ProductDescription: pd.ProductDescription,"
                    + "ProductNumberCode: pnc.ProductNumberCode,"
                    + "CustomsDetails: CustomsDetails"
                    + "}"
                    + ") AS Products\n"

                    //TaxTable
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_TAX_TABLE + "]->(tb:" + Entities.Labels.TaxTable + ")\n"
                    + "MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_TAX_TYPE + "]->(tt:" + Entities.Labels.TaxTable + ")\n"
                    + "MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION + "]->(tcr:" + Entities.Labels.TaxTable + ")\n"
                    + "MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_DESCRIPTION + "]->(td:" + Entities.Labels.TaxTable + ")\n"
                    + "OPTIONAL MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_TAX_EXPIRATION_DATE + "]->(ted:" + Entities.Labels.TaxTable + ")\n"
                    + "OPTIONAL MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_TAX_PERCENTAGE + "]->(tp:" + Entities.Labels.TaxTable + ")\n"
                    + "OPTIONAL MATCH (tb)-[:" + Entities.TaxTableRelationships.HAS_TAX_AMOUNT + "]->(ta:" + Entities.Labels.TaxTable + ")\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, Suppliers, Products, "
                    + "collect("
                    + "{"
                    + "TaxType: tt.TaxType,"
                    + "TaxCountryRegion: tcr.TaxCountryRegion,"
                    + "TaxCode: tb.TaxCode,"
                    + "Description: td.Description,"
                    + "TaxExpirationDate: ted.TaxExpirationDate,"
                    + "TaxPercentage: tp.TaxPercentage,"
                    + "TaxAmount: ta.TaxAmount"
                    + "}"
                    + ") AS TaxTablesEntries\n"

                    + "WITH f, Header, GeneralLedgerAccounts, Customers, Suppliers, Products, "
                    + "{ TaxTableEntries: TaxTablesEntries } AS TaxTable\n"

                    + "WITH f, Header, "
                    + "{"
                    + "GeneralLedgerAccounts: GeneralLedgerAccounts,"
                    + "Customers: Customers,"
                    + "Suppliers: Suppliers,"
                    + "Products: Products,"
                    + "TaxTable: TaxTable"
                    + "} AS MasterFiles\n"

                    //GeneralLedgerEntries
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_GENERAL_LEDGER_ENTRIES + "]->(gle:" + Entities.Labels.GeneralLedgerEntries + ")\n"
                    + "MATCH (gle)-[:" + Entities.GeneralLedgerEntriesRelationships.HAS_TOTAL_DEBIT + "]->(gtd:" + Entities.Labels.GeneralLedgerEntries + ")\n"
                    + "MATCH (gle)-[:" + Entities.GeneralLedgerEntriesRelationships.HAS_TOTAL_CREDIT + "]->(gtc:" + Entities.Labels.GeneralLedgerEntries + ")\n"

                    //Journal
                    + "MATCH (gle)-[:" + Entities.GeneralLedgerEntriesRelationships.HAS_JOURNAL + "]->(j:" + Entities.Labels.Journal + ")\n"
                    + "MATCH (j)-[:" + Entities.JournalRelationships.HAS_DESCRIPTION + "]->(jd:" + Entities.Labels.JournalInfo + ")\n"

                    //Transaction
                    + "MATCH (j)-[:" + Entities.JournalRelationships.HAS_TRANSACTION + "]->(t:" + Entities.Labels.Transaction + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_PERIOD + "]->(tp:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_TRANSACTION_DATE + "]->(ttd:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_SOURCE_ID + "]->(tsid:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_DESCRIPTION + "]->(td:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER + "]->(tdan:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_TRANSACTION_TYPE + "]->(ttt:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_GL_POSTING_DATE + "]->(tglpd:" + Entities.Labels.TransactionInfo + ")\n"
                    + "OPTIONAL MATCH (t)-[:" + Entities.OtherRelationships.HAS_CUSTOMER + "]->(tc:" + Entities.Labels.Customer + ")\n"
                    + "OPTIONAL MATCH (t)-[:" + Entities.OtherRelationships.HAS_SUPPLIER + "]->(ts:" + Entities.Labels.Supplier + ")\n"

                    //Lines
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_LINES + "]->(tl:" + Entities.Labels.TransactionInfo + ")\n"

                    //DebitLine
                    + "MATCH (tl)-[:" + Entities.LinesRelationships.HAS_DEBIT_LINE + "]->(dl:" + Entities.Labels.DebitLine + ")\n"
                    + "MATCH (dl)-[:" + Entities.OtherRelationships.HAS_ACCOUNT + "]->(acc:" + Entities.Labels.Account + ")\n"
                    + "MATCH (dl)-[:" + Entities.DebitLineRelationships.HAS_SOURCE_DOCUMENT + "]->(sd)\n"
                    + "MATCH (dl)-[:" + Entities.DebitLineRelationships.HAS_SYSTEM_ENTRY_DATE + "]->(sed:" + Entities.Labels.DebitLine + ")\n"
                    + "MATCH (dl)-[:" + Entities.DebitLineRelationships.HAS_DESCRIPTION + "]->(dld:" + Entities.Labels.DebitLine + ")\n"
                    + "MATCH (dl)-[:" + Entities.DebitLineRelationships.HAS_DEBIT_AMOUNT + "]->(da:" + Entities.Labels.DebitLine + ")\n"

                    + "WITH f, Header, MasterFiles, gle, gtd, gtc, j, jd, t, tp, ttd, tsid, td, tdan, ttt, tglpd, tc, ts, tl, "
                    + "collect("
                    + "{"
                    + "RecordID: dl.RecordID,"
                    + "AccountID: acc.AccountID,"
                    + "SourceDocumentID: sd.SourceDocumentID,"
                    + "SystemEntryDate: sed.SystemEntryDate,"
                    + "Description: dld.Description,"
                    + "DebitAmount: da.DebitAmount"
                    + "}"
                    + ") AS DebitLines\n"

                    //CreditLine
                    + "MATCH (tl)-[:" + Entities.LinesRelationships.HAS_CREDIT_LINE + "]->(cl:" + Entities.Labels.CreditLine + ")\n"
                    + "MATCH (cl)-[:" + Entities.OtherRelationships.HAS_ACCOUNT + "]->(acc:" + Entities.Labels.Account + ")\n"
                    + "MATCH (cl)-[:" + Entities.CreditLineRelationships.HAS_SOURCE_DOCUMENT + "]->(sd)\n"
                    + "MATCH (cl)-[:" + Entities.CreditLineRelationships.HAS_SYSTEM_ENTRY_DATE + "]->(sed:" + Entities.Labels.CreditLine + ")\n"
                    + "MATCH (cl)-[:" + Entities.CreditLineRelationships.HAS_DESCRIPTION + "]->(cld:" + Entities.Labels.CreditLine + ")\n"
                    + "MATCH (cl)-[:" + Entities.CreditLineRelationships.HAS_CREDIT_AMOUNT + "]->(ca:" + Entities.Labels.CreditLine + ")\n"

                    + "WITH f, Header, MasterFiles, gle, gtd, gtc, j, jd, t, tp, ttd, tsid, td, tdan, ttt, tglpd, tc, ts, tl, DebitLines, "
                    + "collect("
                    + "{"
                    + "RecordID: cl.RecordID,"
                    + "AccountID: acc.AccountID,"
                    + "SourceDocumentID: sd.SourceDocumentID,"
                    + "SystemEntryDate: sed.SystemEntryDate,"
                    + "Description: cld.Description,"
                    + "CreditAmount: ca.CreditAmount"
                    + "}"
                    + ") AS CreditLines\n"

                    + "WITH f, Header, MasterFiles, gle, gtd, gtc, j, jd, t, tp, ttd, tsid, td, tdan, ttt, tglpd, tc, ts, "
                    + "{"
                    + "DebitLines: DebitLines,"
                    + "CreditLines: CreditLines"
                    + "} AS Lines\n"

                    + "WITH f, Header, MasterFiles, gle, gtd, gtc, j, jd, "
                    + "collect("
                    + "{"
                    + "TransactionID: t.TransactionID,"
                    + "Period: tp.Period,"
                    + "TransactionDate: ttd.TransactionDate,"
                    + "SourceID: tsid.SourceID,"
                    + "Description: td.Description, "
                    + "DocArchivalNumber: tdan.DocArchivalNumber,"
                    + "TransactionType: ttt.TransactionType,"
                    + "GLPostingDate: tglpd.GLPostingDate,"
                    + "CustomerID: tc.CustomerID,"
                    + "SupplierID: ts.SupplierID,"
                    + "Lines: Lines"
                    + "}"
                    + ") AS Transactions\n"

                    + "WITH f, Header, MasterFiles, gle, gtd, gtc, "
                    + "collect("
                    + "{"
                    + "JournalID: j.JournalID,"
                    + "Description: jd.Description,"
                    + "Transactions: Transactions"
                    + "}"
                    + ") AS Journals\n"

                    + "WITH f, Header, MasterFiles, "
                    + "{"
                    + "NumberOfEntries: gle.NumberOfEntries,"
                    + "TotalDebit: gtd.TotalDebit,"
                    + "TotalCredit: gtc.TotalCredit,"
                    + "Journals: Journals"
                    + "} AS GeneralLedgerEntries\n"

                    //SalesInvoices

                    + "RETURN Header, MasterFiles, GeneralLedgerEntries\n"
            ).single());

            return queryResult.asMap();
        }
    }

}
