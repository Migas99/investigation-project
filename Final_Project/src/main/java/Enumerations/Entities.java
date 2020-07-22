package Enumerations;

import java.util.LinkedList;

public class Entities {

    public enum EntitiesList {
        File,
        Company,
        GeneralLedgerAccounts,
        Account,
        Customer,
        Supplier,
        Product,
        TaxTable,
        GeneralLedgerEntries,
        Journal,
        Transaction,
        SalesInvoices,
        Invoice,

        /*MovementOfGoods,
        WorkingDocuments,
        Payments,*/

        Address,
        Contacts;

        public static LinkedList<String> getList() {
            Entities.EntitiesList[] array = values();
            LinkedList<String> list = new LinkedList<>();

            for (EntitiesList entitiesList : array) {
                list.add(entitiesList.name());
            }

            return list;
        }
    }

    public enum EntitiesValues {
        ELEMENT;

        public final static String File = "File";
        public final static String FileInformation = "FileInformation";
        public final static String Company = "Company";
        public final static String FiscalYear = "FiscalYear";
        public final static String GeneralLedgerAccounts = "GeneralLedgerAccounts";
        public final static String Account = "Account";
        public final static String Customer = "Customer";
        public final static String Supplier = "Supplier";
        public final static String Product = "Product";
        public final static String TaxTable = "TaxTable";
        public final static String GeneralLedgerEntries = "GeneralLedgerEntries";
        public final static String Journal = "Journal";
        public final static String Transaction = "Transaction";
        public final static String SalesInvoices = "SalesInvoices";
        public static final String Invoice = "Invoice";


        public final static String MovementOfGoods = "MovementOfGoods";

        public static final String WorkingDocuments = "WorkingDocuments";

        public static final String Payments = "Payments";

        public final static String Address = "Address";
        public final static String Contacts = "Contacts";
    }

    public enum CompanyRelationships {
        ELEMENT;

        public final static String HAS_COMPANY_ID = "HAS_COMPANY_ID";
        public final static String HAS_TAX_REGISTRATION_NUMBER = "HAS_TAX_REGISTRATION_NUMBER";
        public final static String HAS_BUSINESS_NAME = "HAS_BUSINESS_NAME";

        public final static String HAS_COMPANY_ADDRESS = "HAS_COMPANY_ADDRESS";

        public final static String HAS_TELEPHONE = "HAS_TELEPHONE";
        public final static String HAS_FAX = "HAS_FAX";
        public final static String HAS_EMAIL = "HAS_EMAIL";
        public final static String HAS_WEBSITE = "HAS_WEBSITE";

        public final static String RELATED_TO_TRANSACTION = "RELATED_TO_TRANSACTION";

        public final static String SOLD_INVOICE = "SOLD_INVOICE";
        public final static String BOUGHT_INVOICE = "BOUGHT_INVOICE";
    }

    public enum FileRelationships {
        ELEMENT;

        public final static String RELATED_TO = "RELATED_TO";
        public final static String HAS_ADDITIONAL_INFORMATION = "HAS_ADDITIONAL_INFORMATION";
        public final static String HAS_GENERAL_LEDGER_ACCOUNTS = "HAS_GENERAL_LEDGER_ACCOUNTS";
        public final static String HAS_CUSTOMER = "HAS_CUSTOMER";
        public final static String HAS_SUPPLIER = "HAS_SUPPLIER";
        public final static String HAS_PRODUCT = "HAS_PRODUCT";
        public final static String HAS_TAX_TABLE = "HAS_TAX_TABLE";
        public final static String HAS_GENERAL_LEDGER_ENTRIES = "HAS_GENERAL_LEDGER_ENTRIES";
        public final static String HAS_SALES_INVOICES = "HAS_SALES_INVOICES";
    }

    public enum FileInformationRelationships {
        ELEMENT;

