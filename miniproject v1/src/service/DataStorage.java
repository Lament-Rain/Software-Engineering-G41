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

    // Initialize data storage
    public static void initialize() {
        try {
            // Ensure the data directory exists
            File dataDir = new File("src/data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }

            // Load data
            loadUsers();
            loadJobs();
            loadApplications();
            loadLogs();

            // If there are no users, add a default admin account
            if (users.isEmpty()) {
                Admin admin = new Admin("admin1", "admin", "admin123", "admin@bupt.edu.cn", "13800138000", model.AdminLevel.SUPER);
                users.add(admin);
                saveUsers();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Load user data
    private static void loadUsers() {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parse user data
                if (line.startsWith("Admin")) {
                    // Parse Admin user
                    String id = line.split("id='")[1].split("'")[0];
                    String username = line.split("username='")[1].split("'")[0];
                    String password = "123456";
                    if (line.contains("password='")) {
                        password = line.split("password='")[1].split("'")[0];
                    }
                    String level = line.split("level=")[1].split("}")[0];
                    // Create Admin object
                    Admin admin = new Admin(id, username, password, username + "@bupt.edu.cn", "13800138000", AdminLevel.valueOf(level));
                    users.add(admin);
                } else if (line.startsWith("TA")) {
                // Parse TA user
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
                // Create TA object
                TA ta = new TA(id, username, password, email, phone);
                
                // Parse other fields
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
                    // Remove possible extra characters, such as '}'
                    profileStatusStr = profileStatusStr.replaceAll("[}\s]", "");
                    try {
                        model.ProfileStatus profileStatus = model.ProfileStatus.valueOf(profileStatusStr);
                        ta.setProfileStatus(profileStatus);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                        // If parsing fails, set the default value
                        ta.setProfileStatus(model.ProfileStatus.DRAFT);
                    }
                }
                if (line.contains("profileUpdatedAt='")) {
                    String profileUpdatedAt = line.split("profileUpdatedAt='")[1].split("'" )[0];
                    ta.setProfileUpdatedAt(profileUpdatedAt);
                }
                
                users.add(ta);
                } else if (line.startsWith("MO")) {
                    // Parse MO user
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
                    // Create MO object
                    MO mo = new MO(id, username, password, username + "@bupt.edu.cn", "13800138000", department);
                    users.add(mo);
                }
            }
        } catch (FileNotFoundException e) {
            // File does not exist, create an empty list
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save user data
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
                            String id = line.split("id='")[1].split("'" )[0];
                            job.setId(id);
                        }
                        
                        // Parse title
                        if (line.contains("title='")) {
                            String title = line.split("title='")[1].split("'" )[0];
                            job.setTitle(title);
                        }
                        
                        // Parse type
                        if (line.contains("type=")) {
                            String typeStr = line.split("type=")[1].split("," )[0];
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
                            String department = line.split("department='")[1].split("'" )[0];
                            job.setDepartment(department);
                        }
                        
                        // Parse description
                        if (line.contains("description='")) {
                            String description = line.split("description='")[1].split("'" )[0];
                            job.setDescription(description);
                        }
                        
                        // Parse skills
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
                        
                        // Parse workTime
                        if (line.contains("workTime='")) {
                            String workTime = line.split("workTime='")[1].split("'" )[0];
                            job.setWorkTime(workTime);
                        }
                        
                        // Parse recruitNum
                        if (line.contains("recruitNum=")) {
                            String recruitNumStr = line.split("recruitNum=")[1].split("," )[0];
                            try {
                                int recruitNum = Integer.parseInt(recruitNumStr);
                                job.setRecruitNum(recruitNum);
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        }
                        
                        // Parse deadline
                        if (line.contains("deadline='")) {
                            String deadline = line.split("deadline='")[1].split("'" )[0];
                            job.setDeadline(deadline);
                        }
                        
                        // Parse salary
                        if (line.contains("salary='")) {
                            String salary = line.split("salary='")[1].split("'" )[0];
                            job.setSalary(salary);
                        }
                        
                        // Parse location
                        if (line.contains("location='")) {
                            String location = line.split("location='")[1].split("'" )[0];
                            job.setLocation(location);
                        }
                        
                        // Parse extraRequirements
                        if (line.contains("extraRequirements='")) {
                            String extraRequirements = line.split("extraRequirements='")[1].split("'" )[0];
                            job.setExtraRequirements(extraRequirements);
                        }
                        
                        // Parse moId
                        if (line.contains("moId='")) {
                            String moId = line.split("moId='")[1].split("'" )[0];
                            job.setMoId(moId);
                        }
                        
                        // Parse status
                        if (line.contains("status=")) {
                            String statusStr = line.split("status=")[1].split("," )[0];
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
                            String createdAt = line.split("createdAt='")[1].split("'" )[0];
                            job.setCreatedAt(createdAt);
                        }
                        
                        // Parse updatedAt
                        if (line.contains("updatedAt='")) {
                            String updatedAt = line.split("updatedAt='")[1].split("'" )[0];
                            job.setUpdatedAt(updatedAt);
                        }
                        
                        // Parse reviewedBy
                        if (line.contains("reviewedBy='")) {
                            String reviewedBy = line.split("reviewedBy='")[1].split("'" )[0];
                            job.setReviewedBy(reviewedBy);
                        }
                        
                        // Parse reviewTime
                        if (line.contains("reviewTime='")) {
                            String reviewTime = line.split("reviewTime='")[1].split("'" )[0];
                            job.setReviewTime(reviewTime);
                        }
                        
                        // Parse reviewComment
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
    private static void loadLogs() {
        try (BufferedReader reader = new BufferedReader(new FileReader(LOGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Simplified handling
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
    public static List<User> getUsers() {
        return users;
    }

    public static void saveUsers(List<User> userList) {
        users = userList;
        saveUsers();
    }

    // Job-related operations
    public static List<Job> getJobs() {
        return jobs;
    }

    public static void saveJobs(List<Job> jobList) {
        jobs = jobList;
        saveJobs();
    }

    // Application-related operations
    public static List<Application> getApplications() {
        return applications;
    }

    public static void saveApplications(List<Application> applicationList) {
        applications = applicationList;
        saveApplications();
    }

    // Log-related operations
    public static void addLog(String action, String user, String details) {
        Log log = new Log(action, user, details);
        logs.add(log);
        saveLogs();
    }

    public static List<Log> getLogs() {
        return logs;
    }
}
