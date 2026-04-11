package service;

import model.*;
import java.util.List;
import java.util.UUID;

public class JobService {
    // Create and publish a new job (for MO)
    public static Job createJob(String title, model.JobType type, String department, String description,
                               List<String> skills, String workTime, int recruitNum, String deadline,
                               String salary, String location, String extraRequirements, String moId) {
        return createJob(title, type, department, description, skills, workTime, recruitNum, deadline,
                        salary, location, extraRequirements, moId, "MO", null);
    }

    // Create and publish a new job (generic method, supports MO and Admin)
    public static Job createJob(String title, model.JobType type, String department, String description,
                               List<String> skills, String workTime, int recruitNum, String deadline,
                               String salary, String location, String extraRequirements,
                               String publisherId, String publisherType, String publisherName) {
        String id = UUID.randomUUID().toString();
        Job job = new Job(id, title, type, department, description, skills, workTime, recruitNum, deadline, publisherId);
        job.setSalary(salary);
        job.setLocation(location);
        job.setExtraRequirements(extraRequirements);
        job.setPublisherId(publisherId);
        job.setPublisherType(publisherType);
        job.setPublisherName(publisherName);
        job.setStatus(model.JobStatus.PENDING);

        List<Job> jobs = DataStorage.getJobs();
        jobs.add(job);
        DataStorage.saveJobs(jobs);
        DataStorage.addLog("CREATE_JOB", publisherId, "Job created by " + publisherType + ": " + title);

        return job;
    }

    public static List<Job> getAllJobsByMO(String moId) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        for (Job job : jobs) {
            if (job.getMoId() != null && job.getMoId().equals(moId)) {
                result.add(job);
            }
        }
        return result;
    }

    public static boolean updateJob(Job job) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(job.getId())) {
                job.setUpdatedAt(java.time.LocalDateTime.now().toString());
                jobs.set(i, job);
                DataStorage.saveJobs(jobs);
                DataStorage.addLog("UPDATE_JOB", job.getMoId(), "Job updated: " + job.getTitle());
                return true;
            }
        }
        return false;
    }

    public static boolean updateJobByMO(Job job, String moId) {
        if (job == null || moId == null || !moId.equals(job.getMoId())) {
            return false;
        }
        return updateJob(job);
    }

    public static boolean submitJobForReview(String jobId) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(jobId)) {
                Job job = jobs.get(i);
                job.setStatus(model.JobStatus.PENDING);
                job.setUpdatedAt(java.time.LocalDateTime.now().toString());
                jobs.set(i, job);
                DataStorage.saveJobs(jobs);
                DataStorage.addLog("SUBMIT_JOB_REVIEW", job.getMoId(), "Job submitted for review: " + job.getTitle());
                return true;
            }
        }
        return false;
    }

    public static boolean reviewJob(String jobId, model.JobStatus status, String comment, String adminId) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(jobId)) {
                Job job = jobs.get(i);
                job.setStatus(status);
                job.setReviewedBy(adminId);
                job.setReviewTime(java.time.LocalDateTime.now().toString());
                job.setReviewComment(comment);
                job.setUpdatedAt(java.time.LocalDateTime.now().toString());
                jobs.set(i, job);
                DataStorage.saveJobs(jobs);
                DataStorage.addLog("REVIEW_JOB", adminId, "Job reviewed: " + job.getTitle() + " - " + status);
                return true;
            }
        }
        return false;
    }

    public static boolean closeJob(String jobId, String moId) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(jobId)) {
                Job job = jobs.get(i);
                if (job.getMoId() == null || !job.getMoId().equals(moId)) {
                    return false;
                }
                job.setStatus(model.JobStatus.CLOSED);
                job.setUpdatedAt(java.time.LocalDateTime.now().toString());
                jobs.set(i, job);
                DataStorage.saveJobs(jobs);
                DataStorage.addLog("CLOSE_JOB", moId, "Job closed: " + job.getTitle());
                return true;
            }
        }
        return false;
    }

    public static Job getJobById(String jobId) {
        List<Job> jobs = DataStorage.getJobs();
        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    public static List<Job> getJobsByMO(String moId) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        for (Job job : jobs) {
            if (job.getMoId() != null && job.getMoId().equals(moId)) {
                result.add(job);
            }
        }
        return result;
    }

    public static List<Job> getAllJobs() {
        return DataStorage.getJobs();
    }

    public static List<Job> getAvailableJobs() {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Job job : jobs) {
            if (job.getStatus() != model.JobStatus.PUBLISHED) {
                continue;
            }
            if (!isJobStillAvailable(job, today)) {
                continue;
            }
            result.add(job);
        }
        return result;
    }

    private static boolean isJobStillAvailable(Job job, java.time.LocalDate today) {
        if (job == null || job.getDeadline() == null || job.getDeadline().trim().isEmpty() || "null".equalsIgnoreCase(job.getDeadline().trim())) {
            return false;
        }

        String deadline = job.getDeadline().trim();
        try {
            java.time.LocalDate deadlineDate = java.time.LocalDate.parse(deadline);
            return !deadlineDate.isBefore(today);
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        try {
            java.time.LocalDateTime deadlineDateTime = java.time.LocalDateTime.parse(deadline);
            return !deadlineDateTime.toLocalDate().isBefore(today);
        } catch (java.time.format.DateTimeParseException ignored) {
        }

        String[] parts = deadline.split("-");
        if (parts.length == 3) {
            try {
                java.time.LocalDate deadlineDate = java.time.LocalDate.of(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2])
                );
                return !deadlineDate.isBefore(today);
            } catch (NumberFormatException | java.time.DateTimeException ignored) {
            }
        }

        return false;
    }

    public static List<Job> filterJobs(model.JobType type, String department, String deadline) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Job job : jobs) {
            if (job.getStatus() != model.JobStatus.PUBLISHED || !isJobStillAvailable(job, today)) {
                continue;
            }

            boolean match = true;
            if (type != null && job.getType() != type) {
                match = false;
            }
            if (department != null && !job.getDepartment().equals(department)) {
                match = false;
            }
            if (deadline != null && !deadline.trim().isEmpty() && job.getDeadline().compareTo(deadline) < 0) {
                match = false;
            }
            if (match) {
                result.add(job);
            }
        }
        return result;
    }

    public static List<Job> searchJobs(String keyword) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        java.time.LocalDate today = java.time.LocalDate.now();
        for (Job job : jobs) {
            if (job.getStatus() == model.JobStatus.PUBLISHED && isJobStillAvailable(job, today)) {
                if (job.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                    job.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    result.add(job);
                }
            }
        }
        return result;
    }
}
