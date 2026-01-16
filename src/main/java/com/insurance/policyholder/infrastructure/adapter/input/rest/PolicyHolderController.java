package com.insurance.policyholder.infrastructure.adapter.input.rest;

import com.insurance.policyholder.application.command.AddPolicyCommand;
import com.insurance.policyholder.application.command.CreatePolicyHolderCommand;
import com.insurance.policyholder.application.command.DeletePolicyHolderCommand;
import com.insurance.policyholder.application.command.UpdatePolicyHolderCommand;
import com.insurance.policyholder.application.commandhandler.AddPolicyCommandHandler;
import com.insurance.policyholder.application.commandhandler.CreatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.DeletePolicyHolderCommandHandler;
import com.insurance.policyholder.application.commandhandler.UpdatePolicyHolderCommandHandler;
import com.insurance.policyholder.application.query.GetPolicyHolderByNationalIdQuery;
import com.insurance.policyholder.application.query.GetPolicyHolderPoliciesQuery;
import com.insurance.policyholder.application.query.GetPolicyHolderQuery;
import com.insurance.policyholder.application.query.GetPolicyQuery;
import com.insurance.policyholder.application.query.SearchPolicyHoldersQuery;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderPoliciesQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyHolderQueryHandler;
import com.insurance.policyholder.application.queryhandler.GetPolicyQueryHandler;
import com.insurance.policyholder.application.queryhandler.SearchPolicyHoldersQueryHandler;
import com.insurance.policyholder.application.readmodel.PagedResult;
import com.insurance.policyholder.application.readmodel.PolicyHolderReadModel;
import com.insurance.policyholder.application.readmodel.PolicyReadModel;
import com.insurance.policyholder.infrastructure.adapter.input.rest.mapper.PolicyHolderRestMapper;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.AddPolicyRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.CreatePolicyHolderRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.request.UpdatePolicyHolderRequest;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.ApiResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PageResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyHolderListItemResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyHolderResponse;
import com.insurance.policyholder.infrastructure.adapter.input.rest.response.PolicyResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 保戶 REST Controller
 * 提供保戶相關的 REST API 端點
 */
@RestController
@RequestMapping("/api/v1/policyholders")
@Tag(name = "PolicyHolder", description = "保戶管理 API")
public class PolicyHolderController {

    private static final Logger log = LoggerFactory.getLogger(PolicyHolderController.class);

    private final CreatePolicyHolderCommandHandler createPolicyHolderCommandHandler;
    private final UpdatePolicyHolderCommandHandler updatePolicyHolderCommandHandler;
    private final DeletePolicyHolderCommandHandler deletePolicyHolderCommandHandler;
    private final AddPolicyCommandHandler addPolicyCommandHandler;
    private final GetPolicyHolderQueryHandler getPolicyHolderQueryHandler;
    private final SearchPolicyHoldersQueryHandler searchPolicyHoldersQueryHandler;
    private final GetPolicyHolderPoliciesQueryHandler getPolicyHolderPoliciesQueryHandler;
    private final GetPolicyQueryHandler getPolicyQueryHandler;
    private final PolicyHolderRestMapper mapper;

    public PolicyHolderController(
            CreatePolicyHolderCommandHandler createPolicyHolderCommandHandler,
            UpdatePolicyHolderCommandHandler updatePolicyHolderCommandHandler,
            DeletePolicyHolderCommandHandler deletePolicyHolderCommandHandler,
            AddPolicyCommandHandler addPolicyCommandHandler,
            GetPolicyHolderQueryHandler getPolicyHolderQueryHandler,
            SearchPolicyHoldersQueryHandler searchPolicyHoldersQueryHandler,
            GetPolicyHolderPoliciesQueryHandler getPolicyHolderPoliciesQueryHandler,
            GetPolicyQueryHandler getPolicyQueryHandler,
            PolicyHolderRestMapper mapper) {
        this.createPolicyHolderCommandHandler = createPolicyHolderCommandHandler;
        this.updatePolicyHolderCommandHandler = updatePolicyHolderCommandHandler;
        this.deletePolicyHolderCommandHandler = deletePolicyHolderCommandHandler;
        this.addPolicyCommandHandler = addPolicyCommandHandler;
        this.getPolicyHolderQueryHandler = getPolicyHolderQueryHandler;
        this.searchPolicyHoldersQueryHandler = searchPolicyHoldersQueryHandler;
        this.getPolicyHolderPoliciesQueryHandler = getPolicyHolderPoliciesQueryHandler;
        this.getPolicyQueryHandler = getPolicyQueryHandler;
        this.mapper = mapper;
    }

