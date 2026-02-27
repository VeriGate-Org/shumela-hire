-- V015: Time & Attendance Module Tables (SQL Server)
-- Creates geofences, shifts, shift_patterns, shift_schedules,
-- attendance_records, overtime_records, shift_swap_requests

-- =====================================================
-- Geofences — office/site location boundaries
-- =====================================================
CREATE TABLE geofences (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    center_latitude DECIMAL(10, 7) NOT NULL,
    center_longitude DECIMAL(10, 7) NOT NULL,
    radius_meters INT NOT NULL,
    address NVARCHAR(500),
    city NVARCHAR(100),
    province NVARCHAR(100),
    site_code NVARCHAR(50),
    geofence_type NVARCHAR(30) NOT NULL DEFAULT 'CIRCLE',
    polygon_coordinates NVARCHAR(MAX),
    is_active BIT NOT NULL DEFAULT 1,
    enforce_on_clock_in BIT NOT NULL DEFAULT 1,
    enforce_on_clock_out BIT NOT NULL DEFAULT 0,
    allow_override_with_reason BIT NOT NULL DEFAULT 1,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT chk_geofence_type CHECK (geofence_type IN ('CIRCLE', 'POLYGON')),
    CONSTRAINT chk_geofence_radius CHECK (radius_meters > 0)
);

CREATE INDEX idx_geofences_tenant ON geofences(tenant_id);
CREATE INDEX idx_geofences_active ON geofences(is_active) WHERE is_active = 1;
CREATE INDEX idx_geofences_site_code ON geofences(site_code) WHERE site_code IS NOT NULL;
GO

-- =====================================================
-- Shifts — shift definitions (e.g., Morning, Night)
-- =====================================================
CREATE TABLE shifts (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    code NVARCHAR(20),
    description NVARCHAR(MAX),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_duration_minutes INT NOT NULL DEFAULT 60,
    total_hours DECIMAL(4, 2),
    grace_period_minutes INT NOT NULL DEFAULT 15,
    is_overnight BIT NOT NULL DEFAULT 0,
    is_active BIT NOT NULL DEFAULT 1,
    color NVARCHAR(50),
    geofence_id BIGINT,
    min_hours_for_overtime DECIMAL(4, 2),
    department NVARCHAR(200),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_shifts_geofence FOREIGN KEY (geofence_id) REFERENCES geofences(id),
    CONSTRAINT chk_shift_break CHECK (break_duration_minutes >= 0)
);

CREATE INDEX idx_shifts_tenant ON shifts(tenant_id);
CREATE INDEX idx_shifts_active ON shifts(is_active) WHERE is_active = 1;
CREATE INDEX idx_shifts_department ON shifts(department) WHERE department IS NOT NULL;
GO

-- =====================================================
-- Shift Patterns — recurring patterns (e.g., 5-on-2-off)
-- =====================================================
CREATE TABLE shift_patterns (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(MAX),
    days_on INT NOT NULL,
    days_off INT NOT NULL,
    cycle_length_days INT NOT NULL,
    pattern_definition NVARCHAR(MAX),
    default_shift_id BIGINT,
    is_active BIT NOT NULL DEFAULT 1,
    department NVARCHAR(200),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

    CONSTRAINT fk_shift_patterns_shift FOREIGN KEY (default_shift_id) REFERENCES shifts(id),
    CONSTRAINT chk_pattern_days_on CHECK (days_on > 0),
    CONSTRAINT chk_pattern_days_off CHECK (days_off >= 0),
    CONSTRAINT chk_pattern_cycle CHECK (cycle_length_days > 0)
);

CREATE INDEX idx_shift_patterns_tenant ON shift_patterns(tenant_id);
CREATE INDEX idx_shift_patterns_active ON shift_patterns(is_active) WHERE is_active = 1;
GO

