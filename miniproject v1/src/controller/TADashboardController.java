package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.input.MouseButton;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import model.TA;
import model.Task;
import model.ProfileStatus;
import model.Job;
import model.ApplicationStatus;
import model.Application;
import model.UserRole;
import service.ApplicationService;
import service.JobService;
import service.PDFResumeParserService;
import service.AIService;
import service.WorkloadNotificationService;
import controller.SkillGapAnalysisController;
import service.SkillGapAnalysisService;
import service.SkillGapAnalysisService.*;
import service.WorkloadService;
import service.AdminConfigService;
import service.UserService;
import model.AdminConfig;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Optional;

public class TADashboardController {
    @FXML
    private Label openPositionsLabel;
    @FXML
    private Label myApplicationsLabel;
    @FXML
    private Label pendingActionsLabel;
    @FXML
    private Label workloadLabel;
    @FXML
    private Label workloadBandLabel;
    @FXML
    private GridPane scheduleGrid;
    @FXML
    private VBox pendingTaskListContainer;
    
    private TA user;
    private boolean showWelcomeGuideOnInit = false;
    private Stage stage;

    private static final String STATUS_FREE = "free";
    private static final String STATUS_OCCUPIED = "occupied";
    private static final String STATUS_BUSY = "busy";
    private static final String AVAILABLE_TIME_PREFIX = "SCHEDULE_V1|";
    private static final String TASK_PREFIX = "TASKS_V1|";
    private static final String SYSTEM_PROFILE = "Complete Personal Profile";
    private static final String SYSTEM_RESUME = "Upload Resume";
    private static final String SYSTEM_SKILL = "Complete Skill Assessment";

    private Region[][] scheduleCells;
    private String[][] scheduleStatuses;
    private final Set<Region> selectedCells = new HashSet<>();
    private final List<TaskItem> dashboardTasks = new ArrayList<>();
    
    public void setUser(TA user) {
        this.user = user;
        initializeDashboard();
        showPendingWorkloadNotifications();
    }
    
    public void setUser(TA user, boolean showWelcomeGuide) {
        this.user = user;
        this.showWelcomeGuideOnInit = showWelcomeGuide;
        initializeDashboard();
        showPendingWorkloadNotifications();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    private void initializeDashboard() {
        if (user == null) return;
        
        try {
            // Show welcome guide only on first login
            if (showWelcomeGuideOnInit) {
                showWelcomeGuide();
                showWelcomeGuideOnInit = false; // Reset flag after showing
            }
            
            // Load statistics
            loadStatistics();
            
            // Load pending tasks
            loadPendingTasks();

            // Initialize 7*14 timetable
            initializeScheduleGrid();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to initialize dashboard");
            alert.setContentText(e.getMessage());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    private void loadStatistics() {
        // Get number of open positions
        int openJobsCount = JobService.getAvailableJobs().size();
        openPositionsLabel.setText(String.valueOf(openJobsCount));
        
        // Get number of my applications
        int myApplicationsCount = ApplicationService.getApplicationsByTA(user.getId()).size();
        myApplicationsLabel.setText(String.valueOf(myApplicationsCount));
        
        // Get number of pending tasks
        // Simplified handling here; actual implementation should calculate based on the user's pending tasks
        int pendingTasksCount = 2; // Mock data
        pendingActionsLabel.setText(String.valueOf(pendingTasksCount));

        int workload = WorkloadService.getCurrentWorkload(user);
        AdminConfig cfg = AdminConfigService.loadConfig();
        workloadLabel.setText(String.valueOf(workload));
        if (workload < cfg.getMidThreshold()) {
            workloadLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: #16a34a;");
            workloadBandLabel.setText("Your current workload looks healthy and manageable.");
            workloadBandLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #16a34a;");
        } else if (workload < cfg.getHighThreshold()) {
            workloadLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: #eab308;");
            workloadBandLabel.setText("Your workload is moderate—plan your tasks carefully.");
            workloadBandLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #eab308;");
        } else {
            workloadLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: 700; -fx-text-fill: #dc2626;");
            workloadBandLabel.setText("Your workload is high. Consider reducing commitments.");
            workloadBandLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #dc2626;");
        }
    }
    
    private void loadPendingTasks() {
        dashboardTasks.clear();

        if (user == null) {
            pendingActionsLabel.setText("0");
            renderPendingTasks();
            return;
        }

        // System tasks (cannot be manually toggled)
        if (user.getProfileStatus() == ProfileStatus.DRAFT || user.getProfileStatus() == ProfileStatus.PENDING) {
            String profileTaskTitle = user.getProfileStatus() == ProfileStatus.PENDING
                    ? "Profile Under Review"
                    : SYSTEM_PROFILE;
            dashboardTasks.add(new TaskItem(profileTaskTitle, "2026-04-30", true, false));
        }
        if (user.getResumePath() == null || user.getResumePath().isEmpty()) {
            dashboardTasks.add(new TaskItem(SYSTEM_RESUME, "2026-04-25", true, false));
        }

        boolean hasSkillInfo = user.getSkills() != null && !user.getSkills().isEmpty()
                && user.getExperience() != null && !user.getExperience().trim().isEmpty();
        if (!hasSkillInfo) {
            dashboardTasks.add(new TaskItem(SYSTEM_SKILL, "2026-05-05", true, false));
        }

        // User custom tasks
        dashboardTasks.addAll(loadCustomTasksFromAvailableTime());

        // Remove completed tasks from visible list
        dashboardTasks.removeIf(TaskItem::isCompleted);

        pendingActionsLabel.setText(String.valueOf(dashboardTasks.size()));
        renderPendingTasks();
    }

    private void renderPendingTasks() {
        if (pendingTaskListContainer == null) {
            return;
        }

        pendingTaskListContainer.getChildren().clear();
        if (dashboardTasks.isEmpty()) {
            Label empty = new Label("No pending tasks. Great job!");
            empty.setStyle("-fx-font-size: 13px; -fx-text-fill: #6b7280; -fx-padding: 8 4;");
            pendingTaskListContainer.getChildren().add(empty);
            return;
        }

        for (TaskItem task : dashboardTasks) {
            HBox row = new HBox();
            row.setSpacing(12);
            row.setStyle("-fx-padding: 10px 12px; -fx-background-color: #f8f9fa; -fx-background-radius: 8px;");

            VBox info = new VBox();
            info.setSpacing(3);
            Label title = new Label(task.getTitle());
            title.setStyle("-fx-font-size: 14px; -fx-font-weight: 500; -fx-text-fill: #333333;");
            Label due = new Label("Due: " + task.getDueDate());
            due.setStyle("-fx-font-size: 12px; -fx-text-fill: #666666;");
            info.getChildren().addAll(title, due);
            HBox.setHgrow(info, javafx.scene.layout.Priority.ALWAYS);

            if (task.isSystemTask()) {
                Label tag = new Label("PENDING");
                tag.setStyle("-fx-font-size: 12px; -fx-font-weight: 500; -fx-text-fill: #ff9f43; -fx-background-color: rgba(255, 159, 67, 0.1); -fx-padding: 4 8; -fx-background-radius: 12px;");
                row.getChildren().addAll(info, tag);

                row.setOnMouseClicked(e -> {
                    if (e.getClickCount() == 2) {
                        jumpToTask(task.getTitle());
                    }
                });
            } else {
                CheckBox done = new CheckBox();
                done.setStyle("-fx-font-size: 14px;");
                done.setOnAction(e -> {
                    task.setCompleted(done.isSelected());
                    saveCustomTasksToAvailableTime();
                    loadPendingTasks();
                });
                row.getChildren().addAll(info, done);
            }

            pendingTaskListContainer.getChildren().add(row);
        }
    }

    private void jumpToTask(String title) {
        if (SYSTEM_PROFILE.equals(title) || SYSTEM_SKILL.equals(title) || "Profile Under Review".equals(title)) {
            handleUpdateProfile(new ActionEvent());
            return;
        }
        if (SYSTEM_RESUME.equals(title)) {
            handleUploadResume(new ActionEvent());
        }
    }

    private List<TaskItem> loadCustomTasksFromAvailableTime() {
        List<TaskItem> customTasks = new ArrayList<>();
        String available = user.getAvailableTime();
        if (available == null || !available.contains(TASK_PREFIX)) {
            return customTasks;
        }

        int idx = available.indexOf(TASK_PREFIX);
        String payload = available.substring(idx + TASK_PREFIX.length());
        if (payload.trim().isEmpty()) {
            return customTasks;
        }

        String[] items = payload.split(";;");
        for (String item : items) {
            if (item.trim().isEmpty()) continue;
            String[] parts = item.split("\\|", 3);
            if (parts.length < 3) continue;
            boolean completed = "1".equals(parts[2]);
            customTasks.add(new TaskItem(parts[0], parts[1], false, completed));
        }
        return customTasks;
    }

    private void saveCustomTasksToAvailableTime() {
        if (user == null) return;

        List<TaskItem> custom = new ArrayList<>();
        for (TaskItem t : dashboardTasks) {
            if (!t.isSystemTask()) {
                custom.add(t);
            }
        }

        StringBuilder taskPart = new StringBuilder(TASK_PREFIX);
        for (int i = 0; i < custom.size(); i++) {
            TaskItem t = custom.get(i);
            taskPart.append(t.getTitle()).append("|").append(t.getDueDate()).append("|").append(t.isCompleted() ? "1" : "0");
            if (i < custom.size() - 1) taskPart.append(";;");
        }

        String available = user.getAvailableTime() == null ? "" : user.getAvailableTime();
        String schedulePart = available;
        int taskIdx = schedulePart.indexOf(TASK_PREFIX);
        if (taskIdx >= 0) {
            schedulePart = schedulePart.substring(0, taskIdx);
        }

        user.setAvailableTime(schedulePart + taskPart);
        UserService.updateTAProfile(user);
    }


    private void initializeScheduleGrid() {
        if (scheduleGrid == null) {
            return;
        }

        scheduleGrid.getChildren().clear();

        String[] days = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        String[] periodTimes = {
                "08:00-08:45", "08:50-09:35", "09:50-10:35", "10:40-11:25",
                "11:30-12:15", "13:00-13:45", "13:50-14:35", "14:45-15:30",
                "15:40-16:25", "16:35-17:20", "18:00-18:45", "18:50-19:35",
                "19:40-20:25", "20:30-21:15"
        };

        for (int col = 0; col < 7; col++) {
            Label dayLabel = new Label(days[col]);
            dayLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #475569;");
            dayLabel.setMaxWidth(Double.MAX_VALUE);
            dayLabel.setAlignment(Pos.CENTER);
            scheduleGrid.add(dayLabel, col + 1, 0);
        }

        for (int row = 0; row < 14; row++) {
            Label periodLabel = new Label((row + 1) + "\n" + periodTimes[row]);
            periodLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");
            periodLabel.setAlignment(Pos.CENTER_RIGHT);
            periodLabel.setPrefWidth(68);
            scheduleGrid.add(periodLabel, 0, row + 1);
        }

        scheduleCells = new Region[14][7];
        scheduleStatuses = new String[14][7];

        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 7; col++) {
                Region cell = new Region();
                cell.setMinSize(52, 24);
                cell.setPrefSize(52, 24);

                final int r = row;
                final int c = col;
                scheduleStatuses[row][col] = STATUS_FREE;
                applyStatusToCell(cell, STATUS_FREE);

                cell.setOnMouseClicked(event -> {
                    if (event.getButton() != MouseButton.PRIMARY) {
                        return;
                    }
                    handleScheduleCellClick(r, c, event.isControlDown());
                });

                scheduleCells[row][col] = cell;
                scheduleGrid.add(cell, col + 1, row + 1);
            }
        }

        loadScheduleFromUserAvailableTime();
    }

    private void handleScheduleCellClick(int row, int col, boolean controlDown) {
        Region cell = scheduleCells[row][col];
        if (cell == null) {
            return;
        }

        if (controlDown) {
            toggleCellSelection(cell);
            return;
        }

        List<String> options = List.of(STATUS_FREE, STATUS_OCCUPIED, STATUS_BUSY);
        ChoiceDialog<String> dialog = new ChoiceDialog<>(scheduleStatuses[row][col], options);
        dialog.setTitle("Set Slot Status");
        dialog.setHeaderText(selectedCells.isEmpty()
                ? "Select status for this class slot"
                : "Select status for selected class slots");
        dialog.setContentText("Status:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(status -> {
            if (selectedCells.isEmpty()) {
                applyStatusByCoordinates(row, col, status);
            } else {
                applyStatusToSelectedCells(status);
            }
            saveScheduleToUserAvailableTime();
        });
    }

