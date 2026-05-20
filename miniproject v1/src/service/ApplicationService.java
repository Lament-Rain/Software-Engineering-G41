package service;

import model.*;
import java.util.List;
import java.util.UUID;
<<<<<<< Updated upstream
=======
import java.util.stream.Collectors;
import java.util.Set;
import java.util.HashSet;
<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes

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

        int currentNumBaseline = Math.max(job.getCurrentNum(), acceptedCount);
        if (currentNumBaseline >= job.getRecruitNum()) {
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
<<<<<<< Updated upstream
<<<<<<< Updated upstream
=======
=======
>>>>>>> Stashed changes
        job.setCurrentNum(currentNumBaseline + 1);
        JobService.updateJob(job);
        IndexService.indexApplicationUpdate(targetApp);
        markAcceptedJobBusySlots(targetApp.getTaId(), job);
        invalidateApplicationCaches(targetApp);
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
=======
    private static void invalidateApplicationCaches(Application app) {
        CacheService.invalidateByPattern(CacheService.jobApplicationsKey(app.getJobId()));
        CacheService.invalidateByPattern(CacheService.taApplicationsKey(app.getTaId()));
        Job job = JobService.getJobById(app.getJobId());
        if (job != null) {
            CacheService.invalidateByPattern(CacheService.pendingApplicationsKey(job.getMoId()));
        }
    }

    public static int batchScreenApplications(List<String> applicationIds, model.ApplicationStatus status, String comment, String moId) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return 0;
        }

        List<Application> applications = DataStorage.getApplications();
        Map<String, Application> appMap = applications.stream()
                .collect(Collectors.toMap(Application::getId, a -> a));

        List<Application> toUpdate = applicationIds.stream()
                .map(appMap::get)
                .filter(app -> app != null && (app.getStatus() == model.ApplicationStatus.PENDING ||
                                                app.getStatus() == model.ApplicationStatus.SCREENED))
                .map(app -> {
                    app.setStatus(status);
                    app.setReviewedBy(moId);
                    app.setReviewTime(java.time.LocalDateTime.now().toString());
                    app.setReviewComment(comment);
                    app.setUpdatedAt(java.time.LocalDateTime.now().toString());
                    return app;
                })
                .collect(Collectors.toList());

        if (toUpdate.isEmpty()) {
            return 0;
        }

        DataStorage.batchUpdateApplications(toUpdate);
        toUpdate.forEach(app -> {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                Job acceptedJob = JobService.getJobById(app.getJobId());
                if (acceptedJob != null) {
                    markAcceptedJobBusySlots(app.getTaId(), acceptedJob);
                }
            }
            invalidateApplicationCaches(app);
        });
        DataStorage.addLog("BATCH_SCREEN_APPLICATION", moId, "Batch screened " + toUpdate.size() + " applications");
        return toUpdate.size();
    }

    public static int batchRejectApplications(List<String> applicationIds, String reason, String moId) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return 0;
        }

        List<Application> applications = DataStorage.getApplications();
        Map<String, Application> appMap = applications.stream()
                .collect(Collectors.toMap(Application::getId, a -> a));

        List<Application> toUpdate = applicationIds.stream()
                .map(appMap::get)
                .filter(app -> app != null && app.getStatus() != model.ApplicationStatus.REJECTED &&
                               app.getStatus() != model.ApplicationStatus.ACCEPTED &&
                               app.getStatus() != model.ApplicationStatus.WITHDRAWN)
                .map(app -> {
                    app.setStatus(model.ApplicationStatus.REJECTED);
                    app.setReviewedBy(moId);
                    app.setReviewTime(java.time.LocalDateTime.now().toString());
                    app.setReviewComment(reason);
                    app.setUpdatedAt(java.time.LocalDateTime.now().toString());
                    return app;
                })
                .collect(Collectors.toList());

        if (toUpdate.isEmpty()) {
            return 0;
        }

        DataStorage.batchUpdateApplications(toUpdate);
        toUpdate.forEach(app -> {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                Job acceptedJob = JobService.getJobById(app.getJobId());
                if (acceptedJob != null) {
                    markAcceptedJobBusySlots(app.getTaId(), acceptedJob);
                }
            }
            invalidateApplicationCaches(app);
        });
        DataStorage.addLog("BATCH_REJECT_APPLICATION", moId, "Batch rejected " + toUpdate.size() + " applications");
        return toUpdate.size();
    }

    public static int batchAcceptApplications(List<String> applicationIds, String moId) {
        if (applicationIds == null || applicationIds.isEmpty()) {
            return 0;
        }

        List<Application> applications = DataStorage.getApplications();
        Map<String, Application> appMap = applications.stream()
                .collect(Collectors.toMap(Application::getId, a -> a));

        List<Application> toUpdate = new java.util.ArrayList<>();
        java.util.Map<String, Integer> acceptedDeltaByJob = new java.util.HashMap<>();
        for (String appId : applicationIds) {
            Application app = appMap.get(appId);
            if (app == null) {
                continue;
            }

            if (app.getStatus() != model.ApplicationStatus.PENDING &&
                app.getStatus() != model.ApplicationStatus.SCREENED) {
                continue;
            }

            Job job = JobService.getJobById(app.getJobId());
            if (job == null) {
                continue;
            }

            long acceptedCount = applications.stream()
                    .filter(a -> a.getJobId().equals(app.getJobId()) && a.getStatus() == model.ApplicationStatus.ACCEPTED)
                    .count();

            int pendingDelta = acceptedDeltaByJob.getOrDefault(app.getJobId(), 0);
            long baseline = Math.max((long) job.getCurrentNum(), acceptedCount) + pendingDelta;
            if (baseline >= job.getRecruitNum()) {
                continue;
            }

            app.setStatus(model.ApplicationStatus.ACCEPTED);
            app.setReviewedBy(moId);
            app.setReviewTime(java.time.LocalDateTime.now().toString());
            app.setUpdatedAt(java.time.LocalDateTime.now().toString());
            toUpdate.add(app);
            acceptedDeltaByJob.put(app.getJobId(), pendingDelta + 1);
        }

        if (toUpdate.isEmpty()) {
            return 0;
        }

        DataStorage.batchUpdateApplications(toUpdate);

        for (Map.Entry<String, Integer> entry : acceptedDeltaByJob.entrySet()) {
            Job job = JobService.getJobById(entry.getKey());
            if (job != null) {
                int next = Math.max(job.getCurrentNum(), 0) + entry.getValue();
                job.setCurrentNum(Math.min(next, job.getRecruitNum()));
                JobService.updateJob(job);
            }
        }

        toUpdate.forEach(app -> {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                Job acceptedJob = JobService.getJobById(app.getJobId());
                if (acceptedJob != null) {
                    markAcceptedJobBusySlots(app.getTaId(), acceptedJob);
                }
            }
            invalidateApplicationCaches(app);
        });
        DataStorage.addLog("BATCH_ACCEPT_APPLICATION", moId, "Batch accepted " + toUpdate.size() + " applications");
        return toUpdate.size();
    }

    private static void markAcceptedJobBusySlots(String taId, Job job) {
        TA ta = UserService.getTAProfile(taId);
        if (ta == null || job == null) {
            return;
        }

        String workTime = job.getWorkTime();
        if (workTime == null || !workTime.startsWith("PERIODS|")) {
            return;
        }

        String[] schedule = toScheduleArray(ta.getAvailableTime());
        applyJobPeriodsToSchedule(schedule, workTime.substring("PERIODS|".length()));
        ta.setAvailableTime("SCHEDULE_V1|" + String.join(",", schedule));
        UserService.updateTAProfile(ta);
    }

    private static String[] toScheduleArray(String availableTime) {
        String[] schedule = new String[98];
        java.util.Arrays.fill(schedule, "free");
        if (availableTime == null || !availableTime.startsWith("SCHEDULE_V1|")) {
            return schedule;
        }

        String[] parts = availableTime.substring("SCHEDULE_V1|".length()).split(",");
        if (parts.length != 98) {
            return schedule;
        }

        for (int i = 0; i < 98; i++) {
            String s = parts[i] == null ? "free" : parts[i].trim().toLowerCase();
            if (!"free".equals(s) && !"occupied".equals(s) && !"busy".equals(s)) {
                s = "free";
            }
            schedule[i] = s;
        }
        return schedule;
    }

    private static void applyJobPeriodsToSchedule(String[] schedule, String payload) {
        if (payload == null || payload.trim().isEmpty()) {
            return;
        }

        String[] dayEntries = payload.split(";");
        for (String entry : dayEntries) {
            if (entry == null || entry.trim().isEmpty() || !entry.contains(":")) {
                continue;
            }

            String[] kv = entry.split(":", 2);
            int dayIndex = dayToIndex(kv[0].trim());
            if (dayIndex < 0) {
                continue;
            }

            Set<Integer> periods = parsePeriodSet(kv[1]);
            for (Integer period : periods) {
                int row = period - 1;
                int idx = row * 7 + dayIndex;
                if (idx >= 0 && idx < schedule.length) {
                    schedule[idx] = "busy";
                }
            }
        }
    }

    private static int dayToIndex(String day) {
        switch (day) {
            case "Sun": return 0;
            case "Mon": return 1;
            case "Tue": return 2;
            case "Wed": return 3;
            case "Thu": return 4;
            case "Fri": return 5;
            case "Sat": return 6;
            default: return -1;
        }
    }

    private static Set<Integer> parsePeriodSet(String raw) {
        Set<Integer> result = new HashSet<>();
        if (raw == null || raw.trim().isEmpty()) {
            return result;
        }
        String[] parts = raw.split(",");
        for (String part : parts) {
            String value = part.trim();
            if (!value.matches("^\\d+$")) {
                continue;
            }
            int p = Integer.parseInt(value);
            if (p >= 1 && p <= 14) {
                result.add(p);
            }
        }
        return result;
    }

