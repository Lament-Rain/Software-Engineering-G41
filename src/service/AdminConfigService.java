package service;

import model.AdminConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class AdminConfigService {
    private static final String CONFIG_FILE = "src/data/admin_config.properties";

    public static AdminConfig loadConfig() {
        ensureFile();
        AdminConfig config = new AdminConfig();
        Properties p = new Properties();

        try (FileInputStream in = new FileInputStream(CONFIG_FILE)) {
            p.load(in);
            config.setLowThreshold(parseInt(p.getProperty("lowThreshold"), 0));
            config.setMidThreshold(parseInt(p.getProperty("midThreshold"), 8));
            config.setHighThreshold(parseInt(p.getProperty("highThreshold"), 10));
            config.setMaxThreshold(parseInt(p.getProperty("maxThreshold"), 12));
            config.setAutoReportEnabled(Boolean.parseBoolean(p.getProperty("autoReportEnabled", "false")));
            config.setReportHour(parseInt(p.getProperty("reportHour"), 2));
            config.setAiSkillWeight(parseDouble(p.getProperty("aiSkillWeight"), 0.5));
            config.setAiAvailabilityWeight(parseDouble(p.getProperty("aiAvailabilityWeight"), 0.3));
            config.setAiHistoryWeight(parseDouble(p.getProperty("aiHistoryWeight"), 0.2));
        } catch (IOException ignored) {
        }

        return config;
    }

    public static void saveConfig(AdminConfig config) {
        ensureFile();
        Properties p = new Properties();
        p.setProperty("lowThreshold", String.valueOf(config.getLowThreshold()));
        p.setProperty("midThreshold", String.valueOf(config.getMidThreshold()));
        p.setProperty("highThreshold", String.valueOf(config.getHighThreshold()));
        p.setProperty("maxThreshold", String.valueOf(config.getMaxThreshold()));
        p.setProperty("autoReportEnabled", String.valueOf(config.isAutoReportEnabled()));
        p.setProperty("reportHour", String.valueOf(config.getReportHour()));
        p.setProperty("aiSkillWeight", String.valueOf(config.getAiSkillWeight()));
        p.setProperty("aiAvailabilityWeight", String.valueOf(config.getAiAvailabilityWeight()));
        p.setProperty("aiHistoryWeight", String.valueOf(config.getAiHistoryWeight()));

        try (FileOutputStream out = new FileOutputStream(CONFIG_FILE)) {
            p.store(out, "Admin configuration");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save admin config", e);
        }
    }

    private static int parseInt(String value, int def) {
        try { return Integer.parseInt(value); } catch (Exception e) { return def; }
    }

    private static double parseDouble(String value, double def) {
        try { return Double.parseDouble(value); } catch (Exception e) { return def; }
    }

    private static void ensureFile() {
        try {
            File file = new File(CONFIG_FILE);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                saveConfig(new AdminConfig());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize admin config", e);
        }
    }
}
