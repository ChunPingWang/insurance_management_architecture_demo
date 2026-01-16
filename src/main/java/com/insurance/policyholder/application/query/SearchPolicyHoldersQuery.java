package com.insurance.policyholder.application.query;

import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;

/**
 * 搜尋保戶查詢
 * CQRS Query - 支援姓名模糊搜尋與狀態篩選
 */
public class SearchPolicyHoldersQuery {

    private final String name;
    private final PolicyHolderStatus status;
    private final int page;
    private final int size;

    private SearchPolicyHoldersQuery(String name, PolicyHolderStatus status, int page, int size) {
        this.name = name;
        this.status = status;
        this.page = Math.max(0, page);
        this.size = Math.max(1, Math.min(100, size)); // 限制每頁最多 100 筆
    }

    /**
     * 根據姓名搜尋
     */
    public static SearchPolicyHoldersQuery byName(String name, int page, int size) {
        return new SearchPolicyHoldersQuery(name, null, page, size);
    }

    /**
     * 根據狀態搜尋
     */
    public static SearchPolicyHoldersQuery byStatus(PolicyHolderStatus status, int page, int size) {
        return new SearchPolicyHoldersQuery(null, status, page, size);
    }

    /**
     * 建立空查詢（回傳所有保戶）
     */
    public static SearchPolicyHoldersQuery all(int page, int size) {
        return new SearchPolicyHoldersQuery(null, null, page, size);
    }

    public String getName() {
        return name;
    }

    public PolicyHolderStatus getStatus() {
        return status;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public boolean hasNameFilter() {
        return name != null && !name.isBlank();
    }

    public boolean hasStatusFilter() {
        return status != null;
    }
}