    /**
     * 建立新保戶
     * POST /api/v1/policyholders
     */
    @PostMapping
    @Operation(summary = "建立新保戶", description = "建立新的保戶基本資料，系統自動產生唯一保戶編號")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "保戶建立成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "請求資料驗證失敗"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "身分證字號已存在")
    })
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> createPolicyHolder(
            @Valid @RequestBody CreatePolicyHolderRequest request) {

        log.info("Creating policy holder with national ID: {}", maskNationalId(request.getNationalId()));

        CreatePolicyHolderCommand command = mapper.toCommand(request);
        PolicyHolderReadModel readModel = createPolicyHolderCommandHandler.handle(command);
        PolicyHolderResponse response = mapper.toResponse(readModel);

        log.info("Policy holder created successfully with ID: {}", response.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Policy holder created successfully"));
    }

    /**
     * 查詢單一保戶
     * GET /api/v1/policyholders/{id}
     */
    @GetMapping("/{id}")
    @Operation(summary = "查詢保戶", description = "根據保戶編號查詢保戶詳細資料")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查詢成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> getPolicyHolder(
            @Parameter(description = "保戶編號") @PathVariable String id) {

        log.info("Getting policy holder by ID: {}", id);

        GetPolicyHolderQuery query = new GetPolicyHolderQuery(id);
        PolicyHolderReadModel readModel = getPolicyHolderQueryHandler.handle(query);
        PolicyHolderResponse response = mapper.toResponse(readModel);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 根據身分證字號查詢保戶
     * GET /api/v1/policyholders/national-id/{nationalId}
     */
    @GetMapping("/national-id/{nationalId}")
    @Operation(summary = "依身分證查詢保戶", description = "根據身分證字號查詢保戶詳細資料")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查詢成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> getPolicyHolderByNationalId(
            @Parameter(description = "身分證字號") @PathVariable String nationalId) {

        log.info("Getting policy holder by national ID: {}", maskNationalId(nationalId));

        GetPolicyHolderByNationalIdQuery query = new GetPolicyHolderByNationalIdQuery(nationalId);
        PolicyHolderReadModel readModel = getPolicyHolderQueryHandler.handleByNationalId(query);
        PolicyHolderResponse response = mapper.toResponse(readModel);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 搜尋保戶列表
     * GET /api/v1/policyholders
     */
    @GetMapping
    @Operation(summary = "搜尋保戶", description = "搜尋保戶列表，支援姓名模糊搜尋與分頁")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查詢成功")
    })
    public ResponseEntity<ApiResponse<PageResponse<PolicyHolderListItemResponse>>> searchPolicyHolders(
            @Parameter(description = "姓名（模糊搜尋）") @RequestParam(required = false) String name,
            @Parameter(description = "頁碼（從0開始）") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "每頁筆數") @RequestParam(defaultValue = "20") int size) {

        log.info("Searching policy holders - name: {}, page: {}, size: {}", name, page, size);

        SearchPolicyHoldersQuery query = name != null && !name.isBlank()
                ? SearchPolicyHoldersQuery.byName(name, page, size)
                : SearchPolicyHoldersQuery.all(page, size);

        PagedResult<PolicyHolderReadModel> pagedResult = searchPolicyHoldersQueryHandler.handle(query);
        PageResponse<PolicyHolderListItemResponse> response = PageResponse.from(pagedResult, mapper::toListItemResponse);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 更新保戶資料
     * PUT /api/v1/policyholders/{id}
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新保戶", description = "更新保戶的聯絡資訊與地址，身分證字號不可修改")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "請求資料驗證失敗"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<ApiResponse<PolicyHolderResponse>> updatePolicyHolder(
            @Parameter(description = "保戶編號") @PathVariable String id,
            @Valid @RequestBody UpdatePolicyHolderRequest request) {

        log.info("Updating policy holder: {}", id);

        UpdatePolicyHolderCommand command = mapper.toUpdateCommand(id, request);
        PolicyHolderReadModel readModel = updatePolicyHolderCommandHandler.handle(command);
        PolicyHolderResponse response = mapper.toResponse(readModel);

        log.info("Policy holder updated successfully: {}", id);

        return ResponseEntity.ok(ApiResponse.success(response, "Policy holder updated successfully"));
    }

    /**
     * 刪除保戶（軟刪除）
     * DELETE /api/v1/policyholders/{id}
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除保戶", description = "軟刪除保戶（狀態改為 INACTIVE），資料保留但無法進行新增保單等操作")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "刪除成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "保戶已被刪除"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<Void> deletePolicyHolder(
            @Parameter(description = "保戶編號") @PathVariable String id) {

        log.info("Deleting policy holder: {}", id);

        DeletePolicyHolderCommand command = new DeletePolicyHolderCommand(id);
        deletePolicyHolderCommandHandler.handle(command);

        log.info("Policy holder deleted successfully: {}", id);

        return ResponseEntity.noContent().build();
    }

    /**
     * 新增保單
     * POST /api/v1/policyholders/{id}/policies
     */
    @PostMapping("/{id}/policies")
    @Operation(summary = "新增保單", description = "為指定保戶新增一張新保單")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "保單新增成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "請求資料驗證失敗或保戶狀態不允許"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<ApiResponse<PolicyResponse>> addPolicy(
            @Parameter(description = "保戶編號") @PathVariable String id,
            @Valid @RequestBody AddPolicyRequest request) {

        log.info("Adding policy to policy holder: {}", id);

        AddPolicyCommand command = mapper.toAddPolicyCommand(id, request);
        PolicyReadModel readModel = addPolicyCommandHandler.handle(command);
        PolicyResponse response = mapper.toPolicyResponse(readModel);

        log.info("Policy added successfully: {} to policy holder: {}", response.getId(), id);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Policy added successfully"));
    }

    /**
     * 查詢保戶的保單列表
     * GET /api/v1/policyholders/{id}/policies
     */
    @GetMapping("/{id}/policies")
    @Operation(summary = "查詢保單列表", description = "查詢指定保戶的所有保單，支援保單類型與狀態篩選")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查詢成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶不存在")
    })
    public ResponseEntity<ApiResponse<java.util.List<PolicyResponse>>> getPolicyHolderPolicies(
            @Parameter(description = "保戶編號") @PathVariable String id,
            @Parameter(description = "保單類型（LIFE, HEALTH, ACCIDENT, TRAVEL, PROPERTY, AUTO, SAFETY）") @RequestParam(required = false) String type,
            @Parameter(description = "保單狀態（ACTIVE, EXPIRED, CANCELLED）") @RequestParam(required = false) String status) {

        log.info("Getting policies for policy holder: {}, type: {}, status: {}", id, type, status);

        GetPolicyHolderPoliciesQuery query = new GetPolicyHolderPoliciesQuery(id, type, status);
        java.util.List<PolicyReadModel> readModels = getPolicyHolderPoliciesQueryHandler.handle(query);
        java.util.List<PolicyResponse> responses = readModels.stream()
                .map(mapper::toPolicyResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    /**
     * 查詢單一保單
     * GET /api/v1/policyholders/{id}/policies/{policyId}
     */
    @GetMapping("/{id}/policies/{policyId}")
    @Operation(summary = "查詢單一保單", description = "根據保單編號查詢保單詳細資料")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "查詢成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "保戶或保單不存在")
    })
    public ResponseEntity<ApiResponse<PolicyResponse>> getPolicy(
            @Parameter(description = "保戶編號") @PathVariable String id,
            @Parameter(description = "保單編號") @PathVariable String policyId) {

        log.info("Getting policy: {} for policy holder: {}", policyId, id);

        GetPolicyQuery query = new GetPolicyQuery(id, policyId);
        PolicyReadModel readModel = getPolicyQueryHandler.handle(query);
        PolicyResponse response = mapper.toPolicyResponse(readModel);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 遮蔽身分證字號用於日誌記錄
     */
    private String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 10) {
            return "***";
        }
        return nationalId.substring(0, 4) + "***" + nationalId.substring(7);
    }
}