        public final static String HAS_TAX_ACCOUNTING_BASIS = "HAS_TAX_ACCOUNTING_BASIS";
        public final static String HAS_FISCAL_YEAR = "HAS_FISCAL_YEAR";
        public final static String HAS_CURRENCY_CODE = "HAS_CURRENCY_CODE";
        public final static String HAS_DATE_CREATED = "HAS_DATE_CREATED";
        public final static String HAS_TAX_ENTITY = "HAS_TAX_ENTITY";
        public final static String PRODUCED_BY = "PRODUCED_BY";
        public final static String HAS_ADDITIONAL_COMMENT = "HAS_ADDITIONAL_COMMENT";
    }

    public enum FiscalYearRelationships {
        ELEMENT;

        public final static String HAS_START_DATE = "HAS_START_DATE";
        public final static String HAS_END_DATE = "HAS_END_DATE";
    }

    public enum GeneralLedgerAccountsRelationships {
        ELEMENT;

        public final static String HAS_ACCOUNT = "HAS_ACCOUNT";
    }

    public enum AccountRelationships {
        ELEMENT;

        public final static String HAS_ACCOUNT_DESCRIPTION = "HAS_ACCOUNT_DESCRIPTION";
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

        public final static String REPRESENTS = "REPRESENTS";

        public final static String HAS_CUSTOMER_TAX_ID = "HAS_CUSTOMER_TAX_ID";
        public final static String HAS_CONTACT = "HAS_CONTACT";
        public final static String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
        public final static String HAS_SHIP_TO_ADDRESS = "HAS_SHIP_TO_ADDRESS";
        public final static String HAS_TELEPHONE = "HAS_TELEPHONE";
        public final static String HAS_FAX = "HAX_FAX";
        public final static String HAS_EMAIL = "HAS_EMAIL";
        public final static String HAS_WEBSITE = "HAS_WEBSITE";
        public final static String HAS_SELF_BILLING_INDICATOR = "HAS_SELF_BILLING_INDICATOR";
    }

    public enum SupplierRelationships {
        ELEMENT;

        public final static String REPRESENTS = "REPRESENTS";

        public final static String HAS_SUPPLIER_TAX_ID = "HAS_SUPPLIER_TAX_ID";
        public final static String HAS_CONTACT = "HAS_CONTACT";
        public final static String HAS_BILLING_ADDRESS = "HAS_BILLING_ADDRESS";
        public final static String HAS_SHIP_FROM_ADDRESS = "HAS_SHIP_FROM_ADDRESS";
        public final static String HAS_TELEPHONE = "HAS_TELEPHONE";
        public final static String HAS_FAX = "HAX_FAX";
        public final static String HAS_EMAIL = "HAS_EMAIL";
        public final static String HAS_WEBSITE = "HAS_WEBSITE";
        public final static String HAS_SELF_BILLING_INDICATOR = "HAS_SELF_BILLING_INDICATOR";
    }

    public enum ProductRelationships {
        ELEMENT;

        public final static String HAS_PRODUCT_TYPE = "HAS_PRODUCT_TYPE";
        public final static String HAS_PRODUCT_GROUP = "HAS_PRODUCT_GROUP";
        public final static String HAS_PRODUCT_DESCRIPTION = "HAS_PRODUCT_DESCRIPTION";
        public final static String HAS_PRODUCT_NUMBER_CODE = "HAS_PRODUCT_NUMBER_CODE";
        public final static String HAS_CUSTOMS_DETAILS = "HAS_CUSTOMS_DETAILS";
    }

    public enum TaxTableRelationships {
        ELEMENT;

        public final static String HAS_TAX_TYPE = "HAS_TAX_TYPE";
        public final static String HAS_TAX_COUNTRY_REGION = "HAS_TAX_COUNTRY_REGION";
        public final static String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public final static String HAS_TAX_EXPIRATION_DATE = "HAS_TAX_EXPIRATION_DATE";
        public final static String HAS_TAX_PERCENTAGE = "HAS_TAX_PERCENTAGE";
        public final static String HAS_TAX_AMOUNT = "HAS_TAX_AMOUNT";
    }

    public enum GeneralLedgerEntriesRelationships {
        ELEMENT;

        public final static String HAS_TOTAL_DEBIT = "HAS_TOTAL_DEBIT";
        public final static String HAS_TOTAL_CREDIT = "HAS_TOTAL_CREDIT";
        public final static String HAS_JOURNAL = "HAS_JOURNAL";
    }

