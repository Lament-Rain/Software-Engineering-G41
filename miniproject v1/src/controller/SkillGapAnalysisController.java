package controller;

import service.SkillGapAnalysisService;
import service.SkillGapAnalysisService.*;

import java.util.List;

public class SkillGapAnalysisController {
    
    /**
     * 完整的技能差距分析API
     * @param profileUrl TA的Profile链接
     * @param jobRequirementsJson 目标岗位技能要求（JSON格式）
     * @return 结构化分析结果（JSON格式）
     */
    public static String analyzeSkillGap(String profileUrl, String jobRequirementsJson) {
        try {
            return SkillGapAnalysisService.analyzeSkillGapFromUrl(profileUrl, jobRequirementsJson);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"errorMessage\": \"技能差距分析失败: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * 本地技能差距分析API（无需API调用）
     * @param profileInfo 个人Profile信息
     * @param jobRequirements 目标岗位技能要求列表
     * @return 结构化分析结果（JSON格式）
     */
    public static String analyzeSkillGapLocal(ProfileInfo profileInfo, List<JobSkillRequirement> jobRequirements) {
        try {
            return SkillGapAnalysisService.analyzeSkillGapLocal(profileInfo, jobRequirements);
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"success\": false, \"errorMessage\": \"技能差距分析失败: " + e.getMessage() + "\"}";
        }
    }
    
    /**
     * 设置API配置
     * @param apiKey API密钥
     * @param model 模型名称
     */
    public static void setApiConfig(String apiKey, String model) {
        SkillGapAnalysisService.setApiConfig(apiKey, model);
    }
    
    /**
     * 获取当前API配置
     * @return API配置信息（JSON格式）
     */
    public static String getApiConfig() {
        return "{\"apiKey\": \"" + (SkillGapAnalysisService.getApiKey().isEmpty() ? "未配置" : "已配置") + 
               "\", \"model\": \"" + SkillGapAnalysisService.getCurrentModel() + "\"}";
    }
    
    /**
     * 解析Profile JSON
     * @param profileJson Profile内容JSON
     * @return 解析后的ProfileInfo对象
     */
    public static ProfileInfo parseProfile(String profileJson) {
        return SkillGapAnalysisService.parseProfileJson(profileJson);
    }
    
    /**
     * 解析岗位要求JSON
     * @param jobRequirementsJson 岗位要求JSON
     * @return 解析后的JobSkillRequirement列表
     */
    public static List<JobSkillRequirement> parseJobRequirements(String jobRequirementsJson) {
        return SkillGapAnalysisService.parseJobRequirementsJson(jobRequirementsJson);
    }
    
    /**
     * 测试控制器
     */
    public static void main(String[] args) {
        System.out.println("=== Skill Gap Analysis Controller 测试 ===");
        
        // 测试本地分析
        System.out.println("\n--- 测试本地分析 ---");
        ProfileInfo profile = new ProfileInfo();
        profile.setProfileUrl("https://example.com/profile/test");
        profile.getCurrentSkills().add(new SkillInfo("Java", ProficiencyLevel.ADVANCED));
        profile.getCurrentSkills().add(new SkillInfo("Python", ProficiencyLevel.INTERMEDIATE));
        profile.getCurrentSkills().add(new SkillInfo("SQL", ProficiencyLevel.ADVANCED));
        
        List<JobSkillRequirement> requirements = List.of(
            new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true),
            new JobSkillRequirement("Python", ProficiencyLevel.ADVANCED, true),
            new JobSkillRequirement("深度学习", ProficiencyLevel.INTERMEDIATE, true)
        );
        
        String result = analyzeSkillGapLocal(profile, requirements);
        System.out.println("分析结果: " + result);
    }
}