package antifraud.businesslayer.transaction;

public class TransactionFeedbackRequestDTO {
    private long transactionId;
    private String feedback;

    public TransactionFeedbackRequestDTO() {
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
