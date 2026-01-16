package com.insurance.policyholder.application.queryhandler;

import com.insurance.policyholder.application.port.input.QueryHandler;
import com.insurance.policyholder.application.port.output.PolicyHolderQueryRepository;
import com.insurance.policyholder.application.query.SearchPolicyHoldersQuery;
import com.insurance.policyholder.application.readmodel.PagedResult;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 搜尋保戶的處理器
 */
@Service
@Transactional(readOnly = true)
public class SearchPolicyHoldersQueryHandler implements QueryHandler<SearchPolicyHoldersQuery, PagedResult<PolicyHolderReadModel>> {

    private final PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository;

    public SearchPolicyHoldersQueryHandler(PolicyHolderQueryRepository<PolicyHolderReadModel> queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Override
    public PagedResult<PolicyHolderReadModel> handle(SearchPolicyHoldersQuery query) {
        List<PolicyHolderReadModel> content;
        long totalElements;

        if (query.hasNameFilter()) {
            content = queryRepository.searchByName(query.getName(), query.getPage(), query.getSize());
            totalElements = queryRepository.countByName(query.getName());
        } else if (query.hasStatusFilter()) {
            content = queryRepository.findByStatus(query.getStatus(), query.getPage(), query.getSize());
            totalElements = queryRepository.countByStatus(query.getStatus());
        } else {
            // 預設查詢所有（使用空字串搜尋姓名會匹配所有）
            content = queryRepository.searchByName("", query.getPage(), query.getSize());
            totalElements = queryRepository.countByName("");
        }

        return new PagedResult<>(
                content != null ? content : Collections.emptyList(),
                query.getPage(),
                query.getSize(),
                totalElements
        );
    }
}
