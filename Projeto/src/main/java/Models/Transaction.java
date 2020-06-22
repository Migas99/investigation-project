package Models;

import java.util.Iterator;
import java.util.LinkedList;

public class Transaction {

    private String TransactionID;
    private LinkedList<DebitLine> debitLines;
    private LinkedList<CreditLine> creditLines;

    public Transaction(String TransactionID) {
        this.TransactionID = TransactionID;
        this.debitLines = new LinkedList<>();
        this.creditLines = new LinkedList<>();
    }

    public boolean addDebitLine(DebitLine debitLine) {
        Iterator<DebitLine> iterator = this.debitLines.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getRecordID().equalsIgnoreCase(debitLine.getRecordID())) {
                return false;
            }
        }

        this.debitLines.add(debitLine);

        return true;
    }

    public boolean addCreditLine(CreditLine creditLine) {
        Iterator<CreditLine> iterator = this.creditLines.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getRecordID().equalsIgnoreCase(creditLine.getRecordID())) {
                return false;
            }
        }

        this.creditLines.add(creditLine);

        return true;
    }

    public String getTransactionID() {
        return this.TransactionID;
    }

    public Iterator<DebitLine> getDebitLines() {
        return this.debitLines.iterator();
    }

    public Iterator<CreditLine> getCreditLines() {
        return this.creditLines.iterator();
    }
}
