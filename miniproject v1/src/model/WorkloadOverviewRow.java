package model;

public class WorkloadOverviewRow {
    private final String taId;
    private final String taName;
    private final String department;
    private final int workloadHours;
    private final String workloadBand;
    private final String aiComment;

    public WorkloadOverviewRow(String taId, String taName, String department, int workloadHours, String workloadBand, String aiComment) {
        this.taId = taId;
        this.taName = taName;
        this.department = department;
        this.workloadHours = workloadHours;
        this.workloadBand = workloadBand;
        this.aiComment = aiComment;
    }

    public String getTaId() { return taId; }
    public String getTaName() { return taName; }
    public String getDepartment() { return department; }
    public int getWorkloadHours() { return workloadHours; }
    public String getWorkloadBand() { return workloadBand; }
    public String getAiComment() { return aiComment; }
}
