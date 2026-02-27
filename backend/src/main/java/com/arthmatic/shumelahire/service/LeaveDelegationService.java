package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.LeaveDelegationRequest;
import com.arthmatic.shumelahire.dto.LeaveDelegationResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.LeaveDelegation;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.LeaveDelegationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LeaveDelegationService {

    private static final Logger logger = LoggerFactory.getLogger(LeaveDelegationService.class);

    @Autowired
    private LeaveDelegationRepository leaveDelegationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public LeaveDelegationResponse createDelegation(Long delegatorId, LeaveDelegationRequest request) {
        logger.info("Creating leave delegation from {} to {}", delegatorId, request.getDelegateId());

        if (delegatorId.equals(request.getDelegateId())) {
            throw new IllegalArgumentException("Cannot delegate to yourself");
        }

        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new IllegalArgumentException("End date must be on or after start date");
        }

        // Check for overlapping delegation
        leaveDelegationRepository.findOverlappingDelegation(
                delegatorId, request.getDelegateId(), request.getStartDate(), request.getEndDate())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException("Overlapping delegation already exists");
                });

        Employee delegator = employeeRepository.findById(delegatorId)
                .orElseThrow(() -> new RuntimeException("Delegator not found: " + delegatorId));

        Employee delegate = employeeRepository.findById(request.getDelegateId())
                .orElseThrow(() -> new RuntimeException("Delegate not found: " + request.getDelegateId()));

        LeaveDelegation delegation = new LeaveDelegation();
        delegation.setDelegator(delegator);
        delegation.setDelegate(delegate);
        delegation.setStartDate(request.getStartDate());
        delegation.setEndDate(request.getEndDate());
        delegation.setReason(request.getReason());
        delegation.setCanApproveLeave(request.isCanApproveLeave());
        delegation.setCanApproveEncashment(request.isCanApproveEncashment());

        LeaveDelegation saved = leaveDelegationRepository.save(delegation);
        logger.info("Leave delegation created: {}", saved.getId());
        return LeaveDelegationResponse.fromEntity(saved);
    }

    public void revokeDelegation(Long delegationId) {
        LeaveDelegation delegation = leaveDelegationRepository.findById(delegationId)
                .orElseThrow(() -> new RuntimeException("Delegation not found: " + delegationId));
        delegation.setActive(false);
        leaveDelegationRepository.save(delegation);
        logger.info("Delegation {} revoked", delegationId);
    }

    @Transactional(readOnly = true)
    public List<LeaveDelegationResponse> getMyDelegations(Long delegatorId) {
        return leaveDelegationRepository.findByDelegatorIdAndActiveTrue(delegatorId).stream()
                .map(LeaveDelegationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<LeaveDelegationResponse> getDelegationsToMe(Long delegateId) {
        return leaveDelegationRepository.findByDelegateIdAndActiveTrue(delegateId).stream()
                .map(LeaveDelegationResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean canApproveOnBehalf(Long delegateId, LocalDate date) {
        List<LeaveDelegation> delegations = leaveDelegationRepository
                .findActiveDelegationsForApproval(delegateId, date);
        return !delegations.isEmpty();
    }
}
