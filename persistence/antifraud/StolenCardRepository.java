package antifraud.persistence.antifraud;

import antifraud.businesslayer.antifraud.stolencard.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {
    @Query("SELECT s FROM StolenCard s WHERE s.number = :number")
    StolenCard findStolenCardBy(@Param("number") String number);
}
