package antifraud.businesslayer.antifraud.suspiciousip;

import jakarta.persistence.*;

@Entity
@Table(name = "suspicious_ips")
public class SuspiciousIp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String ip;

    public SuspiciousIp() {}

    public SuspiciousIp(String ip) {
        this.ip = ip;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
