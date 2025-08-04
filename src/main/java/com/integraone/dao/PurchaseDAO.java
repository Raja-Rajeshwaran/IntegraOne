package com.integraone.dao;

import com.integraone.model.Purchase;
import com.integraone.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PurchaseDAO {

	public boolean addPurchase(Purchase purchase) {
	    String sql = "INSERT INTO purchases (product_id, quantity, price, purchase_date, created_at) VALUES (?, ?, ?, ?, ?)";
	    
	    try (Connection conn = DatabaseConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {
	        
	        stmt.setInt(1, purchase.getProductId());
	        stmt.setInt(2, purchase.getQuantity());
	        stmt.setDouble(3, purchase.getPrice());
	        stmt.setDate(4, Date.valueOf(purchase.getPurchaseDate()));
	        
	        // Handle created_at (set to now if null)
	        LocalDateTime createdAt = purchase.getCreatedAt() != null 
	                                  ? purchase.getCreatedAt()
	                                  : LocalDateTime.now();
	        stmt.setTimestamp(5, Timestamp.valueOf(createdAt));
	        
	        return stmt.executeUpdate() > 0;
	        
	    } catch (SQLException e) {
	        System.err.println("[ERROR] Failed to insert purchase:");
	        e.printStackTrace();
	        return false;
	    }
	}


    public List<Purchase> getAllPurchases() {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases ORDER BY purchase_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Purchase purchase = mapResultSetToPurchase(rs);
                purchases.add(purchase);
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch purchases:");
            e.printStackTrace();
        }

        return purchases;
    }

    public List<Purchase> getPurchasesByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Purchase> purchases = new ArrayList<>();
        String sql = "SELECT * FROM purchases WHERE purchase_date BETWEEN ? AND ? ORDER BY purchase_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Purchase purchase = mapResultSetToPurchase(rs);
                purchases.add(purchase);
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to fetch purchases in date range:");
            e.printStackTrace();
        }

        return purchases;
    }

    public double getTotalPurchaseAmount() {
        String sql = "SELECT SUM(quantity * price) AS total FROM purchases";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }

        } catch (SQLException e) {
            System.err.println("[ERROR] Failed to calculate total purchase amount:");
            e.printStackTrace();
        }

        return 0.0;
    }

    private Purchase mapResultSetToPurchase(ResultSet rs) throws SQLException {
        Purchase purchase = new Purchase();
        purchase.setId(rs.getInt("id"));
        purchase.setProductId(rs.getInt("product_id"));
        purchase.setQuantity(rs.getInt("quantity"));
        purchase.setPrice(rs.getDouble("price"));
        purchase.setPurchaseDate(rs.getDate("purchase_date").toLocalDate());

        // Optional status column
        
        return purchase;
    }
}
