package NotizProjekt_All;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 * Simplified version of GUI_MenuTabelle that doesn't rely on the swing-layout library
 */
public class SimpleGUI_MenuTabelle extends JFrame {
    
    private JTable notizTabelle;
    private JButton btnNeueNotiz;
    private JButton btnBearbeiten;
    private JButton btnLoeschen;
    private JButton btnSuchen;
    private JTextField txtSuche;
    private JLabel lblTitel;
    
    private DBVerbindung konnektor;
    private int nutzerID;
    private ArrayList<Integer> notizIDs = new ArrayList<>();
    
    public SimpleGUI_MenuTabelle(int nutzerID) {
        this.nutzerID = nutzerID;
        initComponents();
        initDatabase();
        refreshTable();
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
        setTitle("Notizen Übersicht");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Create components
        lblTitel = new JLabel("Notizen Übersicht");
        lblTitel.setFont(new Font("Tahoma", Font.BOLD, 18));
        
        // Table setup
        notizTabelle = new JTable();
        notizTabelle.setModel(new DefaultTableModel(
            new Object [][] {},
            new String [] {"Titel", "Tag"}
        ));
        JScrollPane scrollPane = new JScrollPane(notizTabelle);
        
        // Buttons
        btnNeueNotiz = new JButton("Neue Notiz");
        btnBearbeiten = new JButton("Bearbeiten");
        btnLoeschen = new JButton("Löschen");
        btnSuchen = new JButton("Suchen");
        txtSuche = new JTextField(20);
        
        // Layout
        setLayout(new BorderLayout(10, 10));
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.add(lblTitel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Table panel
        add(scrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(btnNeueNotiz);
        buttonPanel.add(btnBearbeiten);
        buttonPanel.add(btnLoeschen);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.add(txtSuche);
        searchPanel.add(btnSuchen);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonPanel, BorderLayout.NORTH);
        bottomPanel.add(searchPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Add action listeners
        btnNeueNotiz.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SimpleGUI_NeueNotiz neueNotiz = new SimpleGUI_NeueNotiz(nutzerID);
                neueNotiz.setVisible(true);
                dispose();
            }
        });
        
        btnBearbeiten.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = notizTabelle.getSelectedRow();
                if (selectedRow >= 0) {
                    int notizID = notizIDs.get(selectedRow);
                    SimpleGUI_BearbeitenNotiz bearbeiten = new SimpleGUI_BearbeitenNotiz(notizID, nutzerID);
                    bearbeiten.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Bitte wählen Sie eine Notiz aus.");
                }
            }
        });
        
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
    }
    
    private void refreshTable() {
        try {
            DefaultTableModel model = (DefaultTableModel) notizTabelle.getModel();
            model.setRowCount(0);
            notizIDs.clear();
            
            ResultSet rs = konnektor.fuehreAbfrageAus(
                "SELECT N_id, Titel, Tag FROM notiz WHERE B_id = " + nutzerID);
            
            while (rs.next()) {
                int notizID = rs.getInt("N_id");
                String titel = rs.getString("Titel");
                String tag = rs.getString("Tag");
                
                model.addRow(new Object[]{titel, tag});
                notizIDs.add(notizID);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Laden der Notizen: " + ex.getMessage());
        }
    }
    
    private void searchNotes(String searchText) {
        try {
            DefaultTableModel model = (DefaultTableModel) notizTabelle.getModel();
            model.setRowCount(0);
            notizIDs.clear();
            
            ResultSet rs = konnektor.fuehreAbfrageAus(
                "SELECT N_id, Titel, Tag FROM notiz WHERE B_id = " + nutzerID + 
                " AND (Titel LIKE '%" + searchText + "%' OR Tag LIKE '%" + searchText + 
                "%' OR Inhalt LIKE '%" + searchText + "%')");
            
            while (rs.next()) {
                int notizID = rs.getInt("N_id");
                String titel = rs.getString("Titel");
                String tag = rs.getString("Tag");
                
                model.addRow(new Object[]{titel, tag});
                notizIDs.add(notizID);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Suche: " + ex.getMessage());
        }
    }
    
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SimpleGUI_MenuTabelle(1).setVisible(true);
            }
        });
    }
}