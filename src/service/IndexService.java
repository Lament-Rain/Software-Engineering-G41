package service;

import model.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class IndexService {
    private static final Map<String, List<String>> applicationStatusIndex = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> applicationTaIdIndex = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> applicationJobIdIndex = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> applicationCreateTimeIndex = new ConcurrentHashMap<>();

    private static final Map<String, List<String>> userRoleIdIndex = new ConcurrentHashMap<>();

    private static final Map<String, List<String>> jobStatusIndex = new ConcurrentHashMap<>();
    private static final Map<String, List<String>> jobMoIdIndex = new ConcurrentHashMap<>();

    private static boolean indexesBuilt = false;

    public static synchronized void rebuildIndexes() {
        applicationStatusIndex.clear();
        applicationTaIdIndex.clear();
        applicationJobIdIndex.clear();
        applicationCreateTimeIndex.clear();

        userRoleIdIndex.clear();

        jobStatusIndex.clear();
        jobMoIdIndex.clear();

        for (Application app : DataStorage.getApplications()) {
            indexApplication(app);
        }

        for (User user : DataStorage.getUsers()) {
            indexUser(user);
        }

        for (Job job : DataStorage.getJobs()) {
            indexJob(job);
        }

        indexesBuilt = true;
    }

    private static void ensureIndexesBuilt() {
        if (!indexesBuilt) {
            rebuildIndexes();
        }
    }

    private static void indexApplication(Application app) {
        String statusKey = app.getStatus() != null ? app.getStatus().name() : "NULL";
        applicationStatusIndex.computeIfAbsent(statusKey, k -> new ArrayList<>()).add(app.getId());

        if (app.getTaId() != null) {
            applicationTaIdIndex.computeIfAbsent(app.getTaId(), k -> new ArrayList<>()).add(app.getId());
        }

        if (app.getJobId() != null) {
            applicationJobIdIndex.computeIfAbsent(app.getJobId(), k -> new ArrayList<>()).add(app.getId());
        }

        if (app.getCreatedAt() != null) {
            String dateKey = app.getCreatedAt().substring(0, Math.min(10, app.getCreatedAt().length()));
            applicationCreateTimeIndex.computeIfAbsent(dateKey, k -> new ArrayList<>()).add(app.getId());
        }
    }

    private static void indexUser(User user) {
        if (user.getRole() != null) {
            userRoleIdIndex.computeIfAbsent(user.getRole().name(), k -> new ArrayList<>()).add(user.getId());
        }
    }

    private static void indexJob(Job job) {
        if (job.getStatus() != null) {
            jobStatusIndex.computeIfAbsent(job.getStatus().name(), k -> new ArrayList<>()).add(job.getId());
        }

        if (job.getMoId() != null) {
            jobMoIdIndex.computeIfAbsent(job.getMoId(), k -> new ArrayList<>()).add(job.getId());
        }
    }

    public static void indexApplicationUpdate(Application app) {
        invalidateApplicationIndex(app);
        indexApplication(app);
    }

    public static void indexUserUpdate(User user) {
        invalidateUserIndex(user);
        indexUser(user);
    }

    public static void indexJobUpdate(Job job) {
        invalidateJobIndex(job);
        indexJob(job);
    }

    public static void invalidateApplicationIndex(Application app) {
        if (app.getStatus() != null) {
            List<String> statusList = applicationStatusIndex.get(app.getStatus().name());
            if (statusList != null) {
                statusList.remove(app.getId());
            }
        }

        if (app.getTaId() != null) {
            List<String> taList = applicationTaIdIndex.get(app.getTaId());
            if (taList != null) {
                taList.remove(app.getId());
            }
        }

        if (app.getJobId() != null) {
            List<String> jobList = applicationJobIdIndex.get(app.getJobId());
            if (jobList != null) {
                jobList.remove(app.getId());
            }
        }
    }

    public static void invalidateUserIndex(User user) {
        if (user.getRole() != null) {
            List<String> roleList = userRoleIdIndex.get(user.getRole().name());
            if (roleList != null) {
                roleList.remove(user.getId());
            }
        }
    }

    public static void invalidateJobIndex(Job job) {
        if (job.getStatus() != null) {
            List<String> statusList = jobStatusIndex.get(job.getStatus().name());
            if (statusList != null) {
                statusList.remove(job.getId());
            }
        }

        if (job.getMoId() != null) {
            List<String> moList = jobMoIdIndex.get(job.getMoId());
            if (moList != null) {
                moList.remove(job.getId());
            }
        }
    }

    public static List<Application> getApplicationsByStatus(ApplicationStatus status) {
        ensureIndexesBuilt();
        List<String> appIds = applicationStatusIndex.get(status.name());
        if (appIds == null || appIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Application> appMap = DataStorage.getApplications().stream()
                .collect(Collectors.toMap(Application::getId, a -> a));
        return appIds.stream()
                .map(appMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Application> getApplicationsByTaId(String taId) {
        ensureIndexesBuilt();
        List<String> appIds = applicationTaIdIndex.get(taId);
        if (appIds == null || appIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Application> appMap = DataStorage.getApplications().stream()
                .collect(Collectors.toMap(Application::getId, a -> a));
        return appIds.stream()
                .map(appMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Application> getApplicationsByJobId(String jobId) {
        ensureIndexesBuilt();
        List<String> appIds = applicationJobIdIndex.get(jobId);
        if (appIds == null || appIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Application> appMap = DataStorage.getApplications().stream()
                .collect(Collectors.toMap(Application::getId, a -> a));
        return appIds.stream()
                .map(appMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Application> getApplicationsByCreateTimeRange(String startDate, String endDate) {
        ensureIndexesBuilt();
        List<String> allDateKeys = new ArrayList<>(applicationCreateTimeIndex.keySet());
        return allDateKeys.stream()
                .filter(date -> date.compareTo(startDate) >= 0 && date.compareTo(endDate) <= 0)
                .flatMap(date -> applicationCreateTimeIndex.getOrDefault(date, Collections.emptyList()).stream())
                .map(id -> DataStorage.getApplications().stream()
                        .filter(a -> a.getId().equals(id))
                        .findFirst().orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<User> getUsersByRole(UserRole role) {
        ensureIndexesBuilt();
        List<String> userIds = userRoleIdIndex.get(role.name());
        if (userIds == null || userIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, User> userMap = DataStorage.getUsers().stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        return userIds.stream()
                .map(userMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Job> getJobsByStatus(JobStatus status) {
        ensureIndexesBuilt();
        List<String> jobIds = jobStatusIndex.get(status.name());
        if (jobIds == null || jobIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Job> jobMap = DataStorage.getJobs().stream()
                .collect(Collectors.toMap(Job::getId, j -> j));
        return jobIds.stream()
                .map(jobMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<Job> getJobsByMoId(String moId) {
        ensureIndexesBuilt();
        List<String> jobIds = jobMoIdIndex.get(moId);
        if (jobIds == null || jobIds.isEmpty()) {
            return Collections.emptyList();
        }

        Map<String, Job> jobMap = DataStorage.getJobs().stream()
                .collect(Collectors.toMap(Job::getId, j -> j));
        return jobIds.stream()
                .map(jobMap::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static List<TA> getPendingTAs() {
        return DataStorage.getUsers().stream()
                .filter(u -> u instanceof TA)
                .map(u -> (TA) u)
                .filter(ta -> ta.getProfileStatus() == ProfileStatus.PENDING)
                .collect(Collectors.toList());
    }
}
