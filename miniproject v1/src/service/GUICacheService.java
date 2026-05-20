package service;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GUICacheService {
    private static final Map<String, CachedLayout> layoutCache = new ConcurrentHashMap<>();
    private static final Map<String, Object> renderCache = new ConcurrentHashMap<>();

    private static final long LAYOUT_CACHE_TTL = 30 * 60 * 1000;
    private static final long RENDER_CACHE_TTL = 10 * 60 * 1000;

    public static class CachedLayout {
        final double x;
        final double y;
        final double width;
        final double height;
        final boolean maximized;
        final long timestamp;

        CachedLayout(double x, double y, double width, double height, boolean maximized) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.maximized = maximized;
            this.timestamp = System.currentTimeMillis();
        }

        boolean isExpired() {
            return System.currentTimeMillis() - timestamp > LAYOUT_CACHE_TTL;
        }
    }

    public static void saveWindowLayout(String windowId, Stage window) {
        if (windowId == null || window == null) {
            return;
        }

        double x = window.getX();
        double y = window.getY();
        double width = window.getWidth();
        double height = window.getHeight();
        boolean maximized = window.isMaximized();

        if (isValidLayout(x, y, width, height)) {
            CachedLayout layout = new CachedLayout(x, y, width, height, maximized);
            layoutCache.put(windowId, layout);
        }
    }

    public static void restoreWindowLayout(String windowId, Stage window) {
        CachedLayout layout = layoutCache.get(windowId);
        if (layout == null || layout.isExpired()) {
            layoutCache.remove(windowId);
            return;
        }

        if (isValidLayout(layout.x, layout.y, layout.width, layout.height)) {
            window.setX(layout.x);
            window.setY(layout.y);
            window.setWidth(layout.width);
            window.setHeight(layout.height);
            if (layout.maximized) {
                window.setMaximized(true);
            }
        }
    }

    private static boolean isValidLayout(double x, double y, double width, double height) {
        if (width <= 0 || height <= 0) {
            return false;
        }

        Screen primaryScreen = Screen.getPrimary();
        Rectangle2D screenBounds = primaryScreen.getVisualBounds();

        return x >= screenBounds.getMinX() - width / 2 &&
               x <= screenBounds.getMaxX() - width / 2 &&
               y >= screenBounds.getMinY() &&
               y <= screenBounds.getMaxY() - height / 2;
    }

    public static void putRenderCache(String key, Object value) {
        if (key != null && value != null) {
            renderCache.put(key, value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getRenderCache(String key) {
        return (T) renderCache.get(key);
    }

    public static void invalidateRenderCache(String key) {
        if (key != null) {
            renderCache.remove(key);
        }
    }

    public static void invalidateAllRenderCache() {
        renderCache.clear();
    }

    public static void invalidateLayoutCache(String key) {
        if (key != null) {
            layoutCache.remove(key);
        }
    }

    public static void clearAllCaches() {
        layoutCache.clear();
        renderCache.clear();
    }

    public static String getWindowLayoutKey(Class<?> controllerClass, String suffix) {
        return controllerClass.getName() + "_layout_" + (suffix != null ? suffix : "default");
    }

    public static String getRenderCacheKey(Class<?> controllerClass, String dataKey) {
        return controllerClass.getName() + "_render_" + (dataKey != null ? dataKey : "default");
    }
}
