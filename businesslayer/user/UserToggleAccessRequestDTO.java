package antifraud.businesslayer.user;

public class UserToggleAccessRequestDTO {
    private String username;
    private String operation;

    public UserToggleAccessRequestDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
