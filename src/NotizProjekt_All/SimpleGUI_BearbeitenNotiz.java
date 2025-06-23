package NotizProjekt_All;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple implementation of GUI_BearbeitenNotiz
 */
public class SimpleGUI_BearbeitenNotiz extends JFrame {
    
    private JTextField txtTitel;
    private JTextField txtTag;
    private JTextArea txtInhalt;
    private JButton btnSpeichern;
    private JButton btnAbbrechen;
    
    private DBVerbindung konnektor;
    private int notizID;
    private int nutzerID;
    
    public SimpleGUI_BearbeitenNotiz(int notizID, int nutzerID) {
        this.notizID = notizID;
        this.nutzerID = nutzerID;
        try {
            // Verwende hier den System-Look-and-Feel oder alternativ FlatLaf, wenn verfügbar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        initDatabase();
        initComponents();
        loadNoteData();
    }
    
    private void initDatabase() {
        try {
            konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            konnektor.open();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    private void initComponents() {
        // Hauptpanel mit BorderLayout für klare Struktur
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Untertitel
        JLabel subtitleLabel = new JLabel("Notiz bearbeiten", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center-Panel: Enthält Labels und Textfelder
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        // Titel-Label
        JLabel lblTitel = new JLabel("Titel:");
        lblTitel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblTitel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(lblTitel, gbc);
        
        // Titel-Textfeld
        txtTitel = new JTextField();
        txtTitel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtTitel.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 1;
        centerPanel.add(txtTitel, gbc);
        
        // Tag-Label
        JLabel lblTag = new JLabel("Tag:");
        lblTag.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblTag.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        centerPanel.add(lblTag, gbc);
        
        // Tag-Textfeld
        txtTag = new JTextField();
        txtTag.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtTag.setPreferredSize(new Dimension(500, 40));
        gbc.gridy = 3;
        centerPanel.add(txtTag, gbc);
        
        // Inhalt-Label
        JLabel lblInhalt = new JLabel("Inhalt:");
        lblInhalt.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblInhalt.setForeground(new Color(70, 70, 70));
        gbc.gridy = 4;
        centerPanel.add(lblInhalt, gbc);
        
        // Inhalt-TextArea mit ScrollPane
        txtInhalt = new JTextArea(10, 40);
        txtInhalt.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtInhalt.setLineWrap(true);
        txtInhalt.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtInhalt);
        scrollPane.setPreferredSize(new Dimension(500, 250));
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Abbrechen-Button im modernen Flat-Design
        btnAbbrechen = new JButton("Abbrechen");
        btnAbbrechen.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnAbbrechen.setBackground(new Color(149, 165, 166));
        btnAbbrechen.setForeground(Color.WHITE);
        btnAbbrechen.setOpaque(true);
        btnAbbrechen.setBorderPainted(false);
        btnAbbrechen.setPreferredSize(new Dimension(150, 50));
        footerPanel.add(btnAbbrechen);
        
        // Speichern-Button im modernen Flat-Design
        btnSpeichern = new JButton("Speichern");
        btnSpeichern.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnSpeichern.setBackground(new Color(52, 152, 219));
        btnSpeichern.setForeground(Color.WHITE);
        btnSpeichern.setOpaque(true);
        btnSpeichern.setBorderPainted(false);
        btnSpeichern.setPreferredSize(new Dimension(150, 50));
        footerPanel.add(btnSpeichern);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Notiz bearbeiten");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 700));
        this.pack();
        this.setLocationRelativeTo(null);
        
        // Add action listeners
        btnSpeichern.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveNote();
            }
        });
        
        btnAbbrechen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleGUI_MenuTabelle menuTabelle = new SimpleGUI_MenuTabelle(nutzerID);
                menuTabelle.setVisible(true);
                dispose();
            }
        });
    }
    
    private void loadNoteData() {
        try {
            ResultSet rs = konnektor.fuehreAbfrageAus(
                "SELECT Titel, Tag, Inhalt FROM notiz WHERE N_id = " + notizID);
            
            if (rs.next()) {
                txtTitel.setText(rs.getString("Titel"));
                txtTag.setText(rs.getString("Tag"));
                txtInhalt.setText(rs.getString("Inhalt"));
            } else {
                JOptionPane.showMessageDialog(this, "Notiz konnte nicht gefunden werden.");
                SimpleGUI_MenuTabelle menuTabelle = new SimpleGUI_MenuTabelle(nutzerID);
                menuTabelle.setVisible(true);
                dispose();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Notiz: " + ex.getMessage());
        }
    }
    
    private void saveNote() {
        String titel = txtTitel.getText().trim();
        String tag = txtTag.getText().trim();
        String inhalt = txtInhalt.getText().trim();
        
        if (titel.isEmpty() || inhalt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte geben Sie einen Titel und Inhalt ein.");
            return;
        }
        
        try {
            int result = konnektor.fuehreVeraenderungAus(
                "UPDATE notiz SET Titel = '" + titel + "', Tag = '" + tag + "', Inhalt = '" + inhalt + 
                "' WHERE N_id = " + notizID);
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Notiz wurde erfolgreich aktualisiert.");
                SimpleGUI_MenuTabelle menuTabelle = new SimpleGUI_MenuTabelle(nutzerID);
                menuTabelle.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Fehler beim Aktualisieren der Notiz.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Aktualisieren der Notiz: " + ex.getMessage());
        }
    }
}