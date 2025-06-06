package NotizProjekt_All;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Simple implementation of GUI_NeuHier
 */
public class SimpleGUI_NeuHier extends JFrame {
    
    private JTextField txtNutzername;
    private JPasswordField txtPasswort1;
    private JPasswordField txtPasswort2;
    private JButton btnRegistrieren;
    private JButton btnAbbrechen;
    
    private DBVerbindung konnektor;
    
    public SimpleGUI_NeuHier() {
        try {
            // Verwende hier den System-Look-and-Feel oder alternativ FlatLaf, wenn verfügbar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        initDatabase();
        initComponents();
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
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Untertitel
        JLabel subtitleLabel = new JLabel("Registrierung", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
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
        
        // Benutzername-Label
        JLabel lblNutzername = new JLabel("Benutzername (max. 20 Zeichen):");
        lblNutzername.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblNutzername.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(lblNutzername, gbc);
        
        // Benutzername-Textfeld
        txtNutzername = new JTextField();
        txtNutzername.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtNutzername.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 1;
        centerPanel.add(txtNutzername, gbc);
        
        // Passwort-Label
        JLabel lblPasswort1 = new JLabel("Passwort (max. 60 Zeichen):");
        lblPasswort1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblPasswort1.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        centerPanel.add(lblPasswort1, gbc);
        
        // Passwort-Feld
        txtPasswort1 = new JPasswordField();
        txtPasswort1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtPasswort1.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 3;
        centerPanel.add(txtPasswort1, gbc);
        
        // Passwort bestätigen-Label
        JLabel lblPasswort2 = new JLabel("Passwort bestätigen:");
        lblPasswort2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblPasswort2.setForeground(new Color(70, 70, 70));
        gbc.gridy = 4;
        centerPanel.add(lblPasswort2, gbc);
        
        // Passwort bestätigen-Feld
        txtPasswort2 = new JPasswordField();
        txtPasswort2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtPasswort2.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 5;
        centerPanel.add(txtPasswort2, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons, angeordnet mit FlowLayout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Zurück-Button im modernen Flat-Design
        btnAbbrechen = new JButton("Zurück");
        btnAbbrechen.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnAbbrechen.setBackground(new Color(149, 165, 166));
        btnAbbrechen.setForeground(Color.WHITE);
        btnAbbrechen.setOpaque(true);
        btnAbbrechen.setBorderPainted(false);
        btnAbbrechen.setPreferredSize(new Dimension(150, 50));
        btnAbbrechen.addActionListener(e -> {
            GUI_Anmelden anmelden = new GUI_Anmelden();
            anmelden.setVisible(true);
            dispose();
        });
        footerPanel.add(btnAbbrechen);
        
        // Registrieren-Button im modernen Flat-Design
        btnRegistrieren = new JButton("Registrieren");
        btnRegistrieren.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnRegistrieren.setBackground(new Color(46, 204, 113));
        btnRegistrieren.setForeground(Color.WHITE);
        btnRegistrieren.setOpaque(true);
        btnRegistrieren.setBorderPainted(false);
        btnRegistrieren.setPreferredSize(new Dimension(150, 50));
        btnRegistrieren.addActionListener(e -> registerUser());
        footerPanel.add(btnRegistrieren);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Registrierung");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 600));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    private boolean nutzerdatenCheck() {
        String benutzername = txtNutzername.getText().trim();
        boolean status = false;
        try {      
            // Only check if username exists, not password
            ResultSet ergebnis = konnektor.fuehreAbfrageAus("SELECT Benutzername FROM nutzer WHERE Benutzername = '"+benutzername+"'");
            status = ergebnis.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
    }
    
    private void registerUser() {
        String benutzername = txtNutzername.getText().trim();
        String passwort1 = new String(txtPasswort1.getPassword());
        String passwort2 = new String(txtPasswort2.getPassword());
        
        if (benutzername.isEmpty() || passwort1.isEmpty() || passwort2.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie alle Felder aus.");
            return;
        }
        
        if (!passwort1.equals(passwort2)) {
            JOptionPane.showMessageDialog(this, "Die Passwörter stimmen nicht überein.");
            return;
        }
        
        if (nutzerdatenCheck()) {
            JOptionPane.showMessageDialog(this, "Dieser Benutzername ist bereits vergeben.");
            return;
        }
        
        try {
            int result = konnektor.fuehreVeraenderungAus(
                "INSERT INTO nutzer (Benutzername, Passwort) VALUES ('" + 
                benutzername + "', '" + 
                passwort1 + "')");
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Benutzer wurde erfolgreich registriert.");
                GUI_Anmelden anmelden = new GUI_Anmelden();
                anmelden.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Fehler bei der Registrierung.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Registrierung: " + ex.getMessage());
        }
    }
}