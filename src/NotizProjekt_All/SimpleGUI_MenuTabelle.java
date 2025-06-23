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
        try {
            // Verwende hier den System-Look-and-Feel oder alternativ FlatLaf, wenn verfügbar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            System.err.println("Fehler beim Setzen des Look-and-Feel: " + ex);
        }
        
        initDatabase();
        initComponents();
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
        // Hauptpanel mit BorderLayout für klare Struktur
        JPanel mainPanel = new JPanel(new BorderLayout());
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
        JLabel subtitleLabel = new JLabel("Notizen Übersicht - " + GUI_Anmelden.AngemeldeterUser, SwingConstants.CENTER);
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
            new String [] {"Titel", "Tag"}
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
        btnNeueNotiz = new JButton("Neue Notiz");
        btnNeueNotiz.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnNeueNotiz.setBackground(new Color(46, 204, 113));
        btnNeueNotiz.setForeground(Color.WHITE);
        btnNeueNotiz.setOpaque(true);
        btnNeueNotiz.setBorderPainted(false);
        btnNeueNotiz.setPreferredSize(new Dimension(140, 45));
        buttonPanel.add(btnNeueNotiz);
        
        btnBearbeiten = new JButton("Bearbeiten");
        btnBearbeiten.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnBearbeiten.setBackground(new Color(52, 152, 219));
        btnBearbeiten.setForeground(Color.WHITE);
        btnBearbeiten.setOpaque(true);
        btnBearbeiten.setBorderPainted(false);
        btnBearbeiten.setPreferredSize(new Dimension(140, 45));
        buttonPanel.add(btnBearbeiten);
        
        btnLoeschen = new JButton("Löschen");
        btnLoeschen.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnLoeschen.setBackground(new Color(231, 76, 60));
        btnLoeschen.setForeground(Color.WHITE);
        btnLoeschen.setOpaque(true);
        btnLoeschen.setBorderPainted(false);
        btnLoeschen.setPreferredSize(new Dimension(140, 45));
        buttonPanel.add(btnLoeschen);
        
        footerPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBackground(new Color(245, 245, 245));
        
        JLabel searchLabel = new JLabel("Suchen:");
        searchLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        searchLabel.setForeground(new Color(70, 70, 70));
        searchPanel.add(searchLabel);
        
        txtSuche = new JTextField();
        txtSuche.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtSuche.setPreferredSize(new Dimension(300, 35));
        searchPanel.add(txtSuche);
        
        btnSuchen = new JButton("Suchen");
        btnSuchen.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSuchen.setBackground(new Color(149, 165, 166));
        btnSuchen.setForeground(Color.WHITE);
        btnSuchen.setOpaque(true);
        btnSuchen.setBorderPainted(false);
        btnSuchen.setPreferredSize(new Dimension(100, 35));
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