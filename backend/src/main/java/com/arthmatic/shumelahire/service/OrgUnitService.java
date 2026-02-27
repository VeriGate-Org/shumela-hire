package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.OrgUnitRequest;
import com.arthmatic.shumelahire.dto.org.OrgUnitResponse;
import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.OrgUnit;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrgUnitService {

    private static final Logger logger = LoggerFactory.getLogger(OrgUnitService.class);

    @Autowired
    private OrgUnitRepository orgUnitRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public OrgUnitResponse createOrgUnit(OrgUnitRequest request) {
        logger.info("Creating org unit: {}", request.getName());

        if (request.getCode() != null && !request.getCode().isBlank()) {
            // Tenant check handled by TenantAwareEntity
            OrgUnit unit = buildOrgUnit(request);
            OrgUnit saved = orgUnitRepository.save(unit);
            logger.info("Org unit created: {} (id={})", saved.getName(), saved.getId());
            return OrgUnitResponse.fromEntity(saved);
        }

        OrgUnit unit = buildOrgUnit(request);
        OrgUnit saved = orgUnitRepository.save(unit);
        logger.info("Org unit created: {} (id={})", saved.getName(), saved.getId());
        return OrgUnitResponse.fromEntity(saved);
    }

    public OrgUnitResponse updateOrgUnit(Long id, OrgUnitRequest request) {
        logger.info("Updating org unit: {}", id);
        OrgUnit unit = findById(id);
        mapRequestToEntity(request, unit);
        OrgUnit saved = orgUnitRepository.save(unit);
        return OrgUnitResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public OrgUnitResponse getOrgUnit(Long id) {
        return OrgUnitResponse.fromEntity(findById(id));
    }

    @Transactional(readOnly = true)
    public List<OrgUnitResponse> getAllOrgUnits() {
        return orgUnitRepository.findByIsActiveTrue().stream()
                .map(OrgUnitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrgUnitResponse> getOrgTree() {
        List<OrgUnit> roots = orgUnitRepository.findRootUnits();
        return roots.stream()
                .map(OrgUnitResponse::fromEntityWithChildren)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrgUnitResponse> getChildUnits(Long parentId) {
        return orgUnitRepository.findByParentId(parentId).stream()
                .map(OrgUnitResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public void deleteOrgUnit(Long id) {
        OrgUnit unit = findById(id);
        unit.setIsActive(false);
        orgUnitRepository.save(unit);
        logger.info("Org unit deactivated: {}", id);
    }

    public OrgUnitResponse moveOrgUnit(Long id, Long newParentId) {
        OrgUnit unit = findById(id);
        if (newParentId != null) {
            OrgUnit newParent = findById(newParentId);
            unit.setParent(newParent);
        } else {
            unit.setParent(null);
        }
        OrgUnit saved = orgUnitRepository.save(unit);
        logger.info("Org unit {} moved to parent {}", id, newParentId);
        return OrgUnitResponse.fromEntity(saved);
    }

    private OrgUnit buildOrgUnit(OrgUnitRequest request) {
        OrgUnit unit = new OrgUnit();
        mapRequestToEntity(request, unit);
        return unit;
    }

    private void mapRequestToEntity(OrgUnitRequest request, OrgUnit unit) {
        unit.setName(request.getName());
        unit.setCode(request.getCode());
        unit.setUnitType(request.getUnitType());
        unit.setCostCentre(request.getCostCentre());
        unit.setDescription(request.getDescription());
        unit.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);

        if (request.getParentId() != null) {
            OrgUnit parent = findById(request.getParentId());
            unit.setParent(parent);
        } else {
            unit.setParent(null);
        }

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found: " + request.getManagerId()));
            unit.setManager(manager);
        } else {
            unit.setManager(null);
        }
    }

    private OrgUnit findById(Long id) {
        return orgUnitRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Org unit not found: " + id));
    }
}
