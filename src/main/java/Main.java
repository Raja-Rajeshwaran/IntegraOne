

import com.integraone.ui.LoginFrame;
import com.integraone.util.DatabaseConnection;

import javax.swing.*;

/**
 * Main class - Entry point for IntegraOne ERP System
 */
public class Main {
    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test database connection
        if (!DatabaseConnection.testConnection()) {
            JOptionPane.showMessageDialog(null, 
                "Database connection failed! Please check your MySQL server and configuration.",
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        
        // Set application properties
        System.setProperty("java.awt.headless", "false");
        
        // Launch application on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginFrame().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Application Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}