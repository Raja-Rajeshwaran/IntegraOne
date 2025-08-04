package com.integraone.ui;

import com.integraone.dao.EmployeeDAO;
import com.integraone.model.Employee;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Employee/HR Management Panel
 */
@SuppressWarnings("serial")
public class EmployeePanel extends JPanel {
    private DashboardFrame parentFrame;
    private User currentUser;
    private EmployeeDAO employeeDAO;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField roleField;
    private JTextField basicSalaryField;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private Employee selectedEmployee;
    
    public EmployeePanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.employeeDAO = new EmployeeDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadEmployees();
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
        
        roleField = new JTextField();
        roleField.setPreferredSize(UIConstants.FIELD_SIZE);
        roleField.setFont(UIConstants.LABEL_FONT);
        
        basicSalaryField = new JTextField();
        basicSalaryField.setPreferredSize(UIConstants.FIELD_SIZE);
        basicSalaryField.setFont(UIConstants.LABEL_FONT);
        
        // Table
        String[] columnNames = {"S.No", "Name", "Email", "Phone", "Role", "Basic Salary"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        employeeTable = new JTable(tableModel);
        employeeTable.setFont(UIConstants.TABLE_FONT);
        employeeTable.setRowHeight(25);
        employeeTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        employeeTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        employeeTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        employeeTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("HR/Employee Management");
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
        
        // Table Panel
        JPanel tablePanel = createTablePanel();
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.CONTENT_COLOR);
        contentPanel.add(formPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UIConstants.CONTENT_COLOR);
        formPanel.setBorder(BorderFactory.createTitledBorder("Employee Information"));
        
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
        
        // Role
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(roleField, gbc);
        
        // Basic Salary
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Basic Salary ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(basicSalaryField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton addButton = createButton("Add Employee", UIConstants.SUCCESS_COLOR);
        JButton updateButton = createButton("Update", UIConstants.WARNING_COLOR);
        JButton deleteButton = createButton("Delete", UIConstants.ERROR_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        JButton payrollButton = createButton("Payroll", UIConstants.BUTTON_COLOR);
        
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
        buttonPanel.add(payrollButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Employee List"));
        
        JScrollPane scrollPane = new JScrollPane(employeeTable);
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
        findButton("Add Employee").addActionListener(e -> addEmployee());
        
        // Update button
        findButton("Update").addActionListener(e -> updateEmployee());
        
        // Delete button
        findButton("Delete").addActionListener(e -> deleteEmployee());
        
        // Clear button
        findButton("Clear").addActionListener(e -> clearForm());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> loadEmployees());
        
        // Payroll button
        findButton("Payroll").addActionListener(e -> openPayrollPanel());
        
        // Table selection
        employeeTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedEmployee();
            }
        });
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
    
    private void addEmployee() {
        if (!validateForm()) return;
        
        Employee employee = new Employee();
        employee.setName(nameField.getText().trim());
        employee.setEmail(emailField.getText().trim());
        employee.setPhone(phoneField.getText().trim());
        employee.setRole(roleField.getText().trim());
        employee.setBasicSalary(Double.parseDouble(basicSalaryField.getText().trim()));
        
        if (employeeDAO.addEmployee(employee)) {
            JOptionPane.showMessageDialog(this, "Employee added successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadEmployees();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add employee.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateEmployee() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee to update.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) return;
        
        selectedEmployee.setName(nameField.getText().trim());
        selectedEmployee.setEmail(emailField.getText().trim());
        selectedEmployee.setPhone(phoneField.getText().trim());
        selectedEmployee.setRole(roleField.getText().trim());
        selectedEmployee.setBasicSalary(Double.parseDouble(basicSalaryField.getText().trim()));
        
        if (employeeDAO.updateEmployee(selectedEmployee)) {
            JOptionPane.showMessageDialog(this, "Employee updated successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadEmployees();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update employee.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteEmployee() {
        if (selectedEmployee == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete employee: " + selectedEmployee.getName() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (employeeDAO.deleteEmployee(selectedEmployee.getId())) {
                JOptionPane.showMessageDialog(this, "Employee deleted successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadEmployees();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete employee.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        roleField.setText("");
        basicSalaryField.setText("");
        selectedEmployee = null;
        employeeTable.clearSelection();
    }
    
    private void loadEmployees() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();
        
        int serialNo = 1;
        for (Employee employee : employees) {
            Object[] row = {
                serialNo++,
                employee.getName(),
                employee.getEmail(),
                employee.getPhone(),
                employee.getRole(),
                ValidationUtil.formatCurrency(employee.getBasicSalary())
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedEmployee() {
        int selectedRow = employeeTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String email = (String) tableModel.getValueAt(selectedRow, 2);
            String phone = (String) tableModel.getValueAt(selectedRow, 3);
            String role = (String) tableModel.getValueAt(selectedRow, 4);
            String salaryStr = (String) tableModel.getValueAt(selectedRow, 5);
            
            nameField.setText(name);
            emailField.setText(email);
            phoneField.setText(phone);
            roleField.setText(role);
            // Remove currency formatting for editing
            salaryStr = salaryStr.replace("$", "");
            basicSalaryField.setText(salaryStr);
            
            // Find the employee object
            List<Employee> employees = employeeDAO.getAllEmployees();
            for (Employee employee : employees) {
                if (employee.getName().equals(name) && employee.getEmail().equals(email)) {
                    selectedEmployee = employee;
                    break;
                }
            }
        }
    }
    
    private void openPayrollPanel() {
        JFrame payrollFrame = new JFrame("Payroll Management");
        payrollFrame.setSize(800, 600);
        payrollFrame.setLocationRelativeTo(this);
        payrollFrame.add(new PayrollPanel(employeeDAO));
        payrollFrame.setVisible(true);
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
        
        if (!ValidationUtil.isNotEmpty(roleField.getText())) {
            JOptionPane.showMessageDialog(this, "Role is required.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            roleField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidPrice(basicSalaryField.getText()) || 
            Double.parseDouble(basicSalaryField.getText().trim()) <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid basic salary (greater than 0).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            basicSalaryField.requestFocus();
            return false;
        }
        
        return true;
    }
}