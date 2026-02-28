package com.arthmatic.shumelahire.service;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class AttendanceServiceTest {

    // ==================== Late Minutes Calculation ====================

    @Test
    void calculateLateMinutes_OnTime_ReturnsZero() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(8, 0), LocalTime.of(8, 0), 5);
        assertThat(late).isEqualTo(0);
    }

    @Test
    void calculateLateMinutes_WithinGracePeriod_ReturnsZero() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(8, 3), LocalTime.of(8, 0), 5);
        assertThat(late).isEqualTo(0);
    }

    @Test
    void calculateLateMinutes_AtGraceLimit_ReturnsZero() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(8, 5), LocalTime.of(8, 0), 5);
        assertThat(late).isEqualTo(0);
    }

    @Test
    void calculateLateMinutes_AfterGracePeriod_ReturnsMinutes() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(8, 15), LocalTime.of(8, 0), 5);
        assertThat(late).isEqualTo(15);
    }

    @Test
    void calculateLateMinutes_VeryLate_ReturnsCorrectMinutes() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(9, 30), LocalTime.of(8, 0), 0);
        assertThat(late).isEqualTo(90);
    }

    @Test
    void calculateLateMinutes_NoGracePeriod_LateByOneMinute() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(8, 1), LocalTime.of(8, 0), 0);
        assertThat(late).isEqualTo(1);
    }

    @Test
    void calculateLateMinutes_Early_ReturnsZero() {
        int late = AttendanceService.calculateLateMinutes(
                LocalTime.of(7, 45), LocalTime.of(8, 0), 5);
        assertThat(late).isEqualTo(0);
    }

    // ==================== Early Departure Calculation ====================

    @Test
    void calculateEarlyDeparture_OnTime_ReturnsZero() {
        int early = AttendanceService.calculateEarlyDeparture(
                LocalTime.of(17, 0), LocalTime.of(17, 0));
        assertThat(early).isEqualTo(0);
    }

    @Test
    void calculateEarlyDeparture_LeftLate_ReturnsZero() {
        int early = AttendanceService.calculateEarlyDeparture(
                LocalTime.of(17, 30), LocalTime.of(17, 0));
        assertThat(early).isEqualTo(0);
    }

    @Test
    void calculateEarlyDeparture_LeftEarly_ReturnsMinutes() {
        int early = AttendanceService.calculateEarlyDeparture(
                LocalTime.of(16, 0), LocalTime.of(17, 0));
        assertThat(early).isEqualTo(60);
    }

    @Test
    void calculateEarlyDeparture_VeryEarly_ReturnsCorrectMinutes() {
        int early = AttendanceService.calculateEarlyDeparture(
                LocalTime.of(14, 0), LocalTime.of(17, 0));
        assertThat(early).isEqualTo(180);
    }

    // ==================== Shift Scheduled Hours ====================

    @Test
    void shiftScheduledHours_DayShift8Hours() {
        com.arthmatic.shumelahire.entity.Shift shift = new com.arthmatic.shumelahire.entity.Shift();
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(17, 0));
        shift.setBreakDurationMins(60);
        shift.setNightShift(false);

        assertThat(shift.getScheduledHours()).isEqualTo(8.0);
    }

    @Test
    void shiftScheduledHours_NightShift() {
        com.arthmatic.shumelahire.entity.Shift shift = new com.arthmatic.shumelahire.entity.Shift();
        shift.setStartTime(LocalTime.of(22, 0));
        shift.setEndTime(LocalTime.of(6, 0));
        shift.setBreakDurationMins(30);
        shift.setNightShift(true);

        assertThat(shift.getScheduledHours()).isEqualTo(7.5);
    }

    @Test
    void shiftScheduledHours_HalfDay() {
        com.arthmatic.shumelahire.entity.Shift shift = new com.arthmatic.shumelahire.entity.Shift();
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(12, 0));
        shift.setBreakDurationMins(0);
        shift.setNightShift(false);

        assertThat(shift.getScheduledHours()).isEqualTo(4.0);
    }
}
