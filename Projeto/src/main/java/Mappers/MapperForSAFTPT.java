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
    private LinkedList<GraphNode> graphXMLStructureNodeContainer = new LinkedList<>();
    private LinkedList<GraphNode> graphNodeContainer = new LinkedList<>();

    public MapperForSAFTPT(Neo4j driver) {
        this.driver = driver;
        this.heads = this.getHeads();
        this.stack = new Stack<>();
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
                    break;
                case "Customer":
                    break;
                case "BillingAddress":
                    break;
                case "Product":
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
                this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement));
                this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                this.driver.addRelationship(this.findNodeId(XMLElement), this.findStructureId(XMLElement), XMLStructureRelationships.TYPE_OF.toString());
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
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));

                    this.driver.addRelationship(this.findNodeId(XMLElement), this.findStructureId(XMLElement), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.previousHead), this.findStructureId(XMLElement), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(XMLElement), this.findStructureId(this.previousHead), XMLStructureRelationships.CHILD_OF.toString());

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
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.TaxAccountingBasis.toString()), HeaderEntities.TaxAccountingBasis.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.TaxAccountingBasis.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), this.findStructureId(HeaderEntities.TaxAccountingBasis.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.TaxAccountingBasis.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.TaxAccountingBasis.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxAccountingBasis.toString()), HeaderRelationships.HAS_TAX_ACCOUNTING_BASIS.toString());

                    break;

                case "CompanyName":
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.Company.toString()), HeaderEntities.Company.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.Company.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.Company.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.Company.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

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

                    this.driver.addRelationship(this.findNodeId("CompanyName"), this.findNodeId(XMLElement), HeaderRelationships.HAS_BUSINESS_NAME.toString());

                    break;

                case "FiscalYear":
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.FiscalYear.toString()), HeaderEntities.FiscalYear.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.FiscalYear.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.FiscalYear.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.FiscalYear.toString()), this.findStructureId(HeaderEntities.FiscalYear.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.FiscalYear.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.FiscalYear.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

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
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.TaxEntity.toString()), HeaderEntities.TaxEntity.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.TaxEntity.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.TaxEntity.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.TaxEntity.toString()), this.findStructureId(HeaderEntities.TaxEntity.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.TaxEntity.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.TaxEntity.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

                    this.driver.addRelationship(this.findNodeId(this.currentHead), this.findNodeId(HeaderEntities.TaxEntity.toString()), HeaderRelationships.HAS_TAX_ENTITY.toString());


                    break;

                case "ProductCompanyTaxID":
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.ProductCompany.toString()), HeaderEntities.ProductCompany.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.ProductCompany.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.ProductCompany.toString()), this.findStructureId(HeaderEntities.ProductCompany.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.ProductCompany.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.ProductCompany.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

                    break;

                case "SoftwareCertificateNumber":
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.ProductCompany.toString()), XMLElement, value);

                    break;

                case "ProductID":
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.Product.toString()), HeaderEntities.Product.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.Product.toString(), value));
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.Product.toString()), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Product.toString()), this.findStructureId(HeaderEntities.Product.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.currentHead), this.findStructureId(HeaderEntities.Product.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.Product.toString()), this.findStructureId(this.currentHead), XMLStructureRelationships.CHILD_OF.toString());

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.TaxEntity.toString()), this.findNodeId(HeaderEntities.Product.toString()), HeaderRelationships.HAS_PRODUCT.toString());
                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Product.toString()), this.findNodeId(HeaderEntities.ProductCompany.toString()), HeaderRelationships.HAS_PRODUCT_COMPANY.toString());

                    break;

                case "ProductVersion":
                    this.driver.addAttributesToNode(this.findNodeId(HeaderEntities.Product.toString()), XMLElement, value);

                    break;

                case "HeaderComment":
                    this.driver.addAttributesToNode(this.findNodeId(this.currentHead), XMLElement, value);

                    break;

                case "Telephone":
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(HeaderEntities.Company.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.CHILD_OF.toString());

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
                        this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.TYPE_OF.toString());
                        this.driver.addRelationship(this.findStructureId(HeaderEntities.Company.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                        //this.driver.addRelationship(this.findStructureId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.CHILD_OF.toString());

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
                        this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.TYPE_OF.toString());
                        this.driver.addRelationship(this.findStructureId(HeaderEntities.Company.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                        //this.driver.addRelationship(this.findStructureId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.CHILD_OF.toString());

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
                        this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(HeaderEntities.CompanyContact.toString()), HeaderEntities.CompanyContact.toString()));
                        this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), HeaderEntities.CompanyContact.toString()));

                        this.driver.addRelationship(this.findNodeId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.TYPE_OF.toString());
                        this.driver.addRelationship(this.findStructureId(HeaderEntities.Company.toString()), this.findStructureId(HeaderEntities.CompanyContact.toString()), XMLStructureRelationships.HAS_CHILD.toString());
                        //this.driver.addRelationship(this.findStructureId(HeaderEntities.CompanyContact.toString()), this.findStructureId(HeaderEntities.Company.toString()), XMLStructureRelationships.CHILD_OF.toString());

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
                    this.graphXMLStructureNodeContainer.add(new GraphNode(this.driver.addNode(XMLElement), XMLElement));
                    this.graphNodeContainer.add(new GraphNode(this.driver.addNode(), XMLElement, value));
                    this.driver.addAttributesToNode(this.findNodeId(XMLElement), XMLElement, value);

                    this.driver.addRelationship(this.findNodeId(XMLElement), this.findStructureId(XMLElement), XMLStructureRelationships.TYPE_OF.toString());
                    this.driver.addRelationship(this.findStructureId(this.previousHead), this.findStructureId(XMLElement), XMLStructureRelationships.HAS_CHILD.toString());
                    //this.driver.addRelationship(this.findStructureId(XMLElement), this.findStructureId(this.previousHead), XMLStructureRelationships.CHILD_OF.toString());

                    this.driver.addRelationship(this.findNodeId(HeaderEntities.Company.toString()), this.findNodeId(XMLElement), HeaderRelationships.HAS_COMPANY_ADDRESS.toString());

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

    private long findStructureId(String XMLElement) throws NodeException {
        Iterator<GraphNode> iterator = this.graphXMLStructureNodeContainer.iterator();

        while (iterator.hasNext()) {
            GraphNode node = iterator.next();
            if (node.getXMLElement().equalsIgnoreCase(XMLElement)) {
                return node.getId();
            }
        }

        throw new NodeException("Node '" + XMLElement + "' not found!");
    }

    private void createXMLStructure() {
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

    private enum XMLStructureRelationships {
        TYPE_OF, CHILD_OF, HAS_CHILD
    }

    private enum AuditFileEntities {
        Header, MasterFiles, GeneralLedgerEntries, SourceDocuments
    }

    private enum AuditFileRelationships {
        HAS_HEADER, HAS_MASTER_FILES, HAS_GENERAL_LEDGER_ENTRIES, HAS_SOURCE_DOCUMENTS
    }

    private enum HeaderEntities {
        TaxAccountingBasis, Company, CompanyAddress, FiscalYear, TaxEntity, ProductCompany, Product, CompanyContact
    }

    private enum HeaderRelationships {
        HAS_TAX_ACCOUNTING_BASIS, HAS_COMPANY, HAS_COMPANY_ID, HAS_TAX_REGISTRATION_NUMBER,
        HAS_BUSINESS_NAME, HAS_COMPANY_ADDRESS, HAS_BUILDING_NUMBER, HAS_STREET_NAME,
        HAS_CITY, HAS_POSTAL_CODE, HAS_REGION, HAS_COUNTRY,
        HAS_FISCAL_YEAR, HAS_START_DATE, HAS_END_DATE, HAS_TAX_ENTITY,
        HAS_PRODUCT, HAS_PRODUCT_COMPANY, HAS_CONTACT,
        HAS_TELEPHONE, HAS_FAX, HAS_EMAIL, HAS_WEBSITE

    }

    private enum MasterFilesEntities{
        GeneralLedgerAccounts, Customer, Supplier, Product, TaxTable
    }
}
