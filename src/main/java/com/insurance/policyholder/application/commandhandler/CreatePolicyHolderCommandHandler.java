package com.insurance.policyholder.application.commandhandler;

import com.insurance.policyholder.application.command.CreatePolicyHolderCommand;
import com.insurance.policyholder.application.port.input.CommandHandler;
import com.insurance.policyholder.application.port.output.DomainEventPublisher;
import com.insurance.policyholder.application.port.output.PolicyHolderRepository;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.domain.model.aggregate.PolicyHolder;
import com.insurance.policyholder.domain.model.enums.Gender;
import com.insurance.policyholder.domain.model.valueobject.Address;
import com.insurance.policyholder.domain.model.valueobject.ContactInfo;
import com.insurance.policyholder.domain.model.valueobject.NationalId;
import com.insurance.policyholder.domain.model.valueobject.PersonalInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 建立保戶命令處理器
 * 實作 CreatePolicyHolder 用例
 */
@Service
@Transactional
public class CreatePolicyHolderCommandHandler implements CommandHandler<CreatePolicyHolderCommand, PolicyHolderReadModel> {

    private final PolicyHolderRepository policyHolderRepository;
    private final DomainEventPublisher domainEventPublisher;

    public CreatePolicyHolderCommandHandler(
            PolicyHolderRepository policyHolderRepository,
            DomainEventPublisher domainEventPublisher) {
        this.policyHolderRepository = policyHolderRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public PolicyHolderReadModel handle(CreatePolicyHolderCommand command) {
        // 1. 驗證身分證字號是否已存在
        NationalId nationalId = NationalId.of(command.getNationalId());
        if (policyHolderRepository.existsByNationalId(nationalId)) {
            throw new IllegalArgumentException("National ID already exists: " + command.getNationalId());
        }

        // 2. 建立值物件
        PersonalInfo personalInfo = PersonalInfo.of(
                command.getName(),
                Gender.valueOf(command.getGender()),
                command.getBirthDate()
        );

        ContactInfo contactInfo = ContactInfo.of(
                command.getMobilePhone(),
                command.getEmail()
        );

        Address address = Address.of(
                command.getZipCode(),
                command.getCity(),
                command.getDistrict(),
                command.getStreet()
        );

        // 3. 建立保戶聚合根
        PolicyHolder policyHolder = PolicyHolder.create(
                nationalId,
                personalInfo,
                contactInfo,
                address
        );

        // 4. 儲存保戶
        PolicyHolder savedPolicyHolder = policyHolderRepository.save(policyHolder);

        // 5. 發布領域事件
        domainEventPublisher.publishAll(savedPolicyHolder.getDomainEventsAndClear());

        // 6. 轉換為 ReadModel 並回傳
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
                null, // createdAt - 由 JPA Auditing 設定
                null, // updatedAt - 由 JPA Auditing 設定
                policyHolder.getVersion()
        );
    }
}
