package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmployeeCreateRequest;
import com.arthmatic.shumelahire.dto.EmployeeResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeEmploymentType;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DataEncryptionService encryptionService;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee testEmployee;
    private EmployeeCreateRequest testRequest;

    @BeforeEach
    void setUp() {
        testEmployee = new Employee();
        testEmployee.setId(1L);
        testEmployee.setEmployeeNumber("UTW-2026-0001");
        testEmployee.setFirstName("Thabo");
        testEmployee.setLastName("Mokoena");
        testEmployee.setEmail("thabo.mokoena@company.co.za");
        testEmployee.setPhone("+27821234567");
        testEmployee.setDepartment("Engineering");
        testEmployee.setJobTitle("Software Developer");
        testEmployee.setStatus(EmployeeStatus.ACTIVE);
        testEmployee.setEmploymentType(EmployeeEmploymentType.PERMANENT);
        testEmployee.setHireDate(LocalDate.of(2025, 1, 15));
        testEmployee.setSalary(new BigDecimal("45000.00"));
        testEmployee.setSalaryCurrency("ZAR");
        testEmployee.setLocation("Johannesburg");
        testEmployee.setCreatedAt(LocalDateTime.now());
        testEmployee.setUpdatedAt(LocalDateTime.now());

        testRequest = new EmployeeCreateRequest();
        testRequest.setFirstName("Thabo");
        testRequest.setLastName("Mokoena");
        testRequest.setEmail("thabo.mokoena@company.co.za");
        testRequest.setPhone("+27821234567");
        testRequest.setDepartment("Engineering");
        testRequest.setJobTitle("Software Developer");
        testRequest.setHireDate(LocalDate.of(2025, 1, 15));
        testRequest.setEmploymentType(EmployeeEmploymentType.PERMANENT);
        testRequest.setStatus(EmployeeStatus.ACTIVE);
        testRequest.setSalary(new BigDecimal("45000.00"));
        testRequest.setLocation("Johannesburg");
    }

    @Test
    void createEmployee_ValidRequest_ReturnsEmployeeResponse() {
        // Given
        when(employeeRepository.existsByEmail(testRequest.getEmail())).thenReturn(false);
        when(employeeRepository.countByEmployeeNumberPrefix(anyString())).thenReturn(0L);
        when(encryptionService.encryptPII(any())).thenReturn(null);
        when(encryptionService.decryptPII(any())).thenReturn(null);
        when(employeeRepository.save(any(Employee.class))).thenReturn(testEmployee);

        // When
        EmployeeResponse result = employeeService.createEmployee(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Thabo");
        assertThat(result.getLastName()).isEqualTo("Mokoena");
        assertThat(result.getEmail()).isEqualTo("thabo.mokoena@company.co.za");
        assertThat(result.getDepartment()).isEqualTo("Engineering");
        assertThat(result.getJobTitle()).isEqualTo("Software Developer");
        verify(employeeRepository, times(1)).existsByEmail(testRequest.getEmail());
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(auditLogService, times(1)).logSystemAction(eq("EMPLOYEE_CREATED"), eq("EMPLOYEE"), anyString());
    }

    @Test
    void createEmployee_EmailAlreadyExists_ThrowsIllegalArgumentException() {
        // Given
        when(employeeRepository.existsByEmail(testRequest.getEmail())).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.createEmployee(testRequest)
        );

        assertThat(exception.getMessage()).contains("Email already exists");
        verify(employeeRepository, times(1)).existsByEmail(testRequest.getEmail());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void getEmployee_ExistingId_ReturnsEmployeeResponse() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(encryptionService.decryptPII(any())).thenReturn(null);

        // When
        EmployeeResponse result = employeeService.getEmployee(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmployeeNumber()).isEqualTo("UTW-2026-0001");
        assertThat(result.getFullName()).isEqualTo("Thabo Mokoena");
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    void getEmployee_NonExistingId_ThrowsIllegalArgumentException() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.getEmployee(999L)
        );
        verify(employeeRepository, times(1)).findById(999L);
    }

    @Test
    void updateEmployee_ValidRequest_ReturnsUpdatedResponse() {
        // Given
        EmployeeCreateRequest updateRequest = new EmployeeCreateRequest();
        updateRequest.setFirstName("Thabo");
        updateRequest.setLastName("Mokoena");
        updateRequest.setEmail("thabo.mokoena@company.co.za");
        updateRequest.setJobTitle("Senior Software Developer");
        updateRequest.setHireDate(LocalDate.of(2025, 1, 15));
        updateRequest.setDepartment("Engineering");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setEmployeeNumber("UTW-2026-0001");
        updatedEmployee.setFirstName("Thabo");
        updatedEmployee.setLastName("Mokoena");
        updatedEmployee.setEmail("thabo.mokoena@company.co.za");
        updatedEmployee.setJobTitle("Senior Software Developer");
        updatedEmployee.setHireDate(LocalDate.of(2025, 1, 15));
        updatedEmployee.setStatus(EmployeeStatus.ACTIVE);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(encryptionService.encryptPII(any())).thenReturn(null);
        when(encryptionService.decryptPII(any())).thenReturn(null);
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // When
        EmployeeResponse result = employeeService.updateEmployee(1L, updateRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getJobTitle()).isEqualTo("Senior Software Developer");
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(auditLogService, times(1)).logSystemAction(eq("EMPLOYEE_UPDATED"), eq("EMPLOYEE"), anyString());
    }

    @Test
    void updateEmployee_EmailConflict_ThrowsIllegalArgumentException() {
        // Given
        EmployeeCreateRequest updateRequest = new EmployeeCreateRequest();
        updateRequest.setFirstName("Thabo");
        updateRequest.setLastName("Mokoena");
        updateRequest.setEmail("conflicting@company.co.za");
        updateRequest.setHireDate(LocalDate.of(2025, 1, 15));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));
        when(employeeRepository.existsByEmail("conflicting@company.co.za")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.updateEmployee(1L, updateRequest)
        );

        assertThat(exception.getMessage()).contains("Email already exists");
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void searchEmployees_WithSearchTerm_ReturnsFilteredResults() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee));
        when(employeeRepository.findBySearchTerm("Thabo", pageable)).thenReturn(employeePage);
        when(encryptionService.decryptPII(any())).thenReturn(null);

        // When
        Page<EmployeeResponse> results = employeeService.searchEmployees("Thabo", pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getFirstName()).isEqualTo("Thabo");
        verify(employeeRepository, times(1)).findBySearchTerm("Thabo", pageable);
    }

    @Test
    void searchEmployees_NoSearchTerm_ReturnsAll() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee));
        when(employeeRepository.findAll(pageable)).thenReturn(employeePage);
        when(encryptionService.decryptPII(any())).thenReturn(null);

        // When
        Page<EmployeeResponse> results = employeeService.searchEmployees(null, pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    void getDirectory_ReturnsActiveEmployees() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        Page<Employee> employeePage = new PageImpl<>(List.of(testEmployee));
        when(employeeRepository.findActiveEmployees(pageable)).thenReturn(employeePage);

        // When
        Page<EmployeeResponse> results = employeeService.getDirectory(pageable);

        // Then
        assertThat(results.getContent()).hasSize(1);
        verify(employeeRepository, times(1)).findActiveEmployees(pageable);
    }

    @Test
    void getDirectReports_ReturnsList() {
        // Given
        when(employeeRepository.findByReportingManagerId(1L)).thenReturn(List.of(testEmployee));

        // When
        List<EmployeeResponse> reports = employeeService.getDirectReports(1L);

        // Then
        assertThat(reports).hasSize(1);
        verify(employeeRepository, times(1)).findByReportingManagerId(1L);
    }

    @Test
    void deleteEmployee_ExistingId_DeletesSuccessfully() {
        // Given
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(testEmployee));

        // When
        employeeService.deleteEmployee(1L);

        // Then
        verify(employeeRepository, times(1)).delete(testEmployee);
        verify(auditLogService, times(1)).logSystemAction(eq("EMPLOYEE_DELETED"), eq("EMPLOYEE"), anyString());
    }

    @Test
    void deleteEmployee_NonExistingId_ThrowsIllegalArgumentException() {
        // Given
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                IllegalArgumentException.class,
                () -> employeeService.deleteEmployee(999L)
        );
        verify(employeeRepository, never()).delete(any(Employee.class));
    }
}
