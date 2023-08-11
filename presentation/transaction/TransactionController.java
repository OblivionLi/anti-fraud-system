package antifraud.presentation.transaction;

import antifraud.businesslayer.transaction.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {
    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<TransactionResponseDTO> saveTransaction(@RequestBody TransactionRequestDTO requestDTO) {
        return transactionService.saveTransaction(requestDTO);
    }

    @PutMapping("/transaction")
    public ResponseEntity<TransactionFeedbackResponseDTO> addTransactionFeedback(@RequestBody TransactionFeedbackRequestDTO requestDTO) {
        return transactionService.addTransactionFeedback(requestDTO);
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<Transaction>> getCardTransactionsHistories(@PathVariable String number) {
        return transactionService.getCardTransactionsHistories(number);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> findAllTransactionsHistories() {
        return transactionService.findAll();
    }
}
