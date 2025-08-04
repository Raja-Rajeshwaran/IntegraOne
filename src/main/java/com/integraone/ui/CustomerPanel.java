package com.integraone.ui;

import com.integraone.dao.CustomerDAO;
import com.integraone.model.Customer;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Customer Management Panel
 */
@SuppressWarnings("serial")
public class CustomerPanel extends JPanel {
    private DashboardFrame parentFrame;
    private User currentUser;
    private CustomerDAO customerDAO;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressArea;
    private JTextField searchField;
    private JTable customerTable;
    private DefaultTableModel tableModel;
    private Customer selectedCustomer;
    
    public CustomerPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCustomers();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        // Form fields
        nameField = new JTextField();
        nameField.setPreferredSize(UIConstants.FIELD_SIZE);
        nameField.setFont(UIConstants.LABEL_FONT);
        
        emailField = new JTextField();
        emailField.setPreferredSize(UIConstants.FIELD_SIZE);
        emailField.setFont(UIConstants.LABEL_FONT);
        
        phoneField = new JTextField();
        phoneField.setPreferredSize(UIConstants.FIELD_SIZE);
        phoneField.setFont(UIConstants.LABEL_FONT);
        
        addressArea = new JTextArea(3, 20);
        addressArea.setFont(UIConstants.LABEL_FONT);
        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);
        
        searchField = new JTextField();
        searchField.setPreferredSize(UIConstants.LARGE_FIELD_SIZE);
        searchField.setFont(UIConstants.LABEL_FONT);
        
        // Table
        String[] columnNames = {"S.No", "Name", "Email", "Phone", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customerTable = new JTable(tableModel);
        customerTable.setFont(UIConstants.TABLE_FONT);
        customerTable.setRowHeight(25);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customerTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        customerTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        customerTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Customer Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.setFont(UIConstants.BUTTON_FONT);
        backButton.setBackground(UIConstants.BUTTON_COLOR);
        backButton.setForeground(Color.WHITE);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> parentFrame.showDashboard());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        
        // Form Panel
        JPanel formPanel = createFormPanel();
        
        // Search Panel
        JPanel searchPanel = createSearchPanel();
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.CONTENT_COLOR);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.CONTENT_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder("Customer Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        
        // Email
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(emailField, gbc);
        
        // Phone
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(phoneField, gbc);
        
        // Address
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(new JScrollPane(addressArea), gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton addButton = createButton("Add", UIConstants.SUCCESS_COLOR);
        JButton updateButton = createButton("Update", UIConstants.WARNING_COLOR);
        JButton deleteButton = createButton("Delete", UIConstants.ERROR_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        
        // Disable update/delete for non-admin users
        if (currentUser.getRole() != User.UserRole.ADMIN) {
            updateButton.setEnabled(false);
            deleteButton.setEnabled(false);
        }
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(UIConstants.CONTENT_COLOR);
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(UIConstants.LABEL_FONT);
        
        JButton searchButton = createButton("Search", UIConstants.BUTTON_COLOR);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Customer List"));
        
        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setPreferredSize(new Dimension(0, 300));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(UIConstants.BUTTON_FONT);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(UIConstants.BUTTON_SIZE);
        return button;
    }
    
    private void setupEventHandlers() {
        // Add button
        findButton("Add").addActionListener(e -> addCustomer());
        
        // Update button
        findButton("Update").addActionListener(e -> updateCustomer());
        
        // Delete button
        findButton("Delete").addActionListener(e -> deleteCustomer());
        
        // Clear button
        findButton("Clear").addActionListener(e -> clearForm());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> loadCustomers());
        
        // Search button
        findButton("Search").addActionListener(e -> searchCustomers());
        
        // Table selection
        customerTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedCustomer();
            }
        });
        
        // Search field enter key
        searchField.addActionListener(e -> searchCustomers());
    }
    
    private JButton findButton(String text) {
        return findButtonInContainer(this, text);
    }
    
    private JButton findButtonInContainer(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton && ((JButton) component).getText().equals(text)) {
                return (JButton) component;
            } else if (component instanceof Container) {
                JButton found = findButtonInContainer((Container) component, text);
                if (found != null) return found;
            }
        }
        return null;
    }
    
    private void addCustomer() {
        if (!validateForm()) return;
        
        Customer customer = new Customer();
        customer.setName(nameField.getText().trim());
        customer.setEmail(emailField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setAddress(addressArea.getText().trim());
        
        if (customerDAO.addCustomer(customer)) {
            JOptionPane.showMessageDialog(this, "Customer added successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add customer.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer to update.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) return;
        
        selectedCustomer.setName(nameField.getText().trim());
        selectedCustomer.setEmail(emailField.getText().trim());
        selectedCustomer.setPhone(phoneField.getText().trim());
        selectedCustomer.setAddress(addressArea.getText().trim());
        
        if (customerDAO.updateCustomer(selectedCustomer)) {
            JOptionPane.showMessageDialog(this, "Customer updated successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadCustomers();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update customer.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteCustomer() {
        if (selectedCustomer == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete customer: " + selectedCustomer.getName() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (customerDAO.deleteCustomer(selectedCustomer.getId())) {
                JOptionPane.showMessageDialog(this, "Customer deleted successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete customer.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        addressArea.setText("");
        selectedCustomer = null;
        customerTable.clearSelection();
    }
    
    private void loadCustomers() {
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.getAllCustomers();
        
        int serialNo = 1;
        for (Customer customer : customers) {
            Object[] row = {
                serialNo++,
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
            };
            tableModel.addRow(row);
        }
    }
    
    private void searchCustomers() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadCustomers();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Customer> customers = customerDAO.searchCustomers(searchTerm);
        
        int serialNo = 1;
        for (Customer customer : customers) {
            Object[] row = {
                serialNo++,
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress()
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedCustomer() {
        int selectedRow = customerTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String phone = (String) tableModel.getValueAt(selectedRow, 3);
            String address = (String) tableModel.getValueAt(selectedRow, 4);
            
            nameField.setText(name);
            emailField.setText(email);
            phoneField.setText(phone);
            addressArea.setText(address);
            
            // Find the customer object
            List<Customer> customers = customerDAO.getAllCustomers();
            for (Customer customer : customers) {
                if (customer.getName().equals(name) && customer.getEmail().equals(email)) {
                    selectedCustomer = customer;
                    break;
                }
            }
        }
    }
    
    private boolean validateForm() {
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            JOptionPane.showMessageDialog(this, "Name is required.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidEmail(emailField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidPhone(phoneField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid phone number with country code (e.g., +1-555-0123).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            phoneField.requestFocus();
            return false;
        }
        
        return true;
    }
}