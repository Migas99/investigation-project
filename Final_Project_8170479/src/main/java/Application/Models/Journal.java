package Application.Models;

import java.util.Iterator;
import java.util.LinkedList;

public class Journal {

    private String JournalID;
    private String Description;
    private LinkedList<Transaction> transactions;

    public Journal(String JournalID) {
        this.JournalID = JournalID;
        this.transactions = new LinkedList<>();
    }

    public Journal(String JournalID, String Description) {
        this.JournalID = JournalID;
        this.Description = Description;
        this.transactions = new LinkedList<>();
    }

    public boolean addTransaction(Transaction transaction) {
        Iterator<Transaction> iterator = this.transactions.iterator();

        while (iterator.hasNext()) {
            if (iterator.next().getTransactionID().equalsIgnoreCase(transaction.getTransactionID())) {
                return false;
            }
        }

        this.transactions.add(transaction);

        return true;
    }

    public String getJournalID() {
        return this.JournalID;
    }

    public String getDescription() {
        return this.Description;
    }

    public Iterator<Transaction> getTransactions() {
        return this.transactions.iterator();
    }
}
