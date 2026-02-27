-- V015: Time & Attendance Module Tables
-- Creates geofences, shifts, shift_patterns, shift_schedules,
-- attendance_records, overtime_records, shift_swap_requests

-- =====================================================
-- Geofences — office/site location boundaries
-- =====================================================
CREATE TABLE geofences (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    center_latitude NUMERIC(10, 7) NOT NULL,
    center_longitude NUMERIC(10, 7) NOT NULL,
    radius_meters INTEGER NOT NULL,
    address VARCHAR(500),
    city VARCHAR(100),
    province VARCHAR(100),
    site_code VARCHAR(50),
    geofence_type VARCHAR(30) NOT NULL DEFAULT 'CIRCLE',
    polygon_coordinates TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    enforce_on_clock_in BOOLEAN NOT NULL DEFAULT TRUE,
    enforce_on_clock_out BOOLEAN NOT NULL DEFAULT FALSE,
    allow_override_with_reason BOOLEAN NOT NULL DEFAULT TRUE,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT chk_geofence_type CHECK (geofence_type IN ('CIRCLE', 'POLYGON')),
    CONSTRAINT chk_geofence_radius CHECK (radius_meters > 0)
);

CREATE INDEX idx_geofences_tenant ON geofences(tenant_id);
CREATE INDEX idx_geofences_active ON geofences(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_geofences_site_code ON geofences(site_code) WHERE site_code IS NOT NULL;

-- =====================================================
-- Shifts — shift definitions (e.g., Morning, Night)
-- =====================================================
CREATE TABLE shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20),
    description TEXT,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_duration_minutes INTEGER NOT NULL DEFAULT 60,
    total_hours NUMERIC(4, 2),
    grace_period_minutes INTEGER NOT NULL DEFAULT 15,
    is_overnight BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    color VARCHAR(50),
    geofence_id BIGINT,
    min_hours_for_overtime NUMERIC(4, 2),
    department VARCHAR(200),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_shifts_geofence FOREIGN KEY (geofence_id) REFERENCES geofences(id),
    CONSTRAINT chk_shift_break CHECK (break_duration_minutes >= 0)
);

CREATE INDEX idx_shifts_tenant ON shifts(tenant_id);
CREATE INDEX idx_shifts_active ON shifts(is_active) WHERE is_active = TRUE;
CREATE INDEX idx_shifts_department ON shifts(department) WHERE department IS NOT NULL;

-- =====================================================
-- Shift Patterns — recurring patterns (e.g., 5-on-2-off)
-- =====================================================
CREATE TABLE shift_patterns (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    days_on INTEGER NOT NULL,
    days_off INTEGER NOT NULL,
    cycle_length_days INTEGER NOT NULL,
    pattern_definition TEXT,
    default_shift_id BIGINT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    department VARCHAR(200),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_shift_patterns_shift FOREIGN KEY (default_shift_id) REFERENCES shifts(id),
    CONSTRAINT chk_pattern_days_on CHECK (days_on > 0),
    CONSTRAINT chk_pattern_days_off CHECK (days_off >= 0),
    CONSTRAINT chk_pattern_cycle CHECK (cycle_length_days > 0)
);

CREATE INDEX idx_shift_patterns_tenant ON shift_patterns(tenant_id);
CREATE INDEX idx_shift_patterns_active ON shift_patterns(is_active) WHERE is_active = TRUE;

-- =====================================================
-- Shift Schedules — employee shift assignments per day
-- =====================================================
CREATE TABLE shift_schedules (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    shift_pattern_id BIGINT,
    assigned_by BIGINT,
    notes TEXT,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_schedules_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_schedules_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT fk_schedules_pattern FOREIGN KEY (shift_pattern_id) REFERENCES shift_patterns(id),
    CONSTRAINT fk_schedules_assigned_by FOREIGN KEY (assigned_by) REFERENCES employees(id),
    CONSTRAINT chk_schedule_status CHECK (status IN ('SCHEDULED', 'SWAPPED', 'CANCELLED', 'COMPLETED')),
    CONSTRAINT uq_schedule_employee_date UNIQUE (employee_id, schedule_date, tenant_id)
);

