package NotizProjekt_All;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

/**
 * GUI for creating shared notes directly
 * @author OpenHands
 */
public class GUI_CreateSharedNotiz extends JFrame {

    private JPanel contentPane;
    private JTextField titleField;
    private JTextField tagField;
    private JTextArea contentArea;
    private JList<String> userList;
    private DefaultListModel<String> userListModel;
    private ArrayList<User> users;
    private DBVerbindung konnektor;
    private int currentUserId;
    private String currentUsername;

    /**
     * Create the frame for creating shared notes
     * @param userId The ID of the current user
     * @param username The username of the current user
     */
    public GUI_CreateSharedNotiz(int userId, String username) {
        this.currentUserId = userId;
        this.currentUsername = username;
        
        setTitle("Geteilte Notiz erstellen");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 600, 500);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);
        
        JLabel lblTitle = new JLabel("Geteilte Notiz erstellen");
        lblTitle.setFont(new Font("Verdana", Font.BOLD, 18));
        lblTitle.setBounds(20, 20, 300, 25);
        contentPane.add(lblTitle);
        
        // Title field
        JLabel lblTitleField = new JLabel("Titel:");
        lblTitleField.setFont(new Font("Verdana", Font.PLAIN, 14));
        lblTitleField.setBounds(20, 60, 100, 20);
        contentPane.add(lblTitleField);
        
        titleField = new JTextField();
        titleField.setFont(new Font("Verdana", Font.PLAIN, 14));
        titleField.setBounds(120, 60, 450, 25);
        contentPane.add(titleField);
        
        // Tag field
        JLabel lblTagField = new JLabel("Tag:");
        lblTagField.setFont(new Font("Verdana", Font.PLAIN, 14));
        lblTagField.setBounds(20, 95, 100, 20);
        contentPane.add(lblTagField);
        
        tagField = new JTextField();
        tagField.setFont(new Font("Verdana", Font.PLAIN, 14));
        tagField.setBounds(120, 95, 450, 25);
        contentPane.add(tagField);
        
        // Content area
        JLabel lblContent = new JLabel("Inhalt:");
        lblContent.setFont(new Font("Verdana", Font.PLAIN, 14));
        lblContent.setBounds(20, 130, 100, 20);
        contentPane.add(lblContent);
        
        contentArea = new JTextArea();
        contentArea.setFont(new Font("Verdana", Font.PLAIN, 14));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBounds(120, 130, 450, 150);
        contentPane.add(contentScrollPane);
        
        // User selection
        JLabel lblUsers = new JLabel("Teilen mit:");
        lblUsers.setFont(new Font("Verdana", Font.PLAIN, 14));
        lblUsers.setBounds(20, 290, 100, 20);
        contentPane.add(lblUsers);
        
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        userList.setFont(new Font("Verdana", Font.PLAIN, 14));
        
        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setBounds(120, 290, 450, 100);
        contentPane.add(userScrollPane);
        
        // Buttons
        JButton btnCreate = new JButton("Erstellen");
        btnCreate.setFont(new Font("Verdana", Font.BOLD, 14));
        btnCreate.setBounds(180, 410, 120, 30);
        btnCreate.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createSharedNote();
            }
        });
        contentPane.add(btnCreate);
        
        JButton btnCancel = new JButton("Abbrechen");
        btnCancel.setFont(new Font("Verdana", Font.BOLD, 14));
        btnCancel.setBounds(320, 410, 120, 30);
        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        contentPane.add(btnCancel);
        
        // Initialize database connection and load users
        try {
            this.konnektor = new DBVerbindung("localhost", "notizprojekt", "notizuser", "notizpassword");
            this.konnektor.open();
            loadUsers();
        } catch (ClassNotFoundException | SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler! Verbindung konnte nicht aufgebaut werden: " + ex);
        }
    }
    
    /**
     * Load all users except the current user
     */
    private void loadUsers() {
        try {
            users = new ArrayList<>();
            ResultSet ergebnis = this.konnektor.fuehreAbfrageAus(
                    "SELECT id, benutzername, passwort FROM nutzer WHERE id != " + currentUserId);
            
            while (ergebnis.next()) {
                User user = new User(
                        ergebnis.getString("benutzername"),
                        ergebnis.getString("passwort"),
                        ergebnis.getInt("id"));
                users.add(user);
                userListModel.addElement(user.getUName());
            }
            
            if (users.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Keine anderen Benutzer gefunden.");
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler bei der Abfrage der Benutzer: " + ex);
        }
    }
    
    /**
     * Create a shared note and share it with selected users
     */
    private void createSharedNote() {
        String title = titleField.getText().trim();
        String tag = tagField.getText().trim();
        String content = contentArea.getText().trim();
        int[] selectedIndices = userList.getSelectedIndices();
        
        // Validate input
        if (title.isEmpty() || tag.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Bitte füllen Sie alle Felder aus.");
            return;
        }
        
        if (selectedIndices.length == 0) {
            JOptionPane.showMessageDialog(this, "Bitte wählen Sie mindestens einen Benutzer aus.");
            return;
        }
        
        try {
            // Get current date and time
            Date now = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            String currentDate = dateFormat.format(now);
            String currentTime = timeFormat.format(now);
            
            // First create the note for the current user
            int result = this.konnektor.fuehreVeraenderungAus(
                    "INSERT INTO notiz (Titel, Tag, Inhalt, B_id) " +
                    "VALUES ('" + title + "', '" + tag + "', '" + content + "', " + currentUserId + ")");
            
            if (result <= 0) {
                JOptionPane.showMessageDialog(this, "Fehler beim Erstellen der Notiz.");
                return;
            }
            
            // Get the ID of the newly created note
            ResultSet rs = this.konnektor.fuehreAbfrageAus(
                    "SELECT N_id FROM notiz WHERE Titel = '" + title + "' AND B_id = " + currentUserId + 
                    " ORDER BY N_id DESC LIMIT 1");
            
            int noteId = -1;
            if (rs.next()) {
                noteId = rs.getInt("N_id");
            } else {
                JOptionPane.showMessageDialog(this, "Fehler beim Abrufen der erstellten Notiz.");
                return;
            }
            
            // Share the note with selected users
            int successCount = 0;
            
            for (int index : selectedIndices) {
                User selectedUser = users.get(index);
                
                int shareResult = this.konnektor.fuehreVeraenderungAus(
                        "INSERT INTO geteilte_notizen (Titel, Tag, Inhalt, Datum, Uhrzeit, Ort, Mitbenutzer, B_ID) " +
                        "VALUES ('" + title + "', '" + tag + "', '" + content + "', '" + 
                        currentDate + "', '" + currentTime + "', 'Online', '" + 
                        currentUsername + "', " + selectedUser.getB_id() + ")");
                
                if (shareResult > 0) {
                    successCount++;
                }
            }
            
            JOptionPane.showMessageDialog(this, 
                    "Notiz erfolgreich erstellt und mit " + successCount + " Benutzer(n) geteilt.");
            
            // Refresh the parent window's note list if it's a GUI_MenuTabelle
            if (getParent() instanceof GUI_MenuTabelle) {
                GUI_MenuTabelle parent = (GUI_MenuTabelle) getParent();
                parent.refreshNotes();
            }
            
            dispose();
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Fehler beim Erstellen der geteilten Notiz: " + ex);
        }
    }
}