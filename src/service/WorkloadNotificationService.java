package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkloadNotificationService {
    private static final File FILE = new File(DataStorage.getDataDirectory(), "workload_notifications.txt");

    public static synchronized void pushNotification(String taId, String message) {
        ensureFile();
        try (FileWriter writer = new FileWriter(FILE, true)) {
            String safe = message == null ? "" : message.replace("\n", " ").replace("\r", " ");
            writer.write(taId + "|" + safe + "|" + System.currentTimeMillis() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized List<String> consumeNotifications(String taId) {
        ensureFile();
        List<String> messages = new ArrayList<>();
        List<String> remain = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split("\\|", 3);
                if (parts.length < 2) continue;
                if (parts[0].equals(taId)) {
                    messages.add(parts[1]);
                } else {
                    remain.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (FileWriter writer = new FileWriter(FILE, false)) {
            for (String item : remain) {
                writer.write(item + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private static void ensureFile() {
        try {
            File dir = FILE.getParentFile();
            if (dir != null && !dir.exists()) {
                dir.mkdirs();
            }
            if (!FILE.exists()) {
                FILE.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
