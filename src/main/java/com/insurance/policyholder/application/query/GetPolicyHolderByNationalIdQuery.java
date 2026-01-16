package com.insurance.policyholder.application.query;

/**
 * 根據身分證字號查詢保戶
 * CQRS Query
 */
public class GetPolicyHolderByNationalIdQuery {

    private final String nationalId;

    public GetPolicyHolderByNationalIdQuery(String nationalId) {
        if (nationalId == null || nationalId.isBlank()) {
            throw new IllegalArgumentException("National ID cannot be null or empty");
        }
        this.nationalId = nationalId;
    }

    public String getNationalId() {
        return nationalId;
    }
}
