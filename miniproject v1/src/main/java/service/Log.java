package service;

public class Log {
    private String id;
    private String action;
    private String user;
    private String details;
    private String timestamp;

    public Log() {}

    public Log(String action, String user, String details) {
        this.id = java.util.UUID.randomUUID().toString();
        this.action = action;
        this.user = user;
        this.details = details;
        this.timestamp = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", action='" + action + '\'' +
                ", user='" + user + '\'' +
                ", details='" + details + '\'' +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
