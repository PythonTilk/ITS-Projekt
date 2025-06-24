package notizprojekt.desktop.ui;

import notizprojekt.desktop.model.DesktopUser;
import notizprojekt.desktop.service.UserService;
import notizprojekt.desktop.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for viewing and editing user profile
 */
public class ProfileDialog extends JDialog implements ThemeManager.ThemeChangeListener {
    
    private final DesktopUser currentUser;
    private final UserService userService;
    
    private JTextField usernameField;
    private JTextField emailField;
    private JTextField displayNameField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton saveButton;
    private JButton cancelButton;
    private JLabel avatarLabel;
    
    public ProfileDialog(Frame parent, DesktopUser currentUser) {
        super(parent, "User Profile", true);
        
        this.currentUser = currentUser;
        this.userService = new UserService();
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
        applyTheme();
        
        ThemeManager.addThemeChangeListener(this);
        
        setSize(450, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
    }
    
    private void initializeComponents() {
        // Avatar
        avatarLabel = new JLabel(currentUser.getInitials(), SwingConstants.CENTER);
        avatarLabel.setPreferredSize(new Dimension(80, 80));
        avatarLabel.setFont(new Font("Inter", Font.BOLD, 24));
        avatarLabel.setOpaque(true);
        avatarLabel.setBackground(ThemeManager.getPrimaryColor());
        avatarLabel.setForeground(Color.WHITE);
        avatarLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Form fields
        usernameField = createStyledTextField();
        usernameField.setEditable(false); // Username cannot be changed
        
        emailField = createStyledTextField();
        displayNameField = createStyledTextField();
        
        currentPasswordField = createStyledPasswordField();
        newPasswordField = createStyledPasswordField();
        confirmPasswordField = createStyledPasswordField();
        
        // Buttons
        saveButton = createStyledButton("Save Changes", ThemeManager.getPrimaryColor());
        cancelButton = createStyledButton("Cancel", ThemeManager.getBorderColor().darker());
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Avatar section
        JPanel avatarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatarLabel);
        avatarPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Form section
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Basic info
        formPanel.add(createFieldPanel("Username", usernameField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldPanel("Email", emailField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldPanel("Display Name", displayNameField));
        formPanel.add(Box.createVerticalStrut(20));
        
        // Password section
        JLabel passwordSectionLabel = new JLabel("Change Password");
        passwordSectionLabel.setFont(new Font("Inter", Font.BOLD, 14));
        formPanel.add(passwordSectionLabel);
        formPanel.add(Box.createVerticalStrut(8));
        
        formPanel.add(createFieldPanel("Current Password", currentPasswordField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldPanel("New Password", newPasswordField));
        formPanel.add(Box.createVerticalStrut(12));
        formPanel.add(createFieldPanel("Confirm New Password", confirmPasswordField));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(avatarPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(e -> saveProfile());
        cancelButton.addActionListener(e -> dispose());
    }
    
    private void populateFields() {
        usernameField.setText(currentUser.getUsername());
        emailField.setText(currentUser.getEmail());
        displayNameField.setText(currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "");
    }
    
    private void saveProfile() {
        // Validate email
        String email = emailField.getText().trim();
        if (email.isEmpty() || !email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if password change is requested
        String currentPassword = new String(currentPasswordField.getPassword());
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        
        boolean changePassword = !currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty();
        
        if (changePassword) {
            if (currentPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Current password is required to change password", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (newPassword.length() < 6) {
                JOptionPane.showMessageDialog(this, "New password must be at least 6 characters long", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "New passwords do not match", 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Update user object
        currentUser.setEmail(email);
        currentUser.setDisplayName(displayNameField.getText().trim());
        
        // Save changes
        SwingWorker<Boolean, Void> saveWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (changePassword) {
                    return userService.updateUserProfile(currentUser, currentPassword, newPassword);
                } else {
                    return userService.updateUserProfile(currentUser);
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(ProfileDialog.this, 
                            "Profile updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(ProfileDialog.this, 
                            "Failed to update profile. Please check your current password.", 
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ProfileDialog.this, 
                        "Error updating profile: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        saveWorker.execute();
    }
    
    private JPanel createFieldPanel(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.MEDIUM, 12));
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 32));
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }
    
    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setPreferredSize(new Dimension(0, 32));
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 36));
        button.setFont(new Font("Inter", Font.MEDIUM, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void applyTheme() {
        getContentPane().setBackground(ThemeManager.getCardBackground());
        
        // Update avatar colors
        avatarLabel.setBackground(ThemeManager.getPrimaryColor());
        avatarLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Update form fields
        usernameField.setBackground(ThemeManager.getInputBackground());
        usernameField.setForeground(ThemeManager.getTextColor());
        emailField.setBackground(ThemeManager.getInputBackground());
        emailField.setForeground(ThemeManager.getTextColor());
        displayNameField.setBackground(ThemeManager.getInputBackground());
        displayNameField.setForeground(ThemeManager.getTextColor());
        
        currentPasswordField.setBackground(ThemeManager.getInputBackground());
        currentPasswordField.setForeground(ThemeManager.getTextColor());
        newPasswordField.setBackground(ThemeManager.getInputBackground());
        newPasswordField.setForeground(ThemeManager.getTextColor());
        confirmPasswordField.setBackground(ThemeManager.getInputBackground());
        confirmPasswordField.setForeground(ThemeManager.getTextColor());
        
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
}