package model;

public class MO extends User {
    private String name;
    private String department;

    public MO() {}

    public MO(String id, String username, String password, String email, String phone, String department) {
        super(id, username, password, email, phone, UserRole.MO, UserStatus.ACTIVE);
        this.department = department;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    @Override
    public String toString() {
        return "MO{" +
                "id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", name='" + name + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
