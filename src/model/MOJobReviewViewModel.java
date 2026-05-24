package model;

public class MOJobReviewViewModel {
    private final String jobId;
    private final String title;
    private final String department;
    private final String deadline;
    private final String applicantCount;

    public MOJobReviewViewModel(String jobId, String title, String department, String deadline, String applicantCount) {
        this.jobId = jobId;
        this.title = title;
        this.department = department;
        this.deadline = deadline;
        this.applicantCount = applicantCount;
    }

    public String getJobId() { return jobId; }
    public String getTitle() { return title; }
    public String getDepartment() { return department; }
    public String getDeadline() { return deadline; }
    public String getApplicantCount() { return applicantCount; }

    @Override
    public String toString() {
        return title + " | " + department + " | Applicants: " + applicantCount;
    }
}
