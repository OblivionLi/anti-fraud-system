package antifraud.businesslayer.user;

public class UserUpdateRoleRequestDTO {
    private String username;
    private String role;

    public UserUpdateRoleRequestDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