-- =====================================================
-- Shift Schedules — employee shift assignments per day
-- =====================================================
CREATE TABLE shift_schedules (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    shift_id BIGINT NOT NULL,
    schedule_date DATE NOT NULL,
    status NVARCHAR(30) NOT NULL DEFAULT 'SCHEDULED',
    shift_pattern_id BIGINT,
    assigned_by BIGINT,
    notes NVARCHAR(MAX),
    is_published BIT NOT NULL DEFAULT 0,
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

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
CREATE INDEX idx_schedules_published ON shift_schedules(is_published) WHERE is_published = 1;
GO

-- =====================================================
-- Attendance Records — daily clock-in/out records
-- =====================================================
CREATE TABLE attendance_records (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,
    clock_in_time DATETIME2,
    clock_out_time DATETIME2,
    clock_in_method NVARCHAR(30),
    clock_out_method NVARCHAR(30),

    -- Geolocation
    clock_in_latitude DECIMAL(10, 7),
    clock_in_longitude DECIMAL(10, 7),
    clock_out_latitude DECIMAL(10, 7),
    clock_out_longitude DECIMAL(10, 7),
    clock_in_within_geofence BIT,
    clock_out_within_geofence BIT,
    geofence_id BIGINT,

    -- Network
    clock_in_ip_address NVARCHAR(50),
    clock_out_ip_address NVARCHAR(50),

    -- Scheduled times
    scheduled_start_time TIME,
    scheduled_end_time TIME,

    -- Computed hours
    total_hours_worked DECIMAL(5, 2),
    regular_hours DECIMAL(5, 2),
    overtime_hours DECIMAL(5, 2),
    break_duration_minutes INT,

    -- Punctuality
    is_late_arrival BIT DEFAULT 0,
    late_minutes INT,
    is_early_departure BIT DEFAULT 0,
    early_departure_minutes INT,

    status NVARCHAR(30) NOT NULL DEFAULT 'CLOCKED_IN',
    shift_id BIGINT,
    notes NVARCHAR(MAX),
    approved_by BIGINT,
    approved_at DATETIME2,
    device_info NVARCHAR(500),

    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

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
CREATE INDEX idx_attendance_late ON attendance_records(is_late_arrival) WHERE is_late_arrival = 1;
GO

-- =====================================================
-- Overtime Records — SA BCEA–compliant tracking
-- =====================================================
CREATE TABLE overtime_records (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    attendance_record_id BIGINT,
    overtime_date DATE NOT NULL,
    overtime_hours DECIMAL(5, 2) NOT NULL,
    overtime_type NVARCHAR(30) NOT NULL,
    rate_multiplier DECIMAL(4, 2) NOT NULL,
    status NVARCHAR(30) NOT NULL DEFAULT 'PENDING',
    is_pre_approved BIT NOT NULL DEFAULT 0,
    reason NVARCHAR(MAX),
    approved_by BIGINT,
    approved_at DATETIME2,
    rejection_reason NVARCHAR(MAX),
    weekly_overtime_total DECIMAL(5, 2),
    monthly_overtime_total DECIMAL(6, 2),
    exceeds_bcea_weekly_limit BIT DEFAULT 0,
    bcea_weekly_limit_hours DECIMAL(4, 2),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

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
CREATE INDEX idx_overtime_bcea ON overtime_records(exceeds_bcea_weekly_limit) WHERE exceeds_bcea_weekly_limit = 1;
GO

-- =====================================================
-- Shift Swap Requests
-- =====================================================
CREATE TABLE shift_swap_requests (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    requester_id BIGINT NOT NULL,
    requester_schedule_id BIGINT NOT NULL,
    target_employee_id BIGINT NOT NULL,
    target_schedule_id BIGINT,
    swap_date DATE NOT NULL,
    target_date DATE,
    status NVARCHAR(30) NOT NULL DEFAULT 'PENDING_TARGET',
    reason NVARCHAR(MAX),
    target_response_at DATETIME2,
    target_response_notes NVARCHAR(MAX),
    manager_approved_by BIGINT,
    manager_approved_at DATETIME2,
    manager_notes NVARCHAR(MAX),
    tenant_id NVARCHAR(50) NOT NULL,
    created_at DATETIME2 NOT NULL DEFAULT GETDATE(),
    updated_at DATETIME2,

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
GO

-- =====================================================
-- RLS Policies using SESSION_CONTEXT
-- =====================================================

-- Geofences RLS
CREATE FUNCTION dbo.fn_geofences_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.geofences_policy
    ADD FILTER PREDICATE dbo.fn_geofences_filter(tenant_id) ON dbo.geofences,
    ADD BLOCK PREDICATE dbo.fn_geofences_filter(tenant_id) ON dbo.geofences
    WITH (STATE = ON);
GO

-- Shifts RLS
CREATE FUNCTION dbo.fn_shifts_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.shifts_policy
    ADD FILTER PREDICATE dbo.fn_shifts_filter(tenant_id) ON dbo.shifts,
    ADD BLOCK PREDICATE dbo.fn_shifts_filter(tenant_id) ON dbo.shifts
    WITH (STATE = ON);
GO

-- Shift Patterns RLS
CREATE FUNCTION dbo.fn_shift_patterns_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.shift_patterns_policy
    ADD FILTER PREDICATE dbo.fn_shift_patterns_filter(tenant_id) ON dbo.shift_patterns,
    ADD BLOCK PREDICATE dbo.fn_shift_patterns_filter(tenant_id) ON dbo.shift_patterns
    WITH (STATE = ON);
GO

-- Shift Schedules RLS
CREATE FUNCTION dbo.fn_shift_schedules_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.shift_schedules_policy
    ADD FILTER PREDICATE dbo.fn_shift_schedules_filter(tenant_id) ON dbo.shift_schedules,
    ADD BLOCK PREDICATE dbo.fn_shift_schedules_filter(tenant_id) ON dbo.shift_schedules
    WITH (STATE = ON);
GO

-- Attendance Records RLS
CREATE FUNCTION dbo.fn_attendance_records_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.attendance_records_policy
    ADD FILTER PREDICATE dbo.fn_attendance_records_filter(tenant_id) ON dbo.attendance_records,
    ADD BLOCK PREDICATE dbo.fn_attendance_records_filter(tenant_id) ON dbo.attendance_records
    WITH (STATE = ON);
GO

-- Overtime Records RLS
CREATE FUNCTION dbo.fn_overtime_records_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.overtime_records_policy
    ADD FILTER PREDICATE dbo.fn_overtime_records_filter(tenant_id) ON dbo.overtime_records,
    ADD BLOCK PREDICATE dbo.fn_overtime_records_filter(tenant_id) ON dbo.overtime_records
    WITH (STATE = ON);
GO

-- Shift Swap Requests RLS
CREATE FUNCTION dbo.fn_shift_swap_requests_filter(@tenant_id NVARCHAR(50))
RETURNS TABLE WITH SCHEMABINDING AS
RETURN SELECT 1 AS result WHERE @tenant_id = CAST(SESSION_CONTEXT(N'TenantId') AS NVARCHAR(50));
GO

CREATE SECURITY POLICY dbo.shift_swap_requests_policy
    ADD FILTER PREDICATE dbo.fn_shift_swap_requests_filter(tenant_id) ON dbo.shift_swap_requests,
    ADD BLOCK PREDICATE dbo.fn_shift_swap_requests_filter(tenant_id) ON dbo.shift_swap_requests
    WITH (STATE = ON);
GO