    public enum JournalRelationships {
        ELEMENT;

        public final static String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public final static String HAS_TRANSACTION = "HAS_TRANSACTION";
    }

    public enum TransactionRelationships {
        ELEMENT;

        public final static String HAS_PERIOD = "HAS_PERIOD";
        public final static String HAS_TRANSACTION_DATE = "HAS_TRANSACTION_DATE";
        public final static String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public final static String HAS_DOC_ARCHIVAL_NUMBER = "HAS_DOC_ARCHIVAL_NUMBER";
        public final static String HAS_TRANSACTION_TYPE = "HAS_TRANSACTION_TYPE";
        public final static String HAS_GL_POSTING_DATE = "HAS_GL_POSTING_DATE";
        public final static String HAS_LINES = "HAS_LINES";
    }

    public enum LinesRelationships {
        ELEMENT;

        public final static String HAS_CREDIT_LINE = "HAS_CREDIT_LINE";
        public final static String HAS_DEBIT_LINE = "HAS_DEBIT_LINE";
    }

    public enum CreditLineRelationships {
        ELEMENT;

        public final static String HAS_SYSTEM_ENTRY_DATE = "HAS_SYSTEM_ENTRY_DATE";
        public final static String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public final static String HAS_CREDIT_AMOUNT = "HAS_CREDIT_AMOUNT";
        public final static String HAS_SOURCE_DOCUMENT = "HAS_SOURCE_DOCUMENT";
    }

    public enum DebitLineRelationships {
        ELEMENT;

        public final static String HAS_SYSTEM_ENTRY_DATE = "HAS_SYSTEM_ENTRY_DATE";
        public final static String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public final static String HAS_DEBIT_AMOUNT = "HAS_DEBIT_AMOUNT";
        public final static String HAS_SOURCE_DOCUMENT = "HAS_SOURCE_DOCUMENT";
    }

    public enum SalesInvoicesRelationships {
        ELEMENT;

        public static final String HAS_TOTAL_DEBIT = "HAS_TOTAL_DEBIT";
        public static final String HAS_TOTAL_CREDIT = "HAS_TOTAL_CREDIT";
        public static final String HAS_INVOICE = "HAS_INVOICE";
    }

    public enum InvoiceRelationships {
        ELEMENT;

        public static final String HAS_ATCUD = "HAS_ATCUD";
        public static final String HAS_HASH = "HAS_HASH";
        public static final String HAS_HASH_CONTROL = "HAS_HASH_CONTROL";
        public static final String HAS_PERIOD = "HAS_PERIOD";
        public static final String HAS_INVOICE_DATE = "HAS_INVOICE_DATE";
        public static final String HAS_INVOICE_TYPE = "HAS_INVOICE_TYPE";
        public static final String HAS_EAC_Code = "HAS_EAC_Code";
        public static final String HAS_SYSTEM_ENTRY_DATE = "HAS_SYSTEM_ENTRY_DATE";
        public static final String HAS_MOVEMENT_END_TIME = "HAS_MOVEMENT_END_TIME";
        public static final String HAS_MOVEMENT_START_TIME = "HAS_MOVEMENT_START_TIME";

        public static final String HAS_DOCUMENT_STATUS = "HAS_DOCUMENT_STATUS";
        public static final String HAS_SPECIAL_REGIMES = "HAS_SPECIAL_REGIMES";
        public static final String HAS_SHIP_TO = "HAS_SHIP_TO";
        public static final String HAS_SHIP_FROM = "HAS_SHIP_FROM";
        public static final String HAS_LINE = "HAS_LINE";
        public static final String HAS_DOCUMENT_TOTALS = "HAS_DOCUMENT_TOTALS";
        public static final String HAS_WITHHOLDING_TAX = "HAS_WITHHOLDING_TAX";
    }

    public enum DocumentStatusRelationships {
        ELEMENT;
    }

    public enum SpecialRegimesRelationships {
        ELEMENT;
    }

