package notizapp;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Main entry point for the Notiz Desktop Application
 * A 100% Java Swing implementation of the HTML note-taking application
 */
public class NotizDesktopApplication {
    
    public static void main(String[] args) {
        // Windows-specific system properties
        if (isWindows()) {
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
            System.setProperty("sun.java2d.dpiaware", "true");
            System.setProperty("sun.java2d.uiScale", "1.0");
            // Fix for Windows high DPI displays
            System.setProperty("sun.java2d.win.uiScaleX", "1.0");
            System.setProperty("sun.java2d.win.uiScaleY", "1.0");
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Initialize the application
                initializeApplication();
                
                // Show authentication window
                AuthFrame authFrame = new AuthFrame();
                authFrame.setVisible(true);
                
            } catch (Exception e) {
                e.printStackTrace();
                showErrorDialog("Failed to start application: " + e.getMessage(), "Startup Error");
                System.exit(1);
            }
        });
    }
    
    private static void initializeApplication() {
        // Set up Look and Feel with Windows compatibility
        setupLookAndFeel();
        
        // Windows-specific UI tweaks
        if (isWindows()) {
            setupWindowsUI();
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
    
    /**
     * Check if the application is running on Windows
     */
    private static boolean isWindows() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("win");
    }
    
    /**
     * Setup Look and Feel with cross-platform compatibility
     */
    private static void setupLookAndFeel() {
        try {
            // Try to use system look and feel first
            String systemLAF = UIManager.getSystemLookAndFeelClassName();
            
            // On Windows, prefer Windows LAF over Metal
            if (isWindows()) {
                // Check if Windows LAF is available
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equals(info.getName()) || "Windows Classic".equals(info.getName())) {
                        systemLAF = info.getClassName();
                        break;
                    }
                }
            }
            
            UIManager.setLookAndFeel(systemLAF);
            System.out.println("Using Look and Feel: " + systemLAF);
            
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Fallback to cross-platform LAF
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                System.out.println("Fallback to cross-platform Look and Feel");
            } catch (Exception fallbackException) {
                System.err.println("Could not set fallback look and feel: " + fallbackException.getMessage());
            }
        }
    }
    
    /**
     * Setup Windows-specific UI configurations
     */
    private static void setupWindowsUI() {
        // Windows-specific UI Manager properties
        UIManager.put("FileChooser.readOnly", Boolean.FALSE);
        UIManager.put("FileChooser.useSystemExtensionHiding", Boolean.TRUE);
        
        // Better Windows integration
        UIManager.put("Application.useSystemMenuBar", Boolean.TRUE);
        
        // Windows font smoothing
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        UIManager.put("swing.useSystemFontSettings", Boolean.TRUE);
        
        // Windows-specific colors and borders
        UIManager.put("control", new Color(240, 240, 240));
        UIManager.put("controlHighlight", new Color(255, 255, 255));
        UIManager.put("controlShadow", new Color(160, 160, 160));
    }
    
    /**
     * Show error dialog with better Windows compatibility
     */
    private static void showErrorDialog(String message, String title) {
        try {
            // Use system-specific dialog if possible
            if (isWindows() && Desktop.isDesktopSupported()) {
                // Try to use Windows native dialog style
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            // Fallback to console output
            System.err.println(title + ": " + message);
        }
    }
}