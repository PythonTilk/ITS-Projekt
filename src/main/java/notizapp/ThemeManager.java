package notizapp;

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
    
    // Light theme colors with maximum contrast
    public static final Color LIGHT_BG_COLOR = new Color(240, 245, 250);
    public static final Color LIGHT_TEXT_COLOR = new Color(0, 0, 0);
    public static final Color LIGHT_CARD_BG = Color.WHITE;
    public static final Color LIGHT_BORDER_COLOR = new Color(180, 190, 200);
    public static final Color LIGHT_PRIMARY_COLOR = new Color(0, 90, 200);
    public static final Color LIGHT_SECONDARY_COLOR = new Color(100, 50, 200);
    public static final Color LIGHT_ACCENT_COLOR = new Color(0, 140, 180);
    public static final Color LIGHT_SUCCESS_COLOR = new Color(0, 140, 80);
    public static final Color LIGHT_WARNING_COLOR = new Color(200, 120, 0);
    public static final Color LIGHT_DANGER_COLOR = new Color(200, 30, 30);
    public static final Color LIGHT_NOTE_BG = new Color(255, 245, 200);
    public static final Color LIGHT_INPUT_BG = Color.WHITE;
    public static final Color LIGHT_BUTTON_HOVER = new Color(220, 230, 240);
    
    // Dark theme colors with improved contrast
    public static final Color DARK_BG_COLOR = new Color(20, 25, 40);
    public static final Color DARK_TEXT_COLOR = new Color(255, 255, 255);
    public static final Color DARK_CARD_BG = new Color(40, 50, 70);
    public static final Color DARK_BORDER_COLOR = new Color(100, 110, 130);
    public static final Color DARK_PRIMARY_COLOR = new Color(100, 170, 255);
    public static final Color DARK_SECONDARY_COLOR = new Color(180, 150, 255);
    public static final Color DARK_ACCENT_COLOR = new Color(50, 220, 250);
    public static final Color DARK_SUCCESS_COLOR = new Color(70, 230, 170);
    public static final Color DARK_WARNING_COLOR = new Color(255, 200, 50);
    public static final Color DARK_DANGER_COLOR = new Color(255, 120, 120);
    public static final Color DARK_NOTE_BG = new Color(90, 100, 120);
    public static final Color DARK_INPUT_BG = new Color(60, 70, 90);
    public static final Color DARK_BUTTON_HOVER = new Color(70, 80, 100);
    
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