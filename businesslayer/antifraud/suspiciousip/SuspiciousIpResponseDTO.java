package antifraud.businesslayer.antifraud.suspiciousip;

public class SuspiciousIpResponseDTO {
    private long id;
    private String ip;

    public SuspiciousIpResponseDTO(long id, String ip) {
        this.id = id;
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
