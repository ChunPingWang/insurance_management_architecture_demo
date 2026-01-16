package com.insurance.policyholder.application.port.output;

import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;

import java.util.List;
import java.util.Optional;

/**
 * 保戶查詢儲存庫介面（Query 端）
 * 用於保戶的讀取操作，返回 Read Model
 *
 * 這是 Output Port，由 Infrastructure Layer 實作
 *
 * @param <T> Read Model 類型
 */
public interface PolicyHolderQueryRepository<T> {

    /**
     * 根據保戶編號查詢
     *
     * @param id 保戶編號
     * @return Read Model，若不存在則為空
     */
    Optional<T> findById(PolicyHolderId id);

    /**
     * 根據身分證字號查詢
     *
     * @param nationalId 身分證字號
     * @return Read Model，若不存在則為空
     */
    Optional<T> findByNationalId(NationalId nationalId);

    /**
     * 根據姓名模糊搜尋（分頁）
     *
     * @param name 姓名關鍵字
     * @param page 頁碼（從 0 開始）
     * @param size 每頁筆數
     * @return Read Model 列表
     */
    List<T> searchByName(String name, int page, int size);

    /**
     * 根據狀態查詢（分頁）
     *
     * @param status 保戶狀態
     * @param page 頁碼（從 0 開始）
     * @param size 每頁筆數
     * @return Read Model 列表
     */
    List<T> findByStatus(PolicyHolderStatus status, int page, int size);

    /**
     * 計算符合姓名搜尋的總筆數
     *
     * @param name 姓名關鍵字
     * @return 總筆數
     */
    long countByName(String name);

    /**
     * 計算符合狀態的總筆數
     *
     * @param status 保戶狀態
     * @return 總筆數
     */
    long countByStatus(PolicyHolderStatus status);
}
