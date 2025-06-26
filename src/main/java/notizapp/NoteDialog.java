package notizapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Combined dialog for creating, editing, and viewing notes
 * Supports both edit mode and view-only mode
 */
public class NoteDialog extends JDialog implements ThemeManager.ThemeChangeListener {
    
    private final User currentUser;
    private final Note note;
    private boolean isEditMode;
    private boolean noteSaved = false;
    
    // UI Components
    private JPanel mainPanel;
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea contentArea;
    private JEditorPane richContentPane;
    private JComboBox<String> noteTypeCombo;
    private JComboBox<String> privacyCombo;
    private JTextField sharedWithField;
    private JComboBox<String> editingPermissionCombo;
    private JPanel colorPanel;
    private JButton colorButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JButton closeButton;
    private JLabel titleLabel;
    private JLabel tagLabel;
    private JLabel metadataLabel;
    
    // Constants
    private static final String[] NOTE_COLORS = {
        "#FFFF88", "#FFB6C1", "#ADD8E6", "#90EE90", "#D8BFD8", "#F0E68C", "#E6E6FA", "#FFA07A"
    };
    
    /**
     * Constructor for edit mode (create/edit)
     */
    public NoteDialog(Frame parent, Note note, User currentUser) {
        super(parent, note.getId() == null ? "Create Note" : "Edit Note", true);
        
        this.note = note;
        this.currentUser = currentUser;
        this.isEditMode = true;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadNoteData();
        
        ThemeManager.getInstance().addThemeChangeListener(this);
        applyTheme();
        
        setSize(600, 500);
        setLocationRelativeTo(parent);
    }
    
    /**
     * Constructor for view-only mode
     */
    public NoteDialog(Frame parent, Note note) {
        super(parent, "View Note", true);
        
        this.note = note;
        this.currentUser = null;
        this.isEditMode = false;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        loadNoteData();
        
        ThemeManager.getInstance().addThemeChangeListener(this);
        applyTheme();
        
        setSize(500, 400);
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        mainPanel = new JPanel();
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        if (isEditMode) {
            // Edit mode components
            titleField = new JTextField(20);
            tagField = new JTextField(20);
            
            noteTypeCombo = new JComboBox<>(new String[]{"Text", "Code", "Rich Text"});
            privacyCombo = new JComboBox<>(new String[]{"Private", "Shared", "Public"});
            sharedWithField = new JTextField(20);
            editingPermissionCombo = new JComboBox<>(new String[]{"Creator Only", "Collaborative"});
            
            contentArea = new JTextArea(10, 20);
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            
            richContentPane = new JEditorPane();
            richContentPane.setEditorKit(new HTMLEditorKit());
            richContentPane.setContentType("text/html");
            
            colorPanel = new JPanel();
            colorPanel.setPreferredSize(new Dimension(30, 30));
            colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorPanel.setBackground(Color.decode(note.getColor() != null ? note.getColor() : "#FFFF88"));
            
            colorButton = new JButton("Change Color");
            saveButton = new JButton("Save");
            cancelButton = new JButton("Cancel");
        } else {
            // View-only mode components
            titleLabel = new JLabel(note.getTitle());
            titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 18));
            
            tagLabel = new JLabel(note.getTag() != null && !note.getTag().isEmpty() ? "#" + note.getTag() : "");
            tagLabel.setForeground(new Color(100, 100, 100));
            
            if (note.getNoteType() == Note.NoteType.rich) {
                richContentPane = new JEditorPane();
                richContentPane.setEditorKit(new HTMLEditorKit());
                richContentPane.setContentType("text/html");
                richContentPane.setText(note.getContent());
                richContentPane.setEditable(false);
            } else {
                contentArea = new JTextArea(note.getContent());
                contentArea.setLineWrap(true);
                contentArea.setWrapStyleWord(true);
                contentArea.setEditable(false);
            }
            
            String createdBy = "Created by: " + (note.getUserId() != null ? "User #" + note.getUserId() : "Unknown");
            metadataLabel = new JLabel(createdBy);
            
