package model;

import java.util.List;

public class TA extends User {
    // Notification type enum
    public enum NotificationType {
        APPLICATION_STATUS, // Application status updates
        JOB_DEADLINE, // Job deadline reminders
        SYSTEM_ANNOUNCEMENT // System announcements
    }
    
    // Notification settings class
    public static class NotificationSetting {
        private NotificationType type;
        private boolean enabled;
        
        public NotificationSetting(NotificationType type, boolean enabled) {
            this.type = type;
            this.enabled = enabled;
        }
        
        public NotificationType getType() { return type; }
        public void setType(NotificationType type) { this.type = type; }
        
        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }

    private String name;
    private String gender;
    private int age;
    private String department;
    private String grade;
    private String studentId;
    private String availableTime;
    private List<String> skills;
    private String experience;
    private String awards;
    private String languageSkills;
    private String otherSkills;
    private String resumePath;
    private ProfileStatus profileStatus;
    private String profileUpdatedAt;
    private String profileReviewComment;
    private List<String> savedJobs; // List of saved job IDs
    private List<NotificationSetting> notificationSettings; // Notification settings

    public TA() {}

    public TA(String id, String username, String password, String email, String phone) {
        super(id, username, password, email, phone, UserRole.TA, UserStatus.ACTIVE);
        this.profileStatus = ProfileStatus.DRAFT;
        this.savedJobs = new java.util.ArrayList<>();
        this.notificationSettings = new java.util.ArrayList<>();
    }
    
    // Initialize default notification settings
    public void initializeNotificationSettings() {
        this.notificationSettings.add(new NotificationSetting(NotificationType.APPLICATION_STATUS, true));
        this.notificationSettings.add(new NotificationSetting(NotificationType.JOB_DEADLINE, true));
        this.notificationSettings.add(new NotificationSetting(NotificationType.SYSTEM_ANNOUNCEMENT, true));
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    
    public String getAvailableTime() { return availableTime; }
    public void setAvailableTime(String availableTime) { this.availableTime = availableTime; }
    
    public List<String> getSkills() { return skills; }
    public void setSkills(List<String> skills) { this.skills = skills; }
    
    public String getExperience() { return experience; }
    public void setExperience(String experience) { this.experience = experience; }
    
    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }
    
    public String getLanguageSkills() { return languageSkills; }
    public void setLanguageSkills(String languageSkills) { this.languageSkills = languageSkills; }
    
    public String getOtherSkills() { return otherSkills; }
    public void setOtherSkills(String otherSkills) { this.otherSkills = otherSkills; }
    
    public String getResumePath() { return resumePath; }
    public void setResumePath(String resumePath) { this.resumePath = resumePath; }
    
    public ProfileStatus getProfileStatus() { return profileStatus; }
    public void setProfileStatus(ProfileStatus profileStatus) { this.profileStatus = profileStatus; }
    
    public String getProfileUpdatedAt() { return profileUpdatedAt; }
    public void setProfileUpdatedAt(String profileUpdatedAt) { this.profileUpdatedAt = profileUpdatedAt; }

    public String getProfileReviewComment() { return profileReviewComment; }
    public void setProfileReviewComment(String profileReviewComment) { this.profileReviewComment = profileReviewComment; }
    
    public List<String> getSavedJobs() { return savedJobs; }
    public void setSavedJobs(List<String> savedJobs) { this.savedJobs = savedJobs; }
    
    public List<NotificationSetting> getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(List<NotificationSetting> notificationSettings) { this.notificationSettings = notificationSettings; }

    // Save job
    public void saveJob(String jobId) {
        if (!savedJobs.contains(jobId)) {
            savedJobs.add(jobId);
        }
    }
    
    // Unsave job
    public void unsaveJob(String jobId) {
        savedJobs.remove(jobId);
    }
    
    // Check whether a job is already saved
    public boolean isJobSaved(String jobId) {
        return savedJobs.contains(jobId);
    }
    
    // Update notification settings
    public void updateNotificationSetting(NotificationType type, boolean enabled) {
        for (NotificationSetting setting : notificationSettings) {
            if (setting.getType() == type) {
                setting.setEnabled(enabled);
                return;
            }
        }
        // If the setting does not exist, add a new setting
        notificationSettings.add(new NotificationSetting(type, enabled));
    }

    @Override
    public String toString() {
        return "TA{" +
                "id='" + getId() + '\'' +
                ", username='" + getUsername() + '\'' +
                ", password='" + getPassword() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", phone='" + getPhone() + '\'' +
                ", name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", department='" + department + '\'' +
                ", grade='" + grade + '\'' +
                ", studentId='" + studentId + '\'' +
                ", availableTime='" + availableTime + '\'' +
                ", skills=" + skills +
                ", experience='" + experience + '\'' +
                ", awards='" + awards + '\'' +
                ", languageSkills='" + languageSkills + '\'' +
                ", otherSkills='" + otherSkills + '\'' +
                ", resumePath='" + resumePath + '\'' +
                ", profileStatus=" + profileStatus +
                ", profileUpdatedAt='" + profileUpdatedAt + '\'' +
                ", profileReviewComment='" + profileReviewComment + '\'' +
                '}';
    }
}
