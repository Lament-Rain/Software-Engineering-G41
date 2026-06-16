package service;

import model.AdminConfig;
import model.Application;
import model.ApplicationStatus;
import model.Job;
import model.TA;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WorkloadService {
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

    public static int getCurrentWorkload(TA ta) {
        if (ta == null) return 0;
        List<Application> applications = ApplicationService.getApplicationsByTA(ta.getId());
        int totalHours = 0;

        for (Application app : applications) {
            if (app.getStatus() != ApplicationStatus.ACCEPTED) {
                continue;
            }
            Job job = JobService.getJobById(app.getJobId());
            if (job == null) {
                continue;
            }
            totalHours += parseWorkHours(job.getWorkTime());
        }
        return totalHours;
    }

    public static boolean canApply(TA ta) {
        AdminConfig config = AdminConfigService.loadConfig();
        return getCurrentWorkload(ta) < config.getHighThreshold();
    }

    public static String getWorkloadBand(TA ta) {
        AdminConfig c = AdminConfigService.loadConfig();
        int value = getCurrentWorkload(ta);
        if (value < c.getMidThreshold()) return "LOW";
        if (value < c.getHighThreshold()) return "MEDIUM";
        return "HIGH";
    }

    private static int parseWorkHours(String workTime) {
        if (workTime == null || workTime.isBlank()) {
            return 0;
        }
        Matcher matcher = NUMBER_PATTERN.matcher(workTime);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }
}
