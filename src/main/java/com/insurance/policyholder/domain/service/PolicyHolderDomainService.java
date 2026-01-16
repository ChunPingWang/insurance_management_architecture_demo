package com.insurance.policyholder.domain.service;

import com.insurance.policyholder.domain.model.enums.PolicyHolderStatus;
import com.insurance.policyholder.domain.model.valueobject.NationalId;

import java.time.LocalDate;
import java.time.Period;

/**
 * 保戶領域服務
 * 處理跨聚合的驗證與業務規則
 */
public class PolicyHolderDomainService {

    private static final int MINIMUM_AGE = 18;

    /**
     * 驗證是否年滿 18 歲
     *
     * @param birthDate 出生日期
     * @return true if age >= 18
     */
    public boolean isAdult(LocalDate birthDate) {
        if (birthDate == null) {
            return false;
        }
        return Period.between(birthDate, LocalDate.now()).getYears() >= MINIMUM_AGE;
    }

    /**
     * 計算年齡
     *
     * @param birthDate 出生日期
     * @return 年齡
     */
    public int calculateAge(LocalDate birthDate) {
        if (birthDate == null) {
            throw new IllegalArgumentException("Birth date cannot be null");
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 驗證保戶狀態是否可新增保單
     *
     * @param status 保戶狀態
     * @return true if policy holder can add policies
     */
    public boolean canAddPolicy(PolicyHolderStatus status) {
        return status == PolicyHolderStatus.ACTIVE;
    }

    /**
     * 驗證保戶狀態是否可修改
     *
     * @param status 保戶狀態
     * @return true if policy holder can be updated
     */
    public boolean canUpdate(PolicyHolderStatus status) {
        // SUSPENDED 狀態禁止修改
        return status == PolicyHolderStatus.ACTIVE;
    }

    /**
     * 驗證保戶狀態是否可刪除
     *
     * @param status 保戶狀態
     * @return true if policy holder can be deactivated
     */
    public boolean canDeactivate(PolicyHolderStatus status) {
        return status == PolicyHolderStatus.ACTIVE;
    }

    /**
     * 驗證身分證字號格式
     *
     * @param nationalId 身分證字號
     * @return true if valid format
     */
    public boolean isValidNationalIdFormat(String nationalId) {
        if (nationalId == null || nationalId.length() != 10) {
            return false;
        }
        try {
            NationalId.of(nationalId);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 驗證保單日期有效性
     *
     * @param startDate 生效日
     * @param endDate 到期日
     * @return true if valid date range
     */
    public boolean isValidPolicyPeriod(LocalDate startDate, LocalDate endDate) {
        if (startDate == null) {
            return false;
        }
        // 生效日不可早於今日
        if (startDate.isBefore(LocalDate.now())) {
            return false;
        }
        // 如果有到期日，必須在生效日之後
        if (endDate != null && !endDate.isAfter(startDate)) {
            return false;
        }
        return true;
    }

    /**
     * 驗證保費金額
     *
     * @param premiumAmount 保費金額
     * @return true if valid amount
     */
    public boolean isValidPremiumAmount(java.math.BigDecimal premiumAmount) {
        return premiumAmount != null && premiumAmount.compareTo(java.math.BigDecimal.ZERO) > 0;
    }

    /**
     * 驗證保險金額
     *
     * @param sumInsured 保險金額
     * @return true if valid amount
     */
    public boolean isValidSumInsured(java.math.BigDecimal sumInsured) {
        return sumInsured != null && sumInsured.compareTo(java.math.BigDecimal.ZERO) > 0;
    }
}
