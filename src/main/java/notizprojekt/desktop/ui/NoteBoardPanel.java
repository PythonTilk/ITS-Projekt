package notizprojekt.desktop.ui;

import notizprojekt.desktop.model.DesktopNote;
import notizprojekt.desktop.model.DesktopUser;
import notizprojekt.desktop.service.NoteService;
import notizprojekt.desktop.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel that displays notes in a drag-and-drop board layout
 * Replicates the web application's note board functionality
 */
public class NoteBoardPanel extends JPanel implements ThemeManager.ThemeChangeListener {
    
    private final DesktopUser currentUser;
    private final NoteService noteService;
    private final Map<Integer, NoteComponent> noteComponents = new HashMap<>();
    
    private List<DesktopNote> notes = new ArrayList<>();
    private NoteComponent draggedNote = null;
    private Point dragOffset = null;
    
    public NoteBoardPanel(DesktopUser currentUser, NoteService noteService) {
        this.currentUser = currentUser;
        this.noteService = noteService;
        
        setLayout(null); // Absolute positioning for drag-and-drop
        setPreferredSize(new Dimension(2000, 1500)); // Large canvas
        
        ThemeManager.addThemeChangeListener(this);
        applyTheme();
    }
    
    public void setNotes(List<DesktopNote> notes) {
        this.notes = notes;
        refreshNoteComponents();
    }
    
