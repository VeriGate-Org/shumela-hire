package com.arthmatic.shumelahire.service;

import com.arthmatic.shumelahire.entity.OvertimeRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class OvertimeServiceTest {

    // ==================== SA Overtime Type Detection ====================

    @Test
    void determineOvertimeType_Weekday_ReturnsWeekday() {
        // Monday
        LocalDate monday = LocalDate.of(2026, 2, 23);
        assertThat(monday.getDayOfWeek()).isEqualTo(DayOfWeek.MONDAY);

        OvertimeRecord.OvertimeType type = OvertimeService.determineOvertimeType(monday, false);
        assertThat(type).isEqualTo(OvertimeRecord.OvertimeType.WEEKDAY);
    }

    @Test
    void determineOvertimeType_Saturday_ReturnsWeekend() {
        LocalDate saturday = LocalDate.of(2026, 2, 28);
        assertThat(saturday.getDayOfWeek()).isEqualTo(DayOfWeek.SATURDAY);

        OvertimeRecord.OvertimeType type = OvertimeService.determineOvertimeType(saturday, false);
        assertThat(type).isEqualTo(OvertimeRecord.OvertimeType.WEEKEND);
    }

    @Test
    void determineOvertimeType_Sunday_ReturnsWeekend() {
        LocalDate sunday = LocalDate.of(2026, 3, 1);
        assertThat(sunday.getDayOfWeek()).isEqualTo(DayOfWeek.SUNDAY);

        OvertimeRecord.OvertimeType type = OvertimeService.determineOvertimeType(sunday, false);
        assertThat(type).isEqualTo(OvertimeRecord.OvertimeType.WEEKEND);
    }

    @Test
    void determineOvertimeType_PublicHoliday_ReturnsPublicHoliday() {
        // Even if it's a weekday, public holiday takes precedence
        LocalDate weekday = LocalDate.of(2026, 2, 23);
        OvertimeRecord.OvertimeType type = OvertimeService.determineOvertimeType(weekday, true);
        assertThat(type).isEqualTo(OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY);
    }

    @Test
    void determineOvertimeType_PublicHolidayOnSunday_ReturnsPublicHoliday() {
        LocalDate sunday = LocalDate.of(2026, 3, 1);
        OvertimeRecord.OvertimeType type = OvertimeService.determineOvertimeType(sunday, true);
        assertThat(type).isEqualTo(OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY);
    }

    // ==================== SA Rate Multipliers ====================

    @Test
    void getSAMultiplier_Weekday_Returns1point5() {
        BigDecimal multiplier = OvertimeService.getSAMultiplier(OvertimeRecord.OvertimeType.WEEKDAY);
        assertThat(multiplier).isEqualByComparingTo(new BigDecimal("1.5"));
    }

    @Test
    void getSAMultiplier_Weekend_Returns2point0() {
        BigDecimal multiplier = OvertimeService.getSAMultiplier(OvertimeRecord.OvertimeType.WEEKEND);
        assertThat(multiplier).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void getSAMultiplier_PublicHoliday_Returns2point0() {
        BigDecimal multiplier = OvertimeService.getSAMultiplier(OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY);
        assertThat(multiplier).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    @Test
    void getSAMultiplier_Night_Returns2point0() {
        BigDecimal multiplier = OvertimeService.getSAMultiplier(OvertimeRecord.OvertimeType.NIGHT);
        assertThat(multiplier).isEqualByComparingTo(new BigDecimal("2.0"));
    }

    // ==================== Weekly Limit ====================

    @Test
    void maxWeeklyOvertimeHours_Is10() {
        assertThat(OvertimeRecord.MAX_WEEKLY_OVERTIME_HOURS)
                .isEqualByComparingTo(new BigDecimal("10"));
    }

    // ==================== Default Multiplier ====================

    @Test
    void getDefaultMultiplier_AllTypes_ReturnCorrectValues() {
        assertThat(OvertimeRecord.getDefaultMultiplier(OvertimeRecord.OvertimeType.WEEKDAY))
                .isEqualByComparingTo(new BigDecimal("1.5"));
        assertThat(OvertimeRecord.getDefaultMultiplier(OvertimeRecord.OvertimeType.WEEKEND))
                .isEqualByComparingTo(new BigDecimal("2.0"));
        assertThat(OvertimeRecord.getDefaultMultiplier(OvertimeRecord.OvertimeType.PUBLIC_HOLIDAY))
                .isEqualByComparingTo(new BigDecimal("2.0"));
        assertThat(OvertimeRecord.getDefaultMultiplier(OvertimeRecord.OvertimeType.NIGHT))
                .isEqualByComparingTo(new BigDecimal("2.0"));
    }
}
