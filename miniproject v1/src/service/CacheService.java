package service;

import java.util.*;
import java.util.concurrent.*;

public class CacheService {
    private static final long DEFAULT_TTL_MS = 5 * 60 * 1000;
    private static final long EXTENDED_TTL_MS = 30 * 60 * 1000;

    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    private static class CacheEntry {
        final Object value;
        final long expirationTime;

        CacheEntry(Object value, long ttlMs) {
            this.value = value;
            this.expirationTime = System.currentTimeMillis() + ttlMs;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
    }

    static {
        scheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            cache.entrySet().removeIf(entry -> entry.getValue().expirationTime < now);
        }, 1, 1, TimeUnit.MINUTES);
    }

    public static void put(String key, Object value) {
        put(key, value, DEFAULT_TTL_MS);
    }

    public static void put(String key, Object value, long ttlMs) {
        cache.put(key, new CacheEntry(value, ttlMs));
    }

    public static void putWithExtendedTTL(String key, Object value) {
        put(key, value, EXTENDED_TTL_MS);
    }

    @SuppressWarnings("unchecked")
    public static <T> T get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return (T) entry.value;
    }

    public static <T> T getOrCompute(String key, java.util.function.Supplier<T> computeFunc) {
        return get(key);
    }

    public static <T> T getOrCompute(String key, java.util.function.Supplier<T> computeFunc, long ttlMs) {
        T value = get(key);
        if (value != null) {
            return value;
        }
        value = computeFunc.get();
        if (value != null) {
            put(key, value, ttlMs);
        }
        return value;
    }

    public static void invalidate(String key) {
        cache.remove(key);
    }

    public static void invalidateByPattern(String pattern) {
        cache.keySet().removeIf(key -> key.contains(pattern));
    }

    public static void clear() {
        cache.clear();
    }

    public static String pendingApplicationsKey(String moId) {
        return "pending_apps:" + moId;
    }

    public static String jobApplicationsKey(String jobId) {
        return "job_apps:" + jobId;
    }

    public static String taApplicationsKey(String taId) {
        return "ta_apps:" + taId;
    }

    public static String pendingTAsKey() {
        return "pending_tas";
    }

    public static String availableJobsKey() {
        return "available_jobs";
    }
}
