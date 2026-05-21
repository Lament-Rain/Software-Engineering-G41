package service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.io.IOException;

public class PDFResumeParserService {
    
    /**
     * 从PDF文件中提取纯文本内容
     * @param pdfPath PDF文件路径
     * @return 提取的文本内容，失败返回null
     */
    public static String extractTextFromPDF(String pdfPath) {
        if (pdfPath == null || pdfPath.isEmpty()) {
            return null;
        }
        
        File pdfFile = new File(pdfPath);
        if (!pdfFile.exists()) {
            System.out.println("PDF file not found: " + pdfPath);
            return null;
        }
        
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            System.out.println("Successfully extracted text from PDF, length: " + text.length());
            return text;
        } catch (IOException e) {
            System.err.println("Failed to extract text from PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 从PDF文件中提取纯文本内容，限制最大字符数
     * @param pdfPath PDF文件路径
     * @param maxChars 最大字符数
     * @return 提取的文本内容，失败返回null
     */
    public static String extractTextFromPDF(String pdfPath, int maxChars) {
        String text = extractTextFromPDF(pdfPath);
        if (text == null) {
            return null;
        }
        
        if (text.length() > maxChars) {
            text = text.substring(0, maxChars) + " [TRUNCATED]";
        }
        
        return text;
    }
    
    /**
     * 判断文件是否为PDF文件
     * @param filePath 文件路径
     * @return true如果是PDF文件
     */
    public static boolean isPDFFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }
        return filePath.toLowerCase().endsWith(".pdf");
    }
    
    /**
     * 从文件（PDF或文本）中提取内容
     * @param filePath 文件路径
     * @param maxChars 最大字符数
     * @return 提取的文本内容
     */
    public static String extractContentFromFile(String filePath, int maxChars) {
        if (filePath == null || filePath.isEmpty()) {
            return "No resume provided";
        }
        
        File file = new File(filePath);
        if (!file.exists()) {
            return "Resume file not found";
        }
        
        // 判断是否为PDF
        if (isPDFFile(filePath)) {
            String pdfText = extractTextFromPDF(filePath, maxChars);
            return pdfText != null ? pdfText : "Failed to parse PDF resume";
        }
        
        // 如果是普通文本文件，直接读取
        try {
            StringBuilder content = new StringBuilder();
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(file));
            String line;
            int currentLength = 0;
            
            while ((line = reader.readLine()) != null && currentLength < maxChars) {
                content.append(line).append("\n");
                currentLength += line.length() + 1;
            }
            
            reader.close();
            
            if (currentLength > maxChars) {
                content.append("[TRUNCATED]");
            }
            
            return content.toString();
        } catch (Exception e) {
            System.err.println("Failed to read text file: " + e.getMessage());
            return "Failed to read resume file";
        }
    }
}