    public enum ShipToRelationships {
        ELEMENT;

        public static final String HAS_ADDRESS = "HAS_ADDRESS";
    }

    public enum ShipFromRelationships {
        ELEMENT;

        public static final String HAS_ADDRESS = "HAS_ADDRESS";
    }

    public enum LineRelationships {
        ELEMENT;

        public static final String HAS_QUANTITY = "HAS_QUANTITY";
        public static final String HAS_UNIT_OF_MEASURE = "HAS_UNIT_OF_MEASURE";
        public static final String HAS_UNIT_PRICE = "HAS_UNIT_PRICE";
        public static final String HAS_TAX_BASE = "HAS_TAX_BASE";
        public static final String HAS_TAX_POINT_DATE = "HAS_TAX_POINT_DATE";
        public static final String HAS_DESCRIPTION = "HAS_DESCRIPTION";
        public static final String HAS_DEBIT_AMOUNT = "HAS_DEBIT_AMOUNT";
        public static final String HAS_CREDIT_AMOUNT = "HAS_CREDIT_AMOUNT";
        public static final String HAS_TAX_EXEMPTION_REASON = "HAS_TAX_EXEMPTION_REASON";
        public static final String HAS_TAX_EXEMPTION_CODE = "HAS_TAX_EXEMPTION_CODE";
        public static final String HAS_SETTLEMENT_AMOUNT = "HAS_SETTLEMENT_AMOUNT";

        public static final String HAS_ORDER_REFERENCES = "HAS_ORDER_REFERENCES";
        public static final String HAS_REFERENCES = "HAS_REFERENCES";
        public static final String HAS_PRODUCT_SERIAL_NUMBER = "HAS_PRODUCT_SERIAL_NUMBER";
        public static final String HAS_TAX_TABLE = "HAS_TAX_TABLE";
        public static final String HAS_CUSTOMS_INFORMATION = "HAS_CUSTOMS_INFORMATION";
    }

    public enum LineTaxRelationships {
        ELEMENT;

        public static final String HAS_TAX_TYPE = "HAS_TAX_TYPE";
        public static final String HAS_TAX_COUNTRY_REGION = "HAS_TAX_COUNTRY_REGION";
        public static final String HAS_TAX_AMOUNT = "HAS_TAX_AMOUNT";
        public static final String HAS_TAX_PERCENTAGE = "HAS_TAX_PERCENTAGE";
    }

    public enum DocumentTotalsRelationships {
        ELEMENT;

        public static final String HAS_TAX_PAYABLE = "HAS_TAX_PAYABLE";
        public static final String HAS_NET_TOTAL = "HAS_NET_TOTAL";
        public static final String HAS_GROSS_TOTAL = "HAS_GROSS_TOTAL";
        public static final String HAS_CURRENCY = "HAS_CURRENCY";
        public static final String HAS_SETTLEMENT = "HAS_SETTLEMENT";
        public static final String HAS_PAYMENT = "HAS_PAYMENT";
    }

    public enum WithholdingTaxRelationships {
        ELEMENT;

        public static final String HAS_WITHHOLDING_TAX_TYPE = "HAS_WITHHOLDING_TAX_TYPE";
        public static final String HAS_WITHHOLDING_TAX_AMOUNT = "HAS_WITHHOLDING_TAX_AMOUNT";
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

    public enum OtherRelationships {
        ELEMENT;

        public final static String TYPE_OF = "TYPE_OF";
        public final static String HAS_COMPANY = "HAS_COMPANY";
        public final static String HAS_ACCOUNT = "HAS_ACCOUNT";
        public final static String HAS_CUSTOMER = "HAS_CUSTOMER";
        public final static String HAS_SUPPLIER = "HAS_SUPPLIER";
        public final static String HAS_PRODUCT = "HAS_PRODUCT";
        public static final String HAS_TAX_TABLE = "HAS_TAX_TABLE";
        public static final String HAS_TRANSACTION = "HAS_TRANSACTION";
        public static final String HAS_INVOICE = "HAS_INVOICE";
        public static final String HAS_SOURCE = "HAS_SOURCE";
    }
}
