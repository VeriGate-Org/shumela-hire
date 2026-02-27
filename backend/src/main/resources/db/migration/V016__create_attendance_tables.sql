-- V016: Time & Attendance Module Tables
-- Creates geofences, shifts, shift_patterns, shift_schedules, attendance_records,
-- overtime_records, shift_swap_requests

-- =====================================================
-- Geofences table
-- =====================================================
CREATE TABLE geofences (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    site VARCHAR(200),
    geofence_type VARCHAR(20) NOT NULL DEFAULT 'RADIUS',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    radius_meters DOUBLE PRECISION,
    polygon_coordinates TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_geofence_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_geofence_type CHECK (geofence_type IN ('RADIUS', 'POLYGON'))
);

CREATE INDEX idx_geofences_tenant_id ON geofences(tenant_id);
CREATE INDEX idx_geofences_active ON geofences(is_active);
CREATE INDEX idx_geofences_site ON geofences(site);

-- =====================================================
-- Shifts table
-- =====================================================
CREATE TABLE shifts (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_duration_minutes INTEGER NOT NULL DEFAULT 0,
    grace_period_minutes INTEGER NOT NULL DEFAULT 0,
    is_night_shift BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    color VARCHAR(7),

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_shift_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_shift_code_tenant UNIQUE (code, tenant_id)
);

CREATE INDEX idx_shifts_tenant_id ON shifts(tenant_id);
CREATE INDEX idx_shifts_active ON shifts(is_active);

