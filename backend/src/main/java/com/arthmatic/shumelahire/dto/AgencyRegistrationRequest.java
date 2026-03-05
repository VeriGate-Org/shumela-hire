package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AgencyRegistrationRequest {

    @NotBlank(message = "Agency name is required")
    @Size(max = 200)
    private String agencyName;

    @Size(max = 50)
    private String registrationNumber;

    @NotBlank(message = "Contact person name is required")
    @Size(max = 100)
    private String contactPerson;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Valid email is required")
    @Size(max = 100)
    private String contactEmail;

    @Size(max = 20)
    private String contactPhone;

    private String specializations;

    private Integer beeLevel;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 255, message = "Password must be at least 8 characters")
    private String password;

    public AgencyRegistrationRequest() {}

    public String getAgencyName() { return agencyName; }
    public void setAgencyName(String agencyName) { this.agencyName = agencyName; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getSpecializations() { return specializations; }
    public void setSpecializations(String specializations) { this.specializations = specializations; }

    public Integer getBeeLevel() { return beeLevel; }
    public void setBeeLevel(Integer beeLevel) { this.beeLevel = beeLevel; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
