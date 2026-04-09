package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import model.MO;
import model.JobViewModel;
import model.Job;
import model.Application;
import model.ApplicationStatus;
import model.JobStatus;
import model.TA;
import service.ApplicationService;
import service.JobService;
import service.UserService;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;

public class MODashboardController {
    @FXML
    private Label publishedJobsLabel;
    @FXML
    private Label totalApplicationsLabel;
    @FXML
    private Label acceptedApplicationsLabel;
    @FXML
    private TableView<JobViewModel> jobsTable;
    
    private MO user;
    
    public void setUser(MO user) {
        this.user = user;
        initializeDashboard();
    }
    
    private void initializeDashboard() {
        if (user == null) return;
        
        try {
            // 加载统计数据
            loadStatistics();
            
            // 加载最近发布的职位
            loadRecentJobs();
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("初始化仪表板失败");
            alert.setContentText("初始化仪表板失败: " + e.getMessage());
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    private void loadStatistics() {
        // 调试信息
        System.out.println("loadStatistics - User ID: " + user.getId());
        
        // 获取发布的职位数量
        List<Job> jobsForStats = JobService.getAllJobsByMO(user.getId());
        System.out.println("loadStatistics - Job count: " + jobsForStats.size());
        
        int publishedJobsCount = jobsForStats.size();
        publishedJobsLabel.setText(String.valueOf(publishedJobsCount));
        
        // 获取总申请数量
        int totalApplicationsCount = 0;
        int acceptedApplicationsCount = 0;
        
        for (Job job : jobsForStats) {
            System.out.println("loadStatistics - Job: " + job.getTitle() + ", MO ID: " + job.getMoId());
            int appCount = ApplicationService.getApplicationsByJob(job.getId()).size();
            totalApplicationsCount += appCount;
            
            // 计算已接受的申请数量
            for (Application app : ApplicationService.getApplicationsByJob(job.getId())) {
                if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                    acceptedApplicationsCount++;
                }
            }
        }
        
        totalApplicationsLabel.setText(String.valueOf(totalApplicationsCount));
        acceptedApplicationsLabel.setText(String.valueOf(acceptedApplicationsCount));
    }
    
    private void loadRecentJobs() {
        // 从服务中获取实际的职位数据
        ObservableList<JobViewModel> jobs = FXCollections.observableArrayList();
        
        // 获取MO发布的所有职位
        List<Job> jobList = JobService.getAllJobsByMO(user.getId());
        
        // 调试信息
        System.out.println("loadRecentJobs - Job count: " + jobList.size());
        
        // 转换为JobViewModel
        for (Job job : jobList) {
            // 调试信息
            System.out.println("Job: " + job.getTitle() + ", MO ID: " + job.getMoId() + ", Status: " + job.getStatus());
            
            // 计算申请数量
            int applicationCount = ApplicationService.getApplicationsByJob(job.getId()).size();
            
            // 创建JobViewModel并添加到列表
            jobs.add(new JobViewModel(job.getTitle(), job.getDepartment(), job.getDeadline(), applicationCount));
        }
        
        // 调试信息
        System.out.println("JobViewModel count: " + jobs.size());
        
        // 设置表格数据
        jobsTable.setItems(jobs);
        // 刷新表格
        jobsTable.refresh();
    }
    
    // 发布职位
    @FXML
    private void handleCreateJob(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setUser(user);
            
            // 获取当前舞台
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) jobsTable.getScene().getWindow();
            }
            
            controller.setStage(stage);
            
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 发布TA职位");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("发布职位页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // 查看申请
    @FXML
    private void handleViewApplications(ActionEvent event) {
        openApplicationReviewPage(event);
    }
    
