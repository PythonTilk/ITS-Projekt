package notizprojekt.desktop.util;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

/**
 * Manages application themes (light/dark mode) to match the web application's theming
 */
public class ThemeManager {
    
    private static final String THEME_PREFERENCE_KEY = "theme";
    private static final String LIGHT_THEME = "light";
    private static final String DARK_THEME = "dark";
    
    private static boolean isDarkMode = false;
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    
    // Light theme colors (matching CSS variables)
    public static final Color LIGHT_BG_COLOR = new Color(248, 250, 252);
    public static final Color LIGHT_TEXT_COLOR = new Color(30, 41, 59);
    public static final Color LIGHT_CARD_BG = Color.WHITE;
    public static final Color LIGHT_BORDER_COLOR = new Color(226, 232, 240);
    public static final Color LIGHT_PRIMARY_COLOR = new Color(59, 130, 246);
    public static final Color LIGHT_SECONDARY_COLOR = new Color(139, 92, 246);
    public static final Color LIGHT_ACCENT_COLOR = new Color(6, 182, 212);
    public static final Color LIGHT_SUCCESS_COLOR = new Color(16, 185, 129);
    public static final Color LIGHT_WARNING_COLOR = new Color(245, 158, 11);
    public static final Color LIGHT_DANGER_COLOR = new Color(239, 68, 68);
    public static final Color LIGHT_NOTE_BG = new Color(254, 243, 199);
    public static final Color LIGHT_INPUT_BG = Color.WHITE;
    public static final Color LIGHT_BUTTON_HOVER = new Color(241, 245, 249);
    
    // Dark theme colors (matching CSS variables)
    public static final Color DARK_BG_COLOR = new Color(15, 23, 42);
    public static final Color DARK_TEXT_COLOR = new Color(248, 250, 252);
    public static final Color DARK_CARD_BG = new Color(30, 41, 59);
    public static final Color DARK_BORDER_COLOR = new Color(71, 85, 105);
    public static final Color DARK_PRIMARY_COLOR = new Color(96, 165, 250);
    public static final Color DARK_SECONDARY_COLOR = new Color(167, 139, 250);
    public static final Color DARK_ACCENT_COLOR = new Color(34, 211, 238);
    public static final Color DARK_SUCCESS_COLOR = new Color(52, 211, 153);
    public static final Color DARK_WARNING_COLOR = new Color(251, 191, 36);
    public static final Color DARK_DANGER_COLOR = new Color(248, 113, 113);
    public static final Color DARK_NOTE_BG = new Color(71, 85, 105);
    public static final Color DARK_INPUT_BG = new Color(51, 65, 85);
    public static final Color DARK_BUTTON_HOVER = new Color(51, 65, 85);
    
    public interface ThemeChangeListener {
        void onThemeChanged(boolean isDarkMode);
    }
    
