package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import model.*;

public class DataStorage {
    private static List<User> users = new ArrayList<>();
    private static List<Job> jobs = new ArrayList<>();
    private static List<Application> applications = new ArrayList<>();
    private static List<Log> logs = new ArrayList<>();

    private static final String USERS_FILE = "src/data/users.txt";
    private static final String JOBS_FILE = "src/data/jobs.txt";
    private static final String APPLICATIONS_FILE = "src/data/applications.txt";
    private static final String LOGS_FILE = "src/data/logs.txt";

    // 初始化数据存储
    public static void initialize() {
        try {
            // 确保数据目录存在
            File dataDir = new File("src/data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // 加载数据
            loadUsers();
            loadJobs();
            loadApplications();
            loadLogs();

            // 如果没有用户，添加默认管理员账号
            if (users.isEmpty()) {
                Admin admin = new Admin("admin1", "admin", "admin123", "admin@bupt.edu.cn", "13800138000", model.AdminLevel.SUPER);
                users.add(admin);
                saveUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 加载用户数据
    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 解析用户数据
                if (line.startsWith("Admin")) {
                    // 解析Admin用户
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = line.split("password='")[1].split("'")[0];
                    }
                    String level = line.split("level=")[1].split("}")[0];
                    // 创建Admin对象
                    Admin admin = new Admin(id, username, password, username + "@bupt.edu.cn", "13800138000", AdminLevel.valueOf(level));
                    users.add(admin);
                } else if (line.startsWith("TA")) {
                // 解析TA用户
                String id = line.split("id='")[1].split("'" )[0];
                String username = line.split("username='")[1].split("'" )[0];
                String password = "123456";
                if (line.contains("password='")) {
                    password = line.split("password='")[1].split("'" )[0];
                }
                String email = username + "@bupt.edu.cn";
                if (line.contains("email='")) {
                    email = line.split("email='")[1].split("'" )[0];
                }
                String phone = "13800138000";
                if (line.contains("phone='")) {
                    phone = line.split("phone='")[1].split("'" )[0];
                }
                // 创建TA对象
                TA ta = new TA(id, username, password, email, phone);
                
                // 解析其他字段
                if (line.contains("name='")) {
                    String name = line.split("name='")[1].split("'" )[0];
                    ta.setName(name);
                }
                if (line.contains("gender='")) {
                    String gender = line.split("gender='")[1].split("'" )[0];
                    ta.setGender(gender);
                }
                if (line.contains("age=")) {
                    String ageStr = line.split("age=")[1].split("," )[0];
                    try {
                        int age = Integer.parseInt(ageStr);
                        ta.setAge(age);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
                if (line.contains("department='")) {
                    String department = line.split("department='")[1].split("'" )[0];
                    ta.setDepartment(department);
                }
                if (line.contains("grade='")) {
                    String grade = line.split("grade='")[1].split("'" )[0];
                    ta.setGrade(grade);
                }
                if (line.contains("studentId='")) {
                    String studentId = line.split("studentId='")[1].split("'" )[0];
                    ta.setStudentId(studentId);
                }
                if (line.contains("availableTime='")) {
                    String availableTime = line.split("availableTime='")[1].split("'" )[0];
                    ta.setAvailableTime(availableTime);
                }
                if (line.contains("skills=")) {
                    String skillsStr = line.split("skills=")[1].split("," )[0];
                    // 简单处理，实际需要更复杂的解析
                    java.util.List<String> skills = new java.util.ArrayList<>();
                    if (!skillsStr.equals("null")) {
                        skillsStr = skillsStr.replaceAll("\\[|\\]", "");
                        String[] skillArray = skillsStr.split(", ");
                        for (String skill : skillArray) {
                            skills.add(skill);
                        }
                    }
                    ta.setSkills(skills);
                }
                if (line.contains("experience='")) {
                    String experience = line.split("experience='")[1].split("'" )[0];
                    ta.setExperience(experience);
                }
                if (line.contains("awards='")) {
                    String awards = line.split("awards='")[1].split("'" )[0];
                    ta.setAwards(awards);
                }
                if (line.contains("languageSkills='")) {
                    String languageSkills = line.split("languageSkills='")[1].split("'" )[0];
                    ta.setLanguageSkills(languageSkills);
                }
                if (line.contains("otherSkills='")) {
                    String otherSkills = line.split("otherSkills='")[1].split("'" )[0];
                    ta.setOtherSkills(otherSkills);
                }
                if (line.contains("resumePath='")) {
                    String resumePath = line.split("resumePath='")[1].split("'" )[0];
                    ta.setResumePath(resumePath);
                }
                if (line.contains("profileStatus=")) {
                    String profileStatusStr = line.split("profileStatus=")[1].split("," )[0];
                    // 移除可能的多余字符，如 '}'
                    profileStatusStr = profileStatusStr.replaceAll("[}\s]", "");
                    try {
                        model.ProfileStatus profileStatus = model.ProfileStatus.valueOf(profileStatusStr);
                        ta.setProfileStatus(profileStatus);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        // 如果解析失败，设置为默认值
                        ta.setProfileStatus(model.ProfileStatus.DRAFT);
                    }
                }
                if (line.contains("profileUpdatedAt='")) {
                    String profileUpdatedAt = line.split("profileUpdatedAt='")[1].split("'" )[0];
                    ta.setProfileUpdatedAt(profileUpdatedAt);
                }
                
                users.add(ta);
                } else if (line.startsWith("MO")) {
                    // 解析MO用户
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = line.split("password='")[1].split("'")[0];
                    }
                    String department = "null";
                    if (line.contains("department='")) {
                        department = line.split("department='")[1].split("'")[0];
                    }
                    // 创建MO对象
                    MO mo = new MO(id, username, password, username + "@bupt.edu.cn", "13800138000", department);
                    users.add(mo);
                }
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存用户数据
    private static void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            for (User user : users) {
                writer.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载职位数据
    private static void loadJobs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JOBS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Job{")) {
                    // 解析Job对象
                    try {
                        Job job = new Job();
                        
                        // 解析id
                        if (line.contains("id='")) {
                            String id = line.split("id='")[1].split("'" )[0];
                            job.setId(id);
                        }
                        
                        // 解析title
                        if (line.contains("title='")) {
                            String title = line.split("title='")[1].split("'" )[0];
                            job.setTitle(title);
                        }
                        
                        // 解析type
                        if (line.contains("type=")) {
                            String typeStr = line.split("type=")[1].split("," )[0];
                            // 清理字符串，去除可能的额外字符
                            typeStr = typeStr.replaceAll("[^A-Z_]", "");
                            try {
                                model.JobType type = model.JobType.valueOf(typeStr);
                                job.setType(type);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // 解析department
                        if (line.contains("department='")) {
                            String department = line.split("department='")[1].split("'" )[0];
                            job.setDepartment(department);
                        }
                        
                        // 解析description
                        if (line.contains("description='")) {
                            String description = line.split("description='")[1].split("'" )[0];
                            job.setDescription(description);
                        }
                        
                        // 解析skills
                        if (line.contains("skills=")) {
                            String skillsStr = line.split("skills=")[1].split("," )[0];
                            java.util.List<String> skills = new java.util.ArrayList<>();
                            if (!skillsStr.equals("null")) {
                                skillsStr = skillsStr.replaceAll("\\[|\\]", "");
                                String[] skillArray = skillsStr.split(", ");
                                for (String skill : skillArray) {
                                    skills.add(skill);
                                }
                            }
                            job.setSkills(skills);
                        }
                        
                        // 解析workTime
                        if (line.contains("workTime='")) {
                            String workTime = line.split("workTime='")[1].split("'" )[0];
                            job.setWorkTime(workTime);
                        }
                        
                        // 解析recruitNum
                        if (line.contains("recruitNum=")) {
                            String recruitNumStr = line.split("recruitNum=")[1].split("," )[0];
                            try {
                                int recruitNum = Integer.parseInt(recruitNumStr);
                                job.setRecruitNum(recruitNum);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // 解析deadline
                        if (line.contains("deadline='")) {
                            String deadline = line.split("deadline='")[1].split("'" )[0];
                            job.setDeadline(deadline);
                        }
                        
                        // 解析salary
                        if (line.contains("salary='")) {
                            String salary = line.split("salary='")[1].split("'" )[0];
                            job.setSalary(salary);
                        }
                        
                        // 解析location
                        if (line.contains("location='")) {
                            String location = line.split("location='")[1].split("'" )[0];
                            job.setLocation(location);
                        }
                        
                        // 解析extraRequirements
                        if (line.contains("extraRequirements='")) {
                            String extraRequirements = line.split("extraRequirements='")[1].split("'" )[0];
                            job.setExtraRequirements(extraRequirements);
                        }
                        
                        // 解析moId
                        if (line.contains("moId='")) {
                            String moId = line.split("moId='")[1].split("'" )[0];
                            job.setMoId(moId);
                        }
                        
                        // 解析status
                        if (line.contains("status=")) {
                            String statusStr = line.split("status=")[1].split("," )[0];
                            // 清理字符串，去除可能的额外字符如 '}'
                            statusStr = statusStr.replaceAll("[^A-Z_]", "");
                            try {
                                model.JobStatus status = model.JobStatus.valueOf(statusStr);
                                job.setStatus(status);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                job.setStatus(model.JobStatus.DRAFT);
                            }
                        }
                        
                        // 解析createdAt
                        if (line.contains("createdAt='")) {
                            String createdAt = line.split("createdAt='")[1].split("'" )[0];
                            job.setCreatedAt(createdAt);
                        }
                        
                        // 解析updatedAt
                        if (line.contains("updatedAt='")) {
                            String updatedAt = line.split("updatedAt='")[1].split("'" )[0];
                            job.setUpdatedAt(updatedAt);
                        }
                        
                        // 解析reviewedBy
                        if (line.contains("reviewedBy='")) {
                            String reviewedBy = line.split("reviewedBy='")[1].split("'" )[0];
                            job.setReviewedBy(reviewedBy);
                        }
                        
                        // 解析reviewTime
                        if (line.contains("reviewTime='")) {
                            String reviewTime = line.split("reviewTime='")[1].split("'" )[0];
                            job.setReviewTime(reviewTime);
                        }
                        
                        // 解析reviewComment
                        if (line.contains("reviewComment='")) {
                            String reviewComment = line.split("reviewComment='")[1].split("'" )[0];
                            job.setReviewComment(reviewComment);
                        }
                        
                        jobs.add(job);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存职位数据
    private static void saveJobs() {
        try (FileWriter writer = new FileWriter(JOBS_FILE)) {
            for (Job job : jobs) {
                writer.write(job.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载申请数据
    private static void loadApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Application{")) {
                    continue;
                }

                try {
                    Application app = new Application();

                    if (line.contains("id='")) {
                        String id = line.split("id='")[1].split("'")[0];
                        app.setId(id);
                    }
                    if (line.contains("taId='")) {
                        String taId = line.split("taId='")[1].split("'")[0];
                        app.setTaId(taId);
                    }
                    if (line.contains("jobId='")) {
                        String jobId = line.split("jobId='")[1].split("'")[0];
                        app.setJobId(jobId);
                    }
                    if (line.contains("coverLetter='")) {
                        String coverLetter = line.split("coverLetter='")[1].split("'")[0];
                        app.setCoverLetter(coverLetter);
                    }
                    if (line.contains("status=")) {
                        String statusStr = line.split("status=")[1].split(",")[0].replaceAll("[^A-Z_]", "");
                        try {
                            app.setStatus(model.ApplicationStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            app.setStatus(model.ApplicationStatus.PENDING);
                        }
                    }
                    if (line.contains("createdAt='")) {
                        String createdAt = line.split("createdAt='")[1].split("'")[0];
                        app.setCreatedAt(createdAt);
                    }
                    if (line.contains("updatedAt='")) {
                        String updatedAt = line.split("updatedAt='")[1].split("'")[0];
                        app.setUpdatedAt(updatedAt);
                    }
                    if (line.contains("reviewedBy='")) {
                        String reviewedBy = line.split("reviewedBy='")[1].split("'")[0];
                        app.setReviewedBy("null".equals(reviewedBy) ? null : reviewedBy);
                    }
                    if (line.contains("reviewTime='")) {
                        String reviewTime = line.split("reviewTime='")[1].split("'")[0];
                        app.setReviewTime("null".equals(reviewTime) ? null : reviewTime);
                    }
                    if (line.contains("reviewComment='")) {
                        String reviewComment = line.split("reviewComment='")[1].split("'")[0];
                        app.setReviewComment("null".equals(reviewComment) ? null : reviewComment);
                    }
                    if (line.contains("matchScore=")) {
                        String matchScoreStr = line.split("matchScore=")[1].split("}")[0].trim();
                        try {
                            app.setMatchScore(Double.parseDouble(matchScoreStr));
                        } catch (NumberFormatException e) {
                            app.setMatchScore(0.0);
                        }
                    }

                    applications.add(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存申请数据
    private static void saveApplications() {
        try (FileWriter writer = new FileWriter(APPLICATIONS_FILE)) {
            for (Application app : applications) {
                writer.write(app.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 加载日志数据
    private static void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 简化处理
            }
        } catch (FileNotFoundException e) {
            // 文件不存在，创建空列表
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 保存日志数据
    private static void saveLogs() {
        try (FileWriter writer = new FileWriter(LOGS_FILE)) {
            for (Log log : logs) {
                writer.write(log.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 用户相关操作
    public static List<User> getUsers() {
        return users;
    }

    public static void saveUsers(List<User> userList) {
        users = userList;
        saveUsers();
    }

    // 职位相关操作
    public static List<Job> getJobs() {
        return jobs;
    }

    public static void saveJobs(List<Job> jobList) {
        jobs = jobList;
        saveJobs();
    }

    // 申请相关操作
    public static List<Application> getApplications() {
        return applications;
    }

    public static void saveApplications(List<Application> applicationList) {
        applications = applicationList;
        saveApplications();
    }

    // 日志相关操作
    public static void addLog(String action, String user, String details) {
        Log log = new Log(action, user, details);
        logs.add(log);
        saveLogs();
    }

    public static List<Log> getLogs() {
        return logs;
    }
}
