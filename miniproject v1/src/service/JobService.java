package service;

import model.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class JobService {
    private static final long CACHE_TTL_SHORT = 60 * 1000;
    private static final long CACHE_TTL_MEDIUM = 5 * 60 * 1000;

    public static Job createJob(String title, model.JobType type, String department, String description,
                               List<String> skills, String workTime, int recruitNum, String deadline,
                               String salary, String location, String extraRequirements, String moId) {
        return createJob(title, type, department, description, skills, workTime, recruitNum, deadline,
                        salary, location, extraRequirements, moId, "MO", null);
    }

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
        IndexService.indexJobUpdate(job);
        CacheService.invalidate(CacheService.availableJobsKey());
        DataStorage.addLog("CREATE_JOB", publisherId, "Job created by " + publisherType + ": " + title);

        return job;
    }

    public static List<Job> getAllJobsByMO(String moId) {
        String cacheKey = "jobs_by_mo_" + moId;
        return CacheService.getOrCompute(cacheKey, () -> {
            return IndexService.getJobsByMoId(moId);
        }, CACHE_TTL_SHORT);
    }

    public static boolean updateJob(Job job) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(job.getId())) {
                job.setUpdatedAt(java.time.LocalDateTime.now().toString());
                jobs.set(i, job);
                DataStorage.saveJobs(jobs);
                IndexService.indexJobUpdate(job);
                CacheService.invalidate(CacheService.availableJobsKey());
                CacheService.invalidateByPattern("jobs_by_mo_" + job.getMoId());
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
                IndexService.indexJobUpdate(job);
                CacheService.invalidate(CacheService.availableJobsKey());
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
                IndexService.indexJobUpdate(job);
                CacheService.invalidate(CacheService.availableJobsKey());
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
                IndexService.indexJobUpdate(job);
                CacheService.invalidate(CacheService.availableJobsKey());
                DataStorage.addLog("CLOSE_JOB", moId, "Job closed: " + job.getTitle());
                return true;
            }
        }
        return false;
    }

    public static Job getJobById(String jobId) {
        return DataStorage.getJobs().stream()
                .filter(job -> job.getId().equals(jobId))
                .findFirst()
                .orElse(null);
    }

    public static List<Job> getJobsByMO(String moId) {
        String cacheKey = "jobs_by_mo_" + moId;
        return CacheService.getOrCompute(cacheKey, () -> IndexService.getJobsByMoId(moId), CACHE_TTL_SHORT);
    }

    public static List<Job> getAllJobs() {
        return DataStorage.getJobs();
    }

    public static PaginationUtil.Page<Job> getAllJobsPaged(int page, int size) {
        String cacheKey = "all_jobs_paged_" + page + "_" + size;
        return CacheService.getOrCompute(cacheKey, () -> {
            return PaginationUtil.paginate(DataStorage.getJobs(), page, size);
        }, CACHE_TTL_SHORT);
    }

    public static List<Job> getAvailableJobs() {
        return CacheService.getOrCompute(CacheService.availableJobsKey(), () -> {
            List<Job> jobs = DataStorage.getJobs();
            java.time.LocalDate today = java.time.LocalDate.now();
            return jobs.stream()
                    .filter(job -> job.getStatus() == model.JobStatus.PUBLISHED)
                    .filter(job -> isJobStillAvailable(job, today))
                    .collect(Collectors.toList());
        }, CACHE_TTL_MEDIUM);
    }

    public static PaginationUtil.Page<Job> getAvailableJobsPaged(int page, int size) {
        String cacheKey = "available_jobs_paged_" + page + "_" + size;
        return CacheService.getOrCompute(cacheKey, () -> {
            return PaginationUtil.paginate(getAvailableJobs(), page, size);
        }, CACHE_TTL_MEDIUM);
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
        return getAvailableJobs().stream()
                .filter(job -> type == null || job.getType() == type)
                .filter(job -> department == null || job.getDepartment().equals(department))
                .filter(job -> deadline == null || deadline.trim().isEmpty() || job.getDeadline().compareTo(deadline) >= 0)
                .collect(Collectors.toList());
    }

    public static List<Job> searchJobs(String keyword) {
        return getAvailableJobs().stream()
                .filter(job -> job.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                              job.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static List<Job> getPendingJobs() {
        String cacheKey = "pending_jobs";
        return CacheService.getOrCompute(cacheKey, () -> IndexService.getJobsByStatus(JobStatus.PENDING), CACHE_TTL_SHORT);
    }
}
