package service;

import model.*;
import java.util.List;
import java.util.UUID;

public class ApplicationService {
    // Submit application
    public static Application submitApplication(String taId, String jobId, String coverLetter) {
        // Check whether the TA exists and the profile is approved
        TA ta = UserService.getTAProfile(taId);
        if (ta == null || ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            return null;
        }

        // Check whether the job exists and can be applied for
        Job job = JobService.getJobById(jobId);
        if (job == null || job.getStatus() != model.JobStatus.PUBLISHED) {
            return null;
        }

        // Check whether a non-repeatable application already exists
        List<Application> applications = DataStorage.getApplications();
        for (Application app : applications) {
            if (!app.getTaId().equals(taId) || !app.getJobId().equals(jobId)) {
                continue;
            }
            if (app.getStatus() == model.ApplicationStatus.PENDING ||
                    app.getStatus() == model.ApplicationStatus.SCREENED ||
                    app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                return null;
            }
        }

        // Create application
        String id = UUID.randomUUID().toString();
        Application application = new Application(id, taId, jobId, coverLetter);

        // Calculate match score
        double matchScore = calculateMatchScore(ta, job);
        application.setMatchScore(matchScore);

        // Save application
        applications.add(application);
        DataStorage.saveApplications(applications);
        DataStorage.addLog("SUBMIT_APPLICATION", taId, "Application submitted for job: " + job.getTitle());

        return application;
    }

