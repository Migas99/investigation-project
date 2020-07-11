package Mapper;

import Enumerations.Elements;
import Enumerations.Entities;
import Exceptions.MapException;
import org.neo4j.driver.Driver;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class Mapper {

    private final Driver driver;
    private final QueryConstructor constructor;

    private int depth;
    private final LinkedList<String> currentSequences;
    private final String fileName;

    private final Map<String, String> container;

    /*Listas de auxílio à criação de relações entre entidades*/
    private final Map<String, String> companies;
    private final Map<String, String> accounts;
    private final Map<String, String> customers;
    private final Map<String, String> suppliers;
    private final Map<String, String> products;
    private final Map<String, String> sources;
    private final Map<String, String> transactions;
    private final Map<String, String> documents;

    public Mapper(Driver driver, String fileName) {
        this.driver = driver;
        this.constructor = new QueryConstructor();
        this.depth = -1;
        this.currentSequences = new LinkedList<>();
        this.fileName = fileName.substring(0, fileName.length() - 4);

        this.container = new HashMap<>();
        this.companies = new HashMap<>();
        this.accounts = new HashMap<>();
        this.customers = new HashMap<>();
        this.suppliers = new HashMap<>();
        this.products = new HashMap<>();
        this.transactions = new HashMap<>();
        this.documents = new HashMap<>();
        this.sources = new HashMap<>();
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
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("\nElement not mapped found!\nElement: " + element + "\n.");
            System.exit(1);
        }
    }

    public String requestQuery() {
        return this.constructor.getUploadQuery();
    }

    private void processRootElement(String element, String value, int count) throws MapException {

        if (Elements.RootElement.AuditFile.equalsIgnoreCase(this.currentSequences.get(count))) {

            if (Elements.RootElement.AuditFile.equalsIgnoreCase(element)) {
                String identifier = this.constructor.CREATE("FileName", this.fileName);
                this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.File);
                this.container.put(Entities.EntitiesValues.File, identifier);

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

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.FileInformation, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_ADDITIONAL_INFORMATION
                    );

                } else {

                    this.processHeaderChildren(element, value, count);

                }

                break;

            case Elements.AuditFile.MasterFiles:

                if (Elements.AuditFile.MasterFiles.equalsIgnoreCase(element)) {

                    // this.container.clear();

                } else {

                    this.processMasterFilesChildren(element, value, count);

                }

                break;

            case Elements.AuditFile.GeneralLedgerEntries:

                if (Elements.AuditFile.GeneralLedgerEntries.equalsIgnoreCase(element)) {

                    //this.container.clear();

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.GeneralLedgerEntries, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(
                            identifier,
                            Entities.EntitiesValues.GeneralLedgerEntries
                    );

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_GENERAL_LEDGER_ENTRIES
                    );

                } else {

                    this.processGeneralLedgerEntriesChildren(element, value, count);

                }

                break;

            case Elements.AuditFile.SourceDocuments:

                if (Elements.AuditFile.SourceDocuments.equalsIgnoreCase(element)) {

                    //this.container.clear();

                } else {

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
                    this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.FileInformation), element, value);

                    break;

                case Elements.Header.CompanyID:
                    this.container.put(element, this.constructor.CREATE(element, value));

                    break;

                case Elements.Header.TaxRegistrationNumber:
                    this.container.put(element, this.constructor.CREATE(element, Integer.parseInt(value)));

                    break;

                case Elements.Header.TaxAccountingBasis:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            this.constructor.CREATE(element, value),
                            Entities.FileInformationRelationships.HAS_TAX_ACCOUNTING_BASIS
                    );

                    break;

                case Elements.Header.CompanyName:

                    identifier = this.constructor.CREATE(element, value);
                    this.container.put(Entities.EntitiesValues.Company, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Company);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.RELATED_TO
                    );

                    this.constructor.RELATIONSHIP(
                            identifier,
                            this.container.remove(Elements.Header.CompanyID),
                            Entities.CompanyRelationships.HAS_COMPANY_ID
                    );

                    this.constructor.RELATIONSHIP(
                            identifier,
                            this.container.remove(Elements.Header.TaxRegistrationNumber),
                            Entities.CompanyRelationships.HAS_TAX_REGISTRATION_NUMBER
                    );

                    break;

                case Elements.Header.BussinessName:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            this.constructor.CREATE(element, value),
                            Entities.CompanyRelationships.HAS_BUSINESS_NAME
                    );

                    break;

                case Elements.Header.FiscalYear:

                    identifier = this.constructor.CREATE(element, Integer.parseInt(value));
                    this.container.put(Entities.EntitiesValues.FiscalYear, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            identifier,
                            Entities.FileInformationRelationships.HAS_FISCAL_YEAR
                    );

                    break;

                case Elements.Header.StartDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FiscalYear),
                            this.constructor.CREATE(element, value),
                            Entities.FiscalYearRelationships.HAS_START_DATE
                    );

                    break;

                case Elements.Header.EndDate:

                    this.constructor.RELATIONSHIP(
                            this.container.remove(Entities.EntitiesValues.FiscalYear),
                            this.constructor.CREATE(element, value),
                            Entities.FiscalYearRelationships.HAS_END_DATE
                    );

                    break;

                case Elements.Header.CurrencyCode:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            this.constructor.CREATE(element, value),
                            Entities.FileInformationRelationships.HAS_CURRENCY_CODE
                    );

                    break;

                case Elements.Header.DateCreated:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            this.constructor.CREATE(element, value),
                            Entities.FileInformationRelationships.HAS_DATE_CREATED
                    );

                    break;

                case Elements.Header.TaxEntity:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            this.constructor.CREATE(element, value),
                            Entities.FileInformationRelationships.HAS_TAX_ENTITY
                    );

                    break;

                case Elements.Header.ProductCompanyTaxID:

                    identifier = this.constructor.CREATE(element, value);
                    this.container.put(element, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            identifier,
                            Entities.FileInformationRelationships.PRODUCED_BY
                    );

                    break;

                case Elements.Header.SoftwareCertificateNumber:

                    this.constructor.PROPERTY(
                            this.container.get(Elements.Header.ProductCompanyTaxID),
                            element,
                            Integer.parseInt(value)
                    );

                    break;

                case Elements.Header.ProductID:

                    this.constructor.PROPERTY(
                            this.container.get(Elements.Header.ProductCompanyTaxID),
                            element,
                            value
                    );

                    break;

                case Elements.Header.ProductVersion:

                    this.constructor.PROPERTY(
                            this.container.remove(Elements.Header.ProductCompanyTaxID),
                            element,
                            value
                    );

                    break;

                case Elements.Header.HeaderComment:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.FileInformation),
                            this.constructor.CREATE(element, value),
                            Entities.FileInformationRelationships.HAS_ADDITIONAL_COMMENT
                    );

                    break;

                case Elements.Header.Telephone:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            this.constructor.CREATE(element, value),
                            Entities.CompanyRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Header.Fax:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            this.constructor.CREATE(element, value),
                            Entities.CompanyRelationships.HAS_FAX
                    );

                    break;

                case Elements.Header.Email:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            this.constructor.CREATE(element, value),
                            Entities.CompanyRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Header.Website:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            this.constructor.CREATE(element, value),
                            Entities.CompanyRelationships.HAS_WEBSITE
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Header.CompanyAddress.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.Header.CompanyAddress.equals(element)) {

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Address, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Company),
                            identifier,
                            Entities.CompanyRelationships.HAS_ADDRESS
                    );

                } else {

                    this.processAddressChildren(element, value);

                }

            } else {

                throw new MapException(this.currentSequences.get(count));

            }

        }
    }

    private void processMasterFilesChildren(String element, String value, int count) throws MapException {

        count++;
        String identifier;

        switch (this.currentSequences.get(count)) {

            case Elements.MasterFiles.GeneralLedgerAccounts:

                if (Elements.MasterFiles.GeneralLedgerAccounts.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.GeneralLedgerAccounts, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.GeneralLedgerAccounts);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_GENERAL_LEDGER_ACCOUNTS
                    );

                } else {

                    this.processGeneralLedgerAccountsChildren(element, value, count);

                }

                break;

            case Elements.MasterFiles.Customer:

                if (Elements.MasterFiles.Customer.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Customer, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Customer);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_CUSTOMER
                    );

                } else {

                    this.processCustomerChildren(element, value, count);

                }


                break;

            case Elements.MasterFiles.Supplier:

                if (Elements.MasterFiles.Supplier.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Supplier, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Supplier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_SUPPLIER
                    );

                } else {

                    this.processSupplierChildren(element, value, count);

                }

                break;

            case Elements.MasterFiles.Product:

                if (Elements.MasterFiles.Product.equalsIgnoreCase(element)) {

                    identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Product, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Product);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_PRODUCT
                    );

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
                this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.GeneralLedgerAccounts), element, value);

            } else {

                throw new MapException(element);

            }

        } else {

            count++;

            if (Elements.GeneralLedgerAccounts.Account.equals(this.currentSequences.get(count))) {

                if (Elements.GeneralLedgerAccounts.Account.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Account, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Account);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.GeneralLedgerAccounts),
                            identifier,
                            Entities.GeneralLedgerAccountsRelationships.HAS_ACCOUNT
                    );

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
                String identifier = this.container.get(Entities.EntitiesValues.Account);
                this.constructor.PROPERTY(identifier, element, value);
                this.accounts.put(value, identifier);

                break;

            case Elements.Account.AccountDescription:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, value),
                        Entities.AccountRelationships.HAS_ACCOUNT_DESCRIPTION
                );

                break;

            case Elements.Account.OpeningDebitBalance:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.AccountRelationships.HAS_OPENING_DEBIT_BALANCE
                );

                break;

            case Elements.Account.OpeningCreditBalance:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.AccountRelationships.HAS_OPENING_CREDIT_BALANCE
                );

                break;

            case Elements.Account.ClosingDebitBalance:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.AccountRelationships.HAS_CLOSING_DEBIT_BALANCE
                );

                break;

            case Elements.Account.ClosingCreditBalance:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.AccountRelationships.HAS_CLOSING_CREDIT_BALANCE
                );

                break;

            case Elements.Account.GroupingCategory:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, value),
                        Entities.AccountRelationships.HAS_GROUPING_CATEGORY
                );

                break;

            case Elements.Account.GroupingCode:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, value),
                        Entities.AccountRelationships.HAS_GROUPING_CODE
                );

                break;

            case Elements.Account.TaxonomyCode:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Account),
                        this.constructor.CREATE(element, Integer.parseInt(value)),
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
                    identifier = this.container.get(Entities.EntitiesValues.Customer);
                    this.constructor.PROPERTY(identifier, element, value);
                    this.customers.put(value, identifier);

                    break;

                case Elements.Customer.AccountID:

                    if (this.accounts.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
                                this.accounts.get(value),
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
                                identifier,
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                        this.accounts.put(value, identifier);

                    }

                    break;

                case Elements.Customer.CustomerTaxID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_CUSTOMER_TAX_ID
                    );

                    break;

                case Elements.Customer.CompanyName:

                    if (this.companies.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
                                this.companies.get(value),
                                Entities.OtherRelationships.HAS_COMPANY
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
                                identifier,
                                Entities.OtherRelationships.HAS_COMPANY
                        );

                        this.companies.put(value, identifier);

                    }

                    break;

                case Elements.Customer.Contact:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_CONTACT
                    );

                    break;

                case Elements.Customer.Telephone:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Customer.Fax:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_FAX
                    );

                    break;

                case Elements.Customer.Email:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Customer.Website:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, value),
                            Entities.CustomerRelationships.HAS_WEBSITE
                    );

                    break;

                case Elements.Customer.SelfBillingIndicator:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Customer),
                            this.constructor.CREATE(element, Integer.parseInt(value)),
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(Entities.EntitiesValues.Address, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
                                identifier,
                                Entities.CustomerRelationships.HAS_BILLING_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                case Elements.Customer.ShipToAddress:

                    if (Elements.Customer.ShipToAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(Entities.EntitiesValues.Address, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Customer),
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

                    identifier = this.container.get(Entities.EntitiesValues.Supplier);
                    this.constructor.PROPERTY(identifier, element, value);
                    this.suppliers.put(value, identifier);

                    break;

                case Elements.Supplier.AccountID:

                    if (this.accounts.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
                                this.accounts.get(value),
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
                                identifier,
                                Entities.OtherRelationships.HAS_ACCOUNT
                        );

                        this.accounts.put(value, identifier);

                    }

                    break;

                case Elements.Supplier.SupplierTaxID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_SUPPLIER_TAX_ID
                    );

                    break;

                case Elements.Supplier.CompanyName:

                    if (this.companies.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
                                this.companies.get(value),
                                Entities.OtherRelationships.HAS_COMPANY
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
                                identifier,
                                Entities.OtherRelationships.HAS_COMPANY
                        );

                        this.companies.put(value, identifier);

                    }

                    break;

                case Elements.Supplier.Contact:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_CONTACT
                    );

                    break;

                case Elements.Supplier.Telephone:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_TELEPHONE
                    );

                    break;

                case Elements.Supplier.Fax:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_FAX
                    );

                    break;

                case Elements.Supplier.Email:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_EMAIL
                    );

                    break;

                case Elements.Supplier.Website:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, value),
                            Entities.SupplierRelationships.HAS_WEBSITE
                    );

                    break;

                case Elements.Supplier.SelfBillingIndicator:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Supplier),
                            this.constructor.CREATE(element, Integer.parseInt(value)),
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(Entities.EntitiesValues.Address, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
                                identifier,
                                Entities.SupplierRelationships.HAS_BILLING_ADDRESS
                        );

                    } else {

                        this.processAddressChildren(element, value);

                    }

                    break;

                case Elements.Supplier.ShipFromAddress:

                    if (Elements.Supplier.ShipFromAddress.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(Entities.EntitiesValues.Address, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Supplier),
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

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Product),
                            this.constructor.CREATE(element, value),
                            Entities.ProductRelationships.HAS_PRODUCT_TYPE
                    );

                    break;

                case Elements.Product.ProductCode:

                    String identifier = this.container.get(Entities.EntitiesValues.Product);
                    this.constructor.PROPERTY(identifier, element, value);
                    this.products.put(value, identifier);

                    break;

                case Elements.Product.ProductGroup:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Product),
                            this.constructor.CREATE(element, value),
                            Entities.ProductRelationships.HAS_PRODUCT_GROUP
                    );

                    break;

                case Elements.Product.ProductDescription:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Product),
                            this.constructor.CREATE(element, value),
                            Entities.ProductRelationships.HAS_PRODUCT_DESCRIPTION
                    );

                    break;

                case Elements.Product.ProductNumberCode:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Product),
                            this.constructor.CREATE(element, value),
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(element, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Product),
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
                this.constructor.PROPERTY(this.container.get(Elements.Product.CustomsDetails), element, value);

                break;

            default:
                throw new MapException(element);

        }

    }

    private void processTaxTableChildren(String element, String value, int count) throws MapException {

        count++;

        if (Elements.TaxTable.TaxTableEntry.equalsIgnoreCase(this.currentSequences.get(count))) {

            if (Elements.TaxTable.TaxTableEntry.equalsIgnoreCase(element)) {

                String identifier = this.constructor.CREATE();
                this.container.put(Entities.EntitiesValues.TaxTable, identifier);

                this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.TaxTable);

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.File),
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

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, value),
                        Entities.TaxTableRelationships.HAS_TAX_TYPE
                );

                break;

            case Elements.TaxTableEntry.TaxCountryRegion:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, value),
                        Entities.TaxTableRelationships.HAS_TAX_COUNTRY_REGION
                );

                break;

            case Elements.TaxTableEntry.TaxCode:
                this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.TaxTable), element, value);

                break;

            case Elements.TaxTableEntry.Description:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, value),
                        Entities.TaxTableRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.TaxTableEntry.TaxExpirationDate:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, value),
                        Entities.TaxTableRelationships.HAS_TAX_EXPIRATION_DATE
                );

                break;

            case Elements.TaxTableEntry.TaxPercentage:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.TaxTableRelationships.HAS_TAX_PERCENTAGE
                );

                break;

            case Elements.TaxTableEntry.TaxAmount:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.TaxTable),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
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
                    this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.GeneralLedgerEntries), element, Integer.parseInt(value));

                    break;

                case Elements.GeneralLedgerEntries.TotalDebit:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.GeneralLedgerEntries),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.GeneralLedgerEntriesRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.GeneralLedgerEntries.TotalCredit:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.GeneralLedgerEntries),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Journal, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Journal);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.GeneralLedgerEntries),
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
                    this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.Journal), element, value);

                    break;

                case Elements.Journal.Description:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Journal),
                            this.constructor.CREATE(element, value),
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Transaction, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Transaction);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Journal),
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

            switch (element) {

                case Elements.Transaction.TransactionID:
                    String identifier = this.container.get(Entities.EntitiesValues.Transaction);
                    this.constructor.PROPERTY(identifier, element, value);
                    this.transactions.put(value, identifier);

                    break;

                case Elements.Transaction.Period:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, Integer.parseInt(value)),
                            Entities.TransactionRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.Transaction.TransactionDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, value),
                            Entities.TransactionRelationships.HAS_TRANSACTION_DATE
                    );

                    break;

                case Elements.Transaction.SourceID:

                    if (this.sources.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Transaction),
                                this.sources.get(value),
                                Entities.OtherRelationships.HAS_SOURCE
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Transaction),
                                identifier,
                                Entities.OtherRelationships.HAS_SOURCE
                        );

                        this.sources.put(value, identifier);

                    }

                    break;

                case Elements.Transaction.Description:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, value),
                            Entities.TransactionRelationships.HAS_DESCRIPTION
                    );

                    break;

                case Elements.Transaction.DocArchivalNumber:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, value),
                            Entities.TransactionRelationships.HAS_DOC_ARCHIVAL_NUMBER
                    );

                    break;

                case Elements.Transaction.TransactionType:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, value),
                            Entities.TransactionRelationships.HAS_TRANSACTION_TYPE
                    );

                    break;

                case Elements.Transaction.GLPostingDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.constructor.CREATE(element, value),
                            Entities.TransactionRelationships.HAS_GL_POSTING_DATE
                    );

                    break;

                case Elements.Transaction.CustomerID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.customers.get(value),
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    break;

                case Elements.Transaction.SupplierID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
                            this.suppliers.get(value),
                            Entities.OtherRelationships.HAS_SUPPLIER
                    );

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.Transaction.Lines.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.Transaction.Lines.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE();
                    this.container.put(element, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Transaction),
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(element, identifier);

                    this.constructor.RELATIONSHIP(
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(element, identifier);

                    this.constructor.RELATIONSHIP(
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
                this.constructor.PROPERTY(this.container.get(Elements.Lines.CreditLine), element, value);

                break;

            case Elements.CreditLine.AccountID:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.CreditLine),
                        this.accounts.get(value),
                        Entities.OtherRelationships.HAS_ACCOUNT
                );

                break;

            case Elements.CreditLine.SourceDocumentID:

                if (this.documents.containsKey(value)) {

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Lines.CreditLine),
                            this.documents.get(value),
                            Entities.CreditLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                } else {

                    String identifier = this.constructor.CREATE(element, value);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Lines.CreditLine),
                            identifier,
                            Entities.CreditLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                    this.documents.put(value, identifier);

                }

                break;

            case Elements.CreditLine.SystemEntryDate:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.CreditLine),
                        this.constructor.CREATE(element, value),
                        Entities.CreditLineRelationships.HAS_SYSTEM_ENTRY_DATE
                );

                break;

            case Elements.CreditLine.Description:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.CreditLine),
                        this.constructor.CREATE(element, value),
                        Entities.CreditLineRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.CreditLine.CreditAmount:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.CreditLine),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
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
                this.constructor.PROPERTY(this.container.get(Elements.Lines.DebitLine), element, value);

                break;

            case Elements.DebitLine.AccountID:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.DebitLine),
                        this.accounts.get(value),
                        Entities.OtherRelationships.HAS_ACCOUNT
                );

                break;

            case Elements.DebitLine.SourceDocumentID:

                if (this.documents.containsKey(value)) {

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Lines.DebitLine),
                            this.documents.get(value),
                            Entities.DebitLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                } else {

                    String identifier = this.constructor.CREATE(element, value);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Lines.DebitLine),
                            identifier,
                            Entities.DebitLineRelationships.HAS_SOURCE_DOCUMENT
                    );

                    this.documents.put(value, identifier);

                }

                break;

            case Elements.DebitLine.SystemEntryDate:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.DebitLine),
                        this.constructor.CREATE(element, value),
                        Entities.DebitLineRelationships.HAS_SYSTEM_ENTRY_DATE
                );

                break;

            case Elements.DebitLine.Description:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.DebitLine),
                        this.constructor.CREATE(element, value),
                        Entities.DebitLineRelationships.HAS_DESCRIPTION
                );

                break;

            case Elements.DebitLine.DebitAmount:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Lines.DebitLine),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
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

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.SalesInvoices, identifier);

                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.SalesInvoices);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.File),
                            identifier,
                            Entities.FileRelationships.HAS_SALES_INVOICES
                    );

                } else {

                    this.processSalesInvoicesChildren(element, value, count);

                }

                break;

            case Elements.SourceDocuments.MovementOfGoods:

                if (Elements.SourceDocuments.MovementOfGoods.equalsIgnoreCase(element)) {

                    throw new MapException(element);

                } else {

                    throw new MapException(element);

                }

                //break;

            case Elements.SourceDocuments.WorkingDocuments:

                if (Elements.SourceDocuments.WorkingDocuments.equalsIgnoreCase(element)) {

                    throw new MapException(element);

                } else {

                    throw new MapException(element);

                }

                //break;

            case Elements.SourceDocuments.Payments:

                if (Elements.SourceDocuments.Payments.equalsIgnoreCase(element)) {

                    throw new MapException(element);

                } else {

                    throw new MapException(element);

                }

                //break;

            default:
                throw new MapException(this.currentSequences.get(count));
        }

    }

    private void processSalesInvoicesChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.SalesInvoices.NumberOfEntries:
                    this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.SalesInvoices), element, Integer.parseInt(value));

                    break;

                case Elements.SalesInvoices.TotalDebit:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.SalesInvoices),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.SalesInvoicesRelationships.HAS_TOTAL_DEBIT
                    );

                    break;

                case Elements.SalesInvoices.TotalCredit:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.SalesInvoices),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
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
                        this.constructor.PROPERTY(identifier, element, value);

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                    }

                    this.container.put(Entities.EntitiesValues.Invoice, identifier);
                    this.constructor.RELATIONSHIP_TYPE_OF(identifier, Entities.EntitiesValues.Invoice);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.SalesInvoices),
                            identifier,
                            Entities.SalesInvoicesRelationships.HAS_INVOICE
                    );

                    break;

                case Elements.Invoice.ATCUD:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_ATCUD
                    );

                    break;

                case Elements.Invoice.Hash:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_HASH
                    );

                    break;

                case Elements.Invoice.HashControl:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_HASH_CONTROL
                    );

                    break;

                case Elements.Invoice.Period:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, Integer.parseInt(value)),
                            Entities.InvoiceRelationships.HAS_PERIOD
                    );

                    break;

                case Elements.Invoice.InvoiceDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_INVOICE_DATE
                    );

                    break;

                case Elements.Invoice.InvoiceType:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_INVOICE_TYPE
                    );

                    break;

                case Elements.Invoice.SourceID:

                    if (this.sources.containsKey(value)) {

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                this.sources.get(value),
                                Entities.OtherRelationships.HAS_SOURCE
                        );

                    } else {

                        identifier = this.constructor.CREATE(element, value);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.OtherRelationships.HAS_SOURCE
                        );

                        this.sources.put(value, identifier);

                    }

                    break;

                case Elements.Invoice.EACCode:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_EAC_Code
                    );

                    break;

                case Elements.Invoice.SystemEntryDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_SYSTEM_ENTRY_DATE
                    );

                    break;

                case Elements.Invoice.TransactionID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.transactions.get(value),
                            Entities.OtherRelationships.HAS_TRANSACTION
                    );

                    break;

                case Elements.Invoice.CustomerID:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.customers.get(value),
                            Entities.OtherRelationships.HAS_CUSTOMER
                    );

                    break;

                case Elements.Invoice.MovementEndTime:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
                            Entities.InvoiceRelationships.HAS_MOVEMENT_END_TIME
                    );

                    break;

                case Elements.Invoice.MovementStartTime:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Entities.EntitiesValues.Invoice),
                            this.constructor.CREATE(element, value),
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_DOCUMENT_STATUS
                        );

                    } else {

                        this.processDocumentStatusChildren(element, value);

                    }

                    break;

                case Elements.Invoice.SpecialRegimes:

                    if (Elements.Invoice.SpecialRegimes.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SPECIAL_REGIMES
                        );

                    } else {

                        this.processSpecialRegimesChildren(element, value);

                    }

                    break;

                case Elements.Invoice.ShipTo:

                    if (Elements.Invoice.ShipTo.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SHIP_TO
                        );

                    } else {

                        this.processShipToChildren(element, value, count);

                    }

                    break;

                case Elements.Invoice.ShipFrom:

                    if (Elements.Invoice.ShipFrom.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_SHIP_FROM
                        );

                    } else {

                        this.processShipFromChildren(element, value, count);

                    }

                    break;

                case Elements.Invoice.Line:

                    if (Elements.Invoice.Line.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_LINE
                        );

                    } else {

                        this.processLineChildren(element, value, count);

                    }

                    break;

                case Elements.Invoice.DocumentTotals:

                    if (Elements.Invoice.DocumentTotals.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_DOCUMENT_TOTALS
                        );

                    } else {

                        this.processDocumentTotalsChildren(element, value, count);

                    }

                    break;

                case Elements.Invoice.WithholdingTax:

                    if (Elements.Invoice.WithholdingTax.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Entities.EntitiesValues.Invoice),
                                identifier,
                                Entities.InvoiceRelationships.HAS_WITHHOLDING_TAX
                        );

                    } else {

                        this.processWithholdingTaxChildren(element, value);

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

            case Elements.DocumentStatus.SourceBilling:

            case Elements.DocumentStatus.InvoiceStatusDate:

            case Elements.DocumentStatus.Reason:
                this.constructor.PROPERTY(this.container.get(Elements.Invoice.DocumentStatus), element, value);

                break;

            case Elements.DocumentStatus.SourceID:

                if (this.sources.containsKey(value)) {

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.DocumentStatus),
                            this.sources.get(value),
                            Entities.OtherRelationships.HAS_SOURCE
                    );

                } else {

                    String identifier = this.constructor.CREATE(element, value);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.DocumentStatus),
                            identifier,
                            Entities.OtherRelationships.HAS_SOURCE
                    );

                    this.sources.put(value, identifier);

                }

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
                this.constructor.PROPERTY(this.container.get(Elements.Invoice.SpecialRegimes), element, value);

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processShipToChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.ShipTo.DeliveryID:

                case Elements.ShipTo.DeliveryDate:

                case Elements.ShipTo.WarehouseID:

                case Elements.ShipTo.LocationID:
                    this.constructor.PROPERTY(this.container.get(Elements.Invoice.ShipTo), element, value);

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.ShipTo.Address.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.ShipTo.Address.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Address, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.ShipTo),
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

    private void processShipFromChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.ShipFrom.DeliveryID:

                case Elements.ShipFrom.DeliveryDate:

                case Elements.ShipFrom.WarehouseID:

                case Elements.ShipFrom.LocationID:
                    this.constructor.PROPERTY(this.container.get(Elements.Invoice.ShipFrom), element, value);

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            if (Elements.ShipFrom.Address.equalsIgnoreCase(this.currentSequences.get(count))) {

                if (Elements.ShipFrom.Address.equalsIgnoreCase(element)) {

                    String identifier = this.constructor.CREATE();
                    this.container.put(Entities.EntitiesValues.Address, identifier);

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.ShipFrom),
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

    private void processLineChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.Line.LineNumber:
                    this.constructor.PROPERTY(this.container.get(Elements.Invoice.Line), element, Integer.parseInt(value));

                    break;

                case Elements.Line.ProductCode:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.products.get(value),
                            Entities.OtherRelationships.HAS_PRODUCT
                    );

                    break;

                case Elements.Line.ProductDescription:
                    //Não é necessário processar

                    break;

                case Elements.Line.Quantity:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.LineRelationships.HAS_QUANTITY
                    );

                    break;

                case Elements.Line.UnitOfMeasure:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, value),
                            Entities.LineRelationships.HAS_UNIT_OF_MEASURE
                    );

                    break;

                case Elements.Line.UnitPrice:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.LineRelationships.HAS_UNIT_PRICE
                    );

                    break;

                case Elements.Line.TaxBase:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.LineRelationships.HAS_TAX_BASE
                    );

                    break;

                case Elements.Line.TaxPointDate:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, value),
                            Entities.LineRelationships.HAS_TAX_POINT_DATE
                    );

                    break;

                case Elements.Line.Description:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, value),
                            Entities.LineRelationships.HAS_DESCRIPTION
                    );

                    break;

                case Elements.Line.DebitAmount:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.LineRelationships.HAS_DEBIT_AMOUNT
                    );

                    break;

                case Elements.Line.CreditAmount:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
                            Entities.LineRelationships.HAS_CREDIT_AMOUNT
                    );

                    break;

                case Elements.Line.TaxExemptionReason:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, value),
                            Entities.LineRelationships.HAS_TAX_EXEMPTION_REASON
                    );

                    break;

                case Elements.Line.TaxExemptionCode:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, value),
                            Entities.LineRelationships.HAS_TAX_EXEMPTION_CODE
                    );

                    break;

                case Elements.Line.SettlementAmount:

                    this.constructor.RELATIONSHIP(
                            this.container.get(Elements.Invoice.Line),
                            this.constructor.CREATE(element, Double.parseDouble(value)),
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
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

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_TAX_TABLE
                        );

                    } else {

                        this.processTaxChildren(element, value);

                    }

                    break;

                case Elements.Line.CustomsInformation:

                    if (Elements.Line.CustomsInformation.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Elements.Invoice.Line),
                                identifier,
                                Entities.LineRelationships.HAS_CUSTOMS_INFORMATION
                        );

                    } else {

                        this.processCustomsInformationChildren(element, value);

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
                this.constructor.PROPERTY(this.container.get(Elements.Line.OrderReferences), element, value);

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processReferencesChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.References.Reference:

            case Elements.References.Reason:
                this.constructor.PROPERTY(this.container.get(Elements.Line.References), element, value);

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processProductSerialNumberChildren(String element, String value) throws MapException {

        if (Elements.ProductSerialNumber.SerialNumber.equalsIgnoreCase(element)) {
            this.constructor.PROPERTY(this.container.get(Elements.Line.ProductSerialNumber), element, value);

        } else {

            throw new MapException(element);

        }

    }

    private void processTaxChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.Tax.TaxType:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Line.Tax),
                        this.constructor.CREATE(element, value),
                        Entities.LineTaxRelationships.HAS_TAX_TYPE
                );

                break;

            case Elements.Tax.TaxCountryRegion:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Line.Tax),
                        this.constructor.CREATE(element, value),
                        Entities.LineTaxRelationships.HAS_TAX_COUNTRY_REGION
                );

                break;

            case Elements.Tax.TaxCode:
                this.constructor.PROPERTY(this.container.get(Elements.Line.Tax), element, value);

                break;

            case Elements.Tax.TaxPercentage:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Line.Tax),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
                        Entities.LineTaxRelationships.HAS_TAX_PERCENTAGE
                );

                break;

            case Elements.Tax.TaxAmount:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Line.Tax),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
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
                this.constructor.PROPERTY(this.container.get(Elements.Line.CustomsInformation), element, value);

                break;

            case Elements.CustomsInformation.IECAmount:
                this.constructor.PROPERTY(this.container.get(Elements.Line.CustomsInformation), element, Double.parseDouble(value));

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processDocumentTotalsChildren(String element, String value, int count) throws MapException {

        if (this.depth == count) {

            switch (element) {

                case Elements.DocumentTotals.TaxPayable:

                case Elements.DocumentTotals.NetTotal:

                case Elements.DocumentTotals.GrossTotal:
                    this.constructor.PROPERTY(this.container.get(Elements.Invoice.DocumentTotals), element, Double.parseDouble(value));

                    break;

                default:
                    throw new MapException(element);
            }

        } else {

            count++;

            switch (this.currentSequences.get(count)) {

                case Elements.DocumentTotals.Currency:

                    if (Elements.DocumentTotals.Currency.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Elements.Invoice.DocumentTotals),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_CURRENCY
                        );

                    } else {

                        this.processCurrencyChildren(element, value);

                    }

                    break;

                case Elements.DocumentTotals.Settlement:

                    if (Elements.DocumentTotals.Settlement.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Elements.Invoice.DocumentTotals),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_SETTLEMENT
                        );

                    } else {

                        this.processSettlementChildren(element, value);

                    }

                    break;

                case Elements.DocumentTotals.Payment:

                    if (Elements.DocumentTotals.Payment.equalsIgnoreCase(element)) {

                        String identifier = this.constructor.CREATE();
                        this.container.put(element, identifier);

                        this.constructor.RELATIONSHIP(
                                this.container.get(Elements.Invoice.DocumentTotals),
                                identifier,
                                Entities.DocumentTotalsRelationships.HAS_PAYMENT
                        );

                    } else {

                        this.processPaymentChildren(element, value);

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
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Currency), element, value);

                break;

            case Elements.Currency.CurrencyAmount:

            case Elements.Currency.ExchangeRate:
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Currency), element, Double.parseDouble(value));

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
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Settlement), element, value);

                break;

            case Elements.Settlement.SettlementAmount:
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Settlement), element, Double.parseDouble(value));

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processPaymentChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.Payment.PaymentMechanism:

            case Elements.Payment.PaymentDate:
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Payment), element, value);

                break;

            case Elements.Payment.PaymentAmount:
                this.constructor.PROPERTY(this.container.get(Elements.DocumentTotals.Payment), element, Double.parseDouble(value));

                break;

            default:
                throw new MapException(element);
        }

    }

    private void processWithholdingTaxChildren(String element, String value) throws MapException {

        switch (element) {

            case Elements.WithholdingTax.WithholdingTaxType:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Invoice.WithholdingTax),
                        this.constructor.CREATE(element, value),
                        Entities.WithholdingTaxRelationships.HAS_WITHHOLDING_TAX_TYPE
                );

                break;

            case Elements.WithholdingTax.WithholdingTaxDescription:
                this.constructor.PROPERTY(this.container.get(Elements.Invoice.WithholdingTax), element, value);

                break;

            case Elements.WithholdingTax.WithholdingTaxAmount:

                this.constructor.RELATIONSHIP(
                        this.container.get(Elements.Invoice.WithholdingTax),
                        this.constructor.CREATE(element, Double.parseDouble(value)),
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

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_BUILDING_NUMBER
                );

                break;

            case Elements.CompanyAddress.StreetName:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_STREET_NAME
                );

                break;

            case Elements.CompanyAddress.AddressDetail:
                this.constructor.PROPERTY(this.container.get(Entities.EntitiesValues.Address), element, value);

                break;

            case Elements.CompanyAddress.City:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_CITY
                );

                break;

            case Elements.CompanyAddress.PostalCode:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_POSTAL_CODE
                );

                break;

            case Elements.CompanyAddress.Region:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_REGION
                );

                break;

            case Elements.CompanyAddress.Country:

                this.constructor.RELATIONSHIP(
                        this.container.get(Entities.EntitiesValues.Address),
                        this.constructor.CREATE(element, value),
                        Entities.AddressRelationships.HAS_COUNTRY
                );

                break;

            default:
                throw new MapException(element);
        }

    }
}
