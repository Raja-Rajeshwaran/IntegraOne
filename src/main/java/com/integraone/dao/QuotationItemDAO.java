// src/main/java/com/integraone/dao/QuotationItemDAO.java
package com.integraone.dao;

import com.integraone.model.QuotationItem;
import com.integraone.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuotationItemDAO {
    
    public boolean addQuotationItem(QuotationItem quotationItem) {
        String sql = "INSERT INTO quotation_items (quotation_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationItem.getQuotationId());
            stmt.setInt(2, quotationItem.getProductId());
            stmt.setInt(3, quotationItem.getQuantity());
            stmt.setDouble(4, quotationItem.getUnitPrice());
            stmt.setDouble(5, quotationItem.getSubtotal());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean updateQuotationItem(QuotationItem quotationItem) {
        String sql = "UPDATE quotation_items SET quotation_id = ?, product_id = ?, quantity = ?, unit_price = ?, subtotal = ? WHERE quotation_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationItem.getQuotationId());
            stmt.setInt(2, quotationItem.getProductId());
            stmt.setInt(3, quotationItem.getQuantity());
            stmt.setDouble(4, quotationItem.getUnitPrice());
            stmt.setDouble(5, quotationItem.getSubtotal());
            stmt.setInt(6, quotationItem.getQuotationId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteQuotationItem(int quotationItemId) {
        String sql = "DELETE FROM quotation_items WHERE quotation_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationItemId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteQuotationItemsByQuotationId(int quotationId) {
        String sql = "DELETE FROM quotation_items WHERE quotation_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public QuotationItem getQuotationItemById(int quotationItemId) {
        String sql = "SELECT * FROM quotation_items WHERE quotation_item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationItemId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractQuotationItemFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<QuotationItem> getQuotationItemsByQuotationId(int quotationId) {
        String sql = "SELECT * FROM quotation_items WHERE quotation_id = ?";
        List<QuotationItem> quotationItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                quotationItems.add(extractQuotationItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quotationItems;
    }
    
    public List<QuotationItem> getAllQuotationItems() {
        String sql = "SELECT * FROM quotation_items";
        List<QuotationItem> quotationItems = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                quotationItems.add(extractQuotationItemFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quotationItems;
    }
    
    private QuotationItem extractQuotationItemFromResultSet(ResultSet rs) throws SQLException {
        QuotationItem quotationItem = new QuotationItem();
        quotationItem.setQuotationId(rs.getInt("quotation_item_id"));
        quotationItem.setQuotationId(rs.getInt("quotation_id"));
        quotationItem.setProductId(rs.getInt("product_id"));
        quotationItem.setQuantity(rs.getInt("quantity"));
        quotationItem.setUnitPrice(rs.getDouble("unit_price"));
        quotationItem.setSubtotal(rs.getDouble("subtotal"));
        return quotationItem;
    }
}