    // Screen applicants
    public static boolean screenApplication(String applicationId, model.ApplicationStatus status, String comment, String moId) {
        List<Application> applications = DataStorage.getApplications();
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getId().equals(applicationId)) {
                Application app = applications.get(i);
                app.setStatus(status);
                app.setReviewedBy(moId);
                app.setReviewTime(java.time.LocalDateTime.now().toString());
                app.setReviewComment(comment);
                app.setUpdatedAt(java.time.LocalDateTime.now().toString());
                applications.set(i, app);
                DataStorage.saveApplications(applications);
                DataStorage.addLog("SCREEN_APPLICATION", moId, "Application screened: " + status);
                return true;
            }
        }
        return false;
    }

    // Reject application
    public static boolean rejectApplication(String applicationId, String reason, String moId) {
        List<Application> applications = DataStorage.getApplications();
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getId().equals(applicationId)) {
                Application app = applications.get(i);
                app.setStatus(model.ApplicationStatus.REJECTED);
                app.setReviewedBy(moId);
                app.setReviewTime(java.time.LocalDateTime.now().toString());
                app.setReviewComment(reason);
                app.setUpdatedAt(java.time.LocalDateTime.now().toString());
                applications.set(i, app);
                DataStorage.saveApplications(applications);
                DataStorage.addLog("REJECT_APPLICATION", moId, "Application rejected: TA " + app.getTaId() + " reason: " + reason);
                return true;
            }
        }
        return false;
    }

    // Accept application
    public static boolean acceptApplication(String applicationId, String moId) {
        List<Application> applications = DataStorage.getApplications();
        Application targetApp = null;
        
        // Find target application
        for (Application app : applications) {
            if (app.getId().equals(applicationId)) {
                targetApp = app;
                break;
            }
        }

        if (targetApp == null) {
            return false;
        }

        // Check whether the job exists
        Job job = JobService.getJobById(targetApp.getJobId());
        if (job == null) {
            return false;
        }

        // Check whether the number of accepted applicants has reached the limit
        int acceptedCount = 0;
        for (Application app : applications) {
            if (app.getJobId().equals(targetApp.getJobId()) && app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                acceptedCount++;
            }
        }

        if (acceptedCount >= job.getRecruitNum()) {
            return false;
        }

        // Update application status
        targetApp.setStatus(model.ApplicationStatus.ACCEPTED);
        targetApp.setReviewedBy(moId);
        targetApp.setReviewTime(java.time.LocalDateTime.now().toString());
        targetApp.setUpdatedAt(java.time.LocalDateTime.now().toString());

        // Save updates
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getId().equals(applicationId)) {
                applications.set(i, targetApp);
                break;
            }
        }

        DataStorage.saveApplications(applications);
        DataStorage.addLog("ACCEPT_APPLICATION", moId, "Application accepted: TA " + targetApp.getTaId() + " for job " + job.getTitle());

        return true;
    }
    
    // Filter applications by status
    public static List<Application> getApplicationsByJobAndStatus(String jobId, model.ApplicationStatus status) {
        List<Application> applications = DataStorage.getApplications();
        List<Application> result = new java.util.ArrayList<>();
        for (Application app : applications) {
            if (app.getJobId().equals(jobId) && (status == null || app.getStatus() == status)) {
                result.add(app);
            }
        }
        return result;
    }

    // Get TA applications
    public static List<Application> getApplicationsByTA(String taId) {
        List<Application> applications = DataStorage.getApplications();
        List<Application> result = new java.util.ArrayList<>();
        for (Application app : applications) {
            if (app.getTaId().equals(taId)) {
                result.add(app);
            }
        }
        return result;
    }

    // Get job applications
    public static List<Application> getApplicationsByJob(String jobId) {
        List<Application> applications = DataStorage.getApplications();
        List<Application> result = new java.util.ArrayList<>();
        for (Application app : applications) {
            if (app.getJobId().equals(jobId)) {
                result.add(app);
            }
        }
        return result;
    }

    // Get all applications
    public static List<Application> getAllApplications() {
        return DataStorage.getApplications();
    }

    // Withdraw application
    public static boolean withdrawApplication(String applicationId, String taId) {
        List<Application> applications = DataStorage.getApplications();
        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            if (!app.getId().equals(applicationId) || !app.getTaId().equals(taId)) {
                continue;
            }

            if (app.getStatus() != model.ApplicationStatus.PENDING) {
                return false;
            }

            Job job = JobService.getJobById(app.getJobId());
            if (job == null || isDeadlinePassed(job.getDeadline())) {
                return false;
            }

            app.setStatus(model.ApplicationStatus.WITHDRAWN);
            app.setUpdatedAt(java.time.LocalDateTime.now().toString());
            applications.set(i, app);
            DataStorage.saveApplications(applications);
            DataStorage.addLog("WITHDRAW_APPLICATION", taId, "Application withdrawn: " + applicationId);
            return true;
        }
        return false;
    }

    public static boolean isDeadlinePassed(String deadline) {
        if (deadline == null || deadline.trim().isEmpty() || "null".equalsIgnoreCase(deadline.trim())) {
            return true;
        }

        java.time.LocalDate today = java.time.LocalDate.now();
        String value = deadline.trim();
        java.util.List<java.time.format.DateTimeFormatter> formatters = java.util.Arrays.asList(
                java.time.format.DateTimeFormatter.ISO_LOCAL_DATE,
                java.time.format.DateTimeFormatter.ofPattern("yyyy-M-d"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-d"),
                java.time.format.DateTimeFormatter.ofPattern("yyyy-M-dd")
        );

        for (java.time.format.DateTimeFormatter formatter : formatters) {
            try {
                java.time.LocalDate date = java.time.LocalDate.parse(value, formatter);
                return !date.isAfter(today);
            } catch (java.time.format.DateTimeParseException ignored) {
            }
        }

        try {
            java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(value);
            return !dateTime.toLocalDate().isAfter(today);
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        return true;
    }

    // Calculate match score
    private static double calculateMatchScore(TA ta, Job job) {
        double score = 0.0;
        int totalSkills = 0;
        int matchedSkills = 0;

        // Skill matching
        if (job.getSkills() != null) {
            totalSkills = job.getSkills().size();
            if (totalSkills > 0) {
                for (String skill : job.getSkills()) {
                    if (ta.getSkills() != null && ta.getSkills().contains(skill)) {
                        matchedSkills++;
                    }
                }
                score += (double) matchedSkills / totalSkills * 60; // Skill matching accounts for 60%
            }
        }

        // Experience matching (simple simulation)
        if (ta.getExperience() != null && !ta.getExperience().isEmpty()) {
            score += 20; // Experience adds 20 points
        }

        // Department matching
        if (ta.getDepartment() != null && job.getDepartment() != null && ta.getDepartment().equals(job.getDepartment())) {
            score += 20; // Add 20 points for the same department
        }

        return Math.min(score, 100.0);
    }
}
