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
import javax.swing.JTextArea;
import javax.swing.JTextField;

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
        initComponents();
        initDatabase();
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
        setTitle("Notiz bearbeiten");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        
        // Create components
        JLabel lblTitel = new JLabel("Titel:");
        lblTitel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JLabel lblTag = new JLabel("Tag:");
        lblTag.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        JLabel lblInhalt = new JLabel("Inhalt:");
        lblInhalt.setFont(new Font("Tahoma", Font.PLAIN, 14));
        
        txtTitel = new JTextField(20);
        txtTag = new JTextField(20);
        txtInhalt = new JTextArea(10, 30);
        
        btnSpeichern = new JButton("Speichern");
        btnAbbrechen = new JButton("Abbrechen");
        
        // Layout
        setLayout(new BorderLayout(10, 10));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.add(lblTitel);
        formPanel.add(txtTitel);
        formPanel.add(lblTag);
        formPanel.add(txtTag);
        formPanel.add(lblInhalt);
        formPanel.add(txtInhalt);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnSpeichern);
        buttonPanel.add(btnAbbrechen);
        
        // Add panels to frame
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
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