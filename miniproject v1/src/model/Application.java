package model;

public class Application {
    private String id;
    private String taId;
    private String jobId;
    private String coverLetter;
    private ApplicationStatus status;
    private String createdAt;
    private String updatedAt;
    private String reviewedBy;
    private String reviewTime;
    private String reviewComment;
    private double matchScore;

    public Application() {}

    public Application(String id, String taId, String jobId, String coverLetter) {
        this.id = id;
        this.taId = taId;
        this.jobId = jobId;
        this.coverLetter = coverLetter;
        this.status = ApplicationStatus.PENDING;
        this.createdAt = java.time.LocalDateTime.now().toString();
        this.updatedAt = java.time.LocalDateTime.now().toString();
        this.matchScore = 0.0;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTaId() { return taId; }
    public void setTaId(String taId) { this.taId = taId; }
    
    public String getJobId() { return jobId; }
    public void setJobId(String jobId) { this.jobId = jobId; }
    
    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
    
    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }
    
    public String getReviewTime() { return reviewTime; }
    public void setReviewTime(String reviewTime) { this.reviewTime = reviewTime; }
    
    public String getReviewComment() { return reviewComment; }
    public void setReviewComment(String reviewComment) { this.reviewComment = reviewComment; }
    
    public double getMatchScore() { return matchScore; }
    public void setMatchScore(double matchScore) { this.matchScore = matchScore; }

    @Override
    public String toString() {
        return "Application{" +
                "id='" + id + '\'' +
                ", taId='" + taId + '\'' +
                ", jobId='" + jobId + '\'' +
                ", coverLetter='" + coverLetter + '\'' +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", reviewTime='" + reviewTime + '\'' +
                ", reviewComment='" + reviewComment + '\'' +
                ", matchScore=" + matchScore +
                '}';
    }
}

