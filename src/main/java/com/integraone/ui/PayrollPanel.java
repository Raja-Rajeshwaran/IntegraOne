package com.integraone.ui;

import com.integraone.dao.EmployeeDAO;
import com.integraone.dao.PayrollDAO;
import com.integraone.model.Employee;
import com.integraone.model.Payroll;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Payroll Management Panel
 */
@SuppressWarnings("serial")
public class PayrollPanel extends JPanel {
    private EmployeeDAO employeeDAO;
    private PayrollDAO payrollDAO;
    
    private JComboBox<Employee> employeeComboBox;
    private JTextField basicSalaryField;
    private JTextField allowancesField;
    private JTextField deductionsField;
    private JTextField netPayField;
    private JTextField payDateField;
    private JTable payrollTable;
    private DefaultTableModel tableModel;
    
    public PayrollPanel(EmployeeDAO employeeDAO) {
        this.employeeDAO = employeeDAO;
        this.payrollDAO = new PayrollDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadEmployees();
        loadPayrollRecords();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        // Form fields
        employeeComboBox = new JComboBox<>();
        employeeComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        employeeComboBox.setFont(UIConstants.LABEL_FONT);
        
        basicSalaryField = new JTextField();
        basicSalaryField.setPreferredSize(UIConstants.FIELD_SIZE);
        basicSalaryField.setFont(UIConstants.LABEL_FONT);
        basicSalaryField.setEditable(false);
        
        allowancesField = new JTextField("0.00");
        allowancesField.setPreferredSize(UIConstants.FIELD_SIZE);
        allowancesField.setFont(UIConstants.LABEL_FONT);
        
        deductionsField = new JTextField("0.00");
        deductionsField.setPreferredSize(UIConstants.FIELD_SIZE);
        deductionsField.setFont(UIConstants.LABEL_FONT);
        
        netPayField = new JTextField();
        netPayField.setPreferredSize(UIConstants.FIELD_SIZE);
        netPayField.setFont(UIConstants.LABEL_FONT);
        netPayField.setEditable(false);
        netPayField.setBackground(UIConstants.BACKGROUND_COLOR);
        
        payDateField = new JTextField(LocalDate.now().toString());
        payDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        payDateField.setFont(UIConstants.LABEL_FONT);
        
        // Table
        String[] columnNames = {"S.No", "Employee", "Basic Salary", "Allowances", "Deductions", "Net Pay", "Pay Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        payrollTable = new JTable(tableModel);
        payrollTable.setFont(UIConstants.TABLE_FONT);
        payrollTable.setRowHeight(25);
        payrollTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        payrollTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        payrollTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        payrollTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Payroll Management");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        headerPanel.add(titleLabel);
        
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Payroll Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employee
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Employee:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(employeeComboBox, gbc);
        
        // Basic Salary
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Basic Salary:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(basicSalaryField, gbc);
        
        // Allowances
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Allowances ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(allowancesField, gbc);
        
        // Deductions
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Deductions ($):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(deductionsField, gbc);
        
        // Net Pay
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Net Pay:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(netPayField, gbc);
        
        // Pay Date
        gbc.gridx = 2; gbc.gridy = 2;
        formPanel.add(new JLabel("Pay Date:"), gbc);
        gbc.gridx = 3; gbc.gridy = 2;
        formPanel.add(payDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton calculateButton = createButton("Calculate", UIConstants.WARNING_COLOR);
        JButton processPayrollButton = createButton("Process Payroll", UIConstants.SUCCESS_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        
        buttonPanel.add(calculateButton);
        buttonPanel.add(processPayrollButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Payroll Records"));
        
        JScrollPane scrollPane = new JScrollPane(payrollTable);
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
        // Employee selection - auto-fill basic salary
        employeeComboBox.addActionListener(e -> {
            Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
            if (selectedEmployee != null) {
                basicSalaryField.setText(String.valueOf(selectedEmployee.getBasicSalary()));
                calculateNetPay();
            }
        });
        
        // Calculate button
        findButton("Calculate").addActionListener(e -> calculateNetPay());
        
        // Process payroll button
        findButton("Process Payroll").addActionListener(e -> processPayroll());
        
        // Clear button
        findButton("Clear").addActionListener(e -> clearForm());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> {
            loadEmployees();
            loadPayrollRecords();
        });
        
        // Auto-calculate when allowances or deductions change
        allowancesField.addActionListener(e -> calculateNetPay());
        deductionsField.addActionListener(e -> calculateNetPay());
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
    
    private void loadEmployees() {
        employeeComboBox.removeAllItems();
        List<Employee> employees = employeeDAO.getAllEmployees();
        for (Employee employee : employees) {
            employeeComboBox.addItem(employee);
        }
    }
    
    private void loadPayrollRecords() {
        tableModel.setRowCount(0);
        List<Payroll> payrollRecords = payrollDAO.getAllPayrollRecords();
        
        int serialNo = 1;
        for (Payroll payroll : payrollRecords) {
            Employee employee = employeeDAO.getEmployeeById(payroll.getEmployeeId());
            
            Object[] row = {
                serialNo++,
                employee != null ? employee.getName() : "Unknown Employee",
                ValidationUtil.formatCurrency(payroll.getBasicSalary()),
                ValidationUtil.formatCurrency(payroll.getAllowances()),
                ValidationUtil.formatCurrency(payroll.getDeductions()),
                ValidationUtil.formatCurrency(payroll.getNetPay()),
                payroll.getPayDate().toString()
            };
            tableModel.addRow(row);
        }
    }
    
    private void calculateNetPay() {
        try {
            double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
            double allowances = Double.parseDouble(allowancesField.getText().trim());
            double deductions = Double.parseDouble(deductionsField.getText().trim());
            
            double netPay = basicSalary + allowances - deductions;
            netPayField.setText(ValidationUtil.formatCurrency(netPay));
        } catch (NumberFormatException e) {
            netPayField.setText("$0.00");
        }
    }
    
    private void processPayroll() {
        if (!validateForm()) return;
        
        Employee selectedEmployee = (Employee) employeeComboBox.getSelectedItem();
        double basicSalary = Double.parseDouble(basicSalaryField.getText().trim());
        double allowances = Double.parseDouble(allowancesField.getText().trim());
        double deductions = Double.parseDouble(deductionsField.getText().trim());
        double netPay = basicSalary + allowances - deductions;
        LocalDate payDate = LocalDate.parse(payDateField.getText().trim());
        
        Payroll payroll = new Payroll();
        payroll.setEmployeeId(selectedEmployee.getId());
        payroll.setBasicSalary(basicSalary);
        payroll.setAllowances(allowances);
        payroll.setDeductions(deductions);
        payroll.setNetPay(netPay);
        payroll.setPayDate(payDate);
        
        if (payrollDAO.addPayrollRecord(payroll)) {
            JOptionPane.showMessageDialog(this, 
                "Payroll processed successfully for " + selectedEmployee.getName() + 
                "\nNet Pay: " + ValidationUtil.formatCurrency(netPay), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            loadPayrollRecords();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to process payroll.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        employeeComboBox.setSelectedIndex(-1);
        basicSalaryField.setText("");
        allowancesField.setText("0.00");
        deductionsField.setText("0.00");
        netPayField.setText("");
        payDateField.setText(LocalDate.now().toString());
    }
    
    private boolean validateForm() {
        if (employeeComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select an employee.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!ValidationUtil.isValidPrice(allowancesField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid allowances amount.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            allowancesField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidPrice(deductionsField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid deductions amount.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            deductionsField.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(payDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD format).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            payDateField.requestFocus();
            return false;
        }
        
        return true;
    }
}