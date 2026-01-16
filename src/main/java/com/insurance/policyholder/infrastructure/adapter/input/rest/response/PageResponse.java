package com.insurance.policyholder.infrastructure.adapter.input.rest.response;

import com.insurance.policyholder.application.readmodel.PagedResult;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 分頁回應包裝
 *
 * @param <T> 內容元素類型
 */
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public PageResponse() {
    }

    public PageResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    /**
     * 從 PagedResult 轉換為 PageResponse
     */
    public static <S, T> PageResponse<T> from(PagedResult<S> pagedResult, Function<S, T> mapper) {
        List<T> content = pagedResult.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                pagedResult.getPage(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.isFirst(),
                pagedResult.isLast()
        );
    }

    /**
     * 直接從 PagedResult 建立（不轉換內容）
     */
    public static <T> PageResponse<T> from(PagedResult<T> pagedResult) {
        return new PageResponse<>(
                pagedResult.getContent(),
                pagedResult.getPage(),
                pagedResult.getSize(),
                pagedResult.getTotalElements(),
                pagedResult.getTotalPages(),
                pagedResult.isFirst(),
                pagedResult.isLast()
        );
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }
}