    private void refreshNoteComponents() {
        // Clear existing components
        removeAll();
        noteComponents.clear();
        
        // Create components for each note
        for (DesktopNote note : notes) {
            NoteComponent noteComp = new NoteComponent(note, currentUser, noteService);
            noteComponents.put(note.getId(), noteComp);
            
            // Position the note
            int x = Math.max(0, note.getPositionX());
            int y = Math.max(0, note.getPositionY());
            noteComp.setBounds(x, y, 280, 200); // Standard note size
            
            add(noteComp);
        }
        
        revalidate();
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw grid background (like the web version)
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        g2d.setColor(ThemeManager.getBorderColor().brighter());
        g2d.setStroke(new BasicStroke(0.5f));
        
        int gridSize = 20;
        int width = getWidth();
        int height = getHeight();
        
        // Draw vertical lines
        for (int x = 0; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }
        
        // Draw horizontal lines
        for (int y = 0; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
        
        g2d.dispose();
    }
    
    private void applyTheme() {
        setBackground(ThemeManager.getBackgroundColor());
        repaint();
    }
    
    @Override
    public void onThemeChanged(boolean isDarkMode) {
        SwingUtilities.invokeLater(this::applyTheme);
    }
    
    /**
     * Individual note component that can be dragged around
     */
    private class NoteComponent extends JPanel {
        private final DesktopNote note;
        private final DesktopUser currentUser;
        private final NoteService noteService;
        
        private JLabel titleLabel;
        private JLabel tagLabel;
        private JTextArea contentArea;
        private JPanel headerPanel;
        private JPanel footerPanel;
        private boolean isDragging = false;
        
        public NoteComponent(DesktopNote note, DesktopUser currentUser, NoteService noteService) {
            this.note = note;
            this.currentUser = currentUser;
            this.noteService = noteService;
            
            initializeComponents();
            setupLayout();
            setupEventHandlers();
            applyNoteTheme();
        }
        
        private void initializeComponents() {
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            
            // Header with title and metadata
            headerPanel = new JPanel(new BorderLayout());
            headerPanel.setOpaque(false);
            
            titleLabel = new JLabel(note.getTitle());
            titleLabel.setFont(new Font("Inter", Font.BOLD, 14));
            
            // Tag and type indicator
            JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            metaPanel.setOpaque(false);
            
            if (note.getTag() != null && !note.getTag().trim().isEmpty()) {
                tagLabel = new JLabel(note.getTag());
                tagLabel.setFont(new Font("Inter", Font.PLAIN, 10));
                tagLabel.setOpaque(true);
                tagLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
                tagLabel.setBackground(ThemeManager.getAccentColor());
                tagLabel.setForeground(Color.WHITE);
                metaPanel.add(tagLabel);
            }
            
            // Note type indicator
            JLabel typeLabel = new JLabel(note.getNoteType().name().substring(0, 1).toUpperCase());
            typeLabel.setFont(new Font("Inter", Font.BOLD, 10));
            typeLabel.setOpaque(true);
            typeLabel.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            typeLabel.setBackground(ThemeManager.getSecondaryColor());
            typeLabel.setForeground(Color.WHITE);
            metaPanel.add(typeLabel);
            
            // Privacy indicator
            String privacyText = note.getPrivacyLevel().getValue();
            JLabel privacyLabel = new JLabel(privacyText);
            privacyLabel.setFont(new Font("Inter", Font.PLAIN, 9));
            privacyLabel.setForeground(ThemeManager.getTextColor().darker());
            metaPanel.add(privacyLabel);
            
            headerPanel.add(titleLabel, BorderLayout.WEST);
            headerPanel.add(metaPanel, BorderLayout.EAST);
            
            // Content area
            contentArea = new JTextArea(note.getContent());
            contentArea.setFont(new Font("Inter", Font.PLAIN, 12));
            contentArea.setLineWrap(true);
            contentArea.setWrapStyleWord(true);
            contentArea.setEditable(false);
            contentArea.setOpaque(false);
            contentArea.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            
            // Limit content display
            if (contentArea.getText().length() > 150) {
                contentArea.setText(contentArea.getText().substring(0, 147) + "...");
            }
            
            // Footer with actions
            footerPanel = new JPanel(new BorderLayout());
            footerPanel.setOpaque(false);
            
            JLabel timestampLabel = new JLabel("Just now");
            timestampLabel.setFont(new Font("Inter", Font.PLAIN, 10));
            timestampLabel.setForeground(ThemeManager.getTextColor().darker());
            
            JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
            actionsPanel.setOpaque(false);
            
            // Action buttons based on ownership
            if (note.isOwnedBy(currentUser.getId())) {
                JButton editButton = createActionButton("✏", "Edit");
                JButton deleteButton = createActionButton("🗑", "Delete");
                
                editButton.addActionListener(e -> editNote());
                deleteButton.addActionListener(e -> deleteNote());
                
                actionsPanel.add(editButton);
                actionsPanel.add(deleteButton);
            } else {
                JButton viewButton = createActionButton("👁", "View");
                viewButton.addActionListener(e -> viewNote());
                actionsPanel.add(viewButton);
            }
            
            footerPanel.add(timestampLabel, BorderLayout.WEST);
            footerPanel.add(actionsPanel, BorderLayout.EAST);
        }
        
        private void setupLayout() {
            add(headerPanel, BorderLayout.NORTH);
            add(contentArea, BorderLayout.CENTER);
            add(footerPanel, BorderLayout.SOUTH);
        }
        
        private void setupEventHandlers() {
            // Drag and drop functionality
            MouseAdapter mouseHandler = new MouseAdapter() {
                private Point startPoint;
                
                @Override
                public void mousePressed(MouseEvent e) {
                    if (note.isOwnedBy(currentUser.getId())) {
                        startPoint = e.getPoint();
                        draggedNote = NoteComponent.this;
                        dragOffset = startPoint;
                        isDragging = false;
                    }
                }
                
                @Override
                public void mouseReleased(MouseEvent e) {
                    if (isDragging && draggedNote == NoteComponent.this) {
                        // Save new position
                        Point location = getLocation();
                        noteService.updateNotePosition(note.getId(), location.x, location.y);
                        note.setPositionX(location.x);
                        note.setPositionY(location.y);
                    }
                    draggedNote = null;
                    dragOffset = null;
                    isDragging = false;
                    setCursor(Cursor.getDefaultCursor());
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (draggedNote == NoteComponent.this && dragOffset != null) {
                        isDragging = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                        
                        Point currentPoint = SwingUtilities.convertPoint(NoteComponent.this, e.getPoint(), getParent());
                        int newX = Math.max(0, currentPoint.x - dragOffset.x);
                        int newY = Math.max(0, currentPoint.y - dragOffset.y);
                        
                        setLocation(newX, newY);
                        getParent().repaint();
                    }
                }
            };
            
            addMouseListener(mouseHandler);
            addMouseMotionListener(mouseHandler);
        }
        
        private JButton createActionButton(String text, String tooltip) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            button.setPreferredSize(new Dimension(24, 24));
            button.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setToolTipText(tooltip);
            button.setOpaque(false);
            button.setContentAreaFilled(false);
            
            return button;
        }
        
        private void editNote() {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                NoteEditDialog dialog = new NoteEditDialog((MainFrame) window, currentUser, note);
                dialog.setVisible(true);
                
                if (dialog.isNoteSaved()) {
                    ((MainFrame) window).refreshNotes();
                }
            }
        }
        
        private void viewNote() {
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window instanceof MainFrame) {
                NoteViewDialog dialog = new NoteViewDialog((MainFrame) window, note);
                dialog.setVisible(true);
            }
        }
        
        private void deleteNote() {
            int result = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this note?",
                "Delete Note", JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                boolean success = noteService.deleteNote(note.getId(), currentUser.getId());
                if (success) {
                    Window window = SwingUtilities.getWindowAncestor(this);
                    if (window instanceof MainFrame) {
                        ((MainFrame) window).refreshNotes();
                    }
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete note",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        private void applyNoteTheme() {
            // Parse note color
            Color noteColor;
            try {
                noteColor = Color.decode(note.getColor());
            } catch (NumberFormatException e) {
                noteColor = ThemeManager.getNoteBackground();
            }
            
            setBackground(noteColor);
            titleLabel.setForeground(ThemeManager.getTextColor());
            contentArea.setForeground(ThemeManager.getTextColor());
            
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ThemeManager.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
        }
    }
}