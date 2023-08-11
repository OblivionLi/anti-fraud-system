package antifraud.businesslayer.user;

public class UserToggleAccessResponseDTO {
    private String status;

    public UserToggleAccessResponseDTO(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
