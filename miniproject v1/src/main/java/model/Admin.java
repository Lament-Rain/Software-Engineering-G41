package model;

public class Admin extends User {
    private AdminLevel level;

    public Admin() {}

    public Admin(String id, String username, String password, String email, String phone, AdminLevel level) {
        super(id, username, password, email, phone, UserRole.ADMIN, UserStatus.ACTIVE);
        this.level = level;
    }

    // Getters and Setters
    public AdminLevel getLevel() { return level; }
    public void setLevel(AdminLevel level) { this.level = level; }

    @Override
    public String toString() {
        return "Admin{" +
                "id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", level=" + level +
                '}';
    }
}


