package service;

import model.*;
import java.util.*;
import java.util.stream.Collectors;
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
    private static String currentModel = "";
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/responses";
    private static final String API_KEYS_FILE = "api_keys.txt";

    // 静态初始化，加载保存的API密钥和模型
    static {
        loadApiConfigs();
    }

    // 设置API密钥和模型
    public static void setApiConfig(String key, String model) {
        apiKey = key;
        currentModel = model;
    }

    // 获取API密钥
    public static String getApiKey() {
        if (apiKey.isEmpty()) {
            loadApiConfigs();
        }
        return apiKey;
    }

    // 获取当前模型
    public static String getCurrentModel() {
        return currentModel;
    }

    // 保存API配置到文件
    public static void saveApiConfig(String key, String model) throws IOException {
        // 检查API配置是否已存在
        java.util.List<java.util.Map<String, String>> existingConfigs = loadApiConfigs();
        for (java.util.Map<String, String> config : existingConfigs) {
            if (config.get("apiKey").equals(key) && config.get("model").equals(model)) {
                throw new IllegalArgumentException("该API配置已存在！");
            }
        }
        
        java.io.File file = new java.io.File(API_KEYS_FILE);
        java.io.FileWriter writer = new java.io.FileWriter(file, true); // 追加模式
        writer.write(key + "," + model + "\n");
        writer.close();
        apiKey = key;
        currentModel = model;
    }

    // 加载所有保存的API配置
    public static java.util.List<java.util.Map<String, String>> loadApiConfigs() {
        java.util.List<java.util.Map<String, String>> apiConfigs = new java.util.ArrayList<>();
        java.io.File file = new java.io.File(API_KEYS_FILE);
        if (file.exists()) {
            try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (!line.isEmpty()) {
                        String[] parts = line.split(",");
                        if (parts.length == 2) {
                            java.util.Map<String, String> config = new java.util.HashMap<>();
                            config.put("apiKey", parts[0]);
                            config.put("model", parts[1]);
                            apiConfigs.add(config);
                        }
                    }
                }
                // 如果有API配置，默认使用第一个
                if (!apiConfigs.isEmpty() && apiKey.isEmpty()) {
                    java.util.Map<String, String> firstConfig = apiConfigs.get(0);
                    apiKey = firstConfig.get("apiKey");
                    currentModel = firstConfig.get("model");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return apiConfigs;
    }

    // 清除所有保存的API配置
    public static void clearApiConfigs() throws IOException {
        java.io.File file = new java.io.File(API_KEYS_FILE);
        if (file.exists()) {
            file.delete();
        }
        apiKey = "";
        currentModel = "";
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
        requestBody.addProperty("model", currentModel);
        requestBody.addProperty("input", input);
        
        JsonObject thinking = new JsonObject();
        thinking.addProperty("type", "enabled");
        requestBody.add("thinking", thinking);
        
        requestBody.addProperty("stream", false);

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
        // 打印原始响应内容，以便调试
        System.out.println("=== 原始API响应 ===");
        System.out.println(response);
        System.out.println("=================");
        
        // 保存原始响应到文件，便于调试
        saveRawResponseToFile(response);
        
        // 尝试直接解析整个响应为JSON
        try {
            JsonObject responseJson = new Gson().fromJson(response, JsonObject.class);
            System.out.println("直接解析为JSON成功");
            
            // 检查是否是错误响应
            if (responseJson.has("error")) {
                JsonObject errorObj = responseJson.getAsJsonObject("error");
                String errorMessage = errorObj.has("message") ? errorObj.get("message").getAsString() : "Unknown error";
                String errorCode = errorObj.has("code") ? errorObj.get("code").getAsString() : "Unknown code";
                System.out.println("API返回错误: " + errorCode + " - " + errorMessage);
                return "{\"error\": \"API Error: " + errorCode + ": " + errorMessage + "\"}";
            }
            
            // 尝试从各种可能的结构中提取内容
            String extractedContent = extractContentFromResponse(responseJson);
            if (extractedContent != null) {
                System.out.println("从响应中提取到内容: " + extractedContent.substring(0, Math.min(200, extractedContent.length())));
                // 尝试从提取的内容中找到JSON
                String jsonResult = extractJsonFromString(extractedContent);
                if (jsonResult != null) {
                    System.out.println("成功提取JSON: " + jsonResult.substring(0, Math.min(200, jsonResult.length())));
                    return jsonResult;
                }
                // 如果没有找到JSON，但有内容，将内容包装成JSON返回
                return "{\"content\": \"" + escapeJson(extractedContent) + "\"}";
            }
            
            // 如果没有找到特殊结构，检查响应本身是否已经是我们需要的格式
            if (responseJson.has("matching_results") || responseJson.has("overall_summary")) {
                System.out.println("响应本身已经包含匹配结果");
                return new Gson().toJson(responseJson);
            }
            
            // 尝试将整个响应作为内容返回
            return "{\"raw_response\": " + new Gson().toJson(responseJson) + "}";
        } catch (Exception e) {
            System.out.println("直接解析JSON失败，尝试其他方法: " + e.getMessage());
            e.printStackTrace();
        }
        
        // 如果直接解析失败，尝试从字符串中提取JSON
        try {
            String jsonResult = extractJsonFromString(response);
            if (jsonResult != null) {
                System.out.println("从字符串中成功提取JSON");
                return jsonResult;
            }
        } catch (Exception e) {
            System.out.println("从字符串提取JSON失败: " + e.getMessage());
        }
        
        // 如果所有方法都失败，返回原始响应的包装
        System.out.println("所有解析方法都失败，返回原始响应");
        return "{\"raw_response\": \"" + escapeJson(response) + "\"}";
    }
    
    // 从响应对象中提取内容
    private static String extractContentFromResponse(JsonObject responseJson) {
        // 尝试output字段
        if (responseJson.has("output")) {
            try {
                JsonArray outputArray = responseJson.getAsJsonArray("output");
                for (int i = 0; i < outputArray.size(); i++) {
                    JsonObject outputItem = outputArray.get(i).getAsJsonObject();
                    String content = extractContentFromOutputItem(outputItem);
                    if (content != null) return content;
                }
            } catch (Exception e) {
                System.out.println("从output数组提取失败: " + e.getMessage());
            }
        }
        
        // 尝试messages字段
        if (responseJson.has("messages")) {
            try {
                JsonArray messagesArray = responseJson.getAsJsonArray("messages");
                for (int i = 0; i < messagesArray.size(); i++) {
                    JsonObject messageItem = messagesArray.get(i).getAsJsonObject();
                    String content = extractContentFromMessageItem(messageItem);
                    if (content != null) return content;
                }
            } catch (Exception e) {
                System.out.println("从messages数组提取失败: " + e.getMessage());
            }
        }
        
        // 尝试choices字段（OpenAI格式）
        if (responseJson.has("choices")) {
            try {
                JsonArray choicesArray = responseJson.getAsJsonArray("choices");
                for (int i = 0; i < choicesArray.size(); i++) {
                    JsonObject choiceItem = choicesArray.get(i).getAsJsonObject();
                    String content = extractContentFromChoiceItem(choiceItem);
                    if (content != null) return content;
                }
            } catch (Exception e) {
                System.out.println("从choices数组提取失败: " + e.getMessage());
            }
        }
        
        // 尝试content字段
        if (responseJson.has("content")) {
            try {
                return responseJson.get("content").getAsString();
            } catch (Exception e) {
                System.out.println("从content字段提取失败: " + e.getMessage());
            }
        }
        
        return null;
    }
    
    // 从output item中提取内容
    private static String extractContentFromOutputItem(JsonObject outputItem) {
        if (outputItem.has("content")) {
            try {
                if (outputItem.get("content").isJsonArray()) {
                    JsonArray contentArray = outputItem.getAsJsonArray("content");
                    for (int j = 0; j < contentArray.size(); j++) {
                        JsonObject contentItem = contentArray.get(j).getAsJsonObject();
                        if (contentItem.has("output_text")) {
                            return contentItem.get("output_text").getAsString();
                        }
                        if (contentItem.has("text")) {
                            return contentItem.get("text").getAsString();
                        }
                    }
                } else if (outputItem.get("content").isJsonPrimitive()) {
                    return outputItem.get("content").getAsString();
                }
            } catch (Exception e) {
                System.out.println("从output item提取内容失败: " + e.getMessage());
            }
        }
        return null;
    }
    
    // 从message item中提取内容
    private static String extractContentFromMessageItem(JsonObject messageItem) {
        try {
            if (messageItem.has("role") && messageItem.get("role").getAsString().equals("assistant")) {
                if (messageItem.has("content")) {
                    if (messageItem.get("content").isJsonArray()) {
                        JsonArray contentArray = messageItem.getAsJsonArray("content");
                        for (int j = 0; j < contentArray.size(); j++) {
                            JsonObject contentItem = contentArray.get(j).getAsJsonObject();
                            if (contentItem.has("text")) {
                                return contentItem.get("text").getAsString();
                            }
                            if (contentItem.has("output_text")) {
                                return contentItem.get("output_text").getAsString();
                            }
                        }
                    } else if (messageItem.get("content").isJsonPrimitive()) {
                        return messageItem.get("content").getAsString();
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("从message item提取内容失败: " + e.getMessage());
        }
        return null;
    }
    
    // 从choice item中提取内容（OpenAI格式）
    private static String extractContentFromChoiceItem(JsonObject choiceItem) {
        try {
            if (choiceItem.has("message")) {
                JsonObject message = choiceItem.getAsJsonObject("message");
                if (message.has("content")) {
                    return message.get("content").getAsString();
                }
            }
            if (choiceItem.has("text")) {
                return choiceItem.get("text").getAsString();
            }
        } catch (Exception e) {
            System.out.println("从choice item提取内容失败: " + e.getMessage());
        }
        return null;
    }
    
    // 从字符串中提取JSON
    private static String extractJsonFromString(String text) {
        // 寻找第一个{和最后一个}
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            String potentialJson = text.substring(firstBrace, lastBrace + 1);
            System.out.println("提取的潜在JSON片段: " + potentialJson.substring(0, Math.min(200, potentialJson.length())));
            
            // 尝试解析
            try {
                JsonObject json = new Gson().fromJson(potentialJson, JsonObject.class);
                return new Gson().toJson(json);
            } catch (Exception e) {
                System.out.println("解析潜在JSON失败: " + e.getMessage());
                // 尝试清理可能的转义问题
                try {
                    String cleanedJson = potentialJson.replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", "\t");
                    JsonObject json = new Gson().fromJson(cleanedJson, JsonObject.class);
                    return new Gson().toJson(json);
                } catch (Exception e2) {
                    System.out.println("清理后解析仍然失败: " + e2.getMessage());
                }
            }
        }
        return null;
    }
    
    // 转义JSON字符串中的特殊字符
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // 保存原始响应到文件
    private static void saveRawResponseToFile(String response) {
        try {
            java.io.File dir = new java.io.File("api_responses");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
            java.io.File file = new java.io.File(dir, "response_" + timestamp + ".json");
            java.io.FileWriter writer = new java.io.FileWriter(file);
            writer.write(response);
            writer.close();
            System.out.println("原始响应已保存到: " + file.getAbsolutePath());
        } catch (Exception e) {
            System.out.println("保存响应文件失败: " + e.getMessage());
        }
    }

    // 数据处理模块：简历提取、数据清洗、标准化、隐私脱敏
    public static String processData(String applicantInfo, String resumeText, String jobInfo) throws IOException {
        String prompt = "# Data Processing Task\n" +
                "Process the following TA applicant and job data:\n\n" +
                "## Applicant Information\n" + applicantInfo + "\n\n" +
                "## Resume Text\n" + resumeText + "\n\n" +
                "## Job Information\n" + jobInfo + "\n\n" +
                "### Task Requirements\n" +
                "1. Extract structured data from unstructured text\n" +
                "2. Clean outliers and missing values\n" +
                "3. Standardize skills, majors, and grade formats\n" +
                "4. Anonymize privacy info (phone, ID, etc.)\n" +
                "5. Output standardized, privacy-safe applicant/job data\n\n" +
                "Return results in JSON format.";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 特征提取模块：自动生成匹配维度特征+动态权重配置
    public static String extractFeatures(String standardizedData) throws IOException {
        String prompt = "# 特征提取任务\n" +
                "请对以下标准化后的TA申请人和岗位数据进行特征提取：\n\n" +
                standardizedData + "\n\n" +
                "### 任务要求\n" +
                "1. 提取学术、技能、经验、偏好、场景适配5大核心特征\n" +
                "2. 根据理论课/实验课/核心课类型，自动分配特征权重\n" +
                "3. 输出量化特征分值和岗位专属权重配置\n\n" +
                "请以JSON格式返回提取结果。";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 核心匹配算法模块：规则初筛、精准评分、智能排序、优化分配
    public static String matchJobs(String batchApplicants, String batchJobs) throws IOException {
        String prompt = "# 核心匹配算法任务\n" +
                "请对以下批量TA申请人和批量岗位数据进行智能匹配：\n\n" +
                "## 申请人数据\n" + batchApplicants + "\n\n" +
                "## 岗位数据\n" + batchJobs + "\n\n" +
                "### 任务要求\n" +
                "1. 规则初筛：执行硬性门槛校验（修课要求、成绩、核心技能、时段重叠率≥50%），返回通过/不通过\n" +
                "2. 精准评分：计算0-100分总匹配分，生成可解释的中文匹配理由\n" +
                "3. 智能排序：对多个申请人按匹配分降序排序，标注适配优先级\n" +
                "4. 优化分配：遵循岗位人数上限、申请人工作量均衡，输出最优多岗位分配方案\n\n" +
                "请以JSON格式返回匹配结果，包含初筛结果、匹配分数、排序列表、最终分配方案和完整理由。";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 交互输出模块：面向三类角色生成个性化内容
    public static String generateOutput(String role, String matchResults) throws IOException {
        String prompt = "# 交互输出任务\n" +
                "请根据以下匹配结果，为" + role + "生成个性化内容：\n\n" +
                matchResults + "\n\n" +
                "### 任务要求\n" +
                "- 面向授课老师：生成申请人排序列表、维度得分、匹配理由、面试邀请模板\n" +
                "- 面向申请人：生成推荐岗位列表、个人优势/不足分析、能力提升建议\n" +
                "- 面向管理员：生成匹配进度报表、数据统计汇总\n" +
                "- 按指定角色、指定格式生成人性化文本，禁止冗余内容\n\n" +
                "请以JSON格式返回生成结果。";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 评估迭代模块：匹配效果评估、用户反馈分析、自动优化
    public static String evaluateAndOptimize(String historicalData, String feedback) throws IOException {
        String prompt = "# 评估迭代任务\n" +
                "请对以下历史匹配数据和用户反馈进行分析：\n\n" +
                "## 历史匹配数据\n" + historicalData + "\n\n" +
                "## 用户反馈\n" + feedback + "\n\n" +
                "### 任务要求\n" +
                "1. 计算精准度指标\n" +
                "2. 检测匹配公平性\n" +
                "3. 解析反馈建议\n" +
                "4. 输出模型指令优化方案\n\n" +
                "请以JSON格式返回评估结果和优化建议。";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 合规公平模块：算法公平性校验、可解释性保障、隐私合规
    public static String ensureCompliance(String matchingLogic, String finalResults) throws IOException {
        String prompt = "# 合规公平任务\n" +
                "请对以下匹配逻辑和最终结果进行合规性检查：\n\n" +
                "## 匹配逻辑\n" + matchingLogic + "\n\n" +
                "## 最终结果\n" + finalResults + "\n\n" +
                "### 任务要求\n" +
                "1. 剔除性别/民族等敏感特征\n" +
                "2. 校验算法偏见\n" +
                "3. 确保所有匹配结果可解释\n" +
                "4. 符合个人信息保护规范\n\n" +
                "请以JSON格式返回合规校验报告和公平性检测结果。";
        
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }

    // 单岗位匹配API：调用大模型，返回单个岗位的申请人匹配结果
    public static String matchSingleJob(String jobId, List<TA> applicants) throws IOException {
        // 构建岗位信息
        Job job = JobService.getJobById(jobId);
        if (job == null) {
            return "{\"error\": \"Job not found\"}";
        }
        
        // 构建申请人信息
        StringBuilder applicantsJson = new StringBuilder("[");
        for (int i = 0; i < applicants.size(); i++) {
            TA ta = applicants.get(i);
            applicantsJson.append("{");
            applicantsJson.append("\"id\": \"").append(ta.getId()).append("\",");
            applicantsJson.append("\"name\": \"").append(ta.getName()).append("\",");
            applicantsJson.append("\"skills\": [");
            if (ta.getSkills() != null) {
                for (int j = 0; j < ta.getSkills().size(); j++) {
                    applicantsJson.append("\"").append(ta.getSkills().get(j)).append("\"");
                    if (j < ta.getSkills().size() - 1) applicantsJson.append(",");
                }
            }
            applicantsJson.append("],");
            applicantsJson.append("\"experience\": \"").append(ta.getExperience()).append("\",");
            applicantsJson.append("\"availableTime\": \"").append(ta.getAvailableTime()).append("\"");
            applicantsJson.append("}");
            if (i < applicants.size() - 1) applicantsJson.append(",");
        }
        applicantsJson.append("]");
        
        // 构建岗位信息JSON
        StringBuilder jobJson = new StringBuilder("{");
        jobJson.append("\"id\": \"").append(job.getId()).append("\",");
        jobJson.append("\"title\": \"").append(job.getTitle()).append("\",");
        jobJson.append("\"type\": \"").append(job.getType()).append("\",");
        jobJson.append("\"skills\": [");
        if (job.getSkills() != null) {
            for (int i = 0; i < job.getSkills().size(); i++) {
                jobJson.append("\"").append(job.getSkills().get(i)).append("\"");
                if (i < job.getSkills().size() - 1) jobJson.append(",");
            }
        }
        jobJson.append("],");
        jobJson.append("\"workTime\": \"").append(job.getWorkTime()).append("\",");
        jobJson.append("\"recruitNum\": ").append(job.getRecruitNum());
        jobJson.append("}");
        
        // 调用匹配算法
        return matchJobs(applicantsJson.toString(), jobJson.toString());
    }

    // 批量匹配分配API：调用大模型，完成多岗位+多申请人智能分配
    public static String batchMatchAndAssign(List<TA> applicants, List<Job> jobs) throws IOException {
        // 构建申请人信息
        StringBuilder applicantsJson = new StringBuilder("[");
        for (int i = 0; i < applicants.size(); i++) {
            TA ta = applicants.get(i);
            applicantsJson.append("{");
            applicantsJson.append("\"id\": \"").append(ta.getId()).append("\",");
            applicantsJson.append("\"name\": \"").append(ta.getName()).append("\",");
            applicantsJson.append("\"skills\": [");
            if (ta.getSkills() != null) {
                for (int j = 0; j < ta.getSkills().size(); j++) {
                    applicantsJson.append("\"").append(ta.getSkills().get(j)).append("\"");
                    if (j < ta.getSkills().size() - 1) applicantsJson.append(",");
                }
            }
            applicantsJson.append("] ,");
            applicantsJson.append("\"experience\": \"").append(ta.getExperience()).append("\",");
            applicantsJson.append("\"availableTime\": \"").append(ta.getAvailableTime()).append("\"");
            applicantsJson.append("}");
            if (i < applicants.size() - 1) applicantsJson.append(",");
        }
        applicantsJson.append("]");
        
        // 构建岗位信息
        StringBuilder jobsJson = new StringBuilder("[");
        for (int i = 0; i < jobs.size(); i++) {
            Job job = jobs.get(i);
            jobsJson.append("{");
            jobsJson.append("\"id\": \"").append(job.getId()).append("\",");
            jobsJson.append("\"title\": \"").append(job.getTitle()).append("\",");
            jobsJson.append("\"type\": \"").append(job.getType()).append("\",");
            jobsJson.append("\"skills\": [");
            if (job.getSkills() != null) {
                for (int j = 0; j < job.getSkills().size(); j++) {
                    jobsJson.append("\"").append(job.getSkills().get(j)).append("\"");
                    if (j < job.getSkills().size() - 1) jobsJson.append(",");
                }
            }
            jobsJson.append("] ,");
            jobsJson.append("\"workTime\": \"").append(job.getWorkTime()).append("\",");
            jobsJson.append("\"recruitNum\": ").append(job.getRecruitNum());
            jobsJson.append("}");
            if (i < jobs.size() - 1) jobsJson.append(",");
        }
        jobsJson.append("]");
        
        // 调用匹配算法
        return matchJobs(applicantsJson.toString(), jobsJson.toString());
    }
    
    // 第1次调用：申请人信息结构化精简（轻量任务）
    public static String extractApplicantKeyInfo(String applicantInfo, String resumeText) throws IOException {
        // 构建prompt
        String prompt = "# 申请人信息结构化精简任务\n" +
                "你现在需要处理TA申请人的匹配信息，从给定的Profile文本（必填）和可选简历文本中，提取并精简出与岗位匹配相关的关键信息，**仅输出JSON格式，禁止任何额外文本**。\n" +
                "提取规则：\n" +
                "1.  从Profile中提取：可用工作时间（work_available_time）、核心技能（core_skills，列表形式，最多5个关键词）、TA相关经验（ta_experience，列表形式，最多3个核心经历）、额外优势（additional_advantages，如奖项/语言技能，无则写\"无\"）\n" +
                "2.  若提供简历，从简历中补充提取与TA岗位相关的技能/经验，合并到上述字段中，禁止重复\n" +
                "3.  所有字段仅保留核心关键词，禁止冗余描述\n" +
                "\n" +
                "输入Profile文本：" + applicantInfo + "\n" +
                "输入简历文本（可选，无则忽略）：" + (resumeText != null ? resumeText : "无") + "\n" +
                "\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"applicant_key_info\": {\n" +
                "    \"work_available_time\": \"xxx\",\n" +
                "    \"core_skills\": [\"xxx\", \"xxx\"],\n" +
                "    \"ta_experience\": [\"xxx\", \"xxx\"],\n" +
                "    \"additional_advantages\": \"xxx\"\n" +
                "  }\n" +
                "}";
        
        // 调用API
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }
    
    // 第2次调用：岗位信息结构化精简（轻量任务）
    public static String extractJobKeyInfo(String jobsText) throws IOException {
        // 构建prompt
        String prompt = "# 岗位信息结构化精简任务\n" +
                "你现在需要处理TA岗位列表，从给定的最多5个岗位文本中，提取并精简出与申请人匹配相关的核心要求，**仅输出JSON格式，禁止任何额外文本**。\n" +
                "提取规则：\n" +
                "1.  每个岗位提取：岗位ID（job_id，从1开始编号）、岗位名称（job_title）、所属部门（department）、要求工作时间（required_work_time）、核心技能要求（required_skills，列表形式，最多5个关键词）、经验要求（required_experience，精简为1句话，无特殊要求则写\"无\"）\n" +
                "2.  最多处理5个岗位，超过部分忽略\n" +
                "3.  所有字段仅保留核心要求，禁止冗余描述\n" +
                "\n" +
                "输入岗位文本：" + jobsText + "\n" +
                "\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"jobs\": [\n" +
                "    {\n" +
                "      \"job_id\": 1,\n" +
                "      \"job_title\": \"xxx\",\n" +
                "      \"department\": \"xxx\",\n" +
                "      \"required_work_time\": \"xxx\",\n" +
                "      \"required_skills\": [\"xxx\", \"xxx\"],\n" +
                "      \"required_experience\": \"xxx\"\n" +
                "    },\n" +
                "    ...（最多5个）\n" +
                "  ]\n" +
                "}";
        
        // 调用API
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }
    
    // 第3次调用：批量匹配 + 打分 + 排序 + 报告生成（核心任务）
    public static String matchJobsWithScoring(String applicantKeyInfo, String jobsKeyInfo) throws IOException {
        // 构建prompt
        String prompt = "# TA岗位匹配分析任务\n" +
                "你现在需要基于申请人和岗位的精简信息，完成TA岗位匹配分析，**仅输出JSON格式，禁止任何额外文本**。\n" +
                "任务规则：\n" +
                "1.  比对申请人与每个岗位，从三个维度打分（总分100分，权重固定）：\n" +
                "    - 时间匹配（20分）：工作时间完全匹配得20分，部分重叠按比例扣分，无重叠得0分\n" +
                "    - 技能匹配（50分）：核心技能与岗位要求的契合度，按匹配项数占比扣分，完全匹配得50分\n" +
                "    - 经验匹配（30分）：TA经验与岗位要求的相关性，有直接相关经验得30分，弱相关按情况扣分，无相关经验得0分\n" +
                "2.  计算每个岗位的总分，按总分降序排序\n" +
                "3.  取排名前3的岗位（不足3个则全部列出），每个岗位输出：排名、岗位ID、岗位名称、所属部门、总分、三个维度分项得分、精简匹配理由（不超过2句话，说明核心适配点）\n" +
                "4.  最后生成1句整体匹配总结，说明申请人的核心适配优势与推荐方向\n" +
                "\n" +
                "输入申请人信息：" + applicantKeyInfo + "\n" +
                "输入岗位列表：" + jobsKeyInfo + "\n" +
                "\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"matching_results\": [\n" +
                "    {\n" +
                "      \"rank\": 1,\n" +
                "      \"job_id\": 1,\n" +
                "      \"job_title\": \"xxx\",\n" +
                "      \"department\": \"xxx\",\n" +
                "      \"total_score\": 92,\n" +
                "      \"time_match_score\": 20,\n" +
                "      \"skill_match_score\": 47,\n" +
                "      \"experience_match_score\": 25,\n" +
                "      \"reason\": \"xxx\"\n" +
                "    },\n" +
                "    ...（最多3个）\n" +
                "  ],\n" +
                "  \"overall_summary\": \"xxx\"\n" +
                "}";
        
        // 调用API
        String response = callDoubaoDeepThinking(prompt);
        return parseDoubaoResponse(response);
    }
    
    // 工具方法：截断文本
    public static String truncateText(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength);
    }
    
    // 工具方法：构建申请人精简文本
    public static String buildApplicantInfoText(TA ta) {
        StringBuilder info = new StringBuilder();
        info.append("申请人信息：\n");
        info.append("可用工作时间：").append(ta.getAvailableTime()).append("\n");
        info.append("专业技能：");
        if (ta.getSkills() != null && !ta.getSkills().isEmpty()) {
            info.append(String.join(", ", ta.getSkills()));
        } else {
            info.append("无");
        }
        info.append("\n");
        info.append("TA相关经验：");
        if (ta.getExperience() != null && !ta.getExperience().isEmpty()) {
            info.append(ta.getExperience());
        } else {
            info.append("无");
        }
        info.append("\n");
        info.append("奖项/语言技能：");
        List<String> awardsAndLanguages = new ArrayList<>();
        if (ta.getLanguageSkills() != null && !ta.getLanguageSkills().isEmpty()) {
            awardsAndLanguages.add(ta.getLanguageSkills());
        }
        if (ta.getAwards() != null && !ta.getAwards().isEmpty()) {
            awardsAndLanguages.add(ta.getAwards());
        }
        if (awardsAndLanguages.isEmpty()) {
            info.append("无");
        } else {
            info.append(String.join(", ", awardsAndLanguages));
        }
        return info.toString();
    }
    
    // 工具方法：构建岗位精简文本
    public static String buildJobsInfoText(List<Job> jobs) {
        StringBuilder info = new StringBuilder();
        int count = 0;
        for (Job job : jobs) {
            if (count >= 5) break;
            count++;
            info.append("岗位").append(count).append("：\n");
            info.append("名称：").append(job.getTitle()).append(" 部门：").append(job.getDepartment()).append("\n");
            info.append("要求工作时间：").append(job.getWorkTime()).append("\n");
            info.append("岗位描述：").append(truncateText(job.getDescription(), 300)).append("\n");
            info.append("技能要求：");
            if (job.getSkills() != null && !job.getSkills().isEmpty()) {
                info.append(String.join(", ", job.getSkills()));
            } else {
                info.append("无");
            }
            info.append("\n\n");
        }
        return info.toString();
    }
    
    // 工具方法：带重试的API调用
    public static String callWithRetry(String prompt, int maxRetries, int timeoutMs) throws IOException {
        if (apiKey.isEmpty()) {
            throw new IllegalArgumentException("API key is not set");
        }
        
        int retries = 0;
        while (retries < maxRetries) {
            try {
                // 设置超时
                URL url = new URL(API_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Authorization", "Bearer " + apiKey);
                connection.setDoOutput(true);
                connection.setConnectTimeout(timeoutMs);
                connection.setReadTimeout(timeoutMs * 2); // 增加读取超时时间，确保有足够时间获取响应

                // 构建请求体
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("model", currentModel);
                requestBody.addProperty("input", prompt);
                
                JsonObject thinking = new JsonObject();
                thinking.addProperty("type", "enabled");
                requestBody.add("thinking", thinking);
                
                requestBody.addProperty("stream", false);

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
                    return parseDoubaoResponse(response.toString());
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
            } catch (IOException e) {
                retries++;
                if (retries >= maxRetries) {
                    throw e;
                }
                // 等待一段时间后重试
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        throw new IOException("Max retries reached");
    }
    
    // 工具方法：调用指定模型的API
    public static String callModel(String model, String prompt, int maxRetries, int timeoutMs) throws IOException {
        String originalModel = currentModel;
        try {
            currentModel = model;
            return callWithRetry(prompt, maxRetries, timeoutMs);
        } finally {
            currentModel = originalModel;
        }
    }
    

    
    // 完整的AI Job Matching流程（带容错的5步优化版，集成PDF解析）
    public static String optimizedJobMatchingFlow(TA ta, String resumePath) throws IOException {
        try {
            // 1. 智能获取简历内容（自动处理PDF和文本文件）
            System.out.println("=== 准备阶段：加载简历文件 ===");
            String resumeText = getResumeContent(resumePath);
            System.out.println("简历内容长度: " + (resumeText != null ? resumeText.length() : 0));
            
            // 2. 构建整合后的申请人资料（Profile + Resume）
            String combinedApplicantProfile = buildCombinedApplicantProfile(ta, resumeText);
            System.out.println("整合后的申请人信息长度: " + combinedApplicantProfile.length());
            
            // 3. 获取岗位数据
            List<Job> jobs = getAllJobsForMatching();
            if (jobs.size() > 5) {
                jobs = jobs.subList(0, 5);
            }
            
            System.out.println("\n=== 调试信息 - 准备数据 ===");
            System.out.println("申请人信息长度: " + combinedApplicantProfile.length());
            System.out.println("可用岗位数: " + jobs.size());
            for (int i = 0; i < jobs.size(); i++) {
                System.out.println("  岗位" + (i+1) + ": " + jobs.get(i).getTitle() + " (状态: " + jobs.get(i).getStatus() + ")");
            }
            System.out.println("==========================");
            
            // ========== 稳健的5步流程开始 ==========
            
            // 第1步：数据清洗
            System.out.println("\n===== 第1步：数据清洗 =====");
            String cleanedData = safeCleanData(combinedApplicantProfile, resumeText, jobs);
            System.out.println("结果: " + cleanedData);
            
            // 第2步：结构化信息提取
            System.out.println("\n===== 第2步：结构化提取 =====");
            String structuredInfo = safeExtractStructuredInfo(cleanedData, combinedApplicantProfile, resumeText, jobs);
            System.out.println("结果: " + structuredInfo);
            
            // 第3步：直接使用结构化信息匹配（跳过相似度计算，避免token浪费）
            System.out.println("\n===== 第3步：综合匹配分析 =====");
            String matchingResults = safeGenerateMatchingResults(structuredInfo, jobs);
            System.out.println("结果: " + matchingResults);
            
            // 第4步：验证结果有效性
            System.out.println("\n===== 第4步：结果验证 =====");
            String finalResults = safeValidateResults(matchingResults, structuredInfo, jobs);
            System.out.println("最终结果: " + finalResults);
            
            return finalResults;
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // 如果5步流程失败，降级到更简单的3步流程
            System.out.println("\n===== 5步流程失败，使用备用3步流程 =====");
            try {
                String resumeText = getResumeContent(resumePath);
                String combinedApplicantProfile = buildCombinedApplicantProfile(ta, resumeText);
                List<Job> jobs = getAllJobsForMatching();
                if (jobs.size() > 5) jobs = jobs.subList(0, 5);
                
                String applicantKeyInfo = extractApplicantKeyInfo(combinedApplicantProfile, resumeText);
                String jobsInfo = buildJobsInfoText(jobs);
                String jobsKeyInfo = extractJobKeyInfo(jobsInfo);
                String matchingResults = matchJobsWithScoring(applicantKeyInfo, jobsKeyInfo);
                return safeValidateResults(matchingResults, combinedApplicantProfile, jobs);
            } catch (Exception e2) {
                e2.printStackTrace();
                return "{\"error\": \"匹配服务暂时不可用，请稍后重试: " + e.getMessage() + "\"}";
            }
        }
    }
    
    // 第1步：调用Mini模型进行数据清洗（进一步优化）
    public static String cleanData(String applicantProfile, String resumeText, List<Job> jobs) throws IOException {
        // 构建岗位文本 - 进一步精简
        StringBuilder jobsText = new StringBuilder();
        int count = 0;
        for (Job job : jobs) {
            if (count >= 5) break;
            count++;
            jobsText.append("岗位").append(count).append("：\n");
            jobsText.append("名称：").append(job.getTitle()).append("\n");
            jobsText.append("工作时间：").append(job.getWorkTime()).append("\n");
            jobsText.append("技能要求：").append(job.getSkills() != null ? String.join(", ", job.getSkills()) : "无").append("\n");
            // 仅保留岗位描述的前100字符
            jobsText.append("岗位描述：").append(truncateText(job.getDescription() != null ? job.getDescription() : "无", 100)).append("\n\n");
        }
        
        // 构建prompt - 更简洁
        String prompt = "# 数据清洗任务\n" +
                "请对以下TA申请人和岗位数据进行清洗和标准化：\n\n" +
                "## 申请人信息\n" + truncateText(applicantProfile, 500) + "\n\n" +
                "## 简历文本\n" + (resumeText != null ? resumeText : "无") + "\n\n" +
                "## 岗位信息\n" + jobsText.toString() + "\n" +
                "### 任务要求\n" +
                "1. 清洗申请人信息：仅保留工作时间、技能、经验、奖项\n" +
                "2. 清洗简历文本：仅保留与TA岗位相关的内容\n" +
                "3. 清洗岗位信息：仅保留工作时间、技能要求、岗位描述\n" +
                "4. 输出清洗后的精简文本，格式清晰，无冗余\n\n" +
                "请以JSON格式返回清洗结果，包含：cleaned_applicant_info、cleaned_resume、cleaned_jobs\n";
        
        // 调用Mini模型
        return callModel("doubao-seed-2-0-mini-260215", prompt, 2, 30000);
    }
    
    // 第2步：调用Lite模型进行结构化信息提取（进一步优化）
    public static String extractStructuredInfo(String cleanedData) throws IOException {
        // 构建prompt - 更简洁
        String prompt = "# 结构化信息提取任务\n" +
                "请从以下清洗后的TA申请人和岗位数据中提取结构化关键信息：\n\n" +
                truncateText(cleanedData, 1000) + "\n\n" + // 限制输入大小
                "### 任务要求\n" +
                "1. 提取申请人结构化参数：\n" +
                "   - work_availability：工作时间\n" +
                "   - professional_skills：专业技能（列表）\n" +
                "   - experience：经验（列表）\n" +
                "   - awards：奖项\n" +
                "2. 提取岗位结构化参数（最多5个）：\n" +
                "   - job_id：岗位ID（从1开始）\n" +
                "   - job_title：岗位名称\n" +
                "   - required_work_time：要求工作时间\n" +
                "   - required_skills：技能要求（列表）\n" +
                "   - job_description：岗位描述\n" +
                "3. 仅输出JSON格式，禁止任何额外文本\n\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"applicant\": {\n" +
                "    \"work_availability\": \"周一/周三 18:00-22:00\",\n" +
                "    \"professional_skills\": [\"Python\", \"Java\"],\n" +
                "    \"experience\": [\"AI研发TA\"],\n" +
                "    \"awards\": \"校级奖学金\"\n" +
                "  },\n" +
                "  \"jobs\": [\n" +
                "    {\n" +
                "      \"job_id\": 1,\n" +
                "      \"job_title\": \"AI助教\",\n" +
                "      \"required_work_time\": \"周一/周三 19:00-21:00\",\n" +
                "      \"required_skills\": [\"Python\", \"AI开发\"],\n" +
                "      \"job_description\": \"辅助AI课程教学\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        // 调用Lite模型
        return callModel("doubao-seed-2-0-lite-260215", prompt, 2, 30000);
    }
    
    // 第3步：调用Lite模型进行相似度计算（进一步优化）
    public static String calculateSimilarity(String structuredInfo) throws IOException {
        // 构建prompt - 更简洁
        String prompt = "# 相似度计算任务\n" +
                "请对以下结构化的TA申请人和岗位数据进行相似度计算：\n\n" +
                truncateText(structuredInfo, 1000) + "\n\n" + // 限制输入大小
                "### 任务要求\n" +
                "1. 分析申请人与每个岗位的匹配程度\n" +
                "2. 从工作时间、专业技能、经验、奖项四个维度进行评估\n" +
                "3. 计算每个岗位的相似度得分（0~1分）\n" +
                "4. 仅输出JSON格式，包含：similarity_scores\n\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"similarity_scores\": [\n" +
                "    {\n" +
                "      \"job_id\": 1,\n" +
                "      \"score\": 0.92\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        
        // 调用Lite模型
        return callModel("doubao-seed-2-0-lite-260215", prompt, 2, 30000);
    }
    
    // 第4步：调用Pro模型进行综合打分和排名（进一步优化）
    public static String generateMatchingResults(String structuredInfo, String similarityScores) throws IOException {
        // 构建prompt - 更简洁
        String prompt = "# 综合匹配分析任务\n" +
                "请基于以下结构化信息和相似度分数，完成TA岗位匹配分析：\n\n" +
                "## 结构化信息\n" + truncateText(structuredInfo, 800) + "\n\n" + // 限制输入大小
                "## 相似度分数\n" + truncateText(similarityScores, 500) + "\n\n" + // 限制输入大小
                "### 任务要求\n" +
                "1. 计算每个岗位的总分：相似度分 × 100（0~100分）\n" +
                "2. 按总分降序排序，取Top3（不足3个则全部展示）\n" +
                "3. 每个岗位输出：排名、岗位ID、岗位名称、总分、相似度分、精简匹配理由（1句话）\n" +
                "4. 生成1句整体匹配总结\n" +
                "5. 仅输出JSON格式，禁止任何额外文本\n\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"matching_results\": [\n" +
                "    {\n" +
                "      \"rank\": 1,\n" +
                "      \"job_id\": 1,\n" +
                "      \"job_title\": \"AI助教\",\n" +
                "      \"total_score\": 95,\n" +
                "      \"similarity_score\": 0.92,\n" +
                "      \"reason\": \"工作时间高度匹配，技能完全契合\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"overall_summary\": \"你最适配AI助教岗位\"\n" +
                "}";
        
        // 调用Pro模型
        return callModel("doubao-seed-2-0-pro-260215", prompt, 2, 30000);
    }
    
    // 第5步：调用Mini模型进行结果校验和格式优化（进一步优化）
    public static String validateAndOptimizeResults(String matchingResults) throws IOException {
        // 构建prompt - 更简洁
        String prompt = "# 结果校验和优化任务\n" +
                "请对以下TA岗位匹配结果进行校验和格式优化：\n\n" +
                truncateText(matchingResults, 1000) + "\n\n" + // 限制输入大小
                "### 任务要求\n" +
                "1. 检查结果格式是否正确，补全缺失字段\n" +
                "2. 优化语句精简度\n" +
                "3. 确保输出可以直接渲染到前端\n" +
                "4. **严格只输出JSON格式**，**禁止任何额外文本、解释或说明**\n" +
                "5. 输出必须是有效的JSON对象，包含matching_results数组和overall_summary字段\n\n" +
                "输出格式示例：\n" +
                "{\n" +
                "  \"matching_results\": [\n" +
                "    {\n" +
                "      \"rank\": 1,\n" +
                "      \"job_id\": \"J001\",\n" +
                "      \"job_title\": \"AI研发助教\",\n" +
                "      \"total_score\": 92,\n" +
                "      \"similarity_score\": 0.92,\n" +
                "      \"reason\": \"具备AI研发方向TA任职经验，掌握Java技能，工作时间完全匹配岗位要求\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"overall_summary\": \"您与AI研发助教岗位匹配度最高，也可考虑公共课辅导助教相关岗位\"\n" +
                "}\n";
        
        // 调用Mini模型
        return callModel("doubao-seed-2-0-mini-260215", prompt, 2, 30000);
    }
    
    // 获取所有可用于匹配的岗位（包括草稿和待审核状态）
    private static List<Job> getAllJobsForMatching() {
        List<Job> allJobs = DataStorage.getJobs();
        java.time.LocalDate today = java.time.LocalDate.now();
        
        return allJobs.stream()
                .filter(job -> job != null)
                .filter(job -> job.getTitle() != null && !job.getTitle().isEmpty())
                .filter(job -> job.getWorkTime() != null && !job.getWorkTime().equals("null"))
                .filter(job -> job.getDescription() != null && !job.getDescription().equals("null"))
                .filter(job -> job.getSkills() != null && !job.getSkills().isEmpty())
                .collect(Collectors.toList());
    }
    
    // ========== 带容错的安全方法 ==========
    
    // 安全的数据清洗（带验证和备用）
    private static String safeCleanData(String applicantProfile, String resumeText, List<Job> jobs) {
        try {
            String result = cleanData(applicantProfile, resumeText, jobs);
            
            // 验证结果是否有效
            if (result != null && !result.contains("error") && result.length() > 50) {
                return result;
            }
            
            System.out.println("数据清洗结果不理想，使用原始数据代替");
        } catch (Exception e) {
            System.out.println("数据清洗出错: " + e.getMessage());
        }
        
        // 备用方案：直接使用原始数据拼接
        StringBuilder fallback = new StringBuilder();
        fallback.append("## Applicant Information\n").append(applicantProfile).append("\n\n");
        fallback.append("## Resume Text\n").append(resumeText != null ? resumeText : "None").append("\n\n");
        fallback.append("## Job Information\n");
        int count = 0;
        for (Job job : jobs) {
            if (count >= 5) break;
            count++;
            fallback.append("Job ").append(count).append(":\n");
            fallback.append("Title: ").append(job.getTitle()).append("\n");
            fallback.append("Work Time: ").append(job.getWorkTime()).append("\n");
            fallback.append("Required Skills: ").append(job.getSkills() != null ? String.join(", ", job.getSkills()) : "None").append("\n\n");
        }
        return fallback.toString();
    }
    
    // 安全的结构化提取（带验证和备用）
    private static String safeExtractStructuredInfo(String cleanedData, String applicantProfile, String resumeText, List<Job> jobs) {
        try {
            String result = extractStructuredInfo(cleanedData);
            
            // 验证是否包含关键信息
            if (result != null && !result.contains("error") && 
                (result.contains("applicant") || result.contains("jobs"))) {
                return result;
            }
            
            System.out.println("结构化提取结果不理想，使用备用方案");
        } catch (Exception e) {
            System.out.println("结构化提取出错: " + e.getMessage());
        }
        
        // 备用方案：手动构建基本的结构化JSON
        return buildFallbackStructuredInfo(applicantProfile, resumeText, jobs);
    }
    
    // 手动构建备用结构化信息
    private static String buildFallbackStructuredInfo(String applicantProfile, String resumeText, List<Job> jobs) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"applicant\": {\n");
        json.append("    \"work_availability\": \"").append(extractValue(applicantProfile, "可用工作时间", "")).append("\",\n");
        json.append("    \"professional_skills\": [\"").append(extractSkills(applicantProfile)).append("\"],\n");
        json.append("    \"experience\": [\"").append(extractValue(applicantProfile, "TA相关经验", "")).append("\"],\n");
        json.append("    \"awards\": \"").append(extractValue(applicantProfile, "奖项/语言技能", "")).append("\"\n");
        json.append("  },\n");
        json.append("  \"jobs\": [\n");
        
        for (int i = 0; i < jobs.size() && i < 5; i++) {
            Job job = jobs.get(i);
            if (i > 0) json.append(",\n");
            json.append("    {\n");
            json.append("      \"job_id\": ").append(i+1).append(",\n");
            json.append("      \"job_title\": \"").append(job.getTitle()).append("\",\n");
            json.append("      \"required_work_time\": \"").append(job.getWorkTime()).append("\",\n");
            json.append("      \"required_skills\": [\"").append(job.getSkills() != null ? String.join("\", \"", job.getSkills()) : "").append("\"],\n");
            json.append("      \"job_description\": \"").append(truncateText(job.getDescription() != null ? job.getDescription() : "", 100)).append("\"\n");
            json.append("    }");
        }
        
        json.append("\n  ]\n");
        json.append("}");
        return json.toString();
    }
    
    // 辅助方法：从文本中提取值
    private static String extractValue(String text, String keyword, String defaultValue) {
        int idx = text.indexOf(keyword);
        if (idx >= 0) {
            int start = idx + keyword.length();
            int end = text.indexOf("\n", start);
            if (end > start) {
                String value = text.substring(start, end).trim();
                if (value.startsWith(":")) value = value.substring(1).trim();
                return value.replace("\"", "'");
            }
        }
        return defaultValue;
    }
    
    // 辅助方法：提取技能
    private static String extractSkills(String text) {
        int idx = text.indexOf("专业技能");
        if (idx >= 0) {
            int start = idx + "专业技能".length();
            int end = text.indexOf("\n", start);
            if (end > start) {
                String skills = text.substring(start, end).trim();
                if (skills.startsWith(":")) skills = skills.substring(1).trim();
                return skills.replace("\"", "'").replace(",", "\", \"");
            }
        }
        return "";
    }
    
    // 安全的匹配结果生成（优化prompt，控制token）
    private static String safeGenerateMatchingResults(String structuredInfo, List<Job> jobs) throws IOException {
        try {
            // 构建岗位信息文本（确保岗位信息不被截断）
            StringBuilder jobsText = new StringBuilder();
            jobsText.append("## Job Information\n");
            int count = 0;
            for (Job job : jobs) {
                if (count >= 5) break;
                count++;
                jobsText.append("Job " + count + ":\n");
                jobsText.append("Title: " + job.getTitle() + "\n");
                jobsText.append("Work Time: " + job.getWorkTime() + "\n");
                jobsText.append("Required Skills: " + (job.getSkills() != null ? String.join(", ", job.getSkills()) : "None") + "\n");
                jobsText.append("Description: " + (job.getDescription() != null ? job.getDescription() : "None") + "\n\n");
            }
            
            // 优化的prompt：更简洁，直接要求输出结果，避免中间步骤
            String prompt = "# TA Job Matching Analysis\n" +
                    "Analyze the following applicant and job data to generate matching results:\n\n" +
                    "## Structured Applicant Data\n" +
                    truncateText(structuredInfo, 1000) + "\n\n" +
                    jobsText.toString() + "\n" +
                    "### Requirements\n" +
                    "1. Calculate matching scores and rank jobs\n" +
                    "2. Output JSON format with matching_results array and overall_summary\n" +
                    "3. Each result must include: rank, job_id, job_title, total_score, reason\n\n" +
                    "Output format:\n" +
                    "{\n" +
                    "  \"matching_results\": [\n" +
                    "    {\"rank\": 1, \"job_id\": 1, \"job_title\": \"...\", \"total_score\": 90, \"reason\": \"...\"}\n" +
                    "  ],\n" +
                    "  \"overall_summary\": \"...\"\n" +
                    "}";
            
            return callModel("doubao-seed-2-0-pro-260215", prompt, 2, 40000);
        } catch (Exception e) {
            System.out.println("匹配结果生成出错: " + e.getMessage());
            throw e;
        }
    }
    
    // 安全的结果验证
    private static String safeValidateResults(String results, String structuredInfo, List<Job> jobs) throws IOException {
        try {
            // 先检查当前结果是否有效
            if (isValidMatchingResults(results)) {
                System.out.println("结果验证通过，直接返回");
                return results;
            }
            
            System.out.println("结果验证失败，尝试修复...");
            
            // 如果当前结果无效，尝试直接用结构化信息重新生成
            return safeGenerateMatchingResults(structuredInfo, jobs);
        } catch (Exception e) {
            System.out.println("结果验证出错: " + e.getMessage());
            throw e;
        }
    }
    
    // 验证匹配结果是否有效
    private static boolean isValidMatchingResults(String results) {
        if (results == null || results.contains("error")) return false;
        
        try {
            JsonObject json = new Gson().fromJson(results, JsonObject.class);
            if (json.has("matching_results")) {
                JsonArray arr = json.getAsJsonArray("matching_results");
                return arr.size() > 0; // 至少有一个匹配结果
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
    
    // ========== 简历关键信息提取功能 ==========
    
    /**
     * 使用AI从简历文本中提取关键信息（技能、经验、证书等）
     * @param rawResumeText 原始简历文本
     * @param taProfileText TA个人资料文本（用于上下文补充）
     * @return 结构化的JSON，包含提取的关键信息
     */
    public static String extractKeyInfoFromResume(String rawResumeText, String taProfileText) throws IOException {
        if (rawResumeText == null || rawResumeText.trim().isEmpty() || rawResumeText.equals("None")) {
            return "{\"resume_analysis\": \"No resume provided\", \"extracted_info\": {}}";
        }
        
        String prompt = "# Resume Key Information Extraction\n" +
                "Extract and summarize key information from this TA applicant's resume.\n\n" +
                "## TA Profile (Context)\n" +
                (taProfileText != null ? taProfileText : "Not provided") + "\n\n" +
                "## Raw Resume Text\n" +
                truncateText(rawResumeText, 3000) + "\n\n" +
                "## Extraction Requirements\n" +
                "Please extract the following information and output ONLY JSON format:\n" +
                "1. Key skills (technical, soft skills, languages)\n" +
                "2. Work/TA experience summary\n" +
                "3. Education details\n" +
                "4. Certifications/awards\n" +
                "5. Notable achievements\n" +
                "6. Key strengths\n\n" +
                "Output JSON with these fields: extracted_skills, experience_summary, education, certifications, achievements, key_strengths\n" +
                "Keep each field concise (max 200 characters each).";
        
        return callModel("doubao-seed-2-0-pro-260215", prompt, 1, 30000);
    }
    
    /**
     * 整合TA个人资料和简历提取的信息
     * @param ta TA对象
     * @param resumeText 简历文本（从PDF或文件提取）
     * @return 整合后的完整申请人信息
     */
    public static String buildCombinedApplicantProfile(TA ta, String resumeText) {
        StringBuilder profile = new StringBuilder();
        
        // 首先添加TA基本信息
        profile.append("## TA Profile Information\n");
        profile.append("Name: ").append(ta.getName()).append("\n");
        profile.append("Department: ").append(ta.getDepartment()).append("\n");
        profile.append("Grade: ").append(ta.getGrade()).append("\n");
        profile.append("Available Time: ").append(ta.getAvailableTime() != null ? ta.getAvailableTime() : "Not specified").append("\n");
        
        if (ta.getSkills() != null && !ta.getSkills().isEmpty()) {
            profile.append("Skills: ").append(String.join(", ", ta.getSkills())).append("\n");
        }
        
        if (ta.getExperience() != null && !ta.getExperience().trim().isEmpty()) {
            profile.append("Experience: ").append(ta.getExperience()).append("\n");
        }
        
        if (ta.getLanguageSkills() != null && !ta.getLanguageSkills().trim().isEmpty()) {
            profile.append("Language Skills: ").append(ta.getLanguageSkills()).append("\n");
        }
        
        if (ta.getOtherSkills() != null && !ta.getOtherSkills().trim().isEmpty()) {
            profile.append("Other Skills: ").append(ta.getOtherSkills()).append("\n");
        }
        
        if (ta.getAwards() != null && !ta.getAwards().trim().isEmpty()) {
            profile.append("Awards: ").append(ta.getAwards()).append("\n");
        }
        
        // 然后添加简历信息（如果有）
        if (resumeText != null && !resumeText.trim().isEmpty() && !resumeText.equals("None")) {
            profile.append("\n## Resume Information (from PDF/file)\n");
            profile.append(truncateText(resumeText, 2000)).append("\n");
        } else {
            profile.append("\n## Resume Information\n");
            profile.append("No resume uploaded\n");
        }
        
        return profile.toString();
    }
    
    /**
     * 智能获取简历内容（自动处理PDF和文本文件）
     * @param resumePath 简历文件路径
     * @return 提取的简历文本
     */
    public static String getResumeContent(String resumePath) {
        if (resumePath == null || resumePath.trim().isEmpty()) {
            return "None";
        }
        
        // 使用PDF解析服务
        return PDFResumeParserService.extractContentFromFile(resumePath, 3000);
    }
}  

