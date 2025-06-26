package NotizProjekt_All;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Konsolidierte GUI-Klasse, die alle GUI-Funktionalitäten des Projekts enthält.
 * Diese Klasse ersetzt alle separaten GUI-Klassen und vereinfacht die Projektstruktur.
 */
public class GUI extends JFrame {
    
    // Gemeinsame Komponenten
    private DBVerbindung konnektor;
    private JPanel mainPanel;
    
    // Anmelden-Komponenten
    private ArrayList<User> userListe;
    private JTextField usernameField;
    private JPasswordField passwordField;
    
    // MenuTabelle-Komponenten
    private JTable notizTabelle;
    private ArrayList<Integer> notizIDs = new ArrayList<>();
    private int nutzerID;
    
    // NeueNotiz-Komponenten
    private JTextField titelField;
    private JTextField tagField;
    private JTextArea inhaltArea;
    private JComboBox<String> typComboBox;
    
    // Bearbeiten-Komponenten
    private int notizID;
    private Notiz aktuelleNotiz;
    
    // Konstanten für GUI-Typen
    public static final int ANMELDEN = 0;
    public static final int REGISTRIEREN = 1;
    public static final int MENU = 2;
    public static final int NEUE_NOTIZ = 3;
    public static final int BEARBEITEN = 4;
    
