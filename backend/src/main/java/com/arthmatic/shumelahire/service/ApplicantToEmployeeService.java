package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.EmployeeCreateRequest;
import com.arthmatic.shumelahire.dto.EmployeeResponse;
import com.arthmatic.shumelahire.dto.EmploymentEventRequest;
import com.arthmatic.shumelahire.entity.Applicant;
import com.arthmatic.shumelahire.entity.EmployeeEmploymentType;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.entity.EmploymentEventType;
import com.arthmatic.shumelahire.repository.ApplicantRepository;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class ApplicantToEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicantToEmployeeService.class);

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmploymentEventService employmentEventService;

    @Autowired
    private AuditLogService auditLogService;

    public EmployeeResponse convertApplicantToEmployee(Long applicantId, LocalDate hireDate,
                                                        String department, String jobTitle,
                                                        Long reportingManagerId,
                                                        EmployeeEmploymentType employmentType) {
        logger.info("Converting applicant {} to employee", applicantId);

        Applicant applicant = applicantRepository.findById(applicantId)
                .orElseThrow(() -> new IllegalArgumentException("Applicant not found: " + applicantId));

        // Check if applicant already converted
        if (employeeRepository.findByApplicantId(applicantId).isPresent()) {
            throw new IllegalArgumentException("Applicant already converted to employee: " + applicantId);
        }

        // Build employee create request from applicant data
        EmployeeCreateRequest request = new EmployeeCreateRequest();
        request.setFirstName(applicant.getName());
        request.setLastName(applicant.getSurname());
        request.setEmail(applicant.getEmail());
        request.setPhone(applicant.getPhone());
        request.setIdNumber(applicant.getIdPassportNumber());
        request.setPhysicalAddress(applicant.getAddress());
        request.setHireDate(hireDate);
        request.setDepartment(department);
        request.setJobTitle(jobTitle);
        request.setReportingManagerId(reportingManagerId);
        request.setEmploymentType(employmentType != null ? employmentType : EmployeeEmploymentType.PERMANENT);
        request.setStatus(EmployeeStatus.PROBATION);
        request.setApplicantId(applicantId);

        // Carry over employment equity data
        request.setGender(applicant.getGender());
        request.setRace(applicant.getRace());
        request.setDisabilityStatus(applicant.getDisabilityStatus());
        request.setCitizenshipStatus(applicant.getCitizenshipStatus());

        EmployeeResponse employee = employeeService.createEmployee(request);

        // Create HIRE employment event
        EmploymentEventRequest eventRequest = new EmploymentEventRequest();
        eventRequest.setEmployeeId(employee.getId());
        eventRequest.setEventType(EmploymentEventType.HIRE);
        eventRequest.setEventDate(hireDate);
        eventRequest.setEffectiveDate(hireDate);
        eventRequest.setDescription("Employee hired — converted from applicant #" + applicantId);
        eventRequest.setNewValue("{\"department\":\"" + department + "\",\"jobTitle\":\"" + jobTitle + "\"}");

        employmentEventService.createEvent(eventRequest);

        auditLogService.logSystemAction("APPLICANT_CONVERTED", "EMPLOYEE",
                "Applicant " + applicantId + " converted to employee " + employee.getEmployeeNumber());

        logger.info("Applicant {} converted to employee {} ({})",
                applicantId, employee.getId(), employee.getEmployeeNumber());

        return employee;
    }
}
