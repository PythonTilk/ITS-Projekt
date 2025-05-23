package NotizProjekt_All;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
        initComponents();
        initDatabase();
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
        setTitle("Neuer Benutzer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        
        // Create components
        JLabel lblNutzername = new JLabel("Benutzername:");
        lblNutzername.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JLabel lblPasswort1 = new JLabel("Passwort:");
        lblPasswort1.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JLabel lblPasswort2 = new JLabel("Passwort wiederholen:");
        lblPasswort2.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        txtNutzername = new JTextField(20);
        txtPasswort1 = new JPasswordField(20);
        txtPasswort2 = new JPasswordField(20);
        
        btnRegistrieren = new JButton("Registrieren");
        btnAbbrechen = new JButton("Abbrechen");
        
        // Layout
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(lblNutzername);
        formPanel.add(txtNutzername);
        formPanel.add(lblPasswort1);
        formPanel.add(txtPasswort1);
        formPanel.add(lblPasswort2);
        formPanel.add(txtPasswort2);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnRegistrieren);
        buttonPanel.add(btnAbbrechen);
        
        // Add panels to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        btnRegistrieren.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        
        btnAbbrechen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GUI_Anmelden anmelden = new GUI_Anmelden();
                anmelden.setVisible(true);
                dispose();
            }
        });
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