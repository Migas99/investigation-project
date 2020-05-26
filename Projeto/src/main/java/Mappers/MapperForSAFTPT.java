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
    private Stack<String> stack;
    private String previousHead = null;
    private String currentHead = null;
    private LinkedList<GraphNode> graphNodeContainer = new LinkedList<>();

    public MapperForSAFTPT(Neo4j driver) {
        this.driver = driver;
        this.heads = this.getHeads();
        this.stack = new Stack<>();

        if (!this.driver.isXMLStructureLoaded()) {
            this.loadXMLStructure();
        }
    }

    public void processStartElement(String XMLElement, String value) {

        if (this.heads.contains(XMLElement)) {
            if (!this.stack.empty()) {
                this.previousHead = this.stack.peek();
            }

            this.currentHead = XMLElement;
            this.stack.push(XMLElement);
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
                    this.processAuditFileMasterFilesGeneralLedgerAccounts(XMLElement, value);
                    break;
                case "Account":
                    this.processAuditFileMasterFilesGeneralLedgerAccountsAccount(XMLElement, value);
                    break;
                case "Customer":
                    break;
                case "BillingAddress":
                    break;
                case "ShipToAddress":
                    break;
                case "ProductSoftware":
                    break;
                case "TaxTable":
                    break;
                case "TaxCodeDetails":
                    break;
                case "SourceDocuments":
                    break;
                case "SalesInvoices":
                    break;
                case "Line":
                    break;
                case "Tax":
                    break;
                default:
                    break;
            }
        } catch (MapException e) {
            System.exit(1);
        }
    }

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

    private void processAuditFile(String XMLElement, String value) throws MapException {
        try {
            if ("AuditFile".equalsIgnoreCase(XMLElement)) {
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);
            } else {
                throw new MapException();
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileHeader(String XMLElement, String value) {
        try {
            switch (XMLElement) {

                case "Header":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), AuditFileRelationships.HAS_HEADER.toString());

                    break;

                case "AuditFileVersion":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "CompanyID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    break;

                case "TaxRegistrationNumber":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    break;

                case "TaxAccountingBasis":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.TaxAccountingBasis.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderEntities.TaxAccountingBasis.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderRelationships.HAS_TAX_ACCOUNTING_BASIS.toString());

                    break;

                case "CompanyName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.Company.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.Company.toString()), HeaderEntities.Company.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.Company.toString()), XMLElement, value);
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
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_BUSINESS_NAME.toString());

                    break;

                case "FiscalYear":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.FiscalYear.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.FiscalYear.toString()), HeaderEntities.FiscalYear.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.FiscalYear.toString()), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.FiscalYear.toString()), HeaderRelationships.HAS_FISCAL_YEAR.toString());

                    break;

                case "StartDate":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.FiscalYear.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_START_DATE.toString());

                    break;

                case "EndDate":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
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
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.TaxEntity.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderEntities.TaxEntity.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxEntity.toString()), XMLElement, value);
                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderRelationships.HAS_TAX_ENTITY.toString());

                    break;

                case "ProductCompanyTaxID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.ProductCompany.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.ProductCompany.toString()), HeaderEntities.ProductCompany.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), XMLElement, value);

                    break;

                case "SoftwareCertificateNumber":
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), XMLElement, value);

                    break;

                case "ProductID":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.ProductSoftware.toString(), value));
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
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_TELEPHONE.toString());

                    break;

                case "Fax":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_FAX.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_FAX.toString());
                    }

                    break;

                case "Email":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_EMAIL.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_EMAIL.toString());
                    }

                    break;

                case "Website":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    try {
                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_WEBSITE.toString());
                    } catch (NodeException e) {
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));
                        this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyContact.toString()), HeaderRelationships.HAS_CONTACT.toString());

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_WEBSITE.toString());
                    }

                    break;

                default:

                    break;
            }
        } catch (NodeException e) {
        }
    }

    private void processAuditFileHeaderCompanyAddress(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "CompanyAddress":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyAddress.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(HeaderEntities.CompanyAddress.toString()), HeaderEntities.CompanyAddress.toString());

                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.CompanyAddress.toString()), HeaderEntities.CompanyAddress.toString(), value);
                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(HeaderEntities.CompanyAddress.toString()), HeaderRelationships.HAS_COMPANY_ADDRESS.toString());

                    break;

                case "BuildingNumber":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_BUILDING_NUMBER.toString());

                    break;

                case "StreetName":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_STREET_NAME.toString());

                    break;

                case "AddressDetail":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "City":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_CITY.toString());

                    break;

                case "PostalCode":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_POSTAL_CODE.toString());

                    break;

                case "Region":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_REGION.toString());

                    break;

                case "Country":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(XMLElement), HeaderRelationships.HAS_COUNTRY.toString());

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
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
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
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), MasterFilesEntities.GeneralLedgerAccounts.toString(), value));
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
     *
     * @param XMLElement
     * @param value
     * @throws MapException
     */
    private void processAuditFileMasterFilesGeneralLedgerAccountsAccount(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Account":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), MasterFilesRelationships.HAS_ACCOUNT.toString());

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

    private void processAuditFileMasterFilesCustomer(String XMLElement, String value) throws MapException {
        try {
            switch (XMLElement) {

                case "Customer":
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(XMLElement), XMLElement);

                    this.driver.addRelationship(this.findNodeId(this.previousHead), this.findNodeId(XMLElement), MasterFilesRelationships.HAS_CUSTOMER.toString());

                    break;

                case "CustomerID":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "AccountID":
                    break;

                case "CustomerTaxID":
                    break;

                case "CompanyName":


                    break;

                case "Contact":

                    break;

                case "Telephone":

                    break;

                case "Fax":

                    break;

                case "Email":

                    break;

                case "Website":
                    break;

                case "SelfBillingIndicator":
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
        this.driver.addXMLStructureNode(HeaderEntities.CompanyAddress.toString());
        this.driver.addXMLStructureRelationShip(currentHead, HeaderEntities.CompanyAddress.toString());
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

        this.driver.addXMLStructureNode(TaxTableEntities.TaxType.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxType.toString());
        this.driver.addXMLStructureNode(TaxTableEntities.TaxCountryRegion.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxCountryRegion.toString());
        this.driver.addXMLStructureNode(TaxTableEntities.TaxExpirationDate.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxExpirationDate.toString());
        this.driver.addXMLStructureNode(TaxTableEntities.TaxPercentage.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxPercentage.toString());
        this.driver.addXMLStructureNode(TaxTableEntities.TaxAmount.toString());
        this.driver.addXMLStructureRelationShip(currentHead, TaxTableEntities.TaxAmount.toString());

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
        this.driver.addXMLStructureNode(SalesInvoicesEntities.SystemEntryDate.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.SystemEntryDate.toString());
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
        this.driver.addXMLStructureNode(SalesInvoicesEntities.TaxTableEntry.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.TaxTableEntry.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.TaxExemption.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.TaxExemption.toString());
        this.driver.addXMLStructureNode(SalesInvoicesEntities.CustomsInformation.toString());
        this.driver.addXMLStructureRelationShip(currentHead, SalesInvoicesEntities.CustomsInformation.toString());

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

    private enum HeaderEntities {
        TaxAccountingBasis, Company, CompanyAddress, FiscalYear, TaxEntity, ProductCompany, ProductSoftware, CompanyContact
    }

    private enum HeaderRelationships {
        HAS_TAX_ACCOUNTING_BASIS, HAS_COMPANY, HAS_COMPANY_ID, HAS_TAX_REGISTRATION_NUMBER,
        HAS_BUSINESS_NAME, HAS_COMPANY_ADDRESS, HAS_BUILDING_NUMBER, HAS_STREET_NAME,
        HAS_CITY, HAS_POSTAL_CODE, HAS_REGION, HAS_COUNTRY,
        HAS_FISCAL_YEAR, HAS_START_DATE, HAS_END_DATE, HAS_TAX_ENTITY,
        HAS_PRODUCT, HAS_PRODUCT_COMPANY, HAS_CONTACT,
        HAS_TELEPHONE, HAS_FAX, HAS_EMAIL, HAS_WEBSITE

    }

    private enum MasterFilesEntities {
        GeneralLedgerAccounts, Account, OpeningBalance, ClosingBalance, Group, TaxonomyCode, Customer, Supplier, Product, TaxTable
    }

    private enum MasterFilesRelationships {
        HAS_GENERAL_LEDGER_ACCOUNTS, HAS_ACCOUNT, HAS_OPENING_BALANCE, HAS_CLOSING_BALANCE,
        HAS_GROUP, HAS_TAXONOMY_CODE, HAS_CUSTOMER
    }

    private enum CustomerEntities {
        Account, Company, BillingAddress, ShipToAddress, Contacts
    }

    private enum CustomerRelationships {
        HAS_CUSTOMER_TAX_ID, HAS_COMPANY, HAS_BILLING_ADDRESS, HAS_SHIP_TO_ADDRESS, HAS_CONTACTS
    }

    private enum ProductEntities {
        ProductType, ProductGroup, CustomDetails
    }

    private enum ProductRelationships {

    }

    private enum TaxTableEntities {
        TaxType, TaxCountryRegion, TaxExpirationDate, TaxPercentage, TaxAmount
    }

    private enum SourceDocumentsEntities {

    }

    private enum SalesInvoicesEntities {
        Total, Invoice, ATCUD, DocumentStatus, Hash, HashControl, InvoiceType, SpecialRegimes,
        SourceID, EACCode, SystemEntryDate, Transaction, Customer, ShipTo, ShipFrom, Address,
        MovementTime, Line, OrderReferences, Product, References, TaxTableEntry, TaxExemption,
        CustomsInformation, DocumentTotals, Currency, Settlement, Payment, WithholdingTax
    }
}
