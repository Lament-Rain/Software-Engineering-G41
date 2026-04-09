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

    public static void initialize() {
        try {
            File dataDir = new File("src/data");
            if (!dataDir.exists()) dataDir.mkdirs();
            loadUsers();
            loadJobs();
            loadApplications();
            loadLogs();
            if (users.isEmpty()) {
                users.add(new Admin("admin1", "admin", "admin123", "admin@bupt.edu.cn", "13800138000", AdminLevel.SUPER));
                saveUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Admin")) {
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = line.contains("password='") ? line.split("password='")[1].split("'")[0] : "123456";
                    String level = line.split("level=")[1].split("}")[0];
                    users.add(new Admin(id, username, password, username + "@bupt.edu.cn", "13800138000", AdminLevel.valueOf(level)));
                } else if (line.startsWith("TA")) {
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = line.contains("password='") ? line.split("password='")[1].split("'")[0] : "123456";
                    String email = line.contains("email='") ? line.split("email='")[1].split("'")[0] : username + "@bupt.edu.cn";
                    String phone = line.contains("phone='") ? line.split("phone='")[1].split("'")[0] : "13800138000";
                    TA ta = new TA(id, username, password, email, phone);
                    if (line.contains("name='")) ta.setName(line.split("name='")[1].split("'")[0]);
                    if (line.contains("gender='")) ta.setGender(line.split("gender='")[1].split("'")[0]);
                    if (line.contains("age=")) ta.setAge(Integer.parseInt(line.split("age=")[1].split(",")[0]));
                    if (line.contains("department='")) ta.setDepartment(line.split("department='")[1].split("'")[0]);
                    if (line.contains("grade='")) ta.setGrade(line.split("grade='")[1].split("'")[0]);
                    if (line.contains("studentId='")) ta.setStudentId(line.split("studentId='")[1].split("'")[0]);
                    if (line.contains("availableTime='")) ta.setAvailableTime(line.split("availableTime='")[1].split("'")[0]);
                    if (line.contains("skills=")) {
                        String skillsStr = line.split("skills=")[1].split(",")[0].replaceAll("\\[|\\]", "");
                        List<String> skills = new ArrayList<>();
                        if (!skillsStr.equals("null") && !skillsStr.isEmpty()) {
                            for (String skill : skillsStr.split(", ")) skills.add(skill);
                        }
                        ta.setSkills(skills);
                    }
                    if (line.contains("experience='")) ta.setExperience(line.split("experience='")[1].split("'")[0]);
                    if (line.contains("awards='")) ta.setAwards(line.split("awards='")[1].split("'")[0]);
                    if (line.contains("languageSkills='")) ta.setLanguageSkills(line.split("languageSkills='")[1].split("'")[0]);
                    if (line.contains("otherSkills='")) ta.setOtherSkills(line.split("otherSkills='")[1].split("'")[0]);
                    if (line.contains("resumePath='")) ta.setResumePath(line.split("resumePath='")[1].split("'")[0]);
                    if (line.contains("profileStatus=")) {
                        String status = line.split("profileStatus=")[1].split(",")[0].replaceAll("[}\\s]", "");
                        ta.setProfileStatus(ProfileStatus.valueOf(status));
                    }
                    if (line.contains("profileUpdatedAt='")) ta.setProfileUpdatedAt(line.split("profileUpdatedAt='")[1].split("'")[0]);
                    if (line.contains("profileUpdatedAt='")) ta.setProfileUpdatedAt(line.split("profileUpdatedAt='")[1].split("'")[0]);
                    if (line.contains("profileReviewComment='")) ta.setProfileReviewComment(line.split("profileReviewComment='")[1].split("'")[0]);
                    users.add(ta);
                } else if (line.startsWith("MO")) {
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = line.contains("password='") ? line.split("password='")[1].split("'")[0] : "123456";
                    String department = line.contains("department='") ? line.split("department='")[1].split("'")[0] : "null";
                    users.add(new MO(id, username, password, username + "@bupt.edu.cn", "13800138000", department));
                }
            }
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveUsers() {
        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            for (User user : users) writer.write(user + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadJobs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(JOBS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Job{")) continue;
                try {
                    Job job = new Job();
                    if (line.contains("id='")) job.setId(line.split("id='")[1].split("'")[0]);
                    if (line.contains("title='")) job.setTitle(line.split("title='")[1].split("'")[0]);
                    if (line.contains("type=")) job.setType(JobType.valueOf(line.split("type=")[1].split(",")[0].replaceAll("[^A-Z_]", "")));
                    if (line.contains("department='")) job.setDepartment(line.split("department='")[1].split("'")[0]);
                    if (line.contains("description='")) job.setDescription(line.split("description='")[1].split("'")[0]);
                    if (line.contains("skills=")) {
                        String skillsStr = line.split("skills=")[1].split(",")[0].replaceAll("\\[|\\]", "");
                        List<String> skills = new ArrayList<>();
                        if (!skillsStr.equals("null") && !skillsStr.isEmpty()) for (String skill : skillsStr.split(", ")) skills.add(skill);
                        job.setSkills(skills);
                    }
                    if (line.contains("workTime='")) job.setWorkTime(line.split("workTime='")[1].split("'")[0]);
                    if (line.contains("recruitNum=")) job.setRecruitNum(Integer.parseInt(line.split("recruitNum=")[1].split(",")[0]));
                    if (line.contains("deadline='")) job.setDeadline(line.split("deadline='")[1].split("'")[0]);
                    if (line.contains("salary='")) job.setSalary(line.split("salary='")[1].split("'")[0]);
                    if (line.contains("location='")) job.setLocation(line.split("location='")[1].split("'")[0]);
                    if (line.contains("extraRequirements='")) job.setExtraRequirements(line.split("extraRequirements='")[1].split("'")[0]);
                    if (line.contains("moId='")) job.setMoId(line.split("moId='")[1].split("'")[0]);
                    if (line.contains("status=")) job.setStatus(JobStatus.valueOf(line.split("status=")[1].split(",")[0].replaceAll("[^A-Z_]", "")));
                    if (line.contains("createdAt='")) job.setCreatedAt(line.split("createdAt='")[1].split("'")[0]);
                    if (line.contains("updatedAt='")) job.setUpdatedAt(line.split("updatedAt='")[1].split("'")[0]);
                    if (line.contains("reviewedBy='")) job.setReviewedBy(line.split("reviewedBy='")[1].split("'")[0]);
                    if (line.contains("reviewTime='")) job.setReviewTime(line.split("reviewTime='")[1].split("'")[0]);
                    if (line.contains("reviewComment='")) job.setReviewComment(line.split("reviewComment='")[1].split("'")[0]);
                    jobs.add(job);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveJobs() {
        try (FileWriter writer = new FileWriter(JOBS_FILE)) {
            for (Job job : jobs) writer.write(job + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadApplications() {
        try (BufferedReader reader = new BufferedReader(new FileReader(APPLICATIONS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("Application{")) continue;
                try {
                    Application app = new Application();
                    if (line.contains("id='")) app.setId(line.split("id='")[1].split("'")[0]);
                    if (line.contains("taId='")) app.setTaId(line.split("taId='")[1].split("'")[0]);
                    if (line.contains("jobId='")) app.setJobId(line.split("jobId='")[1].split("'")[0]);
                    if (line.contains("coverLetter='")) app.setCoverLetter(line.split("coverLetter='")[1].split("'")[0]);
                    if (line.contains("status=")) app.setStatus(ApplicationStatus.valueOf(line.split("status=")[1].split(",")[0].replaceAll("[^A-Z_]", "")));
                    if (line.contains("createdAt='")) app.setCreatedAt(line.split("createdAt='")[1].split("'")[0]);
                    if (line.contains("updatedAt='")) app.setUpdatedAt(line.split("updatedAt='")[1].split("'")[0]);
                    if (line.contains("reviewedBy='")) app.setReviewedBy(line.split("reviewedBy='")[1].split("'")[0]);
                    if (line.contains("reviewTime='")) app.setReviewTime(line.split("reviewTime='")[1].split("'")[0]);
                    if (line.contains("reviewComment='")) app.setReviewComment(line.split("reviewComment='")[1].split("'")[0]);
                    if (line.contains("matchScore=")) {
                        String score = line.split("matchScore=")[1].split("}")[0].replaceAll("[^0-9.]", "");
                        if (!score.isEmpty()) app.setMatchScore(Double.parseDouble(score));
                    }
                    applications.add(app);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveApplications() {
        try (FileWriter writer = new FileWriter(APPLICATIONS_FILE)) {
            for (Application app : applications) writer.write(app + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            while (reader.readLine() != null) {}
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveLogs() {
        try (FileWriter writer = new FileWriter(LOGS_FILE)) {
            for (Log log : logs) writer.write(log + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<User> getUsers() { return users; }
    public static void saveUsers(List<User> userList) { users = userList; saveUsers(); }
    public static List<Job> getJobs() { return jobs; }
    public static void saveJobs(List<Job> jobList) { jobs = jobList; saveJobs(); }
    public static List<Application> getApplications() { return applications; }
    public static void saveApplications(List<Application> applicationList) { applications = applicationList; saveApplications(); }
    public static void addLog(String action, String user, String details) { logs.add(new Log(action, user, details)); saveLogs(); }
    public static List<Log> getLogs() { return logs; }
}
