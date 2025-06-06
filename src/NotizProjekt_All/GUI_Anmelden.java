package NotizProjekt_All;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class GUI_Anmelden extends JFrame {

    private DBVerbindung konnektor;
    private ArrayList<User> userListe;
    static int NutzerID;
    static String AngemeldeterUser;

    // Komponenten
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton anmeldenBtn;
    private JButton neuHierBtn;

    public GUI_Anmelden() {
        try {
            // Verwende hier den System-Look-and-Feel oder alternativ FlatLaf, wenn verfügbar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        // Initialisierung des Connectors und Füllen der Benutzerliste
        try {
            userListe = new ArrayList<>();
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            getUser();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
        
        initComponents();
    }

    public void getUser() {
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

    public boolean anmeldenCheck() {
        String name = usernameField.getText();
        String passwort = new String(passwordField.getPassword());
        for (User user : userListe) {
            if (name.equals(user.getUName()) && passwort.equals(user.getUPasswort())) {
                NutzerID = user.getB_id();
                AngemeldeterUser = user.getUName();
                return true;
            }
        }
        return false;
    }

    private void initComponents() {
        // Hauptpanel mit BorderLayout für klare Struktur
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Header: Titel der App
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(245, 245, 245));
        titleLabel = new JLabel("NoteGO", SwingConstants.CENTER);
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
        usernameLabel = new JLabel("Benutzername:");
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
        passwordLabel = new JLabel("Passwort:");
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
        anmeldenBtn = new JButton("Anmelden");
        anmeldenBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        anmeldenBtn.setBackground(new Color(52, 152, 219));
        anmeldenBtn.setForeground(Color.WHITE);
        anmeldenBtn.setOpaque(true);
        anmeldenBtn.setBorderPainted(false);
        anmeldenBtn.setPreferredSize(new Dimension(150, 50));
        anmeldenBtn.addActionListener(evt -> {
            if (anmeldenCheck()) {
                new SimpleGUI_MenuTabelle(NutzerID).setVisible(true);
                JOptionPane.showMessageDialog(null,
                        "Login successful for user: " + AngemeldeterUser + " with ID: " + NutzerID,
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(null,
                        "Falsches Passwort oder Benutzername nicht vorhanden.",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        });
        footerPanel.add(anmeldenBtn);
        
        // Registrierungs-Button im modernen Flat-Design
        neuHierBtn = new JButton("Neu?");
        neuHierBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        neuHierBtn.setBackground(new Color(46, 204, 113));
        neuHierBtn.setForeground(Color.WHITE);
        neuHierBtn.setOpaque(true);
        neuHierBtn.setBorderPainted(false);
        neuHierBtn.setPreferredSize(new Dimension(150, 50));
        neuHierBtn.addActionListener(evt -> {
            new SimpleGUI_NeuHier().setVisible(true);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI_Anmelden().setVisible(true));
    }
}
