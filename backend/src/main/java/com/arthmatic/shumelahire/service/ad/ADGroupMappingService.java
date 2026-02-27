package com.arthmatic.shumelahire.service.ad;

import com.arthmatic.shumelahire.dto.ad.ADGroupRoleMappingRequest;
import com.arthmatic.shumelahire.entity.ADGroupRoleMapping;
import com.arthmatic.shumelahire.repository.ADGroupRoleMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@ConditionalOnProperty(name = "shumelahire.ad.enabled", havingValue = "true")
public class ADGroupMappingService {

    private static final Logger logger = LoggerFactory.getLogger(ADGroupMappingService.class);

    private final ADGroupRoleMappingRepository repository;

    public ADGroupMappingService(ADGroupRoleMappingRepository repository) {
        this.repository = repository;
    }

    public List<ADGroupRoleMapping> getAllMappings() {
        return repository.findAll();
    }

    public List<ADGroupRoleMapping> getActiveMappings() {
        return repository.findByIsActiveTrue();
    }

    @Transactional
    public ADGroupRoleMapping createMapping(ADGroupRoleMappingRequest request) {
        if (repository.existsByAdGroupDN(request.getAdGroupDN())) {
            throw new RuntimeException("A mapping already exists for AD group DN: " + request.getAdGroupDN());
        }

        ADGroupRoleMapping mapping = new ADGroupRoleMapping();
        mapping.setAdGroupName(request.getAdGroupName());
        mapping.setAdGroupDN(request.getAdGroupDN());
        mapping.setShumelaRole(request.getShumelaRole());
        mapping.setDescription(request.getDescription());
        mapping.setPriority(request.getPriority());
        mapping.setIsActive(true);

        logger.info("Creating AD group mapping: {} -> {}", request.getAdGroupDN(), request.getShumelaRole());
        return repository.save(mapping);
    }

    @Transactional
    public ADGroupRoleMapping updateMapping(Long id, ADGroupRoleMappingRequest request) {
        ADGroupRoleMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found with id: " + id));

        mapping.setAdGroupName(request.getAdGroupName());
        mapping.setAdGroupDN(request.getAdGroupDN());
        mapping.setShumelaRole(request.getShumelaRole());
        mapping.setDescription(request.getDescription());
        mapping.setPriority(request.getPriority());

        logger.info("Updated AD group mapping: {} -> {}", request.getAdGroupDN(), request.getShumelaRole());
        return repository.save(mapping);
    }

    @Transactional
    public void deleteMapping(Long id) {
        ADGroupRoleMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found with id: " + id));
        logger.info("Deleting AD group mapping: {}", mapping.getAdGroupDN());
        repository.delete(mapping);
    }

    @Transactional
    public ADGroupRoleMapping toggleMapping(Long id, boolean active) {
        ADGroupRoleMapping mapping = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mapping not found with id: " + id));
        mapping.setIsActive(active);
        logger.info("Toggled AD group mapping {} to active={}", mapping.getAdGroupDN(), active);
        return repository.save(mapping);
    }
}
