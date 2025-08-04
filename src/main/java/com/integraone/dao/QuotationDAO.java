package com.integraone.dao;

import com.integraone.model.Quotation;
import com.integraone.model.QuotationItem;
import com.integraone.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Quotation operations
 */
public class QuotationDAO {
    
    public boolean createQuotationWithItems(Quotation quotation, List<QuotationItem> quotationItems) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction
            
            // Insert quotation
            String quotationSql = "INSERT INTO quotations (customer_id, quotation_date, total_amount, status) VALUES (?, ?, ?, ?)";
            PreparedStatement quotationStmt = conn.prepareStatement(quotationSql, Statement.RETURN_GENERATED_KEYS);
            
            quotationStmt.setInt(1, quotation.getCustomerId());
            quotationStmt.setDate(2, Date.valueOf(quotation.getQuotationDate()));
            quotationStmt.setDouble(3, quotation.getTotalAmount());
            quotationStmt.setString(4, quotation.getStatus().toString());
            
            int quotationResult = quotationStmt.executeUpdate();
            if (quotationResult == 0) {
                conn.rollback();
                return false;
            }
            
            // Get generated quotation ID
            ResultSet generatedKeys = quotationStmt.getGeneratedKeys();
            int quotationId = 0;
            if (generatedKeys.next()) {
                quotationId = generatedKeys.getInt(1);
            } else {
                conn.rollback();
                return false;
            }
            
            // Insert quotation items
            String itemSql = "INSERT INTO quotation_items (quotation_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement itemStmt = conn.prepareStatement(itemSql);
            
            for (QuotationItem item : quotationItems) {
                itemStmt.setInt(1, quotationId);
                itemStmt.setInt(2, item.getProductId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getUnitPrice());
                itemStmt.setDouble(5, item.getSubtotal());
                itemStmt.addBatch();
            }
            
            int[] itemResults = itemStmt.executeBatch();
            for (int result : itemResults) {
                if (result == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit(); // Commit transaction
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public List<Quotation> getAllQuotations() {
        List<Quotation> quotations = new ArrayList<>();
        String sql = "SELECT * FROM quotations ORDER BY quotation_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Quotation quotation = new Quotation();
                quotation.setId(rs.getInt("id"));
                quotation.setCustomerId(rs.getInt("customer_id"));
                quotation.setQuotationDate(rs.getDate("quotation_date").toLocalDate());
                quotation.setTotalAmount(rs.getDouble("total_amount"));
                quotation.setStatus(Quotation.QuotationStatus.valueOf(rs.getString("status")));
                quotations.add(quotation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quotations;
    }
    
    public List<QuotationItem> getQuotationItems(int quotationId) {
        List<QuotationItem> quotationItems = new ArrayList<>();
        String sql = "SELECT * FROM quotation_items WHERE quotation_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, quotationId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                QuotationItem quotationItem = new QuotationItem();
                quotationItem.setId(rs.getInt("id"));
                quotationItem.setQuotationId(rs.getInt("quotation_id"));
                quotationItem.setProductId(rs.getInt("product_id"));
                quotationItem.setQuantity(rs.getInt("quantity"));
                quotationItem.setUnitPrice(rs.getDouble("unit_price"));
                quotationItem.setSubtotal(rs.getDouble("subtotal"));
                quotationItems.add(quotationItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quotationItems;
    }
    
    public boolean updateQuotationStatus(int quotationId, Quotation.QuotationStatus status) {
        String sql = "UPDATE quotations SET status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.toString());
            stmt.setInt(2, quotationId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Quotation> getQuotationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Quotation> quotations = new ArrayList<>();
        String sql = "SELECT * FROM quotations WHERE quotation_date BETWEEN ? AND ? ORDER BY quotation_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Quotation quotation = new Quotation();
                quotation.setId(rs.getInt("id"));
                quotation.setCustomerId(rs.getInt("customer_id"));
                quotation.setQuotationDate(rs.getDate("quotation_date").toLocalDate());
                quotation.setTotalAmount(rs.getDouble("total_amount"));
                quotation.setStatus(Quotation.QuotationStatus.valueOf(rs.getString("status")));
                quotations.add(quotation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return quotations;
    }
    
    public double getTotalQuotationsAmount() {
        String sql = "SELECT SUM(total_amount) as total FROM quotations WHERE status != 'REJECTED'";
        
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