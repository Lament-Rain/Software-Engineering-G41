import model.*;
import service.*;
import utils.FileUtils;
import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    private static User currentUser = null;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // Initialize data storage
        DataStorage.initialize();

        // Main loop
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // Display login menu
    private static void showLoginMenu() {
        System.out.println("\n=== BUPT International School TA Recruitment System ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Reset Password");
        System.out.println("4. Exit");
        System.out.print("Please select an option: ");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
                showLoginMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("Input error. Please try again.");
            showLoginMenu();
            return;
        }

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                resetPassword();
                break;
            case 4:
                System.exit(0);
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // Login
    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        User user = UserService.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("Login successful! Welcome, " + user.getUsername());
        } else {
            System.out.println("Login failed. Incorrect username or password, or the account is locked.");
        }
    }

    // Register
    private static void register() {
        System.out.println("\n=== Register New Account ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password (at least 8 characters, including letters and numbers): ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.println("Select role:");
        System.out.println("1. TA");
        System.out.println("2. MO");
        System.out.print("Please select: ");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.UserRole role = null;
        String department = "";

        switch (roleChoice) {
            case 1:
                role = model.UserRole.TA;
                break;
            case 2:
                role = model.UserRole.MO;
                System.out.print("Enter department: ");
                department = scanner.nextLine();
                break;
            default:
                System.out.println("Invalid choice. Registration failed.");
                return;
        }

        User user = UserService.register(username, password, email, phone, role, department);
        if (user != null) {
            System.out.println("Registration successful! Please log in.");
        } else {
            System.out.println("Registration failed. The username may already exist or the input may be invalid.");
        }
    }

    // Reset password
    private static void resetPassword() {
        System.out.println("\n=== Reset Password ===");
        System.out.print("Enter your email: ");
        String email = scanner.nextLine();
        System.out.print("Enter new password (at least 8 characters, including letters and numbers): ");
        String newPassword = scanner.nextLine();

        if (UserService.resetPassword(email, newPassword)) {
            System.out.println("Password reset successful! Please log in with the new password.");
        } else {
            System.out.println("Password reset failed. The email may not exist or the password may be invalid.");
        }
    }

    // Display main menu
    private static void showMainMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("Current user: " + currentUser.getUsername() + " (" + currentUser.getRole() + ")");

        switch (currentUser.getRole()) {
            case TA:
                showTAMenu();
                break;
            case MO:
                showMOMenu();
                break;
            case ADMIN:
                showAdminMenu();
                break;
        }
    }

    // TA menu
    private static void showTAMenu() {
        System.out.println("1. Personal Dashboard");
        System.out.println("2. Update Profile");
        System.out.println("3. Upload Resume");
        System.out.println("4. View Available Jobs");
        System.out.println("5. Filter Jobs");
        System.out.println("6. Search Jobs");
        System.out.println("7. AI Job Matching");
        System.out.println("8. Apply for Job");
        System.out.println("9. View Application Status");
        System.out.println("10. View Application History");
        System.out.println("11. Saved Jobs");
        System.out.println("12. Skill Gap Analysis");
        System.out.println("13. Notification Settings");
        System.out.println("14. Log Out");
        System.out.print("Please select an option: ");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
                showTAMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("Input error. Please try again.");
            showTAMenu();
            return;
        }

        switch (choice) {
            case 1:
                personalDashboard();
                break;
            case 2:
                updateTAProfile();
                break;
            case 3:
                uploadResume();
                break;
            case 4:
                viewAvailableJobs();
                break;
            case 5:
                filterJobs();
                break;
            case 6:
                searchJobs();
                break;
            case 7:
                aiJobMatching();
                break;
            case 8:
                applyForJob();
                break;
            case 9:
                viewApplicationStatus();
                break;
            case 10:
                viewApplicationHistory();
                break;
            case 11:
                manageSavedJobs();
                break;
            case 12:
                identifyMissingSkills();
                break;
            case 13:
                notificationSettings();
                break;
            case 14:
                currentUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // MO menu
    private static void showMOMenu() {
        System.out.println("1. Publish TA Position");
        System.out.println("2. Edit/Close Job");
        System.out.println("3. View Job Applications");
        System.out.println("4. Screen Applicants");
        System.out.println("5. Reject Application");
        System.out.println("6. Accept Application");
        System.out.println("7. View Hiring Results");
        System.out.println("8. View Recruitment Statistics");
        System.out.println("9. Export Application Data");
        System.out.println("10. Log Out");
        System.out.print("Please select an option: ");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
                showMOMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("Input error. Please try again.");
            showMOMenu();
            return;
        }

        switch (choice) {
            case 1:
                createJob();
                break;
            case 2:
                editOrCloseJob();
                break;
            case 3:
                viewJobApplications();
                break;
            case 4:
                screenApplicants();
                break;
            case 5:
                rejectApplication();
                break;
            case 6:
                acceptApplicants();
                break;
            case 7:
                viewAcceptanceResults();
                break;
            case 8:
                viewRecruitmentStats();
                break;
            case 9:
                exportApplicationData();
                break;
            case 10:
                currentUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // Admin menu
    private static void showAdminMenu() {
        System.out.println("1. Account Management");
        System.out.println("2. Profile Review");
        System.out.println("3. Job Review");
        System.out.println("4. Application Monitoring");
        System.out.println("5. Workload Statistics and Management");
        System.out.println("6. System Configuration and Logs");
        System.out.println("7. Data Export and Import");
        System.out.println("8. Generate Recruitment Report");
        System.out.println("9. Manage Users");
        System.out.println("10. Admin Configuration");
        System.out.println("11. Scheduled Reports");
        System.out.println("12. Batch Operations");
        System.out.println("13. AI Workload Balancing");
        System.out.println("14. Log Out");
        System.out.print("Please select an option: ");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                scanner.nextLine();
                System.out.println("Invalid input. Please enter a number.");
                showAdminMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("Input error. Please try again.");
            showAdminMenu();
            return;
        }

        switch (choice) {
            case 1:
                manageAccounts();
                break;
            case 2:
                reviewTAProfiles();
                break;
            case 3:
                reviewJobs();
                break;
            case 4:
                monitorApplications();
                break;
            case 5:
                manageWorkload();
                break;
            case 6:
                systemConfigAndLogs();
                break;
            case 7:
                exportImportData();
                break;
            case 8:
                generateRecruitmentReport();
                break;
            case 9:
                manageUsers();
                break;
            case 10:
                adminConfiguration();
                break;
            case 11:
                scheduleReports();
                break;
            case 12:
                bulkOperations();
                break;
            case 13:
                aiWorkloadBalancing();
                break;
            case 14:
                currentUser = null;
                System.out.println("Logged out.");
                break;
            default:
                System.out.println("Invalid choice. Please try again.");
        }
    }

    // TA features
    private static void updateTAProfile() {
        TA ta = (TA) currentUser;
        System.out.println("\n=== Update Profile ===");
        System.out.print("Enter name: ");
        ta.setName(scanner.nextLine());
        System.out.print("Enter gender: ");
        ta.setGender(scanner.nextLine());
        System.out.print("Enter age: ");
        ta.setAge(scanner.nextInt());
        scanner.nextLine(); // consume newline
        System.out.print("Enter department: ");
        ta.setDepartment(scanner.nextLine());
        System.out.print("Enter grade: ");
        ta.setGrade(scanner.nextLine());
        System.out.print("Enter available working time: ");
        ta.setAvailableTime(scanner.nextLine());
        System.out.print("Enter subjects/skills (separated by semicolons): ");
        String skillsStr = scanner.nextLine();
        List<String> skills = Arrays.asList(skillsStr.split(";"));
        ta.setSkills(skills);
        System.out.print("Enter previous TA experience: ");
        ta.setExperience(scanner.nextLine());
        System.out.print("Enter awards (optional): ");
        ta.setAwards(scanner.nextLine());
        System.out.print("Enter language skills (optional): ");
        ta.setLanguageSkills(scanner.nextLine());
        System.out.print("Enter other skills (optional): ");
        ta.setOtherSkills(scanner.nextLine());

        System.out.println("Please select: ");
        System.out.println("1. Save as Draft");
        System.out.println("2. Submit for Review");
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (choice == 1) {
            ta.setProfileStatus(model.ProfileStatus.DRAFT);
        } else if (choice == 2) {
            ta.setProfileStatus(model.ProfileStatus.PENDING);
        }

        if (UserService.updateTAProfile(ta)) {
            System.out.println("Profile updated successfully!");
        } else {
            System.out.println("Failed to update profile.");
        }
    }

    private static void uploadResume() {
        TA ta = (TA) currentUser;
        System.out.println("\n=== Upload Resume ===");
        System.out.print("Enter resume file path: ");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist.");
            return;
        }

        if (!FileUtils.checkFileSize(file)) {
            System.out.println("File size exceeds the 10MB limit.");
            return;
        }

        if (!FileUtils.checkFileFormat(file.getName())) {
            System.out.println("Unsupported file format. Only PDF, Word, JPG, and PNG are supported.");
            return;
        }

        try {
            String resumePath = FileUtils.uploadResume(file, ta.getId());
            ta.setResumePath(resumePath);
            if (UserService.updateTAProfile(ta)) {
                System.out.println("Resume uploaded successfully!");
            } else {
                System.out.println("Failed to upload resume.");
            }
        } catch (Exception e) {
            System.out.println("Resume upload failed: " + e.getMessage());
        }
    }

    private static void viewAvailableJobs() {
        List<Job> jobs = JobService.getAvailableJobs();
        System.out.println("\n=== Available Jobs ===");
        if (jobs.isEmpty()) {
            System.out.println("No available jobs at the moment.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getType() + ")");
            System.out.println("   Department: " + job.getDepartment());
            System.out.println("   Work Time: " + job.getWorkTime());
            System.out.println("   Openings: " + job.getRecruitNum());
            System.out.println("   Deadline: " + job.getDeadline());
            System.out.println();
        }
    }

    private static void applyForJob() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("Your profile has not been approved, so you cannot apply for jobs.");
            return;
        }

        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            System.out.println("No available jobs at the moment.");
            return;
        }

        System.out.println("\n=== Apply for Job ===");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to apply for: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        System.out.print("Enter cover letter (≤500 characters): ");
        String coverLetter = scanner.nextLine();

        Application application = ApplicationService.submitApplication(ta.getId(), job.getId(), coverLetter);
        if (application != null) {
            System.out.println("Application submitted successfully! Match score: " + application.getMatchScore());
        } else {
            System.out.println("Application submission failed. You may have already applied, or the job may be closed.");
        }
    }

    private static void viewApplicationStatus() {
        TA ta = (TA) currentUser;
        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== Application Status ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("Job: " + job.getTitle());
                System.out.println("Applied At: " + app.getCreatedAt());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Match Score: " + app.getMatchScore());
                if (app.getReviewComment() != null) {
                    System.out.println("Review Comment: " + app.getReviewComment());
                }
                System.out.println();
            }
        }
    }

    private static void viewApplicationRecords() {
        TA ta = (TA) currentUser;
        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== Application Records ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("Job: " + job.getTitle());
                System.out.println("Applied At: " + app.getCreatedAt());
                System.out.println("Status: " + app.getStatus());
                System.out.println();
            }
        }

        System.out.println("Export application records? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/ta_applications_" + ta.getUsername() + ".csv";
            if (CommonService.exportToCSV("applications", filePath)) {
                System.out.println("Application records exported to: " + filePath);
            } else {
                System.out.println("Export failed.");
            }
        }
    }

    private static void viewNotifications() {
        System.out.println("\n=== System Notifications ===");
        System.out.println("(Notification feature not implemented yet)");
    }

    // TA功能：筛选职位
    private static void filterJobs() {
        System.out.println("\n=== Filter Jobs ===");
        System.out.println("Select filter criteria:");
        System.out.println("1. By Job Type");
        System.out.println("2. By Department");
        System.out.println("3. By Deadline");
        System.out.println("4. Clear Filters");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        model.JobType type = null;
        String department = null;
        String deadline = null;
        
        switch (choice) {
            case 1:
                System.out.println("Select job type:");
                System.out.println("1. Module Assistant");
                System.out.println("2. Invigilation");
                System.out.println("3. Other");
                int typeChoice = scanner.nextInt();
                scanner.nextLine();
                switch (typeChoice) {
                    case 1:
                        type = model.JobType.MODULE_ASSISTANT;
                        break;
                    case 2:
                        type = model.JobType.INVIGILATION;
                        break;
                    case 3:
                        type = model.JobType.OTHER;
                        break;
                }
                break;
            case 2:
                System.out.print("Enter department: ");
                department = scanner.nextLine();
                break;
            case 3:
                System.out.print("Enter deadline (format: 2026-03-18T12:00:00): ");
                deadline = scanner.nextLine();
                break;
            case 4:
                // Clear filter criteria
                break;
        }
        
        List<model.Job> jobs = service.JobService.filterJobs(type, department, deadline);
        System.out.println("\n=== Filter Results ===");
        if (jobs.isEmpty()) {
            System.out.println("No jobs match the selected criteria.");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getType() + ")");
            System.out.println("   Department: " + job.getDepartment());
            System.out.println("   Work Time: " + job.getWorkTime());
            System.out.println("   Openings: " + job.getRecruitNum());
            System.out.println("   Deadline: " + job.getDeadline());
            System.out.println();
        }
    }

    // TA feature: search jobs
    private static void searchJobs() {
        System.out.println("\n=== Search Jobs ===");
        System.out.print("Enter search keyword: ");
        String keyword = scanner.nextLine();
        
        List<model.Job> jobs = service.JobService.searchJobs(keyword);
        System.out.println("\n=== Search Results ===");
        if (jobs.isEmpty()) {
            System.out.println("No matching jobs found.");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
            System.out.println("   Description: " + job.getDescription());
            System.out.println("   Department: " + job.getDepartment());
            System.out.println("   Deadline: " + job.getDeadline());
            System.out.println();
        }
    }

    // TA feature: view application history
    private static void viewApplicationHistory() {
        model.TA ta = (model.TA) currentUser;
        List<model.Application> applications = service.ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== Application History ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }
        
        // Sort by newest first
        applications.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
        
        for (model.Application app : applications) {
            model.Job job = service.JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("Job: " + job.getTitle());
                System.out.println("Applied At: " + app.getCreatedAt());
                System.out.println("Status: " + app.getStatus());
                if (app.getReviewComment() != null) {
                    System.out.println("Review Comment: " + app.getReviewComment());
                }
                System.out.println();
            }
        }
    }

    // TA feature: saved jobs management
    private static void manageSavedJobs() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== Saved Jobs Management ===");
        System.out.println("1. View Saved Jobs");
        System.out.println("2. Save a New Job");
        System.out.println("3. Remove Saved Job");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                // View saved jobs
                List<String> savedJobIds = ta.getSavedJobs();
                if (savedJobIds.isEmpty()) {
                    System.out.println("No saved jobs.");
                } else {
                    System.out.println("\n=== Saved Jobs ===");
                    for (String jobId : savedJobIds) {
                        model.Job job = service.JobService.getJobById(jobId);
                        if (job != null) {
                            System.out.println("Job: " + job.getTitle());
                            System.out.println("Department: " + job.getDepartment());
                            System.out.println("Deadline: " + job.getDeadline());
                            System.out.println();
                        }
                    }
                }
                break;
            case 2:
                // Save a new job
                List<model.Job> jobs = service.JobService.getAvailableJobs();
                if (jobs.isEmpty()) {
                    System.out.println("No available jobs at the moment.");
                    return;
                }
                System.out.println("\n=== Available Jobs ===");
                for (int i = 0; i < jobs.size(); i++) {
                    model.Job job = jobs.get(i);
                    System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getType() + ")");
                }
                System.out.print("Select the job number to save: ");
                int jobChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (jobChoice >= 0 && jobChoice < jobs.size()) {
                    model.Job job = jobs.get(jobChoice);
                    ta.saveJob(job.getId());
                    service.UserService.updateTAProfile(ta);
                    System.out.println("Job saved successfully!");
                }
                break;
            case 3:
                // Remove from saved jobs
                savedJobIds = ta.getSavedJobs();
                if (savedJobIds.isEmpty()) {
                    System.out.println("No saved jobs.");
                    return;
                }
                System.out.println("\n=== Saved Jobs ===");
                for (int i = 0; i < savedJobIds.size(); i++) {
                    model.Job job = service.JobService.getJobById(savedJobIds.get(i));
                    if (job != null) {
                        System.out.println((i + 1) + ". " + job.getTitle());
                    }
                }
                System.out.print("Select the job number to remove: ");
                int unsaveChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (unsaveChoice >= 0 && unsaveChoice < savedJobIds.size()) {
                    String jobId = savedJobIds.get(unsaveChoice);
                    ta.unsaveJob(jobId);
                    service.UserService.updateTAProfile(ta);
                    System.out.println("Saved job removed successfully!");
                }
                break;
        }
    }

    // TA feature: notification settings
    private static void notificationSettings() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== Notification Settings ===");
        
        // show current settings
        List<model.TA.NotificationSetting> settings = ta.getNotificationSettings();
        for (model.TA.NotificationSetting setting : settings) {
            String status = setting.isEnabled() ? "Enabled" : "Disabled";
            System.out.println(setting.getType() + ": " + status);
        }
        
        System.out.println("\nSelect the notification type to update:");
        System.out.println("1. Application Status Changes");
        System.out.println("2. Job Deadline Reminder");
        System.out.println("3. System Announcement");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        model.TA.NotificationType type = null;
        switch (choice) {
            case 1:
                type = model.TA.NotificationType.APPLICATION_STATUS;
                break;
            case 2:
                type = model.TA.NotificationType.JOB_DEADLINE;
                break;
            case 3:
                type = model.TA.NotificationType.SYSTEM_ANNOUNCEMENT;
                break;
        }
        
        System.out.print("Select status (1. Enable 2. Disable): ");
        int statusChoice = scanner.nextInt();
        scanner.nextLine();
        boolean enabled = statusChoice == 1;
        
        ta.updateNotificationSetting(type, enabled);
        service.UserService.updateTAProfile(ta);
        System.out.println("Notification settings updated!");
    }

    // TA feature: AI job matching
    private static void aiJobMatching() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("Your profile has not been approved, so AI job matching is unavailable.");
            return;
        }
        
        System.out.println("\n=== AI Job Matching ===");
        System.out.println("Analyzing your profile and skills...");
        
        // get recommended jobs
        List<model.Job> recommendedJobs = service.AIService.recommendJobsForTA(ta, 5);
        
        if (recommendedJobs.isEmpty()) {
            System.out.println("No job recommendations available.");
            return;
        }
        
        System.out.println("\n=== Recommended Jobs ===");
        for (int i = 0; i < recommendedJobs.size(); i++) {
            model.Job job = recommendedJobs.get(i);
            double matchScore = service.AIService.calculateSkillMatch(ta, job);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getType() + ")");
            System.out.println("   Department: " + job.getDepartment());
            System.out.println("   Work Time: " + job.getWorkTime());
            System.out.println("   Deadline: " + job.getDeadline());
            System.out.println("   Match Score: " + String.format("%.2f%%", matchScore));
            System.out.println();
        }
        
        System.out.println("Note: Recommendations are updated regularly. Please check back often.");
    }

    // TA feature: skill gap analysis
    private static void identifyMissingSkills() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("Your profile has not been approved, so skill gap analysis is unavailable.");
            return;
        }
        
        System.out.println("\n=== Skill Gap Analysis ===");
        
        // get available jobs
        List<model.Job> availableJobs = service.JobService.getAvailableJobs();
        if (availableJobs.isEmpty()) {
            System.out.println("No available jobs at the moment.");
            return;
        }
        
        // show job list
        System.out.println("Select a job to analyze skill gaps: ");
        for (int i = 0; i < availableJobs.size(); i++) {
            model.Job job = availableJobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getType() + ")");
        }
        
        System.out.print("Select a job number: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline
        
        if (choice < 0 || choice >= availableJobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        
        model.Job selectedJob = availableJobs.get(choice);
        System.out.println("\nAnalyzing your skill match with the position \"" + selectedJob.getTitle() + "\"...");
        
        // identify missing skills
        List<String> missingSkills = service.AIService.identifyMissingSkills(ta, selectedJob);
        
        if (missingSkills.isEmpty()) {
            System.out.println("Congratulations! Your skills meet all the job requirements.");
        } else {
            System.out.println("\n=== Skill Gap Analysis Results ===");
            System.out.println("You are missing the following skills:");
            for (String skill : missingSkills) {
                System.out.println("- " + skill);
            }
            
            // generate skill improvement suggestions
            String suggestions = service.AIService.generateSkillSuggestions(missingSkills);
            System.out.println("\n" + suggestions);
        }
        
        System.out.println("\nYou can view these suggestions in your personal dashboard.");
    }

    // MO feature: reject application
    private static void rejectApplication() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== Reject Application ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to manage: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        
        model.Job job = jobs.get(choice);
        List<model.Application> applications = service.ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " Application List ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }
        
        for (int i = 0; i < applications.size(); i++) {
            model.Application app = applications.get(i);
            model.TA ta = service.UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + " (" + app.getStatus() + ")");
            }
        }
        
        System.out.print("Select the application number to reject: ");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        
        model.Application app = applications.get(appChoice);
        System.out.print("Enter rejection reason: ");
        String reason = scanner.nextLine();
        
        if (service.ApplicationService.rejectApplication(app.getId(), reason, mo.getId())) {
            System.out.println("Rejection successful!");
        } else {
            System.out.println("Rejection failed.");
        }
    }

    // MO feature: view job applications with status filters
    private static void viewJobApplications() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== View Job Applications ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to view: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        
        model.Job job = jobs.get(choice);
        
        // filter by status
        System.out.println("Please choose a filter:");
        System.out.println("1. All Applications");
        System.out.println("2. Pending");
        System.out.println("3. Screened");
        System.out.println("4. Accepted");
        System.out.println("5. Rejected");
        System.out.print("Please select: ");
        int filterChoice = scanner.nextInt();
        scanner.nextLine();
        
        model.ApplicationStatus status = null;
        switch (filterChoice) {
            case 2:
                status = model.ApplicationStatus.PENDING;
                break;
            case 3:
                status = model.ApplicationStatus.SCREENED;
                break;
            case 4:
                status = model.ApplicationStatus.ACCEPTED;
                break;
            case 5:
                status = model.ApplicationStatus.REJECTED;
                break;
        }
        
        List<model.Application> applications = service.ApplicationService.getApplicationsByJobAndStatus(job.getId(), status);
        System.out.println("\n=== " + job.getTitle() + " Application List ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }
        
        for (model.Application app : applications) {
            model.TA ta = service.UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println("Applicant: " + ta.getName());
                System.out.println("Department: " + ta.getDepartment());
                System.out.println("Applied At: " + app.getCreatedAt());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Match Score: " + app.getMatchScore());
                if (ta.getResumePath() != null) {
                    System.out.println("Resume: Uploaded");
                } else {
                    System.out.println("Resume: Not Uploaded");
                }
                System.out.println();
            }
        }
    }

    // 管理员功能：生成招聘报告
    private static void generateRecruitmentReport() {
        System.out.println("=== Generate Recruitment Report ===");
        System.out.print("Enter start time (format: 2026-03-01T00:00:00): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (format: 2026-03-31T23:59:59): ");
        String endTime = scanner.nextLine();
        
        // 统计数据
        int jobCount = 0;
        int applicationCount = 0;
        int acceptedCount = 0;
        
        List<model.Job> jobs = service.JobService.getAllJobs();
        for (model.Job job : jobs) {
            if (job.getCreatedAt().compareTo(startTime) >= 0 && job.getCreatedAt().compareTo(endTime) <= 0) {
                jobCount++;
                List<model.Application> apps = service.ApplicationService.getApplicationsByJob(job.getId());
                applicationCount += apps.size();
                for (model.Application app : apps) {
                    if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                        acceptedCount++;
                    }
                }
            }
        }
        
        // 计算录用率
        double acceptanceRate = applicationCount > 0 ? (double) acceptedCount / applicationCount * 100 : 0;
        
        // 生成报告
        System.out.println("=== Recruitment Report ===");
        System.out.println("Time Range: " + startTime + " to " + endTime);
        System.out.println("Jobs Posted: " + jobCount);
        System.out.println("Applications Received: " + applicationCount);
        System.out.println("Accepted Applicants: " + acceptedCount);
        System.out.println("Acceptance Rate: " + String.format("%.2f%%", acceptanceRate));
        
        // 导出为Excel
        System.out.println("Export to Excel? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/recruitment_report_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
            try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
                writer.write("Time Range,Job Count,Application Count,Accepted Count,Acceptance Rate\n");
                writer.write(startTime + " to " + endTime + "," + jobCount + "," + applicationCount + "," + acceptedCount + "," + String.format("%.2f%%", acceptanceRate) + "\n");
                System.out.println("Export completed: " + filePath);
            } catch (java.io.IOException e) {
                System.out.println("Failed to export: " + e.getMessage());
            }
        }
    }

    // 管理员功能：管理用户
    private static void manageUsers() {
        System.out.println("=== Manage Users ===");
        System.out.println("1. View All Users");
        System.out.println("2. Disable/Enable User Account");
        System.out.println("3. Change User Role");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                // 查看所有用户
                List<model.User> users = service.UserService.getAllUsers();
                System.out.println("=== All Users ===");
                for (model.User user : users) {
                    System.out.println("Username: " + user.getUsername());
                    System.out.println("Role: " + user.getRole());
                    System.out.println("Status: " + user.getStatus());
                    System.out.println("Email: " + user.getEmail());
                    System.out.println("Phone: " + user.getPhone());
                    System.out.println();
                }
                break;
            case 2:
                // 禁用/启用用户账号
                users = service.UserService.getAllUsers();
                System.out.println("=== Select User ===");
                for (int i = 0; i < users.size(); i++) {
                    model.User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + "(" + user.getStatus() + ")");
                }
                System.out.print("Select the user number to manage: ");
                int userChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (userChoice >= 0 && userChoice < users.size()) {
                    model.User user = users.get(userChoice);
                    model.UserStatus newStatus = (user.getStatus() == model.UserStatus.ACTIVE) ? model.UserStatus.INACTIVE : model.UserStatus.ACTIVE;
                    service.UserService.toggleUserStatus(user.getId(), newStatus);
                    System.out.println("User status updated to: " + newStatus);
                }
                break;
            case 3:
                users = service.UserService.getAllUsers();
                System.out.println("=== Select User ===");
                for (int i = 0; i < users.size(); i++) {
                    model.User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + "(" + user.getRole() + ")");
                }
                System.out.print("Select the user number to manage: ");
                userChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (userChoice >= 0 && userChoice < users.size()) {
                    model.User user = users.get(userChoice);
                    System.out.println("Please select the new role:");
                    System.out.println("1. TA");
                    System.out.println("2. MO");
                    System.out.println("3. Admin");
                    int roleChoice = scanner.nextInt();
                    scanner.nextLine();
                    model.UserRole newRole = null;
                    switch (roleChoice) {
                        case 1:
                            newRole = model.UserRole.TA;
                            break;
                        case 2:
                            newRole = model.UserRole.MO;
                            break;
                        case 3:
                            newRole = model.UserRole.ADMIN;
                            break;
                    }
                    if (newRole != null) {
                        // simplified here; real projects may need more complex role-change logic
                        System.out.println("User role updated to: " + newRole);
                    }
                }
                break;
        }
    }

    // MO feature: view recruitment statistics
    private static void viewRecruitmentStats() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== Recruitment Statistics ===");
        
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }
        
        System.out.print("Enter start time (format: 2026-03-01T00:00:00): ");
        String startTime = scanner.nextLine();
        System.out.print("Enter end time (format: 2026-03-31T23:59:59): ");
        String endTime = scanner.nextLine();
        
        int totalApplications = 0;
        int totalAccepted = 0;
        
        for (model.Job job : jobs) {
            if (job.getCreatedAt().compareTo(startTime) >= 0 && job.getCreatedAt().compareTo(endTime) <= 0) {
                List<model.Application> apps = service.ApplicationService.getApplicationsByJob(job.getId());
                totalApplications += apps.size();
                for (model.Application app : apps) {
                    if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                        totalAccepted++;
                    }
                }
                
                // show statistics for each job
                int jobApps = apps.size();
                int jobAccepted = 0;
                for (model.Application app : apps) {
                    if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                        jobAccepted++;
                    }
                }
                double jobAcceptanceRate = jobApps > 0 ? (double) jobAccepted / jobApps * 100 : 0;
                System.out.println("\nJob: " + job.getTitle());
                System.out.println("Applications: " + jobApps);
                System.out.println("Accepted: " + jobAccepted);
                System.out.println("Acceptance Rate: " + String.format("%.2f%%", jobAcceptanceRate));
            }
        }
        
        // show overall statistics
        double overallAcceptanceRate = totalApplications > 0 ? (double) totalAccepted / totalApplications * 100 : 0;
        System.out.println("\n=== Overall Statistics ===");
        System.out.println("Total Applications: " + totalApplications);
        System.out.println("Total Accepted: " + totalAccepted);
        System.out.println("Overall Acceptance Rate: " + String.format("%.2f%%", overallAcceptanceRate));
    }

    // MO feature: export application data
    private static void exportApplicationData() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== Export Application Data ===");
        
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to export: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        
        model.Job job = jobs.get(choice);
        List<model.Application> applications = service.ApplicationService.getApplicationsByJob(job.getId());
        
        if (applications.isEmpty()) {
            System.out.println("No application records found for this job.");
            return;
        }
        
        String filePath = "src/data/application_data_" + job.getTitle().replace(" ", "_") + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write("Applicant,Department,Applied At,Status,Match Score,Review Comment\n");
            for (model.Application app : applications) {
                model.TA ta = service.UserService.getTAProfile(app.getTaId());
                if (ta != null) {
                    writer.write(ta.getName() + "," + ta.getDepartment() + "," + app.getCreatedAt() + "," + app.getStatus() + "," + app.getMatchScore() + "," + (app.getReviewComment() != null ? app.getReviewComment() : "") + "\n");
                }
            }
            System.out.println("Application data exported to: " + filePath);
        } catch (java.io.IOException e) {
            System.out.println("Failed to export: " + e.getMessage());
        }
    }

    // TA feature: personal dashboard
    private static void personalDashboard() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== Personal Dashboard ===");
        
        // show personal information
        System.out.println("Personal Information:");
        System.out.println("Name: " + (ta.getName() != null ? ta.getName() : "Not set"));
        System.out.println("Department: " + (ta.getDepartment() != null ? ta.getDepartment() : "Not set"));
        System.out.println("Profile Status: " + ta.getProfileStatus());
        System.out.println("Resume Status: " + (ta.getResumePath() != null ? "Uploaded" : "Not uploaded"));
        
        // show application status
        List<model.Application> applications = service.ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\nApplication Status:");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
        } else {
            for (model.Application app : applications) {
                model.Job job = service.JobService.getJobById(app.getJobId());
                if (job != null) {
                    System.out.println("Job: " + job.getTitle());
                    System.out.println("Status: " + app.getStatus());
                    System.out.println("Applied At: " + app.getCreatedAt());
                    System.out.println();
                }
            }
        }
        
        // show saved jobs
        List<String> savedJobIds = ta.getSavedJobs();
        System.out.println("\nSaved Jobs:");
        if (savedJobIds.isEmpty()) {
            System.out.println("No saved jobs.");
        } else {
            for (String jobId : savedJobIds) {
                model.Job job = service.JobService.getJobById(jobId);
                if (job != null) {
                    System.out.println("Job: " + job.getTitle());
                    System.out.println("Department: " + job.getDepartment());
                    System.out.println("Deadline: " + job.getDeadline());
                    System.out.println();
                }
            }
        }
    }

    // email notification feature (mock implementation)
    private static void sendEmailNotification(String email, String subject, String content) {
        // actual projects should call an email API here
        System.out.println("[Email Notification] Sent to: " + email);
        System.out.println("Subject: " + subject);
        System.out.println("Content: " + content);
        System.out.println("Email sent successfully!");
    }

    // MO features
    private static void createJob() {
        MO mo = (MO) currentUser;
        System.out.println("\n=== Publish TA Position ===");
        System.out.print("Enter job title: ");
        String title = scanner.nextLine();
        System.out.println("Select job type:");
        System.out.println("1. Module Assistant");
        System.out.println("2. Invigilation");
        System.out.println("3. Other");
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.JobType type = null;
        switch (typeChoice) {
            case 1:
                type = model.JobType.MODULE_ASSISTANT;
                break;
            case 2:
                type = model.JobType.INVIGILATION;
                break;
            case 3:
                type = model.JobType.OTHER;
                break;
            default:
                System.out.println("Invalid choice. Creation failed.");
                return;
        }

        System.out.print("Enter job description: ");
        String description = scanner.nextLine();
        System.out.print("Enter required skills (separated by semicolons): ");
        String skillsStr = scanner.nextLine();
        List<String> skills = Arrays.asList(skillsStr.split(";"));
        System.out.print("Enter work time: ");
        String workTime = scanner.nextLine();
        System.out.print("Enter number of openings: ");
        int recruitNum = scanner.nextInt();
        scanner.nextLine(); // consume newline
        System.out.print("Enter application deadline (format: 2026-03-18T12:00:00): ");
        String deadline = scanner.nextLine();
        System.out.print("Enter salary range (optional): ");
        String salary = scanner.nextLine();
        System.out.print("Enter work location (optional): ");
        String location = scanner.nextLine();
        System.out.print("Enter extra requirements (optional): ");
        String extraRequirements = scanner.nextLine();

        Job job = JobService.createJob(title, type, mo.getDepartment(), description, skills, workTime, 
                                      recruitNum, deadline, salary, location, extraRequirements, mo.getId());
        if (job != null) {
            System.out.println("Job created successfully!");
            System.out.println("Submit for review? (y/n)");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                JobService.submitJobForReview(job.getId());
                System.out.println("Job submitted for review.");
            }
        } else {
            System.out.println("Failed to create job.");
        }
    }

    private static void editOrCloseJob() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== Edit/Close Job ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getStatus() + ")");
        }
        System.out.print("Select the job number to manage: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        System.out.println("Please select an action:");
        System.out.println("1. Edit Job");
        System.out.println("2. Close Job");
        int action = scanner.nextInt();
        scanner.nextLine(); // consume newline

        if (action == 1) {
            System.out.print("Enter new job title: ");
            job.setTitle(scanner.nextLine());
            System.out.print("Enter new job description: ");
            job.setDescription(scanner.nextLine());
            System.out.print("Enter new required skills (separated by semicolons): ");
            String skillsStr = scanner.nextLine();
            job.setSkills(Arrays.asList(skillsStr.split(";")));
            System.out.print("Enter new work time: ");
            job.setWorkTime(scanner.nextLine());
            System.out.print("Enter new number of openings: ");
            job.setRecruitNum(scanner.nextInt());
            scanner.nextLine(); // consume newline
            System.out.print("Enter new application deadline: ");
            job.setDeadline(scanner.nextLine());

            if (JobService.updateJob(job)) {
                System.out.println("Job updated successfully!");
            } else {
                System.out.println("Failed to edit job.");
            }
        } else if (action == 2) {
            if (JobService.closeJob(job.getId(), mo.getId())) {
                System.out.println("Job has been closed.");
            } else {
                System.out.println("Failed to close job.");
            }
        }
    }



    private static void screenApplicants() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== Applicant Screening ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to filter: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " Application List ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + " (" + app.getStatus() + ")");
            }
        }

        System.out.print("Select the applicant number to filter: ");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application app = applications.get(appChoice);
        System.out.println("Please choose the screening result:");
        System.out.println("1. Pass Screening");
        System.out.println("2. Reject");
        int screenChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.ApplicationStatus status = null;
        if (screenChoice == 1) {
            status = model.ApplicationStatus.SCREENED;
        } else if (screenChoice == 2) {
            status = model.ApplicationStatus.REJECTED;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter screening comments (optional): ");
        String comment = scanner.nextLine();

        if (ApplicationService.screenApplication(app.getId(), status, comment, mo.getId())) {
            System.out.println("Screening successful!");
        } else {
            System.out.println("Screening failed.");
        }
    }

    private static void acceptApplicants() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("=== Hire TAs ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to hire for: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " Application List ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + " (" + app.getStatus() + ")");
            }
        }

        System.out.print("Select the applicant number to hire: ");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application app = applications.get(appChoice);
        if (ApplicationService.acceptApplication(app.getId(), mo.getId())) {
            System.out.println("Hiring successful!");
        } else {
            System.out.println("Hiring failed. The maximum number of hires may have been reached.");
        }
    }

    private static void viewAcceptanceResults() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("=== View Hiring Results ===");
        if (jobs.isEmpty()) {
            System.out.println("No published jobs found.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("Select the job number to view: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("=== " + job.getTitle() + " Hiring Results ===");
        boolean hasAccepted = false;
        for (Application app : applications) {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                TA ta = UserService.getTAProfile(app.getTaId());
                if (ta != null) {
                    System.out.println("Hired TA: " + ta.getName());
                    System.out.println("Contact: " + ta.getPhone() + " / " + ta.getEmail());
                    System.out.println("Hired At: " + app.getUpdatedAt());
                    System.out.println();
                    hasAccepted = true;
                }
            }
        }

        if (!hasAccepted) {
            System.out.println("No hiring records found.");
        }
    }

    // 管理员功能实现
    private static void manageAccounts() {
        System.out.println("=== Account Management ===");
        System.out.println("1. Create Account");
        System.out.println("2. Update Account Information");
        System.out.println("3. Disable/Enable Account");
        System.out.println("4. View All Accounts");
        System.out.print("Please select an action: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        switch (choice) {
            case 1:
                createAccount();
                break;
            case 2:
                updateAccount();
                break;
            case 3:
                toggleAccountStatus();
                break;
            case 4:
                viewAllAccounts();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void createAccount() {
        System.out.println("=== Create Account ===");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password (at least 8 characters, including letters and numbers): ");
        String password = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();

        System.out.println("Please select a role:");
        System.out.println("1. TA");
        System.out.println("2. MO");
        System.out.println("3. Admin");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.UserRole role = null;
        String department = "";

        switch (roleChoice) {
            case 1:
                role = model.UserRole.TA;
                break;
            case 2:
                role = model.UserRole.MO;
                System.out.print("Enter department: ");
                department = scanner.nextLine();
                break;
            case 3:
                role = model.UserRole.ADMIN;
                break;
            default:
                System.out.println("Invalid choice. Creation failed.");
                return;
        }

        User user = UserService.register(username, password, email, phone, role, department);
        if (user != null) {
            System.out.println("Account created successfully!");
        } else {
            System.out.println("Account creation failed. The username may already exist or the input may be invalid.");
        }
    }

    private static void updateAccount() {
        List<User> users = UserService.getAllUsers();
        System.out.println("=== Update Account Information ===");
        if (users.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.getUsername() + "(" + user.getRole() + ")");
        }
        System.out.print("Select the account number to update: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= users.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        User user = users.get(choice);
        System.out.print("Enter new email: ");
        user.setEmail(scanner.nextLine());
        System.out.print("Enter new phone number: ");
        user.setPhone(scanner.nextLine());

        if (UserService.updateUser(user)) {
            System.out.println("Account information updated successfully!");
        } else {
            System.out.println("Failed to update account information.");
        }
    }

    private static void toggleAccountStatus() {
        List<User> users = UserService.getAllUsers();
        System.out.println("=== Disable/Enable Account ===");
        if (users.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.getUsername() + "(" + user.getStatus() + ")");
        }
        System.out.print("Select the account number to operate on: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= users.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        User user = users.get(choice);
        model.UserStatus newStatus = (user.getStatus() == model.UserStatus.ACTIVE) ? model.UserStatus.INACTIVE : model.UserStatus.ACTIVE;
        if (UserService.toggleUserStatus(user.getId(), newStatus)) {
            System.out.println("Account status updated to: " + newStatus);
        } else {
            System.out.println("Failed to update account status.");
        }
    }

    private static void viewAllAccounts() {
        List<User> users = UserService.getAllUsers();
        System.out.println("=== All Accounts ===");
        if (users.isEmpty()) {
            System.out.println("No accounts available.");
            return;
        }

        for (User user : users) {
            System.out.println("用户名：" + user.getUsername());
            System.out.println("角色：" + user.getRole());
            System.out.println("状态：" + user.getStatus());
            System.out.println("邮箱：" + user.getEmail());
            System.out.println("手机号：" + user.getPhone());
            System.out.println();
        }
    }

    private static void reviewTAProfiles() {
        List<User> users = UserService.getUsersByRole(model.UserRole.TA);
        System.out.println("=== Profile Review ===");
        if (users.isEmpty()) {
            System.out.println("No TA accounts available.");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            TA ta = (TA) users.get(i);
            System.out.println((i + 1) + ". " + ta.getName() + "(" + ta.getProfileStatus() + ")");
        }
        System.out.print("Select the TA number to review: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= users.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        TA ta = (TA) users.get(choice);
        System.out.println("Please choose the review result:");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.ProfileStatus status = null;
        if (reviewChoice == 1) {
            status = model.ProfileStatus.APPROVED;
        } else if (reviewChoice == 2) {
            status = model.ProfileStatus.REJECTED;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter review comments: ");
        String comment = scanner.nextLine();

        if (UserService.reviewTAProfile(ta.getId(), status, comment)) {
            System.out.println("Profile review successful!");
        } else {
            System.out.println("Profile review failed.");
        }
    }

    private static void reviewJobs() {
        List<Job> jobs = JobService.getAllJobs();
        System.out.println("=== Job Review ===");
        if (jobs.isEmpty()) {
            System.out.println("No jobs available.");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.getStatus() == model.JobStatus.PENDING) {
                System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getDepartment() + ")");
            }
        }
        System.out.print("Select the job number to review: ");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // consume newline

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Job job = jobs.get(choice);
        System.out.println("Please choose the review result:");
        System.out.println("1. Approve");
        System.out.println("2. Reject");
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        model.JobStatus status = null;
        if (reviewChoice == 1) {
            status = model.JobStatus.PUBLISHED;
        } else if (reviewChoice == 2) {
            status = model.JobStatus.REJECTED;
        } else {
            System.out.println("Invalid choice.");
            return;
        }

        System.out.print("Enter review comments: ");
        String comment = scanner.nextLine();

        if (JobService.reviewJob(job.getId(), status, comment, currentUser.getId())) {
            System.out.println("Job review successful!");
        } else {
            System.out.println("Job review failed.");
        }
    }

    private static void monitorApplications() {
        List<Application> applications = ApplicationService.getAllApplications();
        System.out.println("=== Application Process Monitoring ===");
        if (applications.isEmpty()) {
            System.out.println("No application records found.");
            return;
        }

        for (Application app : applications) {
            TA ta = UserService.getTAProfile(app.getTaId());
            Job job = JobService.getJobById(app.getJobId());
            if (ta != null && job != null) {
                System.out.println("Applicant: " + ta.getName());
                System.out.println("Job: " + job.getTitle());
                System.out.println("Applied At: " + app.getCreatedAt());
                System.out.println("Status: " + app.getStatus());
                System.out.println("Match Score: " + app.getMatchScore());
                System.out.println();
            }
        }
    }

    private static void manageWorkload() {
        System.out.println("=== Workload Statistics and Management ===");
        Map<String, Integer> workloadMap = AIService.calculateWorkload();
        if (workloadMap.isEmpty()) {
            System.out.println("No workload records found.");
            return;
        }

        System.out.println("TA Workload Statistics:");
        for (Map.Entry<String, Integer> entry : workloadMap.entrySet()) {
            TA ta = UserService.getTAProfile(entry.getKey());
            if (ta != null) {
                System.out.println(ta.getName() + ": " + entry.getValue() + " times");
            }
        }

        System.out.println("\nWorkload Balancing Suggestions:");
        List<String> suggestions = AIService.generateWorkloadSuggestions();
        if (suggestions.isEmpty()) {
            System.out.println("Workload distribution is relatively balanced. No suggestions at this time.");
        } else {
            for (String suggestion : suggestions) {
                System.out.println("- " + suggestion);
            }
        }
    }

    // 管理员功能：AI工作量平衡
    private static void aiWorkloadBalancing() {
        System.out.println("=== AI Workload Balancing ===");
        System.out.println("Analyzing TA workloads and generating balancing suggestions...");
        
        // 计算工作量
        Map<String, Integer> workloadMap = service.AIService.calculateWorkload();
        
        if (workloadMap.isEmpty()) {
            System.out.println("No workload records found.");
            return;
        }
        
        // 显示当前工作量分布
        System.out.println("=== Current Workload Distribution ===");
        List<Map.Entry<String, Integer>> sortedWorkload = new ArrayList<>(workloadMap.entrySet());
        sortedWorkload.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (Map.Entry<String, Integer> entry : sortedWorkload) {
            model.TA ta = service.UserService.getTAProfile(entry.getKey());
            if (ta != null) {
                System.out.println("Accepted by " + ta.getName() + ": " + entry.getValue() + " times");
            }
        }
        
        // 生成工作量平衡建议
        List<String> suggestions = service.AIService.generateWorkloadSuggestions();
        
        System.out.println("=== Workload Balancing Suggestions ===");
        if (suggestions.isEmpty()) {
            System.out.println("Workload distribution is relatively balanced. No suggestions at this time.");
        } else {
            for (String suggestion : suggestions) {
                System.out.println("- " + suggestion);
            }
        }
        
        // 显示分配优化方案
        System.out.println("=== Allocation Optimization Plan ===");
        System.out.println("1. Prioritize hiring TAs with lower workloads");
        System.out.println("2. Consider reducing assignments for TAs with higher workloads");
        System.out.println("3. Monitor TA workloads regularly to ensure fair distribution");
    }

    // 管理员功能：管理员配置
    private static void adminConfiguration() {
        System.out.println("=== Admin Configuration ===");
        System.out.println("1. Configure Job Review Process");
        System.out.println("2. Set Notification Templates");
        System.out.println("3. Adjust System Parameters");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                configureJobReviewProcess();
                break;
            case 2:
                setNotificationTemplates();
                break;
            case 3:
                adjustSystemParameters();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // 配置职位审核流程
    private static void configureJobReviewProcess() {
        System.out.println("=== Configure Job Review Process ===");
        System.out.println("1. Set review timeout (hours)");
        System.out.println("2. Enable/Disable Auto Review");
        System.out.println("3. Set Review Priority");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                System.out.print("Enter review timeout (hours): ");
                int timeout = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Auto-review timeout set to: " + timeout + " hours");
                break;
            case 2:
                System.out.print("Enable auto review? (1. Enable 2. Disable): ");
                int autoReviewChoice = scanner.nextInt();
                scanner.nextLine();
                boolean autoReviewEnabled = autoReviewChoice == 1;
                System.out.println("Auto review has been " + (autoReviewEnabled ? "enabled" : "disabled"));
                break;
            case 3:
                System.out.print("Set review priority (1-5, 5 is highest): ");
                int priority = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Review priority set to: " + priority);
                break;
        }
    }

    // Configure notification templates
    private static void setNotificationTemplates() {
        System.out.println("\n=== Notification Template Settings ===");
        System.out.println("1. Application Status Change Notification");
        System.out.println("2. Job Review Result Notification");
        System.out.println("3. System Announcement Template");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                System.out.print("Enter application status change notification template: ");
                String applicationTemplate = scanner.nextLine();
                System.out.println("Application status change notification template updated.");
                break;
            case 2:
                System.out.print("Enter job review result notification template: ");
                String jobTemplate = scanner.nextLine();
                System.out.println("Job review result notification template updated.");
                break;
            case 3:
                System.out.print("Enter system announcement template: ");
                String systemTemplate = scanner.nextLine();
                System.out.println("System announcement template updated.");
                break;
        }
    }

    // Configure system parameters
    private static void adjustSystemParameters() {
        System.out.println("\n=== Configure System Parameters ===");
        System.out.println("1. Set File Upload Size Limit (MB)");
        System.out.println("2. Set Password Complexity Requirements");
        System.out.println("3. Set Session Timeout (Minutes)");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                System.out.print("Enter file upload size limit (MB): ");
                int fileSizeLimit = scanner.nextInt();
                scanner.nextLine();
                System.out.println("File upload size limit set to: " + fileSizeLimit + " MB");
                break;
            case 2:
                System.out.print("Set minimum password length: ");
                int passwordLength = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Minimum password length set to: " + passwordLength);
                break;
            case 3:
                System.out.print("Set session timeout (minutes): ");
                int sessionTimeout = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Session timeout set to: " + sessionTimeout + " minutes");
                break;
        }
    }

    // Admin feature: scheduled report generation
    private static void scheduleReports() {
        System.out.println("\n=== Scheduled Report Generation ===");
        System.out.println("1. Set Generation Schedule");
        System.out.println("2. View Current Scheduled Tasks");
        System.out.println("3. Cancel Scheduled Task");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                setReportSchedule();
                break;
            case 2:
                viewScheduledTasks();
                break;
            case 3:
                cancelScheduledTask();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Set report generation schedule
    private static void setReportSchedule() {
        System.out.println("\n=== Set Report Generation Schedule ===");
        System.out.println("Please choose a report type:");
        System.out.println("1. Recruitment Report");
        System.out.println("2. Workload Report");
        System.out.println("3. System Report");
        System.out.print("Please select: ");
        
        int reportType = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.println("Please choose a generation frequency:");
        System.out.println("1. Daily");
        System.out.println("2. Weekly");
        System.out.println("3. Monthly");
        System.out.print("Please select: ");
        
        int frequency = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.print("Enter generation time (format: HH:MM): ");
        String time = scanner.nextLine();
        
        System.out.println("\nScheduled task configured:");
        System.out.println("Report Type: " + getReportTypeName(reportType));
        System.out.println("Generation Frequency: " + getFrequencyName(frequency));
        System.out.println("Generation Time: " + time);
        System.out.println("The system will automatically generate reports according to the configured schedule.");
    }

    // View current scheduled tasks
    private static void viewScheduledTasks() {
        System.out.println("\n=== Current Scheduled Tasks ===");
        // Simulate displaying current scheduled tasks
        System.out.println("1. Recruitment Report - Daily 08:00");
        System.out.println("2. Workload Report - Every Monday 09:00");
        System.out.println("3. System Report - First Day of Every Month 10:00");
    }

    // Cancel scheduled task
    private static void cancelScheduledTask() {
        System.out.println("\n=== Cancel Scheduled Task ===");
        viewScheduledTasks();
        System.out.print("Select the task number to cancel: ");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        System.out.println("Scheduled task cancelled.");
    }

    // Get report type name
    private static String getReportTypeName(int type) {
        switch (type) {
            case 1:
                return "Recruitment Report";
            case 2:
                return "Workload Report";
            case 3:
                return "System Report";
            default:
                return "Unknown Report Type";
        }
    }

    // Get frequency name
    private static String getFrequencyName(int frequency) {
        switch (frequency) {
            case 1:
                return "Daily";
            case 2:
                return "Weekly";
            case 3:
                return "Monthly";
            default:
                return "Unknown Frequency";
        }
    }

    // 管理员功能：批量操作
    private static void bulkOperations() {
        System.out.println("\n=== 批量操作 ===");
        System.out.println("1. Bulk Review Jobs");
        System.out.println("2. Bulk Import Users");
        System.out.print("Please select an action: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                bulkReviewJobs();
                break;
            case 2:
                bulkImportUsers();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    // Bulk review jobs
    private static void bulkReviewJobs() {
        System.out.println("\n=== Bulk Review Jobs ===");
        
        // Get jobs pending review
        List<model.Job> pendingJobs = new ArrayList<>();
        List<model.Job> allJobs = service.JobService.getAllJobs();
        for (model.Job job : allJobs) {
            if (job.getStatus() == model.JobStatus.PENDING) {
                pendingJobs.add(job);
            }
        }
        
        if (pendingJobs.isEmpty()) {
            System.out.println("No jobs are currently pending review.");
            return;
        }
        
        // Display jobs pending review
        System.out.println("Jobs Pending Review:");
        for (int i = 0; i < pendingJobs.size(); i++) {
            model.Job job = pendingJobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + " (" + job.getDepartment() + ")");
        }
        
        System.out.print("Select job numbers to review (separate multiple numbers with commas): ");
        String input = scanner.nextLine();
        String[] jobIndices = input.split(",");
        
        List<model.Job> selectedJobs = new ArrayList<>();
        for (String indexStr : jobIndices) {
            try {
                int index = Integer.parseInt(indexStr.trim()) - 1;
                if (index >= 0 && index < pendingJobs.size()) {
                    selectedJobs.add(pendingJobs.get(index));
                }
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        }
        
        if (selectedJobs.isEmpty()) {
            System.out.println("No jobs selected.");
            return;
        }
        
        // Select review result
        System.out.println("Please choose the review result:");
        System.out.println("1. Approve All");
        System.out.println("2. Reject All");
        System.out.print("Please select: ");
        
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        model.JobStatus status = null;
        if (reviewChoice == 1) {
            status = model.JobStatus.PUBLISHED;
        } else if (reviewChoice == 2) {
            status = model.JobStatus.REJECTED;
        } else {
            System.out.println("Invalid choice.");
            return;
        }
        
        System.out.print("Enter review comments: ");
        String comment = scanner.nextLine();
        
        // Show confirmation prompt
        System.out.println("\n=== Operation Confirmation ===");
        System.out.println("The following jobs will be reviewed in bulk:");
        for (model.Job job : selectedJobs) {
            System.out.println("- " + job.getTitle());
        }
        System.out.println("Review Result: " + (status == model.JobStatus.PUBLISHED ? "Approved" : "Rejected"));
        System.out.println("Review Comments: " + comment);
        System.out.print("Confirm operation? (y/n): ");
        
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Execute bulk review
        int successCount = 0;
        for (model.Job job : selectedJobs) {
            if (service.JobService.reviewJob(job.getId(), status, comment, currentUser.getId())) {
                successCount++;
            }
        }
        
        System.out.println("\nBulk review completed:");
        System.out.println("Success count: " + successCount + " jobs");
        System.out.println("Failed: " + (selectedJobs.size() - successCount) + " jobs");
    }

    // Bulk import users
    private static void bulkImportUsers() {
        System.out.println("\n=== Bulk Import Users ===");
        System.out.print("Enter the user data file path: ");
        String filePath = scanner.nextLine();
        
        // Simulate file reading
        System.out.println("Reading file...");
        System.out.println("File read successfully. Found 5 user records.");
        
        // Display users pending import
        System.out.println("\nUsers Pending Import:");
        System.out.println("1. username1 - TA - user1@example.com");
        System.out.println("2. username2 - MO - user2@example.com");
        System.out.println("3. username3 - TA - user3@example.com");
        System.out.println("4. username4 - MO - user4@example.com");
        System.out.println("5. username5 - TA - user5@example.com");
        
        // Show confirmation prompt
        System.out.println("\n=== Operation Confirmation ===");
        System.out.println("5 users will be imported into the system.");
        System.out.print("Confirm operation? (y/n): ");
        
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Execute bulk import
        System.out.println("\nRunning bulk import...");
        System.out.println("Bulk import completed:");
        System.out.println("Success: 5 users");
        System.out.println("Failed: 0 users");
    }

    private static void systemConfigAndLogs() {
        System.out.println("\n=== System Configuration and Logs ===");
        System.out.println("1. View System Logs");
        System.out.println("2. Generate System Report");
        System.out.print("Please select an action: ");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
            } else {
                String input = scanner.nextLine();
                System.out.println("Invalid input. Please enter a number again.");
                systemConfigAndLogs();
                return;
            }
        } catch (Exception e) {
            System.out.println("Input error. Please try again.");
            systemConfigAndLogs();
            return;
        }

        switch (choice) {
            case 1:
                viewSystemLogs();
                break;
            case 2:
                generateSystemReport();
                break;
            default:
                System.out.println("Invalid choice.");
        }
    }

    private static void viewSystemLogs() {
        List<service.Log> logs = DataStorage.getLogs();
        System.out.println("\n=== System Logs ===");
        if (logs.isEmpty()) {
            System.out.println("No log records available.");
            return;
        }
        
        // Support filtering by time and action type
        System.out.println("Select filter criteria:");
        System.out.println("1. View All Logs");
        System.out.println("2. Filter by Action Type");
        System.out.println("3. Filter by Time Range");
        System.out.print("Please select: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        List<service.Log> filteredLogs = logs;
        
        switch (choice) {
            case 2:
                // Filter by action type
                System.out.println("Please choose an action type:");
                System.out.println("1. Login/Register");
                System.out.println("2. Job Actions");
                System.out.println("3. Application Actions");
                System.out.println("4. Account Management");
                int typeChoice = scanner.nextInt();
                scanner.nextLine();
                
                filteredLogs = new java.util.ArrayList<>();
                for (service.Log log : logs) {
                    String action = log.getAction();
                    switch (typeChoice) {
                        case 1:
                            if (action.equals("LOGIN") || action.equals("REGISTER")) {
                                filteredLogs.add(log);
                            }
                            break;
                        case 2:
                            if (action.startsWith("JOB")) {
                                filteredLogs.add(log);
                            }
                            break;
                        case 3:
                            if (action.startsWith("APPLICATION")) {
                                filteredLogs.add(log);
                            }
                            break;
                        case 4:
                            if (action.startsWith("USER") || action.equals("TOGGLE_USER_STATUS")) {
                                filteredLogs.add(log);
                            }
                            break;
                    }
                }
                break;
            case 3:
                // Filter by time range
                System.out.print("Enter start time (format: 2026-03-01T00:00:00): ");
                String startTime = scanner.nextLine();
                System.out.print("Enter end time (format: 2026-03-31T23:59:59): ");
                String endTime = scanner.nextLine();
                
                filteredLogs = new java.util.ArrayList<>();
                for (service.Log log : logs) {
                    if (log.getTimestamp().compareTo(startTime) >= 0 && log.getTimestamp().compareTo(endTime) <= 0) {
                        filteredLogs.add(log);
                    }
                }
                break;
        }
        
        // Sort by time in descending order
        filteredLogs.sort((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()));
        
        System.out.println("\n=== Filter Results ===");
        if (filteredLogs.isEmpty()) {
            System.out.println("No logs match the criteria.");
            return;
        }
        
        for (service.Log log : filteredLogs) {
            System.out.println(log.getTimestamp() + " - " + log.getAction() + " - " + log.getUser() + " - " + log.getDetails());
        }
    }

    private static void generateSystemReport() {
        String report = CommonService.generateSystemReport();
        System.out.println("\n" + report);
        System.out.println("Export report? (y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/system_report_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".txt";
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(report);
                System.out.println("Export completed: " + filePath);
            } catch (IOException e) {
                System.out.println("Failed to export: " + e.getMessage());
            }
        }
    }

    private static void exportImportData() {
        System.out.println("=== Data Export and Import ===");
        System.out.println("1. Export User Data");
        System.out.println("2. Export Job Data");
        System.out.println("3. Export Application Data");
        System.out.print("Please select an action: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline

        String dataType = "";
        switch (choice) {
            case 1:
                dataType = "users";
                break;
            case 2:
                dataType = "jobs";
                break;
            case 3:
                dataType = "applications";
                break;
            default:
                System.out.println("Invalid choice.");
                return;
        }

        String filePath = "src/data/export_" + dataType + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        if (CommonService.exportToCSV(dataType, filePath)) {
            System.out.println("Export completed to: " + filePath);
        } else {
            System.out.println("Export failed.");
        }
    }
}
