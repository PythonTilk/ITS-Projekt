package notizapp;

import notizapp.DesktopUser;
import notizapp.UserService;
import notizapp.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Login window for the desktop application
 * Matches the styling of the web application's login page
 */
public class LoginFrame extends JFrame implements ThemeManager.ThemeChangeListener {
    
    private final UserService userService = new UserService();
    
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JButton themeToggleButton;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel statusLabel;
    
    public LoginFrame() {
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        applyTheme();
        
        // Register for theme changes
        ThemeManager.addThemeChangeListener(this);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Notiz Projekt - Login");
        setSize(450, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Theme toggle button
        themeToggleButton = createThemeToggleButton();
        
        // Title and subtitle
        titleLabel = new JLabel("Notiz Projekt", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        
        subtitleLabel = new JLabel("Sign in to your account", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 16));
        
        // Form labels
        usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        
        passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        
        // Form fields
        usernameField = createStyledTextField();
        usernameField.setFont(new Font("Inter", Font.PLAIN, 14));
        
        passwordField = createStyledPasswordField();
        passwordField.setFont(new Font("Inter", Font.PLAIN, 14));
        
        // Buttons
        loginButton = createStyledButton("Sign In", ThemeManager.getPrimaryColor());
        registerButton = createStyledButton("Create Account", ThemeManager.getSecondaryColor());
        
        // Status label
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Inter", Font.PLAIN, 12));
    }
    
    private void setupLayout() {
        // Header panel with theme toggle
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(themeToggleButton, BorderLayout.EAST);
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.setBorder(new EmptyBorder(20, 0, 40, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(subtitleLabel);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Username field
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Password field
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Buttons
        formPanel.add(loginButton);
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(registerButton);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(statusLabel);
        
        // Main layout
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(titlePanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        loginButton.addActionListener(e -> performLogin());
        registerButton.addActionListener(e -> showRegisterDialog());
        
        // Enter key handling
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        
        themeToggleButton.addActionListener(e -> ThemeManager.toggleTheme());
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showStatus("Please enter both username and password", ThemeManager.getDangerColor());
            return;
        }
        
        // Disable UI during login
        setUIEnabled(false);
        showStatus("Signing in...", ThemeManager.getTextColor());
        
        // Perform login in background thread
        SwingWorker<DesktopUser, Void> loginWorker = new SwingWorker<DesktopUser, Void>() {
            @Override
            protected DesktopUser doInBackground() throws Exception {
                return userService.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    DesktopUser user = get();
                    if (user != null) {
                        showStatus("Login successful!", ThemeManager.getSuccessColor());
                        // Open main application window
                        SwingUtilities.invokeLater(() -> {
                            new MainFrame(user).setVisible(true);
                            dispose();
                        });
                    } else {
                        showStatus("Invalid username or password", ThemeManager.getDangerColor());
                        setUIEnabled(true);
                    }
                } catch (Exception e) {
                    showStatus("Login failed: " + e.getMessage(), ThemeManager.getDangerColor());
                    setUIEnabled(true);
                }
            }
        };
        
        loginWorker.execute();
    }
    
    private void showRegisterDialog() {
        RegisterDialog dialog = new RegisterDialog(this);
        dialog.setVisible(true);
        
        if (dialog.isRegistrationSuccessful()) {
            showStatus("Registration successful! Please sign in.", ThemeManager.getSuccessColor());
            usernameField.setText(dialog.getRegisteredUsername());
            passwordField.requestFocus();
        }
    }
    
    private void setUIEnabled(boolean enabled) {
        usernameField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        loginButton.setEnabled(enabled);
        registerButton.setEnabled(enabled);
    }
    
    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        return field;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(0, 44));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(0, 102, 204)); // Standard blue
        button.setForeground(Color.WHITE);
        
        // Simple border
        button.setBorder(BorderFactory.createEmptyBorder(10, 24, 10, 24));
        
        // Make sure button shows its background color
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        button.setFocusPainted(false);
        
        return button;
    }
    
    private JButton createThemeToggleButton() {
        JButton button = new JButton("Theme");
        button.setPreferredSize(new Dimension(80, 30));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setToolTipText("Toggle dark mode");
        
        // Make sure button shows its background color
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set colors
        button.setBackground(new Color(0, 102, 204)); // Standard blue
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        
        button.addActionListener(e -> {
            ThemeManager.toggleTheme();
        });
        
        return button;
    }
    
    private void updateThemeToggleIcon(JButton button) {
        // Just update the text
        button.setText("Theme");
    }
    
    private void applyTheme() {
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        
        // Text elements
        titleLabel.setForeground(ThemeManager.getTextColor());
        subtitleLabel.setForeground(ThemeManager.getTextColor());
        usernameLabel.setForeground(ThemeManager.getTextColor());
        passwordLabel.setForeground(ThemeManager.getTextColor());
        
        // Input fields
        usernameField.setBackground(ThemeManager.getInputBackground());
        usernameField.setForeground(ThemeManager.getTextColor());
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        passwordField.setBackground(ThemeManager.getInputBackground());
        passwordField.setForeground(ThemeManager.getTextColor());
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Buttons - all blue
        loginButton.setBackground(new Color(0, 102, 204));
        loginButton.setForeground(Color.WHITE);
        
        registerButton.setBackground(new Color(0, 102, 204));
        registerButton.setForeground(Color.WHITE);
        
        // Theme toggle
        themeToggleButton.setBackground(new Color(0, 102, 204));
        themeToggleButton.setForeground(Color.WHITE);
        
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
}