package antifraud.errors.transaction;

public class TransactionErrorMessage {
    private String error;

    public TransactionErrorMessage(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
