package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmploymentEventRequest;
import com.arthmatic.shumelahire.dto.EmploymentEventResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmploymentEvent;
import com.arthmatic.shumelahire.entity.EmploymentEventType;
import com.arthmatic.shumelahire.repository.EmploymentEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmploymentEventServiceTest {

    @Mock
    private EmploymentEventRepository eventRepository;

    @Mock
    private EmployeeService employeeService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private EmploymentEventService employmentEventService;

    private Employee testEmployee;
    private EmploymentEvent testEvent;
    private EmploymentEventRequest testRequest;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setEmployeeNumber("UTW-2026-0001");
        testEmployee.setFirstName("Thabo");
        testEmployee.setLastName("Mokoena");

        testEvent = new EmploymentEvent();
        testEvent.setId(1L);
        testEvent.setEmployee(testEmployee);
        testEvent.setEventType(EmploymentEventType.HIRE);
        testEvent.setEventDate(LocalDate.of(2025, 1, 15));
        testEvent.setEffectiveDate(LocalDate.of(2025, 1, 15));
        testEvent.setDescription("New hire");
        testEvent.setCreatedAt(LocalDateTime.now());

        testRequest = new EmploymentEventRequest();
        testRequest.setEmployeeId(1L);
        testRequest.setEventType(EmploymentEventType.HIRE);
        testRequest.setEventDate(LocalDate.of(2025, 1, 15));
        testRequest.setEffectiveDate(LocalDate.of(2025, 1, 15));
        testRequest.setDescription("New hire");
    }

    @Test
    void createEvent_ValidRequest_ReturnsEventResponse() {
        // Given
        when(employeeService.findEmployeeById(1L)).thenReturn(testEmployee);
        when(eventRepository.save(any(EmploymentEvent.class))).thenReturn(testEvent);

        // When
        EmploymentEventResponse result = employmentEventService.createEvent(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(EmploymentEventType.HIRE);
        assertThat(result.getEmployeeName()).isEqualTo("Thabo Mokoena");
        verify(eventRepository, times(1)).save(any(EmploymentEvent.class));
        verify(auditLogService, times(1)).logSystemAction(
                eq("EMPLOYMENT_EVENT_CREATED"), eq("EMPLOYMENT_EVENT"), anyString());
    }

    @Test
    void getEmployeeHistory_ReturnsEventsInOrder() {
        // Given
        EmploymentEvent promotionEvent = new EmploymentEvent();
        promotionEvent.setId(2L);
        promotionEvent.setEmployee(testEmployee);
        promotionEvent.setEventType(EmploymentEventType.PROMOTION);
        promotionEvent.setEventDate(LocalDate.of(2025, 6, 1));
        promotionEvent.setEffectiveDate(LocalDate.of(2025, 6, 1));
        promotionEvent.setCreatedAt(LocalDateTime.now());

        when(eventRepository.findByEmployeeIdOrderByEventDateDesc(1L))
                .thenReturn(List.of(promotionEvent, testEvent));

        // When
        List<EmploymentEventResponse> history = employmentEventService.getEmployeeHistory(1L);

        // Then
        assertThat(history).hasSize(2);
        assertThat(history.get(0).getEventType()).isEqualTo(EmploymentEventType.PROMOTION);
        assertThat(history.get(1).getEventType()).isEqualTo(EmploymentEventType.HIRE);
    }

    @Test
    void getEvent_ExistingId_ReturnsEvent() {
        // Given
        when(eventRepository.findById(1L)).thenReturn(Optional.of(testEvent));

        // When
        EmploymentEventResponse result = employmentEventService.getEvent(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEventType()).isEqualTo(EmploymentEventType.HIRE);
    }

    @Test
    void getEvent_NonExistingId_ThrowsIllegalArgumentException() {
        // Given
        when(eventRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> employmentEventService.getEvent(999L)
        );
    }
}