    public static void initialize() {
        // Load saved theme preference
        String savedTheme = prefs.get(THEME_PREFERENCE_KEY, LIGHT_THEME);
        isDarkMode = DARK_THEME.equals(savedTheme);
        applyTheme();
    }
    
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
        saveThemePreference();
        applyTheme();
        notifyListeners();
    }
    
    public static void setDarkMode(boolean darkMode) {
        if (isDarkMode != darkMode) {
            isDarkMode = darkMode;
            saveThemePreference();
            applyTheme();
            notifyListeners();
        }
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }
    
    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }
    
    private static void saveThemePreference() {
        prefs.put(THEME_PREFERENCE_KEY, isDarkMode ? DARK_THEME : LIGHT_THEME);
    }
    
    private static void applyTheme() {
        if (isDarkMode) {
            applyDarkTheme();
        } else {
            applyLightTheme();
        }
    }
    
    private static void applyLightTheme() {
        UIManager.put("Panel.background", LIGHT_BG_COLOR);
        UIManager.put("Button.background", LIGHT_CARD_BG);
        UIManager.put("Button.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("Button.select", LIGHT_BUTTON_HOVER);
        UIManager.put("TextField.background", LIGHT_INPUT_BG);
        UIManager.put("TextField.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("TextArea.background", LIGHT_INPUT_BG);
        UIManager.put("TextArea.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("Label.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("Menu.background", LIGHT_CARD_BG);
        UIManager.put("Menu.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("MenuItem.background", LIGHT_CARD_BG);
        UIManager.put("MenuItem.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("MenuBar.background", LIGHT_CARD_BG);
        UIManager.put("ScrollPane.background", LIGHT_BG_COLOR);
        UIManager.put("Viewport.background", LIGHT_BG_COLOR);
        UIManager.put("List.background", LIGHT_INPUT_BG);
        UIManager.put("List.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("Tree.background", LIGHT_INPUT_BG);
        UIManager.put("Tree.foreground", LIGHT_TEXT_COLOR);
        UIManager.put("TabbedPane.background", LIGHT_BG_COLOR);
        UIManager.put("TabbedPane.foreground", LIGHT_TEXT_COLOR);
    }
    
    private static void applyDarkTheme() {
        UIManager.put("Panel.background", DARK_BG_COLOR);
        UIManager.put("Button.background", DARK_CARD_BG);
        UIManager.put("Button.foreground", DARK_TEXT_COLOR);
        UIManager.put("Button.select", DARK_BUTTON_HOVER);
        UIManager.put("TextField.background", DARK_INPUT_BG);
        UIManager.put("TextField.foreground", DARK_TEXT_COLOR);
        UIManager.put("TextArea.background", DARK_INPUT_BG);
        UIManager.put("TextArea.foreground", DARK_TEXT_COLOR);
        UIManager.put("Label.foreground", DARK_TEXT_COLOR);
        UIManager.put("Menu.background", DARK_CARD_BG);
        UIManager.put("Menu.foreground", DARK_TEXT_COLOR);
        UIManager.put("MenuItem.background", DARK_CARD_BG);
        UIManager.put("MenuItem.foreground", DARK_TEXT_COLOR);
        UIManager.put("MenuBar.background", DARK_CARD_BG);
        UIManager.put("ScrollPane.background", DARK_BG_COLOR);
        UIManager.put("Viewport.background", DARK_BG_COLOR);
        UIManager.put("List.background", DARK_INPUT_BG);
        UIManager.put("List.foreground", DARK_TEXT_COLOR);
        UIManager.put("Tree.background", DARK_INPUT_BG);
        UIManager.put("Tree.foreground", DARK_TEXT_COLOR);
        UIManager.put("TabbedPane.background", DARK_BG_COLOR);
        UIManager.put("TabbedPane.foreground", DARK_TEXT_COLOR);
    }
    
    private static void notifyListeners() {
        for (ThemeChangeListener listener : listeners) {
            listener.onThemeChanged(isDarkMode);
        }
    }
    
    // Utility methods for getting current theme colors
    public static Color getBackgroundColor() {
        return isDarkMode ? DARK_BG_COLOR : LIGHT_BG_COLOR;
    }
    
    public static Color getTextColor() {
        return isDarkMode ? DARK_TEXT_COLOR : LIGHT_TEXT_COLOR;
    }
    
    public static Color getCardBackground() {
        return isDarkMode ? DARK_CARD_BG : LIGHT_CARD_BG;
    }
    
    public static Color getBorderColor() {
        return isDarkMode ? DARK_BORDER_COLOR : LIGHT_BORDER_COLOR;
    }
    
    public static Color getPrimaryColor() {
        return isDarkMode ? DARK_PRIMARY_COLOR : LIGHT_PRIMARY_COLOR;
    }
    
    public static Color getSecondaryColor() {
        return isDarkMode ? DARK_SECONDARY_COLOR : LIGHT_SECONDARY_COLOR;
    }
    
    public static Color getAccentColor() {
        return isDarkMode ? DARK_ACCENT_COLOR : LIGHT_ACCENT_COLOR;
    }
    
    public static Color getSuccessColor() {
        return isDarkMode ? DARK_SUCCESS_COLOR : LIGHT_SUCCESS_COLOR;
    }
    
    public static Color getWarningColor() {
        return isDarkMode ? DARK_WARNING_COLOR : LIGHT_WARNING_COLOR;
    }
    
    public static Color getDangerColor() {
        return isDarkMode ? DARK_DANGER_COLOR : LIGHT_DANGER_COLOR;
    }
    
    public static Color getNoteBackground() {
        return isDarkMode ? DARK_NOTE_BG : LIGHT_NOTE_BG;
    }
    
    public static Color getInputBackground() {
        return isDarkMode ? DARK_INPUT_BG : LIGHT_INPUT_BG;
    }
    
    public static Color getButtonHoverColor() {
        return isDarkMode ? DARK_BUTTON_HOVER : LIGHT_BUTTON_HOVER;
    }
}