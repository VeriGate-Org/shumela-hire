package com.arthmatic.shumelahire.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "employees")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Employee extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee number is required")
    @Column(name = "employee_number", nullable = false, unique = true, length = 50)
    private String employeeNumber;

    @Column(length = 20)
    private String title;

    @NotBlank(message = "First name is required")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "preferred_name", length = 100)
    private String preferredName;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Column(nullable = false, length = 255)
    private String email;

    @Column(name = "personal_email", length = 255)
    private String personalEmail;

    @Column(length = 20)
    private String phone;

    @Column(name = "mobile_phone", length = 20)
    private String mobilePhone;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(length = 20)
    private String gender;

    @Column(name = "marital_status", length = 20)
    private String maritalStatus;

    @Column(length = 100)
    private String nationality;

    // Encrypted PII fields — stored encrypted via DataEncryptionService
    @Column(name = "id_number", length = 500)
    private String idNumber;

    @Column(name = "tax_number", length = 500)
    private String taxNumber;

    @Column(name = "passport_number", length = 500)
    private String passportNumber;

    @Column(name = "bank_name", length = 255)
    private String bankName;

    @Column(name = "bank_branch_code", length = 255)
    private String bankBranchCode;

    @Column(name = "bank_account_number", length = 500)
    private String bankAccountNumber;

    @Column(name = "bank_account_type", length = 50)
    private String bankAccountType;

    // Address
    @Column(name = "physical_address", columnDefinition = "TEXT")
    private String physicalAddress;

    @Column(name = "postal_address", columnDefinition = "TEXT")
    private String postalAddress;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String province;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 100)
    private String country;

    // Employment details
    @Column(length = 100)
    private String department;

    @Column(length = 100)
    private String division;

    @Column(name = "job_title", length = 255)
    private String jobTitle;

    @Column(name = "job_grade", length = 50)
    private String jobGrade;

    @Column(name = "cost_centre", length = 100)
    private String costCentre;

    @Column(length = 255)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private EmployeeEmploymentType employmentType = EmployeeEmploymentType.PERMANENT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EmployeeStatus status = EmployeeStatus.ACTIVE;

    @NotNull(message = "Hire date is required")
    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "probation_end_date")
    private LocalDate probationEndDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "termination_reason", columnDefinition = "TEXT")
    private String terminationReason;

    // Compensation
    @Column(precision = 15, scale = 2)
    private BigDecimal salary;

    @Column(name = "salary_currency", length = 3)
    private String salaryCurrency = "ZAR";

    @Column(name = "pay_frequency", length = 20)
    private String payFrequency = "MONTHLY";

    // Org hierarchy
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_manager_id")
    private Employee reportingManager;

    @Column(name = "reporting_manager_id", insertable = false, updatable = false)
    private Long reportingManagerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    @Column(name = "applicant_id", insertable = false, updatable = false)
    private Long applicantId;

    @Column(name = "user_id")
    private Long userId;

    // Employment equity
    @Column(length = 50)
    private String race;

    @Column(name = "disability_status", length = 50)
    private String disabilityStatus;

    @Column(name = "citizenship_status", length = 50)
    private String citizenshipStatus;

    // Emergency contact
    @Column(name = "emergency_contact_name", length = 200)
    private String emergencyContactName;

    @Column(name = "emergency_contact_phone", length = 20)
    private String emergencyContactPhone;

    @Column(name = "emergency_contact_relationship", length = 50)
    private String emergencyContactRelationship;

    // Metadata
    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "profile_photo_url", length = 500)
    private String profilePhotoUrl;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Relationships
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmployeeDocument> documents = new ArrayList<>();

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<EmploymentEvent> employmentEvents = new ArrayList<>();

    @OneToMany(mappedBy = "reportingManager", fetch = FetchType.LAZY)
    private List<Employee> directReports = new ArrayList<>();

    // Constructors
    public Employee() {}

    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getDisplayName() {
        return preferredName != null ? preferredName + " " + lastName : getFullName();
    }

    public boolean isActive() {
        return status == EmployeeStatus.ACTIVE || status == EmployeeStatus.PROBATION;
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

    public Employee getReportingManager() { return reportingManager; }
    public void setReportingManager(Employee reportingManager) { this.reportingManager = reportingManager; }

    public Long getReportingManagerId() { return reportingManagerId; }

    public Applicant getApplicant() { return applicant; }
    public void setApplicant(Applicant applicant) { this.applicant = applicant; }

    public Long getApplicantId() { return applicantId; }

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

    public List<EmployeeDocument> getDocuments() { return documents; }
    public void setDocuments(List<EmployeeDocument> documents) { this.documents = documents; }

    public List<EmploymentEvent> getEmploymentEvents() { return employmentEvents; }
    public void setEmploymentEvents(List<EmploymentEvent> employmentEvents) { this.employmentEvents = employmentEvents; }

    public List<Employee> getDirectReports() { return directReports; }
    public void setDirectReports(List<Employee> directReports) { this.directReports = directReports; }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                '}';
    }
}
