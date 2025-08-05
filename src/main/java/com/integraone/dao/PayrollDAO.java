package com.integraone.dao;

import com.integraone.model.Payroll;
import com.integraone.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PayrollDAO {
    
    public boolean addPayrollRecord(Payroll payroll) {
        String sql = "INSERT INTO payroll (employee_id, basic_salary, allowances, deductions, net_pay, pay_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payroll.getEmployeeId());
            stmt.setDouble(2, payroll.getBasicSalary());
            stmt.setDouble(3, payroll.getAllowances());
            stmt.setDouble(4, payroll.getDeductions());
            stmt.setDouble(5, payroll.getNetPay());
            stmt.setDate(6, Date.valueOf(payroll.getPayDate()));
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Payroll> getAllPayrollRecords() {
        List<Payroll> payrollRecords = new ArrayList<>();
        String sql = "SELECT * FROM payroll ORDER BY pay_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setId(rs.getInt("id"));
                payroll.setEmployeeId(rs.getInt("employee_id"));
                payroll.setBasicSalary(rs.getDouble("basic_salary"));
                payroll.setAllowances(rs.getDouble("allowances"));
                payroll.setDeductions(rs.getDouble("deductions"));
                payroll.setNetPay(rs.getDouble("net_pay"));
                payroll.setPayDate(rs.getDate("pay_date").toLocalDate());
                payrollRecords.add(payroll);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payrollRecords;
    }
    
    public List<Payroll> getPayrollByEmployee(int employeeId) {
        List<Payroll> payrollRecords = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE employee_id = ? ORDER BY pay_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setId(rs.getInt("id"));
                payroll.setEmployeeId(rs.getInt("employee_id"));
                payroll.setBasicSalary(rs.getDouble("basic_salary"));
                payroll.setAllowances(rs.getDouble("allowances"));
                payroll.setDeductions(rs.getDouble("deductions"));
                payroll.setNetPay(rs.getDouble("net_pay"));
                payroll.setPayDate(rs.getDate("pay_date").toLocalDate());
                payrollRecords.add(payroll);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payrollRecords;
    }
    
    public List<Payroll> getPayrollByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Payroll> payrollRecords = new ArrayList<>();
        String sql = "SELECT * FROM payroll WHERE pay_date BETWEEN ? AND ? ORDER BY pay_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Payroll payroll = new Payroll();
                payroll.setId(rs.getInt("id"));
                payroll.setEmployeeId(rs.getInt("employee_id"));
                payroll.setBasicSalary(rs.getDouble("basic_salary"));
                payroll.setAllowances(rs.getDouble("allowances"));
                payroll.setDeductions(rs.getDouble("deductions"));
                payroll.setNetPay(rs.getDouble("net_pay"));
                payroll.setPayDate(rs.getDate("pay_date").toLocalDate());
                payrollRecords.add(payroll);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return payrollRecords;
    }
    
    public double getTotalPayrollAmount() {
        String sql = "SELECT SUM(net_pay) as total FROM payroll";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}