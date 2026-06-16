package service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import model.*;

public class DataStorage {
    // 数据存储中心：统一负责用户、岗位、申请和日志的读取与保存
    private static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Job> jobs = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Application> applications = new CopyOnWriteArrayList<>();
    private static CopyOnWriteArrayList<Log> logs = new CopyOnWriteArrayList<>();

    // 根据运行环境确定实际使用的 data 文件夹位置
    private static final File DATA_DIR = resolveDataDirectory();
    private static final String USERS_FILE = new File(DATA_DIR, "users.txt").getAbsolutePath();
    private static final String JOBS_FILE = new File(DATA_DIR, "jobs.txt").getAbsolutePath();
    private static final String APPLICATIONS_FILE = new File(DATA_DIR, "applications.txt").getAbsolutePath();
    private static final String LOGS_FILE = new File(DATA_DIR, "logs.txt").getAbsolutePath();

    private static final Object usersLock = new Object();
    private static final Object jobsLock = new Object();
    private static final Object applicationsLock = new Object();
    private static final Object logsLock = new Object();

    // Initialize data storage
    // 系统启动时调用：加载所有数据、重建索引，并在没有用户时创建默认管理员
    public static void initialize() {
        try {
            if (!DATA_DIR.exists()) {
                DATA_DIR.mkdirs();
            }

            loadUsers();
            loadJobs();
            loadApplications();
            loadLogs();

            IndexService.rebuildIndexes();

            if (users.isEmpty()) {
                String encodedPassword = service.PasswordEncoder.encode("admin123");
                Admin admin = new Admin("admin1", "admin", encodedPassword, "admin@bupt.edu.cn", "13800138000", model.AdminLevel.SUPER);
                users.add(admin);
                saveUsers();
                IndexService.indexUserUpdate(admin);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load user data
    // 提供当前数据目录，方便其他服务定位数据文件
    public static File getDataDirectory() {
        return DATA_DIR;
    }

    // 优先使用自定义数据目录，否则在 data 和 src/data 之间自动选择
    private static File resolveDataDirectory() {
        String override = System.getProperty("app.data.dir");
        if (override != null && !override.isBlank()) {
            return new File(override);
        }

        File appData = new File("data");
        if (appData.exists() || !new File("src/data").exists()) {
            return appData;
        }
        return new File("src/data");
    }

    // 从 users.txt 中读取 Admin、TA、MO 用户数据
    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse user data safely
                if (line.startsWith("Admin")) {
                    // Parse Admin user
                    String id = parseField(line, "id='");
                    String username = parseField(line, "username='");
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = parseField(line, "password='");
                    }
                    String level = parseField(line, "level=", "}");
                    // Create Admin object
                    Admin admin = new Admin(id, username, password, username + "@bupt.edu.cn", "13800138000", AdminLevel.valueOf(level));
                    users.add(admin);
                } else if (line.startsWith("TA")) {
                    // Parse TA user
                    String id = parseField(line, "id='");
                    String username = parseField(line, "username='");
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = parseField(line, "password='");
                    }
                    String email = username + "@bupt.edu.cn";
                    if (line.contains("email='")) {
                        email = parseField(line, "email='");
                    }
                    String phone = "13800138000";
                    if (line.contains("phone='")) {
                        phone = parseField(line, "phone='");
                    }
                    // Create TA object
                    TA ta = new TA(id, username, password, email, phone);
                    
                    // Parse other fields
                    if (line.contains("name='")) {
                        String name = parseField(line, "name='");
                        ta.setName(name);
                    }
                    if (line.contains("gender='")) {
                        String gender = parseField(line, "gender='");
                        ta.setGender(gender);
                    }
                    if (line.contains("age=")) {
                        String ageStr = parseField(line, "age=", ",");
                        try {
                            int age = Integer.parseInt(ageStr);
                            ta.setAge(age);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    if (line.contains("department='")) {
                        String department = parseField(line, "department='");
                        ta.setDepartment(department);
                    }
                    if (line.contains("grade='")) {
                        String grade = parseField(line, "grade='");
                        ta.setGrade(grade);
                    }
                    if (line.contains("studentId='")) {
                        String studentId = parseField(line, "studentId='");
                        ta.setStudentId(studentId);
                    }
                    if (line.contains("availableTime='")) {
                        String availableTime = parseField(line, "availableTime='");
                        ta.setAvailableTime(availableTime);
                    }
                    if (line.contains("skills=")) {
                        String skillsStr = parseField(line, "skills=", ",");
                        // Simple handling, actual parsing would need to be more complex
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
                        String experience = parseField(line, "experience='");
                        ta.setExperience(experience);
                    }
                    if (line.contains("awards='")) {
                        String awards = parseField(line, "awards='");
                        ta.setAwards(awards);
                    }
                    if (line.contains("languageSkills='")) {
                        String languageSkills = parseField(line, "languageSkills='");
                        ta.setLanguageSkills(languageSkills);
                    }
                    if (line.contains("otherSkills='")) {
                        String otherSkills = parseField(line, "otherSkills='");
                        ta.setOtherSkills(otherSkills);
                    }
                    if (line.contains("resumePath='")) {
                        String resumePath = parseField(line, "resumePath='");
                        ta.setResumePath(resumePath);
                    }
                    if (line.contains("status=")) {
                        String statusStr = parseField(line, "status=", ",");
                        statusStr = statusStr.replaceAll("[^A-Z_]", "");
                        try {
                            ta.setStatus(model.UserStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            ta.setStatus(model.UserStatus.ACTIVE);
                        }
                    }
                    if (line.contains("profileStatus=")) {
                        String profileStatusStr = parseField(line, "profileStatus=", ",");
                        profileStatusStr = profileStatusStr.replaceAll("[}\\s]", "");
                        try {
                            model.ProfileStatus profileStatus = model.ProfileStatus.valueOf(profileStatusStr);
                            ta.setProfileStatus(profileStatus);
                        } catch (IllegalArgumentException e) {
                            ta.setProfileStatus(model.ProfileStatus.DRAFT);
                        }
                    }
                    if (line.contains("profileUpdatedAt='")) {
                        String profileUpdatedAt = parseField(line, "profileUpdatedAt='");
                        ta.setProfileUpdatedAt(profileUpdatedAt);
                    }
                    
                    users.add(ta);
                } else if (line.startsWith("MO")) {
                    String id = parseField(line, "id='");
                    String username = parseField(line, "username='");
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = parseField(line, "password='");
                    }
                    String department = "null";
                    if (line.contains("department='")) {
                        department = parseField(line, "department='");
                    }
                    MO mo = new MO(id, username, password, username + "@bupt.edu.cn", "13800138000", department);
                    users.add(mo);
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!users.isEmpty()) {
            users = new CopyOnWriteArrayList<>(users);
        }
    }
    
    // Safe field parsing method
    // 从 toString 格式的文本中截取指定字段，供用户、岗位、申请和日志读取复用
    private static String parseField(String line, String fieldPrefix) {
        return parseField(line, fieldPrefix, "'");
    }
    
    private static String parseField(String line, String fieldPrefix, String fieldSuffix) {
        int startIndex = line.indexOf(fieldPrefix);
        if (startIndex == -1) {
            return "";
        }
        startIndex += fieldPrefix.length();
        int endIndex = line.indexOf(fieldSuffix, startIndex);
        if (endIndex == -1) {
            return "";
        }
        return line.substring(startIndex, endIndex);
    }

    // Save user data
    // 将当前内存中的用户列表写回 users.txt
    private static void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            for (User user : users) {
                writer.write(user.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load job data
    // 从 jobs.txt 中读取岗位数据，包括岗位状态和审核信息
    private static void loadJobs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JOBS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Job{")) {
                    // Parse Job object
                    try {
                        Job job = new Job();
                        
                        // Parse id
                        if (line.contains("id='")) {
                            String id = parseField(line, "id='");
                            job.setId(id);
                        }
                        
                        // Parse title
                        if (line.contains("title='")) {
                            String title = parseField(line, "title='");
                            job.setTitle(title);
                        }
                        
                        // Parse type
                        if (line.contains("type=")) {
                            String typeStr = parseField(line, "type=", ",");
                            // Clean the string and remove possible extra characters
                            typeStr = typeStr.replaceAll("[^A-Z_]", "");
                            try {
                                model.JobType type = model.JobType.valueOf(typeStr);
                                job.setType(type);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // Parse department
                        if (line.contains("department='")) {
                            String department = parseField(line, "department='");
                            job.setDepartment(department);
                        }
                        
                        // Parse description
                        if (line.contains("description='")) {
                            String description = parseField(line, "description='");
                            job.setDescription(description);
                        }
                        
                        // Parse skills
                        if (line.contains("skills=")) {
                            String skillsStr = parseField(line, "skills=", ",");
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
                        
                        // Parse workTime
                        if (line.contains("workTime='")) {
                            String workTime = parseField(line, "workTime='");
                            job.setWorkTime(workTime);
                        }
                        
                        // Parse recruitNum
                        if (line.contains("recruitNum=")) {
                            String recruitNumStr = parseField(line, "recruitNum=", ",");
                            try {
                                int recruitNum = Integer.parseInt(recruitNumStr);
                                job.setRecruitNum(recruitNum);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // Parse deadline
                        if (line.contains("deadline='")) {
                            String deadline = parseField(line, "deadline='");
                            job.setDeadline(deadline);
                        }
                        
                        // Parse salary
                        if (line.contains("salary='")) {
                            String salary = parseField(line, "salary='");
                            job.setSalary(salary);
                        }
                        
                        // Parse location
                        if (line.contains("location='")) {
                            String location = parseField(line, "location='");
                            job.setLocation(location);
                        }
                        
                        // Parse extraRequirements
                        if (line.contains("extraRequirements='")) {
                            String extraRequirements = parseField(line, "extraRequirements='");
                            job.setExtraRequirements(extraRequirements);
                        }
                        
                        // Parse moId
                        if (line.contains("moId='")) {
                            String moId = parseField(line, "moId='");
                            job.setMoId(moId);
                        }
                        
                        // Parse status
                        if (line.contains("status=")) {
                            String statusStr = parseField(line, "status=", ",");
                            // Clean the string and remove possible extra characters such as '}'
                            statusStr = statusStr.replaceAll("[^A-Z_]", "");
                            try {
                                model.JobStatus status = model.JobStatus.valueOf(statusStr);
                                job.setStatus(status);
                            } catch (IllegalArgumentException e) {
                                e.printStackTrace();
                                job.setStatus(model.JobStatus.DRAFT);
                            }
                        }
                        
                        // Parse createdAt
                        if (line.contains("createdAt='")) {
                            String createdAt = parseField(line, "createdAt='");
                            job.setCreatedAt(createdAt);
                        }
                        
                        // Parse updatedAt
                        if (line.contains("updatedAt='")) {
                            String updatedAt = parseField(line, "updatedAt='");
                            job.setUpdatedAt(updatedAt);
                        }
                        
                        // Parse reviewedBy
                        if (line.contains("reviewedBy='")) {
                            String reviewedBy = parseField(line, "reviewedBy='");
                            job.setReviewedBy(reviewedBy);
                        }
                        
                        // Parse reviewTime
                        if (line.contains("reviewTime='")) {
                            String reviewTime = parseField(line, "reviewTime='");
                            job.setReviewTime(reviewTime);
                        }
                        
                        // Parse reviewComment
                        if (line.contains("reviewComment='")) {
                            String reviewComment = parseField(line, "reviewComment='");
                            job.setReviewComment(reviewComment);
                        }
                        
                        jobs.add(job);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist, create an empty list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save job data
    private static void saveJobs() {
        try (FileWriter writer = new FileWriter(JOBS_FILE)) {
            for (Job job : jobs) {
                writer.write(job.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load application data
    // 从 applications.txt 中读取 TA 申请记录和申请状态
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
                        String id = parseField(line, "id='");
                        app.setId(id);
                    }
                    if (line.contains("taId='")) {
                        String taId = parseField(line, "taId='");
                        app.setTaId(taId);
                    }
                    if (line.contains("jobId='")) {
                        String jobId = parseField(line, "jobId='");
                        app.setJobId(jobId);
                    }
                    if (line.contains("coverLetter='")) {
                        String coverLetter = parseField(line, "coverLetter='");
                        app.setCoverLetter(coverLetter);
                    }
                    if (line.contains("status=")) {
                        String statusStr = parseField(line, "status=", ",");
                        statusStr = statusStr.replaceAll("[^A-Z_]", "");
                        try {
                            app.setStatus(model.ApplicationStatus.valueOf(statusStr));
                        } catch (IllegalArgumentException e) {
                            app.setStatus(model.ApplicationStatus.PENDING);
                        }
                    }
                    if (line.contains("createdAt='")) {
                        String createdAt = parseField(line, "createdAt='");
                        app.setCreatedAt(createdAt);
                    }
                    if (line.contains("updatedAt='")) {
                        String updatedAt = parseField(line, "updatedAt='");
                        app.setUpdatedAt(updatedAt);
                    }
                    if (line.contains("reviewedBy='")) {
                        String reviewedBy = parseField(line, "reviewedBy='");
                        app.setReviewedBy("null".equals(reviewedBy) ? null : reviewedBy);
                    }
                    if (line.contains("reviewTime='")) {
                        String reviewTime = parseField(line, "reviewTime='");
                        app.setReviewTime("null".equals(reviewTime) ? null : reviewTime);
                    }
                    if (line.contains("reviewComment='")) {
                        String reviewComment = parseField(line, "reviewComment='");
                        app.setReviewComment("null".equals(reviewComment) ? null : reviewComment);
                    }
                    if (line.contains("matchScore=")) {
                        String matchScoreStr = parseField(line, "matchScore=", "}").trim();
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
            // File does not exist, create an empty list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save application data
    private static void saveApplications() {
        try (FileWriter writer = new FileWriter(APPLICATIONS_FILE)) {
            for (Application app : applications) {
                writer.write(app.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Load log data
    // 从 logs.txt 中读取系统操作日志，供管理员查看
    private static void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Log{")) {
                    try {
                        Log log = new Log();
                        
                        if (line.contains("id='")) {
                            String id = parseField(line, "id='");
                            log.setId(id);
                        }
                        if (line.contains("action='")) {
                            String action = parseField(line, "action='");
                            log.setAction(action);
                        }
                        if (line.contains("user='")) {
                            String user = parseField(line, "user='");
                            log.setUser(user);
                        }
                        if (line.contains("details='")) {
                            String details = parseField(line, "details='");
                            log.setDetails(details);
                        }
                        if (line.contains("timestamp='")) {
                            String timestamp = parseField(line, "timestamp='");
                            log.setTimestamp(timestamp);
                        }
                        if (line.contains("ip='")) {
                            String ip = parseField(line, "ip='");
                            log.setIp(ip);
                        }
                        
                        logs.add(log);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist, create an empty list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save log data
    private static void saveLogs() {
        try (FileWriter writer = new FileWriter(LOGS_FILE)) {
            for (Log log : logs) {
                writer.write(log.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // User-related operations
    // 对外提供用户数据的读取和保存入口
    public static List<User> getUsers() {
        return users;
    }

    public static void saveUsers(List<User> userList) {
        synchronized (usersLock) {
            users = new CopyOnWriteArrayList<>(userList);
            saveUsers();
        }
    }

    public static void saveUsersAtomic(List<User> userList) {
        synchronized (usersLock) {
            users = new CopyOnWriteArrayList<>(userList);
        }
    }

    public static void persistUsersAsync() {
        new Thread(() -> {
            synchronized (usersLock) {
                saveUsers();
            }
        }).start();
    }

    // Job-related operations
    // 对外提供岗位数据的读取和保存入口
    public static List<Job> getJobs() {
        return jobs;
    }

    public static void saveJobs(List<Job> jobList) {
        synchronized (jobsLock) {
            jobs = new CopyOnWriteArrayList<>(jobList);
            saveJobs();
        }
    }

    public static void saveJobsAtomic(List<Job> jobList) {
        synchronized (jobsLock) {
            jobs = new CopyOnWriteArrayList<>(jobList);
        }
    }

    public static void persistJobsAsync() {
        new Thread(() -> {
            synchronized (jobsLock) {
                saveJobs();
            }
        }).start();
    }

    public static void batchSaveJobs(List<Job> jobList, boolean async) {
        if (async) {
            persistJobsAsync();
        } else {
            saveJobs(jobList);
        }
    }

    // Application-related operations
    // 对外提供申请数据的读取、保存和批量更新入口
    public static List<Application> getApplications() {
        return applications;
    }

    public static void saveApplications(List<Application> applicationList) {
        synchronized (applicationsLock) {
            applications = new CopyOnWriteArrayList<>(applicationList);
            saveApplications();
        }
    }

    public static void saveApplicationsAtomic(List<Application> applicationList) {
        synchronized (applicationsLock) {
            applications = new CopyOnWriteArrayList<>(applicationList);
        }
    }

    public static void persistApplicationsAsync() {
        new Thread(() -> {
            synchronized (applicationsLock) {
                saveApplications();
            }
        }).start();
    }

    public static void batchSaveApplications(List<Application> applicationList, boolean async) {
        if (async) {
            persistApplicationsAsync();
        } else {
            saveApplications(applicationList);
        }
    }

    public static int batchAddApplications(List<Application> newApps) {
        synchronized (applicationsLock) {
            int count = 0;
            for (Application app : newApps) {
                if (!applications.contains(app)) {
                    applications.add(app);
                    IndexService.indexApplicationUpdate(app);
                    count++;
                }
            }
            persistApplicationsAsync();
            return count;
        }
    }

    public static int batchUpdateApplications(List<Application> updatedApps) {
        synchronized (applicationsLock) {
            int count = 0;
            Map<String, Application> appMap = applications.stream()
                    .collect(Collectors.toMap(Application::getId, a -> a));

            for (Application updated : updatedApps) {
                if (appMap.containsKey(updated.getId())) {
                    IndexService.indexApplicationUpdate(updated);
                    applications.set(applications.indexOf(appMap.get(updated.getId())), updated);
                    count++;
                }
            }
            persistApplicationsAsync();
            return count;
        }
    }

    // Log-related operations
    // 写入系统日志：记录关键操作，支持管理员审计和问题追踪
    public static void addLog(String action, String user, String details, String ip) {
        Log log = new Log(action, user, details, ip);
        logs.add(log);
        saveLogs();
    }
    
    public static void addLog(String action, String user, String details) {
        addLog(action, user, details, "unknown");
    }

    public static List<Log> getLogs() {
        return logs;
    }
}



