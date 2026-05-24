package service;

public class Log {
    // 系统日志对象：用于记录关键操作的类型、用户、详情、时间和 IP
    private String id;
    private String action;
    private String user;
    private String details;
    private String timestamp;
    private String ip;

    // 空构造方法：方便文件读取或对象反序列化时创建 Log 对象
    public Log() {}

    // 创建日志时自动生成唯一 ID，并记录当前时间
    public Log(String action, String user, String details, String ip) {
        this.id = java.util.UUID.randomUUID().toString();
        this.action = action;
        this.user = user;
        this.details = details;
        this.timestamp = java.time.LocalDateTime.now().toString();
        this.ip = ip;
    }
    
    public Log(String action, String user, String details) {
        this(action, user, details, "unknown");
    }

    // Getters and Setters：供 DataStorage 和界面读取/保存日志字段
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
    
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    // 日志显示格式：管理员查看日志时会用到这个字符串输出
    @Override
    public String toString() {
        return "Log{" +
                "id='" + id + '\'' +
                ", action='" + action + '\'' +
                ", user='" + user + '\'' +
                ", details='" + details + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
