package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.ShiftRequest;
import com.arthmatic.shumelahire.dto.attendance.ShiftResponse;
import com.arthmatic.shumelahire.entity.Shift;
import com.arthmatic.shumelahire.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShiftServiceTest {

    @Mock
    private ShiftRepository shiftRepository;

    @Mock
    private ShiftScheduleRepository scheduleRepository;

    @Mock
    private ShiftPatternRepository patternRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private GeofenceRepository geofenceRepository;

    @InjectMocks
    private ShiftService shiftService;

    @Test
    void createShift_validRequest_createsSuccessfully() {
        ShiftRequest request = new ShiftRequest();
        request.setName("Morning Shift");
        request.setCode("MS");
        request.setStartTime(LocalTime.of(8, 0));
        request.setEndTime(LocalTime.of(17, 0));
        request.setBreakDurationMinutes(60);

        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ShiftResponse response = shiftService.createShift(request);

        assertThat(response.getName()).isEqualTo("Morning Shift");
        assertThat(response.getCode()).isEqualTo("MS");
        // 9 hours - 1 hour break = 8 hours
        assertThat(response.getTotalHours()).isEqualByComparingTo(new BigDecimal("8.00"));
        verify(shiftRepository).save(any(Shift.class));
    }

    @Test
    void createShift_overnightShift_calculatesCorrectHours() {
        ShiftRequest request = new ShiftRequest();
        request.setName("Night Shift");
        request.setCode("NS");
        request.setStartTime(LocalTime.of(22, 0));
        request.setEndTime(LocalTime.of(6, 0));
        request.setBreakDurationMinutes(30);
        request.setIsOvernight(true);

        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ShiftResponse response = shiftService.createShift(request);

        // 8 hours - 30 min break = 7.5 hours
        assertThat(response.getTotalHours()).isEqualByComparingTo(new BigDecimal("7.50"));
        assertThat(response.getIsOvernight()).isTrue();
    }

    @Test
    void getAllShifts_returnsAll() {
        Shift s1 = new Shift();
        s1.setId(1L);
        s1.setName("Morning");
        s1.setStartTime(LocalTime.of(8, 0));
        s1.setEndTime(LocalTime.of(17, 0));
        s1.setBreakDurationMinutes(60);

        Shift s2 = new Shift();
        s2.setId(2L);
        s2.setName("Night");
        s2.setStartTime(LocalTime.of(22, 0));
        s2.setEndTime(LocalTime.of(6, 0));
        s2.setBreakDurationMinutes(30);

        when(shiftRepository.findAll()).thenReturn(Arrays.asList(s1, s2));

        List<ShiftResponse> result = shiftService.getAllShifts();
        assertThat(result).hasSize(2);
    }

    @Test
    void getShift_notFound_throwsException() {
        when(shiftRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shiftService.getShift(999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Shift not found");
    }

    @Test
    void toggleShiftActive_deactivates() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setName("Test");
        shift.setIsActive(true);
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(17, 0));
        shift.setBreakDurationMinutes(60);

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ShiftResponse response = shiftService.toggleShiftActive(1L, false);
        assertThat(response.getIsActive()).isFalse();
    }

    @Test
    void deleteShift_existingId_deletes() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setName("Test");

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));

        shiftService.deleteShift(1L);
        verify(shiftRepository).delete(shift);
    }
}
