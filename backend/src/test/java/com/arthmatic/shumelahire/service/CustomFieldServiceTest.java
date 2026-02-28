package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.config.tenant.TenantContext;
import com.arthmatic.shumelahire.dto.CustomFieldRequest;
import com.arthmatic.shumelahire.dto.CustomFieldResponse;
import com.arthmatic.shumelahire.dto.CustomFieldValueRequest;
import com.arthmatic.shumelahire.entity.*;
import com.arthmatic.shumelahire.repository.CustomFieldRepository;
import com.arthmatic.shumelahire.repository.CustomFieldValueRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomFieldServiceTest {

    @Mock
    private CustomFieldRepository customFieldRepository;

    @Mock
    private CustomFieldValueRepository customFieldValueRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CustomFieldService customFieldService;

    private MockedStatic<TenantContext> tenantContextMock;
    private CustomField testField;
    private CustomFieldRequest testRequest;

    @BeforeEach
    void setUp() {
        tenantContextMock = mockStatic(TenantContext.class);
        tenantContextMock.when(TenantContext::requireCurrentTenant).thenReturn("test-tenant");

        testField = new CustomField();
        testField.setId(1L);
        testField.setEntityType(CustomFieldEntityType.EMPLOYEE);
        testField.setFieldName("employee_badge_id");
        testField.setFieldLabel("Badge ID");
        testField.setFieldType(CustomFieldType.TEXT);
        testField.setIsRequired(false);
        testField.setIsActive(true);
        testField.setDisplayOrder(1);
        testField.setCreatedAt(LocalDateTime.now());
        testField.setUpdatedAt(LocalDateTime.now());

        testRequest = new CustomFieldRequest();
        testRequest.setEntityType(CustomFieldEntityType.EMPLOYEE);
        testRequest.setFieldName("employee_badge_id");
        testRequest.setFieldLabel("Badge ID");
        testRequest.setFieldType(CustomFieldType.TEXT);
    }

    @AfterEach
    void tearDown() {
        tenantContextMock.close();
    }

    @Test
    void createField_ValidRequest_ReturnsFieldResponse() {
        // Given
        when(customFieldRepository.existsByFieldNameAndEntityTypeAndTenantId(
                "employee_badge_id", CustomFieldEntityType.EMPLOYEE, "test-tenant")).thenReturn(false);
        when(customFieldRepository.save(any(CustomField.class))).thenReturn(testField);

        // When
        CustomFieldResponse result = customFieldService.createField(testRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getFieldName()).isEqualTo("employee_badge_id");
        assertThat(result.getFieldLabel()).isEqualTo("Badge ID");
        assertThat(result.getFieldType()).isEqualTo(CustomFieldType.TEXT);
        verify(customFieldRepository, times(1)).save(any(CustomField.class));
    }

    @Test
    void createField_DuplicateName_ThrowsIllegalArgumentException() {
        // Given
        when(customFieldRepository.existsByFieldNameAndEntityTypeAndTenantId(
                "employee_badge_id", CustomFieldEntityType.EMPLOYEE, "test-tenant")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> customFieldService.createField(testRequest)
        );

        assertThat(exception.getMessage()).contains("already exists");
        verify(customFieldRepository, never()).save(any(CustomField.class));
    }

    @Test
    void getFieldsByEntityType_ReturnsActiveFields() {
        // Given
        when(customFieldRepository.findByEntityTypeAndIsActiveTrueOrderByDisplayOrderAsc(
                CustomFieldEntityType.EMPLOYEE)).thenReturn(List.of(testField));

        // When
        List<CustomFieldResponse> fields = customFieldService.getFieldsByEntityType(CustomFieldEntityType.EMPLOYEE);

        // Then
        assertThat(fields).hasSize(1);
        assertThat(fields.get(0).getFieldName()).isEqualTo("employee_badge_id");
    }

    @Test
    void setFieldValue_NewValue_CreatesEntry() {
        // Given
        CustomFieldValueRequest valueRequest = new CustomFieldValueRequest();
        valueRequest.setCustomFieldId(1L);
        valueRequest.setEntityType(CustomFieldEntityType.EMPLOYEE);
        valueRequest.setEntityId(1L);
        valueRequest.setFieldValue("BADGE-001");

        when(customFieldRepository.findById(1L)).thenReturn(Optional.of(testField));
        when(customFieldValueRepository.findByCustomFieldIdAndEntityTypeAndEntityId(
                1L, CustomFieldEntityType.EMPLOYEE, 1L)).thenReturn(Optional.empty());
        when(customFieldValueRepository.save(any(CustomFieldValue.class))).thenReturn(new CustomFieldValue());

        // When
        customFieldService.setFieldValue(valueRequest);

        // Then
        verify(customFieldValueRepository, times(1)).save(any(CustomFieldValue.class));
    }

    @Test
    void getFieldValues_ReturnsMap() {
        // Given
        CustomFieldValue value = new CustomFieldValue();
        value.setCustomField(testField);
        value.setEntityType(CustomFieldEntityType.EMPLOYEE);
        value.setEntityId(1L);
        value.setFieldValue("BADGE-001");

        when(customFieldValueRepository.findByEntityTypeAndEntityId(
                CustomFieldEntityType.EMPLOYEE, 1L)).thenReturn(List.of(value));

        // When
        Map<String, String> values = customFieldService.getFieldValues(CustomFieldEntityType.EMPLOYEE, 1L);

        // Then
        assertThat(values).hasSize(1);
        assertThat(values.get("employee_badge_id")).isEqualTo("BADGE-001");
    }

    @Test
    void deleteField_ExistingId_DeletesFieldAndValues() {
        // Given
        when(customFieldRepository.findById(1L)).thenReturn(Optional.of(testField));

        // When
        customFieldService.deleteField(1L);

        // Then
        verify(customFieldValueRepository, times(1)).deleteByCustomFieldId(1L);
        verify(customFieldRepository, times(1)).delete(testField);
        verify(auditLogService, times(1)).logSystemAction(
                eq("CUSTOM_FIELD_DELETED"), eq("CUSTOM_FIELD"), anyString());
    }
}
