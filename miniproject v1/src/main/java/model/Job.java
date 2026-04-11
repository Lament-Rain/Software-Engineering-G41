package model;

import java.util.List;

public class Job {
    private String id;
    private String title;
    private JobType type;
    private String department;
    private String description;
    private List<String> skills;
    private String workTime;
    private int recruitNum;
    private String deadline;
    private String salary;
    private String location;
    private String extraRequirements;
    private String moId; // 保留以兼容旧数据
    private String publisherId; // 发布者ID（可以是MO或Admin）
    private String publisherType; // 发布者类型: "MO" 或 "ADMIN"
    private String publisherName; // 发布者名称
    private JobStatus status;
    private String createdAt;
    private String updatedAt;
    private String reviewedBy;
    private String reviewTime;
    private String reviewComment;

    public Job() {}

    public Job(String id, String title, JobType type, String department, String description, 
               List<String> skills, String workTime, int recruitNum, String deadline, 
               String moId) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.department = department;
        this.description = description;
        this.skills = skills;
        this.workTime = workTime;
        this.recruitNum = recruitNum;
        this.deadline = deadline;
        this.moId = moId;
        this.status = JobStatus.DRAFT;
        this.createdAt = java.time.LocalDateTime.now().toString();
        this.updatedAt = java.time.LocalDateTime.now().toString();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public JobType getType() { return type; }
    public void setType(JobType type) { this.type = type; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    
    public String getWorkTime() { return workTime; }
    public void setWorkTime(String workTime) { this.workTime = workTime; }
    
    public int getRecruitNum() { return recruitNum; }
    public void setRecruitNum(int recruitNum) { this.recruitNum = recruitNum; }
    
    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }
    
    public String getSalary() { return salary; }
    public void setSalary(String salary) { this.salary = salary; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getExtraRequirements() { return extraRequirements; }
    public void setExtraRequirements(String extraRequirements) { this.extraRequirements = extraRequirements; }

    public String getMoId() { return moId; }
    public void setMoId(String moId) { this.moId = moId; }

    public String getPublisherId() { return publisherId; }
    public void setPublisherId(String publisherId) { this.publisherId = publisherId; }

    public String getPublisherType() { return publisherType; }
    public void setPublisherType(String publisherType) { this.publisherType = publisherType; }

    public String getPublisherName() { return publisherName; }
    public void setPublisherName(String publisherName) { this.publisherName = publisherName; }

    public JobStatus getStatus() { return status; }
    public void setStatus(JobStatus status) { this.status = status; }
    
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

    @Override
    public String toString() {
        return "Job{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", department='" + department + '\'' +
                ", description='" + description + '\'' +
                ", skills=" + skills +
                ", workTime='" + workTime + '\'' +
                ", recruitNum=" + recruitNum +
                ", deadline='" + deadline + '\'' +
                ", salary='" + salary + '\'' +
                ", location='" + location + '\'' +
                ", extraRequirements='" + extraRequirements + '\'' +
                ", moId='" + moId + '\'' +
                ", publisherId='" + publisherId + '\'' +
                ", publisherType='" + publisherType + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", status=" + status +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", reviewedBy='" + reviewedBy + '\'' +
                ", reviewTime='" + reviewTime + '\'' +
                ", reviewComment='" + reviewComment + '\'' +
                '}';
    }
}

