package antifraud.businesslayer.antifraud.suspiciousip;

public class SuspiciousIpRequestDTO {
    private String ip;

    public SuspiciousIpRequestDTO() {}

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
