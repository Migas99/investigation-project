package Enums;

public class EnumsOfElements {

    public enum RootElement {
        ELEMENT;

        public static final String AuditFile = "AuditFile";
    }

    public enum AuditFile {
        ELEMENT;

        public static final String Header = "Header";
        public static final String MasterFiles = "MasterFiles";
        public static final String GeneralLedgerEntries = "GeneralLedgerEntries";
        public static final String SourceDocuments = "SourceDocuments";
    }

    public enum Header {
        ELEMENT;

        public static final String AuditFileVersion = "AuditFileVersion";
        public static final String CompanyID = "CompanyID";
        public static final String TaxRegistrationNumber = "TaxRegistrationNumber";
        public static final String TaxAccountingBasis = "TaxAccountingBasis";
        public static final String CompanyName = "CompanyName";
        public static final String BussinessName = "BussinessName";
        public static final String CompanyAddress = "CompanyAddress";
        public static final String FiscalYear = "FiscalYear";
        public static final String StartDate = "StartDate";
        public static final String EndDate = "EndDate";
        public static final String CurrencyCode = "CurrencyCode";
        public static final String DateCreated = "DateCreated";
        public static final String TaxEntity = "TaxEntity";
        public static final String ProductCompanyTaxID = "ProductCompanyTaxID";
        public static final String SoftwareCertificateNumber = "SoftwareCertificateNumber";
        public static final String ProductID = "ProductID";
        public static final String ProductVersion = "ProductVersion";
        public static final String HeaderComment = "HeaderComment";
        public static final String Telephone = "Telephone";
        public static final String Fax = "Fax";
        public static final String Email = "Email";
        public static final String Website = "Website";
    }

    public enum CompanyAddress {
        ELEMENT;

        public static final String BuildingNumber = "BuildingNumber";
        public static final String StreetName = "StreetName";
        public static final String AddressDetail = "AddressDetail";
        public static final String City = "City";
        public static final String PostalCode = "PostalCode";
        public static final String Region = "Region";
        public static final String Country = "Country";
    }

    public enum MasterFiles {
        ELEMENT;

        public static final String GeneralLedgerAccounts = "GeneralLedgerAccounts";
        public static final String Customer = "Customer";
        public static final String Supplier = "Supplier";
        public static final String Product = "Product";
        public static final String TaxTable = "TaxTable";
    }

    public enum GeneralLedgerAccounts {
        ELEMENT;

        public static final String TaxonomyReference = "TaxonomyReference";
        public static final String Account = "Account";
    }

    public enum Account {
        ELEMENT;

        public static final String AccountID = "AccountID";
        public static final String AccountDescription = "AccountDescription";
        public static final String OpeningDebitBalance = "OpeningDebitBalance";
        public static final String OpeningCreditBalance = "OpeningCreditBalance";
        public static final String ClosingDebitBalance = "ClosingDebitBalance";
        public static final String ClosingCreditBalance = "ClosingCreditBalance";
        public static final String GroupingCategory = "GroupingCategory";
        public static final String GroupingCode = "GroupingCode";
        public static final String TaxonomyCode = "TaxonomyCode";
    }

    public enum Customer {
        ELEMENT;

        public static final String CustomerID = "CustomerID";
        public static final String AccountID = "AccountID";
        public static final String CustomerTaxID = "CustomerTaxID";
        public static final String CompanyName = "CompanyName";
        public static final String Contact = "Contact";
        public static final String BillingAddress = "BillingAddress";
        public static final String ShipToAddress = "ShipToAddress";
        public static final String Telephone = "Telephone";
        public static final String Fax = "Fax";
        public static final String Email = "Email";
        public static final String Website = "Website";
        public static final String SelfBillingIndicator = "SelfBillingIndicator";
    }

    public enum BillingAddress {
        ELEMENT;

        public static final String BuildingNumber = "BuildingNumber";
        public static final String StreetName = "StreetName";
        public static final String AddressDetail = "AddressDetail";
        public static final String City = "City";
        public static final String PostalCode = "PostalCode";
        public static final String Region = "Region";
        public static final String Country = "Country";
    }

    public enum ShipToAddress {
        ELEMENT;

        public static final String BuildingNumber = "BuildingNumber";
        public static final String StreetName = "StreetName";
        public static final String AddressDetail = "AddressDetail";
        public static final String City = "City";
        public static final String PostalCode = "PostalCode";
        public static final String Region = "Region";
        public static final String Country = "Country";
    }

    public enum Supplier {
        ELEMENT;

        public static final String SupplierID = "SupplierID";
        public static final String AccountID = "AccountID";
        public static final String SupplierTaxID = "SupplierTaxID";
        public static final String CompanyName = "CompanyName";
        public static final String Contact = "Contact";
        public static final String BillingAddress = "BillingAddress";
        public static final String ShipFromAddress = "ShipFromAddress";
        public static final String Telephone = "Telephone";
        public static final String Fax = "Fax";
        public static final String Email = "Email";
        public static final String Website = "Website";
        public static final String SelfBillingIndicator = "SelfBillingIndicator";
    }