    /**
     * Konstruktor für Anmelde- und Registrierungsbildschirm
     * @param guiType Typ der GUI (ANMELDEN oder REGISTRIEREN)
     */
    public GUI(int guiType) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        try {
            konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            konnektor.open();
            
            if (guiType == ANMELDEN) {
                userListe = new ArrayList<>();
                getUser();
                initAnmeldenGUI();
            } else if (guiType == REGISTRIEREN) {
                initRegistrierenGUI();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    /**
     * Konstruktor für Menü-Bildschirm
     * @param guiType Typ der GUI (MENU)
     * @param nutzerID ID des angemeldeten Nutzers
     */
    public GUI(int guiType, int nutzerID) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        try {
            this.nutzerID = nutzerID;
            konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            konnektor.open();
            
            if (guiType == MENU) {
                initMenuGUI();
                refreshTable();
            } else if (guiType == NEUE_NOTIZ) {
                initNeueNotizGUI();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    /**
     * Konstruktor für Bearbeiten-Bildschirm
     * @param guiType Typ der GUI (BEARBEITEN)
     * @param notizID ID der zu bearbeitenden Notiz
     * @param nutzerID ID des angemeldeten Nutzers
     */
    public GUI(int guiType, int notizID, int nutzerID) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        try {
            this.nutzerID = nutzerID;
            this.notizID = notizID;
            konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            konnektor.open();
            
            if (guiType == BEARBEITEN) {
                ladeNotiz();
                initBearbeitenGUI();
            }
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    /**
     * Lädt die Benutzerliste aus der Datenbank
     */
    private void getUser() {
        try {
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus("SELECT benutzername, passwort, id FROM nutzer");
            while (ergebnis.next()) {
                User naechsterUser = new User(ergebnis.getString("benutzername"),
                                               ergebnis.getString("passwort"),
                                               ergebnis.getInt("id"));
                this.userListe.add(naechsterUser);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler bei der Abfrage der Datenbank: " + ex);
        }
    }
    
    /**
     * Überprüft die Anmeldedaten
     * @return true, wenn die Anmeldedaten korrekt sind
     */
    private boolean anmeldenCheck() {
        String name = usernameField.getText();
        String passwort = new String(passwordField.getPassword());
        for (User user : userListe) {
            if (name.equals(user.getUName()) && passwort.equals(user.getUPasswort())) {
                nutzerID = user.getB_id();
                return true;
            }
        }
        return false;
    }
    
    /**
     * Initialisiert die Anmelde-GUI
     */
    private void initAnmeldenGUI() {
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
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
        JLabel usernameLabel = new JLabel("Benutzername:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(usernameLabel, gbc);
        
        // Benutzername-Textfeld - vergrößert
        usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 1;
        centerPanel.add(usernameField, gbc);
        
        // Passwort-Label
        JLabel passwordLabel = new JLabel("Passwort:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordLabel.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);
        
        // Passwort-Feld - vergrößert
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 3;
        centerPanel.add(passwordField, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons, angeordnet mit FlowLayout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Anmelde-Button im modernen Flat-Design
        JButton anmeldenBtn = new JButton("Anmelden");
        anmeldenBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        anmeldenBtn.setBackground(new Color(52, 152, 219));
        anmeldenBtn.setForeground(Color.WHITE);
        anmeldenBtn.setOpaque(true);
        anmeldenBtn.setBorderPainted(false);
        anmeldenBtn.setPreferredSize(new Dimension(150, 50));
        anmeldenBtn.addActionListener(evt -> {
            if (anmeldenCheck()) {
                new GUI(MENU, nutzerID).setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Falsches Passwort oder Benutzername nicht vorhanden.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        footerPanel.add(anmeldenBtn);
        
        // Registrierungs-Button im modernen Flat-Design
        JButton neuHierBtn = new JButton("Neu?");
        neuHierBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        neuHierBtn.setBackground(new Color(46, 204, 113));
        neuHierBtn.setForeground(Color.WHITE);
        neuHierBtn.setOpaque(true);
        neuHierBtn.setBorderPainted(false);
        neuHierBtn.setPreferredSize(new Dimension(150, 50));
        neuHierBtn.addActionListener(evt -> {
            new GUI(REGISTRIEREN).setVisible(true);
            this.dispose();
        });
        footerPanel.add(neuHierBtn);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Anmeldung");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 500));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Initialisiert die Registrierungs-GUI
     */
    private void initRegistrierenGUI() {
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 50));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
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
        JLabel usernameLabel = new JLabel("Benutzername:");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(usernameLabel, gbc);
        
        // Benutzername-Textfeld
        usernameField = new JTextField();
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 1;
        centerPanel.add(usernameField, gbc);
        
        // Passwort-Label
        JLabel passwordLabel = new JLabel("Passwort:");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordLabel.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);
        
        // Passwort-Feld
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 3;
        centerPanel.add(passwordField, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Registrieren-Button
        JButton registrierenBtn = new JButton("Registrieren");
        registrierenBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        registrierenBtn.setBackground(new Color(46, 204, 113));
        registrierenBtn.setForeground(Color.WHITE);
        registrierenBtn.setOpaque(true);
        registrierenBtn.setBorderPainted(false);
        registrierenBtn.setPreferredSize(new Dimension(150, 50));
        registrierenBtn.addActionListener(evt -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            
            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Bitte füllen Sie alle Felder aus.");
                return;
            }
            
            try {
                // Prüfen, ob Benutzername bereits existiert
                ResultSet rs = konnektor.fuehreAbfrageAus("SELECT * FROM nutzer WHERE benutzername = '" + username + "'");
                if (rs.next()) {
                    JOptionPane.showMessageDialog(null, "Benutzername existiert bereits.");
                    return;
                }
                
                // Neuen Benutzer anlegen
                konnektor.fuehreVeraenderungAus("INSERT INTO nutzer (benutzername, passwort) VALUES ('" + 
                                               username + "', '" + password + "')");
                
                JOptionPane.showMessageDialog(null, "Registrierung erfolgreich!");
                
                // Zurück zum Login
                new GUI(ANMELDEN).setVisible(true);
                this.dispose();
                
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, "Fehler bei der Registrierung: " + ex.getMessage());
            }
        });
        footerPanel.add(registrierenBtn);
        
        // Zurück-Button
        JButton zurueckBtn = new JButton("Zurück");
        zurueckBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        zurueckBtn.setBackground(new Color(149, 165, 166));
        zurueckBtn.setForeground(Color.WHITE);
        zurueckBtn.setOpaque(true);
        zurueckBtn.setBorderPainted(false);
        zurueckBtn.setPreferredSize(new Dimension(150, 50));
        zurueckBtn.addActionListener(evt -> {
            new GUI(ANMELDEN).setVisible(true);
            this.dispose();
        });
        footerPanel.add(zurueckBtn);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Registrierung");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 500));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Initialisiert die Menü-GUI
     */
    private void initMenuGUI() {
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Untertitel mit Benutzername
        String username = "";
        try {
            ResultSet rs = konnektor.fuehreAbfrageAus("SELECT benutzername FROM nutzer WHERE id = " + nutzerID);
            if (rs.next()) {
                username = rs.getString("benutzername");
            }
        } catch (SQLException ex) {
            System.err.println("Fehler beim Laden des Benutzernamens: " + ex);
        }
        
        JLabel subtitleLabel = new JLabel("Notizen Übersicht - " + username, SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center-Panel: Tabelle
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Table setup mit modernem Design
        notizTabelle = new JTable();
        notizTabelle.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"Titel", "Tag", "Typ", "Ersteller"}
        ));
        notizTabelle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        notizTabelle.setRowHeight(30);
        notizTabelle.setBackground(Color.WHITE);
        notizTabelle.setSelectionBackground(new Color(52, 152, 219));
        notizTabelle.setSelectionForeground(Color.WHITE);
        notizTabelle.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        notizTabelle.getTableHeader().setBackground(new Color(70, 70, 70));
        notizTabelle.getTableHeader().setForeground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(notizTabelle);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons und Suche
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(new Color(245, 245, 245));
        
        // Buttons im modernen Flat-Design
        JButton btnNeueNotiz = new JButton("Neue Notiz");
        btnNeueNotiz.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNeueNotiz.setBackground(new Color(46, 204, 113));
        btnNeueNotiz.setForeground(Color.WHITE);
        btnNeueNotiz.setOpaque(true);
        btnNeueNotiz.setBorderPainted(false);
        btnNeueNotiz.setPreferredSize(new Dimension(140, 45));
        btnNeueNotiz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI(NEUE_NOTIZ, nutzerID).setVisible(true);
                dispose();
            }
        });
        buttonPanel.add(btnNeueNotiz);
        
        JButton btnBearbeiten = new JButton("Bearbeiten");
        btnBearbeiten.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnBearbeiten.setBackground(new Color(52, 152, 219));
        btnBearbeiten.setForeground(Color.WHITE);
        btnBearbeiten.setOpaque(true);
        btnBearbeiten.setBorderPainted(false);
        btnBearbeiten.setPreferredSize(new Dimension(140, 45));
        btnBearbeiten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = notizTabelle.getSelectedRow();
                if (selectedRow >= 0) {
                    int notizID = notizIDs.get(selectedRow);
                    new GUI(BEARBEITEN, notizID, nutzerID).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Bitte wählen Sie eine Notiz aus.");
                }
            }
        });
        buttonPanel.add(btnBearbeiten);
        
        JButton btnLoeschen = new JButton("Löschen");
        btnLoeschen.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLoeschen.setBackground(new Color(231, 76, 60));
        btnLoeschen.setForeground(Color.WHITE);
        btnLoeschen.setOpaque(true);
        btnLoeschen.setBorderPainted(false);
        btnLoeschen.setPreferredSize(new Dimension(140, 45));
        btnLoeschen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = notizTabelle.getSelectedRow();
                if (selectedRow >= 0) {
                    int notizID = notizIDs.get(selectedRow);
                    try {
                        konnektor.fuehreVeraenderungAus("DELETE FROM notiz WHERE N_id = " + notizID);
                        refreshTable();
                        JOptionPane.showMessageDialog(null, "Notiz wurde gelöscht.");
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Fehler beim Löschen der Notiz: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Bitte wählen Sie eine Notiz aus.");
                }
            }
        });
        buttonPanel.add(btnLoeschen);
        
        footerPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        
        JLabel searchLabel = new JLabel("Suchen:");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchLabel.setForeground(new Color(70, 70, 70));
        searchPanel.add(searchLabel);
        
        JTextField txtSuche = new JTextField();
        txtSuche.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtSuche.setPreferredSize(new Dimension(300, 35));
        searchPanel.add(txtSuche);
        
        JButton btnSuchen = new JButton("Suchen");
        btnSuchen.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSuchen.setBackground(new Color(149, 165, 166));
        btnSuchen.setForeground(Color.WHITE);
        btnSuchen.setOpaque(true);
        btnSuchen.setBorderPainted(false);
        btnSuchen.setPreferredSize(new Dimension(100, 35));
        btnSuchen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String suchText = txtSuche.getText().trim();
                if (!suchText.isEmpty()) {
                    searchNotes(suchText);
                } else {
                    refreshTable();
                }
            }
        });
        searchPanel.add(btnSuchen);
        
        footerPanel.add(searchPanel, BorderLayout.SOUTH);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Notizen Übersicht");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(900, 700));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Aktualisiert die Notizentabelle
     */
    private void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) notizTabelle.getModel();
            model.setRowCount(0);
            notizIDs.clear();
            
            // Prüfen, ob das Feld Typ existiert
            boolean typExists = false;
            try {
                ResultSet columns = konnektor.fuehreAbfrageAus(
                    "SHOW COLUMNS FROM notiz LIKE 'Typ'");
                typExists = columns.next();
            } catch (SQLException e) {
                System.err.println("Fehler beim Prüfen der Spalte Typ: " + e.getMessage());
            }
            
            // Wenn die Spalte nicht existiert, fügen wir sie hinzu
            if (!typExists) {
                try {
                    konnektor.fuehreVeraenderungAus(
                        "ALTER TABLE notiz ADD COLUMN Typ varchar(20) DEFAULT 'PRIVAT'");
                    System.out.println("Spalte Typ wurde zur Tabelle notiz hinzugefügt");
                    typExists = true;
                } catch (SQLException e) {
                    System.err.println("Fehler beim Hinzufügen der Spalte Typ: " + e.getMessage());
                }
            }
            
            String query;
            if (typExists) {
                // Lade private Notizen des Benutzers und alle öffentlichen Notizen
                query = "SELECT N_id, Titel, Tag, Inhalt, Typ, B_id FROM notiz WHERE " +
                        "(B_id = " + nutzerID + " AND Typ = 'PRIVAT') OR Typ = 'OEFFENTLICH'";
            } else {
                query = "SELECT N_id, Titel, Tag, Inhalt FROM notiz WHERE B_id = " + nutzerID;
            }
            
            ResultSet rs = konnektor.fuehreAbfrageAus(query);
            
            while (rs.next()) {
                int notizID = rs.getInt("N_id");
                String titel = rs.getString("Titel");
                String tag = rs.getString("Tag");
                
                if (typExists) {
                    String typ = rs.getString("Typ");
                    int erstellerId = rs.getInt("B_id");
                    
                    // Benutzername des Erstellers laden
                    String ersteller = "Unbekannt";
                    try {
                        ResultSet userRs = konnektor.fuehreAbfrageAus("SELECT benutzername FROM nutzer WHERE id = " + erstellerId);
                        if (userRs.next()) {
                            ersteller = userRs.getString("benutzername");
                        }
                    } catch (SQLException e) {
                        System.err.println("Fehler beim Laden des Benutzernamens: " + e.getMessage());
                    }
                    
                    String typAnzeige = typ.equals("PRIVAT") ? "Privat" : "Öffentlich";
                    model.addRow(new Object[]{titel, tag, typAnzeige, ersteller});
                } else {
                    model.addRow(new Object[]{titel, tag, "Privat", "Sie"});
                }
                notizIDs.add(notizID);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Notizen: " + ex.getMessage());
        }
    }
    
    /**
     * Sucht nach Notizen
     * @param searchText Suchtext
     */
    private void searchNotes(String searchText) {
        try {
            DefaultTableModel model = (DefaultTableModel) notizTabelle.getModel();
            model.setRowCount(0);
            notizIDs.clear();
            
            // Prüfen, ob das Feld Typ existiert
            boolean typExists = false;
            try {
                ResultSet columns = konnektor.fuehreAbfrageAus(
                    "SHOW COLUMNS FROM notiz LIKE 'Typ'");
                typExists = columns.next();
            } catch (SQLException e) {
                System.err.println("Fehler beim Prüfen der Spalte Typ: " + e.getMessage());
            }
            
            String query;
            if (typExists) {
                // Suche in privaten Notizen des Benutzers und allen öffentlichen Notizen
                query = "SELECT N_id, Titel, Tag, Inhalt, Typ, B_id FROM notiz WHERE " +
                        "((B_id = " + nutzerID + " AND Typ = 'PRIVAT') OR Typ = 'OEFFENTLICH') " +
                        "AND (Titel LIKE '%" + searchText + "%' OR Tag LIKE '%" + searchText + 
                        "%' OR Inhalt LIKE '%" + searchText + "%')";
            } else {
                query = "SELECT N_id, Titel, Tag, Inhalt FROM notiz WHERE B_id = " + nutzerID + 
                        " AND (Titel LIKE '%" + searchText + "%' OR Tag LIKE '%" + searchText + 
                        "%' OR Inhalt LIKE '%" + searchText + "%')";
            }
            
            ResultSet rs = konnektor.fuehreAbfrageAus(query);
            
            while (rs.next()) {
                int notizID = rs.getInt("N_id");
                String titel = rs.getString("Titel");
                String tag = rs.getString("Tag");
                
                if (typExists) {
                    String typ = rs.getString("Typ");
                    int erstellerId = rs.getInt("B_id");
                    
                    // Benutzername des Erstellers laden
                    String ersteller = "Unbekannt";
                    try {
                        ResultSet userRs = konnektor.fuehreAbfrageAus("SELECT benutzername FROM nutzer WHERE id = " + erstellerId);
                        if (userRs.next()) {
                            ersteller = userRs.getString("benutzername");
                        }
                    } catch (SQLException e) {
                        System.err.println("Fehler beim Laden des Benutzernamens: " + e.getMessage());
                    }
                    
                    String typAnzeige = typ.equals("PRIVAT") ? "Privat" : "Öffentlich";
                    model.addRow(new Object[]{titel, tag, typAnzeige, ersteller});
                } else {
                    model.addRow(new Object[]{titel, tag, "Privat", "Sie"});
                }
                notizIDs.add(notizID);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Suche: " + ex.getMessage());
        }
    }
    
    /**
     * Initialisiert die NeueNotiz-GUI
     */
    private void initNeueNotizGUI() {
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Neue Notiz erstellen", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center-Panel: Formular
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Titel
        JLabel titelLabel = new JLabel("Titel:");
        titelLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        titelLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        centerPanel.add(titelLabel, gbc);
        
        titelField = new JTextField();
        titelField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        titelField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titelField, gbc);
        
        // Tag
        JLabel tagLabel = new JLabel("Tag:");
        tagLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tagLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(tagLabel, gbc);
        
        tagField = new JTextField();
        tagField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tagField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        centerPanel.add(tagField, gbc);
        
        // Typ
        JLabel typLabel = new JLabel("Typ:");
        typLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        typLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        centerPanel.add(typLabel, gbc);
        
        typComboBox = new JComboBox<>(new String[]{"Privat", "Öffentlich"});
        typComboBox.setFont(new Font("SansSerif", Font.PLAIN, 18));
        typComboBox.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(typComboBox, gbc);
        
        // Inhalt
        JLabel inhaltLabel = new JLabel("Inhalt:");
        inhaltLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inhaltLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        centerPanel.add(inhaltLabel, gbc);
        
        inhaltArea = new JTextArea();
        inhaltArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inhaltArea.setLineWrap(true);
        inhaltArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(inhaltArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        JButton speichernBtn = new JButton("Speichern");
        speichernBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        speichernBtn.setBackground(new Color(46, 204, 113));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        speichernBtn.setPreferredSize(new Dimension(140, 45));
        speichernBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speichereNeueNotiz();
            }
        });
        footerPanel.add(speichernBtn);
        
        JButton abbrechenBtn = new JButton("Abbrechen");
        abbrechenBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        abbrechenBtn.setBackground(new Color(231, 76, 60));
        abbrechenBtn.setForeground(Color.WHITE);
        abbrechenBtn.setOpaque(true);
        abbrechenBtn.setBorderPainted(false);
        abbrechenBtn.setPreferredSize(new Dimension(140, 45));
        abbrechenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI(MENU, nutzerID).setVisible(true);
                dispose();
            }
        });
        footerPanel.add(abbrechenBtn);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Neue Notiz");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 700));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Speichert eine neue Notiz
     */
    private void speichereNeueNotiz() {
        String titel = titelField.getText().trim();
        String tag = tagField.getText().trim();
        String inhalt = inhaltArea.getText().trim();
        int typ = typComboBox.getSelectedIndex();
        
        if (titel.isEmpty() || inhalt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie Titel und Inhalt aus.");
            return;
        }
        
        try {
            Notiz.NotizTyp notizTyp = Notiz.NotizTyp.values()[typ];
            
            String sql = "INSERT INTO notiz (Titel, Tag, Inhalt, B_id, Typ) VALUES ('" +
                         titel + "', '" + tag + "', '" + inhalt + "', " + nutzerID + ", '" + notizTyp.name() + "')";
            
            konnektor.fuehreVeraenderungAus(sql);
            
            JOptionPane.showMessageDialog(this, "Notiz wurde erfolgreich gespeichert.");
            
            new GUI(MENU, nutzerID).setVisible(true);
            this.dispose();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Speichern der Notiz: " + ex.getMessage());
        }
    }
    
    /**
     * Lädt eine Notiz aus der Datenbank
     */
    private void ladeNotiz() {
        try {
            // Prüfen, ob das Feld Typ existiert
            boolean typExists = false;
            try {
                ResultSet columns = konnektor.fuehreAbfrageAus(
                    "SHOW COLUMNS FROM notiz LIKE 'Typ'");
                typExists = columns.next();
            } catch (SQLException e) {
                System.err.println("Fehler beim Prüfen der Spalte Typ: " + e.getMessage());
            }
            
            String query;
            if (typExists) {
                query = "SELECT Titel, Tag, Inhalt, Typ, B_id FROM notiz WHERE N_id = " + notizID;
            } else {
                query = "SELECT Titel, Tag, Inhalt, B_id FROM notiz WHERE N_id = " + notizID;
                
                // Versuchen, die Spalte Typ hinzuzufügen
                try {
                    konnektor.fuehreVeraenderungAus(
                        "ALTER TABLE notiz ADD COLUMN Typ varchar(20) DEFAULT 'PRIVAT'");
                    System.out.println("Spalte Typ wurde zur Tabelle notiz hinzugefügt");
                } catch (SQLException e) {
                    System.err.println("Fehler beim Hinzufügen der Spalte Typ: " + e.getMessage());
                }
            }
            
            ResultSet rs = konnektor.fuehreAbfrageAus(query);
            
            if (rs.next()) {
                String titel = rs.getString("Titel");
                String tag = rs.getString("Tag");
                String inhalt = rs.getString("Inhalt");
                int originalUserId = rs.getInt("B_id");
                
                Notiz.NotizTyp typ = Notiz.NotizTyp.PRIVAT; // Default
                
                if (typExists) {
                    String typStr = rs.getString("Typ");
                    if (typStr != null && !typStr.isEmpty()) {
                        try {
                            typ = Notiz.NotizTyp.valueOf(typStr);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Ungültiger Notiztyp: " + typStr);
                        }
                    }
                }
                
                // Prüfen, ob der Benutzer berechtigt ist, die Notiz zu bearbeiten
                // Private Notizen: nur der Ersteller
                // Öffentliche Notizen: alle Benutzer
                if (typ == Notiz.NotizTyp.PRIVAT && originalUserId != nutzerID) {
                    JOptionPane.showMessageDialog(this, "Sie haben keine Berechtigung, diese private Notiz zu bearbeiten.");
                    this.dispose();
                    return;
                }
                
                aktuelleNotiz = new Notiz(notizID, titel, tag, inhalt, originalUserId);
                aktuelleNotiz.setTyp(typ);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Notiz: " + ex.getMessage());
        }
    }
    
    /**
     * Initialisiert die Bearbeiten-GUI
     */
    private void initBearbeitenGUI() {
        if (aktuelleNotiz == null) {
            JOptionPane.showMessageDialog(this, "Fehler: Notiz konnte nicht geladen werden.");
            new GUI(MENU, nutzerID).setVisible(true);
            this.dispose();
            return;
        }
        
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        JLabel titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 40));
        titleLabel.setForeground(new Color(45, 45, 45));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        JLabel subtitleLabel = new JLabel("Notiz bearbeiten", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        subtitleLabel.setForeground(new Color(70, 70, 70));
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center-Panel: Formular
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(245, 245, 245));
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Titel
        JLabel titelLabel = new JLabel("Titel:");
        titelLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        titelLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        centerPanel.add(titelLabel, gbc);
        
        titelField = new JTextField(aktuelleNotiz.getTitel());
        titelField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        titelField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(titelField, gbc);
        
        // Tag
        JLabel tagLabel = new JLabel("Tag:");
        tagLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tagLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        centerPanel.add(tagLabel, gbc);
        
        tagField = new JTextField(aktuelleNotiz.getTag());
        tagField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        tagField.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        centerPanel.add(tagField, gbc);
        
        // Typ
        JLabel typLabel = new JLabel("Typ:");
        typLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        typLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        centerPanel.add(typLabel, gbc);
        
        typComboBox = new JComboBox<>(new String[]{"Privat", "Öffentlich"});
        typComboBox.setSelectedIndex(aktuelleNotiz.getTyp().ordinal());
        typComboBox.setFont(new Font("SansSerif", Font.PLAIN, 18));
        typComboBox.setPreferredSize(new Dimension(400, 40));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        centerPanel.add(typComboBox, gbc);
        
        // Inhalt
        JLabel inhaltLabel = new JLabel("Inhalt:");
        inhaltLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inhaltLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        centerPanel.add(inhaltLabel, gbc);
        
        inhaltArea = new JTextArea(aktuelleNotiz.getInhalt());
        inhaltArea.setFont(new Font("SansSerif", Font.PLAIN, 18));
        inhaltArea.setLineWrap(true);
        inhaltArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(inhaltArea);
        scrollPane.setPreferredSize(new Dimension(600, 300));
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        centerPanel.add(scrollPane, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        JButton speichernBtn = new JButton("Speichern");
        speichernBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        speichernBtn.setBackground(new Color(46, 204, 113));
        speichernBtn.setForeground(Color.WHITE);
        speichernBtn.setOpaque(true);
        speichernBtn.setBorderPainted(false);
        speichernBtn.setPreferredSize(new Dimension(140, 45));
        speichernBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                speichereBearbeitung();
            }
        });
        footerPanel.add(speichernBtn);
        
        JButton abbrechenBtn = new JButton("Abbrechen");
        abbrechenBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        abbrechenBtn.setBackground(new Color(231, 76, 60));
        abbrechenBtn.setForeground(Color.WHITE);
        abbrechenBtn.setOpaque(true);
        abbrechenBtn.setBorderPainted(false);
        abbrechenBtn.setPreferredSize(new Dimension(140, 45));
        abbrechenBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI(MENU, nutzerID).setVisible(true);
                dispose();
            }
        });
        footerPanel.add(abbrechenBtn);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Notiz bearbeiten");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 700));
        this.pack();
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Speichert die Änderungen an einer Notiz
     */
    private void speichereBearbeitung() {
        String titel = titelField.getText().trim();
        String tag = tagField.getText().trim();
        String inhalt = inhaltArea.getText().trim();
        int typ = typComboBox.getSelectedIndex();
        
        if (titel.isEmpty() || inhalt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie Titel und Inhalt aus.");
            return;
        }
        
        try {
            Notiz.NotizTyp notizTyp = Notiz.NotizTyp.values()[typ];
            
            String sql = "UPDATE notiz SET Titel = '" + titel + "', Tag = '" + tag + 
                         "', Inhalt = '" + inhalt + "', Typ = '" + notizTyp.name() + 
                         "' WHERE N_id = " + notizID;
            
            konnektor.fuehreVeraenderungAus(sql);
            
            JOptionPane.showMessageDialog(this, "Notiz wurde erfolgreich aktualisiert.");
            
            new GUI(MENU, nutzerID).setVisible(true);
            this.dispose();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Aktualisieren der Notiz: " + ex.getMessage());
        }
    }
    
    /**
     * Hauptmethode zum Testen der GUI
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI(ANMELDEN).setVisible(true));
    }
}