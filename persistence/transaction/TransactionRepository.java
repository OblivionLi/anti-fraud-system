package antifraud.persistence.transaction;

import antifraud.businesslayer.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("SELECT t FROM Transaction t WHERE t.date BETWEEN :start AND :end AND t.number = :number")
    List<Transaction> findAllByDateBetweenAndNumber(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("number") String number);

    @Query("SELECT t FROM Transaction t WHERE t.id = :transactionId")
    Transaction findById(@Param("transactionId") long transactionId);

    @Query("SELECT t FROM Transaction t WHERE t.number = :number")
    List<Transaction> findAllByCardNumber(@Param("number") String number);

    @Modifying
    @Query("UPDATE Transaction t SET t.feedback = :feedback WHERE t.id = :id")
    void updateTransactionFeedback(@Param("feedback") String feedback, @Param("id") long id);
}
