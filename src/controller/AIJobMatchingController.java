package controller;

import model.*;
import service.AIService;
import service.JobService;
import service.UserService;
import java.io.IOException;
import java.util.List;

public class AIJobMatchingController {
    // 数据提交API：接收申请人信息、简历、岗位发布数据
    public static String submitData(String applicantInfo, String resumeText, String jobInfo) {
        try {
            return AIService.processData(applicantInfo, resumeText, jobInfo);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to process data: \" + e.getMessage() + \"}";
        }
    }

    // 单岗位匹配API：调用大模型，返回单个岗位的申请人匹配结果
    public static String matchSingleJob(String jobId) {
        try {
            // 获取所有已批准的TA申请人
            List<User> users = UserService.getUsersByRole(UserRole.TA);
            List<TA> applicants = new java.util.ArrayList<>();
            for (User user : users) {
                TA ta = (TA) user;
                if (ta.getProfileStatus() == ProfileStatus.APPROVED) {
                    applicants.add(ta);
                }
            }
            
            return AIService.matchSingleJob(jobId, applicants);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to match job: \" + e.getMessage() + \"}";
        }
    }

    // 批量匹配分配API：调用大模型，完成多岗位+多申请人智能分配
    public static String batchMatchAndAssign() {
        try {
            // 获取所有已批准的TA申请人
            List<User> users = UserService.getUsersByRole(UserRole.TA);
            List<TA> applicants = new java.util.ArrayList<>();
            for (User user : users) {
                TA ta = (TA) user;
                if (ta.getProfileStatus() == ProfileStatus.APPROVED) {
                    applicants.add(ta);
                }
            }
            
            // 获取所有可用的岗位
            List<Job> jobs = JobService.getAvailableJobs();
            
            return AIService.batchMatchAndAssign(applicants, jobs);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to batch match: \" + e.getMessage() + \"}";
        }
    }

    // 匹配结果查询API：返回匹配分数、理由、分析报告
    public static String getMatchResults(String role, String matchResults) {
        try {
            return AIService.generateOutput(role, matchResults);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to generate output: \" + e.getMessage() + \"}";
        }
    }

    // 用户反馈API：收集评价，用于优化Prompt指令
    public static String submitFeedback(String historicalData, String feedback) {
        try {
            return AIService.evaluateAndOptimize(historicalData, feedback);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to evaluate: \" + e.getMessage() + \"}";
        }
    }

    // 系统配置API：管理员修改Prompt模板、匹配规则、阈值
    public static String updateSystemConfig(String matchingLogic, String finalResults) {
        try {
            return AIService.ensureCompliance(matchingLogic, finalResults);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to ensure compliance: \" + e.getMessage() + \"}";
        }
    }

    // 推荐岗位给TA
    public static String recommendJobsForTA(String taId, int limit) {
        try {
            TA ta = UserService.getTAProfile(taId);
            if (ta == null) {
                return "{\"error\": \"TA not found\"}";
            }
            
            List<Job> recommendedJobs = AIService.recommendJobsForTA(ta, limit);
            
            // 构建推荐结果JSON
            StringBuilder result = new StringBuilder("{");
            result.append("\"recommendedJobs\": [");
            for (int i = 0; i < recommendedJobs.size(); i++) {
                Job job = recommendedJobs.get(i);
                result.append("{");
                result.append("\"id\": \"").append(job.getId()).append("\",");
                result.append("\"title\": \"").append(job.getTitle()).append("\",");
                result.append("\"type\": \"").append(job.getType()).append("\"");
                result.append("}");
                if (i < recommendedJobs.size() - 1) result.append(",");
            }
            result.append("]}");
            
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to recommend jobs: \" + e.getMessage() + \"}";
        }
    }

    // 推荐TA给岗位
    public static String recommendTAsForJob(String jobId, int limit) {
        try {
            Job job = JobService.getJobById(jobId);
            if (job == null) {
                return "{\"error\": \"Job not found\"}";
            }
            
            List<TA> recommendedTAs = AIService.recommendTAsForJob(job, limit);
            
            // 构建推荐结果JSON
            StringBuilder result = new StringBuilder("{");
            result.append("\"recommendedTAs\": [");
            for (int i = 0; i < recommendedTAs.size(); i++) {
                TA ta = recommendedTAs.get(i);
                result.append("{");
                result.append("\"id\": \"").append(ta.getId()).append("\",");
                result.append("\"name\": \"").append(ta.getName()).append("\",");
                result.append("\"skills\": [");
                if (ta.getSkills() != null) {
                    for (int j = 0; j < ta.getSkills().size(); j++) {
                        result.append("\"").append(ta.getSkills().get(j)).append("\"");
                        if (j < ta.getSkills().size() - 1) result.append(",");
                    }
                }
                result.append("]");
                result.append("}");
                if (i < recommendedTAs.size() - 1) result.append(",");
            }
            result.append("]}");
            
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to recommend TAs: \" + e.getMessage() + \"}";
        }
    }
    
    // 优化后的AI job matching流程（旧版本）
    public static String optimizedJobMatching(String taId, String resumeText) {
        try {
            // 1. 获取TA信息
            TA ta = UserService.getTAProfile(taId);
            if (ta == null) {
                return "{\"error\": \"TA not found\"}";
            }
            
            // 2. 获取前5个岗位
            List<Job> jobs = JobService.getAvailableJobs();
            if (jobs.size() > 5) {
                jobs = jobs.subList(0, 5); // 截断为前5个岗位
            }
            
            // 3. 构建申请人精简文本
            String applicantInfo = AIService.buildApplicantInfoText(ta);
            
            // 4. 截断简历文本（如果有）
            if (resumeText != null) {
                resumeText = AIService.truncateText(resumeText, 1000);
            }
            
            // 5. 第1次调用：申请人信息结构化精简
            String applicantKeyInfo = AIService.extractApplicantKeyInfo(applicantInfo, resumeText);
            
            // 6. 构建岗位精简文本
            String jobsInfo = AIService.buildJobsInfoText(jobs);
            
            // 7. 第2次调用：岗位信息结构化精简
            String jobsKeyInfo = AIService.extractJobKeyInfo(jobsInfo);
            
            // 8. 第3次调用：批量匹配+打分+排序+报告生成
            String matchingResults = AIService.matchJobsWithScoring(applicantKeyInfo, jobsKeyInfo);
            
            // 9. 检查匹配结果，处理无匹配情况
            // 这里可以解析JSON并检查匹配分数，如果所有分数都低于60分，添加提示
            
            return matchingResults;
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"匹配服务暂时不可用，请稍后重试\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"匹配过程中发生错误，请稍后重试\"}";
        }
    }
    
    // 全新的AI job matching流程（使用4个模型的5步调用）
    public static String newOptimizedJobMatching(String taId, String resumeText) {
        try {
            // 1. 获取TA信息
            TA ta = UserService.getTAProfile(taId);
            if (ta == null) {
                return "{\"error\": \"TA not found\"}";
            }
            
            // 2. 调用完整的5步流程
            return AIService.optimizedJobMatchingFlow(ta, resumeText);
        } catch (IOException e) {
            e.printStackTrace();
            return "{\"error\": \"匹配服务暂时不可用，请稍后重试\"}";
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"匹配过程中发生错误，请稍后重试\"}";
        }
    }
} 
