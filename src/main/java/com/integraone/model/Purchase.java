package com.integraone.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Purchase {
    private int id;
    private int productId;
    private int quantity;
    private double price;
    private LocalDate purchaseDate;
    private LocalDateTime createdAt;
    
    public Purchase() {}
    
    public Purchase(int productId, int quantity, double price, LocalDate purchaseDate) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.purchaseDate = purchaseDate;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public double getTotalAmount() {
        return quantity * price;
    }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", purchaseDate=" + purchaseDate +
                '}';
    }
}