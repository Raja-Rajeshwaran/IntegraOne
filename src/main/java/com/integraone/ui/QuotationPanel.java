package com.integraone.ui;

import com.integraone.dao.CustomerDAO;
import com.integraone.dao.ProductDAO;
import com.integraone.dao.QuotationDAO;
import com.integraone.model.Customer;
import com.integraone.model.Product;
import com.integraone.model.Quotation;
import com.integraone.model.QuotationItem;
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
 * Quotation/Invoice Management Panel
 */
@SuppressWarnings("serial")
public class QuotationPanel extends JPanel {
    private DashboardFrame parentFrame;
    @SuppressWarnings("unused")
	private User currentUser;
    private CustomerDAO customerDAO;
    private ProductDAO productDAO;
    private QuotationDAO quotationDAO;
    
    private JComboBox<Customer> customerComboBox;
    private JTextField quotationDateField;
    private JComboBox<Product> productComboBox;
    private JTextField quantityField;
    private JTextField unitPriceField;
    private JTable quotationItemsTable;
    private DefaultTableModel quotationItemsTableModel;
    private JTable quotationsTable;
    private DefaultTableModel quotationsTableModel;
    private JLabel totalAmountLabel;
    
    private List<QuotationItem> currentQuotationItems;
    private double currentQuotationTotal;
    
    public QuotationPanel(DashboardFrame parent, User user) {
        this.parentFrame = parent;
        this.currentUser = user;
        this.customerDAO = new CustomerDAO();
        this.productDAO = new ProductDAO();
        this.quotationDAO = new QuotationDAO();
        this.currentQuotationItems = new ArrayList<>();
        this.currentQuotationTotal = 0.0;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadCustomers();
        loadProducts();
        loadQuotations();
    }
    
    private void initializeComponents() {
        setBackground(UIConstants.CONTENT_COLOR);
        setLayout(new BorderLayout());
        
        customerComboBox = new JComboBox<>();
        customerComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        customerComboBox.setFont(UIConstants.LABEL_FONT);
        
        quotationDateField = new JTextField(LocalDate.now().toString());
        quotationDateField.setPreferredSize(UIConstants.FIELD_SIZE);
        quotationDateField.setFont(UIConstants.LABEL_FONT);
        
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
        
        String[] quotationItemColumns = {"Product", "Quantity", "Unit Price", "Subtotal"};
        quotationItemsTableModel = new DefaultTableModel(quotationItemColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        quotationItemsTable = new JTable(quotationItemsTableModel);
        quotationItemsTable.setFont(UIConstants.TABLE_FONT);
        quotationItemsTable.setRowHeight(25);
        quotationItemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quotationItemsTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        quotationItemsTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        quotationItemsTable.getTableHeader().setForeground(Color.WHITE);
        
        String[] quotationColumns = {"S.No", "Customer", "Quotation Date", "Total Amount", "Status"};
        quotationsTableModel = new DefaultTableModel(quotationColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        quotationsTable = new JTable(quotationsTableModel);
        quotationsTable.setFont(UIConstants.TABLE_FONT);
        quotationsTable.setRowHeight(25);
        quotationsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        quotationsTable.getTableHeader().setFont(UIConstants.LABEL_FONT);
        quotationsTable.getTableHeader().setBackground(UIConstants.BUTTON_COLOR);
        quotationsTable.getTableHeader().setForeground(Color.WHITE);
    }
    
    private void setupLayout() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UIConstants.CONTENT_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(UIConstants.PADDING, UIConstants.MARGIN, 
                                                              UIConstants.PADDING, UIConstants.MARGIN));
        
        JLabel titleLabel = new JLabel("Quotation/Invoice Management");
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
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.LABEL_FONT);
        
        JPanel newQuotationPanel = createNewQuotationPanel();
        tabbedPane.addTab("New Quotation", newQuotationPanel);
        
        JPanel quotationHistoryPanel = createQuotationHistoryPanel();
        tabbedPane.addTab("Quotation History", quotationHistoryPanel);
        
