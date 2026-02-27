package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.dto.org.PositionRequest;
import com.arthmatic.shumelahire.dto.org.PositionResponse;
import com.arthmatic.shumelahire.entity.Position;
import com.arthmatic.shumelahire.entity.PositionStatus;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.OrgUnitRepository;
import com.arthmatic.shumelahire.repository.PositionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PositionServiceTest {

    @Mock
    private PositionRepository positionRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private OrgUnitRepository orgUnitRepository;

    @InjectMocks
    private PositionService positionService;

    private Position testPosition;
    private PositionRequest testRequest;

    @BeforeEach
    void setUp() {
        testPosition = new Position();
        testPosition.setId(1L);
        testPosition.setTitle("Software Engineer");
        testPosition.setCode("ENG-001");
        testPosition.setDepartment("Engineering");
        testPosition.setGrade("L3");
        testPosition.setFte(BigDecimal.ONE);
        testPosition.setStatus(PositionStatus.ACTIVE);
        testPosition.setIsVacant(true);
        testPosition.setJobSharingAllowed(false);
        testPosition.setCreatedAt(LocalDateTime.now());
        testPosition.setUpdatedAt(LocalDateTime.now());

        testRequest = new PositionRequest();
        testRequest.setTitle("Software Engineer");
        testRequest.setCode("ENG-001");
        testRequest.setDepartment("Engineering");
        testRequest.setGrade("L3");
        testRequest.setFte(BigDecimal.ONE);
        testRequest.setStatus("ACTIVE");
        testRequest.setIsVacant(true);
    }

    @Test
    void createPosition_ValidRequest_ReturnsResponse() {
        when(positionRepository.save(any(Position.class))).thenReturn(testPosition);

        PositionResponse result = positionService.createPosition(testRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Software Engineer");
        assertThat(result.getDepartment()).isEqualTo("Engineering");
        assertThat(result.getIsVacant()).isTrue();
        verify(positionRepository, times(1)).save(any(Position.class));
    }

    @Test
    void updatePosition_ExistingId_ReturnsUpdatedResponse() {
        PositionRequest updateRequest = new PositionRequest();
        updateRequest.setTitle("Senior Software Engineer");
        updateRequest.setDepartment("Engineering");
        updateRequest.setFte(BigDecimal.ONE);
        updateRequest.setStatus("ACTIVE");
        updateRequest.setIsVacant(false);

        Position updatedPosition = new Position();
        updatedPosition.setId(1L);
        updatedPosition.setTitle("Senior Software Engineer");
        updatedPosition.setDepartment("Engineering");
        updatedPosition.setFte(BigDecimal.ONE);
        updatedPosition.setStatus(PositionStatus.ACTIVE);
        updatedPosition.setIsVacant(false);
        updatedPosition.setJobSharingAllowed(false);
        updatedPosition.setCreatedAt(LocalDateTime.now());
        updatedPosition.setUpdatedAt(LocalDateTime.now());

        when(positionRepository.findById(1L)).thenReturn(Optional.of(testPosition));
        when(positionRepository.save(any(Position.class))).thenReturn(updatedPosition);

        PositionResponse result = positionService.updatePosition(1L, updateRequest);

        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo("Senior Software Engineer");
        assertThat(result.getIsVacant()).isFalse();
    }

    @Test
    void getPosition_ExistingId_ReturnsResponse() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(testPosition));

        PositionResponse result = positionService.getPosition(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Software Engineer");
    }

    @Test
    void getPosition_NonExistingId_ThrowsIllegalArgumentException() {
        when(positionRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> positionService.getPosition(999L));
    }

    @Test
    void getVacantPositions_ReturnsVacantList() {
        when(positionRepository.findVacantPositions()).thenReturn(List.of(testPosition));

        List<PositionResponse> result = positionService.getVacantPositions();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsVacant()).isTrue();
    }

    @Test
    void deletePosition_ExistingId_DeactivatesPosition() {
        when(positionRepository.findById(1L)).thenReturn(Optional.of(testPosition));
        when(positionRepository.save(any(Position.class))).thenReturn(testPosition);

        positionService.deletePosition(1L);

        verify(positionRepository, times(1)).save(argThat(p -> p.getStatus() == PositionStatus.INACTIVE));
    }

    @Test
    void getVacancySummary_ReturnsSummaryMap() {
        when(positionRepository.countActivePositions()).thenReturn(10L);
        when(positionRepository.countVacantPositions()).thenReturn(3L);

        Map<String, Object> result = positionService.getVacancySummary();

        assertThat(result).isNotNull();
        assertThat(result.get("totalPositions")).isEqualTo(10L);
        assertThat(result.get("vacantPositions")).isEqualTo(3L);
        assertThat(result.get("filledPositions")).isEqualTo(7L);
    }

    @Test
    void getPositions_WithFilters_ReturnsFilteredPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Position> page = new PageImpl<>(List.of(testPosition));

        when(positionRepository.findByFilters(eq("Engineering"), eq(PositionStatus.ACTIVE), eq(true), eq(pageable)))
                .thenReturn(page);

        Page<PositionResponse> result = positionService.getPositions("Engineering", "ACTIVE", true, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDepartment()).isEqualTo("Engineering");
    }
}
