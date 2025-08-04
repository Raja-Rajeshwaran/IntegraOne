package com.integraone.model;

/**
 * QuotationItem model class
 */
public class QuotationItem {
    private int id;
    private int quotationId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    
    // Constructors
    public QuotationItem() {}
    
    public QuotationItem(int quotationId, int productId, int quantity, double unitPrice, double subtotal) {
        this.quotationId = quotationId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = subtotal;
    }
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getQuotationId() { return quotationId; }
    public void setQuotationId(int quotationId) { this.quotationId = quotationId; }
    
    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    @Override
    public String toString() {
        return "QuotationItem{" +
                "id=" + id +
                ", quotationId=" + quotationId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", subtotal=" + subtotal +
                '}';
    }
}