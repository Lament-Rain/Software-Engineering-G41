package service;

import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class AsyncTaskService {
    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() - 1),
            r -> {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
    );

    public interface ProgressCallback {
        void onProgress(double progress, String message);
    }

    public interface CompleteCallback<T> {
        void onComplete(T result);
        void onError(Exception e);
    }

    public static class TaskHandle {
        private final Future<?> future;
        private volatile boolean cancelled = false;

        TaskHandle(Future<?> future) {
            this.future = future;
        }

        public void cancel() {
            cancelled = true;
            if (future != null) {
                future.cancel(true);
            }
        }

        public boolean isDone() {
            return future == null || future.isDone() || cancelled;
        }
    }

    public static <T> TaskHandle submitAsync(Callable<T> task, CompleteCallback<T> callback) {
        Future<T> future = executor.submit(task);
        TaskHandle handle = new TaskHandle(future);

        executor.execute(() -> {
            try {
                T result = future.get();
                if (!handle.cancelled && callback != null) {
                    Platform.runLater(() -> callback.onComplete(result));
                }
            } catch (Exception e) {
                if (!handle.cancelled && callback != null) {
                    Platform.runLater(() -> callback.onError(e));
                }
            }
        });

        return handle;
    }

    public static <T> TaskHandle submitAsyncWithProgress(
            int totalSteps,
            java.util.function.Function<Integer, T> stepFunction,
            ProgressCallback progressCallback,
            CompleteCallback<List<T>> callback) {

        Task<List<T>> task = new Task<List<T>>() {
            @Override
            protected List<T> call() throws Exception {
                List<T> results = new ArrayList<>();
                for (int i = 0; i < totalSteps; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    final int currentIndex = i;
                    T result = stepFunction.apply(currentIndex);
                    results.add(result);

                    final double currentProgress = (double) (currentIndex + 1) / totalSteps;
                    updateProgress(currentProgress * 100, 100);
                    updateMessage("Processing " + (currentIndex + 1) + " of " + totalSteps);

                    if (progressCallback != null) {
                        Platform.runLater(() -> progressCallback.onProgress(currentProgress * 100,
                                "Processing " + (currentIndex + 1) + " of " + totalSteps));
                    }
                }
                return results;
            }
        };

        if (callback != null) {
            task.setOnSucceeded(e -> callback.onComplete(task.getValue()));
            task.setOnFailed(e -> {
                Throwable ex = task.getException();
                if (ex instanceof Exception) {
                    callback.onError((Exception) ex);
                } else {
                    callback.onError(new Exception(ex));
                }
            });
        }

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return new TaskHandle(null);
    }

    public static TaskHandle batchProcess(
            List<?> items,
            java.util.function.Consumer<Object> processor,
            ProgressCallback progressCallback) {

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int total = items.size();
                for (int i = 0; i < total; i++) {
                    if (isCancelled()) {
                        break;
                    }
                    final int current = i + 1;
                    processor.accept(items.get(i));

                    final double progress = (double) current / total;
                    updateProgress(progress * 100, 100);

                    if (progressCallback != null) {
                        Platform.runLater(() -> progressCallback.onProgress(progress * 100,
                                "Processing " + current + " of " + total));
                    }
                }
                return null;
            }
        };

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return new TaskHandle(null);
    }

    public static TaskHandle exportDataAsync(
            java.util.function.Supplier<String> exportFunction,
            java.util.function.Consumer<String> onComplete,
            ProgressCallback progressCallback) {

        Task<String> task = new Task<String>() {
            @Override
            protected String call() throws Exception {
                updateProgress(0, 100);
                updateMessage("Starting export...");

                if (progressCallback != null) {
                    Platform.runLater(() -> progressCallback.onProgress(10, "Starting export..."));
                }

                updateProgress(50, 100);
                updateMessage("Generating data...");

                if (progressCallback != null) {
                    Platform.runLater(() -> progressCallback.onProgress(50, "Generating data..."));
                }

                String result = exportFunction.get();

                updateProgress(90, 100);
                updateMessage("Finalizing...");

                if (progressCallback != null) {
                    Platform.runLater(() -> progressCallback.onProgress(90, "Finalizing..."));
                }

                updateProgress(100, 100);
                return result;
            }
        };

        if (onComplete != null) {
            task.setOnSucceeded(e -> {
                try {
                    onComplete.accept(task.get());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        }

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();

        return new TaskHandle(null);
    }

    public static void shutdown() {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