    public enum ShipFromAddress {
        ELEMENT;

        public static final String BuildingNumber = "BuildingNumber";
        public static final String StreetName = "StreetName";
        public static final String AddressDetail = "AddressDetail";
        public static final String City = "City";
        public static final String PostalCode = "PostalCode";
        public static final String Region = "Region";
        public static final String Country = "Country";
    }

    public enum Product {
        ELEMENT;

        public static final String ProductType = "ProductType";
        public static final String ProductCode = "ProductCode";
        public static final String ProductGroup = "ProductGroup";
        public static final String ProductDescription = "ProductDescription";
        public static final String ProductNumberCode = "ProductNumberCode";
        public static final String CustomsDetails = "CustomsDetails";
    }

    public enum CustomsDetails {
        ELEMENT;

        public static final String CNCode = "CNCode";
        public static final String UNNumber = "UNNumber";
    }

    public enum TaxTable {
        ELEMENT;

        public static final String TaxTableEntry = "TaxTableEntry";
    }

    public enum TaxTableEntry {
        ELEMENT;

        public static final String TaxType = "TaxType";
        public static final String TaxCountryRegion = "TaxCountryRegion";
        public static final String TaxCode = "TaxCode";
        public static final String Description = "Description";
        public static final String TaxExpirationDate = "TaxExpirationDate";
        public static final String TaxPercentage = "TaxPercentage";
        public static final String TaxAmount = "TaxAmount";
    }

    public enum GeneralLedgerEntries {
        ELEMENT;

        public static final String NumberOfEntries = "NumberOfEntries";
        public static final String TotalCredit = "TotalCredit";
        public static final String TotalDebit = "TotalDebit";
        public static final String Journal = "Journal";
    }

    public enum Journal {
        ELEMENT;

        public static final String JournalID = "JournalID";
        public static final String Description = "Description";
        public static final String Transaction = "Transaction";
    }

    public enum Transaction {
        ELEMENT;

        public static final String TransactionID = "TransactionID";
        public static final String Period = "Period";
        public static final String TransactionDate = "TransactionDate";
        public static final String SourceID = "SourceID";
        public static final String Description = "Description";
        public static final String DocArchivalNumber = "DocArchivalNumber";
        public static final String TransactionType = "TransactionType";
        public static final String GLPostingDate = "GLPostingDate";
        public static final String CustomerID = "CustomerID";
        public static final String SupplierID = "SupplierID";
        public static final String Lines = "Lines";
    }

    public enum Lines {
        ELEMENT;

        public static final String CreditLine = "CreditLine";
        public static final String DebitLine = "DebitLine";
    }

    public enum CreditLine {
        ELEMENT;

        public static final String RecordID = "RecordID";
        public static final String AccountID = "AccountID";
        public static final String SystemEntryDate = "SystemEntryDate";
        public static final String Description = "Description";
        public static final String CreditAmount = "CreditAmount";
    }

    public enum DebitLine {
        ELEMENT;

        public static final String RecordID = "RecordID";
        public static final String AccountID = "AccountID";
        public static final String SystemEntryDate = "SystemEntryDate";
        public static final String Description = "Description";
        public static final String DebitAmount = "DebitAmount";
    }

    public enum SourceDocuments {
        ELEMENT;

        public static final String SalesInvoices = "SalesInvoices";
        public static final String MovementOfGoods = "MovementOfGoods";
        public static final String WorkingDocuments = "WorkingDocuments";
        public static final String Payments = "Payments";
    }

    public enum SalesInvoices {
        ELEMENT;

        public static final String NumberOfEntries = "NumberOfEntries";
        public static final String TotalDebit = "TotalDebit";
        public static final String TotalCredit = "TotalCredit";
        public static final String Invoice = "Invoice";
    }

    public enum Invoice {
        ELEMENT;

        public static final String InvoiceNo = "InvoiceNo";
        public static final String ATCUD = "ATCUD";
        public static final String DocumentStatus = "DocumentStatus";
        public static final String Hash = "Hash";
        public static final String HashControl = "HashControl";
        public static final String Period = "Period";
        public static final String InvoiceDate = "InvoiceDate";
        public static final String InvoiceType = "InvoiceType";
        public static final String SpecialRegimes = "SpecialRegimes";
        public static final String SourceID = "SourceID";
        public static final String EACCode = "EACCode";
        public static final String SystemEntryDate = "SystemEntryDate";
        public static final String TransactionID = "TransactionID";
        public static final String CustomerID = "CustomerID";
        public static final String ShipTo = "ShipTo";
        public static final String ShipFrom = "ShipFrom";
        public static final String MovementEndTime = "MovementEndTime";
        public static final String MovementStartTime = "MovementStartTime";
        public static final String Line = "Line";
        public static final String DocumentTotals = "DocumentTotals";
        public static final String WithholdingTax = "WithholdingTax";
    }

