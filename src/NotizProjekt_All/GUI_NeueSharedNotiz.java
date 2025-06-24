package NotizProjekt_All;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.*;

/**
 * GUI for creating shared notes
 * @author OpenHands
 */
public class GUI_NeueSharedNotiz extends JFrame {
    
    private DBVerbindung konnektor;
    private ArrayList<User> users;
    private JTextField txfTitel;
    private JTextField txfTag;
    private JTextArea txfNotiz;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private JButton btnErstellen;
    private JButton btnZurueck;
    
    private String titel;
    private String tag;
    private String notiz;
    private int Nutzer = GUI_Anmelden.NutzerID;
    
    public GUI_NeueSharedNotiz() {
        initComponents();
        setupDatabase();
        loadUsers();
    }
    
    private void setupDatabase() {
        try {
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Datenbankverbindung fehlgeschlagen: " + ex.getMessage());
        }
    }
    
    private void loadUsers() {
        try {
            users.clear();
            userListModel.clear();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + Nutzer);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                userListModel.addElement(user.getUName());
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Abfrage der Benutzer: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("Neue geteilte Notiz erstellen");
        setBackground(new Color(96, 96, 96));
        
        // Initialize components
        txfTitel = new JTextField();
        txfTag = new JTextField();
        txfNotiz = new JTextArea(5, 20);
        users = new ArrayList<>();
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        btnErstellen = new JButton("Geteilte Notiz erstellen");
        btnZurueck = new JButton("Zurück");
        
        // Setup styling
        setupStyling();
        
        // Setup layout
        setupLayout();
        
        // Add action listeners
        btnErstellen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                erstelleSharedNotiz();
            }
        });
        
        btnZurueck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GUI_MenuTabelle().setVisible(true);
                dispose();
            }
        });
    }
    
    private void setupStyling() {
        Color bgColor = new Color(96, 96, 96);
        Color fgColor = new Color(255, 255, 255);
        Font font = new Font("Arial", 0, 14);
        
        getContentPane().setBackground(bgColor);
        
        txfTitel.setBackground(bgColor);
        txfTitel.setForeground(fgColor);
        txfTitel.setFont(font);
        txfTitel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(fgColor), "Titel:", 
                0, 0, font, fgColor));
        
        txfTag.setBackground(bgColor);
        txfTag.setForeground(fgColor);
        txfTag.setFont(font);
        txfTag.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(fgColor), "Tag:", 
                0, 0, font, fgColor));
        
        txfNotiz.setBackground(bgColor);
        txfNotiz.setForeground(fgColor);
        txfNotiz.setFont(font);
        txfNotiz.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(fgColor), "Notiz:", 
                0, 0, font, fgColor));
        txfNotiz.setLineWrap(true);
        txfNotiz.setWrapStyleWord(true);
        
        userList.setBackground(bgColor);
        userList.setForeground(fgColor);
        userList.setFont(font);
        
        btnErstellen.setBackground(bgColor);
        btnErstellen.setForeground(fgColor);
        btnErstellen.setFont(font);
        
        btnZurueck.setBackground(bgColor);
        btnZurueck.setForeground(fgColor);
        btnZurueck.setFont(font);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(96, 96, 96));
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Title
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 5, 10);
        mainPanel.add(txfTitel, gbc);
        
        // Tag
        gbc.gridy = 1;
        mainPanel.add(txfTag, gbc);
        
        // Note content
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        JScrollPane noteScroll = new JScrollPane(txfNotiz);
        noteScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Notiz Inhalt:", 
                0, 0, new Font("Arial", 0, 12), Color.WHITE));
        mainPanel.add(noteScroll, gbc);
        
        // User selection
        gbc.gridy = 3;
        gbc.weighty = 0.4;
        JScrollPane userScroll = new JScrollPane(userList);
        userScroll.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.WHITE), "Benutzer auswählen (mehrere möglich):", 
                0, 0, new Font("Arial", 0, 12), Color.WHITE));
        mainPanel.add(userScroll, gbc);
        
        // Buttons
        gbc.gridy = 4;
        gbc.weighty = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridwidth = 1;
        
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(96, 96, 96));
        buttonPanel.add(btnZurueck);
        buttonPanel.add(btnErstellen);
        
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        mainPanel.add(buttonPanel, gbc);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Add title label
        JLabel titleLabel = new JLabel("Neue geteilte Notiz erstellen");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(titleLabel, BorderLayout.NORTH);
        
        pack();
        setLocationRelativeTo(null);
    }
    
    private void erstelleSharedNotiz() {
        // Get input values
        titel = txfTitel.getText().trim();
        tag = txfTag.getText().trim();
        notiz = txfNotiz.getText().trim();
        
        // Validate input
        if (titel.isEmpty() || tag.isEmpty() || notiz.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Alle Felder müssen ausgefüllt sein!");
            return;
        }
        
        int[] selectedIndices = userList.getSelectedIndices();
        if (selectedIndices.length == 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens einen Benutzer aus!");
            return;
        }
        
        try {
            // Get current date and time
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);
            
            // Create shared notes for each selected user
            int successCount = 0;
            for (int index : selectedIndices) {
                User selectedUser = users.get(index);
                
                // Insert shared note
                int result = this.konnektor.fuehreVeraenderungAus(
                        "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                        "VALUES ('" + titel.replace("'", "''") + "', '" + 
                        tag.replace("'", "''") + "', '" + 
                        notiz.replace("'", "''") + "', '" + 
                        currentDate + "', '" + 
                        currentTime + "', 'Online', '" + 
                        GUI_Anmelden.AngemeldeterUser + "', " + 
                        selectedUser.getB_id() + ")");
                
                if (result > 0) {
                    successCount++;
                }
            }
            
            if (successCount > 0) {
                JOptionPane.showMessageDialog(this, 
                    "Notiz erfolgreich mit " + successCount + " Benutzer(n) geteilt!");
                
                // Clear form
                txfTitel.setText("");
                txfTag.setText("");
                txfNotiz.setText("");
                userList.clearSelection();
                
                // Go back to main menu
                new GUI_MenuTabelle().setVisible(true);
                this.dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Fehler beim Teilen der Notiz!");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Datenbankfehler: " + ex.getMessage());
        }
    }
    
    public static void main(String args[]) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new GUI_NeueSharedNotiz().setVisible(true);
            }
        });
    }
}