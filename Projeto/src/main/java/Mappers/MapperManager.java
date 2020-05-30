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

    private void processRootElement(String XMLElement, String value, int count) throws MapException, NodeException {

        if (EnumsOfElements.RootElement.AuditFile.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

            if (EnumsOfElements.RootElement.AuditFile.equalsIgnoreCase(XMLElement)) {
                //O root element não é processado!

            } else {

                count++;
                this.processAuditFileChildren(XMLElement, value, count);

            }

        } else {

            throw new MapException(this.manageSequenceElements.get(count));

        }
    }

    private void processAuditFileChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        switch (this.manageSequenceElements.get(count)) {

            case EnumsOfElements.AuditFile.Header:

                if (EnumsOfElements.AuditFile.Header.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo FileInformation

                    this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfEntities.Entities.FileInformation.toString());

                } else {

                    count++;
                    this.processHeaderChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.MasterFiles:

                if (EnumsOfElements.AuditFile.MasterFiles.equalsIgnoreCase(XMLElement)) {
                    //Não se processa o MasterFiles, em príncipio

                } else {

                    count++;
                    this.processMasterFilesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.GeneralLedgerEntries:

                if (EnumsOfElements.AuditFile.GeneralLedgerEntries.equalsIgnoreCase(XMLElement)) {
                    //Criamos o nó do tipo GeneralLedgerEntries

                    this.nodesContainer.add(new GraphNode(this.driver.addNode(EnumsOfEntities.Entities.GeneralLedgerEntries.toString()), EnumsOfEntities.Entities.GeneralLedgerEntries.toString(), value));
                    this.driver.addRelationshipTypeOf(this.findNodeId(EnumsOfEntities.Entities.GeneralLedgerEntries.toString()), EnumsOfEntities.Entities.GeneralLedgerEntries.toString());

                } else {

                    count++;
                    this.processGeneralLedgerEntriesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.AuditFile.SourceDocuments:

                if (EnumsOfElements.AuditFile.SourceDocuments.equalsIgnoreCase(XMLElement)) {
                    //Não se processa o SourceDocuments, em príncipio

                } else {

                    count++;
                    this.processSourceDocumentsChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count));
        }
    }

    private void processHeaderChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Header.AuditFileVersion:

                    this.driver.addAttributesToNode(this.findNodeId(EnumsOfEntities.Entities.FileInformation.toString()), EnumsOfElements.Header.AuditFileVersion, value);

                    break;

                case EnumsOfElements.Header.CompanyID:
                    break;

                case EnumsOfElements.Header.TaxRegistrationNumber:
                    break;

                case EnumsOfElements.Header.TaxAccountingBasis:
                    break;

                case EnumsOfElements.Header.CompanyName:
                    break;

                case EnumsOfElements.Header.BussinessName:
                    break;

                case EnumsOfElements.Header.FiscalYear:
                    break;

                case EnumsOfElements.Header.StartDate:
                    break;

                case EnumsOfElements.Header.EndDate:
                    break;

                case EnumsOfElements.Header.CurrencyCode:
                    break;

                case EnumsOfElements.Header.DateCreated:
                    break;

                case EnumsOfElements.Header.TaxEntity:
                    break;

                case EnumsOfElements.Header.ProductCompanyTaxID:
                    break;

                case EnumsOfElements.Header.SoftwareCertificateNumber:
                    break;

                case EnumsOfElements.Header.ProductID:
                    break;

                case EnumsOfElements.Header.ProductVersion:
                    break;

                case EnumsOfElements.Header.HeaderComment:
                    break;

                case EnumsOfElements.Header.Telephone:
                    break;

                case EnumsOfElements.Header.Fax:
                    break;

                case EnumsOfElements.Header.Email:
                    break;

                case EnumsOfElements.Header.Website:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.Header.CompanyAddress.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.Header.CompanyAddress.equals(XMLElement)) {
                    //Processa o CompanyAddress

                } else {

                    this.processCompanyAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }
    }

    private void processCompanyAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CompanyAddress.BuildingNumber:
                break;

            case EnumsOfElements.CompanyAddress.StreetName:
                break;

            case EnumsOfElements.CompanyAddress.AddressDetail:
                break;

            case EnumsOfElements.CompanyAddress.City:
                break;

            case EnumsOfElements.CompanyAddress.PostalCode:
                break;

            case EnumsOfElements.CompanyAddress.Region:
                break;

            case EnumsOfElements.CompanyAddress.Country:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processMasterFilesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        switch (this.manageSequenceElements.get(count)) {

            case EnumsOfElements.MasterFiles.GeneralLedgerAccounts:

                if (EnumsOfElements.MasterFiles.GeneralLedgerAccounts.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    count++;
                    this.processGeneralLedgerAccountsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.Customer:

                if (EnumsOfElements.MasterFiles.Customer.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    count++;
                    this.processCustomerChildren(XMLElement, value, count);

                }


                break;

            case EnumsOfElements.MasterFiles.Supplier:

                if (EnumsOfElements.MasterFiles.Supplier.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    count++;
                    this.processSupplierChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.Product:

                if (EnumsOfElements.MasterFiles.Product.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    count++;
                    this.processProductChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.MasterFiles.TaxTable:

                if (EnumsOfElements.MasterFiles.TaxTable.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    count++;
                    this.processTaxTableChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count));
        }

    }

    private void processGeneralLedgerAccountsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            if (EnumsOfElements.GeneralLedgerAccounts.TaxonomyReference.equalsIgnoreCase(XMLElement)) {
                //Processar o elemento

            } else {

                throw new MapException(XMLElement);

            }

        } else {

            if (EnumsOfElements.GeneralLedgerAccounts.Account.equals(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.GeneralLedgerAccounts.Account.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    this.processAccountChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processAccountChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Account.AccountID:
                break;

            case EnumsOfElements.Account.AccountDescription:
                break;

            case EnumsOfElements.Account.OpeningDebitBalance:
                break;

            case EnumsOfElements.Account.OpeningCreditBalance:
                break;

            case EnumsOfElements.Account.ClosingDebitBalance:
                break;

            case EnumsOfElements.Account.ClosingCreditBalance:
                break;

            case EnumsOfElements.Account.GroupingCategory:
                break;

            case EnumsOfElements.Account.GroupingCode:
                break;

            case EnumsOfElements.Account.TaxonomyCode:
                break;

            default:
                throw new MapException(XMLElement);
        }
    }

    private void processCustomerChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Customer.CustomerID:
                    break;

                case EnumsOfElements.Customer.AccountID:
                    break;

                case EnumsOfElements.Customer.CustomerTaxID:
                    break;

                case EnumsOfElements.Customer.CompanyName:
                    break;

                case EnumsOfElements.Customer.Contact:
                    break;

                case EnumsOfElements.Customer.Telephone:
                    break;

                case EnumsOfElements.Customer.Fax:
                    break;

                case EnumsOfElements.Customer.Email:
                    break;

                case EnumsOfElements.Customer.Website:
                    break;

                case EnumsOfElements.Customer.SelfBillingIndicator:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.Customer.BillingAddress:

                    if (EnumsOfElements.Customer.BillingAddress.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processBillingAddressChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Customer.ShipToAddress:

                    if (EnumsOfElements.Customer.ShipToAddress.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processShipToAddressChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count));
            }

        }
    }

    private void processSupplierChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Supplier.SupplierID:
                    break;

                case EnumsOfElements.Supplier.AccountID:
                    break;

                case EnumsOfElements.Supplier.SupplierTaxID:
                    break;

                case EnumsOfElements.Supplier.CompanyName:
                    break;

                case EnumsOfElements.Supplier.Contact:
                    break;

                case EnumsOfElements.Supplier.Telephone:
                    break;

                case EnumsOfElements.Supplier.Fax:
                    break;

                case EnumsOfElements.Supplier.Email:
                    break;

                case EnumsOfElements.Supplier.Website:
                    break;

                case EnumsOfElements.Supplier.SelfBillingIndicator:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.Supplier.BillingAddress:

                    if (EnumsOfElements.Supplier.BillingAddress.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processBillingAddressChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Supplier.ShipFromAddress:

                    if (EnumsOfElements.Supplier.ShipFromAddress.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processShipFromAddressChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count));
            }

        }

    }

    private void processProductChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Product.ProductType:
                    break;

                case EnumsOfElements.Product.ProductCode:
                    break;

                case EnumsOfElements.Product.ProductGroup:
                    break;

                case EnumsOfElements.Product.ProductDescription:
                    break;

                case EnumsOfElements.Product.ProductNumberCode:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.Product.CustomsDetails.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.Product.CustomsDetails.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    this.processCustomsDetailsChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processCustomsDetailsChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CustomsDetails.CNCode:
                break;

            case EnumsOfElements.CustomsDetails.UNNumber:
                break;

            default:
                throw new MapException(XMLElement);

        }

    }

    private void processTaxTableChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (EnumsOfElements.TaxTable.TaxTableEntry.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

            if (EnumsOfElements.TaxTable.TaxTableEntry.equalsIgnoreCase(XMLElement)) {
                //Processa o elemento

            } else {

                this.processTaxTableEntryChildren(XMLElement, value);

            }

        } else {

            throw new MapException(this.manageSequenceElements.get(count));

        }

    }

    private void processTaxTableEntryChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.TaxTableEntry.TaxType:
                break;

            case EnumsOfElements.TaxTableEntry.TaxCountryRegion:
                break;

            case EnumsOfElements.TaxTableEntry.TaxCode:
                break;

            case EnumsOfElements.TaxTableEntry.Description:
                break;

            case EnumsOfElements.TaxTableEntry.TaxExpirationDate:
                break;

            case EnumsOfElements.TaxTableEntry.TaxPercentage:
                break;

            case EnumsOfElements.TaxTableEntry.TaxAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processGeneralLedgerEntriesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.GeneralLedgerEntries.NumberOfEntries:
                    break;

                case EnumsOfElements.GeneralLedgerEntries.TotalDebit:
                    break;

                case EnumsOfElements.GeneralLedgerEntries.TotalCredit:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.GeneralLedgerEntries.Journal.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.GeneralLedgerEntries.Journal.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    this.processJournalChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processJournalChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Journal.JournalID:
                    break;

                case EnumsOfElements.Journal.Description:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.Journal.Transaction.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.Journal.Transaction.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    this.processTransactionChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processTransactionChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Transaction.TransactionID:
                    break;

                case EnumsOfElements.Transaction.Period:
                    break;

                case EnumsOfElements.Transaction.TransactionDate:
                    break;

                case EnumsOfElements.Transaction.SourceID:
                    break;

                case EnumsOfElements.Transaction.Description:
                    break;

                case EnumsOfElements.Transaction.DocArchivalNumber:
                    break;

                case EnumsOfElements.Transaction.TransactionType:
                    break;

                case EnumsOfElements.Transaction.GLPostingDate:
                    break;

                case EnumsOfElements.Transaction.CustomerID:
                    break;

                case EnumsOfElements.Transaction.SupplierID:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.Transaction.Lines.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.Transaction.Lines.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    this.processLinesChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processLinesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        switch (this.manageSequenceElements.get(count)) {

            case EnumsOfElements.Lines.CreditLine:

                if (EnumsOfElements.Lines.CreditLine.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    this.processCreditLine(XMLElement, value);

                }

                break;

            case EnumsOfElements.Lines.DebitLine:

                if (EnumsOfElements.Lines.DebitLine.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    this.processDebitLine(XMLElement, value);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count));
        }

    }

    private void processCreditLine(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CreditLine.RecordID:
                break;

            case EnumsOfElements.CreditLine.AccountID:
                break;

            case EnumsOfElements.CreditLine.SystemEntryDate:
                break;

            case EnumsOfElements.CreditLine.Description:
                break;

            case EnumsOfElements.CreditLine.CreditAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processDebitLine(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.DebitLine.RecordID:
                break;

            case EnumsOfElements.DebitLine.AccountID:
                break;

            case EnumsOfElements.DebitLine.SystemEntryDate:
                break;

            case EnumsOfElements.DebitLine.Description:
                break;

            case EnumsOfElements.DebitLine.DebitAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSourceDocumentsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        switch (this.manageSequenceElements.get(count)) {

            case EnumsOfElements.SourceDocuments.SalesInvoices:

                if (EnumsOfElements.SourceDocuments.SalesInvoices.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    this.processSalesInvoicesChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.MovementOfGoods:

                if (EnumsOfElements.SourceDocuments.MovementOfGoods.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    //this.processMovementOfGoodsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.WorkingDocuments:

                if (EnumsOfElements.SourceDocuments.WorkingDocuments.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    //this.processWorkingDocumentsChildren(XMLElement, value, count);

                }

                break;

            case EnumsOfElements.SourceDocuments.Payments:

                if (EnumsOfElements.SourceDocuments.Payments.equalsIgnoreCase(XMLElement)) {
                    //Processa o elemento

                } else {

                    count++;
                    //this.processPaymentsChildren(XMLElement, value, count);

                }

                break;

            default:
                throw new MapException(this.manageSequenceElements.get(count));
        }

    }

    private void processSalesInvoicesChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.SalesInvoices.NumberOfEntries:
                    break;

                case EnumsOfElements.SalesInvoices.TotalDebit:
                    break;

                case EnumsOfElements.SalesInvoices.TotalCredit:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.SalesInvoices.Invoice.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.SalesInvoices.Invoice.equalsIgnoreCase(XMLElement)) {
                    //Processa o element

                } else {

                    count++;
                    this.processInvoiceChildren(XMLElement, value, count);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processInvoiceChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Invoice.InvoiceNo:
                    break;

                case EnumsOfElements.Invoice.ATCUD:
                    break;

                case EnumsOfElements.Invoice.Hash:
                    break;

                case EnumsOfElements.Invoice.HashControl:
                    break;

                case EnumsOfElements.Invoice.Period:
                    break;

                case EnumsOfElements.Invoice.InvoiceDate:
                    break;

                case EnumsOfElements.Invoice.InvoiceType:
                    break;

                case EnumsOfElements.Invoice.SourceID:
                    break;

                case EnumsOfElements.Invoice.EACCode:
                    break;

                case EnumsOfElements.Invoice.SystemEntryDate:
                    break;

                case EnumsOfElements.Invoice.TransactionID:
                    break;

                case EnumsOfElements.Invoice.CustomerID:
                    break;

                case EnumsOfElements.Invoice.MovementEndTime:
                    break;

                case EnumsOfElements.Invoice.MovementStartTime:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.Invoice.DocumentStatus:

                    if (EnumsOfElements.Invoice.DocumentStatus.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        this.processDocumentStatusChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Invoice.SpecialRegimes:

                    if (EnumsOfElements.Invoice.SpecialRegimes.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        this.processSpecialRegimesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Invoice.ShipTo:

                    if (EnumsOfElements.Invoice.ShipTo.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        count++;
                        this.processShipToChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.ShipFrom:

                    if (EnumsOfElements.Invoice.ShipFrom.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        count++;
                        this.processShipFromChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.Line:

                    if (EnumsOfElements.Invoice.Line.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        count++;
                        this.processLineChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.DocumentTotals:

                    if (EnumsOfElements.Invoice.DocumentTotals.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        count++;
                        this.processDocumentTotalsChildren(XMLElement, value, count);

                    }

                    break;

                case EnumsOfElements.Invoice.WithholdingTax:

                    if (EnumsOfElements.Invoice.WithholdingTax.equalsIgnoreCase(XMLElement)) {
                        //Processar o elemento

                    } else {

                        this.processWithholdingTaxChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count));
            }

        }

    }

    private void processDocumentStatusChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.DocumentStatus.InvoiceStatus:
                break;

            case EnumsOfElements.DocumentStatus.InvoiceStatusDate:
                break;

            case EnumsOfElements.DocumentStatus.Reason:
                break;

            case EnumsOfElements.DocumentStatus.SourceID:
                break;

            case EnumsOfElements.DocumentStatus.SourceBilling:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSpecialRegimesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.SpecialRegimes.SelfBillingIndicator:
                break;

            case EnumsOfElements.SpecialRegimes.CashVATSchemeIndicator:
                break;

            case EnumsOfElements.SpecialRegimes.ThirdPartiesBillingIndicator:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processShipToChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.ShipTo.DeliveryID:
                    break;

                case EnumsOfElements.ShipTo.DeliveryDate:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.ShipTo.Address.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.ShipTo.Address.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    this.processAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processShipFromChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.ShipFrom.DeliveryID:
                    break;

                case EnumsOfElements.ShipFrom.DeliveryDate:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            if (EnumsOfElements.ShipFrom.Address.equalsIgnoreCase(this.manageSequenceElements.get(count))) {

                if (EnumsOfElements.ShipFrom.Address.equalsIgnoreCase(XMLElement)) {
                    //Processar o elemento

                } else {

                    this.processAddressChildren(XMLElement, value);

                }

            } else {

                throw new MapException(this.manageSequenceElements.get(count));

            }

        }

    }

    private void processLineChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.Line.LineNumber:
                    break;

                case EnumsOfElements.Line.ProductCode:
                    break;

                case EnumsOfElements.Line.ProductDescription:
                    break;

                case EnumsOfElements.Line.Quantity:
                    break;

                case EnumsOfElements.Line.UnitOfMeasure:
                    break;

                case EnumsOfElements.Line.UnitPrice:
                    break;

                case EnumsOfElements.Line.TaxBase:
                    break;

                case EnumsOfElements.Line.TaxPointDate:
                    break;

                case EnumsOfElements.Line.Description:
                    break;

                case EnumsOfElements.Line.DebitAmount:
                    break;

                case EnumsOfElements.Line.CreditAmount:
                    break;

                case EnumsOfElements.Line.TaxExemptionReason:
                    break;

                case EnumsOfElements.Line.TaxExemptionCode:
                    break;

                case EnumsOfElements.Line.SettlementAmount:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.Line.OrderReferences:

                    if (EnumsOfElements.Line.OrderReferences.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processOrderReferencesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.References:

                    if (EnumsOfElements.Line.References.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processReferencesChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.ProductSerialNumber:

                    if (EnumsOfElements.Line.ProductSerialNumber.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processProductSerialNumberChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.Tax:

                    if (EnumsOfElements.Line.Tax.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processTaxChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.Line.CustomsInformation:

                    if (EnumsOfElements.Line.CustomsInformation.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processCustomsInformationChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count));
            }

        }

    }

    private void processOrderReferencesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.OrderReferences.OriginatingON:
                break;

            case EnumsOfElements.OrderReferences.OrderDate:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processReferencesChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.References.Reference:
                break;

            case EnumsOfElements.References.Reason:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processProductSerialNumberChildren(String XMLElement, String value) throws MapException, NodeException {

        if (EnumsOfElements.ProductSerialNumber.SerialNumber.equalsIgnoreCase(XMLElement)) {
            //Processar o elemento

        } else {

            throw new MapException(XMLElement);

        }

    }

    private void processTaxChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Tax.TaxType:
                break;

            case EnumsOfElements.Tax.TaxCountryRegion:
                break;

            case EnumsOfElements.Tax.TaxCode:
                break;

            case EnumsOfElements.Tax.TaxPercentage:
                break;

            case EnumsOfElements.Tax.TaxAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processCustomsInformationChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.CustomsInformation.ARCNo:
                break;

            case EnumsOfElements.CustomsInformation.IECAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processDocumentTotalsChildren(String XMLElement, String value, int count) throws MapException, NodeException {

        if (this.depth == count) {

            switch (XMLElement) {

                case EnumsOfElements.DocumentTotals.TaxPayable:
                    break;

                case EnumsOfElements.DocumentTotals.NetTotal:
                    break;

                case EnumsOfElements.DocumentTotals.GrossTotal:
                    break;

                default:
                    throw new MapException(XMLElement);
            }

        } else {

            switch (this.manageSequenceElements.get(count)) {

                case EnumsOfElements.DocumentTotals.Currency:

                    if (EnumsOfElements.DocumentTotals.Currency.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processCurrencyChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.DocumentTotals.Settlement:

                    if (EnumsOfElements.DocumentTotals.Settlement.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processSettlementChildren(XMLElement, value);

                    }

                    break;

                case EnumsOfElements.DocumentTotals.Payment:

                    if (EnumsOfElements.DocumentTotals.Payment.equalsIgnoreCase(XMLElement)) {
                        //Processa o elemento

                    } else {

                        this.processPaymentChildren(XMLElement, value);

                    }

                    break;

                default:
                    throw new MapException(this.manageSequenceElements.get(count));
            }

        }

    }

    private void processCurrencyChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Currency.CurrencyCode:
                break;

            case EnumsOfElements.Currency.CurrencyAmount:
                break;

            case EnumsOfElements.Currency.ExchangeRate:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processSettlementChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Settlement.SettlementDiscount:
                break;

            case EnumsOfElements.Settlement.SettlementAmount:
                break;

            case EnumsOfElements.Settlement.SettlementDate:
                break;

            case EnumsOfElements.Settlement.PaymentTerms:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processPaymentChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.Payment.PaymentMechanism:
                break;

            case EnumsOfElements.Payment.PaymentAmount:
                break;

            case EnumsOfElements.Payment.PaymentDate:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processWithholdingTaxChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.WithholdingTax.WithholdingTaxType:
                break;

            case EnumsOfElements.WithholdingTax.WithholdingTaxDescription:
                break;

            case EnumsOfElements.WithholdingTax.WithholdingTaxAmount:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processBillingAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.BillingAddress.BuildingNumber:
                break;

            case EnumsOfElements.BillingAddress.StreetName:
                break;

            case EnumsOfElements.BillingAddress.AddressDetail:
                break;

            case EnumsOfElements.BillingAddress.City:
                break;

            case EnumsOfElements.BillingAddress.PostalCode:
                break;

            case EnumsOfElements.BillingAddress.Region:
                break;

            case EnumsOfElements.BillingAddress.Country:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processShipToAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.ShipToAddress.BuildingNumber:
                break;

            case EnumsOfElements.ShipToAddress.StreetName:
                break;

            case EnumsOfElements.ShipToAddress.AddressDetail:
                break;

            case EnumsOfElements.ShipToAddress.City:
                break;

            case EnumsOfElements.ShipToAddress.PostalCode:
                break;

            case EnumsOfElements.ShipToAddress.Region:
                break;

            case EnumsOfElements.ShipToAddress.Country:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processShipFromAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.ShipFromAddress.BuildingNumber:
                break;

            case EnumsOfElements.ShipFromAddress.StreetName:
                break;

            case EnumsOfElements.ShipFromAddress.AddressDetail:
                break;

            case EnumsOfElements.ShipFromAddress.City:
                break;

            case EnumsOfElements.ShipFromAddress.PostalCode:
                break;

            case EnumsOfElements.ShipFromAddress.Region:
                break;

            case EnumsOfElements.ShipFromAddress.Country:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }

    private void processAddressChildren(String XMLElement, String value) throws MapException, NodeException {

        switch (XMLElement) {

            case EnumsOfElements.ShipFromAddress.BuildingNumber:
                break;

            case EnumsOfElements.ShipFromAddress.StreetName:
                break;

            case EnumsOfElements.ShipFromAddress.AddressDetail:
                break;

            case EnumsOfElements.ShipFromAddress.City:
                break;

            case EnumsOfElements.ShipFromAddress.PostalCode:
                break;

            case EnumsOfElements.ShipFromAddress.Region:
                break;

            case EnumsOfElements.ShipFromAddress.Country:
                break;

            default:
                throw new MapException(XMLElement);
        }

    }
}