        add(headerPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createNewQuotationPanel() {
        JPanel newQuotationPanel = new JPanel(new BorderLayout());
        newQuotationPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JPanel quotationInfoPanel = createQuotationInfoPanel();
        
        JPanel quotationItemsPanel = createQuotationItemsPanel();
        
        newQuotationPanel.add(quotationInfoPanel, BorderLayout.NORTH);
        newQuotationPanel.add(quotationItemsPanel, BorderLayout.CENTER);
        
        return newQuotationPanel;
    }
    
    private JPanel createQuotationInfoPanel() {
        JPanel quotationInfoPanel = new JPanel(new GridBagLayout());
        quotationInfoPanel.setBackground(UIConstants.CONTENT_COLOR);
        quotationInfoPanel.setBorder(BorderFactory.createTitledBorder("Quotation Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        quotationInfoPanel.add(new JLabel("Customer:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        quotationInfoPanel.add(customerComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        quotationInfoPanel.add(new JLabel("Quotation Date:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        quotationInfoPanel.add(quotationDateField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        quotationInfoPanel.add(totalAmountLabel, gbc);
        
        return quotationInfoPanel;
    }
    
    private JPanel createQuotationItemsPanel() {
        JPanel quotationItemsPanel = new JPanel(new BorderLayout());
        quotationItemsPanel.setBackground(UIConstants.CONTENT_COLOR);
        quotationItemsPanel.setBorder(BorderFactory.createTitledBorder("Quotation Items"));
        
        JPanel addItemPanel = createAddItemPanel();
        
        JScrollPane itemsScrollPane = new JScrollPane(quotationItemsTable);
        itemsScrollPane.setPreferredSize(new Dimension(0, 200));
        
        JPanel quotationActionsPanel = createQuotationActionsPanel();
        
        quotationItemsPanel.add(addItemPanel, BorderLayout.NORTH);
        quotationItemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        quotationItemsPanel.add(quotationActionsPanel, BorderLayout.SOUTH);
        
        return quotationItemsPanel;
    }
    
    private JPanel createAddItemPanel() {
        JPanel addItemPanel = new JPanel(new GridBagLayout());
        addItemPanel.setBackground(UIConstants.CONTENT_COLOR);
        addItemPanel.setBorder(BorderFactory.createTitledBorder("Add Item"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Product:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        addItemPanel.add(productComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3; gbc.gridy = 0;
        addItemPanel.add(quantityField, gbc);
        
        gbc.gridx = 4; gbc.gridy = 0;
        addItemPanel.add(new JLabel("Unit Price:"), gbc);
        gbc.gridx = 5; gbc.gridy = 0;
        addItemPanel.add(unitPriceField, gbc);
        
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
    
    private JPanel createQuotationActionsPanel() {
        JPanel quotationActionsPanel = new JPanel(new FlowLayout());
        quotationActionsPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton createQuotationButton = createButton("Create Quotation", UIConstants.SUCCESS_COLOR);
        JButton clearQuotationButton = createButton("Clear Quotation", UIConstants.WARNING_COLOR);
        JButton generatePDFButton = createButton("Generate PDF", UIConstants.BUTTON_COLOR);
        
        quotationActionsPanel.add(createQuotationButton);
        quotationActionsPanel.add(clearQuotationButton);
        quotationActionsPanel.add(generatePDFButton);
        
        return quotationActionsPanel;
    }
    
    private JPanel createQuotationHistoryPanel() {
        JPanel quotationHistoryPanel = new JPanel(new BorderLayout());
        quotationHistoryPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JScrollPane quotationsScrollPane = new JScrollPane(quotationsTable);
        quotationsScrollPane.setPreferredSize(new Dimension(0, 400));
        
        JPanel historyButtonPanel = new JPanel(new FlowLayout());
        historyButtonPanel.setBackground(UIConstants.CONTENT_COLOR);
        
        JButton refreshQuotationsButton = createButton("Refresh", UIConstants.BUTTON_COLOR);
        JButton viewQuotationButton = createButton("View Details", UIConstants.BUTTON_COLOR);
        JButton convertToInvoiceButton = createButton("Convert to Invoice", UIConstants.SUCCESS_COLOR);
        
        historyButtonPanel.add(refreshQuotationsButton);
        historyButtonPanel.add(viewQuotationButton);
        historyButtonPanel.add(convertToInvoiceButton);
        
        quotationHistoryPanel.add(quotationsScrollPane, BorderLayout.CENTER);
        quotationHistoryPanel.add(historyButtonPanel, BorderLayout.SOUTH);
        
        return quotationHistoryPanel;
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
        productComboBox.addActionListener(e -> {
            Product selectedProduct = (Product) productComboBox.getSelectedItem();
            if (selectedProduct != null) {
                unitPriceField.setText(String.valueOf(selectedProduct.getPrice()));
            }
        });
        
        findButton("Add Item").addActionListener(e -> addItemToQuotation());
        
        findButton("Remove Item").addActionListener(e -> removeItemFromQuotation());
        
        findButton("Create Quotation").addActionListener(e -> createQuotation());
        
        findButton("Clear Quotation").addActionListener(e -> clearCurrentQuotation());
        
        findButton("Generate PDF").addActionListener(e -> generatePDF());
        
        findButton("Refresh").addActionListener(e -> loadQuotations());
        
        findButton("View Details").addActionListener(e -> viewQuotationDetails());
        
        findButton("Convert to Invoice").addActionListener(e -> convertToInvoice());
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
    
    private void loadQuotations() {
        quotationsTableModel.setRowCount(0);
        List<Quotation> quotations = quotationDAO.getAllQuotations();
        
        int serialNo = 1;
        for (Quotation quotation : quotations) {
            Customer customer = customerDAO.getCustomerById(quotation.getCustomerId());
            
            Object[] row = {
                serialNo++,
                customer != null ? customer.getName() : "Unknown Customer",
                quotation.getQuotationDate().toString(),
                ValidationUtil.formatCurrency(quotation.getTotalAmount()),
                quotation.getStatus()
            };
            quotationsTableModel.addRow(row);
        }
    }
    
    private void addItemToQuotation() {
        if (!validateItemForm()) return;
        
        Product selectedProduct = (Product) productComboBox.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
        double subtotal = quantity * unitPrice;
        
        QuotationItem quotationItem = new QuotationItem();
        quotationItem.setProductId(selectedProduct.getId());
        quotationItem.setQuantity(quantity);
        quotationItem.setUnitPrice(unitPrice);
        quotationItem.setSubtotal(subtotal);
        
        currentQuotationItems.add(quotationItem);
        currentQuotationTotal += subtotal;
        
        Object[] row = {
            selectedProduct.getName(),
            quantity,
            ValidationUtil.formatCurrency(unitPrice),
            ValidationUtil.formatCurrency(subtotal)
        };
        quotationItemsTableModel.addRow(row);
        
        totalAmountLabel.setText("Total: " + ValidationUtil.formatCurrency(currentQuotationTotal));
        
        clearItemForm();
    }
    
    private void removeItemFromQuotation() {
        int selectedRow = quotationItemsTable.getSelectedRow();
        if (selectedRow >= 0) {
            QuotationItem removedItem = currentQuotationItems.remove(selectedRow);
            currentQuotationTotal -= removedItem.getSubtotal();
            quotationItemsTableModel.removeRow(selectedRow);
            totalAmountLabel.setText("Total: " + ValidationUtil.formatCurrency(currentQuotationTotal));
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void createQuotation() {
        if (!validateQuotationForm()) return;
        
        Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
        LocalDate quotationDate = LocalDate.parse(quotationDateField.getText().trim());
        
        Quotation quotation = new Quotation();
        quotation.setCustomerId(selectedCustomer.getId());
        quotation.setQuotationDate(quotationDate);
        quotation.setTotalAmount(currentQuotationTotal);
        quotation.setStatus(Quotation.QuotationStatus.DRAFT);
        
        if (quotationDAO.createQuotationWithItems(quotation, currentQuotationItems)) {
            JOptionPane.showMessageDialog(this, 
                "Quotation created successfully!\nQuotation Total: " + ValidationUtil.formatCurrency(currentQuotationTotal), 
                "Success", JOptionPane.INFORMATION_MESSAGE);
            
            clearCurrentQuotation();
            loadQuotations();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to create quotation.", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearCurrentQuotation() {
        currentQuotationItems.clear();
        currentQuotationTotal = 0.0;
        quotationItemsTableModel.setRowCount(0);
        totalAmountLabel.setText("Total: $0.00");
        customerComboBox.setSelectedIndex(-1);
        quotationDateField.setText(LocalDate.now().toString());
        clearItemForm();
    }
    
    private void clearItemForm() {
        productComboBox.setSelectedIndex(-1);
        quantityField.setText("");
        unitPriceField.setText("");
    }
    
    private void generatePDF() {
        if (currentQuotationItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add items to generate PDF.", 
                                        "No Items", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JOptionPane.showMessageDialog(this, 
            "PDF generation functionality would be implemented here using iText library.\n" +
            "The PDF would include company details, customer information, and itemized quotation.", 
            "Generate PDF", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewQuotationDetails() {
        int selectedRow = quotationsTable.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showMessageDialog(this, "Quotation details functionality would be implemented here.", 
                                        "Quotation Details", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a quotation to view details.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void convertToInvoice() {
        int selectedRow = quotationsTable.getSelectedRow();
        if (selectedRow >= 0) {
            JOptionPane.showMessageDialog(this, "Convert to invoice functionality would be implemented here.", 
                                        "Convert to Invoice", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a quotation to convert to invoice.", 
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
    
    private boolean validateQuotationForm() {
        if (customerComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        if (currentQuotationItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the quotation.", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        try {
            LocalDate.parse(quotationDateField.getText().trim());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid date (YYYY-MM-DD format).", 
                                        "Validation Error", JOptionPane.WARNING_MESSAGE);
            quotationDateField.requestFocus();
            return false;
        }
        
        return true;
    }
}