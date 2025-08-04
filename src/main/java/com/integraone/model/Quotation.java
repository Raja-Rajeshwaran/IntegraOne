package com.integraone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Quotation model class
 */
public class Quotation {
    private int id;
    private int customerId;
    private LocalDate quotationDate;
    private double totalAmount;
    private QuotationStatus status;
    private LocalDateTime createdAt;
    
    public enum QuotationStatus {
        DRAFT, SENT, ACCEPTED, REJECTED
    }
    
    // Constructors
    public Quotation() {}
    
    public Quotation(int customerId, LocalDate quotationDate, double totalAmount, QuotationStatus status) {
        this.customerId = customerId;
        this.quotationDate = quotationDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    
    public LocalDate getQuotationDate() { return quotationDate; }
    public void setQuotationDate(LocalDate quotationDate) { this.quotationDate = quotationDate; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public QuotationStatus getStatus() { return status; }
    public void setStatus(QuotationStatus status) { this.status = status; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Quotation{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", quotationDate=" + quotationDate +
                ", totalAmount=" + totalAmount +
                ", status=" + status +
                '}';
    }
}