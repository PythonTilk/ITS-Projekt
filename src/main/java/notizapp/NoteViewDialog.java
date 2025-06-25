package notizapp;

import notizapp.DesktopNote;
import notizapp.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Dialog for viewing read-only notes (shared notes)
 */
public class NoteViewDialog extends JDialog implements ThemeManager.ThemeChangeListener {
    
    private final DesktopNote note;
    
    private JLabel titleLabel;
    private JLabel tagLabel;
    private JTextArea contentArea;
    private JLabel metadataLabel;
    private JButton closeButton;
    
    public NoteViewDialog(Frame parent, DesktopNote note) {
        super(parent, "View Note", true);
        
        this.note = note;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
        applyTheme();
        
        ThemeManager.addThemeChangeListener(this);
        
        setSize(500, 600);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    private void initializeComponents() {
        titleLabel = new JLabel(note.getTitle());
        titleLabel.setFont(new Font("Inter", Font.BOLD, 20));
        
        if (note.getTag() != null && !note.getTag().trim().isEmpty()) {
            tagLabel = new JLabel(note.getTag());
            tagLabel.setFont(new Font("Inter", Font.PLAIN, 12));
            tagLabel.setOpaque(true);
            tagLabel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            tagLabel.setBackground(ThemeManager.getAccentColor());
            tagLabel.setForeground(Color.WHITE);
        }
        
        contentArea = new JTextArea(note.getContent());
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setEditable(false);
        contentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        contentArea.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        // Metadata
        StringBuilder metadata = new StringBuilder();
        metadata.append("Type: ").append(note.getNoteType().name()).append(" | ");
        metadata.append("Privacy: ").append(note.getPrivacyLevel().getValue());
        if (note.getSharedWith() != null && !note.getSharedWith().trim().isEmpty()) {
            metadata.append(" | Shared with: ").append(note.getSharedWith());
        }
        
        metadataLabel = new JLabel(metadata.toString());
        metadataLabel.setFont(new Font("Inter", Font.PLAIN, 11));
        metadataLabel.setForeground(Color.GRAY);
        
        closeButton = new JButton("Close");
        closeButton.setPreferredSize(new Dimension(100, 36));
        closeButton.setFont(new Font("Inter", Font.PLAIN, 14));
        closeButton.setBackground(ThemeManager.getBorderColor().darker());
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        if (tagLabel != null) {
            JPanel tagPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            tagPanel.setOpaque(false);
            tagPanel.add(tagLabel);
            headerPanel.add(tagPanel, BorderLayout.EAST);
        }
        
        headerPanel.setBorder(new EmptyBorder(0, 0, 16, 0));
        
        // Content
        JScrollPane scrollPane = new JScrollPane(contentArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1));
        scrollPane.setPreferredSize(new Dimension(0, 400));
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        
        footerPanel.add(metadataLabel, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(closeButton);
        footerPanel.add(buttonPanel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        closeButton.addActionListener(e -> dispose());
    }
    
    private void populateFields() {
        // Parse and apply note color as background
        try {
            Color noteColor = Color.decode(note.getColor());
            contentArea.setBackground(noteColor);
        } catch (NumberFormatException e) {
            contentArea.setBackground(ThemeManager.getNoteBackground());
        }
    }
    
    private void applyTheme() {
        getContentPane().setBackground(ThemeManager.getCardBackground());
        titleLabel.setForeground(ThemeManager.getTextColor());
        contentArea.setForeground(ThemeManager.getTextColor());
        
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
}