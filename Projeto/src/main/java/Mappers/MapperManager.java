package Mappers;

import Database.Neo4j;
import Enums.EnumsOfElements;
import Enums.EnumsOfEntities;
import Exceptions.MapException;
import Exceptions.NodeException;
import Models.TaxTable;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author José Miguel Ribeiro Cunha
 */
public class MapperManager {

    private final Neo4j driver;
    private int depth;
    private final LinkedList<String> sequenceElements;
    private final LinkedList<ManageSequence> manageSequenceElements;
    private final LinkedList<GraphNode> nodesContainer;
    private TaxTable table;

    public MapperManager(Neo4j driver) {
        this.driver = driver;
        this.depth = -1;
        this.sequenceElements = new LinkedList<>();
        this.manageSequenceElements = new LinkedList<>();
        this.nodesContainer = new LinkedList<>();
        this.loadSequenceElements();
        this.loadIdentitiesNodes();
    }

    /**
     * Método responsável por processar um StartElement e o seu valor
     *
     * @param XMLElement o StartElement
     * @param value      o seu valor
     */
    public void processStartElement(String XMLElement, String value) {

        try {

            if (this.sequenceElements.contains(XMLElement)) {
                this.manageSequenceElements.add(new ManageSequence(XMLElement));
                this.depth++;
            }

            int count = 0;
            this.processRootElement(XMLElement, value, count);

        } catch (MapException e) {
            e.getError();
            System.exit(1);
        } catch (NodeException e) {
            e.getError();
            System.exit(1);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("\nElement not mapped found!\nElement: " + XMLElement + "\n.");
            System.exit(1);
        }
    }

    /**
     * Método responsável por processar um EndElement
     *
     * @param XMLElement o EndElement
     */
    public void processEndElement(String XMLElement) {
        if (this.sequenceElements.contains(XMLElement)) {

            if (XMLElement.equalsIgnoreCase(EnumsOfElements.RootElement.AuditFile)) {
                this.driver.close();

            } else {
                this.depth--;

                int remove = this.manageSequenceElements.removeLast().getChildrenCount();

                while (remove > 0) {
                    this.nodesContainer.removeLast();
                    remove--;
                }

            }
        }
    }

    /**
     * Método responsável por carregar para uma LinkedList os elementos do tipo sequência
     */
    private void loadSequenceElements() {
        EnumsOfElements.SequenceElements[] enums = EnumsOfElements.SequenceElements.values();
        for (int i = 0; i < enums.length; i++) {
            this.sequenceElements.add(enums[i].toString());
        }
    }

    /**
     * Método responsável por criar os nós de identidade na base de dados
     */
    private void loadIdentitiesNodes() {
        EnumsOfEntities.EntitiesValues[] enums = EnumsOfEntities.EntitiesValues.values();
        for (int i = 0; i < enums.length; i++) {
            this.driver.addIdentityNode(enums[i].toString());
        }
    }

    /**
     * Método responsável por encontrar um o id de um nó, dando o elemento a qual este se encontra associado
     *
     * @param XMLElement o elemento ao qual o nó se encontra associado
     * @return o id do nó
     * @throws NodeException caso não seja encontrado um nó associado ao elemento passado como argumento
     */
    private long findNodeId(String XMLElement) throws NodeException {
        Iterator<GraphNode> iterator = this.nodesContainer.iterator();

        while (iterator.hasNext()) {
            GraphNode node = iterator.next();
            if (node.getXMLElement().equalsIgnoreCase(XMLElement)) {
                return node.getId();
            }
        }

        throw new NodeException(XMLElement);
    }

    private long addNode() {
        this.manageSequenceElements.getLast().incrementChildren();
        return this.driver.addNode();
    }

    private long addNode(String attribute, int value) {
        this.manageSequenceElements.getLast().incrementChildren();
        return this.driver.addNode(attribute, value);
    }

    private long addNode(String attribute, double value) {
        this.manageSequenceElements.getLast().incrementChildren();
        return this.driver.addNode(attribute, value);
    }

    private long addNode(String attribute, String value) {
        this.manageSequenceElements.getLast().incrementChildren();
        return this.driver.addNode(attribute, value);
    }

    private void processRootElement(String XMLElement, String value, int count) throws MapException, NodeException {

        if (EnumsOfElements.RootElement.AuditFile.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

            if (EnumsOfElements.RootElement.AuditFile.equalsIgnoreCase(XMLElement)) {
                //Não é necessário mapear

            } else {

                this.processAuditFileChildren(XMLElement, value, count);

            }

        } else {

            throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

        }
    }

    private void processAuditFileChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        count++;

