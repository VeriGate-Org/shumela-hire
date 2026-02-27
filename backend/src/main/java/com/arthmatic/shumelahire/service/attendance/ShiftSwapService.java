package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.ShiftSwapRequestDto;
import com.arthmatic.shumelahire.dto.attendance.ShiftSwapResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.ShiftSchedule;
import com.arthmatic.shumelahire.entity.ShiftSwapRequest;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.ShiftScheduleRepository;
import com.arthmatic.shumelahire.repository.ShiftSwapRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftSwapService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftSwapService.class);

    private final ShiftSwapRequestRepository swapRepository;
    private final ShiftScheduleRepository scheduleRepository;
    private final EmployeeRepository employeeRepository;

    public ShiftSwapService(ShiftSwapRequestRepository swapRepository,
                            ShiftScheduleRepository scheduleRepository,
                            EmployeeRepository employeeRepository) {
        this.swapRepository = swapRepository;
        this.scheduleRepository = scheduleRepository;
        this.employeeRepository = employeeRepository;
    }

    public ShiftSwapResponse createRequest(Long requesterId, ShiftSwapRequestDto request) {
        Employee requester = employeeRepository.findById(requesterId)
                .orElseThrow(() -> new IllegalArgumentException("Requester not found"));
        Employee target = employeeRepository.findById(request.getTargetEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Target employee not found"));
        ShiftSchedule requesterSchedule = scheduleRepository.findById(request.getRequesterScheduleId())
                .orElseThrow(() -> new IllegalArgumentException("Requester schedule not found"));

        ShiftSwapRequest swap = new ShiftSwapRequest();
        swap.setRequester(requester);
        swap.setRequesterSchedule(requesterSchedule);
        swap.setTargetEmployee(target);
        swap.setSwapDate(request.getSwapDate());
        swap.setTargetDate(request.getTargetDate());
        swap.setReason(request.getReason());

        if (request.getTargetScheduleId() != null) {
            ShiftSchedule targetSchedule = scheduleRepository.findById(request.getTargetScheduleId())
                    .orElseThrow(() -> new IllegalArgumentException("Target schedule not found"));
            swap.setTargetSchedule(targetSchedule);
        }

        swap = swapRepository.save(swap);
        logger.info("Shift swap request created: {} -> {} for date {}",
                requester.getFullName(), target.getFullName(), request.getSwapDate());
        return ShiftSwapResponse.fromEntity(swap);
    }

    public ShiftSwapResponse respondAsTarget(Long swapId, Long targetEmployeeId, boolean accept, String notes) {
        ShiftSwapRequest swap = findById(swapId);
        if (!swap.getTargetEmployee().getId().equals(targetEmployeeId)) {
            throw new IllegalArgumentException("Only the target employee can respond");
        }
        if (swap.getStatus() != ShiftSwapRequest.SwapStatus.PENDING_TARGET) {
            throw new IllegalArgumentException("Swap request is not pending target response");
        }

        swap.setTargetResponseAt(LocalDateTime.now());
        swap.setTargetResponseNotes(notes);

        if (accept) {
            swap.setStatus(ShiftSwapRequest.SwapStatus.PENDING_MANAGER);
            logger.info("Shift swap {} accepted by target, pending manager approval", swapId);
        } else {
            swap.setStatus(ShiftSwapRequest.SwapStatus.TARGET_REJECTED);
            logger.info("Shift swap {} rejected by target", swapId);
        }

        swap = swapRepository.save(swap);
        return ShiftSwapResponse.fromEntity(swap);
    }

    public ShiftSwapResponse approveAsManager(Long swapId, Long managerId, boolean approve, String notes) {
        ShiftSwapRequest swap = findById(swapId);
        if (swap.getStatus() != ShiftSwapRequest.SwapStatus.PENDING_MANAGER) {
            throw new IllegalArgumentException("Swap request is not pending manager approval");
        }

        Employee manager = employeeRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        swap.setManagerApprovedBy(manager);
        swap.setManagerApprovedAt(LocalDateTime.now());
        swap.setManagerNotes(notes);

        if (approve) {
            swap.setStatus(ShiftSwapRequest.SwapStatus.APPROVED);
            executeSwap(swap);
            logger.info("Shift swap {} approved by manager {}", swapId, manager.getFullName());
        } else {
            swap.setStatus(ShiftSwapRequest.SwapStatus.REJECTED);
            logger.info("Shift swap {} rejected by manager {}", swapId, manager.getFullName());
        }

        swap = swapRepository.save(swap);
        return ShiftSwapResponse.fromEntity(swap);
    }

    public ShiftSwapResponse cancel(Long swapId, Long requesterId) {
        ShiftSwapRequest swap = findById(swapId);
        if (!swap.getRequester().getId().equals(requesterId)) {
            throw new IllegalArgumentException("Only the requester can cancel");
        }
        swap.setStatus(ShiftSwapRequest.SwapStatus.CANCELLED);
        swap = swapRepository.save(swap);
        logger.info("Shift swap {} cancelled", swapId);
        return ShiftSwapResponse.fromEntity(swap);
    }

    @Transactional(readOnly = true)
    public Page<ShiftSwapResponse> getByRequester(Long requesterId, Pageable pageable) {
        return swapRepository.findByRequesterId(requesterId, pageable)
                .map(ShiftSwapResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getPendingForTarget(Long targetId) {
        return swapRepository.findPendingForTarget(targetId).stream()
                .map(ShiftSwapResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<ShiftSwapResponse> getPendingManagerApproval(Pageable pageable) {
        return swapRepository.findPendingManagerApproval(pageable)
                .map(ShiftSwapResponse::fromEntity);
    }

    private void executeSwap(ShiftSwapRequest swap) {
        ShiftSchedule requesterSchedule = swap.getRequesterSchedule();
        ShiftSchedule targetSchedule = swap.getTargetSchedule();

        if (targetSchedule != null) {
            // Swap the shifts between two employees
            Employee tempEmp = requesterSchedule.getEmployee();
            requesterSchedule.setEmployee(swap.getTargetEmployee());
            targetSchedule.setEmployee(tempEmp);
            requesterSchedule.setStatus(ShiftSchedule.ScheduleStatus.SWAPPED);
            targetSchedule.setStatus(ShiftSchedule.ScheduleStatus.SWAPPED);
            scheduleRepository.save(requesterSchedule);
            scheduleRepository.save(targetSchedule);
        } else {
            // Simple reassignment
            requesterSchedule.setEmployee(swap.getTargetEmployee());
            requesterSchedule.setStatus(ShiftSchedule.ScheduleStatus.SWAPPED);
            scheduleRepository.save(requesterSchedule);
        }
    }

    private ShiftSwapRequest findById(Long id) {
        return swapRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift swap request not found"));
    }
}
