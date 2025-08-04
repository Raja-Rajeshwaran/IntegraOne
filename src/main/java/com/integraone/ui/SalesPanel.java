package com.integraone.ui;

import com.integraone.dao.ProductDAO;
import com.integraone.dao.SalesDAO;
import com.integraone.model.Product;
import com.integraone.model.Sale;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Sales Module Panel
 */
@SuppressWarnings("serial")
public class SalesPanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private ProductDAO productDAO;
    private SalesDAO salesDAO;
    
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField saleDateField;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel availableStockLabel;
    
    public SalesPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        this.salesDAO = new SalesDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProducts();
        loadSales();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        // Form fields
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        productComboBox.setFont(UIConstants.LABEL_FONT);
        
        quantityField = new JTextField();
        quantityField.setPreferredSize(UIConstants.FIELD_SIZE);
        quantityField.setFont(UIConstants.LABEL_FONT);
        
        priceField = new JTextField();
        priceField.setPreferredSize(UIConstants.FIELD_SIZE);
        priceField.setFont(UIConstants.LABEL_FONT);
        
        saleDateField = new JTextField(LocalDate.now().toString());
        saleDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        saleDateField.setFont(UIConstants.LABEL_FONT);
        
        availableStockLabel = new JLabel("Available Stock: 0");
        availableStockLabel.setFont(UIConstants.LABEL_FONT);
        availableStockLabel.setForeground(UIConstants.SUCCESS_COLOR);
        
        // Table
        String[] columnNames = {"S.No", "Product", "Quantity", "Unit Price", "Total", "Sale Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setFont(UIConstants.TABLE_FONT);
        salesTable.setRowHeight(25);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        salesTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        salesTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Sales Management");
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Sale Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(productComboBox, gbc);
        
        // Available Stock
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(availableStockLabel, gbc);
        
        // Quantity
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(quantityField, gbc);
        
        // Price
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Unit Price ($):"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(priceField, gbc);
        
        // Sale Date
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Sale Date:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        formPanel.add(saleDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton recordSaleButton = createButton("Record Sale", UIConstants.SUCCESS_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        
        buttonPanel.add(recordSaleButton);
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
        tablePanel.setBorder(BorderFactory.createTitledBorder("Sales History"));
        
        JScrollPane scrollPane = new JScrollPane(salesTable);
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
        // Record sale button
        findButton("Record Sale").addActionListener(e -> recordSale());
        
        // Clear button
        findButton("Clear").addActionListener(e -> clearForm());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> {
            loadProducts();
            loadSales();
        });
        
        // Product selection - auto-fill price and show stock
        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                priceField.setText(String.valueOf(selectedProduct.getPrice()));
                availableStockLabel.setText("Available Stock: " + selectedProduct.getStock() + " units");
                
                // Change color based on stock level
                if (selectedProduct.getStock() <= 10) {
                    availableStockLabel.setForeground(UIConstants.ERROR_COLOR);
                } else {
                    availableStockLabel.setForeground(UIConstants.SUCCESS_COLOR);
                }
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
    
    private void loadProducts() {
        productComboBox.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product product : products) {
            productComboBox.addItem(product);
        }
    }
    
    private void loadSales() {
        tableModel.setRowCount(0);
        List<Sale> sales = salesDAO.getAllSales();
        
        int serialNo = 1;
        for (Sale sale : sales) {
            Product product = productDAO.getProductById(sale.getProductId());
            double total = sale.getQuantity() * sale.getPrice();
            
            Object[] row = {
                serialNo++,
                product != null ? product.getName() : "Unknown Product",
                sale.getQuantity(),
                ValidationUtil.formatCurrency(sale.getPrice()),
                ValidationUtil.formatCurrency(total),
                sale.getSaleDate().toString()
            };
            tableModel.addRow(row);
        }
    }
    
    private void recordSale() {
        if (!validateForm()) return;
        
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double price = Double.parseDouble(priceField.getText().trim());
        LocalDate saleDate = LocalDate.parse(saleDateField.getText().trim());
        
        // Check if enough stock is available
        if (selectedProduct.getStock() < quantity) {
            JOptionPane.showMessageDialog(this, 
                "Insufficient stock! Available: " + selectedProduct.getStock() + " units, Requested: " + quantity + " units.", 
                "Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create sale record
        Sale sale = new Sale();
        sale.setProductId(selectedProduct.getId());
        sale.setQuantity(quantity);
        sale.setPrice(price);
        sale.setSaleDate(saleDate);
        
        // Record sale and update stock
        if (salesDAO.addSale(sale)) {
            // Update product stock
            int newStock = selectedProduct.getStock() - quantity;
            if (productDAO.updateStock(selectedProduct.getId(), newStock)) {
                JOptionPane.showMessageDialog(this, 
                    "Sale recorded successfully!\nProduct stock updated from " + 
                    selectedProduct.getStock() + " to " + newStock + " units.", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                clearForm();
                loadProducts(); // Refresh to show updated stock
                loadSales();
            } else {
                JOptionPane.showMessageDialog(this, "Sale recorded but failed to update stock.", 
                                            "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Failed to record sale.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        productComboBox.setSelectedIndex(-1);
        quantityField.setText("");
        priceField.setText("");
        saleDateField.setText(LocalDate.now().toString());
        availableStockLabel.setText("Available Stock: 0");
    }
    
    private boolean validateForm() {
        if (productComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (!ValidationUtil.isValidInteger(quantityField.getText()) || 
            Integer.parseInt(quantityField.getText().trim()) <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity (greater than 0).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            quantityField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidPrice(priceField.getText()) || 
            Double.parseDouble(priceField.getText().trim()) <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price (greater than 0).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        try {
            LocalDate.parse(saleDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD format).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            saleDateField.requestFocus();
            return false;
        }
        
        return true;
    }
}