    // 导出数据
    @FXML
    private void handleExportData(ActionEvent event) {
        if (user == null) return;
        
        List<Job> jobs = JobService.getAllJobsByMO(user.getId());
        if (jobs.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText("导出申请数据");
            alert.setContentText("暂无发布的职位！");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // 构建职位列表
        StringBuilder jobList = new StringBuilder("=== 导出申请数据 ===\n\n");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobList.append((i + 1)).append(". " + job.getTitle() + "\n");
        }
        
        javafx.scene.control.Alert jobAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        jobAlert.setTitle("导出申请数据");
        jobAlert.setHeaderText("选择职位");
        jobAlert.setContentText(jobList.toString());
        jobAlert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        jobAlert.showAndWait();
        
        // 这里简化处理，实际应该弹出一个对话框让用户选择职位
        // 为了演示，我们选择第一个职位
        if (!jobs.isEmpty()) {
            Job job = jobs.get(0);
            List<Application> applications = ApplicationService.getApplicationsByJob(job.getId());
            
            if (applications.isEmpty()) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText("导出申请数据");
                alert.setContentText("该职位暂无申请记录！");
                alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                alert.showAndWait();
                return;
            }
            
            // 导出为CSV
            String filePath = "src/data/application_data_" + job.getTitle().replace(" ", "_") + "_" + java.time.LocalDateTime.now().toString().replace(":", "-") + ".csv";
            try (java.io.FileWriter writer = new java.io.FileWriter(filePath)) {
                writer.write("申请人,所属院系,申请时间,状态,匹配度,审核意见\n");
                for (Application app : applications) {
                    TA ta = UserService.getTAProfile(app.getTaId());
                    if (ta != null) {
                        writer.write(ta.getName() + "," + ta.getDepartment() + "," + app.getCreatedAt() + "," + app.getStatus() + "," + app.getMatchScore() + "," + (app.getReviewComment() != null ? app.getReviewComment() : "") + "\n");
                    }
                }
                javafx.scene.control.Alert successAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                successAlert.setTitle("导出成功");
                successAlert.setHeaderText("数据导出成功");
                successAlert.setContentText("数据已导出至：" + filePath);
                successAlert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                successAlert.showAndWait();
            } catch (java.io.IOException e) {
                javafx.scene.control.Alert errorAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                errorAlert.setTitle("导出失败");
                errorAlert.setHeaderText("数据导出失败");
                errorAlert.setContentText("导出失败：" + e.getMessage());
                errorAlert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                errorAlert.showAndWait();
            }
        }
    }
    
    // 查看统计
    @FXML
    private void handleViewStatistics(ActionEvent event) {
        if (user == null) return;
        
        List<Job> jobs = JobService.getAllJobsByMO(user.getId());
        if (jobs.isEmpty()) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("提示");
            alert.setHeaderText("招聘统计");
            alert.setContentText("暂无发布的职位！");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
            return;
        }
        
        // 这里简化处理，实际应该弹出一个对话框让用户选择时间范围
        String startTime = "2026-03-01T00:00:00";
        String endTime = "2026-03-31T23:59:59";
        
        int totalApplications = 0;
        int totalAccepted = 0;
        
        // 构建统计信息
        StringBuilder statistics = new StringBuilder("=== 招聘统计 ===\n\n");
        
        for (Job job : jobs) {
            if (job.getCreatedAt().compareTo(startTime) >= 0 && job.getCreatedAt().compareTo(endTime) <= 0) {
                List<Application> apps = ApplicationService.getApplicationsByJob(job.getId());
                totalApplications += apps.size();
                for (Application app : apps) {
                    if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                        totalAccepted++;
                    }
                }
                
                // 显示每个职位的统计
                int jobApps = apps.size();
                int jobAccepted = 0;
                for (Application app : apps) {
                    if (app.getStatus() == ApplicationStatus.ACCEPTED) {
                        jobAccepted++;
                    }
                }
                double jobAcceptanceRate = jobApps > 0 ? (double) jobAccepted / jobApps * 100 : 0;
                statistics.append("职位：" + job.getTitle() + "\n");
                statistics.append("申请数：" + jobApps + "\n");
                statistics.append("录用数：" + jobAccepted + "\n");
                statistics.append("通过率：" + String.format("%.2f%%", jobAcceptanceRate) + "\n\n");
            }
        }
        
        // 显示总体统计
        double overallAcceptanceRate = totalApplications > 0 ? (double) totalAccepted / totalApplications * 100 : 0;
        statistics.append("=== 总体统计 ===\n");
        statistics.append("总申请数：" + totalApplications + "\n");
        statistics.append("总录用数：" + totalAccepted + "\n");
        statistics.append("总通过率：" + String.format("%.2f%%", overallAcceptanceRate));
        
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("招聘统计");
        alert.setHeaderText("统计结果");
        alert.setContentText(statistics.toString());
        alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        alert.showAndWait();
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
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("登录页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
    
    // 处理首页按钮点击
    @FXML
    private void handleHome(ActionEvent event) {
        // 刷新当前页面
        initializeDashboard();
    }
    
    // 处理职位管理按钮点击
    @FXML
    private void handleJobManagement(ActionEvent event) {
        if (user == null) return;

        for (Job job : JobService.getAllJobsByMO(user.getId())) {
            if (job.getStatus() == JobStatus.REJECTED) {
                openRejectedJobEditor(event, job);
                return;
            }
        }
        handleCreateJob(event);
    }

    private void openRejectedJobEditor(ActionEvent event, Job job) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOCreateJob.fxml"));
            Parent root = loader.load();
            MOCreateJobController controller = loader.getController();
            controller.setUser(user);
            controller.setStage((Stage) ((Button) event.getSource()).getScene().getWindow());
            controller.setEditingJob(job);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 重新编辑职位");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("被驳回职位编辑页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // 处理申请管理按钮点击
    @FXML
    private void handleApplicationManagement(ActionEvent event) {
        openApplicationReviewPage(event);
    }

    private void openApplicationReviewPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MOApplicationReview.fxml"));
            Parent root = loader.load();
            MOApplicationReviewController controller = loader.getController();
            controller.setUser(user);

            Stage stage = null;
            if (event != null && event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) jobsTable.getScene().getWindow();
            }

            controller.setStage(stage);
            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 申请审核");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("申请审核页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }

    // 处理统计分析按钮点击
    @FXML
    private void handleStatisticsAnalysis(ActionEvent event) {
        // 显示统计分析相关功能
        handleViewStatistics(event);
    }

    // 查看所有职位需求
    @FXML
    private void handleViewAllJobs(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/JobList.fxml"));
            Parent root = loader.load();
            JobListController controller = loader.getController();

            controller.setUser(user, model.UserRole.MO);

            // 获取当前舞台
            Stage stage = null;
            if (event.getSource() instanceof Button) {
                stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            } else {
                stage = (Stage) jobsTable.getScene().getWindow();
            }

            controller.setStage(stage);

            Scene scene = new Scene(root, stage.getWidth(), stage.getHeight());
            scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("BUPT国际学校TA招聘系统 - 职位需求");
        } catch (Exception e) {
            e.printStackTrace();
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("错误");
            alert.setHeaderText("页面加载失败");
            alert.setContentText("职位列表页面加载失败，请稍后重试。");
            alert.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            alert.showAndWait();
        }
    }
}