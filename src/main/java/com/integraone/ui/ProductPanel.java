package com.integraone.ui;

import com.integraone.dao.ProductDAO;
import com.integraone.model.Product;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

@SuppressWarnings("serial")
public class ProductPanel extends JPanel {
    private DashboardFrame parentFrame;
    private User currentUser;
    private ProductDAO productDAO;
    
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField priceField;
    private JTextField stockField;
    private JTable productTable;
    private DefaultTableModel tableModel;
    private Product selectedProduct;
    
    public ProductPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.productDAO = new ProductDAO();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadProducts();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        nameField = new JTextField();
        nameField.setPreferredSize(UIConstants.FIELD_SIZE);
        nameField.setFont(UIConstants.LABEL_FONT);
        
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setFont(UIConstants.LABEL_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        priceField = new JTextField();
        priceField.setPreferredSize(UIConstants.FIELD_SIZE);
        priceField.setFont(UIConstants.LABEL_FONT);
        
        stockField = new JTextField();
        stockField.setPreferredSize(UIConstants.FIELD_SIZE);
        stockField.setFont(UIConstants.LABEL_FONT);
        
        String[] columnNames = {"S.No", "Name", "Description", "Price", "Stock"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        productTable = new JTable(tableModel);
        productTable.setFont(UIConstants.TABLE_FONT);
        productTable.setRowHeight(25);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        productTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        productTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Product Management");
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
        
        JPanel formPanel = createFormPanel();
        
        JPanel tablePanel = createTablePanel();
        
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Product Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(nameField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        formPanel.add(new JLabel("Price ($):"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        formPanel.add(priceField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Stock:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(stockField, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 3; gbc.gridy = 1;
        formPanel.add(new JScrollPane(descriptionArea), gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton addButton = createButton("Add", UIConstants.SUCCESS_COLOR);
        JButton updateButton = createButton("Update", UIConstants.WARNING_COLOR);
        JButton deleteButton = createButton("Delete", UIConstants.ERROR_COLOR);
        JButton clearButton = createButton("Clear", UIConstants.BUTTON_COLOR);
        JButton refreshButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        
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
    
    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(UIConstants.CONTENT_COLOR);
        tablePanel.setBorder(BorderFactory.createTitledBorder("Product List"));
        
        JScrollPane scrollPane = new JScrollPane(productTable);
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
        findButton("Add").addActionListener(e -> addProduct());
        
        findButton("Update").addActionListener(e -> updateProduct());
        
        findButton("Delete").addActionListener(e -> deleteProduct());
        
        findButton("Clear").addActionListener(e -> clearForm());
        
        findButton("Refresh").addActionListener(e -> loadProducts());
        
        productTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedProduct();
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
    
    private void addProduct() {
        if (!validateForm()) return;
        
        Product product = new Product();
        product.setName(nameField.getText().trim());
        product.setDescription(descriptionArea.getText().trim());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setStock(Integer.parseInt(stockField.getText().trim()));
        
        if (productDAO.addProduct(product)) {
            JOptionPane.showMessageDialog(this, "Product added successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add product.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateProduct() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (!validateForm()) return;
        
        selectedProduct.setName(nameField.getText().trim());
        selectedProduct.setDescription(descriptionArea.getText().trim());
        selectedProduct.setPrice(Double.parseDouble(priceField.getText().trim()));
        selectedProduct.setStock(Integer.parseInt(stockField.getText().trim()));
        
        if (productDAO.updateProduct(selectedProduct)) {
            JOptionPane.showMessageDialog(this, "Product updated successfully!", 
                                        "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadProducts();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update product.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteProduct() {
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete product: " + selectedProduct.getName() + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(selectedProduct.getId())) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadProducts();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete product.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        nameField.setText("");
        descriptionArea.setText("");
        priceField.setText("");
        stockField.setText("");
        selectedProduct = null;
        productTable.clearSelection();
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productDAO.getAllProducts();
        
        int serialNo = 1;
        for (Product product : products) {
            Object[] row = {
                serialNo++,
                product.getName(),
                product.getDescription(),
                ValidationUtil.formatCurrency(product.getPrice()),
                product.getStock()
            };
            tableModel.addRow(row);
            
            if (product.getStock() <= 10) {
                @SuppressWarnings("unused")
				int rowIndex = tableModel.getRowCount() - 1;
            }
        }
    }
    
    private void loadSelectedProduct() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            String name = (String) tableModel.getValueAt(selectedRow, 1);
            String description = (String) tableModel.getValueAt(selectedRow, 2);
            String priceStr = (String) tableModel.getValueAt(selectedRow, 3);
            Integer stock = (Integer) tableModel.getValueAt(selectedRow, 4);
            
            nameField.setText(name);
            descriptionArea.setText(description);
            priceStr = priceStr.replace("$", "");
            priceField.setText(priceStr);
            stockField.setText(stock.toString());
            
            List<Product> products = productDAO.getAllProducts();
            for (Product product : products) {
                if (product.getName().equals(name)) {
                    selectedProduct = product;
                    break;
                }
            }
        }
    }
    
    private boolean validateForm() {
        if (!ValidationUtil.isNotEmpty(nameField.getText())) {
            JOptionPane.showMessageDialog(this, "Product name is required.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidPrice(priceField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid price.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            priceField.requestFocus();
            return false;
        }
        
        if (!ValidationUtil.isValidInteger(stockField.getText())) {
            JOptionPane.showMessageDialog(this, "Please enter a valid stock quantity.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            stockField.requestFocus();
            return false;
        }
        
        return true;
    }
}