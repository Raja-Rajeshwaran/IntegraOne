package com.integraone.ui;

import com.integraone.dao.UserDAO;
import com.integraone.model.User;
import com.integraone.util.UIConstants;
import com.integraone.util.ValidationUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login frame for user authentication
 */
@SuppressWarnings("serial")
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private UserDAO userDAO;
    
    public LoginFrame() {
        userDAO = new UserDAO();
        initializeComponents();
        setupLayout();
        setupEventHandlers();
    }
    
    private void initializeComponents() {
        setTitle("IntegraOne - Login");
        setSize(UIConstants.LOGIN_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        getContentPane().setBackground(UIConstants.BACKGROUND_COLOR);
        
        usernameField = new JTextField();
        usernameField.setPreferredSize(UIConstants.FIELD_SIZE);
        usernameField.setFont(UIConstants.LABEL_FONT);
        
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(UIConstants.FIELD_SIZE);
        passwordField.setFont(UIConstants.LABEL_FONT);
    }
    
    private void setupLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(UIConstants.PADDING, UIConstants.PADDING, 
                               UIConstants.PADDING, UIConstants.PADDING);
        
        // Title
        JLabel titleLabel = new JLabel("IntegraOne ERP System");
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(titleLabel, gbc);
        
        // Username
        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(UIConstants.LABEL_FONT);
        usernameLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 1;
        add(usernameLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        add(usernameField, gbc);
        
        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(UIConstants.LABEL_FONT);
        passwordLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 2;
        add(passwordLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        add(passwordField, gbc);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(UIConstants.BACKGROUND_COLOR);
        
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(UIConstants.BUTTON_SIZE);
        loginButton.setFont(UIConstants.BUTTON_FONT);
        loginButton.setBackground(UIConstants.BUTTON_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        
        JButton registerButton = new JButton("Register");
        registerButton.setPreferredSize(UIConstants.BUTTON_SIZE);
        registerButton.setFont(UIConstants.BUTTON_FONT);
        registerButton.setBackground(UIConstants.SUCCESS_COLOR);
        registerButton.setForeground(Color.WHITE);
        registerButton.setFocusPainted(false);
        
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);
        
        // Default credentials info
        JLabel infoLabel = new JLabel("<html><center>Default Login:<br>Admin: admin/admin123<br>User: user/user123</center></html>");
        infoLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        infoLabel.setForeground(UIConstants.TEXT_COLOR);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        add(infoLabel, gbc);
    }
    
    private void setupEventHandlers() {
        // Login button action
        getRootPane().getDefaultButton();
        JButton loginButton = findButton("Login");
        if (loginButton != null) {
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    performLogin();
                }
            });
        }
        
        // Register button action
        JButton registerButton = findButton("Register");
        if (registerButton != null) {
            registerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    openRegisterFrame();
                }
            });
        }
        
        // Enter key for login
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
    }
    
    private JButton findButton(String text) {
        return findButtonInContainer(getContentPane(), text);
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
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (!ValidationUtil.isNotEmpty(username) || !ValidationUtil.isNotEmpty(password)) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password.", 
                "Validation Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User user = userDAO.authenticate(username, password);
        if (user != null) {
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + user.getUsername() + "!", 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open dashboard
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame(user).setVisible(true);
                dispose();
            });
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password.", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }
    
    private void openRegisterFrame() {
        new RegisterFrame(this).setVisible(true);
    }
}