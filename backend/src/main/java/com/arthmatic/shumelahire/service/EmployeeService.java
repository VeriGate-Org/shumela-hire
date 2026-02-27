package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmployeeCreateRequest;
import com.arthmatic.shumelahire.dto.EmployeeResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DataEncryptionService encryptionService;

    @Autowired
    private AuditLogService auditLogService;

    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        logger.info("Creating new employee: {} {}", request.getFirstName(), request.getLastName());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Employee employee = new Employee();
        mapRequestToEntity(request, employee);

        // Generate employee number if not provided
        if (employee.getEmployeeNumber() == null || employee.getEmployeeNumber().isBlank()) {
            employee.setEmployeeNumber(generateEmployeeNumber());
        }

        // Encrypt PII fields
        encryptPiiFields(employee);

        Employee saved = employeeRepository.save(employee);

        auditLogService.logSystemAction("EMPLOYEE_CREATED", "EMPLOYEE",
                "Employee created: " + saved.getEmployeeNumber() + " - " + saved.getFullName());

        logger.info("Employee created with ID: {} ({})", saved.getId(), saved.getEmployeeNumber());

        // Decrypt for response
        decryptPiiFields(saved);
        return EmployeeResponse.fromEntity(saved);
    }

    @CacheEvict(value = "employees", key = "#id")
    public EmployeeResponse updateEmployee(Long id, EmployeeCreateRequest request) {
        logger.info("Updating employee: {}", id);

        Employee employee = findEmployeeById(id);

        // Check email uniqueness if changed
        if (!employee.getEmail().equals(request.getEmail()) &&
                employeeRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        mapRequestToEntity(request, employee);
        encryptPiiFields(employee);

        Employee updated = employeeRepository.save(employee);

        auditLogService.logSystemAction("EMPLOYEE_UPDATED", "EMPLOYEE",
                "Employee updated: " + updated.getEmployeeNumber());

        logger.info("Employee updated: {}", updated.getId());

        decryptPiiFields(updated);
        return EmployeeResponse.fromEntity(updated);
    }

    @Cacheable(value = "employees", key = "#id")
    @Transactional(readOnly = true)
    public EmployeeResponse getEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        decryptPiiFields(employee);
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeByNumber(String employeeNumber) {
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + employeeNumber));
        decryptPiiFields(employee);
        return EmployeeResponse.fromEntity(employee);
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> searchEmployees(String searchTerm, Pageable pageable) {
        Page<Employee> employees;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            employees = employeeRepository.findBySearchTerm(searchTerm, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }
        return employees.map(e -> {
            decryptPiiFields(e);
            return EmployeeResponse.fromEntity(e);
        });
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> filterEmployees(String department, EmployeeStatus status,
                                                   String location, String jobTitle,
                                                   String searchTerm, Pageable pageable) {
        Page<Employee> employees = employeeRepository.findByFilters(
                department, status, location, jobTitle, searchTerm, pageable);
        return employees.map(e -> {
            decryptPiiFields(e);
            return EmployeeResponse.fromEntity(e);
        });
    }

    @Transactional(readOnly = true)
    public Page<EmployeeResponse> getDirectory(Pageable pageable) {
        Page<Employee> employees = employeeRepository.findActiveEmployees(pageable);
        return employees.map(EmployeeResponse::directoryView);
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> getDirectReports(Long managerId) {
        List<Employee> reports = employeeRepository.findByReportingManagerId(managerId);
        return reports.stream()
                .map(EmployeeResponse::directoryView)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Object[]> getHeadcountByDepartment() {
        return employeeRepository.countByDepartment();
    }

    @Transactional(readOnly = true)
    public List<Object[]> getHeadcountByStatus() {
        return employeeRepository.countByStatus();
    }

    @Transactional(readOnly = true)
    public List<String> getDepartments() {
        return employeeRepository.findDistinctDepartments();
    }

    @Transactional(readOnly = true)
    public List<String> getLocations() {
        return employeeRepository.findDistinctLocations();
    }

    @Transactional(readOnly = true)
    public List<String> getJobTitles() {
        return employeeRepository.findDistinctJobTitles();
    }

    @CacheEvict(value = "employees", key = "#id")
    public void deleteEmployee(Long id) {
        Employee employee = findEmployeeById(id);
        employeeRepository.delete(employee);

        auditLogService.logSystemAction("EMPLOYEE_DELETED", "EMPLOYEE",
                "Employee deleted: " + employee.getEmployeeNumber());

        logger.info("Employee deleted: {}", id);
    }

    // --- Helper methods ---

    Employee findEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + id));
    }

    private String generateEmployeeNumber() {
        String year = String.valueOf(Year.now().getValue());
        String prefix = "UTW-" + year + "-";
        long count = employeeRepository.countByEmployeeNumberPrefix(prefix + "%");
        return prefix + String.format("%04d", count + 1);
    }

    private void mapRequestToEntity(EmployeeCreateRequest request, Employee employee) {
        employee.setTitle(request.getTitle());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setPreferredName(request.getPreferredName());
        employee.setEmail(request.getEmail());
        employee.setPersonalEmail(request.getPersonalEmail());
        employee.setPhone(request.getPhone());
        employee.setMobilePhone(request.getMobilePhone());
        employee.setDateOfBirth(request.getDateOfBirth());
        employee.setGender(request.getGender());
        employee.setMaritalStatus(request.getMaritalStatus());
        employee.setNationality(request.getNationality());

        // PII — store raw values; encryption happens separately
        employee.setIdNumber(request.getIdNumber());
        employee.setTaxNumber(request.getTaxNumber());
        employee.setPassportNumber(request.getPassportNumber());
        employee.setBankName(request.getBankName());
        employee.setBankBranchCode(request.getBankBranchCode());
        employee.setBankAccountNumber(request.getBankAccountNumber());
        employee.setBankAccountType(request.getBankAccountType());

        // Address
        employee.setPhysicalAddress(request.getPhysicalAddress());
        employee.setPostalAddress(request.getPostalAddress());
        employee.setCity(request.getCity());
        employee.setProvince(request.getProvince());
        employee.setPostalCode(request.getPostalCode());
        employee.setCountry(request.getCountry());

        // Employment
        employee.setDepartment(request.getDepartment());
        employee.setDivision(request.getDivision());
        employee.setJobTitle(request.getJobTitle());
        employee.setJobGrade(request.getJobGrade());
        employee.setCostCentre(request.getCostCentre());
        employee.setLocation(request.getLocation());
        if (request.getEmploymentType() != null) {
            employee.setEmploymentType(request.getEmploymentType());
        }
        if (request.getStatus() != null) {
            employee.setStatus(request.getStatus());
        }
        employee.setHireDate(request.getHireDate());
        employee.setProbationEndDate(request.getProbationEndDate());

        // Compensation
        employee.setSalary(request.getSalary());
        if (request.getSalaryCurrency() != null) {
            employee.setSalaryCurrency(request.getSalaryCurrency());
        }
        if (request.getPayFrequency() != null) {
            employee.setPayFrequency(request.getPayFrequency());
        }

        // Org hierarchy
        if (request.getReportingManagerId() != null) {
            Employee manager = findEmployeeById(request.getReportingManagerId());
            employee.setReportingManager(manager);
        }
        employee.setUserId(request.getUserId());

        // Employment equity
        employee.setRace(request.getRace());
        employee.setDisabilityStatus(request.getDisabilityStatus());
        employee.setCitizenshipStatus(request.getCitizenshipStatus());

        // Emergency contact
        employee.setEmergencyContactName(request.getEmergencyContactName());
        employee.setEmergencyContactPhone(request.getEmergencyContactPhone());
        employee.setEmergencyContactRelationship(request.getEmergencyContactRelationship());

        employee.setNotes(request.getNotes());
    }

    private void encryptPiiFields(Employee employee) {
        employee.setIdNumber(encryptionService.encryptPII(employee.getIdNumber()));
        employee.setTaxNumber(encryptionService.encryptPII(employee.getTaxNumber()));
        employee.setPassportNumber(encryptionService.encryptPII(employee.getPassportNumber()));
        employee.setBankAccountNumber(encryptionService.encryptPII(employee.getBankAccountNumber()));
    }

    private void decryptPiiFields(Employee employee) {
        employee.setIdNumber(encryptionService.decryptPII(employee.getIdNumber()));
        employee.setTaxNumber(encryptionService.decryptPII(employee.getTaxNumber()));
        employee.setPassportNumber(encryptionService.decryptPII(employee.getPassportNumber()));
        employee.setBankAccountNumber(encryptionService.decryptPII(employee.getBankAccountNumber()));
    }
}
