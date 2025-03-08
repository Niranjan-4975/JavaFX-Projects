package View;

import java.util.Objects;

public class PassbookTransactions {
    private int srno;
    private int accno;
    private double debit;
    private double credit;
    private String txndate;
    private double balance;

    public PassbookTransactions(int srNo, int accNo, double credit, double debit, String txnDate, double Balance) {
        this.srno = srNo;
        this.accno = accNo;
        this.credit = credit;
        this.debit = debit;
        this.txndate = txnDate;
        this.balance = Balance;

    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PassbookTransactions that = (PassbookTransactions) o;
        return srno == that.srno && accno == that.accno &&
                Double.compare(that.debit, debit) == 0 &&
                Double.compare(that.credit, credit) == 0 &&
                txndate.equals(that.txndate) &&
                Double.compare(that.balance, balance) == 0;
    }
    @Override
    public int hashCode() {
        return Objects.hash(srno, accno, debit, credit, txndate, balance);
    }
    @SuppressWarnings("unused")
    public int getSrno() {
        return srno;
    }
    @SuppressWarnings("unused")
    public void setSrno(int srno) {
        this.srno = srno;
    }
    @SuppressWarnings("unused")
    public int getAccno() {
        return accno;
    }
    @SuppressWarnings("unused")
    public void setAccno(int accno) {
        this.accno = accno;
    }
    @SuppressWarnings("unused")
    public double getDebit() {
        return debit;
    }
    @SuppressWarnings("unused")
    public void setDebit(double debit) {
        this.debit = debit;
    }
    @SuppressWarnings("unused")
    public double getCredit() {
        return credit;
    }
    @SuppressWarnings("unused")
    public void setCredit(double credit) {
        this.credit = credit;
    }
    @SuppressWarnings("unused")
    public String getTxndate() {
        return txndate;
    }
    @SuppressWarnings("unused")
    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
}
