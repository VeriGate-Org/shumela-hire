package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeaveBalance;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeaveBalanceResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private Integer leaveYear;
    private BigDecimal openingBalance;
    private BigDecimal accrued;
    private BigDecimal used;
    private BigDecimal pending;
    private BigDecimal carriedOver;
    private BigDecimal adjustment;
    private String adjustmentReason;
    private BigDecimal encashed;
    private BigDecimal forfeited;
    private BigDecimal closingBalance;
    private BigDecimal availableBalance;
    private LocalDate lastAccrualDate;

    public LeaveBalanceResponse(LeaveBalance balance) {
        this.id = balance.getId();
        this.employeeId = balance.getEmployee().getId();
        this.employeeName = balance.getEmployee().getFullName();
        this.leaveTypeId = balance.getLeaveType().getId();
        this.leaveTypeName = balance.getLeaveType().getName();
        this.leaveTypeCode = balance.getLeaveType().getCode();
        this.leaveYear = balance.getLeaveYear();
        this.openingBalance = balance.getOpeningBalance();
        this.accrued = balance.getAccrued();
        this.used = balance.getUsed();
        this.pending = balance.getPending();
        this.carriedOver = balance.getCarriedOver();
        this.adjustment = balance.getAdjustment();
        this.adjustmentReason = balance.getAdjustmentReason();
        this.encashed = balance.getEncashed();
        this.forfeited = balance.getForfeited();
        this.closingBalance = balance.getClosingBalance();
        this.availableBalance = balance.getAvailableBalance();
        this.lastAccrualDate = balance.getLastAccrualDate();
    }

    public static LeaveBalanceResponse fromEntity(LeaveBalance balance) {
        return new LeaveBalanceResponse(balance);
    }

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public String getLeaveTypeCode() { return leaveTypeCode; }
    public Integer getLeaveYear() { return leaveYear; }
    public BigDecimal getOpeningBalance() { return openingBalance; }
    public BigDecimal getAccrued() { return accrued; }
    public BigDecimal getUsed() { return used; }
    public BigDecimal getPending() { return pending; }
    public BigDecimal getCarriedOver() { return carriedOver; }
    public BigDecimal getAdjustment() { return adjustment; }
    public String getAdjustmentReason() { return adjustmentReason; }
    public BigDecimal getEncashed() { return encashed; }
    public BigDecimal getForfeited() { return forfeited; }
    public BigDecimal getClosingBalance() { return closingBalance; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public LocalDate getLastAccrualDate() { return lastAccrualDate; }
}
