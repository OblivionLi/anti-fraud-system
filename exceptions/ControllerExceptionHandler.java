package antifraud.exceptions;

import antifraud.errors.transaction.TransactionErrorMessage;
import antifraud.exceptions.antifraud.InvalidTransactionAmountException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(InvalidTransactionAmountException.class)
    public ResponseEntity<TransactionErrorMessage> handleReturnTicketWrongToken(InvalidTransactionAmountException e, WebRequest request) {
        TransactionErrorMessage body = new TransactionErrorMessage(e.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
