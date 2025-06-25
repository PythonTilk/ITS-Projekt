package notizapp;

import notizapp.DesktopNote;
import notizapp.DesktopUser;
import notizapp.NoteService;
import notizapp.ThemeManager;

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
        
        // Create a panel to hold everything
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        // Create a panel for the floating button with null layout
        JPanel buttonPanel = new JPanel(null);
        buttonPanel.setOpaque(false);
        
        // Add the button to the panel
        addNoteButton.setBounds(0, 0, 60, 60);
        buttonPanel.add(addNoteButton);
        
        // Create a layered pane with absolute positioning
        JLayeredPane layeredPane = new JLayeredPane() {
            @Override
            public void doLayout() {
                // Make all components take the full size of the layered pane
                for (Component c : getComponents()) {
                    c.setBounds(0, 0, getWidth(), getHeight());
                }
            }
        };
        
        // Add both panels to the layered pane
        layeredPane.add(contentPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(buttonPanel, JLayeredPane.PALETTE_LAYER);
        
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
                Container grandparent = parent.getParent();
                if (grandparent != null) {
                    int x = grandparent.getWidth() - 80;
                    int y = grandparent.getHeight() - 80;
                    addNoteButton.setBounds(x, y, 60, 60);
                    parent.revalidate();
                    parent.repaint();
                }
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
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE, 2),
            BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Make sure button shows its background color
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set initial colors
        button.setBackground(ThemeManager.isDarkMode() ? 
            ThemeManager.DARK_PRIMARY_COLOR : ThemeManager.LIGHT_PRIMARY_COLOR);
        button.setForeground(Color.WHITE);
        
        return button;
    }
    
    private JButton createFloatingActionButton() {
        JButton button = new JButton("Add Note");
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        button.setToolTipText("Add new note");
        
        // Make sure background is visible
        button.setContentAreaFilled(true);
        button.setOpaque(true);
        
        // Set colors - WHITE with BLACK text
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        
        return button;
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
        
        // Set colors - WHITE with BLACK text
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
    
    private JLabel createUserAvatar() {
        JLabel avatar = new JLabel(currentUser.getInitials(), SwingConstants.CENTER);
        avatar.setPreferredSize(new Dimension(30, 30));
        avatar.setFont(new Font("Arial", Font.BOLD, 14));
        avatar.setOpaque(true);
        
        // Simple border
        avatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Set colors - standard blue
        avatar.setBackground(new Color(0, 102, 204));
        avatar.setForeground(Color.WHITE);
        
        return avatar;
    }
    
    private void applyTheme() {
        // Header
        headerPanel.setBackground(ThemeManager.getCardBackground());
        titleLabel.setForeground(ThemeManager.getTextColor());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        userLabel.setForeground(ThemeManager.getTextColor());
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Search field
        searchField.setBackground(ThemeManager.getInputBackground());
        if (!searchField.getText().equals("Search notes...")) {
            searchField.setForeground(ThemeManager.getTextColor());
        }
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.BLACK, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        // WHITE buttons with BLACK text
        Color buttonBg = Color.WHITE;
        Color textColor = Color.BLACK;
        
        // Make sure buttons show their background color
        profileButton.setContentAreaFilled(true);
        profileButton.setOpaque(true);
        logoutButton.setContentAreaFilled(true);
        logoutButton.setOpaque(true);
        themeToggleButton.setContentAreaFilled(true);
        themeToggleButton.setOpaque(true);
        addNoteButton.setContentAreaFilled(true);
        addNoteButton.setOpaque(true);
        
        // Apply consistent styling to all buttons
        profileButton.setBackground(buttonBg);
        profileButton.setForeground(textColor);
        profileButton.setFont(new Font("Arial", Font.BOLD, 14));
        profileButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        logoutButton.setBackground(buttonBg);
        logoutButton.setForeground(textColor);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Floating button
        addNoteButton.setBackground(buttonBg);
        addNoteButton.setForeground(textColor);
        addNoteButton.setFont(new Font("Arial", Font.BOLD, 14));
        addNoteButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        // Theme toggle
        themeToggleButton.setBackground(buttonBg);
        themeToggleButton.setForeground(textColor);
        themeToggleButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        updateThemeToggleIcon(themeToggleButton);
        
        // User avatar - keep this blue with white text for contrast
        JLabel avatar = (JLabel) ((JPanel) headerPanel.getComponent(2)).getComponent(0);
        avatar.setBackground(new Color(0, 102, 204));
        avatar.setForeground(Color.WHITE);
        avatar.setFont(new Font("Arial", Font.BOLD, 14));
        avatar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        // Main panel
        mainPanel.setBackground(ThemeManager.getBackgroundColor());
        
        // Update note board panel
        if (noteBoardPanel != null) {
            noteBoardPanel.setBackground(ThemeManager.getBackgroundColor());
            noteBoardPanel.repaint();
        }
        
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