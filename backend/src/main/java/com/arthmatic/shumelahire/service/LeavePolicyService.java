package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeavePolicyRequest;
import com.arthmatic.shumelahire.dto.LeavePolicyResponse;
import com.arthmatic.shumelahire.entity.LeaveAccrualFrequency;
import com.arthmatic.shumelahire.entity.LeavePolicy;
import com.arthmatic.shumelahire.entity.LeaveType;
import com.arthmatic.shumelahire.repository.LeavePolicyRepository;
import com.arthmatic.shumelahire.repository.LeaveTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeavePolicyService {

    private static final Logger logger = LoggerFactory.getLogger(LeavePolicyService.class);

    @Autowired
    private LeavePolicyRepository leavePolicyRepository;

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public LeavePolicyResponse createPolicy(LeavePolicyRequest request) {
        String tenantId = TenantContext.requireCurrentTenant();
        logger.info("Creating leave policy: {} for tenant: {}", request.getName(), tenantId);

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + request.getLeaveTypeId()));

        LeavePolicy policy = new LeavePolicy();
        mapRequestToEntity(request, policy, leaveType);

        LeavePolicy saved = leavePolicyRepository.save(policy);
        return LeavePolicyResponse.fromEntity(saved);
    }

    public LeavePolicyResponse updatePolicy(Long id, LeavePolicyRequest request) {
        LeavePolicy policy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave policy not found: " + id));

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + request.getLeaveTypeId()));

        mapRequestToEntity(request, policy, leaveType);
        LeavePolicy saved = leavePolicyRepository.save(policy);
        return LeavePolicyResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public LeavePolicyResponse getPolicy(Long id) {
        LeavePolicy policy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave policy not found: " + id));
        return LeavePolicyResponse.fromEntity(policy);
    }

    @Transactional(readOnly = true)
    public List<LeavePolicyResponse> getActivePolicies() {
        String tenantId = TenantContext.requireCurrentTenant();
        return leavePolicyRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(LeavePolicyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeavePolicyResponse> getPoliciesByLeaveType(Long leaveTypeId) {
        String tenantId = TenantContext.requireCurrentTenant();
        return leavePolicyRepository.findByTenantIdAndLeaveTypeId(tenantId, leaveTypeId).stream()
                .map(LeavePolicyResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deactivatePolicy(Long id) {
        LeavePolicy policy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave policy not found: " + id));
        policy.setActive(false);
        leavePolicyRepository.save(policy);
        logger.info("Deactivated leave policy: {}", id);
    }

    private void mapRequestToEntity(LeavePolicyRequest request, LeavePolicy entity, LeaveType leaveType) {
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());
        entity.setLeaveType(leaveType);
        entity.setEmploymentType(request.getEmploymentType());
        entity.setDepartment(request.getDepartment());
        entity.setJobGrade(request.getJobGrade());
        entity.setMinServiceMonths(request.getMinServiceMonths());
        entity.setAnnualEntitlement(request.getAnnualEntitlement());
        entity.setAccrualFrequency(LeaveAccrualFrequency.valueOf(request.getAccrualFrequency()));
        entity.setProRataOnJoin(request.isProRataOnJoin());
        entity.setProRataOnLeave(request.isProRataOnLeave());
        entity.setMaxNegativeBalance(request.getMaxNegativeBalance());
        entity.setRequireManagerApproval(request.isRequireManagerApproval());
        entity.setRequireHrApproval(request.isRequireHrApproval());
        entity.setAutoApproveDaysThreshold(request.getAutoApproveDaysThreshold());
        entity.setEffectiveFrom(request.getEffectiveFrom());
        entity.setEffectiveTo(request.getEffectiveTo());
    }
}