            closeButton = new JButton("Close");
        }
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        if (isEditMode) {
            // Edit mode layout
            mainPanel.setLayout(new BorderLayout());
            
            // Header panel (title, tag, type)
            JPanel headerPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            headerPanel.add(new JLabel("Title:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            headerPanel.add(titleField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            headerPanel.add(new JLabel("Tag:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            headerPanel.add(tagField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            headerPanel.add(new JLabel("Type:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            headerPanel.add(noteTypeCombo, gbc);
            
            // Content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(BorderFactory.createTitledBorder("Content"));
            
            JScrollPane contentScrollPane = new JScrollPane(contentArea);
            contentPanel.add(contentScrollPane, BorderLayout.CENTER);
            
            JScrollPane richScrollPane = new JScrollPane(richContentPane);
            
            // Settings panel (privacy, sharing, color)
            JPanel settingsPanel = new JPanel(new GridBagLayout());
            settingsPanel.setBorder(BorderFactory.createTitledBorder("Settings"));
            
            gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0;
            gbc.gridy = 0;
            settingsPanel.add(new JLabel("Privacy:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            settingsPanel.add(privacyCombo, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            settingsPanel.add(new JLabel("Share with:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 1;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            settingsPanel.add(sharedWithField, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            settingsPanel.add(new JLabel("Editing:"), gbc);
            
            gbc.gridx = 1;
            gbc.gridy = 2;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            settingsPanel.add(editingPermissionCombo, gbc);
            
            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            settingsPanel.add(new JLabel("Color:"), gbc);
            
            JPanel colorSelectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            colorSelectionPanel.add(colorPanel);
            colorSelectionPanel.add(colorButton);
            
            gbc.gridx = 1;
            gbc.gridy = 3;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;
            settingsPanel.add(colorSelectionPanel, gbc);
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            
            // Add all panels to main panel
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            mainPanel.add(settingsPanel, BorderLayout.SOUTH);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
            
            // Show the appropriate content pane based on note type
            updateContentPaneVisibility();
        } else {
            // View-only mode layout
            mainPanel.setLayout(new BorderLayout());
            
            // Header panel
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.add(titleLabel, BorderLayout.NORTH);
            
            if (tagLabel.getText() != null && !tagLabel.getText().isEmpty()) {
                headerPanel.add(tagLabel, BorderLayout.CENTER);
            }
            
            headerPanel.add(metadataLabel, BorderLayout.SOUTH);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            
            // Content panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            
            if (note.getNoteType() == Note.NoteType.rich) {
                JScrollPane scrollPane = new JScrollPane(richContentPane);
                contentPanel.add(scrollPane, BorderLayout.CENTER);
            } else {
                JScrollPane scrollPane = new JScrollPane(contentArea);
                contentPanel.add(scrollPane, BorderLayout.CENTER);
            }
            
            // Button panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(closeButton);
            
            // Add all panels to main panel
            mainPanel.add(headerPanel, BorderLayout.NORTH);
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            add(mainPanel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }
    
    private void setupEventHandlers() {
        if (isEditMode) {
            // Note type change handler
            noteTypeCombo.addActionListener(e -> updateContentPaneVisibility());
            
            // Privacy level change handler
            privacyCombo.addActionListener(e -> {
                boolean isShared = privacyCombo.getSelectedIndex() == 1; // "Shared" option
                sharedWithField.setEnabled(isShared);
                editingPermissionCombo.setEnabled(isShared || privacyCombo.getSelectedIndex() == 2); // "Shared" or "Public"
            });
            
            // Color button handler
            colorButton.addActionListener(e -> {
                String selectedColor = showColorPicker();
                if (selectedColor != null) {
                    colorPanel.setBackground(Color.decode(selectedColor));
                }
            });
            
            // Save button handler
            saveButton.addActionListener(e -> {
                if (validateInput()) {
                    saveNote();
                    noteSaved = true;
                    dispose();
                }
            });
            
            // Cancel button handler
            cancelButton.addActionListener(e -> dispose());
        } else {
            // Close button handler
            closeButton.addActionListener(e -> dispose());
        }
    }
    
    private void loadNoteData() {
        if (isEditMode) {
            // Load data into edit form
            titleField.setText(note.getTitle());
            tagField.setText(note.getTag());
            
            // Set note type
            if (note.getNoteType() != null) {
                switch (note.getNoteType()) {
                    case text:
                        noteTypeCombo.setSelectedIndex(0);
                        break;
                    case code:
                        noteTypeCombo.setSelectedIndex(1);
                        break;
                    case rich:
                        noteTypeCombo.setSelectedIndex(2);
                        break;
                }
            }
            
            // Set content based on note type
            if (note.getNoteType() == Note.NoteType.rich) {
                richContentPane.setText(note.getContent());
            } else {
                contentArea.setText(note.getContent());
            }
            
            // Set privacy level
            if (note.getPrivacyLevel() != null) {
                switch (note.getPrivacyLevel()) {
                    case private_:
                        privacyCombo.setSelectedIndex(0);
                        break;
                    case some_people:
                        privacyCombo.setSelectedIndex(1);
                        break;
                    case everyone:
                        privacyCombo.setSelectedIndex(2);
                        break;
                }
            }
            
            sharedWithField.setText(note.getSharedWith());
            sharedWithField.setEnabled(privacyCombo.getSelectedIndex() == 1); // Enable only if "Shared" is selected
            
            // Set editing permission
            if (note.getEditingPermission() != null) {
                switch (note.getEditingPermission()) {
                    case creator_only:
                        editingPermissionCombo.setSelectedIndex(0);
                        break;
                    case collaborative:
                        editingPermissionCombo.setSelectedIndex(1);
                        break;
                }
            }
            
            editingPermissionCombo.setEnabled(privacyCombo.getSelectedIndex() > 0); // Enable if not "Private"
            
            // Set color
            if (note.getColor() != null) {
                colorPanel.setBackground(Color.decode(note.getColor()));
            }
        }
    }
    
    private void updateContentPaneVisibility() {
        if (!isEditMode) return;
        
        Container parent = contentArea.getParent();
        if (parent instanceof JViewport) {
            parent = parent.getParent(); // Get the JScrollPane
            if (parent != null) {
                parent = parent.getParent(); // Get the content panel
            }
        }
        
        if (parent != null) {
            parent.removeAll();
            
            if (noteTypeCombo.getSelectedIndex() == 2) { // Rich Text
                JScrollPane richScrollPane = new JScrollPane(richContentPane);
                parent.add(richScrollPane, BorderLayout.CENTER);
            } else {
                JScrollPane contentScrollPane = new JScrollPane(contentArea);
                parent.add(contentScrollPane, BorderLayout.CENTER);
            }
            
            parent.revalidate();
            parent.repaint();
        }
    }
    
    private String showColorPicker() {
        JPanel colorPickerPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        
        for (String colorHex : NOTE_COLORS) {
            JPanel colorOption = new JPanel();
            colorOption.setBackground(Color.decode(colorHex));
            colorOption.setPreferredSize(new Dimension(30, 30));
            colorOption.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            colorOption.setToolTipText(colorHex);
            
            colorOption.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    for (Component comp : colorPickerPanel.getComponents()) {
                        comp.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    }
                    colorOption.setBorder(BorderFactory.createLineBorder(Color.BLUE, 3));
                }
            });
            
            colorPickerPanel.add(colorOption);
        }
        
        int result = JOptionPane.showConfirmDialog(this, colorPickerPanel, 
                "Choose Note Color", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            for (Component comp : colorPickerPanel.getComponents()) {
                if (comp.getBorder().getBorderInsets(comp).top == 3) {
                    return ((JPanel) comp).getToolTipText();
                }
            }
        }
        
        return null;
    }
    
    private boolean validateInput() {
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            titleField.requestFocus();
            return false;
        }
        
        if (privacyCombo.getSelectedIndex() == 1 && sharedWithField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please specify users to share with", "Validation Error", JOptionPane.ERROR_MESSAGE);
            sharedWithField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    private void saveNote() {
        // Update note with form data
        note.setTitle(titleField.getText().trim());
        note.setTag(tagField.getText().trim());
        
        // Set note type
        switch (noteTypeCombo.getSelectedIndex()) {
            case 0:
                note.setNoteType(Note.NoteType.text);
                note.setContent(contentArea.getText());
                break;
            case 1:
                note.setNoteType(Note.NoteType.code);
                note.setContent(contentArea.getText());
                break;
            case 2:
                note.setNoteType(Note.NoteType.rich);
                note.setContent(richContentPane.getText());
                break;
        }
        
        // Set privacy level
        switch (privacyCombo.getSelectedIndex()) {
            case 0:
                note.setPrivacyLevel(Note.PrivacyLevel.private_);
                note.setSharedWith("");
                break;
            case 1:
                note.setPrivacyLevel(Note.PrivacyLevel.some_people);
                note.setSharedWith(sharedWithField.getText().trim());
                break;
            case 2:
                note.setPrivacyLevel(Note.PrivacyLevel.everyone);
                break;
        }
        
        // Set editing permission
        switch (editingPermissionCombo.getSelectedIndex()) {
            case 0:
                note.setEditingPermission(Note.EditingPermission.creator_only);
                break;
            case 1:
                note.setEditingPermission(Note.EditingPermission.collaborative);
                break;
        }
        
        // Set color
        Color selectedColor = colorPanel.getBackground();
        note.setColor(String.format("#%02X%02X%02X", 
                selectedColor.getRed(), selectedColor.getGreen(), selectedColor.getBlue()));
        
        // Set user ID if creating a new note
        if (note.getUserId() == null) {
            note.setUserId(currentUser.getId());
        }
        
        // Save to database
        note.save();
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
            
            if (isEditMode) {
                titleField.setBackground(new Color(60, 60, 60));
                titleField.setForeground(Color.WHITE);
                tagField.setBackground(new Color(60, 60, 60));
                tagField.setForeground(Color.WHITE);
                contentArea.setBackground(new Color(60, 60, 60));
                contentArea.setForeground(Color.WHITE);
                richContentPane.setBackground(new Color(60, 60, 60));
                richContentPane.setForeground(Color.WHITE);
                noteTypeCombo.setBackground(new Color(60, 60, 60));
                noteTypeCombo.setForeground(Color.WHITE);
                privacyCombo.setBackground(new Color(60, 60, 60));
                privacyCombo.setForeground(Color.WHITE);
                sharedWithField.setBackground(new Color(60, 60, 60));
                sharedWithField.setForeground(Color.WHITE);
                editingPermissionCombo.setBackground(new Color(60, 60, 60));
                editingPermissionCombo.setForeground(Color.WHITE);
            } else {
                if (titleLabel != null) titleLabel.setForeground(Color.WHITE);
                if (tagLabel != null) tagLabel.setForeground(new Color(180, 180, 180));
                if (metadataLabel != null) metadataLabel.setForeground(new Color(180, 180, 180));
                if (contentArea != null) {
                    contentArea.setBackground(new Color(60, 60, 60));
                    contentArea.setForeground(Color.WHITE);
                }
                if (richContentPane != null) {
                    richContentPane.setBackground(new Color(60, 60, 60));
                    richContentPane.setForeground(Color.WHITE);
                }
            }
        } else {
            mainPanel.setBackground(new Color(240, 240, 240));
            mainPanel.setForeground(Color.BLACK);
            
            if (isEditMode) {
                titleField.setBackground(Color.WHITE);
                titleField.setForeground(Color.BLACK);
                tagField.setBackground(Color.WHITE);
                tagField.setForeground(Color.BLACK);
                contentArea.setBackground(Color.WHITE);
                contentArea.setForeground(Color.BLACK);
                richContentPane.setBackground(Color.WHITE);
                richContentPane.setForeground(Color.BLACK);
                noteTypeCombo.setBackground(Color.WHITE);
                noteTypeCombo.setForeground(Color.BLACK);
                privacyCombo.setBackground(Color.WHITE);
                privacyCombo.setForeground(Color.BLACK);
                sharedWithField.setBackground(Color.WHITE);
                sharedWithField.setForeground(Color.BLACK);
                editingPermissionCombo.setBackground(Color.WHITE);
                editingPermissionCombo.setForeground(Color.BLACK);
            } else {
                if (titleLabel != null) titleLabel.setForeground(Color.BLACK);
                if (tagLabel != null) tagLabel.setForeground(new Color(100, 100, 100));
                if (metadataLabel != null) metadataLabel.setForeground(new Color(100, 100, 100));
                if (contentArea != null) {
                    contentArea.setBackground(Color.WHITE);
                    contentArea.setForeground(Color.BLACK);
                }
                if (richContentPane != null) {
                    richContentPane.setBackground(Color.WHITE);
                    richContentPane.setForeground(Color.BLACK);
                }
            }
        }
    }
    
    /**
     * Check if the note was saved
     */
    public boolean isNoteSaved() {
        return noteSaved;
    }
    
    /**
     * Get the saved note
     */
    public Note getNote() {
        return note;
    }
}