        switch (this.manageSequenceElements.get(count).getXMLElement()) {

            case EnumsOfElements.AuditFile.Header:

                if (EnumsOfElements.AuditFile.Header.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo FileInformation
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.FileInformation));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FileInformation), EnumsOfEntities.Entities.FileInformation);

                } else {

                    this.processHeaderChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.MasterFiles:

                if (EnumsOfElements.AuditFile.MasterFiles.equalsIgnoreCase(XMLElement)) {
                    //Não é necessário mapear

                } else {

                    this.processMasterFilesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.GeneralLedgerEntries:

                if (EnumsOfElements.AuditFile.GeneralLedgerEntries.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo GeneralLedgerEntries
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.GeneralLedgerEntries));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries), EnumsOfEntities.Entities.GeneralLedgerEntries);

                } else {

                    this.processGeneralLedgerEntriesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.SourceDocuments:

                if (EnumsOfElements.AuditFile.SourceDocuments.equalsIgnoreCase(XMLElement)) {
                    //Não é necessário mapear

                } else {

                    this.processSourceDocumentsChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
        }
    }

    private void processHeaderChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Header.AuditFileVersion:
                    //Adicionamos como atributo ao nó FileInformation
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.FileInformation), XMLElement, value);

                    break;

                case EnumsOfElements.Header.CompanyID:
                    //Criamos um nó com o atributo CompanyID e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    break;

                case EnumsOfElements.Header.TaxRegistrationNumber:
                    //Criamos um nó com o atributo TaxRegistrationNumber e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Integer.valueOf(value)), XMLElement));

                    break;

                case EnumsOfElements.Header.TaxAccountingBasis:
                    //Criamos um nó com o atributo TaxAccountingBasis e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(XMLElement), EnumsOfEntities.FileInformationRelationships.HAS_TAX_ACCOUNTING_BASIS);

                    break;

                case EnumsOfElements.Header.CompanyName:
                    //Criamos o nó do tipo Company com o atributo CompanyName e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), EnumsOfEntities.Entities.Company));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Company), EnumsOfEntities.Entities.Company);

                    //Criamos agora uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(EnumsOfEntities.Entities.Company), EnumsOfEntities.FileInformationRelationships.HAS_COMPANY);

                    //Criamos agora uma relação com nó previamente criado que contêm o id da empresa
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfElements.Header.CompanyID), EnumsOfEntities.CompanyRelationships.HAS_COMPANY_ID);

                    //Criamos outra relação com o nó que contêm o número de identificação fiscal da empresa
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfElements.Header.TaxRegistrationNumber), EnumsOfEntities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER);

                    break;

                case EnumsOfElements.Header.BussinessName:
                    //Criamos o nó com o atributo BussinessName e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Criamos uma relação com a empresa
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(XMLElement), EnumsOfEntities.CompanyRelationships.HAS_BUSINESS_NAME);

                    break;

                case EnumsOfElements.Header.FiscalYear:
                    //Criamos o nó do tipo FiscalYear com o atributo FiscalYear e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Integer.valueOf(value)), EnumsOfEntities.Entities.FiscalYear));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FiscalYear), EnumsOfEntities.Entities.FiscalYear);

                    //Criamos agora uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(EnumsOfEntities.Entities.FiscalYear), EnumsOfEntities.FileInformationRelationships.HAS_FISCAL_YEAR);

                    break;

                case EnumsOfElements.Header.StartDate:
                    //Criamos um novo nó com o atributo StartDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Criamos uma relação com o nó FiscalYear
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FiscalYear), this.findNodeId(XMLElement), EnumsOfEntities.FiscalYearRelationships.HAS_START_DATE);

                    break;

                case EnumsOfElements.Header.EndDate:
                    //Criamos um novo nó com o atributo EndDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Criamos uma relação com o ano fiscal
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FiscalYear), this.findNodeId(XMLElement), EnumsOfEntities.FiscalYearRelationships.HAS_END_DATE);

                    break;

                case EnumsOfElements.Header.CurrencyCode:
                    //Criamos um novo nó com o atributo CurrencyCode e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(XMLElement), EnumsOfEntities.FileInformationRelationships.HAS_CURRENCY_CODE);

                    break;

                case EnumsOfElements.Header.DateCreated:
                    //Criamos um novo nó com o atributo DateCreated e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(XMLElement), EnumsOfEntities.FileInformationRelationships.HAS_DATE_CREATED);

                    break;

                case EnumsOfElements.Header.TaxEntity:
                    //Criamos um novo nó com o atributo TaxEntity e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(XMLElement), EnumsOfEntities.FileInformationRelationships.HAS_TAX_ENTITY);

                    break;

                case EnumsOfElements.Header.ProductCompanyTaxID:
                    //Criamos um novo nó com o atributo ProductCompanyTaxID e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    break;

                case EnumsOfElements.Header.SoftwareCertificateNumber:
                    //Adicionamos como atributo ao nó que contêm o ProductCompanyTaxID
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Header.ProductCompanyTaxID), XMLElement, Integer.valueOf(value));

                    break;

                case EnumsOfElements.Header.ProductID:
                    //Criamos um novo nó do tipo Product com o atributo ProductID e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), EnumsOfEntities.Entities.Product));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Product), EnumsOfEntities.Entities.Product);

                    //Criamos agora uma relação com o FileInformation
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.FileInformation), this.findNodeId(EnumsOfEntities.Entities.Product), EnumsOfEntities.FileInformationRelationships.HAS_PRODUCT);

                    //Criamos agora uma relação com nó que contêm a empresa que criou o produto
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Product), this.findNodeId(EnumsOfElements.Header.ProductCompanyTaxID), EnumsOfEntities.ProductRelationships.HAS_COMPANY);

                    break;

                case EnumsOfElements.Header.ProductVersion:
                    //Adicionamos como atributo ao nó Product
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Product), XMLElement, value);

                    break;

                case EnumsOfElements.Header.HeaderComment:
                    //Adicionamos como atributo ao nó FileInformation
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.FileInformation), XMLElement, value);

                    break;

                case EnumsOfElements.Header.Telephone:
                    //Criamos o nó do tipo Contacts
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                    //Adicionamos uma relação deste nó com a empresa
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CompanyRelationships.HAS_CONTACTS);

                    //Criamos agora o nó do Telephone
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó do Telephone com o Contacts
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE);

                    break;

                case EnumsOfElements.Header.Fax:
                    //Criamos agora o nó do Fax
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó do Fax com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado

                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com a empresa
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CompanyRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó do Fax com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);
                    }

                    break;

                case EnumsOfElements.Header.Email:
                    //Criamos agora o nó do Email
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó do Email com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado

                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com a empresa
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CompanyRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó do Email com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);
                    }

                    break;

                case EnumsOfElements.Header.Website:
                    //Criamos agora o nó do Website
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó do Website com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado

                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com a empresa
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CompanyRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó do Website com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);
                    }

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.Header.CompanyAddress.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.Header.CompanyAddress.equals(XMLElement)) {
                    //Criamos o nó do tipo Address
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                    //Criamos agora uma relação com a empresa
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Company), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.CompanyRelationships.HAS_ADDRESS);

                } else {

                    this.processCompanyAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }
    }

    private void processCompanyAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CompanyAddress.BuildingNumber:
                //Criamos agora o nó do BuildingNumber
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER);

                break;

            case EnumsOfElements.CompanyAddress.StreetName:
                //Criamos agora o nó do StreetName
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_STREET_NAME);

                break;

            case EnumsOfElements.CompanyAddress.AddressDetail:
                //Adicionamos como atributo ao nó Address
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Address), XMLElement, value);

                break;

            case EnumsOfElements.CompanyAddress.City:
                //Criamos agora o nó do City
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_CITY);

                break;

            case EnumsOfElements.CompanyAddress.PostalCode:
                //Criamos agora o nó do PostalCode
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE);

                break;

            case EnumsOfElements.CompanyAddress.Region:
                //Criamos agora o nó do Region
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_REGION);

                break;

            case EnumsOfElements.CompanyAddress.Country:
                //Criamos agora o nó do Country
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_COUNTRY);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processMasterFilesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        count++;

        switch (this.manageSequenceElements.get(count).getXMLElement()) {

            case EnumsOfElements.MasterFiles.GeneralLedgerAccounts:

                if (EnumsOfElements.MasterFiles.GeneralLedgerAccounts.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo GeneralLedgerAccounts
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.GeneralLedgerAccounts));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerAccounts), EnumsOfEntities.Entities.GeneralLedgerAccounts);

                } else {

                    this.processGeneralLedgerAccountsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.Customer:

                if (EnumsOfElements.MasterFiles.Customer.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Customer
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Customer));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Customer), EnumsOfEntities.Entities.Customer);

                } else {

                    this.processCustomerChildren(XMLElement, value, count);

                }


                break;

            case EnumsOfElements.MasterFiles.Supplier:

                if (EnumsOfElements.MasterFiles.Supplier.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Supplier
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Supplier));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Supplier), EnumsOfEntities.Entities.Supplier);

                } else {

                    this.processSupplierChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.Product:

                if (EnumsOfElements.MasterFiles.Product.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Product
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Product));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Product), EnumsOfEntities.Entities.Product);


                } else {

                    this.processProductChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.TaxTable:

                if (EnumsOfElements.MasterFiles.TaxTable.equalsIgnoreCase(XMLElement)) {
                    //Não é preciso processar o elemento TaxTable

                } else {

                    this.processTaxTableChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
        }

    }

    private void processGeneralLedgerAccountsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            if (EnumsOfElements.GeneralLedgerAccounts.TaxonomyReference.equalsIgnoreCase(XMLElement)) {
                //Adicionamos como atributo ao GeneralLedgerAccounts
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerAccounts), XMLElement, value);

            } else {

                throw new MapException(XMLElement);

            }

        } else {

            count++;

            if (EnumsOfElements.GeneralLedgerAccounts.Account.equals(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.GeneralLedgerAccounts.Account.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Account
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Account));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Account), EnumsOfEntities.Entities.Account);

                    //Adicionamos uma relação com o GeneralLedgerAccounts
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerAccounts), this.findNodeId(EnumsOfEntities.Entities.Account), EnumsOfEntities.GeneralLedgerAccountsRelationships.HAS_ACCOUNT);

                } else {

                    this.processAccountChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processAccountChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Account.AccountID:
                //Adicionamos como atributo ao nó Account
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Account), XMLElement, value);

                break;

            case EnumsOfElements.Account.AccountDescription:
                //Adicionamos como atributo ao nó Account
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Account), XMLElement, value);

                break;

            case EnumsOfElements.Account.OpeningDebitBalance:
                //Criamos agora o nó do OpeningDebitBalance
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_OPENING_DEBIT_BALANCE);

                break;

            case EnumsOfElements.Account.OpeningCreditBalance:
                //Criamos agora o nó do OpeningCreditBalance
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_OPENING_CREDIT_BALANCE);

                break;

            case EnumsOfElements.Account.ClosingDebitBalance:
                //Criamos agora o nó do ClosingDebitBalance
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_CLOSING_DEBIT_BALANCE);

                break;

            case EnumsOfElements.Account.ClosingCreditBalance:
                //Criamos agora o nó do ClosingCreditBalance
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_CLOSING_CREDIT_BALANCE);

                break;

            case EnumsOfElements.Account.GroupingCategory:
                //Criamos agora o nó do GroupingCategory
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_GROUPING_CATEGORY);

                break;

            case EnumsOfElements.Account.GroupingCode:
                //Criamos agora o nó do GroupingCode
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_GROUPING_CODE);

                break;

            case EnumsOfElements.Account.TaxonomyCode:
                //Criamos agora o nó do TaxonomyCode
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Integer.valueOf(value)), XMLElement));

                //Adicionamos a relação entre o nó criado com a Account
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Account), this.findNodeId(XMLElement), EnumsOfEntities.AccountRelationships.HAS_TAXONOMY_CODE);

                break;

            default:
                throw new MapException(XMLElement);
        }
    }

    private void processCustomerChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Customer.CustomerID:
                    //Adicionamos como atributo ao nó customer
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Customer), XMLElement, value);

                    break;

                case EnumsOfElements.Customer.AccountID:
                    //Adicionamos uma relação entre o customer e a conta
                    this.driver.addRelationshipToAccount(this.findNodeId(EnumsOfEntities.Entities.Customer), value);

                    break;

                case EnumsOfElements.Customer.CustomerTaxID:
                    //Criamos agora o nó do CustomerTaxID
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó criado com o Customer
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(XMLElement), EnumsOfEntities.CustomerRelationships.HAS_CUSTOMER_TAX_ID);

                    break;

                case EnumsOfElements.Customer.CompanyName:
                    //Adicionamos uma relação entre o customer e a Company
                    this.driver.addRelationshipToCompany(this.findNodeId(EnumsOfEntities.Entities.Customer), value);

                    break;

                case EnumsOfElements.Customer.Contact:
                    //Criamos o nó do tipo Contacts com o atributo Contact e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), EnumsOfEntities.Entities.Contacts));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                    //Adicionamos uma relação deste nó com o Customer
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                    break;

                case EnumsOfElements.Customer.Telephone:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE);
                    }

                    break;

                case EnumsOfElements.Customer.Fax:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado e o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);
                    }

                    break;

                case EnumsOfElements.Customer.Email:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado e o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);
                    }

                    break;

                case EnumsOfElements.Customer.Website:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado e o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);
                    }

                    break;

                case EnumsOfElements.Customer.SelfBillingIndicator:
                    //Adicionamos como atributo ao nó customer
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Customer), XMLElement, Integer.valueOf(value));

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            switch (this.manageSequenceElements.get(count).getXMLElement()) {

                case EnumsOfElements.Customer.BillingAddress:

                    if (EnumsOfElements.Customer.BillingAddress.equalsIgnoreCase(XMLElement)) {
                        //Criamos o nó do tipo Address
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                        //Criamos agora uma relação com o Customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.CustomerRelationships.HAS_BILLING_ADDRESS);

                    } else {

                        this.processAddressChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Customer.ShipToAddress:

                    if (EnumsOfElements.Customer.ShipToAddress.equalsIgnoreCase(XMLElement)) {
                        //Criamos o nó do tipo Address
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                        //Criamos agora uma relação com o Customer
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Customer), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.CustomerRelationships.HAS_SHIP_TO_ADDRESS);

                    } else {

                        this.processAddressChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
            }

        }
    }

    private void processSupplierChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Supplier.SupplierID:
                    //Adicionamos como atributo ao nó supplier
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Supplier), XMLElement, value);

                    break;

                case EnumsOfElements.Supplier.AccountID:
                    //Adicionamos uma relação entre o supplier e a conta
                    this.driver.addRelationshipToAccount(this.findNodeId(EnumsOfEntities.Entities.Supplier), value);

                    break;

                case EnumsOfElements.Supplier.SupplierTaxID:
                    //Criamos agora o nó do SupplierTaxID
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó criado com o Supplier
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(XMLElement), EnumsOfEntities.SupplierRelationships.HAS_SUPPLIER_TAX_ID);

                    break;

                case EnumsOfElements.Supplier.CompanyName:
                    //Adicionamos uma relação entre o supplier e a Company
                    this.driver.addRelationshipToCompany(this.findNodeId(EnumsOfEntities.Entities.Supplier), value);

                    break;

                case EnumsOfElements.Supplier.Contact:
                    //Criamos o nó do tipo Contacts com o atributo Contact e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), EnumsOfEntities.Entities.Contacts));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                    //Adicionamos uma relação deste nó com o Supplier
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.SupplierRelationships.HAS_CONTACTS);

                    break;

                case EnumsOfElements.Supplier.Telephone:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o Supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_TELEPHONE);
                    }

                    break;

                case EnumsOfElements.Supplier.Fax:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o Supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_FAX);
                    }

                    break;

                case EnumsOfElements.Supplier.Email:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o Supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_EMAIL);
                    }

                    break;

                case EnumsOfElements.Supplier.Website:
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    try {

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);

                    } catch (NodeException e) {
                        //Caso dê errado, significa que o nó do tipo contacts ainda não foi criado
                        //Criamos o nó do tipo Contacts
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Contacts));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.Entities.Contacts);

                        //Adicionamos uma relação deste nó com o Supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Contacts), EnumsOfEntities.CustomerRelationships.HAS_CONTACTS);

                        //Adicionamos a relação entre o nó criado com o Contacts
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Contacts), this.findNodeId(XMLElement), EnumsOfEntities.ContactsRelationships.HAS_WEBSITE);
                    }

                    break;

                case EnumsOfElements.Supplier.SelfBillingIndicator:
                    //Adicionamos como atributo ao nó supplier
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Supplier), XMLElement, Integer.valueOf(value));

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            switch (this.manageSequenceElements.get(count).getXMLElement()) {

                case EnumsOfElements.Supplier.BillingAddress:

                    if (EnumsOfElements.Supplier.BillingAddress.equalsIgnoreCase(XMLElement)) {
                        //Criamos o nó do tipo Address
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                        //Criamos agora uma relação com o supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.SupplierRelationships.HAS_BILLING_ADDRESS);

                    } else {

                        this.processAddressChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Supplier.ShipFromAddress:

                    if (EnumsOfElements.Supplier.ShipFromAddress.equalsIgnoreCase(XMLElement)) {
                        //Criamos o nó do tipo Address
                        this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                        //Criamos agora uma relação com o supplier
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Supplier), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.SupplierRelationships.HAS_SHIP_FROM_ADDRESS);

                    } else {

                        this.processAddressChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
            }

        }

    }

    private void processProductChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Product.ProductType:
                    //Criamos agora o nó do ProductType
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó criado com o Product
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Product), this.findNodeId(XMLElement), EnumsOfEntities.ProductRelationships.HAS_PRODUCT_TYPE);

                    break;

                case EnumsOfElements.Product.ProductCode:
                    //Adicionamos como atributo ao nó product
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Product), XMLElement, value);

                    break;

                case EnumsOfElements.Product.ProductGroup:
                    //Criamos agora o nó do ProductGroup
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó criado com o Product
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Product), this.findNodeId(XMLElement), EnumsOfEntities.ProductRelationships.HAS_PRODUCT_GROUP);

                    break;

                case EnumsOfElements.Product.ProductDescription:
                    //Adicionamos como atributo ao nó product
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Product), XMLElement, value);

                    break;

                case EnumsOfElements.Product.ProductNumberCode:
                    //Criamos agora o nó do ProductNumberCode
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação entre o nó criado com o Product
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Product), this.findNodeId(XMLElement), EnumsOfEntities.ProductRelationships.HAS_PRODUCT_NUMBER_CODE);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.Product.CustomsDetails.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.Product.CustomsDetails.equalsIgnoreCase(XMLElement)) {
                    //Criamos agora o nó do CustomsDetails
                    this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                } else {

                    this.processCustomsDetailsChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processCustomsDetailsChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CustomsDetails.CNCode:
                //Adicionamos como atributo ao CustomsDetails
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Product.CustomsDetails), XMLElement, value);

                break;

            case EnumsOfElements.CustomsDetails.UNNumber:
                //Adicionamos como atributo ao CustomsDetails
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Product.CustomsDetails), XMLElement, value);

                break;

            default:
                throw new MapException(XMLElement);

        }

    }

    private void processTaxTableChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        count++;

        if (EnumsOfElements.TaxTable.TaxTableEntry.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

            if (EnumsOfElements.TaxTable.TaxTableEntry.equalsIgnoreCase(XMLElement)) {
                //Criamos o nó do tipo TaxTable
                this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.TaxTable));
                this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.TaxTable), EnumsOfEntities.Entities.TaxTable);

            } else {

                this.processTaxTableEntryChildren(XMLElement, value);

            }

        } else {

            throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

        }

    }

    private void processTaxTableEntryChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.TaxTableEntry.TaxType:
                //Criamos agora o nó do TaxType
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a TaxTable
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.TaxTable), this.findNodeId(XMLElement), EnumsOfEntities.TaxTableRelationships.HAS_TAX_TYPE);

                break;

            case EnumsOfElements.TaxTableEntry.TaxCountryRegion:
                //Criamos agora o nó do TaxCountryRegion
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a TaxTable
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.TaxTable), this.findNodeId(XMLElement), EnumsOfEntities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION);

                break;

            case EnumsOfElements.TaxTableEntry.TaxCode:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.TaxTable), XMLElement, value);

                break;

            case EnumsOfElements.TaxTableEntry.Description:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.TaxTable), XMLElement, value);

                break;

            case EnumsOfElements.TaxTableEntry.TaxExpirationDate:
                //Criamos agora o nó do TaxExpirationDate
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a TaxTable
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.TaxTable), this.findNodeId(XMLElement), EnumsOfEntities.TaxTableRelationships.HAS_TAX_EXPIRATION_DATE);

                break;

            case EnumsOfElements.TaxTableEntry.TaxPercentage:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.TaxTable), XMLElement, Double.valueOf(value));

                break;

            case EnumsOfElements.TaxTableEntry.TaxAmount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.TaxTable), XMLElement, Double.valueOf(value));

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processGeneralLedgerEntriesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.GeneralLedgerEntries.NumberOfEntries:
                    //Adicionamos como propriedade do nó GeneralLedgerEntries
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries), XMLElement, Integer.valueOf(value));

                    break;

                case EnumsOfElements.GeneralLedgerEntries.TotalDebit:
                    //Criamos um novo nó com a propeidade TotalDebit e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó GeneralLedgerEntries
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries), this.findNodeId(XMLElement), EnumsOfEntities.GeneralLedgerEntriesRelationships.HAS_TOTAL_DEBIT);

                    break;

                case EnumsOfElements.GeneralLedgerEntries.TotalCredit:
                    //Criamos um novo nó com a propeidade TotalCredit e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó GeneralLedgerEntries
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries), this.findNodeId(XMLElement), EnumsOfEntities.GeneralLedgerEntriesRelationships.HAS_TOTAL_CREDIT);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.GeneralLedgerEntries.Journal.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.GeneralLedgerEntries.Journal.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Journal
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Journal));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Journal), EnumsOfEntities.Entities.Journal);

                    //Adicionamos uma relação deste com o GeneralLedgerEntries
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries), this.findNodeId(EnumsOfEntities.Entities.Journal), EnumsOfEntities.GeneralLedgerEntriesRelationships.HAS_JOURNAL);

                } else {

                    this.processJournalChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processJournalChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Journal.JournalID:
                    //Adicionamos como propriedade do Journal
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Journal), XMLElement, value);

                    break;

                case EnumsOfElements.Journal.Description:
                    //Adicionamos como propriedade do Journal
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Journal), XMLElement, value);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.Journal.Transaction.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.Journal.Transaction.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Transaction
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Transaction));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Transaction), EnumsOfEntities.Entities.Transaction);

                    //Adicionamos uma relação deste com o Journal
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Journal), this.findNodeId(EnumsOfEntities.Entities.Transaction), EnumsOfEntities.JournalRelationships.HAS_TRANSACTION);

                } else {

                    this.processTransactionChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processTransactionChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Transaction.TransactionID:
                    //Adicionamos como atributo da Transaction
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Transaction), XMLElement, value);

                    break;

                case EnumsOfElements.Transaction.Period:
                    //Criamos um novo nó com a propeidade Period e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Integer.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_PERIOD);

                    break;

                case EnumsOfElements.Transaction.TransactionDate:
                    //Criamos um novo nó com a propeidade TransactionDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_DATE);

                    break;

                case EnumsOfElements.Transaction.SourceID:
                    //Adicionamos uma relação entre a Transaction e o Source
                    this.driver.addRelationshipToSourceID(this.findNodeId(EnumsOfEntities.Entities.Transaction), value);

                    break;

                case EnumsOfElements.Transaction.Description:
                    //Adicionamos como atributo da Transaction
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Transaction), XMLElement, value);

                    break;

                case EnumsOfElements.Transaction.DocArchivalNumber:
                    //Criamos um novo nó com a propeidade DocArchivalNumber e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER);

                    break;

                case EnumsOfElements.Transaction.TransactionType:
                    //Criamos um novo nó com a propeidade TransactionType e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_TRANSACTION_TYPE);

                    break;

                case EnumsOfElements.Transaction.GLPostingDate:
                    //Criamos um novo nó com a propeidade GLPostingDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_GL_POSTING_DATE);

                    break;

                case EnumsOfElements.Transaction.CustomerID:
                    //Adicionamos uma relação entre a transaction e um customer
                    this.driver.addRelationshipToCustomer(this.findNodeId(EnumsOfEntities.Entities.Transaction), value);

                    break;

                case EnumsOfElements.Transaction.SupplierID:
                    //Adicionamos uma relação entre a transaction e um supplier
                    this.driver.addRelationshipToSupplier(this.findNodeId(EnumsOfEntities.Entities.Transaction), value);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.Transaction.Lines.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.Transaction.Lines.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                    //Adicionamos uma relação deste com a Transaction
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Transaction), this.findNodeId(XMLElement), EnumsOfEntities.TransactionRelationships.HAS_LINES);

                } else {

                    this.processLinesChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processLinesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        count++;

        switch (this.manageSequenceElements.get(count).getXMLElement()) {

            case EnumsOfElements.Lines.CreditLine:

                if (EnumsOfElements.Lines.CreditLine.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                    //Adicionamos uma relação deste com o Lines
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Transaction.Lines), this.findNodeId(XMLElement), EnumsOfEntities.LinesRelationships.HAS_CREDIT_LINE);

                } else {

                    this.processCreditLine(XMLElement, value);

                }

                break;

            case EnumsOfElements.Lines.DebitLine:

                if (EnumsOfElements.Lines.DebitLine.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó
                    this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                    //Adicionamos uma relação deste com o Lines
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Transaction.Lines), this.findNodeId(XMLElement), EnumsOfEntities.LinesRelationships.HAS_DEBIT_LINE);

                } else {

                    this.processDebitLine(XMLElement, value);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
        }

    }

    private void processCreditLine(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CreditLine.RecordID:
                //Adicionamos como propriedade do nó CreditLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.CreditLine), XMLElement, value);

                break;

            case EnumsOfElements.CreditLine.AccountID:
                //Adicionamos uma relação entre o CreditLine e uma account
                this.driver.addRelationshipToAccount(this.findNodeId(EnumsOfElements.Lines.CreditLine), value);

                break;

            case EnumsOfElements.CreditLine.SourceDocumentID:
                //Ainda não decidi como processar bem, visto que aparentemente isto indica o InvoiceNo

                //Adicionamos como propriedade do nó CreditLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.CreditLine), XMLElement, value);

                break;

            case EnumsOfElements.CreditLine.SystemEntryDate:
                //Criamos um novo nó com a propriedade  e o seu valor
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação deste com o nó CreditLine
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Lines.CreditLine), this.findNodeId(XMLElement), EnumsOfEntities.CreditLineRelationships.HAS_SYSTEM_ENTRY_DATE);

                break;

            case EnumsOfElements.CreditLine.Description:
                //Adicionamos como propriedade do nó CreditLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.CreditLine), XMLElement, value);

                break;

            case EnumsOfElements.CreditLine.CreditAmount:
                //Criamos um novo nó com a propriedade  e o seu valor
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação deste com o nó CreditLine
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Lines.CreditLine), this.findNodeId(XMLElement), EnumsOfEntities.CreditLineRelationships.HAS_CREDIT_AMOUNT);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processDebitLine(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.DebitLine.RecordID:
                //Adicionamos como propriedade do nó DebitLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.DebitLine), XMLElement, value);

                break;

            case EnumsOfElements.DebitLine.AccountID:
                //Adicionamos uma relação entre o DebitLine e uma account
                this.driver.addRelationshipToAccount(this.findNodeId(EnumsOfElements.Lines.DebitLine), value);

                break;

            case EnumsOfElements.DebitLine.SourceDocumentID:
                //Ainda não decidi como processar bem, visto que aparentemente isto indica o InvoiceNo

                //Adicionamos como propriedade do nó CreditLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.DebitLine), XMLElement, value);

                break;

            case EnumsOfElements.DebitLine.SystemEntryDate:
                //Criamos um novo nó com a propriedade  e o seu valor
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação deste com o nó DebitLine
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Lines.DebitLine), this.findNodeId(XMLElement), EnumsOfEntities.DebitLineRelationships.HAS_SYSTEM_ENTRY_DATE);

                break;

            case EnumsOfElements.DebitLine.Description:
                //Adicionamos como propriedade do nó DebitLine
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Lines.DebitLine), XMLElement, value);

                break;

            case EnumsOfElements.DebitLine.DebitAmount:
                //Criamos um novo nó com a propriedade  e o seu valor
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos a relação deste com o nó DebitLine
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Lines.DebitLine), this.findNodeId(XMLElement), EnumsOfEntities.DebitLineRelationships.HAS_DEBIT_AMOUNT);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSourceDocumentsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        count++;

        switch (this.manageSequenceElements.get(count).getXMLElement()) {

            case EnumsOfElements.SourceDocuments.SalesInvoices:

                if (EnumsOfElements.SourceDocuments.SalesInvoices.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo SalesInvoices
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.SalesInvoices));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.SalesInvoices), EnumsOfEntities.Entities.SalesInvoices);

                } else {

                    this.processSalesInvoicesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.MovementOfGoods:

                if (EnumsOfElements.SourceDocuments.MovementOfGoods.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo MovementOfGoods
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.MovementOfGoods));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.MovementOfGoods), EnumsOfEntities.Entities.MovementOfGoods);

                } else {

                    throw new MapException(XMLElement);
                    //this.processMovementOfGoodsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.WorkingDocuments:

                if (EnumsOfElements.SourceDocuments.WorkingDocuments.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo WorkingDocuments
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.WorkingDocuments));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.WorkingDocuments), EnumsOfEntities.Entities.WorkingDocuments);

                } else {

                    throw new MapException(XMLElement);
                    //this.processWorkingDocumentsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.Payments:

                if (EnumsOfElements.SourceDocuments.Payments.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Payments
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Payments));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Payments), EnumsOfEntities.Entities.Payments);

                } else {

                    throw new MapException(XMLElement);
                    //this.processPaymentsChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
        }

    }

    private void processSalesInvoicesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.SalesInvoices.NumberOfEntries:
                    //Adicionamos como propriedade do nó SalesInvoices
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.SalesInvoices), XMLElement, Integer.valueOf(value));

                    break;

                case EnumsOfElements.SalesInvoices.TotalDebit:
                    //Criamos um novo nó com a propriedade TotalDebit e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó SalesInvoices
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.SalesInvoices), this.findNodeId(XMLElement), EnumsOfEntities.SalesInvoicesRelationships.HAS_TOTAL_DEBIT);

                    break;

                case EnumsOfElements.SalesInvoices.TotalCredit:
                    //Criamos um novo nó com a propriedade TotalCredit e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó SalesInvoices
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.SalesInvoices), this.findNodeId(XMLElement), EnumsOfEntities.SalesInvoicesRelationships.HAS_TOTAL_CREDIT);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.SalesInvoices.Invoice.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.SalesInvoices.Invoice.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Invoice
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Invoice));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Invoice), EnumsOfEntities.Entities.Invoice);

                    //Adicionamos uma relação com o nó SalesInvoices
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.SalesInvoices), this.findNodeId(EnumsOfEntities.Entities.Invoice), EnumsOfEntities.SalesInvoicesRelationships.HAS_INVOICE);

                } else {

                    this.processInvoiceChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processInvoiceChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Invoice.InvoiceNo:
                    //Adicionamos como atributo ao nó Invoice
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Invoice), XMLElement, value);

                    break;

                case EnumsOfElements.Invoice.ATCUD:
                    //Criamos um novo nó com a propriedade ATCUD e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_ATCUD);

                    break;

                case EnumsOfElements.Invoice.Hash:
                    //Criamos um novo nó com a propriedade Hash e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_HASH);

                    break;

                case EnumsOfElements.Invoice.HashControl:
                    //Criamos um novo nó com a propriedade HashControl e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_HASH_CONTROL);

                    break;

                case EnumsOfElements.Invoice.Period:
                    //Criamos um novo nó com a propriedade Period e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Integer.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_PERIOD);

                    break;

                case EnumsOfElements.Invoice.InvoiceDate:
                    //Criamos um novo nó com a propriedade InvoiceDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_INVOICE_DATE);

                    break;

                case EnumsOfElements.Invoice.InvoiceType:
                    //Criamos um novo nó com a propriedade InvoiceType e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_INVOICE_TYPE);

                    break;

                case EnumsOfElements.Invoice.SourceID:
                    //Adicionamos uma relação com a Source
                    this.driver.addRelationshipToSourceID(this.findNodeId(EnumsOfEntities.Entities.Invoice), value);

                    break;

                case EnumsOfElements.Invoice.EACCode:
                    //Criamos um novo nó com a propriedade EACCode e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_EAC_Code);

                    break;

                case EnumsOfElements.Invoice.SystemEntryDate:
                    //Criamos um novo nó com a propriedade SystemEntryDate e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_SYSTEM_ENTRY_DATE);

                    break;

                case EnumsOfElements.Invoice.TransactionID:
                    //Adicionamos uma relação com uma Transaction
                    this.driver.addRelationshipToTransaction(this.findNodeId(EnumsOfEntities.Entities.Invoice), value);

                    break;

                case EnumsOfElements.Invoice.CustomerID:
                    //Adicionamos uma relação com um Customer
                    this.driver.addRelationshipToCustomer(this.findNodeId(EnumsOfEntities.Entities.Invoice), value);

                    break;

                case EnumsOfElements.Invoice.MovementEndTime:
                    //Criamos um novo nó com a propriedade MovementEndTime e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_MOVEMENT_END_TIME);

                    break;

                case EnumsOfElements.Invoice.MovementStartTime:
                    //Criamos um novo nó com a propriedade MovementStartTime e o seu valor
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Invoice
                    this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_MOVEMENT_START_TIME);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            switch (this.manageSequenceElements.get(count).getXMLElement()) {

                case EnumsOfElements.Invoice.DocumentStatus:

                    if (EnumsOfElements.Invoice.DocumentStatus.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_DOCUMENT_STATUS);

                    } else {

                        this.processDocumentStatusChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Invoice.SpecialRegimes:

                    if (EnumsOfElements.Invoice.SpecialRegimes.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_SPECIAL_REGIMES);

                    } else {

                        this.processSpecialRegimesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Invoice.ShipTo:

                    if (EnumsOfElements.Invoice.ShipTo.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_SHIP_TO);

                    } else {

                        this.processShipToChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.ShipFrom:

                    if (EnumsOfElements.Invoice.ShipFrom.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_SHIP_FROM);

                    } else {

                        this.processShipFromChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.Line:

                    if (EnumsOfElements.Invoice.Line.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_LINE);

                    } else {

                        this.processLineChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.DocumentTotals:

                    if (EnumsOfElements.Invoice.DocumentTotals.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_DOCUMENT_TOTALS);

                    } else {

                        this.processDocumentTotalsChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.WithholdingTax:

                    if (EnumsOfElements.Invoice.WithholdingTax.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Invoice
                        this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Invoice), this.findNodeId(XMLElement), EnumsOfEntities.InvoiceRelationships.HAS_WITHHOLDING_TAX);

                    } else {

                        this.processWithholdingTaxChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
            }

        }

    }

    private void processDocumentStatusChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.DocumentStatus.InvoiceStatus:
                //Adicionamos como propriedade ao nó DocumentStatus
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.DocumentStatus), XMLElement, value);

                break;

            case EnumsOfElements.DocumentStatus.InvoiceStatusDate:
                //Adicionamos como propriedade ao nó DocumentStatus
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.DocumentStatus), XMLElement, value);

                break;

            case EnumsOfElements.DocumentStatus.Reason:
                //Criamos um novo nó
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação deste com o nó DocumentStatus
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentStatus), this.findNodeId(XMLElement), EnumsOfEntities.DocumentStatusRelationships.HAS_REASON);

                break;

            case EnumsOfElements.DocumentStatus.SourceID:
                //Adicionamos uma relação com o Source
                this.driver.addRelationshipToSourceID(this.findNodeId(EnumsOfElements.Invoice.DocumentStatus), value);

                break;

            case EnumsOfElements.DocumentStatus.SourceBilling:
                //Criamos um novo nó
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação deste com o nó DocumentStatus
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentStatus), this.findNodeId(XMLElement), EnumsOfEntities.DocumentStatusRelationships.HAS_SOURCE_BILLING);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSpecialRegimesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.SpecialRegimes.SelfBillingIndicator:
                //Adicionamos como propriedade ao nó SpecialRegimes
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.SpecialRegimes), XMLElement, Integer.valueOf(value));

                break;

            case EnumsOfElements.SpecialRegimes.CashVATSchemeIndicator:
                //Adicionamos como propriedade ao nó SpecialRegimes
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.SpecialRegimes), XMLElement, Integer.valueOf(value));

                break;

            case EnumsOfElements.SpecialRegimes.ThirdPartiesBillingIndicator:
                //Adicionamos como propriedade ao nó SpecialRegimes
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.SpecialRegimes), XMLElement, Integer.valueOf(value));

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processShipToChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.ShipTo.DeliveryID:
                    //Adicionamos uma nova propriedade ao nó ShipTo
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.ShipTo), XMLElement, value);

                    break;

                case EnumsOfElements.ShipTo.DeliveryDate:
                    //Adicionamos uma nova propriedade ao nó ShipTo
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.ShipTo), XMLElement, value);

                    break;

                case EnumsOfElements.ShipTo.WarehouseID:
                    //Não decidi como processar ainda
                    throw new MapException(XMLElement);

                case EnumsOfElements.ShipTo.LocationID:
                    //Não decidi como processar ainda
                    throw new MapException(XMLElement);

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.ShipTo.Address.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.ShipTo.Address.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Address
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                    //Criamos agora uma relação com o ShipTo
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.ShipTo), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.ShipToRelationships.HAS_ADDRESS);

                } else {

                    this.processAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processShipFromChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.ShipFrom.DeliveryID:
                    //Adicionamos uma nova propriedade ao nó ShipFrom
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.ShipFrom), XMLElement, value);

                    break;

                case EnumsOfElements.ShipFrom.DeliveryDate:
                    //Adicionamos uma nova propriedade ao nó ShipFrom
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.ShipFrom), XMLElement, value);

                    break;

                case EnumsOfElements.ShipTo.WarehouseID:
                    //Não decidi como processar ainda
                    throw new MapException(XMLElement);

                case EnumsOfElements.ShipTo.LocationID:
                    //Não decidi como processar ainda
                    throw new MapException(XMLElement);

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            if (EnumsOfElements.ShipFrom.Address.equalsIgnoreCase(this.manageSequenceElements.get(count).getXMLElement())) {

                if (EnumsOfElements.ShipFrom.Address.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo Address
                    this.nodesContainer.add(new GraphNode(this.addNode(), EnumsOfEntities.Entities.Address));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.Entities.Address);

                    //Criamos agora uma relação com o ShipFrom
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.ShipFrom), this.findNodeId(EnumsOfEntities.Entities.Address), EnumsOfEntities.ShipFromRelationships.HAS_ADDRESS);

                } else {

                    this.processAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count).getXMLElement());

            }

        }

    }

    private void processLineChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Line.LineNumber:
                    //Adicionamos como atributo ao nó Line
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.Line), XMLElement, Integer.valueOf(value));

                    break;

                case EnumsOfElements.Line.ProductCode:
                    //Adicionamos uma relação com um Product
                    this.driver.addRelationshipToProduct(this.findNodeId(EnumsOfEntities.Entities.Invoice), value);

                    break;

                case EnumsOfElements.Line.ProductDescription:
                    //Não é necessário processar

                    break;

                case EnumsOfElements.Line.Quantity:
                    //Possivelmente deveria ser atributo de uma relação

                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_QUANTITY);

                    break;

                case EnumsOfElements.Line.UnitOfMeasure:
                    //Possivelmente deveria ser atributo de uma relação

                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_UNIT_OF_MEASURE);

                    break;

                case EnumsOfElements.Line.UnitPrice:
                    //Possivelmente deveria ser atributo de uma relação

                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_UNIT_PRICE);

                    break;

                case EnumsOfElements.Line.TaxBase:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_TAX_BASE);

                    break;

                case EnumsOfElements.Line.TaxPointDate:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_TAX_POINT_DATE);

                    break;

                case EnumsOfElements.Line.Description:
                    //Adicionamos como atributo ao nó Line
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.Line), XMLElement, value);

                    break;

                case EnumsOfElements.Line.DebitAmount:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_DEBIT_AMOUNT);

                    break;

                case EnumsOfElements.Line.CreditAmount:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_CREDIT_AMOUNT);

                    break;

                case EnumsOfElements.Line.TaxExemptionReason:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_TAX_EXEMPTION);

                    break;

                case EnumsOfElements.Line.TaxExemptionCode:
                    //Adicionamos como atributo ao nó TaxExemption
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.TaxExemptionReason), XMLElement, value);

                    break;

                case EnumsOfElements.Line.SettlementAmount:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos a relação deste com o nó Line
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_SETTLEMENT_AMOUNT);

                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            switch (this.manageSequenceElements.get(count).getXMLElement()) {

                case EnumsOfElements.Line.OrderReferences:

                    if (EnumsOfElements.Line.OrderReferences.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Line
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_ORDER_REFERENCES);

                    } else {

                        this.processOrderReferencesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.References:

                    if (EnumsOfElements.Line.References.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Line
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_REFERENCES);

                    } else {

                        this.processReferencesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.ProductSerialNumber:

                    if (EnumsOfElements.Line.ProductSerialNumber.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Line
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_PRODUCT_SERIAL_NUMBER);

                    } else {

                        this.processProductSerialNumberChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.Tax:

                    if (EnumsOfElements.Line.Tax.equalsIgnoreCase(XMLElement)) {
                        //Criamos uma nova instância
                        this.table = new TaxTable();

                    } else {

                        this.processTaxChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.CustomsInformation:

                    if (EnumsOfElements.Line.CustomsInformation.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos a relação deste com o nó Line
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.Line), this.findNodeId(XMLElement), EnumsOfEntities.LineRelationships.HAS_CUSTOMS_INFORMATION);

                    } else {

                        this.processCustomsInformationChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
            }

        }

    }

    private void processOrderReferencesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.OrderReferences.OriginatingON:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.OrderReferences), XMLElement, value);

                break;

            case EnumsOfElements.OrderReferences.OrderDate:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.OrderReferences), XMLElement, value);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processReferencesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.References.Reference:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.References), XMLElement, value);

                break;

            case EnumsOfElements.References.Reason:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.References), XMLElement, value);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processProductSerialNumberChildren(String XMLElement, String value) throws MapException, NodeException {

        if (EnumsOfElements.ProductSerialNumber.SerialNumber.equalsIgnoreCase(XMLElement)) {
            //Adicionamos como atributo
            this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.ProductSerialNumber), XMLElement, value);

        } else {

            throw new MapException(XMLElement);

        }

    }

    private void processTaxChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Tax.TaxType:
                //Definimos os valores
                this.table.setTaxType(value);

                break;

            case EnumsOfElements.Tax.TaxCountryRegion:
                //Definimos os valores
                this.table.setTaxCountryRegion(value);

                break;

            case EnumsOfElements.Tax.TaxCode:
                //Definimos os valores
                this.table.setTaxCode(value);

                break;

            case EnumsOfElements.Tax.TaxPercentage:
                //Definimos os valores
                this.table.setTaxPercentage(Double.valueOf(value));

                //Temos todos os atributos necessários, definimos a relação
                this.driver.addRelationshipToTaxTable(this.findNodeId(EnumsOfElements.Invoice.Line), this.table);

                break;

            case EnumsOfElements.Tax.TaxAmount:
                //Definimos os valores
                this.table.setTaxAmount(Double.valueOf(value));

                //Temos todos os atributos necessários, definimos a relação
                this.driver.addRelationshipToTaxTable(this.findNodeId(EnumsOfElements.Invoice.Line), this.table);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processCustomsInformationChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CustomsInformation.ARCNo:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.CustomsInformation), XMLElement, value);

                break;

            case EnumsOfElements.CustomsInformation.IECAmount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Line.CustomsInformation), XMLElement, Double.valueOf(value));

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processDocumentTotalsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.DocumentTotals.TaxPayable:
                    //Adicionamos como atributo do nó DocumentTotals
                    //this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), XMLElement, Double.valueOf(value));

                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_TAX_PAYABLE);

                    break;

                case EnumsOfElements.DocumentTotals.NetTotal:
                    //Criamos um novo nó
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                    //Adicionamos uma relação com o nó DocumentTotals
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_NET_TOTAL);

                    break;

                case EnumsOfElements.DocumentTotals.GrossTotal:
                    //Adicionamos como atributo do nó DocumentTotals
                    this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), XMLElement, Double.valueOf(value));

                   /*
                    this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));
                    this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_GROSS_TOTAL);
*/
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            count++;

            switch (this.manageSequenceElements.get(count).getXMLElement()) {

                case EnumsOfElements.DocumentTotals.Currency:

                    if (EnumsOfElements.DocumentTotals.Currency.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos uma relação com o nó DocumentTotals
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_CURRENCY);

                    } else {

                        this.processCurrencyChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.DocumentTotals.Settlement:

                    if (EnumsOfElements.DocumentTotals.Settlement.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos uma relação com o nó DocumentTotals
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_SETTLEMENT);

                    } else {

                        this.processSettlementChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.DocumentTotals.Payment:

                    if (EnumsOfElements.DocumentTotals.Payment.equalsIgnoreCase(XMLElement)) {
                        //Criamos um novo nó
                        this.nodesContainer.add(new GraphNode(this.addNode(), XMLElement));

                        //Adicionamos uma relação com o nó DocumentTotals
                        this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.DocumentTotals), this.findNodeId(XMLElement), EnumsOfEntities.DocumentTotalsRelationships.HAS_PAYMENT);

                    } else {

                        this.processPaymentChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count).getXMLElement());
            }

        }

    }

    private void processCurrencyChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Currency.CurrencyCode:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Currency), XMLElement, value);

                break;

            case EnumsOfElements.Currency.CurrencyAmount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Currency), XMLElement, Double.valueOf(value));

                break;

            case EnumsOfElements.Currency.ExchangeRate:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Currency), XMLElement, Double.valueOf(value));

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSettlementChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Settlement.SettlementDiscount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Settlement), XMLElement, value);

                break;

            case EnumsOfElements.Settlement.SettlementAmount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Settlement), XMLElement, Double.valueOf(value));

                break;

            case EnumsOfElements.Settlement.SettlementDate:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Settlement), XMLElement, value);

                break;

            case EnumsOfElements.Settlement.PaymentTerms:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Settlement), XMLElement, value);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processPaymentChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Payment.PaymentMechanism:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Payment), XMLElement, value);

                break;

            case EnumsOfElements.Payment.PaymentAmount:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Payment), XMLElement, Double.valueOf(value));

                break;

            case EnumsOfElements.Payment.PaymentDate:
                //Adicionamos como atributo
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.DocumentTotals.Payment), XMLElement, value);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processWithholdingTaxChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.WithholdingTax.WithholdingTaxType:
                //Criamos um novo nó
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos uma relação com o nó WithholdingTax
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.WithholdingTax), this.findNodeId(XMLElement), EnumsOfEntities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_TYPE);

                break;

            case EnumsOfElements.WithholdingTax.WithholdingTaxDescription:
                //Adicionamos como atributo do nó WithholdingTax
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfElements.Invoice.WithholdingTax), XMLElement, value);

                break;

            case EnumsOfElements.WithholdingTax.WithholdingTaxAmount:
                //Criamos um novo nó
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, Double.valueOf(value)), XMLElement));

                //Adicionamos uma relação com o nó WithholdingTax
                this.driver.addRelationship(this.findNodeId(EnumsOfElements.Invoice.WithholdingTax), this.findNodeId(XMLElement), EnumsOfEntities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_AMOUNT);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CompanyAddress.BuildingNumber:
                //Criamos agora o nó do BuildingNumber
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_BUILDING_NUMBER);

                break;

            case EnumsOfElements.CompanyAddress.StreetName:
                //Criamos agora o nó do StreetName
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_STREET_NAME);

                break;

            case EnumsOfElements.CompanyAddress.AddressDetail:
                //Adicionamos como atributo ao nó Address
                this.driver.addPropertyToNode(this.findNodeId(EnumsOfEntities.Entities.Address), XMLElement, value);

                break;

            case EnumsOfElements.CompanyAddress.City:
                //Criamos agora o nó do City
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_CITY);

                break;

            case EnumsOfElements.CompanyAddress.PostalCode:
                //Criamos agora o nó do PostalCode
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_POSTAL_CODE);

                break;

            case EnumsOfElements.CompanyAddress.Region:
                //Criamos agora o nó do Region
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_REGION);

                break;

            case EnumsOfElements.CompanyAddress.Country:
                //Criamos agora o nó do BuildingNumber
                this.nodesContainer.add(new GraphNode(this.addNode(XMLElement, value), XMLElement));

                //Adicionamos a relação entre o nó criado com a Address
                this.driver.addRelationship(this.findNodeId(EnumsOfEntities.Entities.Address), this.findNodeId(XMLElement), EnumsOfEntities.AddressRelationships.HAS_COUNTRY);

                break;

            default:
                throw new MapException(XMLElement);
        }

    }

}
