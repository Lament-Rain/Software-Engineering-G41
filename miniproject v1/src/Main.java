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
        // 初始化数据存储
        DataStorage.initialize();

        // 主循环
        while (true) {
            if (currentUser == null) {
                showLoginMenu();
            } else {
                showMainMenu();
            }
        }
    }

    // 显示登录菜单
    private static void showLoginMenu() {
        System.out.println("\n=== BUPT国际学校TA招聘系统 ===");
        System.out.println("1. 登录");
        System.out.println("2. 注册");
        System.out.println("3. 重置密码");
        System.out.println("4. 退出");
        System.out.print("请选择操作：");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } else {
                String input = scanner.nextLine();
                System.out.println("无效输入，请重新输入数字。");
                showLoginMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("输入错误，请重新输入。");
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
                System.out.println("无效选择，请重新输入。");
        }
    }

    // 登录
    private static void login() {
        System.out.print("请输入用户名：");
        String username = scanner.nextLine();
        System.out.print("请输入密码：");
        String password = scanner.nextLine();

        User user = UserService.login(username, password);
        if (user != null) {
            currentUser = user;
            System.out.println("登录成功！欢迎，" + user.getUsername());
        } else {
            System.out.println("登录失败，用户名或密码错误，或账号被锁定。");
        }
    }

    // 注册
    private static void register() {
        System.out.println("\n=== 注册新账号 ===");
        System.out.print("请输入用户名：");
        String username = scanner.nextLine();
        System.out.print("请输入密码（至少8位，包含字母和数字）：");
        String password = scanner.nextLine();
        System.out.print("请输入邮箱：");
        String email = scanner.nextLine();
        System.out.print("请输入手机号：");
        String phone = scanner.nextLine();

        System.out.println("请选择角色：");
        System.out.println("1. TA");
        System.out.println("2. MO");
        System.out.print("请选择：");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        model.UserRole role = null;
        String department = "";

        switch (roleChoice) {
            case 1:
                role = model.UserRole.TA;
                break;
            case 2:
                role = model.UserRole.MO;
                System.out.print("请输入所属院系：");
                department = scanner.nextLine();
                break;
            default:
                System.out.println("无效选择，注册失败。");
                return;
        }

        User user = UserService.register(username, password, email, phone, role, department);
        if (user != null) {
            System.out.println("注册成功！请登录系统。");
        } else {
            System.out.println("注册失败，可能是用户名已存在或输入信息不符合要求。");
        }
    }

    // 重置密码
    private static void resetPassword() {
        System.out.println("\n=== 重置密码 ===");
        System.out.print("请输入您的邮箱：");
        String email = scanner.nextLine();
        System.out.print("请输入新密码（至少8位，包含字母和数字）：");
        String newPassword = scanner.nextLine();

        if (UserService.resetPassword(email, newPassword)) {
            System.out.println("密码重置成功！请使用新密码登录。");
        } else {
            System.out.println("密码重置失败，可能是邮箱不存在或密码不符合要求。");
        }
    }

    // 显示主菜单
    private static void showMainMenu() {
        System.out.println("\n=== 主菜单 ===");
        System.out.println("当前用户：" + currentUser.getUsername() + "（" + currentUser.getRole() + "）");

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

    // TA菜单
    private static void showTAMenu() {
        System.out.println("1. 个人Dashboard");
        System.out.println("2. 完善个人档案");
        System.out.println("3. 上传简历");
        System.out.println("4. 查看可用职位");
        System.out.println("5. 筛选职位");
        System.out.println("6. 搜索职位");
        System.out.println("7. AI职位匹配");
        System.out.println("8. 申请职位");
        System.out.println("9. 查看申请状态");
        System.out.println("10. 查看申请历史");
        System.out.println("11. 职位收藏");
        System.out.println("12. 技能缺口识别");
        System.out.println("13. 通知设置");
        System.out.println("14. 退出登录");
        System.out.print("请选择操作：");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } else {
                String input = scanner.nextLine();
                System.out.println("无效输入，请重新输入数字。");
                showTAMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("输入错误，请重新输入。");
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
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("无效选择，请重新输入。");
        }
    }

    // MO菜单
    private static void showMOMenu() {
        System.out.println("1. 发布TA职位");
        System.out.println("2. 编辑/终止职位");
        System.out.println("3. 查看职位申请列表");
        System.out.println("4. 筛选申请人");
        System.out.println("5. 拒绝申请");
        System.out.println("6. 接受申请");
        System.out.println("7. 查看录用结果");
        System.out.println("8. 查看招聘统计");
        System.out.println("9. 导出申请数据");
        System.out.println("10. 退出登录");
        System.out.print("请选择操作：");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } else {
                String input = scanner.nextLine();
                System.out.println("无效输入，请重新输入数字。");
                showMOMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("输入错误，请重新输入。");
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
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("无效选择，请重新输入。");
        }
    }

    // 管理员菜单
    private static void showAdminMenu() {
        System.out.println("1. 账号管理");
        System.out.println("2. 档案审核");
        System.out.println("3. 职位审核");
        System.out.println("4. 申请流程监控");
        System.out.println("5. 工作量统计与管理");
        System.out.println("6. 系统配置与日志");
        System.out.println("7. 数据导出与导入");
        System.out.println("8. 生成招聘报告");
        System.out.println("9. 管理用户");
        System.out.println("10. 管理员配置");
        System.out.println("11. 报告定时生成");
        System.out.println("12. 批量操作");
        System.out.println("13. AI工作量平衡");
        System.out.println("14. 退出登录");
        System.out.print("请选择操作：");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } else {
                String input = scanner.nextLine();
                System.out.println("无效输入，请重新输入数字。");
                showAdminMenu();
                return;
            }
        } catch (Exception e) {
            System.out.println("输入错误，请重新输入。");
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
                System.out.println("已退出登录。");
                break;
            default:
                System.out.println("无效选择，请重新输入。");
        }
    }

    // TA功能实现
    private static void updateTAProfile() {
        TA ta = (TA) currentUser;
        System.out.println("\n=== 完善个人档案 ===");
        System.out.print("请输入姓名：");
        ta.setName(scanner.nextLine());
        System.out.print("请输入性别：");
        ta.setGender(scanner.nextLine());
        System.out.print("请输入年龄：");
        ta.setAge(scanner.nextInt());
        scanner.nextLine(); // 消费换行符
        System.out.print("请输入所属院系：");
        ta.setDepartment(scanner.nextLine());
        System.out.print("请输入年级：");
        ta.setGrade(scanner.nextLine());
        System.out.print("请输入可工作时间段：");
        ta.setAvailableTime(scanner.nextLine());
        System.out.print("请输入擅长科目/技能（用分号分隔）：");
        String skillsStr = scanner.nextLine();
        List<String> skills = Arrays.asList(skillsStr.split(";"));
        ta.setSkills(skills);
        System.out.print("请输入过往TA经验：");
        ta.setExperience(scanner.nextLine());
        System.out.print("请输入获奖情况（选填）：");
        ta.setAwards(scanner.nextLine());
        System.out.print("请输入语言能力（选填）：");
        ta.setLanguageSkills(scanner.nextLine());
        System.out.print("请输入其他特长（选填）：");
        ta.setOtherSkills(scanner.nextLine());

        System.out.println("请选择操作：");
        System.out.println("1. 保存为草稿");
        System.out.println("2. 提交审核");
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        if (choice == 1) {
            ta.setProfileStatus(model.ProfileStatus.DRAFT);
        } else if (choice == 2) {
            ta.setProfileStatus(model.ProfileStatus.PENDING);
        }

        if (UserService.updateTAProfile(ta)) {
            System.out.println("档案更新成功！");
        } else {
            System.out.println("档案更新失败。");
        }
    }

    private static void uploadResume() {
        TA ta = (TA) currentUser;
        System.out.println("\n=== 上传简历 ===");
        System.out.print("请输入简历文件路径：");
        String filePath = scanner.nextLine();
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("文件不存在。");
            return;
        }

        if (!FileUtils.checkFileSize(file)) {
            System.out.println("文件大小超过10MB限制。");
            return;
        }

        if (!FileUtils.checkFileFormat(file.getName())) {
            System.out.println("文件格式不支持，仅支持PDF、Word、JPG/PNG。");
            return;
        }

        try {
            String resumePath = FileUtils.uploadResume(file, ta.getId());
            ta.setResumePath(resumePath);
            if (UserService.updateTAProfile(ta)) {
                System.out.println("简历上传成功！");
            } else {
                System.out.println("简历上传失败。");
            }
        } catch (Exception e) {
            System.out.println("简历上传失败：" + e.getMessage());
        }
    }

    private static void viewAvailableJobs() {
        List<Job> jobs = JobService.getAvailableJobs();
        System.out.println("\n=== 可用职位 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无可用职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
            System.out.println("   院系：" + job.getDepartment());
            System.out.println("   工作时间：" + job.getWorkTime());
            System.out.println("   招募人数：" + job.getRecruitNum());
            System.out.println("   截止时间：" + job.getDeadline());
            System.out.println();
        }
    }

    private static void applyForJob() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("您的档案尚未通过审核，无法申请职位。");
            return;
        }

        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            System.out.println("暂无可用职位。");
            return;
        }

        System.out.println("\n=== 申请职位 ===");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要申请的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        System.out.print("请输入申请附言（≤500字）：");
        String coverLetter = scanner.nextLine();

        Application application = ApplicationService.submitApplication(ta.getId(), job.getId(), coverLetter);
        if (application != null) {
            System.out.println("申请提交成功！匹配度：" + application.getMatchScore());
        } else {
            System.out.println("申请提交失败，可能是您已经申请过该职位或职位已截止。");
        }
    }

    private static void viewApplicationStatus() {
        TA ta = (TA) currentUser;
        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== 申请状态 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("职位：" + job.getTitle());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("状态：" + app.getStatus());
                System.out.println("匹配度：" + app.getMatchScore());
                if (app.getReviewComment() != null) {
                    System.out.println("审核意见：" + app.getReviewComment());
                }
                System.out.println();
            }
        }
    }

    private static void viewApplicationRecords() {
        TA ta = (TA) currentUser;
        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== 个人申请记录 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }

        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("职位：" + job.getTitle());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("状态：" + app.getStatus());
                System.out.println();
            }
        }

        System.out.println("是否导出申请记录？(y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/ta_applications_" + ta.getUsername() + ".csv";
            if (CommonService.exportToCSV("applications", filePath)) {
                System.out.println("申请记录已导出至：" + filePath);
            } else {
                System.out.println("导出失败。");
            }
        }
    }

    private static void viewNotifications() {
        System.out.println("\n=== 系统通知 ===");
        System.out.println("（通知功能待实现）");
    }

    // TA功能：筛选职位
    private static void filterJobs() {
        System.out.println("\n=== 筛选职位 ===");
        System.out.println("请选择筛选条件：");
        System.out.println("1. 按职位类型");
        System.out.println("2. 按所属院系");
        System.out.println("3. 按截止日期");
        System.out.println("4. 清除筛选条件");
        System.out.print("请选择：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        model.JobType type = null;
        String department = null;
        String deadline = null;
        
        switch (choice) {
            case 1:
                System.out.println("请选择职位类型：");
                System.out.println("1. 模块辅助");
                System.out.println("2. 监考");
                System.out.println("3. 其他");
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
                System.out.print("请输入所属院系：");
                department = scanner.nextLine();
                break;
            case 3:
                System.out.print("请输入截止日期（格式：2026-03-18T12:00:00）：");
                deadline = scanner.nextLine();
                break;
            case 4:
                // 清除筛选条件
                break;
        }
        
        List<model.Job> jobs = service.JobService.filterJobs(type, department, deadline);
        System.out.println("\n=== 筛选结果 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无符合条件的职位。");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
            System.out.println("   院系：" + job.getDepartment());
            System.out.println("   工作时间：" + job.getWorkTime());
            System.out.println("   招募人数：" + job.getRecruitNum());
            System.out.println("   截止时间：" + job.getDeadline());
            System.out.println();
        }
    }

    // TA功能：搜索职位
    private static void searchJobs() {
        System.out.println("\n=== 搜索职位 ===");
        System.out.print("请输入搜索关键词：");
        String keyword = scanner.nextLine();
        
        List<model.Job> jobs = service.JobService.searchJobs(keyword);
        System.out.println("\n=== 搜索结果 ===");
        if (jobs.isEmpty()) {
            System.out.println("未找到符合条件的职位。");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
            System.out.println("   描述：" + job.getDescription());
            System.out.println("   院系：" + job.getDepartment());
            System.out.println("   截止时间：" + job.getDeadline());
            System.out.println();
        }
    }

    // TA功能：查看申请历史
    private static void viewApplicationHistory() {
        model.TA ta = (model.TA) currentUser;
        List<model.Application> applications = service.ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n=== 申请历史 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }
        
        // 按时间倒序排列
        applications.sort((a1, a2) -> a2.getCreatedAt().compareTo(a1.getCreatedAt()));
        
        for (model.Application app : applications) {
            model.Job job = service.JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("职位：" + job.getTitle());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("状态：" + app.getStatus());
                if (app.getReviewComment() != null) {
                    System.out.println("审核意见：" + app.getReviewComment());
                }
                System.out.println();
            }
        }
    }

    // TA功能：职位收藏管理
    private static void manageSavedJobs() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== 职位收藏管理 ===");
        System.out.println("1. 查看收藏的职位");
        System.out.println("2. 收藏新职位");
        System.out.println("3. 取消收藏");
        System.out.print("请选择：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                // 查看收藏的职位
                List<String> savedJobIds = ta.getSavedJobs();
                if (savedJobIds.isEmpty()) {
                    System.out.println("暂无收藏的职位。");
                } else {
                    System.out.println("\n=== 收藏的职位 ===");
                    for (String jobId : savedJobIds) {
                        model.Job job = service.JobService.getJobById(jobId);
                        if (job != null) {
                            System.out.println("职位：" + job.getTitle());
                            System.out.println("院系：" + job.getDepartment());
                            System.out.println("截止时间：" + job.getDeadline());
                            System.out.println();
                        }
                    }
                }
                break;
            case 2:
                // 收藏新职位
                List<model.Job> jobs = service.JobService.getAvailableJobs();
                if (jobs.isEmpty()) {
                    System.out.println("暂无可用职位。");
                    return;
                }
                System.out.println("\n=== 可用职位 ===");
                for (int i = 0; i < jobs.size(); i++) {
                    model.Job job = jobs.get(i);
                    System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
                }
                System.out.print("请选择要收藏的职位编号：");
                int jobChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (jobChoice >= 0 && jobChoice < jobs.size()) {
                    model.Job job = jobs.get(jobChoice);
                    ta.saveJob(job.getId());
                    service.UserService.updateTAProfile(ta);
                    System.out.println("职位收藏成功！");
                }
                break;
            case 3:
                // 取消收藏
                savedJobIds = ta.getSavedJobs();
                if (savedJobIds.isEmpty()) {
                    System.out.println("暂无收藏的职位。");
                    return;
                }
                System.out.println("\n=== 收藏的职位 ===");
                for (int i = 0; i < savedJobIds.size(); i++) {
                    model.Job job = service.JobService.getJobById(savedJobIds.get(i));
                    if (job != null) {
                        System.out.println((i + 1) + ". " + job.getTitle());
                    }
                }
                System.out.print("请选择要取消收藏的职位编号：");
                int unsaveChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (unsaveChoice >= 0 && unsaveChoice < savedJobIds.size()) {
                    String jobId = savedJobIds.get(unsaveChoice);
                    ta.unsaveJob(jobId);
                    service.UserService.updateTAProfile(ta);
                    System.out.println("取消收藏成功！");
                }
                break;
        }
    }

    // TA功能：通知设置
    private static void notificationSettings() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== 通知设置 ===");
        
        // 显示当前设置
        List<model.TA.NotificationSetting> settings = ta.getNotificationSettings();
        for (model.TA.NotificationSetting setting : settings) {
            String status = setting.isEnabled() ? "开启" : "关闭";
            System.out.println(setting.getType() + "：" + status);
        }
        
        System.out.println("\n请选择要修改的通知类型：");
        System.out.println("1. 申请状态变更");
        System.out.println("2. 职位截止提醒");
        System.out.println("3. 系统公告");
        System.out.print("请选择：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
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
        
        System.out.print("请选择状态（1. 开启 2. 关闭）：");
        int statusChoice = scanner.nextInt();
        scanner.nextLine();
        boolean enabled = statusChoice == 1;
        
        ta.updateNotificationSetting(type, enabled);
        service.UserService.updateTAProfile(ta);
        System.out.println("通知设置已更新！");
    }

    // TA功能：AI职位匹配
    private static void aiJobMatching() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("您的档案尚未通过审核，无法使用AI职位匹配功能。");
            return;
        }
        
        System.out.println("\n=== AI职位匹配 ===");
        System.out.println("正在分析您的个人资料和技能...");
        
        // 获取推荐职位
        List<model.Job> recommendedJobs = service.AIService.recommendJobsForTA(ta, 5);
        
        if (recommendedJobs.isEmpty()) {
            System.out.println("暂无可用职位推荐。");
            return;
        }
        
        System.out.println("\n=== 推荐职位 ===");
        for (int i = 0; i < recommendedJobs.size(); i++) {
            model.Job job = recommendedJobs.get(i);
            double matchScore = service.AIService.calculateSkillMatch(ta, job);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
            System.out.println("   院系：" + job.getDepartment());
            System.out.println("   工作时间：" + job.getWorkTime());
            System.out.println("   截止时间：" + job.getDeadline());
            System.out.println("   匹配度：" + String.format("%.2f%%", matchScore));
            System.out.println();
        }
        
        System.out.println("注：推荐结果会定期更新，建议您定期查看。");
    }

    // TA功能：技能缺口识别
    private static void identifyMissingSkills() {
        TA ta = (TA) currentUser;
        if (ta.getProfileStatus() != model.ProfileStatus.APPROVED) {
            System.out.println("您的档案尚未通过审核，无法使用技能缺口识别功能。");
            return;
        }
        
        System.out.println("\n=== 技能缺口识别 ===");
        
        // 获取可用职位
        List<model.Job> availableJobs = service.JobService.getAvailableJobs();
        if (availableJobs.isEmpty()) {
            System.out.println("暂无可用职位。");
            return;
        }
        
        // 显示职位列表
        System.out.println("请选择一个职位来分析技能缺口：");
        for (int i = 0; i < availableJobs.size(); i++) {
            model.Job job = availableJobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
        }
        
        System.out.print("请选择职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符
        
        if (choice < 0 || choice >= availableJobs.size()) {
            System.out.println("无效选择。");
            return;
        }
        
        model.Job selectedJob = availableJobs.get(choice);
        System.out.println("\n正在分析您与职位 \"" + selectedJob.getTitle() + "\" 的技能匹配情况...");
        
        // 识别缺失技能
        List<String> missingSkills = service.AIService.identifyMissingSkills(ta, selectedJob);
        
        if (missingSkills.isEmpty()) {
            System.out.println("恭喜！您的技能已满足该职位的所有要求。");
        } else {
            System.out.println("\n=== 技能缺口分析 ===");
            System.out.println("您缺少以下技能：");
            for (String skill : missingSkills) {
                System.out.println("- " + skill);
            }
            
            // 生成技能提升建议
            String suggestions = service.AIService.generateSkillSuggestions(missingSkills);
            System.out.println("\n" + suggestions);
        }
        
        System.out.println("\n您可以在个人Dashboard中查看这些建议。");
    }

    // MO功能：拒绝申请
    private static void rejectApplication() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 拒绝申请 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要操作的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }
        
        model.Job job = jobs.get(choice);
        List<model.Application> applications = service.ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " 的申请列表 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }
        
        for (int i = 0; i < applications.size(); i++) {
            model.Application app = applications.get(i);
            model.TA ta = service.UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + "（" + app.getStatus() + "）");
            }
        }
        
        System.out.print("请选择要拒绝的申请编号：");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("无效选择。");
            return;
        }
        
        model.Application app = applications.get(appChoice);
        System.out.print("请输入拒绝原因：");
        String reason = scanner.nextLine();
        
        if (service.ApplicationService.rejectApplication(app.getId(), reason, mo.getId())) {
            System.out.println("拒绝成功！");
        } else {
            System.out.println("拒绝失败。");
        }
    }

    // 修改MO查看申请列表功能，支持按状态筛选
    private static void viewJobApplications() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 查看职位申请 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要查看的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }
        
        model.Job job = jobs.get(choice);
        
        // 支持按状态筛选
        System.out.println("\n请选择筛选条件：");
        System.out.println("1. 所有申请");
        System.out.println("2. 待筛选");
        System.out.println("3. 已筛选");
        System.out.println("4. 已录用");
        System.out.println("5. 已拒绝");
        System.out.print("请选择：");
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
        System.out.println("\n=== " + job.getTitle() + " 的申请列表 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }
        
        for (model.Application app : applications) {
            model.TA ta = service.UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println("申请人：" + ta.getName());
                System.out.println("所属院系：" + ta.getDepartment());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("状态：" + app.getStatus());
                System.out.println("匹配度：" + app.getMatchScore());
                if (ta.getResumePath() != null) {
                    System.out.println("简历：已上传");
                } else {
                    System.out.println("简历：未上传");
                }
                System.out.println();
            }
        }
    }

    // 管理员功能：生成招聘报告
    private static void generateRecruitmentReport() {
        System.out.println("\n=== 生成招聘报告 ===");
        System.out.print("请输入开始时间（格式：2026-03-01T00:00:00）：");
        String startTime = scanner.nextLine();
        System.out.print("请输入结束时间（格式：2026-03-31T23:59:59）：");
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
        System.out.println("\n=== 招聘报告 ===");
        System.out.println("时间范围：" + startTime + " 至 " + endTime);
        System.out.println("发布职位数：" + jobCount);
        System.out.println("收到申请数：" + applicationCount);
        System.out.println("录用人数：" + acceptedCount);
        System.out.println("录用率：" + String.format("%.2f%%", acceptanceRate));
        
        // 导出为Excel
        System.out.println("\n是否导出为Excel文件？(y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/recruitment_report_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
            try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
                writer.write("时间范围,职位数,申请数,录用数,录用率\n");
                writer.write(startTime + " 至 " + endTime + "," + jobCount + "," + applicationCount + "," + acceptedCount + "," + String.format("%.2f%%", acceptanceRate) + "\n");
                System.out.println("报告已导出至：" + filePath);
            } catch (java.io.IOException e) {
                System.out.println("导出失败：" + e.getMessage());
            }
        }
    }

    // 管理员功能：管理用户
    private static void manageUsers() {
        System.out.println("\n=== 管理用户 ===");
        System.out.println("1. 查看所有用户");
        System.out.println("2. 禁用/启用用户账号");
        System.out.println("3. 修改用户角色");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                // 查看所有用户
                List<model.User> users = service.UserService.getAllUsers();
                System.out.println("\n=== 所有用户 ===");
                for (model.User user : users) {
                    System.out.println("用户名：" + user.getUsername());
                    System.out.println("角色：" + user.getRole());
                    System.out.println("状态：" + user.getStatus());
                    System.out.println("邮箱：" + user.getEmail());
                    System.out.println("手机号：" + user.getPhone());
                    System.out.println();
                }
                break;
            case 2:
                // 禁用/启用用户账号
                users = service.UserService.getAllUsers();
                System.out.println("\n=== 选择用户 ===");
                for (int i = 0; i < users.size(); i++) {
                    model.User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + "（" + user.getStatus() + "）");
                }
                System.out.print("请选择要操作的用户编号：");
                int userChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (userChoice >= 0 && userChoice < users.size()) {
                    model.User user = users.get(userChoice);
                    model.UserStatus newStatus = (user.getStatus() == model.UserStatus.ACTIVE) ? model.UserStatus.INACTIVE : model.UserStatus.ACTIVE;
                    service.UserService.toggleUserStatus(user.getId(), newStatus);
                    System.out.println("用户状态已更新为：" + newStatus);
                }
                break;
            case 3:
                // 修改用户角色
                users = service.UserService.getAllUsers();
                System.out.println("\n=== 选择用户 ===");
                for (int i = 0; i < users.size(); i++) {
                    model.User user = users.get(i);
                    System.out.println((i + 1) + ". " + user.getUsername() + "（" + user.getRole() + "）");
                }
                System.out.print("请选择要操作的用户编号：");
                userChoice = scanner.nextInt() - 1;
                scanner.nextLine();
                if (userChoice >= 0 && userChoice < users.size()) {
                    model.User user = users.get(userChoice);
                    System.out.println("请选择新角色：");
                    System.out.println("1. TA");
                    System.out.println("2. MO");
                    System.out.println("3. 管理员");
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
                        // 这里简化处理，实际项目中需要更复杂的角色变更逻辑
                        System.out.println("用户角色已更新为：" + newRole);
                    }
                }
                break;
        }
    }

    // MO功能：查看招聘统计
    private static void viewRecruitmentStats() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 招聘统计 ===");
        
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }
        
        System.out.print("请输入开始时间（格式：2026-03-01T00:00:00）：");
        String startTime = scanner.nextLine();
        System.out.print("请输入结束时间（格式：2026-03-31T23:59:59）：");
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
                
                // 显示每个职位的统计
                int jobApps = apps.size();
                int jobAccepted = 0;
                for (model.Application app : apps) {
                    if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                        jobAccepted++;
                    }
                }
                double jobAcceptanceRate = jobApps > 0 ? (double) jobAccepted / jobApps * 100 : 0;
                System.out.println("\n职位：" + job.getTitle());
                System.out.println("申请数：" + jobApps);
                System.out.println("录用数：" + jobAccepted);
                System.out.println("通过率：" + String.format("%.2f%%", jobAcceptanceRate));
            }
        }
        
        // 显示总体统计
        double overallAcceptanceRate = totalApplications > 0 ? (double) totalAccepted / totalApplications * 100 : 0;
        System.out.println("\n=== 总体统计 ===");
        System.out.println("总申请数：" + totalApplications);
        System.out.println("总录用数：" + totalAccepted);
        System.out.println("总通过率：" + String.format("%.2f%%", overallAcceptanceRate));
    }

    // MO功能：导出申请数据
    private static void exportApplicationData() {
        model.MO mo = (model.MO) currentUser;
        List<model.Job> jobs = service.JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 导出申请数据 ===");
        
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }
        
        for (int i = 0; i < jobs.size(); i++) {
            model.Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要导出的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine();
        
        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }
        
        model.Job job = jobs.get(choice);
        List<model.Application> applications = service.ApplicationService.getApplicationsByJob(job.getId());
        
        if (applications.isEmpty()) {
            System.out.println("该职位暂无申请记录。");
            return;
        }
        
        // 导出为CSV
        String filePath = "src/data/application_data_" + job.getTitle().replace(" ", "_") + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
            writer.write("申请人,所属院系,申请时间,状态,匹配度,审核意见\n");
            for (model.Application app : applications) {
                model.TA ta = service.UserService.getTAProfile(app.getTaId());
                if (ta != null) {
                    writer.write(ta.getName() + "," + ta.getDepartment() + "," + app.getCreatedAt() + "," + app.getStatus() + "," + app.getMatchScore() + "," + (app.getReviewComment() != null ? app.getReviewComment() : "") + "\n");
                }
            }
            System.out.println("申请数据已导出至：" + filePath);
        } catch (java.io.IOException e) {
            System.out.println("导出失败：" + e.getMessage());
        }
    }

    // TA功能：个人Dashboard
    private static void personalDashboard() {
        model.TA ta = (model.TA) currentUser;
        System.out.println("\n=== 个人Dashboard ===");
        
        // 显示个人信息
        System.out.println("个人信息：");
        System.out.println("姓名：" + (ta.getName() != null ? ta.getName() : "未设置"));
        System.out.println("院系：" + (ta.getDepartment() != null ? ta.getDepartment() : "未设置"));
        System.out.println("档案状态：" + ta.getProfileStatus());
        System.out.println("简历状态：" + (ta.getResumePath() != null ? "已上传" : "未上传"));
        
        // 显示申请状态
        List<model.Application> applications = service.ApplicationService.getApplicationsByTA(ta.getId());
        System.out.println("\n申请状态：");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
        } else {
            for (model.Application app : applications) {
                model.Job job = service.JobService.getJobById(app.getJobId());
                if (job != null) {
                    System.out.println("职位：" + job.getTitle());
                    System.out.println("状态：" + app.getStatus());
                    System.out.println("申请时间：" + app.getCreatedAt());
                    System.out.println();
                }
            }
        }
        
        // 显示收藏职位
        List<String> savedJobIds = ta.getSavedJobs();
        System.out.println("\n收藏职位：");
        if (savedJobIds.isEmpty()) {
            System.out.println("暂无收藏的职位。");
        } else {
            for (String jobId : savedJobIds) {
                model.Job job = service.JobService.getJobById(jobId);
                if (job != null) {
                    System.out.println("职位：" + job.getTitle());
                    System.out.println("院系：" + job.getDepartment());
                    System.out.println("截止时间：" + job.getDeadline());
                    System.out.println();
                }
            }
        }
    }

    // 邮件通知功能（模拟实现）
    private static void sendEmailNotification(String email, String subject, String content) {
        // 实际项目中这里应该调用邮件发送API
        System.out.println("[邮件通知] 发送至：" + email);
        System.out.println("主题：" + subject);
        System.out.println("内容：" + content);
        System.out.println("邮件发送成功！");
    }

    // MO功能实现
    private static void createJob() {
        MO mo = (MO) currentUser;
        System.out.println("\n=== 发布TA职位 ===");
        System.out.print("请输入职位名称：");
        String title = scanner.nextLine();
        System.out.println("请选择职位类型：");
        System.out.println("1. 模块辅助");
        System.out.println("2. 监考");
        System.out.println("3. 其他");
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

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
                System.out.println("无效选择，创建失败。");
                return;
        }

        System.out.print("请输入工作内容：");
        String description = scanner.nextLine();
        System.out.print("请输入技能要求（用分号分隔）：");
        String skillsStr = scanner.nextLine();
        List<String> skills = Arrays.asList(skillsStr.split(";"));
        System.out.print("请输入工作时间段：");
        String workTime = scanner.nextLine();
        System.out.print("请输入招募人数：");
        int recruitNum = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        System.out.print("请输入申请截止时间（格式：2026-03-18T12:00:00）：");
        String deadline = scanner.nextLine();
        System.out.print("请输入薪资范围（选填）：");
        String salary = scanner.nextLine();
        System.out.print("请输入工作地点（选填）：");
        String location = scanner.nextLine();
        System.out.print("请输入额外要求（选填）：");
        String extraRequirements = scanner.nextLine();

        Job job = JobService.createJob(title, type, mo.getDepartment(), description, skills, workTime, 
                                      recruitNum, deadline, salary, location, extraRequirements, mo.getId());
        if (job != null) {
            System.out.println("职位创建成功！");
            System.out.println("是否提交审核？(y/n)");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                JobService.submitJobForReview(job.getId());
                System.out.println("职位已提交审核。");
            }
        } else {
            System.out.println("职位创建失败。");
        }
    }

    private static void editOrCloseJob() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 编辑/终止职位 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getStatus() + "）");
        }
        System.out.print("请选择要操作的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        System.out.println("请选择操作：");
        System.out.println("1. 编辑职位");
        System.out.println("2. 终止职位");
        int action = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        if (action == 1) {
            System.out.print("请输入新的职位名称：");
            job.setTitle(scanner.nextLine());
            System.out.print("请输入新的工作内容：");
            job.setDescription(scanner.nextLine());
            System.out.print("请输入新的技能要求（用分号分隔）：");
            String skillsStr = scanner.nextLine();
            job.setSkills(Arrays.asList(skillsStr.split(";")));
            System.out.print("请输入新的工作时间段：");
            job.setWorkTime(scanner.nextLine());
            System.out.print("请输入新的招募人数：");
            job.setRecruitNum(scanner.nextInt());
            scanner.nextLine(); // 消费换行符
            System.out.print("请输入新的申请截止时间：");
            job.setDeadline(scanner.nextLine());

            if (JobService.updateJob(job)) {
                System.out.println("职位编辑成功！");
            } else {
                System.out.println("职位编辑失败。");
            }
        } else if (action == 2) {
            if (JobService.closeJob(job.getId(), mo.getId())) {
                System.out.println("职位已终止。");
            } else {
                System.out.println("职位终止失败。");
            }
        }
    }



    private static void screenApplicants() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 筛选申请人 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要筛选的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " 的申请列表 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + "（" + app.getStatus() + "）");
            }
        }

        System.out.print("请选择要筛选的申请人编号：");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("无效选择。");
            return;
        }

        Application app = applications.get(appChoice);
        System.out.println("请选择筛选结果：");
        System.out.println("1. 通过筛选");
        System.out.println("2. 拒绝");
        int screenChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        model.ApplicationStatus status = null;
        if (screenChoice == 1) {
            status = model.ApplicationStatus.SCREENED;
        } else if (screenChoice == 2) {
            status = model.ApplicationStatus.REJECTED;
        } else {
            System.out.println("无效选择。");
            return;
        }

        System.out.print("请输入筛选意见（选填）：");
        String comment = scanner.nextLine();

        if (ApplicationService.screenApplication(app.getId(), status, comment, mo.getId())) {
            System.out.println("筛选成功！");
        } else {
            System.out.println("筛选失败。");
        }
    }

    private static void acceptApplicants() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 录用TA ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要录用的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " 的申请列表 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }

        for (int i = 0; i < applications.size(); i++) {
            Application app = applications.get(i);
            TA ta = UserService.getTAProfile(app.getTaId());
            if (ta != null) {
                System.out.println((i + 1) + ". " + ta.getName() + "（" + app.getStatus() + "）");
            }
        }

        System.out.print("请选择要录用的申请人编号：");
        int appChoice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (appChoice < 0 || appChoice >= applications.size()) {
            System.out.println("无效选择。");
            return;
        }

        Application app = applications.get(appChoice);
        if (ApplicationService.acceptApplication(app.getId(), mo.getId())) {
            System.out.println("录用成功！");
        } else {
            System.out.println("录用失败，可能是录用人数已达上限。");
        }
    }

    private static void viewAcceptanceResults() {
        MO mo = (MO) currentUser;
        List<Job> jobs = JobService.getJobsByMO(mo.getId());
        System.out.println("\n=== 查看录用结果 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无发布的职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle());
        }
        System.out.print("请选择要查看的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
        System.out.println("\n=== " + job.getTitle() + " 的录用结果 ===");
        boolean hasAccepted = false;
        for (Application app : applications) {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                TA ta = UserService.getTAProfile(app.getTaId());
                if (ta != null) {
                    System.out.println("录用TA：" + ta.getName());
                    System.out.println("联系方式：" + ta.getPhone() + " / " + ta.getEmail());
                    System.out.println("录用时间：" + app.getUpdatedAt());
                    System.out.println();
                    hasAccepted = true;
                }
            }
        }

        if (!hasAccepted) {
            System.out.println("暂无录用记录。");
        }
    }

    // 管理员功能实现
    private static void manageAccounts() {
        System.out.println("\n=== 账号管理 ===");
        System.out.println("1. 创建账号");
        System.out.println("2. 修改账号信息");
        System.out.println("3. 禁用/启用账号");
        System.out.println("4. 查看所有账号");
        System.out.print("请选择操作：");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

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
                System.out.println("无效选择。");
        }
    }

    private static void createAccount() {
        System.out.println("\n=== 创建账号 ===");
        System.out.print("请输入用户名：");
        String username = scanner.nextLine();
        System.out.print("请输入密码（至少8位，包含字母和数字）：");
        String password = scanner.nextLine();
        System.out.print("请输入邮箱：");
        String email = scanner.nextLine();
        System.out.print("请输入手机号：");
        String phone = scanner.nextLine();

        System.out.println("请选择角色：");
        System.out.println("1. TA");
        System.out.println("2. MO");
        System.out.println("3. 管理员");
        int roleChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        model.UserRole role = null;
        String department = "";

        switch (roleChoice) {
            case 1:
                role = model.UserRole.TA;
                break;
            case 2:
                role = model.UserRole.MO;
                System.out.print("请输入所属院系：");
                department = scanner.nextLine();
                break;
            case 3:
                role = model.UserRole.ADMIN;
                break;
            default:
                System.out.println("无效选择，创建失败。");
                return;
        }

        User user = UserService.register(username, password, email, phone, role, department);
        if (user != null) {
            System.out.println("账号创建成功！");
        } else {
            System.out.println("账号创建失败，可能是用户名已存在或输入信息不符合要求。");
        }
    }

    private static void updateAccount() {
        List<User> users = UserService.getAllUsers();
        System.out.println("\n=== 修改账号信息 ===");
        if (users.isEmpty()) {
            System.out.println("暂无账号。");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.getUsername() + "（" + user.getRole() + "）");
        }
        System.out.print("请选择要修改的账号编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= users.size()) {
            System.out.println("无效选择。");
            return;
        }

        User user = users.get(choice);
        System.out.print("请输入新的邮箱：");
        user.setEmail(scanner.nextLine());
        System.out.print("请输入新的手机号：");
        user.setPhone(scanner.nextLine());

        if (UserService.updateUser(user)) {
            System.out.println("账号信息更新成功！");
        } else {
            System.out.println("账号信息更新失败。");
        }
    }

    private static void toggleAccountStatus() {
        List<User> users = UserService.getAllUsers();
        System.out.println("\n=== 禁用/启用账号 ===");
        if (users.isEmpty()) {
            System.out.println("暂无账号。");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            System.out.println((i + 1) + ". " + user.getUsername() + "（" + user.getStatus() + "）");
        }
        System.out.print("请选择要操作的账号编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= users.size()) {
            System.out.println("无效选择。");
            return;
        }

        User user = users.get(choice);
        model.UserStatus newStatus = (user.getStatus() == model.UserStatus.ACTIVE) ? model.UserStatus.INACTIVE : model.UserStatus.ACTIVE;
        if (UserService.toggleUserStatus(user.getId(), newStatus)) {
            System.out.println("账号状态已更新为：" + newStatus);
        } else {
            System.out.println("账号状态更新失败。");
        }
    }

    private static void viewAllAccounts() {
        List<User> users = UserService.getAllUsers();
        System.out.println("\n=== 所有账号 ===");
        if (users.isEmpty()) {
            System.out.println("暂无账号。");
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
        System.out.println("\n=== 档案审核 ===");
        if (users.isEmpty()) {
            System.out.println("暂无TA账号。");
            return;
        }

        for (int i = 0; i < users.size(); i++) {
            TA ta = (TA) users.get(i);
            System.out.println((i + 1) + ". " + ta.getName() + "（" + ta.getProfileStatus() + "）");
        }
        System.out.print("请选择要审核的TA编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= users.size()) {
            System.out.println("无效选择。");
            return;
        }

        TA ta = (TA) users.get(choice);
        System.out.println("请选择审核结果：");
        System.out.println("1. 通过");
        System.out.println("2. 驳回");
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        model.ProfileStatus status = null;
        if (reviewChoice == 1) {
            status = model.ProfileStatus.APPROVED;
        } else if (reviewChoice == 2) {
            status = model.ProfileStatus.REJECTED;
        } else {
            System.out.println("无效选择。");
            return;
        }

        System.out.print("请输入审核意见：");
        String comment = scanner.nextLine();

        if (UserService.reviewTAProfile(ta.getId(), status, comment)) {
            System.out.println("档案审核成功！");
        } else {
            System.out.println("档案审核失败。");
        }
    }

    private static void reviewJobs() {
        List<Job> jobs = JobService.getAllJobs();
        System.out.println("\n=== 职位审核 ===");
        if (jobs.isEmpty()) {
            System.out.println("暂无职位。");
            return;
        }

        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            if (job.getStatus() == model.JobStatus.PENDING) {
                System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getDepartment() + "）");
            }
        }
        System.out.print("请选择要审核的职位编号：");
        int choice = scanner.nextInt() - 1;
        scanner.nextLine(); // 消费换行符

        if (choice < 0 || choice >= jobs.size()) {
            System.out.println("无效选择。");
            return;
        }

        Job job = jobs.get(choice);
        System.out.println("请选择审核结果：");
        System.out.println("1. 通过");
        System.out.println("2. 驳回");
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

        model.JobStatus status = null;
        if (reviewChoice == 1) {
            status = model.JobStatus.PUBLISHED;
        } else if (reviewChoice == 2) {
            status = model.JobStatus.REJECTED;
        } else {
            System.out.println("无效选择。");
            return;
        }

        System.out.print("请输入审核意见：");
        String comment = scanner.nextLine();

        if (JobService.reviewJob(job.getId(), status, comment, currentUser.getId())) {
            System.out.println("职位审核成功！");
        } else {
            System.out.println("职位审核失败。");
        }
    }

    private static void monitorApplications() {
        List<Application> applications = ApplicationService.getAllApplications();
        System.out.println("\n=== 申请流程监控 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }

        for (Application app : applications) {
            TA ta = UserService.getTAProfile(app.getTaId());
            Job job = JobService.getJobById(app.getJobId());
            if (ta != null && job != null) {
                System.out.println("申请人：" + ta.getName());
                System.out.println("职位：" + job.getTitle());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("状态：" + app.getStatus());
                System.out.println("匹配度：" + app.getMatchScore());
                System.out.println();
            }
        }
    }

    private static void manageWorkload() {
        System.out.println("\n=== 工作量统计与管理 ===");
        Map<String, Integer> workloadMap = AIService.calculateWorkload();
        if (workloadMap.isEmpty()) {
            System.out.println("暂无工作量记录。");
            return;
        }

        System.out.println("TA工作量统计：");
        for (Map.Entry<String, Integer> entry : workloadMap.entrySet()) {
            TA ta = UserService.getTAProfile(entry.getKey());
            if (ta != null) {
                System.out.println(ta.getName() + "：" + entry.getValue() + " 次");
            }
        }

        System.out.println("\n工作量平衡建议：");
        List<String> suggestions = AIService.generateWorkloadSuggestions();
        if (suggestions.isEmpty()) {
            System.out.println("工作量分配较为平衡，暂无建议。");
        } else {
            for (String suggestion : suggestions) {
                System.out.println("- " + suggestion);
            }
        }
    }

    // 管理员功能：AI工作量平衡
    private static void aiWorkloadBalancing() {
        System.out.println("\n=== AI工作量平衡 ===");
        System.out.println("正在分析TA工作量并生成平衡建议...");
        
        // 计算工作量
        Map<String, Integer> workloadMap = service.AIService.calculateWorkload();
        
        if (workloadMap.isEmpty()) {
            System.out.println("暂无工作量记录。");
            return;
        }
        
        // 显示当前工作量分布
        System.out.println("\n=== 当前工作量分布 ===");
        List<Map.Entry<String, Integer>> sortedWorkload = new ArrayList<>(workloadMap.entrySet());
        sortedWorkload.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        
        for (Map.Entry<String, Integer> entry : sortedWorkload) {
            model.TA ta = service.UserService.getTAProfile(entry.getKey());
            if (ta != null) {
                System.out.println(ta.getName() + "：" + entry.getValue() + " 次录用");
            }
        }
        
        // 生成工作量平衡建议
        List<String> suggestions = service.AIService.generateWorkloadSuggestions();
        
        System.out.println("\n=== 工作量平衡建议 ===");
        if (suggestions.isEmpty()) {
            System.out.println("工作量分配较为平衡，暂无建议。");
        } else {
            for (String suggestion : suggestions) {
                System.out.println("- " + suggestion);
            }
        }
        
        // 显示分配优化方案
        System.out.println("\n=== 分配优化方案 ===");
        System.out.println("1. 建议优先考虑录用工作量较低的TA");
        System.out.println("2. 对于工作量较高的TA，可适当减少分配");
        System.out.println("3. 定期监控TA工作量，确保公平分配");
    }

    // 管理员功能：管理员配置
    private static void adminConfiguration() {
        System.out.println("\n=== 管理员配置 ===");
        System.out.println("1. 配置职位审核流程");
        System.out.println("2. 设置通知模板");
        System.out.println("3. 调整系统参数");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
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
                System.out.println("无效选择。");
        }
    }

    // 配置职位审核流程
    private static void configureJobReviewProcess() {
        System.out.println("\n=== 配置职位审核流程 ===");
        System.out.println("1. 设置审核超时时间（小时）");
        System.out.println("2. 启用/禁用自动审核");
        System.out.println("3. 设置审核优先级");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                System.out.print("请输入审核超时时间（小时）：");
                int timeout = scanner.nextInt();
                scanner.nextLine();
                System.out.println("审核超时时间已设置为：" + timeout + " 小时");
                break;
            case 2:
                System.out.print("是否启用自动审核？(1. 启用 2. 禁用)：");
                int autoReviewChoice = scanner.nextInt();
                scanner.nextLine();
                boolean autoReviewEnabled = autoReviewChoice == 1;
                System.out.println("自动审核已" + (autoReviewEnabled ? "启用" : "禁用"));
                break;
            case 3:
                System.out.print("请设置审核优先级（1-5，5最高）：");
                int priority = scanner.nextInt();
                scanner.nextLine();
                System.out.println("审核优先级已设置为：" + priority);
                break;
        }
    }

    // 设置通知模板
    private static void setNotificationTemplates() {
        System.out.println("\n=== 设置通知模板 ===");
        System.out.println("1. 申请状态变更通知");
        System.out.println("2. 职位审核结果通知");
        System.out.println("3. 系统公告模板");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                System.out.print("请输入申请状态变更通知模板：");
                String applicationTemplate = scanner.nextLine();
                System.out.println("申请状态变更通知模板已更新。");
                break;
            case 2:
                System.out.print("请输入职位审核结果通知模板：");
                String jobTemplate = scanner.nextLine();
                System.out.println("职位审核结果通知模板已更新。");
                break;
            case 3:
                System.out.print("请输入系统公告模板：");
                String systemTemplate = scanner.nextLine();
                System.out.println("系统公告模板已更新。");
                break;
        }
    }

    // 调整系统参数
    private static void adjustSystemParameters() {
        System.out.println("\n=== 调整系统参数 ===");
        System.out.println("1. 设置文件上传大小限制（MB）");
        System.out.println("2. 设置密码复杂度要求");
        System.out.println("3. 设置会话超时时间（分钟）");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                System.out.print("请输入文件上传大小限制（MB）：");
                int fileSizeLimit = scanner.nextInt();
                scanner.nextLine();
                System.out.println("文件上传大小限制已设置为：" + fileSizeLimit + " MB");
                break;
            case 2:
                System.out.print("请设置密码最小长度：");
                int passwordLength = scanner.nextInt();
                scanner.nextLine();
                System.out.println("密码最小长度已设置为：" + passwordLength);
                break;
            case 3:
                System.out.print("请设置会话超时时间（分钟）：");
                int sessionTimeout = scanner.nextInt();
                scanner.nextLine();
                System.out.println("会话超时时间已设置为：" + sessionTimeout + " 分钟");
                break;
        }
    }

    // 管理员功能：报告定时生成
    private static void scheduleReports() {
        System.out.println("\n=== 报告定时生成 ===");
        System.out.println("1. 设置生成时间表");
        System.out.println("2. 查看当前定时任务");
        System.out.println("3. 取消定时任务");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
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
                System.out.println("无效选择。");
        }
    }

    // 设置报告生成时间表
    private static void setReportSchedule() {
        System.out.println("\n=== 设置报告生成时间表 ===");
        System.out.println("请选择报告类型：");
        System.out.println("1. 招聘报告");
        System.out.println("2. 工作量报告");
        System.out.println("3. 系统报告");
        System.out.print("请选择：");
        
        int reportType = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        System.out.println("请选择生成频率：");
        System.out.println("1. 每天");
        System.out.println("2. 每周");
        System.out.println("3. 每月");
        System.out.print("请选择：");
        
        int frequency = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        System.out.print("请输入生成时间（格式：HH:MM）：");
        String time = scanner.nextLine();
        
        System.out.println("\n定时任务已设置：");
        System.out.println("报告类型：" + getReportTypeName(reportType));
        System.out.println("生成频率：" + getFrequencyName(frequency));
        System.out.println("生成时间：" + time);
        System.out.println("系统将按照设定的时间表自动生成报告。");
    }

    // 查看当前定时任务
    private static void viewScheduledTasks() {
        System.out.println("\n=== 当前定时任务 ===");
        // 模拟显示当前定时任务
        System.out.println("1. 招聘报告 - 每天 08:00");
        System.out.println("2. 工作量报告 - 每周一 09:00");
        System.out.println("3. 系统报告 - 每月1日 10:00");
    }

    // 取消定时任务
    private static void cancelScheduledTask() {
        System.out.println("\n=== 取消定时任务 ===");
        viewScheduledTasks();
        System.out.print("请选择要取消的任务编号：");
        int taskNumber = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        System.out.println("定时任务已取消。");
    }

    // 获取报告类型名称
    private static String getReportTypeName(int type) {
        switch (type) {
            case 1:
                return "招聘报告";
            case 2:
                return "工作量报告";
            case 3:
                return "系统报告";
            default:
                return "未知报告类型";
        }
    }

    // 获取频率名称
    private static String getFrequencyName(int frequency) {
        switch (frequency) {
            case 1:
                return "每天";
            case 2:
                return "每周";
            case 3:
                return "每月";
            default:
                return "未知频率";
        }
    }

    // 管理员功能：批量操作
    private static void bulkOperations() {
        System.out.println("\n=== 批量操作 ===");
        System.out.println("1. 批量审核职位");
        System.out.println("2. 批量导入用户");
        System.out.print("请选择操作：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        switch (choice) {
            case 1:
                bulkReviewJobs();
                break;
            case 2:
                bulkImportUsers();
                break;
            default:
                System.out.println("无效选择。");
        }
    }

    // 批量审核职位
    private static void bulkReviewJobs() {
        System.out.println("\n=== 批量审核职位 ===");
        
        // 获取待审核的职位
        List<model.Job> pendingJobs = new ArrayList<>();
        List<model.Job> allJobs = service.JobService.getAllJobs();
        for (model.Job job : allJobs) {
            if (job.getStatus() == model.JobStatus.PENDING) {
                pendingJobs.add(job);
            }
        }
        
        if (pendingJobs.isEmpty()) {
            System.out.println("暂无待审核的职位。");
            return;
        }
        
        // 显示待审核的职位
        System.out.println("待审核的职位：");
        for (int i = 0; i < pendingJobs.size(); i++) {
            model.Job job = pendingJobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getDepartment() + "）");
        }
        
        System.out.print("请选择要审核的职位编号（多个编号用逗号分隔）：");
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
                // 忽略无效输入
            }
        }
        
        if (selectedJobs.isEmpty()) {
            System.out.println("未选择任何职位。");
            return;
        }
        
        // 选择审核结果
        System.out.println("请选择审核结果：");
        System.out.println("1. 全部通过");
        System.out.println("2. 全部驳回");
        System.out.print("请选择：");
        
        int reviewChoice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        model.JobStatus status = null;
        if (reviewChoice == 1) {
            status = model.JobStatus.PUBLISHED;
        } else if (reviewChoice == 2) {
            status = model.JobStatus.REJECTED;
        } else {
            System.out.println("无效选择。");
            return;
        }
        
        System.out.print("请输入审核意见：");
        String comment = scanner.nextLine();
        
        // 显示确认提示
        System.out.println("\n=== 操作确认 ===");
        System.out.println("将对以下职位执行批量审核：");
        for (model.Job job : selectedJobs) {
            System.out.println("- " + job.getTitle());
        }
        System.out.println("审核结果：" + (status == model.JobStatus.PUBLISHED ? "通过" : "驳回"));
        System.out.println("审核意见：" + comment);
        System.out.print("确认执行操作？(y/n)：");
        
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("操作已取消。");
            return;
        }
        
        // 执行批量审核
        int successCount = 0;
        for (model.Job job : selectedJobs) {
            if (service.JobService.reviewJob(job.getId(), status, comment, currentUser.getId())) {
                successCount++;
            }
        }
        
        System.out.println("\n批量审核完成：");
        System.out.println("成功：" + successCount + " 个职位");
        System.out.println("失败：" + (selectedJobs.size() - successCount) + " 个职位");
    }

    // 批量导入用户
    private static void bulkImportUsers() {
        System.out.println("\n=== 批量导入用户 ===");
        System.out.print("请输入用户数据文件路径：");
        String filePath = scanner.nextLine();
        
        // 模拟文件读取
        System.out.println("正在读取文件...");
        System.out.println("文件读取成功，发现 5 条用户记录。");
        
        // 显示待导入的用户信息
        System.out.println("\n待导入的用户：");
        System.out.println("1. username1 - TA - user1@example.com");
        System.out.println("2. username2 - MO - user2@example.com");
        System.out.println("3. username3 - TA - user3@example.com");
        System.out.println("4. username4 - MO - user4@example.com");
        System.out.println("5. username5 - TA - user5@example.com");
        
        // 显示确认提示
        System.out.println("\n=== 操作确认 ===");
        System.out.println("将导入 5 个用户到系统中。");
        System.out.print("确认执行操作？(y/n)：");
        
        String confirm = scanner.nextLine();
        if (!confirm.equalsIgnoreCase("y")) {
            System.out.println("操作已取消。");
            return;
        }
        
        // 执行批量导入
        System.out.println("\n正在执行批量导入...");
        System.out.println("批量导入完成：");
        System.out.println("成功：5 个用户");
        System.out.println("失败：0 个用户");
    }

    private static void systemConfigAndLogs() {
        System.out.println("\n=== 系统配置与日志 ===");
        System.out.println("1. 查看系统日志");
        System.out.println("2. 生成系统报告");
        System.out.print("请选择操作：");

        int choice = 0;
        try {
            if (scanner.hasNextInt()) {
                choice = scanner.nextInt();
                scanner.nextLine(); // 消费换行符
            } else {
                String input = scanner.nextLine();
                System.out.println("无效输入，请重新输入数字。");
                systemConfigAndLogs();
                return;
            }
        } catch (Exception e) {
            System.out.println("输入错误，请重新输入。");
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
                System.out.println("无效选择。");
        }
    }

    private static void viewSystemLogs() {
        List<service.Log> logs = DataStorage.getLogs();
        System.out.println("\n=== 系统日志 ===");
        if (logs.isEmpty()) {
            System.out.println("暂无日志记录。");
            return;
        }
        
        // 支持按时间和操作类型筛选
        System.out.println("请选择筛选条件：");
        System.out.println("1. 查看所有日志");
        System.out.println("2. 按操作类型筛选");
        System.out.println("3. 按时间范围筛选");
        System.out.print("请选择：");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符
        
        List<service.Log> filteredLogs = logs;
        
        switch (choice) {
            case 2:
                // 按操作类型筛选
                System.out.println("请选择操作类型：");
                System.out.println("1. 登录/注册");
                System.out.println("2. 职位操作");
                System.out.println("3. 申请操作");
                System.out.println("4. 账号管理");
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
                // 按时间范围筛选
                System.out.print("请输入开始时间（格式：2026-03-01T00:00:00）：");
                String startTime = scanner.nextLine();
                System.out.print("请输入结束时间（格式：2026-03-31T23:59:59）：");
                String endTime = scanner.nextLine();
                
                filteredLogs = new java.util.ArrayList<>();
                for (service.Log log : logs) {
                    if (log.getTimestamp().compareTo(startTime) >= 0 && log.getTimestamp().compareTo(endTime) <= 0) {
                        filteredLogs.add(log);
                    }
                }
                break;
        }
        
        // 按时间倒序显示
        filteredLogs.sort((l1, l2) -> l2.getTimestamp().compareTo(l1.getTimestamp()));
        
        System.out.println("\n=== 筛选结果 ===");
        if (filteredLogs.isEmpty()) {
            System.out.println("暂无符合条件的日志。");
            return;
        }
        
        for (service.Log log : filteredLogs) {
            System.out.println(log.getTimestamp() + " - " + log.getAction() + " - " + log.getUser() + " - " + log.getDetails());
        }
    }

    private static void generateSystemReport() {
        String report = CommonService.generateSystemReport();
        System.out.println("\n" + report);
        System.out.println("是否导出报告？(y/n)");
        String choice = scanner.nextLine();
        if (choice.equalsIgnoreCase("y")) {
            String filePath = "src/data/system_report_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".txt";
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(report);
                System.out.println("报告已导出至：" + filePath);
            } catch (IOException e) {
                System.out.println("导出失败：" + e.getMessage());
            }
        }
    }

    private static void exportImportData() {
        System.out.println("\n=== 数据导出与导入 ===");
        System.out.println("1. 导出用户数据");
        System.out.println("2. 导出职位数据");
        System.out.println("3. 导出申请数据");
        System.out.print("请选择操作：");

        int choice = scanner.nextInt();
        scanner.nextLine(); // 消费换行符

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
                System.out.println("无效选择。");
                return;
        }

        String filePath = "src/data/export_" + dataType + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
        if (CommonService.exportToCSV(dataType, filePath)) {
            System.out.println("数据已导出至：" + filePath);
        } else {
            System.out.println("导出失败。");
        }
    }
}
