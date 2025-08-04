package com.integraone.ui;

import com.integraone.dao.CustomerDAO;
import com.integraone.dao.OrderDAO;
import com.integraone.dao.ProductDAO;
import com.integraone.model.Customer;
import com.integraone.model.Order;
import com.integraone.model.OrderItem;
import com.integraone.model.Product;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Order Processing Panel
 */
@SuppressWarnings("serial")
public class OrderPanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;
    
    private JComboBox<Customer> customerComboBox;
    private JTextField orderDateField;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JTextField unitPriceField;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;
    private JTable ordersTable;
    private DefaultTableModel ordersTableModel;
    private JLabel totalAmountLabel;
    
    private List<OrderItem> currentOrderItems;
    private double currentOrderTotal;
    
    public OrderPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();
        this.currentOrderItems = new ArrayList<>();
        this.currentOrderTotal = 0.0;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCustomers();
        loadProducts();
        loadOrders();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        // Form fields
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        customerComboBox.setFont(UIConstants.LABEL_FONT);
        
        orderDateField = new JTextField(LocalDate.now().toString());
        orderDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        orderDateField.setFont(UIConstants.LABEL_FONT);
        
        productComboBox = new JComboBox<>();
        productComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        productComboBox.setFont(UIConstants.LABEL_FONT);
        
        quantityField = new JTextField();
        quantityField.setPreferredSize(UIConstants.FIELD_SIZE);
        quantityField.setFont(UIConstants.LABEL_FONT);
        
        unitPriceField = new JTextField();
        unitPriceField.setPreferredSize(UIConstants.FIELD_SIZE);
        unitPriceField.setFont(UIConstants.LABEL_FONT);
        
        totalAmountLabel = new JLabel("Total: $0.00");
        totalAmountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalAmountLabel.setForeground(UIConstants.SUCCESS_COLOR);
        
        // Order Items Table
        String[] orderItemColumns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        orderItemsTableModel = new DefaultTableModel(orderItemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        orderItemsTable = new JTable(orderItemsTableModel);
        orderItemsTable.setFont(UIConstants.TABLE_FONT);
        orderItemsTable.setRowHeight(25);
        orderItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderItemsTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        orderItemsTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        orderItemsTable.getTableHeader().setForeground(Color.WHITE);
        
        // Orders Table
        String[] orderColumns = {"S.No", "Customer", "Order Date", "Total Amount", "Status"};
        ordersTableModel = new DefaultTableModel(orderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        ordersTable = new JTable(ordersTableModel);
        ordersTable.setFont(UIConstants.TABLE_FONT);
        ordersTable.setRowHeight(25);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ordersTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        ordersTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        ordersTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Order Processing");
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
        
        // Create main content with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.LABEL_FONT);
        
        // New Order Tab
        JPanel newOrderPanel = createNewOrderPanel();
        tabbedPane.addTab("New Order", newOrderPanel);
        
        // Order History Tab
        JPanel orderHistoryPanel = createOrderHistoryPanel();
        tabbedPane.addTab("Order History", orderHistoryPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createNewOrderPanel() {
        JPanel newOrderPanel = new JPanel(new BorderLayout());
        newOrderPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        // Order Info Panel
        JPanel orderInfoPanel = createOrderInfoPanel();
        
        // Order Items Panel
        JPanel orderItemsPanel = createOrderItemsPanel();
        
        newOrderPanel.add(orderInfoPanel, BorderLayout.NORTH);
        newOrderPanel.add(orderItemsPanel, BorderLayout.CENTER);
        
        return newOrderPanel;
    }
    
    private JPanel createOrderInfoPanel() {
        JPanel orderInfoPanel = new JPanel(new GridBagLayout());
        orderInfoPanel.setBackground(UIConstants.CONTENT_COLOR);
        orderInfoPanel.setBorder(BorderFactory.createTitledBorder("Order Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Customer
        gbc.gridx = 0; gbc.gridy = 0;
        orderInfoPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        orderInfoPanel.add(customerComboBox, gbc);
        
        // Order Date
        gbc.gridx = 2; gbc.gridy = 0;
        orderInfoPanel.add(new JLabel("Order Date:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        orderInfoPanel.add(orderDateField, gbc);
        
        // Total Amount
        gbc.gridx = 4; gbc.gridy = 0;
        orderInfoPanel.add(totalAmountLabel, gbc);
        
        return orderInfoPanel;
    }
    
    private JPanel createOrderItemsPanel() {
        JPanel orderItemsPanel = new JPanel(new BorderLayout());
        orderItemsPanel.setBackground(UIConstants.CONTENT_COLOR);
        orderItemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));
        
        // Add Item Panel
        JPanel addItemPanel = createAddItemPanel();
        
        // Items Table
        JScrollPane itemsScrollPane = new JScrollPane(orderItemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(0, 200));
        
        // Order Actions Panel
        JPanel orderActionsPanel = createOrderActionsPanel();
        
        orderItemsPanel.add(addItemPanel, BorderLayout.NORTH);
        orderItemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        orderItemsPanel.add(orderActionsPanel, BorderLayout.SOUTH);
        
        return orderItemsPanel;
    }
    
    private JPanel createAddItemPanel() {
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBackground(UIConstants.CONTENT_COLOR);
        addItemPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Product
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        addItemPanel.add(productComboBox, gbc);
        
        // Quantity
        gbc.gridx = 2; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        addItemPanel.add(quantityField, gbc);
        
        // Unit Price
        gbc.gridx = 4; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 5; gbc.gridy = 0;
        addItemPanel.add(unitPriceField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton addItemButton = createButton("Add Item", UIConstants.SUCCESS_COLOR);
        JButton removeItemButton = createButton("Remove Item", UIConstants.ERROR_COLOR);
        
        buttonPanel.add(addItemButton);
        buttonPanel.add(removeItemButton);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.CENTER;
        addItemPanel.add(buttonPanel, gbc);
        
        return addItemPanel;
    }
    
    private JPanel createOrderActionsPanel() {
        JPanel orderActionsPanel = new JPanel(new FlowLayout());
        orderActionsPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton createOrderButton = createButton("Create Order", UIConstants.SUCCESS_COLOR);
        JButton clearOrderButton = createButton("Clear Order", UIConstants.WARNING_COLOR);
        
        orderActionsPanel.add(createOrderButton);
        orderActionsPanel.add(clearOrderButton);
        
        return orderActionsPanel;
    }
    
    private JPanel createOrderHistoryPanel() {
        JPanel orderHistoryPanel = new JPanel(new BorderLayout());
        orderHistoryPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JScrollPane ordersScrollPane = new JScrollPane(ordersTable);
        ordersScrollPane.setPreferredSize(new Dimension(0, 400));
        
        JPanel historyButtonPanel = new JPanel(new FlowLayout());
        historyButtonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton refreshOrdersButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        JButton viewOrderButton = createButton("View Details", UIConstants.BUTTON_COLOR);
        
        historyButtonPanel.add(refreshOrdersButton);
        historyButtonPanel.add(viewOrderButton);
        
        orderHistoryPanel.add(ordersScrollPane, BorderLayout.CENTER);
        orderHistoryPanel.add(historyButtonPanel, BorderLayout.SOUTH);
        
        return orderHistoryPanel;
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
        // Product selection - auto-fill price
        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                unitPriceField.setText(String.valueOf(selectedProduct.getPrice()));
            }
        });
        
        // Add item button
        findButton("Add Item").addActionListener(e -> addItemToOrder());
        
        // Remove item button
        findButton("Remove Item").addActionListener(e -> removeItemFromOrder());
        
        // Create order button
        findButton("Create Order").addActionListener(e -> createOrder());
        
        // Clear order button
        findButton("Clear Order").addActionListener(e -> clearCurrentOrder());
        
        // Refresh orders button
        findButton("Refresh").addActionListener(e -> loadOrders());
        
        // View order button
        findButton("View Details").addActionListener(e -> viewOrderDetails());
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
    
    private void loadCustomers() {
        customerComboBox.removeAllItems();
        List<Customer> customers = customerDAO.getAllCustomers();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer);
        }
    }
    
    private void loadProducts() {
        productComboBox.removeAllItems();
        List<Product> products = productDAO.getAllProducts();
        for (Product product : products) {
            productComboBox.addItem(product);
        }
    }
    
    private void loadOrders() {
        ordersTableModel.setRowCount(0);
        List<Order> orders = orderDAO.getAllOrders();
        
        int serialNo = 1;
        for (Order order : orders) {
            Customer customer = customerDAO.getCustomerById(order.getCustomerId());
            
            Object[] row = {
                serialNo++,
                customer != null ? customer.getName() : "Unknown Customer",
                order.getOrderDate().toString(),
                ValidationUtil.formatCurrency(order.getTotalAmount()),
                order.getStatus()
            };
            ordersTableModel.addRow(row);
        }
    }
    
    private void addItemToOrder() {
        if (!validateItemForm()) return;
        
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
        double subtotal = quantity * unitPrice;
        
        // Check stock availability
        if (selectedProduct.getStock() < quantity) {
            JOptionPane.showMessageDialog(this, 
                "Insufficient stock! Available: " + selectedProduct.getStock() + " units", 
                "Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setProductId(selectedProduct.getId());
        orderItem.setQuantity(quantity);
        orderItem.setUnitPrice(unitPrice);
        orderItem.setSubtotal(subtotal);
        
        currentOrderItems.add(orderItem);
        currentOrderTotal += subtotal;
        
        // Add to table
        Object[] row = {
            selectedProduct.getName(),
            quantity,
            ValidationUtil.formatCurrency(unitPrice),
            ValidationUtil.formatCurrency(subtotal)
        };
        orderItemsTableModel.addRow(row);
        
        // Update total
        totalAmountLabel.setText("Total: " + ValidationUtil.formatCurrency(currentOrderTotal));
        
        // Clear item form
        clearItemForm();
    }
    
    private void removeItemFromOrder() {
        int selectedRow = orderItemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            OrderItem removedItem = currentOrderItems.remove(selectedRow);
            currentOrderTotal -= removedItem.getSubtotal();
            orderItemsTableModel.removeRow(selectedRow);
            totalAmountLabel.setText("Total: " + ValidationUtil.formatCurrency(currentOrderTotal));
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void createOrder() {
        if (!validateOrderForm()) return;
        
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        LocalDate orderDate = LocalDate.parse(orderDateField.getText().trim());
        
        // Create order
        Order order = new Order();
        order.setCustomerId(selectedCustomer.getId());
        order.setOrderDate(orderDate);
        order.setTotalAmount(currentOrderTotal);
        order.setStatus(Order.OrderStatus.PENDING);
        
        // Save order with items
        if (orderDAO.createOrderWithItems(order, currentOrderItems)) {
            JOptionPane.showMessageDialog(this, 
                "Order created successfully!\nOrder Total: " + ValidationUtil.formatCurrency(currentOrderTotal), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearCurrentOrder();
            loadOrders();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create order.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearCurrentOrder() {
        currentOrderItems.clear();
        currentOrderTotal = 0.0;
        orderItemsTableModel.setRowCount(0);
        totalAmountLabel.setText("Total: $0.00");
        customerComboBox.setSelectedIndex(-1);
        orderDateField.setText(LocalDate.now().toString());
        clearItemForm();
    }
    
    private void clearItemForm() {
        productComboBox.setSelectedIndex(-1);
        quantityField.setText("");
        unitPriceField.setText("");
    }
    
    private void viewOrderDetails() {
        int selectedRow = ordersTable.getSelectedRow();
        if (selectedRow >= 0) {
            // In a complete implementation, you would show order details in a new dialog
            JOptionPane.showMessageDialog(this, "Order details functionality would be implemented here.", 
                                        "Order Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an order to view details.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private boolean validateItemForm() {
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
        
        if (!ValidationUtil.isValidPrice(unitPriceField.getText()) || 
            Double.parseDouble(unitPriceField.getText().trim()) <= 0) {
            JOptionPane.showMessageDialog(this, "Please enter a valid unit price (greater than 0).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            unitPriceField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private boolean validateOrderForm() {
        if (customerComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (currentOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the order.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            LocalDate.parse(orderDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD format).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            orderDateField.requestFocus();
            return false;
        }
        
        return true;
    }
}