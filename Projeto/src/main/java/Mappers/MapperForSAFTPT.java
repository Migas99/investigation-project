package Mappers;

import Database.Neo4j;

import java.util.LinkedList;
import java.util.Stack;

public class MapperForSAFTPT {

    private final Neo4j driver;
    private LinkedList<String> heads;
    private Stack<String> stack;
    private String currentHead = null;
    private LinkedList<Long> ids = new LinkedList<>();
    private LinkedList<String> nodes = new LinkedList<>();

    public MapperForSAFTPT(Neo4j driver) {
        this.driver = driver;
        this.heads = this.getHeads();
        this.stack = new Stack<>();
    }

    private enum Heads {
        AuditFile, Header, CompanyAddress, MasterFiles,
        GeneralLedgerAccounts, Account, Customer, BillingAddress,
        ShipToAddress, Supplier, ShipFromAddress, Product,
        CustomsDetails, TaxTable, TaxTableEntry, GeneralLedgerEntries,
        Journal, Transaction, Lines, CreditLine,
        DebitLine, SourceDocuments, SalesInvoices, Invoice,
        DocumentStatus, SpecialRegimes, ShipTo, Address,
        ShipFrom, Line, OrderReferences, References,
        ProductSerialNumber, Tax, CustomsInformation, DocumentTotals,
        Currency, Settlement, Payment, WithholdingTax,
        MovementOfGoods, StockMovement, WorkingDocuments, WorkDocument,
        Payments, PaymentMethod, SourceDocumentID
    }

    private enum Labels {
        AuditFileInformation, Company
    }

    private enum Relationships {
        HAS, HAS_INFORMATION_OF, TYPE_OF, PART_OF
    }

    private LinkedList<String> getHeads() {
        Heads[] headsEnum = Heads.values();
        LinkedList<String> heads = new LinkedList<>();

        for (int i = 0; i < headsEnum.length; i++) {
            heads.add(String.valueOf(headsEnum[i]));
        }

        return heads;
    }

    public void processStartElement(String XMLElement, String value) {

        if (this.heads.contains(XMLElement)) {
            this.currentHead = XMLElement;
            this.stack.push(XMLElement);
        }

        switch (this.currentHead) {
            case "AuditFile":
                this.driver.addNode("AuditFile");
                break;
            case "Header":
                this.processAuditFileHeader(XMLElement, value);
                break;
            case "CompanyAddress":
                this.processAuditFileHeaderCompanyAddress(XMLElement, value);
                break;
        }
    }

    /**
     * @param XMLElement end element
     */
    public void processEndElement(String XMLElement) {
        if (this.heads.contains(XMLElement)) {
            this.stack.pop();

            if (XMLElement.equalsIgnoreCase("AuditFile")) {
                this.driver.close();
            } else {
                this.currentHead = this.stack.peek();
            }

        }
    }

    private void processAuditFileHeader(String XMLElement, String value) {
        switch (XMLElement) {
            case "Header":
                this.driver.addNode("Header");
                this.driver.addRelationship(12, 12, Relationships.HAS.toString());
                break;
            case "AuditFileVersion":
                this.driver.addAttributesToNode(12, XMLElement, value);
                break;
            case "CompanyID":
                this.driver.addAttributesToNode(this.driver.addNode("Company"), XMLElement, value);
                this.driver.addRelationship(12, 12, Relationships.HAS_INFORMATION_OF.toString());
                break;
            case "TaxRegistrationNumber":
                break;
            case "TaxAccountingBasis":
                break;
            case "CompanyName":
                break;
            case "BusinessName":
                break;
            case "FiscalYear":
                break;
            case "StartDate":
                break;
            case "EndDate":
                break;
            case "CurrencyCode":
                break;
            case "DateCreated":
                break;
            case "TaxEntity":
                break;
            case "ProductCompanyTaxID":
                break;
            case "SoftwareCertificateNumber":
                break;
            case "ProductID":
                break;
            case "ProductVersion":
                break;
            case "HeaderComment":
                break;
            case "Fax":
                break;
            case "Email":
                break;
            case "Website":
                break;
        }
    }

    private void processAuditFileHeaderCompanyAddress(String XMLElement, String value) {
        switch (XMLElement) {
            case "CompanyAddress":
                break;
            case "BuildingNumber":
                break;
            case "StreetName":
                break;
            case "AddressDetail":
                break;
            case "City":
                break;
            case "PostalCode":
                break;
            case "Region":
                break;
            case "Country":
                break;
        }
    }
}
