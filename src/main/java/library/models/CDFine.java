package library.models;

import library.utils.DateUtils;
import java.time.LocalDateTime;

/**
 * CD Fine model representing a financial penalty for CD overdue
 * @author Library Team
 * @version 1.0
 */
public class CDFine {

    private String id;
    private String userId;
    private String cdLoanId;
    private double amount;
    private double paidAmount;
    private String issuedDate;
    private String paidDate;
    private boolean isPaid;

    // THIS is the real remaining amount
    private double remainingAmount;

    public CDFine() {}

    public CDFine(String userId, String cdLoanId, double amount) {
        if (userId == null || cdLoanId == null || amount <= 0) {
            throw new IllegalArgumentException("Invalid CD fine parameters");
        }

        this.userId = userId;
        this.cdLoanId = cdLoanId;
        this.amount = amount;
        this.paidAmount = 0.0;
        this.issuedDate = DateUtils.toString(LocalDateTime.now());
        this.isPaid = false;

        this.remainingAmount = amount; // REAL initial value
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCdLoanId() { return cdLoanId; }
    public void setCdLoanId(String cdLoanId) { this.cdLoanId = cdLoanId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Amount cannot be negative");
        this.amount = amount;
        recalcRemaining();
    }

    public double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(double paidAmount) {
        if (paidAmount < 0) throw new IllegalArgumentException("Paid amount cannot be negative");
        this.paidAmount = paidAmount;
        recalcRemaining();
    }

    public String getIssuedDate() { return issuedDate; }
    public void setIssuedDate(String issuedDate) { this.issuedDate = issuedDate; }

    public String getPaidDate() { return paidDate; }
    public void setPaidDate(String paidDate) { this.paidDate = paidDate; }

    public boolean isPaid() { return isPaid; }
    public void setPaid(boolean paid) { isPaid = paid; }

    // *** FIXED: unified remaining amount ***
    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        if (remainingAmount < 0) {
            throw new IllegalArgumentException("Remaining amount cannot be negative");
        }
        this.remainingAmount = remainingAmount;
    }

    private void recalcRemaining() {
        this.remainingAmount = amount - paidAmount;
        if (remainingAmount < 0) remainingAmount = 0;
    }

    public boolean makePayment(double paymentAmount) {
        if (paymentAmount <= 0 || paymentAmount > remainingAmount) {
            return false;
        }

        this.paidAmount += paymentAmount;
        recalcRemaining();

        if (remainingAmount == 0) {
            isPaid = true;
            setPaidDate(DateUtils.toString(LocalDateTime.now()));
        }

        return true;
    }
}
