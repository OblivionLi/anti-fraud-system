package antifraud.exceptions.antifraud;

public class InvalidTransactionAmountException extends RuntimeException{
    public InvalidTransactionAmountException(String message) {
        super(message);
    }
}
