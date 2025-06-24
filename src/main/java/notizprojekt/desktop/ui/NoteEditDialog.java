package notizprojekt.desktop.ui;

import notizprojekt.desktop.model.DesktopNote;
import notizprojekt.desktop.model.DesktopUser;
import notizprojekt.desktop.service.NoteService;
import notizprojekt.desktop.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 * Dialog for creating and editing notes
 * Replicates the functionality of the web application's note modal
 */
public class NoteEditDialog extends JDialog implements ThemeManager.ThemeChangeListener {
    
    private final DesktopUser currentUser;
    private final NoteService noteService;
    private DesktopNote note;
    private boolean isEditMode;
    private boolean noteSaved = false;
    
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea contentArea;
    private JEditorPane richContentArea;
    private JComboBox<DesktopNote.NoteType> noteTypeCombo;
    private JComboBox<DesktopNote.PrivacyLevel> privacyCombo;
    private JTextField sharedWithField;
    private JButton colorButton;
    private JButton saveButton;
    private JButton cancelButton;
    private JToggleButton richTextToggle;
    
    private Color selectedColor = Color.decode("#FFFF88"); // Default yellow
    
    public NoteEditDialog(Frame parent, DesktopUser currentUser, DesktopNote note) {
        super(parent, note == null ? "Add New Note" : "Edit Note", true);
        
        this.currentUser = currentUser;
        this.noteService = new NoteService();
        this.note = note;
        this.isEditMode = (note != null);
        
        if (!isEditMode) {
            this.note = new DesktopNote();
            this.note.setUserId(currentUser.getId());
            // Random position for new notes
            Random rand = new Random();
            this.note.setPositionX(rand.nextInt(500));
            this.note.setPositionY(rand.nextInt(300));
        } else {
            try {
                this.selectedColor = Color.decode(note.getColor());
            } catch (NumberFormatException e) {
                this.selectedColor = Color.decode("#FFFF88");
            }
        }
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
        applyTheme();
        
        ThemeManager.addThemeChangeListener(this);
        
        setSize(600, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
    }
    
    private void initializeComponents() {
        // Form fields
        titleField = createStyledTextField();
        tagField = createStyledTextField();
        
        contentArea = new JTextArea(10, 40);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        
        richContentArea = new JEditorPane();
        richContentArea.setContentType("text/html");
        richContentArea.setEditorKit(new HTMLEditorKit());
        richContentArea.setFont(new Font("Inter", Font.PLAIN, 14));
        
        noteTypeCombo = new JComboBox<>(DesktopNote.NoteType.values());
        privacyCombo = new JComboBox<>(DesktopNote.PrivacyLevel.values());
        
        sharedWithField = createStyledTextField();
        
        colorButton = new JButton("Color");
        colorButton.setPreferredSize(new Dimension(80, 32));
        colorButton.setBackground(selectedColor);
        
        richTextToggle = new JToggleButton("Rich Text");
        richTextToggle.setFont(new Font("Inter", Font.PLAIN, 12));
        
        saveButton = createStyledButton("Save", ThemeManager.getPrimaryColor());
        cancelButton = createStyledButton("Cancel", ThemeManager.getBorderColor().darker());
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Title
        formPanel.add(createFieldPanel("Title", titleField));
        formPanel.add(Box.createVerticalStrut(12));
        
        // Tag
        formPanel.add(createFieldPanel("Tag", tagField));
        formPanel.add(Box.createVerticalStrut(12));
        
        // Note type and privacy
        JPanel typePrivacyPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        typePrivacyPanel.setOpaque(false);
        typePrivacyPanel.add(createFieldPanel("Type", noteTypeCombo));
        typePrivacyPanel.add(createFieldPanel("Privacy", privacyCombo));
        formPanel.add(typePrivacyPanel);
        formPanel.add(Box.createVerticalStrut(12));
        
        // Shared with (conditional)
        formPanel.add(createFieldPanel("Shared with (usernames, comma-separated)", sharedWithField));
        formPanel.add(Box.createVerticalStrut(12));
        
        // Color and rich text toggle
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optionsPanel.setOpaque(false);
        optionsPanel.add(new JLabel("Color: "));
        optionsPanel.add(colorButton);
        optionsPanel.add(Box.createHorizontalStrut(20));
        optionsPanel.add(richTextToggle);
        formPanel.add(optionsPanel);
        formPanel.add(Box.createVerticalStrut(12));
        
        // Content area
        JLabel contentLabel = new JLabel("Content");
        contentLabel.setFont(new Font("Inter", Font.MEDIUM, 14));
        formPanel.add(contentLabel);
        formPanel.add(Box.createVerticalStrut(8));
        
        // Tabbed content area
        JTabbedPane contentTabs = new JTabbedPane();
        contentTabs.addTab("Plain Text", new JScrollPane(contentArea));
        contentTabs.addTab("Rich Text", new JScrollPane(richContentArea));
        contentTabs.setPreferredSize(new Dimension(0, 300));
        
        formPanel.add(contentTabs);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void setupEventHandlers() {
        saveButton.addActionListener(e -> saveNote());
        cancelButton.addActionListener(e -> dispose());
        
        colorButton.addActionListener(e -> chooseColor());
        
        noteTypeCombo.addActionListener(e -> updateUIForNoteType());
        privacyCombo.addActionListener(e -> updateUIForPrivacy());
        
        richTextToggle.addActionListener(e -> toggleRichText());
    }
    
    private void populateFields() {
        if (isEditMode) {
            titleField.setText(note.getTitle());
            tagField.setText(note.getTag());
            contentArea.setText(note.getContent());
            richContentArea.setText(note.getContent());
            noteTypeCombo.setSelectedItem(note.getNoteType());
            privacyCombo.setSelectedItem(note.getPrivacyLevel());
            sharedWithField.setText(note.getSharedWith() != null ? note.getSharedWith() : "");
        }
        
        updateUIForNoteType();
        updateUIForPrivacy();
    }
    
    private void saveNote() {
        // Validation
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title is required", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update note object
        note.setTitle(title);
        note.setTag(tagField.getText().trim());
        
        String content = richTextToggle.isSelected() ? richContentArea.getText() : contentArea.getText();
        note.setContent(content);
        
        note.setNoteType((DesktopNote.NoteType) noteTypeCombo.getSelectedItem());
        note.setPrivacyLevel((DesktopNote.PrivacyLevel) privacyCombo.getSelectedItem());
        note.setSharedWith(sharedWithField.getText().trim());
        note.setColor(String.format("#%06X", selectedColor.getRGB() & 0xFFFFFF));
        
        // Save to database
        SwingWorker<Boolean, Void> saveWorker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                if (isEditMode) {
                    return noteService.updateNote(note);
                } else {
                    DesktopNote savedNote = noteService.createNote(note);
                    return savedNote != null;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        noteSaved = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(NoteEditDialog.this,
                            "Failed to save note", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(NoteEditDialog.this,
                        "Error saving note: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        saveWorker.execute();
    }
    
    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Choose Note Color", selectedColor);
        if (newColor != null) {
            selectedColor = newColor;
            colorButton.setBackground(selectedColor);
        }
    }
    
    private void updateUIForNoteType() {
        DesktopNote.NoteType selectedType = (DesktopNote.NoteType) noteTypeCombo.getSelectedItem();
        // Could add specific UI changes based on note type
    }
    
    private void updateUIForPrivacy() {
        DesktopNote.PrivacyLevel selectedPrivacy = (DesktopNote.PrivacyLevel) privacyCombo.getSelectedItem();
        sharedWithField.setEnabled(selectedPrivacy == DesktopNote.PrivacyLevel.some_people);
    }
    
    private void toggleRichText() {
        // Sync content between plain and rich text areas
        if (richTextToggle.isSelected()) {
            richContentArea.setText(contentArea.getText());
        } else {
            contentArea.setText(richContentArea.getText().replaceAll("<[^>]*>", ""));
        }
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
    
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(100, 36));
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
        
        // Update all components with theme colors
        titleField.setBackground(ThemeManager.getInputBackground());
        titleField.setForeground(ThemeManager.getTextColor());
        tagField.setBackground(ThemeManager.getInputBackground());
        tagField.setForeground(ThemeManager.getTextColor());
        sharedWithField.setBackground(ThemeManager.getInputBackground());
        sharedWithField.setForeground(ThemeManager.getTextColor());
        
        contentArea.setBackground(ThemeManager.getInputBackground());
        contentArea.setForeground(ThemeManager.getTextColor());
        richContentArea.setBackground(ThemeManager.getInputBackground());
        richContentArea.setForeground(ThemeManager.getTextColor());
        
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
    
    public boolean isNoteSaved() {
        return noteSaved;
    }
}
