package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.PositionRequest;
import com.arthmatic.shumelahire.dto.org.PositionResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.OrgUnit;
import com.arthmatic.shumelahire.entity.Position;
import com.arthmatic.shumelahire.entity.PositionStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import com.arthmatic.shumelahire.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PositionService {

    private static final Logger logger = LoggerFactory.getLogger(PositionService.class);

    @Autowired
    private PositionRepository positionRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    public PositionResponse createPosition(PositionRequest request) {
        logger.info("Creating position: {}", request.getTitle());

        Position position = new Position();
        mapRequestToEntity(request, position);

        Position saved = positionRepository.save(position);
        logger.info("Position created: {} (id={})", saved.getTitle(), saved.getId());
        return PositionResponse.fromEntity(saved);
    }

    public PositionResponse updatePosition(Long id, PositionRequest request) {
        logger.info("Updating position: {}", id);
        Position position = findById(id);
        mapRequestToEntity(request, position);
        Position saved = positionRepository.save(position);
        return PositionResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PositionResponse getPosition(Long id) {
        return PositionResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public Page<PositionResponse> getPositions(String department, String status, Boolean isVacant, Pageable pageable) {
        PositionStatus statusEnum = status != null ? PositionStatus.valueOf(status) : null;
        return positionRepository.findByFilters(department, statusEnum, isVacant, pageable)
                .map(PositionResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> getVacantPositions() {
        return positionRepository.findVacantPositions().stream()
                .map(PositionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PositionResponse> getPositionsByOrgUnit(Long orgUnitId) {
        return positionRepository.findByOrgUnitId(orgUnitId).stream()
                .map(PositionResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deletePosition(Long id) {
        Position position = findById(id);
        position.setStatus(PositionStatus.INACTIVE);
        positionRepository.save(position);
        logger.info("Position deactivated: {}", id);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getVacancySummary() {
        long total = positionRepository.countActivePositions();
        long vacant = positionRepository.countVacantPositions();
        return Map.of(
                "totalPositions", total,
                "vacantPositions", vacant,
                "filledPositions", total - vacant,
                "vacancyRate", total > 0 ? (double) vacant / total * 100 : 0
        );
    }

    private void mapRequestToEntity(PositionRequest request, Position position) {
        position.setTitle(request.getTitle());
        position.setCode(request.getCode());
        position.setDepartment(request.getDepartment());
        position.setGrade(request.getGrade());
        position.setFte(request.getFte());
        position.setIsVacant(request.getIsVacant() != null ? request.getIsVacant() : true);
        position.setJobSharingAllowed(request.getJobSharingAllowed() != null ? request.getJobSharingAllowed() : false);
        position.setDescription(request.getDescription());
        position.setLocation(request.getLocation());

        if (request.getStatus() != null) {
            position.setStatus(PositionStatus.valueOf(request.getStatus()));
        }

        if (request.getReportingPositionId() != null) {
            Position reporting = findById(request.getReportingPositionId());
            position.setReportingPosition(reporting);
        } else {
            position.setReportingPosition(null);
        }

        if (request.getCurrentEmployeeId() != null) {
            Employee emp = employeeRepository.findById(request.getCurrentEmployeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + request.getCurrentEmployeeId()));
            position.setCurrentEmployee(emp);
            position.setIsVacant(false);
        } else {
            position.setCurrentEmployee(null);
        }

        if (request.getOrgUnitId() != null) {
            OrgUnit orgUnit = orgUnitRepository.findById(request.getOrgUnitId())
                    .orElseThrow(() -> new IllegalArgumentException("Org unit not found: " + request.getOrgUnitId()));
            position.setOrgUnit(orgUnit);
        } else {
            position.setOrgUnit(null);
        }
    }

    private Position findById(Long id) {
        return positionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Position not found: " + id));
    }
}
