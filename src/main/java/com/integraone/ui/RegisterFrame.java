package com.integraone.ui;

import com.integraone.dao.UserDAO;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class RegisterFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JComboBox<User.UserRole> roleComboBox;
    private UserDAO userDAO;
    private LoginFrame parentFrame;
    
    public RegisterFrame(LoginFrame parent) {
        this.parentFrame = parent;
        this.userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("IntegraOne ERP - Register New User");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parentFrame);
        setResizable(false);
        
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);
        
        usernameField = new JTextField();
        usernameField.setPreferredSize(UIConstants.FIELD_SIZE);
        usernameField.setFont(UIConstants.LABEL_FONT);
        
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(UIConstants.FIELD_SIZE);
        passwordField.setFont(UIConstants.LABEL_FONT);
        
        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setPreferredSize(UIConstants.FIELD_SIZE);
        confirmPasswordField.setFont(UIConstants.LABEL_FONT);
        
        emailField = new JTextField();
        emailField.setPreferredSize(UIConstants.FIELD_SIZE);
        emailField.setFont(UIConstants.LABEL_FONT);
        
        roleComboBox = new JComboBox<>(User.UserRole.values());
        roleComboBox.setPreferredSize(UIConstants.FIELD_SIZE);
        roleComboBox.setFont(UIConstants.LABEL_FONT);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.PADDING, UIConstants.PADDING, 
                               UIConstants.PADDING, UIConstants.PADDING);
        
        JLabel titleLabel = new JLabel("Register New User");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        
        addFormField("Username:", usernameField, gbc, 1);
        
        addFormField("Password:", passwordField, gbc, 2);
        
        addFormField("Confirm Password:", confirmPasswordField, gbc, 3);
        
        addFormField("Email:", emailField, gbc, 4);
        
        addFormField("Role:", roleComboBox, gbc, 5);
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(UIConstants.BUTTON_SIZE);
        registerButton.setFont(UIConstants.BUTTON_FONT);
        registerButton.setBackground(UIConstants.SUCCESS_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(UIConstants.BUTTON_SIZE);
        cancelButton.setFont(UIConstants.BUTTON_FONT);
        cancelButton.setBackground(UIConstants.ERROR_COLOR);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFocusPainted(false);
        
        buttonPanel.add(registerButton);
        buttonPanel.add(cancelButton);
        
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
        
        registerButton.addActionListener(e -> performRegistration());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void addFormField(String labelText, JComponent field, GridBagConstraints gbc, int row) {
        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.LABEL_FONT);
        label.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = row;
        add(label, gbc);
        
        gbc.gridx = 1; gbc.gridy = row;
        add(field, gbc);
    }
    
    private void setupEventHandlers() {
        confirmPasswordField.addActionListener(e -> performRegistration());
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText().trim();
        User.UserRole role = (User.UserRole) roleComboBox.getSelectedItem();
        
        if (!ValidationUtil.isNotEmpty(username)) {
            showError("Username is required.");
            return;
        }
        
        if (!ValidationUtil.isNotEmpty(password)) {
            showError("Password is required.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match.");
            return;
        }
        
        if (!ValidationUtil.isValidEmail(email)) {
            showError("Please enter a valid email address.");
            return;
        }
        
        if (userDAO.isUsernameExists(username)) {
            showError("Username already exists. Please choose a different username.");
            return;
        }
        
        User user = new User(username, password, email, role);
        if (userDAO.createUser(user)) {
            JOptionPane.showMessageDialog(this, 
                "User registered successfully!", 
                "Registration Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Failed to register user. Please try again.");
        }
    }
    
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Registration Error", JOptionPane.ERROR_MESSAGE);
    }
}