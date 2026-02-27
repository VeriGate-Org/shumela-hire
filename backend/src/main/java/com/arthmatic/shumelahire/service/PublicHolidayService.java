package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.PublicHolidayRequest;
import com.arthmatic.shumelahire.dto.PublicHolidayResponse;
import com.arthmatic.shumelahire.entity.PublicHoliday;
import com.arthmatic.shumelahire.repository.PublicHolidayRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PublicHolidayService {

    private static final Logger logger = LoggerFactory.getLogger(PublicHolidayService.class);

    @Autowired
    private PublicHolidayRepository publicHolidayRepository;

    public PublicHolidayResponse createHoliday(PublicHolidayRequest request) {
        String tenantId = TenantContext.requireCurrentTenant();
        logger.info("Creating public holiday: {} on {} for tenant: {}", request.getName(), request.getHolidayDate(), tenantId);

        if (publicHolidayRepository.existsByTenantIdAndHolidayDateAndCountry(
                tenantId, request.getHolidayDate(), request.getCountry())) {
            throw new IllegalArgumentException("Holiday already exists for this date and country");
        }

        PublicHoliday holiday = new PublicHoliday();
        mapRequestToEntity(request, holiday);

        PublicHoliday saved = publicHolidayRepository.save(holiday);
        return PublicHolidayResponse.fromEntity(saved);
    }

    public PublicHolidayResponse updateHoliday(Long id, PublicHolidayRequest request) {
        PublicHoliday holiday = publicHolidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Public holiday not found: " + id));
        mapRequestToEntity(request, holiday);
        PublicHoliday saved = publicHolidayRepository.save(holiday);
        return PublicHolidayResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<PublicHolidayResponse> getHolidaysByYear(int year) {
        String tenantId = TenantContext.requireCurrentTenant();
        return publicHolidayRepository.findByTenantAndYear(tenantId, year).stream()
                .map(PublicHolidayResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PublicHolidayResponse> getHolidaysInRange(LocalDate startDate, LocalDate endDate, String country) {
        String tenantId = TenantContext.requireCurrentTenant();
        return publicHolidayRepository.findHolidaysInRange(tenantId, startDate, endDate, country).stream()
                .map(PublicHolidayResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public boolean isPublicHoliday(LocalDate date) {
        String tenantId = TenantContext.requireCurrentTenant();
        return publicHolidayRepository.existsByTenantIdAndHolidayDateAndCountry(tenantId, date, "ZA");
    }

    public void deleteHoliday(Long id) {
        PublicHoliday holiday = publicHolidayRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Public holiday not found: " + id));
        holiday.setActive(false);
        publicHolidayRepository.save(holiday);
        logger.info("Deactivated public holiday: {}", id);
    }

    private void mapRequestToEntity(PublicHolidayRequest request, PublicHoliday entity) {
        entity.setName(request.getName());
        entity.setHolidayDate(request.getHolidayDate());
        entity.setDescription(request.getDescription());
        entity.setCountry(request.getCountry());
        entity.setRegion(request.getRegion());
        entity.setRecurring(request.isRecurring());
    }
}
