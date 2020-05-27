package Mappers;

import Database.Neo4j;
import Exceptions.MapException;
import Exceptions.NodeException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Stack;

public class MapperForSAFTPT {

    private final Neo4j driver;
    private LinkedList<String> heads;
    private LinkedList<String> headsCheck;
    private String rootElement = "AuditFile";
    private String previousHead = null;
    private String currentHead = null;
    private LinkedList<GraphNode> graphNodeContainer = new LinkedList<>();

    public MapperForSAFTPT(Neo4j driver) {
        this.driver = driver;
        this.heads = this.getHeads();
        this.headsCheck = new LinkedList<>();
        this.loadXMLStructure();
    }

    public void processStartElement(String XMLElement, String value) {

        if (this.heads.contains(XMLElement)) {
            this.headsCheck.add(XMLElement);

            if (this.headsCheck.size() >= 2) {
                this.previousHead = this.headsCheck.get(this.headsCheck.size() - 2);
            }

            this.currentHead = XMLElement;
        }

        try {
            switch (this.currentHead) {
                case "AuditFile":
                    this.processAuditFile(XMLElement, value);
                    break;
                case "Header":
                    this.processAuditFileHeader(XMLElement, value);
                    break;
                case "CompanyAddress":
                    this.processAuditFileHeaderCompanyAddress(XMLElement, value);
                    break;
                case "MasterFiles":
                    this.processAuditFileMasterFiles(XMLElement, value);
                    break;
                case "GeneralLedgerAccounts":
                    //this.processAuditFileMasterFilesGeneralLedgerAccounts(XMLElement, value);
                    break;
                case "Account":
                    //this.processAuditFileMasterFilesGeneralLedgerAccountsAccount(XMLElement, value);
                    break;
                case "Customer":
                    this.processAuditFileMasterFilesCustomer(XMLElement, value);
                    break;
                case "BillingAddress":
                    this.processAuditFileMasterFilesBillingAddress(XMLElement, value);
                    break;
                case "ShipToAddress":
                    this.processAuditFileMasterFilesShipToAddress(XMLElement, value);
                    break;
                case "Supplier":
                    break;
                case "ShipFromAddress":
                    break;
                case "Product":
                    this.processAuditFileMasterFilesProduct(XMLElement, value);
                    break;
                case "CustomsDetails":
                    this.processAuditFileMasterFilesProductCustomsDetails(XMLElement, value);
                    break;
                case "TaxTable":
                    this.processAuditFileMasterFilesTaxTable(XMLElement, value);
                    break;
                case "TaxTableEntry":
                    this.processAuditFileMasterFilesTaxTableTaxTableEntry(XMLElement, value);
                    break;
                case "GeneralLedgerEntries":
                    break;
                case "SourceDocuments":
                    this.processAuditFileSourceDocuments(XMLElement, value);
                    break;
                case "SalesInvoices":
                    this.processAuditFileSourceDocumentsSalesInvoices(XMLElement, value);
                    break;
                case "Invoice":
                    this.processAuditFileSourceDocumentsSalesInvoicesInvoice(XMLElement, value);
                    break;
                case "DocumentStatus":
                    this.processAuditFileSourceDocumentsSalesInvoicesInvoiceDocumentStatus(XMLElement, value);
                    break;
                case "SpecialRegimes":
                    break;
                case "ShipTo":
                    break;
                case "ShipFrom":
                    break;
                case "Line":
                    this.processAuditFileSourceDocumentsSalesInvoicesInvoiceLine(XMLElement, value);
                    break;
                case "OrderReferences":
                    break;
                case "References":
                    break;
                case "ProductSerialNumber":
                    break;
                case "Tax":
                    this.processAuditFileSourceDocumentsSalesInvoicesInvoiceLineTax(XMLElement, value);
                    break;
                case "CustomsInformation":
                    break;
                case "DocumentTotals":
                    this.processAuditFileSourceDocumentsSalesInvoicesInvoiceDocumentTotals(XMLElement, value);
                    break;
                case "Currency":
                    break;
                case "Settlement":
                    break;
                case "Payment":
                    break;
                case "WithholdingTax":
                    break;
                default:
                    break;
            }
        } catch (MapException e) {

        }
    }

    public void processEndElement(String XMLElement) {
        if (this.heads.contains(XMLElement)) {
            if (XMLElement.equalsIgnoreCase(this.rootElement)) {
                this.driver.close();
            } else {
                this.headsCheck.removeLast();
                this.currentHead = this.headsCheck.getLast();

                if (this.headsCheck.size() >= 2) {
                    this.previousHead = this.headsCheck.get(this.headsCheck.size() - 2);
                }

                boolean found = false;
                while (!found && !this.graphNodeContainer.isEmpty()) {
                    if (this.graphNodeContainer.getLast().getXMLElement().equalsIgnoreCase(XMLElement)) {
                        found = true;
                    }

                    this.graphNodeContainer.removeLast();
                }
            }

        }
    }

