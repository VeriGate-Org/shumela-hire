package com.arthmatic.shumelahire.repository;

import com.arthmatic.shumelahire.entity.ShiftSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShiftScheduleRepository extends JpaRepository<ShiftSchedule, Long> {

    List<ShiftSchedule> findByEmployeeIdAndScheduleDateBetween(Long employeeId, LocalDate start, LocalDate end);

    List<ShiftSchedule> findByScheduleDateBetween(LocalDate start, LocalDate end);

    Optional<ShiftSchedule> findByEmployeeIdAndScheduleDate(Long employeeId, LocalDate date);

    List<ShiftSchedule> findByEmployeeIdAndScheduleDateBetweenAndStatus(
            Long employeeId, LocalDate start, LocalDate end, ShiftSchedule.ScheduleStatus status);

    List<ShiftSchedule> findByShiftIdAndScheduleDateBetween(Long shiftId, LocalDate start, LocalDate end);
}
