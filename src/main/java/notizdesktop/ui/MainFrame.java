package notizdesktop.ui;

import notizdesktop.model.DesktopNote;
import notizdesktop.model.DesktopUser;
import notizdesktop.service.NoteService;
import notizdesktop.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * Main application window with note board
 * Replicates the functionality of the web application's board.html
 */
public class MainFrame extends JFrame implements ThemeManager.ThemeChangeListener {
    
    private final DesktopUser currentUser;
    private final NoteService noteService = new NoteService();
    
    private JPanel headerPanel;
    private JPanel mainPanel;
    private NoteBoardPanel noteBoardPanel;
    private JTextField searchField;
    private JButton addNoteButton;
    private JButton profileButton;
    private JButton logoutButton;
    private JButton themeToggleButton;
    private JLabel userLabel;
    private JLabel titleLabel;
    
    public MainFrame(DesktopUser user) {
        this.currentUser = user;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadNotes();
        applyTheme();
        
        ThemeManager.addThemeChangeListener(this);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setTitle("Notiz Projekt - " + user.getDisplayNameOrUsername());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1000, 700));
    }
    
    private void initializeComponents() {
        // Header components
        titleLabel = new JLabel("Notiz Projekt");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        
        searchField = createStyledSearchField();
        
        userLabel = new JLabel(currentUser.getDisplayNameOrUsername());
        userLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        
        addNoteButton = createFloatingActionButton();
        profileButton = createHeaderButton("Profile");
        logoutButton = createHeaderButton("Logout");
        themeToggleButton = createThemeToggleButton();
        
        // Main components
        noteBoardPanel = new NoteBoardPanel(currentUser, noteService);
        
        // Panels
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(16, 24, 16, 24));
        headerPanel.setPreferredSize(new Dimension(0, 80));
        
        mainPanel = new JPanel(new BorderLayout());
    }
    
    private void setupLayout() {
        // Left side of header - title
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftHeaderPanel.setOpaque(false);
        leftHeaderPanel.add(titleLabel);
        
        // Center of header - search
        JPanel centerHeaderPanel = new JPanel(new BorderLayout());
        centerHeaderPanel.setOpaque(false);
        centerHeaderPanel.setBorder(new EmptyBorder(0, 40, 0, 40));
        centerHeaderPanel.add(searchField, BorderLayout.CENTER);
        
        // Right side of header - user menu
        JPanel rightHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        rightHeaderPanel.setOpaque(false);
        
        // User avatar (simple circle with initials)
        JLabel avatarLabel = createUserAvatar();
        rightHeaderPanel.add(avatarLabel);
        rightHeaderPanel.add(userLabel);
        rightHeaderPanel.add(profileButton);
        rightHeaderPanel.add(logoutButton);
        rightHeaderPanel.add(themeToggleButton);
        
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);
        headerPanel.add(centerHeaderPanel, BorderLayout.CENTER);
        headerPanel.add(rightHeaderPanel, BorderLayout.EAST);
        
        // Main content area
        JScrollPane scrollPane = new JScrollPane(noteBoardPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Floating add button
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        layeredPane.add(mainPanel, BorderLayout.CENTER);
        
        // Position floating button
        addNoteButton.setBounds(0, 0, 60, 60);
        layeredPane.add(addNoteButton, JLayeredPane.PALETTE_LAYER);
        
        // Layout main frame
        setLayout(new BorderLayout());
        add(headerPanel, BorderLayout.NORTH);
        add(layeredPane, BorderLayout.CENTER);
    }
    
    private void setupEventHandlers() {
        addNoteButton.addActionListener(e -> showNoteDialog(null));
        profileButton.addActionListener(e -> showProfileDialog());
        logoutButton.addActionListener(e -> performLogout());
        themeToggleButton.addActionListener(e -> ThemeManager.toggleTheme());
        
        // Search functionality
        searchField.addActionListener(e -> performSearch());
        
        // Window closing
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                performLogout();
            }
        });
        
        // Position floating button when window resizes
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                positionFloatingButton();
            }
        });
    }
    
    private void loadNotes() {
        SwingWorker<List<DesktopNote>, Void> worker = new SwingWorker<List<DesktopNote>, Void>() {
            @Override
            protected List<DesktopNote> doInBackground() throws Exception {
                return noteService.getAllNotesForUser(currentUser.getId());
            }
            
            @Override
            protected void done() {
                try {
                    List<DesktopNote> notes = get();
                    noteBoardPanel.setNotes(notes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Failed to load notes: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void performSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadNotes(); // Show all notes
            return;
        }
        
        SwingWorker<List<DesktopNote>, Void> worker = new SwingWorker<List<DesktopNote>, Void>() {
            @Override
            protected List<DesktopNote> doInBackground() throws Exception {
                return noteService.searchNotes(currentUser.getId(), searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<DesktopNote> notes = get();
                    noteBoardPanel.setNotes(notes);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(MainFrame.this,
                        "Search failed: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void showNoteDialog(DesktopNote note) {
        NoteEditDialog dialog = new NoteEditDialog(this, currentUser, note);
        dialog.setVisible(true);
        
        if (dialog.isNoteSaved()) {
            loadNotes(); // Refresh the board
        }
    }
    
    private void showProfileDialog() {
        ProfileDialog dialog = new ProfileDialog(this, currentUser);
        dialog.setVisible(true);
    }
    
    private void performLogout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout", JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    private void positionFloatingButton() {
        if (addNoteButton != null) {
            Container parent = addNoteButton.getParent();
            if (parent != null) {
                int x = parent.getWidth() - 80;
                int y = parent.getHeight() - 80;
                addNoteButton.setBounds(x, y, 60, 60);
            }
        }
    }
    
    private JTextField createStyledSearchField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(300, 40));
        field.setFont(new Font("Inter", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Add placeholder text effect
        field.setText("Search notes...");
        field.setForeground(Color.GRAY);
        
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (field.getText().equals("Search notes...")) {
                    field.setText("");
                    field.setForeground(ThemeManager.getTextColor());
                }
            }
            
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText("Search notes...");
                    field.setForeground(Color.GRAY);
                }
            }
        });
        
        return field;
    }
    
    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.PLAIN, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private JButton createFloatingActionButton() {
        JButton button = new JButton("+");
        button.setFont(new Font("Inter", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(60, 60));
        button.setBorder(null);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Add new note");
        
        // Make it circular
        button.setContentAreaFilled(false);
        button.setOpaque(true);
        
        return button;
    }
    
    private JButton createThemeToggleButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(36, 36));
        button.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText("Toggle dark mode");
        
        updateThemeToggleIcon(button);
        return button;
    }
    
    private void updateThemeToggleIcon(JButton button) {
        String iconText = ThemeManager.isDarkMode() ? "☀" : "🌙";
        button.setText(iconText);
        button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
    }
    
    private JLabel createUserAvatar() {
        JLabel avatar = new JLabel(currentUser.getInitials(), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setFont(new Font("Inter", Font.BOLD, 12));
        avatar.setOpaque(true);
        avatar.setBorder(BorderFactory.createEmptyBorder());
        
        // Make it circular (approximation)
        avatar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        
        return avatar;
    }
    
    private void applyTheme() {
        // Header
        headerPanel.setBackground(ThemeManager.getCardBackground());
        titleLabel.setForeground(ThemeManager.getTextColor());
        userLabel.setForeground(ThemeManager.getTextColor());
        
        // Search field
        searchField.setBackground(ThemeManager.getInputBackground());
        if (!searchField.getText().equals("Search notes...")) {
            searchField.setForeground(ThemeManager.getTextColor());
        }
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // Buttons
        profileButton.setBackground(ThemeManager.getCardBackground());
        profileButton.setForeground(ThemeManager.getTextColor());
        logoutButton.setBackground(ThemeManager.getCardBackground());
        logoutButton.setForeground(ThemeManager.getTextColor());
        
        // Floating button
        addNoteButton.setBackground(ThemeManager.getPrimaryColor());
        addNoteButton.setForeground(Color.WHITE);
        
        // Theme toggle
        themeToggleButton.setBackground(ThemeManager.getCardBackground());
        themeToggleButton.setForeground(ThemeManager.getTextColor());
        updateThemeToggleIcon(themeToggleButton);
        
        // Main panel
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
    
    // Public method to refresh notes (called from dialogs)
    public void refreshNotes() {
        loadNotes();
    }
}