    private void toggleCellSelection(Region cell) {
        if (selectedCells.contains(cell)) {
            selectedCells.remove(cell);
            updateCellVisual(cell);
        } else {
            selectedCells.add(cell);
            String originalStyle = cell.getStyle();
            cell.setStyle(originalStyle + " -fx-border-width: 2; -fx-border-color: #1d4ed8;");
        }
    }

    private void applyStatusToSelectedCells(String status) {
        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 7; col++) {
                Region cell = scheduleCells[row][col];
                if (selectedCells.contains(cell)) {
                    applyStatusByCoordinates(row, col, status);
                }
            }
        }
        clearSelection();
    }

    private void clearSelection() {
        for (Region cell : selectedCells) {
            updateCellVisual(cell);
        }
        selectedCells.clear();
    }

    private void applyStatusByCoordinates(int row, int col, String status) {
        String normalized = normalizeStatus(status);
        scheduleStatuses[row][col] = normalized;
        applyStatusToCell(scheduleCells[row][col], normalized);
    }

    private void applyStatusToCell(Region cell, String status) {
        String normalized = normalizeStatus(status);
        cell.setStyle(styleForStatus(normalized));

        String tooltipText;
        switch (normalized) {
            case STATUS_OCCUPIED:
                tooltipText = "Occupied";
                break;
            case STATUS_BUSY:
                tooltipText = "Busy";
                break;
            case STATUS_FREE:
            default:
                tooltipText = "Free";
                break;
        }
        Tooltip.install(cell, new Tooltip(tooltipText));
    }

    private void updateCellVisual(Region cell) {
        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 7; col++) {
                if (scheduleCells[row][col] == cell) {
                    applyStatusToCell(cell, scheduleStatuses[row][col]);
                    return;
                }
            }
        }
    }

    private String normalizeStatus(String status) {
        if (status == null) {
            return STATUS_FREE;
        }
        String normalized = status.toLowerCase();
        if (!STATUS_OCCUPIED.equals(normalized) && !STATUS_BUSY.equals(normalized) && !STATUS_FREE.equals(normalized)) {
            return STATUS_FREE;
        }
        return normalized;
    }

    private void saveScheduleToUserAvailableTime() {
        if (user == null || scheduleStatuses == null) {
            return;
        }

        StringBuilder sb = new StringBuilder(AVAILABLE_TIME_PREFIX);
        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 7; col++) {
                sb.append(scheduleStatuses[row][col]);
                if (!(row == 13 && col == 6)) {
                    sb.append(',');
                }
            }
        }

        user.setAvailableTime(sb.toString());
        UserService.updateTAProfile(user);
    }

    private void loadScheduleFromUserAvailableTime() {
        if (user == null || user.getAvailableTime() == null || !user.getAvailableTime().startsWith(AVAILABLE_TIME_PREFIX)) {
            return;
        }

        String payload = user.getAvailableTime().substring(AVAILABLE_TIME_PREFIX.length());
        String[] parts = payload.split(",");
        if (parts.length != 98) {
            return;
        }

        int idx = 0;
        for (int row = 0; row < 14; row++) {
            for (int col = 0; col < 7; col++) {
                String status = normalizeStatus(parts[idx++]);
                scheduleStatuses[row][col] = status;
                applyStatusToCell(scheduleCells[row][col], status);
            }
        }
    }

    private String styleForStatus(String status) {
        switch (status) {
            case STATUS_OCCUPIED:
                return "-fx-background-color: #fde68a; -fx-background-radius: 4; -fx-border-color: #f59e0b; -fx-border-radius: 4;";
            case STATUS_BUSY:
                return "-fx-background-color: #fecaca; -fx-background-radius: 4; -fx-border-color: #ef4444; -fx-border-radius: 4;";
            case STATUS_FREE:
            default:
                return "-fx-background-color: #dbeafe; -fx-background-radius: 4; -fx-border-color: #93c5fd; -fx-border-radius: 4;";
        }
    }
    
    private void showPendingWorkloadNotifications() {
        if (user == null) {
            return;
        }

        java.util.List<String> messages = WorkloadNotificationService.consumeNotifications(user.getId());
        if (messages == null || messages.isEmpty()) {
            return;
        }

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < messages.size(); i++) {
            content.append(i + 1).append(") ").append(messages.get(i)).append("\n\n");
        }

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Workload Notice");
        alert.setHeaderText("Message from Admin");
        alert.setContentText(content.toString().trim());
        alert.initModality(Modality.APPLICATION_MODAL);
        if (stage != null) {
            alert.initOwner(stage);
        }
        alert.showAndWait();
    }

    // AI Job Matching
    @FXML
    private void handleAIJobMatching(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            // Show prompt message
            Alert alert1 = new Alert(AlertType.INFORMATION);
            alert1.setTitle("Information");
            alert1.setHeaderText("Cannot use AI Job Matching");
            alert1.setContentText("Your profile has not been approved yet, cannot use AI job matching feature.");
            alert1.initModality(Modality.APPLICATION_MODAL);
            alert1.showAndWait();
            return;
        }
        
        // Create a non-closeable progress dialog
        Stage progressStage = new Stage();
        progressStage.setTitle("AI Job Matching");
        progressStage.initModality(Modality.APPLICATION_MODAL);
        if (stage != null) {
            progressStage.initOwner(stage);
        }
        progressStage.setResizable(false);
        
        // Allow user to close this progress window if needed
        
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        
        Label progressLabel = new Label("Analyzing your profile, skills, and resume...");
        progressLabel.setAlignment(Pos.CENTER);
        
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        
        vbox.getChildren().addAll(progressLabel, progressIndicator);
        
        Scene scene = new Scene(vbox, 400, 200);
        progressStage.setScene(scene);
        progressStage.show();
        
        // Run the analysis in a background thread
        new Thread(() -> {
            try {
                // Get all jobs from job board
                List<Job> availableJobs = JobService.getAllJobs();
                
                // Build recommendation message
                StringBuilder message = new StringBuilder();
                final boolean[] hasJobs = {false};
                final String[] rawOutputHolder = {""};
                
                if (!availableJobs.isEmpty()) {
                    // Build applicant information
                    StringBuilder applicantInfo = new StringBuilder();
                    applicantInfo.append("Name: " ).append(user.getName()).append("\n");
                    applicantInfo.append("Department: " ).append(user.getDepartment()).append("\n");
                    applicantInfo.append("Grade: " ).append(user.getGrade()).append("\n");
                    applicantInfo.append("Skills: " ).append(user.getSkills() != null ? String.join(", ", user.getSkills()) : "None").append("\n");
                    applicantInfo.append("Experience: " ).append(user.getExperience() != null ? user.getExperience() : "None").append("\n");
                    applicantInfo.append("Other Skills: " ).append(user.getOtherSkills() != null ? user.getOtherSkills() : "None").append("\n");
                    applicantInfo.append("Available Time: " ).append(user.getAvailableTime() != null ? user.getAvailableTime() : "None").append("\n");
                    
                    // Use AI service to handle resume parsing (supports PDF and text files)
                    String resumePath = user.getResumePath();
                    System.out.println("简历文件路径: " + resumePath);
                    
                    // Build job information
                    StringBuilder jobInfo = new StringBuilder("[");
                    int maxJobs = 5; // Limit to 5 jobs to avoid token limit
                    int jobCount = 0;
                    for (int i = 0; i < availableJobs.size() && jobCount < maxJobs; i++) {
                        Job job = availableJobs.get(i);
                        jobInfo.append("{");
                        jobInfo.append("\"id\": \"" ).append(job.getId()).append("\",");
                        jobInfo.append("\"title\": \"" ).append(job.getTitle()).append("\",");
                        jobInfo.append("\"type\": \"" ).append(job.getType()).append("\",");
                        jobInfo.append("\"department\": \"" ).append(job.getDepartment()).append("\",");
                        // Limit job description to avoid token limit
                        String description = job.getDescription() != null ? job.getDescription() : "None";
                        if (description.length() > 200) {
                            description = description.substring(0, 200) + "...";
                        }
                        jobInfo.append("\"description\": \"" ).append(description.replace("\"", "\\\"")).append("\",");
                        jobInfo.append("\"skills\": [");
                        if (job.getSkills() != null) {
                            for (int j = 0; j < job.getSkills().size(); j++) {
                                jobInfo.append("\"" ).append(job.getSkills().get(j)).append("\"");
                                if (j < job.getSkills().size() - 1) jobInfo.append(",");
                            }
                        }
                        jobInfo.append("],");
                        jobInfo.append("\"workTime\": \"" ).append(job.getWorkTime()).append("\",");
                        jobInfo.append("\"deadline\": \"" ).append(job.getDeadline()).append("\",");
                        jobInfo.append("\"recruitNum\": " ).append(job.getRecruitNum());
                        jobInfo.append("}");
                        jobCount++;
                        if (i < availableJobs.size() - 1 && jobCount < maxJobs) jobInfo.append(",");
                    }
                    jobInfo.append("]");
                    
                    // 使用新的优化匹配流程
                    rawOutputHolder[0] = AIJobMatchingController.newOptimizedJobMatching(user.getId(), resumePath);
                    String output = rawOutputHolder[0];
                    
                    // Build recommendation message
                    message.append("=== AI Job Matching Results ===\n\n");
                    message.append("Based on your profile, skills, and resume (if provided), we analyzed your fit against available positions.\n\n");
                    
                    // 解析新API返回的结果
                    try {
                        // Explicitly include AI score in output for MO ranking view
                        System.out.println("API Output: " + output);
                        
                        // 直接尝试解析原始output作为JSON
                        try {
                            com.google.gson.JsonObject jsonObject = new com.google.gson.Gson().fromJson(output, com.google.gson.JsonObject.class);
                            
                            // 检查是否有错误
                            if (jsonObject.has("error")) {
                                // API返回错误信息，使用备用方法
                                throw new Exception("API returned error: " + jsonObject.get("error").getAsString());
                            }
                            
                            // 打印解析后的JSON对象，以便调试
                            System.out.println("Parsed JSON Object: " + jsonObject.toString());
                            
                            // 增强的JSON解析
                            boolean hasMatchingResults = false;
                            
                            // 获取匹配结果（主内容）
                            if (jsonObject.has("matching_results")) {
                                hasMatchingResults = true;
                                com.google.gson.JsonArray resultsArray = jsonObject.getAsJsonArray("matching_results");
                                System.out.println("Matching Results Array Size: " + resultsArray.size());
                                for (int i = 0; i < resultsArray.size(); i++) {
                                    com.google.gson.JsonObject result = resultsArray.get(i).getAsJsonObject();
                                    System.out.println("Result " + i + ": " + result.toString());
                                    
                                    // 提取并显示匹配结果的所有可能字段
                                    StringBuilder matchResult = new StringBuilder();
                                    
                                    // 提取排名
                                    int rank = 0;
                                    if (result.has("rank")) {
                                        rank = result.get("rank").getAsInt();
                                        matchResult.append(rank).append(". ");
                                    } else {
                                        matchResult.append((i + 1)).append(". ");
                                    }
                                    
                                    // 提取岗位名称
                                    String jobTitle = "未知岗位";
                                    if (result.has("job_title")) {
                                        jobTitle = result.get("job_title").getAsString();
                                        matchResult.append(jobTitle);
                                    } else if (result.has("title")) {
                                        jobTitle = result.get("title").getAsString();
                                        matchResult.append(jobTitle);
                                    }
                                    
                                    // 提取部门信息
                                    String department = "";
                                    if (result.has("department")) {
                                        department = result.get("department").getAsString();
                                        if (!department.isEmpty()) {
                                            matchResult.append(" (").append(department).append(")");
                                        }
                                    }
                                    matchResult.append("\n");
                                    
                                    // 提取岗位ID
                                    String jobId = "";
                                    if (result.has("job_id")) {
                                        jobId = result.get("job_id").getAsString();
                                        matchResult.append("   岗位编号: ").append(jobId).append("\n");
                                    } else if (result.has("id")) {
                                        jobId = result.get("id").getAsString();
                                        matchResult.append("   岗位编号: ").append(jobId).append("\n");
                                    }
                                    
                                    // 提取总分
                                    double totalScore = 0.0;
                                    if (result.has("total_score")) {
                                        totalScore = result.get("total_score").getAsDouble();
                                        matchResult.append("   Overall Score: ").append(String.format("%.2f", totalScore)).append(" pts\n");
                                    } else if (result.has("score")) {
                                        totalScore = result.get("score").getAsDouble();
                                        matchResult.append("   Match Score: ").append(String.format("%.2f", totalScore)).append(" pts\n");
                                    }
                                    
                                    // 提取相似度分数
                                    double similarityScore = 0.0;
                                    if (result.has("similarity_score")) {
                                        similarityScore = result.get("similarity_score").getAsDouble();
                                        matchResult.append("   Similarity: ").append(String.format("%.2f%%", similarityScore * 100)).append("\n");
                                    }
                                    
                                    // 显示详细的匹配维度分数（时间、技能、经验）
                                    boolean hasDetailScores = false;
                                    StringBuilder detailScores = new StringBuilder();
                                    
                                    // 时间匹配
                                    if (result.has("time_match_score")) {
                                        double timeMatchScore = result.get("time_match_score").getAsDouble();
                                        if (timeMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Time Match: ").append(String.format("%.2f", timeMatchScore)).append(" pts\n");
                                        }
                                    } else if (result.has("time_score")) {
                                        double timeMatchScore = result.get("time_score").getAsDouble();
                                        if (timeMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Time Match: ").append(String.format("%.2f", timeMatchScore)).append(" pts\n");
                                        }
                                    }
                                    
                                    // 技能匹配
                                    if (result.has("skill_match_score")) {
                                        double skillMatchScore = result.get("skill_match_score").getAsDouble();
                                        if (skillMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Skill Match: ").append(String.format("%.2f", skillMatchScore)).append(" pts\n");
                                        }
                                    } else if (result.has("skill_score")) {
                                        double skillMatchScore = result.get("skill_score").getAsDouble();
                                        if (skillMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Skill Match: ").append(String.format("%.2f", skillMatchScore)).append(" pts\n");
                                        }
                                    }
                                    
                                    // 经验匹配
                                    if (result.has("experience_match_score")) {
                                        double experienceMatchScore = result.get("experience_match_score").getAsDouble();
                                        if (experienceMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Experience Match: ").append(String.format("%.2f", experienceMatchScore)).append(" pts\n");
                                        }
                                    } else if (result.has("experience_score")) {
                                        double experienceMatchScore = result.get("experience_score").getAsDouble();
                                        if (experienceMatchScore > 0) {
                                            if (!hasDetailScores) {
                                                detailScores.append("   Detailed Scoring:\n");
                                                hasDetailScores = true;
                                            }
                                            detailScores.append("     Experience Match: ").append(String.format("%.2f", experienceMatchScore)).append(" pts\n");
                                        }
                                    }
                                    
                                    if (hasDetailScores) {
                                        matchResult.append(detailScores);
                                    }
                                    
                                    // 提取匹配理由/分析
                                    String reason = "N/A";
                                    if (result.has("reason")) {
                                        reason = result.get("reason").getAsString();
                                    } else if (result.has("matching_reason")) {
                                        reason = result.get("matching_reason").getAsString();
                                    } else if (result.has("analysis")) {
                                        reason = result.get("analysis").getAsString();
                                    } else if (result.has("description")) {
                                        reason = result.get("description").getAsString();
                                    }
                                    matchResult.append("   Rationale: ").append(reason).append("\n\n");
                                    
                                    message.append(matchResult);
                                }
                            }
                            
                            // 获取整体总结
                            boolean hasOverallSummary = false;
                            if (jsonObject.has("overall_summary")) {
                                hasOverallSummary = true;
                                String overallSummary = jsonObject.get("overall_summary").getAsString();
                                System.out.println("Overall Summary: " + overallSummary);
                                message.append("=== Overall Recommendation ===\n");
                                message.append(overallSummary).append("\n");
                            } else if (jsonObject.has("summary")) {
                                hasOverallSummary = true;
                                String summary = jsonObject.get("summary").getAsString();
                                message.append("=== Overall Recommendation ===\n");
                                message.append(summary).append("\n");
                            } else if (jsonObject.has("recommendation")) {
                                hasOverallSummary = true;
                                String recommendation = jsonObject.get("recommendation").getAsString();
                                message.append("=== Overall Recommendation ===\n");
                                message.append(recommendation).append("\n");
                            } else {
                                System.out.println("No overall_summary field found in JSON");
                                // 如果没有整体总结，使用默认文本
                                message.append("=== Overall Recommendation ===\n");
                                message.append("基于您的个人资料、技能和简历（如有提供），我们已分析您与可用职位的匹配度。\n");
                            }
                            
                            // 增强：检查并显示所有可能的其他字段
                            // 分析/详细分析
                            if (jsonObject.has("analysis")) {
                                String analysis = jsonObject.get("analysis").getAsString();
                                message.append("\n=== 详细分析 ===\n");
                                message.append(analysis).append("\n");
                            } else if (jsonObject.has("detailed_analysis")) {
                                String detailedAnalysis = jsonObject.get("detailed_analysis").getAsString();
                                message.append("\n=== 详细分析 ===\n");
                                message.append(detailedAnalysis).append("\n");
                            } else if (jsonObject.has("detailed_report")) {
                                String detailedReport = jsonObject.get("detailed_report").getAsString();
                                message.append("\n=== 详细报告 ===\n");
                                message.append(detailedReport).append("\n");
                            }
                            
                            // 技能建议
                            if (jsonObject.has("skill_suggestions")) {
                                String skillSuggestions = jsonObject.get("skill_suggestions").getAsString();
                                message.append("\n=== 技能提升建议 ===\n");
                                message.append(skillSuggestions).append("\n");
                            } else if (jsonObject.has("suggestions")) {
                                String suggestions = jsonObject.get("suggestions").getAsString();
                                message.append("\n=== 建议 ===\n");
                                message.append(suggestions).append("\n");
                            } else if (jsonObject.has("improvement_tips")) {
                                String tips = jsonObject.get("improvement_tips").getAsString();
                                message.append("\n=== 提升建议 ===\n");
                                message.append(tips).append("\n");
                            }
                            
                            // 其他可能的字段
                            // 检查是否有content字段（可能是备用结构）
                            if (jsonObject.has("content")) {
                                String content = jsonObject.get("content").getAsString();
                                message.append("\n=== Additional Content ===\n");
                                message.append(content).append("\n");
                            }
                            
                            // 如果没有找到matching_results，尝试其他可能的结构
                            if (!hasMatchingResults) {
                                // 尝试results字段
                                if (jsonObject.has("results")) {
                                    com.google.gson.JsonElement resultsElem = jsonObject.get("results");
                                    if (resultsElem.isJsonArray()) {
                                        com.google.gson.JsonArray resultsArray = resultsElem.getAsJsonArray();
                                        message.append("\n=== Recommended Results ===\n");
                                        for (int i = 0; i < resultsArray.size(); i++) {
                                            message.append(resultsArray.get(i).toString()).append("\n");
                                        }
                                    } else {
                                        message.append("\n=== Recommended Results ===\n");
                                        message.append(resultsElem.toString()).append("\n");
                                    }
                                }
                                // 尝试raw_response字段
                                else if (jsonObject.has("raw_response")) {
                                    String rawResp = jsonObject.get("raw_response").getAsString();
                                    message.append("\n=== Raw Result ===\n");
                                    message.append(rawResp).append("\n");
                                }
                            }
                        } catch (com.google.gson.JsonSyntaxException e) {
                            // 如果直接解析失败，尝试提取JSON部分
                            String jsonPart = extractJsonFromOutput(output);
                            if (jsonPart.trim().startsWith("{")) {
                                try {
                                    com.google.gson.JsonObject jsonObject = new com.google.gson.Gson().fromJson(jsonPart, com.google.gson.JsonObject.class);
                                    
                                    // 检查是否有错误
                                    if (jsonObject.has("error")) {
                                        // API返回错误信息，使用备用方法
                                        throw new Exception("API returned error: " + jsonObject.get("error").getAsString());
                                    }
                                    
                                    // 重新使用相同的逻辑解析
                                    // 获取匹配结果
                                    if (jsonObject.has("matching_results")) {
                                        com.google.gson.JsonArray resultsArray = jsonObject.getAsJsonArray("matching_results");
                                        for (int i = 0; i < resultsArray.size(); i++) {
                                            com.google.gson.JsonObject result = resultsArray.get(i).getAsJsonObject();
                                            
                                            int rank = result.has("rank") ? result.get("rank").getAsInt() : (i + 1);
                                            String jobId = result.has("job_id") ? result.get("job_id").getAsString() : (result.has("id") ? result.get("id").getAsString() : "");
                                            String jobTitle = result.has("job_title") ? result.get("job_title").getAsString() : (result.has("title") ? result.get("title").getAsString() : "Unknown Position");
                                            double totalScore = result.has("total_score") ? result.get("total_score").getAsDouble() : (result.has("score") ? result.get("score").getAsDouble() : 0.0);
                                            double similarityScore = result.has("similarity_score") ? result.get("similarity_score").getAsDouble() : 0.0;
                                            String reason = result.has("reason") ? result.get("reason").getAsString() : 
                                                         (result.has("matching_reason") ? result.get("matching_reason").getAsString() : 
                                                          (result.has("analysis") ? result.get("analysis").getAsString() : "无"));
                                            String department = result.has("department") ? result.get("department").getAsString() : "";
                                            
                                            message.append(rank).append(". ").append(jobTitle);
                                            if (!department.isEmpty()) {
                                                message.append(" (").append(department).append(")");
                                            }
                                            message.append("\n");
                                            if (!jobId.isEmpty()) {
                                                message.append("   Job ID: ").append(jobId).append("\n");
                                            }
                                            message.append("   匹配总分: ").append(String.format("%.2f", totalScore)).append(" 分\n");
                                            if (similarityScore > 0) {
                                                message.append("   相似度: ").append(String.format("%.2f%%", similarityScore * 100)).append("\n");
                                            }
                                            
                                            message.append("   分析依据: ").append(reason).append("\n\n");
                                        }
                                    }
                                    
                                    // 获取整体总结
                                    if (jsonObject.has("overall_summary")) {
                                        message.append("=== Overall Recommendation ===\n");
                                        message.append(jsonObject.get("overall_summary").getAsString()).append("\n");
                                    } else if (jsonObject.has("summary")) {
                                        message.append("=== Overall Recommendation ===\n");
                                        message.append(jsonObject.get("summary").getAsString()).append("\n");
                                    } else {
                                        message.append("=== Overall Recommendation ===\n");
                                        message.append("Based on your profile, skills, and resume (if provided), we analyzed your fit against available positions.\n");
                                    }
                                } catch (Exception innerE) {
                                    throw e; // 抛出异常，使用备用方法
                                }
                            } else {
                                throw new Exception("Not a valid JSON format");
                            }
                        }
                    } catch (Exception e) {
                        // 打印异常信息，以便调试
                        e.printStackTrace();
                        // 如果解析失败，使用旧方法作为备用
                        message.append("Unable to parse AI output. Fallback ranking is used.\n\n");
                        
                        // Get top 3 jobs (or fewer if less than 3)
                        List<Job> recommendedJobs = AIService.recommendJobsForTA(user, 3);
                        int limit = Math.min(3, recommendedJobs.size());
                        
                        for (int i = 0; i < limit; i++) {
                            Job job = recommendedJobs.get(i);
                            double matchScore = AIService.calculateSkillMatch(user, job);
                            message.append((i + 1)).append(". " ).append(job.getTitle()).append(" (" ).append(job.getType()).append(")\n" );
                            message.append("   Department: ").append(job.getDepartment()).append("\n" );
                            message.append("   Work Time: ").append(job.getWorkTime()).append("\n" );
                            message.append("   Deadline: ").append(job.getDeadline()).append("\n" );
                            message.append("   Match Score: ").append(String.format("%.2f%%", matchScore)).append("\n" );
                            
                            // Add brief analysis
                            message.append("   Analysis: ");
                            if (matchScore >= 90) {
                                message.append("Excellent match! Your skills and experience align very well with this role.");
                            } else if (matchScore >= 70) {
                                message.append("Good match. You meet most of the required skills and experience.");
                            } else if (matchScore >= 50) {
                                message.append("Moderate match. You meet part of the requirements, with room to improve.");
                            } else {
                                message.append("Limited match. Consider improving relevant skills and experience.");
                            }
                            message.append("\n\n");
                        }
                        
                        // Add overall recommendation
                        message.append("=== 综合推荐 ===\n");
                        message.append("Based on your profile, we recommend applying to the positions above.\n");
                        message.append("If you uploaded a resume, its content is also considered in the analysis.\n");
                    }
                    
                    hasJobs[0] = true;
                }
                
                // Close progress dialog and show results
                javafx.application.Platform.runLater(() -> {
                    progressStage.close();
                    
                    if (hasJobs[0]) {
                        // Create a resizable results window
                        Stage resultsStage = new Stage();
                        resultsStage.setTitle("AI Job Matching Results");
                        resultsStage.initModality(Modality.APPLICATION_MODAL);
                        if (stage != null) {
                            resultsStage.initOwner(stage);
                        }
                        resultsStage.setResizable(true);
                        
                        VBox vboxResults = new VBox(20);
                        vboxResults.setPadding(new Insets(30));
                        vboxResults.setAlignment(Pos.TOP_LEFT);
                        
                        TextArea resultsTextArea = new TextArea(message.toString());
                        resultsTextArea.setEditable(false);
                        resultsTextArea.setWrapText(true);
                        resultsTextArea.setPrefSize(600, 500);
                        
                        // 添加按钮区域
                        HBox buttonBox = new HBox(15);
                        buttonBox.setAlignment(Pos.CENTER);
                        
                        // 查看原始JSON按钮
                        Button showRawJsonButton = new Button("View Raw JSON");
                        showRawJsonButton.setOnAction(e -> {
                            // 创建显示原始JSON的窗口
                            Stage rawJsonStage = new Stage();
                            rawJsonStage.setTitle("Raw API Response JSON");
                            rawJsonStage.initModality(Modality.APPLICATION_MODAL);
                            if (stage != null) {
                                rawJsonStage.initOwner(stage);
                            }
                            rawJsonStage.setResizable(true);
                            
                            VBox rawJsonVbox = new VBox(15);
                            rawJsonVbox.setPadding(new Insets(20));
                            
                            TextArea rawJsonTextArea = new TextArea(rawOutputHolder[0]);
                            rawJsonTextArea.setEditable(false);
                            rawJsonTextArea.setWrapText(true);
                            rawJsonTextArea.setPrefSize(750, 500);
                            
                            Button rawJsonCloseButton = new Button("Close");
                            rawJsonCloseButton.setOnAction(ev -> rawJsonStage.close());
                            
                            // 添加复制JSON按钮
                            Button copyJsonButton = new Button("Copy JSON");
                            copyJsonButton.setOnAction(ev -> {
                                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                                javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                                content.putString(rawOutputHolder[0]);
                                clipboard.setContent(content);
                                
                                // 显示复制成功的提示
                                Alert copyAlert = new Alert(AlertType.INFORMATION);
                                copyAlert.setTitle("Notice");
                                copyAlert.setHeaderText(null);
                                copyAlert.setContentText("JSON has been copied to clipboard.");
                                copyAlert.initModality(Modality.APPLICATION_MODAL);
                                copyAlert.showAndWait();
                            });
                            
                            HBox rawJsonButtonBox = new HBox(10);
                            rawJsonButtonBox.setAlignment(Pos.CENTER);
                            rawJsonButtonBox.getChildren().addAll(copyJsonButton, rawJsonCloseButton);
                            
                            rawJsonVbox.getChildren().addAll(
                                new Label("Raw JSON response from API:"),
                                rawJsonTextArea,
                                rawJsonButtonBox
                            );
                            VBox.setMargin(rawJsonButtonBox, new Insets(10, 0, 0, 0));
                            
                            Scene rawJsonScene = new Scene(rawJsonVbox, 800, 600);
                            rawJsonStage.setScene(rawJsonScene);
                            rawJsonStage.show();
                        });
                        
                        // 关闭按钮
                        Button closeButton = new Button("Close");
                        closeButton.setOnAction(e -> resultsStage.close());
                        
                        buttonBox.getChildren().addAll(showRawJsonButton, closeButton);
                        
                        vboxResults.getChildren().addAll(resultsTextArea, buttonBox);
                        VBox.setMargin(buttonBox, new Insets(10, 0, 0, 0));
                        
                        Scene resultsScene = new Scene(vboxResults, 700, 600);
                        resultsStage.setScene(resultsScene);
                        resultsStage.show();
                    } else {
                        Alert alert3 = new Alert(AlertType.INFORMATION);
                        alert3.setTitle("Information");
                        alert3.setHeaderText("AI Job Matching");
                        alert3.setContentText("No available job recommendations.");
                        alert3.initModality(Modality.APPLICATION_MODAL);
                        alert3.showAndWait();
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    progressStage.close();
                    Alert alert5 = new Alert(Alert.AlertType.ERROR);
                    alert5.setTitle("Error");
                    alert5.setHeaderText("AI Job Matching Failed");
                    alert5.setContentText("AI job matching failed: " + e.getMessage());
                    alert5.initModality(Modality.APPLICATION_MODAL);
                    alert5.showAndWait();
                });
            }
        }).start();
    }
    
    // Skill Gap Analysis
    @FXML
    private void handleIdentifyMissingSkills(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Cannot use Skill Gap Analysis");
            alert.setContentText("Your profile has not been approved yet, cannot use skill gap analysis feature.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        List<Job> availableJobs = JobService.getAllJobs();
        if (availableJobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Skill Gap Analysis");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Create a job selection dialog
        Stage selectionStage = new Stage();
        selectionStage.setTitle("Select Target Job");
        selectionStage.initModality(Modality.APPLICATION_MODAL);
        if (stage != null) {
            selectionStage.initOwner(stage);
        }
        selectionStage.setResizable(true);
        
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(20));
        vbox.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Select a job to analyze skill gaps:");
        
        // Create job list with radio buttons
        ObservableList<Job> jobItems = FXCollections.observableArrayList(availableJobs);
        javafx.scene.control.ListView<Job> jobListView = new javafx.scene.control.ListView<>(jobItems);
        jobListView.setPrefSize(500, 300);
        jobListView.setCellFactory(param -> new javafx.scene.control.ListCell<Job>() {
            @Override
            protected void updateItem(Job job, boolean empty) {
                super.updateItem(job, empty);
                if (empty || job == null) {
                    setText(null);
                } else {
                    setText(job.getTitle() + " (" + job.getDepartment() + ")");
                }
            }
        });
        
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button selectButton = new Button("Analyze Skill Gap");
        selectButton.setDisable(true);
        selectButton.setOnAction(e -> {
            Job selectedJob = jobListView.getSelectionModel().getSelectedItem();
            if (selectedJob != null) {
                selectionStage.close();
                performSkillGapAnalysis(selectedJob);
            }
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> selectionStage.close());
        
        buttonBox.getChildren().addAll(selectButton, cancelButton);
        
        // Enable select button when an item is selected
        jobListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectButton.setDisable(newVal == null);
        });
        
        vbox.getChildren().addAll(titleLabel, jobListView, buttonBox);
        
        Scene scene = new Scene(vbox, 550, 400);
        selectionStage.setScene(scene);
        selectionStage.show();
    }
    
    /**
     * Perform skill gap analysis for the selected job
     */
    private void performSkillGapAnalysis(Job selectedJob) {
        Stage progressStage = new Stage();
        progressStage.setTitle("Skill Gap Analysis");
        progressStage.initModality(Modality.APPLICATION_MODAL);
        if (stage != null) {
            progressStage.initOwner(stage);
        }
        progressStage.setResizable(false);
        progressStage.setOnCloseRequest(e -> e.consume());
        
        VBox vbox = new VBox(20);
        vbox.setPadding(new Insets(30));
        vbox.setAlignment(Pos.CENTER);
        
        Label progressLabel = new Label("Extracting skills from profile and resume...");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        
        vbox.getChildren().addAll(progressLabel, progressIndicator);
        
        Scene scene = new Scene(vbox, 400, 200);
        progressStage.setScene(scene);
        progressStage.show();
        
        new Thread(() -> {
            try {
                System.out.println("\n************************************************");
                System.out.println("[TADashboard] Skill Gap Analysis 开始");
                System.out.println("************************************************");
                
                // Step 1: Build profile text from user's personal information
                StringBuilder profileText = new StringBuilder();
                profileText.append("Name: ").append(user.getName()).append("\n");
                profileText.append("Department: ").append(user.getDepartment()).append("\n");
                profileText.append("Grade: ").append(user.getGrade()).append("\n");
                profileText.append("Available Time: ").append(user.getAvailableTime() != null ? user.getAvailableTime() : "Not specified").append("\n");
                profileText.append("Skills: ").append(user.getSkills() != null ? String.join(", ", user.getSkills()) : "None").append("\n");
                profileText.append("Experience: ").append(user.getExperience() != null ? user.getExperience() : "None").append("\n");
                profileText.append("Language Skills: ").append(user.getLanguageSkills() != null ? user.getLanguageSkills() : "None").append("\n");
                profileText.append("Other Skills: ").append(user.getOtherSkills() != null ? user.getOtherSkills() : "None").append("\n");
                
                System.out.println("\n[Step 1] 用户Profile信息已构建");
                System.out.println("- 姓名: " + user.getName());
                System.out.println("- 院系: " + user.getDepartment());
                System.out.println("- 年级: " + user.getGrade());
                System.out.println("- 技能: " + (user.getSkills() != null ? String.join(", ", user.getSkills()) : "None"));
                
                // Step 2: Extract resume content if available
                String resumeContent = "";
                String resumePath = user.getResumePath();
                if (resumePath != null && !resumePath.isEmpty()) {
                    System.out.println("\n[Step 2] 检测到简历文件，开始提取...");
                    System.out.println("简历路径: " + resumePath);
                    resumeContent = PDFResumeParserService.extractContentFromFile(resumePath, 2000);
                    if (resumeContent != null && !resumeContent.isEmpty()) {
                        profileText.append("\nResume Content:\n").append(resumeContent);
                        System.out.println("简历内容提取成功，长度: " + resumeContent.length() + " 字符");
                    } else {
                        System.out.println("简历内容为空或提取失败");
                    }
                } else {
                    System.out.println("\n[Step 2] 未检测到简历文件，跳过简历提取");
                }
                
                // Step 3: Build job requirements text
                StringBuilder jobRequirementsText = new StringBuilder();
                jobRequirementsText.append("Job Title: ").append(selectedJob.getTitle()).append("\n");
                jobRequirementsText.append("Department: ").append(selectedJob.getDepartment()).append("\n");
                jobRequirementsText.append("Job Type: ").append(selectedJob.getType()).append("\n");
                jobRequirementsText.append("Work Time: ").append(selectedJob.getWorkTime()).append("\n");
                jobRequirementsText.append("Description: ").append(selectedJob.getDescription() != null ? selectedJob.getDescription() : "None").append("\n");
                jobRequirementsText.append("Required Skills: ").append(selectedJob.getSkills() != null ? String.join(", ", selectedJob.getSkills()) : "None").append("\n");
                
                System.out.println("\n[Step 3] 岗位要求信息已构建");
                System.out.println("- 岗位: " + selectedJob.getTitle());
                System.out.println("- 部门: " + selectedJob.getDepartment());
                System.out.println("- 所需技能: " + (selectedJob.getSkills() != null ? String.join(", ", selectedJob.getSkills()) : "None"));
                
                // Step 4: Call AI API to extract structured skills and perform analysis
                final Job finalSelectedJob = selectedJob;
                final String finalProfileText = profileText.toString();
                final String finalJobRequirementsText = jobRequirementsText.toString();
                
                System.out.println("\n[Step 4] 准备调用AI API进行技能差距分析...");
                
                String tempResultJson;
                try {
                    tempResultJson = SkillGapAnalysisService.analyzeSkillGapFromProfileAndJob(finalProfileText, finalJobRequirementsText);
                    System.out.println("\n[Step 5] AI API调用成功!");
                } catch (Exception apiEx) {
                    // If API fails, fall back to local analysis
                    System.out.println("\n[!] AI API调用失败: " + apiEx.getMessage());
                    System.out.println("[!] 切换到本地分析模式...");
                    tempResultJson = performLocalSkillGapAnalysis(finalProfileText, finalJobRequirementsText, finalSelectedJob);
                    System.out.println("[Step 5] 本地分析完成");
                }
                
                final String finalResultJson = tempResultJson;
                
                System.out.println("\n[Step 6] 分析结果:");
                System.out.println("-----------------------------------");
                System.out.println(finalResultJson.length() > 300 ? finalResultJson.substring(0, 300) + "..." : finalResultJson);
                System.out.println("-----------------------------------");
                System.out.println("\n************************************************");
                System.out.println("[TADashboard] Skill Gap Analysis 完成");
                System.out.println("************************************************\n");
                
                javafx.application.Platform.runLater(() -> {
                    progressStage.close();
                    displaySkillGapResult(finalResultJson, finalSelectedJob.getTitle());
                });
                
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    progressStage.close();
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Skill Gap Analysis Failed");
                    alert.setContentText("Failed to perform skill gap analysis: " + e.getMessage());
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.showAndWait();
                });
            }
        }).start();
    }
    
    /**
     * Perform local skill gap analysis when AI API is unavailable
     */
    private String performLocalSkillGapAnalysis(String profileText, String jobRequirementsText, Job selectedJob) {
        ProfileInfo profileInfo = new ProfileInfo();
        profileInfo.setProfileUrl("User Profile");
        
        // Extract skills from profile text
        List<String> profileSkills = extractSkillsFromText(profileText);
        for (String skill : profileSkills) {
            profileInfo.getCurrentSkills().add(new SkillInfo(skill, ProficiencyLevel.INTERMEDIATE));
        }
        
        // Add work experience
        if (user.getExperience() != null && !user.getExperience().isEmpty()) {
            profileInfo.getWorkExperience().add(user.getExperience());
        }
        
        // Add education background
        profileInfo.getEducationBackground().add(user.getGrade() + ", " + user.getDepartment());
        
        // Build job requirements
        List<JobSkillRequirement> requirements = new ArrayList<>();
        if (selectedJob.getSkills() != null) {
            for (String skill : selectedJob.getSkills()) {
                requirements.add(new JobSkillRequirement(skill, ProficiencyLevel.INTERMEDIATE, true));
            }
        }
        
        return SkillGapAnalysisService.analyzeSkillGapLocal(profileInfo, requirements);
    }
    
    /**
     * Extract skills from text using simple pattern matching
     */
    private List<String> extractSkillsFromText(String text) {
        List<String> skills = new ArrayList<>();
        
        if (text == null || text.isEmpty()) {
            return skills;
        }
        
        // Common technical skills to look for
        String[] commonSkills = {"Java", "Python", "SQL", "JavaScript", "C++", "C#", "HTML", "CSS", 
            "Machine Learning", "Deep Learning", "AI", "Git", "Linux", "Docker", "Spring", 
            "React", "Node.js", "MySQL", "PostgreSQL", "MongoDB", "TensorFlow", "PyTorch"};
        
        for (String skill : commonSkills) {
            if (text.toLowerCase().contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }
        
        // Also extract skills from Skills field
        int skillsStart = text.indexOf("Skills: ");
        if (skillsStart != -1) {
            int skillsEnd = text.indexOf("\n", skillsStart + 8);
            if (skillsEnd == -1) skillsEnd = text.length();
            String skillsStr = text.substring(skillsStart + 8, skillsEnd).trim();
            String[] skillArray = skillsStr.split("[,;、，；]");
            for (String skill : skillArray) {
                String trimmedSkill = skill.trim();
                if (!trimmedSkill.isEmpty() && !trimmedSkill.equalsIgnoreCase("None") && !skills.contains(trimmedSkill)) {
                    skills.add(trimmedSkill);
                }
            }
        }
        
        return skills;
    }
    
    /**
     * Display the skill gap analysis result
     */
    private void displaySkillGapResult(String resultJson, String jobTitle) {
        try {
            com.google.gson.JsonObject result = new com.google.gson.Gson().fromJson(resultJson, com.google.gson.JsonObject.class);
            
            StringBuilder message = new StringBuilder();
            message.append("=== Skill Gap Analysis Report ===\n\n");
            message.append("Target Job: ").append(jobTitle).append("\n\n");
            
            // Section 1: Your Existing Skills
            message.append("[1. Your Existing Skills]\n");
            boolean hasMatchedSkills = false;
            if (result.has("matchedSkills") && result.get("matchedSkills").isJsonArray()) {
                com.google.gson.JsonArray matchedArray = result.getAsJsonArray("matchedSkills");
                if (matchedArray.size() > 0) {
                    hasMatchedSkills = true;
                    for (int i = 0; i < matchedArray.size(); i++) {
                        message.append(" ✓ ").append(matchedArray.get(i).getAsString()).append("\n");
                    }
                }
            }
            if (!hasMatchedSkills) {
                message.append("  No matched skills\n");
            }
            message.append("\n");
            
            // Section 2: Areas for Improvement
            message.append("[2. Areas for Improvement]\n");
            boolean hasImprovementAreas = false;
            
            // Missing Skills
            if (result.has("missingSkills") && result.get("missingSkills").isJsonArray()) {
                com.google.gson.JsonArray missingArray = result.getAsJsonArray("missingSkills");
                if (missingArray.size() > 0) {
                    hasImprovementAreas = true;
                    message.append(" ▶ Missing Skills:\n");
                    for (int i = 0; i < missingArray.size(); i++) {
                        com.google.gson.JsonObject skillObj = missingArray.get(i).getAsJsonObject();
                        String skillName = skillObj.has("skillName") ? skillObj.get("skillName").getAsString() : "Unknown";
                        String priority = skillObj.has("priority") ? skillObj.get("priority").getAsString() : "Low";
                        message.append("    - ").append(skillName).append(" (Priority: ").append(priority).append(")\n");
                        
                        // Add improvement suggestions
                        if (skillObj.has("improvementSuggestions") && skillObj.get("improvementSuggestions").isJsonArray()) {
                            com.google.gson.JsonArray suggestionsArray = skillObj.getAsJsonArray("improvementSuggestions");
                            for (int j = 0; j < suggestionsArray.size(); j++) {
                                message.append("      → ").append(suggestionsArray.get(j).getAsString()).append("\n");
                            }
                        }
                    }
                }
            }
            
            // Insufficient Skills (Proficiency Gap)
            if (result.has("insufficientSkills") && result.get("insufficientSkills").isJsonArray()) {
                com.google.gson.JsonArray insufficientArray = result.getAsJsonArray("insufficientSkills");
                if (insufficientArray.size() > 0) {
                    hasImprovementAreas = true;
                    message.append(" ▶ Insufficient Skills:\n");
                    for (int i = 0; i < insufficientArray.size(); i++) {
                        com.google.gson.JsonObject skillObj = insufficientArray.get(i).getAsJsonObject();
                        String skillName = skillObj.has("skillName") ? skillObj.get("skillName").getAsString() : "Unknown";
                        String currentProf = skillObj.has("currentProficiency") ? skillObj.get("currentProficiency").getAsString() : "Unknown";
                        String requiredProf = skillObj.has("requiredProficiency") ? skillObj.get("requiredProficiency").getAsString() : "Unknown";
                        String priority = skillObj.has("priority") ? skillObj.get("priority").getAsString() : "Low";
                        message.append("    - ").append(skillName).append(": Current ").append(currentProf).append(" → Required ").append(requiredProf);
                        message.append(" (Priority: ").append(priority).append(")\n");
                        
                        // Add improvement suggestions
                        if (skillObj.has("improvementSuggestions") && skillObj.get("improvementSuggestions").isJsonArray()) {
                            com.google.gson.JsonArray suggestionsArray = skillObj.getAsJsonArray("improvementSuggestions");
                            for (int j = 0; j < suggestionsArray.size(); j++) {
                                message.append("      → ").append(suggestionsArray.get(j).getAsString()).append("\n");
                            }
                        }
                    }
                }
            }
            
            if (!hasImprovementAreas) {
                message.append("  No areas for improvement needed. You meet all requirements!\n");
            }
            message.append("\n");
            
            // Section 3: Overall Assessment and Recommendations
            message.append("[3. Overall Assessment and Recommendations]\n");
            if (result.has("summary")) {
                message.append(result.get("summary").getAsString()).append("\n");
            } else {
                message.append("Please refer to the analysis above and improve relevant skills accordingly.\n");
            }
            
            // Create result window
            Stage resultsStage = new Stage();
            resultsStage.setTitle("Skill Gap Analysis Result");
            resultsStage.initModality(Modality.APPLICATION_MODAL);
            if (stage != null) {
                resultsStage.initOwner(stage);
            }
            resultsStage.setResizable(true);
            
            VBox vbox = new VBox(20);
            vbox.setPadding(new Insets(30));
            vbox.setAlignment(Pos.TOP_LEFT);
            
            TextArea resultsTextArea = new TextArea(message.toString());
            resultsTextArea.setEditable(false);
            resultsTextArea.setWrapText(true);
            resultsTextArea.setPrefSize(600, 500);
            
            Button closeButton = new Button("Close");
            closeButton.setOnAction(e -> resultsStage.close());
            
            HBox buttonBox = new HBox();
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().add(closeButton);
            
            vbox.getChildren().addAll(resultsTextArea, buttonBox);
            VBox.setMargin(buttonBox, new Insets(10, 0, 0, 0));
            
            Scene resultsScene = new Scene(vbox, 700, 600);
            resultsStage.setScene(resultsScene);
            resultsStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to display result");
            alert.setContentText("Failed to parse skill gap analysis result: " + e.getMessage());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // View Jobs
    @FXML
    private void handleViewJobs(ActionEvent event) {
        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("View Jobs");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list
        StringBuilder jobList = new StringBuilder("=== Available Jobs ===\n\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobList.append((i + 1)).append(". " + job.getTitle() + " (" + job.getType() + ")\n");
            jobList.append("   Department: " + job.getDepartment() + "\n");
            jobList.append("   Work Time: " + job.getWorkTime() + "\n");
            jobList.append("   Recruitment Number: " + job.getRecruitNum() + "\n");
            jobList.append("   Deadline: " + job.getDeadline() + "\n\n");
        }
        
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("View Jobs");
        alert.setHeaderText("Available Jobs");
        alert.setContentText(jobList.toString());
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }
    
    // Apply for Job
    @FXML
    private void handleApplyForJob(ActionEvent event) {
        if (user == null || user.getProfileStatus() != ProfileStatus.APPROVED) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Apply for Job");
            alert.setContentText("Your profile has not been approved yet, cannot apply for jobs.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        List<Job> jobs = JobService.getAvailableJobs();
        if (jobs.isEmpty()) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Apply for Job");
            alert.setContentText("No available jobs!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // Build job list
        StringBuilder jobList = new StringBuilder("=== Apply for Job ===\n\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobList.append((i + 1)).append(". " + job.getTitle() + " (" + job.getType() + ")\n");
        }
        
        Alert jobAlert = new Alert(AlertType.INFORMATION);
        jobAlert.setTitle("Apply for Job");
        jobAlert.setHeaderText("Select Job");
        jobAlert.setContentText(jobList.toString());
        jobAlert.initModality(Modality.APPLICATION_MODAL);
        jobAlert.showAndWait();
        
        // Simplified processing here, should pop up a dialog for user to select job and input cover letter in actual implementation
        // For demonstration, we select the first job
        if (!jobs.isEmpty()) {
            Job job = jobs.get(0);
            String coverLetter = "I am very interested in this position and hope to have the opportunity to join.";
            
            Application application = ApplicationService.submitApplication(user.getId(), job.getId(), coverLetter);
            if (application != null) {
                Alert successAlert = new Alert(AlertType.INFORMATION);
                successAlert.setTitle("Application Success");
                successAlert.setHeaderText("Application Submitted Successfully");
                successAlert.setContentText("Application submitted successfully! Match Score: " + application.getMatchScore());
                successAlert.initModality(Modality.APPLICATION_MODAL);
                successAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(AlertType.INFORMATION);
                errorAlert.setTitle("Application Failed");
                errorAlert.setHeaderText("Failed to Submit Application");
                errorAlert.setContentText("Failed to submit application, you may have already applied for this position or the position has expired.");
                errorAlert.initModality(Modality.APPLICATION_MODAL);
                errorAlert.showAndWait();
            }
        }
    }
    
    // Update Profile
    @FXML
    private void handleUpdateProfile(ActionEvent event) {
        try {
            if (user == null) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("User Not Found");
                alert.setContentText("User information is not available. Please log in again.");
                alert.initModality(Modality.APPLICATION_MODAL);
                alert.showAndWait();
                return;
            }
            
            // 清除缓存并重新获取用户数据
            service.CacheService.invalidate("ta_profile_" + user.getId());
            model.TA updatedUser = service.UserService.getTAProfile(user.getId());
            if (updatedUser != null) {
                user = updatedUser;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileEdit.fxml"));
            Parent root = loader.load();
            TAProfileEditController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = this.stage;
            if (stage == null && event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            }
            if (stage == null) {
                if (pendingTaskListContainer != null && pendingTaskListContainer.getScene() != null) {
                    stage = (Stage) pendingTaskListContainer.getScene().getWindow();
                } else if (openPositionsLabel != null && openPositionsLabel.getScene() != null) {
                    stage = (Stage) openPositionsLabel.getScene().getWindow();
                }
            }
            if (stage == null) {
                throw new IllegalStateException("Stage is not available.");
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Update Profile");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load update profile page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Upload Resume
    @FXML
    private void handleUploadResume(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAUploadResume.fxml"));
            Parent root = loader.load();
            TAUploadResumeController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = this.stage;
            if (stage == null && event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            }
            if (stage == null) {
                if (pendingTaskListContainer != null && pendingTaskListContainer.getScene() != null) {
                    stage = (Stage) pendingTaskListContainer.getScene().getWindow();
                } else if (openPositionsLabel != null && openPositionsLabel.getScene() != null) {
                    stage = (Stage) openPositionsLabel.getScene().getWindow();
                }
            }
            if (stage == null) {
                throw new IllegalStateException("Stage is not available.");
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Upload Resume");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load upload resume page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Logout
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
            stage.setTitle("BUPT International School TA Recruitment System - Login");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load login page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Show welcome guide
    private void showWelcomeGuide() {
        // Check if user is first login and profile not completed
        // Only show when profile status is DRAFT (initial state) and resume not uploaded
        if (user.getProfileStatus() == ProfileStatus.DRAFT && (user.getResumePath() == null || user.getResumePath().isEmpty())) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Welcome to TA Recruitment System");
            alert.setHeaderText("Welcome Guide");
            alert.setContentText("To successfully apply for TA positions, please follow these steps:\n\n" +
                    "1. Complete Personal Profile - Fill in basic information, skills and experience\n" +
                    "2. Upload Resume - Upload your personal resume\n" +
                    "3. Use AI Job Matching - Find the most suitable positions for you\n" +
                    "4. Apply for Jobs - Submit applications and wait for review\n\n" +
                    "Please click 'Update Profile' button to start your TA application journey!");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle jobs card click
    @FXML
    private void handleJobsCardClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();
            controller.setUser(user, UserRole.TA);

            Stage currentStage = this.stage;
            if (currentStage == null && event != null && event.getSource() instanceof javafx.scene.Node) {
                currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            }
            if (currentStage == null) {
                throw new IllegalStateException("Stage is not available for navigation.");
            }

            controller.setStage(currentStage);
            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Job Board");
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load job board page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // Handle user card click
    @FXML
    private void handleUserCardClick(MouseEvent event) {
        handleMyApplicationsClick(event);
    }

    // Handle open positions card click
    @FXML
    private void handleOpenPositionsClick(MouseEvent event) {
        // Keep legacy behavior for compatibility
        handleJobsCardClick(event);
    }
    
    // Handle my applications card click
    @FXML
    public void handleMyApplicationsClick(MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
            Parent root = loader.load();
            TAApplicationHistoryController controller = loader.getController();
            controller.setUser(user);

            Stage stage = this.stage;
            if (stage == null) {
                // If stage is not set, get it from the event source
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            }
            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Application History");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load application history page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle pending tasks card click
    @FXML
    private void handlePendingTasksClick(MouseEvent event) {
        // Refresh task list
        loadPendingTasks();
    }
    
    // Handle task table click
    @FXML
    private void handleTaskClick(MouseEvent event) {
        // Task click handling is now handled directly in FXML
    }
    
    // Handle home button click
    @FXML
    private void handleHome(ActionEvent event) {
        // Refresh current page
        initializeDashboard();
    }

    // Handle application management button click
    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAApplicationHistory.fxml"));
            Parent root = loader.load();
            TAApplicationHistoryController controller = loader.getController();
            controller.setUser(user);

            Stage currentStage = this.stage;
            if (currentStage == null && event.getSource() instanceof javafx.scene.Node) {
                currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            }
            if (currentStage == null) {
                throw new IllegalStateException("Stage is not available for navigation.");
            }

            controller.setStage(currentStage);

            Scene scene = new Scene(root, currentStage.getWidth(), currentStage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            currentStage.setScene(scene);
            currentStage.setTitle("BUPT International School TA Recruitment System - Application History");
            currentStage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load application history page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // Handle job requirements button click
    @FXML
    private void handleJobRequirements(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAJobBoard.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(user, model.UserRole.TA);

            // Get current stage
            Stage stage = this.stage;
            if (stage == null && event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            }
            if (stage == null) {
                if (pendingTaskListContainer != null && pendingTaskListContainer.getScene() != null) {
                    stage = (Stage) pendingTaskListContainer.getScene().getWindow();
                } else if (openPositionsLabel != null && openPositionsLabel.getScene() != null) {
                    stage = (Stage) openPositionsLabel.getScene().getWindow();
                }
            }
            if (stage == null) {
                throw new IllegalStateException("Stage is not available.");
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Job Board");
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load job list page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // Handle personal center button click
    @FXML
    private void handlePersonalCenter(ActionEvent event) {
        // Navigate to personal center display page
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/TAProfileView.fxml"));
            Parent root = loader.load();
            TAProfileViewController controller = loader.getController();
            controller.setUser(user);
            
            // Get current stage
            Stage stage = this.stage;
            if (stage == null && event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            }
            if (stage == null) {
                if (pendingTaskListContainer != null && pendingTaskListContainer.getScene() != null) {
                    stage = (Stage) pendingTaskListContainer.getScene().getWindow();
                } else if (openPositionsLabel != null && openPositionsLabel.getScene() != null) {
                    stage = (Stage) openPositionsLabel.getScene().getWindow();
                }
            }
            if (stage == null) {
                throw new IllegalStateException("Stage is not available.");
            }
            
            controller.setStage(stage);
            
            // Keep current window size and add stylesheet
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            // Add stylesheet
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT International School TA Recruitment System - Personal Center");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Page Loading Failed");
            alert.setContentText("Failed to load personal center page, please try again later.");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // Handle add task button click
    @FXML
    private void handleAddTask(ActionEvent event) {
        if (user == null) {
            return;
        }

        Dialog<TaskItem> dialog = new Dialog<>();
        dialog.setTitle("Add Task");
        dialog.setHeaderText("Create a custom task");

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10));

        TextField titleField = new TextField();
        titleField.setPromptText("Task title");
        DatePicker duePicker = new DatePicker();
        CheckBox completedBox = new CheckBox("Completed");

        pane.add(new Label("Title:"), 0, 0);
        pane.add(titleField, 1, 0);
        pane.add(new Label("Due date:"), 0, 1);
        pane.add(duePicker, 1, 1);
        pane.add(completedBox, 1, 2);

        dialog.getDialogPane().setContent(pane);
        dialog.setResultConverter(btn -> {
            if (btn == saveType) {
                String title = titleField.getText() == null ? "" : titleField.getText().trim();
                if (title.isEmpty() || duePicker.getValue() == null) {
                    return null;
                }
                return new TaskItem(title, duePicker.getValue().toString(), false, completedBox.isSelected());
            }
            return null;
        });

        Optional<TaskItem> result = dialog.showAndWait();
        result.ifPresent(item -> {
            dashboardTasks.add(item);
            saveCustomTasksToAvailableTime();
            loadPendingTasks();
        });
    }
    
    // 从输出中提取JSON部分
    private String extractJsonFromOutput(String output) {
        // 寻找JSON的开始和结束位置
        int startIndex = output.indexOf("{");
        if (startIndex == -1) {
            return output; // 没有找到JSON，返回原始输出
        }
        
        // 寻找对应的结束括号
        int braceCount = 1;
        int endIndex = startIndex + 1;
        while (endIndex < output.length() && braceCount > 0) {
            char c = output.charAt(endIndex);
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
            endIndex++;
        }
        
        if (braceCount == 0) {
            return output.substring(startIndex, endIndex);
        } else {
            return output; // 没有找到完整的JSON，返回原始输出
        }
    }

    private static class TaskItem {
        private final String title;
        private final String dueDate;
        private final boolean systemTask;
        private boolean completed;

        TaskItem(String title, String dueDate, boolean systemTask, boolean completed) {
            this.title = title;
            this.dueDate = dueDate;
            this.systemTask = systemTask;
            this.completed = completed;
        }

        public String getTitle() { return title; }
        public String getDueDate() { return dueDate; }
        public boolean isSystemTask() { return systemTask; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }
}