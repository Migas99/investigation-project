package Models;

public class CreditLine {

    private String RecordID;
    private String AccountID;
    private String SourceDocumentID;
    private String SystemEntryDate;
    private String Description;
    private String CreditAmount;

    public CreditLine(String RecordID){
        this.RecordID = RecordID;
    }

    public String getRecordID() {
        return RecordID;
    }

    public String getAccountID() {
        return AccountID;
    }

    public String getSourceDocumentID() {
        return SourceDocumentID;
    }

    public String getSystemEntryDate() {
        return SystemEntryDate;
    }

    public String getDescription() {
        return Description;
    }

    public String getCreditAmount() {
        return CreditAmount;
    }

    public void setAccountID(String accountID) {
        AccountID = accountID;
    }

    public void setSourceDocumentID(String sourceDocumentID) {
        SourceDocumentID = sourceDocumentID;
    }

    public void setSystemEntryDate(String systemEntryDate) {
        SystemEntryDate = systemEntryDate;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setCreditAmount(String creditAmount) {
        CreditAmount = creditAmount;
    }
}
