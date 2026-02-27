-- V016: Time & Attendance tables
-- Geofences, Shifts, Shift Schedules, Shift Patterns, Attendance Records,
-- Overtime Records, Shift Swap Requests

-- ============================================================
-- Geofences
-- ============================================================
CREATE TABLE geofences (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    site            VARCHAR(200),
    type            VARCHAR(20) NOT NULL CHECK (type IN ('RADIUS', 'POLYGON')),
    latitude        DOUBLE PRECISION,
    longitude       DOUBLE PRECISION,
    radius_meters   DOUBLE PRECISION,
    polygon_coords  TEXT,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id       VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_geofences_tenant ON geofences(tenant_id);
CREATE INDEX idx_geofences_active ON geofences(tenant_id, active);

-- ============================================================
-- Shifts
-- ============================================================
CREATE TABLE shifts (
    id                  BIGSERIAL PRIMARY KEY,
    name                VARCHAR(200) NOT NULL,
    start_time          TIME NOT NULL,
    end_time            TIME NOT NULL,
    break_duration_mins INTEGER NOT NULL DEFAULT 0,
    grace_period_mins   INTEGER NOT NULL DEFAULT 0,
    night_shift         BOOLEAN NOT NULL DEFAULT FALSE,
    color               VARCHAR(20),
    active              BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id           VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shifts_tenant ON shifts(tenant_id);

-- ============================================================
-- Shift Patterns (rotational)
-- ============================================================
CREATE TABLE shift_patterns (
    id              BIGSERIAL PRIMARY KEY,
    name            VARCHAR(200) NOT NULL,
    description     TEXT,
    rotation_days   INTEGER NOT NULL,
    pattern_json    TEXT NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id       VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_shift_patterns_tenant ON shift_patterns(tenant_id);

-- ============================================================
-- Shift Schedules (employee-shift-date assignments)
-- ============================================================
CREATE TABLE shift_schedules (
    id              BIGSERIAL PRIMARY KEY,
    employee_id     BIGINT NOT NULL,
    shift_id        BIGINT NOT NULL,
    schedule_date   DATE NOT NULL,
    status          VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED' CHECK (status IN ('SCHEDULED', 'COMPLETED', 'MISSED', 'SWAPPED', 'CANCELLED')),
    notes           TEXT,
    tenant_id       VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_schedule_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_schedule_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT uk_schedule_employee_date UNIQUE (employee_id, schedule_date, tenant_id)
);

CREATE INDEX idx_shift_schedules_tenant ON shift_schedules(tenant_id);
CREATE INDEX idx_shift_schedules_employee ON shift_schedules(employee_id, schedule_date);
CREATE INDEX idx_shift_schedules_date ON shift_schedules(schedule_date, tenant_id);

-- ============================================================
-- Attendance Records
-- ============================================================
CREATE TABLE attendance_records (
    id                  BIGSERIAL PRIMARY KEY,
    employee_id         BIGINT NOT NULL,
    record_date         DATE NOT NULL,
    clock_in            TIMESTAMP,
    clock_out           TIMESTAMP,
    clock_in_latitude   DOUBLE PRECISION,
    clock_in_longitude  DOUBLE PRECISION,
    clock_out_latitude  DOUBLE PRECISION,
    clock_out_longitude DOUBLE PRECISION,
    geofence_id         BIGINT,
    clock_in_within_geofence  BOOLEAN,
    clock_out_within_geofence BOOLEAN,
    status              VARCHAR(30) NOT NULL DEFAULT 'PRESENT' CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'ON_LEAVE', 'PUBLIC_HOLIDAY')),
    regular_hours       NUMERIC(5,2) NOT NULL DEFAULT 0,
    overtime_hours      NUMERIC(5,2) NOT NULL DEFAULT 0,
    break_minutes       INTEGER NOT NULL DEFAULT 0,
    late_minutes        INTEGER NOT NULL DEFAULT 0,
    early_departure_mins INTEGER NOT NULL DEFAULT 0,
    auto_clocked_out    BOOLEAN NOT NULL DEFAULT FALSE,
    notes               TEXT,
    shift_schedule_id   BIGINT,
    tenant_id           VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_attendance_geofence FOREIGN KEY (geofence_id) REFERENCES geofences(id),
    CONSTRAINT fk_attendance_schedule FOREIGN KEY (shift_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT uk_attendance_employee_date UNIQUE (employee_id, record_date, tenant_id)
);

CREATE INDEX idx_attendance_tenant ON attendance_records(tenant_id);
CREATE INDEX idx_attendance_employee ON attendance_records(employee_id, record_date);
CREATE INDEX idx_attendance_date ON attendance_records(record_date, tenant_id);
CREATE INDEX idx_attendance_status ON attendance_records(status, tenant_id);

-- ============================================================
-- Overtime Records
-- ============================================================
CREATE TABLE overtime_records (
    id                  BIGSERIAL PRIMARY KEY,
    employee_id         BIGINT NOT NULL,
    attendance_record_id BIGINT,
    overtime_date       DATE NOT NULL,
    hours               NUMERIC(5,2) NOT NULL,
    type                VARCHAR(30) NOT NULL CHECK (type IN ('WEEKDAY', 'WEEKEND', 'PUBLIC_HOLIDAY', 'NIGHT')),
    rate_multiplier     NUMERIC(4,2) NOT NULL DEFAULT 1.5,
    status              VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PAID')),
    approved_by_id      BIGINT,
    approved_at         TIMESTAMP,
    reason              TEXT,
    rejection_reason    TEXT,
    tenant_id           VARCHAR(50) NOT NULL,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_overtime_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_overtime_attendance FOREIGN KEY (attendance_record_id) REFERENCES attendance_records(id),
    CONSTRAINT fk_overtime_approver FOREIGN KEY (approved_by_id) REFERENCES employees(id)
);

CREATE INDEX idx_overtime_tenant ON overtime_records(tenant_id);
CREATE INDEX idx_overtime_employee ON overtime_records(employee_id, overtime_date);
CREATE INDEX idx_overtime_status ON overtime_records(status, tenant_id);
CREATE INDEX idx_overtime_date ON overtime_records(overtime_date, tenant_id);

-- ============================================================
-- Shift Swap Requests
-- ============================================================
CREATE TABLE shift_swap_requests (
    id                      BIGSERIAL PRIMARY KEY,
    requester_employee_id   BIGINT NOT NULL,
    target_employee_id      BIGINT NOT NULL,
    requester_schedule_id   BIGINT NOT NULL,
    target_schedule_id      BIGINT NOT NULL,
    status                  VARCHAR(30) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'TARGET_ACCEPTED', 'APPROVED', 'REJECTED', 'CANCELLED')),
    reason                  TEXT,
    rejection_reason        TEXT,
    approved_by_id          BIGINT,
    approved_at             TIMESTAMP,
    tenant_id               VARCHAR(50) NOT NULL,
    created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_swap_requester FOREIGN KEY (requester_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_target FOREIGN KEY (target_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_requester_schedule FOREIGN KEY (requester_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_target_schedule FOREIGN KEY (target_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_approver FOREIGN KEY (approved_by_id) REFERENCES employees(id)
);

CREATE INDEX idx_swap_tenant ON shift_swap_requests(tenant_id);
CREATE INDEX idx_swap_requester ON shift_swap_requests(requester_employee_id);
CREATE INDEX idx_swap_target ON shift_swap_requests(target_employee_id);
CREATE INDEX idx_swap_status ON shift_swap_requests(status, tenant_id);

-- ============================================================
-- Updated_at triggers
-- ============================================================
CREATE OR REPLACE FUNCTION update_attendance_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_geofences_updated_at BEFORE UPDATE ON geofences
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_shifts_updated_at BEFORE UPDATE ON shifts
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_shift_patterns_updated_at BEFORE UPDATE ON shift_patterns
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_shift_schedules_updated_at BEFORE UPDATE ON shift_schedules
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_attendance_records_updated_at BEFORE UPDATE ON attendance_records
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_overtime_records_updated_at BEFORE UPDATE ON overtime_records
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

CREATE TRIGGER trg_shift_swap_requests_updated_at BEFORE UPDATE ON shift_swap_requests
    FOR EACH ROW EXECUTE FUNCTION update_attendance_updated_at();

-- Table comments
COMMENT ON TABLE geofences IS 'GPS geofence zones for clock-in/out validation';
COMMENT ON TABLE shifts IS 'Shift definitions with start/end times and break config';
COMMENT ON TABLE shift_patterns IS 'Rotational shift patterns with JSON shift arrays';
COMMENT ON TABLE shift_schedules IS 'Employee-shift-date assignments';
COMMENT ON TABLE attendance_records IS 'Daily clock-in/out records with GPS and hours breakdown';
COMMENT ON TABLE overtime_records IS 'Overtime hours with SA rate multipliers and approval workflow';
COMMENT ON TABLE shift_swap_requests IS 'Shift swap requests between employees with manager approval';
