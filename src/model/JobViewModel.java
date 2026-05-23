package model;

public class JobViewModel {
    private final String jobId;
    private final String title;
    private final String department;
    private final String jobType;
    private final String requirements;
    private final String deadline;
    private final String status;
    private final int applicationCount;

    public JobViewModel(String jobId, String title, String department, String jobType, String requirements, String deadline, String status, int applicationCount) {
        this.jobId = jobId;
        this.title = title;
        this.department = department;
        this.jobType = jobType;
        this.requirements = requirements;
        this.deadline = deadline;
        this.status = status;
        this.applicationCount = applicationCount;
    }

    public String getJobId() {
        return jobId;
    }

    public String getTitle() {
        return title;
    }

    public String getDepartment() {
        return department;
    }

    public String getJobType() {
        return jobType;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getStatus() {
        return status;
    }

    public int getApplicationCount() {
        return applicationCount;
    }
}
