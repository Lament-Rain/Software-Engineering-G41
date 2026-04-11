package model;

public class ApplicantReviewViewModel {
    private final String applicationId;
    private final String taId;
    private final String name;
    private final String department;
    private final String submittedAt;
    private final String status;
    private final String matchScore;
    private final String resumeStatus;
    private final String experience;
    private final String coverLetter;
    private final String reviewComment;

    public ApplicantReviewViewModel(String applicationId, String taId, String name, String department,
                                    String submittedAt, String status, String matchScore, String resumeStatus,
                                    String experience, String coverLetter, String reviewComment) {
        this.applicationId = applicationId;
        this.taId = taId;
        this.name = name;
        this.department = department;
        this.submittedAt = submittedAt;
        this.status = status;
        this.matchScore = matchScore;
        this.resumeStatus = resumeStatus;
        this.experience = experience;
        this.coverLetter = coverLetter;
        this.reviewComment = reviewComment;
    }

    public String getApplicationId() { return applicationId; }
    public String getTaId() { return taId; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getSubmittedAt() { return submittedAt; }
    public String getStatus() { return status; }
    public String getMatchScore() { return matchScore; }
    public String getResumeStatus() { return resumeStatus; }
    public String getExperience() { return experience; }
    public String getCoverLetter() { return coverLetter; }
    public String getReviewComment() { return reviewComment; }
}
