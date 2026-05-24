package service;

import service.SkillGapAnalysisService.*;
import java.util.List;

public class SkillGapAnalysisTest {
    
    public static void main(String[] args) {
        System.out.println("=== Skill Gap Analysis 测试用例 ===");
        
        // 测试1：基本功能测试
        System.out.println("\n--- 测试1：基本技能差距分析 ---");
        testBasicAnalysis();
        
        // 测试2：边界情况测试 - 空技能列表
        System.out.println("\n--- 测试2：空技能列表测试 ---");
        testEmptySkills();
        
        // 测试3：边界情况测试 - 所有技能匹配
        System.out.println("\n--- 测试3：所有技能匹配测试 ---");
        testAllSkillsMatched();
        
        // 测试4：边界情况测试 - 所有技能缺失
        System.out.println("\n--- 测试4：所有技能缺失测试 ---");
        testAllSkillsMissing();
        
        // 测试5：优先级测试
        System.out.println("\n--- 测试5：差距优先级测试 ---");
        testPriorityCalculation();
        
        System.out.println("\n=== 所有测试完成 ===");
    }
    
    /**
     * 测试基本技能差距分析
     */
    private static void testBasicAnalysis() {
        try {
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/test");
            
            profile.getCurrentSkills().add(new SkillInfo("Java", ProficiencyLevel.ADVANCED));
            profile.getCurrentSkills().add(new SkillInfo("Python", ProficiencyLevel.INTERMEDIATE));
            profile.getCurrentSkills().add(new SkillInfo("SQL", ProficiencyLevel.EXPERT));
            profile.getCurrentSkills().add(new SkillInfo("Git", ProficiencyLevel.BEGINNER));
            
            profile.getWorkExperience().add("2年TA经验，负责Java课程辅导");
            profile.getEducationBackground().add("本科，计算机科学");
            
            List<JobSkillRequirement> requirements = List.of(
                new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true),
                new JobSkillRequirement("Python", ProficiencyLevel.ADVANCED, true),
                new JobSkillRequirement("深度学习", ProficiencyLevel.INTERMEDIATE, true),
                new JobSkillRequirement("Git", ProficiencyLevel.INTERMEDIATE, false),
                new JobSkillRequirement("Linux", ProficiencyLevel.BEGINNER, false)
            );
            
            String result = SkillGapAnalysisService.analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果: " + result);
            
            // 验证结果
            if (result.contains("\"success\":true") && 
                result.contains("missingSkills") && 
                result.contains("insufficientSkills") &&
                result.contains("matchedSkills")) {
                System.out.println("✓ 测试通过");
            } else {
                System.out.println("✗ 测试失败：结果格式不正确");
            }
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试空技能列表
     */
    private static void testEmptySkills() {
        try {
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/empty");
            
            List<JobSkillRequirement> requirements = List.of(
                new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true)
            );
            
            String result = SkillGapAnalysisService.analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果: " + result);
            
            if (result.contains("\"success\":true") && 
                result.contains("missingSkills") &&
                result.contains("Java")) {
                System.out.println("✓ 测试通过");
            } else {
                System.out.println("✗ 测试失败");
            }
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试所有技能匹配
     */
    private static void testAllSkillsMatched() {
        try {
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/perfect");
            
            profile.getCurrentSkills().add(new SkillInfo("Java", ProficiencyLevel.ADVANCED));
            profile.getCurrentSkills().add(new SkillInfo("Python", ProficiencyLevel.INTERMEDIATE));
            
            List<JobSkillRequirement> requirements = List.of(
                new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true),
                new JobSkillRequirement("Python", ProficiencyLevel.INTERMEDIATE, false)
            );
            
            String result = SkillGapAnalysisService.analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果: " + result);
            
            if (result.contains("\"success\":true") && 
                result.contains("\"missingSkills\":[]") && 
                result.contains("\"insufficientSkills\":[]") &&
                result.contains("matchedSkills")) {
                System.out.println("✓ 测试通过");
            } else {
                System.out.println("✗ 测试失败");
            }
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试所有技能缺失
     */
    private static void testAllSkillsMissing() {
        try {
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/noskills");
            
            profile.getCurrentSkills().add(new SkillInfo("C++", ProficiencyLevel.ADVANCED));
            
            List<JobSkillRequirement> requirements = List.of(
                new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true),
                new JobSkillRequirement("Python", ProficiencyLevel.INTERMEDIATE, true)
            );
            
            String result = SkillGapAnalysisService.analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果: " + result);
            
            if (result.contains("\"success\":true") && 
                result.contains("missingSkills") &&
                result.contains("Java") &&
                result.contains("Python")) {
                System.out.println("✓ 测试通过");
            } else {
                System.out.println("✗ 测试失败");
            }
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
        }
    }
    
    /**
     * 测试差距优先级计算
     */
    private static void testPriorityCalculation() {
        try {
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/priority");
            
            profile.getCurrentSkills().add(new SkillInfo("Java", ProficiencyLevel.BEGINNER));
            profile.getCurrentSkills().add(new SkillInfo("Python", ProficiencyLevel.INTERMEDIATE));
            
            List<JobSkillRequirement> requirements = List.of(
                new JobSkillRequirement("Java", ProficiencyLevel.EXPERT, true),      // 必需，差距大 -> 高优先级
                new JobSkillRequirement("Python", ProficiencyLevel.ADVANCED, true),  // 必需，差距小 -> 中优先级
                new JobSkillRequirement("SQL", ProficiencyLevel.INTERMEDIATE, false)  // 非必需，缺失 -> 低优先级
            );
            
            String result = SkillGapAnalysisService.analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果: " + result);
            
            if (result.contains("\"priority\":\"高\"") && 
                result.contains("\"priority\":\"中\"") &&
                result.contains("\"priority\":\"低\"")) {
                System.out.println("✓ 测试通过");
            } else {
                System.out.println("✗ 测试失败");
            }
            
        } catch (Exception e) {
            System.out.println("✗ 测试失败: " + e.getMessage());
        }
    }
}