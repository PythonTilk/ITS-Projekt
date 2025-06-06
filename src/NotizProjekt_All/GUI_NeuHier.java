/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NotizProjekt_All;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author felix.haerter
 */
public class GUI_NeuHier extends JFrame {
    
    private DBVerbindung konnektor;
    
    // Komponenten
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JLabel usernameLabel;
    private JLabel passwordLabel;
    private JLabel confirmPasswordLabel;
    private JTextField nutzername;
    private JPasswordField passwort1;
    private JPasswordField passwort2;
    private JButton speichern;
    private JButton zurueck;
    
    public GUI_NeuHier() {
        try {
            // Verwende hier den System-Look-and-Feel oder alternativ FlatLaf, wenn verfügbar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        try {
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(getRootPane(), "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
        
        initComponents();
    }
public boolean NutzerdatenCheck() {
        String benutzername = nutzername.getText();
        boolean status = false;
        try {      
            // Only check if username exists, not password
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus("SELECT Benutzername FROM nutzer WHERE Benutzername = '"+benutzername+"'");
            status = ergebnis.next();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return status;
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
        usernameLabel = new JLabel("Benutzername (max. 20 Zeichen):");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        usernameLabel.setForeground(new Color(70, 70, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        centerPanel.add(usernameLabel, gbc);
        
        // Benutzername-Textfeld
        nutzername = new JTextField();
        nutzername.setFont(new Font("SansSerif", Font.PLAIN, 18));
        nutzername.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 1;
        centerPanel.add(nutzername, gbc);
        
        // Passwort-Label
        passwordLabel = new JLabel("Passwort (max. 60 Zeichen):");
        passwordLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwordLabel.setForeground(new Color(70, 70, 70));
        gbc.gridy = 2;
        centerPanel.add(passwordLabel, gbc);
        
        // Passwort-Feld
        passwort1 = new JPasswordField();
        passwort1.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwort1.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 3;
        centerPanel.add(passwort1, gbc);
        
        // Passwort bestätigen-Label
        confirmPasswordLabel = new JLabel("Passwort bestätigen:");
        confirmPasswordLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        confirmPasswordLabel.setForeground(new Color(70, 70, 70));
        gbc.gridy = 4;
        centerPanel.add(confirmPasswordLabel, gbc);
        
        // Passwort bestätigen-Feld
        passwort2 = new JPasswordField();
        passwort2.setFont(new Font("SansSerif", Font.PLAIN, 18));
        passwort2.setPreferredSize(new Dimension(400, 40));
        gbc.gridy = 5;
        centerPanel.add(passwort2, gbc);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer-Panel: Buttons, angeordnet mit FlowLayout
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 20));
        footerPanel.setBackground(new Color(245, 245, 245));
        
        // Zurück-Button im modernen Flat-Design
        zurueck = new JButton("Zurück");
        zurueck.setFont(new Font("SansSerif", Font.BOLD, 18));
        zurueck.setBackground(new Color(149, 165, 166));
        zurueck.setForeground(Color.WHITE);
        zurueck.setOpaque(true);
        zurueck.setBorderPainted(false);
        zurueck.setPreferredSize(new Dimension(150, 50));
        zurueck.addActionListener(evt -> zurueckActionPerformed(evt));
        footerPanel.add(zurueck);
        
        // Speichern-Button im modernen Flat-Design
        speichern = new JButton("Registrieren");
        speichern.setFont(new Font("SansSerif", Font.BOLD, 18));
        speichern.setBackground(new Color(46, 204, 113));
        speichern.setForeground(Color.WHITE);
        speichern.setOpaque(true);
        speichern.setBorderPainted(false);
        speichern.setPreferredSize(new Dimension(150, 50));
        speichern.addActionListener(evt -> speichernActionPerformed(evt));
        footerPanel.add(speichern);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Frame konfigurieren
        this.setTitle("NoteGO - Registrierung");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.getContentPane().add(mainPanel);
        this.setMinimumSize(new Dimension(800, 600));
        this.pack();
        this.setLocationRelativeTo(null);
    }

    private void zurueckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zurueckActionPerformed
    
        new GUI_Anmelden().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_zurueckActionPerformed

    private void speichernActionPerformed(java.awt.event.ActionEvent evt) {
         String name = nutzername.getText();
         String passwort = new String(passwort1.getPassword());
         String passwortu = new String(passwort2.getPassword());
         
         // Only check once
         boolean userExists = NutzerdatenCheck();
         
         System.out.println("New user registration requested");
         
        if(passwort.equals(passwortu) && !userExists)
        {
            try {      
                int ergebnis = this.konnektor.fuehreVeraenderungAus("INSERT INTO `nutzer` (`Benutzername`, `Passwort`) VALUES ('"+name+"', '"+passwort+"')");
                JOptionPane.showMessageDialog(null, "Benutzer erfolgreich registriert", "Erfolg", JOptionPane.INFORMATION_MESSAGE);
                new GUI_Anmelden().setVisible(true);
                this.dispose();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Fehler bei der Registrierung: " + e.getMessage(), "Fehler", JOptionPane.ERROR_MESSAGE);
            }
        } else if (!passwort.equals(passwortu)) {
            JOptionPane.showMessageDialog(null, "Fehler, Passwörter stimmen nicht überein", "Fehler", JOptionPane.ERROR_MESSAGE);
        } else if (userExists) {
            JOptionPane.showMessageDialog(null, "Fehler, Benutzername existiert bereits", "Fehler", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI_NeuHier().setVisible(true));
    }
}
