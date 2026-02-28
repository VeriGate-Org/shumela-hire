package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmploymentEventRequest;
import com.arthmatic.shumelahire.dto.EmploymentEventResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmploymentEvent;
import com.arthmatic.shumelahire.entity.EmploymentEventType;
import com.arthmatic.shumelahire.repository.EmploymentEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmploymentEventService {

    private static final Logger logger = LoggerFactory.getLogger(EmploymentEventService.class);

    @Autowired
    private EmploymentEventRepository eventRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private AuditLogService auditLogService;

    public EmploymentEventResponse createEvent(EmploymentEventRequest request) {
        logger.info("Creating employment event: {} for employee {}", request.getEventType(), request.getEmployeeId());

        Employee employee = employeeService.findEmployeeById(request.getEmployeeId());

        EmploymentEvent event = new EmploymentEvent();
        event.setEmployee(employee);
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setEffectiveDate(request.getEffectiveDate());
        event.setDescription(request.getDescription());
        event.setPreviousValue(request.getPreviousValue());
        event.setNewValue(request.getNewValue());
        event.setReason(request.getReason());
        event.setReferenceNumber(request.getReferenceNumber());

        EmploymentEvent saved = eventRepository.save(event);

        auditLogService.logSystemAction("EMPLOYMENT_EVENT_CREATED", "EMPLOYMENT_EVENT",
                "Event " + request.getEventType() + " for employee " + employee.getEmployeeNumber());

        logger.info("Employment event created with ID: {}", saved.getId());

        return EmploymentEventResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<EmploymentEventResponse> getEmployeeHistory(Long employeeId) {
        List<EmploymentEvent> events = eventRepository.findByEmployeeIdOrderByEventDateDesc(employeeId);
        return events.stream()
                .map(EmploymentEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EmploymentEventResponse> getEmployeeHistoryPaged(Long employeeId, Pageable pageable) {
        return eventRepository.findByEmployeeId(employeeId, pageable)
                .map(EmploymentEventResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<EmploymentEventResponse> getEmployeeEventsByType(Long employeeId, EmploymentEventType eventType) {
        return eventRepository.findByEmployeeIdAndEventType(employeeId, eventType).stream()
                .map(EmploymentEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EmploymentEventResponse> getEmployeeEventsByDateRange(Long employeeId,
                                                                       LocalDate startDate,
                                                                       LocalDate endDate) {
        return eventRepository.findByEmployeeAndDateRange(employeeId, startDate, endDate).stream()
                .map(EmploymentEventResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<EmploymentEventResponse> getRecentEvents(Pageable pageable) {
        return eventRepository.findRecentEvents(pageable)
                .map(EmploymentEventResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public EmploymentEventResponse getEvent(Long eventId) {
        EmploymentEvent event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Employment event not found: " + eventId));
        return EmploymentEventResponse.fromEntity(event);
    }
}
