package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeEmploymentType;
import com.arthmatic.shumelahire.entity.EmployeeStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeResponse {

    private Long id;
    private String employeeNumber;
    private String title;
    private String firstName;
    private String lastName;
    private String preferredName;
    private String fullName;
    private String displayName;
    private String email;
    private String personalEmail;
    private String phone;
    private String mobilePhone;
    private LocalDate dateOfBirth;
    private String gender;
    private String maritalStatus;
    private String nationality;

    // PII — decrypted for authorized viewers
    private String idNumber;
    private String taxNumber;
    private String passportNumber;
    private String bankName;
    private String bankBranchCode;
    private String bankAccountNumber;
    private String bankAccountType;

    // Address
    private String physicalAddress;
    private String postalAddress;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    // Employment details
    private String department;
    private String division;
    private String jobTitle;
    private String jobGrade;
    private String costCentre;
    private String location;
    private EmployeeEmploymentType employmentType;
    private EmployeeStatus status;
    private LocalDate hireDate;
    private LocalDate probationEndDate;
    private LocalDate terminationDate;
    private String terminationReason;

    // Compensation
    private BigDecimal salary;
    private String salaryCurrency;
    private String payFrequency;

    // Org hierarchy
    private Long reportingManagerId;
    private String reportingManagerName;
    private Long applicantId;
    private Long userId;

    // Employment equity
    private String race;
    private String disabilityStatus;
    private String citizenshipStatus;

    // Emergency contact
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String emergencyContactRelationship;

    private String notes;
    private String profilePhotoUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public EmployeeResponse() {}

    public EmployeeResponse(Employee employee) {
        this.id = employee.getId();
        this.employeeNumber = employee.getEmployeeNumber();
        this.title = employee.getTitle();
        this.firstName = employee.getFirstName();
        this.lastName = employee.getLastName();
        this.preferredName = employee.getPreferredName();
        this.fullName = employee.getFullName();
        this.displayName = employee.getDisplayName();
        this.email = employee.getEmail();
        this.personalEmail = employee.getPersonalEmail();
        this.phone = employee.getPhone();
        this.mobilePhone = employee.getMobilePhone();
        this.dateOfBirth = employee.getDateOfBirth();
        this.gender = employee.getGender();
        this.maritalStatus = employee.getMaritalStatus();
        this.nationality = employee.getNationality();

        // PII fields (already decrypted by service layer)
        this.idNumber = employee.getIdNumber();
        this.taxNumber = employee.getTaxNumber();
        this.passportNumber = employee.getPassportNumber();
        this.bankName = employee.getBankName();
        this.bankBranchCode = employee.getBankBranchCode();
        this.bankAccountNumber = employee.getBankAccountNumber();
        this.bankAccountType = employee.getBankAccountType();

        // Address
        this.physicalAddress = employee.getPhysicalAddress();
        this.postalAddress = employee.getPostalAddress();
        this.city = employee.getCity();
        this.province = employee.getProvince();
        this.postalCode = employee.getPostalCode();
        this.country = employee.getCountry();

        // Employment
        this.department = employee.getDepartment();
        this.division = employee.getDivision();
        this.jobTitle = employee.getJobTitle();
        this.jobGrade = employee.getJobGrade();
        this.costCentre = employee.getCostCentre();
        this.location = employee.getLocation();
        this.employmentType = employee.getEmploymentType();
        this.status = employee.getStatus();
        this.hireDate = employee.getHireDate();
        this.probationEndDate = employee.getProbationEndDate();
        this.terminationDate = employee.getTerminationDate();
        this.terminationReason = employee.getTerminationReason();

        // Compensation
        this.salary = employee.getSalary();
        this.salaryCurrency = employee.getSalaryCurrency();
        this.payFrequency = employee.getPayFrequency();

        // Org hierarchy
        this.reportingManagerId = employee.getReportingManagerId();
        if (employee.getReportingManager() != null) {
            this.reportingManagerName = employee.getReportingManager().getFullName();
        }
        this.applicantId = employee.getApplicantId();
        this.userId = employee.getUserId();

        // Employment equity
        this.race = employee.getRace();
        this.disabilityStatus = employee.getDisabilityStatus();
        this.citizenshipStatus = employee.getCitizenshipStatus();

        // Emergency contact
        this.emergencyContactName = employee.getEmergencyContactName();
        this.emergencyContactPhone = employee.getEmergencyContactPhone();
        this.emergencyContactRelationship = employee.getEmergencyContactRelationship();

        this.notes = employee.getNotes();
        this.profilePhotoUrl = employee.getProfilePhotoUrl();
        this.createdAt = employee.getCreatedAt();
        this.updatedAt = employee.getUpdatedAt();
    }

    public static EmployeeResponse fromEntity(Employee employee) {
        return new EmployeeResponse(employee);
    }

    // Directory-level response (limited fields)
    public static EmployeeResponse directoryView(Employee employee) {
        EmployeeResponse response = new EmployeeResponse();
        response.id = employee.getId();
        response.employeeNumber = employee.getEmployeeNumber();
        response.firstName = employee.getFirstName();
        response.lastName = employee.getLastName();
        response.preferredName = employee.getPreferredName();
        response.fullName = employee.getFullName();
        response.displayName = employee.getDisplayName();
        response.email = employee.getEmail();
        response.phone = employee.getPhone();
        response.department = employee.getDepartment();
        response.jobTitle = employee.getJobTitle();
        response.location = employee.getLocation();
        response.status = employee.getStatus();
        response.profilePhotoUrl = employee.getProfilePhotoUrl();
        response.reportingManagerId = employee.getReportingManagerId();
        if (employee.getReportingManager() != null) {
            response.reportingManagerName = employee.getReportingManager().getFullName();
        }
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmployeeNumber() { return employeeNumber; }
    public void setEmployeeNumber(String employeeNumber) { this.employeeNumber = employeeNumber; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPreferredName() { return preferredName; }
    public void setPreferredName(String preferredName) { this.preferredName = preferredName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPersonalEmail() { return personalEmail; }
    public void setPersonalEmail(String personalEmail) { this.personalEmail = personalEmail; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getMobilePhone() { return mobilePhone; }
    public void setMobilePhone(String mobilePhone) { this.mobilePhone = mobilePhone; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getBankBranchCode() { return bankBranchCode; }
    public void setBankBranchCode(String bankBranchCode) { this.bankBranchCode = bankBranchCode; }

    public String getBankAccountNumber() { return bankAccountNumber; }
    public void setBankAccountNumber(String bankAccountNumber) { this.bankAccountNumber = bankAccountNumber; }

    public String getBankAccountType() { return bankAccountType; }
    public void setBankAccountType(String bankAccountType) { this.bankAccountType = bankAccountType; }

    public String getPhysicalAddress() { return physicalAddress; }
    public void setPhysicalAddress(String physicalAddress) { this.physicalAddress = physicalAddress; }

    public String getPostalAddress() { return postalAddress; }
    public void setPostalAddress(String postalAddress) { this.postalAddress = postalAddress; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getDivision() { return division; }
    public void setDivision(String division) { this.division = division; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }

    public String getCostCentre() { return costCentre; }
    public void setCostCentre(String costCentre) { this.costCentre = costCentre; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public EmployeeEmploymentType getEmploymentType() { return employmentType; }
    public void setEmploymentType(EmployeeEmploymentType employmentType) { this.employmentType = employmentType; }

    public EmployeeStatus getStatus() { return status; }
    public void setStatus(EmployeeStatus status) { this.status = status; }

    public LocalDate getHireDate() { return hireDate; }
    public void setHireDate(LocalDate hireDate) { this.hireDate = hireDate; }

    public LocalDate getProbationEndDate() { return probationEndDate; }
    public void setProbationEndDate(LocalDate probationEndDate) { this.probationEndDate = probationEndDate; }

    public LocalDate getTerminationDate() { return terminationDate; }
    public void setTerminationDate(LocalDate terminationDate) { this.terminationDate = terminationDate; }

    public String getTerminationReason() { return terminationReason; }
    public void setTerminationReason(String terminationReason) { this.terminationReason = terminationReason; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public String getSalaryCurrency() { return salaryCurrency; }
    public void setSalaryCurrency(String salaryCurrency) { this.salaryCurrency = salaryCurrency; }

    public String getPayFrequency() { return payFrequency; }
    public void setPayFrequency(String payFrequency) { this.payFrequency = payFrequency; }

    public Long getReportingManagerId() { return reportingManagerId; }
    public void setReportingManagerId(Long reportingManagerId) { this.reportingManagerId = reportingManagerId; }

    public String getReportingManagerName() { return reportingManagerName; }
    public void setReportingManagerName(String reportingManagerName) { this.reportingManagerName = reportingManagerName; }

    public Long getApplicantId() { return applicantId; }
    public void setApplicantId(Long applicantId) { this.applicantId = applicantId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRace() { return race; }
    public void setRace(String race) { this.race = race; }

    public String getDisabilityStatus() { return disabilityStatus; }
    public void setDisabilityStatus(String disabilityStatus) { this.disabilityStatus = disabilityStatus; }

    public String getCitizenshipStatus() { return citizenshipStatus; }
    public void setCitizenshipStatus(String citizenshipStatus) { this.citizenshipStatus = citizenshipStatus; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getProfilePhotoUrl() { return profilePhotoUrl; }
    public void setProfilePhotoUrl(String profilePhotoUrl) { this.profilePhotoUrl = profilePhotoUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
