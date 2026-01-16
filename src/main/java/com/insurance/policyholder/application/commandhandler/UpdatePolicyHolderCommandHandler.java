package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.UpdatePolicyHolderCommand;
import com.insurance.policyholder.application.port.input.CommandHandler;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.exception.PolicyHolderNotFoundException;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.valueobject.Address;
import com.insurance.policyholder.domain.model.valueobject.ContactInfo;
import com.insurance.policyholder.domain.model.valueobject.PolicyHolderId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 更新保戶命令處理器
 * 處理保戶聯絡資訊與地址的更新
 */
@Service
@Transactional
public class UpdatePolicyHolderCommandHandler implements CommandHandler<UpdatePolicyHolderCommand, PolicyHolderReadModel> {

    private final PolicyHolderRepository repository;

    public UpdatePolicyHolderCommandHandler(PolicyHolderRepository repository) {
        this.repository = repository;
    }

    @Override
    public PolicyHolderReadModel handle(UpdatePolicyHolderCommand command) {

        // 1. 查詢現有保戶
        PolicyHolderId id = PolicyHolderId.of(command.getPolicyHolderId());
        PolicyHolder policyHolder = repository.findById(id)
                .orElseThrow(() -> new PolicyHolderNotFoundException(command.getPolicyHolderId()));

        // 2. 更新聯絡資訊
        ContactInfo newContactInfo = ContactInfo.of(
                command.getMobilePhone(),
                command.getEmail()
        );
        policyHolder.updateContactInfo(newContactInfo);

        // 3. 更新地址
        Address newAddress = Address.of(
                command.getZipCode(),
                command.getCity(),
                command.getDistrict(),
                command.getStreet()
        );
        policyHolder.updateAddress(newAddress);

        // 4. 儲存更新後的保戶
        PolicyHolder savedPolicyHolder = repository.save(policyHolder);

        // 5. 轉換為 ReadModel 並回傳
        return toReadModel(savedPolicyHolder);
    }

    private PolicyHolderReadModel toReadModel(PolicyHolder policyHolder) {
        return new PolicyHolderReadModel(
                policyHolder.getId().getValue(),
                policyHolder.getNationalId().getValue(),
                policyHolder.getPersonalInfo().getName(),
                policyHolder.getPersonalInfo().getGender().name(),
                policyHolder.getPersonalInfo().getBirthDate(),
                policyHolder.getContactInfo().getMobilePhone(),
                policyHolder.getContactInfo().getEmail(),
                policyHolder.getAddress().getZipCode(),
                policyHolder.getAddress().getCity(),
                policyHolder.getAddress().getDistrict(),
                policyHolder.getAddress().getStreet(),
                policyHolder.getStatus().name(),
                null, // createdAt - managed by persistence
                null, // updatedAt - managed by persistence
                policyHolder.getVersion()
        );
    }
}
