package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.HeadcountPlanRequest;
import com.arthmatic.shumelahire.dto.org.HeadcountPlanResponse;
import com.arthmatic.shumelahire.entity.HeadcountPlan;
import com.arthmatic.shumelahire.entity.OrgUnit;
import com.arthmatic.shumelahire.repository.HeadcountPlanRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class HeadcountPlanService {

    private static final Logger logger = LoggerFactory.getLogger(HeadcountPlanService.class);

    @Autowired
    private HeadcountPlanRepository headcountPlanRepository;

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    public HeadcountPlanResponse createHeadcountPlan(HeadcountPlanRequest request) {
        logger.info("Creating headcount plan for dept: {} year: {}", request.getDepartment(), request.getFiscalYear());

        HeadcountPlan plan = new HeadcountPlan();
        mapRequestToEntity(request, plan);

        HeadcountPlan saved = headcountPlanRepository.save(plan);
        logger.info("Headcount plan created: id={}", saved.getId());
        return HeadcountPlanResponse.fromEntity(saved);
    }

    public HeadcountPlanResponse updateHeadcountPlan(Long id, HeadcountPlanRequest request) {
        logger.info("Updating headcount plan: {}", id);
        HeadcountPlan plan = findById(id);
        mapRequestToEntity(request, plan);
        HeadcountPlan saved = headcountPlanRepository.save(plan);
        return HeadcountPlanResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public HeadcountPlanResponse getHeadcountPlan(Long id) {
        return HeadcountPlanResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public List<HeadcountPlanResponse> getHeadcountPlansByYear(Integer fiscalYear) {
        return headcountPlanRepository.findByFiscalYearOrdered(fiscalYear).stream()
                .map(HeadcountPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HeadcountPlanResponse> getHeadcountPlansByDepartment(String department) {
        return headcountPlanRepository.findByDepartment(department).stream()
                .map(HeadcountPlanResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Integer> getAvailableFiscalYears() {
        return headcountPlanRepository.findDistinctFiscalYears();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getYearSummary(Integer fiscalYear) {
        Long totalPlanned = headcountPlanRepository.sumPlannedHeadcountByYear(fiscalYear);
        Long totalCurrent = headcountPlanRepository.sumCurrentHeadcountByYear(fiscalYear);
        BigDecimal totalBudget = headcountPlanRepository.sumBudgetByYear(fiscalYear);

        long planned = totalPlanned != null ? totalPlanned : 0;
        long current = totalCurrent != null ? totalCurrent : 0;

        return Map.of(
                "fiscalYear", fiscalYear,
                "totalPlannedHeadcount", planned,
                "totalCurrentHeadcount", current,
                "totalVariance", planned - current,
                "totalBudget", totalBudget != null ? totalBudget : BigDecimal.ZERO
        );
    }

    public void deleteHeadcountPlan(Long id) {
        HeadcountPlan plan = findById(id);
        headcountPlanRepository.delete(plan);
        logger.info("Headcount plan deleted: {}", id);
    }

    private void mapRequestToEntity(HeadcountPlanRequest request, HeadcountPlan plan) {
        plan.setDepartment(request.getDepartment());
        plan.setFiscalYear(request.getFiscalYear());
        plan.setPlannedHeadcount(request.getPlannedHeadcount() != null ? request.getPlannedHeadcount() : 0);
        plan.setCurrentHeadcount(request.getCurrentHeadcount() != null ? request.getCurrentHeadcount() : 0);
        plan.setBudget(request.getBudget());
        plan.setNotes(request.getNotes());
        plan.setForecastVacancies(request.getForecastVacancies() != null ? request.getForecastVacancies() : 0);
        plan.setNewPositionRequests(request.getNewPositionRequests() != null ? request.getNewPositionRequests() : 0);

        if (request.getOrgUnitId() != null) {
            OrgUnit orgUnit = orgUnitRepository.findById(request.getOrgUnitId())
                    .orElseThrow(() -> new IllegalArgumentException("Org unit not found: " + request.getOrgUnitId()));
            plan.setOrgUnit(orgUnit);
        } else {
            plan.setOrgUnit(null);
        }
    }

    private HeadcountPlan findById(Long id) {
        return headcountPlanRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Headcount plan not found: " + id));
    }
}
