package utils;

import java.io.*;
import java.util.List;

public class JsonUtils {
    // 写入对象列表到文件（简单文本格式）
    public static void writeToFile(List<?> list, String filePath) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            for (Object obj : list) {
                writer.write(obj.toString());
                writer.write("\n");
            }
        }
    }

    // 读取文件到字符串
    public static String readFromFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
}
