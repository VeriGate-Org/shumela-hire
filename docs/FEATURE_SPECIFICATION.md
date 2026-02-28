# ShumelaHire Feature Specification
## Cornerstone OnDemand Parity + uThukela Water RFP Compliance

**Document Version:** 1.0
**Date:** 27 February 2026
**Product:** ShumelaHire — Cloud-based Human Resource Management System
**By:** Arthmatic DevWorks

---

## Table of Contents

1. [Executive Summary](#1-executive-summary)
2. [Current State Summary](#2-current-state-summary)
3. [Module Inventory Matrix](#3-module-inventory-matrix)
4. [Module Specifications](#4-module-specifications)
   - 4.1 [Core HR & Employee Lifecycle](#41-core-hr--employee-lifecycle)
   - 4.2 [Leave Administration](#42-leave-administration)
   - 4.3 [Time & Attendance](#43-time--attendance)
   - 4.4 [Performance Management (Enhancement)](#44-performance-management-enhancement)
   - 4.5 [Goal Management](#45-goal-management)
   - 4.6 [360 Feedback](#46-360-feedback)
   - 4.7 [Competency & Skills Framework](#47-competency--skills-framework)
   - 4.8 [Learning Management System (LMS)](#48-learning-management-system-lms)
   - 4.9 [Content Authoring](#49-content-authoring)
   - 4.10 [Succession Planning](#410-succession-planning)
   - 4.11 [Employee Engagement](#411-employee-engagement)
   - 4.12 [Compensation Management](#412-compensation-management)
   - 4.13 [Org Management](#413-org-management)
   - 4.14 [Recruitment & Onboarding (Enhancement)](#414-recruitment--onboarding-enhancement)
   - 4.15 [Analytics & Reporting (Enhancement)](#415-analytics--reporting-enhancement)
   - 4.16 [Compliance & Security (Enhancement)](#416-compliance--security-enhancement)
   - 4.17 [Payroll Integration — Sage Platform](#417-payroll-integration--sage-platform)
5. [Platform Capabilities](#5-platform-capabilities)
   - 5.1 [Mobile Application (iOS/Android)](#51-mobile-application-iosandroid)
   - 5.2 [Hybrid Deployment Architecture](#52-hybrid-deployment-architecture)
   - 5.3 [Active Directory Integration](#53-active-directory-integration)
   - 5.4 [Low-Bandwidth Optimization](#54-low-bandwidth-optimization)
   - 5.5 [Multilingual Support](#55-multilingual-support)
   - 5.6 [Marketplace & Ecosystem](#56-marketplace--ecosystem)
6. [Module Dependency Map](#6-module-dependency-map)
7. [Implementation Phases](#7-implementation-phases)

---

## 1. Executive Summary

This document specifies every module and feature required to:

1. **Match Cornerstone OnDemand** — transforming ShumelaHire from a Talent Acquisition Platform (ATS) into a full-spectrum Human Capital Management (HCM) suite covering learning, performance, talent, compensation, and workforce management.
2. **Comply with uThukela Water RFP HR2026-BID-007** — meeting all 11 functional modules, the hybrid deployment architecture, Sage 300 People / Sage Evolution ERP integration, POPIA/LRA compliance, and mobile app requirements.

The specification covers **17 functional modules** and **6 platform capabilities**, organized to align with ShumelaHire's existing architecture:

- **Frontend:** Next.js 15 (App Router), React 19, TypeScript, Tailwind CSS, SWR
- **Backend:** Spring Boot 3.4, Java 17, PostgreSQL (primary), Flyway migrations
- **Auth:** AWS Cognito (production), JWT (dev), SAML/Azure AD SSO
- **Infrastructure:** AWS (S3, SES, SQS), Docker, multi-tenant with RLS
- **AI:** Claude / OpenAI provider abstraction (existing 13 AI capabilities)
- **Real-time:** WebSocket service with auto-reconnection

Each module specification includes: features, data entities, API endpoints, frontend pages/components, integrations, and AI capabilities — all following existing patterns and conventions.

---

## 2. Current State Summary

### What ShumelaHire Has Today

| Domain | Capability | Maturity |
|--------|-----------|----------|
| **Recruitment** | Full ATS — requisitions, job posting (5 boards), pipeline, screening, shortlisting | Production |
| **Interviews** | Scheduling, calendar, feedback, multi-round, availability checking | Production |
| **Offers** | Full lifecycle — drafting, approval, negotiation, e-signature (DocuSign), versioning | Production |
| **Background Checks** | Dots Africa integration — 12 check types, consent, webhook-driven | Production |
| **Performance** | Basic performance reviews, analytics, dashboards | Partial |
| **Training** | Basic onboarding wizards only | Minimal |
| **Analytics** | Custom report builder, scheduling, multi-format export, recruitment KPIs | Production |
| **AI** | 13 capabilities — CV screening, ranking, JD generation, bias detection, smart search, etc. | Production |
| **Compliance** | GDPR/POPIA compliance manager, RBAC (7 roles), audit logging | Production |
| **Integrations** | LinkedIn, Indeed, PNet, CareerJunction, DocuSign, SAP Payroll, MS Teams, Outlook | Production |
| **Multi-tenancy** | Tenant isolation with RLS, tenant-aware entities, dynamic routing | Production |
| **Notifications** | Multi-channel (in-app, email, SMS, push), WebSocket real-time, scheduling | Production |

### What ShumelaHire Does NOT Have

- Core HR / Employee lifecycle management (beyond applicants)
- Leave administration
- Time & attendance / geofencing
- Goal management (OKRs)
- 360 feedback
- Competency & skills framework
- Learning management system (LMS)
- Content authoring tools
- Succession planning
- Employee engagement (surveys, recognition, wellness)
- Compensation management
- Org charts / workforce planning
- Sage 300 People / Sage Evolution ERP integration
- Native mobile app (iOS/Android)
- Hybrid on-premises deployment
- Active Directory native integration
- Multilingual support

---

## 3. Module Inventory Matrix

| # | Module | Status | RFP Section | Cornerstone Match | Priority |
|---|--------|--------|-------------|------------------|----------|
| 4.1 | Core HR & Employee Lifecycle | **New** | 2.1 Employee Profiles | Core HR | P0 |
| 4.2 | Leave Administration | **New** | 2.2 Leave Admin | — (Gap) | P0 |
| 4.3 | Time & Attendance | **New** | 2.5 Time & Attendance | — (Gap) | P0 |
| 4.4 | Performance Management | **Enhance** | 2.3 Performance | Performance Mgmt | P0 |
| 4.5 | Goal Management | **New** | 2.3 (Goals/KRAs) | Goals | P1 |
| 4.6 | 360 Feedback | **New** | 2.3 (360-degree) | 360 Feedback | P1 |
| 4.7 | Competency & Skills | **New** | 2.3 + 2.4 (Skill gaps) | Skills Edge | P1 |
| 4.8 | Learning Management (LMS) | **New** | 2.4 Training & Dev | LMS (Core) | P1 |
| 4.9 | Content Authoring | **New** | 2.4 (Training materials) | Content Studio | P2 |
| 4.10 | Succession Planning | **New** | — | Succession | P2 |
| 4.11 | Employee Engagement | **New** | 2.7 Engagement | — (Weak) | P1 |
| 4.12 | Compensation Management | **New** | 2.10 (Partial) | Compensation | P2 |
| 4.13 | Org Management | **New** | — (Implicit) | Org Management | P2 |
| 4.14 | Recruitment & Onboarding | **Enhance** | 2.6 Recruitment | Recruiting | P0 |
| 4.15 | Analytics & Reporting | **Enhance** | 2.8 Analytics | Analytics | P1 |
| 4.16 | Compliance & Security | **Enhance** | 2.9 Compliance | Compliance | P0 |
| 4.17 | Payroll — Sage Platform | **New** | 2.10 + Section 4 | — (Gap) | P0 |
| 5.1 | Mobile App | **New** | 2.11 Mobile App | Mobile | P0 |
| 5.2 | Hybrid Deployment | **New** | Section 3 Infrastructure | — (Gap) | P0 |
| 5.3 | Active Directory Integration | **New** | 3.1 (AD/SSO) | Partial (SAML) | P0 |
| 5.4 | Low-Bandwidth Optimization | **New** | 3.2 (Network) | — (Gap) | P1 |
| 5.5 | Multilingual Support | **New** | UX Section | Limited | P2 |
| 5.6 | Marketplace & Ecosystem | **New** | — | Marketplace | P3 |

**Priority Legend:** P0 = RFP-critical (must-have for bid), P1 = High (Cornerstone parity + RFP value), P2 = Medium (Cornerstone parity), P3 = Future (Cornerstone advanced)

---

## 4. Module Specifications

---

### 4.1 Core HR & Employee Lifecycle

**Category:** New Module
**RFP:** Section 2.1 — Employee Profiles Module
**Cornerstone:** Core HR (note: Cornerstone itself is weak here — it relies on external HRIS)
**Priority:** P0

#### Description

Transform ShumelaHire from an applicant-centric system to a full employee lifecycle platform. Currently, the `Applicant` entity tracks candidates; this module introduces an `Employee` entity that carries the person from hire through their entire employment lifecycle. When an applicant is hired (offer accepted), their record transitions into an employee record.

#### Features

**F-4.1.1 Employee Master Record**
- Comprehensive employee profile: personal details, contact info, emergency contacts, banking details (encrypted), tax number
- Employment record: job title, department, division, cost centre, reporting line, employment type, grade/level
- Employment dates: hire date, probation end, confirmation date, termination date
- Custom fields: configurable fields for municipal-specific requirements (e.g., ward assignment, site allocation)
- Profile photo upload (S3 storage, following existing Document pattern)

**F-4.1.2 Document Management**
- Store employee documents: ID copies, qualifications, certifications, contracts, disciplinary records
- Document types: IDENTITY, QUALIFICATION, CERTIFICATION, CONTRACT, DISCIPLINARY, MEDICAL, TAX, OTHER
- Document expiry tracking with automated renewal reminders (via existing Notification system)
- Version history for documents that get updated (e.g., renewed certifications)
- Bulk document upload

**F-4.1.3 Employee Self-Service Portal**
- Employees can view and update personal details (address, phone, emergency contacts, banking)
- Update requests go through HR approval workflow before persisting
- View own documents, payslips (from Sage), leave balances
- View org chart position, reporting structure
- Download employment confirmation letters (auto-generated PDF via existing PDFBox)

**F-4.1.4 Applicant-to-Employee Transition**
- Automated conversion when offer status = SIGNED/ACCEPTED
- Carry forward: name, surname, email, phone, ID/passport, documents, demographics
- Assign employee number (configurable format, e.g., UTW-{YYYY}-{SEQ})
- Trigger onboarding workflow automatically
- Create user account with EMPLOYEE role (extend existing User entity)

**F-4.1.5 Employment Events**
- Track lifecycle events: hire, promotion, transfer, demotion, suspension, reinstatement, resignation, dismissal, retirement, contract end
- Each event creates an immutable `EmploymentEvent` record with reason, effective date, approved by
- Link events to approval workflows where required (promotion, transfer, termination)
- Event history visible on employee profile timeline

**F-4.1.6 Employee Directory**
- Searchable employee directory with filters (department, location, job title, status)
- Quick contact cards with email, phone, office location
- Org chart navigation from directory
- Privacy controls: hide sensitive fields based on viewer role

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Employee` | id, employeeNumber (unique), userId, applicantId (nullable), firstName, lastName, email, phone, idNumber (encrypted), taxNumber (encrypted), dateOfBirth, gender, race, disability, citizenshipStatus, maritalStatus, physicalAddress, postalAddress, emergencyContactName, emergencyContactPhone, emergencyContactRelation, bankName, bankAccountNumber (encrypted), bankBranchCode, bankAccountType, jobTitle, department, division, costCentre, grade, level, employmentType, reportingManagerId, hireDate, probationEndDate, confirmationDate, terminationDate, terminationReason, status (ACTIVE, PROBATION, SUSPENDED, TERMINATED, RESIGNED, RETIRED), profilePhotoUrl, tenantId, createdAt, updatedAt | ManyToOne: reportingManager (self-ref), OneToMany: employeeDocuments, employmentEvents, leaveBalances |
| `EmployeeDocument` | id, employeeId, type (enum), filename, url, fileSize, contentType, expiryDate, isVerified, verifiedBy, verifiedAt, version, tenantId, createdAt, updatedAt | ManyToOne: employee |
| `EmploymentEvent` | id, employeeId, eventType (enum), effectiveDate, reason, details, previousJobTitle, newJobTitle, previousDepartment, newDepartment, previousGrade, newGrade, previousReportingManagerId, newReportingManagerId, approvedBy, approvalDate, attachmentUrl, tenantId, createdAt | ManyToOne: employee |
| `CustomField` | id, entityType (EMPLOYEE, LEAVE, etc.), fieldName, fieldLabel, fieldType (TEXT, NUMBER, DATE, DROPDOWN, BOOLEAN), dropdownOptions (JSON), isRequired, isActive, displayOrder, tenantId | — |
| `CustomFieldValue` | id, customFieldId, entityId, entityType, value, tenantId, createdAt, updatedAt | ManyToOne: customField |

#### API Endpoints

```
POST   /api/employees                          — Create employee (manual or from applicant)
GET    /api/employees                          — List/search with pagination & filters
GET    /api/employees/{id}                     — Get employee profile
PUT    /api/employees/{id}                     — Update employee (HR)
PATCH  /api/employees/{id}/personal            — Self-service update (triggers approval)
GET    /api/employees/{id}/documents           — List documents
POST   /api/employees/{id}/documents           — Upload document
DELETE /api/employees/{id}/documents/{docId}   — Remove document
GET    /api/employees/{id}/events              — Employment event history
POST   /api/employees/{id}/events              — Record employment event
POST   /api/employees/from-applicant/{applicantId} — Convert applicant to employee
GET    /api/employees/directory                — Employee directory (limited fields)
GET    /api/employees/{id}/org-chart           — Org chart from employee
GET    /api/employees/export                   — Export employee data (CSV, XLSX)
GET    /api/custom-fields                      — List custom fields
POST   /api/custom-fields                      — Create custom field
PUT    /api/custom-fields/{id}                 — Update custom field
```

#### Frontend Pages

```
/employees                  — Employee directory & management console
/employees/[id]             — Employee profile detail view
/employees/[id]/documents   — Document management tab
/employees/[id]/events      — Employment history timeline
/employee-self-service      — Self-service portal (update personal info, view payslips, documents)
/admin/custom-fields        — Custom field configuration
```

#### Frontend Components

```
EmployeeProfile.tsx         — Comprehensive profile view with tabs
EmployeeDirectory.tsx       — Searchable directory with filters
EmployeeForm.tsx            — Create/edit employee form
EmploymentTimeline.tsx      — Visual timeline of employment events
DocumentManager.tsx         — Upload, view, track document expiry
SelfServicePortal.tsx       — Employee self-service dashboard
ApplicantToEmployeeWizard.tsx — Conversion wizard
CustomFieldBuilder.tsx      — Admin tool for configuring custom fields
```

#### AI Capabilities

- **AI Employee Data Enrichment** — Auto-populate employee fields from uploaded CV/documents during applicant-to-employee conversion
- **AI Document Classification** — Automatically classify uploaded documents by type

---

### 4.2 Leave Administration

**Category:** New Module
**RFP:** Section 2.2 — Leave Administration Module
**Cornerstone:** Not available (this is a Cornerstone gap — ShumelaHire advantage)
**Priority:** P0

#### Description

Full leave management system supporting South African leave types as defined by the Basic Conditions of Employment Act (BCEA), with configurable policies for municipal-specific rules, automated approval workflows, and integration with payroll (Sage) for leave encashment and balance sync.

#### Features

**F-4.2.1 Leave Types & Policies**
- Pre-configured SA leave types: Annual (15+ days), Sick (30 days/3yr cycle), Family Responsibility (3 days), Maternity (4 months), Parental (10 days), Adoption (10 weeks), Commissioning Parental (10 weeks)
- Custom leave types: Study, Religious, Special (municipal-specific)
- Per-type configuration: accrual rate, max balance, carry-forward rules, encashment rules, minimum service requirement, pro-rata calculation, documentation required
- Policy assignment by employment type, grade, department, or individual

**F-4.2.2 Leave Requests & Approvals**
- Online leave request with date range picker, leave type, reason, supporting documents
- Real-time balance display before submission
- Conflict detection: warn if overlapping with team members or blackout periods
- Multi-level approval workflow (configurable per leave type): direct manager → HR → department head
- Auto-approve rules for certain leave types below threshold
- Delegation: employees can set an acting person during leave
- Cancel/modify request (with re-approval if already approved)

**F-4.2.3 Leave Balances & Accruals**
- Real-time balance calculation per leave type
- Accrual engine: daily/monthly/annual accrual based on policy
- Pro-rata calculation for mid-year hires
- Carry-forward processing at cycle end (configurable max)
- Balance adjustment by HR with audit trail and reason
- Sick leave 3-year cycle tracking (BCEA requirement)
- Negative balance prevention with override option (HR only)

**F-4.2.4 Leave Encashment**
- Encashment of excess annual leave days
- Configurable rules: minimum remaining balance after encashment, max encashable days
- Approval workflow for encashment requests
- Integration with payroll (Sage) for encashment payment processing
- Tax implications flagged

**F-4.2.5 Leave Calendar & Team View**
- Team leave calendar: see who is on leave, who has pending requests
- Department-level leave planner
- Public holiday calendar (South African public holidays, configurable per region)
- Blackout period configuration (e.g., year-end, audit periods)

**F-4.2.6 Leave Reports & Analytics**
- Leave utilization by department, type, period
- Absenteeism trends and patterns
- Sick leave frequency analysis (flag excessive short sick leave)
- Leave liability report (financial impact of accrued balances)
- Bradford Factor calculation for absenteeism scoring

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `LeaveType` | id, name, code (unique), description, defaultDaysPerYear, accrualMethod (ANNUAL, MONTHLY, DAILY, NONE), maxBalance, carryForwardMax, carryForwardExpiry, requiresDocument, requiresDocumentAfterDays, encashmentAllowed, encashmentMaxDays, encashmentMinRemaining, minimumServiceMonths, isPaid, isActive, tenantId | OneToMany: leavePolicies |
| `LeavePolicy` | id, leaveTypeId, name, daysPerYear, accrualMethod, maxBalance, carryForwardMax, applicableTo (ALL, DEPARTMENT, GRADE, EMPLOYMENT_TYPE, INDIVIDUAL), applicableValue, effectiveFrom, effectiveTo, isActive, tenantId | ManyToOne: leaveType |
| `LeaveBalance` | id, employeeId, leaveTypeId, currentBalance, accruedThisCycle, usedThisCycle, carriedForward, cycleStartDate, cycleEndDate, lastAccrualDate, tenantId, updatedAt | ManyToOne: employee, leaveType |
| `LeaveRequest` | id, employeeId, leaveTypeId, startDate, endDate, totalDays, halfDay (NONE, FIRST_HALF, SECOND_HALF), reason, status (PENDING, APPROVED, REJECTED, CANCELLED, RECALLED), supportingDocumentUrl, delegateEmployeeId, requestedAt, approvedAt, approvedBy, rejectedAt, rejectedBy, rejectionReason, cancelledAt, cancellationReason, tenantId | ManyToOne: employee, leaveType |
| `LeaveApproval` | id, leaveRequestId, approverId, approvalLevel, status (PENDING, APPROVED, REJECTED), comments, actionedAt, tenantId | ManyToOne: leaveRequest |
| `LeaveEncashment` | id, employeeId, leaveTypeId, daysEncashed, dailyRate, totalAmount, status (PENDING, APPROVED, PROCESSED, REJECTED), approvedBy, approvedAt, processedAt, payrollReference, tenantId | ManyToOne: employee, leaveType |
| `LeaveAdjustment` | id, employeeId, leaveTypeId, adjustmentDays, reason, adjustedBy, tenantId, createdAt | ManyToOne: employee, leaveType |
| `PublicHoliday` | id, name, date, isRecurring, country, region, isActive, tenantId | — |
| `LeaveBlackoutPeriod` | id, name, startDate, endDate, applicableDepartments (JSON), reason, createdBy, tenantId | — |

#### API Endpoints

```
# Leave Types & Policies
GET    /api/leave/types                            — List leave types
POST   /api/leave/types                            — Create leave type (admin)
PUT    /api/leave/types/{id}                       — Update leave type
GET    /api/leave/policies                         — List policies
POST   /api/leave/policies                         — Create policy
PUT    /api/leave/policies/{id}                    — Update policy

# Leave Requests
POST   /api/leave/requests                         — Submit leave request
GET    /api/leave/requests                         — List requests (with filters)
GET    /api/leave/requests/{id}                    — Get request detail
PUT    /api/leave/requests/{id}                    — Modify request
POST   /api/leave/requests/{id}/cancel             — Cancel request
POST   /api/leave/requests/{id}/approve            — Approve
POST   /api/leave/requests/{id}/reject             — Reject with reason
GET    /api/leave/requests/pending-approval        — Requests awaiting my approval
GET    /api/leave/requests/team/{managerId}        — Team requests

# Balances
GET    /api/leave/balances/{employeeId}            — All balances for employee
GET    /api/leave/balances/{employeeId}/{typeId}   — Specific balance
POST   /api/leave/balances/adjust                  — Manual adjustment (HR)
POST   /api/leave/balances/accrue                  — Trigger accrual run (scheduled)
POST   /api/leave/balances/carry-forward           — Year-end carry-forward

# Encashment
POST   /api/leave/encashment                       — Request encashment
GET    /api/leave/encashment/{employeeId}          — Encashment history
POST   /api/leave/encashment/{id}/approve          — Approve encashment
POST   /api/leave/encashment/{id}/process          — Mark as processed in payroll

# Calendar & Holidays
GET    /api/leave/calendar                         — Team/department calendar
GET    /api/leave/holidays                         — Public holidays
POST   /api/leave/holidays                         — Add holiday
GET    /api/leave/blackout-periods                 — Blackout periods
POST   /api/leave/blackout-periods                 — Create blackout period

# Reports
GET    /api/leave/reports/utilization              — Utilization report
GET    /api/leave/reports/absenteeism              — Absenteeism trends
GET    /api/leave/reports/liability                — Leave liability (financial)
GET    /api/leave/reports/bradford-factor           — Bradford Factor scores
```

#### Frontend Pages

```
/leave                      — Leave dashboard (my balances, pending requests, team calendar)
/leave/request              — Submit new leave request
/leave/requests             — My leave history
/leave/approvals            — Pending approvals (manager view)
/leave/calendar             — Team/department leave calendar
/leave/reports              — Leave analytics & reports
/leave/encashment           — Encashment requests
/admin/leave-types          — Leave type & policy configuration
/admin/leave-holidays       — Public holiday management
```

#### Frontend Components

```
LeaveBalanceCard.tsx         — Display balance per leave type (coloured indicators)
LeaveRequestForm.tsx         — Request form with calendar picker, balance preview, conflict warning
LeaveApprovalQueue.tsx       — Manager approval queue with bulk actions
LeaveCalendar.tsx            — Full calendar view (team/department) with colour-coded leave types
LeaveBalanceChart.tsx        — Visual balance chart (accrued vs used vs remaining)
LeaveHistoryTable.tsx        — Sortable/filterable leave history
LeaveEncashmentForm.tsx      — Encashment request with calculation preview
LeavePolicyBuilder.tsx       — Admin policy configuration interface
AbsenteeismDashboard.tsx     — Absenteeism trends, Bradford Factor, alerts
```

#### Integrations

- **Sage 300 People** — Sync leave balances and leave taken records bi-directionally
- **Sage Evolution ERP** — Leave encashment payment processing
- **Notification Service** — Leave request/approval/rejection notifications (email + in-app + push)
- **Calendar** — Outlook calendar integration for leave blocks

#### AI Capabilities

- **AI Absenteeism Detection** — Flag unusual leave patterns (e.g., consistent Monday/Friday sick leave)
- **AI Leave Forecasting** — Predict team capacity based on historical leave patterns

---

### 4.3 Time & Attendance

**Category:** New Module
**RFP:** Section 2.5 — Time & Attendance Module
**Cornerstone:** Not available (Cornerstone gap — ShumelaHire advantage)
**Priority:** P0

#### Description

GPS-enabled time tracking system for distributed workforce across multiple sites. Supports clock-in/clock-out via mobile and web, geofenced attendance zones, shift scheduling, overtime management, and payroll integration.

#### Features

**F-4.3.1 Clock-In/Clock-Out**
- Web-based clock-in/out from desktop
- Mobile app clock-in/out with GPS capture
- QR code check-in at site kiosks (optional)
- Automatic clock-out after configurable hours (safety net)
- Break tracking (lunch, tea) — configurable per shift

**F-4.3.2 Geofencing & GPS Tracking**
- Define geofence zones per work site (polygon or radius-based)
- GPS coordinates captured at clock-in and clock-out
- Block clock-in if outside geofence (configurable: block or flag)
- Location history for field workers (periodic GPS pings — consent-based, POPIA compliant)
- Site boundary alerts for supervisors

**F-4.3.3 Shift Management**
- Define shift patterns: day, night, rotational, split shifts
- Shift templates assignable to departments, teams, or individuals
- Shift swap requests between employees (with manager approval)
- Shift calendar with drag-and-drop scheduling
- Auto-assign shifts based on rotation rules

**F-4.3.4 Overtime Management**
- Automatic overtime detection based on shift hours and clock data
- SA overtime rules: max 10 hours/week, 1.5x rate (weekday), 2x rate (Sunday/public holiday)
- Pre-approved vs post-approved overtime workflows
- Overtime budget tracking per department
- Manager alerts when overtime thresholds approach

**F-4.3.5 Attendance Dashboard**
- Real-time attendance status per site/department (who's clocked in, who's absent)
- Daily attendance summary: present, absent, late, half-day, on leave
- Late arrival tracking with configurable grace period
- Early departure tracking
- Attendance percentage per employee, team, department

**F-4.3.6 Attendance Reports**
- Monthly attendance register (printable)
- Overtime report per employee/department
- Punctuality report
- Site-level attendance summary
- Payroll-ready time data export

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `AttendanceRecord` | id, employeeId, date, clockInTime, clockOutTime, clockInLatitude, clockInLongitude, clockOutLatitude, clockOutLongitude, clockInMethod (WEB, MOBILE, QR, MANUAL), clockOutMethod, clockInGeofenceId, clockOutGeofenceId, status (PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE, PUBLIC_HOLIDAY), totalHoursWorked, regularHours, overtimeHours, breakMinutes, lateMinutes, earlyDepartureMinutes, isManualEntry, manualEntryReason, approvedBy, tenantId, createdAt, updatedAt | ManyToOne: employee, shift |
| `Geofence` | id, name, siteId, siteName, type (RADIUS, POLYGON), centreLatitude, centreLongitude, radiusMetres, polygonCoordinates (JSON), isActive, tenantId | OneToMany: attendanceRecords |
| `Shift` | id, name, code, startTime, endTime, breakStartTime, breakEndTime, breakDurationMinutes, graceMinutesLate, graceMinutesEarly, isNightShift, isActive, tenantId | — |
| `ShiftSchedule` | id, employeeId, shiftId, date, isOverride, overrideReason, createdBy, tenantId | ManyToOne: employee, shift |
| `ShiftPattern` | id, name, description, rotationDays, shifts (JSON — array of shift IDs per day), isActive, tenantId | — |
| `OvertimeRecord` | id, attendanceRecordId, employeeId, date, overtimeHours, overtimeType (WEEKDAY, WEEKEND, PUBLIC_HOLIDAY, NIGHT), rateMultiplier, status (PENDING, APPROVED, REJECTED), preApproved, approvedBy, approvedAt, rejectionReason, tenantId | ManyToOne: employee, attendanceRecord |
| `ShiftSwapRequest` | id, requestorEmployeeId, targetEmployeeId, requestorShiftScheduleId, targetShiftScheduleId, reason, status (PENDING, APPROVED, REJECTED, CANCELLED), approvedBy, tenantId, createdAt | ManyToOne: employees |

#### API Endpoints

```
# Clock-In/Out
POST   /api/attendance/clock-in                    — Clock in (captures GPS)
POST   /api/attendance/clock-out                   — Clock out (captures GPS)
GET    /api/attendance/status/{employeeId}          — Current clock status

# Records
GET    /api/attendance/records                     — List with filters (date range, employee, department)
GET    /api/attendance/records/{employeeId}         — Employee attendance history
POST   /api/attendance/records/manual              — Manual entry (admin/manager)
PUT    /api/attendance/records/{id}                — Update record (admin)
GET    /api/attendance/daily-summary               — Today's attendance by site/department

# Geofences
GET    /api/attendance/geofences                   — List geofences
POST   /api/attendance/geofences                   — Create geofence
PUT    /api/attendance/geofences/{id}              — Update geofence
DELETE /api/attendance/geofences/{id}              — Deactivate geofence
POST   /api/attendance/geofences/validate          — Check if GPS coords are within a geofence

# Shifts
GET    /api/attendance/shifts                      — List shift definitions
POST   /api/attendance/shifts                      — Create shift
PUT    /api/attendance/shifts/{id}                 — Update shift
GET    /api/attendance/shift-schedules             — Get schedules (filters: employee, department, date range)
POST   /api/attendance/shift-schedules             — Assign shift schedule
POST   /api/attendance/shift-schedules/bulk        — Bulk assign shifts
POST   /api/attendance/shift-swaps                 — Request shift swap
POST   /api/attendance/shift-swaps/{id}/approve    — Approve swap

# Overtime
GET    /api/attendance/overtime                    — List overtime records
POST   /api/attendance/overtime/{id}/approve       — Approve overtime
POST   /api/attendance/overtime/{id}/reject        — Reject overtime
GET    /api/attendance/overtime/budget/{department} — Overtime budget status

# Reports
GET    /api/attendance/reports/register            — Monthly attendance register
GET    /api/attendance/reports/overtime             — Overtime summary
GET    /api/attendance/reports/punctuality          — Punctuality report
GET    /api/attendance/reports/site-summary         — Per-site attendance
GET    /api/attendance/reports/payroll-export       — Payroll-ready time data
```

#### Frontend Pages

```
/attendance                  — Attendance dashboard (real-time status, clock-in/out)
/attendance/my-time          — My attendance history & clock status
/attendance/records          — Attendance records management (admin/manager)
/attendance/shifts           — Shift calendar & scheduling
/attendance/overtime         — Overtime management & approvals
/attendance/reports          — Attendance reports
/admin/attendance/geofences  — Geofence zone management (map-based)
/admin/attendance/shifts     — Shift definition & pattern configuration
```

#### Frontend Components

```
ClockInOutWidget.tsx          — Clock-in/out button with GPS status indicator
AttendanceDashboard.tsx       — Real-time attendance overview (by site/department)
GeofenceMap.tsx               — Interactive map for defining/viewing geofence zones (Leaflet/Mapbox)
ShiftCalendar.tsx             — Drag-and-drop shift scheduling calendar
ShiftSwapModal.tsx            — Shift swap request form
OvertimeApprovalQueue.tsx     — Manager overtime approval interface
AttendanceRegister.tsx        — Monthly register table (printable)
GPSTracker.tsx                — Mobile GPS capture component (for React Native)
SiteAttendanceCard.tsx        — Per-site attendance summary card
PunctualityChart.tsx          — Punctuality trend visualization
```

#### Integrations

- **Sage 300 People / Sage Evolution** — Export time data for payroll calculation
- **Geolocation API** — Browser/device GPS for clock-in coordinates
- **Maps Service** — Geofence visualization (Leaflet with OpenStreetMap or Mapbox)
- **Notification Service** — Late arrival alerts, overtime threshold warnings

---

### 4.4 Performance Management (Enhancement)

**Category:** Enhancement of existing module
**RFP:** Section 2.3 — Performance Management Module
**Cornerstone:** Performance Management (strong match)
**Priority:** P0

#### Description

Expand existing basic performance reviews into a comprehensive performance management system with structured review cycles, KRA alignment, PIPs, competency mapping, real-time dashboards, and direct linking to goals and skills frameworks.

#### Features (New — additions to existing)

**F-4.4.1 Performance Review Cycles**
- Configurable review cycles: annual, bi-annual, quarterly, probation
- Cycle management: create, open, close, extend
- Auto-assign reviews to employees based on department, grade, hire date
- Review templates with configurable sections and weighting
- Partial completion with auto-save

**F-4.4.2 KRA/KPI Alignment**
- Link individual performance to Key Result Areas (KRAs) and KPIs
- Cascade organisational objectives → departmental KRAs → individual KPIs
- Weighted scoring per KRA (must total 100%)
- Evidence/artifact attachment per KPI (documents, links, metrics)
- Mid-cycle KRA adjustments with approval

**F-4.4.3 Self-Assessment & Manager Review**
- Employee self-assessment against each KRA/KPI with rating and comments
- Manager assessment against same criteria (sees self-assessment after completing own)
- Calibration view for HR: side-by-side comparison of self vs manager ratings
- Moderation workflow: HR can flag outlier ratings for calibration discussion

**F-4.4.4 Performance Improvement Plans (PIPs)**
- Create PIP for underperforming employees
- PIP template: objectives, milestones, timeline (30/60/90 days), support provided, consequences
- Regular check-in tracking against PIP milestones
- PIP outcome: successfully completed, extended, terminated
- Integration with disciplinary process (feeds into compliance module)
- Audit trail for legal defensibility (LRA compliance)

**F-4.4.5 Competency Mapping**
- Link competencies from Skills Framework (Module 4.7) to job roles
- Assess employees against required competencies during reviews
- Gap visualization: required level vs assessed level per competency
- Generate development recommendations from competency gaps
- Feed gaps into Training module (4.8) for learning recommendations

**F-4.4.6 Performance Dashboards**
- Employee view: my ratings, goals progress, development plan
- Manager view: team performance heatmap, review completion status, PIP tracking
- HR view: organisation-wide distribution curve, department comparisons, calibration status
- Executive view: high-performer identification, retention risk, talent 9-box preview

#### Data Entities (New)

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `PerformanceReviewCycle` | id, name, type (ANNUAL, BIANNUAL, QUARTERLY, PROBATION), startDate, endDate, status (DRAFT, OPEN, IN_REVIEW, CALIBRATION, CLOSED), selfAssessmentDeadline, managerReviewDeadline, calibrationDeadline, tenantId | OneToMany: reviews |
| `PerformanceReview` | id, cycleId, employeeId, reviewerId, templateId, status (NOT_STARTED, SELF_ASSESSMENT, MANAGER_REVIEW, CALIBRATION, COMPLETED, ACKNOWLEDGED), overallRating, overallComments, employeeAcknowledgedAt, tenantId, createdAt, completedAt | ManyToOne: cycle, employee, reviewer |
| `PerformanceReviewTemplate` | id, name, sections (JSON — array of {name, weight, criteria[]}), ratingScale (3, 4, 5), ratingLabels (JSON), isActive, tenantId | OneToMany: reviews |
| `PerformanceKRA` | id, reviewId, kraName, kraDescription, weight, selfRating, selfComment, selfEvidence, managerRating, managerComment, finalRating, tenantId | ManyToOne: review |
| `PerformanceImprovementPlan` | id, employeeId, managerId, hrContactId, reason, startDate, endDate, status (ACTIVE, EXTENDED, COMPLETED_SUCCESS, COMPLETED_FAIL, TERMINATED), supportProvided, consequences, outcome, outcomeDate, outcomeNotes, tenantId, createdAt, updatedAt | ManyToOne: employee |
| `PIPMilestone` | id, pipId, description, targetDate, status (PENDING, MET, NOT_MET, PARTIALLY_MET), evidence, reviewedBy, reviewedAt, notes, tenantId | ManyToOne: pip |

#### API Endpoints (New additions)

```
# Review Cycles
POST   /api/performance/cycles                     — Create review cycle
GET    /api/performance/cycles                     — List cycles
PUT    /api/performance/cycles/{id}                — Update cycle
POST   /api/performance/cycles/{id}/open           — Open for submissions
POST   /api/performance/cycles/{id}/close          — Close cycle

# Reviews
GET    /api/performance/reviews                    — List reviews (filters: cycle, department, status)
GET    /api/performance/reviews/{id}               — Get review detail
POST   /api/performance/reviews/{id}/self-assess   — Submit self-assessment
POST   /api/performance/reviews/{id}/manager-review — Submit manager review
POST   /api/performance/reviews/{id}/calibrate     — HR calibration adjustment
POST   /api/performance/reviews/{id}/acknowledge   — Employee acknowledges final rating

# KRAs
GET    /api/performance/reviews/{id}/kras          — List KRAs for review
POST   /api/performance/reviews/{id}/kras          — Add KRA
PUT    /api/performance/kras/{kraId}               — Update KRA rating/comment

# PIPs
POST   /api/performance/pips                       — Create PIP
GET    /api/performance/pips                       — List PIPs
GET    /api/performance/pips/{id}                  — Get PIP detail
PUT    /api/performance/pips/{id}                  — Update PIP
POST   /api/performance/pips/{id}/milestones       — Add milestone
PUT    /api/performance/pips/milestones/{id}       — Update milestone status
POST   /api/performance/pips/{id}/close            — Close PIP with outcome

# Dashboards
GET    /api/performance/dashboard/employee/{id}    — Employee performance summary
GET    /api/performance/dashboard/team/{managerId}  — Team heatmap
GET    /api/performance/dashboard/organisation      — Org-wide distribution
```

#### Frontend Pages (New additions)

```
/performance/cycles          — Review cycle management
/performance/reviews         — My reviews / team reviews
/performance/reviews/[id]    — Review detail (self-assess / manager review)
/performance/calibration     — HR calibration view
/performance/pips            — PIP management
/performance/pips/[id]       — PIP detail with milestone tracking
```

---

### 4.5 Goal Management

**Category:** New Module
**RFP:** Section 2.3 — "Goal-setting aligned with KRAs, skillsets, and organizational objectives"
**Cornerstone:** Goals module
**Priority:** P1

#### Description

OKR (Objectives & Key Results) and goal management system that cascades from organizational strategy down to individual objectives, links directly to performance reviews, and provides real-time progress tracking.

#### Features

**F-4.5.1 Goal Hierarchy**
- Organisation-level objectives (set by executive)
- Department-level goals aligned to org objectives
- Team-level goals aligned to department goals
- Individual goals aligned to team/department goals
- Visual cascade tree: see how individual goals roll up to strategy

**F-4.5.2 Goal CRUD**
- Create goals with: title, description, owner, type (OKR, KPI, PROJECT, DEVELOPMENT), category, alignment (parent goal), due date, weight
- Key Results per Objective: measurable, with target value, current value, unit
- Goal status: NOT_STARTED, ON_TRACK, AT_RISK, BEHIND, COMPLETED, CANCELLED
- Tags and categories for filtering

**F-4.5.3 Progress Tracking**
- Manual progress updates with comments
- Auto-progress from key result updates (percentage of KRs completed)
- Check-in reminders (weekly/bi-weekly/monthly)
- Progress history timeline
- Manager review and approval of goal completion

**F-4.5.4 Goal-Performance Link**
- Link individual goals to performance review KRAs
- Auto-populate review with goal achievement data
- Weight goals in performance scoring

**F-4.5.5 Goal Analytics**
- Goal completion rate by department, team, individual
- Alignment coverage: % of org goals with cascaded departmental goals
- Overdue goal alerts
- Quarterly goal review summaries

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Goal` | id, title, description, ownerId, ownerType (ORGANISATION, DEPARTMENT, TEAM, INDIVIDUAL), type (OKR, KPI, PROJECT, DEVELOPMENT), category, parentGoalId (self-ref), status, weight, startDate, dueDate, completedDate, progress (0-100), visibility (PUBLIC, DEPARTMENT, PRIVATE), tenantId, createdAt, updatedAt | ManyToOne: parentGoal, OneToMany: keyResults, goalUpdates, childGoals |
| `KeyResult` | id, goalId, title, targetValue, currentValue, unit, startValue, status, dueDate, tenantId | ManyToOne: goal |
| `GoalUpdate` | id, goalId, authorId, progressBefore, progressAfter, comment, tenantId, createdAt | ManyToOne: goal |

#### API Endpoints

```
POST   /api/goals                               — Create goal
GET    /api/goals                               — List goals (filters: owner, type, status, parent)
GET    /api/goals/{id}                          — Get goal detail
PUT    /api/goals/{id}                          — Update goal
DELETE /api/goals/{id}                          — Delete goal
POST   /api/goals/{id}/key-results              — Add key result
PUT    /api/goals/key-results/{krId}            — Update key result progress
POST   /api/goals/{id}/check-in                 — Post progress update
GET    /api/goals/cascade/{goalId}              — Get goal cascade tree
GET    /api/goals/analytics                     — Goal analytics
GET    /api/goals/employee/{employeeId}          — Goals for employee
```

#### Frontend Pages

```
/goals                       — My goals dashboard
/goals/[id]                  — Goal detail with key results and updates
/goals/cascade               — Visual goal cascade/alignment tree
/goals/team                  — Team goals overview (manager view)
/goals/organisation          — Org-wide goal dashboard (executive view)
```

#### Frontend Components

```
GoalCard.tsx                  — Goal summary card with progress bar
GoalCascadeTree.tsx           — Visual hierarchy tree of aligned goals
KeyResultTracker.tsx          — Key result progress input and visualization
GoalCheckInForm.tsx           — Check-in comment and progress update
GoalAlignmentPicker.tsx       — Select parent goal for alignment
GoalAnalyticsChart.tsx        — Completion rates, trends
```

---

### 4.6 360 Feedback

**Category:** New Module
**RFP:** Section 2.3 — "360-degree feedback from peers, subordinates, and managers"
**Cornerstone:** 360 Feedback module
**Priority:** P1

#### Description

Multi-rater feedback system enabling comprehensive evaluation from managers, peers, direct reports, and self. Can run standalone or integrated within performance review cycles.

#### Features

**F-4.6.1 Feedback Campaigns**
- Create 360 campaign: name, target employees, feedback providers, questionnaire, anonymity settings, timeline
- Campaign types: Full 360 (all directions), 180 (manager + self), Peer Only, Upward Only
- Schedule campaigns: one-time or recurring
- Bulk invite via CSV or department selection

**F-4.6.2 Questionnaire Builder**
- Pre-built question banks by competency area (leadership, communication, teamwork, etc.)
- Custom question creation: Likert scale, open-ended, ranking
- Configurable sections per rater type (different questions for peers vs direct reports)
- Anonymous vs named feedback (configurable)

**F-4.6.3 Feedback Collection**
- Raters receive email/in-app notification with link
- Mobile-friendly feedback form
- Save partial responses
- Reminders for incomplete feedback (configurable cadence)
- Minimum response threshold before results are visible (to protect anonymity)

**F-4.6.4 Results & Reports**
- Aggregated scores by category and rater type (spider/radar chart)
- Gap analysis: self-rating vs others' ratings
- Comparison to previous 360 cycles (trend)
- Strengths and development areas summary
- Export individual 360 report as PDF

**F-4.6.5 Action Planning**
- Generate development actions from 360 results
- Link actions to learning recommendations (LMS module)
- Track action completion over time

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `FeedbackCampaign` | id, name, type (FULL_360, 180, PEER, UPWARD), status (DRAFT, ACTIVE, CLOSED, ANALYSED), startDate, endDate, isAnonymous, minimumResponses, questionnaireId, createdBy, tenantId | OneToMany: feedbackRequests |
| `FeedbackQuestionnaire` | id, name, sections (JSON — [{name, questions: [{text, type, scale, required}]}]), isTemplate, tenantId | — |
| `FeedbackRequest` | id, campaignId, subjectEmployeeId, raterEmployeeId, raterType (SELF, MANAGER, PEER, DIRECT_REPORT, EXTERNAL), status (PENDING, IN_PROGRESS, COMPLETED, DECLINED), completedAt, tenantId | ManyToOne: campaign |
| `FeedbackResponse` | id, feedbackRequestId, responses (JSON — [{questionId, rating, comment}]), overallComment, submittedAt, tenantId | ManyToOne: feedbackRequest |

#### API Endpoints

```
POST   /api/feedback/campaigns                     — Create campaign
GET    /api/feedback/campaigns                     — List campaigns
GET    /api/feedback/campaigns/{id}                — Campaign detail & status
POST   /api/feedback/campaigns/{id}/launch         — Launch campaign (send invites)
POST   /api/feedback/campaigns/{id}/close          — Close campaign
POST   /api/feedback/campaigns/{id}/remind         — Send reminders
GET    /api/feedback/requests/my-pending           — Feedback I need to give
POST   /api/feedback/requests/{id}/respond         — Submit feedback
GET    /api/feedback/results/{employeeId}/{campaignId} — 360 results for employee
GET    /api/feedback/results/{employeeId}/trends   — Historical trends
GET    /api/feedback/results/{employeeId}/report   — Download PDF report
```

#### Frontend Pages

```
/feedback                    — My feedback dashboard (pending to give, my results)
/feedback/campaigns          — Campaign management (HR view)
/feedback/campaigns/[id]     — Campaign detail and progress
/feedback/give/[requestId]   — Feedback submission form
/feedback/results/[employeeId] — 360 results view with charts
```

#### Frontend Components

```
FeedbackCampaignBuilder.tsx   — Campaign creation wizard
FeedbackForm.tsx              — Rater feedback questionnaire
RadarChart360.tsx             — Spider/radar chart for 360 results
FeedbackGapAnalysis.tsx       — Self vs others comparison
FeedbackCompletionTracker.tsx — Campaign progress (% complete per rater type)
FeedbackReportExport.tsx      — PDF report generation
```

---

### 4.7 Competency & Skills Framework

**Category:** New Module
**RFP:** Section 2.3 (competency mapping) + Section 2.4 (skill gap analysis)
**Cornerstone:** Skills Edge / Competency module
**Priority:** P1

#### Description

Centralized skills taxonomy and competency framework that underpins performance reviews, hiring criteria, learning recommendations, succession planning, and career pathing. Provides gap analysis between required and assessed competency levels.

#### Features

**F-4.7.1 Skills Taxonomy**
- Hierarchical skills catalog: categories → skills → proficiency levels
- Pre-loaded industry skill libraries (IT, Engineering, Finance, Water Services, Municipal)
- Custom skill creation by HR
- Skill synonyms and aliases for search consistency
- Skill deprecation (archive, not delete) for historical integrity

**F-4.7.2 Competency Models**
- Define competency models per job role/grade/department
- Competencies with required proficiency level per role (e.g., "Project Management: Level 3 of 5")
- Behavioural indicators per level (what "Level 3" looks like)
- Core competencies (apply to all roles) vs role-specific competencies

**F-4.7.3 Employee Skills Profiles**
- Employees self-declare skills with proficiency level
- Manager validation/endorsement of declared skills
- Auto-populate from performance assessments and training completions
- Certification tracking linked to skills (with expiry)

**F-4.7.4 Gap Analysis**
- Compare employee skills profile vs role competency model
- Visual gap chart (required vs actual per competency)
- Department-level skill gap heatmap
- Priority scoring: gap severity × competency importance
- Generate personalized learning paths from gaps (link to LMS module 4.8)

**F-4.7.5 Skill-Based Search & Matching**
- Search employees by skill and proficiency
- Internal talent marketplace: match employees to projects/roles based on skills
- Succession readiness based on skill coverage (link to module 4.10)
- Hiring criteria: generate competency requirements for job postings from competency model

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `SkillCategory` | id, name, description, parentCategoryId (self-ref), isActive, tenantId | OneToMany: skills, childCategories |
| `Skill` | id, categoryId, name, description, aliases (JSON), isActive, tenantId | ManyToOne: category |
| `ProficiencyLevel` | id, skillId, level (1-5), name (Beginner to Expert), description, behaviouralIndicators (JSON), tenantId | ManyToOne: skill |
| `CompetencyModel` | id, name, description, applicableTo (JOB_ROLE, GRADE, DEPARTMENT), applicableValue, isActive, tenantId | OneToMany: competencies |
| `CompetencyRequirement` | id, competencyModelId, skillId, requiredLevel, importance (CRITICAL, HIGH, MEDIUM, LOW), tenantId | ManyToOne: competencyModel, skill |
| `EmployeeSkill` | id, employeeId, skillId, selfAssessedLevel, managerAssessedLevel, validatedLevel, validatedBy, validatedAt, source (SELF_DECLARED, ASSESSMENT, TRAINING, CERTIFICATION), certificationId, tenantId, createdAt, updatedAt | ManyToOne: employee, skill |

#### API Endpoints

```
# Taxonomy
GET    /api/skills/categories                    — List skill categories (tree)
POST   /api/skills/categories                    — Create category
GET    /api/skills                               — List/search skills
POST   /api/skills                               — Create skill
PUT    /api/skills/{id}                          — Update skill

# Competency Models
GET    /api/competency-models                    — List models
POST   /api/competency-models                    — Create model
GET    /api/competency-models/{id}               — Get model with requirements
PUT    /api/competency-models/{id}               — Update model
POST   /api/competency-models/{id}/requirements  — Add competency requirement

# Employee Skills
GET    /api/skills/employee/{employeeId}          — Employee skill profile
POST   /api/skills/employee/{employeeId}          — Self-declare skill
PUT    /api/skills/employee/{id}/validate         — Manager validates skill
GET    /api/skills/gap-analysis/{employeeId}      — Individual gap analysis
GET    /api/skills/gap-analysis/department/{dept}  — Department gap heatmap
GET    /api/skills/search                         — Find employees by skill/level
```

#### Frontend Pages

```
/skills                      — Skills dashboard (my skills, gaps, recommendations)
/skills/taxonomy             — Skills catalog browser
/skills/gap-analysis         — Personal gap analysis with learning recommendations
/admin/skills                — Skills taxonomy management
/admin/competency-models     — Competency model builder
/skills/search               — Skill-based employee search
```

---

### 4.8 Learning Management System (LMS)

**Category:** New Module
**RFP:** Section 2.4 — Training & Development (LMS integration)
**Cornerstone:** Learning Management System (this is Cornerstone's core product)
**Priority:** P1

#### Description

Full learning management system supporting course creation, SCORM/xAPI content, learning paths, certifications, compliance training, and skill-gap-driven recommendations. This is the module that most directly competes with Cornerstone's core offering.

#### Features

**F-4.8.1 Course Management**
- Create courses with: title, description, category, skill tags, duration, difficulty, thumbnail
- Course types: eLearning (SCORM/xAPI), Video, Document, Classroom (ILT), Virtual Classroom, Blended
- SCORM 1.2 and SCORM 2004 package upload and player
- xAPI (Tin Can) support with LRS (Learning Record Store)
- Video hosting (upload to S3 with streaming)
- Document-based courses (PDF, PPT with progress tracking)

**F-4.8.2 Learning Paths**
- Ordered sequence of courses forming a learning path
- Prerequisites: complete course A before course B
- Branching paths based on assessment scores
- Estimated total duration
- Certificate issued on path completion

**F-4.8.3 Certifications & Compliance Training**
- Define certifications with validity period (e.g., "Fire Safety — 12 months")
- Mandatory certification assignment by role/department
- Automated renewal reminders (30, 14, 7 days before expiry)
- Re-certification workflow: re-take course + assessment
- Compliance dashboard: who is certified, who is expired, who is due
- Compliance training deadlines with escalation (employee → manager → HR)

**F-4.8.4 Course Enrolment & Delivery**
- Self-enrolment from course catalog
- Manager-assigned enrolment
- Auto-enrolment rules (e.g., all new hires get "Onboarding Path", all Water Treatment staff get "Safety Certification")
- Course player: SCORM player, video player, document viewer
- Progress tracking: started, in-progress, completed, failed
- Bookmarks and resume-from-where-left-off

**F-4.8.5 Assessments & Quizzes**
- Quiz builder: multiple choice, true/false, matching, short answer, drag-and-drop
- Question banks with random selection
- Pass mark configuration per assessment
- Attempt limits
- Immediate feedback or delayed grading
- Assessment analytics: question difficulty, discrimination index

**F-4.8.6 Classroom/ILT Training**
- Schedule instructor-led training sessions
- Venue management (rooms, capacity)
- Waitlist management
- Attendance marking (manual or QR code)
- Trainer feedback collection
- Training cost tracking (venue, trainer, materials)

**F-4.8.7 Skill-Gap-Driven Recommendations**
- Integrate with Competency module (4.7): identify skill gaps
- AI-recommended courses to fill specific gaps
- "Suggested for you" based on role, competency gaps, career goals
- Manager can assign training to address performance review gaps

**F-4.8.8 Training Reports & Analytics**
- Course completion rates
- Training hours per employee/department
- Certification compliance rates
- Training effectiveness (pre/post assessment score improvement)
- Training cost analysis
- Individual training transcript (all completed training with dates and scores)

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Course` | id, title, description, category, type (ELEARNING, VIDEO, DOCUMENT, ILT, VIRTUAL, BLENDED), skillTags (JSON), durationMinutes, difficulty (BEGINNER, INTERMEDIATE, ADVANCED), thumbnailUrl, contentUrl, scormPackageUrl, xapiActivityId, isPublished, isMandatory, createdBy, tenantId, createdAt, updatedAt | OneToMany: enrolments, modules |
| `CourseModule` | id, courseId, title, type, contentUrl, order, durationMinutes, tenantId | ManyToOne: course |
| `LearningPath` | id, name, description, courses (JSON — ordered list with prerequisites), thumbnailUrl, estimatedDurationMinutes, certificateTemplateId, isPublished, tenantId | OneToMany: learningPathCourses |
| `LearningPathCourse` | id, learningPathId, courseId, order, isRequired, prerequisiteCourseIds (JSON), tenantId | ManyToOne: learningPath, course |
| `Enrolment` | id, employeeId, courseId, learningPathId (nullable), status (NOT_STARTED, IN_PROGRESS, COMPLETED, FAILED, EXPIRED), progress (0-100), enrolledBy (SELF, MANAGER, AUTO, ADMIN), enrolledAt, startedAt, completedAt, dueDate, score, attempts, lastAccessedAt, bookmarkData (JSON), tenantId | ManyToOne: employee, course |
| `Certification` | id, name, description, courseId (or learningPathId), validityMonths, isMandatory, applicableRoles (JSON), applicableDepartments (JSON), tenantId | ManyToOne: course |
| `EmployeeCertification` | id, employeeId, certificationId, issuedDate, expiryDate, status (ACTIVE, EXPIRED, REVOKED, PENDING_RENEWAL), certificateUrl, score, tenantId | ManyToOne: employee, certification |
| `Assessment` | id, courseId, title, questions (JSON), passMarkPercentage, maxAttempts, timeLimitMinutes, shuffleQuestions, showFeedback, tenantId | ManyToOne: course |
| `AssessmentAttempt` | id, assessmentId, employeeId, answers (JSON), score, passed, startedAt, completedAt, tenantId | ManyToOne: assessment, employee |
| `TrainingSession` | id, courseId, title, trainerId, venueId, startDateTime, endDateTime, maxParticipants, currentParticipants, waitlistCount, status (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED), cost, notes, tenantId | ManyToOne: course |
| `TrainingAttendance` | id, sessionId, employeeId, status (REGISTERED, ATTENDED, NO_SHOW, CANCELLED), registeredAt, attendedAt, feedback, rating, tenantId | ManyToOne: session, employee |

#### API Endpoints

```
# Courses
POST   /api/lms/courses                           — Create course
GET    /api/lms/courses                           — Catalog (search, filter by type/category/skill)
GET    /api/lms/courses/{id}                      — Course detail
PUT    /api/lms/courses/{id}                      — Update course
POST   /api/lms/courses/{id}/publish              — Publish course
POST   /api/lms/courses/{id}/scorm-upload         — Upload SCORM package
GET    /api/lms/courses/{id}/scorm-launch         — Get SCORM launch URL

# Learning Paths
POST   /api/lms/learning-paths                    — Create learning path
GET    /api/lms/learning-paths                    — List paths
GET    /api/lms/learning-paths/{id}               — Path detail with courses

# Enrolment
POST   /api/lms/enrolments                       — Enrol employee(s) in course/path
GET    /api/lms/enrolments/my                     — My enrolments
GET    /api/lms/enrolments/employee/{employeeId}  — Employee enrolments (transcript)
PUT    /api/lms/enrolments/{id}/progress          — Update progress/bookmark
POST   /api/lms/enrolments/{id}/complete          — Mark complete

# Certifications
POST   /api/lms/certifications                    — Define certification
GET    /api/lms/certifications                    — List certifications
GET    /api/lms/certifications/compliance          — Compliance dashboard data
GET    /api/lms/certifications/expiring            — Expiring certifications
GET    /api/lms/certifications/employee/{employeeId} — Employee certifications

# Assessments
POST   /api/lms/assessments                       — Create assessment
POST   /api/lms/assessments/{id}/attempt          — Start attempt
POST   /api/lms/assessments/{id}/submit           — Submit answers
GET    /api/lms/assessments/{id}/results           — Get results

# ILT Sessions
POST   /api/lms/sessions                          — Schedule session
GET    /api/lms/sessions                          — List sessions
POST   /api/lms/sessions/{id}/register            — Register for session
POST   /api/lms/sessions/{id}/attendance          — Mark attendance

# Reports
GET    /api/lms/reports/completion                — Completion rates
GET    /api/lms/reports/compliance                — Certification compliance
GET    /api/lms/reports/training-hours            — Hours per employee/dept
GET    /api/lms/reports/transcript/{employeeId}   — Individual transcript
GET    /api/lms/reports/effectiveness             — Pre/post assessment comparison

# Recommendations
GET    /api/lms/recommendations/{employeeId}      — AI-recommended courses
```

#### Frontend Pages

```
/learning                    — My learning dashboard (enrolments, recommendations, certifications)
/learning/catalog             — Course catalog with search & filters
/learning/course/[id]         — Course detail & player
/learning/paths               — Learning paths browser
/learning/paths/[id]          — Learning path detail with progress
/learning/certifications      — My certifications & expiry dates
/learning/transcript          — My training transcript
/learning/sessions            — Upcoming ILT sessions
/admin/learning/courses       — Course management (create, edit, publish)
/admin/learning/paths         — Learning path builder
/admin/learning/certifications — Certification management
/admin/learning/compliance    — Compliance dashboard
/admin/learning/assessments   — Assessment/quiz builder
```

#### Frontend Components

```
CourseCatalog.tsx             — Filterable course grid/list
CourseCard.tsx                — Course preview card with thumbnail, duration, difficulty
CoursePlayer.tsx              — SCORM/xAPI/video/document player (embedded)
ScormPlayer.tsx               — SCORM package runtime (iframe-based)
LearningPathProgress.tsx      — Visual path with completion indicators
CertificationTracker.tsx      — Certification status with expiry countdown
ComplianceDashboard.tsx       — Org-wide compliance heatmap
QuizBuilder.tsx               — Assessment question builder
QuizPlayer.tsx                — Quiz taking interface
TrainingCalendar.tsx          — ILT session calendar
TrainingTranscript.tsx        — Individual training history
LearningRecommendations.tsx   — AI-powered course suggestions
```

#### AI Capabilities

- **AI Course Recommendations** — Recommend courses based on skill gaps, role, career goals, and peer learning patterns
- **AI Content Summarization** — Summarize lengthy training documents
- **AI Quiz Generation** — Auto-generate quiz questions from course content

---

### 4.9 Content Authoring

**Category:** New Module
**RFP:** Section 2.4 — Training materials
**Cornerstone:** Content Studio / Content Authoring
**Priority:** P2

#### Description

Built-in course creation tools that allow subject matter experts to create training content without external authoring tools.

#### Features

**F-4.9.1 Slide-Based Course Builder**
- Drag-and-drop slide editor (text, images, video embeds, interactive hotspots)
- Pre-built templates: tutorial, process walkthrough, policy document, scenario-based
- Branching scenarios (if learner selects X, go to slide Y)
- Embedded quizzes within slides

**F-4.9.2 Video-Based Content**
- Upload video with chapter markers
- Screen recording integration (embed link to external recorder)
- Auto-generated captions (AI-powered transcription)
- Interactive video: overlay quiz questions at timestamps

**F-4.9.3 Microlearning**
- Short-form content cards (bite-sized lessons, 2-5 minutes)
- Flashcard-style learning
- Daily learning nudges (push notification with micro-lesson)

**F-4.9.4 Content Templates**
- Policy acknowledgment template
- Standard Operating Procedure (SOP) template
- Onboarding checklist template
- Safety training template
- Template marketplace (share across tenants)

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `ContentProject` | id, title, type (SLIDE_COURSE, VIDEO_COURSE, MICROLEARNING), authorId, status (DRAFT, REVIEW, PUBLISHED), slides (JSON), tenantId, createdAt, updatedAt | — |
| `ContentTemplate` | id, name, type, category, structure (JSON), thumbnailUrl, isPublic, tenantId | — |

#### API Endpoints

```
POST   /api/content/projects                      — Create content project
GET    /api/content/projects                      — List projects
GET    /api/content/projects/{id}                 — Get project
PUT    /api/content/projects/{id}                 — Save project (auto-save)
POST   /api/content/projects/{id}/publish         — Publish as course
GET    /api/content/templates                     — List templates
POST   /api/content/projects/{id}/export-scorm    — Export as SCORM package
```

#### Frontend Pages

```
/admin/content/projects       — Content project list
/admin/content/editor/[id]    — Drag-and-drop course editor
/admin/content/templates      — Template gallery
```

---

### 4.10 Succession Planning

**Category:** New Module
**RFP:** Not explicitly required (Cornerstone parity)
**Cornerstone:** Succession Planning module
**Priority:** P2

#### Description

Identify and develop future leaders through 9-box talent grids, readiness assessments, career pathing, and succession bench strength tracking for critical roles.

#### Features

**F-4.10.1 9-Box Talent Grid**
- Plot employees on Performance (x-axis) vs Potential (y-axis)
- Auto-populate performance from review scores
- Potential assessed by managers (with calibration)
- Interactive: click on box to see employees, drag between boxes
- Filter by department, level, tenure

**F-4.10.2 Critical Role Identification**
- Tag roles as critical (high impact if vacant)
- Risk assessment: current incumbent's flight risk, time-to-impact if vacant
- Required lead time to fill

**F-4.10.3 Succession Bench**
- For each critical role, identify succession candidates
- Readiness levels: Ready Now, Ready 1-2 Years, Ready 3+ Years, Development Needed
- Gap analysis per candidate (skills/experience gaps vs role requirements)
- Development actions per candidate to address gaps

**F-4.10.4 Career Pathing**
- Define career paths per role family (e.g., Junior Engineer → Engineer → Senior Engineer → Principal Engineer → Engineering Manager)
- Required competencies, experience, and certifications per step
- Employee career aspirations capture
- Suggested next roles based on current skills and aspirations

**F-4.10.5 Talent Reviews**
- Structured talent review meetings: agenda, nominees, discussion notes, actions
- Review outcomes feed into succession plans
- Annual talent review cycle management

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `SuccessionPlan` | id, criticalRoleTitle, criticalRoleDepartment, currentIncumbentId, riskLevel (LOW, MEDIUM, HIGH, CRITICAL), vacancyImpact, timeToFillMonths, status (ACTIVE, FILLED, ARCHIVED), tenantId, createdAt, updatedAt | ManyToOne: currentIncumbent, OneToMany: successors |
| `SuccessionCandidate` | id, successionPlanId, employeeId, readiness (READY_NOW, READY_1_2_YEARS, READY_3_PLUS, DEVELOPMENT_NEEDED), performanceRating, potentialRating, gapNotes, developmentActions (JSON), tenantId | ManyToOne: successionPlan, employee |
| `CareerPath` | id, roleFamilyName, steps (JSON — [{title, level, requiredCompetencies, requiredExperience, requiredCertifications}]), isActive, tenantId | — |
| `TalentReview` | id, name, reviewDate, facilitatorId, status (PLANNED, IN_PROGRESS, COMPLETED), notes, outcomes (JSON), tenantId | OneToMany: nominees |

#### API Endpoints

```
POST   /api/succession/plans                     — Create succession plan
GET    /api/succession/plans                     — List plans
GET    /api/succession/plans/{id}                — Plan detail with candidates
POST   /api/succession/plans/{id}/candidates     — Add successor candidate
PUT    /api/succession/candidates/{id}           — Update readiness/development
GET    /api/succession/nine-box                  — 9-box grid data
GET    /api/succession/career-paths              — List career paths
POST   /api/succession/career-paths              — Create career path
GET    /api/succession/talent-reviews             — List talent reviews
POST   /api/succession/talent-reviews             — Create talent review
GET    /api/succession/dashboard                  — Succession health metrics
```

#### Frontend Pages

```
/succession                  — Succession planning dashboard
/succession/nine-box         — Interactive 9-box grid
/succession/plans            — Critical roles and succession plans
/succession/plans/[id]       — Plan detail with candidate pipeline
/succession/career-paths     — Career path browser
/succession/talent-reviews   — Talent review management
```

#### Frontend Components

```
NineBoxGrid.tsx               — Interactive 9-box with drag-and-drop
SuccessionPlanCard.tsx        — Critical role with successor pipeline
ReadinessIndicator.tsx        — Visual readiness level indicator
CareerPathDiagram.tsx         — Visual career progression path
TalentReviewBoard.tsx         — Talent review discussion board
BenchStrengthChart.tsx        — Succession bench depth visualization
```

---

### 4.11 Employee Engagement

**Category:** New Module
**RFP:** Section 2.7 — Employee Engagement Module
**Cornerstone:** Limited (Cornerstone weakness — ShumelaHire advantage)
**Priority:** P1

#### Description

Employee engagement platform with pulse surveys, recognition programs, wellness tracking, and social collaboration. Goes beyond Cornerstone's limited engagement capabilities.

#### Features

**F-4.11.1 Pulse Surveys**
- Quick surveys (5-10 questions) deployed weekly/bi-weekly/monthly
- Pre-built templates: engagement, satisfaction, wellbeing, manager effectiveness
- Custom survey builder with question types: Likert, NPS, open-ended, multiple choice
- Anonymous by default (configurable)
- eNPS (Employee Net Promoter Score) tracking over time
- Department-level results with benchmarking
- Action planning from survey insights

**F-4.11.2 Recognition & Rewards**
- Peer-to-peer recognition: kudos, shout-outs tied to company values
- Manager recognition with points/badges
- Recognition wall: public feed of recognitions
- Monthly/quarterly awards: automated nomination and voting
- Points system with redeemable rewards (configurable catalog)
- Recognition analytics: who gives/receives most, trending values

**F-4.11.3 Employee Wellness**
- Wellness check-ins (voluntary mood tracking)
- Wellness program enrollment (fitness challenges, mental health resources)
- Health tracking integration (steps, activity — opt-in)
- EAP (Employee Assistance Program) resource directory
- Wellness dashboard: organization mood trends, program participation

**F-4.11.4 Social Collaboration**
- Team channels for announcements and discussions
- Company-wide news feed
- Event management: company events, team activities
- Employee directory with interest groups
- Idea submission box (innovation suggestions)

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Survey` | id, title, type (PULSE, ENGAGEMENT, SATISFACTION, CUSTOM), questions (JSON), isAnonymous, frequency (ONE_TIME, WEEKLY, BIWEEKLY, MONTHLY), status (DRAFT, ACTIVE, CLOSED), startDate, endDate, targetAudience (JSON — departments, roles), createdBy, tenantId | OneToMany: surveyResponses |
| `SurveyResponse` | id, surveyId, respondentId (nullable if anonymous), answers (JSON), submittedAt, tenantId | ManyToOne: survey |
| `Recognition` | id, giverId, receiverId, type (KUDOS, SHOUT_OUT, AWARD, BADGE), message, companyValue (JSON — linked value), points, isPublic, tenantId, createdAt | ManyToOne: giver (employee), receiver (employee) |
| `WellnessCheckIn` | id, employeeId, mood (1-5), energyLevel (1-5), stressLevel (1-5), notes, checkedInAt, tenantId | ManyToOne: employee |
| `WellnessProgram` | id, name, description, type (FITNESS, MENTAL_HEALTH, NUTRITION, GENERAL), startDate, endDate, maxParticipants, isActive, tenantId | OneToMany: enrolments |
| `EngagementPost` | id, authorId, type (ANNOUNCEMENT, DISCUSSION, EVENT, IDEA), title, content, channel, isPinned, reactions (JSON), tenantId, createdAt | ManyToOne: author (employee) |

#### API Endpoints

```
# Surveys
POST   /api/engagement/surveys                    — Create survey
GET    /api/engagement/surveys                    — List surveys
GET    /api/engagement/surveys/{id}               — Survey detail
POST   /api/engagement/surveys/{id}/launch        — Launch survey
POST   /api/engagement/surveys/{id}/respond       — Submit response
GET    /api/engagement/surveys/{id}/results       — Aggregated results
GET    /api/engagement/enps                       — eNPS score and trend

# Recognition
POST   /api/engagement/recognitions               — Give recognition
GET    /api/engagement/recognitions/wall           — Recognition feed
GET    /api/engagement/recognitions/leaderboard    — Top recognized employees
GET    /api/engagement/recognitions/my             — My received recognitions

# Wellness
POST   /api/engagement/wellness/check-in          — Submit wellness check-in
GET    /api/engagement/wellness/trends/{employeeId} — Personal wellness trends
GET    /api/engagement/wellness/dashboard          — Org wellness metrics
GET    /api/engagement/wellness/programs           — Available programs
POST   /api/engagement/wellness/programs/{id}/enrol — Enrol in program

# Social
POST   /api/engagement/posts                      — Create post
GET    /api/engagement/posts                      — Feed (filtered by channel)
POST   /api/engagement/posts/{id}/react           — React to post
```

#### Frontend Pages

```
/engagement                  — Engagement hub (surveys, recognition wall, wellness)
/engagement/surveys          — Available/past surveys
/engagement/surveys/[id]     — Take survey or view results
/engagement/recognition      — Recognition wall & give recognition
/engagement/wellness         — Wellness dashboard & check-in
/engagement/social           — Social feed & discussions
/admin/engagement/surveys    — Survey builder & management
/admin/engagement/programs   — Wellness program management
```

---

### 4.12 Compensation Management

**Category:** New Module
**RFP:** Section 2.10 (partial — salary processing, leave encashment)
**Cornerstone:** Compensation module
**Priority:** P2

#### Description

Compensation planning and total rewards management beyond basic salary. Includes pay structures, compensation reviews, budget allocation, and total reward statements.

#### Features

**F-4.12.1 Pay Structures**
- Define pay grades with salary ranges (min, midpoint, max)
- Pay bands per grade/level
- Annual pay scale adjustments
- Geographic differentials (cost-of-living adjustments per location)
- Compa-ratio tracking (employee salary vs midpoint)

**F-4.12.2 Compensation Reviews**
- Annual compensation review cycles
- Merit increase pool allocation by department
- Manager recommendation with guidelines (e.g., high performer: 8-12%, meets expectations: 4-6%)
- Budget tracking: allocated vs spent per department
- Multi-level approval workflow
- What-if modelling: simulate different increase scenarios

**F-4.12.3 Total Rewards Statement**
- Employee-facing total rewards view: base salary + benefits + leave days + training value + retirement + medical
- PDF export of total rewards statement
- Year-over-year comparison

**F-4.12.4 Benefits Administration**
- Track employee benefit enrolments (medical aid, retirement fund, group life)
- Open enrolment periods for benefit selection
- Benefit cost tracking and employer contribution management

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `PayGrade` | id, name, level, salaryMin, salaryMidpoint, salaryMax, currency, effectiveDate, tenantId | — |
| `CompensationReview` | id, cycleId, employeeId, currentSalary, proposedIncrease, proposedPercentage, meritRating, managerNotes, status (PENDING, APPROVED, REJECTED), approvedBy, budgetPool, tenantId | ManyToOne: employee |
| `CompensationCycle` | id, name, year, status (PLANNING, ACTIVE, CLOSED), totalBudget, allocatedBudget, tenantId | OneToMany: reviews |
| `BenefitPlan` | id, name, type (MEDICAL, RETIREMENT, LIFE, OTHER), provider, employerContribution, employeeContribution, isActive, tenantId | — |
| `EmployeeBenefit` | id, employeeId, benefitPlanId, enrolmentDate, status, dependents (JSON), tenantId | ManyToOne: employee, benefitPlan |

#### API Endpoints

```
GET    /api/compensation/pay-grades               — List pay grades
POST   /api/compensation/pay-grades               — Create pay grade
GET    /api/compensation/cycles                   — List review cycles
POST   /api/compensation/cycles                   — Create cycle
GET    /api/compensation/reviews                  — Reviews in cycle (filters)
POST   /api/compensation/reviews/{id}/recommend   — Manager recommendation
POST   /api/compensation/reviews/{id}/approve     — Approve increase
GET    /api/compensation/total-rewards/{employeeId} — Total rewards statement
GET    /api/compensation/benefits                  — Available benefit plans
POST   /api/compensation/benefits/enrol           — Enrol in benefit
GET    /api/compensation/analytics                — Comp analytics (compa-ratios, etc.)
```

#### Frontend Pages

```
/compensation                 — Compensation overview (my comp, total rewards)
/compensation/review          — Manager compensation review interface
/compensation/total-rewards   — Total rewards statement
/admin/compensation/grades    — Pay grade management
/admin/compensation/cycles    — Compensation cycle management
/admin/compensation/benefits  — Benefits plan management
```

---

### 4.13 Org Management

**Category:** New Module
**RFP:** Implicit (organizational objectives, departmental structure)
**Cornerstone:** Org Management module
**Priority:** P2

#### Description

Organization structure management with interactive org charts, position management, headcount planning, and workforce analytics.

#### Features

**F-4.13.1 Org Chart**
- Interactive organizational chart (expandable/collapsible nodes)
- Navigate by department, division, location
- Click-through to employee profile
- Dotted-line reporting relationships
- Drag-and-drop restructuring (with approval)
- Export org chart as image/PDF

**F-4.13.2 Position Management**
- Define positions independent of employees (a position can be filled or vacant)
- Position attributes: title, department, grade, cost centre, reporting-to position, FTE
- Distinguish between position and person (one position, multiple holders for job sharing)
- Vacant position tracking

**F-4.13.3 Headcount Planning**
- Headcount budget by department
- Planned vs actual headcount
- Forecast: upcoming vacancies (retirements, contract ends)
- New position requests with approval workflow

**F-4.13.4 Workforce Analytics**
- Demographics: age distribution, gender, race, disability, tenure
- Turnover rates by department/period
- Span of control analysis (direct reports per manager)
- Workforce cost analysis

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Position` | id, title, positionCode (unique), department, division, costCentre, grade, reportingPositionId (self-ref), fte, status (ACTIVE, FROZEN, ABOLISHED), currentEmployeeId, isVacant, tenantId | ManyToOne: reportingPosition, currentEmployee |
| `HeadcountPlan` | id, department, fiscalYear, plannedHeadcount, currentHeadcount, budgetedCost, approvedBy, status, tenantId | — |
| `OrgUnit` | id, name, type (COMPANY, DIVISION, DEPARTMENT, TEAM, SITE), parentOrgUnitId (self-ref), managerId, costCentre, location, isActive, tenantId | ManyToOne: parent, manager |

#### API Endpoints

```
GET    /api/org/chart                             — Full org chart data
GET    /api/org/chart/{orgUnitId}                  — Subtree from org unit
GET    /api/org/units                             — List org units
POST   /api/org/units                             — Create org unit
GET    /api/org/positions                         — List positions (filter: department, vacant, grade)
POST   /api/org/positions                         — Create position
PUT    /api/org/positions/{id}                    — Update position
GET    /api/org/headcount                         — Headcount plan vs actual
GET    /api/org/workforce-analytics               — Demographics, turnover, etc.
```

#### Frontend Pages

```
/org                          — Org chart (interactive tree)
/org/positions                — Position management
/org/headcount                — Headcount planning
/org/workforce                — Workforce analytics dashboard
/admin/org/units              — Org unit management
```

---

### 4.14 Recruitment & Onboarding (Enhancement)

**Category:** Enhancement of existing module
**RFP:** Section 2.6 — Recruitment & Onboarding
**Cornerstone:** Recruiting + Onboarding
**Priority:** P0

#### Description

ShumelaHire's ATS is already strong. This specification covers enhancements to the onboarding sub-module to meet the RFP's requirements for structured onboarding workflows, digital document signing, and checklist management.

#### Features (New — additions to existing)

**F-4.14.1 Structured Onboarding Workflows**
- Configurable onboarding templates per role/department
- Multi-phase onboarding: preboarding (before day 1), day 1, first week, first month, probation
- Task assignment to multiple parties: new hire, manager, HR, IT, facilities
- Task types: document upload, form completion, training enrolment, meeting schedule, acknowledgment
- Automatic task creation when applicant converts to employee

**F-4.14.2 Onboarding Checklists**
- Configurable checklists: equipment provisioning, system access, policy acknowledgment, benefits enrolment
- Checklist completion tracking with percentage
- Escalation for overdue tasks
- New hire can see their onboarding progress

**F-4.14.3 Preboarding Portal**
- Pre-day-1 access for new hires: complete paperwork, upload documents, read welcome materials
- Digital signing of employment contract, NDA, policies (via DocuSign — existing integration)
- Tax forms (IRP5 setup), banking details capture
- Welcome video and team introduction

**F-4.14.4 Onboarding Analytics**
- Onboarding completion rate per department
- Average time to full onboarding
- Task completion rates (bottleneck identification)
- New hire satisfaction survey at 30/60/90 days

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `OnboardingTemplate` | id, name, applicableRole, applicableDepartment, phases (JSON — [{name, order, tasks[]}]), isActive, tenantId | — |
| `OnboardingPlan` | id, employeeId, templateId, status (NOT_STARTED, IN_PROGRESS, COMPLETED), startDate, completedDate, tenantId | ManyToOne: employee, OneToMany: onboardingTasks |
| `OnboardingTask` | id, planId, phase, title, description, type (DOCUMENT_UPLOAD, FORM, TRAINING, MEETING, ACKNOWLEDGMENT, CUSTOM), assigneeType (NEW_HIRE, MANAGER, HR, IT, FACILITIES), assigneeId, dueDate, status (PENDING, IN_PROGRESS, COMPLETED, OVERDUE, SKIPPED), completedAt, evidence, tenantId | ManyToOne: onboardingPlan |

#### API Endpoints (New additions)

```
POST   /api/onboarding/templates                  — Create template
GET    /api/onboarding/templates                  — List templates
POST   /api/onboarding/plans                      — Create onboarding plan for employee
GET    /api/onboarding/plans/{employeeId}          — Get plan with tasks
POST   /api/onboarding/tasks/{id}/complete        — Mark task complete
GET    /api/onboarding/my-tasks                   — New hire's pending tasks
GET    /api/onboarding/assigned-tasks             — Tasks I need to complete for new hires
GET    /api/onboarding/analytics                  — Onboarding completion metrics
```

#### Frontend Pages (New additions)

```
/onboarding/my-plan           — New hire onboarding portal
/onboarding/manage            — HR onboarding management
/admin/onboarding/templates   — Onboarding template builder
```

---

### 4.15 Analytics & Reporting (Enhancement)

**Category:** Enhancement of existing module
**RFP:** Section 2.8 — Analytics & Reporting Dashboard
**Cornerstone:** Analytics module
**Priority:** P1

#### Description

Extend existing custom report builder and recruitment analytics with predictive analytics, workforce planning, and cross-module reporting for all new modules.

#### Features (New — additions to existing)

**F-4.15.1 Cross-Module Reporting**
- Report builder data sources expanded to include: employees, leave, attendance, performance, goals, learning, engagement, compensation, succession
- Pre-built report templates for each new module
- Combined reports (e.g., "employees with performance score < 3 AND no training completed this year")

**F-4.15.2 Predictive Analytics**
- **Turnover prediction** — ML model predicting flight risk based on tenure, performance, engagement survey scores, leave patterns, manager changes
- **Workforce demand forecasting** — Predict staffing needs based on historical patterns, seasonal trends, growth plans
- **Training ROI prediction** — Predict performance improvement from specific training programs

**F-4.15.3 Real-Time HR Dashboard**
- Unified HR dashboard: headcount, turnover, leave utilization, attendance, open positions, training compliance, engagement score
- Real-time widgets updated via WebSocket (extending existing WebSocket service)
- Executive summary: single-page PDF export of key HR metrics

**F-4.15.4 Compliance Reporting**
- Employment equity report (EEA2/EEA4 — South African statutory requirement)
- Skills development report (WSP/ATR — SETA submission)
- POPIA data subject request log
- Labour Relations Act compliance report

#### API Endpoints (New additions)

```
GET    /api/analytics/hr-dashboard                — Unified HR dashboard data
GET    /api/analytics/predictive/turnover         — Turnover risk predictions
GET    /api/analytics/predictive/workforce-demand  — Demand forecast
GET    /api/analytics/compliance/ee-report         — Employment equity data
GET    /api/analytics/compliance/wsp-atr           — Skills development report data
```

#### AI Capabilities

- **AI Turnover Prediction** — Predict employee flight risk (add to existing AI service abstraction)
- **AI Report Narrative Generation** — Extend existing AiReportNarrativeService to cover all new modules
- **AI Anomaly Detection** — Flag unusual patterns in HR data (sudden attendance drops, leave spikes)

---

### 4.16 Compliance & Security (Enhancement)

**Category:** Enhancement of existing module
**RFP:** Section 2.9 — Compliance & Security Module
**Cornerstone:** Compliance module
**Priority:** P0

#### Description

Strengthen existing GDPR/POPIA compliance and RBAC with South African legislative compliance specific to the uThukela Water context (BCEA, LRA, Employment Equity Act, Skills Development Act, municipal policies).

#### Features (New — additions to existing)

**F-4.16.1 POPIA Compliance Suite**
- Consent management: capture, store, and audit employee consent for data processing
- Data subject access requests (DSAR): employee can request all their data, downloadable as JSON/PDF
- Right to erasure: anonymize employee data (retain aggregate statistics, remove PII)
- Data retention policies: auto-archive/delete data past retention period
- Information Officer register
- POPIA incident breach notification workflow

**F-4.16.2 Labour Relations Act Compliance**
- Disciplinary process workflow: verbal warning → written warning → final written warning → dismissal hearing
- Disciplinary hearing management: notice, chairperson assignment, evidence collection, outcome recording
- Grievance management: capture, investigate, resolve, escalate
- CCMA referral tracking
- Unfair dismissal defence documentation (audit trail)

**F-4.16.3 Employment Equity Compliance**
- EE plan management: targets vs actuals by occupational level, race, gender, disability
- EEA2/EEA4 report generation (South African statutory format)
- Workforce profile dashboard aligned to EE categories
- Barrier analysis tracking

**F-4.16.4 Certification & License Tracking**
- Employee certifications with expiry dates (e.g., water treatment operator license, first aid, safety)
- Auto-reminders for expiring certifications (30, 14, 7 days)
- Compliance dashboard: % of workforce with valid certifications per requirement
- Block assignment to shifts/sites if required certification is expired

**F-4.16.5 Automated Compliance Calendar**
- Annual submission deadlines: EE reports (15 Jan), WSP/ATR (30 Apr), tax reconciliation, etc.
- Auto-generated reminders to responsible parties
- Completion tracking

#### Data Entities (New)

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `Consent` | id, employeeId, purpose, consentText, isGranted, grantedAt, revokedAt, expiresAt, tenantId | ManyToOne: employee |
| `DataSubjectRequest` | id, requestorId, type (ACCESS, ERASURE, RECTIFICATION, PORTABILITY), status (RECEIVED, IN_PROGRESS, COMPLETED, DENIED), requestedAt, completedAt, completedBy, responseUrl, tenantId | ManyToOne: employee |
| `DisciplinaryCase` | id, employeeId, type (VERBAL_WARNING, WRITTEN_WARNING, FINAL_WARNING, DISMISSAL_HEARING, GRIEVANCE), reason, description, status (OPEN, INVESTIGATION, HEARING_SCHEDULED, HEARING_COMPLETED, APPEAL, CLOSED), hearingDate, chairpersonId, outcome, outcomeDate, appealDeadline, isAppealed, ccmaReferenceNumber, tenantId, createdAt | ManyToOne: employee |
| `ComplianceDeadline` | id, name, description, dueDate, frequency (ANNUAL, QUARTERLY, MONTHLY), responsibleRoleId, status (PENDING, COMPLETED, OVERDUE), completedAt, completedBy, tenantId | — |

#### API Endpoints (New additions)

```
# POPIA
POST   /api/compliance/consent                    — Record consent
GET    /api/compliance/consent/{employeeId}        — Employee consent records
POST   /api/compliance/dsar                       — Submit data subject request
GET    /api/compliance/dsar                       — List DSARs
POST   /api/compliance/dsar/{id}/complete         — Complete DSAR

# Disciplinary
POST   /api/compliance/disciplinary               — Create case
GET    /api/compliance/disciplinary                — List cases
GET    /api/compliance/disciplinary/{id}           — Case detail
PUT    /api/compliance/disciplinary/{id}           — Update case
POST   /api/compliance/disciplinary/{id}/outcome   — Record outcome

# Employment Equity
GET    /api/compliance/ee/workforce-profile        — EE workforce profile
GET    /api/compliance/ee/report                   — Generate EEA2/EEA4 report
GET    /api/compliance/ee/targets                  — EE targets vs actuals

# Compliance Calendar
GET    /api/compliance/calendar                    — Compliance deadlines
POST   /api/compliance/calendar/{id}/complete      — Mark deadline completed
```

#### Frontend Pages (New additions)

```
/compliance                   — Compliance hub dashboard
/compliance/popia             — POPIA compliance (consents, DSARs, retention)
/compliance/disciplinary      — Disciplinary case management
/compliance/disciplinary/[id] — Case detail view
/compliance/ee                — Employment equity dashboard & report
/compliance/calendar          — Compliance deadline calendar
/admin/compliance/policies    — Retention policy configuration
```

---

### 4.17 Payroll Integration — Sage Platform

**Category:** New Module
**RFP:** Section 2.10 + Section 4 — Payroll Integration & Sage Integration
**Cornerstone:** Not available (critical Cornerstone gap — ShumelaHire advantage)
**Priority:** P0

#### Description

Bi-directional, real-time integration with Sage 300 People (HR/payroll) and Sage Evolution ERP (financial). This is the RFP's make-or-break requirement. Extends the existing SAP Payroll integration pattern to support Sage as an additional payroll provider.

#### Features

**F-4.17.1 Sage 300 People Integration**
- **Employee master data sync** — Bi-directional: new employees in ShumelaHire → Sage 300, changes in Sage 300 → ShumelaHire
- **Leave data sync** — Leave taken in ShumelaHire → Sage 300 for payroll deductions, leave balances from Sage 300 → ShumelaHire
- **Attendance/time data sync** — Hours worked, overtime → Sage 300 for payroll calculation
- **Payslip retrieval** — Pull payslips from Sage 300 → display in employee self-service
- **Tax data sync** — IRP5 data, tax calculations
- Sync modes: real-time (webhook/event-driven) + scheduled batch (configurable frequency)

**F-4.17.2 Sage Evolution ERP Integration**
- **Payroll journal posting** — Monthly payroll totals posted to Sage Evolution GL
- **Cost centre mapping** — ShumelaHire departments ↔ Sage Evolution cost centres
- **Leave encashment processing** — Encashment amount → Sage Evolution for payment
- **Overtime payment processing** — Calculated overtime → Sage Evolution
- **Budget data retrieval** — Department budgets from Sage Evolution → ShumelaHire compensation module

**F-4.17.3 Integration Framework**
- Provider pattern (extending existing SAP integration architecture): `PayrollProvider` interface with `SagePayrollProvider` implementation alongside existing `SapPayrollProvider`
- Configurable per tenant: which payroll system to integrate with
- Data mapping configuration: field-level mapping between ShumelaHire and Sage schemas
- Conflict resolution: last-write-wins with manual override queue for conflicts
- Error handling: failed syncs go to retry queue (SQS), admin dashboard shows sync status
- Secure API communication: TLS 1.2+, encrypted credentials, API key rotation

**F-4.17.4 Sync Monitoring & Administration**
- Integration dashboard: last sync time, success/failure counts, pending items
- Sync log with full details (request/response payloads, masked PII)
- Manual re-sync trigger per employee or per data type
- Sync health alerts (email/notification if sync fails consecutively)

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `SageIntegrationConfig` | id, type (SAGE_300_PEOPLE, SAGE_EVOLUTION), baseUrl, apiKey (encrypted), clientId, clientSecret (encrypted), companyId, syncFrequencyMinutes, isActive, lastSyncAt, fieldMapping (JSON), tenantId | — |
| `SageSyncLog` | id, configId, direction (INBOUND, OUTBOUND), dataType (EMPLOYEE, LEAVE, TIME, PAYSLIP, JOURNAL), entityId, status (SUCCESS, FAILED, PENDING_RETRY, CONFLICT), requestPayload (JSON, masked), responsePayload (JSON, masked), errorMessage, retryCount, tenantId, createdAt | ManyToOne: config |
| `SageFieldMapping` | id, configId, shumelaField, sageField, transformationType (DIRECT, LOOKUP, CALCULATED), transformationConfig (JSON), tenantId | ManyToOne: config |
| `PayrollProvider` | (interface) — abstracts SAP/Sage/future providers; existing `SapPayrollService` refactored to implement this | — |

#### API Endpoints

```
# Configuration
GET    /api/integrations/sage/config               — Get Sage integration config
POST   /api/integrations/sage/config               — Create/update config
POST   /api/integrations/sage/config/test           — Test connection

# Sync Operations
POST   /api/integrations/sage/sync/employees        — Trigger employee sync
POST   /api/integrations/sage/sync/leave            — Trigger leave sync
POST   /api/integrations/sage/sync/time             — Trigger time/attendance sync
POST   /api/integrations/sage/sync/payroll-journal   — Post payroll journal to Evolution
POST   /api/integrations/sage/sync/employee/{id}    — Sync specific employee

# Monitoring
GET    /api/integrations/sage/sync/status           — Integration health dashboard
GET    /api/integrations/sage/sync/logs             — Sync logs (paginated, filterable)
GET    /api/integrations/sage/sync/conflicts        — Pending conflicts
POST   /api/integrations/sage/sync/conflicts/{id}/resolve — Resolve conflict

# Payslips
GET    /api/integrations/sage/payslips/{employeeId}  — Employee payslips from Sage
GET    /api/integrations/sage/payslips/{employeeId}/{period} — Specific payslip

# Webhooks (Sage → ShumelaHire)
POST   /api/integrations/sage/webhook               — Receive Sage change events
```

#### Frontend Pages

```
/admin/integrations/sage      — Sage integration configuration
/admin/integrations/sage/sync — Sync monitoring dashboard
/admin/integrations/sage/logs — Sync log viewer
/admin/integrations/sage/mapping — Field mapping configuration
```

---

## 5. Platform Capabilities

---

### 5.1 Mobile Application (iOS/Android)

**Category:** New Platform Capability
**RFP:** Section 2.11 — Mobile App
**Cornerstone:** Mobile (learning-focused)
**Priority:** P0

#### Description

Native mobile application for iOS and Android providing core HR self-service features including leave requests, attendance tracking with GPS/geofencing, push notifications, and learning access.

#### Technology

- **Framework:** React Native (shares component logic with existing React codebase)
- **State Management:** React Query (aligns with SWR pattern on web)
- **Navigation:** React Navigation
- **Push Notifications:** Firebase Cloud Messaging (FCM)
- **GPS/Location:** React Native Geolocation + background location services
- **Offline:** AsyncStorage + sync queue for offline-first capability
- **Distribution:** Apple App Store + Google Play Store

#### Mobile Features

| Feature | Description |
|---------|------------|
| **Leave** | View balances, submit requests, approve/reject (manager), leave calendar |
| **Attendance** | Clock-in/out with GPS, view attendance history, shift schedule |
| **Notifications** | Push notifications for approvals, reminders, deadlines, announcements |
| **Performance** | View reviews, update goal progress, acknowledge reviews |
| **Learning** | Browse courses, complete mobile-friendly courses, view certifications |
| **Directory** | Employee directory with quick call/email |
| **Profile** | View/edit personal details, view payslip, documents |
| **Engagement** | Complete pulse surveys, give recognition |
| **Approvals** | Universal approval inbox (leave, overtime, requisitions, expenses) |
| **Offline Mode** | Cache key data, queue actions for sync when reconnected |

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `DeviceRegistration` | id, employeeId, deviceToken, platform (IOS, ANDROID), deviceModel, osVersion, appVersion, isActive, registeredAt, lastActiveAt, tenantId | ManyToOne: employee |

#### API Endpoints (New)

```
POST   /api/mobile/devices/register              — Register device for push
DELETE /api/mobile/devices/{token}                — Unregister device
POST   /api/mobile/push/send                     — Send push notification
GET    /api/mobile/sync/delta                    — Delta sync (changes since timestamp)
```

---

### 5.2 Hybrid Deployment Architecture

**Category:** New Platform Capability
**RFP:** Section 3 — Technical Infrastructure Requirements
**Priority:** P0

#### Description

Adapt ShumelaHire for hybrid deployment supporting both cloud (existing) and on-premises (new) hosting to meet uThukela Water's Hyper-V + Azure Stack HCI requirements.

#### Features

**F-5.2.1 Database Abstraction**
- Abstract data layer to support both PostgreSQL (cloud default) and Microsoft SQL Server 2022 (on-prem requirement)
- Flyway migrations compatible with both databases (avoid PostgreSQL-specific syntax)
- Connection pooling configuration for SQL Server (HikariCP)
- SQL Server TDE (Transparent Data Encryption) support

**F-5.2.2 Deployment Modes**
- **Cloud Mode** (existing): AWS infrastructure, PostgreSQL RDS, S3, SES, SQS, Cognito
- **Hybrid Mode** (new): Spring Boot application on Hyper-V VM within Azure Stack HCI, SQL Server database, on-prem file storage with cloud sync, AD authentication
- **On-Premises Mode** (new): Fully self-contained deployment for air-gapped environments
- Docker containers for consistent deployment across modes
- Helm charts for Kubernetes deployment (Azure Stack HCI supports AKS)

**F-5.2.3 Infrastructure Compatibility**
- Windows Server 2022 support (Docker on Windows or native Java deployment)
- Hyper-V VM template with pre-configured environment
- Compatible with Vodacom SD-WAN (standard HTTP/HTTPS traffic)
- Firewall port documentation (SonicWall NSA2700, Meraki)
- NAS-compatible backup strategy (database dumps + file backup)

**F-5.2.4 Disaster Recovery**
- Automated database backups (full daily, differential hourly)
- Point-in-time recovery capability
- Geo-redundant backup storage
- DR runbook with RTO/RPO targets
- Failover testing procedures

#### Configuration Changes

```yaml
# New application-hybrid.yml profile
spring:
  datasource:
    url: jdbc:sqlserver://${DB_HOST}:1433;databaseName=${DB_NAME};encrypt=true;trustServerCertificate=true
    driver-class-name: com.microsoft.sqlserver.jdbc.SQLServerDriver
  jpa:
    database-platform: org.hibernate.dialect.SQLServerDialect

storage:
  provider: local  # or azure-blob for Azure Stack HCI
  local:
    base-path: /data/shumelahire/files

auth:
  provider: active-directory  # instead of cognito
  ad:
    domain: uthukela.local
    url: ldap://ad.uthukela.local:389
```

---

### 5.3 Active Directory Integration

**Category:** New Platform Capability
**RFP:** Section 3.1 — "Full Active Directory integration for SSO and RBAC"
**Priority:** P0

#### Description

Native Active Directory integration for authentication and role mapping, extending the existing Cognito/SAML authentication to support on-premises AD environments.

#### Features

**F-5.3.1 AD Authentication**
- LDAP bind authentication against on-premises Active Directory
- Kerberos/SPNEGO for seamless SSO from domain-joined workstations
- ADFS integration for federated authentication
- Fallback to form-based login for non-domain devices

**F-5.3.2 AD User Provisioning**
- Auto-create ShumelaHire user accounts from AD group membership
- Sync user attributes: name, email, department, title, phone
- Configurable AD group → ShumelaHire role mapping (e.g., "HR-Managers" AD group → HR_MANAGER role)
- Scheduled sync for attribute changes (e.g., department change in AD reflects in ShumelaHire)
- Disable ShumelaHire account when AD account is disabled

**F-5.3.3 Strong Password Policy**
- Inherit AD password policy (complexity, expiry, history)
- Multi-factor authentication support (AD MFA or Azure MFA)

#### Data Entities

| Entity | Key Fields | Relationships |
|--------|-----------|---------------|
| `ADGroupRoleMapping` | id, adGroupName, adGroupDN, shumelaRole, isActive, tenantId | — |
| `ADSyncLog` | id, syncType (FULL, DELTA), usersCreated, usersUpdated, usersDisabled, errors (JSON), syncedAt, tenantId | — |

#### API Endpoints

```
POST   /api/auth/ad/login                        — AD authentication
GET    /api/auth/ad/sso                          — SPNEGO/Kerberos SSO
GET    /api/admin/ad/groups                       — List AD groups
POST   /api/admin/ad/group-mappings              — Create group → role mapping
GET    /api/admin/ad/group-mappings              — List mappings
POST   /api/admin/ad/sync                        — Trigger manual sync
GET    /api/admin/ad/sync/logs                    — Sync history
```

---

### 5.4 Low-Bandwidth Optimization

**Category:** New Platform Capability
**RFP:** Section 3.2 — "Optimized for 8MB-10MB links with LTE failover (100GB cap)"
**Priority:** P1

#### Features

**F-5.4.1 Frontend Optimization**
- Aggressive code splitting (already partial — enhance per-module splitting)
- Image optimization: WebP format, lazy loading, responsive sizes
- Compression: Brotli/gzip for all responses
- Service Worker caching: cache static assets, API responses for offline
- Skeleton loading for perceived performance

**F-5.4.2 Data Optimization**
- API pagination with configurable page sizes (smaller defaults for low-bandwidth)
- Delta sync: only send changed records since last sync timestamp
- Compressed API responses (JSON with gzip)
- Field-level selection: API consumers can request only needed fields
- Background data prefetching during idle time

**F-5.4.3 Offline-First Design**
- IndexedDB for client-side data storage
- Action queue: offline actions (leave requests, clock-in, approvals) queued and synced when online
- Conflict resolution: server-wins with user notification
- Connection status indicator in UI
- Graceful degradation: read-only mode when offline, full function when online

**F-5.4.4 Progressive Web App (PWA)**
- PWA manifest for installability
- Service Worker for offline caching
- Background sync for queued actions
- Push notifications via Web Push API (complement to native push)

---

### 5.5 Multilingual Support

**Category:** New Platform Capability
**RFP:** UX section mentions multilingual support
**Priority:** P2

#### Features

**F-5.5.1 Internationalization (i18n) Framework**
- next-intl or react-i18next integration in Next.js
- Translation key system for all UI strings
- Language selector in user preferences
- Default language: English
- RTL support architecture (for future languages)

**F-5.5.2 South African Languages (Initial)**
- English (default)
- isiZulu (primary for KZN)
- Afrikaans
- Additional languages loaded dynamically from translation files

**F-5.5.3 Content Translation**
- System UI translated via key files
- User-generated content (surveys, policies, announcements) — manual translation with language variants
- Email templates with language variants
- Mobile app language follows user preference

---

### 5.6 Marketplace & Ecosystem

**Category:** New Platform Capability
**RFP:** Not required
**Cornerstone:** Content Marketplace + Integration Marketplace
**Priority:** P3

#### Features

**F-5.6.1 Content Marketplace**
- Curated third-party learning content catalog
- Content providers can list courses with pricing
- Tenant-level content purchasing
- Usage tracking and licensing

**F-5.6.2 Integration Marketplace**
- Pre-built connectors for common HR tools
- Connector configuration without code
- Custom webhook integration builder

**F-5.6.3 API Ecosystem**
- Public API documentation (OpenAPI/Swagger — extending existing SpringDoc)
- API key management for third-party developers
- Rate limiting and usage analytics
- Developer portal

---

## 6. Module Dependency Map

Modules should be built in dependency order. Arrows (→) indicate "depends on":

```
Core HR (4.1) → [Foundation — all other modules depend on Employee entity]
  ├── Leave Administration (4.2)
  │     └── Payroll/Sage Integration (4.17)
  ├── Time & Attendance (4.3)
  │     └── Payroll/Sage Integration (4.17)
  ├── Performance Management (4.4)
  │     ├── Goal Management (4.5)
  │     ├── 360 Feedback (4.6)
  │     └── Competency & Skills (4.7)
  │           ├── Learning Management (4.8)
  │           │     └── Content Authoring (4.9)
  │           └── Succession Planning (4.10)
  ├── Employee Engagement (4.11)
  ├── Compensation Management (4.12)
  ├── Org Management (4.13)
  ├── Recruitment & Onboarding Enhancement (4.14)
  └── Compliance & Security Enhancement (4.16)

Analytics & Reporting Enhancement (4.15) → [All modules — cross-cutting]

Platform Capabilities (independent, parallel):
  ├── Hybrid Deployment (5.2) — can start immediately
  ├── Active Directory (5.3) — depends on 5.2
  ├── Low-Bandwidth Optimization (5.4) — can start immediately
  ├── Mobile App (5.1) — depends on 4.1, 4.2, 4.3 APIs existing
  ├── Multilingual (5.5) — can start immediately (i18n framework)
  └── Marketplace (5.6) — depends on 4.8
```

---

## 7. Implementation Phases

### Phase 0: Foundation (Weeks 1-4)
> Architectural prerequisites that all other modules depend on.

| Module | Scope |
|--------|-------|
| **Core HR (4.1)** | Employee entity, applicant-to-employee conversion, employee directory, document management, custom fields |
| **Hybrid Deployment (5.2)** | SQL Server support, dual-database Flyway migrations, deployment profiles |
| **Active Directory (5.3)** | LDAP authentication, AD group mapping, user provisioning |
| **Compliance Enhancement (4.16)** | POPIA consent management, disciplinary workflow, EE reporting |

### Phase 1: RFP-Critical Modules (Weeks 5-10)
> Modules explicitly required by the uThukela Water RFP.

| Module | Scope |
|--------|-------|
| **Leave Administration (4.2)** | Full leave lifecycle — types, requests, approvals, balances, encashment, calendar |
| **Time & Attendance (4.3)** | Clock-in/out, geofencing, shifts, overtime, attendance reports |
| **Payroll/Sage Integration (4.17)** | Sage 300 People sync, Sage Evolution ERP journal posting, payslip retrieval |
| **Recruitment Enhancement (4.14)** | Structured onboarding workflows, checklists, preboarding portal |
| **Performance Enhancement (4.4)** | Review cycles, KRA alignment, PIPs, competency mapping |
| **Employee Engagement (4.11)** | Pulse surveys, recognition, wellness check-ins |

### Phase 2: Cornerstone Parity — Talent (Weeks 11-16)
> Modules needed to match Cornerstone's talent management suite.

| Module | Scope |
|--------|-------|
| **Goal Management (4.5)** | OKR hierarchy, progress tracking, goal-performance linking |
| **360 Feedback (4.6)** | Feedback campaigns, questionnaires, results/reports |
| **Competency & Skills (4.7)** | Skills taxonomy, competency models, gap analysis |
| **Learning Management (4.8)** | Course management, SCORM/xAPI, learning paths, certifications, assessments |
| **Analytics Enhancement (4.15)** | Cross-module reporting, predictive analytics, compliance reports |

### Phase 3: Cornerstone Parity — Enterprise (Weeks 17-22)
> Advanced enterprise modules for full Cornerstone parity.

| Module | Scope |
|--------|-------|
| **Succession Planning (4.10)** | 9-box grid, succession bench, career pathing, talent reviews |
| **Compensation Management (4.12)** | Pay grades, comp reviews, total rewards, benefits |
| **Org Management (4.13)** | Org charts, position management, headcount planning |
| **Content Authoring (4.9)** | Slide-based course builder, video content, microlearning |
| **Mobile App (5.1)** | React Native app with leave, attendance, notifications, learning |
| **Low-Bandwidth Optimization (5.4)** | Offline-first, service workers, delta sync |

### Phase 4: Platform Maturity (Weeks 23+)
> Polish, scale, and differentiate.

| Module | Scope |
|--------|-------|
| **Multilingual Support (5.5)** | i18n framework, isiZulu, Afrikaans translations |
| **Marketplace (5.6)** | Content marketplace, integration marketplace, API ecosystem |
| **AI Enhancements** | Extend AI across all new modules (turnover prediction, learning recommendations, anomaly detection) |

---

## Appendix A: RFP Compliance Mapping

| RFP Section | ShumelaHire Module | Status |
|------------|-------------------|--------|
| 2.1 Employee Profiles | 4.1 Core HR & Employee Lifecycle | New Build |
| 2.2 Leave Administration | 4.2 Leave Administration | New Build |
| 2.3 Performance Management | 4.4 + 4.5 + 4.6 + 4.7 | Enhance + New |
| 2.4 Training & Development | 4.8 LMS + 4.9 Content Authoring | New Build |
| 2.5 Time & Attendance | 4.3 Time & Attendance | New Build |
| 2.6 Recruitment & Onboarding | 4.14 Enhancement | Enhance |
| 2.7 Employee Engagement | 4.11 Employee Engagement | New Build |
| 2.8 Analytics & Reporting | 4.15 Enhancement | Enhance |
| 2.9 Compliance & Security | 4.16 Enhancement | Enhance |
| 2.10 Payroll Integration | 4.17 Sage Platform | New Build |
| 2.11 Mobile App | 5.1 Mobile Application | New Build |
| 3.1 Infrastructure | 5.2 Hybrid Deployment + 5.3 AD | New Build |
| 3.2 Network | 5.4 Low-Bandwidth Optimization | New Build |
| Section 4 Sage Integration | 4.17 Sage Platform | New Build |

## Appendix B: Cornerstone Parity Mapping

| Cornerstone Module | ShumelaHire Module | Status |
|-------------------|-------------------|--------|
| Core HR | 4.1 Core HR & Employee Lifecycle | New Build |
| Performance | 4.4 Performance Management | Enhance |
| Goals | 4.5 Goal Management | New Build |
| 360 Feedback | 4.6 360 Feedback | New Build |
| Skills Edge | 4.7 Competency & Skills Framework | New Build |
| Learning (LMS) | 4.8 Learning Management System | New Build |
| Content Studio | 4.9 Content Authoring | New Build |
| Succession | 4.10 Succession Planning | New Build |
| Compensation | 4.12 Compensation Management | New Build |
| Recruiting | Existing ATS (already strong) | Production |
| Onboarding | 4.14 Recruitment & Onboarding Enhancement | Enhance |
| Analytics | 4.15 Analytics & Reporting Enhancement | Enhance |
| Compliance | 4.16 Compliance & Security Enhancement | Enhance |
| Mobile | 5.1 Mobile Application | New Build |

## Appendix C: New Entity Count Summary

| Module | New Entities |
|--------|:---:|
| Core HR (4.1) | 5 |
| Leave Administration (4.2) | 8 |
| Time & Attendance (4.3) | 7 |
| Performance (4.4) | 6 |
| Goal Management (4.5) | 3 |
| 360 Feedback (4.6) | 4 |
| Competency & Skills (4.7) | 6 |
| LMS (4.8) | 11 |
| Content Authoring (4.9) | 2 |
| Succession Planning (4.10) | 4 |
| Employee Engagement (4.11) | 6 |
| Compensation (4.12) | 5 |
| Org Management (4.13) | 3 |
| Onboarding (4.14) | 3 |
| Compliance (4.16) | 4 |
| Sage Integration (4.17) | 4 |
| Mobile (5.1) | 1 |
| AD Integration (5.3) | 2 |
| **Total New Entities** | **84** |

---

*This document serves as the authoritative feature specification for expanding ShumelaHire from a Talent Acquisition Platform to a full Human Capital Management suite. Each module should be built following existing architectural patterns: Spring Boot service layer, JPA repositories, REST controllers, Next.js App Router pages, and the established multi-tenant security model.*
