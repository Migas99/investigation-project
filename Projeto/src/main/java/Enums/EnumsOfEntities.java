package Enums;

public class EnumsOfEntities {

    public enum EntitiesValues {
        FileInformation,
        Company,
        FiscalYear,
        Product,
        GeneralLedgerAccounts,
        Account,
        Customer,
        Supplier,
        TaxTable,
        GeneralLedgerEntries,
        Journal,
        Transaction,
        SalesInvoices,
        Invoices,
        Address,
        Contacts
    }

    public enum Entities {
        ELEMENT;

        public final static String FileInformation = "FileInformation";
        public final static String Company = "Company";
        public final static String FiscalYear = "FiscalYear";
        public final static String Product = "Product";
        public final static String GeneralLedgerAccounts = "GeneralLedgerAccounts";
        public final static String Account = "Account";
        public final static String Customer = "Customer";
        public final static String Supplier = "Supplier";
        public final static String TaxTable = "TaxTable";
        public final static String GeneralLedgerEntries = "GeneralLedgerEntries";

        public final static String Address = "Address";
        public final static String Contacts = "Contacts";
    }

    public enum FileInformationRelationships {
        ELEMENT;

        public final static String HAS_COMPANY = "HAS_COMPANY";
        public final static String HAS_TAX_ACCOUNTING_BASIS = "HAS_TAX_ACCOUNTING_BASIS";
        public final static String HAS_FISCAL_YEAR = "HAS_FISCAL_YEAR";
        public final static String HAS_CURRENCY_CODE = "HAS_CURRENCY_CODE";
        public final static String HAS_DATE_CREATED = "HAS_DATE_CREATED";
        public final static String HAS_TAX_ENTITY = "HAS_TAX_ENTITY";
        public final static String HAS_PRODUCT = "HAS_PRODUCT";
    }

    public enum CompanyRelationships {
        ELEMENT;

        public final static String HAS_COMPANY_ID = "HAS_COMPANY_ID";
        public final static String HAS_TAX_REGISTRATION_NUMBER = "HAS_TAX_REGISTRATION_NUMBER";
        public final static String HAS_BUSINESS_NAME = "HAS_BUSINESS_NAME";
        public final static String HAS_ADDRESS = "HAS_ADDRESS";
        public final static String HAS_CONTACTS = "HAS_CONTACTS";
    }

    public enum FiscalYearRelationships {
        ELEMENT;

        public final static String HAS_START_DATE = "HAS_START_DATE";
        public final static String HAS_END_DATE = "HAS_END_DATE";
    }

    public enum ProductRelationships {
        ELEMENT;

        public final static String HAS_COMPANY = "HAS_COMPANY";
        public final static String HAS_PRODUCT_TYPE = "HAS_PRODUCT_TYPE";
        public final static String HAS_PRODUCT_GROUP = "HAS_PRODUCT_GROUP";
        public final static String HAS_PRODUCT_NUMBER_CODE = "HAS_PRODUCT_NUMBER_CODE";
    }

    public enum GeneralLedgerAccountsRelationships {
        ELEMENT;

        public final static String HAS_ACCOUNT = "HAS_ACCOUNT";
    }

    public enum AccountRelationships {
        ELEMENT;

        public final static String HAS_OPENING_DEBIT_BALANCE = "HAS_OPENING_DEBIT_BALANCE";
        public final static String HAS_OPENING_CREDIT_BALANCE = "HAS_OPENING_CREDIT_BALANCE";
        public final static String HAS_CLOSING_DEBIT_BALANCE = "HAS_CLOSING_DEBIT_BALANCE";
        public final static String HAS_CLOSING_CREDIT_BALANCE = "HAS_CLOSING_CREDIT_BALANCE";
        public final static String HAS_GROUPING_CATEGORY = "HAS_GROUPING_CATEGORY";
        public final static String HAS_GROUPING_CODE = "HAS_GROUPING_CODE";
        public final static String HAS_TAXONOMY_CODE = "HAS_TAXONOMY_CODE";
    }

    public enum CustomerRelationships {
        ELEMENT;

        public final static String HAS_CUSTOMER_TAX_ID = "HAS_CUSTOMER_TAX_ID";
        public final static String HAS_CONTACTS = "HAS_CONTACTS";
        public final static String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
        public final static String HAS_SHIP_TO_ADDRESS = "HAS_SHIP_TO_ADDRESS";
    }

    public enum SupplierRelationships {
        ELEMENT;

        public final static String HAS_SUPPLIER_TAX_ID = "HAS_SUPPLIER_TAX_ID";
        public final static String HAS_CONTACTS = "HAS_CONTACTS";
        public final static String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
        public final static String HAS_SHIP_FROM_ADDRESS = "HAS_SHIP_FROM_ADDRESS";
    }

    public enum ContactsRelationships {
        ELEMENT;

        public final static String HAS_TELEPHONE = "HAS_TELEPHONE";
        public final static String HAS_FAX = "HAX_FAX";
        public final static String HAS_EMAIL = "HAS_EMAIL";
        public final static String HAS_WEBSITE = "HAS_WEBSITE";
    }

    public enum TaxTableRelationships {
        ELEMENT;

        public final static String HAS_TAX_TYPE = "HAS_TAX_TYPE";
        public final static String HAS_TAX_COUNTRY_REGION = "HAS_TAX_COUNTRY_REGION";
        public final static String HAS_TAX_EXPIRATION_DATE = "HAS_TAX_EXPIRATION_DATE";
        public final static String HAS_TAX_PERCENTAGE = "HAS_TAX_PERCENTAGE";
        public final static String HAS_TAX_AMOUNT = "HAS_TAX_AMOUNT";
    }

    public enum AddressRelationships {
        ELEMENT;

        public final static String HAS_BUILDING_NUMBER = "HAS_BUILDING_NUMBER";
        public final static String HAS_STREET_NAME = "HAS_STREET_NAME";
        public final static String HAS_CITY = "HAS_CITY";
        public final static String HAS_POSTAL_CODE = "HAS_POSTAL_CODE";
        public final static String HAS_REGION = "HAS_REGION";
        public final static String HAS_COUNTRY = "HAS_COUNTRY";
    }

}
