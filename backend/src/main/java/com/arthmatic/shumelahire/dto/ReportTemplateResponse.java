package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.ReportTemplate;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * DTO that maps ReportTemplate entity to the SavedReport shape expected by the frontend.
 */
public class ReportTemplateResponse {

    private static final ObjectMapper mapper = new ObjectMapper();

    private String id;
    private String name;
    private String description;
    private List<String> fields;
    private List<Map<String, Object>> filters;
    private Map<String, Object> visualization;
    private Map<String, Object> schedule;
    private Map<String, String> dateRange;
    private String createdAt;
    private String updatedAt;
    private String createdBy;
    private boolean isShared;
    private boolean isSystem;
    private String lastRun;
    private int runCount;
    private List<String> tags;

    public ReportTemplateResponse() {}

    public ReportTemplateResponse(ReportTemplate entity) {
        this.id = String.valueOf(entity.getId());
        this.name = entity.getName();
        this.description = entity.getDescription();
        this.createdBy = entity.getCreatedBy();
        this.isShared = entity.isShared();
        this.isSystem = entity.isSystem();
        this.runCount = entity.getRunCount();
        this.createdAt = entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null;
        this.updatedAt = entity.getUpdatedAt() != null ? entity.getUpdatedAt().toString() : null;
        this.lastRun = entity.getLastRun() != null ? entity.getLastRun().toString() : null;

        this.fields = parseJson(entity.getFieldsJson(), new TypeReference<List<String>>() {}, Collections.emptyList());
        this.filters = parseJson(entity.getFiltersJson(), new TypeReference<List<Map<String, Object>>>() {}, Collections.emptyList());
        this.visualization = parseJson(entity.getVisualizationJson(), new TypeReference<Map<String, Object>>() {}, Map.of("type", "table"));
        this.schedule = parseJson(entity.getScheduleJson(), new TypeReference<Map<String, Object>>() {}, null);
        this.dateRange = parseJson(entity.getDateRangeJson(), new TypeReference<Map<String, String>>() {}, Map.of("start", "", "end", ""));
        this.tags = parseJson(entity.getTagsJson(), new TypeReference<List<String>>() {}, Collections.emptyList());
    }

    private <T> T parseJson(String json, TypeReference<T> type, T defaultValue) {
        if (json == null || json.isBlank()) return defaultValue;
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getFields() { return fields; }
    public List<Map<String, Object>> getFilters() { return filters; }
    public Map<String, Object> getVisualization() { return visualization; }
    public Map<String, Object> getSchedule() { return schedule; }
    public Map<String, String> getDateRange() { return dateRange; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public String getCreatedBy() { return createdBy; }
    public boolean getIsShared() { return isShared; }
    public boolean getIsSystem() { return isSystem; }
    public String getLastRun() { return lastRun; }
    public int getRunCount() { return runCount; }
    public List<String> getTags() { return tags; }
}
