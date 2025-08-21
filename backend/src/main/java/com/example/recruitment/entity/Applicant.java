package com.example.recruitment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "applicants")
public class Applicant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Column(nullable = false, length = 100)
    private String name;
    
    @NotBlank(message = "Surname is required")
    @Column(nullable = false, length = 100)
    private String surname;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    @Column(nullable = false, unique = true, length = 255)
    private String email;
    
    @Column(length = 20)
    private String phone;
    
    @Column(name = "id_passport_number", length = 50)
    private String idPassportNumber;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    // JSON column for education list
    @Column(name = "education", columnDefinition = "TEXT")
    private String education;
    
    // JSON column for experience list
    @Column(name = "experience", columnDefinition = "TEXT")
    private String experience;
    
    // JSON column for skills tags
    @Column(name = "skills", columnDefinition = "TEXT")
    private String skills;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();
    
    // Constructors
    public Applicant() {}
    
    public Applicant(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSurname() {
        return surname;
    }
    
    public void setSurname(String surname) {
        this.surname = surname;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getIdPassportNumber() {
        return idPassportNumber;
    }
    
    public void setIdPassportNumber(String idPassportNumber) {
        this.idPassportNumber = idPassportNumber;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getEducation() {
        return education;
    }
    
    public void setEducation(String education) {
        this.education = education;
    }
    
    public String getExperience() {
        return experience;
    }
    
    public void setExperience(String experience) {
        this.experience = experience;
    }
    
    public String getSkills() {
        return skills;
    }
    
    public void setSkills(String skills) {
        this.skills = skills;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<Document> getDocuments() {
        return documents;
    }
    
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }
    
    // Helper methods
    public String getFullName() {
        return name + " " + surname;
    }
    
    @Override
    public String toString() {
        return "Applicant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}