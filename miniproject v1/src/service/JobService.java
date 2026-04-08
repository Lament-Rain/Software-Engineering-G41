package service;

import model.*;
import java.util.List;
import java.util.UUID;

public class JobService {
    // 发布新职位（MO使用）
    public static Job createJob(String title, model.JobType type, String department, String description,
                               List<String> skills, String workTime, int recruitNum, String deadline,
                               String salary, String location, String extraRequirements, String moId) {
        return createJob(title, type, department, description, skills, workTime, recruitNum, deadline,
                        salary, location, extraRequirements, moId, "MO", null);
    }

    // 发布新职位（通用方法，支持MO和Admin）
    public static Job createJob(String title, model.JobType type, String department, String description,
                               List<String> skills, String workTime, int recruitNum, String deadline,
                               String salary, String location, String extraRequirements,
                               String publisherId, String publisherType, String publisherName) {
        // 创建职位
        String id = UUID.randomUUID().toString();
        Job job = new Job(id, title, type, department, description, skills, workTime, recruitNum, deadline, publisherId);
        job.setSalary(salary);
        job.setLocation(location);
        job.setExtraRequirements(extraRequirements);
        job.setPublisherId(publisherId);
        job.setPublisherType(publisherType);
        job.setPublisherName(publisherName);
        // 设置职位状态为已发布
        job.setStatus(model.JobStatus.PUBLISHED);

        // 保存职位
        List<Job> jobs = DataStorage.getJobs();
        jobs.add(job);
        DataStorage.saveJobs(jobs);
        DataStorage.addLog("CREATE_JOB", publisherId, "Job created by " + publisherType + ": " + title);

        return job;
    }
    
    // 获取MO发布的所有职位（包括所有状态）
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

    // 更新职位
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

    // 提交职位审核
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

    // 审核职位
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

    // 终止职位
    public static boolean closeJob(String jobId, String moId) {
        List<Job> jobs = DataStorage.getJobs();
        for (int i = 0; i < jobs.size(); i++) {
            if (jobs.get(i).getId().equals(jobId)) {
                Job job = jobs.get(i);
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

    // 获取职位详情
    public static Job getJobById(String jobId) {
        List<Job> jobs = DataStorage.getJobs();
        for (Job job : jobs) {
            if (job.getId().equals(jobId)) {
                return job;
            }
        }
        return null;
    }

    // 获取MO发布的职位
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

    // 获取所有职位
    public static List<Job> getAllJobs() {
        return DataStorage.getJobs();
    }

    // 获取可申请的职位（已发布且未截止）
    public static List<Job> getAvailableJobs() {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        String now = java.time.LocalDateTime.now().toString();
        for (Job job : jobs) {
            if (job.getStatus() == model.JobStatus.PUBLISHED && job.getDeadline().compareTo(now) > 0) {
                result.add(job);
            }
        }
        return result;
    }

    // 按条件筛选职位
    public static List<Job> filterJobs(model.JobType type, String department, String deadline) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        String now = java.time.LocalDateTime.now().toString();
        for (Job job : jobs) {
            // 只筛选已发布且未截止的职位
            if (job.getStatus() != model.JobStatus.PUBLISHED || job.getDeadline().compareTo(now) <= 0) {
                continue;
            }
            
            boolean match = true;
            if (type != null && job.getType() != type) {
                match = false;
            }
            if (department != null && !job.getDepartment().equals(department)) {
                match = false;
            }
            if (deadline != null && job.getDeadline().compareTo(deadline) < 0) {
                match = false;
            }
            if (match) {
                result.add(job);
            }
        }
        return result;
    }
    
    // 搜索职位
    public static List<Job> searchJobs(String keyword) {
        List<Job> jobs = DataStorage.getJobs();
        List<Job> result = new java.util.ArrayList<>();
        String now = java.time.LocalDateTime.now().toString();
        for (Job job : jobs) {
            // 只搜索已发布且未截止的职位
            if (job.getStatus() == model.JobStatus.PUBLISHED && job.getDeadline().compareTo(now) > 0) {
                if (job.getTitle().toLowerCase().contains(keyword.toLowerCase()) || 
                    job.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                    result.add(job);
                }
            }
        }
        return result;
    }
} 
