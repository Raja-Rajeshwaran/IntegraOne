package com.integraone.ui;
import com.integraone.dao.*;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.integraone.model.Product;
import com.integraone.model.Sale;
import com.integraone.model.Purchase;
import com.integraone.model.Customer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.io.File;
import java.io.FileOutputStream;


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
    private JTextArea summaryArea;
    
    // Maps to store separate tables and models for each report
    private Map<String, JTable> reportTables = new HashMap<>();
    private Map<String, DefaultTableModel> reportModels = new HashMap<>();
    
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
        
        startDateField = new JTextField(LocalDate.now().minusMonths(1).toString());
        startDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        startDateField.setFont(UIConstants.LABEL_FONT);
        
        endDateField = new JTextField(LocalDate.now().toString());
        endDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        endDateField.setFont(UIConstants.LABEL_FONT);
        
        summaryArea = new JTextArea();
        summaryArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        summaryArea.setEditable(false);
        summaryArea.setBackground(UIConstants.BACKGROUND_COLOR);
        summaryArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
    
    private void setupLayout() {
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
        
        JPanel controlPanel = createControlPanel();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.LABEL_FONT);
        
        JPanel summaryPanel = createSummaryPanel();
        tabbedPane.addTab("Dashboard Summary", summaryPanel);
        
        JPanel salesReportPanel = createReportPanel("Sales Report");
        tabbedPane.addTab("Sales Report", salesReportPanel);
        
        JPanel purchaseReportPanel = createReportPanel("Purchase Report");
        tabbedPane.addTab("Purchase Report", purchaseReportPanel);
        
        JPanel inventoryReportPanel = createReportPanel("Inventory Report");
        tabbedPane.addTab("Inventory Report", inventoryReportPanel);
        
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
        
        // Create a new table model for this report
        DefaultTableModel model = new DefaultTableModel(new String[]{"Description", "Value"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(UIConstants.TABLE_FONT);
        table.setRowHeight(25);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setFont(UIConstants.LABEL_FONT);
        table.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        table.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(reportType + " Data"));
        
        reportPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Store references to the table and model
        reportTables.put(reportType, table);
        reportModels.put(reportType, model);
        
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
        findButton("Generate Report").addActionListener(e -> generateSelectedReport());
        
        findButton("Export PDF").addActionListener(e -> exportToPDF());
        
        // Updated refresh button to refresh current report
        findButton("Refresh").addActionListener(e -> {
            JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
            int selectedTab = tabbedPane.getSelectedIndex();
            String tabTitle = tabbedPane.getTitleAt(selectedTab);
            
            if ("Dashboard Summary".equals(tabTitle)) {
                generateDashboardSummary();
            } else {
                // Refresh the current report with existing date range
                generateSelectedReport();
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
    
    private void generateDashboardSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("=".repeat(60)).append("\n");
        summary.append("                INTEGRAONE ERP - BUSINESS SUMMARY\n");
        summary.append("=".repeat(60)).append("\n");
        summary.append("Generated on: ").append(LocalDate.now()).append("\n\n");
        
        int totalCustomers = customerDAO.getAllCustomers().size();
        summary.append("CUSTOMER STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Customers: %d\n\n", totalCustomers));
        
        int totalProducts = productDAO.getAllProducts().size();
        int lowStockProducts = productDAO.getLowStockProducts(10).size();
        summary.append("PRODUCT & INVENTORY STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Products: %d\n", totalProducts));
        summary.append(String.format("Low Stock Products: %d\n\n", lowStockProducts));
        
        double totalSales = salesDAO.getTotalSalesAmount();
        summary.append("SALES STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Sales Amount: %s\n\n", ValidationUtil.formatCurrency(totalSales)));
        
        double totalPurchases = purchaseDAO.getTotalPurchaseAmount();
        summary.append("PURCHASE STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Purchase Amount: %s\n\n", ValidationUtil.formatCurrency(totalPurchases)));
        
        double totalOrders = orderDAO.getTotalOrdersAmount();
        summary.append("ORDER STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Orders Amount: %s\n\n", ValidationUtil.formatCurrency(totalOrders)));
        
        double totalPayroll = payrollDAO.getTotalPayrollAmount();
        summary.append("PAYROLL STATISTICS:\n");
        summary.append("-".repeat(30)).append("\n");
        summary.append(String.format("Total Payroll Amount: %s\n\n", ValidationUtil.formatCurrency(totalPayroll)));
        
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
        summaryArea.setCaretPosition(0); 
    }
    
    private void generateSelectedReport() {
        JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        int selectedTab = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedTab);
        
        if ("Dashboard Summary".equals(tabTitle)) {
            generateDashboardSummary();
            return;
        }
        
        // Get the correct table model for this report
        DefaultTableModel model = reportModels.get(tabTitle);
        if (model == null) {
            JOptionPane.showMessageDialog(this, "Report model not found: " + tabTitle, 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        model.setRowCount(0);
        
        try {
            // Validate date format first
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(startDateField.getText().trim(), formatter);
            LocalDate endDate = LocalDate.parse(endDateField.getText().trim(), formatter);
            
            // Validate that end date is not before start date
            if (endDate.isBefore(startDate)) {
                JOptionPane.showMessageDialog(this, "End date cannot be before start date.", 
                                            "Date Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            switch (tabTitle) {
                case "Sales Report":
                    generateSalesReport(startDate, endDate, model);
                    break;
                case "Purchase Report":
                    generatePurchaseReport(startDate, endDate, model);
                    break;
                case "Inventory Report":
                    generateInventoryReport(model);
                    break;
                case "Customer Report":
                    generateCustomerReport(model);
                    break;
            }
            
            // Force UI refresh
            model.fireTableDataChanged();
            JTable table = reportTables.get(tabTitle);
            if (table != null) {
                table.revalidate();
                table.repaint();
            }
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid dates in YYYY-MM-DD format.", 
                                        "Date Format Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), 
                                        "Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }    
    private void generateSalesReport(LocalDate startDate, LocalDate endDate, DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"Product", "Quantity Sold", "Unit Price", "Total Amount", "Sale Date"});
        
        try {
            List<Sale> sales = salesDAO.getSalesByDateRange(startDate, endDate);
            System.out.println("Found " + sales.size() + " sales records");
            
            if (sales.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No sales data found for the selected period.", 
                                            "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Sale sale : sales) {
                Product product = productDAO.getProductById(sale.getProductId());
                String productName = (product != null) ? product.getName() : "Unknown Product";
                
                Object[] row = {
                    productName,
                    sale.getQuantity(),
                    ValidationUtil.formatCurrency(sale.getPrice()),
                    ValidationUtil.formatCurrency(sale.getTotalAmount()),
                    sale.getSaleDate().toString()
                };
                model.addRow(row);
                System.out.println("Added row: " + productName);
            }
            
            System.out.println("Total rows in model: " + model.getRowCount());
            JOptionPane.showMessageDialog(this, 
                "Sales report generated for period: " + startDate + " to " + endDate, 
                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating sales report: " + e.getMessage(), 
                                        "Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void generatePurchaseReport(LocalDate startDate, LocalDate endDate, DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"Product", "Quantity Purchased", "Unit Price", "Total Amount", "Purchase Date"});
        
        try {
            List<Purchase> purchases = purchaseDAO.getPurchasesByDateRange(startDate, endDate);
            System.out.println("Found " + purchases.size() + " purchase records");
            
            if (purchases.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No purchase data found for the selected period.", 
                                            "No Data", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            for (Purchase purchase : purchases) {
                Product product = productDAO.getProductById(purchase.getProductId());
                String productName = (product != null) ? product.getName() : "Unknown Product";
                
                Object[] row = {
                    productName,
                    purchase.getQuantity(),
                    ValidationUtil.formatCurrency(purchase.getPrice()),
                    ValidationUtil.formatCurrency(purchase.getTotalAmount()),
                    purchase.getPurchaseDate().toString()
                };
                model.addRow(row);
                System.out.println("Added row: " + productName);
            }
            
            System.out.println("Total rows in model: " + model.getRowCount());
            JOptionPane.showMessageDialog(this, 
                "Purchase report generated for period: " + startDate + " to " + endDate, 
                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating purchase report: " + e.getMessage(), 
                                        "Report Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void generateInventoryReport(DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"Product", "Current Stock", "Unit Price", "Stock Value", "Status"});
        
        List<Product> products = productDAO.getAllProducts();
        System.out.println("Found " + products.size() + " product records");
        
        for (Product product : products) {
            String status = product.getStock() <= 10 ? "LOW STOCK" : "IN STOCK";
            double stockValue = product.getPrice() * product.getStock();
            
            Object[] row = {
                product.getName(),
                product.getStock(),
                ValidationUtil.formatCurrency(product.getPrice()),
                ValidationUtil.formatCurrency(stockValue),
                status
            };
            model.addRow(row);
            System.out.println("Added row: " + product.getName());
        }
        
        System.out.println("Total rows in model: " + model.getRowCount());
        JOptionPane.showMessageDialog(this, "Inventory report generated successfully.", 
                                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void generateCustomerReport(DefaultTableModel model) {
        model.setColumnIdentifiers(new String[]{"Customer Name", "Email", "Phone", "Address", "Registration Date"});
        
        List<Customer> customers = customerDAO.getAllCustomers();
        System.out.println("Found " + customers.size() + " customer records");
        
        for (Customer customer : customers) {
            Object[] row = {
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                "N/A" 
            };
            model.addRow(row);
            System.out.println("Added row: " + customer.getName());
        }
        
        System.out.println("Total rows in model: " + model.getRowCount());
        JOptionPane.showMessageDialog(this, "Customer report generated successfully.", 
                                    "Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportToPDF() {
        JTabbedPane tabbedPane = (JTabbedPane) ((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER);
        int selectedTab = tabbedPane.getSelectedIndex();
        String tabTitle = tabbedPane.getTitleAt(selectedTab);
        
        // Show file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setSelectedFile(new File(tabTitle.replace(" ", "_") + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }
        
        File fileToSave = fileChooser.getSelectedFile();
        
        try {
            // Create document with margins
            com.itextpdf.text.Document document = new com.itextpdf.text.Document(com.itextpdf.text.PageSize.A4, 36, 36, 36, 36);
            @SuppressWarnings("unused")
			com.itextpdf.text.pdf.PdfWriter writer = com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
            document.open();
            
            // Add report header
            addReportHeader(document, tabTitle);
            
            // Add date information
            addDateInformation(document, tabTitle);
            
            // Add report content based on type
            if ("Dashboard Summary".equals(tabTitle)) {
                addSummaryContent(document);
            } else {
                addTableContent(document, tabTitle);
            }
            
            // Add report footer
            addReportFooter(document);
            
            document.close();
            
            JOptionPane.showMessageDialog(this, 
                "Report exported successfully to:\n" + fileToSave.getAbsolutePath(), 
                "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Error generating PDF: " + e.getMessage(), 
                "Export Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Helper method to add a horizontal line
    private void addHorizontalLine(com.itextpdf.text.Document document, com.itextpdf.text.BaseColor color) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.pdf.PdfPTable table = new com.itextpdf.text.pdf.PdfPTable(1);
        table.setWidthPercentage(100);
        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
        cell.setBorder(com.itextpdf.text.Rectangle.BOTTOM);
        cell.setBorderColor(color);
        cell.setBorderWidth(0.5f);
        cell.setPadding(0);
        cell.setLeading(0, 0);
        table.addCell(cell);
        document.add(table);
    }
    
    private void addReportHeader(com.itextpdf.text.Document document, String reportTitle) throws com.itextpdf.text.DocumentException {
        // Company name
        com.itextpdf.text.Font companyFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 14, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Paragraph company = new com.itextpdf.text.Paragraph("INTEGRAONE ERP", companyFont);
        company.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(company);
        
        // Report title
        com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 18, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph(reportTitle.toUpperCase() + " REPORT", titleFont);
        title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        title.setSpacingBefore(10);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Add separator line
        addHorizontalLine(document, com.itextpdf.text.BaseColor.LIGHT_GRAY);
        document.add(com.itextpdf.text.Chunk.NEWLINE);
    }
    
    private void addDateInformation(com.itextpdf.text.Document document, String reportTitle) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font dateFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.NORMAL);
        
        if ("Dashboard Summary".equals(reportTitle)) {
            com.itextpdf.text.Paragraph dateInfo = new com.itextpdf.text.Paragraph("Generated on: " + LocalDate.now(), dateFont);
            dateInfo.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(dateInfo);
        } else {
            com.itextpdf.text.Paragraph dateInfo = new com.itextpdf.text.Paragraph(
                "Report Period: " + startDateField.getText() + " to " + endDateField.getText(), 
                dateFont);
            dateInfo.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            document.add(dateInfo);
        }
        
        document.add(com.itextpdf.text.Chunk.NEWLINE);
    }
    
    private void addSummaryContent(com.itextpdf.text.Document document) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font summaryFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.COURIER, 10, com.itextpdf.text.Font.NORMAL);
        String[] lines = summaryArea.getText().split("\n");
        
        for (String line : lines) {
            com.itextpdf.text.Paragraph p = new com.itextpdf.text.Paragraph(line, summaryFont);
            p.setSpacingBefore(2);
            p.setSpacingAfter(2);
            document.add(p);
        }
    }
    
    private void addTableContent(com.itextpdf.text.Document document, String reportTitle) throws com.itextpdf.text.DocumentException {
        DefaultTableModel model = reportModels.get(reportTitle);
        if (model == null || model.getRowCount() == 0) {
            com.itextpdf.text.Font noDataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.ITALIC);
            com.itextpdf.text.Paragraph noData = new com.itextpdf.text.Paragraph("No data available for this report.", noDataFont);
            noData.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(noData);
            return;
        }
        
        // Create PDF table
        com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(model.getColumnCount());
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10);
        pdfTable.setSpacingAfter(10);
        
        // Add table headers
        com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.BaseColor headerColor = new com.itextpdf.text.BaseColor(70, 130, 180); // Steel blue
        
        for (int i = 0; i < model.getColumnCount(); i++) {
            com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(model.getColumnName(i), headerFont));
            cell.setBackgroundColor(headerColor);
            cell.setBorderColor(com.itextpdf.text.BaseColor.WHITE);
            cell.setPadding(8);
            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
            pdfTable.addCell(cell);
        }
        
        // Add table rows
        com.itextpdf.text.Font cellFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        com.itextpdf.text.BaseColor evenRowColor = new com.itextpdf.text.BaseColor(240, 248, 255); // Alice blue
        
        for (int i = 0; i < model.getRowCount(); i++) {
            for (int j = 0; j < model.getColumnCount(); j++) {
                Object value = model.getValueAt(i, j);
                String cellText = (value != null) ? value.toString() : "";
                
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(new com.itextpdf.text.Phrase(cellText, cellFont));
                cell.setBorderColor(com.itextpdf.text.BaseColor.LIGHT_GRAY);
                cell.setPadding(6);
                
                // Alternate row colors
                if (i % 2 == 0) {
                    cell.setBackgroundColor(evenRowColor);
                }
                
                // Align numbers to the right
                if (isNumericColumn(j, reportTitle)) {
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                } else {
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                }
                
                pdfTable.addCell(cell);
            }
        }
        
        document.add(pdfTable);
        
        // Add summary statistics
        addTableSummary(document, model, reportTitle);
    }
    
    private boolean isNumericColumn(int columnIndex, String reportTitle) {
        // Define which columns should be right-aligned (numeric)
        switch (reportTitle) {
            case "Sales Report":
                return columnIndex == 1 || columnIndex == 2 || columnIndex == 3; // Quantity, Unit Price, Total Amount
            case "Purchase Report":
                return columnIndex == 1 || columnIndex == 2 || columnIndex == 3; // Quantity, Unit Price, Total Amount
            case "Inventory Report":
                return columnIndex == 1 || columnIndex == 2 || columnIndex == 3; // Stock, Unit Price, Stock Value
            default:
                return false;
        }
    }
    
    private void addTableSummary(com.itextpdf.text.Document document, DefaultTableModel model, String reportTitle) throws com.itextpdf.text.DocumentException {
        if (model.getRowCount() == 0) return;
        
        // Calculate summary statistics
        int rowCount = model.getRowCount();
        
        // Add separator
        document.add(com.itextpdf.text.Chunk.NEWLINE);
        addHorizontalLine(document, com.itextpdf.text.BaseColor.LIGHT_GRAY);
        document.add(com.itextpdf.text.Chunk.NEWLINE);
        
        // Summary text
        com.itextpdf.text.Font summaryFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
        com.itextpdf.text.Paragraph summary = new com.itextpdf.text.Paragraph("Report Summary:", summaryFont);
        summary.setSpacingBefore(10);
        document.add(summary);
        
        com.itextpdf.text.Font detailFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        
        // Add total records
        com.itextpdf.text.Paragraph totalRecords = new com.itextpdf.text.Paragraph("Total Records: " + rowCount, detailFont);
        document.add(totalRecords);
        
        // Add specific summary based on report type
        switch (reportTitle) {
            case "Sales Report":
                addSalesSummary(document, model);
                break;
            case "Purchase Report":
                addPurchaseSummary(document, model);
                break;
            case "Inventory Report":
                addInventorySummary(document, model);
                break;
            case "Customer Report":
                addCustomerSummary(document, model);
                break;
        }
    }
    
    private void addSalesSummary(com.itextpdf.text.Document document, DefaultTableModel model) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font detailFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        
        // Calculate total sales
        double totalSales = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String amountStr = model.getValueAt(i, 3).toString().replace("$", "").replace(",", "");
            try {
                totalSales += Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        
        com.itextpdf.text.Paragraph totalSalesPara = new com.itextpdf.text.Paragraph("Total Sales Amount: $" + String.format("%.2f", totalSales), detailFont);
        document.add(totalSalesPara);
    }
    
    private void addPurchaseSummary(com.itextpdf.text.Document document, DefaultTableModel model) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font detailFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        
        // Calculate total purchases
        double totalPurchases = 0;
        for (int i = 0; i < model.getRowCount(); i++) {
            String amountStr = model.getValueAt(i, 3).toString().replace("$", "").replace(",", "");
            try {
                totalPurchases += Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        
        com.itextpdf.text.Paragraph totalPurchasesPara = new com.itextpdf.text.Paragraph("Total Purchase Amount: $" + String.format("%.2f", totalPurchases), detailFont);
        document.add(totalPurchasesPara);
    }
    
    private void addInventorySummary(com.itextpdf.text.Document document, DefaultTableModel model) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font detailFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        
        // Count low stock items
        int lowStockCount = 0;
        double totalInventoryValue = 0;
        
        for (int i = 0; i < model.getRowCount(); i++) {
            String status = model.getValueAt(i, 4).toString();
            if ("LOW STOCK".equals(status)) {
                lowStockCount++;
            }
            
            String valueStr = model.getValueAt(i, 3).toString().replace("$", "").replace(",", "");
            try {
                totalInventoryValue += Double.parseDouble(valueStr);
            } catch (NumberFormatException e) {
                // Skip invalid values
            }
        }
        
        com.itextpdf.text.Paragraph lowStockPara = new com.itextpdf.text.Paragraph("Low Stock Items: " + lowStockCount, detailFont);
        document.add(lowStockPara);
        
        com.itextpdf.text.Paragraph totalValuePara = new com.itextpdf.text.Paragraph("Total Inventory Value: $" + String.format("%.2f", totalInventoryValue), detailFont);
        document.add(totalValuePara);
    }
    
    private void addCustomerSummary(com.itextpdf.text.Document document, DefaultTableModel model) throws com.itextpdf.text.DocumentException {
        com.itextpdf.text.Font detailFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.NORMAL);
        
        // Just show total customers for customer report
        com.itextpdf.text.Paragraph totalCustomersPara = new com.itextpdf.text.Paragraph("Total Customers: " + model.getRowCount(), detailFont);
        document.add(totalCustomersPara);
    }
    
    private void addReportFooter(com.itextpdf.text.Document document) throws com.itextpdf.text.DocumentException {
        document.add(com.itextpdf.text.Chunk.NEWLINE);
        document.add(com.itextpdf.text.Chunk.NEWLINE);
        
        // Add separator
        addHorizontalLine(document, com.itextpdf.text.BaseColor.LIGHT_GRAY);
        document.add(com.itextpdf.text.Chunk.NEWLINE);
        
        // Footer text
        com.itextpdf.text.Font footerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.ITALIC);
        com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph("Generated by IntegraOne ERP System", footerFont);
        footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(footer);
        
        // Add generation timestamp
        com.itextpdf.text.Font timestampFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8, com.itextpdf.text.Font.ITALIC);
        com.itextpdf.text.Paragraph timestamp = new com.itextpdf.text.Paragraph("Generated on: " + LocalDate.now() + " at " + 
            new java.util.Date().toString(), timestampFont);
        timestamp.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
        document.add(timestamp);
    }
}