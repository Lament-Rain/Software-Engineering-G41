package model;

public class SystemStatus {
    private String item;
    private String status;
    private String details;
    
    public SystemStatus(String item, String status, String details) {
        this.item = item;
        this.status = status;
        this.details = details;
    }
    
    public String getItem() {
        return item;
    }
    
    public String getStatus() {
        return status;
    }
    
    public String getDetails() {
        return details;
    }
}