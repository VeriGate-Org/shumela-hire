package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.LeaveTypeRequest;
import com.arthmatic.shumelahire.dto.LeaveTypeResponse;
import com.arthmatic.shumelahire.entity.GenderRestriction;
import com.arthmatic.shumelahire.entity.LeaveType;
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
public class LeaveTypeService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveTypeService.class);

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public LeaveTypeResponse createLeaveType(LeaveTypeRequest request) {
        String tenantId = TenantContext.requireCurrentTenant();
        logger.info("Creating leave type: {} for tenant: {}", request.getCode(), tenantId);

        if (leaveTypeRepository.existsByTenantIdAndCode(tenantId, request.getCode())) {
            throw new IllegalArgumentException("Leave type code already exists: " + request.getCode());
        }

        LeaveType leaveType = new LeaveType();
        mapRequestToEntity(request, leaveType);

        LeaveType saved = leaveTypeRepository.save(leaveType);
        return LeaveTypeResponse.fromEntity(saved);
    }

    public LeaveTypeResponse updateLeaveType(Long id, LeaveTypeRequest request) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + id));

        // Check code uniqueness if changed
        if (!leaveType.getCode().equals(request.getCode())) {
            String tenantId = TenantContext.requireCurrentTenant();
            if (leaveTypeRepository.existsByTenantIdAndCode(tenantId, request.getCode())) {
                throw new IllegalArgumentException("Leave type code already exists: " + request.getCode());
            }
        }

        mapRequestToEntity(request, leaveType);
        LeaveType saved = leaveTypeRepository.save(leaveType);
        return LeaveTypeResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public LeaveTypeResponse getLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + id));
        return LeaveTypeResponse.fromEntity(leaveType);
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getAllLeaveTypes() {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveTypeRepository.findByTenantIdOrderBySortOrderAsc(tenantId).stream()
                .map(LeaveTypeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveTypeResponse> getActiveLeaveTypes() {
        String tenantId = TenantContext.requireCurrentTenant();
        return leaveTypeRepository.findByTenantIdAndActiveTrue(tenantId).stream()
                .map(LeaveTypeResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deactivateLeaveType(Long id) {
        LeaveType leaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Leave type not found: " + id));
        leaveType.setActive(false);
        leaveTypeRepository.save(leaveType);
        logger.info("Deactivated leave type: {}", id);
    }

    private void mapRequestToEntity(LeaveTypeRequest request, LeaveType entity) {
        entity.setName(request.getName());
        entity.setCode(request.getCode());
        entity.setDescription(request.getDescription());
        entity.setDefaultDaysPerYear(request.getDefaultDaysPerYear());
        entity.setMaxCarryOverDays(request.getMaxCarryOverDays());
        entity.setCarryOverAllowed(request.isCarryOverAllowed());
        entity.setCarryOverExpiryMonths(request.getCarryOverExpiryMonths());
        entity.setRequiresApproval(request.isRequiresApproval());
        entity.setRequiresDocumentation(request.isRequiresDocumentation());
        entity.setMinDaysNotice(request.getMinDaysNotice());
        entity.setMaxConsecutiveDays(request.getMaxConsecutiveDays());
        entity.setPaid(request.isPaid());
        if (request.getGenderRestriction() != null) {
            entity.setGenderRestriction(GenderRestriction.valueOf(request.getGenderRestriction()));
        }
        entity.setAppliesToEmploymentTypes(request.getAppliesToEmploymentTypes());
        entity.setColorCode(request.getColorCode());
        entity.setSortOrder(request.getSortOrder());
    }
}
