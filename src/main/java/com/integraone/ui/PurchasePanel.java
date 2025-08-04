package com.integraone.ui;

import com.integraone.dao.ProductDAO;
import com.integraone.dao.PurchaseDAO;
import com.integraone.model.Product;
import com.integraone.model.Purchase;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Purchase Module Panel
 */
@SuppressWarnings("serial")
public class PurchasePanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private ProductDAO productDAO;
    
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JTextField priceField;
    private JTextField purchaseDateField;
    private JTable purchaseTable;
    private DefaultTableModel tableModel;
    
    public PurchasePanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProducts();
        loadPurchases();
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
        
        purchaseDateField = new JTextField(LocalDate.now().toString());
        purchaseDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        purchaseDateField.setFont(UIConstants.LABEL_FONT);
        
        // Table
        String[] columnNames = {"S.No", "Product", "Quantity", "Price", "Total", "Purchase Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        purchaseTable = new JTable(tableModel);
        purchaseTable.setFont(UIConstants.TABLE_FONT);
        purchaseTable.setRowHeight(25);
        purchaseTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        purchaseTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        purchaseTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        purchaseTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Purchase Management");
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Purchase Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(productComboBox, gbc);
        
        // Quantity
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(quantityField, gbc);
        
        // Price
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Unit Price ($):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(priceField, gbc);
        
        // Purchase Date
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Purchase Date:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(purchaseDateField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton addPurchaseButton = createButton("Add Purchase", UIConstants.SUCCESS_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        
        buttonPanel.add(addPurchaseButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);
        
        return formPanel;
    }
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Purchase History"));
        
        JScrollPane scrollPane = new JScrollPane(purchaseTable);
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
        // Add purchase button
        findButton("Add Purchase").addActionListener(e -> addPurchase());
        
        // Clear button
        findButton("Clear").addActionListener(e -> clearForm());
        
        // Refresh button
        findButton("Refresh").addActionListener(e -> {
            loadProducts();
            loadPurchases();
        });
        
        // Product selection - auto-fill price
        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                priceField.setText(String.valueOf(selectedProduct.getPrice()));
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
    
    private void loadPurchases() {
        tableModel.setRowCount(0);
        PurchaseDAO purchaseDAO = new PurchaseDAO();
        List<Purchase> purchases = purchaseDAO.getAllPurchases();

        int serial = 1;
        for (Purchase p : purchases) {
            Product product = productDAO.getProductById(p.getProductId());
            double total = p.getPrice() * p.getQuantity();
            tableModel.addRow(new Object[]{
                    serial++,
                    product != null ? product.getName() : "Unknown",
                    p.getQuantity(),
                    String.format("$%.2f", p.getPrice()),
                    String.format("$%.2f", total),
                    p.getPurchaseDate().toString()
            });
        }
    }

    
    private void addPurchase() {
        if (!validateForm()) return;

        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double price = Double.parseDouble(priceField.getText().trim());
        LocalDate purchaseDate = LocalDate.parse(purchaseDateField.getText().trim());

        // Create purchase object
        Purchase purchase = new Purchase();
        purchase.setProductId(selectedProduct.getId());
        purchase.setQuantity(quantity);
        purchase.setPrice(price);
        purchase.setPurchaseDate(purchaseDate);

        int newStock = selectedProduct.getStock() + quantity;

        PurchaseDAO purchaseDAO = new PurchaseDAO();
        boolean success = purchaseDAO.addPurchase(purchase) &&
                          productDAO.updateStock(selectedProduct.getId(), newStock);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Purchase recorded successfully!\nProduct stock updated.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            loadProducts();   // Refresh stock
            loadPurchases();  // Refresh table
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to record purchase.",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    
    private void clearForm() {
        productComboBox.setSelectedIndex(-1);
        quantityField.setText("");
        priceField.setText("");
        purchaseDateField.setText(LocalDate.now().toString());
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
            JOptionPane.showMessageDialog(this, "Please enter a valid unit price (greater than 0).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return false;
        }

        String purchaseDateText = purchaseDateField.getText().trim();
        try {
            LocalDate.parse(purchaseDateText); // Validates format like "2025-07-29"
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid purchase date (format: YYYY-MM-DD).",
                    "Validation Error", JOptionPane.WARNING_MESSAGE);
            purchaseDateField.requestFocus();
            return false;
        }

        return true;
    }

}