    public enum DocumentStatus {
        ELEMENT;

        public static final String InvoiceStatus = "InvoiceStatus";
        public static final String InvoiceStatusDate = "InvoiceStatusDate";
        public static final String Reason = "Reason";
        public static final String SourceID = "SourceID";
        public static final String SourceBilling = "SourceBilling";
    }

    public enum SpecialRegimes {
        ELEMENT;

        public static final String SelfBillingIndicator = "SelfBillingIndicator";
        public static final String CashVATSchemeIndicator = "CashVATSchemeIndicator";
        public static final String ThirdPartiesBillingIndicator = "ThirdPartiesBillingIndicator";
    }

    public enum ShipTo {
        ELEMENT;

        public static final String DeliveryID = "DeliveryID";
        public static final String DeliveryDate = "DeliveryDate";
        public static final String Address = "Address";
    }

    public enum ShipFrom {
        ELEMENT;

        public static final String DeliveryID = "DeliveryID";
        public static final String DeliveryDate = "DeliveryDate";
        public static final String Address = "Address";
    }

    public enum Address {
        ELEMENT;

        public static final String BuildingNumber = "BuildingNumber";
        public static final String StreetName = "StreetName";
        public static final String AddressDetail = "AddressDetail";
        public static final String City = "City";
        public static final String PostalCode = "PostalCode";
        public static final String Region = "Region";
        public static final String Country = "Country";
    }

    public enum Line {
        ELEMENT;

        public static final String LineNumber = "LineNumber";
        public static final String OrderReferences = "OrderReferences";
        public static final String ProductCode = "ProductCode";
        public static final String ProductDescription = "ProductDescription";
        public static final String Quantity = "Quantity";
        public static final String UnitOfMeasure = "UnitOfMeasure";
        public static final String UnitPrice = "UnitPrice";
        public static final String TaxBase = "TaxBase";
        public static final String TaxPointDate = "TaxPointDate";
        public static final String References = "References";
        public static final String Description = "Description";
        public static final String ProductSerialNumber = "ProductSerialNumber";
        public static final String DebitAmount = "DebitAmount";
        public static final String CreditAmount = "CreditAmount";
        public static final String Tax = "Tax";
        public static final String TaxExemptionReason = "TaxExemptionReason";
        public static final String TaxExemptionCode = "TaxExemptionCode";
        public static final String SettlementAmount = "SettlementAmount";
        public static final String CustomsInformation = "CustomsInformation";
    }

    public enum OrderReferences {
        ELEMENT;

        public static final String OriginatingON = "OriginatingON";
        public static final String OrderDate = "OrderDate";
    }

    public enum References {
        ELEMENT;

        public static final String Reference = "Reference";
        public static final String Reason = "Reason";
    }

    public enum ProductSerialNumber {
        ELEMENT;

        public static final String SerialNumber = "SerialNumber";
    }

    public enum Tax {
        ELEMENT;

        public static final String TaxType = "TaxType";
        public static final String TaxCountryRegion = "TaxCountryRegion";
        public static final String TaxCode = "TaxCode";
        public static final String TaxPercentage = "TaxPercentage";
        public static final String TaxAmount = "TaxAmount";
    }

    public enum CustomsInformation {
        ELEMENT;

        public static final String ARCNo = "ARCNo";
        public static final String IECAmount = "IECAmount";
    }

    public enum DocumentTotals {
        ELEMENT;

        public static final String TaxPayable = "TaxPayable";
        public static final String NetTotal = "NetTotal";
        public static final String GrossTotal = "GrossTotal";
        public static final String Currency = "Currency";
        public static final String Settlement = "Settlement";
        public static final String Payment = "Payment";
    }

    public enum Currency {
        ELEMENT;

        public static final String CurrencyCode = "CurrencyCode";
        public static final String CurrencyAmount = "CurrencyAmount";
        public static final String ExchangeRate = "ExchangeRate";
    }

    public enum Settlement {
        ELEMENT;

        public static final String SettlementDiscount = "SettlementDiscount";
        public static final String SettlementAmount = "SettlementAmount";
        public static final String SettlementDate = "SettlementDate";
        public static final String PaymentTerms = "PaymentTerms";
    }

    public enum Payment {
        ELEMENT;

        public static final String PaymentMechanism = "PaymentMechanism";
        public static final String PaymentAmount = "PaymentAmount";
        public static final String PaymentDate = "PaymentDate";
    }

    public enum WithholdingTax {
        ELEMENT;

        public static final String WithholdingTaxType = "WithholdingTaxType";
        public static final String WithholdingTaxDescription = "WithholdingTaxDescription";
        public static final String WithholdingTaxAmount = "WithholdingTaxAmount";
    }

    public enum SequenceElements {
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
}
