package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    Optional<Employee> findByApplicantId(Long applicantId);

    // Search by name, email, employee number, department, job title
    @Query("SELECT e FROM Employee e WHERE " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.employeeNumber) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.department) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%'))")
    Page<Employee> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    // Filter by department
    Page<Employee> findByDepartment(String department, Pageable pageable);

    // Filter by status
    Page<Employee> findByStatus(EmployeeStatus status, Pageable pageable);

    // Filter by department and status
    Page<Employee> findByDepartmentAndStatus(String department, EmployeeStatus status, Pageable pageable);

    // Filter by job title
    Page<Employee> findByJobTitleContainingIgnoreCase(String jobTitle, Pageable pageable);

    // Filter by location
    Page<Employee> findByLocation(String location, Pageable pageable);

    // Directory query — limited fields for listing
    @Query("SELECT e FROM Employee e WHERE e.status IN ('ACTIVE', 'PROBATION') ORDER BY e.lastName, e.firstName")
    Page<Employee> findActiveEmployees(Pageable pageable);

    // Direct reports for a manager
    List<Employee> findByReportingManagerId(Long managerId);

    // Count by department
    @Query("SELECT e.department, COUNT(e) FROM Employee e WHERE e.status IN ('ACTIVE', 'PROBATION') GROUP BY e.department")
    List<Object[]> countByDepartment();

    // Count by status
    @Query("SELECT e.status, COUNT(e) FROM Employee e GROUP BY e.status")
    List<Object[]> countByStatus();

    // Find employees with expiring probation
    @Query("SELECT e FROM Employee e WHERE e.status = 'PROBATION' AND e.probationEndDate <= CURRENT_DATE")
    List<Employee> findExpiredProbations();

    // Get distinct departments
    @Query("SELECT DISTINCT e.department FROM Employee e WHERE e.department IS NOT NULL ORDER BY e.department")
    List<String> findDistinctDepartments();

    // Get distinct locations
    @Query("SELECT DISTINCT e.location FROM Employee e WHERE e.location IS NOT NULL ORDER BY e.location")
    List<String> findDistinctLocations();

    // Get distinct job titles
    @Query("SELECT DISTINCT e.jobTitle FROM Employee e WHERE e.jobTitle IS NOT NULL ORDER BY e.jobTitle")
    List<String> findDistinctJobTitles();

    // Advanced search with multiple filters
    @Query("SELECT e FROM Employee e WHERE " +
           "(:department IS NULL OR e.department = :department) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "(:location IS NULL OR e.location = :location) AND " +
           "(:jobTitle IS NULL OR LOWER(e.jobTitle) LIKE LOWER(CONCAT('%', CAST(:jobTitle AS string), '%'))) AND " +
           "(:searchTerm IS NULL OR " +
           "LOWER(e.firstName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.lastName) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')) OR " +
           "LOWER(e.email) LIKE LOWER(CONCAT('%', CAST(:searchTerm AS string), '%')))")
    Page<Employee> findByFilters(
            @Param("department") String department,
            @Param("status") EmployeeStatus status,
            @Param("location") String location,
            @Param("jobTitle") String jobTitle,
            @Param("searchTerm") String searchTerm,
            Pageable pageable);

    // Sequence for employee number generation
    @Query(value = "SELECT COUNT(*) FROM employees WHERE employee_number LIKE :prefix", nativeQuery = true)
    long countByEmployeeNumberPrefix(@Param("prefix") String prefix);
}
