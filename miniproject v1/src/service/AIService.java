package service;

import model.*;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class AIService {
    // Skill match calculation
    public static double calculateSkillMatch(TA ta, Job job) {
        double score = 0.0;
        int totalSkills = job.getSkills().size();
        int matchedSkills = 0;

        // Hard skill matching
        if (totalSkills > 0) {
            for (String skill : job.getSkills()) {
                if (ta.getSkills() != null && ta.getSkills().contains(skill)) {
                    matchedSkills++;
                }
            }
            score += (double) matchedSkills / totalSkills * 60; // Hard skills account for 60%
        }

        // Soft skill matching (simple simulation)
        if (ta.getExperience() != null && !ta.getExperience().isEmpty()) {
            score += 20; // Add 20 points for experience
        }

        // Language skill matching
        if (ta.getLanguageSkills() != null && !ta.getLanguageSkills().isEmpty()) {
            score += 10; // Add 10 points for language skills
        }

        // Other skill matching
        if (ta.getOtherSkills() != null && !ta.getOtherSkills().isEmpty()) {
            score += 10; // Add 10 points for other skills
        }

        return Math.min(score, 100.0);
    }

    // Missing skill identification
    public static List<String> identifyMissingSkills(TA ta, Job job) {
        List<String> missingSkills = new ArrayList<>();

        // Identify missing hard skills
        if (job.getSkills() != null && !job.getSkills().isEmpty()) {
            for (String skill : job.getSkills()) {
                if (ta.getSkills() == null || !ta.getSkills().contains(skill)) {
                    missingSkills.add(skill);
                }
            }
        }

        return missingSkills;
    }

    // Generate skill improvement suggestions
    public static String generateSkillSuggestions(List<String> missingSkills) {
        if (missingSkills.isEmpty()) {
            return "Your skills already meet the job requirements. No additional improvement is needed.";
        }

        StringBuilder suggestions = new StringBuilder("Suggested skills to improve:");
        for (String skill : missingSkills) {
            suggestions.append("\n- ").append(skill);
        }

        return suggestions.toString();
    }

    // Workload statistics
    public static Map<String, Integer> calculateWorkload() {
        Map<String, Integer> workloadMap = new HashMap<>();
        List<Application> applications = ApplicationService.getAllApplications();

        // Count how many times each TA has been accepted
        for (Application app : applications) {
            if (app.getStatus() == model.ApplicationStatus.ACCEPTED) {
                String taId = app.getTaId();
                workloadMap.put(taId, workloadMap.getOrDefault(taId, 0) + 1);
            }
        }

        return workloadMap;
    }

    // Workload balancing suggestions
    public static List<String> generateWorkloadSuggestions() {
        List<String> suggestions = new ArrayList<>();
        Map<String, Integer> workloadMap = calculateWorkload();

        // Find the TAs with the highest and lowest workloads
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

        // Generate suggestions
        if (highestTA != null && lowestTA != null && highestWorkload > lowestWorkload + 1) {
            TA highTA = UserService.getTAProfile(highestTA);
            TA lowTA = UserService.getTAProfile(lowestTA);

            if (highTA != null && lowTA != null) {
                suggestions.add("TA " + highTA.getName() + " has been accepted " + highestWorkload + " times. Consider prioritizing TA " + lowTA.getName() + " (only " + lowestWorkload + " times).");
            }
        }

        return suggestions;
    }

    // Recommend jobs for TA
    public static List<Job> recommendJobsForTA(TA ta, int limit) {
        List<Job> availableJobs = JobService.getAvailableJobs();
        List<Job> recommendedJobs = new ArrayList<>();

        // Calculate the match score for each job
        Map<Job, Double> jobMatchScores = new HashMap<>();
        for (Job job : availableJobs) {
            double matchScore = calculateSkillMatch(ta, job);
            jobMatchScores.put(job, matchScore);
        }

        // Sort by match score
        List<Map.Entry<Job, Double>> sortedJobs = new ArrayList<>(jobMatchScores.entrySet());
        sortedJobs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Take the top limit jobs
        for (int i = 0; i < Math.min(limit, sortedJobs.size()); i++) {
            recommendedJobs.add(sortedJobs.get(i).getKey());
        }

        return recommendedJobs;
    }

    // Recommend TAs for job
    public static List<TA> recommendTAsForJob(Job job, int limit) {
        List<User> users = UserService.getUsersByRole(model.UserRole.TA);
        List<TA> availableTAs = new ArrayList<>();

        // Filter TAs whose profiles have been approved
        for (User user : users) {
            TA ta = (TA) user;
            if (ta.getProfileStatus() == model.ProfileStatus.APPROVED) {
                availableTAs.add(ta);
            }
        }

        // Calculate the match score for each TA
        Map<TA, Double> taMatchScores = new HashMap<>();
        for (TA ta : availableTAs) {
            double matchScore = calculateSkillMatch(ta, job);
            taMatchScores.put(ta, matchScore);
        }

        // Sort by match score
        List<Map.Entry<TA, Double>> sortedTAs = new ArrayList<>(taMatchScores.entrySet());
        sortedTAs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        // Take the top limit TAs
        List<TA> recommendedTAs = new ArrayList<>();
        for (int i = 0; i < Math.min(limit, sortedTAs.size()); i++) {
            recommendedTAs.add(sortedTAs.get(i).getKey());
        }

        return recommendedTAs;
    }

    // 豆包大模型深度思考能力API接入
    private static String apiKey = "";
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/responses";
    private static final String MODEL = "doubao-seed-2-0-pro-260215";

    // 设置API密钥
    public static void setApiKey(String key) {
        apiKey = key;
    }

    // 获取API密钥
    public static String getApiKey() {
        return apiKey;
    }

    // 调用深度思考API
    public static String callDoubaoDeepThinking(String input) throws IOException {
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key is not set");
        }

        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000); // 10秒超时
        connection.setReadTimeout(30000); // 30秒读取超时

        // 构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", MODEL);
        requestBody.addProperty("input", input);
        
        JsonObject thinking = new JsonObject();
        thinking.addProperty("type", "enabled");
        requestBody.add("thinking", thinking);
        
        requestBody.addProperty("stream", true);

        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] inputBytes = requestBody.toString().getBytes("utf-8");
            os.write(inputBytes, 0, inputBytes.length);
        }

        // 读取响应
        StringBuilder response = new StringBuilder();
        int responseCode = connection.getResponseCode();
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                    response.append("\n");
                }
            }
        } else {
            // 读取错误响应
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                    response.append("\n");
                }
            }
            throw new IOException("API request failed with code " + responseCode + ": " + response.toString());
        }

        return response.toString();
    }

    // 解析深度思考API响应
    public static String parseDoubaoResponse(String response) {
        // 检查是否是错误响应
        if (response.contains("error")) {
            try {
                // 尝试解析错误信息
                JsonObject errorJson = new Gson().fromJson(response, JsonObject.class);
                if (errorJson.has("error")) {
                    JsonObject errorObj = errorJson.getAsJsonObject("error");
                    String errorMessage = errorObj.has("message") ? errorObj.get("message").getAsString() : "Unknown error";
                    String errorCode = errorObj.has("code") ? errorObj.get("code").getAsString() : "Unknown code";
                    return "API Error: " + errorCode + "\n" + errorMessage;
                }
            } catch (Exception e) {
                // 解析失败，返回原始响应
            }
        }
        
        // 对于成功响应，返回原始内容
        return response;
    }
}

