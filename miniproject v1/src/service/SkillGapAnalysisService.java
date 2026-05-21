package service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class SkillGapAnalysisService {
    
    // ==================== 配置模块 ====================
    
    private static final String API_URL = "https://ark.cn-beijing.volces.com/api/v3/responses";
    
    // 配置参数
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;  // 10秒连接超时
    private static final int DEFAULT_READ_TIMEOUT = 30000;     // 30秒读取超时
    private static final int DEFAULT_MAX_RETRIES = 3;          // 最大重试次数
    
    // 默认API配置
    private static final String DEFAULT_API_KEY = "d790f9c3-a851-434d-a229-04a99c131099";
    private static final String DEFAULT_MODEL = "doubao-seed-2-0-mini-260215";
    
    // 设置API配置（调用AIService，保持配置一致）
    public static void setApiConfig(String key, String model) {
        AIService.setApiConfig(key, model);
    }
    
    // 获取API密钥（从AIService获取，保持配置一致）
    public static String getApiKey() {
        String key = AIService.getApiKey();
        // 如果AIService没有配置API Key，使用默认值
        if (key.isEmpty()) {
            return DEFAULT_API_KEY;
        }
        return key;
    }
    
    // 获取当前模型（从AIService获取，保持配置一致）
    public static String getCurrentModel() {
        String model = AIService.getCurrentModel();
        // 如果AIService没有配置模型，使用默认值
        if (model.isEmpty()) {
            return DEFAULT_MODEL;
        }
        return model;
    }
    
    // 熟练度等级枚举
    public enum ProficiencyLevel {
        BEGINNER("入门", 1),
        INTERMEDIATE("中级", 2),
        ADVANCED("高级", 3),
        EXPERT("专家", 4);
        
        private final String description;
        private final int level;
        
        ProficiencyLevel(String description, int level) {
            this.description = description;
            this.level = level;
        }
        
        public String getDescription() { return description; }
        public int getLevel() { return level; }
        
        public static ProficiencyLevel fromString(String value) {
            if (value == null) return BEGINNER;
            String lower = value.toLowerCase().trim();
            if (lower.contains("专家") || lower.contains("精通") || lower.contains("expert")) return EXPERT;
            if (lower.contains("高级") || lower.contains("熟练") || lower.contains("advanced")) return ADVANCED;
            if (lower.contains("中级") || lower.contains("intermediate")) return INTERMEDIATE;
            return BEGINNER;
        }
    }
    
    // 差距优先级枚举
    public enum GapPriority {
        HIGH("高"),
        MEDIUM("中"),
        LOW("低");
        
        private final String description;
        
        GapPriority(String description) {
            this.description = description;
        }
        
        public String getDescription() { return description; }
    }
    
    // ==================== 数据模型 ====================
    
    /**
     * 技能信息
     */
    public static class SkillInfo {
        private String skillName;
        private ProficiencyLevel proficiency;
        
        public SkillInfo() {}
        
        public SkillInfo(String skillName, ProficiencyLevel proficiency) {
            this.skillName = skillName;
            this.proficiency = proficiency;
        }
        
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public ProficiencyLevel getProficiency() { return proficiency; }
        public void setProficiency(ProficiencyLevel proficiency) { this.proficiency = proficiency; }
    }
    
    /**
     * 个人Profile信息
     */
    public static class ProfileInfo {
        private String profileUrl;
        private List<SkillInfo> currentSkills;
        private List<String> workExperience;
        private List<String> educationBackground;
        
        public ProfileInfo() {
            currentSkills = new ArrayList<>();
            workExperience = new ArrayList<>();
            educationBackground = new ArrayList<>();
        }
        
        public String getProfileUrl() { return profileUrl; }
        public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
        public List<SkillInfo> getCurrentSkills() { return currentSkills; }
        public void setCurrentSkills(List<SkillInfo> currentSkills) { this.currentSkills = currentSkills; }
        public List<String> getWorkExperience() { return workExperience; }
        public void setWorkExperience(List<String> workExperience) { this.workExperience = workExperience; }
        public List<String> getEducationBackground() { return educationBackground; }
        public void setEducationBackground(List<String> educationBackground) { this.educationBackground = educationBackground; }
    }
    
    /**
     * 目标岗位技能要求
     */
    public static class JobSkillRequirement {
        private String skillName;
        private ProficiencyLevel requiredProficiency;
        private boolean isMandatory;  // 是否为必需技能
        
        public JobSkillRequirement() {}
        
        public JobSkillRequirement(String skillName, ProficiencyLevel requiredProficiency, boolean isMandatory) {
            this.skillName = skillName;
            this.requiredProficiency = requiredProficiency;
            this.isMandatory = isMandatory;
        }
        
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public ProficiencyLevel getRequiredProficiency() { return requiredProficiency; }
        public void setRequiredProficiency(ProficiencyLevel requiredProficiency) { this.requiredProficiency = requiredProficiency; }
        public boolean isMandatory() { return isMandatory; }
        public void setMandatory(boolean mandatory) { isMandatory = mandatory; }
    }
    
    /**
     * 技能差距详情
     */
    public static class SkillGapDetail {
        private String skillName;
        private ProficiencyLevel currentProficiency;
        private ProficiencyLevel requiredProficiency;
        private GapPriority priority;
        private List<String> improvementSuggestions;
        
        public SkillGapDetail() {
            improvementSuggestions = new ArrayList<>();
        }
        
        public String getSkillName() { return skillName; }
        public void setSkillName(String skillName) { this.skillName = skillName; }
        public ProficiencyLevel getCurrentProficiency() { return currentProficiency; }
        public void setCurrentProficiency(ProficiencyLevel currentProficiency) { this.currentProficiency = currentProficiency; }
        public ProficiencyLevel getRequiredProficiency() { return requiredProficiency; }
        public void setRequiredProficiency(ProficiencyLevel requiredProficiency) { this.requiredProficiency = requiredProficiency; }
        public GapPriority getPriority() { return priority; }
        public void setPriority(GapPriority priority) { this.priority = priority; }
        public List<String> getImprovementSuggestions() { return improvementSuggestions; }
        public void setImprovementSuggestions(List<String> improvementSuggestions) { this.improvementSuggestions = improvementSuggestions; }
    }
    
    /**
     * 技能差距分析结果
     */
    public static class SkillGapAnalysisResult {
        private boolean success;
        private String errorMessage;
        private ProfileInfo profileInfo;
        private List<JobSkillRequirement> jobRequirements;
        private List<SkillGapDetail> missingSkills;      // 缺失技能
        private List<SkillGapDetail> insufficientSkills; // 熟练度不足技能
        private List<String> matchedSkills;              // 匹配技能
        private String summary;
        
        public SkillGapAnalysisResult() {
            missingSkills = new ArrayList<>();
            insufficientSkills = new ArrayList<>();
            matchedSkills = new ArrayList<>();
        }
        
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public ProfileInfo getProfileInfo() { return profileInfo; }
        public void setProfileInfo(ProfileInfo profileInfo) { this.profileInfo = profileInfo; }
        public List<JobSkillRequirement> getJobRequirements() { return jobRequirements; }
        public void setJobRequirements(List<JobSkillRequirement> jobRequirements) { this.jobRequirements = jobRequirements; }
        public List<SkillGapDetail> getMissingSkills() { return missingSkills; }
        public void setMissingSkills(List<SkillGapDetail> missingSkills) { this.missingSkills = missingSkills; }
        public List<SkillGapDetail> getInsufficientSkills() { return insufficientSkills; }
        public void setInsufficientSkills(List<SkillGapDetail> insufficientSkills) { this.insufficientSkills = insufficientSkills; }
        public List<String> getMatchedSkills() { return matchedSkills; }
        public void setMatchedSkills(List<String> matchedSkills) { this.matchedSkills = matchedSkills; }
        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }
        
        /**
         * 转换为JSON字符串
         */
        public String toJson() {
            JsonObject result = new JsonObject();
            result.addProperty("success", success);
            
            if (!success) {
                result.addProperty("errorMessage", errorMessage);
                return result.toString();
            }
            
            // Profile信息
            JsonObject profileJson = new JsonObject();
            profileJson.addProperty("profileUrl", profileInfo.getProfileUrl());
            
            JsonArray skillsArray = new JsonArray();
            for (SkillInfo skill : profileInfo.getCurrentSkills()) {
                JsonObject skillObj = new JsonObject();
                skillObj.addProperty("skillName", skill.getSkillName());
                skillObj.addProperty("proficiency", skill.getProficiency().getDescription());
                skillsArray.add(skillObj);
            }
            profileJson.add("currentSkills", skillsArray);
            
            JsonArray experienceArray = new JsonArray();
            for (String exp : profileInfo.getWorkExperience()) {
                experienceArray.add(exp);
            }
            profileJson.add("workExperience", experienceArray);
            
            JsonArray educationArray = new JsonArray();
            for (String edu : profileInfo.getEducationBackground()) {
                educationArray.add(edu);
            }
            profileJson.add("educationBackground", educationArray);
            result.add("profileInfo", profileJson);
            
            // 岗位要求
            JsonArray requirementsArray = new JsonArray();
            if (jobRequirements != null) {
                for (JobSkillRequirement req : jobRequirements) {
                    JsonObject reqObj = new JsonObject();
                    reqObj.addProperty("skillName", req.getSkillName());
                    reqObj.addProperty("requiredProficiency", req.getRequiredProficiency().getDescription());
                    reqObj.addProperty("isMandatory", req.isMandatory());
                    requirementsArray.add(reqObj);
                }
            }
            result.add("jobRequirements", requirementsArray);
            
            // 缺失技能
            JsonArray missingArray = new JsonArray();
            for (SkillGapDetail gap : missingSkills) {
                JsonObject gapObj = convertGapDetailToJson(gap);
                missingArray.add(gapObj);
            }
            result.add("missingSkills", missingArray);
            
            // 熟练度不足技能
            JsonArray insufficientArray = new JsonArray();
            for (SkillGapDetail gap : insufficientSkills) {
                JsonObject gapObj = convertGapDetailToJson(gap);
                insufficientArray.add(gapObj);
            }
            result.add("insufficientSkills", insufficientArray);
            
            // 匹配技能
            JsonArray matchedArray = new JsonArray();
            for (String skill : matchedSkills) {
                matchedArray.add(skill);
            }
            result.add("matchedSkills", matchedArray);
            
            // 总结
            result.addProperty("summary", summary);
            
            return result.toString();
        }
        
        private JsonObject convertGapDetailToJson(SkillGapDetail gap) {
            JsonObject gapObj = new JsonObject();
            gapObj.addProperty("skillName", gap.getSkillName());
            gapObj.addProperty("currentProficiency", gap.getCurrentProficiency() != null ? gap.getCurrentProficiency().getDescription() : "无");
            gapObj.addProperty("requiredProficiency", gap.getRequiredProficiency().getDescription());
            gapObj.addProperty("priority", gap.getPriority().getDescription());
            
            JsonArray suggestionsArray = new JsonArray();
            for (String suggestion : gap.getImprovementSuggestions()) {
                suggestionsArray.add(suggestion);
            }
            gapObj.add("improvementSuggestions", suggestionsArray);
            return gapObj;
        }
    }
    
    // ==================== 豆包API调用模块 ====================
    
    /**
     * 调用豆包API提取Profile链接内容
     * @param profileUrl Profile链接
     * @return 提取的Profile结构化内容（JSON格式）
     * @throws IOException API调用异常
     */
    public static String extractProfileContent(String profileUrl) throws IOException {
        if (getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API key is not set");
        }
        
        String prompt = "# TA Profile内容提取任务\n" +
                "请从以下Profile链接中提取TA申请人的核心信息，**仅输出JSON格式，禁止任何额外文本**。\n\n" +
                "Profile链接：" + profileUrl + "\n\n" +
                "提取规则：\n" +
                "1. 提取个人当前掌握的技能清单及熟练度（熟练度分为：入门、中级、高级、专家）\n" +
                "2. 提取工作经历（最多3条核心经历）\n" +
                "3. 提取教育背景（学历、专业）\n" +
                "4. 如果链接内包含岗位适配的目标技能要求，也请提取\n\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"currentSkills\": [\n" +
                "    {\"skillName\": \"Java\", \"proficiency\": \"高级\"},\n" +
                "    {\"skillName\": \"Python\", \"proficiency\": \"中级\"}\n" +
                "  ],\n" +
                "  \"workExperience\": [\"XX公司TA经验\", \"YY项目经验\"],\n" +
                "  \"educationBackground\": [\"本科，计算机科学\", \"硕士，软件工程\"],\n" +
                "  \"targetSkills\": []\n" +
                "}\n";
        
        return callApiWithRetry(prompt, DEFAULT_MAX_RETRIES, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    
    /**
     * 调用豆包API进行技能差距分析
     * @param profileJson 提取的Profile内容（JSON格式）
     * @param jobRequirementsJson 目标岗位技能要求（JSON格式）
     * @return 技能差距分析结果（JSON格式）
     * @throws IOException API调用异常
     */
    public static String analyzeSkillGap(String profileJson, String jobRequirementsJson) throws IOException {
        if (getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API key is not set");
        }
        
        String prompt = "# TA技能差距分析任务\n" +
                "请基于以下TA申请人的Profile信息和目标岗位技能要求，完成技能差距分析，**仅输出JSON格式，禁止任何额外文本**。\n\n" +
                "## 申请人Profile信息\n" + profileJson + "\n\n" +
                "## 目标岗位技能要求\n" + jobRequirementsJson + "\n\n" +
                "分析规则：\n" +
                "1. 识别缺失技能：申请人未掌握但岗位要求的技能\n" +
                "2. 识别熟练度不足技能：申请人已掌握但熟练度低于要求的技能\n" +
                "3. 标注差距优先级（高/中/低）：必需技能缺失为高优先级，熟练度差距大且为必需技能为中优先级，其他为低优先级\n" +
                "4. 为每个差距技能生成1-2条具体提升建议（学习路径、认证考试、项目实践等）\n" +
                "5. 识别完全匹配的技能\n" +
                "6. 生成综合分析总结\n\n" +
                "输出格式（严格遵循）：\n" +
                "{\n" +
                "  \"missingSkills\": [\n" +
                "    {\n" +
                "      \"skillName\": \"深度学习\",\n" +
                "      \"currentProficiency\": \"无\",\n" +
                "      \"requiredProficiency\": \"高级\",\n" +
                "      \"priority\": \"高\",\n" +
                "      \"improvementSuggestions\": [\"学习Coursera深度学习专项课程\", \"参与深度学习项目实践\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"insufficientSkills\": [\n" +
                "    {\n" +
                "      \"skillName\": \"Python\",\n" +
                "      \"currentProficiency\": \"中级\",\n" +
                "      \"requiredProficiency\": \"高级\",\n" +
                "      \"priority\": \"中\",\n" +
                "      \"improvementSuggestions\": [\"完成LeetCode Python算法题\", \"参与开源Python项目\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"matchedSkills\": [\"Java\", \"SQL\"],\n" +
                "  \"summary\": \"申请人整体技能匹配度良好，但缺少深度学习技能，建议优先补充。\"\n" +
                "}\n";
        
        return callApiWithRetry(prompt, DEFAULT_MAX_RETRIES, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    
    /**
     * 调用豆包API进行技能差距分析（接受纯文本格式输入）
     * @param profileText 申请人Profile文本（包含个人信息、技能、经验、简历内容等）
     * @param jobRequirementsText 目标岗位要求文本
     * @return 技能差距分析结果（JSON格式）
     * @throws IOException API调用异常
     */
    public static String analyzeSkillGapFromProfileAndJob(String profileText, String jobRequirementsText) throws IOException {
        System.out.println("\n========================================");
        System.out.println("[SkillGapAnalysis] 技能差距分析开始");
        System.out.println("========================================");
        
        if (getApiKey().isEmpty()) {
            throw new IllegalArgumentException("API key is not set");
        }
        
        System.out.println("\n[阶段1] Profile信息:");
        System.out.println("-----------------------------------");
        System.out.println(profileText.length() > 500 ? profileText.substring(0, 500) + "..." : profileText);
        System.out.println("-----------------------------------\n");
        
        System.out.println("[阶段2] 岗位要求信息:");
        System.out.println("-----------------------------------");
        System.out.println(jobRequirementsText.length() > 500 ? jobRequirementsText.substring(0, 500) + "..." : jobRequirementsText);
        System.out.println("-----------------------------------\n");
        
        String prompt = "# TA Skill Gap Analysis Task\n" +
                "Perform a comprehensive skill gap analysis between TA applicant and job requirements. Output ONLY valid JSON format, no extra text.\n\n" +
                "## Step 1: Data Cleaning\n" +
                "- Extract work time, skills, experience, awards from applicant info\n" +
                "- Keep only TA-relevant content from resume\n" +
                "- Keep work time, skill requirements, job description from job info\n\n" +
                "## Step 2: Structured Information Extraction\n" +
                "- Extract skills with proficiency levels from applicant\n" +
                "- Extract required skills with proficiency levels from job\n\n" +
                "## Step 3: Comprehensive Matching Analysis (Total 100 points)\n" +
                "- Skill/Experience Matching: 60 points\n" +
                "- Other factors (certificates, awards): 40 points\n\n" +
                "## Step 4: Result Validation\n" +
                "- Verify all extracted skills are valid\n" +
                "- Ensure proper priority assignment\n\n" +
                "## Applicant Profile\n" + profileText + "\n\n" +
                "## Target Job Requirements\n" + jobRequirementsText + "\n\n" +
                "## Output Format (strictly follow):\n" +
                "{\n" +
                "  \"missingSkills\": [\n" +
                "    {\n" +
                "      \"skillName\": \"Python\",\n" +
                "      \"currentProficiency\": \"None\",\n" +
                "      \"requiredProficiency\": \"Intermediate\",\n" +
                "      \"priority\": \"High\",\n" +
                "      \"improvementSuggestions\": [\"Learn Python basics online\", \"Practice with small projects\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"insufficientSkills\": [\n" +
                "    {\n" +
                "      \"skillName\": \"Java\",\n" +
                "      \"currentProficiency\": \"Beginner\",\n" +
                "      \"requiredProficiency\": \"Advanced\",\n" +
                "      \"priority\": \"Medium\",\n" +
                "      \"improvementSuggestions\": [\"Complete advanced Java courses\", \"Build Java applications\"]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"matchedSkills\": [\"SQL\", \"Communication\"],\n" +
                "  \"matchScore\": 75,\n" +
                "  \"summary\": \"The applicant meets 75% of the job requirements. Major gaps in Python skills need attention.\"\n" +
                "}\n";
        
        return callApiWithRetry(prompt, DEFAULT_MAX_RETRIES, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }
    
    /**
     * 带重试机制的API调用
     */
    private static String callApiWithRetry(String prompt, int maxRetries, int connectTimeout, int readTimeout) throws IOException {
        int retries = 0;
        IOException lastException = null;
        
        while (retries < maxRetries) {
            try {
                return callApi(prompt, connectTimeout, readTimeout);
            } catch (IOException e) {
                lastException = e;
                retries++;
                System.out.println("API调用失败，第" + retries + "次重试...");
                try {
                    Thread.sleep(1000 * retries);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("请求被中断", ie);
                }
            }
        }
        
        throw new IOException("API调用失败，已重试" + maxRetries + "次", lastException);
    }
    
    /**
     * 执行API调用
     */
    private static String callApi(String prompt, int connectTimeout, int readTimeout) throws IOException {
        String apiKey = getApiKey();
        String model = getCurrentModel();
        
        System.out.println("\n========== [SkillGapAnalysis] API调用开始 ==========");
        System.out.println("[1/5] 使用模型: " + model);
        System.out.println("[2/5] API Key: " + (apiKey.isEmpty() ? "未配置" : "已配置"));
        System.out.println("[3/5] 请求URL: " + API_URL);
        
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        connection.setDoOutput(true);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", model);
        requestBody.addProperty("input", prompt);
        
        JsonObject thinking = new JsonObject();
        thinking.addProperty("type", "enabled");
        requestBody.add("thinking", thinking);
        
        requestBody.addProperty("stream", false);
        
        System.out.println("[4/5] 发送请求中...");
        
        try (OutputStream os = connection.getOutputStream()) {
            byte[] inputBytes = requestBody.toString().getBytes("utf-8");
            os.write(inputBytes, 0, inputBytes.length);
        }
        
        int responseCode = connection.getResponseCode();
        StringBuilder response = new StringBuilder();
        
        if (responseCode == HttpURLConnection.HTTP_OK) {
            System.out.println("[5/5] 收到响应，状态码: " + responseCode + " (成功)");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                }
            }
        } else {
            System.out.println("[!] 收到响应，状态码: " + responseCode + " (失败)");
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine);
                }
            }
            throw new IOException("API请求失败，状态码: " + responseCode + "，响应: " + response.toString());
        }
        
        String rawResponse = response.toString();
        System.out.println("[API调用] 原始响应长度: " + rawResponse.length() + " 字符");
        System.out.println("========== [SkillGapAnalysis] API调用结束 ==========\n");
        
        return parseApiResponse(rawResponse);
    }
    
    /**
     * 解析API响应，提取JSON内容
     */
    private static String parseApiResponse(String response) {
        System.out.println("[解析] 开始解析API响应...");
        try {
            JsonObject responseJson = JsonParser.parseString(response).getAsJsonObject();
            
            if (responseJson.has("error")) {
                String errorMsg = responseJson.getAsJsonObject("error").get("message").getAsString();
                System.out.println("[解析] 发现错误: " + errorMsg);
                throw new RuntimeException("API错误: " + errorMsg);
            }
            
            if (responseJson.has("output")) {
                System.out.println("[解析] 从 'output' 字段提取内容...");
                JsonArray outputArray = responseJson.getAsJsonArray("output");
                for (int i = 0; i < outputArray.size(); i++) {
                    JsonObject outputItem = outputArray.get(i).getAsJsonObject();
                    if (outputItem.has("content")) {
                        try {
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
                        } catch (Exception e) {
                            if (outputItem.get("content").isJsonPrimitive()) {
                                return outputItem.get("content").getAsString();
                            }
                        }
                    }
                }
            }
            
            if (responseJson.has("messages")) {
                JsonArray messagesArray = responseJson.getAsJsonArray("messages");
                for (int i = 0; i < messagesArray.size(); i++) {
                    JsonObject messageItem = messagesArray.get(i).getAsJsonObject();
                    if ("assistant".equals(messageItem.get("role").getAsString()) && messageItem.has("content")) {
                        return messageItem.get("content").getAsString();
                    }
                }
            }
            
            if (responseJson.has("choices")) {
                JsonArray choicesArray = responseJson.getAsJsonArray("choices");
                for (int i = 0; i < choicesArray.size(); i++) {
                    JsonObject choiceItem = choicesArray.get(i).getAsJsonObject();
                    if (choiceItem.has("message")) {
                        JsonObject message = choiceItem.getAsJsonObject("message");
                        if (message.has("content")) {
                            return message.get("content").getAsString();
                        }
                    }
                }
            }
            
            return extractJsonFromString(response);
            
        } catch (Exception e) {
            return extractJsonFromString(response);
        }
    }
    
    /**
     * 从字符串中提取JSON
     */
    private static String extractJsonFromString(String text) {
        int firstBrace = text.indexOf('{');
        int lastBrace = text.lastIndexOf('}');
        
        if (firstBrace != -1 && lastBrace != -1 && lastBrace > firstBrace) {
            String potentialJson = text.substring(firstBrace, lastBrace + 1);
            try {
                JsonParser.parseString(potentialJson);
                return potentialJson;
            } catch (Exception e) {
                String cleanedJson = potentialJson.replace("\\\"", "\"").replace("\\n", "\n").replace("\\t", "\t");
                try {
                    JsonParser.parseString(cleanedJson);
                    return cleanedJson;
                } catch (Exception e2) {
                    return text;
                }
            }
        }
        return text;
    }
    
    // ==================== 技能差距分析模块 ====================
    
    /**
     * 解析Profile JSON为ProfileInfo对象
     */
    public static ProfileInfo parseProfileJson(String profileJson) {
        ProfileInfo profile = new ProfileInfo();
        
        try {
            JsonObject json = JsonParser.parseString(profileJson).getAsJsonObject();
            
            if (json.has("currentSkills")) {
                JsonArray skillsArray = json.getAsJsonArray("currentSkills");
                for (int i = 0; i < skillsArray.size(); i++) {
                    JsonObject skillObj = skillsArray.get(i).getAsJsonObject();
                    String skillName = skillObj.get("skillName").getAsString();
                    String proficiencyStr = skillObj.has("proficiency") ? skillObj.get("proficiency").getAsString() : "入门";
                    profile.getCurrentSkills().add(new SkillInfo(skillName, ProficiencyLevel.fromString(proficiencyStr)));
                }
            }
            
            if (json.has("workExperience")) {
                JsonArray expArray = json.getAsJsonArray("workExperience");
                for (int i = 0; i < expArray.size(); i++) {
                    profile.getWorkExperience().add(expArray.get(i).getAsString());
                }
            }
            
            if (json.has("educationBackground")) {
                JsonArray eduArray = json.getAsJsonArray("educationBackground");
                for (int i = 0; i < eduArray.size(); i++) {
                    profile.getEducationBackground().add(eduArray.get(i).getAsString());
                }
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException("Profile JSON解析失败: " + e.getMessage(), e);
        }
        
        return profile;
    }
    
    /**
     * 解析岗位要求JSON为JobSkillRequirement列表
     */
    public static List<JobSkillRequirement> parseJobRequirementsJson(String jobRequirementsJson) {
        List<JobSkillRequirement> requirements = new ArrayList<>();
        
        try {
            JsonObject json = JsonParser.parseString(jobRequirementsJson).getAsJsonObject();
            
            if (json.has("requirements")) {
                JsonArray reqArray = json.getAsJsonArray("requirements");
                for (int i = 0; i < reqArray.size(); i++) {
                    JsonObject reqObj = reqArray.get(i).getAsJsonObject();
                    String skillName = reqObj.get("skillName").getAsString();
                    String proficiencyStr = reqObj.has("requiredProficiency") ? reqObj.get("requiredProficiency").getAsString() : "入门";
                    boolean isMandatory = reqObj.has("isMandatory") ? reqObj.get("isMandatory").getAsBoolean() : true;
                    requirements.add(new JobSkillRequirement(skillName, ProficiencyLevel.fromString(proficiencyStr), isMandatory));
                }
            }
            
        } catch (Exception e) {
            throw new IllegalArgumentException("岗位要求JSON解析失败: " + e.getMessage(), e);
        }
        
        return requirements;
    }
    
    /**
     * 执行技能差距分析
     * @param profileInfo 个人Profile信息
     * @param jobRequirements 目标岗位技能要求
     * @return 技能差距分析结果
     */
    public static SkillGapAnalysisResult analyzeSkillGap(ProfileInfo profileInfo, List<JobSkillRequirement> jobRequirements) {
        SkillGapAnalysisResult result = new SkillGapAnalysisResult();
        result.setProfileInfo(profileInfo);
        result.setJobRequirements(jobRequirements);
        
        // 构建个人技能映射
        Map<String, ProficiencyLevel> currentSkillsMap = new HashMap<>();
        for (SkillInfo skill : profileInfo.getCurrentSkills()) {
            currentSkillsMap.put(skill.getSkillName().toLowerCase().trim(), skill.getProficiency());
        }
        
        // 分析每个岗位要求
        for (JobSkillRequirement requirement : jobRequirements) {
            String skillNameLower = requirement.getSkillName().toLowerCase().trim();
            ProficiencyLevel currentProficiency = currentSkillsMap.get(skillNameLower);
            
            if (currentProficiency == null) {
                // 缺失技能
                SkillGapDetail gap = new SkillGapDetail();
                gap.setSkillName(requirement.getSkillName());
                gap.setCurrentProficiency(null);
                gap.setRequiredProficiency(requirement.getRequiredProficiency());
                gap.setPriority(requirement.isMandatory() ? GapPriority.HIGH : GapPriority.MEDIUM);
                gap.setImprovementSuggestions(generateSuggestions(requirement.getSkillName(), requirement.getRequiredProficiency(), null));
                result.getMissingSkills().add(gap);
            } else if (currentProficiency.getLevel() < requirement.getRequiredProficiency().getLevel()) {
                // 熟练度不足
                SkillGapDetail gap = new SkillGapDetail();
                gap.setSkillName(requirement.getSkillName());
                gap.setCurrentProficiency(currentProficiency);
                gap.setRequiredProficiency(requirement.getRequiredProficiency());
                
                int levelDiff = requirement.getRequiredProficiency().getLevel() - currentProficiency.getLevel();
                if (requirement.isMandatory() && levelDiff >= 2) {
                    gap.setPriority(GapPriority.HIGH);
                } else if (requirement.isMandatory() || levelDiff >= 1) {
                    gap.setPriority(GapPriority.MEDIUM);
                } else {
                    gap.setPriority(GapPriority.LOW);
                }
                
                gap.setImprovementSuggestions(generateSuggestions(requirement.getSkillName(), requirement.getRequiredProficiency(), currentProficiency));
                result.getInsufficientSkills().add(gap);
            } else {
                // 匹配技能
                result.getMatchedSkills().add(requirement.getSkillName());
            }
        }
        
        // 生成总结
        result.setSummary(generateSummary(result));
        result.setSuccess(true);
        
        return result;
    }
    
    /**
     * 生成技能提升建议
     */
    private static List<String> generateSuggestions(String skillName, ProficiencyLevel targetLevel, ProficiencyLevel currentLevel) {
        List<String> suggestions = new ArrayList<>();
        
        String levelDiff = currentLevel == null ? "零基础" : 
            (currentLevel.getLevel() < targetLevel.getLevel() ? "进阶" : "保持");
        
        switch (targetLevel) {
            case EXPERT:
                suggestions.add("深入研究" + skillName + "领域前沿技术，参与开源项目贡献");
                suggestions.add("考取" + skillName + "相关高级认证，积累大型项目经验");
                break;
            case ADVANCED:
                suggestions.add("系统学习" + skillName + "高级特性，完成复杂项目实践");
                suggestions.add("参与" + skillName + "技术社区交流，分享技术心得");
                break;
            case INTERMEDIATE:
                suggestions.add("完成" + skillName + "中级课程学习，实践中等复杂度项目");
                suggestions.add("通过实战项目巩固" + skillName + "技能，积累经验");
                break;
            case BEGINNER:
            default:
                suggestions.add("学习" + skillName + "基础课程，掌握核心概念");
                suggestions.add("完成入门级实践项目，建立技能基础");
                break;
        }
        
        return suggestions;
    }
    
    /**
     * 生成分析总结
     */
    private static String generateSummary(SkillGapAnalysisResult result) {
        int missingCount = result.getMissingSkills().size();
        int insufficientCount = result.getInsufficientSkills().size();
        int matchedCount = result.getMatchedSkills().size();
        
        StringBuilder summary = new StringBuilder();
        
        if (matchedCount == 0 && missingCount == 0 && insufficientCount == 0) {
            return "未找到有效的技能匹配数据。";
        }
        
        if (matchedCount > 0) {
            summary.append("已掌握").append(matchedCount).append("项匹配技能");
            if (missingCount > 0 || insufficientCount > 0) {
                summary.append("，");
            }
        }
        
        if (missingCount > 0) {
            summary.append("缺少").append(missingCount).append("项必需技能");
            if (insufficientCount > 0) {
                summary.append("，");
            }
        }
        
        if (insufficientCount > 0) {
            summary.append(insufficientCount).append("项技能熟练度不足");
        }
        
        summary.append("。");
        
        if (missingCount == 0 && insufficientCount == 0) {
            summary.append("技能匹配度优秀，建议直接申请目标岗位。");
        } else {
            summary.append("建议优先提升高优先级技能，逐步缩小差距。");
        }
        
        return summary.toString();
    }
    
    // ==================== 主入口模块 ====================
    
    /**
     * 完整的技能差距分析流程
     * @param profileUrl TA的Profile链接
     * @param jobRequirementsJson 目标岗位技能要求（JSON格式）
     * @return 结构化分析结果（JSON格式）
     */
    public static String analyzeSkillGapFromUrl(String profileUrl, String jobRequirementsJson) {
        SkillGapAnalysisResult result = new SkillGapAnalysisResult();
        
        try {
            // 1. 验证输入
            if (profileUrl == null || profileUrl.trim().isEmpty()) {
                throw new IllegalArgumentException("Profile链接不能为空");
            }
            if (jobRequirementsJson == null || jobRequirementsJson.trim().isEmpty()) {
                throw new IllegalArgumentException("岗位技能要求不能为空");
            }
            
            // 2. 提取Profile内容
            System.out.println("=== 步骤1：提取Profile内容 ===");
            String profileJson = extractProfileContent(profileUrl);
            System.out.println("提取结果: " + (profileJson.length() > 100 ? profileJson.substring(0, 100) + "..." : profileJson));
            
            // 3. 解析Profile和岗位要求
            System.out.println("\n=== 步骤2：解析数据 ===");
            ProfileInfo profileInfo = parseProfileJson(profileJson);
            profileInfo.setProfileUrl(profileUrl);
            List<JobSkillRequirement> requirements = parseJobRequirementsJson(jobRequirementsJson);
            
            // 4. 执行技能差距分析
            System.out.println("\n=== 步骤3：执行技能差距分析 ===");
            result = analyzeSkillGap(profileInfo, requirements);
            
        } catch (IllegalArgumentException e) {
            result.setSuccess(false);
            result.setErrorMessage("参数错误: " + e.getMessage());
        } catch (IOException e) {
            result.setSuccess(false);
            result.setErrorMessage("API调用失败: " + e.getMessage());
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMessage("分析过程出错: " + e.getMessage());
        }
        
        return result.toJson();
    }
    
    /**
     * 使用本地Profile信息进行技能差距分析（无需API调用）
     * @param profileInfo 个人Profile信息
     * @param jobRequirements 目标岗位技能要求
     * @return 结构化分析结果（JSON格式）
     */
    public static String analyzeSkillGapLocal(ProfileInfo profileInfo, List<JobSkillRequirement> jobRequirements) {
        try {
            SkillGapAnalysisResult result = analyzeSkillGap(profileInfo, jobRequirements);
            return result.toJson();
        } catch (Exception e) {
            return "{\"success\": false, \"errorMessage\": \"" + escapeJson(e.getMessage()) + "\"}";
        }
    }
    
    /**
     * 转义JSON字符串
     */
    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
    
    // ==================== 测试示例 ====================
    
    /**
     * 测试入口
     */
    public static void main(String[] args) {
        System.out.println("=== Skill Gap Analysis Service 测试 ===");
        
        // 测试1：测试本地分析（无需API）
        System.out.println("\n--- 测试1：本地技能差距分析 ---");
        testLocalAnalysis();
        
        // 测试2：测试API调用（需要配置API Key）
        System.out.println("\n--- 测试2：API调用测试 ---");
        testApiAnalysis();
    }
    
    /**
     * 测试本地分析功能
     */
    private static void testLocalAnalysis() {
        try {
            // 构建测试Profile信息
            ProfileInfo profile = new ProfileInfo();
            profile.setProfileUrl("https://example.com/profile/test");
            
            profile.getCurrentSkills().add(new SkillInfo("Java", ProficiencyLevel.ADVANCED));
            profile.getCurrentSkills().add(new SkillInfo("Python", ProficiencyLevel.INTERMEDIATE));
            profile.getCurrentSkills().add(new SkillInfo("SQL", ProficiencyLevel.ADVANCED));
            profile.getCurrentSkills().add(new SkillInfo("Git", ProficiencyLevel.BEGINNER));
            
            profile.getWorkExperience().add("2年TA经验，负责Java课程辅导");
            profile.getWorkExperience().add("参与多个软件开发项目");
            
            profile.getEducationBackground().add("本科，计算机科学与技术");
            
            // 构建测试岗位要求
            List<JobSkillRequirement> requirements = new ArrayList<>();
            requirements.add(new JobSkillRequirement("Java", ProficiencyLevel.ADVANCED, true));
            requirements.add(new JobSkillRequirement("Python", ProficiencyLevel.ADVANCED, true));
            requirements.add(new JobSkillRequirement("深度学习", ProficiencyLevel.INTERMEDIATE, true));
            requirements.add(new JobSkillRequirement("Git", ProficiencyLevel.INTERMEDIATE, false));
            requirements.add(new JobSkillRequirement("Linux", ProficiencyLevel.BEGINNER, false));
            
            // 执行分析
            String resultJson = analyzeSkillGapLocal(profile, requirements);
            System.out.println("分析结果:");
            System.out.println(formatJson(resultJson));
            
        } catch (Exception e) {
            System.out.println("测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 测试API调用功能
     */
    private static void testApiAnalysis() {
        if (getApiKey().isEmpty()) {
            System.out.println("未配置API Key，跳过API测试");
            return;
        }
        
        try {
            // 测试Profile链接（实际使用时替换为真实链接）
            String profileUrl = "https://example.com/profile/test";
            
            // 测试岗位要求JSON
            String jobRequirementsJson = "{\"requirements\": [" +
                    "{\"skillName\": \"Java\", \"requiredProficiency\": \"高级\", \"isMandatory\": true}," +
                    "{\"skillName\": \"Python\", \"requiredProficiency\": \"高级\", \"isMandatory\": true}," +
                    "{\"skillName\": \"深度学习\", \"requiredProficiency\": \"中级\", \"isMandatory\": true}," +
                    "{\"skillName\": \"Git\", \"requiredProficiency\": \"中级\", \"isMandatory\": false}" +
                    "]}";
            
            // 执行完整分析流程
            String resultJson = analyzeSkillGapFromUrl(profileUrl, jobRequirementsJson);
            System.out.println("分析结果:");
            System.out.println(formatJson(resultJson));
            
        } catch (Exception e) {
            System.out.println("API测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * 格式化JSON输出
     */
    private static String formatJson(String json) {
        try {
            JsonObject jsonObj = JsonParser.parseString(json).getAsJsonObject();
            return new Gson().toJson(jsonObj);
        } catch (Exception e) {
            return json;
        }
    }
}