CREATE INDEX idx_schedules_tenant ON shift_schedules(tenant_id);
CREATE INDEX idx_schedules_employee ON shift_schedules(employee_id);
CREATE INDEX idx_schedules_date ON shift_schedules(schedule_date);
CREATE INDEX idx_schedules_shift ON shift_schedules(shift_id);
CREATE INDEX idx_schedules_status ON shift_schedules(status);
CREATE INDEX idx_schedules_published ON shift_schedules(is_published) WHERE is_published = TRUE;

-- =====================================================
-- Attendance Records — daily clock-in/out records
-- =====================================================
CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    clock_in_time TIMESTAMP,
    clock_out_time TIMESTAMP,
    clock_in_method VARCHAR(30),
    clock_out_method VARCHAR(30),

    -- Geolocation
    clock_in_latitude NUMERIC(10, 7),
    clock_in_longitude NUMERIC(10, 7),
    clock_out_latitude NUMERIC(10, 7),
    clock_out_longitude NUMERIC(10, 7),
    clock_in_within_geofence BOOLEAN,
    clock_out_within_geofence BOOLEAN,
    geofence_id BIGINT,

    -- Network
    clock_in_ip_address VARCHAR(50),
    clock_out_ip_address VARCHAR(50),

    -- Scheduled times
    scheduled_start_time TIME,
    scheduled_end_time TIME,

    -- Computed hours
    total_hours_worked NUMERIC(5, 2),
    regular_hours NUMERIC(5, 2),
    overtime_hours NUMERIC(5, 2),
    break_duration_minutes INTEGER,

    -- Punctuality
    is_late_arrival BOOLEAN DEFAULT FALSE,
    late_minutes INTEGER,
    is_early_departure BOOLEAN DEFAULT FALSE,
    early_departure_minutes INTEGER,

    status VARCHAR(30) NOT NULL DEFAULT 'CLOCKED_IN',
    shift_id BIGINT,
    notes TEXT,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    device_info VARCHAR(500),

    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_attendance_geofence FOREIGN KEY (geofence_id) REFERENCES geofences(id),
    CONSTRAINT fk_attendance_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT fk_attendance_approved_by FOREIGN KEY (approved_by) REFERENCES employees(id),
    CONSTRAINT chk_attendance_method CHECK (clock_in_method IN ('BIOMETRIC', 'WEB_PORTAL', 'MOBILE_APP', 'KIOSK', 'MANUAL', 'AD_LOGIN')),
    CONSTRAINT chk_attendance_status CHECK (status IN ('CLOCKED_IN', 'CLOCKED_OUT', 'ON_BREAK', 'ABSENT', 'LEAVE', 'HOLIDAY', 'PENDING_APPROVAL', 'APPROVED', 'REJECTED'))
);

CREATE INDEX idx_attendance_tenant ON attendance_records(tenant_id);
CREATE INDEX idx_attendance_employee ON attendance_records(employee_id);
CREATE INDEX idx_attendance_date ON attendance_records(attendance_date);
CREATE INDEX idx_attendance_status ON attendance_records(status);
CREATE INDEX idx_attendance_emp_date ON attendance_records(employee_id, attendance_date);
CREATE INDEX idx_attendance_late ON attendance_records(is_late_arrival) WHERE is_late_arrival = TRUE;

