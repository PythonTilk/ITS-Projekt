package notizdesktop;

import notizdesktop.config.DatabaseConfig;
import notizdesktop.ui.LoginFrame;
import notizdesktop.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main entry point for the Notiz Desktop Application
 * A 100% Java Swing implementation of the HTML note-taking application
 */
public class NotizDesktopApplication {
    
    public static void main(String[] args) {
        // Set system look and feel properties
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize the application
                initializeApplication();
                
                // Show login window
                new LoginFrame().setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Failed to start application: " + e.getMessage(),
                    "Startup Error", 
                    JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
    
    private static void initializeApplication() {
        // Set up Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Fallback to default
            System.err.println("Could not set system look and feel: " + e.getMessage());
        }
        
        // Initialize theme manager
        ThemeManager.initialize();
        
        // Initialize database connection
        DatabaseConfig.initialize();
        
        // Set application icon
        setApplicationIcon();
        
        // Configure global UI defaults
        configureUIDefaults();
    }
    
    private static void setApplicationIcon() {
        try {
            // Create a simple icon for the application
            Image icon = createApplicationIcon();
            if (Taskbar.isTaskbarSupported()) {
                Taskbar.getTaskbar().setIconImage(icon);
            }
        } catch (Exception e) {
            System.err.println("Could not set application icon: " + e.getMessage());
        }
    }
    
    private static Image createApplicationIcon() {
        // Create a simple 32x32 icon
        BufferedImage icon = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = icon.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw a note-like icon
        g2d.setColor(new Color(255, 255, 136)); // Yellow note color
        g2d.fillRoundRect(4, 4, 24, 24, 4, 4);
        
        g2d.setColor(new Color(200, 200, 100));
        g2d.drawRoundRect(4, 4, 24, 24, 4, 4);
        
        // Add some lines to represent text
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawLine(8, 12, 24, 12);
        g2d.drawLine(8, 16, 20, 16);
        g2d.drawLine(8, 20, 22, 20);
        
        g2d.dispose();
        return icon;
    }
    
    private static void configureUIDefaults() {
        // Set global UI properties to match the web application's style
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("ScrollBar.width", 8);
        UIManager.put("ScrollBar.thumbArc", 4);
        UIManager.put("ScrollBar.thumbInsets", new Insets(2, 2, 2, 2));
        
        // Font settings
        Font interFont = new Font("Inter", Font.PLAIN, 14);
        Font fallbackFont = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
        Font defaultFont = interFont.getFamily().equals("Inter") ? interFont : fallbackFont;
        
        UIManager.put("defaultFont", defaultFont);
        UIManager.put("Button.font", defaultFont);
        UIManager.put("Label.font", defaultFont);
        UIManager.put("TextField.font", defaultFont);
        UIManager.put("TextArea.font", defaultFont);
        UIManager.put("Menu.font", defaultFont);
        UIManager.put("MenuItem.font", defaultFont);
    }
}