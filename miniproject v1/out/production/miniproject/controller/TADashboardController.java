package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import model.TA;
import model.Task;
import model.ProfileStatus;
import model.Job;
import model.ApplicationStatus;
import model.Application;
import service.ApplicationService;
import service.JobService;
import service.AIService;
import service.UserService;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class TADashboardController {
    @FXML
    private Label openPositionsLabel;
    @FXML
    private Label myApplicationsLabel;
    @FXML
    private Label pendingActionsLabel;
    @FXML
    private TableView<Task> tasksTable;
    @FXML
    private VBox emptyTasksBox;
    
    private TA user;
    private boolean showWelcomeGuideOnInit = false;
    
    public void setUser(TA user) {
        this.user = user;
        initializeDashboard();
    }
    
    public void setUser(TA user, boolean showWelcomeGuide) {
        this.user = user;
        this.showWelcomeGuideOnInit = showWelcomeGuide;
        initializeDashboard();
    }
    
    private void initializeDashboard() {
        if (user == null) return;
        
        try {
            // 只在首次登录时显示新手引导
            if (showWelcomeGuideOnInit) {
                showWelcomeGuide();
                showWelcomeGuideOnInit = false; // 显示后重置标志
            }
            
            // 加载统计数据
            loadStatistics();
            
            // 加载待处理任务
            loadPendingTasks();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("初始化仪表板失败: " + e.getMessage());
        }
    }
    
    private void loadStatistics() {
        // 获取开放职位数量
        int openJobsCount = JobService.getAvailableJobs().size();
        openPositionsLabel.setText(String.valueOf(openJobsCount));
        
        // 获取我的申请数量
        int myApplicationsCount = ApplicationService.getApplicationsByTA(user.getId()).size();
        myApplicationsLabel.setText(String.valueOf(myApplicationsCount));
        
        // 获取待处理任务数量
        // 这里简化处理，实际应该根据用户的待处理任务计算
        int pendingTasksCount = 2; // 模拟数据
        pendingActionsLabel.setText(String.valueOf(pendingTasksCount));
    }
    
    private void loadPendingTasks() {
        // 从数据库或服务中获取待处理任务
        // 为了演示，我们使用模拟数据
        ObservableList<model.Task> tasks = FXCollections.observableArrayList();
        
        // 检查用户档案状态，如果未完善，添加完善档案任务
        if (user.getProfileStatus() != ProfileStatus.APPROVED) {
            tasks.add(new model.Task("完善个人档案", "2026-03-30", "Pending"));
        }
        
        // 检查用户是否上传简历，如果未上传，添加上传简历任务
        if (user.getResumePath() == null || user.getResumePath().isEmpty()) {
            tasks.add(new model.Task("上传简历", "2026-03-25", "Pending"));
        }
        
        // 模拟其他任务
        tasks.add(new model.Task("申请职位", "2026-03-28", "Approved"));
        
        // 设置表格数据
        tasksTable.setItems(tasks);
        
        // 处理空状态
        if (tasks.isEmpty()) {
            tasksTable.setVisible(false);
            emptyTasksBox.setVisible(true);
        } else {
            tasksTable.setVisible(true);
            emptyTasksBox.setVisible(false);
        }
    }
    
    // AI职位匹配
    @FXML
    private void handleAIJobMatching(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            // 显示提示信息
            System.out.println("您的档案尚未通过审核，无法使用AI职位匹配功能。");
            return;
        }
        
        System.out.println("正在分析您的个人资料和技能...");
        
        // 获取推荐职位
        List<Job> recommendedJobs = AIService.recommendJobsForTA(user, 5);
        
        if (recommendedJobs.isEmpty()) {
            System.out.println("暂无可用职位推荐。");
            return;
        }
        
        System.out.println("=== 推荐职位 ===");
        for (int i = 0; i < recommendedJobs.size(); i++) {
            Job job = recommendedJobs.get(i);
            double matchScore = AIService.calculateSkillMatch(user, job);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
            System.out.println("   院系：" + job.getDepartment());
            System.out.println("   工作时间：" + job.getWorkTime());
            System.out.println("   截止时间：" + job.getDeadline());
            System.out.println("   匹配度：" + String.format("%.2f%%", matchScore));
            System.out.println();
        }
    }
    
    // 技能缺口识别
    @FXML
    private void handleIdentifyMissingSkills(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            System.out.println("您的档案尚未通过审核，无法使用技能缺口识别功能。");
            return;
        }
        
        System.out.println("=== 技能缺口识别 ===");
        
        // 获取可用职位
        List<Job> availableJobs = JobService.getAvailableJobs();
        if (availableJobs.isEmpty()) {
            System.out.println("暂无可用职位。");
            return;
        }
        
        // 显示职位列表
        System.out.println("请选择一个职位来分析技能缺口：");
        for (int i = 0; i < availableJobs.size(); i++) {
            Job job = availableJobs.get(i);
            System.out.println((i + 1) + ". " + job.getTitle() + "（" + job.getType() + "）");
        }
        
        // 这里简化处理，实际应该弹出一个对话框让用户选择职位
        // 为了演示，我们选择第一个职位
        if (!availableJobs.isEmpty()) {
            Job selectedJob = availableJobs.get(0);
            System.out.println("\n正在分析您与职位 \"" + selectedJob.getTitle() + "\" 的技能匹配情况...");
            
            // 识别缺失技能
            List<String> missingSkills = AIService.identifyMissingSkills(user, selectedJob);
            
            if (missingSkills.isEmpty()) {
                System.out.println("恭喜！您的技能已满足该职位的所有要求。");
            } else {
                System.out.println("\n=== 技能缺口分析 ===");
                System.out.println("您缺少以下技能：");
                for (String skill : missingSkills) {
                    System.out.println("- " + skill);
                }
                
                // 生成技能提升建议
                String suggestions = AIService.generateSkillSuggestions(missingSkills);
                System.out.println("\n" + suggestions);
            }
        }
    }
    
    // 查看职位
    @FXML
    private void handleViewJobs(ActionEvent event) {
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
    
    // 申请职位
    @FXML
    private void handleApplyForJob(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
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
        
        // 这里简化处理，实际应该弹出一个对话框让用户选择职位和输入附言
        // 为了演示，我们选择第一个职位
        if (!jobs.isEmpty()) {
            Job job = jobs.get(0);
            String coverLetter = "我对这个职位很感兴趣，希望能有机会加入。";
            
            Application application = ApplicationService.submitApplication(user.getId(), job.getId(), coverLetter);
            if (application != null) {
                System.out.println("申请提交成功！匹配度：" + application.getMatchScore());
            } else {
                System.out.println("申请提交失败，可能是您已经申请过该职位或职位已截止。");
            }
        }
    }
    
    // 完善个人档案
    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            
            // 获取当前舞台
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                // 如果不是从Button触发的事件，使用其他方式获取舞台
                // 这里简单处理，假设当前有一个可见的舞台
                stage = (Stage) tasksTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            // 保持当前窗口尺寸并添加样式表
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // 添加样式表
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 完善个人档案");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("页面加载失败");
        }
    }
    
    // 上传简历
    @FXML
    private void handleUploadResume(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
            Parent root = loader.load();
            TAUploadResumeController controller = loader.getController();
            controller.setUser(user);
            
            // 获取当前舞台
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                // 如果不是从Button触发的事件，使用其他方式获取舞台
                stage = (Stage) tasksTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            // 保持当前窗口尺寸并添加样式表
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // 添加样式表
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 上传简历");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("页面加载失败");
        }
    }
    
    // 退出登录
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            LoginController controller = loader.getController();
            controller.setStage((Stage) ((Button) event.getSource()).getScene().getWindow());
            
            Scene scene = new Scene(root, 800, 600);
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 登录");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("页面加载失败");
        }
    }
    
    // 显示新手引导
    private void showWelcomeGuide() {
        // 检查用户是否是首次登录且档案未完善
        // 只在档案状态为DRAFT（初始状态）且未上传简历时显示
        if (user.getProfileStatus() == ProfileStatus.DRAFT && (user.getResumePath() == null || user.getResumePath().isEmpty())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("欢迎使用TA招聘系统");
            alert.setHeaderText("新手引导");
            alert.setContentText("为了顺利申请TA职位，请按照以下步骤操作：\n\n" +
                    "1. 完善个人档案 - 填写基本信息、技能和经验\n" +
                    "2. 上传简历 - 上传您的个人简历\n" +
                    "3. 使用AI职位匹配 - 找到最适合您的职位\n" +
                    "4. 申请职位 - 提交申请并等待审核\n\n" +
                    "请先点击'完善个人档案'按钮开始您的TA申请之旅！");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // 处理可用职位卡片点击
    @FXML
    private void handleOpenPositionsClick(MouseEvent event) {
        // 这里可以跳转到职位列表页面
        System.out.println("点击了可用职位卡片");
        // 显示职位列表
        handleViewJobs(new ActionEvent());
    }
    
    // 处理我的申请卡片点击
    @FXML
    private void handleMyApplicationsClick(MouseEvent event) {
        // 这里可以跳转到我的申请页面
        System.out.println("点击了我的申请卡片");
        // 显示我的申请
        List<Application> applications = ApplicationService.getApplicationsByTA(user.getId());
        System.out.println("\n=== 我的申请 ===");
        if (applications.isEmpty()) {
            System.out.println("暂无申请记录。");
            return;
        }
        
        for (Application app : applications) {
            Job job = JobService.getJobById(app.getJobId());
            if (job != null) {
                System.out.println("职位：" + job.getTitle());
                System.out.println("状态：" + app.getStatus());
                System.out.println("申请时间：" + app.getCreatedAt());
                System.out.println("匹配度：" + app.getMatchScore());
                System.out.println();
            }
        }
    }
    
    // 处理待处理任务卡片点击
    @FXML
    private void handlePendingTasksClick(MouseEvent event) {
        // 这里可以跳转到任务详情页面
        System.out.println("点击了待处理任务卡片");
        // 刷新任务列表
        loadPendingTasks();
    }
    
    // 处理任务表格点击
    @FXML
    private void handleTaskClick(MouseEvent event) {
        Task selectedTask = tasksTable.getSelectionModel().getSelectedItem();
        if (selectedTask != null) {
            System.out.println("点击了任务：" + selectedTask.getName());
            // 根据任务名称执行相应操作
            if (selectedTask.getName().equals("完善个人档案")) {
                handleUpdateProfile(new ActionEvent());
            } else if (selectedTask.getName().equals("上传简历")) {
                handleUploadResume(new ActionEvent());
            } else if (selectedTask.getName().equals("申请职位")) {
                handleApplyForJob(new ActionEvent());
            }
        }
    }
    
    // 处理首页按钮点击
    @FXML
    private void handleHome(ActionEvent event) {
        System.out.println("点击了首页按钮");
        // 刷新当前页面
        initializeDashboard();
    }
    
    // 处理申请管理按钮点击
    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        System.out.println("点击了申请管理按钮");
        // 显示我的申请
        handleMyApplicationsClick(new MouseEvent(null, 0, 0, 0, 0, null, 0, false, false, false, false, false, false, false, false, false, false, null));
    }
    
    // 处理个人中心按钮点击
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        System.out.println("点击了个人中心按钮");
        // 跳转到个人中心页面
        handleUpdateProfile(new ActionEvent());
    }
}