-- =====================================================
-- Overtime Records — SA BCEA–compliant tracking
-- =====================================================
CREATE TABLE overtime_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_record_id BIGINT,
    overtime_date DATE NOT NULL,
    overtime_hours NUMERIC(5, 2) NOT NULL,
    overtime_type VARCHAR(30) NOT NULL,
    rate_multiplier NUMERIC(4, 2) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    is_pre_approved BOOLEAN NOT NULL DEFAULT FALSE,
    reason TEXT,
    approved_by BIGINT,
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    weekly_overtime_total NUMERIC(5, 2),
    monthly_overtime_total NUMERIC(6, 2),
    exceeds_bcea_weekly_limit BOOLEAN DEFAULT FALSE,
    bcea_weekly_limit_hours NUMERIC(4, 2),
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_overtime_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_overtime_attendance FOREIGN KEY (attendance_record_id) REFERENCES attendance_records(id),
    CONSTRAINT fk_overtime_approved_by FOREIGN KEY (approved_by) REFERENCES employees(id),
    CONSTRAINT chk_overtime_type CHECK (overtime_type IN ('WEEKDAY', 'SATURDAY', 'SUNDAY', 'PUBLIC_HOLIDAY')),
    CONSTRAINT chk_overtime_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT chk_overtime_hours CHECK (overtime_hours > 0)
);

CREATE INDEX idx_overtime_tenant ON overtime_records(tenant_id);
CREATE INDEX idx_overtime_employee ON overtime_records(employee_id);
CREATE INDEX idx_overtime_date ON overtime_records(overtime_date);
CREATE INDEX idx_overtime_status ON overtime_records(status);
CREATE INDEX idx_overtime_emp_date ON overtime_records(employee_id, overtime_date);
CREATE INDEX idx_overtime_bcea ON overtime_records(exceeds_bcea_weekly_limit) WHERE exceeds_bcea_weekly_limit = TRUE;

-- =====================================================
-- Shift Swap Requests
-- =====================================================
CREATE TABLE shift_swap_requests (
    id BIGSERIAL PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    requester_schedule_id BIGINT NOT NULL,
    target_employee_id BIGINT NOT NULL,
    target_schedule_id BIGINT,
    swap_date DATE NOT NULL,
    target_date DATE,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING_TARGET',
    reason TEXT,
    target_response_at TIMESTAMP,
    target_response_notes TEXT,
    manager_approved_by BIGINT,
    manager_approved_at TIMESTAMP,
    manager_notes TEXT,
    tenant_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_swap_requester FOREIGN KEY (requester_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_requester_schedule FOREIGN KEY (requester_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_target_employee FOREIGN KEY (target_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_target_schedule FOREIGN KEY (target_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_manager FOREIGN KEY (manager_approved_by) REFERENCES employees(id),
    CONSTRAINT chk_swap_status CHECK (status IN ('PENDING_TARGET', 'TARGET_ACCEPTED', 'TARGET_REJECTED', 'PENDING_MANAGER', 'APPROVED', 'REJECTED', 'CANCELLED'))
);

CREATE INDEX idx_swap_tenant ON shift_swap_requests(tenant_id);
CREATE INDEX idx_swap_requester ON shift_swap_requests(requester_id);
CREATE INDEX idx_swap_target ON shift_swap_requests(target_employee_id);
CREATE INDEX idx_swap_status ON shift_swap_requests(status);
CREATE INDEX idx_swap_date ON shift_swap_requests(swap_date);

-- =====================================================
-- RLS Policies
-- =====================================================
ALTER TABLE geofences ENABLE ROW LEVEL SECURITY;
CREATE POLICY geofences_tenant_policy ON geofences
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE shifts ENABLE ROW LEVEL SECURITY;
CREATE POLICY shifts_tenant_policy ON shifts
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE shift_patterns ENABLE ROW LEVEL SECURITY;
CREATE POLICY shift_patterns_tenant_policy ON shift_patterns
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE shift_schedules ENABLE ROW LEVEL SECURITY;
CREATE POLICY shift_schedules_tenant_policy ON shift_schedules
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE attendance_records ENABLE ROW LEVEL SECURITY;
CREATE POLICY attendance_records_tenant_policy ON attendance_records
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE overtime_records ENABLE ROW LEVEL SECURITY;
CREATE POLICY overtime_records_tenant_policy ON overtime_records
    USING (tenant_id = current_setting('app.current_tenant', TRUE));

ALTER TABLE shift_swap_requests ENABLE ROW LEVEL SECURITY;
CREATE POLICY shift_swap_requests_tenant_policy ON shift_swap_requests
    USING (tenant_id = current_setting('app.current_tenant', TRUE));
