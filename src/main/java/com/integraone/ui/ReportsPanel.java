package com.integraone.ui;

import com.integraone.dao.*;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;

/**
 * Reports Management Panel
 */
@SuppressWarnings("serial")
public class ReportsPanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private SalesDAO salesDAO;
    private PurchaseDAO purchaseDAO;
    private OrderDAO orderDAO;
    private PayrollDAO payrollDAO;
    
    private JTextField startDateField;
    private JTextField endDateField;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JTextArea summaryArea;
    
    public ReportsPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.salesDAO = new SalesDAO();
        this.purchaseDAO = new PurchaseDAO();
        this.orderDAO = new OrderDAO();
        this.payrollDAO = new PayrollDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        generateDashboardSummary();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        // Date fields
        startDateField = new JTextField(LocalDate.now().minusMonths(1).toString());
        startDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        startDateField.setFont(UIConstants.LABEL_FONT);
        
        endDateField = new JTextField(LocalDate.now().toString());
        endDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        endDateField.setFont(UIConstants.LABEL_FONT);
        
        // Table
        String[] columnNames = {"Description", "Value"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(tableModel);
        reportTable.setFont(UIConstants.TABLE_FONT);
        reportTable.setRowHeight(25);
        reportTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        reportTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        reportTable.getTableHeader().setForeground(Color.WHITE);
        
        // Summary area
        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setEditable(false);
        summaryArea.setBackground(UIConstants.BACKGROUND_COLOR);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Reports & Analytics");
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
        
        // Control Panel
        JPanel controlPanel = createControlPanel();
        
        // Create tabbed pane for different reports
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.LABEL_FONT);
        
        // Dashboard Summary Tab
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("Dashboard Summary", summaryPanel);
        
        // Sales Report Tab
        JPanel salesReportPanel = createReportPanel("Sales Report");
        tabbedPane.addTab("Sales Report", salesReportPanel);
        
        // Purchase Report Tab
        JPanel purchaseReportPanel = createReportPanel("Purchase Report");
        tabbedPane.addTab("Purchase Report", purchaseReportPanel);
        
        // Inventory Report Tab
        JPanel inventoryReportPanel = createReportPanel("Inventory Report");
        tabbedPane.addTab("Inventory Report", inventoryReportPanel);
        
        // Customer Report Tab
        JPanel customerReportPanel = createReportPanel("Customer Report");
        tabbedPane.addTab("Customer Report", customerReportPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(controlPanel, BorderLayout.SOUTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        controlPanel.setBackground(UIConstants.CONTENT_COLOR);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Report Controls"));
        
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setFont(UIConstants.LABEL_FONT);
        
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setFont(UIConstants.LABEL_FONT);
        
        JButton generateReportButton = createButton("Generate Report", UIConstants.SUCCESS_COLOR);
        JButton exportPDFButton = createButton("Export PDF", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.WARNING_COLOR);
        
        controlPanel.add(startDateLabel);
        controlPanel.add(startDateField);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(endDateLabel);
        controlPanel.add(endDateField);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(generateReportButton);
        controlPanel.add(exportPDFButton);
        controlPanel.add(refreshButton);
        
        return controlPanel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JScrollPane summaryScrollPane = new JScrollPane(summaryArea);
        summaryScrollPane.setPreferredSize(new Dimension(0, 400));
        summaryScrollPane.setBorder(BorderFactory.createTitledBorder("Business Summary"));
        
        summaryPanel.add(summaryScrollPane, BorderLayout.CENTER);
        
        return summaryPanel;
    }
    
    private JPanel createReportPanel(String reportType) {
        JPanel reportPanel = new JPanel(new BorderLayout());
        reportPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JScrollPane tableScrollPane = new JScrollPane(reportTable);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(reportType + " Data"));
        
        reportPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        return reportPanel;
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
        // Generate report button
        findButton("Generate Report").addActionListener(e -> generateSelectedReport());
        
        // Export PDF button
        findButton("Export PDF").addActionListener(e -> exportToPDF());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> generateDashboardSummary());
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
    
    private void generateDashboardSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(60)).append("\n");
        summary.append("                INTEGRAONE ERP - BUSINESS SUMMARY\n");
        summary.append("=".repeat(60)).append("\n");
        summary.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        // Customer Statistics
        int totalCustomers = customerDAO.getAllCustomers().size();
        summary.append("CUSTOMER STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Customers: %d\n\n", totalCustomers));
        
        // Product Statistics
        int totalProducts = productDAO.getAllProducts().size();
        int lowStockProducts = productDAO.getLowStockProducts(10).size();
        summary.append("PRODUCT & INVENTORY STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Products: %d\n", totalProducts));
        summary.append(String.format("Low Stock Products: %d\n\n", lowStockProducts));
        
        // Sales Statistics
        double totalSales = salesDAO.getTotalSalesAmount();
        summary.append("SALES STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Sales Amount: %s\n\n", ValidationUtil.formatCurrency(totalSales)));
        
        // Purchase Statistics
        double totalPurchases = purchaseDAO.getTotalPurchaseAmount();
        summary.append("PURCHASE STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Purchase Amount: %s\n\n", ValidationUtil.formatCurrency(totalPurchases)));
        
        // Order Statistics
        double totalOrders = orderDAO.getTotalOrdersAmount();
        summary.append("ORDER STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Orders Amount: %s\n\n", ValidationUtil.formatCurrency(totalOrders)));
        
        // Payroll Statistics
        double totalPayroll = payrollDAO.getTotalPayrollAmount();
        summary.append("PAYROLL STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Payroll Amount: %s\n\n", ValidationUtil.formatCurrency(totalPayroll)));
        
        // Financial Summary
        double grossProfit = totalSales - totalPurchases;
        double netProfit = grossProfit - totalPayroll;
        summary.append("FINANCIAL SUMMARY:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Gross Profit: %s\n", ValidationUtil.formatCurrency(grossProfit)));
        summary.append(String.format("Net Profit: %s\n", ValidationUtil.formatCurrency(netProfit)));
        
        summary.append("\n").append("=".repeat(60)).append("\n");
        summary.append("Report generated by IntegraOne ERP System\n");
        summary.append("=".repeat(60));
        
        summaryArea.setText(summary.toString());
        summaryArea.setCaretPosition(0); // Scroll to top
    }
    
    private void generateSelectedReport() {
        // Get the selected tab
        JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        int selectedTab = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedTab);
        
        if ("Dashboard Summary".equals(tabTitle)) {
            generateDashboardSummary();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        try {
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim());
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim());
            
            switch (tabTitle) {
                case "Sales Report":
                    generateSalesReport(startDate, endDate);
                    break;
                case "Purchase Report":
                    generatePurchaseReport(startDate, endDate);
                    break;
                case "Inventory Report":
                    generateInventoryReport();
                    break;
                case "Customer Report":
                    generateCustomerReport();
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates (YYYY-MM-DD format).", 
                                        "Date Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void generateSalesReport(LocalDate startDate, LocalDate endDate) {
        // Update table columns for sales report
        tableModel.setColumnIdentifiers(new String[]{"Product", "Quantity Sold", "Unit Price", "Total Amount", "Sale Date"});
        
        // In a complete implementation, you would fetch sales data by date range
        // For now, showing sample data structure
        Object[] sampleRow = {"Sample Product", 10, "$99.99", "$999.90", LocalDate.now().toString()};
        tableModel.addRow(sampleRow);
        
        JOptionPane.showMessageDialog(this, 
            "Sales report generated for period: " + startDate + " to " + endDate, 
            "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generatePurchaseReport(LocalDate startDate, LocalDate endDate) {
        // Update table columns for purchase report
        tableModel.setColumnIdentifiers(new String[]{"Product", "Quantity Purchased", "Unit Price", "Total Amount", "Purchase Date"});
        
        // In a complete implementation, you would fetch purchase data by date range
        Object[] sampleRow = {"Sample Product", 20, "$89.99", "$1799.80", LocalDate.now().toString()};
        tableModel.addRow(sampleRow);
        
        JOptionPane.showMessageDialog(this, 
            "Purchase report generated for period: " + startDate + " to " + endDate, 
            "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateInventoryReport() {
        // Update table columns for inventory report
        tableModel.setColumnIdentifiers(new String[]{"Product", "Current Stock", "Unit Price", "Stock Value", "Status"});
        
        productDAO.getAllProducts().forEach(product -> {
            String status = product.getStock() <= 10 ? "LOW STOCK" : "IN STOCK";
            double stockValue = product.getPrice() * product.getStock();
            
            Object[] row = {
                product.getName(),
                product.getStock(),
                ValidationUtil.formatCurrency(product.getPrice()),
                ValidationUtil.formatCurrency(stockValue),
                status
            };
            tableModel.addRow(row);
        });
        
        JOptionPane.showMessageDialog(this, "Inventory report generated successfully.", 
                                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateCustomerReport() {
        // Update table columns for customer report
        tableModel.setColumnIdentifiers(new String[]{"Customer Name", "Email", "Phone", "Address", "Registration Date"});
        
        customerDAO.getAllCustomers().forEach(customer -> {
            Object[] row = {
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                "N/A" // In a complete implementation, you would have registration date
            };
            tableModel.addRow(row);
        });
        
        JOptionPane.showMessageDialog(this, "Customer report generated successfully.", 
                                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportToPDF() {
        // In a complete implementation, you would use iText library to generate PDF
        JOptionPane.showMessageDialog(this, 
            "PDF export functionality would be implemented here using iText library.\n" +
            "The PDF would include:\n" +
            "- Company header and logo\n" +
            "- Report title and date range\n" +
            "- Formatted table data\n" +
            "- Summary statistics\n" +
            "- Professional formatting", 
            "Export to PDF", JOptionPane.INFORMATION_MESSAGE);
    }
}