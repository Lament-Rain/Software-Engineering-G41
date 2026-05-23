package model;

// 用于 Admin Dashboard 的系统状态表格数据模型。
public class SystemStatus {
    // item 表示检查项；status 表示当前状态；details 表示补充说明。
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