<<<<<<< Updated upstream
>>>>>>> Stashed changes
=======
>>>>>>> Stashed changes
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

        double baseScore = Math.min(score, 100.0);
        return baseScore * timeConflictWeight(ta, job);
    }

    private static double timeConflictWeight(TA ta, Job job) {
        return hasTimeConflict(ta, job) ? 0.5 : 1.0;
    }

    private static boolean hasTimeConflict(TA ta, Job job) {
        if (ta == null || job == null || ta.getAvailableTime() == null || job.getWorkTime() == null) {
            return false;
        }
        if (!ta.getAvailableTime().startsWith("SCHEDULE_V1|") || !job.getWorkTime().startsWith("PERIODS|")) {
            return false;
        }

        String[] schedule = ta.getAvailableTime().substring("SCHEDULE_V1|".length()).split(",");
        if (schedule.length != 98) {
            return false;
        }

        String[] entries = job.getWorkTime().substring("PERIODS|".length()).split(";");
        for (String e : entries) {
            if (e == null || e.trim().isEmpty() || !e.contains(":")) continue;
            String[] kv = e.split(":", 2);
            int day = dayToIndex(kv[0].trim());
            if (day < 0) continue;

            Set<Integer> periods = parsePeriodSet(kv[1]);
            for (Integer period : periods) {
                int idx = (period - 1) * 7 + day;
                if (idx >= 0 && idx < schedule.length) {
                    String status = schedule[idx].trim().toLowerCase();
                    if ("occupied".equals(status) || "busy".equals(status)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static double calculateMatchScoreForReview(String taId, String jobId) {
        TA ta = UserService.getTAProfile(taId);
        Job job = JobService.getJobById(jobId);
        if (ta == null || job == null) {
            return 0.0;
        }
        return calculateMatchScore(ta, job);
    }
}
