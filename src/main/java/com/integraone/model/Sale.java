package com.integraone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Sale {
    private int id;
    private int productId;
    private int quantity;
    private double price;
    private LocalDate saleDate;
    private LocalDateTime createdAt;
    
    public Sale() {}
    
    public Sale(int productId, int quantity, double price, LocalDate saleDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.saleDate = saleDate;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public LocalDate getSaleDate() { return saleDate; }
    public void setSaleDate(LocalDate saleDate) { this.saleDate = saleDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", saleDate=" + saleDate +
                '}';
    }
}