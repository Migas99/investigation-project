package Database;

import Enumerations.Entities;
import org.neo4j.driver.*;

import java.time.LocalDate;
import java.util.*;

/**
 * Classe que contêm as Queries de pesquisa e restrição
 */
public class CypherQueries {

    /**
     * Pesquisa por faturas que não estejam associadas a um cliente
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o número da fatura que não está associada com nenhum cliente
     */
    public static LinkedList<Map<String, Object>> obtainListOfInvoicesNotAssociatedWithCustomers(Driver driver) {
        try (Session session = driver.session()) {
            List<Record> queryResults = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_SALES_INVOICES + "]->(si:" + Entities.Labels.SalesInvoices + ")\n"
                    + "MATCH (si)-[:" + Entities.SalesInvoicesRelationships.HAS_INVOICE + "]->(i:" + Entities.Labels.Invoice + ")\n"
                    + "WHERE NOT EXISTS( (i)-[:" + Entities.OtherRelationships.HAS_CUSTOMER + "]->() )\n"
                    + "WITH c, f, collect(i.InvoiceNo) AS InvoicesNo\n"
                    + "WITH c, collect({FileName: f.FileName, InvoicesNo: InvoicesNo}) AS Files\n"
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
     * Pesquisa por transações que contenham linhas onde o seu débito ou crédito é negativo
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm o id da transação, o id da linha e o valor da linha
     */
    public static LinkedList<Map<String, Object>> obtainListOfNegativeAmountsInGeneralLedger(Driver driver) {
        LinkedList<Map<String, Object>> results = new LinkedList<>();

        try (Session session = driver.session()) {

            session.writeTransaction(tx -> tx.run(""
                    + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_GENERAL_LEDGER_ENTRIES + "]->(gle:" + Entities.Labels.GeneralLedgerEntries + ")\n"
                    + "MATCH (gle)-[:" + Entities.GeneralLedgerEntriesRelationships.HAS_JOURNAL + "]->(j:" + Entities.Labels.Journal + ")\n"
                    + "MATCH (j)-[:" + Entities.JournalRelationships.HAS_TRANSACTION + "]->(t:" + Entities.Labels.Transaction + ")\n"
                    + "MATCH (t)-[:" + Entities.TransactionRelationships.HAS_LINES + "]->(l:" + Entities.Labels.TransactionInfo + ")\n"
                    + "MATCH (l)-[:" + Entities.LinesRelationships.HAS_CREDIT_LINE + "]->(c:" + Entities.Labels.CreditLine + ")\n"
                    + "MATCH (l)-[:" + Entities.LinesRelationships.HAS_DEBIT_LINE + "]->(d:" + Entities.Labels.DebitLine + ")\n"
                    + "MATCH (c)-[:" + Entities.CreditLineRelationships.HAS_CREDIT_AMOUNT + "]->(ca:" + Entities.Labels.CreditLine + ")\n"
                    + "MATCH (d)-[:" + Entities.DebitLineRelationships.HAS_DEBIT_AMOUNT + "]->(da:" + Entities.Labels.DebitLine + ")\n"
                    + "WHERE ca.CreditAmount < 0 OR da.DebitAmount < 0\n"
                    + "WITH c, f, j, t, c, d, collect(ca) AS CreditAmounts, collect(da) AS DebitAmounts\n"
            ));

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

            List<Record> queryResults2 = session.writeTransaction(tx -> tx.run(""
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

        }

        return results;
    }

    /**
     * Este método pesquisa pelos dias em que foram realizadas vendas e
     * retorna uma lista que contêm os dias onde não houveram vendas algumas
     *
     * @param driver instância para comunicar com a base de dados
     * @return lista que contêm datas referentes aos dias que não existiram vendas
     */
    public static LinkedList<Map<String, Object>> obtainListOfDaysWithoutSales(Driver driver) {
        try (Session session = driver.session()) {
            LinkedList<Map<String, Object>> answer = new LinkedList<>();

            Iterator<Record> recordsOfCompanies = session.writeTransaction(tx -> tx.run(""
                    + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(:" + Entities.Labels.File + ")\n"
                    + "RETURN DISTINCT(c.CompanyName) AS Company\n"
            ).list().iterator());

            LinkedList<String> listOfCompanies = new LinkedList<>();
            while (recordsOfCompanies.hasNext()) {
                listOfCompanies.add(String.valueOf(recordsOfCompanies.next().asMap().get("Company")));
            }

            Iterator<String> iterateOverCompanies = listOfCompanies.iterator();
            while (iterateOverCompanies.hasNext()) {
                Map<String, Object> first = new HashMap<>();

                String companyName = iterateOverCompanies.next();
                first.put("Company", companyName);

                Iterator<Record> recordsOfFiles = session.writeTransaction(tx -> tx.run(""
                        + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                        + "WHERE c.CompanyName = '" + companyName + "'\n"
                        + "RETURN f.FileName AS File\n"
                ).list().iterator());

                LinkedList<String> listOfFiles = new LinkedList<>();
                while (recordsOfFiles.hasNext()) {
                    listOfFiles.add(String.valueOf(recordsOfFiles.next().asMap().get("File")));
                }

                Iterator<String> iterateOverFiles = listOfFiles.iterator();
                LinkedList<Map<String, Object>> listOfFilesAnswer = new LinkedList<>();
                while (iterateOverFiles.hasNext()) {
                    Map<String, Object> second = new HashMap<>();

                    String fileName = iterateOverFiles.next();
                    second.put("FileName", fileName);

                    Record fiscalYearDates = session.writeTransaction(tx -> tx.run(""
                            + "MATCH (f:" + Entities.Labels.File + ")-[:" + Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION + "]->(fi:" + Entities.Labels.FileInfo + ")\n"
                            + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                            + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_START_DATE + "]->(sd:" + Entities.Labels.FileInfo + ")\n"
                            + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_END_DATE + "]->(ed:" + Entities.Labels.FileInfo + ")\n"
                            + "WHERE f.FileName = '" + fileName + "'\n"
                            + "RETURN fy.FiscalYear AS FiscalYear, sd.StartDate AS StartDate, ed.EndDate AS EndDate\n"
                    ).single());


                    Integer fiscalYear = Integer.parseInt(String.valueOf(fiscalYearDates.asMap().get("FiscalYear")));
                    String startDate = String.valueOf(fiscalYearDates.asMap().get("StartDate"));
                    String endDate = String.valueOf(fiscalYearDates.asMap().get("EndDate"));

                    second.put("FiscalYear", fiscalYear);
                    second.put("StartDate", startDate);
                    second.put("EndDate", endDate);

                    Iterator<Record> daysWithSalesQuery = session.writeTransaction(tx -> tx.run(""
                            + "MATCH (f:" + Entities.Labels.File + ")-[:" + Entities.FileRelationships.HAS_SALES_INVOICES + "]->(si:" + Entities.Labels.SalesInvoices + ")\n"
                            + "MATCH (si)-[:" + Entities.SalesInvoicesRelationships.HAS_INVOICE + "]->(i:" + Entities.Labels.Invoice + ")\n"
                            + "MATCH (i)-[:" + Entities.InvoiceRelationships.HAS_INVOICE_DATE + "]->(id:" + Entities.Labels.InvoiceInfo + ")\n"
                            + "WHERE f.FileName = '" + fileName + "'\n"
                            + "RETURN id.InvoiceDate AS InvoiceDate\n"
                            + "ORDER BY id.InvoiceDate\n"
                    ).list().iterator());

                    LinkedList<String> daysWithSales = new LinkedList<>();
                    while (daysWithSalesQuery.hasNext()) {
                        daysWithSales.add(daysWithSalesQuery.next().asMap().get("InvoiceDate").toString());
                    }

                    LinkedList<String> daysWithoutSales = new LinkedList<>();
                    String currentDate = startDate;

                    while (!currentDate.equalsIgnoreCase(endDate)) {

                        if (!daysWithSales.contains(currentDate)) {
                            daysWithoutSales.add(currentDate);
                        }

                        currentDate = LocalDate.parse(currentDate).plusDays(1).toString();
                    }

                    second.put("DaysWithoutSales", daysWithoutSales);
                    listOfFilesAnswer.add(second);
                }

                first.put("Files", listOfFilesAnswer);
                answer.add(first);
            }

            return answer;
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
                    + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION + "]->(fi:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_SALES_INVOICES + "]->(si:" + Entities.Labels.SalesInvoices + ")\n"
                    + "MATCH (si)-[:" + Entities.SalesInvoicesRelationships.HAS_INVOICE + "]->(i:" + Entities.Labels.Invoice + ")\n"
                    + "MATCH (i)-[:" + Entities.InvoiceRelationships.HAS_LINE + "]->(l:" + Entities.Labels.InvoiceInfo + ")\n"
                    + "MATCH (l)-[:" + Entities.LineRelationships.HAS_TAX_TABLE + "]->(tb:" + Entities.Labels.InvoiceInfo + ")\n"
                    + "MATCH (i)-[:" + Entities.InvoiceRelationships.HAS_DOCUMENT_TOTALS + "]->(dt:" + Entities.Labels.InvoiceInfo + ")\n"
                    + "WITH c, f, fy, { TaxCode: tb.TaxCode, TotalNetTotal: SUM(dt.NetTotal), TotalTaxPayable: SUM(dt.TaxPayable) } AS Amount\n"
                    + "WITH c, f, fy, collect(Amount) AS NetTotalAndTaxPayableByTaxCode\n"
                    + "WITH c, collect({ FileName: f.FileName, FiscalYear: fy.FiscalYear, NetTotalAndTaxPayableByTaxCode: NetTotalAndTaxPayableByTaxCode }) AS Files\n"
                    + "RETURN c.CompanyName AS Company, Files\n"
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
                    + "MATCH (c:" + Entities.Labels.Company + ")-[:" + Entities.CompanyRelationships.HAS_SAFTP_FILE + "]->(f:" + Entities.Labels.File + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION + "]->(fi:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (fi)-[:" + Entities.FileInformationRelationships.HAS_FISCAL_YEAR + "]->(fy:" + Entities.Labels.FileInfo + ")\n"
                    + "MATCH (f)-[:" + Entities.FileRelationships.HAS_SALES_INVOICES + "]->(si:" + Entities.Labels.SalesInvoices + ")\n"
                    + "MATCH (si)-[:" + Entities.SalesInvoicesRelationships.HAS_INVOICE + "]->(i:" + Entities.Labels.Invoice + ")\n"
                    + "MATCH (i)-[:" + Entities.InvoiceRelationships.HAS_PERIOD + "]->(ip:" + Entities.Labels.InvoiceInfo + ")\n"
                    + "MATCH (i)-[:" + Entities.InvoiceRelationships.HAS_DOCUMENT_TOTALS + "]->(idt:" + Entities.Labels.InvoiceInfo + ")\n"
                    + "WITH c, f, fy, { Period: ip.Period, TotalSalesWithoutTax: SUM(idt.GrossTotal), TotalSalesWithTax: SUM(idt.NetTotal) } AS Sale\n"
                    + "WITH c, f, fy, collect(Sale) AS SalesByPeriod\n"
                    + "WITH c, collect({ FileName: f.FileName, FiscalYear: fy.FiscalYear, SalesByPeriod: SalesByPeriod }) AS Files\n"
                    + "RETURN c.CompanyName AS Company, Files\n"
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
