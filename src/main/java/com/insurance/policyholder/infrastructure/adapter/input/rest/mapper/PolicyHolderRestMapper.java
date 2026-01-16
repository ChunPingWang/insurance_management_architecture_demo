package com.insurance.policyholder.infrastructure.adapter.input.rest.mapper;

import com.insurance.policyholder.application.command.AddPolicyCommand;
import com.insurance.policyholder.application.command.CreatePolicyHolderCommand;
import com.insurance.policyholder.application.command.UpdatePolicyHolderCommand;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddPolicyRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.CreatePolicyHolderRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.UpdatePolicyHolderRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.AddressResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyHolderListItemResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyHolderResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyResponse;
import org.springframework.stereotype.Component;

/**
 * REST 層的請求/回應轉換器
 */
@Component
public class PolicyHolderRestMapper {

    /**
     * 將建立保戶請求轉換為命令
     */
    public CreatePolicyHolderCommand toCommand(CreatePolicyHolderRequest request) {
        return new CreatePolicyHolderCommand(
                request.getNationalId(),
                request.getName(),
                request.getGender(),
                request.getBirthDate(),
                request.getMobilePhone(),
                request.getEmail(),
                request.getAddress().getZipCode(),
                request.getAddress().getCity(),
                request.getAddress().getDistrict(),
                request.getAddress().getStreet()
        );
    }

    /**
     * 將更新保戶請求轉換為命令
     */
    public UpdatePolicyHolderCommand toUpdateCommand(String policyHolderId, UpdatePolicyHolderRequest request) {
        return new UpdatePolicyHolderCommand(
                policyHolderId,
                request.getMobilePhone(),
                request.getEmail(),
                request.getAddress().getZipCode(),
                request.getAddress().getCity(),
                request.getAddress().getDistrict(),
                request.getAddress().getStreet()
        );
    }

    /**
     * 將 ReadModel 轉換為回應
     */
    public PolicyHolderResponse toResponse(PolicyHolderReadModel readModel) {
        PolicyHolderResponse response = new PolicyHolderResponse();
        response.setId(readModel.getId());
        response.setNationalId(readModel.getNationalId());
        response.setName(readModel.getName());
        response.setGender(readModel.getGender());
        response.setBirthDate(readModel.getBirthDate());
        response.setMobilePhone(readModel.getMobilePhone());
        response.setEmail(readModel.getEmail());
        response.setAddress(new AddressResponse(
                readModel.getZipCode(),
                readModel.getCity(),
                readModel.getDistrict(),
                readModel.getStreet()
        ));
        response.setStatus(readModel.getStatus());
        response.setCreatedAt(readModel.getCreatedAt());
        response.setUpdatedAt(readModel.getUpdatedAt());
        response.setVersion(readModel.getVersion());
        return response;
    }

    /**
     * 將 ReadModel 轉換為列表項目回應
     */
    public PolicyHolderListItemResponse toListItemResponse(PolicyHolderReadModel readModel) {
        return new PolicyHolderListItemResponse(
                readModel.getId(),
                maskNationalId(readModel.getNationalId()),
                readModel.getName(),
                readModel.getGender(),
                readModel.getBirthDate(),
                readModel.getMobilePhone(),
                readModel.getStatus()
        );
    }

    /**
     * 將新增保單請求轉換為命令
     */
    public AddPolicyCommand toAddPolicyCommand(String policyHolderId, AddPolicyRequest request) {
        return new AddPolicyCommand(
                policyHolderId,
                request.getPolicyType(),
                request.getPremium(),
                request.getSumInsured(),
                request.getStartDate(),
                request.getEndDate()
        );
    }

    /**
     * 將 PolicyReadModel 轉換為回應
     */
    public PolicyResponse toPolicyResponse(PolicyReadModel readModel) {
        PolicyResponse response = new PolicyResponse();
        response.setId(readModel.getId());
        response.setPolicyHolderId(readModel.getPolicyHolderId());
        response.setPolicyType(readModel.getPolicyType());
        response.setPremium(readModel.getPremium());
        response.setSumInsured(readModel.getSumInsured());
        response.setStartDate(readModel.getStartDate());
        response.setEndDate(readModel.getEndDate());
        response.setStatus(readModel.getStatus());
        return response;
    }

    /**
     * 遮蔽身分證字號
     */
    private String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 10) {
            return "***";
        }
        return nationalId.substring(0, 4) + "******";
    }
}
