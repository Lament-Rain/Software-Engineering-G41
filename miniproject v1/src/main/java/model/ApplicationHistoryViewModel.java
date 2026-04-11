package model;

public class ApplicationHistoryViewModel {
    private final String applicationId;
    private final String jobTitle;
    private final String department;
    private final String appliedAt;
    private final String status;
    private final String reviewTime;
    private final String reviewComment;
    private final String matchScore;
    private final boolean withdrawable;

    public ApplicationHistoryViewModel(String applicationId, String jobTitle, String department, String appliedAt,
                                       String status, String reviewTime, String reviewComment,
                                       String matchScore, boolean withdrawable) {
        this.applicationId = applicationId;
        this.jobTitle = jobTitle;
        this.department = department;
        this.appliedAt = appliedAt;
        this.status = status;
        this.reviewTime = reviewTime;
        this.reviewComment = reviewComment;
        this.matchScore = matchScore;
        this.withdrawable = withdrawable;
    }

    public String getApplicationId() { return applicationId; }
    public String getJobTitle() { return jobTitle; }
    public String getDepartment() { return department; }
    public String getAppliedAt() { return appliedAt; }
    public String getStatus() { return status; }
    public String getReviewTime() { return reviewTime; }
    public String getReviewComment() { return reviewComment; }
    public String getMatchScore() { return matchScore; }
    public boolean isWithdrawable() { return withdrawable; }
}
