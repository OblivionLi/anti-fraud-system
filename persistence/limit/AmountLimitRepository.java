package antifraud.persistence.limit;

import antifraud.businesslayer.limit.AmountLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AmountLimitRepository extends JpaRepository<AmountLimit, Long> {
    @Query("SELECT a FROM AmountLimit a WHERE a.number = :number")
    AmountLimit findByNumber(@Param("number") String Number);
}
