package com.insurance.policyholder.application.port.output;

import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;

import java.util.Optional;

/**
 * 保戶儲存庫介面（Command 端）
 * 用於保戶聚合根的持久化操作
 *
 * 這是 Output Port，由 Infrastructure Layer 實作
 */
public interface PolicyHolderRepository {

    /**
     * 儲存保戶
     *
     * @param policyHolder 要儲存的保戶聚合根
     * @return 儲存後的保戶
     */
    PolicyHolder save(PolicyHolder policyHolder);

    /**
     * 根據保戶編號查詢
     *
     * @param id 保戶編號
     * @return 保戶聚合根，若不存在則為空
     */
    Optional<PolicyHolder> findById(PolicyHolderId id);

    /**
     * 根據身分證字號查詢
     *
     * @param nationalId 身分證字號
     * @return 保戶聚合根，若不存在則為空
     */
    Optional<PolicyHolder> findByNationalId(NationalId nationalId);

    /**
     * 檢查身分證字號是否已存在
     *
     * @param nationalId 身分證字號
     * @return 是否已存在
     */
    boolean existsByNationalId(NationalId nationalId);

    /**
     * 刪除保戶
     *
     * @param id 保戶編號
     */
    void deleteById(PolicyHolderId id);
}
