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
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_BUSINESS_NAME + "]->(cbn:" + Entities.Labels.CompanyInfo + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_BUILDING_NUMBER + "]->(cbn:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_STREET_NAME + "]->(csn:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_ADDRESS_DETAIL + "]->(cad:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_CITY + "]->(ccity:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_POSTAL_CODE + "]->(cpc:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_REGION + "]->(cr:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_COUNTRY + "]->(ccountry:" + Entities.Labels.CompanyAddress + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_START_DATE + "]->(fsd:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_END_DATE + "]->(fed:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_CURRENCY_CODE + "]->(fcc:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_DATE_CREATED + "]->(fdc:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_TAX_ENTITY + "]->(fte:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.PRODUCED_BY + "]->(fpctid:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_ADDITIONAL_COMMENT + "]->(fhc:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_TELEPHONE + "]->(ct:" + Entities.Labels.CompanyContact + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_FAX + "]->(cf:" + Entities.Labels.CompanyContact + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_EMAIL + "]->(ce:" + Entities.Labels.CompanyContact + ")\n"
                    + "MATCH (c)-[:" + Entities.CompanyRelationships.HAS_WEBSITE + "]->(cw:" + Entities.Labels.CompanyContact + ")\n"
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

                    + "RETURN { Header: Header,  } AS AuditFile\n"
            ).single());

            return queryResult.asMap();
        }
    }

}
