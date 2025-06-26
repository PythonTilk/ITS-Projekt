package notizapp;

import notizapp.UserService;
import notizapp.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Registration dialog for new users
 */
public class RegisterDialog extends JDialog implements ThemeManager.ThemeChangeListener {
    
    private final UserService userService = new UserService();
    
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField displayNameField;
    private JButton registerButton;
    private JButton cancelButton;
    private JButton themeToggleButton;
    private JLabel statusLabel;
    
    private boolean registrationSuccessful = false;
    private String registeredUsername = "";
    
    public RegisterDialog(Frame parent) {
        super(parent, "Create Account", true);
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyTheme();
        
        ThemeManager.addThemeChangeListener(this);
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Theme toggle button
        themeToggleButton = createThemeToggleButton();
        
        // Form fields
        usernameField = createStyledTextField();
        passwordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        displayNameField = createStyledTextField();
        
        // Buttons
        registerButton = createStyledButton("Create Account", ThemeManager.getPrimaryColor());
        cancelButton = createStyledButton("Cancel", ThemeManager.getBorderColor().darker());
        
        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 12));
    }
    
    private JButton createThemeToggleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(40, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        button.setFocusPainted(false);
        button.setToolTipText("Toggle dark mode");
        
        // Make sure button shows its background color
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set colors
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        
        updateThemeToggleIcon(button);
        
        return button;
    }
    
    private void updateThemeToggleIcon(JButton button) {
        // Create simple sun/moon icon
        String iconText = ThemeManager.isDarkMode() ? "☀" : "🌙";
        button.setText(iconText);
        button.setFont(new Font("Segoe UI Emoji", Font.BOLD, 18));
    }
    
    private void setupLayout() {
        // Header panel with theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(themeToggleButton, BorderLayout.EAST);
        
        // Title
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Username
        formPanel.add(new JLabel("Username"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(16));
        
        // Display name
        formPanel.add(new JLabel("Display Name (optional)"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(displayNameField);
        formPanel.add(Box.createVerticalStrut(16));
        
        // Password
        formPanel.add(new JLabel("Password"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(16));
        
        // Confirm password
        formPanel.add(new JLabel("Confirm Password"));
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(confirmPasswordField);
        formPanel.add(Box.createVerticalStrut(24));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);
        
        formPanel.add(buttonPanel);
        formPanel.add(Box.createVerticalStrut(16));
        formPanel.add(statusLabel);
        
        // Add header panel to the top
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        registerButton.addActionListener(e -> performRegistration());
        cancelButton.addActionListener(e -> dispose());
        themeToggleButton.addActionListener(e -> {
            ThemeManager.toggleTheme();
            updateThemeToggleIcon(themeToggleButton);
        });
        
        // Enter key handling
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performRegistration();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        displayNameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String displayName = displayNameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        // Validation
        if (username.isEmpty()) {
            showStatus("Username is required", ThemeManager.getDangerColor());
            return;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            showStatus("Username must be 3-20 characters", ThemeManager.getDangerColor());
            return;
        }
        
        if (password.isEmpty()) {
            showStatus("Password is required", ThemeManager.getDangerColor());
            return;
        }
        
        if (password.length() < 6) {
            showStatus("Password must be at least 6 characters", ThemeManager.getDangerColor());
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showStatus("Passwords do not match", ThemeManager.getDangerColor());
            return;
        }
        
        // Check for valid username characters
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showStatus("Username can only contain letters, numbers, and underscores", ThemeManager.getDangerColor());
            return;
        }
        
        // Disable UI during registration
        setUIEnabled(false);
        showStatus("Creating account...", ThemeManager.getTextColor());
        
        // Perform registration in background thread
        SwingWorker<Boolean, Void> registrationWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return userService.registerUser(username, password, displayName.isEmpty() ? null : displayName);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showStatus("Account created successfully!", ThemeManager.getSuccessColor());
                        registrationSuccessful = true;
                        registeredUsername = username;
                        
                        // Close dialog after a short delay
                        Timer timer = new Timer(1500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        showStatus("Username already exists", ThemeManager.getDangerColor());
                        setUIEnabled(true);
                    }
                } catch (Exception e) {
                    showStatus("Registration failed: " + e.getMessage(), ThemeManager.getDangerColor());
                    setUIEnabled(true);
                }
            }
        };
        
        registrationWorker.execute();
    }
    
    private void setUIEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        displayNameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        confirmPasswordField.setEnabled(enabled);
        registerButton.setEnabled(enabled);
        cancelButton.setEnabled(enabled);
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 40));
        button.setFont(new Font("Inter", Font.PLAIN, 14));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.LIGHT_GRAY);
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        
        return button;
    }
    
    private void applyTheme() {
        mainPanel.setBackground(ThemeManager.getCardBackground());
        
        // Update theme toggle button
        updateThemeToggleIcon(themeToggleButton);
        themeToggleButton.setBackground(Color.WHITE);
        themeToggleButton.setForeground(Color.BLACK);
        
        // Update all labels
        for (Component comp : getAllComponents(mainPanel)) {
            if (comp instanceof JLabel && !(comp == statusLabel)) {
                comp.setForeground(ThemeManager.getTextColor());
            } else if (comp instanceof JTextField || comp instanceof JPasswordField) {
                comp.setBackground(ThemeManager.getInputBackground());
                comp.setForeground(ThemeManager.getTextColor());
                ((JComponent) comp).setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            } else if (comp instanceof JButton && comp != themeToggleButton) {
                // Make all buttons have black text
                ((JButton) comp).setForeground(Color.BLACK);
                ((JButton) comp).setBackground(Color.WHITE);
            }
        }
        
        repaint();
    }
    
    private java.util.List<Component> getAllComponents(Container container) {
        java.util.List<Component> components = new java.util.ArrayList<>();
        for (Component comp : container.getComponents()) {
            components.add(comp);
            if (comp instanceof Container) {
                components.addAll(getAllComponents((Container) comp));
            }
        }
        return components;
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
    
    public boolean isRegistrationSuccessful() {
        return registrationSuccessful;
    }
    
    public String getRegisteredUsername() {
        return registeredUsername;
    }
}