package antifraud.businesslayer.transaction;

import antifraud.businesslayer.antifraud.stolencard.StolenCard;
import antifraud.businesslayer.antifraud.stolencard.StolenCardService;
import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIp;
import antifraud.businesslayer.enums.TransactionRegion;
import antifraud.businesslayer.enums.TransactionStatus;
import antifraud.businesslayer.limit.AmountLimit;
import antifraud.businesslayer.limit.AmountLimitService;
import antifraud.persistence.antifraud.StolenCardRepository;
import antifraud.persistence.antifraud.SuspiciousIpRepository;
import antifraud.persistence.transaction.TransactionRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TransactionService {
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);
    private String resultOfTransaction;
    private List<String> infoOfTransaction;

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;
    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    StolenCardService stolenCardService;
    @Autowired
    AmountLimitService amountLimitService;
    @Autowired
    private EntityManager entityManager;

    public ResponseEntity<TransactionResponseDTO> saveTransaction(TransactionRequestDTO request) {
        if (request == null
                || request.getAmount() < 1
                || request.getIp() == null
                || request.getNumber() == null
                || request.getRegion() == null
                || request.getDate() == null
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        if (!TransactionRegion.contains(request.getRegion())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Map<String, String> resultBody = getTransactionResult(request);
        Transaction transaction = new Transaction(
                request.getAmount(),
                request.getIp(),
                request.getNumber(),
                request.getRegion(),
                request.getDate(),
                resultBody.get("result"),
                ""
        );
        transactionRepository.save(transaction);

        TransactionResponseDTO response = new TransactionResponseDTO(resultBody.get("result"), resultBody.get("info"));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Transactional
    public ResponseEntity<TransactionFeedbackResponseDTO> addTransactionFeedback(TransactionFeedbackRequestDTO request) {
        if (request == null
                || request.getTransactionId() < 1
                || request.getFeedback() == null
                || !isTransactionFeedbackValid(request.getFeedback())
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        Transaction transaction = transactionRepository.findById(request.getTransactionId());
        if (transaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (transaction.getTransactionStatus().equals(request.getFeedback())) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).build();
        }

        if (transaction.getFeedback() != null
                && transaction.getFeedback().equals(request.getFeedback())
                || !transaction.getFeedback().isEmpty()
        ) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        transactionRepository.updateTransactionFeedback(request.getFeedback(), transaction.getId());
        entityManager.refresh(transaction);
        amountLimitService.updateAmountLimit(transaction);

        TransactionFeedbackResponseDTO response = new TransactionFeedbackResponseDTO(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getIp(),
                transaction.getNumber(),
                transaction.getRegion(),
                transaction.getDate(),
                transaction.getTransactionStatus(),
                request.getFeedback()
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<List<Transaction>> getCardTransactionsHistories(String cardNumber) {
        if (cardNumber.isEmpty()
                || !stolenCardService.isCardNumberValid(cardNumber)
        ) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        List<Transaction> transactions = transactionRepository.findAllByCardNumber(cardNumber);
        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    public ResponseEntity<List<Transaction>> findAll() {
        List<Transaction> transactions = transactionRepository.findAll(Sort.by("id"));
        return ResponseEntity.status(HttpStatus.OK).body(transactions);
    }

    private boolean isTransactionFeedbackValid(String feedback) {
        return feedback.equals(TransactionStatus.ALLOWED.name())
                || feedback.equals(TransactionStatus.PROHIBITED.name())
                || feedback.equals(TransactionStatus.MANUAL_PROCESSING.name());
    }

    private Map<String, String> getTransactionResult(TransactionRequestDTO request) {
        Map<String, String> resultBody = new HashMap<>();
        resultOfTransaction = TransactionStatus.ALLOWED.name();
        infoOfTransaction = new ArrayList<>();

        validateTransactionStolenCardNumber(request.getNumber());
        validateTransactionSuspiciousIp(request.getIp());
        validateTransactionCorrelations(request);
        validateTransactionAmount(request.getAmount(), request.getNumber());

        Collections.sort(infoOfTransaction);
        String infoString = resultOfTransaction.equals(TransactionStatus.ALLOWED.name()) ? "none" : String.join(", ", infoOfTransaction);
        resultBody.put("info", infoString);
        resultBody.put("result", resultOfTransaction);
        return resultBody;
    }

    private void validateTransactionAmount(long amount, String number) {
        AmountLimit amountLimit = amountLimitService.getAmountLimits(number);
        long maxAllowed = amountLimit.getMaxAllowed();
        long maxManual = amountLimit.getMaxManual();

        logger.info("SHOW MAX ALLOWED: {}", maxAllowed);
        logger.info("SHOW MAX MANUAL: {}", maxManual);
        logger.info("Amount Limit: {}", amountLimit);

        if (amount > maxManual) {
            if (!resultOfTransaction.equals(TransactionStatus.PROHIBITED.name())) {
                infoOfTransaction.clear();
            }

            resultOfTransaction = TransactionStatus.PROHIBITED.name();
            infoOfTransaction.add("amount");
        }

        if (amount > maxAllowed
                && amount <= maxManual
                && !resultOfTransaction.equals(TransactionStatus.PROHIBITED.name())
        ) {
            resultOfTransaction = TransactionStatus.MANUAL_PROCESSING.name();
            infoOfTransaction.add("amount");
        }
    }

    private void validateTransactionStolenCardNumber(String cardNumber) {
        StolenCard stolenCard = stolenCardRepository.findStolenCardBy(cardNumber);
        if (stolenCard != null) {
            resultOfTransaction = TransactionStatus.PROHIBITED.name();
            infoOfTransaction.add("card-number");
        }
    }

    private void validateTransactionSuspiciousIp(String ip) {
        SuspiciousIp suspiciousIp = suspiciousIpRepository.findSuspiciousIpBy(ip);
        if (suspiciousIp != null) {
            resultOfTransaction = TransactionStatus.PROHIBITED.name();
            infoOfTransaction.add("ip");
        }
    }

    private void validateTransactionCorrelations(TransactionRequestDTO request) {
        List<Transaction> transactions = transactionRepository.findAllByDateBetweenAndNumber(request.getDate().minusHours(1), request.getDate(), request.getNumber());

        long regionCount = transactions.stream()
                .map(Transaction::getRegion)
                .filter(region -> !Objects.equals(region, request.getRegion()))
                .distinct().count();

        long ipCount = transactions.stream()
                .map(Transaction::getIp)
                .filter(ip -> !Objects.equals(ip, request.getIp()))
                .distinct().count();


        if (regionCount == 2 && !Objects.equals(resultOfTransaction, TransactionStatus.PROHIBITED.name())) {
            resultOfTransaction = TransactionStatus.MANUAL_PROCESSING.name();
            infoOfTransaction.add("region-correlation");
        }

        if (ipCount == 2 && !Objects.equals(resultOfTransaction, TransactionStatus.PROHIBITED.name())) {
            resultOfTransaction = TransactionStatus.MANUAL_PROCESSING.name();
            infoOfTransaction.add("ip-correlation");
        }

        if (regionCount > 2) {
            resultOfTransaction = TransactionStatus.PROHIBITED.name();
            infoOfTransaction.add("region-correlation");
        }

        if (ipCount > 2) {
            resultOfTransaction = TransactionStatus.PROHIBITED.name();
            infoOfTransaction.add("ip-correlation");
        }
    }
}
