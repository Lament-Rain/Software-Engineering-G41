package model;

public class Task {
    private String name;
    private String dueDate;
    private String status;
    
    public Task(String name, String dueDate, String status) {
        this.name = name;
        this.dueDate = dueDate;
        this.status = status;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDueDate() {
        return dueDate;
    }
    
    public String getStatus() {
        return status;
    }
}