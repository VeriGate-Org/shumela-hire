package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.attendance.ShiftSwapResponse;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ShiftSwapService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftSwapService.class);

    @Autowired
    private ShiftSwapRequestRepository swapRepository;

    @Autowired
    private ShiftScheduleRepository scheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ShiftSwapResponse requestSwap(Long requesterEmployeeId, Long targetEmployeeId,
                                         Long requesterScheduleId, Long targetScheduleId, String reason) {
        Employee requester = employeeRepository.findById(requesterEmployeeId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        Employee target = employeeRepository.findById(targetEmployeeId)
                .orElseThrow(() -> new IllegalArgumentException("Target employee not found"));
        ShiftSchedule requesterSchedule = scheduleRepository.findById(requesterScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Requester schedule not found"));
        ShiftSchedule targetSchedule = scheduleRepository.findById(targetScheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Target schedule not found"));

        ShiftSwapRequest swap = new ShiftSwapRequest();
        swap.setRequesterEmployee(requester);
        swap.setTargetEmployee(target);
        swap.setRequesterSchedule(requesterSchedule);
        swap.setTargetSchedule(targetSchedule);
        swap.setStatus(ShiftSwapRequest.SwapStatus.PENDING);
        swap.setReason(reason);

        ShiftSwapRequest saved = swapRepository.save(swap);
        logger.info("Shift swap request created: {} -> {}", requesterEmployeeId, targetEmployeeId);
        return ShiftSwapResponse.fromEntity(saved);
    }

    public ShiftSwapResponse targetAcceptSwap(Long swapId) {
        ShiftSwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));
        if (swap.getStatus() != ShiftSwapRequest.SwapStatus.PENDING) {
            throw new IllegalArgumentException("Swap request is not pending");
        }
        swap.setStatus(ShiftSwapRequest.SwapStatus.TARGET_ACCEPTED);
        return ShiftSwapResponse.fromEntity(swapRepository.save(swap));
    }

    public ShiftSwapResponse approveSwap(Long swapId, Long approverId) {
        ShiftSwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));
        if (swap.getStatus() != ShiftSwapRequest.SwapStatus.TARGET_ACCEPTED) {
            throw new IllegalArgumentException("Swap must be accepted by target before manager approval");
        }

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        swap.setStatus(ShiftSwapRequest.SwapStatus.APPROVED);
        swap.setApprovedBy(approver);
        swap.setApprovedAt(LocalDateTime.now());

        // Execute the swap
        executeSwap(swap);

        logger.info("Shift swap {} approved and executed by {}", swapId, approverId);
        return ShiftSwapResponse.fromEntity(swapRepository.save(swap));
    }

    public ShiftSwapResponse rejectSwap(Long swapId, Long approverId, String rejectionReason) {
        ShiftSwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));

        Employee approver = employeeRepository.findById(approverId)
                .orElseThrow(() -> new IllegalArgumentException("Approver not found"));

        swap.setStatus(ShiftSwapRequest.SwapStatus.REJECTED);
        swap.setApprovedBy(approver);
        swap.setApprovedAt(LocalDateTime.now());
        swap.setRejectionReason(rejectionReason);

        return ShiftSwapResponse.fromEntity(swapRepository.save(swap));
    }

    public ShiftSwapResponse cancelSwap(Long swapId) {
        ShiftSwapRequest swap = swapRepository.findById(swapId)
                .orElseThrow(() -> new IllegalArgumentException("Swap request not found"));
        if (swap.getStatus() == ShiftSwapRequest.SwapStatus.APPROVED) {
            throw new IllegalArgumentException("Cannot cancel an approved swap");
        }
        swap.setStatus(ShiftSwapRequest.SwapStatus.CANCELLED);
        return ShiftSwapResponse.fromEntity(swapRepository.save(swap));
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getSwapsByRequester(Long employeeId) {
        return swapRepository.findByRequesterEmployeeId(employeeId).stream()
                .map(ShiftSwapResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getSwapsByTarget(Long employeeId) {
        return swapRepository.findByTargetEmployeeId(employeeId).stream()
                .map(ShiftSwapResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getPendingSwaps() {
        return swapRepository.findByStatus(ShiftSwapRequest.SwapStatus.TARGET_ACCEPTED).stream()
                .map(ShiftSwapResponse::fromEntity)
                .toList();
    }

    private void executeSwap(ShiftSwapRequest swap) {
        ShiftSchedule reqSchedule = swap.getRequesterSchedule();
        ShiftSchedule tgtSchedule = swap.getTargetSchedule();

        // Swap the shifts
        Shift tempShift = reqSchedule.getShift();
        reqSchedule.setShift(tgtSchedule.getShift());
        tgtSchedule.setShift(tempShift);

        reqSchedule.setStatus(ShiftSchedule.ScheduleStatus.SWAPPED);
        tgtSchedule.setStatus(ShiftSchedule.ScheduleStatus.SWAPPED);

        scheduleRepository.save(reqSchedule);
        scheduleRepository.save(tgtSchedule);
    }
}
