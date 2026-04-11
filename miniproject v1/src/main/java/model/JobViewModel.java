package model;

public class JobViewModel {
    private String title;
    private String department;
    private String deadline;
    private int applicationCount;
    
    public JobViewModel(String title, String department, String deadline, int applicationCount) {
        this.title = title;
        this.department = department;
        this.deadline = deadline;
        this.applicationCount = applicationCount;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public String getDeadline() {
        return deadline;
    }
    
    public int getApplicationCount() {
        return applicationCount;
    }
}