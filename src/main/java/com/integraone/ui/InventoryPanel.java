package com.integraone.ui;

import com.integraone.dao.ProductDAO;
import com.integraone.model.Product;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class InventoryPanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private ProductDAO productDAO;
    
    private JTable inventoryTable;
    private DefaultTableModel tableModel;
    private JTextField lowStockThresholdField;
    private JLabel totalProductsLabel;
    private JLabel lowStockCountLabel;
    private JLabel totalValueLabel;
    
    public InventoryPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadInventory();
        updateSummary();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        totalProductsLabel = new JLabel("Total Products: 0");
        totalProductsLabel.setFont(UIConstants.LABEL_FONT);
        totalProductsLabel.setForeground(UIConstants.TEXT_COLOR);
        
        lowStockCountLabel = new JLabel("Low Stock Items: 0");
        lowStockCountLabel.setFont(UIConstants.LABEL_FONT);
        lowStockCountLabel.setForeground(UIConstants.ERROR_COLOR);
        
        totalValueLabel = new JLabel("Total Inventory Value: $0.00");
        totalValueLabel.setFont(UIConstants.LABEL_FONT);
        totalValueLabel.setForeground(UIConstants.SUCCESS_COLOR);
        
        lowStockThresholdField = new JTextField("10");
        lowStockThresholdField.setPreferredSize(new Dimension(80, 25));
        lowStockThresholdField.setFont(UIConstants.LABEL_FONT);
        
        String[] columnNames = {"S.No", "Product Name", "Description", "Price", "Current Stock", "Stock Value", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        inventoryTable = new JTable(tableModel);
        inventoryTable.setFont(UIConstants.TABLE_FONT);
        inventoryTable.setRowHeight(25);
        inventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        inventoryTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        inventoryTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        inventoryTable.getTableHeader().setForeground(Color.WHITE);
        
        inventoryTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) table.getValueAt(row, 6);
                if (!isSelected) {
                    if ("LOW STOCK".equals(status)) {
                        c.setBackground(new Color(255, 235, 235)); // Light red
                    } else if ("OUT OF STOCK".equals(status)) {
                        c.setBackground(new Color(255, 200, 200)); // Red
                    } else {
                        c.setBackground(Color.WHITE);
                    }
                }
                return c;
            }
        });
    }
    
    private void setupLayout() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Inventory Management");
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
        
        JPanel summaryPanel = createSummaryPanel();
        JPanel controlPanel = createControlPanel();
        JPanel tablePanel = createTablePanel();
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.CONTENT_COLOR);
        topPanel.add(summaryPanel, BorderLayout.NORTH);
        topPanel.add(controlPanel, BorderLayout.CENTER);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(UIConstants.CONTENT_COLOR);
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Inventory Summary"));
        JPanel totalProductsCard = createSummaryCard(totalProductsLabel, UIConstants.BUTTON_COLOR);
        JPanel lowStockCard = createSummaryCard(lowStockCountLabel, UIConstants.ERROR_COLOR);
        JPanel totalValueCard = createSummaryCard(totalValueLabel, UIConstants.SUCCESS_COLOR);
        
        summaryPanel.add(totalProductsCard);
        summaryPanel.add(lowStockCard);
        summaryPanel.add(totalValueCard);
        
        return summaryPanel;
    }
    
    private JPanel createSummaryCard(JLabel label, Color borderColor) {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.CENTER));
        card.setBackground(UIConstants.CONTENT_COLOR);
        card.setBorder(BorderFactory.createLineBorder(borderColor, 2));
        card.add(label);
        return card;
    }
    
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(UIConstants.CONTENT_COLOR);
        controlPanel.setBorder(BorderFactory.createTitledBorder("Controls"));
        
        JLabel thresholdLabel = new JLabel("Low Stock Threshold:");
        thresholdLabel.setFont(UIConstants.LABEL_FONT);
        
        JButton applyThresholdButton = createButton("Apply", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.SUCCESS_COLOR);
        JButton lowStockReportButton = createButton("Low Stock Report", UIConstants.WARNING_COLOR);
        
        controlPanel.add(thresholdLabel);
        controlPanel.add(lowStockThresholdField);
        controlPanel.add(applyThresholdButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(refreshButton);
        controlPanel.add(lowStockReportButton);
        
        return controlPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Inventory Details"));
        
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
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
        findButton("Apply").addActionListener(e -> {
            loadInventory();
            updateSummary();
        });
            findButton("Refresh").addActionListener(e -> {
            loadInventory();
            updateSummary();
        });
            findButton("Low Stock Report").addActionListener(e -> showLowStockReport());
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
    
    private void loadInventory() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        
        int threshold = getThreshold();
        int serialNo = 1;
        
        for (Product product : products) {
            String status = getStockStatus(product.getStock(), threshold);
            double stockValue = product.getPrice() * product.getStock();
            
            Object[] row = {
                serialNo++,
                product.getName(),
                product.getDescription(),
                ValidationUtil.formatCurrency(product.getPrice()),
                product.getStock(),
                ValidationUtil.formatCurrency(stockValue),
                status
            };
            tableModel.addRow(row);
        }
    }
    
    private void updateSummary() {
        List<Product> products = productDAO.getAllProducts();
        int threshold = getThreshold();
        
        int totalProducts = products.size();
        int lowStockCount = 0;
        double totalValue = 0.0;
        
        for (Product product : products) {
            if (product.getStock() <= threshold) {
                lowStockCount++;
            }
            totalValue += product.getPrice() * product.getStock();
        }
        
        totalProductsLabel.setText("Total Products: " + totalProducts);
        lowStockCountLabel.setText("Low Stock Items: " + lowStockCount);
        totalValueLabel.setText("Total Inventory Value: " + ValidationUtil.formatCurrency(totalValue));
    }
    
    private String getStockStatus(int stock, int threshold) {
        if (stock == 0) {
            return "OUT OF STOCK";
        } else if (stock <= threshold) {
            return "LOW STOCK";
        } else {
            return "IN STOCK";
        }
    }
    
    private int getThreshold() {
        try {
            return Integer.parseInt(lowStockThresholdField.getText().trim());
        } catch (NumberFormatException e) {
            return 10; // Default threshold
        }
    }
    
    private void showLowStockReport() {
        int threshold = getThreshold();
        List<Product> lowStockProducts = productDAO.getLowStockProducts(threshold);
        
        if (lowStockProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No products are currently below the low stock threshold.", 
                "Low Stock Report", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder report = new StringBuilder();
        report.append("LOW STOCK ALERT REPORT\n");
        report.append("Threshold: ").append(threshold).append(" units\n");
        report.append("Date: ").append(java.time.LocalDate.now()).append("\n\n");
        report.append("Products requiring attention:\n\n");
        
        for (Product product : lowStockProducts) {
            report.append("• ").append(product.getName())
                  .append(" - Current Stock: ").append(product.getStock())
                  .append(" units\n");
        }
        
        JTextArea textArea = new JTextArea(report.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setRows(15);
        textArea.setColumns(50);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        
        JOptionPane.showMessageDialog(this, scrollPane, 
            "Low Stock Report", JOptionPane.WARNING_MESSAGE);
    }
}