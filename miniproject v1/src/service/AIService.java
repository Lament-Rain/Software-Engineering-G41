package service;

import model.*;
import java.util.*;

public class AIService {
    // 技能匹配计算
    public static double calculateSkillMatch(TA ta, Job job) {
        double score = 0.0;
        int totalSkills = job.getSkills().size();
        int matchedSkills = 0;

        // 硬技能匹配
        if (totalSkills > 0) {
            for (String skill : job.getSkills()) {
                if (ta.getSkills() != null && ta.getSkills().contains(skill)) {
                    matchedSkills++;
                }
            }
            score += (double) matchedSkills / totalSkills * 60; // 硬技能占60%
        }

        // 软技能匹配（简单模拟）
        if (ta.getExperience() != null && !ta.getExperience().isEmpty()) {
            score += 20; // 有经验加20分
        }

        // 语言能力匹配
        if (ta.getLanguageSkills() != null && !ta.getLanguageSkills().isEmpty()) {
            score += 10; // 有语言能力加10分
        }

        // 其他技能匹配
        if (ta.getOtherSkills() != null && !ta.getOtherSkills().isEmpty()) {
            score += 10; // 有其他技能加10分
        }

        return Math.min(score, 100.0);
    }

    // 缺失技能识别
    public static List<String> identifyMissingSkills(TA ta, Job job) {
        List<String> missingSkills = new ArrayList<>();

        // 识别缺失的硬技能
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            for (String skill : job.getSkills()) {
                if (ta.getSkills() == null || !ta.getSkills().contains(skill)) {
                    missingSkills.add(skill);
                }
            }
        }

        return missingSkills;
    }

    // 生成技能提升建议
    public static String generateSkillSuggestions(List<String> missingSkills) {
        if (missingSkills.isEmpty()) {
            return "您的技能已满足职位要求，无需额外提升。";
        }

        StringBuilder suggestions = new StringBuilder("建议提升以下技能：");
        for (String skill : missingSkills) {
            suggestions.append("\n- ").append(skill);
        }

        return suggestions.toString();
    }

    // 工作量统计
    public static Map<String, Integer> calculateWorkload() {
        Map<String, Integer> workloadMap = new HashMap<>();
        List<Application> applications = ApplicationService.getAllApplications();

        // 统计每个TA的录用次数
        for (Application app : applications) {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                String taId = app.getTaId();
                workloadMap.put(taId, workloadMap.getOrDefault(taId, 0) + 1);
            }
        }

        return workloadMap;
    }

    // 工作量平衡建议
    public static List<String> generateWorkloadSuggestions() {
        List<String> suggestions = new ArrayList<>();
        Map<String, Integer> workloadMap = calculateWorkload();

        // 找出工作量最高和最低的TA
        String highestTA = null;
        String lowestTA = null;
        int highestWorkload = 0;
        int lowestWorkload = Integer.MAX_VALUE;

        for (Map.Entry<String, Integer> entry : workloadMap.entrySet()) {
            if (entry.getValue() > highestWorkload) {
                highestWorkload = entry.getValue();
                highestTA = entry.getKey();
            }
            if (entry.getValue() < lowestWorkload) {
                lowestWorkload = entry.getValue();
                lowestTA = entry.getKey();
            }
        }

        // 生成建议
        if (highestTA != null && lowestTA != null && highestWorkload > lowestWorkload + 1) {
            TA highTA = UserService.getTAProfile(highestTA);
            TA lowTA = UserService.getTAProfile(lowestTA);

            if (highTA != null && lowTA != null) {
                suggestions.add("TA " + highTA.getName() + " 已录用 " + highestWorkload + " 次，建议优先考虑录用 TA " + lowTA.getName() + "（仅 " + lowestWorkload + " 次）");
            }
        }

        return suggestions;
    }

    // 推荐职位给TA
    public static List<Job> recommendJobsForTA(TA ta, int limit) {
        List<Job> availableJobs = JobService.getAvailableJobs();
        List<Job> recommendedJobs = new ArrayList<>();

        // 计算每个职位的匹配度
        Map<Job, Double> jobMatchScores = new HashMap<>();
        for (Job job : availableJobs) {
            double matchScore = calculateSkillMatch(ta, job);
            jobMatchScores.put(job, matchScore);
        }

        // 按匹配度排序
        List<Map.Entry<Job, Double>> sortedJobs = new ArrayList<>(jobMatchScores.entrySet());
        sortedJobs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // 取前limit个职位
        for (int i = 0; i < Math.min(limit, sortedJobs.size()); i++) {
            recommendedJobs.add(sortedJobs.get(i).getKey());
        }

        return recommendedJobs;
    }

    // 推荐TA给职位
    public static List<TA> recommendTAsForJob(Job job, int limit) {
        List<User> users = UserService.getUsersByRole(model.UserRole.TA);
        List<TA> availableTAs = new ArrayList<>();

        // 筛选档案已通过的TA
        for (User user : users) {
            TA ta = (TA) user;
            if (ta.getProfileStatus() == model.ProfileStatus.APPROVED) {
                availableTAs.add(ta);
            }
        }

        // 计算每个TA的匹配度
        Map<TA, Double> taMatchScores = new HashMap<>();
        for (TA ta : availableTAs) {
            double matchScore = calculateSkillMatch(ta, job);
            taMatchScores.put(ta, matchScore);
        }

        // 按匹配度排序
        List<Map.Entry<TA, Double>> sortedTAs = new ArrayList<>(taMatchScores.entrySet());
        sortedTAs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // 取前limit个TA
        List<TA> recommendedTAs = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedTAs.size()); i++) {
            recommendedTAs.add(sortedTAs.get(i).getKey());
        }

        return recommendedTAs;
    }
}
