package Mappers;

import Database.Neo4j;
import Enums.EnumsOfElements;
import Enums.EnumsOfEntities;
import Exceptions.MapException;
import Exceptions.NodeException;

import java.util.Iterator;
import java.util.LinkedList;

public class MapperManager {

    private final Neo4j driver;
    private int depth;
    private final LinkedList<String> sequenceElements;
    private final LinkedList<String> manageSequenceElements;
    private final LinkedList<GraphNode> nodesContainer;

    public MapperManager(Neo4j driver) {
        this.driver = driver;
        this.depth = -1;
        this.sequenceElements = new LinkedList<>();
        this.manageSequenceElements = new LinkedList<>();
        this.nodesContainer = new LinkedList<>();
        this.loadSequenceElements();
        this.loadIdentitiesNodes();
    }

    public void processStartElement(String XMLElement, String value) {

        try {

            if (this.sequenceElements.contains(XMLElement)) {
                this.manageSequenceElements.add(XMLElement);
                this.depth++;
            }

            int count = 0;

            this.processRootElement(XMLElement, value, count);

        } catch (MapException | NodeException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void processEndElement(String XMLElement) {
        if (this.sequenceElements.contains(XMLElement)) {
            this.manageSequenceElements.removeLast();
            this.depth--;

            if (XMLElement.equalsIgnoreCase(EnumsOfElements.RootElement.AuditFile.toString())) {
                this.driver.close();

            } else {
                boolean found = false;

                while (!found && !this.nodesContainer.isEmpty()) {
                    if (this.nodesContainer.getLast().getXMLElement().equalsIgnoreCase(XMLElement)) {
                        found = true;
                    }

                    this.nodesContainer.removeLast();
                }

            }
        }
    }

    private void processRootElement(String XMLElement, String value, int count) throws MapException, NodeException {
        if (this.depth == count) {

            if (EnumsOfElements.RootElement.AuditFile.equalsIgnoreCase(XMLElement)) {
                //O root element não é processado!

            } else {
                throw new MapException();
            }

        } else {
            count++;
            this.processAuditFileChilds(XMLElement, value, count);
        }
    }

    private void processAuditFileChilds(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.AuditFile.Header:

                    this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString());

                    break;

                case EnumsOfElements.AuditFile.MasterFiles:
                    //Não se processa o MasterFiles, em príncipio

                    break;

                case EnumsOfElements.AuditFile.GeneralLedgerEntries:

                    this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.GeneralLedgerEntries.toString()), EnumsOfEntities.Entities.GeneralLedgerEntries.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries.toString()), EnumsOfEntities.Entities.GeneralLedgerEntries.toString());

                    break;

                case EnumsOfElements.AuditFile.SourceDocuments:
                    //Não se processa o SourceDocuments, em príncipio

                    break;

                default:
                    throw new MapException();
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.AuditFile.Header:

                    count++;
                    this.processHeaderChilds(XMLElement, value, count);

                    break;

                case EnumsOfElements.AuditFile.MasterFiles:

                    count++;
                    this.processMasterFilesChilds(XMLElement, value, count);

                    break;

                case EnumsOfElements.AuditFile.GeneralLedgerEntries:

                    count++;
                    this.processGeneralLedgerChilds(XMLElement, value, count);

                    break;

                case EnumsOfElements.AuditFile.SourceDocuments:

                    count++;
                    this.processSourceDocumentsChilds(XMLElement, value, count);

                    break;

                default:
                    throw new MapException();
            }

        }


    }

    private void processHeaderChilds(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Header.AuditFileVersion:

                    this.driver.addAttributesToNode(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfElements.Header.AuditFileVersion.toString(), value);

                    break;

                case EnumsOfElements.Header.CompanyAddress:

                    if (this.depth == count) {
                        this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString());
                    } else {
                        count++;
                        this.processCompanyAddressChilds(XMLElement, value, count);
                    }

                default:
                    throw new MapException();
            }

        } else {

            if (EnumsOfElements.Header.CompanyAddress.toString().equals(XMLElement)) {
                //Processa o CompanyAddress

            } else {

                count++;
                this.processCompanyAddressChilds(XMLElement, value, count);

            }

        }
    }

    private void processCompanyAddressChilds(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.CompanyAddress.BuildingNumber:
                    break;

                case EnumsOfElements.Header.CompanyAddress:

                    if (this.depth == count) {
                        this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString(), value));
                        this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString());
                    } else {
                        count++;
                        this.processCompanyAddressChilds(XMLElement, value, count);
                    }

                default:
                    throw new MapException();
            }

        } else {

            throw new MapException();

        }

    }

    private void processMasterFilesChilds(String XMLElement, String value, int count) {

    }

    private void processGeneralLedgerChilds(String XMLElement, String value, int count) {

    }

    private void processSourceDocumentsChilds(String XMLElement, String value, int count) {

    }

    private void loadSequenceElements() {
        EnumsOfElements.SequenceElements[] enums = EnumsOfElements.SequenceElements.values();
        for (int i = 0; i < enums.length; i++) {
            this.sequenceElements.add(enums[i].toString());
        }
    }

    private void loadIdentitiesNodes() {
        EnumsOfEntities.Entities[] enums = EnumsOfEntities.Entities.values();
        for (int i = 0; i < enums.length; i++) {
            this.driver.addIdentityNode(enums[i].toString());
        }
    }

    private long findNodeId(String XMLElement) throws NodeException {
        Iterator<GraphNode> iterator = this.nodesContainer.iterator();

        while (iterator.hasNext()) {
            GraphNode node = iterator.next();
            if (node.getXMLElement().equalsIgnoreCase(XMLElement)) {
                return node.getId();
            }
        }

        throw new NodeException("Node '" + XMLElement + "' not found!");
    }

}
