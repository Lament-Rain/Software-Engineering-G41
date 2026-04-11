package utils;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class FileUtils {
    private static final String RESUME_DIR = "src/main/resources/data/resumes";

    // 计算文件的MD5值
    public static String calculateMD5(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                md.update(buffer, 0, length);
            }
        }
        byte[] bytes = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    // 上传简历文件
    public static String uploadResume(File file, String taId) throws IOException, NoSuchAlgorithmException {
        // 确保简历目录存在
        File resumeDir = new File(RESUME_DIR);
        if (!resumeDir.exists()) {
            resumeDir.mkdirs();
        }

        // 生成唯一文件名
        String originalName = file.getName();
        String extension = originalName.substring(originalName.lastIndexOf("."));
        String uniqueName = taId + "_" + UUID.randomUUID().toString() + extension;
        String filePath = RESUME_DIR + "/" + uniqueName;

        // 复制文件
        try (FileInputStream fis = new FileInputStream(file);
             FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, length);
            }
        }

        return filePath;
    }

    // 删除简历文件
    public static boolean deleteResume(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }

    // 检查文件大小是否符合要求（≤10MB）
    public static boolean checkFileSize(File file) {
        return file.length() <= 10 * 1024 * 1024; // 10MB
    }

    // 检查文件格式是否支持
    public static boolean checkFileFormat(String fileName) {
        String extension = fileName.toLowerCase();
        return extension.endsWith(".pdf");
    }
}
