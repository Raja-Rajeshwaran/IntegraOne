package com.integraone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Payroll {
    private int id;
    private int employeeId;
    private double basicSalary;
    private double allowances;
    private double deductions;
    private double netPay;
    private LocalDate payDate;
    private LocalDateTime createdAt;
    
    public Payroll() {}
    
    public Payroll(int employeeId, double basicSalary, double allowances, double deductions, double netPay, LocalDate payDate) {
        this.employeeId = employeeId;
        this.basicSalary = basicSalary;
        this.allowances = allowances;
        this.deductions = deductions;
        this.netPay = netPay;
        this.payDate = payDate;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    
    public double getAllowances() { return allowances; }
    public void setAllowances(double allowances) { this.allowances = allowances; }
    
    public double getDeductions() { return deductions; }
    public void setDeductions(double deductions) { this.deductions = deductions; }
    
    public double getNetPay() { return netPay; }
    public void setNetPay(double netPay) { this.netPay = netPay; }
    
    public LocalDate getPayDate() { return payDate; }
    public void setPayDate(LocalDate payDate) { this.payDate = payDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Payroll{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", basicSalary=" + basicSalary +
                ", allowances=" + allowances +
                ", deductions=" + deductions +
                ", netPay=" + netPay +
                ", payDate=" + payDate +
                '}';
    }
}