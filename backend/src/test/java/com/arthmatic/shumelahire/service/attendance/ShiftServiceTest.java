package com.arthmatic.shumelahire.service.attendance;

import com.arthmatic.shumelahire.dto.attendance.ShiftRequest;
import com.arthmatic.shumelahire.dto.attendance.ShiftResponse;
import com.arthmatic.shumelahire.entity.attendance.Shift;
import com.arthmatic.shumelahire.repository.EmployeeRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftPatternRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftRepository;
import com.arthmatic.shumelahire.repository.attendance.ShiftScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
    private ShiftScheduleRepository shiftScheduleRepository;

    @Mock
    private ShiftPatternRepository shiftPatternRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ShiftService shiftService;

    @Test
    void createShift_validRequest_createsSuccessfully() {
        ShiftRequest request = new ShiftRequest();
        request.setName("Morning Shift");
        request.setCode("MS");
        request.setStartTime("08:00");
        request.setEndTime("17:00");
        request.setBreakDurationMinutes(60);

        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ShiftResponse response = shiftService.createShift(request);

        assertThat(response.getName()).isEqualTo("Morning Shift");
        assertThat(response.getCode()).isEqualTo("MS");
        assertThat(response.getStartTime()).isEqualTo("08:00");
        assertThat(response.getEndTime()).isEqualTo("17:00");
        verify(shiftRepository).save(any(Shift.class));
    }

    @Test
    void createShift_nightShift_setsNightShiftFlag() {
        ShiftRequest request = new ShiftRequest();
        request.setName("Night Shift");
        request.setCode("NS");
        request.setStartTime("22:00");
        request.setEndTime("06:00");
        request.setBreakDurationMinutes(30);
        request.setIsNightShift(true);

        when(shiftRepository.save(any(Shift.class))).thenAnswer(inv -> {
            Shift s = inv.getArgument(0);
            s.setId(1L);
            return s;
        });

        ShiftResponse response = shiftService.createShift(request);

        assertThat(response.getIsNightShift()).isTrue();
        assertThat(response.getStartTime()).isEqualTo("22:00");
        assertThat(response.getEndTime()).isEqualTo("06:00");
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
    void deleteShift_deactivatesShift() {
        Shift shift = new Shift();
        shift.setId(1L);
        shift.setName("Test");
        shift.setIsActive(true);
        shift.setStartTime(LocalTime.of(8, 0));
        shift.setEndTime(LocalTime.of(17, 0));
        shift.setBreakDurationMinutes(60);

        when(shiftRepository.findById(1L)).thenReturn(Optional.of(shift));
        when(shiftRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        shiftService.deleteShift(1L);

        assertThat(shift.getIsActive()).isFalse();
        verify(shiftRepository).save(shift);
    }
}
