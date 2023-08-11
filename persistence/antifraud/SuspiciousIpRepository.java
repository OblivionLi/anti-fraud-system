package antifraud.persistence.antifraud;

import antifraud.businesslayer.antifraud.suspiciousip.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIp, Long> {
    @Query("SELECT s FROM SuspiciousIp s WHERE s.ip = :ip")
    SuspiciousIp findSuspiciousIpBy(@Param("ip") String ip);
}
