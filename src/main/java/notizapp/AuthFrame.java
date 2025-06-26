package notizapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Combined authentication frame for login and registration
 * Supports both login and registration modes
 */
public class AuthFrame extends JFrame implements ThemeManager.ThemeChangeListener {
    
    // UI Components
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField displayNameField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton switchModeButton;
    private JButton themeToggleButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel statusLabel;
    
    // State
    private boolean isLoginMode = true;
    private boolean authenticationSuccessful = false;
    private User authenticatedUser = null;
    
    /**
     * Constructor
     */
    public AuthFrame() {
        super("Notiz Desktop");
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        
        ThemeManager.getInstance().addThemeChangeListener(this);
        applyTheme();
        
        setSize(400, isLoginMode ? 350 : 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        titleLabel = new JLabel("Notiz Desktop");
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 24));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        subtitleLabel = new JLabel(isLoginMode ? "Login to your account" : "Create a new account");
        subtitleLabel.setFont(new Font(subtitleLabel.getFont().getName(), Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        if (!isLoginMode) {
            confirmPasswordField = new JPasswordField(20);
            displayNameField = new JTextField(20);
        }
        
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        switchModeButton = new JButton(isLoginMode ? "Create Account" : "Back to Login");
        themeToggleButton = new JButton(ThemeManager.getInstance().isDarkTheme() ? "☀" : "🌙");
        themeToggleButton.setFont(new Font(themeToggleButton.getFont().getName(), Font.PLAIN, 16));
        themeToggleButton.setFocusPainted(false);
        themeToggleButton.setBorderPainted(false);
        themeToggleButton.setContentAreaFilled(false);
        
        statusLabel = new JLabel(" ");
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Theme toggle in top right
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(themeToggleButton, BorderLayout.EAST);
        
        // Title and subtitle
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 5, 20, 5);
        mainPanel.add(subtitleLabel, gbc);
        
        // Username field
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        mainPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        mainPanel.add(passwordField, gbc);
        
        if (!isLoginMode) {
            // Confirm password field
            gbc.gridx = 0;
            gbc.gridy = 4;
            mainPanel.add(new JLabel("Confirm Password:"), gbc);
            
            gbc.gridx = 1;
            mainPanel.add(confirmPasswordField, gbc);
            
            // Display name field
            gbc.gridx = 0;
            gbc.gridy = 5;
            mainPanel.add(new JLabel("Display Name:"), gbc);
            
            gbc.gridx = 1;
            mainPanel.add(displayNameField, gbc);
        }
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        if (isLoginMode) {
            buttonPanel.add(loginButton);
        } else {
            buttonPanel.add(registerButton);
        }
        buttonPanel.add(switchModeButton);
        
        // Status label
        gbc.gridx = 0;
        gbc.gridy = isLoginMode ? 4 : 6;
        gbc.gridwidth = 2;
        mainPanel.add(statusLabel, gbc);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupEventHandlers() {
        // Theme toggle button
        themeToggleButton.addActionListener(e -> {
            ThemeManager.getInstance().toggleTheme();
            themeToggleButton.setText(ThemeManager.getInstance().isDarkTheme() ? "☀" : "🌙");
        });
        
        // Login button
        loginButton.addActionListener(e -> {
            if (isLoginMode) {
                performLogin();
            }
        });
        
        // Register button
        registerButton.addActionListener(e -> {
            if (!isLoginMode) {
                performRegistration();
            }
        });
        
        // Switch mode button
        switchModeButton.addActionListener(e -> {
            isLoginMode = !isLoginMode;
            switchAuthMode();
        });
        
        // Key listeners for enter key
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (isLoginMode) {
                        performLogin();
                    } else {
                        performRegistration();
                    }
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
        
        if (!isLoginMode && confirmPasswordField != null) {
            confirmPasswordField.addKeyListener(enterKeyAdapter);
        }
        
        if (!isLoginMode && displayNameField != null) {
            displayNameField.addKeyListener(enterKeyAdapter);
        }
    }
    
    private void switchAuthMode() {
        // Clear fields
        usernameField.setText("");
        passwordField.setText("");
        statusLabel.setText(" ");
        
        if (isLoginMode) {
            // Switch to login mode
            subtitleLabel.setText("Login to your account");
            
            // Remove registration-specific components
            if (confirmPasswordField != null && confirmPasswordField.getParent() != null) {
                confirmPasswordField.getParent().remove(confirmPasswordField);
            }
            
            if (displayNameField != null && displayNameField.getParent() != null) {
                displayNameField.getParent().remove(displayNameField);
            }
            
            // Update buttons
            switchModeButton.setText("Create Account");
            
            // Remove register button and add login button
            Container buttonPanel = switchModeButton.getParent();
            buttonPanel.removeAll();
            buttonPanel.add(loginButton);
            buttonPanel.add(switchModeButton);
            
            setSize(400, 350);
        } else {
            // Switch to registration mode
            subtitleLabel.setText("Create a new account");
            
            // Initialize registration-specific components if needed
            if (confirmPasswordField == null) {
                confirmPasswordField = new JPasswordField(20);
                confirmPasswordField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            performRegistration();
                        }
                    }
                });
            }
            
            if (displayNameField == null) {
                displayNameField = new JTextField(20);
                displayNameField.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                            performRegistration();
                        }
                    }
                });
            }
            
            // Add registration-specific components
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            // Confirm password field
            gbc.gridx = 0;
            gbc.gridy = 4;
            mainPanel.add(new JLabel("Confirm Password:"), gbc);
            
            gbc.gridx = 1;
            mainPanel.add(confirmPasswordField, gbc);
            
            // Display name field
            gbc.gridx = 0;
            gbc.gridy = 5;
            mainPanel.add(new JLabel("Display Name:"), gbc);
            
            gbc.gridx = 1;
            mainPanel.add(displayNameField, gbc);
            
            // Update buttons
            switchModeButton.setText("Back to Login");
            
            // Remove login button and add register button
            Container buttonPanel = switchModeButton.getParent();
            buttonPanel.removeAll();
            buttonPanel.add(registerButton);
            buttonPanel.add(switchModeButton);
            
            setSize(400, 450);
        }
        
        // Update status label position
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = isLoginMode ? 4 : 6;
        gbc.gridwidth = 2;
        
        if (statusLabel.getParent() != null) {
            statusLabel.getParent().remove(statusLabel);
        }
        
        mainPanel.add(statusLabel, gbc);
        
        // Refresh UI
        revalidate();
        repaint();
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Logging in...");
        statusLabel.setForeground(Color.BLUE);
        
        // Use SwingWorker to perform login in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return User.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    authenticatedUser = get();
                    if (authenticatedUser != null) {
                        authenticationSuccessful = true;
                        statusLabel.setText("Login successful!");
                        statusLabel.setForeground(new Color(0, 150, 0));
                        
                        // Close login window after a short delay
                        Timer timer = new Timer(800, e -> {
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setText("Invalid username or password");
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    private void performRegistration() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String displayName = displayNameField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            statusLabel.setText("Please fill all required fields");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            statusLabel.setText("Passwords do not match");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (username.length() < 3) {
            statusLabel.setText("Username must be at least 3 characters");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        if (password.length() < 6) {
            statusLabel.setText("Password must be at least 6 characters");
            statusLabel.setForeground(Color.RED);
            return;
        }
        
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        statusLabel.setText("Creating account...");
        statusLabel.setForeground(Color.BLUE);
        
        // Use SwingWorker to perform registration in background
        SwingWorker<User, Void> worker = new SwingWorker<User, Void>() {
            @Override
            protected User doInBackground() throws Exception {
                return User.register(username, password, displayName);
            }
            
            @Override
            protected void done() {
                try {
                    authenticatedUser = get();
                    if (authenticatedUser != null) {
                        authenticationSuccessful = true;
                        statusLabel.setText("Registration successful!");
                        statusLabel.setForeground(new Color(0, 150, 0));
                        
                        // Close registration window after a short delay
                        Timer timer = new Timer(800, e -> {
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setText("Username already exists");
                        statusLabel.setForeground(Color.RED);
                    }
                } catch (Exception e) {
                    statusLabel.setText("Error: " + e.getMessage());
                    statusLabel.setForeground(Color.RED);
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        
        worker.execute();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkTheme) {
        applyTheme();
    }
    
    private void applyTheme() {
        boolean isDarkTheme = ThemeManager.getInstance().isDarkTheme();
        
        // Apply theme to components
        if (isDarkTheme) {
            mainPanel.setBackground(new Color(50, 50, 50));
            mainPanel.setForeground(Color.WHITE);
            titleLabel.setForeground(Color.WHITE);
            subtitleLabel.setForeground(new Color(200, 200, 200));
            
            usernameField.setBackground(new Color(60, 60, 60));
            usernameField.setForeground(Color.WHITE);
            usernameField.setCaretColor(Color.WHITE);
            
            passwordField.setBackground(new Color(60, 60, 60));
            passwordField.setForeground(Color.WHITE);
            passwordField.setCaretColor(Color.WHITE);
            
            if (confirmPasswordField != null) {
                confirmPasswordField.setBackground(new Color(60, 60, 60));
                confirmPasswordField.setForeground(Color.WHITE);
                confirmPasswordField.setCaretColor(Color.WHITE);
            }
            
            if (displayNameField != null) {
                displayNameField.setBackground(new Color(60, 60, 60));
                displayNameField.setForeground(Color.WHITE);
                displayNameField.setCaretColor(Color.WHITE);
            }
            
            // Set label colors
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof JLabel && comp != titleLabel && comp != subtitleLabel && comp != statusLabel) {
                    comp.setForeground(Color.WHITE);
                }
            }
            
            // Set button styles
            loginButton.setBackground(new Color(70, 130, 180));
            loginButton.setForeground(Color.WHITE);
            
            registerButton.setBackground(new Color(70, 130, 180));
            registerButton.setForeground(Color.WHITE);
            
            switchModeButton.setBackground(new Color(60, 60, 60));
            switchModeButton.setForeground(Color.WHITE);
            
            themeToggleButton.setForeground(Color.WHITE);
            
            // Set content pane background
            getContentPane().setBackground(new Color(50, 50, 50));
            
            // Set button panel background
            if (loginButton.getParent() != null) {
                loginButton.getParent().setBackground(new Color(50, 50, 50));
            }
        } else {
            mainPanel.setBackground(new Color(240, 240, 240));
            mainPanel.setForeground(Color.BLACK);
            titleLabel.setForeground(new Color(70, 130, 180));
            subtitleLabel.setForeground(new Color(100, 100, 100));
            
            usernameField.setBackground(Color.WHITE);
            usernameField.setForeground(Color.BLACK);
            usernameField.setCaretColor(Color.BLACK);
            
            passwordField.setBackground(Color.WHITE);
            passwordField.setForeground(Color.BLACK);
            passwordField.setCaretColor(Color.BLACK);
            
            if (confirmPasswordField != null) {
                confirmPasswordField.setBackground(Color.WHITE);
                confirmPasswordField.setForeground(Color.BLACK);
                confirmPasswordField.setCaretColor(Color.BLACK);
            }
            
            if (displayNameField != null) {
                displayNameField.setBackground(Color.WHITE);
                displayNameField.setForeground(Color.BLACK);
                displayNameField.setCaretColor(Color.BLACK);
            }
            
            // Set label colors
            for (Component comp : mainPanel.getComponents()) {
                if (comp instanceof JLabel && comp != titleLabel && comp != subtitleLabel && comp != statusLabel) {
                    comp.setForeground(Color.BLACK);
                }
            }
            
            // Set button styles
            loginButton.setBackground(new Color(70, 130, 180));
            loginButton.setForeground(Color.WHITE);
            
            registerButton.setBackground(new Color(70, 130, 180));
            registerButton.setForeground(Color.WHITE);
            
            switchModeButton.setBackground(new Color(230, 230, 230));
            switchModeButton.setForeground(Color.BLACK);
            
            themeToggleButton.setForeground(Color.BLACK);
            
            // Set content pane background
            getContentPane().setBackground(new Color(240, 240, 240));
            
            // Set button panel background
            if (loginButton.getParent() != null) {
                loginButton.getParent().setBackground(new Color(240, 240, 240));
            }
        }
    }
    
    /**
     * Check if authentication was successful
     */
    public boolean isAuthenticationSuccessful() {
        return authenticationSuccessful;
    }
    
    /**
     * Get the authenticated user
     */
    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}