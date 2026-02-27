package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.EmployeeStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class WorkforceAnalyticsService {

    private static final Logger logger = LoggerFactory.getLogger(WorkforceAnalyticsService.class);

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private PositionRepository positionRepository;

    public Map<String, Object> getDemographics() {
        logger.info("Computing workforce demographics");

        List<Employee> allEmployees = employeeRepository.findAll();
        List<Employee> active = allEmployees.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalEmployees", active.size());
        result.put("genderBreakdown", groupBy(active, Employee::getGender));
        result.put("raceBreakdown", groupBy(active, Employee::getRace));
        result.put("disabilityBreakdown", groupBy(active, Employee::getDisabilityStatus));
        result.put("ageGroups", computeAgeGroups(active));
        result.put("tenureGroups", computeTenureGroups(active));
        result.put("departmentBreakdown", groupBy(active, Employee::getDepartment));
        result.put("locationBreakdown", groupBy(active, Employee::getLocation));

        return result;
    }

    public Map<String, Object> getTurnoverAnalysis() {
        logger.info("Computing turnover analysis");

        List<Employee> allEmployees = employeeRepository.findAll();
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);

        long terminated = allEmployees.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.TERMINATED || e.getStatus() == EmployeeStatus.RESIGNED)
                .filter(e -> e.getTerminationDate() != null && e.getTerminationDate().isAfter(oneYearAgo))
                .count();

        long active = allEmployees.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .count();

        double turnoverRate = active > 0 ? (double) terminated / (active + terminated) * 100 : 0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("terminationsLastYear", terminated);
        result.put("activeEmployees", active);
        result.put("turnoverRatePercent", Math.round(turnoverRate * 100.0) / 100.0);
        result.put("terminationByDept", computeTerminationsByDept(allEmployees, oneYearAgo));

        return result;
    }

    public Map<String, Object> getSpanOfControl() {
        logger.info("Computing span of control");

        List<Employee> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> e.getStatus() == EmployeeStatus.ACTIVE)
                .collect(Collectors.toList());

        Map<Long, Long> directReportCounts = allEmployees.stream()
                .filter(e -> e.getReportingManager() != null)
                .collect(Collectors.groupingBy(
                        e -> e.getReportingManager().getId(),
                        Collectors.counting()
                ));

        long totalManagers = directReportCounts.size();
        double avgSpan = directReportCounts.values().stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0);

        Map<String, Long> spanBuckets = new LinkedHashMap<>();
        spanBuckets.put("1-3", directReportCounts.values().stream().filter(c -> c >= 1 && c <= 3).count());
        spanBuckets.put("4-6", directReportCounts.values().stream().filter(c -> c >= 4 && c <= 6).count());
        spanBuckets.put("7-10", directReportCounts.values().stream().filter(c -> c >= 7 && c <= 10).count());
        spanBuckets.put("11+", directReportCounts.values().stream().filter(c -> c > 10).count());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalManagers", totalManagers);
        result.put("averageSpanOfControl", Math.round(avgSpan * 100.0) / 100.0);
        result.put("spanDistribution", spanBuckets);
        result.put("totalEmployees", allEmployees.size());
        result.put("individualContributors", allEmployees.size() - (int) directReportCounts.entrySet().stream()
                .filter(e -> allEmployees.stream().anyMatch(emp -> emp.getId().equals(e.getKey())))
                .count());

        return result;
    }

    public Map<String, Object> getWorkforceCost() {
        logger.info("Computing workforce cost summary");

        List<Object[]> deptCounts = employeeRepository.countByDepartment();
        Map<String, Long> deptMap = new LinkedHashMap<>();
        for (Object[] row : deptCounts) {
            String dept = (String) row[0];
            Long count = (Long) row[1];
            if (dept != null) {
                deptMap.put(dept, count);
            }
        }

        long totalVacant = positionRepository.countVacantPositions();
        long totalPositions = positionRepository.countActivePositions();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("headcountByDepartment", deptMap);
        result.put("totalActivePositions", totalPositions);
        result.put("vacantPositions", totalVacant);
        result.put("filledPositions", totalPositions - totalVacant);

        return result;
    }

    // --- Helpers ---

    private Map<String, Long> groupBy(List<Employee> employees, java.util.function.Function<Employee, String> keyFn) {
        return employees.stream()
                .collect(Collectors.groupingBy(
                        e -> {
                            String val = keyFn.apply(e);
                            return val != null ? val : "Not specified";
                        },
                        Collectors.counting()
                ));
    }

    private Map<String, Long> computeAgeGroups(List<Employee> employees) {
        Map<String, Long> groups = new LinkedHashMap<>();
        groups.put("Under 25", 0L);
        groups.put("25-34", 0L);
        groups.put("35-44", 0L);
        groups.put("45-54", 0L);
        groups.put("55+", 0L);
        groups.put("Unknown", 0L);

        for (Employee e : employees) {
            if (e.getDateOfBirth() == null) {
                groups.merge("Unknown", 1L, Long::sum);
                continue;
            }
            int age = Period.between(e.getDateOfBirth(), LocalDate.now()).getYears();
            if (age < 25) groups.merge("Under 25", 1L, Long::sum);
            else if (age < 35) groups.merge("25-34", 1L, Long::sum);
            else if (age < 45) groups.merge("35-44", 1L, Long::sum);
            else if (age < 55) groups.merge("45-54", 1L, Long::sum);
            else groups.merge("55+", 1L, Long::sum);
        }
        return groups;
    }

    private Map<String, Long> computeTenureGroups(List<Employee> employees) {
        Map<String, Long> groups = new LinkedHashMap<>();
        groups.put("Less than 1 year", 0L);
        groups.put("1-3 years", 0L);
        groups.put("3-5 years", 0L);
        groups.put("5-10 years", 0L);
        groups.put("10+ years", 0L);

        for (Employee e : employees) {
            if (e.getHireDate() == null) continue;
            int years = Period.between(e.getHireDate(), LocalDate.now()).getYears();
            if (years < 1) groups.merge("Less than 1 year", 1L, Long::sum);
            else if (years < 3) groups.merge("1-3 years", 1L, Long::sum);
            else if (years < 5) groups.merge("3-5 years", 1L, Long::sum);
            else if (years < 10) groups.merge("5-10 years", 1L, Long::sum);
            else groups.merge("10+ years", 1L, Long::sum);
        }
        return groups;
    }

    private Map<String, Long> computeTerminationsByDept(List<Employee> allEmployees, LocalDate since) {
        return allEmployees.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.TERMINATED || e.getStatus() == EmployeeStatus.RESIGNED)
                .filter(e -> e.getTerminationDate() != null && e.getTerminationDate().isAfter(since))
                .collect(Collectors.groupingBy(
                        e -> e.getDepartment() != null ? e.getDepartment() : "Unknown",
                        Collectors.counting()
                ));
    }
}
