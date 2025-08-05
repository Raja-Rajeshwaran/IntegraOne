package com.integraone.ui;

import com.integraone.model.User;
import com.integraone.util.UIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class DashboardFrame extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public DashboardFrame(User user) {
        this.currentUser = user;
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("IntegraOne - Dashboard (" + currentUser.getUsername() + " - " + currentUser.getRole() + ")");
        setSize(UIConstants.DASHBOARD_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);
        
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(UIConstants.CONTENT_COLOR);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        JPanel navPanel = createNavigationPanel();
        add(navPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        showWelcomePanel();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.BUTTON_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("IntegraOne ERP System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(UIConstants.BUTTON_FONT);
        logoutButton.setBackground(UIConstants.ERROR_COLOR);
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(UIConstants.CONTENT_COLOR);
        navPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.MARGIN, UIConstants.PADDING, 
                                                           UIConstants.MARGIN, UIConstants.PADDING));
        navPanel.setPreferredSize(new Dimension(200, 0));
        String[] modules = {
            "Dashboard", "Customer Management", "Product Management", 
            "Inventory Management", "Purchase Module", "Sales Module",
            "Order Processing", "Quotation/Invoice", "HR/Payroll", "Reports"
        };
        
        for (String module : modules) {
            JButton button = createNavButton(module);
            navPanel.add(button);
            navPanel.add(Box.createVerticalStrut(5));
        }
        
        navPanel.add(Box.createVerticalGlue());
        
        return navPanel;
    }
    
    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setBackground(UIConstants.BUTTON_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 35));
        button.setPreferredSize(new Dimension(180, 35));
        
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToModule(text);
            }
        });
        
        return button;
    }
    
    private void navigateToModule(String moduleName) {
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals(moduleName)) {
                contentPanel.remove(comp);
                break;
            }
        }
        
        JPanel modulePanel = null;
        
        switch (moduleName) {
            case "Dashboard":
                showWelcomePanel();
                return;
            case "Customer Management":
                modulePanel = new CustomerPanel(this, currentUser);
                break;
            case "Product Management":
                modulePanel = new ProductPanel(this, currentUser);
                break;
            case "Inventory Management":
                modulePanel = new InventoryPanel(this, currentUser);
                break;
            case "Purchase Module":
                modulePanel = new PurchasePanel(this, currentUser);
                break;
            case "Sales Module":
                modulePanel = new SalesPanel(this, currentUser);
                break;
            case "Order Processing":
                modulePanel = new OrderPanel(this, currentUser);
                break;
            case "Quotation/Invoice":
                modulePanel = new QuotationPanel(this, currentUser);
                break;
            case "HR/Payroll":
                modulePanel = new EmployeePanel(this, currentUser);
                break;
            case "Reports":
                modulePanel = new ReportsPanel(this, currentUser);
                break;
        }
        
        if (modulePanel != null) {
            modulePanel.setName(moduleName);
            contentPanel.add(modulePanel, moduleName);
            cardLayout.show(contentPanel, moduleName);
        }
    }
    
    private void showWelcomePanel() {
        Component[] components = contentPanel.getComponents();
        for (Component comp : components) {
            if (comp.getName() != null && comp.getName().equals("Welcome")) {
                contentPanel.remove(comp);
                break;
            }
        }
        
        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(UIConstants.CONTENT_COLOR);
        welcomePanel.setName("Welcome");
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.MARGIN, UIConstants.MARGIN, 
                               UIConstants.MARGIN, UIConstants.MARGIN);
        
        JLabel welcomeLabel = new JLabel("Welcome to IntegraOne");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0;
        welcomePanel.add(welcomeLabel, gbc);
        
        JLabel descLabel = new JLabel("<html><center>Your complete business management solution<br>" +
                                     "Select a module from the navigation panel to get started</center></html>");
        descLabel.setFont(UIConstants.LABEL_FONT);
        descLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 1;
        welcomePanel.add(descLabel, gbc);
        
        contentPanel.add(welcomePanel, "Welcome");
        cardLayout.show(contentPanel, "Welcome");
    }
    
    private void setupEventHandlers() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int option = JOptionPane.showConfirmDialog(
                    DashboardFrame.this,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
                );
                if (option == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        }
    }
    
    public void showDashboard() {
        showWelcomePanel();
    }
}