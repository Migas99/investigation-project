package Mapper;

import Database.Neo4jConnector;
import Enumerations.Elements;
import Enumerations.Entities;
import Exceptions.MapException;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Mapper {

    private final QueryConstructor constructor;

    private int depth;
    private final LinkedList<String> currentSequences;
    private final String fileName;

    private final Map<String, String> container;

    /*Listas de auxílio à criação de relações entre entidades*/
    private String rootCompany;
    private final Map<String, String> companies;
    private final Map<String, String> accounts;
    private final Map<String, String> customers;
    private final Map<String, String> suppliers;
    private final Map<String, String> represents;
    private final Map<String, String> products;
    private final Map<String, String> transactions;
    private final Map<String, String> documents;

    public Mapper(String fileName) {
        this.constructor = new QueryConstructor();
        this.depth = -1;
        this.currentSequences = new LinkedList<>();
        this.fileName = fileName.substring(0, fileName.length() - 4);

        this.container = new HashMap<>();
        this.companies = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customers = new HashMap<>();
        this.suppliers = new HashMap<>();
        this.represents = new HashMap<>();
        this.products = new HashMap<>();
        this.transactions = new HashMap<>();
        this.documents = new HashMap<>();
    }

    public void uploadToDatabase() {
        Neo4jConnector.uploadToDatabase(this.constructor.getUploadQuery());
    }

    public void processStartSequence(String element) {
        this.depth++;
        this.currentSequences.add(element);
        this.processElement(element, null);
    }

    public void processEndSequence(String element) {
        this.depth--;
        this.currentSequences.removeLast();
    }

    public void processElement(String element, String value) {

        try {

            int count = 0;
            this.processRootElement(element, value, count);

        } catch (MapException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }

    private void processRootElement(String element, String value, int count) throws MapException {

        if (Elements.RootElement.AuditFile.equalsIgnoreCase(this.currentSequences.get(count))) {

            if (Elements.RootElement.AuditFile.equalsIgnoreCase(element)) {

                this.container.put(
                        element,
                        this.constructor.CREATE(Entities.Labels.File, "FileName", this.fileName)
                );

            } else {

                this.processAuditFileChildren(element, value, count);

            }

        } else {

            throw new MapException(this.currentSequences.get(count));

        }
    }

    private void processAuditFileChildren(String element, String value, int count) throws MapException {

        count++;

        switch (this.currentSequences.get(count)) {

            case Elements.AuditFile.Header:

                if (Elements.AuditFile.Header.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.FileInfo);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processHeaderChildren(element, value, count);

                }

                break;

            case Elements.AuditFile.MasterFiles:

                if (!Elements.AuditFile.MasterFiles.equalsIgnoreCase(element)) {
                    this.processMasterFilesChildren(element, value, count);
                }

                break;

            case Elements.AuditFile.GeneralLedgerEntries:

                if (Elements.AuditFile.GeneralLedgerEntries.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.GeneralLedgerEntries);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_GENERAL_LEDGER_ENTRIES
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processGeneralLedgerEntriesChildren(element, value, count);

                }

                break;

            case Elements.AuditFile.SourceDocuments:

                if (!Elements.AuditFile.SourceDocuments.equalsIgnoreCase(element)) {
                    this.processSourceDocumentsChildren(element, value, count);
                }

                break;

            default:
                throw new MapException(this.currentSequences.get(count));
        }
    }

    private void processHeaderChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Header.AuditFileVersion:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.AuditFile.Header),
                            element,
                            value
                    );

                    break;

                case Elements.Header.CompanyID:

                    this.container.put(
                            element,
                            this.constructor.CREATE(Entities.Labels.CompanyInfo, element, value)
                    );

                    break;

                case Elements.Header.TaxRegistrationNumber:

                    this.container.put(
                            element,
                            this.constructor.CREATE(Entities.Labels.CompanyInfo, element, Integer.parseInt(value))
                    );

                    break;

                case Elements.Header.TaxAccountingBasis:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_TAX_ACCOUNTING_BASIS
                    );

                    break;

                case Elements.Header.CompanyName:

                    this.rootCompany = this.constructor.CREATE(Entities.Labels.Company, element, value);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            this.rootCompany,
                            Entities.FileRelationships.RELATED_TO_COMPANY
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            this.rootCompany,
                            this.container.get(Elements.RootElement.AuditFile),
                            Entities.CompanyRelationships.HAS_SAFTP_FILE
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            this.rootCompany,
                            this.container.get(Elements.Header.CompanyID),
                            Entities.CompanyRelationships.HAS_COMPANY_ID
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            this.rootCompany,
                            this.container.get(Elements.Header.TaxRegistrationNumber),
                            Entities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER
                    );

                    break;

                case Elements.Header.BusinessName:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.rootCompany,
                            Entities.Labels.CompanyInfo,
                            element,
                            value,
                            Entities.CompanyRelationships.HAS_BUSINESS_NAME
                    );

                    break;

                case Elements.Header.FiscalYear:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.FileInformationRelationships.HAS_FISCAL_YEAR
                    );

                    break;

                case Elements.Header.StartDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_START_DATE
                    );

                    break;

                case Elements.Header.EndDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_END_DATE
                    );

                    break;

                case Elements.Header.CurrencyCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_CURRENCY_CODE
                    );

                    break;

                case Elements.Header.DateCreated:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_DATE_CREATED
                    );

                    break;

                case Elements.Header.TaxEntity:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_TAX_ENTITY
                    );

                    break;

                case Elements.Header.ProductCompanyTaxID:

                    identifier = this.constructor.CREATE(Entities.Labels.FileInfo, element, value);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.AuditFile.Header),
                            identifier,
                            Entities.FileInformationRelationships.PRODUCED_BY
                    );

                    this.container.put(element, identifier);

                    break;

                case Elements.Header.SoftwareCertificateNumber:
                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.Header.ProductCompanyTaxID),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.Header.ProductID:
                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.Header.ProductCompanyTaxID),
                            element,
                            value
                    );

                    break;

                case Elements.Header.ProductVersion:
                    this.constructor.SET_PROPERTY(
                            this.container.remove(Elements.Header.ProductCompanyTaxID),
                            element,
                            value
                    );

                    break;

                case Elements.Header.HeaderComment:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.Header),
                            Entities.Labels.FileInfo,
                            element,
                            value,
                            Entities.FileInformationRelationships.HAS_ADDITIONAL_COMMENT
                    );

                    break;

                case Elements.Header.Telephone:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.rootCompany,
                            Entities.Labels.CompanyContact,
                            element,
                            value,
                            Entities.CompanyRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Header.Fax:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.rootCompany,
                            Entities.Labels.CompanyContact,
                            element,
                            value,
                            Entities.CompanyRelationships.HAS_FAX
                    );

                    break;

                case Elements.Header.Email:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.rootCompany,
                            Entities.Labels.CompanyContact,
                            element,
                            value,
                            Entities.CompanyRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Header.Website:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.rootCompany,
                            Entities.Labels.CompanyContact,
                            element,
                            value,
                            Entities.CompanyRelationships.HAS_WEBSITE
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Header.CompanyAddress.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (!Elements.Header.CompanyAddress.equals(element)) {

                    this.processCompanyAddressChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }
    }

    private void processCompanyAddressChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.CompanyAddress.BuildingNumber:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_BUILDING_NUMBER
                );

                break;

            case Elements.CompanyAddress.StreetName:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_STREET_NAME
                );

                break;

            case Elements.CompanyAddress.AddressDetail:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_ADDRESS_DETAIL
                );

                break;

            case Elements.CompanyAddress.City:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_CITY
                );

                break;

            case Elements.CompanyAddress.PostalCode:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_POSTAL_CODE
                );

                break;

            case Elements.CompanyAddress.Region:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_REGION
                );

                break;

            case Elements.CompanyAddress.Country:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.rootCompany,
                        Entities.Labels.CompanyAddress,
                        element,
                        value,
                        Entities.CompanyRelationships.HAS_COUNTRY
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processMasterFilesChildren(String element, String value, int count) throws MapException {

        count++;
        String identifier;

        switch (this.currentSequences.get(count)) {

            case Elements.MasterFiles.GeneralLedgerAccounts:

                if (Elements.MasterFiles.GeneralLedgerAccounts.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE(Entities.Labels.GeneralLedgerAccounts);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_GENERAL_LEDGER_ACCOUNTS
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processGeneralLedgerAccountsChildren(element, value, count);

                }

                break;

            case Elements.MasterFiles.Customer:

                if (Elements.MasterFiles.Customer.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE(Entities.Labels.Customer);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_CUSTOMER
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processCustomerChildren(element, value, count);

                }


                break;

            case Elements.MasterFiles.Supplier:

                if (Elements.MasterFiles.Supplier.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE(Entities.Labels.Supplier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_SUPPLIER
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processSupplierChildren(element, value, count);

                }

                break;

            case Elements.MasterFiles.Product:

                if (Elements.MasterFiles.Product.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE(Entities.Labels.Product);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_PRODUCT
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processProductChildren(element, value, count);

                }

                break;

            case Elements.MasterFiles.TaxTable:

                if (!Elements.MasterFiles.TaxTable.equalsIgnoreCase(element)) {
                    this.processTaxTableChildren(element, value, count);
                }

                break;

            default:
                throw new MapException(this.currentSequences.get(count));
        }

    }

    private void processGeneralLedgerAccountsChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            if (Elements.GeneralLedgerAccounts.TaxonomyReference.equalsIgnoreCase(element)) {

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.MasterFiles.GeneralLedgerAccounts),
                        element,
                        value
                );

            } else {

                throw new MapException(element);

            }

        } else {

            count++;

            if (Elements.GeneralLedgerAccounts.Account.equals(this.currentSequences.get(count))) {

                if (Elements.GeneralLedgerAccounts.Account.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Account);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.MasterFiles.GeneralLedgerAccounts),
                            identifier,
                            Entities.GeneralLedgerAccountsRelationships.HAS_ACCOUNT
                    );

                    this.container.put(element, identifier);

                } else {

                    this.processAccountChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processAccountChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.Account.AccountID:
                String identifier = this.container.get(Elements.GeneralLedgerAccounts.Account);
                this.constructor.SET_PROPERTY(identifier, element, value);
                this.accounts.put(value, identifier);

                break;

            case Elements.Account.AccountDescription:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        value,
                        Entities.AccountRelationships.HAS_ACCOUNT_DESCRIPTION
                );

                break;

            case Elements.Account.OpeningDebitBalance:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        Double.parseDouble(value),
                        Entities.AccountRelationships.HAS_OPENING_DEBIT_BALANCE
                );

                break;

            case Elements.Account.OpeningCreditBalance:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        Double.parseDouble(value),
                        Entities.AccountRelationships.HAS_OPENING_CREDIT_BALANCE
                );

                break;

            case Elements.Account.ClosingDebitBalance:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        Double.parseDouble(value),
                        Entities.AccountRelationships.HAS_CLOSING_DEBIT_BALANCE
                );

                break;

            case Elements.Account.ClosingCreditBalance:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        Double.parseDouble(value),
                        Entities.AccountRelationships.HAS_CLOSING_CREDIT_BALANCE
                );

                break;

            case Elements.Account.GroupingCategory:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        value,
                        Entities.AccountRelationships.HAS_GROUPING_CATEGORY
                );

                break;

            case Elements.Account.GroupingCode:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        value,
                        Entities.AccountRelationships.HAS_GROUPING_CODE
                );

                break;

            case Elements.Account.TaxonomyCode:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.GeneralLedgerAccounts.Account),
                        Entities.Labels.AccountInfo,
                        element,
                        Integer.parseInt(value),
                        Entities.AccountRelationships.HAS_TAXONOMY_CODE
                );

                break;

            default:
                throw new MapException(element);
        }
    }

    private void processCustomerChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Customer.CustomerID:
                    identifier = this.container.get(Elements.MasterFiles.Customer);
                    this.constructor.SET_PROPERTY(identifier, element, value);
                    this.customers.put(value, identifier);

                    break;

                case Elements.Customer.AccountID:

                    if (this.accounts.containsKey(value)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Customer),
                                this.accounts.get(value),
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Account, element, value);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Customer),
                                identifier,
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                        this.accounts.put(value, identifier);

                    }

                    break;

                case Elements.Customer.CustomerTaxID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.CustomerInfo,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_CUSTOMER_TAX_ID
                    );

                    break;

                case Elements.Customer.CompanyName:

                    String customer = this.container.get(Elements.MasterFiles.Customer);

                    if (this.companies.containsKey(value)) {

                        identifier = this.companies.get(value);

                        this.constructor.CREATE_RELATIONSHIP(
                                customer,
                                identifier,
                                Entities.CustomerRelationships.REPRESENTS_AS_CUSTOMER
                        );

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Company, element, value);

                        this.constructor.CREATE_RELATIONSHIP(this.rootCompany, identifier, Entities.CompanyRelationships.IS_SUPPLIER_OF);
                        this.constructor.CREATE_RELATIONSHIP(identifier, this.rootCompany, Entities.CompanyRelationships.IS_CUSTOMER_OF);

                        this.constructor.CREATE_RELATIONSHIP(
                                customer,
                                identifier,
                                Entities.CustomerRelationships.REPRESENTS_AS_CUSTOMER
                        );

                        this.companies.put(value, identifier);

                    }

                    this.represents.put(customer, identifier);

                    break;

                case Elements.Customer.Contact:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.CustomerInfo,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_CONTACT
                    );

                    break;

                case Elements.Customer.Telephone:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Customer.Fax:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_FAX
                    );

                    break;

                case Elements.Customer.Email:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Customer.Website:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.CustomerRelationships.HAS_WEBSITE
                    );

                    break;

                case Elements.Customer.SelfBillingIndicator:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Customer),
                            Entities.Labels.CustomerInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.CustomerRelationships.HAS_SELF_BILLING_INDICATOR
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.Customer.BillingAddress:

                    if (Elements.Customer.BillingAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.Address);
                        this.container.put(Entities.Labels.Address, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Customer),
                                identifier,
                                Entities.CustomerRelationships.HAS_BILLING_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                case Elements.Customer.ShipToAddress:

                    if (Elements.Customer.ShipToAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.Address);
                        this.container.put(Entities.Labels.Address, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Customer),
                                identifier,
                                Entities.CustomerRelationships.HAS_SHIP_TO_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }
    }

    private void processSupplierChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Supplier.SupplierID:

                    identifier = this.container.get(Elements.MasterFiles.Supplier);
                    this.constructor.SET_PROPERTY(identifier, element, value);
                    this.suppliers.put(value, identifier);

                    break;

                case Elements.Supplier.AccountID:

                    if (this.accounts.containsKey(value)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Supplier),
                                this.accounts.get(value),
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Account, element, value);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Supplier),
                                identifier,
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                        this.accounts.put(value, identifier);

                    }

                    break;

                case Elements.Supplier.SupplierTaxID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.SupplierInfo,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_SUPPLIER_TAX_ID
                    );

                    break;

                case Elements.Supplier.CompanyName:

                    String supplier = this.container.get(Elements.MasterFiles.Supplier);

                    if (this.companies.containsKey(value)) {

                        identifier = this.companies.get(value);

                        this.constructor.CREATE_RELATIONSHIP(
                                supplier,
                                identifier,
                                Entities.SupplierRelationships.REPRESENTS_AS_SUPPLIER
                        );

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Company, element, value);

                        this.constructor.CREATE_RELATIONSHIP(this.rootCompany, identifier, Entities.CompanyRelationships.IS_CUSTOMER_OF);
                        this.constructor.CREATE_RELATIONSHIP(identifier, this.rootCompany, Entities.CompanyRelationships.IS_SUPPLIER_OF);

                        this.constructor.CREATE_RELATIONSHIP(
                                supplier,
                                identifier,
                                Entities.OtherRelationships.HAS_COMPANY
                        );

                        this.companies.put(value, identifier);

                    }

                    this.represents.put(supplier, identifier);

                    break;

                case Elements.Supplier.Contact:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.SupplierInfo,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_CONTACT
                    );

                    break;

                case Elements.Supplier.Telephone:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Supplier.Fax:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_FAX
                    );

                    break;

                case Elements.Supplier.Email:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Supplier.Website:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.Contact,
                            element,
                            value,
                            Entities.SupplierRelationships.HAS_WEBSITE
                    );

                    break;

                case Elements.Supplier.SelfBillingIndicator:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Supplier),
                            Entities.Labels.SupplierInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.SupplierRelationships.HAS_SELF_BILLING_INDICATOR
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.Supplier.BillingAddress:

                    if (Elements.Supplier.BillingAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.Address);
                        this.container.put(Entities.Labels.Address, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Supplier),
                                identifier,
                                Entities.SupplierRelationships.HAS_BILLING_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                case Elements.Supplier.ShipFromAddress:

                    if (Elements.Supplier.ShipFromAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.Address);
                        this.container.put(Entities.Labels.Address, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MasterFiles.Supplier),
                                identifier,
                                Entities.SupplierRelationships.HAS_SHIP_FROM_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processProductChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.Product.ProductType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Product),
                            Entities.Labels.ProductInfo,
                            element,
                            value,
                            Entities.ProductRelationships.HAS_PRODUCT_TYPE
                    );

                    break;

                case Elements.Product.ProductCode:

                    String identifier = this.container.get(Elements.MasterFiles.Product);
                    this.constructor.SET_PROPERTY(identifier, element, value);
                    this.products.put(value, identifier);

                    break;

                case Elements.Product.ProductGroup:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Product),
                            Entities.Labels.ProductInfo,
                            element,
                            value,
                            Entities.ProductRelationships.HAS_PRODUCT_GROUP
                    );

                    break;

                case Elements.Product.ProductDescription:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Product),
                            Entities.Labels.ProductInfo,
                            element,
                            value,
                            Entities.ProductRelationships.HAS_PRODUCT_DESCRIPTION
                    );

                    break;

                case Elements.Product.ProductNumberCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MasterFiles.Product),
                            Entities.Labels.ProductInfo,
                            element,
                            value,
                            Entities.ProductRelationships.HAS_PRODUCT_NUMBER_CODE
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Product.CustomsDetails.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.Product.CustomsDetails.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.ProductInfo);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.MasterFiles.Product),
                            identifier,
                            Entities.ProductRelationships.HAS_CUSTOMS_DETAILS
                    );

                } else {

                    this.processCustomsDetailsChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processCustomsDetailsChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.CustomsDetails.CNCode:

            case Elements.CustomsDetails.UNNumber:
                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Product.CustomsDetails),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);

        }

    }

    private void processTaxTableChildren(String element, String value, int count) throws MapException {

        count++;

        if (Elements.TaxTable.TaxTableEntry.equalsIgnoreCase(this.currentSequences.get(count))) {

            if (Elements.TaxTable.TaxTableEntry.equalsIgnoreCase(element)) {

                String identifier = this.constructor.CREATE(Entities.Labels.TaxTable);
                this.container.put(element, identifier);

                this.constructor.CREATE_RELATIONSHIP(
                        this.container.get(Elements.RootElement.AuditFile),
                        identifier,
                        Entities.FileRelationships.HAS_TAX_TABLE
                );

            } else {

                this.processTaxTableEntryChildren(element, value);

            }

        } else {

            throw new MapException(this.currentSequences.get(count));

        }

    }

    private void processTaxTableEntryChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.TaxTableEntry.TaxType:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        value,
                        Entities.TaxTableRelationships.HAS_TAX_TYPE
                );

                break;

            case Elements.TaxTableEntry.TaxCountryRegion:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        value,
                        Entities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION
                );

                break;

            case Elements.TaxTableEntry.TaxCode:
                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        element,
                        value
                );

                break;

            case Elements.TaxTableEntry.Description:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        value,
                        Entities.TaxTableRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.TaxTableEntry.TaxExpirationDate:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        value,
                        Entities.TaxTableRelationships.HAS_TAX_EXPIRATION_DATE
                );

                break;

            case Elements.TaxTableEntry.TaxPercentage:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        Double.parseDouble(value),
                        Entities.TaxTableRelationships.HAS_TAX_PERCENTAGE
                );

                break;

            case Elements.TaxTableEntry.TaxAmount:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.TaxTable.TaxTableEntry),
                        Entities.Labels.TaxTable,
                        element,
                        Double.parseDouble(value),
                        Entities.TaxTableRelationships.HAS_TAX_AMOUNT
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processGeneralLedgerEntriesChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.GeneralLedgerEntries.NumberOfEntries:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.AuditFile.GeneralLedgerEntries),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.GeneralLedgerEntries.TotalDebit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.GeneralLedgerEntries),
                            Entities.Labels.GeneralLedgerEntries,
                            element,
                            Double.parseDouble(value),
                            Entities.GeneralLedgerEntriesRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.GeneralLedgerEntries.TotalCredit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.AuditFile.GeneralLedgerEntries),
                            Entities.Labels.GeneralLedgerEntries,
                            element,
                            Double.parseDouble(value),
                            Entities.GeneralLedgerEntriesRelationships.HAS_TOTAL_CREDIT
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.GeneralLedgerEntries.Journal.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.GeneralLedgerEntries.Journal.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Journal);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.AuditFile.GeneralLedgerEntries),
                            identifier,
                            Entities.GeneralLedgerEntriesRelationships.HAS_JOURNAL
                    );

                } else {

                    this.processJournalChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processJournalChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.Journal.JournalID:
                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.GeneralLedgerEntries.Journal),
                            element,
                            value
                    );

                    break;

                case Elements.Journal.Description:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.GeneralLedgerEntries.Journal),
                            Entities.Labels.JournalInfo,
                            element,
                            value,
                            Entities.JournalRelationships.HAS_DESCRIPTION
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Journal.Transaction.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.Journal.Transaction.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Transaction);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.GeneralLedgerEntries.Journal),
                            identifier,
                            Entities.JournalRelationships.HAS_TRANSACTION
                    );

                } else {

                    this.processTransactionChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processTransactionChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Transaction.TransactionID:
                    identifier = this.container.get(Elements.Journal.Transaction);
                    this.constructor.SET_PROPERTY(identifier, element, value);
                    this.transactions.put(value, identifier);

                    break;

                case Elements.Transaction.Period:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.TransactionRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.Transaction.TransactionDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_TRANSACTION_DATE
                    );

                    break;

                case Elements.Transaction.SourceID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_SOURCE_ID
                    );

                    break;

                case Elements.Transaction.Description:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_DESCRIPTION
                    );

                    break;

                case Elements.Transaction.DocArchivalNumber:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER
                    );

                    break;

                case Elements.Transaction.TransactionType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_TRANSACTION_TYPE
                    );

                    break;

                case Elements.Transaction.GLPostingDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Journal.Transaction),
                            Entities.Labels.TransactionInfo,
                            element,
                            value,
                            Entities.TransactionRelationships.HAS_GL_POSTING_DATE
                    );

                    break;

                case Elements.Transaction.CustomerID:

                    identifier = this.container.get(Elements.Journal.Transaction);
                    String customer = this.customers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            customer,
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.TransactionRelationships.HAS_SELLER
                    );

                    if (this.represents.containsKey(customer)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(customer),
                                Entities.TransactionRelationships.HAS_BUYER
                        );

                    }

                    break;

                case Elements.Transaction.SupplierID:

                    identifier = this.container.get(Elements.Journal.Transaction);
                    String supplier = this.suppliers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            supplier,
                            Entities.OtherRelationships.HAS_SUPPLIER
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.TransactionRelationships.HAS_BUYER
                    );

                    if (this.represents.containsKey(supplier)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(supplier),
                                Entities.TransactionRelationships.HAS_SELLER
                        );

                    }

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Transaction.Lines.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.Transaction.Lines.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.TransactionInfo);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Journal.Transaction),
                            identifier,
                            Entities.TransactionRelationships.HAS_LINES
                    );

                } else {

                    this.processLinesChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processLinesChildren(String element, String value, int count) throws MapException {

        count++;

        switch (this.currentSequences.get(count)) {

            case Elements.Lines.CreditLine:

                if (Elements.Lines.CreditLine.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.CreditLine);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Transaction.Lines),
                            identifier,
                            Entities.LinesRelationships.HAS_CREDIT_LINE
                    );

                } else {

                    this.processCreditLine(element, value);

                }

                break;

            case Elements.Lines.DebitLine:

                if (Elements.Lines.DebitLine.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.DebitLine);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Transaction.Lines),
                            identifier,
                            Entities.LinesRelationships.HAS_DEBIT_LINE
                    );

                } else {

                    this.processDebitLine(element, value);

                }

                break;

            default:
                throw new MapException(this.currentSequences.get(count));
        }

    }

    private void processCreditLine(String element, String value) throws MapException {

        switch (element) {

            case Elements.CreditLine.RecordID:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Lines.CreditLine),
                        element,
                        value
                );

                break;

            case Elements.CreditLine.AccountID:

                this.constructor.CREATE_RELATIONSHIP(
                        this.container.get(Elements.Lines.CreditLine),
                        this.accounts.get(value),
                        Entities.OtherRelationships.HAS_ACCOUNT
                );

                break;

            case Elements.CreditLine.SourceDocumentID:

                if (this.documents.containsKey(value)) {

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Lines.CreditLine),
                            this.documents.get(value),
                            Entities.CreditLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                } else {

                    String identifier = this.constructor.CREATE(element, value);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Lines.CreditLine),
                            identifier,
                            Entities.CreditLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                    this.documents.put(value, identifier);

                }

                break;

            case Elements.CreditLine.SystemEntryDate:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.CreditLine),
                        Entities.Labels.CreditLine,
                        element,
                        value,
                        Entities.CreditLineRelationships.HAS_SYSTEM_ENTRY_DATE
                );

                break;

            case Elements.CreditLine.Description:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.CreditLine),
                        Entities.Labels.CreditLine,
                        element,
                        value,
                        Entities.CreditLineRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.CreditLine.CreditAmount:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.CreditLine),
                        Entities.Labels.CreditLine,
                        element,
                        Double.parseDouble(value),
                        Entities.CreditLineRelationships.HAS_CREDIT_AMOUNT
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processDebitLine(String element, String value) throws MapException {

        switch (element) {

            case Elements.DebitLine.RecordID:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Lines.DebitLine),
                        element,
                        value
                );

                break;

            case Elements.DebitLine.AccountID:

                this.constructor.CREATE_RELATIONSHIP(
                        this.container.get(Elements.Lines.DebitLine),
                        this.accounts.get(value),
                        Entities.OtherRelationships.HAS_ACCOUNT
                );

                break;

            case Elements.DebitLine.SourceDocumentID:

                if (this.documents.containsKey(value)) {

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Lines.DebitLine),
                            this.documents.get(value),
                            Entities.DebitLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                } else {

                    String identifier = this.constructor.CREATE(element, value);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Lines.DebitLine),
                            identifier,
                            Entities.DebitLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                    this.documents.put(value, identifier);

                }

                break;

            case Elements.DebitLine.SystemEntryDate:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.DebitLine),
                        Entities.Labels.DebitLine,
                        element,
                        value,
                        Entities.DebitLineRelationships.HAS_SYSTEM_ENTRY_DATE
                );

                break;

            case Elements.DebitLine.Description:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.DebitLine),
                        Entities.Labels.DebitLine,
                        element,
                        value,
                        Entities.DebitLineRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.DebitLine.DebitAmount:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Lines.DebitLine),
                        Entities.Labels.DebitLine,
                        element,
                        Double.parseDouble(value),
                        Entities.DebitLineRelationships.HAS_DEBIT_AMOUNT
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processSourceDocumentsChildren(String element, String value, int count) throws MapException {

        count++;

        switch (this.currentSequences.get(count)) {

            case Elements.SourceDocuments.SalesInvoices:

                if (Elements.SourceDocuments.SalesInvoices.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.SalesInvoices);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_SALES_INVOICES
                    );

                } else {

                    this.processSalesInvoicesChildren(element, value, count);

                }

                break;

            case Elements.SourceDocuments.MovementOfGoods:

                if (Elements.SourceDocuments.MovementOfGoods.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.MovementOfGoods);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_MOVEMENT_OF_GOODS
                    );

                } else {

                    this.processMovementOfGoodsChildren(element, value, count);

                }

                break;

            case Elements.SourceDocuments.WorkingDocuments:

                if (Elements.SourceDocuments.WorkingDocuments.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.WorkingDocuments);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_WORKING_DOCUMENTS
                    );

                } else {

                    this.processWorkingDocumentsChildren(element, value, count);

                }

                break;

            case Elements.SourceDocuments.Payments:

                if (Elements.SourceDocuments.Payments.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Payments);
                    this.container.put(element, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.RootElement.AuditFile),
                            identifier,
                            Entities.FileRelationships.HAS_PAYMENTS
                    );

                } else {

                    this.processPaymentsChildren(element, value, count);

                }

                break;

            default:
                throw new MapException(this.currentSequences.get(count));
        }

    }

    private void processSalesInvoicesChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.SalesInvoices.NumberOfEntries:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.SourceDocuments.SalesInvoices),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.SalesInvoices.TotalDebit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.SalesInvoices),
                            Entities.Labels.SalesInvoices,
                            element,
                            Double.parseDouble(value),
                            Entities.SalesInvoicesRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.SalesInvoices.TotalCredit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.SalesInvoices),
                            Entities.Labels.SalesInvoices,
                            element,
                            Double.parseDouble(value),
                            Entities.SalesInvoicesRelationships.HAS_TOTAL_CREDIT
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.SalesInvoices.Invoice.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (!Elements.SalesInvoices.Invoice.equalsIgnoreCase(element)) {

                    this.processInvoiceChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processInvoiceChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Invoice.InvoiceNo:

                    if (this.documents.containsKey(value)) {

                        identifier = this.documents.get(value);
                        this.constructor.SET_LABEL(identifier, Entities.Labels.Invoice);
                        this.constructor.SET_PROPERTY(identifier, element, value);

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Invoice, element, value);

                    }

                    this.container.put(Elements.SalesInvoices.Invoice, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.SourceDocuments.SalesInvoices),
                            identifier,
                            Entities.SalesInvoicesRelationships.HAS_INVOICE
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.InvoiceRelationships.HAS_SELLER
                    );

                    break;

                case Elements.Invoice.ATCUD:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_ATCUD
                    );

                    break;

                case Elements.Invoice.Hash:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_HASH
                    );

                    break;

                case Elements.Invoice.HashControl:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_HASH_CONTROL
                    );

                    break;

                case Elements.Invoice.Period:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.InvoiceRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.Invoice.InvoiceDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_INVOICE_DATE
                    );

                    break;

                case Elements.Invoice.InvoiceType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_INVOICE_TYPE
                    );

                    break;

                case Elements.Invoice.SourceID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_SOURCE_ID
                    );

                    break;

                case Elements.Invoice.EACCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_EAC_CODE
                    );

                    break;

                case Elements.Invoice.SystemEntryDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_SYSTEM_ENTRY_DATE
                    );

                    break;

                case Elements.Invoice.TransactionID:

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            this.transactions.get(value),
                            Entities.OtherRelationships.HAS_TRANSACTION
                    );

                    break;

                case Elements.Invoice.CustomerID:

                    identifier = this.container.get(Elements.SalesInvoices.Invoice);
                    String customer = this.customers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            customer,
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    if (this.represents.containsKey(customer)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(customer),
                                Entities.InvoiceRelationships.HAS_BUYER
                        );

                    }

                    break;

                case Elements.Invoice.MovementEndTime:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_MOVEMENT_END_TIME
                    );

                    break;

                case Elements.Invoice.MovementStartTime:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SalesInvoices.Invoice),
                            Entities.Labels.InvoiceInfo,
                            element,
                            value,
                            Entities.InvoiceRelationships.HAS_MOVEMENT_START_TIME
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.Invoice.DocumentStatus:

                    if (Elements.Invoice.DocumentStatus.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_DOCUMENT_STATUS
                        );

                    } else {

                        this.processDocumentStatusChildren(element, value);

                    }

                    break;

                case Elements.Invoice.SpecialRegimes:

                    if (Elements.Invoice.SpecialRegimes.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SPECIAL_REGIMES
                        );

                    } else {

                        this.processSpecialRegimesChildren(element, value);

                    }

                    break;

                case Elements.Invoice.ShipTo:

                    if (Elements.Invoice.ShipTo.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SHIP_TO
                        );

                    } else {

                        this.processShipToChildren(Elements.Invoice.ShipTo, element, value, count);

                    }

                    break;

                case Elements.Invoice.ShipFrom:

                    if (Elements.Invoice.ShipFrom.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SHIP_FROM
                        );

                    } else {

                        this.processShipFromChildren(Elements.Invoice.ShipFrom, element, value, count);

                    }

                    break;

                case Elements.Invoice.Line:

                    if (Elements.Invoice.Line.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_LINE
                        );

                    } else {

                        this.processLineChildren(Entities.Labels.InvoiceInfo, element, value, count);

                    }

                    break;

                case Elements.Invoice.DocumentTotals:

                    if (Elements.Invoice.DocumentTotals.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_DOCUMENT_TOTALS
                        );

                    } else {

                        this.processDocumentTotalsChildren(
                                Elements.Invoice.DocumentTotals,
                                Entities.Labels.InvoiceInfo,
                                element,
                                value,
                                count
                        );

                    }

                    break;

                case Elements.Invoice.WithholdingTax:

                    if (Elements.Invoice.WithholdingTax.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.InvoiceInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.SalesInvoices.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_WITHHOLDING_TAX
                        );

                    } else {

                        this.processWithholdingTaxChildren(
                                Elements.Invoice.WithholdingTax,
                                Entities.Labels.InvoiceInfo,
                                element,
                                value
                        );

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processMovementOfGoodsChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.MovementOfGoods.NumberOfMovementLines:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.SourceDocuments.MovementOfGoods),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.MovementOfGoods.TotalQuantityIssued:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.MovementOfGoods),
                            Entities.Labels.MovementOfGoods,
                            element,
                            Double.parseDouble(value),
                            Entities.MovementOfGoodsRelationships.HAS_TOTAL_QUANTITY_ISSUED
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.MovementOfGoods.StockMovement.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (!Elements.MovementOfGoods.StockMovement.equalsIgnoreCase(element)) {

                    this.processStockMovementChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processStockMovementChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.StockMovement.DocumentNumber:

                    if (this.documents.containsKey(value)) {

                        identifier = this.documents.get(value);
                        this.constructor.SET_LABEL(identifier, Entities.Labels.StockMovement);
                        this.constructor.SET_PROPERTY(identifier, element, value);

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.StockMovement, element, value);

                    }

                    this.container.put(Elements.MovementOfGoods.StockMovement, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.SourceDocuments.MovementOfGoods),
                            identifier,
                            Entities.MovementOfGoodsRelationships.HAS_STOCK_MOVEMENT
                    );

                    break;

                case Elements.StockMovement.ATCUD:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_ATCUD
                    );

                    break;

                case Elements.StockMovement.Hash:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_HASH
                    );

                    break;

                case Elements.StockMovement.HashControl:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_HASH_CONTROL
                    );

                    break;

                case Elements.StockMovement.Period:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.StockMovementRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.StockMovement.MovementDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_MOVEMENT_DATE
                    );

                    break;

                case Elements.StockMovement.MovementType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_MOVEMENT_TYPE
                    );

                    break;

                case Elements.StockMovement.SystemEntryDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_SYSTEM_ENTRY_DATE
                    );

                    break;

                case Elements.StockMovement.TransactionID:

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            this.transactions.get(value),
                            Entities.OtherRelationships.HAS_TRANSACTION
                    );

                    break;

                case Elements.StockMovement.CustomerID:

                    identifier = this.container.get(Elements.MovementOfGoods.StockMovement);
                    String customer = this.customers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            customer,
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.StockMovementRelationships.HAS_SELLER
                    );

                    if (this.represents.containsKey(customer)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(customer),
                                Entities.StockMovementRelationships.HAS_BUYER
                        );

                    }

                    break;

                case Elements.StockMovement.SupplierID:

                    identifier = this.container.get(Elements.MovementOfGoods.StockMovement);
                    String supplier = this.suppliers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            supplier,
                            Entities.OtherRelationships.HAS_SUPPLIER
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.StockMovementRelationships.HAS_BUYER
                    );

                    if (this.represents.containsKey(supplier)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(supplier),
                                Entities.StockMovementRelationships.HAS_SELLER
                        );

                    }

                    break;

                case Elements.StockMovement.SourceID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_SOURCE_ID
                    );

                    break;

                case Elements.StockMovement.EACCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_EAC_CODE
                    );

                    break;

                case Elements.StockMovement.MovementComments:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_MOVEMENT_COMMENTS
                    );

                    break;

                case Elements.StockMovement.MovementEndTime:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_MOVEMENT_END_TIME
                    );

                    break;

                case Elements.StockMovement.MovementStartTime:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_MOVEMENT_START_TIME
                    );

                    break;

                case Elements.StockMovement.ATDocCodeID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.MovementOfGoods.StockMovement),
                            Entities.Labels.StockMovementInfo,
                            element,
                            value,
                            Entities.StockMovementRelationships.HAS_AT_DOC_CODE_ID
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.StockMovement.DocumentStatus:

                    if (Elements.StockMovement.DocumentStatus.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.StockMovementInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MovementOfGoods.StockMovement),
                                identifier,
                                Entities.StockMovementRelationships.HAS_DOCUMENT_STATUS
                        );

                    } else {

                        this.processDocumentStatusChildren(element, value);

                    }

                    break;

                case Elements.StockMovement.ShipTo:

                    if (Elements.StockMovement.ShipTo.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.StockMovementInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MovementOfGoods.StockMovement),
                                identifier,
                                Entities.StockMovementRelationships.HAS_SHIP_TO
                        );

                    } else {

                        this.processShipToChildren(Elements.StockMovement.ShipTo, element, value, count);

                    }

                    break;

                case Elements.StockMovement.ShipFrom:

                    if (Elements.StockMovement.ShipFrom.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.StockMovementInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MovementOfGoods.StockMovement),
                                identifier,
                                Entities.StockMovementRelationships.HAS_SHIP_FROM
                        );

                    } else {

                        this.processShipFromChildren(Elements.StockMovement.ShipFrom, element, value, count);

                    }

                    break;

                case Elements.StockMovement.Line:

                    if (Elements.StockMovement.Line.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.StockMovementInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MovementOfGoods.StockMovement),
                                identifier,
                                Entities.StockMovementRelationships.HAS_LINE
                        );

                    } else {

                        this.processLineChildren(Entities.Labels.StockMovementInfo, element, value, count);

                    }

                    break;

                case Elements.StockMovement.DocumentTotals:

                    if (Elements.StockMovement.DocumentTotals.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.StockMovementInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.MovementOfGoods.StockMovement),
                                identifier,
                                Entities.StockMovementRelationships.HAS_DOCUMENT_TOTALS
                        );

                    } else {

                        this.processDocumentTotalsChildren(
                                Elements.StockMovement.DocumentTotals,
                                Entities.Labels.StockMovementInfo,
                                element,
                                value,
                                count
                        );

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processWorkingDocumentsChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.WorkingDocuments.NumberOfEntries:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.SourceDocuments.WorkingDocuments),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.WorkingDocuments.TotalDebit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.WorkingDocuments),
                            Entities.Labels.WorkingDocuments,
                            element,
                            Double.parseDouble(value),
                            Entities.WorkingDocumentsRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.WorkingDocuments.TotalCredit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.WorkingDocuments),
                            Entities.Labels.WorkingDocuments,
                            element,
                            Double.parseDouble(value),
                            Entities.WorkingDocumentsRelationships.HAS_TOTAL_CREDIT
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.WorkingDocuments.WorkDocument.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (!Elements.WorkingDocuments.WorkDocument.equalsIgnoreCase(element)) {

                    this.processWorkDocumentChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processWorkDocumentChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.WorkDocument.DocumentNumber:

                    if (this.documents.containsKey(value)) {

                        identifier = this.documents.get(value);
                        this.constructor.SET_LABEL(identifier, Entities.Labels.WorkDocument);
                        this.constructor.SET_PROPERTY(identifier, element, value);

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.WorkDocument, element, value);

                    }

                    this.container.put(Elements.WorkingDocuments.WorkDocument, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.SourceDocuments.WorkingDocuments),
                            identifier,
                            Entities.WorkingDocumentsRelationships.HAS_WORK_DOCUMENT
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.WorkDocumentRelationships.HAS_SELLER
                    );

                    break;

                case Elements.WorkDocument.ATCUD:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_ATCUD
                    );

                    break;

                case Elements.WorkDocument.Hash:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_HASH
                    );

                    break;

                case Elements.WorkDocument.HashControl:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_HASH_CONTROL
                    );

                    break;

                case Elements.WorkDocument.Period:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.WorkDocumentRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.WorkDocument.WorkDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_WORK_DATE
                    );

                    break;

                case Elements.WorkDocument.WorkType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_WORK_TYPE
                    );

                    break;

                case Elements.WorkDocument.SourceID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_SOURCE_ID
                    );

                    break;

                case Elements.WorkDocument.EACCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_EAC_CODE
                    );

                    break;

                case Elements.WorkDocument.SystemEntryDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            Entities.Labels.WorkDocumentInfo,
                            element,
                            value,
                            Entities.WorkDocumentRelationships.HAS_SYSTEM_ENTRY_DATE
                    );

                    break;

                case Elements.WorkDocument.TransactionID:

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.WorkingDocuments.WorkDocument),
                            this.transactions.get(value),
                            Entities.OtherRelationships.HAS_TRANSACTION
                    );

                    break;

                case Elements.WorkDocument.CustomerID:

                    identifier = this.container.get(Elements.WorkingDocuments.WorkDocument);
                    String customer = this.customers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            customer,
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    if (this.represents.containsKey(customer)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(customer),
                                Entities.WorkDocumentRelationships.HAS_BUYER
                        );

                    }

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.WorkDocument.DocumentStatus:

                    if (Elements.WorkDocument.DocumentStatus.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.WorkDocumentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.WorkingDocuments.WorkDocument),
                                identifier,
                                Entities.WorkDocumentRelationships.HAS_DOCUMENT_STATUS
                        );

                    } else {

                        this.processDocumentStatusChildren(element, value);

                    }

                    break;

                case Elements.WorkDocument.Line:

                    if (Elements.WorkDocument.Line.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.WorkDocumentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.WorkingDocuments.WorkDocument),
                                identifier,
                                Entities.WorkDocumentRelationships.HAS_LINE
                        );

                    } else {

                        this.processLineChildren(Entities.Labels.WorkDocumentInfo, element, value, count);

                    }

                    break;

                case Elements.WorkDocument.DocumentTotals:

                    if (Elements.WorkDocument.DocumentTotals.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.WorkDocumentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.WorkingDocuments.WorkDocument),
                                identifier,
                                Entities.WorkDocumentRelationships.HAS_DOCUMENT_TOTALS
                        );

                    } else {

                        this.processDocumentTotalsChildren(
                                Elements.WorkDocument.DocumentTotals,
                                Entities.Labels.WorkDocumentInfo,
                                element,
                                value,
                                count
                        );

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processPaymentsChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.Payments.NumberOfEntries:

                    this.constructor.SET_PROPERTY(
                            this.container.get(Elements.SourceDocuments.Payments),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.Payments.TotalDebit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.Payments),
                            Entities.Labels.Payments,
                            element,
                            Double.parseDouble(value),
                            Entities.PaymentsRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.Payments.TotalCredit:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.SourceDocuments.Payments),
                            Entities.Labels.Payments,
                            element,
                            Double.parseDouble(value),
                            Entities.PaymentsRelationships.HAS_TOTAL_CREDIT
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Payments.Payment.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (!Elements.Payments.Payment.equalsIgnoreCase(element)) {

                    this.processPaymentChildren(element, value, count);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processPaymentChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            String identifier;

            switch (element) {

                case Elements.Payment.PaymentRefNo:

                    if (this.documents.containsKey(value)) {

                        identifier = this.documents.get(value);
                        this.constructor.SET_LABEL(identifier, Entities.Labels.Payment);
                        this.constructor.SET_PROPERTY(identifier, element, value);

                    } else {

                        identifier = this.constructor.CREATE(Entities.Labels.Payment, element, value);

                    }

                    this.container.put(Elements.Payments.Payment, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.SourceDocuments.Payments),
                            identifier,
                            Entities.PaymentsRelationships.HAS_PAYMENT
                    );

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            this.rootCompany,
                            Entities.PaymentRelationships.WAS_PAID
                    );

                    break;

                case Elements.Payment.ATCUD:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_ATCUD
                    );

                    break;

                case Elements.Payment.Period:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            Integer.parseInt(value),
                            Entities.PaymentRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.Payment.TransactionID:

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Payments.Payment),
                            this.transactions.get(value),
                            Entities.OtherRelationships.HAS_TRANSACTION
                    );

                    break;

                case Elements.Payment.TransactionDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_TRANSACTION_DATE
                    );

                    break;

                case Elements.Payment.PaymentType:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_PAYMENT_TYPE
                    );

                    break;

                case Elements.Payment.Description:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_DESCRIPTION
                    );

                    break;

                case Elements.Payment.SystemID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_SYSTEM_ID
                    );

                    break;

                case Elements.Payment.SourceID:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_SOURCE_ID
                    );

                    break;

                case Elements.Payment.SystemEntryDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Payments.Payment),
                            Entities.Labels.PaymentInfo,
                            element,
                            value,
                            Entities.PaymentRelationships.HAS_SYSTEM_ENTRY_DATE
                    );

                    break;

                case Elements.Payment.CustomerID:

                    identifier = this.container.get(Elements.Payments.Payment);
                    String customer = this.customers.get(value);

                    this.constructor.CREATE_RELATIONSHIP(
                            identifier,
                            customer,
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    if (this.represents.containsKey(customer)) {

                        this.constructor.CREATE_RELATIONSHIP(
                                identifier,
                                this.represents.get(customer),
                                Entities.PaymentRelationships.PAID
                        );

                    }

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.Payment.DocumentStatus:

                    if (Elements.Payment.DocumentStatus.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.PaymentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Payments.Payment),
                                identifier,
                                Entities.PaymentRelationships.HAS_DOCUMENT_STATUS
                        );

                    } else {

                        this.processDocumentStatusChildren(element, value);

                    }

                    break;

                case Elements.Payment.PaymentMethod:

                    if (Elements.Payment.PaymentMethod.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.PaymentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Payments.Payment),
                                identifier,
                                Entities.PaymentRelationships.HAS_PAYMENT_METHOD
                        );

                    } else {

                        this.processPaymentMethodChildren(element, value);

                    }

                    break;

                case Elements.Payment.Line:

                    if (Elements.Payment.Line.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.PaymentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Payments.Payment),
                                identifier,
                                Entities.PaymentRelationships.HAS_LINE
                        );

                    } else {

                        this.processLineChildren(Entities.Labels.PaymentInfo, element, value, count);

                    }

                    break;

                case Elements.Payment.DocumentTotals:

                    if (Elements.Payment.DocumentTotals.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.PaymentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Payments.Payment),
                                identifier,
                                Entities.PaymentRelationships.HAS_DOCUMENT_TOTALS
                        );

                    } else {

                        this.processDocumentTotalsChildren(
                                Elements.Payment.DocumentTotals,
                                Entities.Labels.PaymentInfo,
                                element,
                                value,
                                count
                        );

                    }

                    break;

                case Elements.Payment.WithholdingTax:

                    if (Elements.Payment.WithholdingTax.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(Entities.Labels.PaymentInfo);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Payments.Payment),
                                identifier,
                                Entities.PaymentRelationships.HAS_WITHHOLDING_TAX
                        );

                    } else {

                        this.processWithholdingTaxChildren(
                                Elements.Payment.WithholdingTax,
                                Entities.Labels.PaymentInfo,
                                element,
                                value
                        );

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processDocumentStatusChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.DocumentStatus.InvoiceStatus:

            case Elements.DocumentStatus.InvoiceStatusDate:

            case Elements.DocumentStatus.MovementStatus:

            case Elements.DocumentStatus.MovementStatusDate:

            case Elements.DocumentStatus.WorkStatus:

            case Elements.DocumentStatus.WorkStatusDate:

            case Elements.DocumentStatus.PaymentStatus:

            case Elements.DocumentStatus.PaymentStatusDate:

            case Elements.DocumentStatus.Reason:

            case Elements.DocumentStatus.SourceID:

            case Elements.DocumentStatus.SourceBilling:

            case Elements.DocumentStatus.SourcePayment:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Invoice.DocumentStatus),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processSpecialRegimesChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.SpecialRegimes.SelfBillingIndicator:

            case Elements.SpecialRegimes.CashVATSchemeIndicator:

            case Elements.SpecialRegimes.ThirdPartiesBillingIndicator:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Invoice.SpecialRegimes),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processShipToChildren(String containerId, String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.ShipTo.DeliveryID:

                case Elements.ShipTo.DeliveryDate:

                case Elements.ShipTo.WarehouseID:

                case Elements.ShipTo.LocationID:

                    this.constructor.SET_PROPERTY(
                            this.container.get(containerId),
                            element,
                            value
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.ShipTo.Address.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.ShipTo.Address.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Address);
                    this.container.put(Entities.Labels.Address, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(containerId),
                            identifier,
                            Entities.ShipToRelationships.HAS_ADDRESS
                    );

                } else {

                    this.processAddressChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processShipFromChildren(String containerId, String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.ShipFrom.DeliveryID:

                case Elements.ShipFrom.DeliveryDate:

                case Elements.ShipFrom.WarehouseID:

                case Elements.ShipFrom.LocationID:

                    this.constructor.SET_PROPERTY(
                            this.container.get(containerId),
                            element,
                            value
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.ShipFrom.Address.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.ShipFrom.Address.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE(Entities.Labels.Address);
                    this.container.put(Entities.Labels.Address, identifier);

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(containerId),
                            identifier,
                            Entities.ShipFromRelationships.HAS_ADDRESS
                    );

                } else {

                    this.processAddressChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }

    }

    private void processLineChildren(String label, String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.Line.LineNumber:
                    this.constructor.SET_PROPERTY(this.container.get(Elements.Invoice.Line), element, Integer.parseInt(value));

                    break;

                case Elements.Line.ProductCode:

                    this.constructor.CREATE_RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.products.get(value),
                            Entities.OtherRelationships.HAS_PRODUCT
                    );

                    break;

                case Elements.Line.ProductDescription:
                    //Não é necessário processar

                    break;

                case Elements.Line.Quantity:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_QUANTITY
                    );

                    break;

                case Elements.Line.UnitOfMeasure:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            value,
                            Entities.LineRelationships.HAS_UNIT_OF_MEASURE
                    );

                    break;

                case Elements.Line.UnitPrice:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_UNIT_PRICE
                    );

                    break;

                case Elements.Line.TaxBase:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_TAX_BASE
                    );

                    break;

                case Elements.Line.TaxPointDate:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            value,
                            Entities.LineRelationships.HAS_TAX_POINT_DATE
                    );

                    break;

                case Elements.Line.Description:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            value,
                            Entities.LineRelationships.HAS_DESCRIPTION
                    );

                    break;

                case Elements.Line.DebitAmount:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_DEBIT_AMOUNT
                    );

                    break;

                case Elements.Line.CreditAmount:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_CREDIT_AMOUNT
                    );

                    break;

                case Elements.Line.TaxExemptionReason:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            value,
                            Entities.LineRelationships.HAS_TAX_EXEMPTION_REASON
                    );

                    break;

                case Elements.Line.TaxExemptionCode:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            value,
                            Entities.LineRelationships.HAS_TAX_EXEMPTION_CODE
                    );

                    break;

                case Elements.Line.SettlementAmount:

                    this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                            this.container.get(Elements.Invoice.Line),
                            label,
                            element,
                            Double.parseDouble(value),
                            Entities.LineRelationships.HAS_SETTLEMENT_AMOUNT
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.Line.OrderReferences:

                    if (Elements.Line.OrderReferences.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_ORDER_REFERENCES
                        );

                    } else {

                        this.processOrderReferencesChildren(element, value);

                    }

                    break;

                case Elements.Line.References:

                    if (Elements.Line.References.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_REFERENCES
                        );

                    } else {

                        this.processReferencesChildren(element, value);

                    }

                    break;

                case Elements.Line.ProductSerialNumber:

                    if (Elements.Line.ProductSerialNumber.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_PRODUCT_SERIAL_NUMBER
                        );

                    } else {

                        this.processProductSerialNumberChildren(element, value);

                    }

                    break;

                case Elements.Line.Tax:

                    if (Elements.Line.Tax.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_TAX_TABLE
                        );

                    } else {

                        this.processTaxChildren(label, element, value);

                    }

                    break;

                case Elements.Line.CustomsInformation:

                    if (Elements.Line.CustomsInformation.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_CUSTOMS_INFORMATION
                        );

                    } else {

                        this.processCustomsInformationChildren(element, value);

                    }

                    break;

                case Elements.Line.SourceDocumentID:

                    if (Elements.Line.SourceDocumentID.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_SOURCE_DOCUMENT_ID
                        );

                    } else {

                        this.processSourceDocumentIDChildren(element, value);

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processOrderReferencesChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.OrderReferences.OriginatingON:

            case Elements.OrderReferences.OrderDate:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.OrderReferences),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processReferencesChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.References.Reference:

            case Elements.References.Reason:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.References),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processProductSerialNumberChildren(String element, String value) throws MapException {

        if (Elements.ProductSerialNumber.SerialNumber.equalsIgnoreCase(element)) {

            this.constructor.SET_PROPERTY(
                    this.container.get(Elements.Line.ProductSerialNumber),
                    element,
                    value
            );

        } else {

            throw new MapException(element);

        }

    }

    private void processTaxChildren(String label, String element, String value) throws MapException {

        switch (element) {

            case Elements.Tax.TaxType:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Line.Tax),
                        label,
                        element,
                        value,
                        Entities.LineTaxRelationships.HAS_TAX_TYPE
                );

                break;

            case Elements.Tax.TaxCountryRegion:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Line.Tax),
                        label,
                        element,
                        value,
                        Entities.LineTaxRelationships.HAS_TAX_COUNTRY_REGION
                );

                break;

            case Elements.Tax.TaxCode:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.Tax),
                        element,
                        value
                );

                break;

            case Elements.Tax.TaxPercentage:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Line.Tax),
                        label,
                        element,
                        Double.parseDouble(value),
                        Entities.LineTaxRelationships.HAS_TAX_PERCENTAGE
                );

                break;

            case Elements.Tax.TaxAmount:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Elements.Line.Tax),
                        label,
                        element,
                        Double.parseDouble(value),
                        Entities.LineTaxRelationships.HAS_TAX_AMOUNT
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processCustomsInformationChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.CustomsInformation.ARCNo:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.CustomsInformation),
                        element,
                        value
                );

                break;

            case Elements.CustomsInformation.IECAmount:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.CustomsInformation),
                        element,
                        Double.parseDouble(value)
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processSourceDocumentIDChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.SourceDocumentID.OriginatingON:

            case Elements.SourceDocumentID.InvoiceDate:

            case Elements.SourceDocumentID.Description:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.Line.SourceDocumentID),
                        element,
                        value
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processDocumentTotalsChildren(String document, String label, String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.DocumentTotals.TaxPayable:

                case Elements.DocumentTotals.NetTotal:

                case Elements.DocumentTotals.GrossTotal:

                    this.constructor.SET_PROPERTY(
                            this.container.get(document),
                            element,
                            Double.parseDouble(value)
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.DocumentTotals.Currency:

                    if (Elements.DocumentTotals.Currency.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(document),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_CURRENCY
                        );

                    } else {

                        this.processCurrencyChildren(element, value);

                    }

                    break;

                case Elements.DocumentTotals.Settlement:

                    if (Elements.DocumentTotals.Settlement.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(document),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_SETTLEMENT
                        );

                    } else {

                        this.processSettlementChildren(element, value);

                    }

                    break;

                case Elements.DocumentTotals.Payment:

                    if (Elements.DocumentTotals.Payment.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE(label);
                        this.container.put(element, identifier);

                        this.constructor.CREATE_RELATIONSHIP(
                                this.container.get(document),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_PAYMENT
                        );

                    } else {

                        this.processPaymentMethodChildren(element, value);

                    }

                    break;

                default:
                    throw new MapException(this.currentSequences.get(count));
            }

        }

    }

    private void processCurrencyChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.Currency.CurrencyCode:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Currency),
                        element,
                        value
                );

                break;

            case Elements.Currency.CurrencyAmount:

            case Elements.Currency.ExchangeRate:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Currency),
                        element,
                        Double.parseDouble(value)
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processSettlementChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.Settlement.SettlementDiscount:

            case Elements.Settlement.SettlementDate:

            case Elements.Settlement.PaymentTerms:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Settlement),
                        element,
                        value
                );

                break;

            case Elements.Settlement.SettlementAmount:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Settlement),
                        element,
                        Double.parseDouble(value)
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processPaymentMethodChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.PaymentMethod.PaymentMechanism:

            case Elements.PaymentMethod.PaymentDate:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Payment),
                        element,
                        value
                );

                break;

            case Elements.PaymentMethod.PaymentAmount:

                this.constructor.SET_PROPERTY(
                        this.container.get(Elements.DocumentTotals.Payment),
                        element,
                        Double.parseDouble(value)
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processWithholdingTaxChildren(String withholding, String label, String element, String value) throws MapException {

        switch (element) {

            case Elements.WithholdingTax.WithholdingTaxType:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(withholding),
                        label,
                        element,
                        value,
                        Entities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_TYPE
                );

                break;

            case Elements.WithholdingTax.WithholdingTaxDescription:

                this.constructor.SET_PROPERTY(
                        this.container.get(withholding),
                        element,
                        value
                );

                break;

            case Elements.WithholdingTax.WithholdingTaxAmount:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(withholding),
                        label,
                        element,
                        Double.parseDouble(value),
                        Entities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_AMOUNT
                );

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processAddressChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.CompanyAddress.BuildingNumber:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_BUILDING_NUMBER
                );

                break;

            case Elements.CompanyAddress.StreetName:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_STREET_NAME
                );

                break;

            case Elements.CompanyAddress.AddressDetail:
                this.constructor.SET_PROPERTY(this.container.get(Entities.Labels.Address), element, value);

                break;

            case Elements.CompanyAddress.City:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_CITY
                );

                break;

            case Elements.CompanyAddress.PostalCode:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_POSTAL_CODE
                );

                break;

            case Elements.CompanyAddress.Region:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_REGION
                );

                break;

            case Elements.CompanyAddress.Country:

                this.constructor.CREATE_AND_RELATE_TO_RIGHT(
                        this.container.get(Entities.Labels.Address),
                        Entities.Labels.Address,
                        element,
                        value,
                        Entities.AddressRelationships.HAS_COUNTRY
                );

                break;

            default:
                throw new MapException(element);
        }

    }
}
