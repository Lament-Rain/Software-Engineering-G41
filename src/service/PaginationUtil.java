package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class PaginationUtil {

    public static class Page<T> {
        private final List<T> content;
        private final int pageNumber;
        private final int pageSize;
        private final long totalElements;
        private final int totalPages;

        public Page(List<T> content, int pageNumber, int pageSize, long totalElements) {
            this.content = Collections.unmodifiableList(content);
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.totalElements = totalElements;
            this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
        }

        public List<T> getContent() {
            return content;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public int getPageSize() {
            return pageSize;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public boolean hasNext() {
            return pageNumber < totalPages - 1;
        }

        public boolean hasPrevious() {
            return pageNumber > 0;
        }

        public boolean isFirst() {
            return pageNumber == 0;
        }

        public boolean isLast() {
            return pageNumber >= totalPages - 1;
        }
    }

    public static class PageRequest {
        private final int page;
        private final int size;
        private final String sortBy;
        private final boolean ascending;

        public PageRequest(int page, int size) {
            this(page, size, null, true);
        }

        public PageRequest(int page, int size, String sortBy, boolean ascending) {
            this.page = Math.max(0, page);
            this.size = Math.max(1, size);
            this.sortBy = sortBy;
            this.ascending = ascending;
        }

        public int getPage() {
            return page;
        }

        public int getSize() {
            return size;
        }

        public String getSortBy() {
            return sortBy;
        }

        public boolean isAscending() {
            return ascending;
        }

        public int getOffset() {
            return page * size;
        }
    }

    public static <T> Page<T> paginate(List<T> fullList, int page, int size) {
        if (fullList == null || fullList.isEmpty()) {
            return new Page<>(Collections.emptyList(), page, size, 0);
        }

        int totalElements = fullList.size();
        int fromIndex = Math.min(page * size, totalElements);
        int toIndex = Math.min(fromIndex + size, totalElements);

        List<T> pageContent = fromIndex < toIndex
                ? new ArrayList<>(fullList.subList(fromIndex, toIndex))
                : Collections.emptyList();

        return new Page<>(pageContent, page, size, totalElements);
    }

    public static <T, R> Page<R> paginateWithConversion(
            List<T> fullList,
            int page,
            int size,
            Function<T, R> converter) {

        Page<T> originalPage = paginate(fullList, page, size);
        List<R> convertedContent = new ArrayList<>();
        for (T item : originalPage.getContent()) {
            convertedContent.add(converter.apply(item));
        }
        return new Page<>(convertedContent, page, size, originalPage.getTotalElements());
    }

    public static <T> Page<T> paginateWithSupplier(
            Supplier<List<T>> dataSupplier,
            int page,
            int size,
            String cacheKey,
            long cacheTtlMs) {

        List<T> fullList = CacheService.getOrCompute(
                cacheKey,
                dataSupplier,
                cacheTtlMs
        );

        return paginate(fullList, page, size);
    }

    public static <T> List<List<T>> splitIntoBatches(List<T> list, int batchSize) {
        if (list == null || list.isEmpty() || batchSize <= 0) {
            return Collections.emptyList();
        }

        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            batches.add(new ArrayList<>(list.subList(i, end)));
        }
        return batches;
    }

    public static <T> void processInBatches(
            List<T> items,
            int batchSize,
            java.util.function.Consumer<List<T>> batchProcessor) {

        List<List<T>> batches = splitIntoBatches(items, batchSize);
        for (List<T> batch : batches) {
            batchProcessor.accept(batch);
        }
    }
}
