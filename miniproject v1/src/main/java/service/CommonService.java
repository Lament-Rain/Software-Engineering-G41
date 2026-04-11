package service;

import model.*;
import java.io.*;
import java.util.*;

public class CommonService {
    // 导出数据为CSV格式
    public static boolean exportToCSV(String dataType, String filePath) {
        try {
            FileWriter writer = new FileWriter(filePath);
            
            switch (dataType.toLowerCase()) {
                case "users":
                    exportUsersToCSV(writer);
                    break;
                case "jobs":
                    exportJobsToCSV(writer);
                    break;
                case "applications":
                    exportApplicationsToCSV(writer);
                    break;
                default:
                    writer.close();
                    return false;
            }
            
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // 导出用户数据为CSV
    private static void exportUsersToCSV(FileWriter writer) throws IOException {
        writer.write("ID,Username,Email,Phone,Role,Status,Department\n");
        List<User> users = UserService.getAllUsers();
        for (User user : users) {
            String department = "";
            if (user instanceof MO) {
                department = ((MO) user).getDepartment();
            } else if (user instanceof TA) {
                department = ((TA) user).getDepartment();
            }
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getPhone(),
                    user.getRole(),
                    user.getStatus(),
                    department));
        }
    }

    // 导出职位数据为CSV
    private static void exportJobsToCSV(FileWriter writer) throws IOException {
        writer.write("ID,Title,Type,Department,Description,Skills,WorkTime,RecruitNum,Deadline,Status\n");
        List<Job> jobs = JobService.getAllJobs();
        for (Job job : jobs) {
            String skills = String.join(";", job.getSkills());
            writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%d,%s,%s\n",
                    job.getId(),
                    job.getTitle(),
                    job.getType(),
                    job.getDepartment(),
                    job.getDescription(),
                    skills,
                    job.getWorkTime(),
                    job.getRecruitNum(),
                    job.getDeadline(),
                    job.getStatus()));
        }
    }

    // 导出申请数据为CSV
    private static void exportApplicationsToCSV(FileWriter writer) throws IOException {
        writer.write("ID,TA ID,Job ID,Cover Letter,Status,Match Score,Created At\n");
        List<Application> applications = ApplicationService.getAllApplications();
        for (Application app : applications) {
            writer.write(String.format("%s,%s,%s,%s,%s,%.2f,%s\n",
                    app.getId(),
                    app.getTaId(),
                    app.getJobId(),
                    app.getCoverLetter(),
                    app.getStatus(),
                    app.getMatchScore(),
                    app.getCreatedAt()));
        }
    }

    // 系统搜索
    public static Map<String, List<?>> search(String keyword, List<String> searchTypes) {
        Map<String, List<?>> results = new HashMap<>();

        if (searchTypes.contains("users")) {
            List<User> userResults = searchUsers(keyword);
            results.put("users", userResults);
        }

        if (searchTypes.contains("jobs")) {
            List<Job> jobResults = searchJobs(keyword);
            results.put("jobs", jobResults);
        }

        if (searchTypes.contains("applications")) {
            List<Application> applicationResults = searchApplications(keyword);
            results.put("applications", applicationResults);
        }

        return results;
    }

    // 搜索用户
    private static List<User> searchUsers(String keyword) {
        List<User> users = UserService.getAllUsers();
        List<User> results = new ArrayList<>();
        for (User user : users) {
            if (user.getUsername().contains(keyword) ||
                user.getEmail().contains(keyword) ||
                user.getPhone().contains(keyword)) {
                results.add(user);
            }
        }
        return results;
    }

    // 搜索职位
    private static List<Job> searchJobs(String keyword) {
        List<Job> jobs = JobService.getAllJobs();
        List<Job> results = new ArrayList<>();
        for (Job job : jobs) {
            if (job.getTitle().contains(keyword) ||
                job.getDescription().contains(keyword) ||
                job.getSkills().stream().anyMatch(skill -> skill.contains(keyword))) {
                results.add(job);
            }
        }
        return results;
    }

    // 搜索申请
    private static List<Application> searchApplications(String keyword) {
        List<Application> applications = ApplicationService.getAllApplications();
        List<Application> results = new ArrayList<>();
        for (Application app : applications) {
            if (app.getCoverLetter().contains(keyword)) {
                results.add(app);
            }
        }
        return results;
    }

    // 生成系统报告
    public static String generateSystemReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== BUPT国际学校TA招聘系统报告 ===\n\n");

        // 用户统计
        List<User> users = UserService.getAllUsers();
        int taCount = 0, moCount = 0, adminCount = 0;
        for (User user : users) {
            switch (user.getRole()) {
                case TA:
                    taCount++;
                    break;
                case MO:
                    moCount++;
                    break;
                case ADMIN:
                    adminCount++;
                    break;
            }
        }
        report.append("用户统计：\n");
        report.append("- TA数量：").append(taCount).append("\n");
        report.append("- MO数量：").append(moCount).append("\n");
        report.append("- 管理员数量：").append(adminCount).append("\n\n");

        // 职位统计
        List<Job> jobs = JobService.getAllJobs();
        int draftJobs = 0, pendingJobs = 0, publishedJobs = 0, closedJobs = 0;
        for (Job job : jobs) {
            switch (job.getStatus()) {
                case DRAFT:
                    draftJobs++;
                    break;
                case PENDING:
                    pendingJobs++;
                    break;
                case PUBLISHED:
                    publishedJobs++;
                    break;
                case CLOSED:
                    closedJobs++;
                    break;
            }
        }
        report.append("职位统计：\n");
        report.append("- 草稿：").append(draftJobs).append("\n");
        report.append("- 待审核：").append(pendingJobs).append("\n");
        report.append("- 已发布：").append(publishedJobs).append("\n");
        report.append("- 已关闭：").append(closedJobs).append("\n\n");

        // 申请统计
        List<Application> applications = ApplicationService.getAllApplications();
        int pendingApps = 0, screenedApps = 0, acceptedApps = 0, rejectedApps = 0;
        for (Application app : applications) {
            switch (app.getStatus()) {
                case PENDING:
                    pendingApps++;
                    break;
                case SCREENED:
                    screenedApps++;
                    break;
                case ACCEPTED:
                    acceptedApps++;
                    break;
                case REJECTED:
                    rejectedApps++;
                    break;
            }
        }
        report.append("申请统计：\n");
        report.append("- 待处理：").append(pendingApps).append("\n");
        report.append("- 已筛选：").append(screenedApps).append("\n");
        report.append("- 已录用：").append(acceptedApps).append("\n");
        report.append("- 已拒绝：").append(rejectedApps).append("\n\n");

        report.append("报告生成时间：").append(java.time.LocalDateTime.now().toString());
        return report.toString();
    }
}
