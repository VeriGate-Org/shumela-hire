package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.ShiftSwapRequestDto;
import com.arthmatic.shumelahire.dto.attendance.ShiftSwapResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.attendance.ScheduleStatus;
import com.arthmatic.shumelahire.entity.attendance.ShiftSchedule;
import com.arthmatic.shumelahire.entity.attendance.ShiftSwapRequest;
import com.arthmatic.shumelahire.entity.attendance.SwapRequestStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftScheduleRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftSwapRequestRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ShiftSwapService {

    private static final Logger logger = LoggerFactory.getLogger(ShiftSwapService.class);

    @Autowired
    private ShiftSwapRequestRepository swapRequestRepository;

    @Autowired
    private ShiftScheduleRepository shiftScheduleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public ShiftSwapResponse createSwapRequest(ShiftSwapRequestDto request) {
        logger.info("Creating shift swap request: employee {} <-> employee {}",
                request.getRequesterEmployeeId(), request.getTargetEmployeeId());

        Employee requester = findEmployeeById(request.getRequesterEmployeeId());
        Employee target = findEmployeeById(request.getTargetEmployeeId());
        ShiftSchedule requesterSchedule = findScheduleById(request.getRequesterScheduleId());
        ShiftSchedule targetSchedule = findScheduleById(request.getTargetScheduleId());

        // Validate schedules belong to the correct employees
        if (!requesterSchedule.getEmployee().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("Requester schedule does not belong to the requester employee");
        }
        if (!targetSchedule.getEmployee().getId().equals(target.getId())) {
            throw new IllegalArgumentException("Target schedule does not belong to the target employee");
        }

        ShiftSwapRequest swap = new ShiftSwapRequest();
        swap.setRequesterEmployee(requester);
        swap.setTargetEmployee(target);
        swap.setRequesterSchedule(requesterSchedule);
        swap.setTargetSchedule(targetSchedule);
        swap.setStatus(SwapRequestStatus.PENDING);
        swap.setReason(request.getReason());

        ShiftSwapRequest saved = swapRequestRepository.save(swap);
        logger.info("Shift swap request created: id={}", saved.getId());
        return ShiftSwapResponse.fromEntity(saved);
    }

    public ShiftSwapResponse approveSwap(Long id, String approver) {
        logger.info("Approving shift swap: {} by {}", id, approver);

        ShiftSwapRequest swap = findById(id);
        if (swap.getStatus() != SwapRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING swap requests can be approved");
        }

        swap.setStatus(SwapRequestStatus.APPROVED);
        swap.setApprovedBy(approver);
        swap.setApprovedAt(LocalDateTime.now());

        // Execute the swap — swap the shifts on the schedules
        ShiftSchedule requesterSchedule = swap.getRequesterSchedule();
        ShiftSchedule targetSchedule = swap.getTargetSchedule();

        var tempShift = requesterSchedule.getShift();
        requesterSchedule.setShift(targetSchedule.getShift());
        targetSchedule.setShift(tempShift);

        requesterSchedule.setStatus(ScheduleStatus.SWAPPED);
        targetSchedule.setStatus(ScheduleStatus.SWAPPED);

        shiftScheduleRepository.save(requesterSchedule);
        shiftScheduleRepository.save(targetSchedule);

        ShiftSwapRequest saved = swapRequestRepository.save(swap);
        logger.info("Shift swap approved and executed: id={}", saved.getId());
        return ShiftSwapResponse.fromEntity(saved);
    }

    public ShiftSwapResponse rejectSwap(Long id, String approver, String reason) {
        logger.info("Rejecting shift swap: {} by {}", id, approver);

        ShiftSwapRequest swap = findById(id);
        if (swap.getStatus() != SwapRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING swap requests can be rejected");
        }

        swap.setStatus(SwapRequestStatus.REJECTED);
        swap.setApprovedBy(approver);
        swap.setApprovedAt(LocalDateTime.now());
        swap.setRejectionReason(reason);

        ShiftSwapRequest saved = swapRequestRepository.save(swap);
        return ShiftSwapResponse.fromEntity(saved);
    }

    public ShiftSwapResponse cancelSwap(Long id) {
        logger.info("Cancelling shift swap: {}", id);

        ShiftSwapRequest swap = findById(id);
        if (swap.getStatus() != SwapRequestStatus.PENDING) {
            throw new IllegalStateException("Only PENDING swap requests can be cancelled");
        }

        swap.setStatus(SwapRequestStatus.CANCELLED);
        ShiftSwapRequest saved = swapRequestRepository.save(swap);
        return ShiftSwapResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public ShiftSwapResponse getSwapRequest(Long id) {
        return ShiftSwapResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getSwapsByEmployee(Long employeeId) {
        Employee employee = findEmployeeById(employeeId);
        return swapRequestRepository.findByEmployee(employeeId, employee.getTenantId())
                .stream().map(ShiftSwapResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftSwapResponse> getPendingSwaps() {
        return swapRequestRepository.findByStatus(SwapRequestStatus.PENDING, null)
                .stream().map(ShiftSwapResponse::fromEntity).collect(Collectors.toList());
    }

    private ShiftSwapRequest findById(Long id) {
        return swapRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift swap request not found: " + id));
    }

    private ShiftSchedule findScheduleById(Long id) {
        return shiftScheduleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shift schedule not found: " + id));
    }

    private Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }
}