-- =====================================================
-- Shift Patterns table (rotation definitions)
-- =====================================================
CREATE TABLE shift_patterns (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    rotation_days INTEGER NOT NULL,
    pattern_definition TEXT NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_shift_pattern_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_shift_patterns_tenant_id ON shift_patterns(tenant_id);
CREATE INDEX idx_shift_patterns_active ON shift_patterns(is_active);

-- =====================================================
-- Shift Schedules table (employee-shift-date assignments)
-- =====================================================
CREATE TABLE shift_schedules (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    shift_pattern_id BIGINT,
    status VARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_schedule_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_schedule_shift FOREIGN KEY (shift_id) REFERENCES shifts(id),
    CONSTRAINT fk_schedule_pattern FOREIGN KEY (shift_pattern_id) REFERENCES shift_patterns(id),
    CONSTRAINT fk_schedule_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT uk_schedule_employee_date UNIQUE (employee_id, schedule_date, tenant_id),
    CONSTRAINT chk_schedule_status CHECK (status IN ('SCHEDULED', 'SWAPPED', 'CANCELLED'))
);

CREATE INDEX idx_schedules_tenant_id ON shift_schedules(tenant_id);
CREATE INDEX idx_schedules_employee ON shift_schedules(employee_id);
CREATE INDEX idx_schedules_date ON shift_schedules(schedule_date);
CREATE INDEX idx_schedules_shift ON shift_schedules(shift_id);

-- =====================================================
-- Attendance Records table
-- =====================================================
CREATE TABLE attendance_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_schedule_id BIGINT,
    record_date DATE NOT NULL,
    clock_in_time TIMESTAMP,
    clock_out_time TIMESTAMP,
    clock_in_latitude DOUBLE PRECISION,
    clock_in_longitude DOUBLE PRECISION,
    clock_out_latitude DOUBLE PRECISION,
    clock_out_longitude DOUBLE PRECISION,
    clock_in_geofence_id BIGINT,
    clock_out_geofence_id BIGINT,
    clock_in_within_geofence BOOLEAN,
    clock_out_within_geofence BOOLEAN,
    status VARCHAR(30) NOT NULL DEFAULT 'PRESENT',
    total_hours NUMERIC(5, 2),
    regular_hours NUMERIC(5, 2),
    overtime_hours NUMERIC(5, 2),
    break_minutes INTEGER DEFAULT 0,
    is_late_arrival BOOLEAN NOT NULL DEFAULT FALSE,
    is_early_departure BOOLEAN NOT NULL DEFAULT FALSE,
    late_minutes INTEGER DEFAULT 0,
    early_departure_minutes INTEGER DEFAULT 0,
    notes TEXT,
    auto_clocked_out BOOLEAN NOT NULL DEFAULT FALSE,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_attendance_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_attendance_schedule FOREIGN KEY (shift_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_attendance_cin_geofence FOREIGN KEY (clock_in_geofence_id) REFERENCES geofences(id),
    CONSTRAINT fk_attendance_cout_geofence FOREIGN KEY (clock_out_geofence_id) REFERENCES geofences(id),
    CONSTRAINT fk_attendance_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_attendance_status CHECK (status IN ('PRESENT', 'ABSENT', 'LATE', 'HALF_DAY', 'ON_LEAVE', 'PUBLIC_HOLIDAY'))
);

CREATE INDEX idx_attendance_tenant_id ON attendance_records(tenant_id);
CREATE INDEX idx_attendance_employee ON attendance_records(employee_id);
CREATE INDEX idx_attendance_date ON attendance_records(record_date);
CREATE INDEX idx_attendance_status ON attendance_records(status);
CREATE INDEX idx_attendance_employee_date ON attendance_records(employee_id, record_date);

-- =====================================================
-- Overtime Records table
-- =====================================================
CREATE TABLE overtime_records (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_record_id BIGINT,
    overtime_date DATE NOT NULL,
    overtime_type VARCHAR(30) NOT NULL,
    hours NUMERIC(5, 2) NOT NULL,
    rate_multiplier NUMERIC(4, 2) NOT NULL DEFAULT 1.5,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    requested_by VARCHAR(255),
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    rejection_reason TEXT,
    notes TEXT,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_overtime_employee FOREIGN KEY (employee_id) REFERENCES employees(id),
    CONSTRAINT fk_overtime_attendance FOREIGN KEY (attendance_record_id) REFERENCES attendance_records(id),
    CONSTRAINT fk_overtime_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_overtime_type CHECK (overtime_type IN ('WEEKDAY', 'WEEKEND', 'PUBLIC_HOLIDAY', 'NIGHT')),
    CONSTRAINT chk_overtime_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PROCESSED'))
);

CREATE INDEX idx_overtime_tenant_id ON overtime_records(tenant_id);
CREATE INDEX idx_overtime_employee ON overtime_records(employee_id);
CREATE INDEX idx_overtime_date ON overtime_records(overtime_date);
CREATE INDEX idx_overtime_status ON overtime_records(status);
CREATE INDEX idx_overtime_type ON overtime_records(overtime_type);

-- =====================================================
-- Shift Swap Requests table
-- =====================================================
CREATE TABLE shift_swap_requests (
    id BIGSERIAL PRIMARY KEY,
    requester_employee_id BIGINT NOT NULL,
    target_employee_id BIGINT NOT NULL,
    requester_schedule_id BIGINT NOT NULL,
    target_schedule_id BIGINT NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    reason TEXT,
    approved_by VARCHAR(255),
    approved_at TIMESTAMP,
    rejection_reason TEXT,

    -- Multi-tenancy
    tenant_id VARCHAR(50) NOT NULL,

    -- Timestamps
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_swap_requester FOREIGN KEY (requester_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_target FOREIGN KEY (target_employee_id) REFERENCES employees(id),
    CONSTRAINT fk_swap_requester_schedule FOREIGN KEY (requester_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_target_schedule FOREIGN KEY (target_schedule_id) REFERENCES shift_schedules(id),
    CONSTRAINT fk_swap_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT chk_swap_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED'))
);

CREATE INDEX idx_swap_tenant_id ON shift_swap_requests(tenant_id);
CREATE INDEX idx_swap_requester ON shift_swap_requests(requester_employee_id);
CREATE INDEX idx_swap_target ON shift_swap_requests(target_employee_id);
CREATE INDEX idx_swap_status ON shift_swap_requests(status);
