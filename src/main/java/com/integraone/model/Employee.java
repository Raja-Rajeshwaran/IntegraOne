package com.integraone.model;

import java.time.LocalDateTime;

public class Employee {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private double basicSalary;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Employee() {}
    
    public Employee(String name, String email, String phone, String role, double basicSalary) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.basicSalary = basicSalary;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    
    public double getBasicSalary() { return basicSalary; }
    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return name; 
    }
}