    private void processAuditFile(String XMLElement, String value) throws MapException {
        try {
            if ("AuditFile".equalsIgnoreCase(XMLElement)) {
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(this.rootElement), this.rootElement, value));
                this.driver.addRelationshipTypeOf(this.findNodeId(this.rootElement), this.rootElement);
            } else {
                throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileHeader(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Header":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(AuditFileEntities.Header.toString()), AuditFileEntities.Header.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(AuditFileEntities.Header.toString()), AuditFileEntities.Header.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(AuditFileEntities.Header.toString()), AuditFileRelationships.HAS_HEADER.toString());

                    break;

                case "AuditFileVersion":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "CompanyID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    break;

                case "TaxRegistrationNumber":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    break;

                case "TaxAccountingBasis":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.TaxAccountingBasis.toString()), HeaderEntities.TaxAccountingBasis.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderEntities.TaxAccountingBasis.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderEntities.TaxAccountingBasis.toString(), value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderRelationships.HAS_TAX_ACCOUNTING_BASIS.toString());

                    break;

                case "CompanyName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.Company.toString()), HeaderEntities.Company.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.Company.toString()), HeaderEntities.Company.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.Company.toString()), HeaderEntities.Company.toString(), value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.Company.toString()), HeaderRelationships.HAS_COMPANY.toString());
                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId("CompanyID"), HeaderRelationships.HAS_COMPANY_ID.toString());
                    } catch (NodeException e) {
                    }
                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId("TaxRegistrationNumber"), HeaderRelationships.HAS_TAX_REGISTRATION_NUMBER.toString());
                    } catch (NodeException e) {
                    }

                    break;

                case "BusinessName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_BUSINESS_NAME.toString());

                    break;

                case "FiscalYear":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.FiscalYear.toString()), HeaderEntities.FiscalYear.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.FiscalYear.toString()), HeaderEntities.FiscalYear.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.FiscalYear.toString()), HeaderEntities.FiscalYear.toString(), value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.FiscalYear.toString()), HeaderRelationships.HAS_FISCAL_YEAR.toString());

                    break;

                case "StartDate":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.FiscalYear.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_START_DATE.toString());

                    break;

                case "EndDate":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.FiscalYear.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_END_DATE.toString());

                    break;

                case "CurrencyCode":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "DateCreated":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "TaxEntity":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.TaxEntity.toString()), HeaderEntities.TaxEntity.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderEntities.TaxEntity.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderEntities.TaxEntity.toString(), value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderRelationships.HAS_TAX_ENTITY.toString());

                    break;

                case "ProductCompanyTaxID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.ProductCompany.toString()), HeaderEntities.ProductCompany.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.ProductCompany.toString()), HeaderEntities.ProductCompany.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), HeaderEntities.ProductCompany.toString(), value);

                    break;

                case "SoftwareCertificateNumber":
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), XMLElement, value);

                    break;

                case "ProductID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), HeaderEntities.ProductSoftware.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.ProductSoftware.toString()), HeaderEntities.ProductSoftware.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductSoftware.toString()), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(HeaderEntities.TaxEntity.toString()), this.findNodeId(HeaderEntities.ProductSoftware.toString()), HeaderRelationships.HAS_PRODUCT.toString());
                    this.driver.addRelationship(this.findNodeId(HeaderEntities.ProductSoftware.toString()), this.findNodeId(HeaderEntities.ProductCompany.toString()), HeaderRelationships.HAS_PRODUCT_COMPANY.toString());

                    break;

                case "ProductVersion":
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductSoftware.toString()), XMLElement, value);

                    break;

                case "HeaderComment":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "Telephone":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_TELEPHONE.toString());

                    break;

                case "Fax":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_FAX.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_FAX.toString());
                    }

                    break;

                case "Email":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_EMAIL.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_EMAIL.toString());
                    }

                    break;

                case "Website":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_WEBSITE.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_WEBSITE.toString());
                    }

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileHeaderCompanyAddress(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "CompanyAddress":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), AddressEntities.CompanyAddress.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(AddressEntities.CompanyAddress.toString()), AddressEntities.CompanyAddress.toString());

                    this.driver.addAttributesToNode(this.findNodeId(AddressEntities.CompanyAddress.toString()), AddressEntities.CompanyAddress.toString(), value);
                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(AddressEntities.CompanyAddress.toString()), AddressRelationships.HAS_COMPANY_ADDRESS.toString());

                    break;

                case "BuildingNumber":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_BUILDING_NUMBER.toString());

                    break;

                case "StreetName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_STREET_NAME.toString());

                    break;

                case "AddressDetail":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "City":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_CITY.toString());

                    break;

                case "PostalCode":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_POSTAL_CODE.toString());

                    break;

                case "Region":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_REGION.toString());

                    break;

                case "Country":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_COUNTRY.toString());

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFiles(String XMLElement, String value) throws MapException {
        try {
            if ("MasterFiles".equalsIgnoreCase(XMLElement)) {
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), AuditFileRelationships.HAS_MASTER_FILES.toString());
            } else {
                throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFilesGeneralLedgerAccounts(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "GeneralLedgerAccounts":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(MasterFilesEntities.GeneralLedgerAccounts.toString()), MasterFilesEntities.GeneralLedgerAccounts.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(MasterFilesEntities.GeneralLedgerAccounts.toString()), MasterFilesEntities.GeneralLedgerAccounts.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(MasterFilesEntities.GeneralLedgerAccounts.toString()), MasterFilesRelationships.HAS_GENERAL_LEDGER_ACCOUNTS.toString());

                    break;
                case "TaxonomyReference":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;
                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileMasterFilesGeneralLedgerAccountsAccount(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Account":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), GeneralLedgerAccountsRelationships.HAS_ACCOUNT.toString());

                    break;

                case "AccountID":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "AccountDescription":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "OpeningDebitBalance":
                    break;

                case "OpeningCreditBalance":
                    break;

                case "ClosingDebitBalance":
                    break;

                case "ClosingCreditBalance":
                    break;

                case "GroupingCategory":
                    break;

                case "GroupingCode":
                    break;

                case "TaxonomyCode":
                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileMasterFilesCustomer(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Customer":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), MasterFilesRelationships.HAS_CUSTOMER.toString());

                    break;

                case "CustomerID":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "AccountID":
                    this.driver.addRelationshipToAccount(this.findNodeId(this.currentHead), value);

                    break;

                case "CustomerTaxID":
                    break;

                case "CompanyName":
                    this.driver.addRelationshipToCompany(this.findNodeId(this.currentHead), value);

                    break;

                case "Contact":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "Telephone":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString()));
                    this.driver.addRelationshipTypeOf(this.findNodeId(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString());

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(CustomerEntities.Contacts.toString()), CustomerRelationships.HAS_CONTACTS.toString());

                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_TELEPHONE.toString());

                    break;

                case "Fax":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_FAX.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString());

                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(CustomerEntities.Contacts.toString()), CustomerRelationships.HAS_CONTACTS.toString());

                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_FAX.toString());
                    }

                    break;

                case "Email":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_EMAIL.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString());

                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(CustomerEntities.Contacts.toString()), CustomerRelationships.HAS_CONTACTS.toString());

                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_EMAIL.toString());
                    }

                    break;

                case "Website":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_WEBSITE.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(CustomerEntities.Contacts.toString()), CustomerEntities.Contacts.toString());

                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(CustomerEntities.Contacts.toString()), CustomerRelationships.HAS_CONTACTS.toString());

                        this.driver.addRelationship(this.findNodeId(CustomerEntities.Contacts.toString()), this.findNodeId(XMLElement), CustomerRelationships.HAS_WEBSITE.toString());
                    }

                    break;

                case "SelfBillingIndicator":
                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFilesBillingAddress(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "BillingAddress":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(AddressEntities.BillingAddress.toString()), AddressEntities.BillingAddress.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(AddressEntities.BillingAddress.toString()), AddressEntities.BillingAddress.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(AddressEntities.BillingAddress.toString()), AddressRelationships.HAS_BILLING_ADDRESS.toString());

                    break;

                case "BuildingNumber":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_BUILDING_NUMBER.toString());

                    break;

                case "StreetName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_STREET_NAME.toString());

                    break;

                case "AddressDetail":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "City":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_CITY.toString());

                    break;

                case "PostalCode":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_POSTAL_CODE.toString());

                    break;

                case "Region":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_REGION.toString());

                    break;

                case "Country":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), AddressRelationships.HAS_COUNTRY.toString());

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileMasterFilesShipToAddress(String XMLElement, String value) throws MapException {
    }

    private void processAuditFileMasterFilesProduct(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Product":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), MasterFilesRelationships.HAS_PRODUCT.toString());

                    break;

                case "ProductType":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), ProductRelationships.HAS_PRODUCT_TYPE.toString());

                    break;

                case "ProductCode":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "ProductGroup":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), ProductRelationships.HAS_PRODUCT_GROUP.toString());

                    break;

                case "ProductDescription":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "ProductNumberCode":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFilesProductCustomsDetails(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "CustomsDetails":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), ProductRelationships.HAS_CUSTOMS_DETAILS.toString());

                    break;

                case "CNCode":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "UNNumber":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFilesTaxTable(String XMLElement, String value) throws MapException {
        try {
            if ("TaxTable".equalsIgnoreCase(XMLElement)) {
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), MasterFilesRelationships.HAS_TAX_TABLE.toString());
            } else {
                throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileMasterFilesTaxTableTaxTableEntry(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "TaxTableEntry":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_TABLE_ENTRY.toString());

                    break;

                case "TaxType":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_TYPE.toString());

                    break;

                case "TaxCountryRegion":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_COUNTRY_REGION.toString());

                    break;

                case "TaxCode":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "Description":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "TaxExpirationDate":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(TaxTableEntities.TaxExpirationDate.toString()), TaxTableEntities.TaxExpirationDate.toString());

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_EXPIRATION_DATE.toString());

                    break;

                case "TaxPercentage":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_PERCENTAGE.toString());

                    break;

                case "TaxAmount":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), TaxTableRelationships.HAS_TAX_AMOUNT.toString());

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileSourceDocuments(String XMLElement, String value) throws MapException {
        try {
            if ("SourceDocuments".equalsIgnoreCase(XMLElement)) {
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), AuditFileRelationships.HAS_SOURCE_DOCUMENTS.toString());
            } else {
                throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileSourceDocumentsSalesInvoices(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "SalesInvoices":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SourceDocumentsEntities.SalesInvoices.toString()), SourceDocumentsEntities.SalesInvoices.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(SourceDocumentsEntities.SalesInvoices.toString()), SourceDocumentsEntities.SalesInvoices.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(SourceDocumentsEntities.SalesInvoices.toString()), SourceDocumentsRelationships.HAS_SALES_INVOICES.toString());

                    break;

                case "NumberOfEntries":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "TotalDebit":
                    try {
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Total.toString()), XMLElement, value);
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Total.toString()), SalesInvoicesEntities.Total.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Total.toString()), SalesInvoicesEntities.Total.toString());

                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(SalesInvoicesEntities.Total.toString()), SalesInvoicesRelationships.HAS_TOTAL.toString());
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Total.toString()), XMLElement, value);
                    }

                    break;

                case "TotalCredit":
                    try {
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Total.toString()), XMLElement, value);
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Total.toString()), SalesInvoicesEntities.Total.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Total.toString()), SalesInvoicesEntities.Total.toString());

                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(SalesInvoicesEntities.Total.toString()), SalesInvoicesRelationships.HAS_TOTAL.toString());
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Total.toString()), XMLElement, value);
                    }

                    break;

                default:
                    throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     * O INVOICETYPE NÃO É ATRIBUTO, É NODE
     */
    private void processAuditFileSourceDocumentsSalesInvoicesInvoice(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Invoice":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Invoice.toString()), SalesInvoicesEntities.Invoice.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Invoice.toString()), SalesInvoicesEntities.Invoice.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(SalesInvoicesEntities.Invoice.toString()), SalesInvoicesRelationships.HAS_INVOICE.toString());

                    break;

                case "InvoiceNo":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "ATCUD":
                    break;

                case "Hash":
                    break;

                case "HashControl":
                    break;

                case "Period":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "InvoiceDate":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "InvoiceType":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "SourceID":
                    break;

                case "EACCode":
                    break;

                case "SystemEntryDate":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "TransactionID":
                    this.driver.addRelationshipToTransaction(this.findNodeId(this.currentHead), value);

                    break;

                case "CustomerID":
                    this.driver.addRelationshipToCustomer(this.findNodeId(this.currentHead), value);

                    break;

                case "MovementEndTime":
                    break;
                case "MovementStartTime":
                    break;
                default:
                    throw new MapException();
            }

        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileSourceDocumentsSalesInvoicesInvoiceDocumentStatus(String XMLElement, String value) throws MapException {

    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileSourceDocumentsSalesInvoicesInvoiceLine(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Line":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Line.toString()), SalesInvoicesEntities.Line.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Line.toString()), SalesInvoicesEntities.Line.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(SalesInvoicesEntities.Line.toString()), SalesInvoicesRelationships.HAS_LINE.toString());

                    break;

                case "LineNumber":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "ProductCode":
                    this.driver.addRelationshipToProduct(this.findNodeId(this.currentHead), value);

                    break;

                case "ProductDescription":
                    break;

                case "Quantity":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "UnitOfMeasure":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "UnitPrice":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "TaxBase":
                    break;

                case "TaxPointDate":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "Discription":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "DebitAmount":
                    try {
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Amount.toString()), XMLElement, value);
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString());
                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesRelationships.HAS_AMOUNT.toString());

                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                        this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);
                        this.driver.addRelationship(this.findNodeId(SalesInvoicesEntities.Amount.toString()), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_DEBIT_AMOUNT.toString());
                    }

                    break;

                case "CreditAmount":
                    try {
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Amount.toString()), XMLElement, value);
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString());
                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesRelationships.HAS_AMOUNT.toString());

                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                        this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);
                        this.driver.addRelationship(this.findNodeId(SalesInvoicesEntities.Amount.toString()), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_CREDIT_AMOUNT.toString());
                    }

                    break;

                case "TaxExemptionReason":
                    break;

                case "TaxExemptionCode":
                    break;

                case "SettlementAmount":
                    try {
                        this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Amount.toString()), XMLElement, value);
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesEntities.Amount.toString());
                        this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(SalesInvoicesEntities.Amount.toString()), SalesInvoicesRelationships.HAS_AMOUNT.toString());

                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                        this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);
                        this.driver.addRelationship(this.findNodeId(SalesInvoicesEntities.Amount.toString()), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_SETTLEMENT_AMOUNT.toString());
                    }

                    break;

                default:
                    throw new MapException();
            }

        } catch (NodeException e) {
        }
    }

    /**
     * INCOMPLETO
     */
    private void processAuditFileSourceDocumentsSalesInvoicesInvoiceLineTax(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Tax":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.Tax.toString()), SalesInvoicesEntities.Tax.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.Tax.toString()), SalesInvoicesEntities.Tax.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(SalesInvoicesEntities.Tax.toString()), SalesInvoicesRelationships.HAS_TAX.toString());

                    break;

                case "TaxType":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_TAX_TYPE.toString());

                    break;

                case "TaxCountryRegion":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_TAX_COUNTRY_REGION.toString());

                    break;

                case "TaxCode":
                    this.driver.addAttributesToNode(this.findNodeId(SalesInvoicesEntities.Tax.toString()), XMLElement, value);

                    break;

                case "TaxPercentage":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_TAX_PERCENTAGE.toString());

                    break;

                case "TaxAmount":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), SalesInvoicesRelationships.HAS_TAX_AMOUNT.toString());

                    break;

                default:
                    throw new MapException();
            }

        } catch (NodeException e) {
        }
    }

    private void processAuditFileSourceDocumentsSalesInvoicesInvoiceDocumentTotals(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "DocumentTotals":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(SalesInvoicesEntities.DocumentTotals.toString()), SalesInvoicesEntities.DocumentTotals.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(SalesInvoicesEntities.DocumentTotals.toString()), SalesInvoicesEntities.DocumentTotals.toString());

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(SalesInvoicesEntities.DocumentTotals.toString()), SalesInvoicesRelationships.HAS_DOCUMENT_TOTALS.toString());

                    break;

                case "TaxPayable":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "NetTotal":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "GrossTotal":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                default:
                    throw new MapException();
            }

        } catch (NodeException e) {
        }
    }

    private long findNodeId(String XMLElement) throws NodeException {
        Iterator<GraphNode> iterator = this.graphNodeContainer.iterator();

        while (iterator.hasNext()) {
            GraphNode node = iterator.next();
            if (node.getXMLElement().equalsIgnoreCase(XMLElement)) {
                return node.getId();
            }
        }

        throw new NodeException("Node '" + XMLElement + "' not found!");
    }

    private void loadXMLStructure() {
        String previousHead = null;
        String currentHead = "AuditFile";

        this.driver.addXMLStructureNode(currentHead);

        /**
         * HEADER
         */
        previousHead = currentHead;
        currentHead = AuditFileEntities.Header.toString();

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(HeaderEntities.TaxAccountingBasis.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.TaxAccountingBasis.toString());
        this.driver.addXMLStructureNode(HeaderEntities.Company.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.Company.toString());
        this.driver.addXMLStructureNode(AddressEntities.CompanyAddress.toString());
        this.driver.addXMLStructureRelationShip(currentHead, AddressEntities.CompanyAddress.toString());
        this.driver.addXMLStructureNode(HeaderEntities.FiscalYear.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.FiscalYear.toString());
        this.driver.addXMLStructureNode(HeaderEntities.TaxEntity.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.TaxEntity.toString());
        this.driver.addXMLStructureNode(HeaderEntities.ProductCompany.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.ProductCompany.toString());
        this.driver.addXMLStructureNode(HeaderEntities.ProductSoftware.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.ProductSoftware.toString());
        this.driver.addXMLStructureNode(HeaderEntities.CompanyContact.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.CompanyContact.toString());

        currentHead = previousHead;
        previousHead = null;

        /**
         * MASTERFILES
         */

        previousHead = currentHead;
        currentHead = "MasterFiles";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        /**
         * CUSTOMER
         */

        previousHead = currentHead;
        currentHead = "Customer";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(CustomerEntities.Company.toString());
        this.driver.addXMLStructureRelationShip(currentHead, CustomerEntities.Company.toString());
        this.driver.addXMLStructureNode(CustomerEntities.BillingAddress.toString());
        this.driver.addXMLStructureRelationShip(currentHead, CustomerEntities.BillingAddress.toString());
        this.driver.addXMLStructureNode(CustomerEntities.ShipToAddress.toString());
        this.driver.addXMLStructureRelationShip(currentHead, CustomerEntities.ShipToAddress.toString());
        this.driver.addXMLStructureNode(CustomerEntities.Contacts.toString());
        this.driver.addXMLStructureRelationShip(currentHead, CustomerEntities.Contacts.toString());

        currentHead = previousHead;
        previousHead = "AuditFile";

        /**
         * Supplier
         */

        /**
         * Product
         */

        previousHead = currentHead;
        currentHead = "Product";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(ProductEntities.ProductType.toString());
        this.driver.addXMLStructureRelationShip(currentHead, ProductEntities.ProductType.toString());
        this.driver.addXMLStructureNode(ProductEntities.ProductGroup.toString());
        this.driver.addXMLStructureRelationShip(currentHead, ProductEntities.ProductGroup.toString());
        this.driver.addXMLStructureNode(ProductEntities.CustomDetails.toString());
        this.driver.addXMLStructureRelationShip(currentHead, ProductEntities.CustomDetails.toString());

        currentHead = previousHead;
        previousHead = "AuditFile";

        /**
         * TaxTable
         */

        previousHead = currentHead;
        currentHead = "TaxTable";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        previousHead = currentHead;
        currentHead = "TaxTableEntry";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(TaxTableEntities.TaxExpirationDate.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxExpirationDate.toString());

        currentHead = "AuditFile";
        previousHead = null;

        /**
         * SourceDocuments
         */

        previousHead = currentHead;
        currentHead = "SourceDocuments";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        previousHead = currentHead;
        currentHead = "SalesInvoices";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.Total.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Total.toString());

        previousHead = currentHead;
        currentHead = "Invoice";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.ATCUD.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.ATCUD.toString());

        previousHead = currentHead;
        currentHead = "DocumentStatus";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.SourceID.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.SourceID.toString());

        currentHead = previousHead;
        previousHead = "SalesInvoices";

        this.driver.addXMLStructureNode(SalesInvoicesEntities.Hash.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Hash.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.HashControl.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.HashControl.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.InvoiceType.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.InvoiceType.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.SpecialRegimes.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.SpecialRegimes.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.SourceID.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.SourceID.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.EACCode.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.EACCode.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Transaction.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Transaction.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Customer.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Customer.toString());


        previousHead = currentHead;
        currentHead = "ShipTo";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.Address.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Address.toString());

        currentHead = previousHead;
        previousHead = "SalesInvoices";

        previousHead = currentHead;
        currentHead = "ShipFrom";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.Address.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Address.toString());

        currentHead = previousHead;
        previousHead = "SalesInvoices";

        this.driver.addXMLStructureNode(SalesInvoicesEntities.MovementTime.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.MovementTime.toString());

        previousHead = currentHead;
        currentHead = "Line";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.OrderReferences.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.OrderReferences.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Product.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Product.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.References.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.References.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Tax.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Tax.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.TaxExemption.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.TaxExemption.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.CustomsInformation.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.CustomsInformation.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Amount.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Amount.toString());

        currentHead = previousHead;
        previousHead = "SalesInvoices";

        previousHead = currentHead;
        currentHead = "DocumentTotals";

        this.driver.addXMLStructureNode(currentHead);
        this.driver.addXMLStructureRelationShip(previousHead, currentHead);

        this.driver.addXMLStructureNode(SalesInvoicesEntities.Currency.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Currency.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Settlement.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Settlement.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.Payment.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.Payment.toString());

        currentHead = previousHead;
        previousHead = "SalesInvoices";

        this.driver.addXMLStructureNode(SalesInvoicesEntities.WithholdingTax.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.WithholdingTax.toString());
    }

    /**
     * Não utilizado
     */
    private void teste123() {
        String currentHead = null;

        this.driver.addXMLStructureNode("AuditFile");
        currentHead = "AuditFile";

        this.driver.addXMLStructureNode("Header");
        this.driver.addXMLStructureRelationShip(currentHead, "Header");
        currentHead = "Header";

        this.driver.addXMLStructureNode("AuditFileVersion");
        this.driver.addXMLStructureRelationShip(currentHead, "AuditFileVersion");
        this.driver.addXMLStructureNode("CompanyId");
        this.driver.addXMLStructureRelationShip(currentHead, "CompanyId");
        this.driver.addXMLStructureNode("TaxRegistrationNumber");
        this.driver.addXMLStructureRelationShip(currentHead, "TaxRegistrationNumber");
        this.driver.addXMLStructureNode("TaxAccountingBasis");
        this.driver.addXMLStructureRelationShip(currentHead, "TaxAccountingBasis");
        this.driver.addXMLStructureNode("CompanyName");
        this.driver.addXMLStructureRelationShip(currentHead, "CompanyName");
        this.driver.addXMLStructureNode("BusinessName");
        this.driver.addXMLStructureRelationShip(currentHead, "BusinessName");
        this.driver.addXMLStructureNode("FiscalYear");
        this.driver.addXMLStructureRelationShip(currentHead, "FiscalYear");
        this.driver.addXMLStructureNode("StartDate");
        this.driver.addXMLStructureRelationShip(currentHead, "StartDate");
        this.driver.addXMLStructureNode("EndDate");
        this.driver.addXMLStructureRelationShip(currentHead, "EndDate");
        this.driver.addXMLStructureNode("CurrencyCode");
        this.driver.addXMLStructureRelationShip(currentHead, "CurrencyCode");
        this.driver.addXMLStructureNode("DateCreated");
        this.driver.addXMLStructureRelationShip(currentHead, "DateCreated");
        this.driver.addXMLStructureNode("TaxEntity");
        this.driver.addXMLStructureRelationShip(currentHead, "TaxEntity");
        this.driver.addXMLStructureNode("ProductCompanyTaxID");
        this.driver.addXMLStructureRelationShip(currentHead, "ProductCompanyTaxID");
        this.driver.addXMLStructureNode("SoftwareCertificateNumber");
        this.driver.addXMLStructureRelationShip(currentHead, "SoftwareCertificateNumber");
        this.driver.addXMLStructureNode("ProductID");
        this.driver.addXMLStructureRelationShip(currentHead, "ProductID");
        this.driver.addXMLStructureNode("ProductVersion");
        this.driver.addXMLStructureRelationShip(currentHead, "ProductVersion");
        this.driver.addXMLStructureNode("HeaderComment");
        this.driver.addXMLStructureRelationShip(currentHead, "HeaderComment");
        this.driver.addXMLStructureNode("Telephone");
        this.driver.addXMLStructureRelationShip(currentHead, "Telephone");
        this.driver.addXMLStructureNode("Fax");
        this.driver.addXMLStructureRelationShip(currentHead, "Fax");
        this.driver.addXMLStructureNode("Email");
        this.driver.addXMLStructureRelationShip(currentHead, "Email");
        this.driver.addXMLStructureNode("Website");
        this.driver.addXMLStructureRelationShip(currentHead, "Website");

        this.driver.addXMLStructureNode("CompanyAddress");
        this.driver.addXMLStructureRelationShip(currentHead, "CompanyAddress");
        currentHead = "CompanyAddress";

        this.driver.addXMLStructureNode("BuildingNumber");
        this.driver.addXMLStructureRelationShip(currentHead, "BuildingNumber");
        this.driver.addXMLStructureNode("StreetName");
        this.driver.addXMLStructureRelationShip(currentHead, "StreetName");
        this.driver.addXMLStructureNode("AddressDetail");
        this.driver.addXMLStructureRelationShip(currentHead, "AddressDetail");
        this.driver.addXMLStructureNode("City");
        this.driver.addXMLStructureRelationShip(currentHead, "City");
        this.driver.addXMLStructureNode("PostalCode");
        this.driver.addXMLStructureRelationShip(currentHead, "PostalCode");
        this.driver.addXMLStructureNode("Region");
        this.driver.addXMLStructureRelationShip(currentHead, "Region");
        this.driver.addXMLStructureNode("Country");
        this.driver.addXMLStructureRelationShip(currentHead, "Country");
    }

    private LinkedList<String> getHeads() {
        Heads[] headsEnum = Heads.values();
        LinkedList<String> heads = new LinkedList<>();

        for (int i = 0; i < headsEnum.length; i++) {
            heads.add(String.valueOf(headsEnum[i]));
        }

        return heads;
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

    private enum AuditFileEntities {
        Header, MasterFiles, GeneralLedgerEntries, SourceDocuments
    }

    private enum AuditFileRelationships {
        HAS_HEADER, HAS_MASTER_FILES, HAS_GENERAL_LEDGER_ENTRIES, HAS_SOURCE_DOCUMENTS
    }

    private enum AddressEntities {
        CompanyAddress, BillingAddress, ShipToAddress, ShipFromAddress
    }

    private enum AddressRelationships {
        HAS_COMPANY_ADDRESS, HAS_BILLING_ADDRESS, HAS_SHIP_TO_ADDRESS,
        HAS_BUILDING_NUMBER, HAS_STREET_NAME, HAS_CITY, HAS_POSTAL_CODE, HAS_REGION, HAS_COUNTRY
    }

    private enum HeaderEntities {
        TaxAccountingBasis, Company, FiscalYear, TaxEntity, ProductCompany, ProductSoftware, CompanyContact
    }

    private enum HeaderRelationships {
        HAS_TAX_ACCOUNTING_BASIS, HAS_COMPANY, HAS_COMPANY_ID, HAS_TAX_REGISTRATION_NUMBER,
        HAS_BUSINESS_NAME,
        HAS_FISCAL_YEAR, HAS_START_DATE, HAS_END_DATE, HAS_TAX_ENTITY,
        HAS_PRODUCT, HAS_PRODUCT_COMPANY, HAS_CONTACT,
        HAS_TELEPHONE, HAS_FAX, HAS_EMAIL, HAS_WEBSITE

    }

    private enum MasterFilesEntities {
        GeneralLedgerAccounts, Customer, Supplier, Product, TaxTable
    }

    private enum MasterFilesRelationships {
        HAS_GENERAL_LEDGER_ACCOUNTS, HAS_CUSTOMER, HAS_SUPPLIER,
        HAS_PRODUCT, HAS_TAX_TABLE
    }

    private enum GeneralLedgerAccountsEntities {
        Account, OpeningBalance, ClosingBalance, Group, TaxonomyCode,
    }

    private enum GeneralLedgerAccountsRelationships {
        HAS_ACCOUNT, HAS_OPENING_BALANCE, HAS_CLOSING_BALANCE,
        HAS_GROUP, HAS_TAXONOMY_CODE,
    }

    private enum CustomerEntities {
        Account, Company, BillingAddress, ShipToAddress, Contacts
    }

    private enum CustomerRelationships {
        HAS_CUSTOMER_TAX_ID, HAS_COMPANY, HAS_BILLING_ADDRESS, HAS_BUILDING_NUMBER, HAS_STREET_NAME,
        HAS_CITY, HAS_POSTAL_CODE, HAS_REGION, HAS_COUNTRY, HAS_SHIP_TO_ADDRESS,
        HAS_CONTACTS, HAS_TELEPHONE, HAS_EMAIL, HAS_FAX, HAS_WEBSITE
    }

    private enum ProductEntities {
        ProductType, ProductGroup, CustomDetails
    }

    private enum ProductRelationships {
        HAS_PRODUCT_TYPE, HAS_PRODUCT_GROUP, HAS_CUSTOMS_DETAILS
    }

    private enum TaxTableEntities {
        TaxExpirationDate
    }

    private enum TaxTableRelationships {
        HAS_TAX_TABLE_ENTRY, HAS_TAX_TYPE, HAS_TAX_COUNTRY_REGION, HAS_TAX_EXPIRATION_DATE, HAS_TAX_PERCENTAGE, HAS_TAX_AMOUNT
    }

    private enum SourceDocumentsEntities {
        SalesInvoices, MovementOfGoods, WorkingDocuments, Payments
    }

    private enum SourceDocumentsRelationships {
        HAS_SALES_INVOICES, HAS_MOVEMENT_OF_GOODS, HAS_WORKING_DOCUMENTS, HAS_PAYMENTS
    }

    private enum SalesInvoicesEntities {
        Total, Invoice, ATCUD, DocumentStatus, Hash, HashControl, InvoiceType, SpecialRegimes,
        SourceID, EACCode, Transaction, Customer, ShipTo, ShipFrom, Address,
        MovementTime, Line, OrderReferences, Product, Amount, Tax, References, TaxTableEntry, TaxExemption,
        CustomsInformation, DocumentTotals, Currency, Settlement, Payment, WithholdingTax
    }

    private enum SalesInvoicesRelationships {
        HAS_TOTAL, HAS_INVOICE, HAS_LINE, HAS_AMOUNT, HAS_DEBIT_AMOUNT, HAS_CREDIT_AMOUNT, HAS_SETTLEMENT_AMOUNT, HAS_TAX, HAS_TAX_TYPE,
        HAS_TAX_COUNTRY_REGION, HAS_TAX_PERCENTAGE, HAS_TAX_AMOUNT, HAS_DOCUMENT_TOTALS
    }
}
